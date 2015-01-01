package com.example.erman.photomapnavigation.models;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by Erman Yafay on 09.11.2014.
 */

public class Photo {

    private String url, title, description;
    private Date createdDate;
    private Bitmap source;
    private LatLng latLng;
    private int ownerEventId;

    public Photo(String url, LatLng latLng, int ownerEventId) {
        this.url = url;
        this.latLng = latLng;
        this.ownerEventId = ownerEventId;

        title = "";
        description = "";
    }

    //Getters

    public String getUrl() {
        return url;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
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
    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setSource(Bitmap source) {
        this.source = source;
    }
}
