package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.DeviceContainerHolder;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.androidquanjiakan.view.MaterialBadgeTextView;
import com.androidquanjiakan.view.RoundImageView;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by Administrator on 2017/2/22 0022.
 */

public class DeviceContainerAdapter extends BaseAdapter {

    private List<BindDeviceEntity> devices;
    private Context mContext;

    private int selectedPosition;

    public DeviceContainerAdapter(Context context, List<BindDeviceEntity> data) {
        this.mContext = context;
        this.devices = data;
        selectedPosition = 0;
    }

    public void setDevices(List<BindDeviceEntity> data) {
        this.devices = data;
    }


    public List<BindDeviceEntity> getDevices() {
        return devices;
    }

    @Override
    public int getCount() {
        if (devices != null) {
            return devices.size();
        }
        return 0;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
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
        final DeviceContainerHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_container, null);
            holder = new DeviceContainerHolder();
            holder.bg = (RoundImageView) convertView.findViewById(R.id.bg);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.notice = (MaterialBadgeTextView) convertView.findViewById(R.id.notice);

            convertView.setTag(holder);
        } else {
            holder = (DeviceContainerHolder) convertView.getTag();
        }
        showVoiceNotice(holder, position);
        if (devices != null && devices.get(position).getIcon() != null && devices.get(position).getIcon().toLowerCase().startsWith("http")) {
            if (position == selectedPosition) {
                setDefault(holder);
            } else {
                setNormal(holder);
            }
            //.transform(new RoundTransformDesign(15))
            Picasso.with(mContext).load(devices.get(position).getIcon()).into(holder.bg);
        } else {
            if (position == selectedPosition) {
                setDefault(holder);
            } else {
                setNormal(holder);
            }
            //.transform(new RoundTransform())
            if ("1".equals(devices.get(position).getW_TYPE())) {
                Picasso.with(mContext).load(R.drawable.index_portrait_children).into(holder.bg);
            } else if ("0".equals(devices.get(position).getW_TYPE())) {
                Picasso.with(mContext).load(R.drawable.index_portrait_old).into(holder.bg);
            } else {
                Picasso.with(mContext).load(R.drawable.index_portrait_old).into(holder.bg);
            }
        }
        if (devices.get(position).getName() != null && devices.get(position).getName().contains("%")) {
            try {
                holder.name.setText(URLDecoder.decode(devices.get(position).getName(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            holder.name.setText(devices.get(position).getName());
        }
        return convertView;
    }

    private void setDefault(DeviceContainerHolder holder) {
        holder.name.setBackgroundResource(R.drawable.selecter_device_container_default);
    }

    private void setNormal(DeviceContainerHolder holder) {
        holder.name.setBackgroundResource(R.drawable.selecter_device_container_normal);
    }

    private void showVoiceNotice(DeviceContainerHolder holder, int position) {
        int value = BaseApplication.getInstances().getKeyNumberValue(BaseApplication.getInstances().getUser_id() + "_" + devices.get(position).getDeviceid() + "_unread");
        if (value > 0) {
            holder.notice.setVisibility(View.VISIBLE);
            holder.notice.setHighLightMode(true);
        } else {
            holder.notice.setVisibility(View.INVISIBLE);
        }

    }
}
