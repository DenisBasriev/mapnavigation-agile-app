package com.example.erman.photomapnavigation.presenters;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by erman on 31.12.2014.
 */
public interface Presenter {
    public void notifyToShowProgressDialog(String message);
    public void notifyToDismissProgressDialog();
    public void asyncTaskDone(JSONObject jsonObject, String givenTask) throws JSONException;
    public void notifyToShowConnectionError();
}
