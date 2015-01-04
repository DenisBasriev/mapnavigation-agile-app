package com.example.erman.photomapnavigation.presenters;

import android.text.TextUtils;

import com.example.erman.photomapnavigation.Constants;
import com.example.erman.photomapnavigation.R;
import com.example.erman.photomapnavigation.RequestTask;
import com.example.erman.photomapnavigation.models.RegisteredUser;
import com.example.erman.photomapnavigation.services.GetRequest;
import com.example.erman.photomapnavigation.views.SignInView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by erman on 08.12.2014.
 */
public class SignInPresenterImpl implements SignInPresenter {

    private SignInView signInView;

    public void setSignInView(SignInView signInView) {
        this.signInView = signInView;
    }

    @Override
    public void signIn(String email, String password) {
        boolean error = false;
        if (TextUtils.isEmpty(email)) {
            error = true;

            signInView.showEmailError();
        }

        if (TextUtils.isEmpty(password)) {
            error = true;

            signInView.showPasswordError();
        }

        if (!error) {
            GetRequest request = new GetRequest(this);
            request.setLoadMessage(signInView.getStringFromR(R.string.sign_in_message));
            String [] requestArgs = new String[2];
            requestArgs[0] = Constants.USER_PAGE + "2.json";
            requestArgs[1] = String.valueOf(RequestTask.SIGN_IN_TASK);
            request.execute(requestArgs);
        }
    }

    @Override
    public void signUp() {
        signInView.navigateToSignUp();
    }

    @Override
    public void lookUp() {
        signInView.navigateVisitorToMap();
    }

    @Override
    public void notifyToShowProgressDialog(String message) {
        signInView.showProgressDialog(message);
    }

    @Override
    public void notifyToDismissProgressDialog() {
        signInView.dismissProgressDialog();
    }

    @Override
    public void asyncTaskDone(JSONObject jsonObject, RequestTask task) throws JSONException{
        int id = jsonObject.getInt("id");

        String firstName = jsonObject.getString("firstName");
        String lastName = jsonObject.getString("email");
        String email = firstName + lastName + "@gmail.com";

        signInView.navigateUserToMap(id, firstName, lastName, email);
    }

    @Override
    public void notifyToShowConnectionError() {
        signInView.alertNoConnection();
    }
}
