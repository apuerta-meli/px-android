package com.mercadopago.android.px.tracking.internal.model;

import android.os.Parcel;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.tracking.internal.mapper.FromUserSelectionToAvailableMethod;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
@Keep
public class ConfirmData extends AvailableMethod {

    private final String reviewType;
    private int paymentMethodSelectedIndex;
    private String bankName;
    private String externalAccountId;

    public static final Creator<ConfirmData> CREATOR = new Creator<ConfirmData>() {
        @Override
        public ConfirmData createFromParcel(final Parcel in) {
            return new ConfirmData(in);
        }

        @Override
        public ConfirmData[] newArray(final int size) {
            return new ConfirmData[size];
        }
    };

    public static ConfirmData from(final String paymentTypeId, final String paymentMethodId, final boolean isCompliant, final boolean hasAdditionalInfoNeeded,
                                   @NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository,
                                   @NonNull final UserSelectionRepository userSelectionRepository) {
        final Map<String, Object> extraInfo = new HashMap<>();
        extraInfo.put("has_payer_information", isCompliant);
        extraInfo.put("additional_information_needed", hasAdditionalInfoNeeded);
        return new ConfirmData(ReviewType.ONE_TAP, new AvailableMethod(paymentMethodId, paymentTypeId, extraInfo), payerPaymentMethodRepository, userSelectionRepository);
    }

    public static ConfirmData from(@NonNull final Set<String> cardsWithEsc,
        @NonNull final UserSelectionRepository userSelectionRepository, @NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository) {
        final AvailableMethod ava = new FromUserSelectionToAvailableMethod(cardsWithEsc).map(userSelectionRepository);
        return new ConfirmData(ReviewType.TRADITIONAL, ava, payerPaymentMethodRepository, userSelectionRepository);
    }

    public ConfirmData(@NonNull final ReviewType reviewType, final int paymentMethodSelectedIndex,
                       @NonNull final AvailableMethod availableMethod, @NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository,
                       @NonNull final UserSelectionRepository userSelectionRepository) {
        super(availableMethod.paymentMethodId, availableMethod.paymentMethodType, availableMethod.extraInfo);
        this.reviewType = reviewType.value;
        this.paymentMethodSelectedIndex = paymentMethodSelectedIndex;
        this.bankName = getBankName(payerPaymentMethodRepository, userSelectionRepository);
        this.externalAccountId = getExternalAccountId(payerPaymentMethodRepository, userSelectionRepository);
    }

    public ConfirmData(@NonNull final ReviewType reviewType,
        @NonNull final AvailableMethod availableMethod, @NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository,
        @NonNull final UserSelectionRepository userSelectionRepository) {
        super(availableMethod.paymentMethodId, availableMethod.paymentMethodType, availableMethod.extraInfo);
        this.reviewType = reviewType.value;
        this.bankName = getBankName(payerPaymentMethodRepository, userSelectionRepository);
        this.externalAccountId = getExternalAccountId(payerPaymentMethodRepository, userSelectionRepository);
    }

    @SuppressWarnings("WeakerAccess")
    protected ConfirmData(final Parcel in) {
        super(in);
        reviewType = in.readString();
        paymentMethodSelectedIndex = in.readInt();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(reviewType);
        dest.writeInt(paymentMethodSelectedIndex);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public enum ReviewType {
        ONE_TAP("one_tap"),
        TRADITIONAL("traditional");

        public final String value;

        ReviewType(@NonNull final String value) {
            this.value = value;
        }
    }

    @Nullable
    private static String getExternalAccountId(@NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository,
                                               @NonNull final UserSelectionRepository userSelectionRepository) {
        final String customOptionId = userSelectionRepository.getCustomOptionId();
        if (customOptionId != null) {
            final CustomSearchItem payerPaymentMethod = payerPaymentMethodRepository.get(customOptionId);
            return payerPaymentMethod != null ? payerPaymentMethod.getId() : null;
        } else {
            return null;
        }
    }

    @Nullable
    private static String getBankName(@NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository,
                                      @NonNull final UserSelectionRepository userSelectionRepository) {
        final String customOptionId = userSelectionRepository.getCustomOptionId();
        if (customOptionId != null) {
            final CustomSearchItem payerPaymentMethod = payerPaymentMethodRepository.get(customOptionId);
            return payerPaymentMethod != null && payerPaymentMethod.getBankInfo() != null ? payerPaymentMethod.getBankInfo().getName() : null;
        } else {
            return null;
        }
    }
}
