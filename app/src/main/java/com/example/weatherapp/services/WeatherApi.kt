package com.example.weatherapp.services

import com.example.weatherapp.data.TodayWeather
import com.example.weatherapp.data.WeatherForecast
import com.example.weatherapp.data.wrapper.ApiResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("onecall")
    fun getWeatherForecastAsync(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String,
        @Query("units") tempUnit: String,
        @Query("appid") accessKey: String
    ): Deferred<ApiResponse<WeatherForecast>>


    @GET("weather")
    fun getCurrentWeatherAsync(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") tempUnit: String,
        @Query("appid") accessKey: String
    ): Deferred<ApiResponse<TodayWeather>>


}