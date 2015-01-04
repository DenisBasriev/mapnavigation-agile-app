package com.example.erman.photomapnavigation.services;

import android.os.AsyncTask;

import com.example.erman.photomapnavigation.Constants;
import com.example.erman.photomapnavigation.RequestTask;
import com.example.erman.photomapnavigation.presenters.Presenter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by erman on 29.12.2014.
 */
public class GetRequest extends AsyncTask<String, Void, JSONObject> {

    private Presenter presenter;
    private static String loadMessage = "Loading...";
    private RequestTask task;

    public GetRequest(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setTask(RequestTask task) {
        this.task = task;
    }

    public void setLoadMessage(String message) {
        loadMessage = message;
    }

    @Override
    protected void onPreExecute() {
        presenter.notifyToShowProgressDialog(loadMessage);
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(strings[0]);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity());

            return new JSONObject(responseString);
        } catch (IOException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        if (jsonObject != null) {
            try {
                presenter.asyncTaskDone(jsonObject, task);
            } catch (JSONException e) {
                presenter.notifyToShowConnectionError();

            }
        } else {
            presenter.notifyToShowConnectionError();
            presenter.notifyToDismissProgressDialog();
        }

        presenter.notifyToDismissProgressDialog();
    }
}
