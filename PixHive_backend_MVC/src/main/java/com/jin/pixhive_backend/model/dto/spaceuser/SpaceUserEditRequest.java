package com.jin.pixhive_backend.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * space_user edit request
 */
@Data
public class SpaceUserEditRequest implements Serializable {

    private Long spaceId;

    private Long spaceuserId;

    /**
     * spaceRole: viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}







