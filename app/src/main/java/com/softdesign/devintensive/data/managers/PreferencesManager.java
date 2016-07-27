package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesign.devintensive.ui.aktivities.MainActivity;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevIntensiveApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс сохранения и получения данных при переворотах
 */
public class PreferencesManager {

    private SharedPreferences mSharedPreferences;

    private static final String[] USER_FIELDS = {
            ConstantManager.USER_PHONE_KEY,
            ConstantManager.USER_EMAIL_KEY,
            ConstantManager.USER_VK_KEY,
            ConstantManager.USER_GIT_KEY,
            ConstantManager.USER_SELF_KEY
    };
    private static final String[] USER_RATINGS = {
            ConstantManager.USER_RATING_KEY,
            ConstantManager.USER_CODE_LINE_KEY,
            ConstantManager.USER_PROJECT_KEY
    };

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
        for (int i = 0; i < USER_FIELDS.length; i++) {
            userFields.add(mSharedPreferences.getString(USER_FIELDS[i], ""));
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
     * Сохранение времени изменения фото
     *
     * @param lastUpdated
     */
    public void saveUserPhotoUpdated(String lastUpdated) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_UPDATE_KEY, lastUpdated);
        editor.apply();
    }

    /**
     * Получение фотографии профиля
     *
     * @return
     */
    public Uri loadUserPhoto() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY, ""));
    }

    /**
     * Сохранение токена
     *
     * @param authToken - токен
     */
    public void saveAuthToken(String authToken) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }

    /**
     * Получение токена
     *
     * @return - токен
     */
    public String getAuthToken() {
        return mSharedPreferences.getString(ConstantManager.AUTH_TOKEN_KEY, "");
    }

    /**
     * Сохранение ИД пользователя
     *
     * @param userId - ИД
     */
    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_ID_KEY, userId);
        editor.apply();
    }

    /**
     * Получение ИД пользователя
     *
     * @return - ИД
     */
    public String getUserId() {
        return mSharedPreferences.getString(ConstantManager.USER_ID_KEY, "");
    }

    /**
     * Сохранение значений рейтинга
     *
     * @param userRatings - массив значений рейтинга
     */
    public void saveUserRatingValues(int[] userRatings) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i = 0; i <USER_RATINGS.length; i++){
            editor.putString(USER_RATINGS[i], String.valueOf(userRatings[i]));
        }
        editor.apply();
    }

    /**
     * Получение значений рейтинга
     *
     * @return - строковые значения рейтинга
     */
    public List<String> loadUserRatingValues() {
        List<String> userRatings = new ArrayList<>();
        for (int i = 0; i < USER_RATINGS.length; i++) {
            userRatings.add(mSharedPreferences.getString(USER_RATINGS[i], "0"));
        }
        return userRatings;
    }

    /**
     * Сохранение аватарки профиля
     *
     * @param uri
     */
    public void saveUserAvatar(Uri uri) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_AVATAR_KEY, uri.toString());
        editor.apply();
    }

    /**
     * Получение аватарки профиля
     *
     * @return
     */
    public Uri loadUserAvatar() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_AVATAR_KEY, ""));
    }

    /**
     *
     */
    public void saveLastActivity(String actuvity) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.LAST_ACTIVITY_KEY, actuvity);
        editor.apply();
    }

    public String getLastActivity() {
        return mSharedPreferences.getString(ConstantManager.LAST_ACTIVITY_KEY, "MainActivity");
    }

    public void saveRemoteIdList(List<String> remoteIdList){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String s = "";
        if (remoteIdList.size() > 0) {
            for (int i = 0; i < remoteIdList.size(); i++) {
                if (s == "") {
                    s = remoteIdList.get(i);
                } else {
                    s = s + ";" + remoteIdList.get(i);
                }
            }
        }
        editor.putString(ConstantManager.REMOTE_IDS_KEY, s);
        editor.apply();
    }

    public List<String> getRemoteIdList() {
        List<String> remoteIdList = new ArrayList<>();
        String s = mSharedPreferences.getString(ConstantManager.REMOTE_IDS_KEY, "");
        if (s != "") {
            String[] tmp = s.split(";");
            if (tmp.length > 0) {
                for (int i = 0; i < tmp.length; i++) {
                    remoteIdList.add(tmp[i]);
                }
            }
        }
        return remoteIdList;
    }
}
