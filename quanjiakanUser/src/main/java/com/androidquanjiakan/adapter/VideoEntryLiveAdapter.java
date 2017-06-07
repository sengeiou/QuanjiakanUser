package com.androidquanjiakan.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.activity.video.VideoEntryActivity;
import com.androidquanjiakan.activity.video.VideoLivePlayActivity;
import com.androidquanjiakan.adapterholder.VideoEntryLiveHolder;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.VideoLiveListEntity;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.androidquanjiakan.view.CircleTransformation;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
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

public class VideoEntryLiveAdapter extends BaseAdapter{

    private List<VideoLiveListEntity> data;
    private Context context;
    private int marginValue;

    public static final int LIVE = 1;
    public static final int DEMAND = 2;
    private int type;

    private LinearLayout.LayoutParams layoutParams;

    public VideoEntryLiveAdapter(List<VideoLiveListEntity> data, Context context, int type) {
        this.data = new ArrayList<>();
        this.data.addAll(data);
        this.context = context;
        marginValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,context.getResources().getDisplayMetrics());
        this.type = type;
    }

    public List<VideoLiveListEntity> getData() {
        return data;
    }

    public void setData(List<VideoLiveListEntity> data) {
        if(this.data==null){
            this.data = new ArrayList<>();
        }else{
            this.data.clear();
        }
        this.data.addAll(data);
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
        final VideoEntryLiveHolder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video_entry_live,null);
            holder = new VideoEntryLiveHolder();

            holder.concern = (ImageView) convertView.findViewById(R.id.concern);
            holder.container = (RelativeLayout) convertView.findViewById(R.id.container);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.title_name = (TextView) convertView.findViewById(R.id.title_name);
            holder.info_line = (RelativeLayout) convertView.findViewById(R.id.info_line);
            holder.header = (ImageView) convertView.findViewById(R.id.header);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.viewer_number = (TextView) convertView.findViewById(R.id.viewer_number);
            convertView.setTag(holder);
        }else{
            holder = (VideoEntryLiveHolder) convertView.getTag();
        }
        final VideoLiveListEntity entity = data.get(position);
        if(type==LIVE){
            layoutParams = (LinearLayout.LayoutParams) holder.container.getLayoutParams();
            if(position%2==0){
                layoutParams.setMargins(0,0,marginValue,0);
            }else{
                layoutParams.setMargins(marginValue,0,0,0);
            }
            holder.container.setLayoutParams(layoutParams);

            holder.status.setText("直播中");
            holder.status.setVisibility(View.VISIBLE);

            if(entity.getImageUrl()!=null && entity.getImageUrl().toLowerCase().startsWith("http")){
                ImageLoadUtil.loadImage(holder.image,entity.getImageUrl(),ImageLoadUtil.optionsNormal);
            }else{
                holder.image.setImageResource(R.color.colorAlphaBlack_33);
            }

            if(entity.getTitle()!=null && entity.getTitle().length()>0){
                holder.title_name.setText(entity.getTitle());
            }else{
                holder.title_name.setText("");
            }

            if(entity.getIcon()!=null && entity.getIcon().toLowerCase().startsWith("http")){
//                ImageLoadUtil.loadImage(holder.header,entity.getIcon(),ImageLoadUtil.optionsCircle);
                Picasso.with(BaseApplication.getInstances()).load(entity.getIcon()).transform(new CircleTransformation()).into(holder.header);
            }else{
                holder.header.setImageResource(R.drawable.ic_patient);
            }

            if(entity.getName()!=null && entity.getName().length()>0){
                holder.name.setText(entity.getName());
            }else{
                holder.name.setText("");
            }

            holder.viewer_number.setText(entity.getLookNum()+"");

//            holder.title_name.setText(data.get(position));
        }else{
            holder.status.setText("点播");
            holder.status.setVisibility(View.GONE);

//            holder.title_name.setText(data.get(position));
        }
        if(entity.getIsFollow()>0){
            holder.concern.setImageResource(R.drawable.icon_focus_on_sel);
        }else{
            holder.concern.setImageResource(R.drawable.icon_focus_on_nor);
        }
        holder.concern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.concern.setEnabled(false);
                if(entity.getIsFollow()>0){
                    MyHandler.putTask((VideoEntryActivity)context,new Task(new HttpResponseInterface() {
                        @Override
                        public void handMsg(String val) {
                            holder.concern.setEnabled(true);
                            if(val!=null && val.toLowerCase().startsWith("{") && val.toLowerCase().endsWith("}")){
                                JsonObject jsonObject =  new GsonParseUtil(val).getJsonObject();
                                if(jsonObject.has("code") && "1".equals(jsonObject.get("code").getAsString())){
                                    entity.setIsFollow(0);
                                    setConcern(entity,holder);
                                }
                            }else{

                            }
                        }
                    }, HttpUrls.postConcern(entity.getUserId(),"0"),null,Task.TYPE_GET_STRING_NOCACHE,null));
                }else{
                    MyHandler.putTask((VideoEntryActivity)context,new Task(new HttpResponseInterface() {
                        @Override
                        public void handMsg(String val) {
                            holder.concern.setEnabled(true);
                            if(val!=null && val.toLowerCase().startsWith("{") && val.toLowerCase().endsWith("}")){
                                JsonObject jsonObject =  new GsonParseUtil(val).getJsonObject();
                                if(jsonObject.has("code") && "1".equals(jsonObject.get("code").getAsString())){
                                    entity.setIsFollow(1);
                                    setConcern(entity,holder);
                                }
                            }else{

                            }
                        }
                    },HttpUrls.postConcern(entity.getUserId(),"1"),null,Task.TYPE_GET_STRING_NOCACHE,null));
                }
            }
        });
        return convertView;
    }

    public void setConcern(VideoLiveListEntity entity,VideoEntryLiveHolder holder){
        if(entity.getIsFollow()>0){
            holder.concern.setImageResource(R.drawable.icon_focus_on_sel);
        }else{
            holder.concern.setImageResource(R.drawable.icon_focus_on_nor);
        }
    }
}
