package com.android.doublegissearch.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

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

    public static String formatDistance(int dist, String m, String km) {
        if (dist < 1000){
            dist = dist - dist % 10;
            return dist + m;
        }
        float kmDist = (float)dist / 1000;
        String format = new DecimalFormat("#.#").format(kmDist);
        return format+km;
    }

    public static void sendEmail(String email, Context context){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        context.startActivity(Intent.createChooser(intent, "Email"));
    }

    public static void openWeb(String url, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(Intent.createChooser(intent, "Web"));
    }

    public static void call(String phone, Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+Uri.encode(phone)));
        context.startActivity(Intent.createChooser(intent, "Call"));
    }

    public static void openMap(String lat, String lon, Context context) {
        String action = String.format("geo:%s,%s?q=%s,%s", lat, lon, lat, lon);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(action));
        context.startActivity(Intent.createChooser(intent, "Map"));
    }

    public static String notNullString(String query) {
        if (query == null){
            return "";
        }
        return query;
    }
}
