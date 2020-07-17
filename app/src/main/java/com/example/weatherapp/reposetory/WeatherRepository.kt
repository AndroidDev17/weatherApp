package com.example.weatherapp.reposetory

import com.example.weatherapp.data.CurrentWeather
import com.example.weatherapp.di.module.AppDispatchers
import com.example.weatherapp.network.NetworkLayerResource
import com.example.weatherapp.services.WeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val appDispatchers: AppDispatchers,
    private val serviceApi: WeatherApi
) {
    private val TAG = "WeatherRepository"
    fun loadCurrentWeather(
        accessKey: String,
        lat: Double,
        lon: Double
    ): NetworkLayerResource<CurrentWeather> {
        return object : NetworkLayerResource<CurrentWeather>(appDispatchers) {
            override fun createCallAsync() = serviceApi.getCurrentWeatherAsync(accessKey, "$lat,$lon")
        }
    }

}