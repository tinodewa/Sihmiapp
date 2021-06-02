package com.roma.android.sihmi.model.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotifClient {
    private static volatile NotifClient instance = null;
    private static Retrofit retrofit;

    public NotifClient(String url){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        retrofit = new Retrofit.Builder().baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NotifClient getInstance(String url){
        if (instance == null){
            synchronized (ApiClient.class){
                instance = new NotifClient(url);
            }
        }
        return instance;
    }

    public SendNotifService getNotifService(){
        return retrofit.create(SendNotifService.class);
    }
}
