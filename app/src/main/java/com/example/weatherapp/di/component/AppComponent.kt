package com.example.weatherapp.di.component

import com.example.weatherapp.ui.WeatherFragment
import com.example.weatherapp.di.module.AppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(fragment: WeatherFragment)
}
