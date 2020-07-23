package com.example.weatherapp.reposetory

import com.example.weatherapp.data.TodayWeather
import com.example.weatherapp.data.WeatherForecast
import com.example.weatherapp.network.NetworkLayerResource

interface IWeatherRepository {
    fun loadCurrentWeather(
        lat: Double,
        lon: Double,
        tempUnit : String,
        accessKey: String
    ): NetworkLayerResource<TodayWeather>

    fun loadWeatherForecast(
        lat: Double,
        lon: Double,
        exclude: String,
        tempUnit: String,
        accessKey: String
    ): NetworkLayerResource<WeatherForecast>
}