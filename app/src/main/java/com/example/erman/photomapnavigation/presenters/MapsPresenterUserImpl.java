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
import com.example.erman.photomapnavigation.operators.GeoTagger;
import com.example.erman.photomapnavigation.operators.JsonModelConverter;
import com.example.erman.photomapnavigation.operators.ScaledBitmapDecoder;
import com.example.erman.photomapnavigation.services.DownloadImageTask;
import com.example.erman.photomapnavigation.services.GetRequest;
import com.example.erman.photomapnavigation.services.PhotoPostRequest;
import com.example.erman.photomapnavigation.services.UploadImageTask;
import com.example.erman.photomapnavigation.views.MapsView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by erman on 04.12.2014.
 */
public class MapsPresenterUserImpl implements MapsPresenterUser {

    private static int REQUEST_TAKE_PHOTO = 1;
    private MapsView mapsView;
    private String mCurrentPhotoPath;
    private String mCurrentConvertedImage;
    private String mConvertedRootImage;
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
            new ScaledBitmapDecoder(this, ScaledBitmapDecoder.BIG_PHOTO).execute(mCurrentPhotoPath);
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


    public void doneDecodingSmallPhoto(Bitmap bitmap) {
        bitmap = bitmapOperator.fixRotation(bitmap, mCurrentPhotoPath);

        mConvertedRootImage = bitmapOperator.bitmapToBase64(bitmap);

        mapsView.hideProgress();

        uploadPhotos();
    }

    @Override
    public void doneDecodingForBigPhoto(Bitmap bitmap) {
        bitmap = bitmapOperator.fixRotation(bitmap, mCurrentPhotoPath);

        mCurrentConvertedImage = bitmapOperator.bitmapToBase64(bitmap);

        new ScaledBitmapDecoder(this, ScaledBitmapDecoder.SMALL_PHOTO).execute(mCurrentPhotoPath);
    }

    private void uploadPhotos() {
        if (mapsView.wifiAvailable()) {
            String [] convertedImages = {mCurrentConvertedImage, mConvertedRootImage};
            new UploadImageTask(this).execute(convertedImages);
        } else if (mapsView.mobileDataAvailable()) {
            mapsView.alertMobileData();
        } else {
            mapsView.alertNoConnection();
        }
    }

    @Override
    public void doneUploadingPhotos(String[] urls) {
        Photo photoToPost = new Photo(urls[0], urls[1], mCurrentLatLng);
        JSONObject jsonObject = new JsonModelConverter().createJSONFromPhoto(photoToPost);
        new PhotoPostRequest(this).execute(jsonObject);
    }

    @Override
    public void photoPosted(JSONObject jsonObject) {
        Log.d("Photo posted", jsonObject.toString());

        try {
            int photoId = jsonObject.getInt("id");
            GetRequest request = new GetRequest(this);
            request.setLoadMessage(mapsView.getStringFromR(R.string.check_events));
            request.setTask(RequestTask.GET_PHOTO_TASK);
            request.execute(Constants.PHOTOS_PAGE + "/" + photoId + Constants.JSON_PAGE_POSTFIX);
        } catch (JSONException e) {
            Log.d("JSON Exception", "Cannot parse JSON");
        }
    }

    @Override
    public void alertDialogAnswered(boolean answer) {
        if (answer) {
            String [] photosToUpload = {mCurrentConvertedImage, mConvertedRootImage};
            new UploadImageTask(this).execute(photosToUpload);
        }
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
            user.setAccessableEvents(new JsonModelConverter().createEventsFromJSON(jsonObject));

            downloadAccessableEventsRootPhotos();
        } else if(task == RequestTask.GET_OWN_EVENTS_TASK) {
            user.setOwnEvents(new JsonModelConverter().createEventsFromJSON(jsonObject));

        } else if(task == RequestTask.GET_EVENTS_PHOTOS) {
            ArrayList<Photo> photos = new ArrayList<Photo>(new JsonModelConverter().createPhotosFromJSON(jsonObject));

            mapsView.sendPhotosToDisplayImages(photos);
        } else if(task == RequestTask.GET_PHOTO_TASK) {
            try {
                Log.d("JSON of get photo task", jsonObject.toString());
                int eventId = jsonObject.getJSONObject("photo").getInt("event_id");
                Log.d("is new event", String.valueOf(user.isNewEvent(eventId)));
                if(user.isNewEvent(eventId)) {
                    loadNewEvent(eventId);
                }
            } catch (JSONException e) {
                Log.d("JSON exception", "cannot parse json");
            }
        } else if(task == RequestTask.NEW_EVENT_TASK) {
            Log.d("JSON of new event task", jsonObject.toString());

            Event event = new JsonModelConverter().createEventFromJSON(jsonObject, user.getUserId());

            user.addAccessableEvent(event);
            user.addOwnEvent(event);

            downloadNewAccEventRootPhoto(event.getEventId());
        }
    }

    private void loadNewEvent(int eventId) {
        GetRequest request = new GetRequest(this);
        request.setLoadMessage(mapsView.getStringFromR(R.string.new_event_load_message));
        request.setTask(RequestTask.NEW_EVENT_TASK);
        request.execute(Constants.EVENTS_PAGE + "/" + eventId + Constants.JSON_PAGE_POSTFIX);
    }

    private void downloadNewAccEventRootPhoto(int eventId) {
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(user.findAccessableEventById(eventId));
        downloadEvents(events, RequestTask.DOWNLOAD_ACCESSABLE_EVENTS_ROOT_PHOTOS);
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
    public void downloadDone(Photo[] photos, RequestTask task){
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

        user.copyCorrespondingAccEventsAsOwnEvents();
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
}
