package com.mercadopago.android.px.internal.view

import android.content.Context
import android.util.AttributeSet
import android.view.accessibility.AccessibilityNodeInfo
import android.view.animation.AnimationUtils
import com.mercadopago.android.px.R

internal class HorizontalElementDescriptorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ElementDescriptorView(context, attrs) {

    private val slideDownIn = AnimationUtils.loadAnimation(context, R.anim.px_summary_slide_down_in)
    private val toolbarAppearAnimation = AnimationUtils.loadAnimation(context, R.anim.px_toolbar_appear)
    private val toolbarDisappearAnimation = AnimationUtils.loadAnimation(context, R.anim.px_toolbar_disappear)

    override fun animateEnter(shouldSlide: Boolean) {
        startAnimation(if (shouldSlide) slideDownIn else toolbarAppearAnimation)
        post {
            performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null)
        }
    }

    override fun animateExit() {
        startAnimation(toolbarDisappearAnimation)
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    }
}