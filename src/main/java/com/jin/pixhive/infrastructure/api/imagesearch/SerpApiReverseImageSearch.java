package com.jin.pixhive.infrastructure.api.imagesearch;

import com.google.gson.*;
import com.jin.pixhive.infrastructure.api.imagesearch.model.ImageSearchResult;
import com.jin.pixhive.infrastructure.exception.BusinessException;
import com.jin.pixhive.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.fluent.Request;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Slf4j
public class SerpApiReverseImageSearch {

    private static final String API_KEY = "xx";

    public static List<ImageSearchResult> searchSimilarImages(String imageUrl) {
        List<ImageSearchResult> results = new ArrayList<>();
        try {
            // Encode the image URL
            String encodedUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString());
            String endpoint = String.format("https://serpapi.com/search.json?engine=google_reverse_image&image_url=%s&api_key=%s",
                    encodedUrl, API_KEY);

            // Send the request
            String response = Request.Get(endpoint)
                    .connectTimeout(5000)
                    .socketTimeout(5000)
                    .execute()
                    .returnContent()
                    .asString();

            System.out.println("RAW RESPONSE:\n" + response);
            // Parse the response
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            JsonArray matches = json.getAsJsonArray("visual_matches");

            if (matches != null && matches.size() > 0) {
                for (JsonElement element : matches) {
                    JsonObject obj = element.getAsJsonObject();

                    ImageSearchResult result = new ImageSearchResult();
                    result.setThumbUrl(obj.has("thumbnail") ? obj.get("thumbnail").getAsString() : null);
                    result.setFromUrl(obj.has("link") ? obj.get("link").getAsString() : null);

                    results.add(result);
                }
            }
        } catch (Exception e) {
            log.error("Search failed", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Search failed");
        }
        return results;
    }

    // Test
    public static void main(String[] args) {
        String imageUrl = "https://hips.hearstapps.com/hmg-prod/images/red-ripe-apples-on-a-white-wooden-background-royalty-free-image-1627315297.jpg";
        List<ImageSearchResult> similarImages = searchSimilarImages(imageUrl);
        similarImages.forEach(img -> System.out.println(img.getFromUrl()));
    }
}

