package com.jin.pixhive.interfaces.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * space update request [for admin]
 */
@Data
public class SpaceUpdateRequest implements Serializable {

    private Long id;

    private String spaceName;

    private Integer spaceLevel;

    private Long maxSize;

    private Long maxCount;

    private static final long serialVersionUID = 1L;
}






