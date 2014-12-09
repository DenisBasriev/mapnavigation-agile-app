package com.example.erman.photomapnavigation.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.erman.photomapnavigation.R;
import com.example.erman.photomapnavigation.presenters.SignUpPresenter;
import com.example.erman.photomapnavigation.presenters.SignUpPresenterImpl;
import com.example.erman.photomapnavigation.views.SignUpView;

public class SignUpActivity extends ActionBarActivity implements SignUpView, View.OnClickListener{

    private EditText email;
    private EditText reEmail;
    private EditText password;
    private EditText rePassword;
    private Button doneButton;
    private Button cancelButton;
    private SignUpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        email = (EditText) findViewById(R.id.email_sign_up);
        reEmail = (EditText) findViewById(R.id.reemail);
        password = (EditText) findViewById(R.id.password_sign_up);
        rePassword = (EditText) findViewById(R.id.repassword);
        doneButton = (Button) findViewById(R.id.done_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        doneButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        presenter = new SignUpPresenterImpl();
        presenter.setView(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int currentViewId = view.getId();
        if (currentViewId == R.id.done_button) {
            presenter.signUp(email.getText().toString(), reEmail.getText().toString(), password.getText().toString(), rePassword.getText().toString());
        } else if (currentViewId == R.id.cancel_button) {
            presenter.cancelSignUp();
        }
    }

    @Override
    public void showEmptyEmailError() {
        email.setError(getString(R.string.email_empty_error));
    }

    @Override
    public void showInvalidEmailError() {
        email.setError(getString(R.string.invalid_email_error));
    }

    @Override
    public void showEmptyReEmailError() {
        reEmail.setError(getString(R.string.email_empty_error));
    }

    @Override
    public void showInvalidReEmailError() {
        reEmail.setError(getString(R.string.invalid_email_error));
    }

    @Override
    public void showEmailsNotMatchError() {
        email.setError(getString(R.string.emails_not_match_error));
        reEmail.setError(getString(R.string.emails_not_match_error));
    }

    @Override
    public void showEmptyPasswordError() {
        password.setError(getString(R.string.password_empty_error));
    }

    @Override
    public void showEmptyRePasswordError() {
        rePassword.setError(getString(R.string.password_empty_error));
    }

    @Override
    public void showPasswordNotMatchError() {
        password.setError(getString(R.string.password_not_match_error));
        rePassword.setError(getString(R.string.password_not_match_error));
    }

    @Override
    public void navigateToSignIn(String email) {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        intent.putExtra("registeredEmail", email);
        startActivity(intent);
    }

    @Override
    public void popPreviousActivity() {
        finish();
    }
}
