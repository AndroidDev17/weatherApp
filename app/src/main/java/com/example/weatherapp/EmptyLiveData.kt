package com.example.weatherapp

import androidx.lifecycle.LiveData

/**
 * this live data post null first time and remains silent after that
 */
class EmptyLiveData<T:Any?> private constructor() :LiveData<T>() {
    init {
        // use post instead of set since this can be created on any thread
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> {
            return EmptyLiveData()
        }
    }
}