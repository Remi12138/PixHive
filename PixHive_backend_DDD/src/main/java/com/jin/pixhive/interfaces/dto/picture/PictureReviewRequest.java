package com.jin.pixhive.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * picture review request
 */
@Data
public class PictureReviewRequest implements Serializable {

    private Long id;

    /**
     * review status：0-reviewing; 1-pass; 2-reject
     */
    private Integer reviewStatus;

    private String reviewMessage;

    private static final long serialVersionUID = 1L;
}

