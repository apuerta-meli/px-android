package com.mercadopago.android.px.core.presentation.extensions

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.annotation.ColorInt
import com.mercadopago.android.px.core.commons.extensions.isNotNullNorEmpty

private const val TAG = "SpannableExt"

/**
 * Wrapper for [Spannable.setSpan] that sets Spannable color when it's not 0
 * using [Spannable.SPAN_EXCLUSIVE_EXCLUSIVE] flag.
 * @param color Color integer that defines the text color.
 */
fun Spannable.setColor(@ColorInt color: Int, indexStart: Int, indexEnd: Int) {
    if (color != 0) {
        setSpan(ForegroundColorSpan(color), indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

/**
 * Wrapper for [Spannable.setColor] that sets Spannable color from a color string when it's not null nor empty
 * @param color String that represents a color. Supported formats are: #RRGGBB #AARRGGBB.
 */
fun Spannable.setColor(color: String?, indexStart: Int, indexEnd: Int) {
    if (color.isNotNullNorEmpty()) {
        try {
            setColor(Color.parseColor(color), indexStart, indexEnd)
        } catch (e: Exception) {
            Log.d(TAG, "Cannot parse color $color")
        }
    }
}

/**
 * Wrapper for [Spannable.setSpan] that sets Spannable style from a [Typeface] style
 * using [Spannable.SPAN_EXCLUSIVE_EXCLUSIVE] flag.
 * @param typeface Typeface to set.
 */
fun Spannable.setFont(typeface: Typeface, indexStart: Int, indexEnd: Int) {
    setSpan(StyleSpan(typeface.style), indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

/**
 * Wrapper for [Spannable.setSpan] that sets Spannable font size (in px) using [Spannable.SPAN_EXCLUSIVE_EXCLUSIVE] flag.
 * @param fontSize fontSize to set in pixels
 */
fun Spannable.setFontSize(fontSize: Int, indexStart: Int, indexEnd: Int) {
    setSpan(AbsoluteSizeSpan(fontSize), indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}
