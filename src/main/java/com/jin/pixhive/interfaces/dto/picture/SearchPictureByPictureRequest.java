package com.jin.pixhive.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * search similar picture request
 */
@Data
public class SearchPictureByPictureRequest implements Serializable {

    private Long pictureId;

    private static final long serialVersionUID = 1L;
}

