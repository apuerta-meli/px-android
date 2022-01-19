package com.mercadopago.android.px.configuration

/***
 * Provides a configuration to be executed just after the payment but before the congrats.
 *
 * @sample "Open a custom flow after a success payment and then continue with the congrats once the custom flow ends."
 */
class PostPaymentConfiguration private constructor(builder: Builder) {

    var postPaymentDeepLinkUrl: String? = null
        private set

    init {
        this.postPaymentDeepLinkUrl = builder.postPaymentDeepLinkUrl
    }

    fun hasPostPaymentUrl() = !postPaymentDeepLinkUrl.isNullOrEmpty()

    class Builder {

        var postPaymentDeepLinkUrl: String? = null
            private set

        /***
         * Sets a DeepLink to be launched after a success payment
         *
         * @param url with the desired deeplink to be launched
         * @return the builder instance
         */
        fun setPostPaymentDeepLinkUrl(url: String) = apply {
            postPaymentDeepLinkUrl = url
        }

        fun build() = PostPaymentConfiguration(this)
    }
}
