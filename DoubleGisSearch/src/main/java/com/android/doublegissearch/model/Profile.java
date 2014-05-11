package com.android.doublegissearch.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a.glyzin on 18.04.2014.
 */
public class Profile {
    @SerializedName("response_code")
    public String responseCode;

    @SerializedName("register_bc_url")
    public String registerBcUrl;

    public String id;
    public String name;
    public String address;
    public String[] rubrics;
    public Schedule schedule;
    public Contacts[] contacts;
    public String lat;
    public String lon;
    public String article;
    public Link link;

    public static class Link{
        public String link;
        public String text;
    }
}
