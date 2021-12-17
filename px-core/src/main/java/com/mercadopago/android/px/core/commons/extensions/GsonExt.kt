package com.mercadopago.android.px.core.commons.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun <T> Gson.getListFromJson(json: String?, classOfT: Class<T>): List<T> =
    fromJson(json, TypeToken.getParameterized(MutableList::class.java, classOfT).type) ?: mutableListOf()

fun <K, V> Gson.getCustomMapFromJson(json: String?, classOfK: Class<K>, classOfV: Class<V>): Map<K, V> =
    fromJson(json, TypeToken.getParameterized(MutableMap::class.java, classOfK, classOfV).type) ?: mutableMapOf()

fun <T> Gson.getMapFromJson(json: String?): Map<String, Any> = fromJson(json, object : TypeToken<T>() {}.type) ?: mutableMapOf()

fun Gson.getStringMapFromJson(json: String?): Map<String, String> =
    fromJson(json, object : TypeToken<Map<String, String>>() {}.type) ?: mutableMapOf()

fun <T> Gson.getMapFromObject(src: Any?): Map<String, Any> = getMapFromJson<T>(toJson(src))
