package com.example.erman.photomapnavigation.models;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by Erman Yafay on 09.11.2014.
 */

public class Photo {

    private String bigPhotoUrl, smallPhotoUrl, title;
    private Date createdDate;
    private Bitmap source;
    private LatLng latLng;
    private int ownerEventId;

    public Photo(String bigPhotoUrl, LatLng latLng, int ownerEventId) {
        this.bigPhotoUrl = bigPhotoUrl;
        this.latLng = latLng;
        this.ownerEventId = ownerEventId;

        title = "Hard-coded title";
        createdDate = new Date();
    }

    public Photo(String bigPhotoUrl, String smallPhotoUrl, LatLng latLng) {
        this.bigPhotoUrl = bigPhotoUrl;
        this.smallPhotoUrl = smallPhotoUrl;
        this.latLng = latLng;

        title = "Hard-coded title";
        createdDate = new Date();
    }

    //Getters

    public String getBigPhotoUrl() {
        return bigPhotoUrl;
    }
    public String getTitle() {
        return title;
    }

    public Date getCreatedDate() {
        return createdDate;
    }
    public Bitmap getSource() {
        return source;
    }
    public LatLng getLatLng() {
        return latLng;
    }
    public int getOwnerEventId() {
        return ownerEventId;
    }

    //Setters

    public void setTitle(String title) {
        this.title = title;
    }


    public void setSource(Bitmap source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return bigPhotoUrl + latLng.toString() + ownerEventId;
    }

    public String getSmallPhotoUrl() {
        return smallPhotoUrl;
    }
}
