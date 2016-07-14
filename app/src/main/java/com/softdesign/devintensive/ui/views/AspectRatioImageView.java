package com.softdesign.devintensive.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.softdesign.devintensive.R;

/**
 *
 */
public class AspectRatioImageView extends ImageView {

    private static final float DEFAUL_ASPECT_RATIO = 1.73f;
    private float mAspectRatio;

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView);
        mAspectRatio = a.getFloat(R.styleable.AspectRatioImageView_aspect_ratio, DEFAUL_ASPECT_RATIO);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int newWidth;
        int newHeight;
        newWidth  = getMeasuredWidth();
        newHeight = (int) (newWidth / mAspectRatio);
        setMeasuredDimension(newWidth, newHeight);
    }
}
