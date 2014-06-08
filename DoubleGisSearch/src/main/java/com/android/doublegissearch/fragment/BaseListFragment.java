package com.android.doublegissearch.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.android.doublegissearch.MainActivity;
import com.android.doublegissearch.R;

/**
 * Created by lain on 11.05.2014.
 */
public abstract class BaseListFragment extends ListFragment {

    private View emptyView;

    protected void updateProgress(boolean visibility){
        getActivity().setProgressBarIndeterminate(true);
        getActivity().setProgressBarVisibility(visibility);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emptyView = getView().findViewById(R.id.empty);
        setEmptyViewVisibility(false);
    }

    protected void setEmptyViewVisibility(boolean visibility){
        //standard emptyView is not for us
        if (emptyView != null){
            if (visibility){
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    protected void setActionBarTitle(String title){
        ActionBarActivity activity = (ActionBarActivity) getActivity();
        activity.getSupportActionBar().setTitle(title);
    }

    protected void setDisplayHomeAsUpEnabled(boolean showHomeAsEnabled){
        ActionBarActivity activity = (ActionBarActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeAsEnabled);
    }
}
