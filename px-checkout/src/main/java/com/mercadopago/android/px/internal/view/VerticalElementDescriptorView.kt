package com.mercadopago.android.px.internal.view

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import com.mercadopago.android.px.R

internal class VerticalElementDescriptorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ElementDescriptorView(context, attrs) {

    private val logoAppearAnimation = AnimationUtils.loadAnimation(context, R.anim.px_summary_logo_appear)
    private val logoDisappearAnimation = AnimationUtils.loadAnimation(context, R.anim.px_summary_logo_disappear)
    private val slideDownIn = AnimationUtils.loadAnimation(context, R.anim.px_summary_slide_down_in)

    override fun animateEnter(shouldSlide: Boolean) {
        visibility = VISIBLE
        startAnimation(if (shouldSlide) slideDownIn else logoAppearAnimation)
    }

    override fun animateExit() {
        startAnimation(logoDisappearAnimation)
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    }
}