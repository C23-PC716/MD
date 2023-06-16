package com.example.dermacare
import com.google.gson.annotations.SerializedName



data class PredictResponse(
    @SerializedName("nama")
    val nama: String,

    @SerializedName("deskripsi")
    val deskripsi: String
)

