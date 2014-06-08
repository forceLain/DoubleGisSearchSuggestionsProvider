package com.android.doublegissearch.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.doublegissearch.DoubleGisSuggestionProvider;
import com.android.doublegissearch.MainActivity;
import com.android.doublegissearch.R;
import com.android.doublegissearch.utils.Utils;

/**
 * Created by lain on 11.05.2014.
 */
public class FirmListFragment extends BaseListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final Uri DOUBLE_GIS_URI = Uri.parse("content://com.android.doublegissearch.DoubleGisSuggestionProvider/api/internal");
    private static final String TAG = FirmListFragment.class.getSimpleName();
    private static final String ARG_QUERY = TAG+".arg_query";
    private String query = null;
    private FirmAdapter firmsAdapter;

    public static FirmListFragment create(String query) {
        FirmListFragment fragment = new FirmListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_QUERY, query);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null){
            query = arguments.getString(ARG_QUERY);
        }
        initFirmsAdapter();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_firm_list, container, false);
        setActionBarTitle(Utils.notNullString(query));
        setDisplayHomeAsUpEnabled(false);
        return root;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        updateProgress(true);
        setEmptyViewVisibility(false);
        return new CursorLoader(getActivity(), DOUBLE_GIS_URI, null, null, new String[]{query}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        firmsAdapter.changeCursor(data);
        updateProgress(false);
        setEmptyViewVisibility(getListAdapter().getCount() == 0);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void initFirmsAdapter() {
        String from[] = {DoubleGisSuggestionProvider.Columns.TEXT_1, DoubleGisSuggestionProvider.Columns.TEXT_2};
        int to[] = {android.R.id.text1, android.R.id.text2};
        firmsAdapter = new FirmAdapter(getActivity(),
                android.R.layout.simple_list_item_2, null, from, to, 0);
        setListAdapter(firmsAdapter);
    }

    public void search(String query){
        this.query = query;
        setActionBarTitle(query);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FirmAdapter simpleCursorAdapter = (FirmAdapter) getListAdapter();
        Cursor cursor = (Cursor) simpleCursorAdapter.getItem(position);
        String data = cursor.getString(cursor.getColumnIndexOrThrow(DoubleGisSuggestionProvider.Columns.INTENT_DATA));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(data));
        intent.setClass(getActivity(), MainActivity.class);
        getActivity().startActivity(intent);
    }

    private class FirmAdapter extends SimpleCursorAdapter{

        LayoutInflater layoutInflater;

        public FirmAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return layoutInflater.inflate(R.layout.firm_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            TextView textView = (TextView) view.findViewById(R.id.text_1);
            textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DoubleGisSuggestionProvider.Columns.TEXT_1)));
            textView = (TextView) view.findViewById(R.id.text_2);
            textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DoubleGisSuggestionProvider.Columns.TEXT_2)));
            int ad = cursor.getInt(cursor.getColumnIndexOrThrow(DoubleGisSuggestionProvider.Columns.AD));
            if (ad > 0){
                view.setBackgroundResource(R.drawable.list_item_bg_ad);
            } else {
                view.setBackgroundResource(R.drawable.list_item_bg);
            }
        }
    }
}
