package com.example.weatherapp.di.component

import com.example.weatherapp.di.module.*
import com.example.weatherapp.ui.WeatherFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ContextModule::class, ViewModelProviderModule::class, LocationModule::class,
    NetworkModule::class, TokenProviderModule::class,RepoModule::class,KeyStoreModule::class])
interface AppComponent {

    fun inject(fragment: WeatherFragment)

    fun injectInto(glideApp: MyAppGlideModule)
}
