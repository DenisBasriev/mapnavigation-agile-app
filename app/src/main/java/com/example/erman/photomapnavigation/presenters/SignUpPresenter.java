package com.example.erman.photomapnavigation.presenters;

import com.example.erman.photomapnavigation.views.SignUpView;

/**
 * Created by erman on 09.12.2014.
 */
public interface SignUpPresenter {

    public void setView(SignUpView view);
    public void signUp(String email, String reEmail, String password, String rePassword);
    public void cancelSignUp();
}
