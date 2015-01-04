package com.example.erman.photomapnavigation.models;

import java.util.ArrayList;

/**
 * Created by Erman Yafay on 09.11.2014.
 */

public class UnregisteredUser
{
    private ArrayList<Event> accessableEvents;

    public UnregisteredUser() {
        accessableEvents = new ArrayList<Event>();
    }

    public ArrayList<Event> getAccessableEvents()
    {
        return accessableEvents;
    }

    public void setAccessableEvents(ArrayList<Event> accEvents) {
        accessableEvents = accEvents;
    }

    public void addAccessableEvent(Event e) {
        accessableEvents.add(e);
    }

    public Event findAccessableEventById(int id) {
        for (Event e: accessableEvents) {
            if (e.getEventId() == id) {
                return e;
            }
        }

        return null;
    }

}
