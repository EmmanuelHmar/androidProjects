package com.emmanuelhmar.newsapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
//    private static final String BASE_URL = "http://content.guardianapis.com/";
    private static final String BASE_URL = "https://api.github.com/";

    public static Retrofit getRetrofitInstance() {


        if (retrofit == null) {
//            retrofit = new Retrofit().Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
