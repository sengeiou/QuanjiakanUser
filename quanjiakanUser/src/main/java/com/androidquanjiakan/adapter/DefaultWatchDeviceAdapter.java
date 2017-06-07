package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.entity.DefaultWatchEntity;
import com.androidquanjiakan.view.RoundTransformDesign;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by Gin on 2017/2/23.
 */

public class DefaultWatchDeviceAdapter extends BaseAdapter {
    private Context context;
    private List<DefaultWatchEntity> list;
    public  DefaultWatchDeviceAdapter(Context context,List<DefaultWatchEntity> list){
        this.context=context;
        this.list=list;
    }

    public void setList(List<DefaultWatchEntity> list) {
        this.list = list;
    }

    @Override

    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null) {
            holder=new ViewHolder();
            view= LayoutInflater.from(context).inflate(R.layout.item_default_device,null);
            holder.icon=(ImageView) view.findViewById(R.id.iv_icon);
            holder.name= (TextView) view.findViewById(R.id.tv_name);
            holder.deviceNumber= (TextView) view.findViewById(R.id.tv_device_number);
            holder.iv_is_select= (ImageView) view.findViewById(R.id.iv_selected);
            view.setTag(holder);

        }else {
            holder= (ViewHolder) view.getTag();

        }

        if (list.get(i).getName().contains("%")) {
            try {
                holder.name.setText(URLDecoder.decode(list.get(i).getName(),"utf-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }else {
            holder.name.setText(list.get(i).getName());
        }

        holder.deviceNumber.setText(list.get(i).getDeviceNumber());
        if (list.get(i).getIcon().length()<1) {
            Picasso.with(context).load(R.drawable.index_portrait_old).transform(new RoundTransformDesign(15)).into(holder.icon);
        }else {
            Picasso.with(context).load(list.get(i).getIcon()).transform(new RoundTransformDesign(15)).into(holder.icon);
        }

        if(list.get(i).isSelect()) {
            holder.iv_is_select.setVisibility(View.VISIBLE);
        }else {
            holder.iv_is_select.setVisibility(View.INVISIBLE);
        }


        return view;
    }


    class ViewHolder{
        private ImageView icon;
        private TextView  name;
        private TextView deviceNumber;
        private ImageView iv_is_select;


    }


}
