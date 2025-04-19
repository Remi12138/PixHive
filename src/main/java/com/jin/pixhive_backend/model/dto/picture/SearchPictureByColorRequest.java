package com.jin.pixhive_backend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Search Picture By Color Request
 */
@Data
public class SearchPictureByColorRequest implements Serializable {

    private String picColor;

    private Long spaceId;

    private static final long serialVersionUID = 1L;
}
