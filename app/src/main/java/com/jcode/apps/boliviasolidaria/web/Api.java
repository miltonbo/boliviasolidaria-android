package com.jcode.apps.boliviasolidaria.web;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api{

    public static final String URL = "https://boliviasolidaria.com/bs-api/";
    private static Retrofit retrofit;

    public static ApiInterface getService(){
        return getRetrofitInstance().create(ApiInterface.class);
    }

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .build();
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
