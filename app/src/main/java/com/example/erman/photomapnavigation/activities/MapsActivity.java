package com.example.erman.photomapnavigation.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

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
    private MenuItem takePhotoButton;
    private int onPrepareCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressDialog = new ProgressDialog(this);
        presenter = new MapsPresenterImpl();
        presenter.setView(this);

        setUpMapIfNeeded();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(onPrepareCounter!=0) {
            takePhotoButton.setVisible(true);
        }

        onPrepareCounter++;

        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.maps_actionbar, menu);

        takePhotoButton = menu.findItem(R.id.action_take_picture);
        takePhotoButton.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_take_picture){
            presenter.takePhoto();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.savePhoto(resultCode);
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
                presenter.setUpMap("ermanyafay@gmail.com");
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
    public void addNewMarker(Bitmap bitmap, LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void startImageCaptureActivity(Intent intent, Integer request) {
        startActivityForResult(intent, request);
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
    public LatLng getCurrentLocation() {
        Location loc = mMap.getMyLocation();

        return new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    @Override
    public void enableUserLocation() {
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void setCameraToCurrentLocation() {
         CameraPosition position = new CameraPosition.Builder().target(getCurrentLocation()).zoom(15.0f).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
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
    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void enableActionTakePicture() {
        invalidateOptionsMenu();
    }
}
