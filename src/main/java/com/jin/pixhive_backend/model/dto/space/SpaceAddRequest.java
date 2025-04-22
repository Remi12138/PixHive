package com.jin.pixhive_backend.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * space add request
 */
@Data
public class SpaceAddRequest implements Serializable {

    private String spaceName;

    /**
     * 0-Starter, 1-Pro, 2-Premium
     */
    private Integer spaceLevel;

    /**
     * 0-private, 1-team
     */
    private Integer spaceType;

    private static final long serialVersionUID = 1L;
}





