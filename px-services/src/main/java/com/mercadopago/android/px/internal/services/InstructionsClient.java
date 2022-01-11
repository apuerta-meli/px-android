package com.mercadopago.android.px.internal.services;

import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.Instructions;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InstructionsClient {

    String INSTRUCTIONS_VERSION = "1.7";

    @GET("{environment}/checkout/payments/{payment_id}/results?api_version=" + INSTRUCTIONS_VERSION)
    MPCall<Instructions> getInstructions(
        @Path(value = "environment", encoded = true) String environment,
        @Path(value = "payment_id", encoded = true) Long paymentId,
        @Query("public_key") String mKey,
        @Query("payment_type") String paymentTypeId);
}