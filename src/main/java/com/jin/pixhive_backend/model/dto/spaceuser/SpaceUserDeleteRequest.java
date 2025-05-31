package com.jin.pixhive_backend.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * space_user delete request
 */
@Data
public class SpaceUserDeleteRequest implements Serializable {

    private Long spaceId;

    private Long spaceuserId;

    private static final long serialVersionUID = 1L;
}







