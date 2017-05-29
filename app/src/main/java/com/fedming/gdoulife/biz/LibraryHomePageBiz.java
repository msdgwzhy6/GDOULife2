package com.fedming.gdoulife.biz;

import android.support.annotation.Nullable;

import com.fedming.gdoulife.utils.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析图书馆首页——热门搜索
 * Created by Bruce on 2016/10/15.
 */

public class LibraryHomePageBiz {

    @Nullable
    public static List<Map<String, String>> parseHotItems(String htmlStr) {

        //如果htmlStr为空，直接返回
        if (!StringUtils.isFine(htmlStr)) {
            return null;
        }
        List<Map<String, String>> hotList = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(htmlStr, "http://210.38.138.7:8080/");
            Elements ai = doc.select("div.list").first().select("div.con");
            Element theySee = ai.last();
            if (!theySee.select("a[href]").isEmpty()) {

                Elements theySeeElements = theySee.select("a[href]");

                for (int i = 0; i < theySeeElements.size(); i++) {

                    Map<String, String> map = new HashMap<>();
                    Element element = theySeeElements.get(i);
                    String bookName = element.text();
                    String bookLnk = element.attr("abs:href");
                    map.put("bookName", bookName);
                    map.put("bookLink", bookLnk);

                    hotList.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hotList;

    }
}
