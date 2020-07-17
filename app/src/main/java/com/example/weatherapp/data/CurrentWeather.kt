package com.example.weatherapp.data


import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @field:SerializedName("location")
    val location: Location,
    @field:SerializedName("current")
    val current: Weather,
    @field:SerializedName("error")
    val error: Error?,
    @field:SerializedName("success")
    val success: Boolean?
) {
    var id: Int = 0

    data class Location(
        val id: Int,
        @field:SerializedName("name")
        val name: String,
        @field:SerializedName("country")
        val country: String,
        @field:SerializedName("region")
        val region: String,
        @field:SerializedName("lat")
        val lat: String,
        @field:SerializedName("lon")
        val lon: String
    )

    data class Weather(
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

    data class Error(
        @field:SerializedName("code")
        val code: Int,
        @field:SerializedName("type")
        val type: String,
        @field:SerializedName("info")
        val info: String
    )
}