package com.example.weatherapp.services

interface IAccessKeyProvider {
    suspend fun provideAccessKey(): String
}