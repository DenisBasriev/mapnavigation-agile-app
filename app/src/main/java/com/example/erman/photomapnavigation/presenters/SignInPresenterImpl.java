package com.example.erman.photomapnavigation.presenters;

import android.text.TextUtils;

import com.example.erman.photomapnavigation.views.SignInView;

/**
 * Created by erman on 08.12.2014.
 */
public class SignInPresenterImpl implements SignInPresenter {

    private SignInView view;

    @Override
    public void setView(SignInView view) {
        this.view = view;
    }

    @Override
    public void signIn(String email, String password) {
        boolean error = false;
        if (TextUtils.isEmpty(email)) {
            error = true;

            view.showEmailError();
        }

        if (TextUtils.isEmpty(password)) {
            error = true;

            view.showPasswordError();
        }

        if (!error) {
            view.navigateUserToMap();
        }
    }

    @Override
    public void signUp() {
        view.navigateToSignUp();
    }

    @Override
    public void lookUp() {
        view.navigateVisitorToMap();
    }
}
