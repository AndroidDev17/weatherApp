package com.example.weatherapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherapp.ApiBasedTest
import com.example.weatherapp.MainCoroutineRule
import com.example.weatherapp.MockResponseReader
import com.example.weatherapp.data.TodayWeather
import com.example.weatherapp.data.wrapper.ApiSuccessResponse
import com.example.weatherapp.util.ACCESS_KEY_OPEN_WEATHER
import com.example.weatherapp.util.TEMP_UNIT_CELSIUS
import com.example.weatherapp.util.log
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class RepoTest : ApiBasedTest() {
    private val TAG = "RepoTest"

    // execute each task synchronously using Architecture Components
    @get: Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing
    // dispatcher for testing
    @ExperimentalCoroutinesApi
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(testDispatcher)

    @Before
    fun setUp() {
        super.setupMockServer()
    }

    @After
    fun tearDown() {
        super.tearDownMockServer()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun currentWeatherApiTest() = runBlocking {
        withContext(mainCoroutineRule.dispatcher) {
            val service = getCurrentWeatherApi()
            setResponse(200,"current_weather_success.json")
            val currentWeatherAsync = service.getCurrentWeatherAsync(
                latitude = 28.459497,
                longitude = 77.026634,
                tempUnit = TEMP_UNIT_CELSIUS,
                accessKey = ACCESS_KEY_OPEN_WEATHER
            )
            val response = currentWeatherAsync.await()

            assertTrue(response is ApiSuccessResponse)

            val successResponse = response as ApiSuccessResponse

            val todayWeather = Gson().fromJson(
                MockResponseReader("current_weather_success.json").content,
                TodayWeather::class.java
            )

            log(TAG,response.body.toString())
            assert(successResponse.body == todayWeather)
        }
    }
}