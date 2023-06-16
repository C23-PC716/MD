package com.example.dermacare

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("/predict")
    fun postData(@Part image: MultipartBody.Part): Call<PredictResponse>
}
