package com.fedming.gdoulife.biz;

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
 * 解析图书搜索结果
 * Created by Bruce on 2016/10/15.
 */

public class BooksItemBiz {

    public static List<Map<String, String>> parseBooksItem(String htmlStr){

        //如果htmlStr为空，直接返回
        if (!StringUtils.isFine(htmlStr)) {
            return null;
        }
        List<Map<String, String>> booksList = new ArrayList<>();

        try {
            Document doc = Jsoup.parse(htmlStr, "http://210.38.138.7:8080/");
            Elements ai = doc.select("ul.list");
            Elements units = ai.select("li");

            for (int i = 0; i < units.size(); i++) {
                Map<String, String> map = new HashMap<>();
                Element unit_ele = units.get(i);

                String book = unit_ele.text();
                String bookInfo = "：暂无该数据";
                try {
                    bookInfo = book.split("著者")[1].split("出版社")[0];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String bookInfo1 = "：暂无";
                try {
                    bookInfo1 = book.split("出版社")[1].split("出版日期")[0];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String bookInfo2 = "：暂无该数据";
                try {
                    bookInfo2 = book.split("出版日期")[1];
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Element unit_ele_p = unit_ele.select("p").first();
                Element unit_ele_a = unit_ele_p.select("a").first();
                String bookName = unit_ele_a.text();
                String bookLink = unit_ele_a.attr("abs:href");

                map.put("bookName", bookName.split("、")[1]);
                map.put("bookLink", bookLink);
                map.put("bookInfo", String.format("著        者%s",bookInfo));
                map.put("bookInfo1", String.format("出        版%s%s",bookInfo1,"出版社"));
                map.put("bookInfo2", String.format("出版日期%s",bookInfo2.trim()));

                booksList.add(map);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return booksList;

    }

}
