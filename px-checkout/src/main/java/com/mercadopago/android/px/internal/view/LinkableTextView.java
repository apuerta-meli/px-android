package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.features.TermsAndConditionsActivity;
import com.mercadopago.android.px.model.LinkableText;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import java.util.Map;

public class LinkableTextView extends androidx.appcompat.widget.AppCompatTextView {

    private LinkableText model;
    private int installmentSelected = -1;

    public LinkableTextView(@NonNull final Context context, @NonNull final AttributeSet attrs) {
        super(context, attrs);
    }

    public void updateModel(@Nullable final LinkableText model) {
        if (model != null) {
            this.model = model;
            render();
        }
    }

    public void updateInstallment(final int installmentSelected) {
        this.installmentSelected = installmentSelected;
    }

    private void render() {
        if (TextUtil.isNotEmpty(model.getText())) {
            final Spannable spannableText = new SpannableStringBuilder(model.getText());
            for (final LinkableText.LinkablePhrase linkablePhrase : model.getLinkablePhrases()) {
                addLinkToSpannable(spannableText, linkablePhrase);
            }
            ViewUtils.setTextColor(this, model.getTextColor());
            setText(spannableText);
            setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void addLinkToSpannable(@NonNull final Spannable spannable, @NonNull final LinkableText.LinkablePhrase link) {
        final String phrase = link.getPhrase();
        final int start = TextUtil.isNotEmpty(phrase) ? model.getText().indexOf(phrase) : -1;
        if (start >= 0) {
            final int end = start + phrase.length();
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull final View widget) {
                    onLinkClicked(link);
                }
            }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ViewUtils.setColorInSpannable(link.getTextColor(), start, end, spannable);
        }
    }

    /* default */ void onLinkClicked(@NonNull final LinkableText.LinkablePhrase linkablePhrase) {
        String data = "";
        Map<String, String> links = model.getLinks();
        if (!links.isEmpty() && installmentSelected != -1) {
            data = links.get(linkablePhrase.getLinkId(installmentSelected));
        } else if (linkablePhrase.getLink() != null || linkablePhrase.getHtml() != null) {
            data = linkablePhrase.getLink() != null ? linkablePhrase.getLink() : linkablePhrase.getHtml();
        }
        TermsAndConditionsActivity.start(getContext(), data);
    }
}