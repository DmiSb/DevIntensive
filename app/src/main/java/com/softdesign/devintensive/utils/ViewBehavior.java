package com.softdesign.devintensive.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class ViewBehavior extends FloatingActionButton.Behavior {

    private LinearLayout mHeaderBar;
    private int mPadding;

    public ViewBehavior() {
        super();
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (child != null) {
            if (child.isShown() == true) {
                if (mHeaderBar != null) {
                    mHeaderBar.setPadding(0, mPadding, 0, mPadding);
                }
            } else {
                if (mHeaderBar != null) {
                    mHeaderBar.setPadding(0, 0, 0, 0);
                }
            }
        }

        return super.onDependentViewChanged(parent, child, dependency);
    }

    public ViewBehavior(Context context, AttributeSet attrs) {
        super();
    }

    public void setHeader(LinearLayout layout, int padding) {
        mHeaderBar = layout;
        mPadding = padding;
    }
}
