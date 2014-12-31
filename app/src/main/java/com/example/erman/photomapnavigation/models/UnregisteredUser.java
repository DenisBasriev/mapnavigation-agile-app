package com.example.erman.photomapnavigation.models;

import java.util.ArrayList;

/**
 * Created by Erman Yafay on 09.11.2014.
 */

public class UnregisteredUser
{
    private ArrayList<Event> accessableEvents;

    public ArrayList<Event> getAccessableEvents()
    {
        return accessableEvents;
    }

    public void setAccessableEvents(ArrayList<Event> accEvents) {
        accessableEvents = accEvents;
    }


}
