package com.wikagedung.myyusuf.myapplication.remote;

/**
 * Created by Bagus on 31/07/2017.
 */

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Log.v("Disini", "Aja");
        }
        return retrofit;
    }
}
