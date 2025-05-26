package com.jin.pixhive_backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin.pixhive_backend.exception.BusinessException;
import com.jin.pixhive_backend.exception.ErrorCode;
import com.jin.pixhive_backend.exception.ThrowUtils;
import com.jin.pixhive_backend.manage.auth.StpKit;
import com.jin.pixhive_backend.manage.upload.FilePictureUpload;
import com.jin.pixhive_backend.model.dto.file.UploadPictureResult;
import com.jin.pixhive_backend.model.dto.user.UserProfileUpdateRequest;
import com.jin.pixhive_backend.model.dto.user.UserQueryRequest;
import com.jin.pixhive_backend.model.dto.user.VipCode;
import com.jin.pixhive_backend.model.entity.User;
import com.jin.pixhive_backend.model.enums.UserRoleEnum;
import com.jin.pixhive_backend.model.vo.LoginUserVO;
import com.jin.pixhive_backend.model.vo.UserVO;
import com.jin.pixhive_backend.service.UserService;
import com.jin.pixhive_backend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.jin.pixhive_backend.constant.UserConstant.USER_LOGIN_STATE;
import static com.jin.pixhive_backend.constant.UserConstant.VIP_ROLE;

/**
* @author xianjinghuang
* @description database operation service implement for table [user]
* @createDate 2025-03-15 10:25:18
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @javax.annotation.Resource
    private FilePictureUpload filePictureUpload;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. check
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Parameter is blank");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "User account length is less than 4");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "User password length is less than 8");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password and check password are different");
        }
        // 2. Check whether the account is duplicate
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account duplication");
        }
        // 3. encrypt
        String encryptPassword = getEncryptPassword(userPassword);
        // 4. insert user
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("DefaultUser");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Registration failed, database insertion error");
        }
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. check
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Parameter is blank");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account error");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password error");
        }
        // 2. encrypt
        String encryptPassword = getEncryptPassword(userPassword);
        // 3. query whether the user exists
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // user not exist
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The user does not exist or the password is incorrect");
        }
        // 3. record user login state
        request.getSession().setAttribute(USER_LOGIN_STATE, user);

        // 4. record user in Sa-token, for space authentication
        // ensure the expiration time of the user information is consistent with that in the SpringSession
        StpKit.SPACE.login(user.getId());
        StpKit.SPACE.getSession().set(USER_LOGIN_STATE, user);

        return this.getLoginUserVO(user);
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // add salt
        final String SALT = "jinnij1";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // Check whether logged in
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // Query from the database (can make sure retrieve the latest info )
        // can comment if want better performance, just return the above results directly
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // Check whether logged in
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "not logged in");
        }
        // remove login state
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Parameter is null");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    @Override
    public boolean updateUserProfile(UserProfileUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Missing update request");
        }

        User loginUser = this.getLoginUser(request);

        User updateUser = new User();
        updateUser.setId(loginUser.getId());

        // Update userName if provided
        if (StrUtil.isNotBlank(updateRequest.getUserName())) {
            updateUser.setUserName(updateRequest.getUserName());
        }

        // Update userAvatar if provided
        if (StrUtil.isNotBlank(updateRequest.getUserAvatar())) {
            updateUser.setUserAvatar(updateRequest.getUserAvatar());
        }

        // Update password if provided (after encryption)
        if (StrUtil.isNotBlank(updateRequest.getUserPassword())) {
            updateUser.setUserPassword(getEncryptPassword(updateRequest.getUserPassword()));
        }

        boolean result = this.updateById(updateUser);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Failed to update profile");
        }

        // Refresh session info after successful update
        User refreshedUser = this.getById(loginUser.getId());
        request.getSession().setAttribute(USER_LOGIN_STATE, refreshedUser);
        StpKit.SPACE.getSession().set(USER_LOGIN_STATE, refreshedUser);

        return true;
    }

    @Override
    public String uploadUserAvatar(MultipartFile file, HttpServletRequest request) {
        User loginUser = this.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        log.info("Uploading avatar for userId={}, fileName={}", loginUser.getId(), file.getOriginalFilename());

        String uploadPathPrefix = String.format("avatar/%s", loginUser.getId());
        UploadPictureResult uploadPictureResult = filePictureUpload.uploadPicture(file, uploadPathPrefix);

        return uploadPictureResult.getUrl();
    }



// region ------- The following code is for the user's vip redemption function --------

    @Autowired
    private ResourceLoader resourceLoader;

    // File read/write lock (ensure concurrent security)
    private final ReentrantLock fileLock = new ReentrantLock();


    @Override
    public boolean redeemVip(User user, String vipCode) {
        // 1. check params
        if (user == null || StrUtil.isBlank(vipCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. validate and Mark VipCode
        VipCode targetCode = validateAndMarkVipCode(vipCode);
        // 3. update User Vip Info in database
        updateUserVipInfo(user, targetCode.getCode());
        return true;
    }

    /**
     * validate and Mark VipCode
     */
    private VipCode validateAndMarkVipCode(String vipCode) {
        fileLock.lock(); // ensures the atomicity of file operations
        try {
            // read JSON
            JSONArray jsonArray = readVipCodeFile();

            // Search for matching unused redemption codes
            List<VipCode> codes = JSONUtil.toList(jsonArray, VipCode.class);
            VipCode target = codes.stream()
                    .filter(code -> code.getCode().equals(vipCode) && !code.isHasUsed())
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "invalid VIP code"));

            // mark as used
            target.setHasUsed(true);

            // update json file
            writeVipCodeFile(JSONUtil.parseArray(codes));
            return target;
        } finally {
            fileLock.unlock();
        }
    }

    /**
     * read VipCode JSON File
     */
    private JSONArray readVipCodeFile() {
        try {
            Resource resource = resourceLoader.getResource("classpath:biz/vipCode.json");
            String content = FileUtil.readString(resource.getFile(), StandardCharsets.UTF_8);
            return JSONUtil.parseArray(content);
        } catch (IOException e) {
            log.error("read VipCode File Failed", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "read VipCode File Failed");
        }
    }

    /**
     * write VipCode JSON File
     */
    private void writeVipCodeFile(JSONArray jsonArray) {
        try {
            Resource resource = resourceLoader.getResource("classpath:biz/vipCode.json");
            FileUtil.writeString(jsonArray.toStringPretty(), resource.getFile(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("update VipCode File Failed", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "update VipCode File Failed");
        }
    }

    /**
     * update User Vip Info in database
     */
    private void updateUserVipInfo(User user, String usedVipCode) {
        // expireTime + 1 year
        Date expireTime = DateUtil.offsetMonth(new Date(), 12);

        // Build the update object
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setVipExpireTime(expireTime);
        updateUser.setVipCode(usedVipCode);
        updateUser.setUserRole(VIP_ROLE);

        // update in database
        boolean updated = this.updateById(updateUser);
        if (!updated) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Failed to operate the database. Failed to redeem vip.");
        }
    }

    // endregion ------- user's vip redemption function --------
}





