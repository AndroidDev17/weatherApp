package com.example.weatherapp.di.module
import com.example.weatherapp.di.ViewModelProviderKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.util.DefaultViewModelFactory
import com.example.weatherapp.ui.LocationViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


/**
 * add provider for all view models in this module
 */
@Module
abstract class ViewModelProviderModule {
    @Binds
    @IntoMap
    @ViewModelProviderKey(LocationViewModel::class)
    abstract fun bindLocationViewModel(locationViewModel: LocationViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: DefaultViewModelFactory): ViewModelProvider.Factory
}