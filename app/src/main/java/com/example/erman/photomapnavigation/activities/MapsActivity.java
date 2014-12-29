package com.example.erman.photomapnavigation.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.erman.photomapnavigation.R;
import com.example.erman.photomapnavigation.presenters.MapsPresenter;
import com.example.erman.photomapnavigation.presenters.MapsPresenterImpl;
import com.example.erman.photomapnavigation.views.MapsView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements MapsView{

    private MapsPresenter presenter;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ProgressBar progressBar;
    private boolean alertDialogAnswer = false;
    private ProgressDialog progressDialog;
    private String userEmail;
    private boolean isRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressDialog = new ProgressDialog(this);
        presenter = new MapsPresenterImpl();
        presenter.setView(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            userEmail = extras.getString("userEmail");
            isRegistered = extras.getBoolean("isRegistered?");
        }
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (isRegistered) {
            getMenuInflater().inflate(R.menu.maps_user_actionbar, menu);
            final MenuItem item = menu.findItem(R.id.action_profile);
            item.setTitle(userEmail);
        } else {
            getMenuInflater().inflate(R.menu.maps_visitor_actionbar, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_take_picture){
            presenter.takePhoto();

            return true;
        } else if (item.getItemId() == R.id.action_sign_up) {
            presenter.signUp();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                    presenter.setUpMap(userEmail, isRegistered);
            }
        }
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void enableUserLocation() {
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public Location getCurrentLocation() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        return location;
        //return mMap.getMyLocation();
    }

    @Override
    public void setCameraToCurrentLocation() {
        Location currentLocation = getCurrentLocation();
        if (currentLocation != null){
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraPosition position = new CameraPosition.Builder().target(latLng).zoom(15.0f).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        } else {
            showLocationError();
        }
    }

    @Override
    public void showLocationError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cannot_find_location_error_title).setMessage(R.string.cannot_find_location_error_message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void startImageCaptureActivity(Intent intent, Integer request) {
        startActivityForResult(intent, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.savePhoto(resultCode);
    }

    @Override
    public int getResultOk() {
        return RESULT_OK;
    }

    @Override
    public void broadcastToGallery(Intent intent) {
        this.sendBroadcast(intent);
    }

    @Override
    public void addNewMarker(Bitmap bitmap, LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public boolean wifiAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        return wifi == NetworkInfo.State.CONNECTED;
    }

    @Override
    public boolean mobileDataAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

        return mobile == NetworkInfo.State.CONNECTED;
    }

    @Override
    public void alertMobileData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.mobiledata_title).setMessage(R.string.mobiledata_message).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialogAnswer = true;
                presenter.alertDialogAnswered(alertDialogAnswer);

                dialogInterface.dismiss();
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialogAnswer = false;
                presenter.alertDialogAnswered(alertDialogAnswer);

                dialogInterface.dismiss();
            }
        }).setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog alert = builder.create();
        alert.show();
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
    public void copyToClipboard(String url) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(url);
        Toast.makeText(this, R.string.url_copied_clipboard, Toast.LENGTH_LONG).show();
    }

    @Override
    public void navigateToSignUp() {
        Intent intent = new Intent(MapsActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void showNonCancellableProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
        progressDialog.setCancelable(true);
    }
}
