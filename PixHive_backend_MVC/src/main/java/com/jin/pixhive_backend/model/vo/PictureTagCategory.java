package com.jin.pixhive_backend.model.vo;

import lombok.Data;

import java.util.List;

/**
 * picture tag menu view
 */
@Data
public class PictureTagCategory {
    private List<String> tagList;
    private List<String> categoryList;
}
