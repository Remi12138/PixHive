package com.jin.pixhive.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * update picture request
 */
@Data
public class PictureUpdateRequest implements Serializable {

    private Long id;

    private String name;

    private String introduction;

    private String category;

    private List<String> tags;

    private static final long serialVersionUID = 1L;
}




