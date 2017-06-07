package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.VolunteerEntryHolder;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.VolunteerStationEntity;
import com.androidquanjiakan.util.AmapLocationUtil;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2016/10/18 0018.
 */

public class VolunteerEntryAdapter extends BaseAdapter {

    private List<VolunteerStationEntity> data;
    private Context context;

    public VolunteerEntryAdapter(Context context,List<VolunteerStationEntity> data){
        this.context = context;
        this.data = data;
    }

    public List<VolunteerStationEntity> getData() {
        return this.data;
    }

    public void setData(List<VolunteerStationEntity> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        if(data!=null){
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final VolunteerEntryHolder holder;
        if(convertView==null){
            holder = new VolunteerEntryHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_volunteer_entry_list,null);
            holder.mStationName = (TextView) convertView.findViewById(R.id.station_name);
            holder.mIcon = (ImageView) convertView.findViewById(R.id.icon);
            holder.mLocationAndDistance = (TextView) convertView.findViewById(R.id.location_distance);
            holder.mImage = (ImageView) convertView.findViewById(R.id.place_img);
            convertView.setTag(holder);
        }else{
            holder = (VolunteerEntryHolder) convertView.getTag();
        }
        SetData(holder,position);
        return convertView;
    }

    private void SetData(VolunteerEntryHolder holder, int position) {
        AmapLocationUtil amapLocationUtil = new AmapLocationUtil(context.getApplicationContext());
//        LocationInfoUtil locationInfoUtil = new LocationInfoUtil(context.getApplicationContext());
//        JsonObject object = data.get(position);
        VolunteerStationEntity object = data.get(position);
//        holder.mStationName.setText(object.get("name").getAsString());
        holder.mStationName.setText(object.getName());
        Picasso.with(BaseApplication.getInstances().getApplicationContext()).load(object.getPhoto()).fit().placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher).into(holder.mImage);
//        Picasso.with(BaseApplication.getInstances().getApplicationContext()).load(object.get("photo").getAsString()).fit().placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher).into(holder.mImage);
//        ImageLoader.getInstance().displayImage(object.get("photo").getAsString(), holder.mImage, ((BaseApplication)((Activity)context).getApplication()).getNormalImageOptions(0, R.drawable.ic_launcher));
//        holder.mLocationAndDistance.setText(object.get("address").getAsString()+"  "+"距您"+locationInfoUtil.getDistance( Double.parseDouble(object.get("lat").getAsString()),Double.parseDouble(object.get("lng").getAsString()))+"KM");
        holder.mLocationAndDistance.setText(object.getAddress()+"  "+"距您"+amapLocationUtil.getDistance( object.getLat(),object.getLng())+"KM");
    }
}
