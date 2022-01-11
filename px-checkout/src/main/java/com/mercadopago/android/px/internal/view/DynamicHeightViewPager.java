package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class DynamicHeightViewPager extends ViewPager {

    public DynamicHeightViewPager(@NonNull final Context context) {
        this(context, null);
    }

    public DynamicHeightViewPager(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final View firstChild = getChildAt(0);
        int measuredHeight = 0;
        if (firstChild != null) {
            firstChild.measure(widthMeasureSpec, heightMeasureSpec);
            measuredHeight = firstChild.getMeasuredHeight();
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY));
    }
}
