package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Модель данных ответа после успешной авторизации
 */
public class UserModelRes {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {

        @SerializedName("user")
        @Expose
        private UserModel.User user;
        @SerializedName("token")
        @Expose
        private String token;

        public UserModel.User getUser() {
            return user;
        }

        public String getToken() {
            return token;
        }
    }
}
