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

import com.android.doublegissearch.model.AdFirm;
import com.android.doublegissearch.model.Firm;
import com.android.doublegissearch.model.FirmResponse;
import com.android.doublegissearch.network.NetworkClient;
import com.android.doublegissearch.network.UrlBuilder;
import com.android.doublegissearch.utils.LocationUtils;
import com.android.doublegissearch.utils.Utils;

import java.util.Arrays;

public class DoubleGisSuggestionProvider extends ContentProvider {

    public static interface Columns{
        String ID = BaseColumns._ID;
        String TEXT_1 = SearchManager.SUGGEST_COLUMN_TEXT_1;
        String TEXT_2 = SearchManager.SUGGEST_COLUMN_TEXT_2;
        String INTENT_DATA = SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID;
        String AD = "ad";
    }

    private static final String[] columnNames = new String[]{
            Columns.ID,
            Columns.TEXT_1,
            Columns.TEXT_2,
            Columns.INTENT_DATA,
            Columns.AD};

    public static final String TAG = DoubleGisSuggestionProvider.class.getSimpleName();

    private Location location;

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
        location = LocationUtils.getLocation(context);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        boolean internal = uri.getLastPathSegment().equals("internal");
        String query = selectionArgs[0];
        return apiSearch(query, internal);
    }

    private Cursor apiSearch(String query, boolean withAd) {
        MatrixCursor cursor = new MatrixCursor(columnNames);
        if (!Utils.isEmpty(query)){
            String urlRequest = new UrlBuilder().search().what(query).where(location.getLongitude(), location.getLatitude()).build();
            NetworkClient client = new NetworkClient();
            client.setUrl(urlRequest);
            FirmResponse firmResponse = client.getModel(FirmResponse.class);
            if (withAd){
                fillCursor(firmResponse.result, firmResponse.advertising, cursor);
            } else {
                fillCursor(firmResponse.result, null, cursor);
            }

        }

        return cursor;
    }

    private void fillCursor(Firm[] firms, AdFirm[] advertising, MatrixCursor cursor) {
        if (advertising != null){
            for (AdFirm adFirm: advertising){
                cursor.addRow(new Object[]{adFirm.id, adFirm.title, adFirm.text, adFirm.id+"_"+adFirm.hash, 1});
            }
        }
        if (firms != null){
            Arrays.sort(firms, new Firm.FirmComparator());
            String km = getContext().getString(R.string.km);
            String m = getContext().getString(R.string.m);
            for (Firm firm : firms) {
                cursor.addRow(new Object[]{firm.id, firm.name, firm.address+"; "+Utils.formatDistance(firm.dist, m, km), firm.id + "_" + firm.hash, 0});
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}
