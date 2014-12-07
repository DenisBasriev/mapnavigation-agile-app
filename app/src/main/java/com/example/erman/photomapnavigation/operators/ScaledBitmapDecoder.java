package com.example.erman.photomapnavigation.operators;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.erman.photomapnavigation.presenters.MapsPresenter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by erman on 07.12.2014.
 */
public class ScaledBitmapDecoder extends AsyncTask<String, Void, Bitmap> {

    private static final int REQ_WIDTH = 100;
    private static final int REQ_HEIGHT = 100;
    private MapsPresenter presenter;

    public ScaledBitmapDecoder(MapsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        return decodeSampledBitmapFromFile(strings[0], REQ_WIDTH, REQ_HEIGHT);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        presenter.doneDecodingForRoot(bitmap);
    }

    private Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        
        return BitmapFactory.decodeFile(filePath, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}
