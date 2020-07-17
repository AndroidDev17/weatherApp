package com.example.weatherapp.services

import com.example.weatherapp.data.CurrentWeather
import com.example.weatherapp.data.wrapper.ApiResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("current")
    fun getCurrentWeatherAsync(@Query("access_key") accessKey: String,
                               @Query("query") location: String) : Deferred<ApiResponse<CurrentWeather>>

    @GET("forecast")
    fun getFutureForecast(
        @Query("access_key") accessKey: String,
        @Query("query") location: String,
        @Query("forecast_days") days: Int
    )
}