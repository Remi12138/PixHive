package com.jin.pixhive.shared.websocket;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.jin.pixhive.shared.auth.SpaceUserAuthManager;
import com.jin.pixhive.shared.auth.model.SpaceUserPermissionConstant;
import com.jin.pixhive.domain.picture.entity.Picture;
import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.domain.space.valueobject.SpaceTypeEnum;
import com.jin.pixhive.application.service.PictureApplicationService;
import com.jin.pixhive.application.service.SpaceApplicationService;
import com.jin.pixhive.application.service.UserApplicationService;
import org.jetbrains.annotations.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Websocket Interceptor: check before connection
 */
@Component
@Slf4j
public class WsHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private PictureApplicationService pictureApplicationService;

    @Resource
    private SpaceApplicationService spaceApplicationService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            // get pictureId
            String pictureId = servletRequest.getParameter("pictureId");
            if (StrUtil.isBlank(pictureId)) {
                log.error("No pictureId, refuse handshake");
                return false;
            }
            User loginUser = userApplicationService.getLoginUser(servletRequest);
            if (ObjUtil.isEmpty(loginUser)) {
                log.error("No logged in, refuse handshake");
                return false;
            }
            // check auth
            Picture picture = pictureApplicationService.getById(pictureId);
            if (picture == null) {
                log.error("Picture not found, refuse handshake");
                return false;
            }
            Long spaceId = picture.getSpaceId();
            Space space = null;
            if (spaceId != null) {
                space = spaceApplicationService.getById(spaceId);
                if (space == null) {
                    log.error("Space not found, refuse handshake");
                    return false;
                }
                if (space.getSpaceType() != SpaceTypeEnum.TEAM.getValue()) {
                    log.info("Not a team space, refuse handshake");
                    return false;
                }
            }
            List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
            if (!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)) {
                log.error("No permission, refuse handshake");
                return false;
            }
            // set attributes to Websocket session
            attributes.put("user", loginUser);
            attributes.put("userId", loginUser.getId());
            attributes.put("pictureId", Long.valueOf(pictureId)); // convert to Long
        }
        return true;
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, Exception exception) {
    }
}

