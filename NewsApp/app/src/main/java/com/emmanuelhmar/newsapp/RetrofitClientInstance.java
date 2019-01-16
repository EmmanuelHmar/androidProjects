package com.emmanuelhmar.newsapp;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://content.guardianapis.com/";
//    private static final String BASE_URL = "https://api.github.com/";

    public static Retrofit getRetrofitInstance() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(loggingInterceptor);


        if (retrofit == null) {
//            retrofit = new Retrofit().Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory
                    (GsonConverterFactory.create()).client(httpClient.build()).build();
        }
        return retrofit;
    }
}
