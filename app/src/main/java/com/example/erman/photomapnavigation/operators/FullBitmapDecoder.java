package com.example.erman.photomapnavigation.operators;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.erman.photomapnavigation.presenters.MapsPresenter;
import com.example.erman.photomapnavigation.presenters.MapsPresenterUser;

/**
 * Created by erman on 07.12.2014.
 */
public class FullBitmapDecoder extends AsyncTask<String, Void, Bitmap> {

    private MapsPresenterUser presenter;

    public FullBitmapDecoder(MapsPresenterUser presenter) {
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
