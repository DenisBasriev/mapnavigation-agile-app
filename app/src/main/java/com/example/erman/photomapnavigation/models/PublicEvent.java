package com.example.erman.photomapnavigation.models;

import java.util.ArrayList;

/**
 * Created by Erman Yafay on 09.11.2014.
 */
public class PublicEvent extends Event {

    private ArrayList<RegisteredUser> contributers;

    public PublicEvent(int eventId, Photo rootPhoto, RegisteredUser creator) {
        super(eventId, rootPhoto, creator);

        contributers = new ArrayList<RegisteredUser>();
    }

    public ArrayList<RegisteredUser> getContributers() {
        return contributers;
    }

    public void addContributer(RegisteredUser user) {
        contributers.add(user);
    }
}
