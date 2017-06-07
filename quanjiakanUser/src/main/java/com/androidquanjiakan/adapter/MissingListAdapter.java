package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.MissingListHolder;
import com.quanjiakan.main.R;

import java.util.List;

/**
 * Created by Administrator on 2016/11/18 0018.
 */

public class MissingListAdapter extends BaseAdapter{

    private List<String> data;
    private Context context;

    public MissingListAdapter(List<String> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
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
        MissingListHolder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_miss_list,null);
            holder = new MissingListHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.publish_time = (TextView) convertView.findViewById(R.id.publish_time);
            holder.gender_value = (TextView) convertView.findViewById(R.id.gender_value);
            holder.age_value = (TextView) convertView.findViewById(R.id.age_value);
            holder.height_value = (TextView) convertView.findViewById(R.id.height_value);
            holder.weight_value = (TextView) convertView.findViewById(R.id.weight_value);
            holder.missing_time_value = (TextView) convertView.findViewById(R.id.missing_time_value);
            holder.child_img = (ImageView) convertView.findViewById(R.id.child_img);
            convertView.setTag(holder);
        }else{
            holder = (MissingListHolder) convertView.getTag();
        }
        /**
         * 儿童名字
         */
        holder.name.setText("寻人-"+data.get(position));
        /**
         * 发布信息
         */
        holder.publish_time.setText("");

        /**
         * 儿童信息
         */
        holder.gender_value.setText("");
        holder.age_value.setText("");
        holder.height_value.setText("");
        holder.weight_value.setText("");
        holder.missing_time_value.setText("");

//        holder.child_img//图片架子啊使用圆角矩形

        return convertView;
    }
}
