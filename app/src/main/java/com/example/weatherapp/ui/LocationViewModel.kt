package com.example.weatherapp.ui

import android.location.Location
import androidx.lifecycle.*
import androidx.room.PrimaryKey
import com.example.weatherapp.EmptyLiveData
import com.example.weatherapp.reposetory.IWeatherRepository
import com.example.weatherapp.reposetory.WeatherRepository
import com.example.weatherapp.services.IAccessKeyProvider
import com.example.weatherapp.storage.KeyStoreProvider
import com.example.weatherapp.storage.LAST_KNOW_LOCATION
import com.example.weatherapp.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationViewModel (
    private val weatherRepository: IWeatherRepository,
    private val tokenProvider: IAccessKeyProvider,
    private val keyStore: KeyStoreProvider,
    coroutineScopeProvider: CoroutineScope?
) : ViewModel() {
    @Inject
    constructor(
        weatherRepository: IWeatherRepository,
        tokenProvider: IAccessKeyProvider,
        keyStore: KeyStoreProvider
    ) : this(weatherRepository, tokenProvider, keyStore, null)

    private val TAG = "LocationViewModel"
    private val _currentLocation = MutableLiveData<Location?>()
    private val coroutineScope = getViewModelScope(coroutineScopeProvider)
    val currentWeather = _currentLocation.distinctUntilChanged().switchMap { location ->
        if (location == null) {
            EmptyLiveData.create()
        } else {
            val loadTodaysWeather = weatherRepository.loadCurrentWeather(
                location.latitude,
                location.longitude,
                TEMP_UNIT_CELSIUS,
                ACCESS_KEY_OPEN_WEATHER
            )
            coroutineScope.launch {
                log(TAG,"startFetchMethod")
                loadTodaysWeather.begin()
            }
            loadTodaysWeather.asLiveData()
        }
    }
    val weatherForecast = _currentLocation.distinctUntilChanged().switchMap { location ->
        if (location == null) {
            EmptyLiveData.create()
        } else {
            val loadSevenDaysForecast = weatherRepository.loadWeatherForecast(
                location.latitude,
                location.longitude,
                EXCLUDE,
                TEMP_UNIT_CELSIUS,
                ACCESS_KEY_OPEN_WEATHER
            )
            coroutineScope.launch {
                loadSevenDaysForecast.begin()
            }
            loadSevenDaysForecast.asLiveData()
        }
    }

    fun storeLastLocation(location: Location) {
        coroutineScope.launch {
            keyStore.userSetting.putAsyncString(
                LAST_KNOW_LOCATION,
                location.toSharedPrefString()
            )
        }
    }

    fun loadDataFromLastLocation() {
        coroutineScope.launch {
            val location = keyStore.userSetting.getString(LAST_KNOW_LOCATION, "")
            location?.let {
                sharedPrefStringToLocation(location)
                    ?.let {
                        _currentLocation.value = (it)
                    }
            }
        }
    }

    fun updateLocation(location: Location?) {
        if (!location.isSame(_currentLocation.value)) {
            _currentLocation.value = location
        }
    }
}



