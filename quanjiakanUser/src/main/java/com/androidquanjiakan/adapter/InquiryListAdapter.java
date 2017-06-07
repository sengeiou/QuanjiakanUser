package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.InquiryListHolder;
import com.androidquanjiakan.entity.InquiryListEntity;
import com.quanjiakan.main.R;

import java.util.List;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public class InquiryListAdapter extends BaseAdapter {

    private Context context;
    private List<InquiryListEntity> data;

    public InquiryListAdapter(Context context,List<InquiryListEntity> iData){
        this.context = context;
        this.data = iData;
    }

    public void setData(List<InquiryListEntity> iData){
        this.data = iData;
    }

    public List<InquiryListEntity> getData(){
        return this.data;
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
        final InquiryListHolder holder;
        final InquiryListEntity inquiryListEntity = data.get(position);
        if(convertView==null){
            holder = new InquiryListHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_inquirylist,null);
            holder.type = (TextView) convertView.findViewById(R.id.type);
            holder.orderid = (TextView) convertView.findViewById(R.id.orderid);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.gander = (TextView) convertView.findViewById(R.id.gander);
            holder.age = (TextView) convertView.findViewById(R.id.age);

            holder.time = (TextView) convertView.findViewById(R.id.time);

            holder.status = (TextView) convertView.findViewById(R.id.status);

            convertView.setTag(holder);
        }else{
            holder = (InquiryListHolder) convertView.getTag();
        }
        setPatientType(holder,inquiryListEntity.getPatient_type());
        holder.orderid.setText(inquiryListEntity.getOrderid());
        holder.name.setText(inquiryListEntity.getName());
        setGander(holder,inquiryListEntity.getSex());
        holder.age.setText(inquiryListEntity.getAge()+"岁");
        holder.time.setText("下单时间:"+inquiryListEntity.getPublishtime());
        setButtonType(holder,inquiryListEntity.getStatus());
        return convertView;
    }

    public void setPatientType(final InquiryListHolder holder,String type){
        if("1".equals(type)){
            holder.type.setText("缓");
            holder.type.setBackgroundResource(R.drawable.selecter_inquiry_green);
        }else if("2".equals(type)){
            holder.type.setText("急");
            holder.type.setBackgroundResource(R.drawable.selecter_inquiry_red);
        }
    }

    public void setGander(final InquiryListHolder holder,String gander){
        if("1".equals(gander)){
            holder.gander.setText("男");
        }else if("2".equals(gander)){
            holder.gander.setText("女");
        }
    }

    public void setButtonType(final InquiryListHolder holder,String status){
        //订单状态: 分为  0.生成   1.发布  2.已抢单, 3.取消订单, 4.重新发布, 5.完成
        //status 1已发布2已抢单3用户取消4已出发5已完成
        if("0".equals(status)){
            holder.status.setText("未付款");
            holder.status.setBackgroundResource(R.drawable.selecter_inquiry_green);
        }else if("1".equals(status)){
            holder.status.setText("已发布");
            holder.status.setBackgroundResource(R.drawable.selecter_inquiry_green);
        }else if("2".equals(status)){
            holder.status.setText("已抢单");
            holder.status.setBackgroundResource(R.drawable.selecter_inquiry_green);
        }else if("3".equals(status)){
            holder.status.setText("用户取消");
            holder.status.setBackgroundResource(R.drawable.selecter_inquiry_grey);
        }else if("4".equals(status)){
            holder.status.setText("已出发");
            holder.status.setBackgroundResource(R.drawable.selecter_inquiry_green);
        }else if("5".equals(status)){
            holder.status.setText("已完成");
            holder.status.setBackgroundResource(R.drawable.selecter_inquiry_grey);
        }
    }
}
