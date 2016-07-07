package com.softdesign.devintensive.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class ViewBehavior extends FloatingActionButton.Behavior {

    private static final String TAG = ConstantManager.TAG_PREFIX + "ViewBehavior";
    // Объект для регулировки отступа
    private LinearLayout mRatingBar;
    // Величина отступа
    private float mPadding;
    // Вертикальный сдвиг, до этого размера может уменьшиться dependency
    private static final float mDif = 112.0f;

    public ViewBehavior() {
        super();
    }

    private int getPadding(float height, float bottom) {
        float f = mPadding * ((bottom - mDif) / (height - mDif));
        return (int) f;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (mRatingBar != null) {
            int padding = getPadding((float) dependency.getHeight(), (float) dependency.getBottom());
            Log.d(TAG, Integer.toString(dependency.getHeight()) + ", " + Integer.toString(dependency.getBottom())+ ", " + Integer.toString(padding));
            if (padding >= 0) {
                mRatingBar.setPadding(0, padding, 0, padding);
            }
        }

        return super.onDependentViewChanged(parent, child, dependency);
    }

    public ViewBehavior(Context context, AttributeSet attrs) {
        super();
    }

    public void setRatingBar(LinearLayout layout,float padding) {
        mRatingBar = layout;
        mPadding = padding;
    }
}
