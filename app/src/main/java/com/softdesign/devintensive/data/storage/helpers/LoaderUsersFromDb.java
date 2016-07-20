package com.softdesign.devintensive.data.storage.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;

import java.util.List;

/**
 * Вспомогательный класс для загрузки из БД через Chronos
 */
public class LoaderUsersFromDb extends ChronosOperation<List<User>> {

    @Nullable
    @Override
    public List<User> run() {
        return DataManager.getInstance().getUserListFromDb();
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<List<User>>> getResultClass() {
        return Result.class;
    }

    public final static class Result extends ChronosOperationResult<List<User>> {

    }
}
