package com.example.weatherapp.storage

import android.content.Context

class PlainStorage(context: Context,@KeyStores fileName: String) :
    SharedPreferenceKeyValueStore(context.getSharedPreferences(fileName, Context.MODE_PRIVATE))