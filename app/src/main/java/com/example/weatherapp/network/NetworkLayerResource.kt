package com.example.weatherapp.network

import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.wrapper.*
import com.example.weatherapp.di.module.AppDispatchers
import com.example.weatherapp.util.log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


/**
 * @param appDispatchers dispatcher to run coroutine
 * <ResultType> Api response type
 */
abstract class NetworkLayerResource<ResultType>
@MainThread constructor(private val appDispatchers: AppDispatchers) {
    private val TAG = NetworkLayerResource::class.simpleName!!
    private val result = MutableLiveData<Resource<ResultType>>()

    suspend fun begin() {
        // mark status as loading
        setValue(Resource.loading(null))
        log(TAG, "fetchFromNetwork ...")
        fetchFromNetwork()
    }

    @MainThread
    private suspend fun setValue(newValue: Resource<ResultType>) =
        withContext(appDispatchers.mainThread()) {
            if (result.value != newValue) {
                result.value = newValue
            }
        }

    private suspend fun fetchFromNetwork() =
        withContext(appDispatchers.networkIO()) {
            log(TAG, "network call")
            val apiResponse = createCallAsync()
//        delay(1000) // fake delay
            when (val response = apiResponse.await()) {
                is ApiSuccessResponse -> {
                    withContext(appDispatchers.diskIO()) {
                        log(TAG, "success Response-> ${response.body}")
                        val processResponse = processResponse(response)
                        setValue(Resource.success(processResponse))
                    }
                }
                is ApiEmptyResponse -> {
                    log(
                        TAG,
                        "ApiEmptyResponse Response"
                    )
                    setValue(Resource.success(null))

                }
                is ApiErrorResponse -> {
                    log(
                        TAG,
                        "ApiErrorResponse Response"
                    )
                    onFetchFailed()
                    setValue(Resource.error(response.errorMessage, null))
                }
            }
        }

    @VisibleForTesting
    private suspend fun blockingFetchFromNetwork() =
        withContext(appDispatchers.networkIO()) {
            log(TAG, "network call")
            val apiResponse = createCallAsync()
//        delay(1000) // fake delay
            when (val response = apiResponse.await()) {
                is ApiSuccessResponse -> {
                    withContext(appDispatchers.diskIO()) {
                        log(TAG, "success Response-> ${response.body}")
                        val processResponse = processResponse(response)
                        setValue(Resource.success(processResponse))
                    }
                }
                is ApiEmptyResponse -> {
                    log(
                        TAG,
                        "ApiEmptyResponse Response"
                    )
                    setValue(Resource.success(null))

                }
                is ApiErrorResponse -> {
                    log(
                        TAG,
                        "ApiErrorResponse Response"
                    )
                    onFetchFailed()
                    setValue(Resource.error(response.errorMessage, null))
                }
            }
        }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<ResultType>) = response.body

    @MainThread
    protected abstract fun createCallAsync(): Deferred<ApiResponse<ResultType>>
}
