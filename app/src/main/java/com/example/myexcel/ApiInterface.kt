package com.example.myexcel.Network


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("PDA/GetAllSKUsPDA") // Replace with your correct API endpoint
   // @GET("GRBAPI") // Replace with your correct API endpoint
    fun getSkuData(): Call<ResponseBody>
}
//http://192.168.1.12/GRBAPI