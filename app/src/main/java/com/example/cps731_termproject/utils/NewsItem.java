package com.example.cps731_termproject.utils;

public class NewsItem {

    String title;
    String desc;
    String author;
    String source;
    String urlToSource;
    String urlToImage;

    public NewsItem(){
        this.title = "temp title";
        this.desc = "temp desc";
        this.author = "temp author";
        this.source = "temp source";
        this.urlToSource = "temp url";
        this.urlToImage = "temp url";
    }

    public NewsItem(String title, String desc, String author, String source, String urlToSource, String urlToImage){
        this.title = title;
        this.desc = desc;
        this.author = author;
        this.source= source;
        this.urlToSource = urlToSource;
        this.urlToImage = urlToImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrlToSource() {
        return urlToSource;
    }

    public void setUrlToSource(String urlToSource) {
        this.urlToSource = urlToSource;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

}
