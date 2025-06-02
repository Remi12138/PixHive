package com.jin.pixhive.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin.pixhive.domain.space.service.SpaceUserDomainService;
import com.jin.pixhive.infrastructure.exception.BusinessException;
import com.jin.pixhive.infrastructure.exception.ErrorCode;
import com.jin.pixhive.infrastructure.exception.ThrowUtils;
import com.jin.pixhive.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.jin.pixhive.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.domain.space.entity.SpaceUser;
import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.domain.space.valueobject.SpaceRoleEnum;
import com.jin.pixhive.interfaces.vo.space.SpaceUserVO;
import com.jin.pixhive.interfaces.vo.space.SpaceVO;
import com.jin.pixhive.interfaces.vo.user.UserVO;
import com.jin.pixhive.application.service.SpaceApplicationService;
import com.jin.pixhive.application.service.SpaceUserApplicationService;
import com.jin.pixhive.infrastructure.mapper.SpaceUserMapper;
import com.jin.pixhive.application.service.UserApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SpaceUserApplicationServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
    implements SpaceUserApplicationService {

    @Resource
    private SpaceUserDomainService spaceUserDomainService;

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    @Lazy
    private SpaceApplicationService spaceApplicationService;

    @Override
    public long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest) {
        // check params
        ThrowUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserAddRequest, spaceUser);
        validSpaceUser(spaceUser, true);
        // save in database
        boolean result = this.save(spaceUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return spaceUser.getId();
    }

    @Override
    public void validSpaceUser(SpaceUser spaceUser, boolean add) {
        ThrowUtils.throwIf(spaceUser == null, ErrorCode.PARAMS_ERROR);
        // when add, [required]spaceId, userId
        Long spaceId = spaceUser.getSpaceId();
        Long userId = spaceUser.getUserId();
        if (add) {
            ThrowUtils.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCode.PARAMS_ERROR);
            User user = userApplicationService.getUserById(userId);
            ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "User not found!");
            Space space = spaceApplicationService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "Space not found!");
        }
        // check role exist
        String spaceRole = spaceUser.getSpaceRole();
        SpaceRoleEnum spaceRoleEnum = SpaceRoleEnum.getEnumByValue(spaceRole);
        if (spaceRole != null && spaceRoleEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Space role does not exist!");
        }
    }

    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        return spaceUserDomainService.getQueryWrapper(spaceUserQueryRequest);
    }

    @Override
    public SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request) {
        SpaceUserVO spaceUserVO = SpaceUserVO.objToVo(spaceUser);
        // user
        Long userId = spaceUser.getUserId();
        if (userId != null && userId > 0) {
            User user = userApplicationService.getUserById(userId);
            UserVO userVO = userApplicationService.getUserVO(user);
            spaceUserVO.setUser(userVO);
        }
        // space
        Long spaceId = spaceUser.getSpaceId();
        if (spaceId != null && spaceId > 0) {
            Space space = spaceApplicationService.getById(spaceId);
            SpaceVO spaceVO = spaceApplicationService.getSpaceVO(space, request);
            spaceUserVO.setSpace(spaceVO);
        }
        return spaceUserVO;
    }

    @Override
    public List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList) {
        // check list empty
        if (CollUtil.isEmpty(spaceUserList)) {
            return Collections.emptyList();
        }

        List<SpaceUserVO> spaceUserVOList = spaceUserList.stream().map(SpaceUserVO::objToVo).collect(Collectors.toList());
        // 1. get userId, spaceId
        Set<Long> userIdSet = spaceUserList.stream().map(SpaceUser::getUserId).collect(Collectors.toSet());
        Set<Long> spaceIdSet = spaceUserList.stream().map(SpaceUser::getSpaceId).collect(Collectors.toSet());
        // 2. query user, space by batch
        Map<Long, List<User>> userIdUserListMap = userApplicationService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Space>> spaceIdSpaceListMap = spaceApplicationService.listByIds(spaceIdSet).stream()
                .collect(Collectors.groupingBy(Space::getId));
        // 3. fill out SpaceUserVO's user & space info
        spaceUserVOList.forEach(spaceUserVO -> {
            Long userId = spaceUserVO.getUserId();
            Long spaceId = spaceUserVO.getSpaceId();
            // user info
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceUserVO.setUser(userApplicationService.getUserVO(user));
            // space info
            Space space = null;
            if (spaceIdSpaceListMap.containsKey(spaceId)) {
                space = spaceIdSpaceListMap.get(spaceId).get(0);
            }
            spaceUserVO.setSpace(SpaceVO.objToVo(space));
        });
        return spaceUserVOList;
    }

}




