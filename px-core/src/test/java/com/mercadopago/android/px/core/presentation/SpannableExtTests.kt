package com.mercadopago.android.px.core.presentation

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.text.getSpans
import com.mercadopago.android.px.core.commons.extensions.EMPTY
import com.mercadopago.android.px.core.presentation.extensions.setColor
import com.mercadopago.android.px.core.presentation.extensions.setFont
import com.mercadopago.android.px.core.utils.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SpannableExtTests {

    @Test
    fun setColorFromHexSetsTheColorWhenItsNotNull() {
        val color = ForegroundColorSpan(Color.parseColor("#999999"))
        with(SpannableStringBuilder("texto spannable 123")) {
            setColor("#999999", 0, length)
            getSpans<ForegroundColorSpan>(0, length).first().foregroundColor.assertEquals(color.foregroundColor)
        }
    }

    @Test
    fun setColorFromHexDoesNotSetsTheColorWhenItsNull() {
        with(SpannableStringBuilder("texto spannable 123")) {
            setColor(null, 0, length)
            assertNull(getSpans<ForegroundColorSpan>(0, length).firstOrNull())
        }
    }

    @Test
    fun setColorFromHexDoesNotSetsTheColorWhenItsEmpty() {
        with(SpannableStringBuilder("texto spannable 123")) {
            setColor(EMPTY, 0, length)
            assertNull(getSpans<ForegroundColorSpan>(0, length).firstOrNull())
        }
    }

    @Test
    fun setColorFromResDoesNotSetsTheColorWhenItsZero() {
        with(SpannableStringBuilder("texto spannable 123")) {
            setColor(0, 0, length)
            assertNull(getSpans<ForegroundColorSpan>(0, length).firstOrNull())
        }
    }

    @Test
    fun setColorFromResSetsTheColorWhenItsNotZero() {
        val color = ForegroundColorSpan(android.R.color.white)
        with(SpannableStringBuilder("texto spannable 123")) {
            setColor(android.R.color.white, 0, length)
            getSpans<ForegroundColorSpan>(0, length).first().foregroundColor.assertEquals(color.foregroundColor)
        }
    }

    @Test
    fun setFontFromTypefaceShouldSetStyle() {
        val typeface = Typeface.DEFAULT_BOLD
        with(SpannableStringBuilder("texto spannable 123")) {
            setFont(typeface, 0, length)
            getSpans<StyleSpan>(0, length).first().style.assertEquals(typeface.style)
        }
    }
}
