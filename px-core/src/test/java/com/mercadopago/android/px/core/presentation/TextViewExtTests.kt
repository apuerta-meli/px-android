package com.mercadopago.android.px.core.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.text.getSpans
import com.mercadopago.android.px.core.R
import com.mercadopago.android.px.core.commons.extensions.EMPTY
import com.mercadopago.android.px.core.presentation.extensions.loadLikeHtmlOrGone
import com.mercadopago.android.px.core.presentation.extensions.loadOrGone
import com.mercadopago.android.px.core.presentation.extensions.loadTextListOrGone
import com.mercadopago.android.px.core.presentation.extensions.setTextColor
import com.mercadopago.android.px.core.utils.BasicRobolectricTest
import com.mercadopago.android.px.core.utils.assertEquals
import com.mercadopago.android.px.core.utils.assertGone
import com.mercadopago.android.px.core.utils.assertText
import com.mercadopago.android.px.core.utils.assertVisible
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TextViewExtTests: BasicRobolectricTest() {

    private val textView: TextView = TextView(getContext())

    @Test
    fun loadOrGoneFromTextShouldBeGoneWhenTextIsNull() {
        with(textView) {
            loadLikeHtmlOrGone(null)
            assertGone()
        }
    }

    @Test
    fun loadOrGoneFromTextShouldBeGoneWhenTextIsEmpty() {
        with(textView) {
            loadLikeHtmlOrGone(EMPTY)
            assertGone()
        }
    }

    @Test
    fun loadHtmlOrGoneFromTextShouldBeVisibleAndSetTextWhenTextIsNotEmpty() {
        with(textView) {
            loadLikeHtmlOrGone("<strong>Text</strong>")
            assertVisible()
            assertText("Text")
        }
    }

    @Test
    fun loadOrGoneFromResIdShouldBeGoneWhenResIsZero() {
        with(textView) {
            loadOrGone(0)
            assertGone()
        }
    }

    @Test
    fun loadOrGoneFromResIdShouldBeVisibleWhenResIsValidRes() {
        val resId = R.string.px_error_title
        with(textView) {
            loadOrGone(resId)
            assertVisible()
            assertText(context.getText(resId))
        }
    }

    @Test
    fun loadHtmlOrGoneFromTextShouldBeGoneWhenTextIsNull() {
        with(textView) {
            loadLikeHtmlOrGone(null)
            assertGone()
        }
    }

    @Test
    fun loadHtmlOrGoneFromTextShouldBeGoneWhenTextIsEmpty() {
        with(textView) {
            loadLikeHtmlOrGone(EMPTY)
            assertGone()
        }
    }

    @Test
    fun loadOrGoneFromTextShouldBeVisibleAndSetTextWhenTextIsNotEmpty() {
        val text = "Text"
        with(textView) {
            loadOrGone(text)
            assertVisible()
            assertText(text)
        }
    }

    @Test
    fun setTextColorFromStringShouldNotSetColorWhenColorIsNull() {
        with(textView) {
            textView.setTextColor(android.R.color.holo_red_dark)
            val colorString: String? = null
            setTextColor(colorString)
            currentTextColor.assertEquals(android.R.color.holo_red_dark)
        }
    }

    @Test
    fun setTextColorFromStringShouldNotSetColorWhenColorIsEmpty() {
        with(textView) {
            textView.setTextColor(android.R.color.holo_red_dark)
            setTextColor(EMPTY)
            currentTextColor.assertEquals(android.R.color.holo_red_dark)
        }
    }

    @Test
    fun setTextColorFromStringShouldSetColorWhenColorIsNotEmpty() {
        with(textView) {
            val color = "#999999"
            textView.setTextColor(android.R.color.holo_red_dark)
            setTextColor(color)
            currentTextColor.assertEquals(Color.parseColor(color))
        }
    }

    @Test
    fun loadTextListOrGoneShouldBeGoneWhenNull() {
        with(textView) {
            loadTextListOrGone(null)
            assertGone()
        }
    }

    @Test
    fun loadTextListOrGoneShouldBeGoneWhenEmpty() {
        with(textView) {
            loadTextListOrGone(listOf())
            assertGone()
        }
    }

    @Test
    fun loadTextListOrGoneShouldApplyStyleWhenSingleText() {
        val color = android.R.color.holo_blue_dark
        val textDescriptor = TextDescriptor("One text", Typeface.DEFAULT_BOLD, color, 14)
        with(textView) {
            loadTextListOrGone(listOf(textDescriptor))
            assertVisible()
            assertText("One text")
        }
        assert(textView.text is SpannedString)
        with(textView.text as SpannedString) {
            getSpans<ForegroundColorSpan>().first().foregroundColor.assertEquals(color)
            getSpans<StyleSpan>().first().style.assertEquals(Typeface.DEFAULT_BOLD.style)
            getSpans<AbsoluteSizeSpan>().first().size.assertEquals(14)
        }
    }

    @Test
    fun loadTextListOrGoneShouldApplyStyleWhenMultipleTexts() {
        val firstColor = android.R.color.holo_blue_dark
        val firstText = "First text"
        val secondText = "Second text"
        val secondColor = android.R.color.holo_orange_dark
        val firstTextDescriptor = TextDescriptor(firstText, Typeface.DEFAULT_BOLD, firstColor, 14)
        val secondTextDescriptor = TextDescriptor(secondText, Typeface.MONOSPACE, secondColor, 16)
        with(textView) {
            loadTextListOrGone(listOf(firstTextDescriptor, secondTextDescriptor))
            assertVisible()
            assertText("First text Second text")
        }
        assert(textView.text is SpannedString)
        with(textView.text as SpannedString) {
            getSpans<ForegroundColorSpan>(0, firstText.length).first().foregroundColor.assertEquals(firstColor)
            getSpans<StyleSpan>(0, firstText.length).first().style.assertEquals(Typeface.DEFAULT_BOLD.style)
            getSpans<AbsoluteSizeSpan>(0, firstText.length).first().size.assertEquals(14)
            val secondTextStartIndex = indexOf(secondText)
            getSpans<ForegroundColorSpan>(secondTextStartIndex, secondTextStartIndex + secondText.length)
                .first().foregroundColor.assertEquals(secondColor)
            getSpans<StyleSpan>(secondTextStartIndex, secondTextStartIndex + secondText.length)
                .first().style.assertEquals(Typeface.MONOSPACE.style)
            getSpans<AbsoluteSizeSpan>(secondTextStartIndex, secondTextStartIndex + secondText.length)
                .first().size.assertEquals(16)
        }
    }

    internal class TextDescriptor(
        private val text: CharSequence,
        private val typeface: Typeface,
        private val color: Int,
        private val textSize: Int?
    ) : ITextDescriptor {

        override fun getText(context: Context) = text

        override fun getFont(context: Context) = typeface

        override fun getTextColor(context: Context) = color

        override fun getFontSize(context: Context) = textSize
    }
}
