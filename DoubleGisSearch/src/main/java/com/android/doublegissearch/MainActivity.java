package com.android.doublegissearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.android.doublegissearch.model.Profile;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;


public class MainActivity extends ActionBarActivity {


    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = new Volley().newRequestQueue(this);

        handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String lastPathSegment = uri.getLastPathSegment();
            if (lastPathSegment != null){
                String[] split = lastPathSegment.split("_");
                if (split.length == 2){
                    searchProfile(split[0], split[1]);
                }
            }
        }
    }

    private void searchProfile(String id, String hash) {
        Response.Listener<Profile> onSuccess = new Response.Listener<Profile>() {
            @Override
            public void onResponse(Profile profile) {
                TextView textView = (TextView) findViewById(R.id.text);
                textView.setText(profile.name+"\n"+profile.address+"\n"+ Arrays.toString(profile.rubrics));
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        };


        String url = "http://catalog.api.2gis.ru/profile?&version=1.3&key=rumobc0685&id="+id+"&hash="+hash;
        GsonRequest<Profile> request = new GsonRequest<Profile>(Request.Method.GET, url, Profile.class, onSuccess, errorListener);
        queue.add(request);
    }

}
