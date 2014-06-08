package com.android.doublegissearch;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.doublegissearch.fragment.FirmListFragment;
import com.android.doublegissearch.fragment.ProfileFragment;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, VolleyActivity {

    private RequestQueue queue;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fillWelcomeContainer();
        queue = new Volley().newRequestQueue(this);
        handleIntent(getIntent());
    }

    private void fillWelcomeContainer() {
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.welcome_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        addView(inflater, viewGroup, "Такси", R.drawable.ic_taxi);
        addView(inflater, viewGroup, "Банкоматы", R.drawable.ic_card);
        addView(inflater, viewGroup, "Автосервис", R.drawable.ic_wheel);

    }

    private void addView(LayoutInflater inflater, ViewGroup viewGroup, String text, int iconResId) {
        View view = inflater.inflate(R.layout.welcome_item, viewGroup, false);
        TextView textView = (TextView) view.findViewById(R.id.welcome_text);
        textView.setText(text);
        ImageView imageView = (ImageView) view.findViewById(R.id.welcome_icon);
        imageView.setImageResource(iconResId);
        viewGroup.addView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (searchItem != null){
            MenuItemCompat.collapseActionView(searchItem);
        }
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String lastPathSegment = uri.getLastPathSegment();
            if (lastPathSegment != null) {
                String[] split = lastPathSegment.split("_");
                if (split.length == 2) {
                    searchProfile(split[0], split[1]);
                }
            }
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchFirms(query);
        }
    }

    private void searchFirms(String query) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_content);
        if (!(fragment instanceof FirmListFragment)){
            fragment = FirmListFragment.create(query);
            fragmentManager.popBackStack(ProfileFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_content, fragment);
            transaction.commit();
        } else {
            ((FirmListFragment) fragment).search(query);
        }
    }

    private void searchProfile(String id, String hash) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_content);
        if (!(fragment instanceof ProfileFragment)){
            boolean addToBackStack = (fragment != null);
            fragment = ProfileFragment.create(id, hash);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_content, fragment);
            if (addToBackStack){
                transaction.addToBackStack(ProfileFragment.TAG);
            }
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.commit();
        } else {
            ((ProfileFragment) fragment).search(id, hash);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        if (searchItem != null){
            MenuItemCompat.collapseActionView(searchItem);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (s.length() > 2){
            searchFirms(s);
            return true;
        }
        return false;
    }

    @Override
    public RequestQueue getRequestQueue() {
        return queue;
    }
}
