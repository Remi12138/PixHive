package com.jin.pixhive_backend.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * upload picture result
 */
@Data
public class UploadPictureResult {

    private String url;

    private String thumbnailUrl;

    private String picName;

    private Long picSize;

    private int picWidth;

    private int picHeight;

    private Double picScale;

    private String picFormat;

}



