package com.android.doublegissearch;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.net.Uri;
import android.provider.BaseColumns;

import com.android.doublegissearch.model.Firm;
import com.android.doublegissearch.model.FirmResponse;
import com.android.doublegissearch.model.ProjectResponse;
import com.android.doublegissearch.network.UrlBuilder;
import com.android.doublegissearch.utils.LocationUtils;
import com.android.doublegissearch.utils.Utils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class DoubleGisSuggestionProvider extends ContentProvider {

    private static final String[] columnNames = new String[]{
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

    public static final String TAG = DoubleGisSuggestionProvider.class.getSimpleName();

    private RequestQueue queue;
    private Cancelable cancelable;
    private Location location;

    public DoubleGisSuggestionProvider() {
        cancelable = new Cancelable();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return "";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return uri;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        queue = new Volley().newRequestQueue(context);
        location = LocationUtils.getLocation(context);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String query = selectionArgs[0];

        return apiSearch(query);
    }

    private Cursor apiSearch(String query) {
        queue.cancelAll(cancelable);
        MatrixCursor cursor = new MatrixCursor(columnNames);
        String urlRequest = new UrlBuilder().search().what(query).where(location.getLongitude(), location.getLatitude()).build();
        RequestFuture<FirmResponse> future = RequestFuture.newFuture();
        GsonRequest<FirmResponse> request = new GsonRequest<FirmResponse>(Request.Method.GET, urlRequest, FirmResponse.class, future, future);
        request.setTag(cancelable);
        queue.add(request);
        try {
            FirmResponse response = future.get();
            if (response.result != null) {
                fillCursor(response.result, cursor);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    private void fillCursor(Firm[] firms, MatrixCursor cursor) {
        Arrays.sort(firms, new Firm.FirmComparator());
        for (Firm firm : firms) {
            cursor.addRow(new Object[]{firm.id, firm.name, firm.address+" "+Utils.formatDistance(firm.dist), firm.id + "_" + firm.hash});
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }

    private class Cancelable {
    }
}
