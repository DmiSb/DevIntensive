package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevIntensiveApp;

import java.util.ArrayList;
import java.util.List;

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

    public void saveUserProfileData (List<String> userFields){
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i = 0; i <USER_FIELDS.length; i++){
            editor.putString(USER_FIELDS[i], userFields.get(i));
        }
        editor.apply();
    }

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
}
