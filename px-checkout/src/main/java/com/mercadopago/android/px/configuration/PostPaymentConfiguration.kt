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

    fun getPostPaymentDeepLinkUrl() = postPaymentDeepLinkUrl.orEmpty()

    fun hasPostPaymentUrl() = !postPaymentDeepLinkUrl.isNullOrEmpty()

    class Builder {

        private var postPaymentDeepLinkUrl: String? = null

        /***
         * Sets a DeepLink to be launched after a success payment
         *
         * @param url with the desired deeplink to be launched
         * @return the builder instance
         */
        fun setPostPaymentDeepLinkUrl(url: String) = apply {
            postPaymentDeepLinkUrl = url
        }

        /***
         * Gets the DeepLink to be launched after a success payment
         *
         * @return the deepLink String
         */
        fun getPostPaymentDeepLinkUrl() = postPaymentDeepLinkUrl

        fun build() = PostPaymentConfiguration(this)
    }
}
