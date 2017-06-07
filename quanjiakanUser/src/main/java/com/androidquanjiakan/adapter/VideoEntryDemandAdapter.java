package com.androidquanjiakan.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.activity.video.VideoDemandCommendListActivity;
import com.androidquanjiakan.activity.video.VideoDemandPlayActivity;
import com.androidquanjiakan.activity.video.VideoEntryActivity;
import com.androidquanjiakan.adapterholder.VideoEntryDemandHolder;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.VideoDemandListEntity;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public class VideoEntryDemandAdapter extends BaseAdapter {

    private List<VideoDemandListEntity> data;
    private Context context;

    public static final int LIVE = 1;
    public static final int DEMAND = 2;
    private int type;

    public VideoEntryDemandAdapter(List<VideoDemandListEntity> data, Context context, int type) {
        this.data = new ArrayList<>();
        this.data.addAll(data);
        this.context = context;
        this.type = type;
        LogUtil.e("" + data.size() + "    ******  LIVE?" + (LIVE == type));
    }

    public List<VideoDemandListEntity> getData() {
        return data;
    }

    public void setData(List<VideoDemandListEntity> data) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        } else {
            this.data.clear();
        }
        this.data.addAll(data);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final VideoEntryDemandHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video_entry_demand, null);
            holder = new VideoEntryDemandHolder();
            holder.container = (LinearLayout) convertView.findViewById(R.id.container);
            holder.divider_line = convertView.findViewById(R.id.divider_line);

            holder.header = (ImageView) convertView.findViewById(R.id.header);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.info_line = (RelativeLayout) convertView.findViewById(R.id.info_line);
            holder.content = (TextView) convertView.findViewById(R.id.content);


            holder.image_line = (RelativeLayout) convertView.findViewById(R.id.image_line);
            holder.video_image = (ImageView) convertView.findViewById(R.id.video_image);
            holder.video_play = (ImageView) convertView.findViewById(R.id.video_play);

            holder.number_value = (TextView) convertView.findViewById(R.id.number_value);
            holder.praise_number = (TextView) convertView.findViewById(R.id.praise_number);
            holder.depraise_number = (TextView) convertView.findViewById(R.id.depraise_number);
            holder.number_line = (RelativeLayout) convertView.findViewById(R.id.number_line);

            convertView.setTag(holder);
        } else {
            holder = (VideoEntryDemandHolder) convertView.getTag();
        }
        final VideoDemandListEntity entity = data.get(position);
        if(entity.getHeadIcon()!=null && entity.getHeadIcon().toLowerCase().startsWith("http")){
            ImageLoadUtil.loadImage(holder.header,entity.getHeadIcon(),ImageLoadUtil.optionsCircle);
            holder.video_play.setVisibility(View.VISIBLE);
        }else{
            holder.header.setImageResource(R.drawable.ic_patient);
            holder.video_play.setVisibility(View.GONE);
        }
        if(entity.getName()!=null){
            holder.name.setText(entity.getName());
        }else{
            holder.name.setText("数据中未含有该字段");
        }
        holder.time.setText(entity.getFormatTimeString());

        holder.content.setText(entity.getContent());
        //加载会存在图片切换时的闪动
        if(entity.getImageUrl()!=null && entity.getImageUrl().toLowerCase().startsWith("http")){
//            ImageLoadUtil.loadImage(holder.video_image,entity.getImageUrl(),null);
            Picasso.with(context).
                    load(entity.getImageUrl()).
                    error(R.drawable.ic_empty).
                    /*transform(new VolunteerMesAdapter.CircleTransformation()).*/
                            into(holder.video_image);
        }else{
            holder.video_image.setImageResource(R.drawable.ic_empty);
        }
        holder.image_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,VideoDemandPlayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "");
                //TODO 这里传入接口获取的视频地址
                bundle.putString("URI", /*inputServer.getText().toString()*/
//                        "http://200048939.vod.myqcloud.com/200048939_2cdbd156b6cc11e6ad39991f76a4df69.f20.mp4"
                        data.get(position).getVedioUrl()
                );
                bundle.putInt("decode_type", 0);//3. 设置缺省编码类型：0表示硬解；1表示软解；
                bundle.putString("id",data.get(position).getId()+"");
                intent.putExtras(bundle);
                ((VideoEntryActivity)context).startActivityForResult(intent, VideoEntryActivity.REQUEST_DEMAND);
            }
        });

        holder.number_value.setText(entity.getPlayCount()+"人浏览");
        holder.depraise_number.setText(entity.getAppraiseCount()+"");
        holder.depraise_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoDemandCommendListActivity.class);
                intent.putExtra(VideoDemandCommendListActivity.PARAMS_VIDEO_ID,data.get(position).getId()+"");//点播的视频ID
                intent.putExtra(VideoDemandCommendListActivity.PARAMS_VIDEO_ENTITY,data.get(position));
                ((VideoEntryActivity)context).startActivityForResult(intent,VideoEntryActivity.REQUEST_DEMAND_COMMEND);
            }
        });

        holder.praise_number.setText(""+entity.getLikeCount());
        if(entity.getLikes()==0){
            Drawable drawable= context.getResources()
                    .getDrawable(R.drawable.icon_streaming_like_nor);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            holder.praise_number.setCompoundDrawables(drawable,null,null,null);
        }else{
            Drawable drawable= context.getResources()
                    .getDrawable(R.drawable.icon_streaming_like_sel);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            holder.praise_number.setCompoundDrawables(drawable,null,null,null);
        }
        holder.praise_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.praise_number.setEnabled(false);
                if(entity.getLikes()==0){
                    MyHandler.putTask(((VideoEntryActivity)context),new Task(new HttpResponseInterface() {
                        @Override
                        public void handMsg(String val) {
                            holder.praise_number.setEnabled(true);
                            if(val!=null && val.length()>0){
                                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                                if(jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull)
                                        && "200".equals(jsonObject.get("code").getAsString())){
                                    Drawable drawable= context.getResources()
                                            .getDrawable(R.drawable.icon_streaming_like_sel);
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                            drawable.getMinimumHeight());
                                    holder.praise_number.setCompoundDrawables(drawable,null,null,null);

                                    entity.setLikes(1);
                                    entity.setMemberId(Integer.parseInt(BaseApplication.getInstances().getUser_id()));
                                    /**
                                     * 重置显示数量
                                     */
                                    if(jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull) &&
                                            jsonObject.get("object").getAsJsonObject().has("likeCount")){
                                        entity.setLikeCount(jsonObject.get("object").getAsJsonObject().get("likeCount").getAsInt());
                                    }else{
                                        entity.setLikeCount(entity.getLikeCount()+1);
                                    }
                                    holder.praise_number.setText(""+entity.getLikeCount());
                                }
                            }
                        }
                    }, HttpUrls.getDemandVideoAddPraiseNumber()+"&memberId="+
                            BaseApplication.getInstances().getUser_id()+
                            "&id="+entity.getId()+"&likes=1",null,Task.TYPE_GET_STRING_NOCACHE,null));
                }else{
                    MyHandler.putTask(((VideoEntryActivity)context),new Task(new HttpResponseInterface() {
                        @Override
                        public void handMsg(String val) {
                            holder.praise_number.setEnabled(true);
                            if(val!=null && val.length()>0){
                                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                                if(jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull)
                                        && "200".equals(jsonObject.get("code").getAsString())){
                                    Drawable drawable= context.getResources()
                                            .getDrawable(R.drawable.icon_streaming_like_nor);
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                            drawable.getMinimumHeight());
                                    holder.praise_number.setCompoundDrawables(drawable,null,null,null);

                                    entity.setLikes(0);
                                    entity.setMemberId(Integer.parseInt(BaseApplication.getInstances().getUser_id()));
                                    /**
                                     * 重置显示数量
                                     */
                                    if(jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull) &&
                                            jsonObject.get("object").getAsJsonObject().has("likeCount")){
                                        entity.setLikeCount(jsonObject.get("object").getAsJsonObject().get("likeCount").getAsInt());
                                    }else{
                                        entity.setLikeCount(entity.getLikeCount()-1);
                                    }
                                    holder.praise_number.setText(""+entity.getLikeCount());
                                }
                            }
                        }
                    }, HttpUrls.getDemandVideoAddPraiseNumber()+"&memberId="+
                            BaseApplication.getInstances().getUser_id()+
                            "&id="+entity.getId()+"&likes=0",null,Task.TYPE_GET_STRING_NOCACHE,null));
                }
            }
        });
        return convertView;
    }

}
