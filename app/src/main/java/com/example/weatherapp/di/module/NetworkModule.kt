package com.example.weatherapp.di.module

import com.example.weatherapp.util.BASE_URL
import com.example.weatherapp.network.DeferredCallAdapterFactory
import com.example.weatherapp.services.WeatherApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideWeatherService(client: OkHttpClient): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(DeferredCallAdapterFactory())
            .build()
            .create(WeatherApi::class.java)
    }

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder().build()
}