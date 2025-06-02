package com.jin.pixhive.interfaces.dto.space;

import com.jin.pixhive.infrastructure.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * space query request
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private Long userId;

    private String spaceName;

    private Integer spaceLevel;

    /**
     * 0-private, 1-team
     */
    private Integer spaceType;

    private static final long serialVersionUID = 1L;
}






