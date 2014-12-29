package com.example.erman.photomapnavigation.services;

import android.os.AsyncTask;

import com.example.erman.photomapnavigation.Constants;
import com.example.erman.photomapnavigation.presenters.MapsPresenter;

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
public class GetUsersPage extends AsyncTask<Integer, Void, JSONObject> {

    private MapsPresenter presenter;
    private static final String loadingMessage = "Loading Events...\n";

    public GetUsersPage(MapsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onPreExecute() {
        presenter.notifyToShowProgressDialog(loadingMessage);
    }

    @Override
    protected JSONObject doInBackground(Integer... integers) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(Constants.USER_PAGE + integers[0].toString() + ".json");

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
            presenter.eventsLoaded(jsonObject);
        } else {
            presenter.notifyToShowConnectionError();
        }

        presenter.notifyToDismissProgressDialog();
    }
}
