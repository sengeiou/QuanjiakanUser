package com.androidquanjiakan.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.activity.setting.concern.ConcernListActivity;
import com.androidquanjiakan.activity.video.VideoEntryActivity;
import com.androidquanjiakan.activity.video.VideoLivePlayActivity;
import com.androidquanjiakan.adapterholder.ConcernListHolder;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.ConcernEntity;
import com.androidquanjiakan.entity.VideoLiveListEntity;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.view.CircleTransformation;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2016/12/22 0022.
 */

public class ConcernListAdapter extends BaseAdapter {
    private Context context;
    private List<ConcernEntity> data;

    public ConcernListAdapter(Context context, List<ConcernEntity> data) {
        this.context = context;
        this.data = data;
    }

    public List<ConcernEntity> getData() {
        return data;
    }

    public void setData(List<ConcernEntity> data) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ConcernListHolder holder;
        if (convertView == null) {
            holder = new ConcernListHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_concern_person, null);
            initHolder(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ConcernListHolder) convertView.getTag();
        }
        //
        final ConcernEntity entity = data.get(position);

        if (entity != null && entity.getDoctorName() != null) {
            holder.name.setText(entity.getDoctorName());
        } else {
            holder.name.setText("医生未设置名称");
        }



        if (entity != null && entity.getIcon() != null && entity.getIcon().toLowerCase().startsWith("http")) {
            ImageLoadUtil.picassoLoad(holder.header, entity.getIcon(), ImageLoadUtil.TYPE_CYCLE);
            LogUtil.e("Net Image");
        } else {
            Picasso.with(BaseApplication.getInstances()).load(R.drawable.touxiang_big_icon).
                    transform(new CircleTransformation()).into(holder.header);
            LogUtil.e("Local Image");
        }
        if (entity != null) {
            holder.fans.setText("粉丝  " + entity.getFollowNum());
        } else {
            holder.fans.setText("粉丝  " + 1);
        }

        setStatus(position, holder, true);

        if(entity!=null){
            if(entity.getState()==1){
                liveStatus(holder, true);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final VideoLiveListEntity entity = new VideoLiveListEntity();
                        entity.setIsFollow(1);
                        entity.setUserId(data.get(position).getAnchorId()+"");
                        final String name = data.get(position).getDoctorName();
                        final String img = data.get(position).getIcon();
                        final String docID = data.get(position).getAnchorId()+"";
                        MyHandler.putTask((ConcernListActivity)context,new Task(new HttpResponseInterface() {
                            @Override
                            public void handMsg(String val) {
                                try{
                                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                                    if(jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull)){

                                        if(jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull)
                                                && jsonObject.get("object").getAsJsonObject()!=null  &&
                                                "1".equals(jsonObject.get("code").getAsString())){

                                            if(jsonObject.get("object").getAsJsonObject().has("isFollow") && !(jsonObject.get("object").getAsJsonObject().get("isFollow") instanceof JsonNull)){
                                                entity.setIsFollow(jsonObject.get("object").getAsJsonObject().get("isFollow").getAsLong());
                                            }else{
                                                entity.setIsFollow(0);
                                            }

                                            if(jsonObject.get("object").getAsJsonObject().has("state") &&
                                                    !(jsonObject.get("object").getAsJsonObject().get("state") instanceof JsonNull) &&
                                                    (1==jsonObject.get("object").getAsJsonObject().get("state").getAsInt())){
                                                Intent intent = new Intent(context,VideoLivePlayActivity.class);
                                                intent.putExtra(BaseConstants.PARAM_ENTITY,entity);
                                                intent.putExtra(BaseConstants.PARAM_URL,jsonObject.get("object").getAsJsonObject().get("flvUrl").getAsString());
                                                intent.putExtra(BaseConstants.PARAM_GROUP,jsonObject.get("object").getAsJsonObject().get("groupId").getAsString());
                                                intent.putExtra(BaseConstants.PARAM_NUMBER,jsonObject.get("object").getAsJsonObject().get("lookNum").getAsLong()+"");
                                                intent.putExtra(BaseConstants.PARAM_NAME,jsonObject.get("object").getAsJsonObject().get("name").getAsString());
                                                intent.putExtra(BaseConstants.PARAM_HEADIMG,jsonObject.get("object").getAsJsonObject().get("icon").getAsString());
                                                intent.putExtra(BaseConstants.PARAM_LIVERID,jsonObject.get("object").getAsJsonObject().get("userId").getAsString());
                                                ((ConcernListActivity)context).startActivityForResult(intent, VideoEntryActivity.REQUEST_LIVE);
                                            }else{
                                                BaseApplication.getInstances().toast(context,"来晚了，直播已经结束!");
                                            }

                                        }else{
                                            if(jsonObject.has("message") && !(jsonObject.get("message") instanceof JsonNull)){
                                                BaseApplication.getInstances().toast(context,jsonObject.get("message").getAsString());
                                            }else{
                                                BaseApplication.getInstances().toast(context,"获取直播地址失败!");
                                            }
                                        }
                                    }else{
                                        BaseApplication.getInstances().toast(context,"获取直播地址失败!");
                                    }
                                }catch (Exception e){
                                    LogUtil.e(e.getMessage());
                                }
                            }
                        }, HttpUrls.getIntoLiveRoom()+"&userId="+docID+"&lookId="+BaseApplication.getInstances().getUser_id()+"&userType=0",
                                null,Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(context)));
                    }
                });
            }else{
                liveStatus(holder, false);
                convertView.setOnClickListener(null);
            }
        }else{
            liveStatus(holder, false);
            convertView.setOnClickListener(null);
        }

        //TODO 需要通过实体数据判断
        holder.concern_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.concern_status.getTag() != null && (Boolean) holder.concern_status.getTag()) {
                    setStatus(position, holder, false);
                } else {
                    setStatus(position, holder, true);
                }
                holder.concern_status.setEnabled(false);
                MyHandler.putTask((ConcernListActivity)context, new Task(new HttpResponseInterface() {
                    @Override
                    public void handMsg(String val) {
                        holder.concern_status.setEnabled(true);
                        if (val != null && val.toLowerCase().startsWith("{") && val.toLowerCase().endsWith("}")) {
                            JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                            if (jsonObject.has("code") && "1".equals(jsonObject.get("code").getAsString())) {
                                ((ConcernListActivity)context).initLoadInfo();
                            }
                        } else {

                        }
                    }
                }, HttpUrls.postConcern(entity.getAnchorId() + "", "0"), null, Task.TYPE_GET_STRING_NOCACHE, null));
            }
        });

        return convertView;
    }

    public void setStatus(final int position, final ConcernListHolder holder, final boolean concerned) {
        //TODO 网络访问变更数据的关注状态 将未关注的也显示出来
        if (!concerned) {
            data.remove(position);
            notifyDataSetChanged();
        } else {
            holder.concern_status.setImageResource(R.drawable.guanzhu_nor);
            holder.concern_status.setTag(true);
        }
    }

    public void liveStatus(ConcernListHolder holder, boolean isLive) {
        if (isLive) {//正在直播
            holder.status.setVisibility(View.VISIBLE);
        } else {//当期未直播
            holder.status.setVisibility(View.GONE);
        }
    }

    public void initHolder(ConcernListHolder holder, View parent) {
        holder.divider = parent.findViewById(R.id.divider);

        holder.header = (ImageView) parent.findViewById(R.id.header);
        holder.name = (TextView) parent.findViewById(R.id.name);
        holder.status = (TextView) parent.findViewById(R.id.status);
        holder.fans = (TextView) parent.findViewById(R.id.fans);

        holder.concern_status = (ImageView) parent.findViewById(R.id.concern_status);
    }
}
