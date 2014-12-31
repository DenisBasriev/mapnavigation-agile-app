package com.example.erman.photomapnavigation.presenters;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.erman.photomapnavigation.views.MapsView;

/**
 * Created by erman on 31.12.2014.
 */
public interface MapsPresenter extends Presenter{



    public void setView(MapsView mapsView);

    public void setUpMap();

    public void takePhoto();

    public void savePhoto(Integer resultCode);

    public void doneDecodingForRoot(Bitmap bitmap);

    public void doneDecodingForUpload(Bitmap bitmap);

    public void alertDialogAnswered(boolean answer);

    public void notifyToCopyUrlToClipboard(String url);

    public void signUp();

    public void signedIn(Bundle extras);
}
