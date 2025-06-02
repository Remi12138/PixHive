package com.jin.pixhive.infrastructure.manage.upload.model.dto.file;

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

    private String picColor;

}



