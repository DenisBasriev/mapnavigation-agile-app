package com.example.erman.photomapnavigation.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.erman.photomapnavigation.presenters.MapsPresenter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by erman on 31.12.2014.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

    private MapsPresenter presenter;
    private static final String loadMessage = "Downloading...\nClick on the map to continue!";
    private static String eventId;

    public DownloadImageTask(MapsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onPreExecute() {
        presenter.notifyToShowProgressDialog(loadMessage);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            eventId = strings[1];
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            InputStream input = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            input.close();

            return bitmap;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            presenter.downloadDone(bitmap, eventId);
        } else {
            presenter.notifyToShowConnectionError();
        }

        presenter.notifyToDismissProgressDialog();
    }
}
