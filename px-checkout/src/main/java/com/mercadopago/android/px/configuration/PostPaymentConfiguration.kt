package com.mercadopago.android.px.configuration

/***
 * Provides a configuration to be executed just after the payment but before the congrats.
 *
 * @sample "Open a custom flow after a success payment and then continue with the congrats once the custom flow ends."
 */
class PostPaymentConfiguration private constructor() {

    private var postPaymentDeepLinkUrl: String? = null

    private constructor(builder: Builder): this() {
        this.postPaymentDeepLinkUrl = builder.getPostPaymentDeepLinkUrl()
    }

    fun getPostPaymentDeepLinkUrl(): String {
        return postPaymentDeepLinkUrl.orEmpty()
    }

    class Builder {

        private var postPaymentDeepLinkUrl: String? = null

        /***
         * sets a DeepLink to be launched after a success payment
         *
         * @param url with the desired deeplink to be launched
         */
        fun setPostPaymentDeepLinkUrl(url: String): Builder {
            postPaymentDeepLinkUrl = url
            return this
        }

        fun getPostPaymentDeepLinkUrl() = postPaymentDeepLinkUrl

        fun build() = PostPaymentConfiguration(this)
    }
}
