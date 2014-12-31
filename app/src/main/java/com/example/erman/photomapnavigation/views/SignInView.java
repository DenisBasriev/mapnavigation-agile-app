package com.example.erman.photomapnavigation.views;

/**
 * Created by erman on 08.12.2014.
 */
public interface SignInView {

    public void showEmailError();
    public void showPasswordError();
    public void navigateUserToMap(int userId, String firstName, String lastName, String email);
    public void navigateVisitorToMap();
    public void navigateToSignUp();
    public void showProgressDialog(String message);
    public void dismissProgressDialog();
    public void alertNoConnection();
    public String getStringFromR(int event_load_message);
}
