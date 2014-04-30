package com.android.doublegissearch.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.android.doublegissearch.model.Project;

/**
 * Created by lain on 01.05.2014.
 */
public class LocationUtils {

    private static final int EARTH_RADIUS = 6371;

    private LocationUtils(){}

    public static Location getLocation(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String  provider = locationManager.getBestProvider(criteria, false);
        return locationManager.getLastKnownLocation(provider);
    }

    public static String getNearestCity(Location currentLocation, Project[] result){
        double minDistance = -1;
        String city = null;
        double lat1 = Math.toRadians(currentLocation.getLatitude());
        double lon1 = Math.toRadians(currentLocation.getLongitude());
        for (Project project: result){
            String location = project.centroid.replace("POINT(", "");
            location = location.replace(")", "");
            String[] points = location.split(" ");
            double lon2 = Double.parseDouble(points[0]);
            double lat2 = Double.parseDouble(points[1]);
            lat2 = Math.toRadians(lat2);
            lon2 = Math.toRadians(lon2);
            double distance = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2)) * EARTH_RADIUS;
            if (minDistance == -1){
                minDistance = distance;
                continue;
            }
            if (distance < minDistance){
                minDistance = distance;
                city = project.name;
            }
        }
        return city;
    }
}
