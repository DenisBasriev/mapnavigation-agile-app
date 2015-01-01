package com.example.erman.photomapnavigation.services;

import android.os.AsyncTask;

import com.example.erman.photomapnavigation.Constants;
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
    private static String givenTask;

    public GetRequest(Presenter presenter) {
        this.presenter = presenter;
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
        givenTask = strings[1];

        try {
            HttpResponse response = httpClient.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = new JSONObject(responseString);

            return jsonObject;
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
                presenter.asyncTaskDone(jsonObject, givenTask);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            presenter.notifyToShowConnectionError();
        }

        presenter.notifyToDismissProgressDialog();
    }
}
