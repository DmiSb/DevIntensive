package com.softdesign.devintensive.ui.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.ui.helpers.UiHelper;

/**
 * Вспомогательный класс для изменения размера плашки рейтинга
 */
public class CustomNestedScrollBehavior extends AppBarLayout.ScrollingViewBehavior {

    private static final String TAG = ConstantManager.TAG_PREFIX + "ScrollBehavior";

    private final int mMaxAppbarHeight;
    private final int mMinAppbarHeight;
    private final int mMaxUserRatingHeight;

    public CustomNestedScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        mMinAppbarHeight = UiHelper.getStatusBarHeight()+UiHelper.getActionBarHeight();
        mMaxAppbarHeight = context.getResources().getDimensionPixelSize(R.dimen.size_profile_image_256);
        mMaxUserRatingHeight = context.getResources().getDimensionPixelSize(R.dimen.size_huge_112);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, final View dependency) {
        float friction = UiHelper.currentFriction(mMinAppbarHeight, mMaxAppbarHeight, dependency.getBottom());
        int offsetY = UiHelper.lerp(mMaxUserRatingHeight/2, mMaxUserRatingHeight, friction);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        layoutParams.topMargin = offsetY;
        child.setLayoutParams(layoutParams);

        //Log.d(TAG, "onDependentViewChanged") ;
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }
}
