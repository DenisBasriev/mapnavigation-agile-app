package com.example.erman.photomapnavigation.models;

import java.util.ArrayList;

/**
 * Created by Erman Yafay on 09.11.2014.
 */
public class GoldUser extends RegisteredUser {

    public final static int PAYMENT_PER_PHOTO = 10;

    public GoldUser(int userId, String email, String password, String firstName, String lastName, ArrayList<Event> accEvent) {
        super(userId, email, password, firstName, lastName, accEvent);
    }
}
