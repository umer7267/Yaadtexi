package com.travel.taxi.Connection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnection {

    private static Retrofit retrofit = null;

    public static Retrofit getClient (String baseUrl){
        if (retrofit == null){
//            Gson gson = new GsonBuilder()
//                    .setLenient()
//
//                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
