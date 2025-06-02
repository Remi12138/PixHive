package com.jin.pixhive.shared.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.jin.pixhive.infrastructure.exception.BusinessException;
import com.jin.pixhive.infrastructure.exception.ErrorCode;
import com.jin.pixhive.shared.auth.model.SpaceUserPermissionConstant;
import com.jin.pixhive.domain.picture.entity.Picture;
import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.domain.space.entity.SpaceUser;
import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.domain.space.valueobject.SpaceRoleEnum;
import com.jin.pixhive.domain.space.valueobject.SpaceTypeEnum;
import com.jin.pixhive.application.service.PictureApplicationService;
import com.jin.pixhive.application.service.SpaceApplicationService;
import com.jin.pixhive.application.service.SpaceUserApplicationService;
import com.jin.pixhive.application.service.UserApplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.jin.pixhive.domain.user.constant.UserConstant.USER_LOGIN_STATE;

/**
 * Custom permission loading interface implementation
 */
@Component // Ensure that this is scanned by SpringBoot to complete the custom permission verification extension of Sa-Token
public class StpInterfaceImpl implements StpInterface {
    // default: /api
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    @Resource
    private PictureApplicationService pictureApplicationService;

    @Resource
    private SpaceUserApplicationService spaceUserApplicationService;

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private SpaceApplicationService spaceApplicationService;

    // choose check via permission/role
    // here use permission (finer granularity)

    /** [CHOOSE]
     * return all the permission of one user
     */
    public List<String> getPermissionList(Object loginId, String loginType) {
        // check loginType, only perform permission verification for the type "space"
        if (!StpKit.SPACE_TYPE.equals(loginType)) {
            return new ArrayList<>();
        }
        // ADMIN_PERMISSIONS
        List<String> ADMIN_PERMISSIONS = spaceUserAuthManager.getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // get Auth Context from Request
        SpaceUserAuthContext authContext = getAuthContextByRequest();
        // isAllFieldsNull -> public, return admin auth
        if (isAllFieldsNull(authContext)) {
            return ADMIN_PERMISSIONS;
        }
        // userId
        User loginUser = (User) StpKit.SPACE.getSessionByLoginId(loginId).get(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "User does not login!");
        }
        Long userId = loginUser.getId();
        // first check if has spaceUser info
        SpaceUser spaceUser = authContext.getSpaceUser();
        if (spaceUser != null) {
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }
        // then check spaceUserId, if has -> team space, fetch SpaceUser obj
        Long spaceUserId = authContext.getSpaceUserId();
        if (spaceUserId != null) {
            spaceUser = spaceUserApplicationService.getById(spaceUserId);
            if (spaceUser == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Cannot find space_user from its ID!");
            }
            // fetch current user
            SpaceUser loginSpaceUser = spaceUserApplicationService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceUser.getSpaceId())
                    .eq(SpaceUser::getUserId, userId)
                    .one();
            if (loginSpaceUser == null) {
                return new ArrayList<>();
            }
            // [todo] This will cause the admin to have no permission in the private space, and can check the library again for handling
            return spaceUserAuthManager.getPermissionsByRole(loginSpaceUser.getSpaceRole());
        }
        // then try to use spaceId or pictureId to get Space
        Long spaceId = authContext.getSpaceId();
        if (spaceId == null) {
            // no spaceId，get Picture and Space via pictureId
            Long pictureId = authContext.getPictureId();
            // no pictureId, return admin_permission
            if (pictureId == null) {
                return ADMIN_PERMISSIONS;
            }
            Picture picture = pictureApplicationService.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .select(Picture::getId, Picture::getSpaceId, Picture::getUserId)
                    .one();
            if (picture == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Cannot find picture from its ID!");
            }
            spaceId = picture.getSpaceId();
            // public -> creator & admin
            if (spaceId == null) {
                if (picture.getUserId().equals(userId) || loginUser.isAdmin()) {
                    return ADMIN_PERMISSIONS;
                } else {
                    // visitor -> only view
                    return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
                }
            }
        }
        // get Space via spaceId
        Space space = spaceApplicationService.getById(spaceId);
        if (space == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Cannot find space from its ID!");
        }
        // check Space type
        if (space.getSpaceType() == SpaceTypeEnum.PRIVATE.getValue()) {
            // private -> self & admin
            if (space.getUserId().equals(userId) || loginUser.isAdmin()) {
                return ADMIN_PERMISSIONS;
            } else {
                return new ArrayList<>();
            }
        } else {
            // team space -> query SpaceUser, get role and permission
            spaceUser = spaceUserApplicationService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceId)
                    .eq(SpaceUser::getUserId, userId)
                    .one();
            if (spaceUser == null) {
                return new ArrayList<>();
            }
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }
    }


    /**
     * return all the role of one user
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    private boolean isAllFieldsNull(Object object) {
        if (object == null) {
            return true;
        }
        // get all field and check all null
        return Arrays.stream(ReflectUtil.getFields(object.getClass()))
                .map(field -> ReflectUtil.getFieldValue(object, field))
                .allMatch(ObjectUtil::isEmpty);
    }


    /**
     * get Auth Context from Request
     */
    private SpaceUserAuthContext getAuthContextByRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String contentType = request.getHeader(Header.CONTENT_TYPE.getValue());
        SpaceUserAuthContext authRequest;
        // get request params
        if (ContentType.JSON.getValue().equals(contentType)) { //JSON (PUT)
            String body = ServletUtil.getBody(request);
            authRequest = JSONUtil.toBean(body, SpaceUserAuthContext.class);
        } else { // not JSON (GET)
            Map<String, String> paramMap = ServletUtil.getParamMap(request);
            authRequest = BeanUtil.toBean(paramMap, SpaceUserAuthContext.class);
        }
        // Distinguish the meaning of the id according to the request path
        Long id = authRequest.getId();
        if (ObjUtil.isNotNull(id)) {
            // /api/picture/aaa?a=1
            String requestUri = request.getRequestURI();
            // partUri: picture/aaa?a=1
            String partUri = requestUri.replace(contextPath + "/", "");
            String moduleName = StrUtil.subBefore(partUri, "/", false);
            switch (moduleName) {
                case "picture":
                    authRequest.setPictureId(id);
                    break;
                case "spaceUser":
                    authRequest.setSpaceUserId(id);
                    break;
                case "space":
                    authRequest.setSpaceId(id);
                    break;
                default:
            }
        }
        return authRequest;
    }

}
