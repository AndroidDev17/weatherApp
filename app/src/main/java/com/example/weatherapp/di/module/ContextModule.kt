package com.example.weatherapp.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule(private val application: Application) {
    @Singleton
    @Provides
    fun providesContext() = application.applicationContext

    @Singleton
    @Provides
    fun providesApplication() = application
}