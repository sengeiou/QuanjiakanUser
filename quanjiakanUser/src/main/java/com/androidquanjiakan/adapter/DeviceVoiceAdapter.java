package com.androidquanjiakan.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.DeviceVoiceHolder;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.datahandler.BindDeviceHandler;
import com.androidquanjiakan.datahandler.DeviceVoiceHandler;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.androidquanjiakan.entity.DeviceVoiceEntity;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.view.MaterialBadgeTextView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/11/10 0010.
 */

public class DeviceVoiceAdapter extends BaseAdapter {
    private boolean vIsActive;
    private AudioManager mAm;
    private List<DeviceVoiceEntity> dataList;
    private Context context;
    private PullToRefreshListView listView;

    private HashMap<String, String> nameMap = new HashMap<>();

    private int[] arr_image = {R.drawable.contact_pic_portrait, R.drawable.contacts_pic_mom, R.drawable.contacts_pic_grandpa,
            R.drawable.contacts_pic_grandma, R.drawable.contacts_pic_grandpa2, R.drawable.contacts_pic_grandma2
            , R.drawable.contacts_pic_sister, R.drawable.contacts_pic_borther, R.drawable.contacts_pic_custom};

    private BindDeviceEntity deviceEntity;
    private MyOnAudioFocusChangeListener mListener;

    private String wathcName;

    private HashMap<Integer,View> viewListMap = new HashMap<>();

    public DeviceVoiceAdapter(Context ctx, List<DeviceVoiceEntity> data, String device_id) {
        context = ctx;
        /*mAm = (AudioManager) context.getSystemService(
                Context.AUDIO_SERVICE);

        vIsActive=mAm.isMusicActive();*/
        if (data == null) {
            dataList = new ArrayList<>();
        } else {
            deviceEntity = BindDeviceHandler.getValue(device_id);
        }
        isPlaying = false;

    }

    public PullToRefreshListView getListView() {
        return listView;
    }

    public void setListView(PullToRefreshListView listView) {
        this.listView = listView;
    }

    public String getWathcName() {
        return wathcName;
    }

    public void setWathcName(String wathcName) {
        this.wathcName = wathcName;
    }

    public void setDataList(List<DeviceVoiceEntity> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        if (dataList != null) {
            return dataList.size();
        }
        return 0;
    }

    public HashMap<String, String> getNameMap() {
        return nameMap;
    }

    public void setNameMap(HashMap<String, String> nameMap) {
        this.nameMap = nameMap;
    }

    public List<DeviceVoiceEntity> getDataList() {
        return dataList;
    }

    @Override
    public Object getItem(int position) {
        return this.getView(position,viewListMap.get(position),listView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DeviceVoiceHolder holder;
        //TODO 给Entity增加了readFlag字段用于标识是否是已读
        if (convertView == null) {
            holder = new DeviceVoiceHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_watch_voice, null);

            holder.baby_line = (RelativeLayout) convertView.findViewById(R.id.baby_line);
            holder.baby_head = (ImageView) convertView.findViewById(R.id.baby_head);
            holder.baby_name = (TextView) convertView.findViewById(R.id.baby_name);
            holder.baby_voice_bg = (ImageView) convertView.findViewById(R.id.baby_voice_bg);
            holder.baby_voice_volumn = (ImageView) convertView.findViewById(R.id.baby_voice_volumn);
            holder.baby_voice_length = (TextView) convertView.findViewById(R.id.baby_voice_length);
            holder.baby_voice_time = (TextView) convertView.findViewById(R.id.baby_voice_time);
            holder.baby_unread_flag = convertView.findViewById(R.id.baby_unread_flag);

            holder.dad_line = (RelativeLayout) convertView.findViewById(R.id.dad_line);
            holder.dad_head = (ImageView) convertView.findViewById(R.id.dad_head);
            holder.dad_name = (TextView) convertView.findViewById(R.id.dad_name);
            holder.dad_voice_bg = (ImageView) convertView.findViewById(R.id.dad_voice_bg);
            holder.dad_voice_volumn = (ImageView) convertView.findViewById(R.id.dad_voice_volumn);
            holder.dad_voice_length = (TextView) convertView.findViewById(R.id.dad_voice_length);
            holder.dad_voice_time = (TextView) convertView.findViewById(R.id.dad_voice_time);

            convertView.setTag(holder);
        } else {
            holder = (DeviceVoiceHolder) convertView.getTag();
        }
        viewListMap.put(position,convertView);
        final DeviceVoiceEntity entity = dataList.get(position);
        if (BaseApplication.getInstances().getUser_id().equals(entity.getUserid())) {// daddy
            holder.baby_line.setVisibility(View.GONE);
            holder.dad_line.setVisibility(View.VISIBLE);
            //******************************************************************
            //TODO OLD 显示自己的名称
//            if(nameMap.containsKey(entity.getUserid()+"Name")) {
//                if(nameMap.get(entity.getUserid()+"Name")!=null && nameMap.get(entity.getUserid()+"Name").contains("%")){
//                    try {
//                        holder.dad_name.setText(URLDecoder.decode(nameMap.get(entity.getUserid()+"Name"),"utf-8"));
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }else{
//                    holder.dad_name.setText("自己");
//                }
//            }else{
//                holder.dad_name.setText("自己");
//            }
            //TODO NEW 隐藏自己的名字
            holder.dad_name.setText("");
            //******************************************************************
            if (nameMap.containsKey(entity.getUserid() + "Image")) {
                if (nameMap.get(entity.getUserid() + "Image") != null) {
                    if (nameMap.get(entity.getUserid() + "Image").contains("http")) {
                        ImageLoadUtil.picassoLoad(holder.dad_head, nameMap.get(entity.getUserid() + "Image"), ImageLoadUtil.TYPE_CYCLE);
                    } else {
                        String index = nameMap.get(entity.getUserid() + "Image");
//                        Picasso.with(context).load(arr_image[Integer.parseInt(index)]).into(holder.dad_head);
                        holder.dad_head.setImageResource(arr_image[Integer.parseInt(index)]);

//                        holder.dad_head.setImageResource(R.drawable.watch_voice_dad_img);
                    }
                }

            } else {
                holder.dad_head.setImageResource(R.drawable.ic_patient);
            }

            //******************************************************************
            //TODO OLD 一直显示消息的时间
//            holder.dad_voice_time.setText(sdf.format(new Date(Long.parseLong(entity.getTime()))));
            //TODO NEW 一直显示消息的时间
            if (position == 0) {
                holder.dad_voice_time.setText(sdf.format(new Date(Long.parseLong(entity.getTime()))));
                holder.dad_voice_time.setVisibility(View.VISIBLE);
            } else {
                DeviceVoiceEntity lastEntity = dataList.get(position - 1);
                if (180000 < (Long.parseLong(entity.getTime()) - Long.parseLong(lastEntity.getTime()))) {
                    holder.dad_voice_time.setText(sdf.format(new Date(Long.parseLong(entity.getTime()))));
                    holder.dad_voice_time.setVisibility(View.VISIBLE);
                } else {
//                    holder.dad_voice_time.setText("");
                    holder.dad_voice_time.setVisibility(View.GONE);
                }
            }
            //******************************************************************
            final int positions = position;
            //TODO 删除不会再用到的引用
            holder.dad_voice_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playVoice(DADDY, holder.dad_voice_volumn, entity.getVoice_path(),positions,dataList.get(positions).isReadMessage());
                }
            });
        } else {// baby
            holder.baby_line.setVisibility(View.VISIBLE);
            holder.dad_line.setVisibility(View.GONE);
            if (nameMap.containsKey(entity.getUserid() + "Name")) {
                if (nameMap.get(entity.getUserid() + "Name") != null && nameMap.get(entity.getUserid() + "Name").contains("%")) {
                    try {
                        holder.baby_name.setText(URLDecoder.decode(nameMap.get(entity.getUserid() + "Name"), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    holder.baby_name.setText(nameMap.get(entity.getUserid() + "Name"));
                }
            } else {
                if (entity.getUserid() != null && entity.getUserid().length() == 15) {
//                    holder.baby_name.setText("手表");
                    //TODO 如果需要显示昵称，则使用下面注释的部分
                    if (deviceEntity != null && deviceEntity.getName() != null) {
                        try {
                            if (deviceEntity.getName().contains("%")) {
                                holder.baby_name.setText(URLDecoder.decode(deviceEntity.getName(), "utf-8"));
                            } else {
                                holder.baby_name.setText(deviceEntity.getName());
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        holder.baby_name.setText("手表");
                    }
                } else {
                    if (entity.getUser_name() != null && entity.getUser_name().length() > 0) {
                        holder.baby_name.setText(entity.getUser_name());
                    } else {
                        holder.baby_name.setText(entity.getUserid());
                    }
                }
            }

            if (entity.isReadMessage()) {
                holder.baby_unread_flag.setVisibility(View.GONE);
            } else {
                holder.baby_unread_flag.setVisibility(View.VISIBLE);
            }

            if (nameMap.containsKey(entity.getUserid() + "Image")) {
                if (nameMap.get(entity.getUserid() + "Image") != null) {

                    if (nameMap.get(entity.getUserid() + "Image").contains("http")) {
                        ImageLoadUtil.picassoLoad(holder.baby_head, nameMap.get(entity.getUserid() + "Image"), ImageLoadUtil.TYPE_CYCLE);
                    } else {
//                        holder.baby_head.setImageResource(R.drawable.watch_voice_dad_baby);
                        String index = nameMap.get(entity.getUserid() + "Image");
//                        Picasso.with(context).load(arr_image[Integer.parseInt(index)]).into(holder.baby_head);
                        holder.baby_head.setImageResource(arr_image[Integer.parseInt(index)]);
                    }
                }
            } else {
                if (entity.getUserid() != null && entity.getUserid().length() == 15) {
                    if (deviceEntity != null && deviceEntity.getIcon() != null &&
                            deviceEntity.getIcon().toLowerCase().startsWith("http")) {
                        ImageLoadUtil.picassoLoad(holder.baby_head, deviceEntity.getIcon(), ImageLoadUtil.TYPE_CYCLE);
                    } else {
                        holder.baby_head.setImageResource(R.drawable.ic_patient);
                    }
                } else {
                    holder.baby_head.setImageResource(R.drawable.ic_patient);
                }
            }
            //******************************************************************
            //TODO OLD 一直显示消息的时间
//            holder.baby_voice_time.setText(sdf.format(new Date(Long.parseLong(entity.getTime()))));
            //TODO NEW 一直显示消息的时间
            if (position == 0) {
                holder.baby_voice_time.setText(sdf.format(new Date(Long.parseLong(entity.getTime()))));
                holder.baby_voice_time.setVisibility(View.VISIBLE);
            } else {
                DeviceVoiceEntity lastEntity = dataList.get(position - 1);
                if (180000 < (Long.parseLong(entity.getTime()) - Long.parseLong(lastEntity.getTime()))) {
                    holder.baby_voice_time.setText(sdf.format(new Date(Long.parseLong(entity.getTime()))));
                    holder.baby_voice_time.setVisibility(View.VISIBLE);
                } else {
//                    holder.baby_voice_time.setText("");
                    holder.baby_voice_time.setVisibility(View.GONE);
                }
            }
            //******************************************************************
            final int positions = position;
            //TODO 删除不会再用到的引用
            holder.baby_voice_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playVoice(BABY, holder.baby_voice_volumn, dataList.get(positions).getVoice_path(),positions,dataList.get(positions).isReadMessage());
                    //TODO 重置未读的标记，变更数据中数据，改变未读标记View的显示状态
                    dataList.get(positions).setReadFlag();
                    DeviceVoiceHandler.updataValue(dataList.get(positions));
                    holder.baby_unread_flag.setVisibility(View.GONE);
//                    notifyDataSetChanged();
//                    LogUtil.e("Position:" + positions);
//                    LogUtil.e("User:" + dataList.get(positions).getUserid());
//                    LogUtil.e("Path:" + dataList.get(positions).getVoice_path());
//                    LogUtil.e("Direction:" + dataList.get(positions).getDirection());
//                    LogUtil.e("ReadFlag:" + dataList.get(positions).getReadFlag());
//                    LogUtil.e("Resave:" + DeviceVoiceHandler.getValue(dataList.get(positions).getUserid(), dataList.get(positions).getDevice_id(), dataList.get(positions).getTime()).toString());
                }
            });
        }
        setTime(entity, holder);

        return convertView;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
    private String temp;
    private int minute;
    private int seconds;
    private MediaPlayer player;

    public synchronized void setTime(final DeviceVoiceEntity entity, final DeviceVoiceHolder holder) {
        final MediaPlayer player = new MediaPlayer();
        if (TableInfo_ValueStub.DEVICE_VOICE_INFO_DIRECTION_RECEIVE.equals(entity.getDirection())) {//baby
            if (entity.getVoiceTime() != null) {
                holder.baby_voice_length.setText(entity.getVoiceTime());
                seconds = Integer.parseInt(entity.getVoiceTime().split("'")[1].replace("\"", ""));
                bgWidth(holder.baby_voice_bg, seconds);
            } else {
                try {
                    File temp = new File(entity.getVoice_path());
                    if (temp.exists()) {
                        FileInputStream fileInputStream = new FileInputStream(temp);
                        player.setDataSource(fileInputStream.getFD());
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player.prepareAsync();
                        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer player) {
                                minute = player.getDuration() / 60000;
                                seconds = (player.getDuration() / 1000) % 60;
                                holder.baby_voice_length.setText(minute + "'" + seconds + "\"");
                                entity.setVoiceTime(minute + "'" + seconds + "\"");
                                bgWidth(holder.baby_voice_bg, seconds);
                                player.reset();
                                player.release();
                                player = null;
                            }
                        });
                    } else {
                        holder.baby_voice_length.setText("");
                        bgWidth(holder.baby_voice_bg, 1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {//daddy
            if (entity.getVoiceTime() != null) {
                holder.dad_voice_length.setText(entity.getVoiceTime());
                seconds = Integer.parseInt(entity.getVoiceTime().split("'")[1].replace("\"", ""));
                bgWidth(holder.dad_voice_bg, seconds);
            } else {
                try {
                    File temp = new File(entity.getVoice_path());
                    if (temp.exists()) {
                        FileInputStream fileInputStream = new FileInputStream(temp);
                        player.setDataSource(fileInputStream.getFD());
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player.prepareAsync();
                        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer player) {
                                minute = player.getDuration() / 60000;
                                seconds = (player.getDuration() / 1000) % 60;
                                holder.dad_voice_length.setText(minute + "'" + seconds + "\"");
                                entity.setVoiceTime(minute + "'" + seconds + "\"");
                                bgWidth(holder.dad_voice_bg, seconds);
                                player.reset();
                                player.release();
                                player = null;
                            }
                        });
                    } else {
                        holder.dad_voice_length.setText("");
                        bgWidth(holder.dad_voice_bg, 1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void bgWidth(ImageView imageView, int length) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.width = QuanjiakanUtil.dip2px(context, (length < 20 ? (95 + length * 5) : 195));
        layoutParams.height = QuanjiakanUtil.dip2px(context, 40);
        imageView.setLayoutParams(layoutParams);
    }

    //*****播放音频时的动画效果
    private FileInputStream mFIS;
    private int count;

    public void playVoice(final int direction, final ImageView imageView, final String path,final int position,final boolean isUnread) {
        mAm = (AudioManager) context.getSystemService(
                Context.AUDIO_SERVICE);

        vIsActive = mAm.isMusicActive();
        if (!isPlaying && !vIsActive) {
            synchronized (this) {
                try {
//                    player = new MediaPlayer();
                    final File temp = new File(path);
                    if (temp.exists()) {
                        if (player != null) {
                            player.reset();
                        } else {
                            player = new MediaPlayer();
                        }
                        mFIS = new FileInputStream(new File(path));
                        player.setDataSource(mFIS.getFD());
                        player.prepare();
                        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer players) {
                                isPlaying = true;
                                player.start();
                                animationView = imageView;
                                timer = new Timer();
                                count = 0;
                                timerTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (count >= 3) {
                                            count = 0;
                                        }
                                        Message msg = mHandler.obtainMessage();
                                        msg.what = MSG_SHOW;
                                        msg.obj = count;
                                        msg.arg1 = direction;
                                        mHandler.sendMessage(msg);
                                        count++;
                                    }
                                };
                                timer.schedule(timerTask, 0, 1000);
                            }
                        });
                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer players) {
                                isPlaying = false;
                                player.stop();
                                player.reset();
                                player.release();
                                player = null;
                                timer.cancel();
                                if (direction == DADDY) {
                                    animationView.setImageResource(R.drawable.data_ico_left_voice_three1);
                                } else {
                                    animationView.setImageResource(R.drawable.data_ico_right_voice_three1);
                                }

                                LogUtil.e("连播--isUnread:"+isUnread+"******************************************");
                                if(isUnread){//TODO 已读语音不进行连播
                                    return;
                                }
                                //TODO 连续的没有问题
                                for(int i = position+1;;i++){
                                    //TODO 从当前播放处向后遍历非自己发出的下一个未读语音
                                    if(i<dataList.size() &&
                                            !dataList.get(i).getUserid().
                                                    equals(BaseApplication.getInstances().getUser_id()) &&
                                            !dataList.get(i).isReadMessage()){
                                        playVoice(BABY, (ImageView)(((View)getItem(i)).findViewById(R.id.baby_voice_volumn)), dataList.get(i).getVoice_path(),i,dataList.get(i).isReadMessage());
                                        //TODO 重置未读的标记，变更数据中数据，改变未读标记View的显示状态
                                        dataList.get(i).setReadFlag();
                                        DeviceVoiceHandler.updataValue(dataList.get(i));
                                        (((View)getItem(i)).findViewById(R.id.baby_unread_flag)).setVisibility(View.GONE);
                                        break;
                                    }else{
                                        if(i>=dataList.size()){
                                            break;
                                        }
                                        continue;
                                    }
//                                        notifyDataSetChanged();//TODO 刷新界面
                                }
                            }
                        });
                        if (direction == BABY) {//收到的

                        } else {//发送的

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (mFIS != null) {
                            mFIS.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            mListener = new MyOnAudioFocusChangeListener();
            int result = mAm.requestAudioFocus(mListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                synchronized (this) {
                    try {
//                    player = new MediaPlayer();
                        final File temp = new File(path);
                        if (temp.exists()) {
                            if (player != null) {
                                player.reset();
                            } else {
                                player = new MediaPlayer();
                            }
                            mFIS = new FileInputStream(new File(path));
                            player.setDataSource(mFIS.getFD());
                            player.prepare();
                            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer players) {
                                    isPlaying = true;
                                    player.start();
                                    animationView = imageView;
                                    timer = new Timer();
                                    count = 0;
                                    timerTask = new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (count >= 3) {
                                                count = 0;
                                            }
                                            Message msg = mHandler.obtainMessage();
                                            msg.what = MSG_SHOW;
                                            msg.obj = count;
                                            msg.arg1 = direction;
                                            mHandler.sendMessage(msg);
                                            count++;
                                        }
                                    };
                                    timer.schedule(timerTask, 0, 1000);
                                }
                            });
                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer players) {
                                    isPlaying = false;
                                    player.stop();
                                    player.reset();
                                    player.release();
                                    player = null;
                                    timer.cancel();
                                    if (direction == DADDY) {
                                        animationView.setImageResource(R.drawable.data_ico_left_voice_three1);
                                    } else {
                                        animationView.setImageResource(R.drawable.data_ico_right_voice_three1);
                                    }

                                    if (vIsActive) {
                                        mAm.abandonAudioFocus(mListener);
                                    }

                                    LogUtil.e("连播--isUnread:"+isUnread+"-----------------------------------------------");
                                    if(isUnread){//TODO 已读语音不进行连播
                                        return;
                                    }
                                    //TODO 向后查找满足条件的语音，并播放
                                    for(int i = position+1;;i++){
//                                        notifyDataSetChanged();//TODO 刷新界面
                                        //TODO 从当前播放处向后遍历非自己发出的下一个未读语音
                                        if(i<dataList.size() &&
                                                !dataList.get(i).getUserid().
                                                        equals(BaseApplication.getInstances().getUser_id()) &&
                                                !dataList.get(i).isReadMessage()){
                                            playVoice(BABY, (ImageView)(((View)getItem(i)).findViewById(R.id.baby_voice_volumn)), dataList.get(i).getVoice_path(),i,dataList.get(i).isReadMessage());
                                            //TODO 重置未读的标记，变更数据中数据，改变未读标记View的显示状态
                                            dataList.get(i).setReadFlag();
                                            DeviceVoiceHandler.updataValue(dataList.get(i));
                                            (((View)getItem(i)).findViewById(R.id.baby_unread_flag)).setVisibility(View.GONE);
                                            break;
                                        }else{
                                            if(i>=dataList.size()){
                                                break;
                                            }
                                            continue;
                                        }
                                    }
                                }
                            });
                            if (direction == BABY) {//收到的

                            } else {//发送的

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (mFIS != null) {
                                mFIS.close();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else {

            }

        }
    }

    private final int MSG_SHOW = 1000;
    private final int DADDY = 1;
    private final int BABY = 2;
    private ImageView animationView;
    private boolean isPlaying = false;
    private Timer timer;
    private TimerTask timerTask;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW:
                    if (animationView != null) {
                        int which = Integer.parseInt(msg.obj.toString());
                        int direction = msg.arg1;
                        if (direction == DADDY) {
                            if (which == 0) {
                                animationView.setImageResource(R.drawable.data_ico_left_voice_one1);
                            } else if (which == 1) {
                                animationView.setImageResource(R.drawable.data_ico_left_voice_two1);
                            } else if (which == 2) {
                                animationView.setImageResource(R.drawable.data_ico_left_voice_three1);
                            }
                        } else {
                            if (which == 0) {
                                animationView.setImageResource(R.drawable.data_ico_right_voice_one1);
                            } else if (which == 1) {
                                animationView.setImageResource(R.drawable.data_ico_right_voice_two1);
                            } else if (which == 2) {
                                animationView.setImageResource(R.drawable.data_ico_right_voice_three1);
                            }
                        }
                    }
                    break;
            }
        }
    };


    public class MyOnAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            // TODO Auto-generated method stub
        }
    }

}
