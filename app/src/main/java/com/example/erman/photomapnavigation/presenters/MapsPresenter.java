package com.example.erman.photomapnavigation.presenters;

import android.os.Bundle;

import com.example.erman.photomapnavigation.RequestTask;
import com.example.erman.photomapnavigation.models.Photo;
import com.example.erman.photomapnavigation.views.MapsView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by erman on 31.12.2014.
 */
public interface MapsPresenter extends Presenter{

    public void setView(MapsView mapsView);

    public void setUpMap();

    public void signedIn(Bundle extras);

    public void markerClicked(String markerId);

}
