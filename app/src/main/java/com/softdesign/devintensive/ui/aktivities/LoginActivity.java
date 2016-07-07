package com.softdesign.devintensive.ui.aktivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Окно авторизации
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    /**
     * Инициализация полей
     */
    @BindView(R.id.login_email) EditText mLoginEmail;
    @BindView(R.id.login_password) EditText mLoginPassword;
    @BindView(R.id.login_button) Button mLoginButton;
    @BindView(R.id.login_fogott_password) TextView mLoginFogott;

    private static final String TAG = ConstantManager.TAG_PREFIX + "LoginActivity";

    /**
     * Создание Activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        Log.d(TAG, "onCreate");
        ButterKnife.bind(this);
        mLoginButton.setOnClickListener(this);
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
                if (mLoginEmail.getText().toString().trim().isEmpty()) {
                    showToast(getString(R.string.login_email_empty));
                } else {
                    if (mLoginPassword.getText().toString().trim().isEmpty()) {
                        showToast(getString(R.string.login_password_empty));
                    } else {
                        Intent mainIntent = new Intent(this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                }
                break;
            case R.id.login_fogott_password:
                // TODO: 06.07.2016 подсказка пароля
                break;
        }
    }
}

