package com.example.erman.photomapnavigation.models;

import java.util.ArrayList;

/**
 * Created by Erman Yafay on 09.11.2014.
 */

public class UnregisteredUser
{
    private ArrayList<Event> accessableEvents;

    public UnregisteredUser(ArrayList<Event> accEvent)
    {
        accessableEvents = new ArrayList<Event>(accEvent);
    }

    public ArrayList<Event> getAccessableEvents()
    {
        return accessableEvents;
    }

    public void accessEvent(Event e)
    {
        accessableEvents.add(e);
    }
}
