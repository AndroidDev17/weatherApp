package com.example.weatherapp.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "weather")
data class CurrentWeather (
    @field:SerializedName("location")
    @field:Embedded(prefix = "Location_")
    val location : Location,
    @field:SerializedName("current")
    @Embedded (prefix = "current_")
    val current : Weather
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int =0
    data class Location (
        val id: Int,
        @field:SerializedName("name")
        val name: String,
        @field:SerializedName("country")
        val country: String,
        @field:SerializedName("region")
        val region: String,
        @field:SerializedName("lat")
        @ColumnInfo(name = "latitude")
        val lat: String,
        @field:SerializedName("lon")
        @ColumnInfo(name = "longitude")
        val lon: String
    )
    data class Weather (
        @field:SerializedName("observation_time")
        val time: String,
        @field:SerializedName("temperature")
        val temp: String,
        @field:SerializedName("weather_code")
        val weatherCode: String,
        @field:SerializedName("weather_icons")
        val icons: ArrayList<String?>?,
        @field:SerializedName("weather_descriptions")
        val descriptions: ArrayList<String?>?,
        @field:SerializedName("visibility")
        val visibility: String,
        @field:SerializedName("is_day")
        val isDay: String
    )
}