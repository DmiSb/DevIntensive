package com.softdesign.devintensive.ui.aktivities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.RoundedDrawable;
import com.softdesign.devintensive.utils.ViewBehavior;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;

    private int mCurrentEditMode = 0;

    private ImageView mCallImg, mAvatar;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private FloatingActionButton mFab;
    private AppBarLayout mAppBar;

    private EditText mUserPhone, mUserMail, mUserVK, mUserGit, mUserSelf;



    private List<EditText> mUserInfoViews;

    @Override
    public void onBackPressed() {
        if(mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private static int dp2px(Resources resource, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resource.getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();

        mCallImg = (ImageView) findViewById(R.id.call_img);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mUserPhone  = (EditText) findViewById(R.id.phone_et);
        mUserMail   = (EditText) findViewById(R.id.email_et);
        mUserVK     = (EditText) findViewById(R.id.vk_et);
        mUserGit    = (EditText) findViewById(R.id.git_et);
        mUserSelf   = (EditText) findViewById(R.id.self_et);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVK);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserSelf);

        mFab.setOnClickListener(this);
        setupToolBar();
        setupDrawer();

        if (mAvatar != null) {
            mAvatar.setImageBitmap(
                RoundedDrawable.getRoundedBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.avatar)));
        }

        loadUserInfoValue();

        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();

        /*
        Отлавливаем событие Behavior и убираем отсутпы сверху у серой плашки рейтинга
        */
        ViewBehavior vBehavior = new ViewBehavior();
        // Делаем пересчет в пиксели
        float iPad = (float) getResources().getDimensionPixelSize(R.dimen.spacing_half_large_28);
        // Передаем идентификатор плашки и начальный паддинг
        vBehavior.setRatingBar((LinearLayout) findViewById(R.id.rating_bar), iPad);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
        params.setBehavior(vBehavior);

        if (savedInstanceState == null) {
            // активити запускается впервые
            //showSnackBar("активити запускается впервые");
        } else {
            // активити уже запущено
            //showSnackBar("активити уже запущено");
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    showSnackBar(item.getTitle().toString());
                    item.setChecked(true);
                    mNavigationDrawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            });
            mAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_avatar);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        loadUserInfoValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        switch (v.getId()) {
            case R.id.call_img:
                showProgress();
                runWithDalay();
                break;
            case R.id.fab:
                changeEditMode(1 - mCurrentEditMode);
                break;
        }
    }

    /**
     * Получение и обработка результата из другой Activity (фото из камеры или галереи)
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

    }

    private void runWithDalay() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: выполнить с задержкой
                hideProgress();
            }
        }, 3000);
    }

    private void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void changeEditMode(int mode) {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }
            showSnackBar("Edit mode");
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
            saveUserInfoValue();
            showSnackBar("User info saved");
        }
        mCurrentEditMode = mode;
    }

    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++){
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (int i = 0; i < mUserInfoViews.size(); i++) {
            userData.add(mUserInfoViews.get(i).getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    /**
     * Загрузка фото из галереи
     */
    private void loadPhotoFromGallery() {

    }

    /**
     * Загрузка фото из камеры
     */
    private void loadPhotoFromCamera() {

    }
}
