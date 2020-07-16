package com.example.weatherapp.storage

interface KeyValueStore {
    fun contains(key: String): Boolean
    fun getBoolean(key: String, defValue: Boolean): Boolean
    fun getFloat(key: String, defValue: Float): Float
    fun getInt(key: String, defValue: Int): Int
    fun getLong(key: String, defValue: Long): Long
    fun getString(key: String, defValue: String): String?
    fun getStringSet(key: String, defValues: Set<String>): MutableSet<String>?
    fun putBoolean(key: String, value: Boolean)
    fun putFloat(key: String, value: Float)
    fun putInt(key: String, value: Int)
    fun putLong(key: String, value: Long)
    fun putString(key: String, value: String)
    fun putStringSet(key: String, value: Set<String>)
    suspend fun putAsyncString(key: String, value: String)
    suspend fun getAsyncString(key: String, defValue: String): String?
    suspend fun putAsyncInt(key: String, value: Int)
    suspend fun getAsyncInt(key: String, defValue: Int): Int?
}