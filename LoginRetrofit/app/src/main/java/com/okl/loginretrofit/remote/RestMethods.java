package com.okl.loginretrofit.remote;

import com.google.gson.JsonElement;
import com.okl.loginretrofit.model.data;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestMethods {
    @POST("client/login")
//    public void login(@Query("id") String id, @Query("phone") String phone, Callback<MyResponse> callback);
    Call<data> login(@Query("id") String id, @Query("phone") String phone);
}
