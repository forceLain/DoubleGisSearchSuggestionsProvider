package com.android.doublegissearch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lain on 10.06.2014.
 */
public class WelcomeAdapter extends BaseAdapter {

    private final String[] titles;
    private final int[] ids;

    public WelcomeAdapter() {
        this.titles = new String[]{"Такси", "Банкоматы", "Автосервис", "Кафе", "Цветы"};
        this.ids = new int[]{R.drawable.ic_taxi, R.drawable.ic_card, R.drawable.ic_wheel, R.drawable.ic_cafe, R.drawable.ic_flower};
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public String getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return ids[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.welcome_item, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.welcome_text);
        textView.setText(titles[position]);
        ImageView icon = (ImageView) convertView.findViewById(R.id.welcome_icon);
        icon.setImageResource(ids[position]);
        return convertView;
    }
}
