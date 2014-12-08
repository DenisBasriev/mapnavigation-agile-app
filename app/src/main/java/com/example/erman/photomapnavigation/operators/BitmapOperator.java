package com.example.erman.photomapnavigation.operators;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by erman on 07.12.2014.
 */

public class BitmapOperator {

    public Bitmap fixRotation(Bitmap bmp, String filePath) {
        Matrix matrix = new Matrix();

        try {
            ExifInterface exif = new ExifInterface(filePath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (rotation) {
                case ExifInterface.ORIENTATION_NORMAL: {} break;
                case ExifInterface.ORIENTATION_ROTATE_90: {
                    matrix.postRotate(90);
                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                } break;
                case ExifInterface.ORIENTATION_ROTATE_180: {
                    matrix.postRotate(180);
                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                } break;
                case ExifInterface.ORIENTATION_ROTATE_270: {
                    matrix.postRotate(270);
                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                } break;
            }
        } catch (IOException ex) {
            ex.printStackTrace();

        }
        return bmp;
    }

    public String bitmapToString(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);

        byte [] b = baos.toByteArray();

        String temp = Base64.encodeToString(b, Base64.DEFAULT);

        return temp;
    }
}
