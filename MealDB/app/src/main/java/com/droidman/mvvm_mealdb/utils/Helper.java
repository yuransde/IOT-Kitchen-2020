package com.droidman.mvvm_mealdb.utils;

import com.droidman.mvvm_mealdb.network.apis.MealDBAPI;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Helper {
    private static final String TAG = "Helper";

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static MealDBAPI restApi = retrofit.create(MealDBAPI.class);

    public static MealDBAPI getRestApi() {
        return restApi;
    }

}
