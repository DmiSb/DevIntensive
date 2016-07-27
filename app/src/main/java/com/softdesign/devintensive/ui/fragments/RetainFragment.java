package com.softdesign.devintensive.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.utils.ConstantManager;

import java.util.List;

/**
 * RetainFragment для сохранения данных при повороте окна
 */
public class RetainFragment extends Fragment {

    private List<User> mUserList;
    public static final String TAG = ConstantManager.TAG_PREFIX + "RetainFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public List<User> getUserList() {
        return mUserList;
    }

    public void setUsersList(List<User> userList) {
        mUserList = userList;
    }
}
