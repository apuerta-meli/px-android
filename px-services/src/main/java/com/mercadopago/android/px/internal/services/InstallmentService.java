package com.mercadopago.android.px.internal.services;

import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.Installment;
import java.math.BigDecimal;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InstallmentService {

    @GET("{environment}/checkout/payment_methods/installments")
    MPCall<List<Installment>> getInstallments(
        @Path(value = "environment", encoded = true) String environment,
        @Query("public_key") String publicKey, @Query("bin") String bin,
        @Query("amount") BigDecimal amount, @Query("issuer.id") Long issuerId,
        @Query("payment_method_id") String paymentMethodId,
        @Query("locale") String locale,
        @Query("processing_mode") String processingMode,
        @Query("differential_pricing_id") Integer differentialPricingId);
}
