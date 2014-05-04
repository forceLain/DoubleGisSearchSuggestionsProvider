package com.android.doublegissearch.network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by lain on 01.05.2014.
 */
public class UrlBuilder {
    private static final String VERSION = "1.3";
    private static final String KEY = "rumobc0685";
    private static final String ENDPOINT = "http://catalog.api.2gis.ru";
    private static final int RESOURCE_PROJECT = 1;
    private static final int RESOURCE_SEARCH = 2;
    private static final int RESOURCE_PROFILE = 3;
    private static final int DEFAULT_LIMIT = 15;

    private String what;
    private String where;
    private int resource;
    private int limit = DEFAULT_LIMIT;
    private String id;
    private String hash;

    public UrlBuilder projects() {
        resource = RESOURCE_PROJECT;
        return this;
    }

    public UrlBuilder search() {
        resource = RESOURCE_SEARCH;
        return this;
    }

    public UrlBuilder profile(String id, String hash){
        resource = RESOURCE_PROFILE;
        this.id = id;
        this.hash = hash;
        return this;
    }

    public UrlBuilder what(String what) {
        this.what = encode(what);
        return this;
    }

    public UrlBuilder where(String where) {
        this.where = encode(where);
        return this;
    }

    public UrlBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public String build() {
        switch (resource) {
            case RESOURCE_PROJECT:
                return buildProjectUrl();
            case RESOURCE_SEARCH:
                return buildSearchUrl();
            case RESOURCE_PROFILE:
                return buildProfile();
            default:
                throw new IllegalStateException("You mast set search() or projects() or profile(...)");
        }
    }

    private String encode(String text) {
        if (text == null) {
            return "";
        }
        try {
            return URLEncoder.encode(text, "UTF8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private String buildProjectUrl() {
        return ENDPOINT + "/project/list?" + "version=" + VERSION + "&key=" + KEY;
    }

    private String buildProfile() {
        return ENDPOINT + "/profile?" + "version=" + VERSION + "&key=" + KEY + "&id=" + id + "&hash="+hash;
    }

    private String buildSearchUrl() {
        return ENDPOINT +
                "/search?what=" + what +
                "&where=" + where + "" +
                "&version=" + VERSION +
                "&key=" + KEY +
                "&pagesize=" + limit +
                "&sort=distance";
    }
}
