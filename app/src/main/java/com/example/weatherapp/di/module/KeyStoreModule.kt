package com.example.weatherapp.di.module

import com.example.weatherapp.storage.IKeyStoreProvider
import com.example.weatherapp.storage.KeyStoreProvider
import dagger.Binds
import dagger.Module

@Module
abstract class KeyStoreModule {

    @Binds
    abstract fun bindKeyStoreProvider(storeProvider: KeyStoreProvider): IKeyStoreProvider
}