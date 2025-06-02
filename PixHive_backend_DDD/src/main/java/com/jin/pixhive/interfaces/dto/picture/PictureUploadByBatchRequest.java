package com.jin.pixhive.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * upload picture by batch request
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {

    /**
     * search text
     */
    private String searchText;

    /**
     * batch size
     */
    private Integer count = 10;

    /**
     * picture name prefix
     */
    private String namePrefix;

    private static final long serialVersionUID = 1L;
}




