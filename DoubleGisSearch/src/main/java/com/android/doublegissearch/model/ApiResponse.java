package com.android.doublegissearch.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a.glyzin on 18.04.2014.
 */
public class ApiResponse {
    @SerializedName("response_code")
    public String responseCode;

    @SerializedName("error_message")
    public String errorMessage;

    @SerializedName("error_code")
    public String errorCode;
}
