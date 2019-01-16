package com.emmanuelhmar.newsapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class JSONResponse<T> {
//    private NewsContent[] news;
    @SerializedName("results")
    List<NewsContent> newsContents;
    List<T> items;

    public JSONResponse() {
        newsContents = new ArrayList<>();
    }

    public List<NewsContent> getNewsContents() {
        return newsContents;
    }

    public static JSONResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        JSONResponse jsonResponse = gson.fromJson(response, JSONResponse.class);
        return jsonResponse;
    }

}
