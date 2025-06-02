package com.jin.pixhive.infrastructure.api.imagesearch.sub;

import com.jin.pixhive.infrastructure.exception.BusinessException;
import com.jin.pixhive.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GetImageFirstUrlApi {

    /**
     * get Image First Url
     */
    public static String getImageFirstUrl(String url) {
        try {
            // get HTML by Jsoup
            Document document = Jsoup.connect(url)
                    .timeout(5000)
                    .get();

            // get elements with <script>
            Elements scriptElements = document.getElementsByTag("script");

            // find "firstUrl"
            for (Element script : scriptElements) {
                String scriptContent = script.html();
                if (scriptContent.contains("\"firstUrl\"")) {
                    // regular expression extracts the value of firstUrl
                    Pattern pattern = Pattern.compile("\"firstUrl\"\\s*:\\s*\"(.*?)\"");
                    Matcher matcher = pattern.matcher(scriptContent);
                    if (matcher.find()) {
                        String firstUrl = matcher.group(1);
                        // process escape characters
                        firstUrl = firstUrl.replace("\\/", "/");
                        return firstUrl;
                    }
                }
            }

            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Didn't find url");
        } catch (Exception e) {
            log.error("Search failed", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Search failed");
        }
    }

    public static void main(String[] args) {
        // request URL
        String url = "https://graph.baidu.com/s?card_key=&entrance=GENERAL&extUiData[isLogoShow]=1&f=all&isLogoShow=1&session_id=1026661216843778369&sign=126e4e97cd54acd88139901745004831&tpl_from=pc";
        String imageFirstUrl = getImageFirstUrl(url);
        System.out.println("Search success, result URL: " + imageFirstUrl);
    }
}

