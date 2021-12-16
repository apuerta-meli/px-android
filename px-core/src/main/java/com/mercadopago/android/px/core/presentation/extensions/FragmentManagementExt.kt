package com.mercadopago.android.px.core.presentation.extensions

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * Returns a boolean that represents if the fragment is visible or not
 */
fun FragmentManager.isFragmentVisible(tag: String): Boolean {
    val fragment = findFragmentByTag(tag)
    return fragment != null && fragment.isAdded && fragment.userVisibleHint
}

/**
 * Try-caught remove by tag
 */
fun FragmentManager.tryRemoveNow(tag: String) {
    val fragment = findFragmentByTag(tag)
    fragment?.let{
        with(beginTransaction().remove(fragment)) {
            try {
                commitNowAllowingStateLoss()
            } catch (e: IllegalStateException) {
                commitAllowingStateLoss()
            }
        }
    }
}

/**
 * Shows a given fragment, if it doesn't exist, replacing it in the container and setting custom animations
 *
 * @param container Container where the fragment is contained
 * @param fragmentTag Tag that represents the fragment
 * @param block Function to obtain the fragment to use
 * @param enterAnim (Optional) Custom enter animation resource id
 * @param exitAnim (Optional) Custom exit animation resource id
 */
fun AppCompatActivity.showFragment(
    container: Int,
    fragmentTag: String,
    block: () -> Fragment,
    @AnimatorRes @AnimRes enterAnim: Int = 0,
    @AnimatorRes @AnimRes exitAnim: Int = 0
) {
    if (!supportFragmentManager.fragmentExist(fragmentTag)) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(enterAnim, exitAnim)
            .replace(container, block(), fragmentTag)
            .commitNowAllowingStateLoss()
    }
}

private fun FragmentManager.fragmentExist(fragmentTag: String): Boolean = findFragmentByTag(fragmentTag) != null
