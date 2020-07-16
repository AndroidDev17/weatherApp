package com.example.weatherapp.reposetory

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.weatherapp.di.module.AppDispatchers
import com.example.weatherapp.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * @param <ResultType>
</ResultType> */
abstract class NetworkBoundResourceNew<ResultType>
@MainThread constructor(private val appDispatchers: AppDispatchers) {
    private val TAG = "NetworkBoundResource"
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        log(TAG,"fetchFromNetwork ...")
        fetchFromNetwork()
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork() {
        log(TAG,"network call")
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly

        log(TAG,"listening network response")
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            log(TAG,"network response -> $response")
            when (response) {
                is ApiSuccessResponse -> {
                    log(TAG,"success Response")
                    val coroutineScope = CoroutineScope(appDispatchers.diskIO())
                    coroutineScope.launch {
                        val processResponse = processResponse(response)
                        log(TAG,"inside coroutine processed -> $processResponse")
                        withContext(appDispatchers.mainThread()) {
                            setValue(Resource.success(processResponse))
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    log(TAG,"ApiEmptyResponse Response")
                    setValue(Resource.success(null))
                }
                is ApiErrorResponse -> {
                    log(TAG,"ApiErrorResponse Response")
                    onFetchFailed()
                    setValue(Resource.error(response.errorMessage, null))
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<ResultType>) = response.body

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<ResultType>>
}
