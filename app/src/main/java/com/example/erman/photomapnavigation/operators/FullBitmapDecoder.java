package com.example.erman.photomapnavigation.operators;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.erman.photomapnavigation.presenters.MapsPresenter;

/**
 * Created by erman on 07.12.2014.
 */
public class FullBitmapDecoder extends AsyncTask<String, Void, Bitmap> {

    private MapsPresenter presenter;

    public FullBitmapDecoder(MapsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        return BitmapFactory.decodeFile(strings[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        presenter.doneDecodingForUpload(bitmap);
    }
}
