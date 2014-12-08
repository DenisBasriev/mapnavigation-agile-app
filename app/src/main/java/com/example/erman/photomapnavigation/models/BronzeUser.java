package com.example.erman.photomapnavigation.models;

import java.util.ArrayList;

/**
 * Created by Erman Yafay on 09.11.2014.
 */
public class BronzeUser extends RegisteredUser {

    public static final int DAILY_UPLOAD_LIMIT = 10;
    public static final int PHOTO_LIFE = 1;
    private int dailyUpload;
    private boolean uploadNumberExceed;

    public BronzeUser(int userId, String email, String password, String firstName, String lastName, ArrayList<Event> accEvent, int dailyUpload, boolean uploadNumberExceed) {
        super(userId, email, password, firstName, lastName, accEvent);
        this.dailyUpload = dailyUpload;
        this.uploadNumberExceed = uploadNumberExceed;
    }

    @Override
    public void createEvent(Event e) {
        super.createEvent(e);
        dailyUpload++;
    }

    public int getDailyUpload() {
        return dailyUpload;
    }
    public boolean isUploadNumberExceed() {
        return uploadNumberExceed;
    }

}
