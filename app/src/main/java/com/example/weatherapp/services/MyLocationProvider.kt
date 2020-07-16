package com.example.weatherapp.services

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.example.weatherapp.log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.conflate
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class MyLocationProvider constructor(private val client: FusedLocationProviderClient) {

    companion object {
        private const val TAG = "MyLocationProvider"
    }

    // last known location
    var currentLocation: Location? = null
        private set

    // provides location for
    val location: Flow<Location>

    init {
        location = getLocationFlow()
    }


    @ExperimentalCoroutinesApi
    @SuppressLint("MissingPermission")
    private fun getLocationFlow() = channelFlow<Location> {
        log(
            TAG,
            "creating location Flow"
        )
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                log(
                    TAG,
                    "location received"
                )
                super.onLocationResult(locationResult)
                if (locationResult?.lastLocation != null) {
                    currentLocation = locationResult.lastLocation
                    log(
                        TAG,
                        "location updated"
                    )
                    offer(locationResult.lastLocation)
                }
            }
        }
        val locationRequest = createLocationRequest(60, 30, 2)

        log(
            TAG,
            "requesting location"
        )
        client.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        awaitClose {
            log(
                TAG,
                "stop location update"
            )
            client.removeLocationUpdates(locationCallback)
        }
    }.conflate()

    private fun createLocationRequest(
        pInterval: Long,
        pFastestInterval: Long,
        pMaxWaitTime: Long
    ): LocationRequest {
        return LocationRequest().apply {
            interval = TimeUnit.SECONDS.toMillis(pInterval)
            fastestInterval = TimeUnit.SECONDS.toMillis(pFastestInterval)
            maxWaitTime = TimeUnit.MINUTES.toMillis(pMaxWaitTime)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

}