package com.mercadopago.android.px.core.presentation

import android.graphics.Color
import android.graphics.PorterDuffColorFilter
import android.view.View
import com.mercadopago.android.px.core.presentation.extensions.resetDrawableBackgroundColor
import com.mercadopago.android.px.core.presentation.extensions.setDrawableBackgroundColor
import com.mercadopago.android.px.core.utils.BasicRobolectricTest
import com.mercadopago.android.px.core.utils.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

@RunWith(RobolectricTestRunner::class)
class ViewExtTests : BasicRobolectricTest() {
    @Test
    fun setDrawableBackgroundColorShouldSetDrawableBackgroundColor() {
        with(View(getContext())){
            background = context.getDrawable(android.R.drawable.ic_menu_close_clear_cancel)
            val color = Color.parseColor("#999999")
            setDrawableBackgroundColor("#999999")
            with(background.colorFilter) {
                assert(this is PorterDuffColorFilter)
                Shadows.shadowOf(this as PorterDuffColorFilter).color.assertEquals(color)
            }
        }
    }

    @Test
    fun setDrawableBackgroundColorShouldSetDrawableBackgroundColorToTransparentWhenColorIsNotAValidColor() {
        with(View(getContext())){
            background = context.getDrawable(android.R.drawable.ic_menu_close_clear_cancel)
            setDrawableBackgroundColor("invalid color")
            with(background.colorFilter) {
                assert(this is PorterDuffColorFilter)
                Shadows.shadowOf(this as PorterDuffColorFilter).color.assertEquals(Color.TRANSPARENT)
            }
        }
    }

    @Test
    fun resetDrawableBackgroundColorShouldSetDrawableBackgroundColorToTransparent() {
        with(View(getContext())){
            background = context.getDrawable(android.R.drawable.ic_menu_close_clear_cancel)
            resetDrawableBackgroundColor()
            with(background.colorFilter) {
                assert(this is PorterDuffColorFilter)
                Shadows.shadowOf(this as PorterDuffColorFilter).color.assertEquals(Color.TRANSPARENT)
            }
        }
    }
}