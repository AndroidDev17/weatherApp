package com.example.weatherapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherapp.di.module.AppDispatchers
import com.example.weatherapp.reposetory.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class LocationViewModelTest {
    // execute each task synchronously using Architecture Components
    @get: Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    @get:Rule
//    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var sut : LocationViewModel
    @ExperimentalCoroutinesApi
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    @Before
    fun setUp() {
        val appDispatchers = AppDispatchers(testDispatcher,testDispatcher,testDispatcher)
        Dispatchers.setMain(testDispatcher)
//        val repo = WeatherRepository()
//        sut = LocationViewModel()
    }

    @After
    fun tearDown() {

    }

    @Test
    fun getWeatherDataTest() {

    }
}