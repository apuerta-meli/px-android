package com.mercadopago.android.px.core.presentation

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.ColorInt

/**
 * Represents a text style description
 */
interface ITextDescriptor {
    fun getText(context: Context): CharSequence
    fun getFont(context: Context): Typeface
    @ColorInt
    fun getTextColor(context: Context): Int
    fun getFontSize(context: Context): Int?
}
