package com.mercadopago.android.px.core.commons.extensions

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.math.BigDecimal
import java.util.Date
import java.util.Objects
import kotlin.reflect.KClass

fun Parcel.writeDate(date: Date?) = writeLong(date?.time ?: -1L)

fun Parcel.readDate(): Date? {
    val long = readLong()
    return if (long != -1L) Date(long) else null
}

fun Parcel.writeBigDecimal(number: BigDecimal) = writeString(number.toString())

fun Parcel.writeNullableBigDecimal(value: BigDecimal?) = value?.let {
    writeByte(1.toByte())
    writeString(it.toString())
} ?: writeByte(0.toByte())

fun Parcel.readBigDecimal() = BigDecimal(readString())

fun Parcel.readNullableBigDecimal() = if (readByte().toInt() == 0) null else BigDecimal(readString())

fun Parcel.writeNullableInt(value: Int?) = value?.let {
    writeByte(1.toByte())
    writeInt(it)
} ?: writeByte(0.toByte())

fun Parcel.readNullableInt() = if (readByte().toInt() == 0) null else readInt()

// For writing to a Serializable
fun <K : Serializable?, V : Serializable?> Parcel.writeSerializableMap(map: Map<K, V?>) {
    writeInt(map.size)
    map.forEach { (key, value) ->
        value?.let {
            writeSerializable(key)
            writeSerializable(value)
        }
    }
}

// For reading from a Serializable
fun <K : Serializable?, V : Serializable?> Parcel.readSerializableMap(map: MutableMap<K, V>, kClass: Class<K>, vClass: Class<V>) {
    val size = readInt()
    for (i in 0 until size) {
        val key = kClass.cast(readSerializable())
        val value = vClass.cast(readSerializable())
        map[key!!] = value!!
    }
}

fun Parcelable.marshall() = Parcel.obtain().let {
    writeToParcel(it, 0)
    val bytes = it.marshall()
    it.recycle()
    bytes
}

fun <T : Parcelable> ByteArray.unmarshall(kClass: KClass<T>): T {
    val parcel = Parcel.obtain().also {
        it.unmarshall(this, 0, size)
        it.setDataPosition(0) // This is extremely important!
    }
    return parcelableCreator(kClass).createFromParcel(parcel).also {
        parcel.recycle()
    }
}

private fun <T : Parcelable> parcelableCreator(kClass: KClass<T>): Parcelable.Creator<T> {
    val creator = kClass.java.getField("CREATOR").get(null)
    @Suppress("UNCHECKED_CAST")
    return creator as Parcelable.Creator<T>
}
