package com.softdesign.devintensive.data.managers;

import android.content.Context;
import android.net.Uri;

import com.softdesign.devintensive.data.network.PicassoCache;
import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserEditPhotoRes;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserLoginRes;
import com.softdesign.devintensive.data.network.res.UserModel;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.DevIntensiveApp;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Класс менеджер данных
 */
public class DataManager {
    private static DataManager INSTANCE = null;
    private Picasso mPicasso;

    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;
    private DaoSession mDaoSession;
    private Bus mBus;

    /**
     * Конструктор класса
     */
    public DataManager(){
        this.mPreferencesManager = new PreferencesManager();
        this.mContext = DevIntensiveApp.getContext();
        this.mRestService = ServiceGenerator.createService(RestService.class);
        this.mPicasso = new PicassoCache(mContext).getPicassoInstance();
        this.mDaoSession = DevIntensiveApp.getDaoSession();
        this.mBus = DevIntensiveApp.getBus();
    }

    /**
     * Получение INSTANCE менеджера
     *
     * @return - INSTANCE менеджера
     */
    public static DataManager getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    /**
     * Получение PreferencesManager
     *
     * @return - PreferencesManager
     */
    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    /**
     * Получение контекста
     *
     * @return - контекста
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Получение контекста библиотеки Picasso
     *
     * @return - Picasso
     */
    public Picasso getPicasso() {
        return mPicasso;
    }

    public Bus getBus() {
        return mBus;
    }

    /**
     * Сохранние данных пользователя в Preferences
     *
     * @param userData - данные
     */
    public void saveOnlyUserData(UserModel.User userData) {
        // Сохраняем статистику пользователя
        int[] userRatings = {
                userData.getProfileValues().getRating(),
                userData.getProfileValues().getCodeLines(),
                userData.getProfileValues().getProjects()
        };
        this.mPreferencesManager.saveUserRatingValues(userRatings);

        // Сохраняем профиль пользователя
        List<String> userFields = new ArrayList<>();
        userFields.add(userData.getContacts().getPhone());
        userFields.add(userData.getContacts().getEmail());
        userFields.add(userData.getContacts().getVk());
        if (userData.getRepositories().getRepo().size() > 0) {
            userFields.add(userData.getRepositories().getRepo().get(0).getGit());
        } else {
            userFields.add("");
        }
        userFields.add(userData.getPublicInfo().getSelf());
        this.mPreferencesManager.saveUserProfileData(userFields);

        // Сохраняем Uri фотографии пользователя
        Uri uri = Uri.parse(userData.getPublicInfo().getPhoto());
        this.mPreferencesManager.saveUserPhoto(uri);

        // Сохранение Uri аватарки пользователя
        uri = Uri.parse(userData.getPublicInfo().getAvatar());
        this.mPreferencesManager.saveUserAvatar(uri);

        // Сохраняем время обновления
        String lastUpdated = userData.getPublicInfo().getLastUpdated();
        this.mPreferencesManager.saveUserPhotoUpdated(lastUpdated);
    }

    /**
     * Сохранение данных пользователя ответа после авторизации
     *
     * @param userModel - данные пользователя после авторизации
     */
    public void saveFullUserData(UserModelRes userModel) {
        // Сохраняем токен и UserId
        this.mPreferencesManager.saveAuthToken(userModel.getData().getToken());
        this.mPreferencesManager.saveUserId(userModel.getData().getUser().getId());

        saveOnlyUserData(userModel.getData().getUser());
    }

    //region ============================ Network ==================================================

    /**
     * Авторизация пользователя
     *
     * @param userLoginReq - логин и пароль
     * @return - данные пользователя после авторизации
     */
    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq){
        return mRestService.loginUser(userLoginReq);
    }

    /**
     * Сохранение фото пользователя на сервере
     *
     * @param fileUr - путь к файлу фото
     * @return - ответ сервера
     */
    public Call<UserEditPhotoRes> editUserPhoto(Uri fileUr) {

        File file = new File(fileUr.getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part bodyPart =
            MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
        return mRestService.editUserPhoto(mPreferencesManager.getUserId(), bodyPart);
    }

    /**
     * Проверка просроченности токена
     *
     * @return - ответ сервера с данными пользователя
     */
    public Call<UserLoginRes> checkToken() {
        return mRestService.checkToken(mPreferencesManager.getUserId());
    }

    /**
     *  Получение списка пользователей с сервера
     *
     * @return - список пользователей
     */
    public Call<UserListRes> getUserListFromNetwork() {
        return mRestService.getUserList();
    }

    //endregion

    //region ============================= Database ================================================

    /**
     * Сессия подключения к БД
     *
     * @return - контекст сессии
     */
    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * Сортировка списка пользователей по сохраненному порядку
     *
     * @param userList
     * @return
     */
    private List<User> sortUserList(List<User> userList) {

        Map<Integer, User> mapUsers = new TreeMap();
        List<String> allRemoteId = DataManager.getInstance().getPreferencesManager().getRemoteIdList();

        if (allRemoteId.size() > 0) {
            if (userList.size() > 0) {
                int idx;
                for (int i = 0; i < userList.size(); i++) {
                    idx = allRemoteId.indexOf(userList.get(i).getRemoteId());
                    mapUsers.put(idx, userList.get(i));
                }

                if (mapUsers.size() > 0) {
                    userList.clear();
                    for (Map.Entry<Integer, User> item : mapUsers.entrySet()) {
                        userList.add(item.getValue());
                    }
                }
            }
        } else {
            for (int i = 0; i < userList.size(); i++) {
                allRemoteId.add(userList.get(i).getRemoteId());
            }
            DataManager.getInstance().getPreferencesManager().saveRemoteIdList(allRemoteId);
        }

        return userList;
    }

    /**
     * Получение списка ползователй из БД
     *
     * @return - список
     */
    public List<User> getUserListFromDb() {
        List<User> userList = new ArrayList<>();

        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.CodeLines.gt(0))
                    .orderDesc(UserDao.Properties.Rating)
                    .build()
                    .list();

            userList = sortUserList(userList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }

    /**
     * Получение списка пользователей с БД после посика по имени
     *
     * @param query - текст поиска
     * @return - список
     */
    public List<User> getUserListByName(String query) {
        List<User> userList = new ArrayList<>();

        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.CodeLines.gt(0),
                            UserDao.Properties.SearchName.like("%" + query.toUpperCase() + "%"))
                    .orderDesc(UserDao.Properties.Rating)
                    .build()
                    .list();

            userList = sortUserList(userList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }

    //endregion
}
