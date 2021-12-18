package com.mercadopago.android.px.internal.features.one_tap.add_new_card;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.util.CardFormWrapper;
import com.mercadopago.android.px.model.CardFormInitType;
import com.mercadopago.android.px.tracking.internal.MPTracker;

public class OtherPaymentMethodPresenter extends BasePresenter<AddNewCard.View> implements AddNewCard.Actions {

    @NonNull private final CardFormWrapper cardFormWrapper;

    /* default */ OtherPaymentMethodPresenter(@NonNull final CardFormWrapper cardFormWrapper,
        @NonNull final MPTracker tracker) {
        super(tracker);
        this.cardFormWrapper = cardFormWrapper;
    }

    @Override
    public void onAddNewCardSelected(@NonNull final CardFormInitType initType) {
        getView().startCardForm(cardFormWrapper, initType);
    }
}
