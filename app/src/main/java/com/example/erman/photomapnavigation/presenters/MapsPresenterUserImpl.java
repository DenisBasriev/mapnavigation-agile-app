package com.example.erman.photomapnavigation.presenters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.example.erman.photomapnavigation.Constants;
import com.example.erman.photomapnavigation.R;
import com.example.erman.photomapnavigation.RequestTask;
import com.example.erman.photomapnavigation.models.Event;
import com.example.erman.photomapnavigation.models.Photo;
import com.example.erman.photomapnavigation.models.RegisteredUser;
import com.example.erman.photomapnavigation.operators.BitmapOperator;
import com.example.erman.photomapnavigation.operators.FileOperator;
import com.example.erman.photomapnavigation.operators.FullBitmapDecoder;
import com.example.erman.photomapnavigation.operators.GeoTagger;
import com.example.erman.photomapnavigation.operators.ScaledBitmapDecoder;
import com.example.erman.photomapnavigation.services.DownloadImageTask;
import com.example.erman.photomapnavigation.services.GetRequest;
import com.example.erman.photomapnavigation.services.UploadImageTask;
import com.example.erman.photomapnavigation.views.MapsView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * Created by erman on 04.12.2014.
 */
public class MapsPresenterUserImpl implements MapsPresenterUser {

    private static int REQUEST_TAKE_PHOTO = 1;
    private MapsView mapsView;
    private String mCurrentPhotoPath;
    private String mCurrentConvertedImage;
    private LatLng mCurrentLatLng;
    private BitmapOperator bitmapOperator;
    private RegisteredUser user;

    public MapsPresenterUserImpl() {
        mCurrentPhotoPath = null;
        mCurrentConvertedImage = null;
        bitmapOperator = new BitmapOperator();
    }

    @Override
    public void signedIn(Bundle extras) {
        user = new RegisteredUser(extras.getInt("userId"), extras.getString("email"), extras.getString("firstName"), extras.getString("lastName"));
    }

    @Override
    public void setView(MapsView mapsView) { this.mapsView = mapsView; }

    @Override
    public void setUpMap() {
        searchForLocation();
        loadAccessableEvents();
        loadOwnEvents();
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

    private void loadOwnEvents() {
        GetRequest request =  new GetRequest(this);
        request.setLoadMessage(mapsView.getStringFromR(R.string.event_load_message));
        request.setTask(RequestTask.GET_OWN_EVENTS_TASK);
        request.execute(Constants.EVENT_USER_PAGE + user.getUserId() + Constants.JSON_PAGE_POSTFIX);
    }

    public void takePhoto() {
        mapsView.showProgress();

        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;

        try {
            f = new FileOperator().setUpPhotoFile();

            mCurrentPhotoPath = f.getAbsolutePath();

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException ex) {
            ex.printStackTrace();

            f = null;

            mCurrentPhotoPath = null;
        }
        mapsView.startImageCaptureActivity(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    @Override
    public void savePhoto(Integer resultCode) {
        if (resultCode==mapsView.getResultOk()) {
            handlePhoto();
        } else {
            File f = new File(mCurrentPhotoPath);
            f.delete();

            mCurrentPhotoPath = null;
            mapsView.hideProgress();
        }
    }

    private void handlePhoto() {
        geoTag();
        galleryAddPic();

        if (mCurrentLatLng != null) {
            new ScaledBitmapDecoder(this).execute(mCurrentPhotoPath);
            new FullBitmapDecoder(this).execute(mCurrentPhotoPath);
        }
    }

    private void geoTag() {
        Location loc = mapsView.getCurrentLocation();

        if (loc != null) {
            mCurrentLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());

            GeoTagger geoTagger = new GeoTagger();

            try {
                geoTagger.geoTagPhoto(mCurrentLatLng, mCurrentPhotoPath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            mapsView.showLocationError();
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);

        mapsView.broadcastToGallery(mediaScanIntent);
    }


    public void doneDecodingForRoot(Bitmap bitmap) {
        bitmap = bitmapOperator.fixRotation(bitmap, mCurrentPhotoPath);

        Log.d("Current LatLng", mCurrentLatLng.toString());
        Log.d("Current date", new Date().toString());
        mapsView.addNewMarker(bitmap, mCurrentLatLng);
        mapsView.hideProgress();
    }

    @Override
    public void doneDecodingForUpload(Bitmap bitmap) {
        bitmap = bitmapOperator.fixRotation(bitmap, mCurrentPhotoPath);

        mCurrentConvertedImage = bitmapOperator.bitmapToBase64(bitmap);

        uploadPhoto();
    }

    private void uploadPhoto() {
        if (mapsView.wifiAvailable()) {
            new UploadImageTask(this).execute(mCurrentConvertedImage);
        } else if (mapsView.mobileDataAvailable()) {
            mapsView.alertMobileData();
        } else {
            mapsView.alertNoConnection();
        }
    }

    @Override
    public void alertDialogAnswered(boolean answer) {
        if (answer) {
            new UploadImageTask(this).execute(mCurrentConvertedImage);
        }
    }

    @Override
    public void notifyToCopyUrlToClipboard(String url) {
        mapsView.copyToClipboard(url);
    }

    @Override
    public void notifyToShowConnectionError() {
        mapsView.alertNoConnection();
    }

    @Override
    public void notifyToShowProgressDialog(String uploadMessage) {
        mapsView.showProgressDialog(uploadMessage);
    }

    @Override
    public void notifyToDismissProgressDialog() {
        mapsView.dismissProgressDialog();
    }

    @Override
    public void asyncTaskDone(JSONObject jsonObject, RequestTask task) {
        if(task == RequestTask.GET_ACCESSABLE_EVENTS_TASK) {
            user.setAccessableEvents(createEventsFromJSON(jsonObject));

            downloadAccessableEventsRootPhotos();
        } else if(task == RequestTask.GET_OWN_EVENTS_TASK) {
            user.setOwnEvents(createEventsFromJSON(jsonObject));

        }
    }

    private ArrayList<Event> createEventsFromJSON(JSONObject jsonObject)  {
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

    private void downloadAccessableEventsRootPhotos() {
        downloadEvents(user.getAccessableEvents(), RequestTask.DOWNLOAD_ACCESSABLE_EVENTS_PHOTOS);
    }

    private void downloadEvents(ArrayList<Event> events, RequestTask task) {
        Photo [] photosToDownload = new Photo[events.size()];

        for (int i = 0; i < events.size(); i++) {
            photosToDownload[i] = events.get(i).getRootPhoto();
        }

        new DownloadImageTask(this, task).execute(photosToDownload);
    }

    @Override
    public void downloadDone(Photo[] photos, RequestTask task){
        if (task == RequestTask.DOWNLOAD_ACCESSABLE_EVENTS_PHOTOS) {
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

        user.copyCorrespondingAccEventsAsOwnEvents();
    }
}
