package com.mercadopago.android.px.services;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.addons.BehaviourProvider;
import com.mercadopago.android.px.internal.model.CardTokenBody;
import com.mercadopago.android.px.internal.model.SecurityType;
import com.mercadopago.android.px.internal.services.BankDealService;
import com.mercadopago.android.px.internal.services.CheckoutService;
import com.mercadopago.android.px.internal.services.DiscountService;
import com.mercadopago.android.px.internal.services.GatewayService;
import com.mercadopago.android.px.internal.services.IdentificationService;
import com.mercadopago.android.px.internal.services.InstallmentService;
import com.mercadopago.android.px.internal.services.InstructionsClient;
import com.mercadopago.android.px.internal.services.IssuersService;
import com.mercadopago.android.px.internal.services.PaymentService;
import com.mercadopago.android.px.internal.services.PreferenceService;
import com.mercadopago.android.px.internal.util.RetrofitUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.BankDeal;
import com.mercadopago.android.px.model.CardToken;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.IdentificationType;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.model.Instructions;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.ProcessingMode;
import com.mercadopago.android.px.model.SavedCardToken;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.requests.SecurityCodeIntent;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Retrofit;

import static com.mercadopago.android.px.services.BuildConfig.API_ENVIRONMENT;

/**
 * MercadoPagoServices provides an interface to access to our main API methods.
 */
@SuppressWarnings("unused")
public class MercadoPagoServices {

    /* default */ final Context context;
    /* default */ final String publicKey;
    /* default */ final String privateKey;
    private final ProcessingMode processingMode;
    private final Retrofit retrofitClient;

    /**
     * @param context    context to obtain connection interceptor and cache.
     * @param publicKey  merchant public key / collector public key {@see <a href="https://www.mercadopago.com/mla/account/credentials">credentials</a>}
     * @param privateKey user private key / access_token if you have it.
     */
    public MercadoPagoServices(@NonNull final Context context,
        @NonNull final String publicKey,
        @Nullable final String privateKey) {
        this.context = context;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        processingMode = ProcessingMode.AGGREGATOR;
        retrofitClient = RetrofitUtil.getRetrofitClient(context);
    }

    public void getCheckoutPreference(final String checkoutPreferenceId, final Callback<CheckoutPreference> callback) {
        final PreferenceService service = retrofitClient.create(PreferenceService.class);
        service.getPreference(checkoutPreferenceId, publicKey).enqueue(callback);
    }

    public void getInstructions(final Long paymentId, final String paymentTypeId,
        final Callback<Instructions> callback) {
        final InstructionsClient service = retrofitClient.create(InstructionsClient.class);
        service.getInstructions(API_ENVIRONMENT, paymentId, publicKey,
            paymentTypeId).enqueue(callback);
    }

    /**
     * @param amount
     * @param excludedPaymentTypes
     * @param excludedPaymentMethods
     * @param cardsWithEsc
     * @param site
     * @param differentialPricing
     * @param callback
     * @deprecated please use {@link #getPaymentMethods(Callback)}}
     */
    @Deprecated
    public void getPaymentMethodSearch(final BigDecimal amount, final List<String> excludedPaymentTypes,
        final List<String> excludedPaymentMethods, final List<String> cardsWithEsc,
        final Site site, @Nullable final Integer differentialPricing,
        final Callback<PaymentMethodSearch> callback) {
        final CheckoutService service = retrofitClient.create(CheckoutService.class);

        final String separator = ",";
        final String excludedPaymentTypesAppended = getListAsString(excludedPaymentTypes, separator);
        final String excludedPaymentMethodsAppended = getListAsString(excludedPaymentMethods, separator);
        final String cardsWithEscAppended = getListAsString(cardsWithEsc, separator);

        service.getPaymentMethodSearch(
            API_ENVIRONMENT, publicKey, amount,
            excludedPaymentTypesAppended, excludedPaymentMethodsAppended, site.getId(),
            processingMode.asQueryParamName(), cardsWithEscAppended,
            differentialPricing, null, false,
            privateKey).
            enqueue(callback);
    }

    public void createToken(final CardToken cardToken, final Callback<Token> callback) {
        cardToken.setDevice(context);
        final GatewayService service = retrofitClient.create(GatewayService.class);
        service.createToken(publicKey, cardToken).enqueue(callback);
    }

    public void createToken(final SavedCardToken savedCardToken, final Callback<Token> callback) {
        savedCardToken.setDevice(context);
        final GatewayService service = retrofitClient.create(GatewayService.class);
        final CardTokenBody body =
            new CardTokenBody(savedCardToken.getCardId(), savedCardToken.getDevice(), savedCardToken.getSecurityCode());
        service.createToken(publicKey, body).enqueue(callback);
    }

    public void createToken(final SavedESCCardToken savedESCCardToken, final Callback<Token> callback) {
        savedESCCardToken.setDevice(context);
        final GatewayService service = retrofitClient.create(GatewayService.class);
        final CardTokenBody body = new CardTokenBody(savedESCCardToken.getCardId(), savedESCCardToken.getDevice(), true,
            savedESCCardToken.getSecurityCode(), savedESCCardToken.getEsc(), null);
        service.createToken(publicKey, body).enqueue(callback);
    }

    public void cloneToken(final String tokenId, final Callback<Token> callback) {
        final GatewayService service = retrofitClient.create(GatewayService.class);
        service.cloneToken(tokenId, publicKey).enqueue(callback);
    }

    public void putSecurityCode(final String tokenId, final SecurityCodeIntent securityCodeIntent,
        final Callback<Token> callback) {
        final GatewayService service = retrofitClient.create(GatewayService.class);
        service.updateToken(tokenId, publicKey, securityCodeIntent).enqueue(callback);
    }

    public void getBankDeals(final Callback<List<BankDeal>> callback) {
        final BankDealService service = retrofitClient.create(BankDealService.class);
        service.getBankDeals(publicKey, BehaviourProvider.getLocaleBehaviour().getLocale().toLanguageTag())
            .enqueue(callback);
    }

    public void getIdentificationTypes(final Callback<List<IdentificationType>> callback) {
        if (TextUtil.isNotEmpty(privateKey)) {
            getIdentificationTypes(privateKey, callback);
        } else {
            final IdentificationService service =
                retrofitClient.create(IdentificationService.class);
            service
                .getIdentificationTypesNonAuthUser(publicKey);
        }
    }

    @Deprecated
    public void getIdentificationTypes(final String accessToken, final Callback<List<IdentificationType>> callback) {
        final IdentificationService service =
            retrofitClient.create(IdentificationService.class);
        service.getIdentificationTypesForAuthUser().enqueue(callback);
    }

    public void getInstallments(final String bin,
        final BigDecimal amount,
        final Long issuerId,
        final String paymentMethodId,
        @Nullable final Integer differentialPricingId,
        final Callback<List<Installment>> callback) {
        final InstallmentService service = retrofitClient.create(InstallmentService.class);
        service.getInstallments(API_ENVIRONMENT, publicKey, bin, amount, issuerId, paymentMethodId,
            BehaviourProvider.getLocaleBehaviour().getLocale().toLanguageTag(),
            processingMode.asQueryParamName(), differentialPricingId).enqueue(callback);
    }

    public void getIssuers(final String paymentMethodId, final String bin, final Callback<List<Issuer>> callback) {
        final IssuersService service = retrofitClient.create(IssuersService.class);
        service
            .getIssuers(API_ENVIRONMENT, publicKey, paymentMethodId, bin,
                processingMode.asQueryParamName()).enqueue(callback);
    }

    public void getPaymentMethods(final Callback<List<PaymentMethod>> callback) {
        final CheckoutService service = retrofitClient.create(CheckoutService.class);
        service.getPaymentMethods(API_ENVIRONMENT, publicKey, privateKey).enqueue(callback);
    }

    /**
     * @param amount     amount to pay
     * @param payerEmail payer email
     * @param callback   your callback
     * @deprecated this mechanism will not be available anymore in {@version 5.0}
     */
    @Deprecated
    public void getDirectDiscount(final String amount, final String payerEmail, final Callback<Discount> callback) {
        final DiscountService service = retrofitClient.create(DiscountService.class);
        service.getDiscount(publicKey, amount, payerEmail).enqueue(callback);
    }

    /**
     * @param amount     amount to pay
     * @param payerEmail payer email
     * @param couponCode the code to be rewarded
     * @param callback   your callback
     * @deprecated this mechanism will not be available anymore in {@version 5.0}
     */
    @Deprecated
    public void getCodeDiscount(final String amount, final String payerEmail, final String couponCode,
        final Callback<Discount> callback) {
        final DiscountService service = retrofitClient.create(DiscountService.class);
        service.getDiscount(publicKey, amount, payerEmail, couponCode).enqueue(callback);
    }

    @Deprecated
    public void createPayment(final String transactionId, final Map<String, Object> paymentData,
        @NonNull final Map<String, String> query, final Callback<Payment> callback) {
        createPayment(transactionId, paymentData, callback);
    }

    public void createPayment(final String transactionId, final Map<String, Object> paymentData,
        final Callback<Payment> callback) {
        createPayment(transactionId, SecurityType.NONE.getValue(), paymentData, callback);
    }

    public void createPayment(final String transactionId, final String securityType,
        final Map<String, Object> paymentData, final Callback<Payment> callback) {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("public_key", publicKey);

        final PaymentService paymentService = retrofitClient.create(PaymentService.class);
        final String profileId = BehaviourProvider.getAuthenticationBehaviour().getDeviceProfileId();
        paymentService.createPayment(transactionId, securityType, profileId, paymentData, queryParams)
            .enqueue(callback);
    }

    private String getListAsString(final List<String> list, final String separator) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (list != null) {
            for (final String typeId : list) {
                stringBuilder.append(typeId);
                if (!typeId.equals(list.get(list.size() - 1))) {
                    stringBuilder.append(separator);
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * @param preferenceBuilder
     * @param callback
     */
    public void createPreference(@NonNull final CheckoutPreference.Builder preferenceBuilder,
        @NonNull final Callback<CheckoutPreference> callback) {
        final PreferenceService preferenceService =
            retrofitClient.create(PreferenceService.class);
        preferenceService.createPreference(preferenceBuilder.build()).enqueue(callback);
    }
}
