package com.example.weatherapp.reposetory

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.CurrentWeather
import com.example.weatherapp.di.module.AppDispatchers
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
    ): LiveData<Resource<CurrentWeather>> {
        return object : NetworkBoundResourceNew<CurrentWeather>(appDispatchers) {
            override fun createCall() = serviceApi.getCurrentWeatherAsync(accessKey, "$lat,$lon")
        }.asLiveData()
    }

}