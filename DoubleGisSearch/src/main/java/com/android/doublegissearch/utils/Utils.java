package com.android.doublegissearch.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public static String readFromAssets(Context context) {
        StringBuilder buf = new StringBuilder();
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open("data.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return buf.toString();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String str;

        try {
            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return buf.toString();
    }
}
