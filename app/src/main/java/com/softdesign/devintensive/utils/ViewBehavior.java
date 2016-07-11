package com.softdesign.devintensive.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Класс для изменения высоты "серой плашки"
 */
public class ViewBehavior extends FloatingActionButton.Behavior {

    private static final String TAG = ConstantManager.TAG_PREFIX + "ViewBehavior";
    // Объект для регулировки отступа
    private LinearLayout mRatingBar;
    // Величина отступа
    private float mPadding;
    // Вертикальный сдвиг, до этого размера может уменьшиться dependency
    private static final float mDif = 112.0f;

    /**
     * Конструктор пустой
     */
    public ViewBehavior() {
        super();
    }

    /**
     * Констрктор с контекстом
     *
     * @param context
     * @param attrs
     */
    public ViewBehavior(Context context, AttributeSet attrs) {
        super();
    }

    /**
     * Конструктор с нужными параметрами
     *
     * @param layout
     * @param padding
     */
    public ViewBehavior(LinearLayout layout,float padding) {
        super();
        mRatingBar = layout;
        mPadding = padding;
    }

    /**
     * Расчет отступа
     *
     * @param height
     * @param bottom
     * @return
     */
    private int getPadding(float height, float bottom) {
        float f = mPadding * ((bottom - mDif) / (height - mDif));
        return (int) f;
    }

    /**
     * Изменение положения элемента
     *
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
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
}
