package com.mercadopago.android.px.core.presentation

import android.widget.ImageView
import com.mercadolibre.android.picassodiskcache.PicassoDiskLoader
import com.mercadopago.android.px.core.commons.extensions.EMPTY
import com.mercadopago.android.px.core.presentation.extensions.loadOrElse
import com.mercadopago.android.px.core.presentation.extensions.loadOrGone
import com.mercadopago.android.px.core.utils.BasicRobolectricTest
import com.mercadopago.android.px.core.utils.assertEquals
import com.mercadopago.android.px.core.utils.assertGone
import com.mercadopago.android.px.core.utils.assertVisible
import com.mercadopago.android.px.core.utils.setField
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Transformation
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

@RunWith(RobolectricTestRunner::class)
class ImageViewExtTests: BasicRobolectricTest() {

    @Mock(answer = Answers.RETURNS_MOCKS)
    private lateinit var picassoMock: Picasso

    @Mock(answer = Answers.RETURNS_MOCKS)
    private lateinit var requestCreator: RequestCreator

    @Mock
    private lateinit var transformation: Transformation

    @Before
    fun setup(){
        whenever(picassoMock.load(any<String>())).thenReturn(requestCreator)
        whenever(picassoMock.load(any<Int>())).thenReturn(requestCreator)
        PicassoDiskLoader.setField("INSTANCE", picassoMock)
    }

    @Test
    fun loadOrGoneFromResShouldBeGoneWhenResIdIsZero() {
        with(ImageView(getContext())) {
            loadOrGone(0)
            assertGone()
        }
    }

    @Test
    fun loadOrGoneFromResShouldBeVisibleWhenResIdIsNotZero() {
        val resId = android.R.drawable.ic_delete
        with(ImageView(getContext())) {
            loadOrGone(resId)
            assertVisible()
            assertNotNull(drawable)
            Shadows.shadowOf(drawable).createdFromResId.assertEquals(resId)
        }
    }

    @Test
    fun loadOrGoneFromUrlShouldBeGoneWhenUrlIsNull() {
        with(ImageView(getContext())) {
            loadOrGone(null)
            assertGone()
        }
    }

    @Test
    fun loadOrGoneFromUrlShouldBeGoneWhenUrlIsEmpty() {
        with(ImageView(getContext())) {
            loadOrGone("")
            assertGone()
        }
    }

    @Test
    fun loadOrGoneFromUrlShouldBeVisibleWhenUrlIsNotEmpty() {
        val url = "https://via.placeholder.com/150"
        with(ImageView(getContext())) {
            loadOrGone(url)
            assertNull(drawable)
            assertVisible()
            verify(picassoMock).load(url)
        }
    }

    @Test
    fun loadOrElseShouldCallPicassoLoadWhenUrlIsNotNull() {
        val url = "https://via.placeholder.com/150"
        val resId = android.R.drawable.ic_delete
        with(ImageView(getContext())) {
            loadOrElse(url, resId)
            verify(picassoMock).load(url)
        }
    }

    @Test
    fun loadOrElseShouldNotCallTransformWhenNoTransformationProvided() {
        with(ImageView(getContext())) {
            loadOrElse(null, android.R.drawable.ic_delete)
            verifyZeroInteractions(requestCreator.transform(any<Transformation>()))
        }
    }

    @Test
    fun loadOrElseShouldApplyTransformationWhenHasTransformation() {
        val url = "https://via.placeholder.com/150"
        val resId = android.R.drawable.ic_delete
        with(ImageView(getContext())) {
            loadOrElse(url, resId, transformation)
            verify(requestCreator).transform(transformation)
        }
    }
    @Test
    fun loadOrElseShouldBeDefaultWhenUrlIsEmpty() {
        val resId = android.R.drawable.ic_delete
        whenever(requestCreator.placeholder(resId)).thenReturn(requestCreator)
        with(ImageView(getContext())) {
            loadOrElse(EMPTY, resId)
            verify(picassoMock).load(resId)
            verify(requestCreator).into(this)
        }
    }

    @Test
    fun loadOrElseShouldBeDefaultWhenUrlIsNull() {
        val resId = android.R.drawable.ic_delete
        whenever(requestCreator.placeholder(resId)).thenReturn(requestCreator)
        with(ImageView(getContext())) {
            loadOrElse(null, resId)
            verify(picassoMock).load(resId)
            verify(requestCreator).into(this)
        }
    }
}
