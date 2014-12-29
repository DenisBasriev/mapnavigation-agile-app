package com.example.erman.photomapnavigation.services;

import android.os.AsyncTask;
import android.util.Log;

import com.example.erman.photomapnavigation.Constants;
import com.example.erman.photomapnavigation.presenters.MapsPresenter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by erman on 07.12.2014.
 */
public class UploadImageTask extends AsyncTask<String, Void, JSONObject>{

    private MapsPresenter presenter;
    private static final String uploadMessage = "Uploading...\nClick on the map to continue";

    public UploadImageTask(MapsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onPreExecute() {
        presenter.notifyToShowProgressDialog(uploadMessage);
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Constants.UPLOAD_URL);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("image", strings[0]));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpPost.setHeader("Authorization", "Client-ID " + Constants.CLIENT_ID);
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(responseString);

            return json;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        if (jsonObject != null) {
            Log.d("JSON Response", jsonObject.toString());

            try {
                String url = getUrlFromJSON(jsonObject);

                presenter.notifyToCopyUrlToClipboard(url);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            presenter.notifyToShowConnectionError();
        }

        presenter.notifyToDismissProgressDialog();
    }

    private String getUrlFromJSON(JSONObject jsonObject) throws JSONException {
        String url = jsonObject.getJSONObject("data").getString("link");

        return url;
    }
}
