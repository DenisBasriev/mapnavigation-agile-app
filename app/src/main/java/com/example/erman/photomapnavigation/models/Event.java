package com.example.erman.photomapnavigation.models;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Erman Yafay on 09.11.2014.
 */
public class Event {

    private int eventId;
    private Calendar createdDate, endDate;
    private String title, description, markerId;
    private Photo rootPhoto;
    private ArrayList<Photo> photos;
    private int userId;
    public boolean eventGoing;

    public Event(int eventId, int userId) {
        this.eventId = eventId;

        createdDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        endDate.add(Calendar.HOUR_OF_DAY, 1);
        title = "";
        description = "";

        photos = new ArrayList<Photo>();

        this.userId = userId;

        eventGoing = true;
    }

    //Getters

    public int getEventId() {
        return eventId;
    }
    public Calendar getCreatedDate() {
        return createdDate;
    }
    public Calendar getEndDate() {
        return endDate;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public Photo getRootPhoto() {
        return rootPhoto;
    }
    public ArrayList<Photo> getPhotos() {
        return photos;
    }
    public int getUserId() {
        return userId;
    }

    //Setters

    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setRootPhoto(Photo rootPhoto) {
        this.rootPhoto = rootPhoto;
    }

    public void addPhoto(Photo p) {
        if(eventGoing) {
            photos.add(p);
        }
    }

    @Override
    public String toString() {
        return eventId + ", " + userId;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }
}
