package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.di.component.AppComponent
import com.example.weatherapp.di.component.DaggerAppComponent
import com.example.weatherapp.di.module.ContextModule
import com.example.weatherapp.di.module.NetworkModule

class WeatherApp : Application() {

    lateinit var appComponent: AppComponent

    companion object {
        lateinit var instance: WeatherApp
    }

    override fun onCreate() {
        instance = this
        appComponent = DaggerAppComponent.builder()
            .networkModule(NetworkModule)
            .contextModule(ContextModule(this))
            .build()
        super.onCreate()
    }


}