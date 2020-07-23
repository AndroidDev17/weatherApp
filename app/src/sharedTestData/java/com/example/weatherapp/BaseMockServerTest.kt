package com.example.weatherapp

import com.example.weatherapp.network.DeferredCallAdapterFactory
import com.example.weatherapp.services.WeatherApi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class BaseMockServerTest {
    private lateinit var server: MockWebServer
    private val httpClient = OkHttpClient.Builder().build()
    private val baseUrl :String = "http://localhost:8080/"
    private val retrofit by lazy {
        Retrofit.Builder()
            .client(httpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(DeferredCallAdapterFactory())
            .build()
    }
    fun before() {
        server = MockWebServer()
        server.start(8080)
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(MockResponseReader("current_weather_success.json").content)
            }
        }
    }

    fun setResponse(responsePath:String,responseCode:Int) {
        server.apply{
            enqueue(MockResponse().setResponseCode(responseCode).setBody(MockResponseReader(responsePath).content))
        }
    }
    fun after() {
        server.shutdown()
    }

    fun getCurrentWeatherApi(): WeatherApi = retrofit.create(WeatherApi::class.java)
}