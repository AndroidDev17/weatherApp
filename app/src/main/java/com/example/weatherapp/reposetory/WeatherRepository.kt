package com.example.weatherapp.reposetory

import com.example.weatherapp.data.CurrentWeather
import com.example.weatherapp.data.TodayWeather
import com.example.weatherapp.data.WeatherForecast
import com.example.weatherapp.di.module.AppDispatchers
import com.example.weatherapp.network.NetworkLayerResource
import com.example.weatherapp.services.WeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val appDispatchers: AppDispatchers,
    private val serviceApi: WeatherApi
) : IWeatherRepository {
    private val TAG = "WeatherRepository"
    override fun loadCurrentWeather(
        lat: Double,
        lon: Double,
        tempUnit :String,
        accessKey: String
    ): NetworkLayerResource<TodayWeather> {
        return object : NetworkLayerResource<TodayWeather>(appDispatchers) {
            override fun createCallAsync() = serviceApi.getCurrentWeatherAsync(
                latitude = lat,
                longitude = lon,
                tempUnit =  tempUnit,
                accessKey = accessKey
            )
        }
    }


    override fun loadWeatherForecast(
        lat: Double,
        lon: Double,
        exclude: String,
        tempUnit: String,
        accessKey: String

    ): NetworkLayerResource<WeatherForecast> {
        return object : NetworkLayerResource<WeatherForecast>(appDispatchers) {
            override fun createCallAsync() =
                serviceApi.getWeatherForecastAsync(
                    latitude = lat,
                    longitude = lon,
                    exclude = exclude,
                    tempUnit = tempUnit,
                    accessKey = accessKey
                )
        }
    }

}