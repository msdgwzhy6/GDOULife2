package com.fedming.gdoulife.biz;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 获取__VIEWSTATE、__VIEWSTATEGENERATOR的值
 * Created by Bruce on 2016/10/26.
 */

public class ParseViewStateValue {

    public static Map<String, String> getViewStateValue(String html) {

        Map<String, String> viewStateValue = new LinkedHashMap<>();

        if (null != html) {
            Document document = Jsoup.parse(html);
            Element viewStateElement =
                    document.select("input[name=\"__VIEWSTATE\"]").first();
            Element viewStateGeneratorElement =
                    document.select("input[name=\"__VIEWSTATEGENERATOR\"]").first();
            if (null != viewStateElement) {
                viewStateValue.put("__VIEWSTATE",
                        viewStateElement.attr("value"));
            }
            if (null != viewStateGeneratorElement) {
                viewStateValue.put("__VIEWSTATEGENERATOR",
                        viewStateGeneratorElement.attr("value"));
            }
        }
        return viewStateValue;
    }
}
