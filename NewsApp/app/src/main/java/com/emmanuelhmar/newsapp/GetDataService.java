package com.emmanuelhmar.newsapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {
    @GET("/search?api-key=test")
    public Call<Response> getAllContent();

//    @GET("/repos/{owner}/{repo}/contributors")
//    Call<List<NewsContent>> contributors(
//            @Path("owner") String owner,
//            @Path("repo") String repo);
}
