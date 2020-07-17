package com.example.weatherapp.services

import com.example.weatherapp.util.ACCESS_KEY_WEATHER_STACK
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessKeyProvider @Inject constructor() : IAccessKeyProvider {
    override suspend fun provideAccessKey(): String {
        delay(100)
        return ACCESS_KEY_WEATHER_STACK
    }
}