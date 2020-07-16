package com.example.weatherapp.storage

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class SharedPreferenceKeyValueStore
    (private val preferences: SharedPreferences) : KeyValueStore {
    override fun contains(key: String) = preferences.contains(key)

    override fun getBoolean(key: String, defValue: Boolean) = preferences.getBoolean(key, defValue)

    override fun getFloat(key: String, defValue: Float) = preferences.getFloat(key, defValue)

    override fun getInt(key: String, defValue: Int) = preferences.getInt(key, defValue)

    override fun getLong(key: String, defValue: Long) = preferences.getLong(key, defValue)

    override fun getString(key: String, defValue: String) = preferences.getString(key, defValue)

    override fun getStringSet(key: String, defValues: Set<String>): MutableSet<String>? =
        preferences.getStringSet(key, defValues)

    override fun putBoolean(key: String, value: Boolean) {
        with(preferences.edit()) {
            putBoolean(key, value)
            commit()
        }
    }

    override fun putFloat(key: String, value: Float) {
        with(preferences.edit()) {
            putFloat(key, value)
            commit()
        }
    }

    override fun putInt(key: String, value: Int) {
        with(preferences.edit()) {
            putInt(key, value)
            commit()
        }
    }

    override fun putLong(key: String, value: Long) {
        with(preferences.edit()) {
            putLong(key, value)
            commit()
        }
    }

    override fun putString(key: String, value: String) {
        with(preferences.edit()) {
            putString(key, value)
            commit()
        }
    }

    override fun putStringSet(key: String, value: Set<String>) {
        with(preferences.edit()) {
            putStringSet(key, value)
            commit()
        }
    }

    // Async functions...


    override suspend fun putAsyncString(key: String, value: String) {
        withContext(Dispatchers.IO) {
            with(preferences.edit()) {
                putString(key, value)
                commit()
            }
        }
    }

    override suspend fun getAsyncString(key: String, defValue: String) =
        withContext(Dispatchers.IO) {
            return@withContext preferences.getString(key, defValue)
        }


    override suspend fun putAsyncInt(key: String, value: Int) {
        withContext(Dispatchers.IO) {
            with(preferences.edit()) {
                putInt(key, value)
                commit()
            }
        }
    }

    override suspend fun getAsyncInt(key: String, defValue: Int) =
        withContext(Dispatchers.IO) {
        return@withContext preferences.getInt(key, defValue)
    }
}