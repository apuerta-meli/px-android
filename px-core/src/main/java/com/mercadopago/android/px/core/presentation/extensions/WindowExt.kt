package com.mercadopago.android.px.core.presentation.extensions

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt

private const val DARKEN_FACTOR = 0.1f

@ColorInt
internal fun darkenColor(@ColorInt color: Int): Int {
    val hsv = FloatArray(3)
    Color.colorToHSV(color, hsv)
    hsv[1] = hsv[1] + DARKEN_FACTOR
    hsv[2] = hsv[2] - DARKEN_FACTOR
    return Color.HSVToColor(hsv)
}

/**
 * Paint the status bar
 *
 * @param color the color to use. The color will be darkened by [.DARKEN_FACTOR] percent
 */
@SuppressLint("InlinedApi")
fun Window.setStatusBarColor(@ColorInt color: Int) {
    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    statusBarColor = darkenColor(color)
}
