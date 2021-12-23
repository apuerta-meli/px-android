package com.mercadopago.android.px.internal.features.one_tap;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import com.mercadolibre.android.cardform.internal.LifecycleListener;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.internal.Configuration;
import com.mercadopago.android.px.addons.model.internal.Experiment;
import com.mercadopago.android.px.configuration.DynamicDialogConfiguration;
import com.mercadopago.android.px.core.DynamicDialogCreator;
import com.mercadopago.android.px.core.internal.TriggerableQueue;
import com.mercadopago.android.px.internal.base.BasePresenterWithState;
import com.mercadopago.android.px.internal.core.AuthorizationProvider;
import com.mercadopago.android.px.internal.datasource.CustomOptionIdSolver;
import com.mercadopago.android.px.internal.domain.CheckoutUseCase;
import com.mercadopago.android.px.internal.domain.CheckoutWithNewCardUseCase;
import com.mercadopago.android.px.internal.experiments.KnownExperiment;
import com.mercadopago.android.px.internal.experiments.KnownVariant;
import com.mercadopago.android.px.internal.experiments.ScrolledVariant;
import com.mercadopago.android.px.internal.experiments.Variant;
import com.mercadopago.android.px.internal.experiments.VariantHandler;
import com.mercadopago.android.px.internal.features.AmountDescriptorViewModelFactory;
import com.mercadopago.android.px.internal.features.one_tap.installments.InstallmentRowHolder;
import com.mercadopago.android.px.internal.features.one_tap.offline_methods.OfflineMethods;
import com.mercadopago.android.px.internal.features.one_tap.slider.HubAdapter;
import com.mercadopago.android.px.internal.features.one_tap.slider.SplitPaymentHeaderAdapter;
import com.mercadopago.android.px.internal.features.generic_modal.ActionType;
import com.mercadopago.android.px.internal.features.generic_modal.ActionTypeWrapper;
import com.mercadopago.android.px.internal.features.generic_modal.FromModalToGenericDialogItem;
import com.mercadopago.android.px.internal.features.pay_button.PayButton;
import com.mercadopago.android.px.internal.mappers.ConfirmButtonViewModelMapper;
import com.mercadopago.android.px.internal.mappers.ElementDescriptorMapper;
import com.mercadopago.android.px.internal.mappers.InstallmentViewModelMapper;
import com.mercadopago.android.px.internal.mappers.PaymentMethodDescriptorMapper;
import com.mercadopago.android.px.internal.mappers.SplitHeaderMapper;
import com.mercadopago.android.px.internal.mappers.SummaryInfoMapper;
import com.mercadopago.android.px.internal.mappers.SummaryViewModelMapper;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository;
import com.mercadopago.android.px.internal.repository.ChargeRepository;
import com.mercadopago.android.px.internal.repository.CustomTextsRepository;
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.ExperimentsRepository;
import com.mercadopago.android.px.internal.repository.ModalRepository;
import com.mercadopago.android.px.internal.repository.OneTapItemRepository;
import com.mercadopago.android.px.internal.repository.PayerComplianceRepository;
import com.mercadopago.android.px.internal.repository.PayerCostSelectionRepository;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.tracking.TrackingRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.CardFormWrapper;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.AmountDescriptorView;
import com.mercadopago.android.px.internal.view.ElementDescriptorView;
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorModelByApplication;
import com.mercadopago.android.px.internal.view.SummaryDetailDescriptorMapper;
import com.mercadopago.android.px.internal.view.experiments.ExperimentHelper;
import com.mercadopago.android.px.internal.viewmodel.ConfirmButtonViewModel;
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction;
import com.mercadopago.android.px.internal.viewmodel.SummaryModel;
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodDrawableItemMapper;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.Application;
import com.mercadopago.android.px.model.internal.FromExpressMetadataToPaymentConfiguration;
import com.mercadopago.android.px.model.internal.Modal;
import com.mercadopago.android.px.model.internal.OneTapItem;
import com.mercadopago.android.px.model.internal.PaymentConfiguration;
import com.mercadopago.android.px.model.internal.SummaryInfo;
import com.mercadopago.android.px.model.one_tap.CheckoutBehaviour;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import com.mercadopago.android.px.tracking.internal.events.ConfirmEvent;
import com.mercadopago.android.px.tracking.internal.events.InstallmentsEventTrack;
import com.mercadopago.android.px.tracking.internal.events.SuspendedFrictionTracker;
import com.mercadopago.android.px.tracking.internal.events.SwipeOneTapEventTracker;
import com.mercadopago.android.px.tracking.internal.events.TargetBehaviourEvent;
import com.mercadopago.android.px.tracking.internal.mapper.FromApplicationToApplicationInfo;
import com.mercadopago.android.px.tracking.internal.mapper.FromSelectedExpressMetadataToAvailableMethods;
import com.mercadopago.android.px.tracking.internal.model.ConfirmData;
import com.mercadopago.android.px.tracking.internal.model.TargetBehaviourTrackData;
import com.mercadopago.android.px.tracking.internal.views.OneTapViewTracker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.Unit;

/* default */ class OneTapPresenter extends BasePresenterWithState<OneTap.View, OneTapState>
    implements OneTap.Presenter, AmountDescriptorView.OnClickListener {

    @NonNull private final AmountRepository amountRepository;
    @NonNull private final ApplicationSelectionRepository applicationSelectionRepository;
    @NonNull private final DiscountRepository discountRepository;
    @NonNull private final PaymentSettingRepository paymentSettingRepository;
    @NonNull private final AmountConfigurationRepository amountConfigurationRepository;
    @NonNull private final DisabledPaymentMethodRepository disabledPaymentMethodRepository;
    @NonNull private final ChargeRepository chargeRepository;
    @NonNull private final ESCManagerBehaviour escManagerBehaviour;
    @NonNull private final ExperimentsRepository experimentsRepository;
    @NonNull private final PayerComplianceRepository payerComplianceRepository;
    @NonNull private final TrackingRepository trackingRepository;
    @NonNull private final PaymentMethodDescriptorMapper paymentMethodDescriptorMapper;
    @NonNull private final CustomTextsRepository customTextsRepository;
    @NonNull private final SummaryInfoMapper summaryInfoMapper;
    @NonNull private final ElementDescriptorMapper elementDescriptorMapper;
    @NonNull private final SummaryDetailDescriptorMapper summaryDetailDescriptorMapper;
    @NonNull private final OneTapItemRepository oneTapItemRepository;
    @NonNull private final ModalRepository modalRepository;
    @NonNull private final CustomOptionIdSolver customOptionIdSolver;
    @NonNull private final CheckoutUseCase checkoutUseCase;
    @NonNull private final CheckoutWithNewCardUseCase checkoutWithNewCardUseCase;
    @NonNull private final PayerCostSelectionRepository payerCostSelectionRepository;
    @NonNull private final PaymentMethodDrawableItemMapper paymentMethodDrawableItemMapper;
    @NonNull private final FromApplicationToApplicationInfo fromApplicationToApplicationInfo;
    @NonNull private final AmountDescriptorViewModelFactory amountDescriptorViewModelFactory;
    @NonNull private final AuthorizationProvider authorizationProvider;
    /* default */ TriggerableQueue triggerableQueue;

    /* default */ OneTapPresenter(@NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final DisabledPaymentMethodRepository disabledPaymentMethodRepository,
        @NonNull final PayerCostSelectionRepository payerCostSelectionRepository,
        @NonNull final ApplicationSelectionRepository applicationSelectionRepository,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final AmountRepository amountRepository,
        @NonNull final CheckoutUseCase checkoutUseCase,
        @NonNull final CheckoutWithNewCardUseCase checkoutWithNewCardUseCase,
        @NonNull final AmountConfigurationRepository amountConfigurationRepository,
        @NonNull final ChargeRepository chargeRepository,
        @NonNull final ESCManagerBehaviour escManagerBehaviour,
        @NonNull final ExperimentsRepository experimentsRepository,
        @NonNull final PayerComplianceRepository payerComplianceRepository,
        @NonNull final TrackingRepository trackingRepository,
        @NonNull final CustomTextsRepository customTextsRepository,
        @NonNull final OneTapItemRepository oneTapItemRepository,
        @NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository,
        @NonNull final ModalRepository modalRepository,
        @NonNull final CustomOptionIdSolver customOptionIdSolver,
        @NonNull final PaymentMethodDrawableItemMapper paymentMethodDrawableItemMapper,
        @NonNull final PaymentMethodDescriptorMapper paymentMethodDescriptorMapper,
        @NonNull final SummaryDetailDescriptorMapper summaryDetailDescriptorMapper,
        @NonNull final SummaryInfoMapper summaryInfoMapper,
        @NonNull final ElementDescriptorMapper elementDescriptorMapper,
        @NonNull final FromApplicationToApplicationInfo fromApplicationToApplicationInfo,
        @NonNull final AuthorizationProvider authorizationProvider,
        @NonNull final AmountDescriptorViewModelFactory amountDescriptorViewModelFactory,
        @NonNull final MPTracker tracker) {
        super(tracker);
        this.paymentSettingRepository = paymentSettingRepository;
        this.disabledPaymentMethodRepository = disabledPaymentMethodRepository;
        this.payerCostSelectionRepository = payerCostSelectionRepository;
        this.applicationSelectionRepository = applicationSelectionRepository;
        this.discountRepository = discountRepository;
        this.amountRepository = amountRepository;
        this.checkoutUseCase = checkoutUseCase;
        this.checkoutWithNewCardUseCase = checkoutWithNewCardUseCase;
        this.amountConfigurationRepository = amountConfigurationRepository;
        this.chargeRepository = chargeRepository;
        this.escManagerBehaviour = escManagerBehaviour;
        this.paymentMethodDrawableItemMapper = paymentMethodDrawableItemMapper;
        this.experimentsRepository = experimentsRepository;
        this.payerComplianceRepository = payerComplianceRepository;
        this.trackingRepository = trackingRepository;
        this.customTextsRepository = customTextsRepository;
        this.summaryInfoMapper = summaryInfoMapper;
        this.elementDescriptorMapper = elementDescriptorMapper;
        this.fromApplicationToApplicationInfo = fromApplicationToApplicationInfo;
        this.paymentMethodDescriptorMapper = paymentMethodDescriptorMapper;
        this.summaryDetailDescriptorMapper = summaryDetailDescriptorMapper;
        this.oneTapItemRepository = oneTapItemRepository;
        this.modalRepository = modalRepository;
        this.customOptionIdSolver = customOptionIdSolver;
        this.amountDescriptorViewModelFactory = amountDescriptorViewModelFactory;
        this.authorizationProvider = authorizationProvider;

        triggerableQueue = new TriggerableQueue();
        setViewTrack(new OneTapViewTracker(
            fromApplicationToApplicationInfo,
            oneTapItemRepository.getValue(),
            paymentSettingRepository.getCheckoutPreference(),
            discountRepository.getCurrentConfiguration(), escManagerBehaviour.getESCCardIds(),
            payerPaymentMethodRepository.getIdsWithSplitAllowed(),
            disabledPaymentMethodRepository.getValue().size(),
            experimentsRepository.getExperiments(Configuration.TrackingMode.NO_CONDITIONAL)
        ));
    }

    @NonNull
    @Override
    public OneTapState initState() {
        return new OneTapState();
    }

    /* default */ void onFailToRetrieveInitResponse(@NonNull final ApiException apiException) {
        getView().showError(MercadoPagoError.createNotRecoverable(apiException, ApiUtil.RequestOrigin.POST_INIT));
    }

    @Override
    public void loadViewModel() {
        final SummaryInfo summaryInfo = summaryInfoMapper.map(paymentSettingRepository.getCheckoutPreference());
        final ElementDescriptorView.Model elementDescriptorModel = elementDescriptorMapper.map(summaryInfo);
        final List<OneTapItem> oneTapItemList = oneTapItemRepository.getValue();
        final List<SummaryModel> summaryModels =
            new SummaryViewModelMapper(discountRepository, amountRepository, elementDescriptorModel, this,
                chargeRepository, amountConfigurationRepository, customTextsRepository, summaryDetailDescriptorMapper,
                applicationSelectionRepository, amountDescriptorViewModelFactory)
                .map(oneTapItemList);

        final List<PaymentMethodDescriptorModelByApplication> paymentModels =
            paymentMethodDescriptorMapper.map(oneTapItemList);

        final List<SplitPaymentHeaderAdapter.Model> splitHeaderModels =
            new SplitHeaderMapper(
                paymentSettingRepository.getCurrency(),
                amountConfigurationRepository)
                .map(oneTapItemList);

        final List<ConfirmButtonViewModel.ByApplication> confirmButtonViewModels =
            new ConfirmButtonViewModelMapper(disabledPaymentMethodRepository).map(oneTapItemList);

        final HubAdapter.Model model =
            new HubAdapter.Model(paymentModels, summaryModels, splitHeaderModels, confirmButtonViewModels);
        getView().configurePayButton(this::overridePayButtonStateChange);
        getView().configurePaymentMethodHeader(getVariants());
        getView().showToolbarElementDescriptor(elementDescriptorModel);
        getView().configureRenderMode(getVariants());
        getView().configureAdapters(paymentSettingRepository.getSite(), paymentSettingRepository.getCurrency());
        getView().updateAdapters(model);
        updateElements();
        getView().updatePaymentMethods(paymentMethodDrawableItemMapper.map(oneTapItemList));
        if (OfflineMethods.shouldLaunch(oneTapItemList)) {
            getView().showOfflineMethodsCollapsed();
        }
    }

    @Override
    public void attachView(@NonNull final OneTap.View view) {
        super.attachView(view);
        initPresenter();
    }

    private void initPresenter() {
        if (isViewAttached()) {
            triggerableQueue.execute();
            loadViewModel();
        }
    }

    @Override
    public void onFreshStart() {
        triggerableQueue.enqueue(() -> {
            trackView();
            return null;
        });
    }

    @Override
    public void onGetViewTrackPath(@NonNull final PayButton.ViewTrackPathCallback callback) {
        callback.call(getViewTrack().getTrack().getPath());
    }

    private OneTapItem getCurrentOneTapItem() {
        return oneTapItemRepository.getValue().get(getState().getPaymentMethodIndex());
    }

    @Override
    public void cancel() {
        trackBack();
        getView().cancel();
    }

    @Override
    public void onBack() {
        trackAbort();
    }

    private void updateElementPosition(final int selectedPayerCost) {
        payerCostSelectionRepository.save(customOptionIdSolver.get(getCurrentOneTapItem()), selectedPayerCost);
        updateElements();
    }

    @Override
    public void onInstallmentsRowPressed() {
        updateInstallments();
        getView().animateInstallmentsList();
        final OneTapItem oneTapItem = getCurrentOneTapItem();
        final AmountConfiguration amountConfiguration =
            amountConfigurationRepository.getConfigurationSelectedFor(customOptionIdSolver.get(oneTapItem));
        track(new InstallmentsEventTrack(oneTapItem, amountConfiguration));
    }

    @Override
    public void updateInstallments() {
        final OneTapItem oneTapItem = getCurrentOneTapItem();
        final String customOptionId = customOptionIdSolver.get(oneTapItem);
        final AmountConfiguration amountConfiguration =
            amountConfigurationRepository.getConfigurationSelectedFor(customOptionId);
        final List<PayerCost> payerCostList = getCurrentPayerCosts();
        final int selectedIndex = amountConfiguration.getCurrentPayerCostIndex(
            getState().getSplitSelectionState().userWantsToSplit(),
            payerCostSelectionRepository.get(customOptionId));
        final List<InstallmentRowHolder.Model> models =
            new InstallmentViewModelMapper(paymentSettingRepository.getCurrency(), oneTapItem.getBenefits(),
                getVariants()).map(payerCostList);
        getView().updateInstallmentsList(selectedIndex, models);
    }

    private List<PayerCost> getCurrentPayerCosts() {
        final OneTapItem oneTapItem = getCurrentOneTapItem();
        final AmountConfiguration amountConfiguration =
            amountConfigurationRepository.getConfigurationSelectedFor(customOptionIdSolver.get(oneTapItem));
        if (amountConfiguration != null) {
            return amountConfiguration.getAppliedPayerCost(getState().getSplitSelectionState().userWantsToSplit());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * When user cancel the payer cost selection this method will be called with the current payment method position
     */
    @Override
    public void onInstallmentSelectionCanceled() {
        updateElements();
        getView().collapseInstallmentsSelection();
    }

    /**
     * When user selects a new payment method this method will be called with the new current paymentMethodIndex.
     *
     * @param paymentMethodIndex current payment method paymentMethodIndex.
     */
    @Override
    public void onSliderOptionSelected(final int paymentMethodIndex) {
        getState().setPaymentMethodIndex(paymentMethodIndex);
        track(new SwipeOneTapEventTracker());
        updateElementPosition(payerCostSelectionRepository.get(customOptionIdSolver.get(getCurrentOneTapItem())));
    }

    private void updateElements() {
        final String customOptionId = customOptionIdSolver.get(getCurrentOneTapItem());
        getView().updateViewForPosition(getState().getPaymentMethodIndex(),
            payerCostSelectionRepository.get(customOptionId),
            getState().getSplitSelectionState(),
            applicationSelectionRepository.get(customOptionId));
    }

    /**
     * When user selects a new payer cost for certain payment method this method will be called.
     *
     * @param payerCostSelected user selected payerCost.
     */
    @Override
    public void onPayerCostSelected(final PayerCost payerCostSelected) {
        final String customOptionId = customOptionIdSolver.get(getCurrentOneTapItem());
        final int selected = amountConfigurationRepository.getConfigurationSelectedFor(customOptionId)
            .getAppliedPayerCost(getState().getSplitSelectionState().userWantsToSplit())
            .indexOf(payerCostSelected);
        updateElementPosition(selected);
        getVariant(KnownVariant.SCROLLED).process(new VariantHandler() {
            @Override
            public void visit(@NonNull final ScrolledVariant variant) {
                if (variant.isDefault()) {
                    getView().collapseInstallmentsSelection();
                }
            }
        });
    }

    public void onDisabledDescriptorViewClick() {
        final String payerPaymentMethodId = customOptionIdSolver.get(getCurrentOneTapItem());
        final Application selectedApplication = applicationSelectionRepository.get(payerPaymentMethodId);
        final String paymentTypeId = selectedApplication.getPaymentMethod().getType();
        getView().showDisabledPaymentMethodDetailDialog(
            disabledPaymentMethodRepository.get(new PayerPaymentMethodKey(payerPaymentMethodId, paymentTypeId)),
            selectedApplication.getStatus());
    }

    @Override
    public void onDiscountAmountDescriptorClicked(@NonNull final DiscountConfigurationModel discountModel) {
        getView().showDiscountDetailDialog(paymentSettingRepository.getCurrency(), discountModel);
    }

    @Override
    public void onChargesAmountDescriptorClicked(@NonNull final DynamicDialogCreator dynamicDialogCreator) {
        final DynamicDialogCreator.CheckoutData checkoutData = new DynamicDialogCreator.CheckoutData(
            paymentSettingRepository.getCheckoutPreference(), Collections.singletonList(new PaymentData()));
        getView().showDynamicDialog(dynamicDialogCreator, checkoutData);
    }

    @Override
    public void onSplitChanged(final boolean isChecked) {
        if (getState().getSplitSelectionState().userWantsToSplit() != isChecked) {
            resetPayerCostSelection();
        }
        getState().getSplitSelectionState().setUserWantsToSplit(isChecked);
        // cancel also update the position.
        // it is used because the installment selection can be expanded by the user.
        onInstallmentSelectionCanceled();
    }

    @Override
    public void onHeaderClicked() {
        final CheckoutPreference checkoutPreference = paymentSettingRepository.getCheckoutPreference();
        final DynamicDialogConfiguration dynamicDialogConfiguration =
            paymentSettingRepository.getAdvancedConfiguration().getDynamicDialogConfiguration();

        final DynamicDialogCreator.CheckoutData checkoutData =
            new DynamicDialogCreator.CheckoutData(checkoutPreference, Collections.singletonList(new PaymentData()));

        if (dynamicDialogConfiguration.hasCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER)) {
            getView().showDynamicDialog(
                dynamicDialogConfiguration.getCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER),
                checkoutData);
        }
    }

    /* default */ void resetPayerCostSelection() {
        payerCostSelectionRepository.reset();
    }

    @Override
    public void onPostPaymentAction(@NonNull final PostPaymentAction postPaymentAction) {
        postPaymentAction.execute(new PostPaymentAction.ActionController() {
            @Override
            public void recoverPayment(@NonNull final PostPaymentAction postPaymentAction) {
                //nothing to do here
            }

            @Override
            public void onChangePaymentMethod() {
                postDisableModelUpdate();
            }
        });
    }

    /* default */ void postDisableModelUpdate() {
        oneTapItemRepository.sortByState();
        if (isViewAttached()) {
            reload();
        }
    }

    @Override
    public void onOtherPaymentMethodClicked() {
        getView().showOfflineMethodsExpanded();
    }

    @Override
    @SuppressLint("WrongConstant")
    public void handlePrePaymentAction(@NonNull final PayButton.OnReadyForPaymentCallback callback) {
        if (!handleBehaviour(CheckoutBehaviour.Type.TAP_PAY)) {
            requireCurrentConfiguration(callback);
        }
    }

    private boolean handleBehaviour(@CheckoutBehaviour.Type @NonNull final String behaviourType) {
        final OneTapItem oneTapItem = getCurrentOneTapItem();

        final CheckoutBehaviour behaviour = oneTapItem.getBehaviour(behaviourType);
        final Modal modal = behaviour != null && behaviour.getModal() != null ?
            modalRepository.getValue().get(behaviour.getModal()) : null;
        final String target = behaviour != null ? behaviour.getTarget() : null;
        final boolean isMethodSuspended = oneTapItem.getStatus().isSuspended();

        if (modal != null) {
            getView().showGenericDialog(
                new FromModalToGenericDialogItem(
                    new ActionTypeWrapper(oneTapItemRepository.getValue()).getActionType(), behaviour.getModal())
                    .map(modal));
            return true;
        } else if (TextUtil.isNotEmpty(target)) {
            track(new TargetBehaviourEvent(getViewTrack(), new TargetBehaviourTrackData(behaviourType, target)));
            getView().startDeepLink(target);
            return true;
        } else if (isMethodSuspended) {
            // is a friction if the method is suspended and does not have any behaviour to handle
            track(SuspendedFrictionTracker.INSTANCE);
            return true;
        } else {
            return false;
        }
    }

    private void requireCurrentConfiguration(@NonNull PayButton.OnReadyForPaymentCallback callback) {
        final OneTapItem oneTapItem = getCurrentOneTapItem();

        final PaymentConfiguration configuration = new FromExpressMetadataToPaymentConfiguration(
            amountConfigurationRepository,
            getState().getSplitSelectionState(),
            payerCostSelectionRepository,
            applicationSelectionRepository,
            customOptionIdSolver).map(oneTapItem);

        callback.call(configuration);
    }

    @Override
    public void handleGenericDialogAction(@NonNull final ActionType type) {
        final ActionTypeWrapper actionTypeWrapper = new ActionTypeWrapper(oneTapItemRepository.getValue());
        switch (type) {
        case PAY_WITH_OTHER_METHOD:
        case PAY_WITH_OFFLINE_METHOD:
            getView().setPagerIndex(actionTypeWrapper.getIndexToReturn());
            break;
        case ADD_NEW_CARD:
            getView().setPagerIndex(actionTypeWrapper.getIndexToReturn());
            getView().startAddNewCardFlow(
                new CardFormWrapper(
                    paymentSettingRepository, trackingRepository, authorizationProvider));
            break;
        default: // do nothing
        }
    }

    @Override
    public void onPaymentExecuted(@NonNull final PaymentConfiguration configuration) {
        final List<Experiment> experiments = new ArrayList<>();
        final ConfirmData confirmData =
            new ConfirmData(ConfirmData.ReviewType.ONE_TAP, getState().getPaymentMethodIndex(),
                new FromSelectedExpressMetadataToAvailableMethods(
                    applicationSelectionRepository, fromApplicationToApplicationInfo,
                    escManagerBehaviour.getESCCardIds(), configuration.getPayerCost(), configuration.getSplitPayment())
                    .map(getCurrentOneTapItem()));
        final Experiment experiment = experimentsRepository.getExperiment(KnownExperiment.INSTALLMENTS_HIGHLIGHT);
        if (getCurrentPayerCosts().size() > 1 && experiment != null) {
            experiments.add(experiment);
        }
        track(new ConfirmEvent(confirmData, experiments));
    }

    private boolean overridePayButtonStateChange(@NonNull final PayButton.State uiState) {
        final OneTapItem currentOneTapItem = getCurrentOneTapItem();
        final String payerPaymentMethodId = customOptionIdSolver.get(currentOneTapItem);
        final String paymentTypeId =
            applicationSelectionRepository.get(payerPaymentMethodId).getPaymentMethod().getType();
        return uiState == PayButton.State.ENABLE && (currentOneTapItem.isNewCard() ||
            currentOneTapItem.isOfflineMethods() || disabledPaymentMethodRepository.hasKey(
            new PayerPaymentMethodKey(payerPaymentMethodId, paymentTypeId)));
    }

    /* default */ void reload() {
        resetPayerCostSelection();
        resetState();
        getView().clearAdapters();
        loadViewModel();
    }

    @Override
    public void handleDeepLink() {
        disabledPaymentMethodRepository.reset();
        if (isViewAttached()) {
            getView().showLoading();
        }
        checkoutUseCase.execute(
            Unit.INSTANCE,
            checkoutResponse -> {
                if (isViewAttached()) {
                    payerComplianceRepository.turnIFPECompliant();
                    reload();
                    getView().hideLoading();
                }
                return Unit.INSTANCE;
            },
            error -> {
                if (isViewAttached()) {
                    getView().hideLoading();
                    onFailToRetrieveInitResponse(error.getApiException());
                }
                return Unit.INSTANCE;
            });
    }

    @Override
    public void onCardAdded(@NonNull final String cardId, @NonNull final LifecycleListener.Callback callback) {
        checkoutWithNewCardUseCase.execute(cardId,
            checkoutResponse -> {
                callback.onSuccess();
                return Unit.INSTANCE;
            },
            error -> {
                callback.onError();
                return Unit.INSTANCE;
            });
    }

    @Override
    public void onCardFormResult() {
        postDisableModelUpdate();
    }

    @Override
    public void onApplicationChanged(@NonNull final String paymentTypeId) {
        final OneTapItem oneTapItem = getCurrentOneTapItem();
        for (final Application application : oneTapItem.getApplications()) {
            if (application.getPaymentMethod().getType().equals(paymentTypeId)) {
                applicationSelectionRepository.set(oneTapItem, application);
                updateElements();
            }
        }
    }

    private List<Variant> getVariants() {
        return ExperimentHelper.INSTANCE.getVariantsFrom(
            experimentsRepository.getExperiments(), KnownVariant.PULSE, KnownVariant.BADGE, KnownVariant.SCROLLED);
    }

    private Variant getVariant(@NonNull final KnownVariant knownVariant) {
        return ExperimentHelper.INSTANCE.getVariantFrom(experimentsRepository.getExperiments(), knownVariant);
    }
}
