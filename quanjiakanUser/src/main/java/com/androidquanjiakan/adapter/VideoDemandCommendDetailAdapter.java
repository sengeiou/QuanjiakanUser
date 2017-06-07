package com.androidquanjiakan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquanjiakan.activity.video.VideoDemandCommendDetailActivity;
import com.androidquanjiakan.entity.VideoCommendSubItemEntity;
import com.quanjiakan.main.R;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21 0021.
 */

public class VideoDemandCommendDetailAdapter extends BaseAdapter {
    private Context context;
    private List<VideoCommendSubItemEntity> data;

    public VideoDemandCommendDetailAdapter(Context context, List<VideoCommendSubItemEntity> data) {
        this.context = context;
        this.data = data;
    }

    public List<VideoCommendSubItemEntity> getData() {
        return data;
    }

    public void setData(List<VideoCommendSubItemEntity> data) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video_demand_more_commend,null);
            holder.more_commend_content = (TextView) convertView.findViewById(R.id.more_commend_content);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        inputMoreCommendContent(holder.more_commend_content,data.get(position));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((VideoDemandCommendDetailActivity)context).callCommend(data.get(position).getName(), data.get(position).getRootAppraiseId() + "", data.get(position).getId() + "", "1");
            }
        });
        return convertView;
    }

    public void inputMoreCommendContent(TextView container, VideoCommendSubItemEntity inputData) {
        //需要根据关系来进行判断
        //判断是否是楼主，若是则添加一个楼主标识
        //回复内容格式  谁[根据是否是楼主添加标识图] 回复 @谁[根据是否是楼主添加标识图]: 回复内容
        String string = (inputData.getName() + "") + " 回复 " + "@" + (inputData.getToname() + "") + ": " + inputData.getContent();
        SpannableString span = new SpannableString(string);
        /**
         * TODO 若要区分点击，此处需要更换类型
         */
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#999999")), 0, (inputData.getName() + "").length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), (inputData.getName() + "").length(), ((inputData.getName() + "") + " 回复 ").length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        /**
         * TODO 若要区分点击，此处需要更换类型
         */
        span.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.color_title_green)), ((inputData.getName() + "") + " 回复 ").length(),
                ((inputData.getName() + "") + " 回复 " + "@" + (inputData.getToname() + "")).length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), ((inputData.getName() + "") + " 回复 " + "@" + (inputData.getToname() + "")).length(),
                string.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        container.setText(span);
    }

    class ViewHolder{
        public TextView more_commend_content;
    }
}
