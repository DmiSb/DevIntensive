package com.softdesign.devintensive.ui.aktivities;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.EditTextValidator;
import com.softdesign.devintensive.utils.RoundedDrawable;
import com.softdesign.devintensive.utils.TransformAndCropImage;
import com.softdesign.devintensive.utils.ViewBehavior;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Главное окно
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

    @BindView(R.id.call_img) ImageView mCallImg;
    @BindView(R.id.mail_send_img) ImageView mMailSendImg;
    @BindView(R.id.vk_open_img) ImageView mVkOpenImg;
    @BindView(R.id.git_open_img) ImageView mGitOpenImg;

    @BindView(R.id.phone_et) EditText mUserPhone;
    @BindView(R.id.email_et) EditText mUserMail;
    @BindView(R.id.vk_et) EditText mUserVK;
    @BindView(R.id.git_et) EditText mUserGit;
    @BindView(R.id.self_et) EditText mUserSelf;

    @BindView(R.id.phone_layout) TextInputLayout mUserPhoneLayoyt;
    @BindView(R.id.email_layout) TextInputLayout mUserMailLayoyt;
    @BindView(R.id.vk_layout) TextInputLayout mUserVKLayoyt;
    @BindView(R.id.git_layout) TextInputLayout mUserGitLayoyt;

    private ImageView mAvatar;
    private AppBarLayout.LayoutParams mAppBarParams = null;

    /**
     * Переменные для валидации вводимых значений
     */
    private EditTextValidator mUserPhoneValidator = null;
    private EditTextValidator mUserMailValidator = null;
    private EditTextValidator mUserVKValidator = null;
    private EditTextValidator mUserGitValidator = null;

    private List<EditText> mUserInfoViews;

    File mPhotoFile = null;
    Uri mSelectedImage = null;

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

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVK);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserSelf);

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        mCallImg.setOnClickListener(this);
        mMailSendImg.setOnClickListener(this);
        mVkOpenImg.setOnClickListener(this);
        mGitOpenImg.setOnClickListener(this);

        setupToolBar();
        setupDrawer();

        if (mAvatar != null) {
            mAvatar.setImageBitmap(
                RoundedDrawable.getRoundedBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.avatar)));
        }

        loadUserInfoValue();
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_photo)
                .into(mUserImage);

        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();

        // Делаем пересчет начального паддинга в пиксели
        float iPad = (float) getResources().getDimensionPixelSize(R.dimen.spacing_half_large_28);
        // Создаем Behavior, чтобы регулировать отсутпы сверху у серой плашки рейтинга
        ViewBehavior vBehavior = new ViewBehavior(mRatingBar, iPad);
        // Делаем привязку на событие изменения круглой кнопки
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
        params.setBehavior(vBehavior);

        if (savedInstanceState == null) {
            mExternalStoragePictureDirectory = getExternalStoragePictureDirectory();
        } else {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");

        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
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
        Log.d(TAG, "onSaveInstanceState");

        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
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
                //showProgress();
                //runWithDalay();
                callUserPhone();
                break;
            case R.id.mail_send_img:
                Intent mailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts("mailto:", mUserMail.getText().toString(), null));
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
                Intent vkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + mUserVK.getText().toString()));
                startActivity(vkIntent);
                break;
            case R.id.git_open_img:
                Intent gitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + mUserGit.getText().toString()));
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
     * Настройка боковой панели
     */
    private void setupDrawer() {
        Log.d(TAG, "setupDrawer");

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

    /**
     * Показ сообщения в нижней части
     * @param message - текст сообщения
     */
    private void showSnackBar(String message) {
        Log.d(TAG, "showSnackBar");

        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Показ прогресса
     */
    private void runWithDalay() {
        Log.d(TAG, "runWithDalay");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: выполнить с задержкой
                hideProgress();
            }
        }, 3000);
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
            // Прячем на время редактирования "серую плашку" освобождая место
            mRatingBar.setVisibility(View.GONE);
            // Блокируем турбар
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

            if (mUserPhoneValidator == null)
                mUserPhoneValidator = new EditTextValidator(this, mUserPhone, mUserPhoneLayoyt, ConstantManager.USER_PHONE_KEY);
            mUserPhone.addTextChangedListener(mUserPhoneValidator);

            if (mUserMailValidator == null)
                mUserMailValidator = new EditTextValidator(this, mUserMail, mUserMailLayoyt, ConstantManager.USER_EMAIL_KEY);
            mUserMail.addTextChangedListener(mUserMailValidator);

            if (mUserVKValidator == null)
                mUserVKValidator = new EditTextValidator(this, mUserVK, mUserVKLayoyt, ConstantManager.USER_VK_KEY);
            mUserVK.addTextChangedListener(mUserVKValidator);

            if (mUserGitValidator == null)
                mUserGitValidator = new EditTextValidator(this, mUserGit, mUserGitLayoyt, ConstantManager.USER_GIT_KEY);
            mUserGit.addTextChangedListener(mUserGitValidator);

            // Устанавливаем фокус на поле редактирования телефона
            mUserPhone.requestFocus();
            // Перемещаем курсор в конец строки
            mUserPhone.setSelection(mUserPhone.getText().length());
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
            hideProfilePlaceholder();

            mUserPhone.removeTextChangedListener(mUserPhoneValidator);
            mUserMail.removeTextChangedListener(mUserMailValidator);
            mUserVK.removeTextChangedListener(mUserVKValidator);
            mUserGit.removeTextChangedListener(mUserGitValidator);

            // Возвращаем "серую плашку"
            mRatingBar.setVisibility(View.VISIBLE);
            // Разблокируем тулбар
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            saveUserInfoValue();
        }
        mCurrentEditMode = mode;
    }

    /*
     * Загрузка профиля пользователя из Preferences
     */
    private void loadUserInfoValue() {
        Log.d(TAG, "loadUserInfoValue");

        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++){
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * Сохранение профиля пользователя в Preferences
      */
    private void saveUserInfoValue() {
        Log.d(TAG, "saveUserInfoValue");

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

        Сделал при первом создании активити определение дирректории с проверкой
        Файл создается в нее, опять же если подключено внешнее хранилище
        */

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
     * @param - код запроса
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
                                showSnackBar("загрузить из галлереи");
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                showSnackBar("сделать снимок");
                                break;
                            default:
                                dialog.cancel();
                                showSnackBar("отмена");
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
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mUserPhone.getText().toString()));
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

}
