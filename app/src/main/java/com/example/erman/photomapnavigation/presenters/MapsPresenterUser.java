package com.example.erman.photomapnavigation.presenters;

import android.graphics.Bitmap;

/**
 * Created by erman on 05.01.2015.
 */
public interface MapsPresenterUser extends MapsPresenter {
    public void takePhoto();

    public void savePhoto(Integer resultCode);

    public void doneDecodingForRoot(Bitmap bitmap);

    public void doneDecodingForUpload(Bitmap bitmap);

    public void alertDialogAnswered(boolean answer);

    public void notifyToCopyUrlToClipboard(String url);
}
