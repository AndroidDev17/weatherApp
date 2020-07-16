package com.example.weatherapp.di.module

import android.app.Application
import androidx.room.Room
import com.example.weatherapp.MyDataBase
import com.example.weatherapp.data.CurrentWeatherDao
import com.example.weatherapp.data.WeatherDB
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ContextModule::class,ViewModelProviderModule::class, LocationModule::class, NetworkModule::class,TokenProviderModule::class])
object AppModule {

    @Singleton
    @Provides
    fun providesDB(application: Application): WeatherDB {
        return Room.databaseBuilder(application.applicationContext,WeatherDB::class.java, MyDataBase)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesUserDao(db: WeatherDB): CurrentWeatherDao {
        return db.currentWeatherDao()
    }
}