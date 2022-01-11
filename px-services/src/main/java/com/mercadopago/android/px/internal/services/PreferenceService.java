package com.mercadopago.android.px.internal.services;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PreferenceService {

    @GET("/v1/checkout/preferences/{preference_id}")
    MPCall<CheckoutPreference> getPreference(@Path(value = "preference_id", encoded = true) String checkoutPreferenceId,
        @Query("public_key") String publicKey);

    @POST("/checkout/preferences")
    MPCall<CheckoutPreference> createPreference(@NonNull @Body final CheckoutPreference preference);
}
