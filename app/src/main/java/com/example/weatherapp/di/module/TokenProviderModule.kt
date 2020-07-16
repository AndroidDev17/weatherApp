package com.example.weatherapp.di.module

import com.example.weatherapp.services.AccessKeyProvider
import com.example.weatherapp.services.IAccessKeyProvider
import dagger.Binds
import dagger.Module

@Module
abstract class TokenProviderModule() {
    @Binds
    abstract fun provideAccessToken(tokenProvider: AccessKeyProvider): IAccessKeyProvider
}