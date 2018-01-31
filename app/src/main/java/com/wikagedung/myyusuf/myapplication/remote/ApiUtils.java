package com.wikagedung.myyusuf.myapplication.remote;

/**
 * Created by Bagus on 31/07/2017.
 */

public class ApiUtils {
    private ApiUtils() {}

    public static final String BASE_URL = "https://www.wgdashboard.com:3000/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
