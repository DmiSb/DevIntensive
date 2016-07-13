package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Модель данных возвращаемая после загрузки фото на сервер
 */
public class UserEditPhotoRes {

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
        @SerializedName("photo")
        @Expose
        private String photo;
        @SerializedName("updated")
        @Expose
        private String updated;

        public String getLastUpdated() {
            return updated;
        }
    }
}
