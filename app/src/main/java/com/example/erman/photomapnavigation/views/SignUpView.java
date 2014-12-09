package com.example.erman.photomapnavigation.views;

/**
 * Created by erman on 09.12.2014.
 */
public interface SignUpView {

    public void showEmptyEmailError();
    public void showInvalidEmailError();
    public void showEmptyReEmailError();
    public void showInvalidReEmailError();
    public void showEmailsNotMatchError();
    public void showEmptyPasswordError();
    public void showEmptyRePasswordError();
    public void showPasswordNotMatchError();
    public void navigateToSignIn(String email);
    public void popPreviousActivity();
}
