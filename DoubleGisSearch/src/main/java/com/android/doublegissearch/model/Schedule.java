package com.android.doublegissearch.model;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;

/**
 * Created by lain on 04.05.2014.
 */
public class Schedule {

    public String comment;

    @SerializedName("Mon")
    public LinkedHashMap<String, WorkInterval> mon;

    @SerializedName("Tue")
    public LinkedHashMap<String, WorkInterval> tue;

    @SerializedName("Wed")
    public LinkedHashMap<String, WorkInterval> wed;

    @SerializedName("Thu")
    public LinkedHashMap<String, WorkInterval> thu;

    @SerializedName("Fri")
    public LinkedHashMap<String, WorkInterval> fri;

    @SerializedName("Sat")
    public LinkedHashMap<String, WorkInterval> sat;

    @SerializedName("Sun")
    public LinkedHashMap<String, WorkInterval> sun;

    public static class WorkInterval{
        public String from;
        public String to;
    }

}
