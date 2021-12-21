package com.mercadopago.android.px.core.presentation.extensions

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.mercadolibre.android.picassodiskcache.PicassoDiskLoader
import com.mercadopago.android.px.core.commons.extensions.isNotNullNorEmpty
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Transformation

private var DISABLED_FILTER: ColorMatrixColorFilter = makeDisabledFilter()

/**
 * Makes a matrix color filter that can be used to transform an image to gray scale
 */
private fun makeDisabledFilter(): ColorMatrixColorFilter {
    val lightingMatrix: FloatArray = floatArrayOf(
        1f, 0f, 0f, 0f, 50f,
        0f, 1f, 0f, 0f, 50f,
        0f, 0f, 1f, 0f, 50f,
        0f, 0f, 0f, 0.9f, 0f
    )
    return ColorMatrixColorFilter(ColorMatrix().also {
        it.setSaturation(0f)
        it.postConcat(ColorMatrix(lightingMatrix))
    })
}

/**
 * Sets the ImageView image from a resource and makes it visible when it's not 0, otherwise it sets visibility to GONE.
 * @param resId Resource id that represents the image to set.
 */
fun ImageView.loadOrGone(@DrawableRes resId: Int) {
    if (resId == 0) {
        isGone = true
    } else {
        setImageResource(resId)
        isVisible = true
    }
}

/**
 * Loads the ImageView image from an URL (with Picasso) and makes it visible when it's not null nor empty,
 * otherwise it sets visibility to GONE.
 * @param url Image id that represents the image to set.
 */
fun ImageView.loadOrGone(url: String?) {
    if (url.isNotNullNorEmpty()) {
        PicassoDiskLoader.get(context).load(url).into(this)
        isVisible = true
    } else {
        isGone = true
    }
}

/**
 * Loads the ImageView image from an URL (with Picasso) or a fallback from a drawable if it's null nor empty.
 * Also applies a transformation if it's not null.
 * @param url Image id that represents the image to set.
 * @param fallback Drawable resource id that represents the fallback image.
 * @param transformation Nullable transformation to apply to the image.
 */
@JvmOverloads
fun ImageView?.loadOrElse(url: String?, @DrawableRes fallback: Int, transformation: Transformation? = null) {
    this?.let {
        it.context?.applicationContext?.let { context ->
            val picasso = PicassoDiskLoader.get(context)
            val requestCreator: RequestCreator = if (url.isNotNullNorEmpty()) picasso.load(url) else picasso.load(fallback)
            transformation?.let { requestCreator.transform(transformation) }
            requestCreator.placeholder(fallback).into(it)
        }
    }
}

/**
 * Applies the [DISABLED_FILTER] matrix as the ImageView color filter to transform it to gray scale.
 */
fun ImageView.grayScaleView() {
    colorFilter = DISABLED_FILTER
}
