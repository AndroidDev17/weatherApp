package com.example.weatherapp.storage

import androidx.annotation.StringDef

// key stores
const val ENCRYPTED_SHARED_PREFERENCE = "encrypted-shared-preference"
const val USER_SETTING = "user-setting"

@Retention(AnnotationRetention.SOURCE)
@StringDef(ENCRYPTED_SHARED_PREFERENCE, USER_SETTING)
annotation class KeyStores

const val LAST_KNOW_LOCATION = "last-known-location"
const val LAST_KNOW_LOCATION_DEFAULT = "28.459497,77.026634"