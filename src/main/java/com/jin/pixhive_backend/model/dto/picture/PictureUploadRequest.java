package com.jin.pixhive_backend.model.dto.picture;

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

    private static final long serialVersionUID = 1L;
}



