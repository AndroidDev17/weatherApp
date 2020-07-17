package com.example.weatherapp.util

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.weatherapp.BuildConfig

fun Context.toast(message: String?) {
    Toast.makeText(this, message ?: "null", Toast.LENGTH_LONG).show()
}

fun log(tag: String, message: String?) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, message ?: "nothing to show")
    }
}

fun Location.toSharedPrefString(): String {
    return "$latitude,$longitude"
}

fun sharedPrefStringToLocation(locationStr: String): Location? {
    if (locationStr.trim().isNotEmpty()
        && locationStr.contains(",")) {
        val latLong = locationStr.split(",")
        if (latLong.size == 2
            && latLong[0].isNotEmpty()
            && latLong[1].isNotEmpty()
        ) {
            try {
                return Location("shared pref").apply {
                    latitude = latLong[0].toDouble()
                    longitude = latLong[1].toDouble()
                }
            } catch (e: NumberFormatException) {
                return null
            }
        }
    }
    return null
}
fun ProgressBar.visible() {
    visibility = View.VISIBLE
}
fun ProgressBar.hide() {
    visibility = View.GONE
}
fun convertToCelsius(temp:String) = "$temp \u2103"
