package com.softdesign.devintensive.utils;

import okhttp3.HttpUrl;

/**
 * Вспомогательный класс для хранения настроек приложения
 */
public interface AppConfig {

    String DOMEN_URL = "http://devintensive.softdesign-apps.ru";
    String BASE_URL = "http://devintensive.softdesign-apps.ru/api/";
    int MAX_CONNECT_TIMEOUT = 3000;
    int MAX_READ_TIMEOUT = 3000;
    int START_DELAY = 1500;
    int SEARCH_DELAY = 2000;
}
