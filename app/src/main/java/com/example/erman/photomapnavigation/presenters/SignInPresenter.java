package com.example.erman.photomapnavigation.presenters;

import com.example.erman.photomapnavigation.views.SignInView;

/**
 * Created by erman on 08.12.2014.
 */
public interface SignInPresenter extends Presenter{

    public void setSignInView(SignInView view);
    public void signIn(String email, String password);
    public void lookUp();
    public void signUp();
}
