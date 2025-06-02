package com.jin.pixhive.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * upload picture request
 */
@Data
public class PictureUploadRequest implements Serializable {

    /**
     * picture id (for revising)
     */
    private Long id;

    private String fileUrl;

    /**
     * picture name
     */
    private String picName;

    private Long spaceId;

    private static final long serialVersionUID = 1L;
}



