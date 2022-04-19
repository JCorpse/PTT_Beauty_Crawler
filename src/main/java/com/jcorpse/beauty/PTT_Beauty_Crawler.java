package com.jcorpse.beauty;

import com.jcorpse.beauty.constant.Constant;
import com.jcorpse.beauty.entity.Article;
import com.jcorpse.beauty.entity.WebList;
import com.jcorpse.beauty.entity.WebPage;
import com.jcorpse.beauty.http.HttpManager;
import com.jcorpse.beauty.parser.ArticleParser;
import com.jcorpse.beauty.parser.ListParser;
import com.jcorpse.beauty.util.ConfigUtil;
import com.jcorpse.beauty.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class PTT_Beauty_Crawler {
    private HttpManager Manager = HttpManager.getInstance();
    private int MaxPage = ConfigUtil.getIntResourceByKey("max.page");
    private int Interval = ConfigUtil.getIntResourceByKey("crawler.interval");
    private String FilePathRoot = ConfigUtil.getStringResourceByKey("file.root");

    public static void main(String[] args) {
        PTT_Beauty_Crawler Beauty = new PTT_Beauty_Crawler();
        Beauty.start();
    }

    public void start() {
        log.info("PTT_Beauty_Crawler Start");
        Manager.setCookie("over18", "1", "www.ptt.cc");
//        WebList Page = Crawler(Constant.START_URL);
//        if (MaxPage > 0) {
//            for (int i = 1; i <= MaxPage; ++i) {
//                Page = Crawler(Page.getPerv());
//            }
//        } else {
//            do {
//                Page = Crawler(Page.getPerv());
//            } while (Page.getPerv() != null);
//        }
        String Url = "https://www.ptt.cc/bbs/Beauty/M.1649492512.A.D60.html";
        Article ArticleData = new Article();
        WebPage ArticlePage = Manager.getBody(Url);
        ArticleData.setImgUrlList(ArticleParser.ArticleHandler(ArticlePage));
        List<String> ImgUrlList = ArticleData.getImgUrlList();
        log.info("Img total: {}",ImgUrlList.size());
        for (int i = 0; i < ImgUrlList.size(); ++i) {
            System.out.println(ImgUrlList.get(i));
        }
    }

    public WebList Crawler(String ListUrl) {
        WebPage ListPage = Manager.getBody(ListUrl);
        WebList WebList = ListParser.ListHandler(ListPage);
        List<Article> ArticleList = WebList.getArticleList();
        for (Article ArticleData : ArticleList) {
            if (!ArticleData.getCategory().matches("(公告)|(討論|(神人))")) {
                log.info("Now Crawler Title:{} Url:{}", ArticleData.getTitle(), ArticleData.getUrl());
                WebPage ArticlePage = Manager.getBody(ArticleData.getUrl());
                ArticleData.setImgUrlList(ArticleParser.ArticleHandler(ArticlePage));
                List<String> ImgUrlList = ArticleData.getImgUrlList();
                log.info("Img total: {}",ImgUrlList.size());
                for (int i = 0; i < ImgUrlList.size(); ++i) {
                    String ImgUrl = ImgUrlList.get(i);
                    String FileName = i + ImgUrl.replaceAll("(.+imgur.com/[a-zA-Z0-9]+)", "");
                    String FilePath = FilePathRoot + ArticleData.getTitle().replaceAll("[\\?*<\":>]","");
                    if(!FileUtil.isExist(FilePath+"/"+FileName)){
                        Manager.Download(ImgUrl, FilePath, FileName);
                        log.info("{} Download Done {}/{}",FileName,i+1,ImgUrlList.size());
                    }else {
                        log.info("{} isExist skip" ,FilePath+"/"+FileName);
                    }

                }
            }
        }
        return WebList;
    }

}
