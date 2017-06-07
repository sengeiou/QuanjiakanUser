package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.entity.LeavingMessageInfo;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/18.
 */

public class DescribleAdapter extends BaseAdapter {


    private List<LeavingMessageInfo> mData;
    private Context mContext;

    public DescribleAdapter(Context mContext, List<LeavingMessageInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public List<LeavingMessageInfo> getmData() {
        return mData;
    }

    public void setmData(List<LeavingMessageInfo> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        if (null != mData) {
            return mData.size();
        }
        return -1;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_volunteer_msg, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvVolunName.setText(mData.get(position).getVolName());
        Picasso.with(mContext).load(mData.get(position).getPicture()).into(holder.ivIcon);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = format.format(new Date(mData.get(position).getCreateTime()));
        holder.tvFindTime.setText(time);
        holder.tvDescribe.setText(mData.get(position).getContent());
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_volun_name)
        TextView tvVolunName;
        @BindView(R.id.tv_find_time)
        TextView tvFindTime;
        @BindView(R.id.tv_describe)
        TextView tvDescribe;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
