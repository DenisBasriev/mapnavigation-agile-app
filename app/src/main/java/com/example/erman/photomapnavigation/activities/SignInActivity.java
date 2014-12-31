package com.example.erman.photomapnavigation.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.erman.photomapnavigation.R;
import com.example.erman.photomapnavigation.presenters.SignInPresenter;
import com.example.erman.photomapnavigation.presenters.SignInPresenterImpl;
import com.example.erman.photomapnavigation.views.SignInView;

public class SignInActivity extends ActionBarActivity implements SignInView, View.OnClickListener{

    private EditText email;
    private EditText password;
    private Button signIn;
    private Button signUp;
    private Button lookOut;
    private ProgressDialog progressDialog;
    private SignInPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signIn = (Button) findViewById(R.id.sign_in_button);
        lookOut = (Button) findViewById(R.id.look_out);
        signUp = (Button) findViewById(R.id.sign_up_button);
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        lookOut.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        presenter = new SignInPresenterImpl();
        presenter.setSignInView(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            email.setText(extras.getString("registeredEmail"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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
        if (currentViewId == R.id.sign_in_button) {
            presenter.signIn(email.getText().toString(), password.getText().toString());
        } else if (currentViewId == R.id.look_out) {
            presenter.lookUp();
        } else if (currentViewId == R.id.sign_up_button) {
            presenter.signUp();
        }
    }

    @Override
    public void showEmailError() {
        email.setError(getString(R.string.email_empty_error));
    }

    @Override
    public void showPasswordError() {
        password.setError(getString(R.string.password_empty_error));
    }

    @Override
    public void navigateUserToMap(int userId, String firstName, String lastName, String email) {
        Intent intent = new Intent(SignInActivity.this, MapsActivity.class);
        intent.putExtra("isRegistered?", true);
        intent.putExtra("userId", userId);
        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    @Override
    public void navigateVisitorToMap() {
        Intent intent = new Intent(SignInActivity.this, MapsActivity.class);
        intent.putExtra("isRegistered?", false);
        startActivity(intent);
    }

    @Override
    public void navigateToSignUp() {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void alertNoConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.no_connection_title).setMessage(R.string.no_connection_message).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public String getStringFromR(int event_load_message) {
        return getString(event_load_message);
    }
}
