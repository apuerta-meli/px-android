package com.mercadopago.android.px.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Amount configuration represents one hash_amount representation for cards. this DTO is strongly linked with a {@link
 * DiscountConfigurationModel}.
 */
@Keep
public final class AmountConfiguration implements Serializable {

    private static final int NO_SELECTED_PAYER_COST = -1;

    /**
     * default selected payer cost configuration for single payment method selection
     */
    private int selectedPayerCostIndex;

    /**
     * Payer cost configuration for single payment method selection
     */
    private List<PayerCost> payerCosts;

    /**
     * Split payment node it it applies.
     */
    @Nullable private Split split;

    /**
     * The discount token associated with this configuration.
     */
    @Nullable private String discountToken;

    /**
     * The amount without taxes or discounts included.
     */
    @Nullable private BigDecimal taxFreeAmount;

    /**
     * The amount without discounts included.
     */
    @Nullable private BigDecimal noDiscountAmount;

    @NonNull
    public List<PayerCost> getPayerCosts() {
        return payerCosts != null ? payerCosts : Collections.emptyList();
    }

    public boolean allowSplit() {
        return split != null;
    }

    @NonNull
    public List<PayerCost> getAppliedPayerCost(final boolean userWantToSplit) {
        if (isSplitPossible(userWantToSplit)) {
            return getSplitConfiguration().primaryPaymentMethod.getPayerCosts();
        } else {
            return getPayerCosts();
        }
    }

    @NonNull
    public PayerCost getCurrentPayerCost(final boolean userWantToSplit, final int userSelectedIndex) {
        return getAppliedPayerCost(userWantToSplit).get(getCurrentPayerCostIndex(userWantToSplit, userSelectedIndex));
    }

    public int getCurrentPayerCostIndex(final boolean userWantToSplit, final int userSelectedIndex) {
        return userSelectedIndex == PayerCost.NO_SELECTED ? (isSplitPossible(userWantToSplit)
            ? getSplitConfiguration().primaryPaymentMethod.selectedPayerCostIndex :
            selectedPayerCostIndex) : userSelectedIndex;
    }

    @Nullable
    public Split getSplitConfiguration() {
        return split;
    }

    @Nullable
    public String getDiscountToken() {
        return discountToken;
    }

    @Nullable
    public PayerCost getPayerCost(final int userSelectedPayerCost) {
        if (userSelectedPayerCost == NO_SELECTED_PAYER_COST) {
            return payerCosts.get(selectedPayerCostIndex);
        } else {
            return payerCosts.get(userSelectedPayerCost);
        }
    }

    @Nullable
    public BigDecimal getTaxFreeAmount() {
        return taxFreeAmount;
    }

    @Nullable
    public BigDecimal getNoDiscountAmount() {
        return noDiscountAmount;
    }

    private boolean isSplitPossible(final boolean userWantToSplit) {
        return userWantToSplit && allowSplit();
    }
}
