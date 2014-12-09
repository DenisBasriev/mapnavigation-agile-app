package com.example.erman.photomapnavigation.presenters;

import android.graphics.Bitmap;

import com.example.erman.photomapnavigation.views.MapsView;

/**
 * Created by erman on 04.12.2014.
 */
public interface MapsPresenter {
    public void takePhoto();
    public void savePhoto(Integer resultCode);
    public void setUpMap(String username, boolean isRegistered);
    public void setView(MapsView mapsView);
    public void notifyToShowProgressDialog(String uploadMessage);
    public void notifyToDismissProgressDialog();
    public void doneDecodingForRoot(Bitmap bitmap);
    public void doneDecodingForUpload(Bitmap bitmap);
    public void alertDialogAnswered(boolean answer);
    public void notifyToCopyUrlToClipboard(String url);
    public void signUp();
}
