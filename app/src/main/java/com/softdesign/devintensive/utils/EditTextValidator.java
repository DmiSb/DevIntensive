package com.softdesign.devintensive.utils;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.softdesign.devintensive.R;

/**
 * Класс валидации полей профиля пользователя
 */
public class EditTextValidator implements TextWatcher {

    private static final String TAG = ConstantManager.TAG_PREFIX + "EditTextValidator";
    // Контекст
    private Context mContext;
    // Редактируемый элемент
    private EditText mEdit;
    // Его корневой элемент
    private TextInputLayout mEditLayout;
    // Тип элемента, константа
    private String mEditType;
    // Позиция курсора
    private int mPosStart, mPosEnd;
    // Признак что курсор в конце текста
    private Boolean mEndPosition = false;

    /**
     * Конструктор класса
     *
     * @param context - контекст главной формы
     * @param validateEdit - проверяемый EditText
     * @param textInputLayout - TextInputLayout, на котором лежит EditText
     * @param editType - константа типа EditText
     */
    public EditTextValidator(Context context, EditText validateEdit, TextInputLayout textInputLayout, String editType){
        super();
        Log.d(TAG, "Constructor");

        mContext = context;
        mEdit = validateEdit;
        mEditLayout = textInputLayout;
        mEditType = editType;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * Событие после изменения текста
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {

        mEdit.removeTextChangedListener(this);
        mPosStart = mEdit.getSelectionStart();
        mPosEnd = mEdit.getSelectionEnd();
        if (mPosStart == mEdit.getText().length())
            mEndPosition = true;

        switch (mEditType) {
            case ConstantManager.USER_PHONE_KEY:
                mEdit.setText(validatePhone(s.toString()));
                break;
            case ConstantManager.USER_EMAIL_KEY:
                validateEmail(s.toString());
                break;
            case ConstantManager.USER_VK_KEY:
                mEdit.setText(validateLink(s.toString(), ConstantManager.VK_SYMBOL));
                break;
            case ConstantManager.USER_GIT_KEY:
                mEdit.setText(validateLink(s.toString(), ConstantManager.GIT_SYMBOL));
        }

        mEdit.addTextChangedListener(this);
        if (mEndPosition) {
            mEdit.setSelection(mEdit.getText().length());
        } else {
            if (mPosStart == mPosEnd) {
                mEdit.setSelection(mPosStart);
            } else {
                mEdit.setSelection(mPosStart, mPosEnd);
            }
        }
    }

    /**
     * Отображение текста ошибка
     *
     * @param resId - ссылка на строковый ресурс
     */
    private void showError(int resId ) {
        mEditLayout.setErrorEnabled(true);
        mEditLayout.setError(mContext.getString(resId));
    }

    /**
     * Сокрытие ошибки
     */
    private void hideError() {
        mEditLayout.setErrorEnabled(false);
        mEditLayout.setError(ConstantManager.EMPTY_STRING);
    }

    /**
     * Проветка на длину номера телефона
     * При длине менее 11 символов выводит ошибку
     *
     * @param phone
     */
    private void checkPhoneLength(String phone) {
        if (phone.length() < 11) {
            showError(R.string.phone_error_value);
        } else {
            hideError();
        }
    }

    /**
     * Валидация введенного номера телефона
     * Форматирование в требуемом формате
     * При длине менее 11 символов выводит ошибку
     *
     * @param phone
     * @return
     */
    private String validatePhone(String phone) {

        String oldVal = "";
        String newVal = "";
        int i;

        if (phone != null && phone.length() > 0) {
            for (i = 0; i < phone.length(); i++) {
                if (Character.isDigit(phone.charAt(i))) {
                    oldVal = oldVal + phone.charAt(i);
                } else {
                    if (i <= mPosStart)
                        mPosStart--;
                    if (i <= mPosEnd)
                        mPosEnd--;
                }
            }

            if (oldVal != null && oldVal.length() > 0) {
                if (oldVal.charAt(0) == '8') {
                    oldVal = '7' + oldVal.substring(1, oldVal.length());
                }

                int l = oldVal.length();
                if (l > 20) {
                    l = 20;
                }

                for (i = 0; i < oldVal.length(); i++) {
                    switch (i) {
                        case 0:
                            newVal = "+" + oldVal.charAt(i);
                            if (i < mPosStart)
                                mPosStart++;
                            if (i < mPosEnd)
                                mPosEnd++;
                            l++;
                            break;
                        case 1:
                        case 4:
                            newVal = newVal + " " + oldVal.charAt(i);
                            if (i < mPosStart)
                                mPosStart++;
                            if (i < mPosEnd)
                                mPosEnd++;
                            l++;
                            break;
                        case 7:
                        case 9:
                            newVal = newVal + "-" + oldVal.charAt(i);
                            if (i < mPosStart)
                                mPosStart++;
                            if (i < mPosEnd)
                                mPosEnd++;
                            l++;
                            break;
                        default:
                            newVal = newVal + oldVal.charAt(i);
                    }
                }

                if (mPosStart < 0)
                    mPosStart = 0;
                if (mPosEnd < 0)
                    mPosEnd = 0;

                if (l > 20) {
                    l = 20;
                }

                Log.d(TAG, oldVal + ", " + newVal + ", " + Integer.toString(mPosStart) + ", " + Integer.toString(mPosEnd));
                checkPhoneLength(newVal);
                return newVal.substring(0, l);
            } else {
                checkPhoneLength(newVal);
                return newVal;
            }

        } else {
            checkPhoneLength(newVal);
            return newVal;
        }
    }

    /**
     * Валидайия введенного E-mail
     * Минимальное число перед @ - 3, перед точкой - 2, домен - 2
     * Выводит ошибку
     *
     * @param email
     */
    private void validateEmail(String email) {

        int posEmail = email.indexOf(ConstantManager.EMAIL_SYMBOL);
        int posPoint = email.indexOf(ConstantManager.POINT_SYMBOL);
        if (posEmail >= 0 && posPoint >= 0) {
            if (posEmail < 3 || (posPoint - posEmail) < 3 || (email.length() - posPoint) < 3) {
                showError(R.string.email_error_value);
            } else {
                hideError();
            }
        } else {
            showError(R.string.email_error_value);
        }
    }

    /**
     * Валидация поля со ссылкой
     * Обрезает не нужные символы до корневого домена
     * Если корневой домен отсутсвует, выводит ошибку
     *
     * @param link - ссылка
     * @param domain - корневой домен
     * @return - новая ссылка
     */
    private String validateLink(String link, String domain) {

        String newValue = link;
        int s = link.length() - mPosStart;
        int e = link.length() - mPosEnd;

        int posVK = link.toLowerCase().indexOf(domain);
        if (posVK >= 0) {
            newValue = domain + link.substring(posVK + domain.length(), link.length());
            hideError();
        } else {
            showError(R.string.link_error_value);
        }
        mPosStart = newValue.length() - s;
        mPosEnd = newValue.length() - e;

        return  newValue;
    }
}
