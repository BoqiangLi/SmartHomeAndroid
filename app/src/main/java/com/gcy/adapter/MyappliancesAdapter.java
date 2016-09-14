package com.gcy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gcy.beans.Myappliances;

import java.util.List;

import activity.gcy.com.demo.R;

/**
 * Created by Mr.G on 2016/5/14.
 */
public class MyappliancesAdapter extends ArrayAdapter<Myappliances> {
    int resourceId;

    public MyappliancesAdapter(Context context, int resource, List<Myappliances> objects) {
        super(context, resource, objects);
        this.resourceId = resource;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Myappliances ma = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
            viewHolder.applianceName = (TextView)convertView.findViewById(R.id.activity_myappliance_item_appliance_name);
            viewHolder.regulation = (TextView)convertView.findViewById(R.id.activity_myappliance_item_appliance_value);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.applianceName.setText(ma.getAppliancesName());
        viewHolder.regulation.setText(ma.getRegulation());
        if(ma.getRegulation().equals("是")){
            viewHolder.regulation.setTextColor(Color.rgb(56,164,178));
            viewHolder.applianceName.setTextColor(Color.BLACK);
        }
        return convertView;
    }
    class ViewHolder {
        TextView applianceName;
        TextView regulation;
    }
}
