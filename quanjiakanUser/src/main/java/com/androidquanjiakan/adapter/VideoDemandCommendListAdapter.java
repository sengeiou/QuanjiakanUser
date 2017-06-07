package com.androidquanjiakan.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.activity.video.VideoDemandCommendDetailActivity;
import com.androidquanjiakan.activity.video.VideoDemandCommendListActivity;
import com.androidquanjiakan.adapterholder.VideoDemandCommendListHolder;
import com.androidquanjiakan.entity.VideoCommendListEntity;
import com.androidquanjiakan.entity.VideoCommendSubItemEntity;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.view.CircleTransformation;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/15 0015.
 */

public class VideoDemandCommendListAdapter extends BaseAdapter {
    private List<VideoCommendListEntity> data;
    private Context context;

    private SimpleDateFormat sdf;

    private VideoDemandCommendListActivity.CommendCallBack callBack;

    public VideoDemandCommendListAdapter(List<VideoCommendListEntity> data, Context context,VideoDemandCommendListActivity.CommendCallBack callBack) {
        this.data = data;
        this.context = context;
        sdf = new SimpleDateFormat("MM-dd HH:mm");
        this.callBack = callBack;
    }

    public List<VideoCommendListEntity> getData() {
        return data;
    }

    public void setData(List<VideoCommendListEntity> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        if (data != null) {
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
        VideoDemandCommendListHolder holder;
        if (convertView == null) {
            holder = new VideoDemandCommendListHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video_demand_single_commend, null);
            initHolder(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (VideoDemandCommendListHolder) convertView.getTag();
        }
        final VideoCommendListEntity entity = data.get(position);
        /**
         * 由于没有该头像字段，暂时显示默认
         */
        holder.more.setVisibility(View.GONE);
        Picasso.with(context).load(R.drawable.touxiang_big_icon).transform(new CircleTransformation()).into(holder.user_header);

        holder.name.setText(entity.getName());
        holder.time.setText(sdf.format(new Date(entity.getCreatetime())));
        holder.floor.setText((position+1) + "楼");
        holder.content.setText(entity.getContent());
        holder.content_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击回复该评论人
                callBack.callCommend(entity.getName(),entity.getId()+"",entity.getId()+"","1");
            }
        });
        showMoreCommend(holder, entity.getChildAppraises(),position);
        return convertView;
    }

    public void initHolder(VideoDemandCommendListHolder holder, View parent) {
        //信息栏
        holder.content_line = (RelativeLayout) parent.findViewById(R.id.content_line);
        holder.user_header = (ImageView) parent.findViewById(R.id.user_header);
        holder.name = (TextView) parent.findViewById(R.id.name);
        holder.time = (TextView) parent.findViewById(R.id.time);
        holder.floor = (TextView) parent.findViewById(R.id.floor);
        holder.more = (ImageView) parent.findViewById(R.id.more);
        //
        holder.content = (TextView) parent.findViewById(R.id.content);
        //更多评论的分割条
        holder.commend_div = parent.findViewById(R.id.commend_div);
        //更多评论的容器
        holder.more_commend = (LinearLayout) parent.findViewById(R.id.more_commend);
        //更多评论详情入口
        holder.detail_info = (TextView) parent.findViewById(R.id.detail_info);
        holder.bottom_div = parent.findViewById(R.id.bottom_div);
    }

    /**
     * 判断是否显示更多评论
     *
     * @param holder
     * @param data   评论列表
     */
    public void showMoreCommend(VideoDemandCommendListHolder holder, final List<VideoCommendSubItemEntity> data,final int position) {
        if (data == null || data.size() < 1) {
            holder.commend_div.setVisibility(View.GONE);
            holder.more_commend.setVisibility(View.GONE);
            holder.detail_info.setVisibility(View.GONE);
        } else {
            //至少有一条评论
            holder.commend_div.setVisibility(View.VISIBLE);
            holder.more_commend.setVisibility(View.VISIBLE);

            //判断 是否显示更多评论的入口
            if (data.size() > 2) {
                holder.detail_info.setVisibility(View.VISIBLE);
                holder.detail_info.setText("更多" + (data.size()-2) + "条回复...");
                holder.detail_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转到评论详情页面
                        Intent intent = new Intent(context,VideoDemandCommendDetailActivity.class);
                        intent.putExtra(VideoDemandCommendDetailActivity.PARAMS_CID,data.get(0).getRootAppraiseId()+"");
                        intent.putExtra(VideoDemandCommendDetailActivity.PARAMS_VID,data.get(0).getIdeoDemandId()+"");
                        intent.putExtra(VideoDemandCommendDetailActivity.PARAMS_FID,position+"");
                        intent.putExtra(VideoDemandCommendListActivity.PARAMS_VIDEO_ENTITY,((VideoDemandCommendListActivity)context).getEntity());
                        ((VideoDemandCommendListActivity)context).startActivityForResult(intent,VideoDemandCommendListActivity.REQUEST_DETAIL);
                    }
                });
            } else {
                holder.detail_info.setVisibility(View.GONE);
            }
            //注入评论内容
            addMoreCommend(holder.more_commend, data);
        }
    }

    /**
     * 注入评论
     *
     * @param container
     * @param data
     */
    public void addMoreCommend(LinearLayout container,final List<VideoCommendSubItemEntity> data) {
        if (container != null) {
            container.removeAllViews();//清除原子View，防止复用造成的数据混乱
            int size = (data.size() > 2 ? 2 : data.size());
            for (int i = 0; i < size; i++) {
                final int position = i;
                View moreCommend = LayoutInflater.from(context).inflate(R.layout.item_video_demand_more_commend, null);
                TextView content = (TextView) moreCommend.findViewById(R.id.more_commend_content);
                inputMoreCommendContent(content, data.get(i));
                moreCommend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击回复该评论的发送人
                        callBack.callCommend(data.get(position).getName(),data.get(position).getRootAppraiseId()+"",data.get(position).getId()+"","1");
                    }
                });
                container.addView(moreCommend);
            }
        }
    }

    /**
     * 展示评论的实际内容
     *
     * @param container
     * @param inputData
     */
    public void inputMoreCommendContent(TextView container, VideoCommendSubItemEntity inputData) {
        //需要根据关系来进行判断
        //判断是否是楼主，若是则添加一个楼主标识
        //回复内容格式  谁[根据是否是楼主添加标识图] 回复 @谁[根据是否是楼主添加标识图]: 回复内容
        String string = (inputData.getName()+"") + " 回复 " + "@" + (inputData.getToname()+"") + ": " + inputData.getContent();
        SpannableString span = new SpannableString(string);
        /**
         * TODO 若要区分点击，此处需要更换类型
         */
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#999999")), 0, (inputData.getName()+"").length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), (inputData.getName()+"").length(), ((inputData.getName()+"") + " 回复 ").length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        /**
         * TODO 若要区分点击，此处需要更换类型
         */
        span.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.color_title_green)), ((inputData.getName()+"") + " 回复 ").length(),
                ((inputData.getName()+"") + " 回复 " + "@" + (inputData.getToname()+"")).length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), ((inputData.getName()+"") + " 回复 " + "@" + (inputData.getToname()+"")).length(),
                string.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        container.setText(span);
    }

}
