package com.example.erman.photomapnavigation.models;

import java.util.ArrayList;

/**
 * Created by Erman Yafay on 09.11.2014.
 */
public class RestrictedEvent extends PublicEvent {

    private ArrayList<RegisteredUser> accessors;

    public RestrictedEvent(int eventId, Photo rootPhoto, RegisteredUser creator) {
        super(eventId, rootPhoto, creator);
        accessors = new ArrayList<RegisteredUser>();
    }

    public ArrayList<RegisteredUser> getAccessors() {
        return accessors;
    }

    public void addAccessor(RegisteredUser user) {
        accessors.add(user);
    }
}
