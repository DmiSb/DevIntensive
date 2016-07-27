package com.softdesign.devintensive.ui.behaviors;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.ui.helpers.UiHelper;

/**
 * Вспомогательный класс для изменения размера плашки рейтинга
 */
public class ViewBehavior<V extends LinearLayout> extends AppBarLayout.ScrollingViewBehavior {

    private static final String TAG = ConstantManager.TAG_PREFIX + "ViewBehavior";

    private final int mMaxAppbarHeight;
    private final int mMinAppbarHeight;
    private final int mMaxUserInfoHeight;
    private final int mMinUserInfoHeight;

    /**
     * Констрктор
     *
     * @param context
     * @param attrs
     */
    public ViewBehavior(Context context, AttributeSet attrs) {
        super();
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ViewBehavior);

        mMinUserInfoHeight =a.getDimensionPixelSize(R.styleable.ViewBehavior_behavior_min_height, 56);
        a.recycle();
        mMaxUserInfoHeight = context.getResources().getDimensionPixelOffset(R.dimen.size_huge_112);

        mMinAppbarHeight = UiHelper.getStatusBarHeight()+UiHelper.getActionBarHeight();
        mMaxAppbarHeight = context.getResources().getDimensionPixelOffset(R.dimen.size_profile_image_256);
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
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, final View dependency) {

        float currentFriction = UiHelper.currentFriction(mMinAppbarHeight, mMaxAppbarHeight, dependency.getBottom());
        int currentHeight = UiHelper.lerp(mMinUserInfoHeight, mMaxUserInfoHeight, currentFriction);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.height = currentHeight;
        child.setLayoutParams(lp);

        Log.d(TAG, "onDependentViewChanged, " + String.valueOf(currentHeight) + ", " + String.valueOf(child.getHeight()));
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency);
    }
}
