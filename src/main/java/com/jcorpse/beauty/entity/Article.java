package com.jcorpse.beauty.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Article {
    private int Push;
    private String Category;
    private String Title;
    private String Url;
    private String Author;
    private List<String> ImgUrlList;
}
