package com.example.weatherapp.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * a common factory for all view models,
 * it uses map provided in class {@link ViewModelProviderModule}
 * @see com.example.kotlinpoc.di.modules.ViewModelProviderModule
 */
@Singleton
class DefaultViewModelFactory @Inject constructor(private val providerMap: Map<Class<out ViewModel>,@JvmSuppressWildcards Provider<ViewModel>>) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val creator = providerMap.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("factory can't create model $modelClass")
        @Suppress("UNCHECKED_CAST")
        return creator.get() as T
    }
}

