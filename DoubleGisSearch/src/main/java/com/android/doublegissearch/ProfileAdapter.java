package com.android.doublegissearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.doublegissearch.model.Contacts;
import com.android.doublegissearch.model.Profile;
import com.android.doublegissearch.model.Schedule;
import com.android.doublegissearch.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lain on 04.05.2014.
 */
public class ProfileAdapter extends BaseAdapter {

    private static final int PROFILE_NAME = 0;
    private static final int PROFILE_ADDRESS = 1;
    private static final int PROFILE_SCHEDULE = 2;
    private static final int PROFILE_CONTACTS = 3;
    private static final int PROFILE_MAP = 4;

    private final LayoutInflater layoutInflater;
    private final Profile profile;
    private interface ProfileData{}
    private List<ProfileData> profileItems;

    public ProfileAdapter(Context context, Profile profile){
        layoutInflater = LayoutInflater.from(context);
        this.profile = profile;
        profileItems = new ArrayList<ProfileData>();
        parseProfile();
    }

    private void parseProfile() {
        ProfileMap profileMap = new ProfileMap();
        profileMap.lat = profile.lat;
        profileMap.lon = profile.lon;
        profileItems.add(profileMap);
        ProfileName profileName = new ProfileName();
        profileName.name = profile.name;
        profileName.rubricks = profile.rubrics;
        profileItems.add(profileName);
        ProfileAddress address = new ProfileAddress();
        address.address = profile.address;
        profileItems.add(address);
        ProfileSchedule schedule = new ProfileSchedule();
        schedule.schedule = profile.schedule;
        profileItems.add(schedule);
        if (profile.contacts != null){
            for (Contacts contacts: profile.contacts){
                if (contacts.contacts != null){
                    for (Contacts.Contact contact: contacts.contacts){
                        ProfileContact profileContact = new ProfileContact();
                        profileContact.contact = contact;
                        profileItems.add(profileContact);
                    }
                }
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        ProfileData profileData = profileItems.get(position);
        if (profileData instanceof ProfileName){
            return PROFILE_NAME;
        } else if (profileData instanceof ProfileAddress){
            return PROFILE_ADDRESS;
        } else if (profileData instanceof ProfileSchedule){
            return PROFILE_SCHEDULE;
        } else if (profileData instanceof ProfileContact){
            return PROFILE_CONTACTS;
        } else if (profileData instanceof ProfileMap){
            return PROFILE_MAP;
        }
        return IGNORE_ITEM_VIEW_TYPE;
    }

    @Override
    public int getCount() {
        return profileItems.size();
    }

    @Override
    public Object getItem(int position) {
        return profileItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)){
            case PROFILE_NAME:
                return getViewTitle(position, convertView, parent);
            case PROFILE_ADDRESS:
                return getViewAddress(position, convertView, parent);
            case PROFILE_SCHEDULE:
                return getViewSchedule(position, convertView, parent);
            case PROFILE_CONTACTS:
                return getViewContacts(position, convertView, parent);
            case PROFILE_MAP:
                return getViewMap(position, convertView, parent);
            default:
                return null;
        }
    }

    private View getViewMap(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.profile_map, parent, false);
        }
        ProfileMap profileMap = (ProfileMap) profileItems.get(position);
        WebView webView = (WebView) convertView;
        webView.getSettings().setJavaScriptEnabled(true);
        String data = Utils.readFromAssets(convertView.getContext());
        webView.loadDataWithBaseURL("http://www.example.com", data, "text/html", "UTF-8", null);
        return webView;
    }

    private View getViewContacts(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.profile_contacts, parent, false);
        }
        ProfileContact profileContact = (ProfileContact) profileItems.get(position);
        String text;
        if (profileContact.contact.alias != null){
            text = profileContact.contact.alias;
        } else {
            text = profileContact.contact.value;
        }
        TextView textView = (TextView) convertView.findViewById(R.id.profile_contact);
        textView.setText(text);

        textView = (TextView) convertView.findViewById(R.id.profile_comment);
        if (Utils.isEmpty(profileContact.contact.comment)){
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(profileContact.contact.comment);
            textView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private View getViewSchedule(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.profile_schedule, parent, false);
        }
        ProfileSchedule profileSchedule = (ProfileSchedule) profileItems.get(position);
        TextView textView = (TextView) convertView.findViewById(R.id.profile_schedule_comment);
        textView.setText(profileSchedule.schedule.comment);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_mon), "Пн", profileSchedule.schedule.mon);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_tue), "Вт", profileSchedule.schedule.tue);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_wed), "Ср", profileSchedule.schedule.wed);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_thu), "Чт", profileSchedule.schedule.thu);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_fri), "Пт", profileSchedule.schedule.fri);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_sat), "Сб", profileSchedule.schedule.sat);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_sun), "Вс", profileSchedule.schedule.sun);
        return convertView;
    }

    private void setSchedule(TextView textView, String title, Map<String, Schedule.WorkInterval> workIntervalMap) {
        StringBuilder sb = new StringBuilder(title);
        sb.append(" ");
        for(Map.Entry<String, Schedule.WorkInterval> entry: workIntervalMap.entrySet()){
            Schedule.WorkInterval workInterval = entry.getValue();
            sb.append(workInterval.from)
                    .append("-")
                    .append(workInterval.to)
                    .append('\n');
        }
        textView.setText(sb.toString());
    }

    private View getViewAddress(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.profile_address, parent, false);
        }
        ProfileAddress profileAddress = (ProfileAddress) profileItems.get(position);
        TextView textView = (TextView) convertView.findViewById(R.id.profile_address);
        textView.setText(profileAddress.address);
        return convertView;
    }

    private View getViewTitle(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.profile_title, parent, false);
        }
        ProfileName profileName = (ProfileName) profileItems.get(position);
        TextView textView = (TextView) convertView.findViewById(R.id.profile_name);
        textView.setText(profileName.name);
        textView = (TextView) convertView.findViewById(R.id.profile_rubricks);
        textView.setText(Arrays.toString(profileName.rubricks));
        return convertView;
    }

    private class ProfileName implements ProfileData{
        String name;
        String[] rubricks;
    }

    private class ProfileAddress implements ProfileData{
        String address;
    }

    private class ProfileSchedule implements ProfileData{
        Schedule schedule;
    }

    private class ProfileContact implements ProfileData{
        Contacts.Contact contact;
    }

    private class ProfileMap implements ProfileData{
        String lat;
        String lon;
    }
}
