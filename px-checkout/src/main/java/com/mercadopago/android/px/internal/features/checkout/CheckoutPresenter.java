package com.mercadopago.android.px.internal.features.checkout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadolibre.android.cardform.internal.LifecycleListener;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.domain.CheckoutUseCase;
import com.mercadopago.android.px.internal.experiments.KnownVariant;
import com.mercadopago.android.px.internal.repository.ExperimentsRepository;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.view.experiments.ExperimentHelper;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import kotlin.Unit;

public class CheckoutPresenter extends BasePresenter<Checkout.View> implements Checkout.Actions {

    @NonNull /* default */ final PaymentRepository paymentRepository;
    @NonNull /* default */ final PaymentSettingRepository paymentSettingRepository;
    @NonNull /* default */ final UserSelectionRepository userSelectionRepository;
    @NonNull private final CheckoutUseCase checkoutUseCase;
    @NonNull private final PostPaymentUrlsMapper postPaymentUrlsMapper;
    @NonNull /* default */ ExperimentsRepository experimentsRepository;
    private final boolean withPrefetch;

    /* default */ CheckoutPresenter(@NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final CheckoutUseCase checkoutUseCase,
        @NonNull final PaymentRepository paymentRepository,
        @NonNull final ExperimentsRepository experimentsRepository,
        @NonNull final PostPaymentUrlsMapper postPaymentUrlsMapper,
        @NonNull final MPTracker tracker,
        final boolean withPrefetch) {
        super(tracker);
        this.paymentSettingRepository = paymentSettingRepository;
        this.userSelectionRepository = userSelectionRepository;
        this.checkoutUseCase = checkoutUseCase;
        this.paymentRepository = paymentRepository;
        this.experimentsRepository = experimentsRepository;
        this.postPaymentUrlsMapper = postPaymentUrlsMapper;
        this.withPrefetch = withPrefetch;
    }

    @Override
    public void initialize() {
        if (!withPrefetch) {
            getView().showProgress();
            if (isViewAttached()) {
                checkoutUseCase.execute(
                    new CheckoutUseCase.CheckoutParams(null),
                    checkoutResponse -> {
                        showOneTap();
                        return Unit.INSTANCE;
                    },
                    error -> {
                        if (isViewAttached()) {
                            getView().showError(error);
                        }
                        return Unit.INSTANCE;
                    });
            }
        } else {
            showOneTap();
        }
    }

    /* default */ void showOneTap() {
        if (isViewAttached()) {
            getView().hideProgress();
            getView().showOneTap(ExperimentHelper.INSTANCE.getVariantFrom(
                experimentsRepository.getExperiments(), KnownVariant.SCROLLED));
        }
    }

    @Override
    public void onRestore() {
        showOneTap();
    }

    @Override
    public void onErrorCancel(@Nullable final MercadoPagoError mercadoPagoError) {
        getView().cancelCheckout();
    }

    @Override
    public void recoverFromFailure() {
        initialize();
    }

    @Override
    public void onPaymentResultResponse(@Nullable final Integer customResultCode, @Nullable final String backUrl,
        @Nullable final String redirectUrl) {
        final IPaymentDescriptor payment = paymentRepository.getPayment();
        final PostPaymentUrlsMapper.Response postPaymentUrls = postPaymentUrlsMapper.map(
            new PostPaymentUrlsMapper.Model(
                redirectUrl, backUrl, payment, paymentSettingRepository.getCheckoutPreference(),
                paymentSettingRepository.getSite().getId()
            )
        );
        new PostCongratsDriver.Builder(payment, postPaymentUrls)
            .customResponseCode(customResultCode)
            .action(new PostCongratsDriver.Action() {
                @Override
                public void goToLink(@NonNull final String link) {
                    getView().goToLink(link);
                }

                @Override
                public void openInWebView(@NonNull final String link) {
                    getView().openInWebView(link);
                }

                @Override
                public void exitWith(@Nullable final Integer customResponseCode, @Nullable final Payment payment) {
                    getView().finishWithPaymentResult(customResultCode, payment);
                }
            }).build().execute();
    }

    @Override
    public void onCardAdded(@NonNull final String cardId, @NonNull final LifecycleListener.Callback callback) {
        checkoutUseCase.execute(new CheckoutUseCase.CheckoutParams(cardId),
            checkoutResponse -> {
                callback.onSuccess();
                return Unit.INSTANCE;
            },
            error -> {
                callback.onError();
                return Unit.INSTANCE;
            });
    }
}
