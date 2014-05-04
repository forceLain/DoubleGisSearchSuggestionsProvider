package com.android.doublegissearch.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by lain on 04.05.2014.
 */
public class Schedule {

    public String comment;

    @SerializedName("Mon")
    public Map<String, WorkInterval> mon;

    @SerializedName("Tue")
    public Map<String, WorkInterval> tue;

    @SerializedName("Wed")
    public Map<String, WorkInterval> wed;

    @SerializedName("Thu")
    public Map<String, WorkInterval> thu;

    @SerializedName("Fri")
    public Map<String, WorkInterval> fri;

    @SerializedName("Sat")
    public Map<String, WorkInterval> sat;

    @SerializedName("Sun")
    public Map<String, WorkInterval> sun;

    public static class WorkInterval{
        public String from;
        public String to;
    }

}
