package com.example.weatherapp.services

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.CurrentWeather
import com.example.weatherapp.reposetory.ApiResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("current")
    fun getCurrentWeatherAsync(@Query("access_key") accessKey: String,
                               @Query("query") location: String) : LiveData<ApiResponse<CurrentWeather>>

    @GET("forecast")
    fun getFutureForecast(
        @Query("access_key") accessKey: String,
        @Query("query") location: String,
        @Query("forecast_days") days: Int
    )
}