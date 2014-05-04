package com.android.doublegissearch.model;

/**
 * Created by lain on 04.05.2014.
 */
public class Contacts {
    public String name;
    public Contacts.Contact[] contacts;

    public static class Contact{
        public String type;
        public String value;
        public String alias;
        public String comment;
    }
}
