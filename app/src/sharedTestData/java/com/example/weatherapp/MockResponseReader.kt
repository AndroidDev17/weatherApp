package com.example.weatherapp

import java.io.InputStreamReader

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MockResponseReader(path:String) {
    val content: String

    init {
        val resourceAsStream = this.javaClass.classLoader!!.getResourceAsStream(path)
        val reader = InputStreamReader(resourceAsStream)
        content = reader.readText()
        reader.close()
    }
}