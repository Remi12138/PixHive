package com.jin.pixhive.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin.pixhive.domain.space.service.SpaceDomainService;
import com.jin.pixhive.infrastructure.exception.BusinessException;
import com.jin.pixhive.infrastructure.exception.ErrorCode;
import com.jin.pixhive.infrastructure.exception.ThrowUtils;
import com.jin.pixhive.interfaces.dto.space.SpaceAddRequest;
import com.jin.pixhive.interfaces.dto.space.SpaceQueryRequest;
import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.domain.space.entity.SpaceUser;
import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.domain.space.valueobject.SpaceLevelEnum;
import com.jin.pixhive.domain.space.valueobject.SpaceRoleEnum;
import com.jin.pixhive.domain.space.valueobject.SpaceTypeEnum;
import com.jin.pixhive.interfaces.vo.space.SpaceVO;
import com.jin.pixhive.interfaces.vo.user.UserVO;
import com.jin.pixhive.application.service.SpaceApplicationService;
import com.jin.pixhive.infrastructure.mapper.SpaceMapper;
import com.jin.pixhive.application.service.SpaceUserApplicationService;
import com.jin.pixhive.application.service.UserApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SpaceApplicationServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceApplicationService {

    @Resource
    private SpaceDomainService spaceDomainService;

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private SpaceUserApplicationService spaceUserApplicationService;

    Map<Long, Object> lockMap = new ConcurrentHashMap<>();

    // sharding optional
//    @Resource
//    @Lazy
//    private DynamicShardingManager dynamicShardingManager;

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // obj->VO
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        //  query user information
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userApplicationService.getUserById(userId);
            UserVO userVO = userApplicationService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }
        // space list -> spaceVO list
        List<SpaceVO> spaceVOList = spaceList.stream().map(SpaceVO::objToVo).collect(Collectors.toList());
        // 1. get userId
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userApplicationService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. set spaceVO user
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userApplicationService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        return spaceDomainService.getQueryWrapper(spaceQueryRequest);
    }

    // fill maxSize, maxCount by space level
    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        spaceDomainService.fillSpaceBySpaceLevel(space);
    }

    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest, space);
        // default value
        if (StrUtil.isBlank(spaceAddRequest.getSpaceName())) {
            space.setSpaceName("Default Space");
        }
        if (spaceAddRequest.getSpaceLevel() == null) {
            space.setSpaceLevel(SpaceLevelEnum.STARTER.getValue());
        }
        if (spaceAddRequest.getSpaceType() == null) {
            spaceAddRequest.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }
        // fill size, count
        this.fillSpaceBySpaceLevel(space);
        // basic check
        space.validSpace(true);
        Long userId = loginUser.getId();
        space.setUserId(userId);
        // role check
        if (SpaceLevelEnum.STARTER.getValue() != spaceAddRequest.getSpaceLevel() && !loginUser.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "No permission to create the specified level of space!");
        }
        // Lock for users, 1 user can only create 1 private space, 1 team space
        Object lock = lockMap.computeIfAbsent(userId, key -> new Object());
        synchronized (lock) {
            try {
                Long newSpaceId = transactionTemplate.execute(status -> {
                    // check exist
                    boolean exists = this.lambdaQuery()
                            .eq(Space::getUserId, userId)
                            .eq(Space::getSpaceType, space.getSpaceType())
                            .exists();
                    ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "Each user can have only 1 private space & 1 team space!");
                    // save to database
                    boolean result = this.save(space);
                    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "Save space to database failed!");

                    // if team space, creator -> admin
                    if (SpaceTypeEnum.TEAM.getValue() == spaceAddRequest.getSpaceType()) {
                        SpaceUser spaceUser = new SpaceUser();
                        spaceUser.setSpaceId(space.getId());
                        spaceUser.setUserId(userId);
                        spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                        result = spaceUserApplicationService.save(spaceUser);
                        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "Setup creator to team admin failed!");
                    }
                    // create sharding table [optional]
//                    dynamicShardingManager.createSpacePictureTable(space);
                    return space.getId();
                });
                return Optional.ofNullable(newSpaceId).orElse(-1L);
            } finally {
                // clear lock, avoid memory leaks
                lockMap.remove(userId);
            }
        }
    }

    @Override
    public void checkSpaceAuth(User loginUser, Space space) {
        spaceDomainService.checkSpaceAuth(loginUser, space);
    }
}




