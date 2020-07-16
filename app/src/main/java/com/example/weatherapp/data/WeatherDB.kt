package com.example.weatherapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * application master database description.
 */
@Database(
    entities = [CurrentWeather::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(ListConverter::class)
abstract class WeatherDB : RoomDatabase() {
    abstract fun currentWeatherDao(): CurrentWeatherDao

}