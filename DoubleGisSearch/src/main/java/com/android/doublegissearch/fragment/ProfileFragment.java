package com.android.doublegissearch.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.doublegissearch.GsonRequest;
import com.android.doublegissearch.MainActivity;
import com.android.doublegissearch.ProfileAdapter;
import com.android.doublegissearch.R;
import com.android.doublegissearch.VolleyActivity;
import com.android.doublegissearch.model.Profile;
import com.android.doublegissearch.network.UrlBuilder;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by lain on 11.05.2014.
 */
public class ProfileFragment extends BaseListFragment {

    public static final String TAG = ProfileFragment.class.getSimpleName();
    private static final String ARG_ID= TAG+".id";
    private static final String ARG_HASH= TAG+".hash";

    private String id;
    private String hash;
    private VolleyActivity volleyActivity;

    public static ProfileFragment create(String id, String hash) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ID, id);
        bundle.putString(ARG_HASH, hash);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof VolleyActivity){
            this.volleyActivity = (VolleyActivity) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        volleyActivity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null){
            id = arguments.getString(ARG_ID);
            hash = arguments.getString(ARG_HASH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ProfileAdapter adapter = new ProfileAdapter(getActivity(), null);
        setListAdapter(adapter);
        search(id, hash);
        setDisplayHomeAsUpEnabled(true);
        return root;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ProfileAdapter profileAdapter = (ProfileAdapter) getListAdapter();
        profileAdapter.handleClick(position, getActivity());
    }

    public void search(String id, String hash) {
        Response.Listener<Profile> onSuccess = new Response.Listener<Profile>() {
            @Override
            public void onResponse(Profile profile) {
                setActionBarTitle(profile.name);
                ((ProfileAdapter) getListAdapter()).setProfile(profile);
                registerView(profile.registerBcUrl);
                updateProgress(false);
                setEmptyViewVisibility(getListAdapter().getCount() == 0);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                updateProgress(false);
                setEmptyViewVisibility(true);
            }
        };

        String url = new UrlBuilder().profile(id, hash).build();
        GsonRequest<Profile> request = new GsonRequest<Profile>(Request.Method.GET, url, Profile.class, onSuccess, errorListener);
        volleyActivity.getRequestQueue().add(request);
        updateProgress(true);
        setEmptyViewVisibility(false);
    }

    private void registerView(String registerBcUrl) {
        Response.Listener<String> onSuccess = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d(TAG, s);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.w(TAG, volleyError.getMessage());
            }
        };
        StringRequest request = new StringRequest(registerBcUrl, onSuccess, errorListener);
        volleyActivity.getRequestQueue().add(request);
    }
}
