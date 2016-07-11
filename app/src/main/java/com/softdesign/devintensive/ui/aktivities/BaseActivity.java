package com.softdesign.devintensive.ui.aktivities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

/**
 * Класс с доп свойствами главного окна
 */
public class BaseActivity extends AppCompatActivity {

    static final String TAG = ConstantManager.TAG_PREFIX + "BaseActivity";
    protected ProgressDialog mProgressDialog;

    /**
     * Показать прогресс
     */
    public void showProgress() {
        Log.d(TAG, "showProgress");

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.custom_dialog);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.progress_splash);
    }

    /**
     * Скрыть прогресс
     */
    public void hideProgress() {
        Log.d(TAG, "hideProgress");

        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        }
    }

    /**
     * Показать ошибку
     *
     * @param message
     * @param error
     */
    public void showError(String message, Exception error) {
        Log.e(TAG, "showError: " + String.valueOf(error));

        showToast(message);
    }

    /**
     * Показать сообщение
     *
     * @param message
     */
    public void showToast(String message) {
        Log.d(TAG, "showToast: " + message);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
