package com.jin.pixhive_backend.manage.auth;

import com.jin.pixhive_backend.model.entity.Picture;
import com.jin.pixhive_backend.model.entity.Space;
import com.jin.pixhive_backend.model.entity.SpaceUser;
import lombok.Data;

/**
 * SpaceUserAuthContext
 * Represents the authorization context of the user within a specific space,
 * including associated images, Spaces, and user info
 */
@Data
public class SpaceUserAuthContext {

    /**
     * Temporary parameters
     * ID in different request represent different meaning(picId, spaceId,...)
     */
    private Long id;

    private Long pictureId;

    private Long spaceId;

    private Long spaceUserId;

    private Picture picture;

    private Space space;

    private SpaceUser spaceUser;
}
