package com.android.doublegissearch.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a.glyzin on 18.04.2014.
 */
public class AdFirm {
    @SerializedName("firm_id")
    public String id;

    public String hash;
    public String title;
    public String text;
    public String lon;
    public String lat;
}
