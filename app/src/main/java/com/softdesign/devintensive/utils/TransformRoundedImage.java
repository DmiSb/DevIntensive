package com.softdesign.devintensive.utils;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Класс транформации для скругдения изображения
 * для загрузки через Picasso
 */
public class TransformRoundedImage implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        return RoundedDrawable.getRoundedBitmap(source);
    }

    @Override
    public String key() {
        return "TransformRoundedImage";
    }
}
