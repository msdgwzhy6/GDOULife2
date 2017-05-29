package com.fedming.gdoulife.biz;

import com.fedming.gdoulife.app.AppConfig;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * 解析正方系统子菜单URL
 * Created by Bruce on 2016/10/25.
 */

public class SquareSystemBiz {

    /**
     * 解析menu菜单以获得子菜单的url
     *
     * @param content 网页html源码
     * @return 返回一个Map集合类, key为子菜单的title，value为url
     */
    public static Map<String, String> parseMenu(String content) {

        Map<String, String> map = new HashMap<>();
        Document document = Jsoup.parse(content);
        Elements elements = document.select("ul.nav a[target=zhuti]");
        for (Element element : elements) {
            map.put(element.text(), String.format("%s%s",AppConfig.REFERER,element.attr("href")));
        }
        return map;
    }

}
