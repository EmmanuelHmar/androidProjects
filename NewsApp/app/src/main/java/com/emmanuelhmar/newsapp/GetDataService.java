package com.emmanuelhmar.newsapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetDataService {
//    @GET("/search?api-key=test")
//    public Call<List<NewsContent>> getAllContent();

    @GET("/repos/{owner}/{repo}/contributors")
    Call<List<NewsContent>> contributors(
            @Path("owner") String owner,
            @Path("repo") String repo);
}
