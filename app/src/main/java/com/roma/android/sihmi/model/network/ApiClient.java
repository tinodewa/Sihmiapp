package com.roma.android.sihmi.model.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.roma.android.sihmi.utils.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static volatile ApiClient instance = null;
    private static Retrofit retrofit;

    public ApiClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder().baseUrl(Constant.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApiClient getInstance(){
        if (instance == null){
            synchronized (ApiClient.class){
                instance = new ApiClient();
            }
        }
        return instance;
    }


    public MasterService getApi(){
        return retrofit.create(MasterService.class);
    }

}
