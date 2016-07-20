package com.softdesign.devintensive.data.storage.helpers;

import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserLoginRes;
import com.softdesign.devintensive.data.network.res.UserModel;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Вспомогательный класс по обработке данных
 */
public class UserDataHelper {

    /**
     * Аутентификация
     */
    public static void Authentify(String login, String password) {

        final DataManager mDataManager = DataManager.getInstance();

        Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(login, password));
        call.enqueue(new Callback<UserModelRes>() {
            @Override
            public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                if (response.code() == 200) {
                    // Сохраняем полученные данные пользователя в Preferenses
                    mDataManager.saveFullUserData(response.body());
                    mDataManager.getBus().post(new EventBus.EventAuth(ConstantManager.EMPTY_STRING));
                } else if (response.code() == 404) {
                    mDataManager.getBus().post(new EventBus.EventAuth(ConstantManager.ERROR_AUTH));
                } else {
                    mDataManager.getBus().post(new EventBus.EventAuth(ConstantManager.ERROR_CONNECT));
                }
            }

            @Override
            public void onFailure(Call<UserModelRes> call, Throwable t) {
                mDataManager.getBus().post(new EventBus.EventAuth(ConstantManager.ERROR_ANSWER));
            }
        });
    }

    /**
     * Проверка токена на сервере
     */
    public static void LoadToken() {

        final DataManager mDataManager = DataManager.getInstance();

        Call<UserLoginRes> call = mDataManager.checkToken();
        call.enqueue(new Callback<UserLoginRes>() {
            @Override
            public void onResponse(Call<UserLoginRes> call, Response<UserLoginRes> response) {
                if (response.code() == 200) {
                    // Запоминаем данные пользователя
                    mDataManager.saveOnlyUserData(response.body().getData());
                    // Читаем с червера и сохраняем список пользователей в БД
                    mDataManager.getBus().post(new EventBus.EventLoadToken(ConstantManager.EMPTY_STRING));
                } else if (response.code() == 404) {
                    mDataManager.getBus().post(new EventBus.EventLoadToken(ConstantManager.NEED_AUTH));
                } else {
                    mDataManager.getBus().post(new EventBus.EventLoadToken(ConstantManager.ERROR_CONNECT));
                }
            }

            @Override
            public void onFailure(Call<UserLoginRes> call, Throwable t) {
                mDataManager.getBus().post(new EventBus.EventLoadToken(ConstantManager.ERROR_ANSWER));
            }
        });
    }

    /**
     * Чтение данных с сервера и запись в БД
     */
    public static void SaveUserInDb() {

        final DataManager mDataManager = DataManager.getInstance();
        final RepositoryDao mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();
        final UserDao mUserDao = mDataManager.getDaoSession().getUserDao();

        Call<UserListRes> call = mDataManager.getUserListFromNetwork();
        call.enqueue(new Callback<UserListRes>() {
            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                try {
                    if (response.code() == 200) {
                        List<Repository> allRepositories = new ArrayList<>();
                        List<User> allUsers = new ArrayList<>();

                        for (UserListRes.UserData userRes : response.body().getData()) {
                            allRepositories.addAll(getRepoListFromUserRes(userRes));
                            allUsers.add(new User(userRes));
                        }

                        mRepositoryDao.insertOrReplaceInTx(allRepositories);
                        mUserDao.insertOrReplaceInTx(allUsers);


                        mDataManager.getBus().post(new EventBus.EventSaveInDbBus(ConstantManager.EMPTY_STRING));
                    } else {
                        mDataManager.getBus().post(new EventBus.EventSaveInDbBus(ConstantManager.ERROR_USER_LIST));
                    }
                } catch (NullPointerException e) {
                    mDataManager.getBus().post(new EventBus.EventSaveInDbBus(ConstantManager.ERROR_RESIVE + e.toString()));
                }
            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                mDataManager.getBus().post(new EventBus.EventSaveInDbBus(ConstantManager.ERROR_RESIVE));
            }
        });
    }

    /**
     * Вспомогательный метод заполнения списка репозиториев
     *
     * @param userData - данные пользователей
     * @return - список
     */
    private static List<Repository> getRepoListFromUserRes(UserListRes.UserData userData) {
        final String userId = userData.getId();

        List<Repository> repositories = new ArrayList<>();
        for (UserModel.Repo repositoryRes : userData.getRepositories().getRepo()) {
            repositories.add(new Repository(repositoryRes, userId));
        }
        return repositories;
    }

    public void moveUsers(int fromPosition, int toPosition) {

    }
}