package com.mercadopago.android.px.model;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

public final class SavedESCCardToken extends SavedCardToken {

    // used in network call
    @SuppressWarnings("unused")
    @SerializedName("require_esc")
    private final boolean requireEsc;

    // used in network call
    @SuppressWarnings("unused")
    private final String esc;

    private SavedESCCardToken(@NonNull final String cardId, @NonNull final String securityCode,
        @NonNull final String esc) {
        super(cardId, securityCode);
        this.requireEsc = true;
        this.esc = esc;
    }

    private SavedESCCardToken(@NonNull final String cardId, @NonNull final String securityCode,
        @NonNull final String esc, @NonNull final Device device) {
        this(cardId, securityCode, esc);
        setDevice(device);
    }

    public String getEsc() {
        return esc;
    }

    public static SavedESCCardToken createWithSecurityCode(@NonNull final String cardId,
        @NonNull final String securityCode) {
        return new SavedESCCardToken(cardId, securityCode, "");
    }

    @Deprecated
    public static SavedESCCardToken createWithEsc(@NonNull final String cardId, @NonNull final String esc) {
        return new SavedESCCardToken(cardId, "", esc);
    }

    public static SavedESCCardToken createWithEsc(@NonNull final String cardId, @NonNull final String esc,
        @NonNull final Device device) {
        return new SavedESCCardToken(cardId, "", esc, device);
    }
}
