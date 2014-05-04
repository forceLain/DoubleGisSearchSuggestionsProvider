package com.android.doublegissearch;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.doublegissearch.model.Profile;
import com.android.doublegissearch.network.UrlBuilder;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {


    private RequestQueue queue;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);

        queue = new Volley().newRequestQueue(this);

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(this);
        searchView.setSearchableInfo(info);
        return true;
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
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_LONG).show();
        }
    }

    private void searchProfile(String id, String hash) {
        Response.Listener<Profile> onSuccess = new Response.Listener<Profile>() {
            @Override
            public void onResponse(Profile profile) {
                ProfileAdapter adapter = new ProfileAdapter(MainActivity.this, profile);
                listView.setAdapter(adapter);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        };


        String url = new UrlBuilder().profile(id, hash).build();
        GsonRequest<Profile> request = new GsonRequest<Profile>(Request.Method.GET, url, Profile.class, onSuccess, errorListener);
        queue.add(request);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
