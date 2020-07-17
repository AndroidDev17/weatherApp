package com.example.weatherapp.network

import com.example.weatherapp.data.wrapper.ApiResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import retrofit2.*
import java.lang.reflect.Type

class DeferredCallAdapter<R>(private val responseType: Type) :
    CallAdapter<R, Deferred<ApiResponse<R>>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<R>): Deferred<ApiResponse<R>> {
        val deferred = CompletableDeferred<ApiResponse<R>>()

        deferred.invokeOnCompletion {
            if (deferred.isCancelled) {
                call.cancel()
            }
        }

        call.enqueue(object : Callback<R> {
            override fun onFailure(call: Call<R>, t: Throwable) {
                // we can install a global Error response handler here
                deferred.complete(ApiResponse.create(t))
            }

            override fun onResponse(call: Call<R>, response: Response<R>) {
                deferred.complete(ApiResponse.create(response))
            }
        })

        return deferred
    }

}