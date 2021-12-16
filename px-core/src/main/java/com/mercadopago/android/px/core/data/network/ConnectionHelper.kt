package com.mercadopago.android.px.core.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

class ConnectionHelper(context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun hasConnection(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkConnection()
        } else {
            checkConnectionLegacy()
        }
    }

    @Suppress("DEPRECATION")
    private fun checkConnectionLegacy(): Boolean {
        return connectivityManager.activeNetworkInfo?.let {
            it.isConnectedOrConnecting
                    && it.type == ConnectivityManager.TYPE_WIFI
                    || it.type == ConnectivityManager.TYPE_MOBILE
        } ?: false
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkConnection(): Boolean {
        return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.let {
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } ?: false
    }
}
