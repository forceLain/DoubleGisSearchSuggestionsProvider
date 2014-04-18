package com.android.doublegissearch.utils;

/**
 * Created by a.glyzin on 18.04.2014.
 */
public class Utils {

    private Utils(){

    }

    public static boolean isEmpty(String string){
        if (string == null || string.length() == 0){
            return true;
        }
        return false;
    }
}
