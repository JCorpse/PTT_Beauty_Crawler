package com.jcorpse.beauty.parser;

import com.jcorpse.beauty.constant.Constant;
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
public class ListParser {
    public static WebList ListHandler(WebPage Webpage) {
        WebList WebList = new WebList();
        Document Doc = Jsoup.parse(Webpage.getBody());
        Element PervElement = Doc.selectXpath("//*[@id=\"action-bar-container\"]/div/div[2]/a[2]").get(0);
        if (PervElement.hasAttr("href")) {
            WebList.setPerv(Constant.Domain + PervElement.attr("href"));
        }
        Element NextElement = Doc.selectXpath("//*[@id=\"action-bar-container\"]/div/div[2]/a[3]").get(0);
        if (NextElement.hasAttr("href")) {
            WebList.setNext(Constant.Domain + NextElement.attr("href"));
        }
        Elements ArticleElements = Doc.select("div.r-list-container.action-bar-margin.bbs-screen > div.r-ent");
        if (ArticleElements.size() > 0) {
            List<Article> ArticleList = new ArrayList<Article>();
            for (Element ArticleElement : ArticleElements) {
                if (ArticleElement.selectFirst("div.title > a") == null) {
                    continue;
                }
                Article ArticleData = new Article();
                if (ArticleElement.select("div.nrec > span").size() < 0) {
                    ArticleData.setPush(Integer.valueOf(ArticleElement.selectFirst("div.nrec > span").text()));
                } else {
                    ArticleData.setPush(0);
                }
                Element TitleElement = ArticleElement.selectFirst("div.title > a");
                ArticleData.setTitle(TitleElement.text());
                ArticleData.setUrl(Constant.Domain + TitleElement.attr("href"));
                ArticleData.setCategory(TitleElement.text().substring(0, TitleElement.text().indexOf(" ")).replaceAll("[\\]\\[]", "").trim());
                Element MetaElement = ArticleElement.selectFirst("div.meta");
                ArticleData.setAuthor(MetaElement.selectFirst("div.author").text());
                ArticleList.add(ArticleData);
            }
            WebList.setArticleList(ArticleList);
        }
        return WebList;
    }
}
