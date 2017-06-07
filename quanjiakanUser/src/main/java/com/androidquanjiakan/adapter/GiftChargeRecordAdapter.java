package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.GiftChargeRecordHolder;
import com.androidquanjiakan.entity.GiftChargeEntity;
import com.quanjiakan.main.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/19 0019.
 */

public class GiftChargeRecordAdapter extends BaseAdapter {
    private Context context;
    private List<GiftChargeEntity> data;

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GiftChargeRecordAdapter(Context context, List<GiftChargeEntity> data) {
        this.context = context;
        this.data = data;
    }

    public List<GiftChargeEntity> getData() {
        return data;
    }

    public void setData(List<GiftChargeEntity> data) {
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
        final GiftChargeRecordHolder holder;
        final GiftChargeEntity  entity = data.get(position);
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gift_charge_record,null);
            holder = new GiftChargeRecordHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.div = convertView.findViewById(R.id.div);
            holder.tv_describe = (TextView) convertView.findViewById(R.id.tv_describe);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            convertView.setTag(holder);
        }else{
            holder = (GiftChargeRecordHolder) convertView.getTag();
        }
        holder.tv_describe.setText("充值了"+entity.getEbeans()+"个E豆");
        holder.tv_time.setText(timeFormat.format(new Date(entity.getRechargeTime())));
        if(entity.getStatus()==0){//成功？
            holder.tv_status.setText("交易失败");
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
        }else if(entity.getStatus()==1){
            holder.tv_status.setText("交易成功");
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.color_title_green));
        }else{
            holder.tv_status.setText("交易失败");
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
        }
        return convertView;
    }
}
