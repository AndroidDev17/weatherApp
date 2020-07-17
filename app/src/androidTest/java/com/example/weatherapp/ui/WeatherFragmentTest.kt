package com.example.weatherapp.ui

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherapp.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherFragmentTest {
    @Test
    fun homeScreenLaunch() {
        // GIVEN - On the home screen
        val scenarioWeather =
            launchFragmentInContainer<WeatherFragment>(Bundle(), R.style.AppTheme)
        // check progress bar is present
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))

    }
}
