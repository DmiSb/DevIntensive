package com.softdesign.devintensive.ui.aktivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Окно авторизации
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = ConstantManager.TAG_PREFIX + "ActivityLogin";
    /**
     * Инициализация полей
     */
    @BindView(R.id.login_email_et) EditText mLogin;
    @BindView(R.id.login_password_et) EditText mPassword;
    @BindView(R.id.login_button) Button mLoginButton;
    @BindView(R.id.login_remember) TextView mLoginRemember;
    @BindView(R.id.login_coordinator_container) CoordinatorLayout mCoordinatorLayout;

    private DataManager mDataManager;

    /**
     * Создание Activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate");
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

        mLoginButton.setOnClickListener(this);
        mLoginRemember.setOnClickListener(this);
    }

    /**
     * Обработка нажатия
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");

        switch (v.getId()) {
            case R.id.login_button:
                if (mLogin.getText().toString().trim().isEmpty()) {
                    showToast(getString(R.string.login_email_empty));
                } else {
                    if (mPassword.getText().toString().trim().isEmpty()) {
                        showToast(getString(R.string.login_password_empty));
                    } else {
                        signIn();
                    }
                }
                break;
            case R.id.login_remember:
                rememberPassword();
                break;
        }
    }

    /**
     * Показ сообщения в нижней части
     * @param message - текст сообщения
     */
    private void showSnackBar(String message) {
        Log.d(TAG, "showSnackBar");

        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void rememberPassword() {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.DOMEN_URL + "/forgotpass"));
        startActivity(rememberIntent);
    }

    private void loginSucces(UserModelRes userModel) {
        showSnackBar("Вход : " + userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());
        saveUserRatings(userModel);
        saveUserProfiles(userModel);
        saveUsetPhoto(userModel);
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void signIn(){

        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString(), mPassword.getText().toString()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        loginSucces(response.body());
                    } else if (response.code() == 404) {
                        showSnackBar("Неправильный логин или пароль");
                    } else {
                        showSnackBar("Ошибка подключения");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    // TODO: 10.07.2016 обработать ошибки
                }
            });
        } else {
            showSnackBar("Сеть не доступна, попробуйте позже");
        }
    }

    private void saveUserRatings(UserModelRes userModel) {
        int[] userRatings = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };
        mDataManager.getPreferencesManager().saveUserRatingValues(userRatings);
    }

    private void saveUserProfiles(UserModelRes userModel) {
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
        mDataManager.getPreferencesManager().saveUserProfileData(userFields);
    }

    private void saveUsetPhoto(UserModelRes userModel) {
        Uri uri = Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto());
        mDataManager.getPreferencesManager().saveUserPhoto(uri);
    }
}

