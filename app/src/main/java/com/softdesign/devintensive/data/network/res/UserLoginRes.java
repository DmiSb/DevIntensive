package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Дима on 14.07.2016.
 */
public class UserLoginRes {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private UserModel.User data;

    public UserModel.User getData() {
        return data;
    }
}
