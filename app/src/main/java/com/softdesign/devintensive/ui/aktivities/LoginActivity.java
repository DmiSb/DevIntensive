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
import com.softdesign.devintensive.data.managers.UserModelManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserLoginRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

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

        mLoginButton.setOnClickListener(this);
        mLoginRemember.setOnClickListener(this);

        mDataManager = DataManager.getInstance();

        checkToken();
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

    /**
     * переход на сайт при нажатии на кнопку "Забили пароль"
     */
    private void rememberPassword() {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.DOMEN_URL + "/forgotpass"));
        startActivity(rememberIntent);
    }

    private void startNewActivity() {
        //Intent mainIntent = new Intent(this, MainActivity.class);
        //startActivity(mainIntent);
        Intent listIntent = new Intent(this, UserListActivity.class);
        startActivity(listIntent);

        finish();
    }

    /**
     * При успешной регистрации на сайте
     *
     * @param userModel - модель данных
     */
    private void loginSucces(UserModelRes userModel) {
        showSnackBar("Вход : " + userModel.getData().getToken());
        // Сохраняем полученные данные пользователя в Preferenses
        UserModelManager.saveUserModelToPreferenses(mDataManager, userModel);

        startNewActivity();
    }

    /**
     * Нажатие на кнопку Войти
     */
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
                    showSnackBar("Нет ответа сервера");
                }
            });
        } else {
            showSnackBar("Сеть не доступна, попробуйте позже");
        }
    }

    private void checkToken(){
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserLoginRes> call = mDataManager.checkToken();
            call.enqueue(new Callback<UserLoginRes>() {
                @Override
                public void onResponse(Call<UserLoginRes> call, Response<UserLoginRes> response) {
                    if (response.code() == 200) {
                        UserModelManager.saveOnlyUserDataToPreferenses(mDataManager, response.body().getData());
                        startNewActivity();
                    } else if (response.code() == 404) {
                        showSnackBar("Неправильный логин или пароль");
                    } else {
                        showSnackBar("Ошибка подключения");
                    }
                }

                @Override
                public void onFailure(Call<UserLoginRes> call, Throwable t) {
                    showSnackBar("Нет ответа сервера");
                }
            });
        } else {
            showSnackBar("Сеть не доступна, попробуйте позже");
        }
    }
}

