package com.demo.budjetkeeperver2.JSON.api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIFactory {
    private static APIFactory apiFactory;
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://apilayer.net/api/";

    private APIFactory() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    public static APIFactory getInstance() {
        if (apiFactory == null) {
            apiFactory = new APIFactory();
        }
        return apiFactory;
    }

    public APIService getApiService() { return retrofit.create(APIService.class); }
}
