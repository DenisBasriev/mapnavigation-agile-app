package com.example.erman.photomapnavigation.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.erman.photomapnavigation.RequestTask;
import com.example.erman.photomapnavigation.models.Photo;
import com.example.erman.photomapnavigation.presenters.MapsPresenter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by erman on 31.12.2014.
 */
public class DownloadImageTask extends AsyncTask<Photo, Void, Photo[]>{

    private MapsPresenter presenter;
    private static final String loadMessage = "Downloading...\nClick on the map to continue!";
    private RequestTask task;

    public DownloadImageTask(MapsPresenter presenter, RequestTask task) {
        this.presenter = presenter;
        this.task = task;
    }

    @Override
    protected void onPreExecute() {
        presenter.notifyToShowProgressDialog(loadMessage);
        Log.d("Pre execute", "run");
    }

    @Override
    protected Photo [] doInBackground(Photo... photos) {
        for (Photo p : photos) {
            try {
                URL url = new URL(p.getUrl());
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.connect();
                InputStream input = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                p.setSource(bitmap);

            } catch (Exception e) {
                Log.d("Download error", "Download error");
            }
        }

        return photos;
    }

    @Override
    protected void onPostExecute(Photo[] photos) {
        if (photos != null) {
            presenter.downloadDone(photos, task);
        } else {
            presenter.notifyToShowConnectionError();
        }

        presenter.notifyToDismissProgressDialog();
    }
}
