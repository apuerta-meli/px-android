package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.features.payment_congrats.model.FromPaymentCongratsDiscountItemToItemId;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.PaymentDataHelper;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethods;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracking.internal.TrackingHelper;
import com.mercadopago.android.px.tracking.internal.mapper.FromDiscountItemToItemId;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class ResultViewTrackModel extends TrackingMapModel {

    private final String style;
    private final Long paymentId;
    private final String paymentStatus;
    private final String paymentStatusDetail;
    private final String currencyId;
    private final boolean hasSplitPayment;
    private final BigDecimal totalAmount;
    private final BigDecimal discountCouponAmount;
    private final String paymentMethodId;
    private final String paymentMethodType;
    private final Integer scoreLevel;
    private final String campaignId;
    private final String campaignsIds;
    private final int discountsCount;
    private final Map<String, Object> extraInfo = new HashMap<>();
    private boolean hasBottomView;
    private boolean hasTopView;
    private boolean hasImportantView;
    private boolean hasMoneySplitView;
    private final String bankName;
    private final String externalAccountId;

    public ResultViewTrackModel(@NonNull final PaymentModel paymentModel,
        @NonNull final PaymentResultScreenConfiguration screenConfiguration,
        @NonNull final CheckoutPreference checkoutPreference, @NonNull final String currencyId, final boolean isMP,
        @NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository, @NonNull final UserSelectionRepository userSelectionRepository) {
        this(Style.GENERIC,
            paymentModel.getPaymentResult().getPaymentId(),
            paymentModel.getPaymentResult().getPaymentStatus(),
            paymentModel.getPaymentResult().getPaymentStatusDetail(),
            PaymentDataHelper.isSplitPaymentData(paymentModel.getPaymentResult().getPaymentDataList()),
            checkoutPreference.getTotalAmount(),
            PaymentDataHelper
                .getTotalDiscountAmount(paymentModel.getPaymentResult().getPaymentDataList()),
            paymentModel.getCongratsResponse().getScore() != null ? paymentModel.getCongratsResponse().getScore()
                .getProgress().getLevel() : null,
            paymentModel.getCongratsResponse().getDiscount() != null ? paymentModel.getCongratsResponse().getDiscount()
                .getItems().size() : 0,
            paymentModel.getCongratsResponse().getDiscount() != null ? TextUtil
                .join(new FromDiscountItemToItemId().map(paymentModel.getCongratsResponse().getDiscount().getItems()))
                : null,
            paymentModel.getPaymentResult().getPaymentData().getCampaign() != null ? paymentModel.getPaymentResult()
                .getPaymentData().getCampaign().getId() : null,
            paymentModel.getPaymentResult().getPaymentData().getPaymentMethod() != null ? paymentModel
                .getPaymentResult().getPaymentData().getPaymentMethod().getId() : null,
            paymentModel.getPaymentResult().getPaymentData().getPaymentMethod() != null ? paymentModel
                .getPaymentResult().getPaymentData().getPaymentMethod().getPaymentTypeId() : null,
            currencyId,
            paymentModel.getPaymentResult().getPaymentData(),
            getBankName(payerPaymentMethodRepository, userSelectionRepository, paymentModel.getPaymentResult().getPaymentMethodId()),
            getExternalAccountId(payerPaymentMethodRepository, userSelectionRepository, paymentModel.getPaymentResult().getPaymentMethodId()));
        hasBottomView = screenConfiguration.hasBottomFragment();
        hasTopView = screenConfiguration.hasTopFragment();
        hasImportantView = false;
        hasMoneySplitView = isMP && paymentModel.getCongratsResponse().getMoneySplit() != null;
    }

    public ResultViewTrackModel(@NonNull final PaymentCongratsModel paymentCongratsModel, final boolean isMP,
        @NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository, @NonNull final UserSelectionRepository userSelectionRepository) {
        this(Style.CUSTOM,
            paymentCongratsModel.getPaymentId(),
            TrackingHelper.getPaymentStatus(paymentCongratsModel),
            paymentCongratsModel.getPxPaymentCongratsTracking().getPaymentStatusDetail(),
            PaymentDataHelper.isSplitPaymentInfo(paymentCongratsModel.getPaymentsInfo()),
            paymentCongratsModel.getPxPaymentCongratsTracking().getTotalAmount(),
            paymentCongratsModel.getDiscountCouponsAmount(),
            paymentCongratsModel.getPaymentCongratsResponse().getLoyalty() != null ? paymentCongratsModel
                .getPaymentCongratsResponse().getLoyalty()
                .getProgress().getLevel() : null,
            paymentCongratsModel.getPaymentCongratsResponse().getDiscount() != null ? paymentCongratsModel
                .getPaymentCongratsResponse().getDiscount()
                .getItems().size() : 0,
            paymentCongratsModel.getPaymentCongratsResponse().getDiscount() != null ? TextUtil
                .join(new FromPaymentCongratsDiscountItemToItemId()
                    .map(paymentCongratsModel.getPaymentCongratsResponse().getDiscount().getItems())) : null,
            paymentCongratsModel.getPxPaymentCongratsTracking().getCampaignId(),
            paymentCongratsModel.getPxPaymentCongratsTracking().getPaymentMethodId(),
            paymentCongratsModel.getPxPaymentCongratsTracking().getPaymentMethodType().toLowerCase(),
            paymentCongratsModel.getPxPaymentCongratsTracking().getCurrencyId(),
            paymentCongratsModel.getPaymentData(),
            getBankName(payerPaymentMethodRepository, userSelectionRepository, paymentCongratsModel.getPxPaymentCongratsTracking().getPaymentMethodId()),
            getExternalAccountId(payerPaymentMethodRepository, userSelectionRepository, paymentCongratsModel.getPxPaymentCongratsTracking().getPaymentMethodId()));
        hasBottomView = paymentCongratsModel.hasBottomFragment();
        hasTopView = paymentCongratsModel.hasTopFragment();
        hasMoneySplitView = isMP && paymentCongratsModel.getPaymentCongratsResponse().getExpenseSplit() != null;
    }

    private ResultViewTrackModel(@NonNull final Style style, @Nullable final Long paymentId, final String paymentStatus,
        final String paymentStatusDetail,
        final boolean hasSplitPayment, @NonNull final BigDecimal totalAmount,
        @Nullable final BigDecimal discountCouponAmount,
        @Nullable final Integer scoreLevel, final int discountsCount, @Nullable final String campaignsIds,
        @Nullable final String campaignId,
        @Nullable final String paymentMethodId, @Nullable final String paymentMethodType,
        @NonNull final String currencyId,
        @Nullable final PaymentData paymentData,
        @Nullable final String bankName,
        @Nullable final String externalAccountId) {
        this.style = style.value;
        this.currencyId = currencyId;
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
        this.paymentStatusDetail = paymentStatusDetail;
        this.hasSplitPayment = hasSplitPayment;
        this.totalAmount = totalAmount;
        this.discountCouponAmount = discountCouponAmount;
        this.scoreLevel = scoreLevel;
        this.discountsCount = discountsCount;
        this.campaignsIds = campaignsIds;
        this.campaignId = campaignId;
        this.paymentMethodId = paymentMethodId;
        this.paymentMethodType = paymentMethodType;
        this.bankName = bankName;
        this.externalAccountId = externalAccountId;

        if (paymentData != null) {
            extraInfo.putAll(PaymentDataExtraInfo.resultPaymentDataExtraInfo(paymentData).toMap());
        }
    }

    private enum Style {
        GENERIC("generic"),
        CUSTOM("custom");

        @NonNull public final String value;

        Style(@NonNull final String value) {
            this.value = value;
        }
    }

    @Nullable
    private static String getExternalAccountId(@NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository,
                                               @NonNull final UserSelectionRepository userSelectionRepository,
                                               @Nullable final String paymentMethodId) {
        final String customOptionId = userSelectionRepository.getCustomOptionId();
        if (customOptionId != null && paymentMethodId != null && paymentMethodId.equals(PaymentMethods.ARGENTINA.DEBIN)) {
            final CustomSearchItem payerPaymentMethod = payerPaymentMethodRepository.get(customOptionId);
            return payerPaymentMethod != null ? payerPaymentMethod.getId() : null;
        } else {
            return null;
        }
    }

    @Nullable
    private static String getBankName(@NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository,
                                      @NonNull final UserSelectionRepository userSelectionRepository,
                                      @Nullable final String paymentMethodId) {
        final String customOptionId = userSelectionRepository.getCustomOptionId();
        if (customOptionId != null &&  paymentMethodId != null && paymentMethodId.equals(PaymentMethods.ARGENTINA.DEBIN)) {
            final CustomSearchItem payerPaymentMethod = payerPaymentMethodRepository.get(customOptionId);
            return payerPaymentMethod != null && payerPaymentMethod.getBankInfo() != null ? payerPaymentMethod.getBankInfo().getName() : null;
        } else {
            return null;
        }
    }
}
