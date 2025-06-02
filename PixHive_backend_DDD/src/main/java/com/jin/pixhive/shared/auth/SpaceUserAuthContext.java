package com.jin.pixhive.shared.auth;

import com.jin.pixhive.domain.picture.entity.Picture;
import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.domain.space.entity.SpaceUser;
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
