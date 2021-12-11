package com.mercadopago.android.px.internal.features.checkout

import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel

internal class PostPaymentDriver(builder: Builder) {

    private val paymentModel = builder.paymentModel
    private val postPaymentUrls = builder.postPaymentUrls
    private val postPaymentDeepLink: String = builder.postPaymentDeepLink
    private val action = builder.action

    fun execute() {
        when {
            postPaymentDeepLink.isNotNullNorEmpty() -> action.showPostPaymentScreen(postPaymentDeepLink, paymentModel)
            postPaymentUrls.redirectUrl.isNotNullNorEmpty() -> action.skipCongrats(paymentModel)
            paymentModel is BusinessPaymentModel -> action.showCongrats(paymentModel)
            else -> action.showCongrats(paymentModel)
        }
    }

    class Builder(
        internal val paymentModel: PaymentModel,
        internal val postPaymentUrls: PostPaymentUrlsMapper.Response
    ) {
        internal lateinit var action: Action
        internal var postPaymentDeepLink: String = ""

        fun action(action: Action) = apply { this.action = action }
        fun postPaymentDeepLink(deepLink: String) = apply { this.postPaymentDeepLink = deepLink }

        fun build() = PostPaymentDriver(this)
    }

    interface Action {
        fun showCongrats(model: PaymentModel)
        fun showCongrats(model: BusinessPaymentModel)
        fun showPostPaymentScreen(postPaymentDeepLink: String, paymentModel: PaymentModel)
        fun skipCongrats(model: PaymentModel)
    }
}
