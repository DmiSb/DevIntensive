package com.softdesign.devintensive.data.managers;

import android.net.Uri;

import com.softdesign.devintensive.data.network.res.UserModelRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Вспомогательный класс для сохранения модели данных профиля пользователя в Preferenses
 */
public class UserModelManager {

    public static void saveUserModelToPreferenses(DataManager dataManager, UserModelRes userModel) {
        // Сохраняем токен и UserId
        dataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        dataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());

        // Сохраняем статистику пользователя
        int[] userRatings = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };
        dataManager.getPreferencesManager().saveUserRatingValues(userRatings);

        // Сохраняем профиль пользователя
        List<String> userFields = new ArrayList<>();
        userFields.add(userModel.getData().getUser().getContacts().getPhone());
        userFields.add(userModel.getData().getUser().getContacts().getEmail());
        userFields.add(userModel.getData().getUser().getContacts().getVk());
        if (userModel.getData().getUser().getRepositories().getRepo().size() > 0) {
            userFields.add(userModel.getData().getUser().getRepositories().getRepo().get(0).getGit());
        } else {
            userFields.add("");
        }
        userFields.add(userModel.getData().getUser().getPublicInfo().getBio());
        dataManager.getPreferencesManager().saveUserProfileData(userFields);

        // Сохраняем Uri фотографии пользователя
        Uri uri = Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto());
        dataManager.getPreferencesManager().saveUserPhoto(uri);

        // Сохранение Uri аватарки пользователя
        uri = Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar());
        dataManager.getPreferencesManager().saveUserAvatar(uri);

        // Сохраняем время обновления
        String lastUpdated = userModel.getData().getUser().getPublicInfo().getLastUpdated();
        dataManager.getPreferencesManager().saveUserPhotoUpdated(lastUpdated);


    }
}
