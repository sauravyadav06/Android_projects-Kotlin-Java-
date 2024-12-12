package com.example.loginandsignup

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET


    interface ApiService {
    @GET("7")
    abstract fun getData(): Call<ResponseBody>

    }

interface ApiInterface {
    @GET("/users")
    fun getData(): Call<List<UserDataClassItem>>
}