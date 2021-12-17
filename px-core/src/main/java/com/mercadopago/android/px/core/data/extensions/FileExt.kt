package com.mercadopago.android.px.core.data.extensions

import android.os.Parcelable
import com.google.gson.Gson
import com.mercadopago.android.px.core.commons.extensions.EMPTY
import com.mercadopago.android.px.core.commons.extensions.getCustomMapFromJson
import com.mercadopago.android.px.core.commons.extensions.getListFromJson
import com.mercadopago.android.px.core.commons.extensions.marshall
import com.mercadopago.android.px.core.commons.extensions.unmarshall
import java.io.File
import kotlin.reflect.KClass

/**
 * Writes file content to a file.
 *
 * @param fileContent to be wrote to the file.
 * @param gson (optional) used to serialize and deserialize fileContent to JSON.
 */
@Synchronized
@JvmOverloads
fun <T> File.writeToFile(fileContent: T, gson: Gson? = null) {
    when (fileContent) {
        is String -> writeText(fileContent)
        is Parcelable -> writeBytes(fileContent.marshall())
        else -> {
            // Maybe we can have a default implementation of gson instead of throwing an exception?
            require(gson != null)
            gson.toJson(fileContent)?.let { writeText(it) }
        }
    }
}

/**
 * Reads list from file.
 *
 * @param tClass to fill the list
 * @param gson used to serialize and deserialize the list
 */
@Synchronized
fun <T> File.readAnyList(tClass: Class<T>, gson: Gson): List<T> = gson.getListFromJson(readTextSync(), tClass)

/**
 * Reads a map from a file
 *
 * @param gson used to serialize and deserialize the map
 * @param kClass keys class used on map
 * @param vClass values class used on map
 */
@Synchronized
fun <K, V> File.readAnyMap(kClass: Class<K>, vClass: Class<V>, gson: Gson): Map<K, V> =
    gson.getCustomMapFromJson(readTextSync(), kClass, vClass)

/**
 * Reads text from a file in a thread-safe way. If the file doesn't exist then it returns an empty string.
 */
@Synchronized
fun File.readTextSync(): String = if (exists()) readText() else EMPTY

/**
 * Reads parcelable from a file in a thread-safe way. If the file doesn't exist then it returns null.
 *
 * @param kClass Parcelable class to read.
 */
@Synchronized
fun <T : Parcelable> File.readParcelable(kClass: KClass<T>): T? = if (exists()) readBytes().unmarshall(kClass) else null

/**
 * Deletes file in a thread-safe way if the file exists.
 */
@Synchronized
fun File.removeFile() = exists() && delete()

/**
 * Write bytes file in a thread-safe way if the file exists.
 */
@Synchronized
fun File.write(bytes: ByteArray) = writeBytes(bytes)

/**
 * Deletes a directory and its content recursively in a thread-safe way.
 */
@Synchronized
fun File.deleteDirIfExists() {
    if (exists() && isDirectory) {
        deleteRecursively()
    }
}
