package com.jin.pixhive.interfaces.dto.picture;

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

    private String category = "Default";

    private List<String> tags;

    private static final long serialVersionUID = 1L;
}




