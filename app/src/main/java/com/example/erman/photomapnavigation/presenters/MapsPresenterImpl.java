package com.example.erman.photomapnavigation.presenters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.erman.photomapnavigation.operators.BitmapOperator;
import com.example.erman.photomapnavigation.operators.ScaledBitmapDecoder;
import com.example.erman.photomapnavigation.operators.FileOperator;
import com.example.erman.photomapnavigation.operators.FullBitmapDecoder;
import com.example.erman.photomapnavigation.operators.GeoTagger;
import com.example.erman.photomapnavigation.services.GetUsersPage;
import com.example.erman.photomapnavigation.services.UploadImageTask;
import com.example.erman.photomapnavigation.views.MapsView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by erman on 04.12.2014.
 */
public class MapsPresenterImpl implements MapsPresenter {

    private static int REQUEST_TAKE_PHOTO = 1;
    private MapsView mapsView;
    private String mCurrentPhotoPath;
    private String mCurrentConvertedImage;
    private LatLng mCurrentLatLng;
    private BitmapOperator bitmapOperator;
    public static final String SETUP_MAP_PROGRESS_DIALOG_MESSAGE = "Setting Up Map...";

    public MapsPresenterImpl() {
        mCurrentPhotoPath = null;
        mCurrentConvertedImage = null;
        bitmapOperator = new BitmapOperator();
    }

    @Override
    public void setView(MapsView mapsView) { this.mapsView = mapsView; }

    @Override
    public void setUpMap(String username, final boolean isRegistered) {
        mapsView.showNonCancellableProgressDialog(SETUP_MAP_PROGRESS_DIALOG_MESSAGE);
        mapsView.enableUserLocation();
        mapsView.setCameraToCurrentLocation();
        mapsView.dismissProgressDialog();
        new GetUsersPage(this).execute(2);
    }

    @Override
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
    public void signUp() {
        mapsView.navigateToSignUp();
    }

    @Override
    public void eventsLoaded(JSONObject jsonObject) {
        if (jsonObject != null) {
            Log.d("Loaded Events", jsonObject.toString());
        } else {
            Log.d("Loaded Events", "NULL");
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
}
