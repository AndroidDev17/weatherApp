package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

data class ApiError (
    @SerializedName("cod") val code: Int,
    @SerializedName("message") val msg: String
)