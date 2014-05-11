package com.android.doublegissearch;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.doublegissearch.model.Contacts;
import com.android.doublegissearch.model.Profile;
import com.android.doublegissearch.model.Schedule;
import com.android.doublegissearch.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private interface ProfileData{}
    private List<ProfileData> profileItems;

    public ProfileAdapter(Context context, Profile profile){
        layoutInflater = LayoutInflater.from(context);
        profileItems = new ArrayList<ProfileData>();
        setProfile(profile);
    }

    public void setProfile(Profile profile) {
        profileItems.clear();
        if (profile == null){
            return;
        }
        ProfileMap profileMap = new ProfileMap();
        profileMap.lat = profile.lat;
        profileMap.lon = profile.lon;
        profileItems.add(profileMap);
        ProfileName profileName = new ProfileName();
        profileName.name = profile.name;
        profileName.rubricks = profile.rubrics;
        profileName.article = profile.article;
        profileName.link = profile.link;
        profileItems.add(profileName);
        ProfileAddress address = new ProfileAddress();
        address.address = profile.address;
        profileItems.add(address);
        if (profile.schedule != null){
            ProfileSchedule schedule = new ProfileSchedule();
            schedule.schedule = profile.schedule;
            profileItems.add(schedule);
        }
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
        notifyDataSetChanged();
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
    public ProfileData getItem(int position) {
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
        final ProfileMap profileMap = (ProfileMap) profileItems.get(position);
        final WebView webView = (WebView) convertView.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setFocusable(false);
        webView.setFocusableInTouchMode(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        String data = Utils.readFromAssets(convertView.getContext());
        data = data.replaceAll("PH_LATITUDE", profileMap.lat);
        data = data.replaceAll("PH_LONGITUDE", profileMap.lon);
        webView.loadDataWithBaseURL("http://www.example.com", data, "text/html", "UTF-8", null);
        webView.setOnTouchListener(new WebViewTouchListener(profileMap, webView.getContext()));
        return convertView;
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
        if (Utils.isEmpty(profileSchedule.schedule.comment)){
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(profileSchedule.schedule.comment);
            textView.setVisibility(View.VISIBLE);
        }
        Context context = convertView.getContext();
        setSchedule((TextView) convertView.findViewById(R.id.schedule_mon), context.getString(R.string.mon), profileSchedule.schedule.mon);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_tue), context.getString(R.string.tue), profileSchedule.schedule.tue);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_wed), context.getString(R.string.wed), profileSchedule.schedule.wed);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_thu), context.getString(R.string.thu), profileSchedule.schedule.thu);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_fri), context.getString(R.string.fri), profileSchedule.schedule.fri);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_sat), context.getString(R.string.sat), profileSchedule.schedule.sat);
        setSchedule((TextView) convertView.findViewById(R.id.schedule_sun), context.getString(R.string.sun), profileSchedule.schedule.sun);
        return convertView;
    }

    private void setSchedule(TextView textView, String title, Map<String, Schedule.WorkInterval> workIntervalMap) {
        if (workIntervalMap == null){
            textView.setVisibility(View.GONE);
            return;
        } else {
            textView.setVisibility(View.VISIBLE);
        }

        String start = null;
        String stop = null;
        ArrayList<String> breakIntervals = new ArrayList<String>();

        for(Map.Entry<String, Schedule.WorkInterval> entry: workIntervalMap.entrySet()){
            Schedule.WorkInterval workInterval = entry.getValue();
            if (start == null){
                start = workInterval.from;
            } else {
                breakIntervals.add(stop);
                breakIntervals.add(workInterval.from);
            }
            stop = workInterval.to;
        }
        StringBuilder sb = new StringBuilder(title+": "+start+" - "+stop);
        if (breakIntervals.size() > 0){
            String breakTitle = " "+textView.getContext().getString(R.string.break_title)+":";
            sb.append(breakTitle);
            for (int i = 0; i < breakIntervals.size()/2; i++){
                sb.append(" "+breakIntervals.get(2*i)+ " - "+breakIntervals.get(2*i+1));
            }
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
        final ProfileName profileName = (ProfileName) profileItems.get(position);
        TextView textView = (TextView) convertView.findViewById(R.id.profile_name);
        textView.setText(profileName.name);
        textView = (TextView) convertView.findViewById(R.id.profile_rubricks);
        if (profileName.rubricks != null){
            textView.setText(Arrays.toString(profileName.rubricks));
        }
        textView = (TextView) convertView.findViewById(R.id.profile_article);
        if (Utils.isEmpty(profileName.article)){
            textView.setVisibility(View.GONE);
            textView.setText("");
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(Html.fromHtml(profileName.article));
        }
        textView = (TextView) convertView.findViewById(R.id.profile_link);
        if (profileName.link == null){
            textView.setVisibility(View.GONE);
            textView.setText("");
            textView.setOnClickListener(null);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(Html.fromHtml(profileName.link.text));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.openWeb(profileName.link.link, v.getContext());
                }
            });
        }

        return convertView;
    }

    private class ProfileName implements ProfileData{
        String name;
        String[] rubricks;
        String article;
        Profile.Link link;
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

    private class WebViewTouchListener implements View.OnTouchListener{

        private final ProfileMap profileMap;
        private final Context context;

        WebViewTouchListener(ProfileMap profileMap, Context context){
            this.profileMap = profileMap;
            this.context = context;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            switch (action) {
                case MotionEvent.ACTION_CANCEL:
                    return true;
                case MotionEvent.ACTION_UP:
                    openMap(profileMap, context);
                    return true;
            }

            return true;
        }
    }


    public void handleClick(int position, Context context) {
        ProfileData item = getItem(position);
        if (item instanceof ProfileContact){
            openContact((ProfileContact)item, context);
        } else if (item instanceof ProfileMap){
            openMap((ProfileMap)item, context);
        }
    }

    private void openMap(ProfileMap item, Context context) {
        Utils.openMap(item.lat, item.lon, context);
    }

    private void openContact(ProfileContact item, Context context) {
        Contacts.Contact contact = item.contact;
        if ("email".equals(contact.type)){
            Utils.sendEmail(contact.value, context);
        } else if ("website".equals(contact.type)){
            Utils.openWeb(contact.value, context);
        } else if ("phone".equals(contact.type) || "fax".equals(contact.type)){
            Utils.call(contact.value, context);
        }
    }
}
