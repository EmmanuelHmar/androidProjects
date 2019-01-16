package com.emmanuelhmar.newsapp;

import com.google.gson.annotations.SerializedName;

public class Response {
    @SerializedName("response")
    private News news;

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }
}
