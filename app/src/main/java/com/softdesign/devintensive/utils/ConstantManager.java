package com.softdesign.devintensive.utils;

public interface ConstantManager {
    String TAG_PREFIX       = "DEV ";

    String EMPTY_STRING = "";

    String EDIT_MODE_KEY            = "EDIT_MODE_KEY";
    String USER_PHONE_KEY           = "USER_PHONE_KEY";
    String USER_EMAIL_KEY           = "USER_EMAIL_KEY";
    String USER_GIT_KEY             = "USER_GIT_KEY";
    String USER_VK_KEY              = "USER_VK_KEY";
    String USER_SELF_KEY            = "USER_SELF_KEY";

    String USER_PHOTO_KEY           = "USER_PHOTO_KEY";
    String USER_AVATAR_KEY          = "USER_AVATAR_KEY";
    String USER_PHOTO_UPDATE_KEY    = "USER_PHOTO_UPDATE_KEY";


    String AUTH_TOKEN_KEY           = "AUTH_TOKEN_KEY";
    String USER_ID_KEY              = "USER_ID_KEY";

    String USER_RATING_KEY          = "USER_RATING_KEY";
    String USER_PROJECT_KEY         = "USER_PROJECT_KEY";
    String USER_CODE_LINE_KEY       = "USER_CODE_LINE_KEY";

    String PARCELABLE_KEY           = "PARCELABLE_KEY";
    String LAST_ACTIVITY_KEY        = "LAST_ACTIVITY_KEY";

    int USER_PHONE_ID               = 0;
    int USER_EMAIL_ID               = 1;
    int USER_VK_ID                  = 2;
    int USER_GIT_ID                 = 3;

    int LOAD_PROFILE_PHOTO          = 1;
    int REQUEST_CAMERA_PICTURE      = 100;
    int REQUEST_GALLIRY_PICTURE     = 101;

    int PERMISSION_REQUEST_SETTING_CODE     = 200;
    int PERMISSION_REQUEST_CAMERA_CODE      = 201;
    int PERMISSION_REQUEST_CALL_CODE        = 202;

    char EMAIL_SYMBOL               = '@';
    char POINT_SYMBOL               = '.';
    String VK_SYMBOL                = "vk.com";
    String GIT_SYMBOL               = "github.com";

    String NEED_AUTH                = "NEED_AUTH";

    String ERROR_AUTH               = "Неправильный логин или пароль ";
    String ERROR_CONNECT            = "Ошибка подключения к серверу ";
    String ERROR_ANSWER             = "Нет ответа сервера ";
    String ERROR_USER_LIST          = "Список пользвателей не может быть получен ";
    String ERROR_RESIVE             = "Ошибка получения данных с сервера ";
    String ERROR_NETWORK            = "Сеть не доступна, попробуйте позже ";
    String ERROR_LOAD               = "Ошибка загрузки ";

    String MESSAGE_AUTH             = "Аутентификация...";
    String MESS_LOAD_USERS          = "Чтение списка пользователей...";
    String MESS_SUCCESS             = "Успешная загрузка";

    int PENDING_REMOVAL_TIMEOUT     = 3000;
}
