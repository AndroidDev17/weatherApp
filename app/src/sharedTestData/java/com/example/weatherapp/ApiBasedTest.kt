package com.example.weatherapp

import com.example.weatherapp.network.DeferredCallAdapterFactory
import com.example.weatherapp.services.WeatherApi
import com.example.weatherapp.util.log
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

open class ApiBasedTest {
    private val TAG = "ApiBasedTest"
    private lateinit var mockServer: MockWebServer
    private val port = 8080
    private val baseUrl = "http://localhost:$port/"
    private val clientBuilder = OkHttpClient.Builder()
    private lateinit var retrofit: Retrofit

    fun setupMockServer() {
        mockServer = MockWebServer()
        mockServer.start(port)
        retrofit = Retrofit.Builder()
            .client(clientBuilder.build())
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(DeferredCallAdapterFactory())
            .build()
    }

    fun tearDownMockServer() {
        try {
            mockServer.shutdown()
        } catch (e: IOException) {
            log(TAG, "shutdown server with exception")
        }
    }

    fun setResponse(responseCode: Int, responsePath: String) {
        mockServer.apply {
            enqueue(
                MockResponse().setResponseCode(responseCode)
                    .setBody(MockResponseReader(responsePath).content)
            )
        }
    }

    fun getCurrentWeatherApi(): WeatherApi = retrofit.create(WeatherApi::class.java)

}