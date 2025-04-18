package com.jin.pixhive_backend.model.dto.picture;

import com.jin.pixhive_backend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * search similar request
 */
@Data
public class SearchPictureByPictureRequest implements Serializable {

    private Long pictureId;

    private static final long serialVersionUID = 1L;
}

