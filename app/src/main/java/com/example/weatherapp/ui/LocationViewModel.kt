package com.example.weatherapp.ui

import android.location.Location
import androidx.lifecycle.*
import com.example.weatherapp.ACCESS_KEY
import com.example.weatherapp.EmptyLiveData
import com.example.weatherapp.data.CurrentWeather
import com.example.weatherapp.reposetory.Resource
import com.example.weatherapp.reposetory.WeatherRepository
import com.example.weatherapp.services.IAccessKeyProvider
import com.example.weatherapp.sharedPrefStringToLocation
import com.example.weatherapp.storage.KeyStoreProvider
import com.example.weatherapp.storage.LAST_KNOW_LOCATION
import com.example.weatherapp.toSharedPrefString
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class LocationViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val tokenProvider: IAccessKeyProvider,
    private val keyStore: KeyStoreProvider
) : ViewModel() {
    private val TAG = "LocationViewModel"
    private val _currentLocation = MutableLiveData<Location?>()
    val currentWeather = _currentLocation.switchMap { location ->
        if (location == null) {
            EmptyLiveData.create()
        } else {
            weatherRepository.loadCurrentWeather(ACCESS_KEY,location.latitude,location.longitude)
        }
    }

    fun loadLocation(
        latitude: Double,
        longitude: Double
    ): LiveData<Resource<CurrentWeather>> {
//        val accessToken = runBlocking{ tokenProvider.provideAccessKey() }
        return weatherRepository.loadCurrentWeather(ACCESS_KEY,latitude,longitude)
    }

    fun storeLastLocation(location: Location) {
        viewModelScope.launch {
            keyStore.userSetting.putAsyncString(
                LAST_KNOW_LOCATION,
                location.toSharedPrefString()
            )
        }
    }

    fun loadDataFromLastLocation() {
        viewModelScope.launch {
            val location = keyStore.userSetting.getString(LAST_KNOW_LOCATION, "")
            location?.let {
                sharedPrefStringToLocation(location)?.let {
                    _currentLocation.value = (it)
                }
            }
        }
    }

}