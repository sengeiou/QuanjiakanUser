package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.MultiItemHolder;
import com.androidquanjiakan.entity.MultiItemEntity;
import com.quanjiakan.main.R;

import java.util.List;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class MultiItemAdapter extends BaseAdapter {

    private Context context;
    private List<MultiItemEntity> data;

    public MultiItemAdapter(Context context, List<MultiItemEntity> data){
        this.context = context;
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
        final MultiItemHolder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_entry_gridview,null);
            holder=new MultiItemHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }else{
            holder = (MultiItemHolder) convertView.getTag();
        }
        holder.img.setImageResource(data.get(position).getImg());
        holder.name.setText(data.get(position).getName());
        return convertView;
    }
}
