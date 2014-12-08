package com.example.erman.photomapnavigation.models;

import java.util.ArrayList;

/**
 * Created by Erman Yafay on 09.11.2014.
 */
public class SilverUser extends RegisteredUser {

    public final static int TOTAL_UPLOAD_LIMIT = 1000;
    public final static int PHOTO_LIFE = 1;
    public final static int INITIAL_PAYMENT = 10;
    public final static double PAYMENT_PER_PHOTO = 0.01;
    private int totalUploadNumber;
    private boolean uploadNumberExceed;

    public SilverUser(int userId, String email, String password, String firstName, String lastName, ArrayList<Event> accEvent, int totalUploadNumber, boolean uploadNumberExceed) {
        super(userId, email, password, firstName, lastName, accEvent);
        this.totalUploadNumber = totalUploadNumber;
        this.uploadNumberExceed = uploadNumberExceed;
    }

    public int getTotalUploadNumber() {
        return totalUploadNumber;
    }
    public boolean isUploadNumberExceed() {
        return uploadNumberExceed;
    }

    @Override
    public void createEvent(Event e) {
        super.createEvent(e);
        totalUploadNumber++;
    }
}
