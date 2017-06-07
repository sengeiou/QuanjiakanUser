package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.InsureDeviceHolder;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.quanjiakan.main.R;

import java.util.List;

/**
 * Created by Administrator on 2016/7/18 0018.
 */
public class InsureDeviceAdapter extends BaseAdapter {
    private Context context;
    private List<BindDeviceEntity> data;
    public InsureDeviceAdapter(Context context,List<BindDeviceEntity> data){
        this.context = context;
        this.data = data;
    }

    public List<BindDeviceEntity> getData(){
        return data;
    }

    @Override
    public int getCount() {
        if(data!=null){
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        InsureDeviceHolder holder;
        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.dialog_popup_list_item,null);
            holder = new InsureDeviceHolder();
            holder.head_icon = (ImageView) view.findViewById(R.id.head_icon);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.device_id = (TextView) view.findViewById(R.id.device_id);
            view.setTag(holder);
        }else{
            holder = (InsureDeviceHolder) view.getTag();
        }
        if(data.get(i).getIcon()!=null && data.get(i).getIcon().length()>0){
            ImageLoadUtil.loadImage(holder.head_icon,data.get(i).getIcon(), ImageLoadUtil.optionsCircle);
        }else{
            holder.head_icon.setVisibility(View.GONE);
        }
        if(data.get(i).getName()!=null && data.get(i).getName().length()>0){
            holder.name.setText(data.get(i).getName());
        }else{
            holder.name.setText("");
        }
        if(data.get(i).getDeviceid()!=null && data.get(i).getDeviceid().length()>0){
            holder.device_id.setText(data.get(i).getDeviceid());
        }else{
            holder.device_id.setText("");
        }
        return view;
    }
}
