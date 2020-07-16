package com.example.weatherapp.reposetory

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.weatherapp.di.module.AppDispatchers
import com.example.weatherapp.log
import kotlinx.coroutines.*

/**
 * A generic class that can provide a
 * resource backed by both the Rooms database and the network.
 * @param <ResultType> local DB return type ( source of truth is local Db)
 * @param <RequestType> network call return value
</RequestType></ResultType> */
abstract class NetworkBoundResource<ResultType, RequestType>
@MainThread constructor(private val appDispatchers: AppDispatchers) {
    private val TAG = "NetworkBoundResource"

    // value which is returned as live data
    private val result = MediatorLiveData<Resource<ResultType>>()

   init {
        // this will emit loading live data
       log(TAG,"sending null")
        result.value = Resource.loading(null)
        @Suppress("LeakingThis")

        log(TAG,"getting DB source")
        // load data from Db as live data
        val dbSource = loadFromDb()

       log(TAG,"attaching DB source")
       // add db source as a source for this live Data
        result.addSource(dbSource) { data ->
            // remove Db source as soon as it return a value
            log(TAG,"data from db source -> $data")
            result.removeSource(dbSource)

            // check whether we should data from DB or not
            if (shouldFetch(data)) {
                log(TAG,"should fetch from network  -> $data")
                // fetch data from network and use this Db source
                fetchFromNetwork(dbSource)
            } else {
                // use this Db source for data
                log(TAG,"should fetch from DB  -> $data")
                result.addSource(dbSource) { newData ->
                    log(TAG,"connected with DB  -> $newData")
                    // emit data from result live data
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    // emit value from result live data
    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        log(TAG,"fetchFromNetwork called...")
        // live data from network response
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        log(TAG,"fetchFromNetwork level 1...")
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }
        // process data from network response and update DB
        log(TAG,"fetchFromNetwork level 2...")
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            log(TAG,"$response")
            log(TAG,"fetchFromNetwork api response $response")
            when (response) {
                is ApiSuccessResponse -> {
                    log(TAG,"success Response")
                    appDispatchers.diskIO().asExecutor().execute {
                        val processResponse = processResponse(response)
                        log(TAG,"inside coroutine processed -> $processResponse")
                        saveCallResult(processResponse)
                        appDispatchers.mainThread().asExecutor().execute {
                            log(TAG,"inside success post response on main live data")
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb()) { newData ->
                                log(TAG,"data from DB ->$newData")
                                setValue(Resource.success(newData))
                            }
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    log(TAG,"fetchFromNetwork ApiEmptyResponse...")
                    appDispatchers.mainThread().asExecutor().execute{
                        // reload from disk whatever we had
                        result.addSource(loadFromDb()) { newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                }
                is ApiErrorResponse -> {
                    log(TAG,"fetchFromNetwork ApiErrorResponse...")
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.error(response.errorMessage, newData))
                    }
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    // return result as live data
    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}
