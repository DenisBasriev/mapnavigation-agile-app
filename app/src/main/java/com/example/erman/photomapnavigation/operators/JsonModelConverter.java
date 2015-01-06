package com.example.erman.photomapnavigation.operators;

import android.util.Log;

import com.example.erman.photomapnavigation.models.Event;
import com.example.erman.photomapnavigation.models.Photo;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by erman on 06.01.2015.
 */
public class JsonModelConverter {

    public ArrayList<Event> createEventsFromJSON(JSONObject jsonObject)  {
        ArrayList<Event> events = new ArrayList<Event>();
        JSONArray jsonArray = null;

        try {
            jsonArray = jsonObject.getJSONArray("events");
        } catch (JSONException e) {
            Log.d("JSON Exception", "Cannot get json array");
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject arrayElement = jsonArray.getJSONObject(i);
                Log.d("Got json object", arrayElement.toString());
                Event event = new Event(arrayElement.getInt("id"), arrayElement.getInt("user_id"));
                Log.d("events", event.toString());
                LatLng latLng = new LatLng(arrayElement.getDouble("latitude"), arrayElement.getDouble("longitude"));
                Log.d("latlngs", latLng.toString());
                Photo photo = new Photo(arrayElement.getString("thumbnail"), latLng, event.getEventId());
                Log.d("photos", photo.toString());
                event.setRootPhoto(photo);
                events.add(event);
                Log.d("event added", "event added");
            } catch (JSONException e) {
                Log.d("JSON exception", "Cannot parse json array's element");
            }
        }

        return events;
    }

    public ArrayList<Photo> createPhotosFromJSON(JSONObject jsonObject) {
        ArrayList<Photo> photos = new ArrayList<Photo>();
        JSONArray jsonArray = null;

        try {
            jsonArray = jsonObject.getJSONArray("photos");
        } catch (JSONException e) {
            Log.d("JSON Exception", "Cannot get json array");
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject arrayElement = jsonArray.getJSONObject(i);
                Log.d("Got json object", arrayElement.toString());
                LatLng latLng = new LatLng(arrayElement.getDouble("latitude"), arrayElement.getDouble("longitude"));
                Photo photo = new Photo(arrayElement.getString("url"), latLng, arrayElement.getInt("event_id"));
                photos.add(photo);
            } catch (JSONException e) {
                Log.d("JSON exception", "Cannot parse json array's element");
            }
        }

        return photos;
    }

    public Event createEventFromJSON(JSONObject jsonObject, int userId) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("photos");
            JSONObject firstArrayElement = jsonArray.getJSONObject(0);
            Event event = new Event(firstArrayElement.getInt("event_id"), userId);
            LatLng latLng = new LatLng(firstArrayElement.getDouble("latitude"), firstArrayElement.getDouble("longitude"));
            Photo photo = new Photo(firstArrayElement.getString("thumbnail"), latLng, event.getEventId());
            event.setRootPhoto(photo);
            return event;
        } catch (JSONException e) {
            Log.d("JSON Exception", "Cannot parse JSON");
            return null;
        }
    }

    public JSONObject createJSONFromPhoto(Photo photoToPost) {
        JSONObject innerObject = new JSONObject();
        JSONObject outerObject = new JSONObject();

        try
        {
            innerObject.put("url", photoToPost.getBigPhotoUrl());
            innerObject.put("thumbnail", photoToPost.getSmallPhotoUrl());
            innerObject.put("title", photoToPost.getTitle());
            innerObject.put("latitude", String.valueOf(photoToPost.getLatLng().latitude));
            innerObject.put("longitude", String.valueOf(photoToPost.getLatLng().longitude));
            innerObject.put("createdDate", String.valueOf(photoToPost.getCreatedDate()));
            outerObject.put("photo", innerObject);
        } catch (JSONException e) {
            Log.d("JSON Exception", "Cannot create JSON");
        }

        return outerObject;
    }
}
