package com.example.weatherapp.ui

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherapp.*
import com.example.weatherapp.data.Status
import com.example.weatherapp.data.TodayWeather
import com.example.weatherapp.data.wrapper.ApiSuccessResponse
import com.example.weatherapp.di.module.AppDispatchers
import com.example.weatherapp.reposetory.WeatherRepository
import com.example.weatherapp.services.AccessKeyProvider
import com.example.weatherapp.services.WeatherApi
import com.example.weatherapp.storage.KeyStoreProvider
import com.example.weatherapp.util.ACCESS_KEY_OPEN_WEATHER
import com.example.weatherapp.util.TEMP_UNIT_CELSIUS
import com.example.weatherapp.util.log
import com.google.gson.Gson
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class LocationViewModel2Test : ApiBasedTest() {
    private val TAG = "LocationViewModelTest"

    // execute each task synchronously using Architecture Components
    @get: Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // actual class to test
    private lateinit var sut: LocationViewModel


    @ExperimentalCoroutinesApi
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
    fun getCurrentWeatherSuccess_WhenUpdateLocationTest() = runBlocking {
        withContext(mainCoroutineRule.dispatcher) {
            // test setup
            val appDispatchers = AppDispatchers(
                mainCoroutineRule.dispatcher,
                mainCoroutineRule.dispatcher,
                mainCoroutineRule.dispatcher
            )
            val weatherApi = getCurrentWeatherApi()
            val weatherRepo = WeatherRepository(appDispatchers, weatherApi)
            val applicationContext = ApplicationProvider.getApplicationContext<WeatherApp>()
            sut = LocationViewModel(
                weatherRepo,
                AccessKeyProvider(),
                KeyStoreProvider(applicationContext),
                TestCoroutineScope(mainCoroutineRule.dispatcher)
            )
            //
            // Location is given
            val location = Location("test provider")
            location.latitude = 28.459497
            location.longitude = 77.026634
            // success response
            setResponse(200, "current_weather_success.json")

            // action updating location
            sut.updateLocation(location)

            // get live value
            val weatherResponseLiveData = sut.currentWeather.getOrAwaitValue()
            // stop dispatcher to observe live data value
            mainCoroutineRule.dispatcher.pauseDispatcher()
            // checking values
            assertEquals(weatherResponseLiveData.status, Status.LOADING)

            assertTrue(weatherResponseLiveData.data == null)
            // forward dispatcher show that live data can emit again
            mainCoroutineRule.dispatcher.resumeDispatcher()
//            mainCoroutineRule.dispatcher.advanceTimeBy(2000)
            // get live value
            val weatherResponseFromApiLiveData = sut.currentWeather.getOrAwaitValue()
            log(
                TAG,
                "status :: ${weatherResponseFromApiLiveData.status} , data :: ${weatherResponseFromApiLiveData.data} , message :: ${weatherResponseFromApiLiveData.message}"
            )
//            assertEquals(weatherResponseFromApiLiveData.status, Status.SUCCESS)
//            assertTrue(weatherResponseFromApiLiveData.data != null)
            sut.currentWeather.observeForever {
                log(
                    TAG,
                    "status :: ${it.status} , data :: ${it.data} , message :: ${it.message}"
                )
            }
            log(TAG,"test finish")
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun apiResponseTest() = runBlocking {
//        withContext(mainCoroutineRule.dispatcher) {
//            val service = getCurrentWeatherApi()
//            val currentWeatherAsync = service.getCurrentWeatherAsync(
//                latitude = 28.459497,
//                longitude = 77.026634,
//                tempUnit = TEMP_UNIT_CELSIUS,
//                accessKey = ACCESS_KEY_OPEN_WEATHER
//            )
//            val await = currentWeatherAsync.await()
//            when (await) {
//                is ApiSuccessResponse -> {
//                    log(TAG, "ApiSuccessResponse -> ${await.body}")
//                }
//                is ApiErrorResponse -> {
//                    log(TAG, "ApiErrorResponse -> ${await.errorMessage}")
//                }
//                is ApiEmptyResponse -> {
//                    log(TAG, "empty data")
//                }
//            }
//        }
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

            Assert.assertTrue(response is ApiSuccessResponse)

            val successResponse = response as ApiSuccessResponse

            log(TAG,"success -> ${successResponse.body}")

            val todayWeather = Gson().fromJson(
                MockResponseReader("current_weather_success.json").content,
                TodayWeather::class.java
            )

            assert(successResponse.body == todayWeather)
        }
    }

}