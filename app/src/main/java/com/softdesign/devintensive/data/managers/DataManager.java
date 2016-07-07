package com.softdesign.devintensive.data.managers;

/**
 * Класс менеджер данных
 */
public class DataManager {
    private static DataManager INSTANCE = null;
    private PreferencesManager mPreferencesManager;

    public DataManager(){
        mPreferencesManager = new PreferencesManager();
    }

    public static DataManager getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }
}
