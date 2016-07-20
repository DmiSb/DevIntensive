package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель данных списка пользователей
 */
public class UserListRes {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private List<UserData> data = new ArrayList<UserData>();

    public List<UserData> getData() {
        return data;
    }

    public class UserData {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("second_name")
        @Expose
        private String secondName;
        @SerializedName("__v")
        @Expose
        private int v;
        @SerializedName("repositories")
        @Expose
        private UserModel.Repositories repositories;
        @SerializedName("profileValues")
        @Expose
        private UserModel.ProfileValues profileValues;
        @SerializedName("publicInfo")
        @Expose
        private UserModel.PublicInfo publicInfo;
        @SerializedName("specialization")
        @Expose
        private String specialization;
        @SerializedName("updated")
        @Expose
        private String updated;

        public String getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getFullName() {
            return firstName + " " + secondName;
        }

        public UserModel.ProfileValues getProfileValues() {
            return profileValues;
        }

        public UserModel.Repositories getRepositories() {
            return repositories;
        }

        public UserModel.PublicInfo getPublicInfo() {
            return publicInfo;
        }
    }
}
