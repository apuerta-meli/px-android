package com.mercadopago.android.px.core.presentation.extensions

import android.text.SpannableStringBuilder
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.graphics.toColorInt
import androidx.core.text.parseAsHtml
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.mercadopago.android.px.core.commons.extensions.EMPTY
import com.mercadopago.android.px.core.commons.extensions.SPACE
import com.mercadopago.android.px.core.commons.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.core.presentation.ITextDescriptor

private const val TAG = "TextViewExt"

/**
 * Sets the TextView text and makes it visible when it's not null nor empty, otherwise it sets visibility to GONE.
 * @param text Text to set.
 */
fun TextView.loadOrGone(text: CharSequence?) {
    if (isEmpty(text)) {
        isGone = true
    } else {
        this.text = text
        isVisible = true
    }
}

/**
 * Sets the TextView text as HTML and makes it visible when it's not null nor empty, otherwise it sets visibility to GONE.
 * @param text Text to set.
 */
fun TextView.loadLikeHtmlOrGone(text: String?) {
    if (isEmpty(text)) {
        isGone = true
    } else {
        this.text = text!!.parseAsHtml()
        isVisible = true
    }
}

/**
 * Sets the TextView text from a resource and makes it visible when it's not 0, otherwise it sets visibility to GONE.
 * @param resId Resource id that represents the text to set.
 */
fun TextView.loadOrGone(@StringRes resId: Int) {
    val value: CharSequence = if (resId == 0) EMPTY else context.getString(resId)
    loadOrGone(value)
}

/**
 * Safe wrapper for TextView setTextColor that sets the text color only when it's not null nor empty
 * @param color Color value in the form 0xAARRGGBB. Do not pass a resource ID.
 * To get a color value from a resource ID, call getColor.
 */
fun TextView.setTextColor(color: String?) {
    if (color.isNotNullNorEmpty()) {
        try {
            setTextColor(color!!.toColorInt())
        } catch (e: Exception) {
            Log.d(TAG, "Cannot parse color $color")
        }
    }
}

/**
 * Loads a [ITextDescriptor] into a TextView applying defined style as spannable.
 *
 * If the descriptor is empty or null it sets TextView visibility to GONE.
 * @param textDescriptor Nullable ITextDescriptor to set on TextView.
 */
fun TextView.loadOrGone(textDescriptor: ITextDescriptor) {
    return loadTextListOrGone(listOf(textDescriptor))
}

/**
 * Loads a list of [ITextDescriptor] into a TextView applying defined styles as spannable.
 *
 * If the list is empty or null it sets TextView visibility to GONE.
 * @param textDescriptors Nullable list of ITextDescriptors to set on TextView.
 */
fun TextView.loadTextListOrGone(textDescriptors: List<ITextDescriptor>?) {
    if (textDescriptors.isNullOrEmpty()) {
        isGone = true
        return
    }
    val spannableStringBuilder = SpannableStringBuilder()
    var startIndex = 0
    var endIndex: Int
    textDescriptors.forEach { textDescriptor ->
        spannableStringBuilder.append(textDescriptor.getText(context)).takeUnless {
            textDescriptor == textDescriptors.last()
        }?.append(SPACE)
        with (spannableStringBuilder) {
            endIndex = length
            setFont(textDescriptor.getFont(context), startIndex, endIndex)
            setColor(textDescriptor.getTextColor(context), startIndex, endIndex)
            textDescriptor.getFontSize(context)?.let {
                setFontSize(it, startIndex, endIndex)
            }
            startIndex = length
        }
    }
    text = spannableStringBuilder
    isVisible = true
}
