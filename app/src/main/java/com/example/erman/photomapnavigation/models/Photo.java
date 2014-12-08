package com.example.erman.photomapnavigation.models;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.Date;

/**
 * Created by Erman Yafay on 09.11.2014.
 */

public class Photo {

    private String url, title, description;
    private Date createdDate;
    private Bitmap source;
    private Location latLng;
    private int ownerEventId;

    public Photo(String url, Date createdDate, Bitmap source, Location latLng, int ownerEventId) {
        this.url = url;
        this.createdDate = createdDate;
        this.source = source;
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
    public Location getLatLng() {
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
}
