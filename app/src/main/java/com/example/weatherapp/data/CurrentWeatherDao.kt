package com.example.weatherapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Interface for database access for current Weather related operations.
 */
@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(currentWeather: CurrentWeather)

    @Query("SELECT * FROM weather WHERE Location_latitude = :lat AND Location_longitude = :lon")
    fun loadCurrentWeather(lat: String, lon: String) : LiveData<CurrentWeather>

    @Query("SELECT * FROM weather WHERE id = :id")
    fun loadAllCurrentWeather(id:Int) : LiveData<CurrentWeather>

    @Query("SELECT * FROM weather")
    fun loadAllCurrent() : List<CurrentWeather>
}