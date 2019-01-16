package com.emmanuelhmar.newsapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class News {
    @SerializedName("orderBy")
    private
    String orderBy;

    @SerializedName("results")
    private
    List<NewsContent> results;

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public List<NewsContent> getResults() {
        return results;
    }

    public void setResults(List<NewsContent> results) {
        this.results = results;
    }
}
