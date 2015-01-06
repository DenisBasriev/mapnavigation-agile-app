package com.example.erman.photomapnavigation.presenters;

import android.os.Bundle;
import android.util.Log;

import com.example.erman.photomapnavigation.Constants;
import com.example.erman.photomapnavigation.R;
import com.example.erman.photomapnavigation.RequestTask;
import com.example.erman.photomapnavigation.models.Event;
import com.example.erman.photomapnavigation.models.Photo;
import com.example.erman.photomapnavigation.models.UnregisteredUser;
import com.example.erman.photomapnavigation.services.DownloadImageTask;
import com.example.erman.photomapnavigation.services.GetRequest;
import com.example.erman.photomapnavigation.views.MapsView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    public void setUpMap() {
        searchForLocation();
        loadAccessableEvents();
    }

    private void searchForLocation() {
        mapsView.showNonCancellableProgressDialog(mapsView.getStringFromR(R.string.search_location_message));
        mapsView.enableUserLocation();
        mapsView.setCameraToCurrentLocation();
        mapsView.dismissProgressDialog();
    }

    private void loadAccessableEvents() {
        GetRequest request = new GetRequest(this);
        request.setLoadMessage(mapsView.getStringFromR(R.string.event_load_message));
        request.setTask(RequestTask.GET_ACCESSABLE_EVENTS_TASK);
        request.execute(Constants.EVENTS_PAGE + Constants.JSON_PAGE_POSTFIX);
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
        if (task == RequestTask.GET_ACCESSABLE_EVENTS_TASK) {
            user.setAccessableEvents(createEventsFromJSON(jsonObject));

            downloadAccessableEventsRootPhotos();
        } else if(task == RequestTask.GET_EVENTS_PHOTOS) {
            ArrayList<Photo> photos = new ArrayList<Photo>(createPhotosFromJSON(jsonObject));

            mapsView.sendPhotosToDisplayImages(photos);
        }
    }

    private ArrayList<Event> createEventsFromJSON(JSONObject jsonObject) {
        ArrayList<Event> events = new ArrayList<Event>();

        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("events");
        } catch (JSONException e) {
            Log.d("JSON Exception", "Cannot get json array");
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject arrayElement = jsonArray.getJSONObject(i);
                Log.d("Got json object", arrayElement.toString());
                Event event = new Event(arrayElement.getInt("id"), arrayElement.getInt("user_id"));
                Log.d("events", event.toString());
                LatLng latLng = new LatLng(arrayElement.getDouble("latitude"), arrayElement.getDouble("longitude"));
                Log.d("latlngs", latLng.toString());
                Photo photo = new Photo(arrayElement.getString("thumbnail"), latLng, event.getEventId());
                Log.d("photos", photo.toString());
                event.setRootPhoto(photo);
                events.add(event);
                Log.d("event added", "event added");
            } catch (JSONException e) {
                Log.d("JSON exception", "Cannot parse json array's element");
            }
        }

        return events;
    }

    private ArrayList<Photo> createPhotosFromJSON(JSONObject jsonObject) {
        ArrayList<Photo> photos = new ArrayList<Photo>();
        JSONArray jsonArray = null;

        try {
            jsonArray = jsonObject.getJSONArray("photos");
        } catch (JSONException e) {
            Log.d("JSON Exception", "Cannot get json array");
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject arrayElement = jsonArray.getJSONObject(i);
                Log.d("Got json object", arrayElement.toString());
                LatLng latLng = new LatLng(arrayElement.getDouble("latitude"), arrayElement.getDouble("longitude"));
                Photo photo = new Photo(arrayElement.getString("url"), latLng, arrayElement.getInt("event_id"));
                photos.add(photo);
            } catch (JSONException e) {
                Log.d("JSON exception", "Cannot parse json array's element");
            }
        }

        return photos;
    }

    private void downloadAccessableEventsRootPhotos() {
        downloadEvents(user.getAccessableEvents(), RequestTask.DOWNLOAD_ACCESSABLE_EVENTS_ROOT_PHOTOS);
    }

    private void downloadEvents(ArrayList<Event> events, RequestTask task) {
        Photo [] photosToDownload = new Photo[events.size()];

        for (int i = 0; i < events.size(); i++) {
            photosToDownload[i] = events.get(i).getRootPhoto();
        }

        new DownloadImageTask(this, task).execute(photosToDownload);
    }

    @Override
    public void downloadDone(Photo[] photos, RequestTask task) {
        if (task == RequestTask.DOWNLOAD_ACCESSABLE_EVENTS_ROOT_PHOTOS) {
            for (Photo p : photos) {
                for (Event e : user.getAccessableEvents()) {
                    if (p.getOwnerEventId() == e.getEventId()) {
                        e.setRootPhoto(p);

                        e.setMarkerId(mapsView.addNewMarker(p.getSource(), p.getLatLng()));

                        break;
                    }
                }
            }
        }
    }

    @Override
    public void markerClicked(String markerId) {
        int clickedEventId = user.findEventIdFromMarkerId(markerId);

        if (clickedEventId != -1) {
            loadEventsPhotos(clickedEventId);
        } else {
            mapsView.alertMarkerNotExist();
        }
    }

    private void loadEventsPhotos(int clickedEventId) {
        GetRequest request = new GetRequest(this);
        request.setLoadMessage(mapsView.getStringFromR(R.string.retrieve_data_message));
        request.setTask(RequestTask.GET_EVENTS_PHOTOS);
        request.execute(Constants.EVENTS_PAGE + "/" + clickedEventId + Constants.JSON_PAGE_POSTFIX);
    }

    @Override
    public void notifyToShowConnectionError() {
        mapsView.alertNoConnection();
    }
}
