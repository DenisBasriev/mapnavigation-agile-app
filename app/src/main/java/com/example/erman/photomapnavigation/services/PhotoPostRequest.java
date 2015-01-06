package com.example.erman.photomapnavigation.services;

import android.os.AsyncTask;
import android.util.Log;

import com.example.erman.photomapnavigation.Constants;
import com.example.erman.photomapnavigation.presenters.MapsPresenterUser;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by erman on 06.01.2015.
 */
public class PhotoPostRequest extends AsyncTask<JSONObject, Void, JSONObject> {

    private MapsPresenterUser presenter;
    private static String loadMessage = "Posting data...\n";

    public PhotoPostRequest(MapsPresenterUser presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onPreExecute() {
        presenter.notifyToShowProgressDialog(loadMessage);
    }

    @Override
    protected JSONObject doInBackground(JSONObject... jsonObjects) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Constants.PHOTOS_PAGE + Constants.JSON_PAGE_POSTFIX);

        try {
            StringEntity stringEntity = new StringEntity(jsonObjects[0].toString());
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = new JSONObject(responseString);
            return jsonObject;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception", "Exception in posting");
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (jsonObject != null) {
            Log.d("JSON response of post", jsonObject.toString());
            presenter.photoPosted(jsonObject);
        } else {
            presenter.notifyToShowConnectionError();
        }

        presenter.notifyToDismissProgressDialog();
    }
}
