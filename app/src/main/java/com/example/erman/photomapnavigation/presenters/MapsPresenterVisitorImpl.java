package com.example.erman.photomapnavigation.presenters;

import android.os.Bundle;

import com.example.erman.photomapnavigation.R;
import com.example.erman.photomapnavigation.RequestTask;
import com.example.erman.photomapnavigation.models.Photo;
import com.example.erman.photomapnavigation.models.UnregisteredUser;
import com.example.erman.photomapnavigation.views.MapsView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by erman on 31.12.2014.
 */
public class MapsPresenterVisitorImpl implements MapsPresenterVisitor{

    private MapsView mapsView;
    private UnregisteredUser user;

    @Override
    public void setView(MapsView mapsView) {
        this.mapsView = mapsView;
    }

    @Override
    public void signedIn(Bundle extras) {
        user = new UnregisteredUser();
    }

    @Override
    public void downloadDone(Photo[] photos, RequestTask task) {

    }

    @Override
    public void setUpMap() {
        mapsView.showNonCancellableProgressDialog(mapsView.getStringFromR(R.string.search_location_message));
        mapsView.enableUserLocation();
        mapsView.setCameraToCurrentLocation();
        mapsView.dismissProgressDialog();
    }

    @Override
    public void signUp() {
        mapsView.navigateToSignUp();
    }

    @Override
    public void notifyToShowProgressDialog(String message) {
        mapsView.showProgressDialog(message);
    }

    @Override
    public void notifyToDismissProgressDialog() {
        mapsView.dismissProgressDialog();
    }

    @Override
    public void asyncTaskDone(JSONObject jsonObject, RequestTask task) throws JSONException {

    }

    @Override
    public void notifyToShowConnectionError() {
        mapsView.alertNoConnection();
    }
}
