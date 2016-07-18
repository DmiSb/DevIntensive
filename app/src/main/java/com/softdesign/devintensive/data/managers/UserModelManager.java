package com.softdesign.devintensive.data.managers;

import android.net.Uri;

import com.softdesign.devintensive.data.network.res.UserModel;
import com.softdesign.devintensive.data.network.res.UserModelRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Вспомогательный класс для сохранения модели данных профиля пользователя в Preferenses
 */
public class UserModelManager {

    /**
     * Сохранение данных полного результатат запроса после регистрации
     *
     * @param dataManager - Менеджер данных
     * @param userModel - данные результата ответа
     */
    public static void saveUserModelToPreferenses(DataManager dataManager, UserModelRes userModel) {
        // Сохраняем токен и UserId
        dataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        dataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());

        saveOnlyUserDataToPreferenses(dataManager, userModel.getData().getUser());
    }

    /**
     * Сохранение только пользовательских данных
     *
     * @param dataManager - Менеджер данных
     * @param userData - данные результата ответа
     */
    public static void saveOnlyUserDataToPreferenses(DataManager dataManager, UserModel.User userData) {
        // Сохраняем статистику пользователя
        int[] userRatings = {
                userData.getProfileValues().getRating(),
                userData.getProfileValues().getLinesCode(),
                userData.getProfileValues().getProjects()
        };
        dataManager.getPreferencesManager().saveUserRatingValues(userRatings);

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
        userFields.add(userData.getPublicInfo().getBio());
        dataManager.getPreferencesManager().saveUserProfileData(userFields);

        // Сохраняем Uri фотографии пользователя
        Uri uri = Uri.parse(userData.getPublicInfo().getPhoto());
        dataManager.getPreferencesManager().saveUserPhoto(uri);

        // Сохранение Uri аватарки пользователя
        uri = Uri.parse(userData.getPublicInfo().getAvatar());
        dataManager.getPreferencesManager().saveUserAvatar(uri);

        // Сохраняем время обновления
        String lastUpdated = userData.getPublicInfo().getLastUpdated();
        dataManager.getPreferencesManager().saveUserPhotoUpdated(lastUpdated);
    }
}
