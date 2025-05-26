package com.jin.pixhive_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jin.pixhive_backend.model.dto.user.UserProfileUpdateRequest;
import com.jin.pixhive_backend.model.dto.user.UserQueryRequest;
import com.jin.pixhive_backend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jin.pixhive_backend.model.vo.LoginUserVO;
import com.jin.pixhive_backend.model.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author xianjinghuang
* @description database operation service for table [user]
* @createDate 2025-03-15 10:25:18
*/
public interface UserService extends IService<User> {
    /**
     * user register
     *
     * @param userAccount   account
     * @param userPassword  password
     * @param checkPassword check_password
     * @return new user id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * user login
     *
     * @param userAccount  account
     * @param userPassword password
     * @param request HttpServletRequest
     * @return Logged in User view (Desensitization)
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * get encrypt password
     * @param userPassword ori password
     * @return encrypt password
     */
    String getEncryptPassword(String userPassword);

    /**
     * Get user information after desensitization
     * @param user original user
     * @return desensitized user
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * get current login user
     *
     * @param request
     * @return User
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * for query: get UserVO
     */
    public UserVO getUserVO(User user);

    /**
     * for query: get UserVO list
     */
    public List<UserVO> getUserVOList(List<User> userList);

    /**
     * logout
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * get query condition
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * is admin?
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * users can revise name/avatar/password themselves
     */
    boolean updateUserProfile(UserProfileUpdateRequest updateRequest, HttpServletRequest request);

    /**
     * upload avatar to COS
     */
    String uploadUserAvatar(MultipartFile file, HttpServletRequest request);

    /**
     * redeem vip
     */
    boolean redeemVip(User user, String vipCode);
}
