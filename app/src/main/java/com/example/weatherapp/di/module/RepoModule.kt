package com.example.weatherapp.di.module

import com.example.weatherapp.reposetory.IWeatherRepository
import com.example.weatherapp.reposetory.WeatherRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RepoModule {
    @Binds
    abstract fun bindWeatherRepo(repo: WeatherRepository): IWeatherRepository

}