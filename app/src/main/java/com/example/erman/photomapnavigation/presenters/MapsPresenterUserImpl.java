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

/**
 * Created by erman on 04.12.2014.
 */
public class MapsPresenterUserImpl implements MapsPresenter {

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
        //loadAccessableEvents();
        loadOwnEvents();
    }

    private void downloadOwnEventsRootPhotos() {
        ArrayList<Event> events = user.getOwnEvents();
        for (int i = 0; i < events.size(); i++) {
            String [] downloadArgs = new String[2];
            downloadArgs[0] = events.get(i).getRootPhoto().getUrl();
            downloadArgs[1] = String.valueOf(events.get(i).getEventId());
            new DownloadImageTask(this).execute(downloadArgs);
        }
    }

    private void searchForLocation() {
        mapsView.showNonCancellableProgressDialog(mapsView.getStringFromR(R.string.search_location_message));
        mapsView.enableUserLocation();
        mapsView.setCameraToCurrentLocation();
        mapsView.dismissProgressDialog();
    }

    private void loadAccessableEvents() {
        /*GetRequest request = new GetRequest(this);
        request.setLoadMessage(mapsView.getStringFromR(R.string.event_load_message));
        request.execute(Constants.EVENTS_PAGE + Constants.JSON_PAGE_POSTFIX);*/

        //This part is currently hard-coded. We will changed it to how it is supposed to be after the event.json page opened for get request.
    }

    private void loadOwnEvents() {
        GetRequest request =  new GetRequest(this);
        request.setLoadMessage(mapsView.getStringFromR(R.string.event_load_message));
        String [] requestArgs = new String[2];
        requestArgs[0] = Constants.EVENT_USER_PAGE + user.getUserId() + Constants.JSON_PAGE_POSTFIX;
        requestArgs[1] = String.valueOf(RequestTask.GET_OWN_EVENTS_TASK);
        request.execute(requestArgs);
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

    @Override
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
    public void signUp() {}

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
    public void asyncTaskDone(JSONObject jsonObject, String givenTask) throws JSONException {
        if(givenTask.equals(Constants.EVENTS_PAGE)) {

        } else if(givenTask.equals(String.valueOf(RequestTask.GET_OWN_EVENTS_TASK))) {
            JSONArray jsonArray = jsonObject.getJSONArray("events");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject arrayElement = jsonArray.getJSONObject(i);
                Event event = new Event(arrayElement.getInt("id"), user);
                LatLng latLng = new LatLng(Double.valueOf(arrayElement.getString("latitude")), Double.valueOf(arrayElement.getString("longitude")));
                Photo rootPhoto = new Photo(arrayElement.getString("thumbnail"), latLng, event.getEventId());
                event.setRootPhoto(rootPhoto);
                user.addOwnEvent(event);
            }
            downloadOwnEventsRootPhotos();
        }
    }

    @Override
    public void downloadDone(Bitmap bitmap, String eventId) {
        ArrayList<Event> events = user.getOwnEvents();
        for (Event e: events) {
            if (e.getEventId() == Integer.valueOf(eventId)) {
                e.getRootPhoto().setSource(bitmap);
                mapsView.addNewMarker(bitmap, e.getRootPhoto().getLatLng());
            }
        }
    }
}
