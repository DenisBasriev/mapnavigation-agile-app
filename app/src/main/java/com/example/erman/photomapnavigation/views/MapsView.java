package com.example.erman.photomapnavigation.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;

import com.example.erman.photomapnavigation.models.Photo;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by erman on 04.12.2014.
 */
public interface MapsView {

    public void showProgress();

    public void hideProgress();

    public String addNewMarker(Bitmap bitmap, LatLng latLng);

    public void startImageCaptureActivity(Intent intent, Integer request);

    public int getResultOk();

    public void broadcastToGallery(Intent intent);

    public Location getCurrentLocation();

    public void enableUserLocation();

    public void setCameraToCurrentLocation();

    public boolean wifiAvailable();

    public boolean mobileDataAvailable();

    public void alertMobileData();

    public void alertNoConnection(); //common

    public void showProgressDialog(String message); //common

    public void dismissProgressDialog();

    public void navigateToSignUp();

    public void showNonCancellableProgressDialog(String message);

    public void showLocationError();

    public String getStringFromR(int event_load_message);

    public void rotateCamera(LatLng latLng);

    public void alertMarkerNotExist();

    public void sendPhotosToDisplayImages(ArrayList<Photo> photos);
}
