package com.jin.pixhive_backend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * edit picture request
 */
@Data
public class PictureEditRequest implements Serializable {

    private Long id;

    private String name;

    private String introduction;

    private String category;

    private List<String> tags;

    private static final long serialVersionUID = 1L;
}




