package com.androidquanjiakan.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.activity.common.ImageViewerActivity;
import com.androidquanjiakan.adapterholder.FreeInquiryAppend;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.FreeInquiryAnswerEntity;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.util.GsonParseUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/11 0011.
 */

public class FreeInquiryAppendAdapter extends BaseAdapter {

    private Context context;
    private List<FreeInquiryAnswerEntity> freeInquiryAnswerEntityList;

    private SimpleDateFormat sdf_today = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm:ss");
    private String today;

    public FreeInquiryAppendAdapter(Context context,List<FreeInquiryAnswerEntity> freeInquiryAnswerEntityList){
        this.context = context;
        this.freeInquiryAnswerEntityList = freeInquiryAnswerEntityList;
        today = sdf_today.format(new Date());
        if(freeInquiryAnswerEntityList==null){
            this.freeInquiryAnswerEntityList = new ArrayList<>();
        }
    }

    public void setData(List<FreeInquiryAnswerEntity> freeInquiryAnswerEntityList){
        if(freeInquiryAnswerEntityList==null){
            this.freeInquiryAnswerEntityList = new ArrayList<>();
        }else{
            this.freeInquiryAnswerEntityList = freeInquiryAnswerEntityList;
        }
        for(int i = 0;i<this.freeInquiryAnswerEntityList.size();i++){
            LogUtil.e("****"+this.freeInquiryAnswerEntityList.get(i).toString());
        }
    }

    public List<FreeInquiryAnswerEntity> getData(){
        return this.freeInquiryAnswerEntityList;
    }

    @Override
    public int getCount() {
        return freeInquiryAnswerEntityList.size();
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
        FreeInquiryAppend holder;
        if(convertView == null){
            holder = new FreeInquiryAppend();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_free_inquiry_append,null);

            holder.time_line = (LinearLayout) convertView.findViewById(R.id.time_line);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            holder.text_line = (LinearLayout) convertView.findViewById(R.id.text_line);
            holder.text_type = (TextView) convertView.findViewById(R.id.text_type);
            holder.content = (TextView) convertView.findViewById(R.id.content);

            holder.voice_line = (RelativeLayout) convertView.findViewById(R.id.voice_line);
            holder.voice_type = (TextView) convertView.findViewById(R.id.voice_type);
            holder.voice_bg = (ImageView) convertView.findViewById(R.id.voice_bg);
            holder.voice_icon = (ImageView) convertView.findViewById(R.id.voice_icon);
            holder.voice_time = (TextView) convertView.findViewById(R.id.voice_time);

            holder.img_line = (LinearLayout) convertView.findViewById(R.id.img_line);
            holder.img_type = (TextView) convertView.findViewById(R.id.img_type);
            holder.img_bg = (ImageView) convertView.findViewById(R.id.img_bg);

            convertView.setTag(holder);
        }else{
            holder = (FreeInquiryAppend) convertView.getTag();
        }
        holder.content.setText("");
        holder.voice_time.setText("");
        final FreeInquiryAnswerEntity value = freeInquiryAnswerEntityList.get(position);
        setTimeLine(position,holder);
        setContent(position,holder,value);
        return convertView;
    }

    public void type(TextView type,String typeValue){
        if("0".equals(typeValue)){
            type.setText("答");
            type.setTextColor(context.getResources().getColor(R.color.color_title_green));
            type.setBackgroundResource(R.drawable.bg_issue_answer);
        }else{
            type.setText("问");
            type.setTextColor(context.getResources().getColor(R.color.white));
            type.setBackgroundResource(R.drawable.bg_issue_ask);
        }
    }


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String temp;
    public void setTimeLine(int position,FreeInquiryAppend holder){
        if(position==0){
            holder.time_line.setVisibility(View.VISIBLE);
            holder.time.setText(sdf.format(new Date(Long.parseLong(freeInquiryAnswerEntityList.get(position).getCreated_time_ms()))));
        }else{
            long last = Long.parseLong(freeInquiryAnswerEntityList.get(position -1).getCreated_time_ms());
            long now = Long.parseLong(freeInquiryAnswerEntityList.get(position).getCreated_time_ms());
            if(now-last>300000){
                temp = sdf.format(new Date(now));
                if(temp.contains(today)){
                    holder.time.setText(sdf_time.format(new Date(now)));
                    holder.time_line.setVisibility(View.VISIBLE);
                }else{
                    holder.time.setText(temp);
                    holder.time_line.setVisibility(View.VISIBLE);
                }
            }else{
                holder.time_line.setVisibility(View.GONE);
            }
        }
    }

    public void setContent(int position,FreeInquiryAppend holder,FreeInquiryAnswerEntity value){
        String content = value.getContent();
        if(content==null || content.length()<1){
            typeHide(position,holder,value,TEXT);
            holder.content.setText(value.getTitle());
        }else{
            final JsonArray jsonArray = new GsonParseUtil(content.replace("\\","")).getJsonArray();
            final JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            if(position==0){
                typeHide(position,holder,value,TEXT);
                holder.content.setText(value.getTitle());
            }else{
                if(jsonObject.has("type") && "text".endsWith(jsonObject.get("type").getAsString().toLowerCase())){
                    typeHide(position,holder,value,TEXT);
                    holder.content.setText(jsonObject.get("text").getAsString());
                }else if(jsonObject.has("type") && "audio".endsWith(jsonObject.get("type").getAsString().toLowerCase())){
                    typeHide(position,holder,value,AUDIO);
                    if(jsonObject.has("file")){
                        setVoiceTime(holder.voice_time,jsonObject.get("file").getAsString().trim(),value);
                    }else if(jsonObject.has("text")){
                        setVoiceTime(holder.voice_time,jsonObject.get("text").getAsString().trim(),value);
                    }

                    holder.voice_bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(jsonObject.has("file")){
                                playVoice(jsonObject.get("file").getAsString().trim());
                            }else if(jsonObject.has("text")){
                                playVoice(jsonObject.get("text").getAsString().trim());
                            }
                        }
                    });

                }else if(jsonObject.has("type") && "image".endsWith(jsonObject.get("type").getAsString().toLowerCase())){
                    typeHide(position,holder,value,IMAGE);
                    if(jsonObject.has("file")) {
                        ImageLoadUtil.loadImage(holder.img_bg, jsonObject.get("file").getAsString().trim(), ImageLoadUtil.optionsNormal);
                    }else if(jsonObject.has("text")){
                        ImageLoadUtil.loadImage(holder.img_bg, jsonObject.get("text").getAsString().trim(), ImageLoadUtil.optionsNormal);
                    }

                    holder.img_bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //-----跳转到统一的图片浏览页
                            Intent intent = new Intent(context, ImageViewerActivity.class);
                            if(jsonObject.has("file")) {
                                intent.putExtra(BaseConstants.PARAMS_URL,jsonObject.get("file").getAsString().trim());
                            }else if(jsonObject.has("text")){
                                intent.putExtra(BaseConstants.PARAMS_URL,jsonObject.get("text").getAsString().trim());
                            }
                            context.startActivity(intent);
                        }
                    });
                }else{
                    typeHide(position,holder,value,TEXT);
                }
            }
        }
    }
    private final int TEXT = 1;
    private final int AUDIO = 2;
    private final int IMAGE = 3;

    public void typeHide(int position,FreeInquiryAppend holder,FreeInquiryAnswerEntity sponsor,int type){
        if(type==TEXT){
            holder.text_line.setVisibility(View.VISIBLE);
            holder.voice_line.setVisibility(View.GONE);
            holder.img_line.setVisibility(View.GONE);
            type(holder.text_type,sponsor.getSponsor());
        }else if(type==AUDIO){
            holder.text_line.setVisibility(View.GONE);
            holder.voice_line.setVisibility(View.VISIBLE);
            holder.img_line.setVisibility(View.GONE);
            type(holder.voice_type,sponsor.getSponsor());
        }else if(type==IMAGE){
            holder.text_line.setVisibility(View.GONE);
            holder.voice_line.setVisibility(View.GONE);
            holder.img_line.setVisibility(View.VISIBLE);
            type(holder.img_type,sponsor.getSponsor());
        }
    }

    private int minute;
    private int seconds;
    public synchronized void setVoiceTime(final TextView voiceTime,String netPath,final FreeInquiryAnswerEntity value){
//        if(player==null){
            if(value.getVoiceTime()!=null){
                voiceTime.setText(value.getVoiceTime());
            }else{
                final MediaPlayer player  =   new MediaPlayer();
                try {
                    player.setDataSource(netPath);
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.prepareAsync();
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            minute = player.getDuration()/60000;
                            seconds = (player.getDuration()/1000)%60;
                            voiceTime.setText(minute+"'"+seconds+"\"");
                            value.setVoiceTime(minute+"'"+seconds+"\"");
                            player.reset();
                            player.release();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//        }else{
//
//            try {
//                player.reset();
//
//                player.setDataSource(netPath);
//                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                player.prepareAsync();
//                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        minute = player.getDuration()/60000;
//                        seconds = (player.getDuration()/1000)%60;
//                        voiceTime.setText(minute+"'"+seconds+"\"");
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
    }

    private MediaPlayer player;
    private Dialog dialog;
    public synchronized void playVoice(String voiceUri){
        if(player==null){

            player  =   new MediaPlayer();
            try {
                dialog = QuanjiakanDialog.getInstance().getDialog(context);
                player.setDataSource(voiceUri);
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        dialog.dismiss();
                        player.start();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            if(player.isPlaying()){
                /**
                 *
                 */
                player.reset();
                /**
                 *
                 */
//                player.stop();
//                player.release();
            }else{
                try {
                    player.reset();

                    dialog = QuanjiakanDialog.getInstance().getDialog(context);
                    player.setDataSource(voiceUri);
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.prepareAsync();
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            dialog.dismiss();
                            player.start();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void release(){
        if(player!=null){
            player.reset();
            player.stop();
            player.release();
            player = null;
        }
    }
}
