package com.jin.pixhive_backend.model.dto.picture;

import com.jin.pixhive_backend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * Search Picture By Color Request
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchPictureByColorRequest extends PageRequest implements Serializable {

    private String picColor;

    private Long spaceId;

    private static final long serialVersionUID = 1L;
}
