package com.jin.pixhive_backend.api.imagesearch;

import com.jin.pixhive_backend.api.imagesearch.model.ImageSearchResult;
import com.jin.pixhive_backend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.jin.pixhive_backend.api.imagesearch.sub.GetImageListApi;
import com.jin.pixhive_backend.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageSearchApiFacade {

    /**
     * search similar image
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;
    }

    public static void main(String[] args) {
        // test search similar image
        String imageUrl = "https://www.xx/logo.png";
        List<ImageSearchResult> resultList = searchImage(imageUrl);
        System.out.println("result list: " + resultList);
    }
}

