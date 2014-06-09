package com.android.doublegissearch.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.doublegissearch.MainActivity;
import com.android.doublegissearch.R;
import com.android.doublegissearch.WelcomeAdapter;

/**
 * Created by lain on 10.06.2014.
 */
public class WelcomeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_welcome, container, false);
        ListView listView = (ListView) root.findViewById(R.id.welcome_list);
        final WelcomeAdapter adapter = new WelcomeAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = adapter.getItem(position);
                MainActivity activity = (MainActivity) getActivity();
                activity.searchFirms(text);
            }
        });
        return root;
    }
}
