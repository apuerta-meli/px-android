package com.mercadopago.android.px.internal.services;

import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.services.BuildConfig;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface PaymentService {

    String PAYMENTS_VERSION = "2.0";

    @POST(BuildConfig.API_ENVIRONMENT + "/px_mobile/payments?api_version=" + PAYMENTS_VERSION)
    MPCall<Payment> createPayment(
        @Header("X-Idempotency-Key") String transactionId, @Header("X-Security") String securityType,
        @Nullable @Header("X-Meli-Session-Id") String profileId, @Body Map<String, Object> additionalInfo,
        @QueryMap Map<String, String> query);
}
