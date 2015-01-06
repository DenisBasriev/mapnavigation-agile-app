package com.example.erman.photomapnavigation.presenters;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.erman.photomapnavigation.RequestTask;
import com.example.erman.photomapnavigation.models.Photo;
import com.example.erman.photomapnavigation.operators.BitmapOperator;
import com.example.erman.photomapnavigation.services.DownloadImageTask;
import com.example.erman.photomapnavigation.views.DisplayImageView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by erman on 05.01.2015.
 */
public class DisplayImagePresenterImpl implements DisplayImagePresenter{

    private DisplayImageView displayImageView;


    public DisplayImagePresenterImpl(DisplayImageView displayImageView) {
        this.displayImageView = displayImageView;
    }

    @Override
    public void notifyToShowProgressDialog(String message) {
        displayImageView.showProgressDialog(message);
    }

    @Override
    public void notifyToDismissProgressDialog() {
        displayImageView.dismissProgressDialog();
    }

    @Override
    public void downloadDone(Photo[] photos, RequestTask task) {
        if (task == RequestTask.DOWNLOAD_EVENTS_PHOTOS) {
            int size = photos.length;

            Bitmap [] sources = new Bitmap[size];

            for (int i = 0; i < size; i++) {
                sources[i] = photos[i].getSource();
            }

            displayImageView.displayPhotos(sources);
        }
    }

    @Override
    public void asyncTaskDone(JSONObject jsonObject, RequestTask task) throws JSONException {

    }

    @Override
    public void notifyToShowConnectionError() {
        displayImageView.alertNoConnection();
    }

    @Override
    public void downloadPhotos(Bundle bundle) {
        String [] urls = bundle.getStringArray("urls");

        int size = urls.length;
        double [] lats = bundle.getDoubleArray("lats");
        double [] longts = bundle.getDoubleArray("longts");
        int [] eventIds = bundle.getIntArray("eventIds");

        Photo[] photosToDownload = new Photo[size];

        for (int i = 0; i < size; i++) {
            Photo photo = new Photo(urls[i], new LatLng(lats[i], longts[i]), eventIds[i]);
            photosToDownload[i] = photo;
        }

        DownloadImageTask downloadImageTask = new DownloadImageTask(this, RequestTask.DOWNLOAD_EVENTS_PHOTOS);
        downloadImageTask.execute(photosToDownload);
    }
}
