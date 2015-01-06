package com.example.erman.photomapnavigation.presenters;

import android.graphics.Bitmap;

import org.json.JSONObject;

/**
 * Created by erman on 05.01.2015.
 */
public interface MapsPresenterUser extends MapsPresenter {
    public void takePhoto();

    public void savePhoto(Integer resultCode);

    public void doneDecodingSmallPhoto(Bitmap bitmap);

    public void doneDecodingForBigPhoto(Bitmap bitmap);

    public void alertDialogAnswered(boolean answer);

    public void doneUploadingPhotos(String[] urls);

    public void photoPosted(JSONObject jsonObject);
}
