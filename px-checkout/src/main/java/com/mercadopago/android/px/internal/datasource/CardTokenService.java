package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.core.AuthorizationProvider;
import com.mercadopago.android.px.internal.model.CardTokenBody;
import com.mercadopago.android.px.internal.model.RemotePaymentToken;
import com.mercadopago.android.px.internal.repository.CardTokenRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.services.GatewayService;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Device;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.services.Callback;

import static com.mercadopago.android.px.services.BuildConfig.API_ENVIRONMENT_NEW;

public class CardTokenService implements CardTokenRepository {

    /* default */ @NonNull final PaymentSettingRepository paymentSettingRepository;
    /* default */ @NonNull final ESCManagerBehaviour escManagerBehaviour;
    /* default */ @NonNull final AuthorizationProvider authorizationProvider;
    @NonNull private final Device device;
    @NonNull private final GatewayService gatewayService;

    public CardTokenService(@NonNull final GatewayService gatewayService,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final Device device,
        @NonNull final ESCManagerBehaviour escManagerBehaviour,
        @NonNull final AuthorizationProvider authorizationProvider) {
        this.gatewayService = gatewayService;
        this.paymentSettingRepository = paymentSettingRepository;
        this.device = device;
        this.escManagerBehaviour = escManagerBehaviour;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public MPCall<Token> createToken(@NonNull final String cardId, @NonNull final String cvv,
        @Nullable final RemotePaymentToken remotePaymentToken, final boolean requireEsc) {
        final CardTokenBody body = new CardTokenBody(cardId, device, requireEsc, cvv, "", remotePaymentToken);
        return gatewayService
            .createToken(paymentSettingRepository.getPublicKey(), body);
    }

    @Override
    public void clearCap(@NonNull final String cardId, @NonNull final ClearCapCallback callback) {
        if (TextUtil.isEmpty(authorizationProvider.getPrivateKey())) {
            callback.execute();
            return;
        }
        gatewayService.clearCap(API_ENVIRONMENT_NEW, cardId)
            .enqueue(new Callback<String>() {
                @Override
                public void success(final String s) {
                    callback.execute();
                }

                @Override
                public void failure(final ApiException apiException) {
                    callback.execute();
                }
            });
    }
}