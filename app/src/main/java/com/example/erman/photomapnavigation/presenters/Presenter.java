package com.example.erman.photomapnavigation.presenters;

import com.example.erman.photomapnavigation.RequestTask;
import com.example.erman.photomapnavigation.models.Photo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by erman on 31.12.2014.
 */
public interface Presenter {
    public void notifyToShowProgressDialog(String message);
    public void notifyToDismissProgressDialog();
    public void downloadDone(Photo[] photos, RequestTask task);
    public void notifyToShowConnectionError();
    public void asyncTaskDone(JSONObject jsonObject, RequestTask task) throws JSONException;
}
