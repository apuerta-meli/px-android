package com.mercadopago.android.px.configuration

class PostPaymentConfiguration private constructor() {

    private var postPaymentURL: String? = null

    private constructor(builder: Builder): this() {
        this.postPaymentURL = builder.postPaymentURL
    }

    fun getPostPaymentDeepLink(): String {
        return postPaymentURL.orEmpty()
    }

    class Builder {

        var postPaymentURL: String? = null

        fun setPostPaymentDeepLinkURL(url: String): Builder {
            postPaymentURL = url
            return this
        }

        fun build() = PostPaymentConfiguration(this)
    }
}
