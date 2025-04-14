package com.jin.pixhive_backend.model.dto.space;

import com.jin.pixhive_backend.common.PageRequest;
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

    private static final long serialVersionUID = 1L;
}






