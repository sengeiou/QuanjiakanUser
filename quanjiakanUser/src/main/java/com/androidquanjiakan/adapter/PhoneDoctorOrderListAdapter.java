package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.PhoneDoctorOrderListHolder;
import com.androidquanjiakan.entity.PhoneDoctorRecordEntity;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.quanjiakan.main.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/9/7 0007.
 */
public class PhoneDoctorOrderListAdapter extends BaseAdapter {

    private Context context;
    private List<PhoneDoctorRecordEntity> data;
    private String chargeValue;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int timeCount;

    public PhoneDoctorOrderListAdapter(Context context,List<PhoneDoctorRecordEntity> data,String charge){
        this.context = context;
        this.data = new ArrayList<>();
        if(data!=null){
            this.data.addAll(data);
        }
        this.chargeValue = charge;
    }

    public List<PhoneDoctorRecordEntity> getData() {
        return data;
    }

    public void setData(List<PhoneDoctorRecordEntity> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    public void setChargeValue(String price){
        this.chargeValue = price;
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
        final PhoneDoctorOrderListHolder holder;
        if(convertView==null){
            holder = new PhoneDoctorOrderListHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_phone_doctor_list,null);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_clinic_title = (TextView) convertView.findViewById(R.id.tv_clinic_title);
            holder.tv_charge = (TextView) convertView.findViewById(R.id.tv_charge);
            holder.tv_starttime = (TextView) convertView.findViewById(R.id.tv_starttime);
            holder.end_time = (TextView) convertView.findViewById(R.id.end_time);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        }else{
            holder = (PhoneDoctorOrderListHolder) convertView.getTag();
        }
        holder.tv_name.setText("儿童医生");
        //2表示订单已经支付过了，1：表示订单未支付 -----status
        if(data.get(position)!=null && data.get(position).getTotal_price()!=null){
            holder.tv_charge.setText("服务费用:"+data.get(position).getTotal_price()+"/年");
        }else{
            holder.tv_charge.setText("服务费用:199/年");
        }

        ImageLoadUtil.loadImage(holder.image,data.get(position).getIcon(),ImageLoadUtil.optionsCircle);

        /*String startT = data.get(position).getCreatetime();
        if(startT!=null && startT.length()>18){
            holder.tv_starttime.setText("订购时间:"+*//*startT.substring(0,19)*//*sdf.format(new Date(Long.parseLong(data.get(position).getStarttime()))));
            holder.end_time.setText("使用期限至:"+*//*(Integer.parseInt(startT.substring(0,4))+1)+startT.substring(4,19)*//*sdf.format(new Date(Long.parseLong(data.get(position).getFinishtime()))));
        }else{
            holder.tv_starttime.setText("订购时间:"+startT);
            holder.end_time.setText("使用期限至:"+(Integer.parseInt(startT.substring(0,4))+1)+startT.substring(4));
        }*/

        holder.tv_starttime.setText("订购时间:"+/*startT.substring(0,19)*/sdf.format(new Date(Long.parseLong(data.get(position).getStarttime()))));
        holder.end_time.setText("使用期限至:"+/*(Integer.parseInt(startT.substring(0,4))+1)+startT.substring(4,19)*/sdf.format(new Date(Long.parseLong(data.get(position).getFinishtime()))));

//        holder.end_time.setText("使用期限:");

        if("1".equals(data.get(position).getStatus())){
            holder.status.setText("未支付");
            holder.status.setBackgroundResource(R.drawable.selecter_inquiry_green);
        }else if("2".equals(data.get(position).getStatus())){
            holder.status.setText("已订购");
            holder.status.setBackgroundResource(R.drawable.selecter_inquiry_green);
        }else{
            holder.status.setText("已过期");
            holder.status.setBackgroundResource(R.drawable.selecter_inquiry_grey);
        }
        return convertView;
    }
}
