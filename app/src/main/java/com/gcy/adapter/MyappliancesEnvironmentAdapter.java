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
public class MyappliancesEnvironmentAdapter extends ArrayAdapter<Myappliances> {
    int resourceId;

    public MyappliancesEnvironmentAdapter(Context context, int resource, List<Myappliances> objects) {
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
            viewHolder.applicanceValue = (TextView)convertView.findViewById(R.id.activity_myappliance_item_appliance_value);
            viewHolder.unit = (TextView)convertView.findViewById(R.id.activity_myappliance_item_appliance_unit);
            viewHolder.status = (TextView)convertView.findViewById(R.id.activity_myappliance_item_appliance_status);
            viewHolder.regulation = (TextView)convertView.findViewById(R.id.activity_myappliance_item_appliance_regulation);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.applianceName.setText(ma.getAppliancesName());
        if(ma.getApplicansValue().equals("null"))
            viewHolder.applicanceValue.setText("离线");
        else
            viewHolder.applicanceValue.setText(ma.getApplicansValue());
        viewHolder.unit.setText(ma.getUnit());
        viewHolder.status.setText(ma.getStatus());
        viewHolder.regulation.setText(ma.getRegulation());
        if(!ma.isNormal()) {

            viewHolder.applicanceValue.setTextColor(Color.RED);
            viewHolder.status.setText("异常");
            viewHolder.status.setTextColor(Color.RED);

        }else{
            viewHolder.applicanceValue.setTextColor(Color.rgb(56,164,178));
            viewHolder.status.setText("正常");
            viewHolder.status.setTextColor(Color.rgb(56,164,178));
        }
        return convertView;
    }
    class ViewHolder {
        TextView applianceName;
        TextView applicanceValue;
        TextView status;
        TextView regulation;
        TextView unit;
    }
}
