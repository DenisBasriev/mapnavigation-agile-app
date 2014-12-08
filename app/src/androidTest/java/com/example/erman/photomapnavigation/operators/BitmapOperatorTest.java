package com.example.erman.photomapnavigation.operators;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.FileOutputStream;

public class BitmapOperatorTest extends TestCase
{
    String  imagePath;
    File image;
    BitmapOperator bitmapOperator;

    @Before
    public void setUp()
    {
        image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.jpg");
        imagePath = image.getPath();
        bitmapOperator = new BitmapOperator();
    }

    @Test
    public void testFixRotation() throws Exception
    {
        Matrix initialMatrix = new Matrix();
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, new FileOutputStream(image));
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);

        Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), initialMatrix, true);

        Matrix rotatedMatrix = new Matrix();
        rotatedMatrix.postRotate(90);
        Bitmap bitmapRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotatedMatrix, true);

        bitmapOperator.fixRotation(bitmap,imagePath);

        Matrix resultMatrix = new Matrix();
        Bitmap.createBitmap(bitmapRotated, 0, 0, bitmapRotated.getWidth(), bitmapRotated.getHeight(), resultMatrix, true);

        assertEquals(initialMatrix,resultMatrix);
    }

    @After
    public void tearDown() throws Exception
    {
        image.delete();
    }
}