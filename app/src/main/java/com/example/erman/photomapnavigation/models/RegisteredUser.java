package com.example.erman.photomapnavigation.models;

import java.util.ArrayList;

/**
 * Created by Erman Yafay on 09.11.2014.
 */
public class RegisteredUser extends UnregisteredUser {

    //Fields
    private int userId;
    private String email;
    private String firstName;
    private String lastName;
    private ArrayList<Event> ownEvents;

    //Constructor
    public RegisteredUser(int userId, String email, String firstName, String lastName) {
        super();
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;

        ownEvents = new ArrayList<Event>();
    }

    //Getters
    public int getUserId() {
        return userId;
    }
    public String getEmail() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public ArrayList<Event> getOwnEvents() {
        return ownEvents;
    }

    //Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setOwnEvents(ArrayList<Event> ownEvents) {
        this.ownEvents = ownEvents;
    }

    public void addOwnEvent(Event event) {
        ownEvents.add(event);
    }

    public void copyCorrespondingAccEventsAsOwnEvents() {
        for (Event e : ownEvents) {
            Event accEvent = findAccessableEventById(e.getEventId());
            e.getRootPhoto().setSource(accEvent.getRootPhoto().getSource());
            e.setMarkerId(accEvent.getMarkerId());
        }
    }

    public Event findOwnEventById(int id) {
        for (Event e : ownEvents) {
            if (e.getEventId() == id) {
                return e;
            }
        }

        return null;
    }

    public boolean isNewEvent(int eventId) {
        for(Event e : ownEvents) {
            if(eventId == e.getEventId()) {
                return false;
            }
        }

        return true;
    }
}



