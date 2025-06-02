package com.jin.pixhive.infrastructure.api.imagesearch.sub;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.jin.pixhive.infrastructure.exception.BusinessException;
import com.jin.pixhive.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GetImagePageUrlApi {

    /**
     * get image page url
     */
    public static String getImagePageUrl(String imageUrl) {
        // 1. request params
        Map<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        // get the current timestamp
        long uptime = System.currentTimeMillis();
        // request url
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;

        try {
            // 2. send POST to baidu
            HttpResponse response = HttpRequest.post(url)
                    // 这里需要指定acs-token 不然会响应系统异常
                    .header("acs-token", RandomUtil.randomString(1))
                    .form(formData)
                    .timeout(5000)
                    .execute();

            // check response status
            if (HttpStatus.HTTP_OK != response.getStatus()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Interface call failed");
            }
            // parse response
            String responseBody = response.body();
            Map<String, Object> result = JSONUtil.toBean(responseBody, Map.class);

            // 3. process response
            if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Interface call failed");
            }
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            String rawUrl = (String) data.get("url");
            // decode url
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            // if url == null
            if (searchResultUrl == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "return null URL");
            }
            return searchResultUrl;
        } catch (Exception e) {
            log.error("Search failed", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Search failed");
        }
    }

    public static void main(String[] args) {
        // Test the similar image search function
        String imageUrl = "https://www.xx/logo.png";
        String result = getImagePageUrl(imageUrl);
        System.out.println("Search success, result URL: " + result);
    }
}
