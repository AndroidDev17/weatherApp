package com.example.weatherapp.di.module

import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * provides CoroutineDispatcher to run a task
 */
@Singleton
open class AppDispatchers(
    private val diskIO: CoroutineDispatcher,
    private val networkIO: CoroutineDispatcher,
    private val mainThread: CoroutineDispatcher
) {
    @OptIn(ObsoleteCoroutinesApi::class)
    @Inject
    constructor() : this(
        newSingleThreadContext("Disk IO") as CoroutineDispatcher,
        newFixedThreadPoolContext(3, "Network IO") as CoroutineDispatcher,
        Dispatchers.Main
    )

    fun diskIO(): CoroutineDispatcher {
        return diskIO
    }

    fun networkIO(): CoroutineDispatcher {
        return networkIO
    }

    fun mainThread(): CoroutineDispatcher {
        return mainThread
    }
}