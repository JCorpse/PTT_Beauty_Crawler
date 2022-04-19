package com.jcorpse.beauty.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class WebList {
    private String Next;
    private String Perv;
    private List<Article> ArticleList;
}
