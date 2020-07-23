package com.example.weatherapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.weatherapp.R
import com.example.weatherapp.WeatherApp
import com.example.weatherapp.data.DailyForecast
import com.example.weatherapp.data.Status
import com.example.weatherapp.data.TodayWeather
import com.example.weatherapp.data.WeatherForecast
import com.example.weatherapp.di.module.GlideApp
import com.example.weatherapp.services.MyLocationProvider
import com.example.weatherapp.services.NetworkBroadcastService
import com.example.weatherapp.services.NetworkMonitor
import com.example.weatherapp.services.WeatherApi
import com.example.weatherapp.util.*
import kotlinx.android.synthetic.main.forecast_row_item.*
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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

    private val networkMonitor: NetworkMonitor by lazy {
        NetworkMonitor(requireContext())
    }

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
        setObserver()

    }

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
    }

    @ExperimentalCoroutinesApi
    override fun onStop() {
        super.onStop()
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun setObserver() {
        viewModel.currentWeather.observe(viewLifecycleOwner, Observer {
            val textData = "status ${it.status} ::data ${it.data} ::msg ${it.message}"
            log(TAG, message = textData)
            when (it.status) {
                Status.LOADING -> {
                    bindLoadingData()
                }
                Status.SUCCESS -> {
                    bindSuccessData(it.data)
                }
                Status.ERROR -> {
                    bindErrorData(getApiErrorMessage(it.message))
                }
            }
        })

        viewModel.weatherForecast.observe(viewLifecycleOwner, Observer {
            val textData = "status ${it.status} ::data ${it.data} ::msg ${it.message}"
            log(TAG, message = textData)
            when (it.status) {
                Status.LOADING -> {
                    bindLoadingData()
                }
                Status.SUCCESS -> {
                    it.data?.let {
                        bindForecast(it)
                    }

                }
                Status.ERROR -> {
                    bindErrorData(getApiErrorMessage(it.message))
                }
            }
        })

        networkMonitor.observe(viewLifecycleOwner, Observer { networkAvailable ->
            log(TAG, "network state -> $networkAvailable")
            if (networkAvailable) {
                viewModel.loadDataFromLastLocation()
            }
        })
    }

    private fun bindErrorData(message: String?) {
        progressBar.hide()
        requireActivity().toast(message ?: "Unknown error")
    }

    private fun bindSuccessData(weather: TodayWeather?) {
        progressBar.hide()
        weather?.let {
            bindData(it)
        }
    }

    private fun bindLoadingData() {
        progressBar.visible()
    }

    @ExperimentalCoroutinesApi
    private fun observeLocation() {
        log(TAG, "observeLocation")
        lifecycleScope.launch {
            locationProvider.location.collect { location ->
                val locationText = "lat :: ${location.latitude} , long :: ${location.longitude}"
                log(TAG, locationText)
                viewModel.storeLastLocation(location)
                viewModel.updateLocation(location)
            }
        }
    }

    private fun bindData(weather: TodayWeather) {
        if (weather.isError()) {
            requireActivity().toast(weather.message)
            return
        }

        tv_location.text = weather.name
        val description = weather.weather[0].main
        description.let {
            tv_weather.text = it
        }
        tv_temp.text =
            convertToCelsius(weather.main.temp.toString())
        weather.weather[0].icon.let {
            val requestListener = object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // important to return false so the error placeholder can be placed
                    e?.logRootCauses("Glide Image loading error")
                    e?.causes?.forEach {
                        log(TAG, "Glide exception -> ${it.printStackTrace()}")
                    }
                    e?.printStackTrace()
                    log(TAG, e?.message)
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // everything worked out, so probably nothing to do
                    return false
                }

            }


            log(TAG, it)

            val imageRequestOptions = RequestOptions().override(48, 48)
            GlideApp.with(requireContext())
                .asBitmap()
                .load(String.format(BASE_IMAGE_URL, it))//String.format(BASE_IMAGE_URL,it)
                .listener(requestListener)
                .into(imageView)

        }

    }

    private fun bindForecast(forecast: WeatherForecast) {
        if (forecast.isError()) {
            requireContext().toast(forecast.message)
            return
        }
        if (forecast.daily.isNotEmpty()) {
            val views = listOf<View>(day_one, day_two, day_three, day_four)
            forecast.daily.drop(1).take(4).forEachIndexed { index, _forecast ->
                bindForecastView(views[index], _forecast)
            }
        }
    }

    private fun bindForecastView(view: View, _forecast: DailyForecast) {
        view.findViewById<AppCompatTextView>(R.id.day).text =
            getDateTime(_forecast.dt * 1000.toLong())
        view.findViewById<AppCompatTextView>(R.id.temp).text =
            convertToCelsius(_forecast.temp.day.toString())
        val forecastImageView = view.findViewById<AppCompatImageView>(R.id.forecastImageView)
        _forecast.weather[0].icon.let {
            val imageRequestOptions = RequestOptions().override(48, 48)
            GlideApp.with(requireContext())
                .asBitmap()
                .load(String.format(BASE_IMAGE_URL, it))//String.format(BASE_IMAGE_URL,it)
                .into(forecastImageView)

        }
    }

}



