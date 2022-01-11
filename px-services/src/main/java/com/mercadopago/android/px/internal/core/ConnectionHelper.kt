package com.mercadopago.android.px.internal.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

class ConnectionHelper(context: Context) {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun hasConnection(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkConnection()
        } else {
            checkConnectionLegacy()
        }
    }

    @Suppress("DEPRECATION")
    private fun checkConnectionLegacy(): Boolean {
        return cm.activeNetworkInfo?.let {
            it.isConnectedOrConnecting
                && it.type == ConnectivityManager.TYPE_WIFI
                || it.type == ConnectivityManager.TYPE_MOBILE
        } ?: false
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkConnection(): Boolean {
        return cm.getNetworkCapabilities(cm.activeNetwork)?.let {
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } ?: false
    }
}
