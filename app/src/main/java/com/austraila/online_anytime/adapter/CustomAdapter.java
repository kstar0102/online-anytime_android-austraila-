package com.austraila.online_anytime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.austraila.online_anytime.R;
import com.austraila.online_anytime.model.Listmodel;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter {
    ArrayList listItem = new ArrayList<>();

    public CustomAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        listItem = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Listmodel model = (Listmodel) listItem.get(position);

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.mainlist_item, null);
        TextView textView = (TextView) v.findViewById(R.id.listitem_title);
        textView.setText(model.getListText());
        return v;

    }
}
