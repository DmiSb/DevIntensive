package com.softdesign.devintensive.utils;

public interface ConstantManager {
    String TAG_PREFIX       = "DEV ";

    String EMPTY_STRING = "";

    String EDIT_MODE_KEY    = "EDIT_MODE_KEY";
    String USER_PHONE_KEY   = "USER_PHONE_KEY";
    String USER_EMAIL_KEY   = "USER_EMAIL_KEY";
    String USER_GIT_KEY     = "USER_GIT_KEY";
    String USER_VK_KEY      = "USER_VK_KEY";
    String USER_SELF_KEY    = "USER_SELF_KEY";
    String USER_PHOTO_KEY   = "USER_PHOTO_KEY";

    int USER_PHONE_ID = 0;
    int USER_EMAIL_ID = 1;
    int USER_VK_ID    = 2;
    int USER_GIT_ID   = 3;

    int LOAD_PROFILE_PHOTO  = 1;
    int REQUEST_CAMERA_PICTURE = 100;
    int REQUEST_GALLIRY_PICTURE = 101;

    int PERMISSION_REQUEST_SETTING_CODE = 200;
    int PERMISSION_REQUEST_CAMERA_CODE = 201;
    int PERMISSION_REQUEST_CALL_CODE = 202;

    char EMAIL_SYMBOL = '@';
    char POINT_SYMBOL = '.';
    String VK_SYMBOL = "vk.com";
    String GIT_SYMBOL = "github.com";
}
