package com.emmanuelhmar.newsapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {
    //    sections?q=world&api-key=test
//    @GET("/search?api-key=test")
//    @GET("/{section}api-key=test")
    @GET("/search")
    Call<Response> getAllContent(@Query("q") String searchTerm, @Query("order-by") String orderBy,
                                        @Query("section") String sectionName, @Query("api-key") String key);

//    @GET("/repos/{owner}/{repo}/contributors")
//    Call<List<NewsContent>> contributors(
//            @Path("owner") String owner,
//            @Path("repo") String repo);
}
