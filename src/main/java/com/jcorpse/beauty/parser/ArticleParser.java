package com.jcorpse.beauty.parser;

import com.jcorpse.beauty.entity.Article;
import com.jcorpse.beauty.entity.WebList;
import com.jcorpse.beauty.entity.WebPage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ArticleParser {

    public static List<String> ArticleHandler(WebPage WebPage) {
        List<String> ImgUrlList = new ArrayList<String>();
        Document Doc = Jsoup.parse(WebPage.getBody());
        Element ContentElement = Doc.getElementById("main-content");
        Elements ImgElements = ContentElement.select(" > a[rel=noreferrer noopener nofollow]");
        for (Element ImgElement : ImgElements) {
            if (ImgElement.hasAttr("href")) {
                if (ImgElement.attr("href").matches("(.+i.imgur.com.+(jpg|.jpeg|gif|png|webp))")) {
                    ImgUrlList.add(ImgElement.attr("href"));
                }else if(ImgElement.attr("href").matches("(.+imgur.com.+)")){
                    ImgUrlList.add(ImgElement.attr("href").replaceAll("(imgur.com)","i.imgur.com")+".jpg");
                }
            }
        }
        return ImgUrlList;
    }
}
