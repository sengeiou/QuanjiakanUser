package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.DeviceList_SceneHolder;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.quanjiakan.main.R;

import java.util.List;

/**
 * Created by Administrator on 2016/7/30 0030.
 */
public class DeviceListAdapter_Scene extends BaseAdapter {

    private Context context;
    private List<BindDeviceEntity> data;

    public DeviceListAdapter_Scene(Context context, List<BindDeviceEntity> data){
        this.context = context;
        this.data = data;
    }

    public List<BindDeviceEntity> getData(){
        return data;
    }

    public void setData(List<BindDeviceEntity> data){
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
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final DeviceList_SceneHolder holder;
        final BindDeviceEntity entity = data.get(i);
        if(view==null){
            holder = new DeviceList_SceneHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_device_list_scene,null);
            holder.image = (ImageView) view.findViewById(R.id.image);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_scene = (TextView) view.findViewById(R.id.tv_scene);
            holder.divider = view.findViewById(R.id.divider);
            view.setTag(holder);
        }else{
            holder = (DeviceList_SceneHolder) view.getTag();
        }
        ImageLoadUtil.loadImage(holder.image,entity.getIcon(),ImageLoadUtil.optionsRoundCorner_Person);
        holder.tv_name.setText(entity.getName());
        return view;
    }
}
