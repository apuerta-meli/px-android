package com.mercadopago.android.px.internal.services;

import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.Issuer;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IssuersService {

    @GET("{environment}/checkout/payment_methods/card_issuers")
    MPCall<List<Issuer>> getIssuers(
        @Path(value = "environment", encoded = true) String environment,
        @Query("public_key") String publicKey,
        @Query("payment_method_id") String paymentMethodId, @Query("bin") String bin,
        @Query("processing_modes") String processingMode);
}