package com.softdesign.devintensive.ui.aktivities;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserEditPhotoRes;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.EditTextValidator;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.softdesign.devintensive.utils.TransformAndCropImage;
import com.softdesign.devintensive.utils.TransformRoundedImage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Главное окно с редактированием профиля пользователя
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;

    private int mCurrentEditMode = 0;
    private File mExternalStoragePictureDirectory = null;

    /**
     * Инициализация полей при помощи ButterKnife
     */
    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout) AppBarLayout mAppBarLayout;
    @BindView(R.id.rating_bar) LinearLayout mRatingBar;
    @BindView(R.id.navigation_drawer) DrawerLayout mNavigationDrawer;

    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder) RelativeLayout mProfilePlaceholder;
    @BindView(R.id.user_photo_img) ImageView mUserImage;

    @BindViews({R.id.call_img, R.id.mail_send_img, R.id.vk_open_img, R.id.git_open_img}) List<ImageView> mUserInfoImgs;
    @BindViews({R.id.phone_et, R.id.email_et, R.id.vk_et, R.id.git_et, R.id.self_et}) List<EditText> mUserInfoViews;
    @BindViews({R.id.phone_layout, R.id.email_layout, R.id.vk_layout, R.id.git_layout}) List<TextInputLayout> mUserInfoLayouts;

    @BindViews({R.id.rating_value, R.id.rating_code_line, R.id.rating_project}) List<TextView> mUserRatings;

    private AppBarLayout.LayoutParams mAppBarParams = null;

    private List<EditTextValidator> mUserInfoValidator;

    private ImageView mAvatar;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    protected Drawable mDummy;

    /**
     * Создание Activity
     *
     * @param savedInstanceState - Bind для сохранения состояния
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();
        mDataManager.getPreferencesManager().saveLastActivity("MainActivity");

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        mUserInfoImgs.get(ConstantManager.USER_PHONE_ID).setOnClickListener(this);
        mUserInfoImgs.get(ConstantManager.USER_EMAIL_ID).setOnClickListener(this);
        mUserInfoImgs.get(ConstantManager.USER_VK_ID).setOnClickListener(this);
        mUserInfoImgs.get(ConstantManager.USER_GIT_ID).setOnClickListener(this);

        mDummy = mUserImage.getContext().getResources().getDrawable(R.drawable.user_bg);

        setupToolBar();
        setupDrawer();

        // Инициализируем рейтинг
        initUserRatings();
        // Инициализируем полей профиля пользователя
        initUserFields();

        // Иницализация фото пользователя
        initUserPhotoWhithProgress();

        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        mUserInfoValidator = new ArrayList<>(4);

        if (savedInstanceState != null) {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }

        if (mAvatar != null)
            DataManager.getInstance().getPicasso()
                    .load(mDataManager.getPreferencesManager().loadUserAvatar())
                    .transform(new TransformRoundedImage())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.avatar_empty)
                    .into(mAvatar, new Callback() {

                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Load avatar from cache");
                        }

                        @Override
                        public void onError() {
                            DataManager.getInstance().getPicasso()
                                    .load(mDataManager.getPreferencesManager().loadUserAvatar())
                                    .transform(new TransformRoundedImage())
                                    .placeholder(R.drawable.avatar_empty)
                                    .into(mAvatar, new Callback() {

                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "Load avatar from network");
                                        }

                                        @Override
                                        public void onError() {
                                            Log.d(TAG, "Error load avatar from network");
                                        }
                                    });
                        }
                    });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart : " + new Date().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume : " + new Date().toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        initUserFields();
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
        Log.d(TAG, "onSaveInstanceState");

        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * назатие на кнопку Back
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");

        if(mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Глобальный обработчик нажатия
     *
     * @param v - источник события
     */
    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");

        switch (v.getId()) {
            case R.id.fab:
                changeEditMode(1 - mCurrentEditMode);
                break;
            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            case R.id.call_img:
                callUserPhone();
                break;
            case R.id.mail_send_img:
                Intent mailIntent = new Intent(Intent.ACTION_SEND,
                        Uri.fromParts("mailto:", mUserInfoViews.get(ConstantManager.USER_EMAIL_ID).getText().toString(), null));
                mailIntent.setType("text/plain");
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_title);
                mailIntent.putExtra(Intent.EXTRA_TEXT, R.string.app_title);
                try {
                    startActivity(Intent.createChooser(mailIntent, getString(R.string.mail_send_action)));
                } catch (android.content.ActivityNotFoundException ex) {
                    showToast(getString(R.string.mail_error_action) + ex.toString());
                }
                break;
            case R.id.vk_open_img:
                String urlVK = "https://" + mUserInfoViews.get(ConstantManager.USER_VK_ID).getText().toString();
                Intent vkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlVK));
                startActivity(vkIntent);
                break;
            case R.id.git_open_img:
                String urlGit = "https://" + mUserInfoViews.get(ConstantManager.USER_GIT_ID).getText().toString();
                Intent gitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlGit));
                startActivity(gitIntent);
                break;
        }
    }

    /**
     * Получение и обработка результата из другой Activity (фото из камеры или галереи)
     *
     * @param requestCode - код запроса
     * @param resultCode - код ответа
     * @param data - данные
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");

        switch (requestCode) {
            case ConstantManager.REQUEST_GALLIRY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();
                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }

    /**
     * Результат запроса разрешений
     *
     * @param requestCode - код запроса
     * @param permissions - код разрешения
     * @param grantResults - резутьтат
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        if (requestCode == ConstantManager.PERMISSION_REQUEST_CAMERA_CODE) {
            if (grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                loadPhotoFromCamera();
            }
        }

        if (requestCode == ConstantManager.PERMISSION_REQUEST_CALL_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPhotoFromCamera();
            }
        }
    }

    /**
     * Загрузк фото профиля пользователя через Picasso
     */
    private void setUserPhotoIntoView() {
        mDataManager.getPicasso()
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(mDummy)
                .error(mDummy)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(mUserImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        hideProgress();
                    }

                    @Override
                    public void onError() {
                        mDataManager.getPicasso()
                                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                                .placeholder(mDummy)
                                .error(mDummy)
                                .into(mUserImage, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        hideProgress();
                                    }

                                    @Override
                                    public void onError() {
                                        showSnackBar(mCoordinatorLayout, ConstantManager.ERROR_LOAD);
                                    }
                                });
                    }
                });
    }

    /**
     * Загрузк фото профиля пользователя через Picasso с отображением прогресса
     */
    private void initUserPhotoWhithProgress() {
        Log.d(TAG, "initUserPhotoWhithProgress");

        showProgress();
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                setUserPhotoIntoView();
            }
        });
    }

    /**
     * Настройка toolbar
     */
    private void setupToolBar() {
        Log.d(TAG, "setupToolBar");

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Настройка боково панели
     */
    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    mNavigationDrawer.closeDrawer(GravityCompat.START);
                    showSnackBar(mCoordinatorLayout, item.getTitle().toString());
                    item.setChecked(true);
                    switch (item.getItemId()) {
                        case R.id.user_profile_menu:
                            break;
                        case R.id.team_menu:
                            Intent listIntent = new Intent(MainActivity.this, UserListActivity.class);
                            startActivity(listIntent);
                            finish();
                            break;
                    }
                    return false;
                }
            });
            mAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_avatar);
        }
    }


    /**
     * Установка валидатора для элементов EditText
     */
    private void setUserInfoValidator() {

        for (int i = 0; i < mUserInfoLayouts.size(); i++) {

            if (mUserInfoValidator.size() <= i || mUserInfoValidator.get(i) == null) {
                mUserInfoValidator.add(new EditTextValidator(this,
                        mUserInfoViews.get(i), mUserInfoLayouts.get(i), i));
            }

            mUserInfoViews.get(i).addTextChangedListener(mUserInfoValidator.get(i));
        }
    }

    /**
     * Отключение валидатора от элементов EditText
     */
    private void removeUserInfoValidator() {
        if (mUserInfoValidator.size() > 0) {
            for (int i = 0; i < mUserInfoLayouts.size(); i++) {
                if (mUserInfoValidator.size() > i) {
                    mUserInfoViews.get(i).removeTextChangedListener(mUserInfoValidator.get(i));
                }
            }
        }
    }

    /**
     * Изменение режима - редактирование/сохранение
     *
     * @param mode - режим (1 - редактирование)
     */
    private void changeEditMode(int mode) {
        Log.d(TAG, "changeEditMode");

        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }

            showProfilePlaceholder();
            // Блокируем турбар
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

            // Устанавливаем валидаторы
            setUserInfoValidator();

            // Устанавливаем фокус на поле редактирования телефона
            mUserInfoViews.get(ConstantManager.USER_PHONE_ID).requestFocus();
            // Перемещаем курсор в конец строки
            mUserInfoViews.get(ConstantManager.USER_PHONE_ID).setSelection(mUserInfoViews.get(ConstantManager.USER_PHONE_ID).getText().length());
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
            hideProfilePlaceholder();

            // Убираем валидаторы
            removeUserInfoValidator();

            // Разблокируем тулбар
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            saveUserFields();
        }
        mCurrentEditMode = mode;
    }

    /*
     * Загрузка профиля пользователя из Preferences
     */
    private void initUserFields() {
        Log.d(TAG, "initUserFields");

        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++){
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * Сохранение профиля пользователя в Preferences
     */
    private void saveUserFields() {
        Log.d(TAG, "saveUserFields");

        List<String> userData = new ArrayList<>();
        for (int i = 0; i < mUserInfoViews.size(); i++) {
            userData.add(mUserInfoViews.get(i).getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    /**
     * Сохранить рейтинг пользователя в Preferences
     */
    private void initUserRatings() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserRatingValues();
        for (int i = 0; i < userData.size(); i++) {
            mUserRatings.get(i).setText(userData.get(i));
        }
    }

    /**
     * Загрузка фото из галереи
     */
    private void loadPhotoFromGallery() {
        Log.d(TAG, "loadPhotoFromGallery");

        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.message_choise_photo)), ConstantManager.REQUEST_GALLIRY_PICTURE);
    }

    /**
     * Получение каталога для сохранения фотографии
     *
      * @return - каталог
     */
    private File getExternalStoragePictureDirectory() {
        Log.d(TAG, "getExternalStoragePictureDirectory");

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (storageDir.exists()) {
                return storageDir;
            } else {
                storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                if (storageDir.exists()) {
                    return storageDir;
                } else {
                    storageDir = Environment.getExternalStorageDirectory();
                    if (storageDir.exists()) {
                        return storageDir;
                    } else {
                        return null;
                    }
                }
            }
        } else {
            return null;
        }

    }

    /**
     * Создание файла для фото
     *
     * @return              - файл фотографии
     * @throws IOException  - ошибка
     */
    private File createImageFile() throws IOException {
        Log.d(TAG, "createImageFile");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPEG_" + timeStamp;

        /*
        В данном варианте не учитывается отсутсвие нужной дирректории

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);

        Сделал при первом обращении определение дирректории с проверкой
        Файл создается в нее, опять же если подключено внешнее хранилище
        */

        if (mExternalStoragePictureDirectory == null)
            mExternalStoragePictureDirectory = getExternalStoragePictureDirectory();

        if (mExternalStoragePictureDirectory != null) {
            File image = File.createTempFile(fileName, ".jpg", mExternalStoragePictureDirectory);

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());
            this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            return image;
        } else {
            return null;
        }
    }

    /**
     * Загрузка фото из камеры
     */
    private void loadPhotoFromCamera() {
        Log.d(TAG, "loadPhotoFromCamera");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                showError(getString(R.string.create_file_error), e);
                loadPhotoFromGallery();
            }

            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            } else {
                showToast(getString(R.string.create_file_error));
                loadPhotoFromGallery();
            }
        } else {
            ActivityCompat.requestPermissions(this,
                new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                ConstantManager.PERMISSION_REQUEST_CAMERA_CODE);

            Snackbar.make(mCoordinatorLayout, R.string.message_permission_needed,
                Snackbar.LENGTH_LONG).setAction(R.string.message_permission, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSetting();
                    }
                }).show();
        }
    }

    /**
     * Отображаем фотограцию и сохраняем в Preferences
     *
     * @param selectedImage - выбранный файл
     */
    private void insertProfileImage(Uri selectedImage) {
        Log.d(TAG, "insertProfileImage");

        Picasso.with(this).load(selectedImage).transform(new TransformAndCropImage()).into(mUserImage);

        if (!selectedImage.equals(mDataManager.getPreferencesManager().loadUserPhoto())) {
            uploadUserPhoto(selectedImage);
        }

        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
        hideProfilePlaceholder();
    }

    /**
     * Скрываем placeholder
     */
    private void hideProfilePlaceholder() {
        Log.d(TAG, "hideProfilePlaceholder");

        mProfilePlaceholder.setVisibility(View.GONE);
    }

    /**
     * Отображаем placeholder
     */
    private void showProfilePlaceholder() {
        Log.d(TAG, "showProfilePlaceholder");

        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    // Блокируем Toolbar
    private void lockToolbar() {
        Log.d(TAG, "lockToolbar");

        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Разблокируем Toolbar
     */
    private void unlockToolbar() {
        Log.d(TAG, "unlockToolbar");

        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Диалог выбора получения фото
     *
     * @id - код запроса
     * @return - созданный диалог
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Log.d(TAG, "onCreateDialog");

        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectedItems = {
                    getString(R.string.user_profile_dialog_gallery),
                    getString(R.string.user_profile_dialog_camera),
                    getString(R.string.user_profile_dialog_cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.user_profile_placeholder_image);
                builder.setItems(selectedItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiseItem) {
                        switch (choiseItem) {
                            case 0:
                                loadPhotoFromGallery();
                                showSnackBar(mCoordinatorLayout, "загрузить из галлереи");
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                showSnackBar(mCoordinatorLayout, "сделать снимок");
                                break;
                            default:
                                dialog.cancel();
                                showSnackBar(mCoordinatorLayout, "отмена");
                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }

    /**
     * Вызов окна настроек телефона
     */
    private void openApplicationSetting() {
        Log.d(TAG, "openApplicationSetting");

        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTING_CODE);
    }

    /**
     * Вызов звонка, выполняется проверка наличия прав, и их запрос при необходимости
     * аналогично получению фотографии
     */
    private void callUserPhone() {
        Log.d(TAG, "callUserPhone");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:" + mUserInfoViews.get(ConstantManager.USER_PHONE_ID).getText().toString()));
                    //Uri.parse("tel:" + mUserPhone.getText().toString()));
            startActivity(callIntent);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CALL_PHONE},
                    ConstantManager.PERMISSION_REQUEST_CALL_CODE);

            Snackbar.make(mCoordinatorLayout, R.string.message_permission_needed,
                    Snackbar.LENGTH_LONG).setAction(R.string.message_permission, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openApplicationSetting();
                }
            }).show();
        }
    }

    /**
     * Получение абсолютного пути к файлу
     *
     * @param uri - путь к файлу
     * @return - абсолютный путь к файлу
     */
    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    /**
     * Загрузка файла фото пользователя на сайт
     *
     * @param fileUri - абсолютный путь файлу
     */
    private void uploadUserPhoto(Uri fileUri) {

        if (NetworkStatusChecker.isNetworkAvailable(this)) {

            String file = getRealPathFromURI(fileUri);

            Call<UserEditPhotoRes> call = mDataManager.editUserPhoto(Uri.parse(file));
            call.enqueue(new retrofit2.Callback<UserEditPhotoRes>() {
                @Override
                public void onResponse(Call<UserEditPhotoRes> call, Response<UserEditPhotoRes> response) {
                    if (response.code() == 200) {
                        showSnackBar(mCoordinatorLayout, ConstantManager.MESS_SUCCESS);
                        mDataManager.getPreferencesManager().saveUserPhotoUpdated(response.body().getData().getLastUpdated());
                    } else {
                        showSnackBar(mCoordinatorLayout, ConstantManager.ERROR_LOAD + Integer.toString(response.code()));
                    }
                }

                @Override
                public void onFailure(Call<UserEditPhotoRes> call, Throwable t) {
                    showSnackBar(mCoordinatorLayout, ConstantManager.ERROR_ANSWER);
                }
            });
        } else {
            showSnackBar(mCoordinatorLayout, ConstantManager.ERROR_NETWORK);
        }
    }

}
