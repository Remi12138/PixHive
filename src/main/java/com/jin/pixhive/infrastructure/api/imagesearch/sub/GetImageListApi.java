package com.jin.pixhive.infrastructure.api.imagesearch.sub;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jin.pixhive.infrastructure.api.imagesearch.model.ImageSearchResult;
import com.jin.pixhive.infrastructure.exception.BusinessException;
import com.jin.pixhive.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class GetImageListApi {

    /**
     * get image list
     */
    public static List<ImageSearchResult> getImageList(String url) {
        try {
            // GET
            HttpResponse response = HttpUtil.createGet(url).execute();

            // response
            int statusCode = response.getStatus();
            String body = response.body();

            if (statusCode == 200) {
                return processResponse(body);
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Interface call failed");
            }
        } catch (Exception e) {
            log.error("Get image list failed", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Get image list failed");
        }
    }

    /**
     * process Response
     *
     * @param responseBody Json returned by interface
     */
    private static List<ImageSearchResult> processResponse(String responseBody) {
        // parse
        JSONObject jsonObject = new JSONObject(responseBody);
        if (!jsonObject.containsKey("data")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Get image list failed");
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (!data.containsKey("list")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Get image list failed");
        }
        JSONArray list = data.getJSONArray("list");
        return JSONUtil.toList(list, ImageSearchResult.class);
    }

    public static void main(String[] args) {
        String url = "https://graph.baidu.com/s?card_key=&entrance=GENERAL&extUiData[isLogoShow]=1&f=all&isLogoShow=1&session_id=1026661216843778369&sign=126e4e97cd54acd88139901745004831&tpl_from=pc";
        List<ImageSearchResult> imageList = getImageList(url);
        System.out.println("Search success: " + imageList);
    }
}


