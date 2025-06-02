package com.jin.pixhive_backend.api.imagesearch.model;

import lombok.Data;

@Data
public class ImageSearchResult {

    private String thumbUrl;

    /**
     * source url
     */
    private String fromUrl;
}
