package com.android.doublegissearch;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.android.doublegissearch.model.ApiResponse;
import com.android.doublegissearch.model.Firm;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class DoubleGisSuggestionProvider extends ContentProvider {

    private static final String[] columnNames = new String[]{
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

    private static final String TAG = DoubleGisSuggestionProvider.class.getSimpleName();

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
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return uri;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        queue = new Volley().newRequestQueue(getContext());

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String  provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);

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
        String where = null;
        String encodedQuery = null;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF8");
            where = URLEncoder.encode("Новосибирск", "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return cursor;
        }
        String urlRequest = "http://catalog.api.2gis.ru/search?what=" + encodedQuery + "&where=" + where + "&version=1.3&key=rumobc0685&pagesize=5";
        RequestFuture<ApiResponse> future = RequestFuture.newFuture();
        GsonRequest request = new GsonRequest(Request.Method.GET, urlRequest, ApiResponse.class, null, null, future, future);
        request.setTag(cancelable);
        queue.add(request);
        try {
            ApiResponse response = future.get();
            if ("200".equals(response.responseCode)) {
                fillCursor(response.result, cursor);
            } else {
                Log.d(TAG, response.errorCode + " " + response.errorMessage);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    private void fillCursor(Firm[] firms, MatrixCursor cursor) {
        String latLong = "";
        if (location != null){
            latLong = location.getLongitude()+" "+location.getLatitude();
        }
        for (Firm firm : firms) {
            cursor.addRow(new Object[]{firm.id, firm.name, firm.address+"\n"+latLong, firm.id + "_" + firm.hash});
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
