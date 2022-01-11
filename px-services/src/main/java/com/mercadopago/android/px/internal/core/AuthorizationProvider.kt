package com.mercadopago.android.px.internal.core

import android.content.SharedPreferences

private const val PREF_PRIVATE_KEY = "PREF_PRIVATE_KEY"

class AuthorizationProvider(private val sharedPreferences: SharedPreferences) {

    var privateKey: String? = null
        get() {
            if (field == null) {
                field = sharedPreferences.getString(PREF_PRIVATE_KEY, null)
            }
            return field
        }

    fun configure(privateKey: String) {
        this.privateKey = privateKey
        sharedPreferences.edit().putString(PREF_PRIVATE_KEY, privateKey).apply()
    }

    fun reset() {
        privateKey = null
        sharedPreferences.edit().remove(PREF_PRIVATE_KEY).apply()
    }
}
