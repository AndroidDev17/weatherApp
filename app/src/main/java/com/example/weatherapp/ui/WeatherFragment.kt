package com.example.weatherapp.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.weatherapp.*
import com.example.weatherapp.data.CurrentWeather
import com.example.weatherapp.services.MyLocationProvider
import com.example.weatherapp.services.WeatherApi
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.annotation.Resource
import javax.inject.Inject

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class WeatherFragment : Fragment() {
    private val TAG = "WeatherFragment"

    @Inject
    lateinit var service: WeatherApi

    @Inject
    lateinit var locationProvider: MyLocationProvider

    @Inject
    lateinit var factory: DefaultViewModelFactory

    private val viewModel by viewModels<LocationViewModel> { factory }

    // Location Permissions Contract
    private val askLocationPermission by lazy {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.count { !it } == 0) {
                log(TAG, "permission granted")
                observeLocation()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        WeatherApp.instance.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // todo->  check user permission and fetch
        //  location when user provide permission
        askLocationPermission.launch(

            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        viewModel.loadDataFromLastLocation()
        viewModel.currentWeather.observe(viewLifecycleOwner, Observer {
            val textData = "status ${it.status} ::data ${it.data} ::msg ${it.message}"
            it.data?.let {
                bindData(it)
            }
            log(TAG, message = textData)
        })

    }

    private fun observeLocation() {
        log(TAG, "observeLocation")
        lifecycleScope.launch {
            locationProvider.location.collect { location ->
                val locationText = "lat :: ${location.latitude} , long :: ${location.longitude}"
                log(TAG, locationText)
                viewModel.storeLastLocation(location)
                viewModel.loadLocation(location.latitude, location.longitude)
            }
        }
    }

    private fun bindData(weather: CurrentWeather) {
        tv_location.text = weather.location.name
        val description = weather.current.descriptions?.get(0)
        description?.let {
            tv_weather.text = it
        }
        tv_temp.text = convertToCelsius(weather.current.temp)
        weather.current.icons?.get(0)?.let {
            Glide.with(imageView)
                .load(it)
                .into(imageView)
        }
        root.setBackgroundColor(
            if (weather.current.isDay.equals("yes", true)) {
                R.color.day_blue
            } else {
                R.color.night_black
            }
        )

    }

}