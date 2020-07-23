package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

data class WeatherForecast(

    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("timezone_offset") val timezone_offset: Int,
    @SerializedName("current") val current: Current,
    @SerializedName("daily") val daily: List<DailyForecast>,
    @SerializedName("cod") val cod: Int?,
    @SerializedName("message") val message: String?
)