package com.dxbcom.matchmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.dxbcom.matchmanager.R;
import com.dxbcom.matchmanager.core.Communicator;
import com.dxbcom.matchmanager.models.Model;
import com.dxbcom.matchmanager.models.Player;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Yaser on 7/20/2016.
 */
public class PlayersSpinnerAdapter extends ArrayAdapter {

    private List mItems;
    private int mResource;
    private LayoutInflater mLayoutInflater;

    public PlayersSpinnerAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        mResource = resource;
        mItems = objects;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mLayoutInflater.inflate(mResource, parent, false);
        }

        Player player = ((Player) getItem(position));

        ((TextView) convertView.findViewById(R.id.name)).setText(player.getName());
        Picasso.with(mLayoutInflater.getContext())
                .load(Communicator.API_URL+"playerPhoto/"+player.getId())
                .placeholder(R.drawable.placeholder)
                .into(((ImageView) convertView.findViewById(R.id.photo)));

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }
}
