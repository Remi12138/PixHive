package com.jin.pixhive_backend.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * space edit request [for user]
 */
@Data
public class SpaceEditRequest implements Serializable {

    private Long id;

    private String spaceName;

    private static final long serialVersionUID = 1L;
}






