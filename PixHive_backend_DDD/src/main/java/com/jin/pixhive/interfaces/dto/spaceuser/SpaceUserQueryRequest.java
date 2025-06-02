package com.jin.pixhive.interfaces.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * space_user query request
 */
@Data
public class SpaceUserQueryRequest implements Serializable {

    private Long id;

    private Long spaceId;

    private Long userId;

    /**
     * spaceRole: viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}








