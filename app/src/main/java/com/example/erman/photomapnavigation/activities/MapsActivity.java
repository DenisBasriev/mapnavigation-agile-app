package com.example.erman.photomapnavigation.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import com.example.erman.photomapnavigation.R;
import com.example.erman.photomapnavigation.models.Photo;
import com.example.erman.photomapnavigation.presenters.MapsPresenterUser;
import com.example.erman.photomapnavigation.presenters.MapsPresenterUserImpl;
import com.example.erman.photomapnavigation.presenters.MapsPresenterVisitor;
import com.example.erman.photomapnavigation.presenters.MapsPresenterVisitorImpl;
import com.example.erman.photomapnavigation.views.MapsView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements MapsView, GoogleMap.OnMarkerClickListener{

    private MapsPresenterUser userPresenter;
    private MapsPresenterVisitor visitorPresenter;
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

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            isRegistered = extras.getBoolean("isRegistered?");

            if (isRegistered) {
                userPresenter = new MapsPresenterUserImpl();
                userPresenter.signedIn(extras);
                userPresenter.setView(this);
                userEmail = extras.getString("email");
            } else {
                visitorPresenter = new MapsPresenterVisitorImpl();
                visitorPresenter.signedIn(extras);
                visitorPresenter.setView(this);
            }
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
            userPresenter.takePhoto();

            return true;
        } else if (item.getItemId() == R.id.action_sign_up) {
            visitorPresenter.signUp();

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
                    mMap.setOnMarkerClickListener(this);
                    if (isRegistered) {
                        userPresenter.setUpMap();
                    } else {
                        visitorPresenter.setUpMap();
                    }
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
    public String getStringFromR(int event_load_message) {
        return getString(event_load_message);
    }

    @Override
    public void startImageCaptureActivity(Intent intent, Integer request) {
        startActivityForResult(intent, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        userPresenter.savePhoto(resultCode);
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
    public String addNewMarker(Bitmap bitmap, LatLng latLng) {
        return mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap))).getId();
    }

    @Override
    public void rotateCamera(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void alertMarkerNotExist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.marker_not_exist_title).setMessage(R.string.marker_not_exist_error)
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
                userPresenter.alertDialogAnswered(alertDialogAnswer);

                dialogInterface.dismiss();
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialogAnswer = false;
                userPresenter.alertDialogAnswered(alertDialogAnswer);

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

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (isRegistered) {
            userPresenter.markerClicked(marker.getId());
        } else {
            visitorPresenter.markerClicked(marker.getId());
        }

        return false;
    }

    @Override
    public void sendPhotosToDisplayImages(ArrayList<Photo> photos) {
        int size = photos.size();

        String [] urls = new String[size];

        double [] lats = new double[size];
        double [] longts = new double[size];
        int [] eventIds = new int[size];
        for (int i = 0; i < size; i++) {
            urls[i] = photos.get(i).getBigPhotoUrl();
            lats[i] = photos.get(i).getLatLng().latitude;
            longts[i] = photos.get(i).getLatLng().longitude;
            eventIds[i] = photos.get(i).getOwnerEventId();
        }
        Bundle bundle = new Bundle();
        bundle.putStringArray("urls", urls);
        bundle.putDoubleArray("lats", lats);
        bundle.putDoubleArray("longts", longts);
        bundle.putIntArray("eventIds", eventIds);
        Intent intent = new Intent(MapsActivity.this, DisplayImageActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
