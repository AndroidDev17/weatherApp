package com.example.weatherapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import com.example.weatherapp.util.isOnline
import com.example.weatherapp.util.log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
class NetworkBroadcastService @Inject constructor() {
    private val TAG = "NetworkBroadcastService"
    private val scope = MainScope()

    private lateinit var connectivityManager: ConnectivityManager

    private lateinit var receiver: BroadcastReceiver

    private lateinit var listener : ConnectivityManager.OnNetworkActiveListener

    private val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

    @ExperimentalCoroutinesApi
    private val broadcastChannel = ConflatedBroadcastChannel<Boolean>()

    val networkState = broadcastChannel.asFlow()

    private val _networkState = Channel<Boolean>()

    init {
        scope.launch {
            broadcastChannel.offer(_networkState.receive())
        }
    }

    fun registerReceiver(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            listener = ConnectivityManager.OnNetworkActiveListener {
                scope.launch {
                    _networkState.send(true)
                }
            }
            connectivityManager.addDefaultNetworkActiveListener(listener)
        } else {
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val connectivityManager =
                        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    try {
                        scope.launch {
                            _networkState.send(connectivityManager.isOnline())
                        }
                    } catch (e: Exception) {
                        log(TAG, "error in sending data")
                    }
                }

            }
            context.registerReceiver(receiver,filter)
        }
    }

    fun unregisterReceiver(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (this::connectivityManager.isInitialized && this::listener.isInitialized) {
                connectivityManager.removeDefaultNetworkActiveListener(listener)
            }
        } else {
            if (this::receiver.isInitialized) {
                context.unregisterReceiver(receiver)
            }
        }
    }
}