package com.example.weatherapp.util

import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.ApiError
import com.example.weatherapp.data.TodayWeather
import com.example.weatherapp.data.WeatherForecast
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*

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

fun getDateTime(epoch: Long): String? {
    try {
        val sdf = SimpleDateFormat("EEE, MMM d",Locale.getDefault())
        return sdf.format(Date(epoch))
    } catch (e: Exception) {
        return "unknown"
    }
}

fun Location?.isSame(value: Location?): Boolean =
    this?.latitude == value?.latitude && this?.longitude == value?.longitude


fun WeatherForecast.isError(): Boolean = this.cod != null && this.cod != 200 && this.message?.isNotEmpty() ?: true

fun TodayWeather.isError(): Boolean = this.cod != 200 && this.message?.isNotEmpty() ?: true

fun getApiErrorMessage(errorMessage: String?): String {
    val apiError: ApiError? = if (errorMessage.isNullOrEmpty()) {
        ApiError(UNKNOWN_ERROR_CODE, errorMessage ?: "Unknown Error")
    } else {
        try {
            Gson().fromJson(errorMessage, ApiError::class.java)
        } catch (e: Exception) {
            null
        }
    }
    return apiError?.msg ?: "unknown error"
}

fun ViewModel.getViewModelScope(coroutineScope: CoroutineScope?) =
    coroutineScope ?: this.viewModelScope

/**
 * return false above LOLLIPOP, and true if below network
 * connected and below LOLLIPOP
 */
fun ConnectivityManager.isOnline(): Boolean {
    val networkInfo: NetworkInfo? = activeNetworkInfo
    return networkInfo?.isConnected == true
}
