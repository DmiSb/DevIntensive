package com.softdesign.devintensive.ui.aktivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.helpers.UserDataHelper;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.EventBus;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Окно авторизации
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = ConstantManager.TAG_PREFIX + "ActivityLogin";
    // Инициализация полей
    @BindView(R.id.login_email_et) EditText mLogin;
    @BindView(R.id.login_password_et) EditText mPassword;
    @BindView(R.id.login_button) Button mLoginButton;
    @BindView(R.id.login_remember) TextView mLoginRemember;
    @BindView(R.id.login_coordinator_container) CoordinatorLayout mCoordinatorLayout;

    private DataManager mDataManager;
    private RepositoryDao mRepositoryDao;
    private UserDao mUserDao;

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
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();
        mUserDao = mDataManager.getDaoSession().getUserDao();

        mDataManager.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDataManager.getBus().unregister(this);
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
     * переход на сайт при нажатии на кнопку "Забыли пароль"
     */
    private void rememberPassword() {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.DOMEN_URL + "/forgotpass"));
        startActivity(rememberIntent);
    }

    private void startNewActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent listIntent = new Intent(LoginActivity.this, UserListActivity.class);
                startActivity(listIntent);

                finish();
            }
        }, AppConfig.START_DELAY);
    }

    /**
     * При успешной регистрации на сайте
     */
    private void loginSucces() {
        // Сохраняем данные в БД
        showSnackBar(mCoordinatorLayout, ConstantManager.MESS_LOAD_USERS);
        UserDataHelper.SaveUserInDb();
    }

    /**
     * Нажатие на кнопку Войти
     */
    private void signIn(){

        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            showSnackBar(mCoordinatorLayout, ConstantManager.MESSAGE_AUTH);
            UserDataHelper.Authentify(mLogin.getText().toString(), mPassword.getText().toString());
        } else {
            showSnackBar(mCoordinatorLayout, ConstantManager.ERROR_NETWORK);
        }
    }

    /**
     * Подписываемся на событие аутентификации на сервере
     *
     * @param eventBus
     */
    @Subscribe
    public void onAuthentify(EventBus.EventAuth eventBus) {
        String event = eventBus.getMessage();
        if (event == "") {
            loginSucces();
        } else {
            showSnackBar(mCoordinatorLayout, event);
        }
    }

    /**
     * Подписываемся на событие сохранения данных в БД
     *
     * @param eventBus - рузультат
     */
    @Subscribe
    public void onSaveInDb(EventBus.EventSaveInDbBus eventBus) {
        String event = eventBus.getMessage();
        if (event == "") {
            startNewActivity();
        } else {
            showSnackBar(mCoordinatorLayout, event);
        }
    }
}

