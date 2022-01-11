package com.mercadopago.android.px.internal.features.one_tap.slider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import com.meli.android.carddrawer.model.CardDrawerView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.MapperProvider;
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesLinkableMapper;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.LinkableTextView;
import com.mercadopago.android.px.internal.viewmodel.DisableConfiguration;
import com.mercadopago.android.px.internal.viewmodel.drawables.ConsumerCreditsDrawableFragmentItem;
import com.mercadopago.android.px.model.ConsumerCreditsDisplayInfo;

public class ConsumerCreditsFragment extends PaymentMethodFragment<ConsumerCreditsDrawableFragmentItem> {

    private ConstraintLayout creditsLagout;
    private ImageView background;
    private ImageView logo;
    private LinkableTextView topText;
    private LinkableTextView bottomText;
    protected Integer installment = -1;
    private RemediesLinkableMapper remediesLinkableMapper = MapperProvider.INSTANCE.getRemediesLinkableMapper();

    private static String INSTALLMENT_SELECTED_EXTRA = "installment_selected";

    @NonNull
    public static Fragment getInstance(final ConsumerCreditsDrawableFragmentItem model) {
        final ConsumerCreditsFragment instance = new ConsumerCreditsFragment();
        instance.storeModel(model);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.px_fragment_consumer_credits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            installment = savedInstanceState.getInt(INSTALLMENT_SELECTED_EXTRA, -1);
            setInstallment(installment);
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initializeViews(@NonNull final View view) {
        super.initializeViews(view);
        creditsLagout = view.findViewById(R.id.credits_layout);
        background = view.findViewById(R.id.background);
        logo = view.findViewById(R.id.logo);
        topText = view.findViewById(R.id.top_text);
        bottomText = view.findViewById(R.id.bottom_text);
        final ConsumerCreditsDisplayInfo displayInfo = model.metadata.displayInfo;
        tintBackground(background, displayInfo.color);
        showDisplayInfo(displayInfo);
        view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
    }

    @Override
    protected void updateCardDrawerView(@NonNull final CardDrawerView cardDrawerView) { }

    public void showDisplayInfo(@NonNull final ConsumerCreditsDisplayInfo displayInfo) {
        if (topText != null) {
            topText.updateModel(remediesLinkableMapper.map(displayInfo.topText));
        }

        if (bottomText != null) {
            bottomText.updateModel(remediesLinkableMapper.map(displayInfo.bottomText));
        }
    }

    public void updateInstallment(final int installmentSelected) {
        final View view = getView();
        if (view != null) {
            view.post(() -> {
                if (installment != installmentSelected) {
                    setInstallment(installmentSelected);
                }
            });
        }
    }

    public void setInstallment(final int installmentSelected) {
        if (topText != null) {
            topText.updateInstallment(installmentSelected);
        }

        if (bottomText != null) {
            bottomText.updateInstallment(installmentSelected);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INSTALLMENT_SELECTED_EXTRA, installment);
    }

    @Override
    public void disable() {
        super.disable();
        final DisableConfiguration disableConfiguration = new DisableConfiguration(getContext());
        ViewUtils.grayScaleViewGroup(creditsLagout);
        background.clearColorFilter();
        background.setImageResource(0);
        background.setBackgroundColor(disableConfiguration.getBackgroundColor());
        if (topText != null) {
            topText.setVisibility(View.GONE);
        }
        bottomText.setVisibility(View.GONE);
        centerLogo();
    }

    private void centerLogo() {
        final ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(creditsLagout);
        constraintSet.connect(logo.getId(), ConstraintSet.LEFT, creditsLagout.getId(), ConstraintSet.LEFT, 0);
        constraintSet.connect(logo.getId(), ConstraintSet.RIGHT, creditsLagout.getId(), ConstraintSet.RIGHT, 0);
        constraintSet.connect(logo.getId(), ConstraintSet.TOP, creditsLagout.getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(logo.getId(), ConstraintSet.BOTTOM, creditsLagout.getId(), ConstraintSet.BOTTOM, 0);
        constraintSet.applyTo(creditsLagout);
    }

    @Override
    protected String getAccessibilityContentDescription() {
        return model.getCommonsByApplication().getCurrent().getDescription();
    }
}