package com.jin.pixhive_backend.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * space_user add request
 */
@Data
public class SpaceUserAddRequest implements Serializable {

    private Long spaceId;

    private Long userId;

    /**
     * spaceRole: viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}






