package com.example.weatherapp.network

import com.example.weatherapp.data.wrapper.ApiResponse
import kotlinx.coroutines.Deferred
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class DeferredCallAdapterFactory(): CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // check for Deferred return type
        if (Deferred::class.java != getRawType(returnType)) {
            return null
        }
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)

        val rawObservableType = getRawType(observableType)

        if (rawObservableType != ApiResponse::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }

        val bodyType = getParameterUpperBound(0, observableType)
        return DeferredCallAdapter<Any>(bodyType)
    }
}