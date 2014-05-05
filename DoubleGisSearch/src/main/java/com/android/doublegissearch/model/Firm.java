package com.android.doublegissearch.model;

import java.util.Comparator;

/**
 * Created by a.glyzin on 18.04.2014.
 */
public class Firm {
    public String id;
    public String name;
    public String address;
    public String hash;
    public int dist;

    public static class FirmComparator implements Comparator<Firm>{

        @Override
        public int compare(Firm lhs, Firm rhs) {
            if (lhs.dist > rhs.dist){
                return 1;
            }
            if (lhs.dist < rhs.dist){
                return -1;
            }
            return 0;
        }
    }
}
