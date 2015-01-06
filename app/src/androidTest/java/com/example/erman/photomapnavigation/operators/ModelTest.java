package com.example.erman.photomapnavigation.operators;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import com.example.erman.photomapnavigation.models.Event;
import com.example.erman.photomapnavigation.models.Photo;
import com.example.erman.photomapnavigation.models.RegisteredUser;
import com.example.erman.photomapnavigation.models.UnregisteredUser;
import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ModelTest extends TestCase
{
    RegisteredUser registeredUser;
    UnregisteredUser unregisteredUser;
    Event ownEvent;
    Photo rootPhoto;

    Event accEvent;
    Photo otherRootPhoto;

    @Before
    public void setUp()
    {
        String url = "Test URL";
        double testLat = 10.5;
        double testLng = 5.5;
        LatLng latLng = new LatLng(testLat,testLng);
        int eventid = 2;
        int userid = 5;
        String firstname = "Test";
        String lastname = "User";
        String email = "test@email.com";
        String markerid = "4";
        Bitmap source = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(source);
        canvas.drawColor(Color.BLACK);

        registeredUser = new RegisteredUser(userid,email,firstname,lastname);
        ownEvent = new Event(eventid,userid);
        rootPhoto = new Photo(url,latLng,eventid);
        ownEvent.setMarkerId(markerid);
        rootPhoto.setSource(source);
        ownEvent.setRootPhoto(rootPhoto);
        registeredUser.addOwnEvent(ownEvent);

        otherRootPhoto = rootPhoto;
        Bitmap otherSource = rootPhoto.getSource();
        Canvas otherCanvas = new Canvas(otherSource);
        otherCanvas.drawColor(Color.BLUE);
        otherRootPhoto.setSource(otherSource);

        String otherMarkerid = "15";
        accEvent = ownEvent;
        accEvent.setMarkerId(otherMarkerid);
        registeredUser.addAccessableEvent(accEvent);

        registeredUser.copyCorrespondingAccEventsAsOwnEvents();
    }

    @Test
    public void testOwnEventRootPhotoIsSameAccesableEventRootPhoto() throws Exception
    {
        assertEquals(registeredUser.findAccessableEventById(accEvent.getEventId()).getRootPhoto(), registeredUser.findOwnEventById(ownEvent.getEventId()).getRootPhoto());
    }

    @Test
    public void testOwnEventMarkerIdIsSameAccesableEventMarkerId() throws Exception
    {
        assertEquals(registeredUser.findAccessableEventById(accEvent.getEventId()).getMarkerId(), registeredUser.findOwnEventById(ownEvent.getEventId()).getMarkerId());
    }

    @After
    public void tearDown()
    {

    }
}
