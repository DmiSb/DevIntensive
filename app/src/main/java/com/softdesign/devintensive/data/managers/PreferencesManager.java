package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevIntensiveApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс сохранения и получения данных
 */
public class PreferencesManager {
    private SharedPreferences mSharedPreferences;
    public static final String[] USER_FIELDS = {
            ConstantManager.USER_PHONE_KEY,
            ConstantManager.USER_EMAIL_KEY,
            ConstantManager.USER_VK_KEY,
            ConstantManager.USER_GIT_KEY,
            ConstantManager.USER_SELF_KEY};

    public PreferencesManager() {
        mSharedPreferences = DevIntensiveApp.getSharedPreferences();
    }

    /**
     * Сохранение данных
     *
     * @param userFields
     */
    public void saveUserProfileData (List<String> userFields){
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i = 0; i <USER_FIELDS.length; i++){
            editor.putString(USER_FIELDS[i], userFields.get(i));
        }
        editor.apply();
    }

    /**
     * Загрузка данных
     *
     * @return
     */
    public List<String> loadUserProfileData (){
        List<String> userFields = new ArrayList<>();
        List<String> defaultUserData = new ArrayList<>();
        defaultUserData.add(DevIntensiveApp.getContext().getResources().getString(R.string.phone_value));
        defaultUserData.add(DevIntensiveApp.getContext().getResources().getString(R.string.email_value));
        defaultUserData.add(DevIntensiveApp.getContext().getResources().getString(R.string.vk_value));
        defaultUserData.add(DevIntensiveApp.getContext().getResources().getString(R.string.git_value));
        defaultUserData.add(DevIntensiveApp.getContext().getResources().getString(R.string.self_value));
        for (int i = 0; i < USER_FIELDS.length; i++) {
            userFields.add(mSharedPreferences.getString(USER_FIELDS[i], defaultUserData.get(i)));
        }
        return userFields;
    }

    /**
     * Сохранение фотографии профиля
     *
     * @param uri
     */
    public void saveUserPhoto(Uri uri) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, uri.toString());
        editor.apply();
    }

    /**
     * Получение фотографии профиля
     *
     * @return
     */
    public Uri loadUserPhoto() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY,
                "android.resource://com.softdesign.devintensive/drawable/user_photo"));
    }
}
