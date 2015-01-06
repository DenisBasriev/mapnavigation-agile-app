package com.example.erman.photomapnavigation.services;

import android.os.AsyncTask;
import android.util.Log;

import com.example.erman.photomapnavigation.Constants;
import com.example.erman.photomapnavigation.presenters.MapsPresenter;
import com.example.erman.photomapnavigation.presenters.MapsPresenterUser;

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
public class UploadImageTask extends AsyncTask<String, Void, JSONObject[]>{

    private MapsPresenterUser presenter;
    private static final String uploadMessage = "Uploading...\nClick on the map to continue";

    public UploadImageTask(MapsPresenterUser presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onPreExecute() {
        presenter.notifyToShowProgressDialog(uploadMessage);
    }

    @Override
    protected JSONObject[] doInBackground(String... strings) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Constants.UPLOAD_URL);
        JSONObject [] jsonObjects = new JSONObject[strings.length];

        for (int i = 0; i < 2; i++) {

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("image", strings[i]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpPost.setHeader("Authorization", "Client-ID " + Constants.CLIENT_ID);
                HttpResponse response = httpClient.execute(httpPost);
                String responseString = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(responseString);
                jsonObjects[i] = json;

            } catch (Exception ex) {
                Log.d("Upload Error", "Couldn't upload photo " + i);
            }
        }

        return jsonObjects;
    }

    @Override
    protected void onPostExecute(JSONObject [] jsonObjects) {

       if (jsonObjects != null) {
           Log.d("Big photo", jsonObjects[0].toString());
           Log.d("Small photo", jsonObjects[1].toString());
           String [] urls = new String[2];
           try{
               urls[0] = getUrlFromJSON(jsonObjects[0]);
               urls[1] = getUrlFromJSON(jsonObjects[1]);
               presenter.doneUploadingPhotos(urls);
           } catch (JSONException e) {
               Log.d("JSON Exception", "Cannot parse json");
           }
       }

       presenter.notifyToDismissProgressDialog();
    }

    private String getUrlFromJSON(JSONObject jsonObject) throws JSONException {
        String url = jsonObject.getJSONObject("data").getString("link");

        return url;
    }
}
