package com.example.erman.photomapnavigation.views;

import android.graphics.Bitmap;

/**
 * Created by erman on 05.01.2015.
 */
public interface DisplayImageView {
    public void showProgressDialog(String message);

    public void dismissProgressDialog();

    public void alertNoConnection();

    public void displayPhotos(Bitmap[] sources);
}
