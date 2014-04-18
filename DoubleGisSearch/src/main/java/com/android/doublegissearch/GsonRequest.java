package com.android.doublegissearch;

import com.android.doublegissearch.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

/**
 * Created by kolodyazhny on 24.09.13.
 */
public class GsonRequest<T> extends Request<T> {

    private final Gson gson;
    private final Class<T> clazz;
    private final Map<String, String> params;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;

    public GsonRequest(int method, String url, Class<T> clazz,
                       Map<String, String> params, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        GsonBuilder gsonBuilder = new GsonBuilder();
        this.gson = gsonBuilder.create();
        this.clazz = clazz;
        this.params = params;
        this.headers = headers;
        this.listener = listener;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params != null ? params : super.getParams();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        if (listener != null) {
            listener.onResponse(response);
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            Response<T> successResponse;
            if (Utils.isEmpty(json)) {
                successResponse = Response.success(null, null);
            } else {
                successResponse = Response.success(
                        gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
            }
            return successResponse;
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }

    }
}
