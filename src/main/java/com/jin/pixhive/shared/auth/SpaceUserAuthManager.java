package com.jin.pixhive.shared.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jin.pixhive.shared.auth.model.SpaceUserPermissionConstant;
import com.jin.pixhive.shared.auth.model.SpaceUserRole;
import com.jin.pixhive.shared.auth.model.SpaceUserAuthConfig;
import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.domain.space.entity.SpaceUser;
import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.domain.space.valueobject.SpaceRoleEnum;
import com.jin.pixhive.domain.space.valueobject.SpaceTypeEnum;
import com.jin.pixhive.application.service.SpaceUserApplicationService;
import com.jin.pixhive.application.service.UserApplicationService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class SpaceUserAuthManager {

    @Resource
    private SpaceUserApplicationService spaceUserApplicationService;

    @Resource
    private UserApplicationService userApplicationService;

    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }


    public List<String> getPermissionsByRole(String spaceUserRole) {
        if (StrUtil.isBlank(spaceUserRole)) {
            return new ArrayList<>();
        }

        SpaceUserRole role = SPACE_USER_AUTH_CONFIG.getRoles().stream()
                .filter(r -> spaceUserRole.equals(r.getKey()))
                .findFirst()
                .orElse(null);
        if (role == null) {
            return new ArrayList<>();
        }
        return role.getPermissions();
    }

    public List<String> getPermissionList(Space space, User loginUser) {
        if (loginUser == null) {
            return new ArrayList<>();
        }
        // ADMIN_PERMISSIONS
        List<String> ADMIN_PERMISSIONS = getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // public
        if (space == null) {
            if (loginUser.isAdmin()) {
                return ADMIN_PERMISSIONS;
            }
            return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
        }
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
        if (spaceTypeEnum == null) {
            return new ArrayList<>();
        }
        // get permission according to space type
        switch (spaceTypeEnum) {
            case PRIVATE:
                // private -> self & admin
                if (space.getUserId().equals(loginUser.getId()) || loginUser.isAdmin()) {
                    return ADMIN_PERMISSIONS;
                } else {
                    return new ArrayList<>();
                }
            case TEAM:
                // team: query SpaceUser to get role, permission
                SpaceUser spaceUser = spaceUserApplicationService.lambdaQuery()
                        .eq(SpaceUser::getSpaceId, space.getId())
                        .eq(SpaceUser::getUserId, loginUser.getId())
                        .one();
                if (spaceUser == null) {
                    return new ArrayList<>();
                } else {
                    return getPermissionsByRole(spaceUser.getSpaceRole());
                }
        }
        return new ArrayList<>();
    }
}

