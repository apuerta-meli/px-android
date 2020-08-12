package com.mercadopago.android.px.testcheckout.pages;

import androidx.test.espresso.action.ViewActions;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class CallForAuthPage extends PageObject<CheckoutValidator> {

    public CallForAuthPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public CallForAuthPage(CheckoutValidator validator) {
        super(validator);
    }

    public SecurityCodeToResultsPage pressAlreadyAuthorizedButton() {
        onView(withId(R.id.px_button_primary)).perform(click());
        return new SecurityCodeToResultsPage(validator);
    }

    public PaymentMethodPage pressChangePaymentMethodButton() {
        onView(withId(R.id.px_button_primary)).perform(scrollTo(), click());
        return new PaymentMethodPage(validator);
    }

    @Override
    public CallForAuthPage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public NoCheckoutPage pressCancelButton() {
        onView(isRoot()).perform(ViewActions.pressBack());
        return new NoCheckoutPage(validator);
    }
}
