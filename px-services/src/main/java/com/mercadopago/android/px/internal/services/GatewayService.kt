package com.mercadopago.android.px.internal.services

import com.mercadopago.android.px.internal.callbacks.MPCall
import com.mercadopago.android.px.internal.model.CardTokenBody
import com.mercadopago.android.px.model.CardToken
import com.mercadopago.android.px.model.Token
import com.mercadopago.android.px.model.requests.SecurityCodeIntent
import com.mercadopago.android.px.services.BuildConfig
import retrofit2.http.*

interface GatewayService {

    @POST("/v1/card_tokens${BuildConfig.GATEWAY_ENVIRONMENT}")
    fun createToken(
        @Query("public_key") publicKey: String,
        @Body body: CardTokenBody
    ): MPCall<Token>

    @DELETE("${BuildConfig.API_ENVIRONMENT_NEW}/px_mobile/v1/esc_cap/{card_id}")
    fun clearCap(
        @Path(value = "card_id") cardId: String
    ): MPCall<String>

    @Deprecated("Not supported anymore")
    @POST("/v1/card_tokens")
    fun createToken(
        @Query("public_key") publicKey: String,
        @Body cardToken: CardToken
    ): MPCall<Token>

    @Deprecated("Not supported anymore")
    @POST("/v1/card_tokens/{token_id}/clone")
    fun cloneToken(
        @Path(value = "token_id") tokenId: String,
        @Query("public_key") publicKey: String
    ): MPCall<Token>

    @Deprecated("Not supported anymore")
    @PUT("/v1/card_tokens/{token_id}")
    fun updateToken(
        @Path(value = "token_id") tokenId: String,
        @Query("public_key") publicKey: String,
        @Body securityCodeIntent: SecurityCodeIntent
    ): MPCall<Token>
}
