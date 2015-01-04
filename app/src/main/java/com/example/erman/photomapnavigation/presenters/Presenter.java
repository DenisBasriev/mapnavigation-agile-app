package com.example.erman.photomapnavigation.presenters;

import com.example.erman.photomapnavigation.RequestTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by erman on 31.12.2014.
 */
public interface Presenter {
    public void notifyToShowProgressDialog(String message);
    public void notifyToDismissProgressDialog();
    public void asyncTaskDone(JSONObject jsonObject, RequestTask task) throws JSONException;
    public void notifyToShowConnectionError();
}
