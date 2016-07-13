package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Модель данных ответа после успешного обновления данных
 */
public class UserEditRes {

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
        public UserModel.User user;

    }
}
