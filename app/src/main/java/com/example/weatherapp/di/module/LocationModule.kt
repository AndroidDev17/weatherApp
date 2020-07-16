package com.example.weatherapp.di.module

import android.content.Context
import com.example.weatherapp.services.MyLocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class LocationModule {

    @Provides
    @Singleton
    fun provideLocationClient(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun getProvideLocationPublisher(client:FusedLocationProviderClient): MyLocationProvider {
        return MyLocationProvider(client)
    }
}