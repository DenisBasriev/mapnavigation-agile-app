package com.example.erman.photomapnavigation.operators;

import android.location.Location;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

/**
 * Created by erman on 04.12.2014.
 */
public class GeoTagger {

    public void geoTagPhoto(LatLng latLng, String imagePath) throws IOException{
        String [] latLotStr;
        latLotStr = getExifFormattedLoc(latLng);
        Log.d("Exif formatted LatLng", latLotStr[0] + ", " + latLotStr[1]);
        ExifInterface exif = new ExifInterface(imagePath);
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latLotStr[0]);
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latLng.latitude>0?"N":"S");
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, latLotStr[1]);
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, latLng.longitude>0?"E":"W");
        exif.saveAttributes();
    }

    private String[] getExifFormattedLoc(LatLng latLng) {
        String [] latLotStr = new String[2];
        latLotStr[0] = convertLocToExif(latLng.latitude);
        latLotStr[1] = convertLocToExif(latLng.longitude);

        return latLotStr;
    }

    private String convertLocToExif(double dimension) {
        double alat = Math.abs(dimension);

        String dms = Location.convert(alat, Location.FORMAT_SECONDS);
        String[] splits = dms.split(":");
        String[] secnds = (splits[2]).split("\\.");
        String seconds;

        if(secnds.length == 0) {
            seconds = splits[2];
        } else {
            seconds = secnds[0];
        }

        String dimensionStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";

        return dimensionStr;
    }
}
