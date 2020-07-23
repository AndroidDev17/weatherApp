package com.example.weatherapp.ui

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.core.app.ApplicationProvider

import androidx.test.platform.app.InstrumentationRegistry
import com.example.weatherapp.BaseMockServerTest
import com.example.weatherapp.MainCoroutineRule
import com.example.weatherapp.WeatherApp
import com.example.weatherapp.data.Status
import com.example.weatherapp.di.module.AppDispatchers
import com.example.weatherapp.getOrAwaitValue
import com.example.weatherapp.reposetory.IWeatherRepository
import com.example.weatherapp.reposetory.WeatherRepository
import com.example.weatherapp.services.AccessKeyProvider
import com.example.weatherapp.storage.KeyStoreProvider
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class LocationViewModelTest :BaseMockServerTest() {
    private  val TAG = "LocationViewModelTest"
    // execute each task synchronously using Architecture Components
    @get: Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var sut : LocationViewModel

    // dispatcher for testing
    @ExperimentalCoroutinesApi
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    // repo for testing

    private lateinit var weatherRepo : IWeatherRepository

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        super.before()
        val appDispatchers = AppDispatchers(testDispatcher,testDispatcher,testDispatcher)
        val service = getCurrentWeatherApi()
        weatherRepo = WeatherRepository(appDispatchers,service)
        // ApplicationProvider.getApplicationContext<WeatherApp>()
        val applicationContext = ApplicationProvider.getApplicationContext<WeatherApp>()
        sut = LocationViewModel(weatherRepo,AccessKeyProvider(), KeyStoreProvider(applicationContext))
    }

    @After
    fun tearDown() {
        super.after()
    }

    @Test
    fun getWeatherDataTest() {
        // Location is given
        val location = Location("test provider")
        location.latitude = 28.459497
        location.longitude = 77.026634
        setResponse("current_weather_success.json",200)
        sut.updateLocation(location)
        val beforeCall = sut.currentWeather.getOrAwaitValue()
        testDispatcher.pauseDispatcher()
        assertEquals(beforeCall.status, Status.LOADING)
        assertTrue(beforeCall.data == null)
        testDispatcher.resumeDispatcher()
    }
}