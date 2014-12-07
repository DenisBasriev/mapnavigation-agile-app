package com.example.erman.photomapnavigation.operators;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by erman on 04.12.2014.
 */
public class FileOperator {

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    public File setUpPhotoFile() throws IOException {
        File file = createImageFile();

        return file;
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);

        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "myApp");

            if (storageDir != null) {
                if(!storageDir.mkdirs()) {
                    if(! storageDir.exists()) {
                        Log.d("myApp", "failed to create directory");

                        return null;
                    }
                }
            }
        } else {
            Log.v("Device Storage", "External storage is not mounted READ/WRITE");
        }

        return storageDir;
    }
}
