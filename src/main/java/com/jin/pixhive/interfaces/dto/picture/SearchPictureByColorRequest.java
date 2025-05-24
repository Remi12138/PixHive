package com.jin.pixhive.interfaces.dto.picture;

import com.jin.pixhive.infrastructure.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
