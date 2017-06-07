package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.VolunteerMessageHolder;
import com.androidquanjiakan.adapterholder.VolunteerPublishHistoryHolder;
import com.androidquanjiakan.entity.VolunteerMessageInfo;
import com.quanjiakan.main.R;

import java.util.List;

/**
 * Created by Administrator on 2016/10/18 0018.
 */

public class VolunteerMessageAdapter extends BaseAdapter {

    private List<VolunteerMessageInfo> data;
    private Context context;

    public VolunteerMessageAdapter(Context context, List<VolunteerMessageInfo> data){
        this.context = context;
        this.data = data;
    }

    public List<VolunteerMessageInfo> getData() {
        return data;
    }

    public void setData(List<VolunteerMessageInfo> data) {
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
        final VolunteerMessageHolder holder;
        if(convertView==null){
            holder = new VolunteerMessageHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_volunteer_message,null);
            holder.mIcon = (ImageView) convertView.findViewById(R.id.head_icon);
            holder.mType = (TextView) convertView.findViewById(R.id.help_type);
            holder.mName = (TextView) convertView.findViewById(R.id.name);
            holder.mMessage = (TextView) convertView.findViewById(R.id.service_message);
            holder.mTime = (TextView) convertView.findViewById(R.id.service_time);
            holder.mStatus = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        }else{
            holder = (VolunteerMessageHolder) convertView.getTag();
        }

        holder.mName.setText(data.get(position).getName());
        if(position%2==0){
            setType(TYPE_HURRY,holder);
//            setStatus(STATUS_UNREAD,holder);
        }else{
            setType(TYPE_NORMAL,holder);
//            setStatus(STATUS_READ,holder);
        }
        if (data.get(position).isRead()){
            holder.mStatus.setText("已阅读");
        }else {
            holder.mStatus.setText("未阅读");
        }

        return convertView;
    }

    final int TYPE_NORMAL = 1;
    final int TYPE_HURRY = 2;
    public void setType(int type,VolunteerMessageHolder holder){
        if(type == TYPE_HURRY){
            holder.mType.setText("急助");
        }else{
            holder.mType.setText("助");
        }
    }

    /*final int STATUS_UNREAD = 1;
    final int STATUS_READ = 2;
    public void setStatus(int type,VolunteerMessageHolder holder){
        if(type == STATUS_UNREAD){
            holder.mStatus.setText("未阅读");
            holder.mStatus.setTextColor(context.getResources().getColor(R.color.color_title_green));
        }else if(type == STATUS_READ){
            holder.mStatus.setText("已阅读");
            holder.mStatus.setTextColor(context.getResources().getColor(R.color.font_color_999999));
        }
    }*/
}
