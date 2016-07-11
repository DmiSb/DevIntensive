package com.softdesign.devintensive.utils;

import android.graphics.Bitmap;

import com.softdesign.devintensive.R;
import com.squareup.picasso.Transformation;

/**
 * Класс для подгонки изображения под размеры PlaceHolder
 */
public class TransformAndCropImage implements Transformation {

    /**
     * Подгонка изображения под размер 512х256
     *
     * @param source - Исходное изображение
     * @return - Подогнанное изображение
     */
    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap result = Bitmap.createScaledBitmap(source, 512, 256, true);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return "TransformAndCropImage";
    }
}
