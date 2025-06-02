package com.jin.pixhive.infrastructure.api.imagesearch;

import com.jin.pixhive.infrastructure.api.imagesearch.model.ImageSearchResult;
import com.jin.pixhive.infrastructure.exception.BusinessException;
import com.jin.pixhive.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import com.google.gson.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Slf4j
public class HiveReverseImageSearch {

    private static final String API_KEY = "xx";
    private static final String HIVE_API_URL = "https://api.thehive.ai/api/v2/task/sync";

    public static List<ImageSearchResult> searchByImageUrl(String imageUrl) {
        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("url", imageUrl)
                    .build();

            Request request = new Request.Builder()
                    .url(HIVE_API_URL)
                    .method("POST", body)
                    .addHeader("accept", "application/json")
                    .addHeader("authorization", "token " + API_KEY)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful() || response.body() == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,
                        "Hive API error: " + response.code() + " - " + response.message());
            }

            String responseBody = response.body().string();
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray matches = root
                    .getAsJsonArray("status")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("response")
                    .getAsJsonObject("output")
                    .getAsJsonArray("matches");

            List<ImageSearchResult> resultList = new ArrayList<>();
            for (JsonElement matchElement : matches) {
                JsonObject match = matchElement.getAsJsonObject();

                String url = match.get("url").getAsString();

                ImageSearchResult result = new ImageSearchResult();
                result.setFromUrl(url);

                resultList.add(result);
            }

            return resultList;
        } catch (Exception e) {
            log.error("Search failed", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Search failed");
        }
    }

    public static void main(String[] args) throws IOException {
//        String imageUrl = "https://hive-data-alt.s3.amazonaws.com/media_fixtures/image/corgi.webp";
        String imageUrl = "https://hips.hearstapps.com/hmg-prod/images/red-ripe-apples-on-a-white-wooden-background-royalty-free-image-1627315297.jpg";
        List<ImageSearchResult> results = searchByImageUrl(imageUrl);

        for (ImageSearchResult result : results) {
            System.out.println(result.getFromUrl());
        }
    }
}


