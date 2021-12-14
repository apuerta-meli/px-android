package com.mercadopago.android.px.configuration

/***
 * Provides a configuration to be executed just after the payment but before the congrats.
 *
 * @sample "Open a custom flow after a success payment and then continue with the congrats."
 */
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

        /***
         * sets a DeepLink to be launched after a success payment
         *
         * @param url with the desired deeplink to be launched
         */
        fun setPostPaymentDeepLinkURL(url: String): Builder {
            postPaymentURL = url
            return this
        }

        fun build() = PostPaymentConfiguration(this)
    }
}
