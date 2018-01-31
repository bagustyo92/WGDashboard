package com.wikagedung.myyusuf.myapplication.remote;

/**
 * Created by Bagus on 31/07/2017.
 */

import com.wikagedung.myyusuf.myapplication.model.Post;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {
    @POST("/comment/1")
    @FormUrlEncoded
    Call<Post> savePost(@Field("name") String name,
                        @Field("comment") String comment,
                        @Field("location") String location,
                        @Field("month") String month,
                        @Field("year") String year);
}
