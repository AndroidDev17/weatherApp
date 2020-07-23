package com.example.weatherapp.storage

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyStoreProvider @Inject constructor(private val context :Context) : IKeyStoreProvider {
    override val userSetting by lazy { PlainStorage(context,USER_SETTING) }
}