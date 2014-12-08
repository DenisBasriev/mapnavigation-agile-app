package com.example.erman.photomapnavigation.operators;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.Environment;
import com.google.android.gms.maps.model.LatLng;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.FileOutputStream;

public class GeoTaggerTest extends TestCase {

    LatLng location;
    String imagePath;
    ExifInterface exif;
    GeoTagger geoTagger;
    File image;

    @Before
    public void setUp()
    {
        location = new LatLng(38.4183349609375, 27.128334045410156); // izmir clock tower
        image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.jpg");
        imagePath = image.getPath();
        geoTagger = new GeoTagger();
    }

    @Test
    public void testGeoTagPhoto() throws Exception
    {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, new FileOutputStream(image));
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
        exif = new ExifInterface(imagePath);
        geoTagger.geoTagPhoto(location,imagePath);
        ExifInterface exif = new ExifInterface(imagePath);
        float result [] = new float[2];
        exif.getLatLong(result);
        double dLat = (double)result[0];
        double dLong = (double)result[1];
        assertEquals(location.latitude, dLat);
        assertEquals(location.longitude, dLong);
    }

    @After
    public void tearDown()
    {
        image.delete();
    }
}