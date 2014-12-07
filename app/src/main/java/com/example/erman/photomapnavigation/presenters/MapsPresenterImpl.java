package com.example.erman.photomapnavigation.presenters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import com.example.erman.photomapnavigation.operators.BitmapOperator;
import com.example.erman.photomapnavigation.operators.ScaledBitmapDecoder;
import com.example.erman.photomapnavigation.operators.FileOperator;
import com.example.erman.photomapnavigation.operators.FullBitmapDecoder;
import com.example.erman.photomapnavigation.operators.GeoTagger;
import com.example.erman.photomapnavigation.services.UploadImageTask;
import com.example.erman.photomapnavigation.views.MapsView;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;

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

    public MapsPresenterImpl() {
        mCurrentPhotoPath = null;
        mCurrentConvertedImage = null;
        bitmapOperator = new BitmapOperator();
    }

    @Override
    public void setView(MapsView mapsView) { this.mapsView = mapsView; }

    @Override
    public void setUpMap(String username) {
        mapsView.showProgress();
        mapsView.enableUserLocation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mapsView.setCameraToCurrentLocation();
                mapsView.hideProgress();
                mapsView.enableActionTakePicture();
            }
        }, 10000);

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
        }
    }

    private void handlePhoto() {
        geoTag();
        galleryAddPic();

        new ScaledBitmapDecoder(this).execute(mCurrentPhotoPath);
        new FullBitmapDecoder(this).execute(mCurrentPhotoPath);
    }

    private void geoTag() {
        mCurrentLatLng = mapsView.getCurrentLocation();

        GeoTagger geoTagger = new GeoTagger();

        try {
            geoTagger.geoTagPhoto(mCurrentLatLng, mCurrentPhotoPath);
        } catch (IOException ex) {
            ex.printStackTrace();
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

        mapsView.addNewMarker(bitmap, mCurrentLatLng);
        mapsView.hideProgress();
    }

    @Override
    public void doneDecodingForUpload(Bitmap bitmap) {
        mCurrentConvertedImage = bitmapOperator.bitmapToString(bitmap);

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
    public void notifyToShowProgressDialog(String uploadMessage) {
        mapsView.showProgressDialog(uploadMessage);
    }

    @Override
    public void notifyToDismissProgressDialog() {
        mapsView.dismissProgressDialog();
    }
}
