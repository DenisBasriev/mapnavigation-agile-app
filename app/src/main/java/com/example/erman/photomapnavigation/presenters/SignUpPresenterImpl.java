package com.example.erman.photomapnavigation.presenters;

import android.text.TextUtils;
import android.util.Log;

import com.example.erman.photomapnavigation.views.SignUpView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by erman on 09.12.2014.
 */
public class SignUpPresenterImpl implements SignUpPresenter {

    private SignUpView view;

    @Override
    public void setView(SignUpView view) {
        this.view = view;
    }

    @Override
    public void signUp(String email, String reEmail, String password, String rePassword) {
        boolean error = false;

        Log.d("emails", "Email: " + email + ", ReEmail: " + reEmail);
        Log.d("passwords", "Password: " + password + ", RePassword: " + rePassword);

        if (TextUtils.isEmpty(email)) {
            error = true;

            view.showEmptyEmailError();
        }

        if (!isEmail(email)) {
            error = true;

            view.showInvalidEmailError();
        }

        if (TextUtils.isEmpty(reEmail)) {
            error = true;

            view.showEmptyReEmailError();
        }

        if (!isEmail(reEmail)) {
            error = true;

            view.showInvalidReEmailError();
        }

        if (!email.equals(reEmail)) {
            error = true;

            view.showEmailsNotMatchError();
        }

        if (TextUtils.isEmpty(password)) {
            error = true;

            view.showEmptyPasswordError();
        }

        if (TextUtils.isEmpty(rePassword)) {
            error = true;

            view.showEmptyRePasswordError();
        }

        if (!password.equals(rePassword)) {
            error = true;

            view.showPasswordNotMatchError();
        }

        if (!error) {
            view.navigateToSignIn(email);
        }
}

    @Override
    public void cancelSignUp() {
        view.popPreviousActivity();
    }

    private boolean isEmail(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;
    }
}
