package com.softdesign.devintensive.ui.aktivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.managers.PreferencesManager;
import com.softdesign.devintensive.data.storage.helpers.UserDataHelper;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.EventBus;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Класс - заставка
 *
 * При создании лпределяет, не просрочен ли токен,
 * если не просрочен и выполняется возврат данных пользователя, то вызывается UserListActivity
 * если просрочен - взывается LoginActivity
 */
public class LogoActivity extends BaseActivity {

    @BindView(R.id.logo_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;

    private static final String TAG = ConstantManager.TAG_PREFIX + "LogoActivity";

    private DataManager mDataManager;
    private RepositoryDao mRepositoryDao;
    private UserDao mUserDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        Log.d(TAG, "onCreate");
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();
        mUserDao = mDataManager.getDaoSession().getUserDao();

        mDataManager.getBus().register(this);

        checkToken();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDataManager.getBus().unregister(this);
    }

    private void checkToken(){

        if (NetworkStatusChecker.isNetworkAvailable(this)) {

            PreferencesManager pManager = mDataManager.getPreferencesManager();

            if (pManager.getUserId() == null || pManager.getUserId().isEmpty() ||
                    pManager.getAuthToken() == null || pManager.getAuthToken().isEmpty() ) {
                // Показываем форму аутентификации
                showLoginActivity();
            } else {
                showSnackBar(mCoordinatorLayout, ConstantManager.MESSAGE_AUTH);
                showProgress();
                // Вызываем проверку токена
                UserDataHelper.LoadToken();
            }
        } else {
            showSnackBar(mCoordinatorLayout, ConstantManager.ERROR_NETWORK);
        }
    }

    /**
     * Вызов формы логина
     */
    private void showLoginActivity() {
        Intent loginIntent = new Intent(LogoActivity.this, LoginActivity.class);
        startActivity(loginIntent);

        finish();
    }

    /**
     * Вызов формы списка
     */
    private void showNewActivity() {
        Intent newIntent;
        String lastActivity = mDataManager.getPreferencesManager().getLastActivity();

        if (lastActivity == "MainActivity") {
            newIntent = new Intent(LogoActivity.this, MainActivity.class);
        } else {
            newIntent = new Intent(LogoActivity.this, UserListActivity.class);
        }

        startActivity(newIntent);
        finish();
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
            showNewActivity();
            hideProgress();
        } else {
            showSnackBar(mCoordinatorLayout, event);
        }
    }

    /**
     * Подписываемся на событие проверки токена
     *
     * @param eventBus
     */
    @Subscribe
    public void onLoadTocken(EventBus.EventLoadToken eventBus) {
        String event = eventBus.getMessage();
        if (event == "") {
            showSnackBar(mCoordinatorLayout, ConstantManager.MESS_LOAD_USERS);
            UserDataHelper.SaveUserInDb();
        } else {
            if (event == ConstantManager.NEED_AUTH) {
                hideProgress();
                showLoginActivity();
            } else {
                showSnackBar(mCoordinatorLayout, event);
            }
        }
    }
}
