package com.androidquanjiakan.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.util.FileHelper;
import com.quanjiakanuser.util.HandleResponseCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.quanjiakanuser.util.TimeFormat;
import com.quanjiakanuser.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

@SuppressLint("NewApi")
public class GroupChatMsgListAdapter extends BaseAdapter {

    private static final String TAG = "MsgListAdapter";
    private Context mContext;
    private String mTargetId;
    private Conversation mConv;
    private List<Message> mMsgList = new ArrayList<Message>();//所有消息列表
    private List<Integer> mIndexList = new ArrayList<Integer>();//语音索引
    private LayoutInflater mInflater;
    private boolean mSetData = false;
    private boolean mIsGroup = false;
    private long mGroupId;
    private int mPosition = -1;// 和mSetData一起组成判断播放哪条录音的依据
    // 9种Item的类型
    // 文本
    private final int TYPE_RECEIVE_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    // 图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;

    // 语音
    private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVER_VOICE = 7;

    private final MediaPlayer mp = new MediaPlayer();
    private AnimationDrawable mVoiceAnimation;
    private FileInputStream mFIS;
    private FileDescriptor mFD;
    private Activity mActivity;
    private boolean autoPlay = false;
    private int nextPlayPosition = 0;
    private boolean mIsEarPhoneOn;
    //当前第0项消息的位置
    private int mStart;
    //上一页的消息数
    private int mOffset = 50;
    private boolean mHasLastPage = false;
    private Dialog mDialog;
    //发送图片消息的队列
    private Queue<Message> mMsgQueue = new LinkedList<Message>();
    private int mSendMsgId;
    private float mDensity;
    private int mWidth;
    private Animation mSendingAnim;
    private ContentLongClickListener mLongClickListener;
    private String mTargetAppKey;
    private JsonObject jsonUserInfo = new JsonObject();

    public GroupChatMsgListAdapter(Context context, long targetId, String appKey,
                                   ContentLongClickListener longClickListener) {
        initData(context);
        this.mTargetId = "juguikeji";
        this.mTargetAppKey = appKey;
        this.mConv = JMessageClient.getGroupConversation(targetId);
//        List<Conversation> conversations = JMessageClient.getConversationList();
        this.mLongClickListener = longClickListener;
        this.mMsgList = mConv.getMessagesFromNewest(0, mOffset);
        reverse(mMsgList);
        mStart = mMsgList.size()-1;
        checkSendingImgMsg();
    }

    private void reverse(List<Message> list) {
        if (list.size() > 0) {
            Collections.reverse(list);
        }
    }

    public GroupChatMsgListAdapter(Context context, long groupId, ContentLongClickListener longClickListener) {
        initData(context);
        this.mGroupId = groupId;
        this.mIsGroup = true;
        this.mLongClickListener = longClickListener;
        this.mConv = JMessageClient.getGroupConversation(groupId);
        this.mMsgList = mConv.getMessagesFromNewest(0, mOffset);
        reverse(mMsgList);
        mStart = mOffset;
        checkSendingImgMsg();
    }

    private void initData(Context context) {
        this.mContext = context;
        mActivity = (Activity) context;
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;
        mDensity = dm.density;
        mInflater = LayoutInflater.from(mContext);
        mSendingAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        mSendingAnim.setInterpolator(lin);
        AudioManager audioManager = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        if (audioManager.isSpeakerphoneOn()) {
            audioManager.setSpeakerphoneOn(true);
        } else {
            audioManager.setSpeakerphoneOn(false);
        }
        mp.setAudioStreamType(AudioManager.STREAM_RING);
        mp.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

    public void dropDownToRefresh() {
        if (mConv != null) {
            List<Message> msgList = mConv.getMessagesFromNewest(mStart, mOffset);
            if (msgList != null) {
                for (Message msg : msgList) {
                    mMsgList.add(0, msg);
                }
                if (msgList.size() > 0) {
                    checkSendingImgMsg();
                    mOffset = msgList.size();
                    mHasLastPage = true;
                } else {
                    mOffset = 0;
                    mHasLastPage = false;
                }
                notifyDataSetChanged();
            }
        }
    }

    public int getOffset() {
        return mOffset;
    }

    public boolean isHasLastPage() {
        return mHasLastPage;
    }

    public void refreshStartPosition() {
        mStart += mOffset;
    }

    //当有新消息加到MsgList，自增mStart
    private void incrementStartPosition() {
        ++mStart;
    }

    public void initMediaPlayer() {
        mp.reset();
    }

    /**
     * 检查图片是否处于创建状态，如果是，则加入发送队列
     */
    private void checkSendingImgMsg() {
        for (Message msg : mMsgList) {
            if (msg.getStatus() == MessageStatus.created
                    && msg.getContentType() == ContentType.image) {
                mMsgQueue.offer(msg);
            }
        }
    }

    //发送图片 将图片加入发送队列
    public void setSendImg(int[] msgIds) {
        Message msg;
        if (mIsGroup) {
            mConv = JMessageClient.getGroupConversation(mGroupId);
        } else {
            mConv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
            Log.d(TAG, "mTargetAppKey: " + mTargetAppKey);
        }
        for (int msgId : msgIds) {
            msg = mConv.getMessage(msgId);
            if (msg != null) {
                mMsgList.add(msg);
                incrementStartPosition();
                mMsgQueue.offer(msg);
            }
        }

        if (mMsgQueue.size() > 0) {
            Message message = mMsgQueue.element();
            sendNextImgMsg(message);
            notifyDataSetChanged();
        }
    }

    /**
     * 从发送队列中出列，并发送图片
     *
     * @param msg 图片消息
     */
    private void sendNextImgMsg(Message msg) {
        JMessageClient.sendMessage(msg);
        msg.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                //出列
                mMsgQueue.poll();
                //如果队列不为空，则继续发送下一张
                if (!mMsgQueue.isEmpty()) {
                    sendNextImgMsg(mMsgQueue.element());
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void releaseMediaPlayer() {
        if (mp != null)
            mp.release();
    }

    public void addMsgToList(Message msg) {
        mMsgList.add(msg);
        incrementStartPosition();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public Message getLastMsg() {
        if (mMsgList.size() > 0) {
            return mMsgList.get(mMsgList.size() - 1);
        } else {
            return null;
        }
    }

    public Message getMessage(int position) {
        return mMsgList.get(position);
    }

    public void removeMessage(int position) {
        mMsgList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMsgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = mMsgList.get(position);
        if(msg.getDirect() == MessageDirect.receive){
            //收到的消息
            String type = msg.getContent().getStringExtra("type");
            if("text".equals(type)){
                return TYPE_RECEIVE_TXT;
            }else if ("image".equals(type) ){
                return TYPE_RECEIVER_IMAGE;
            }else if (  "voice".equals(type)){
                return TYPE_RECEIVER_VOICE;
            }
        }else{
            //发送的消息
            if(msg.getContentType().equals("text")){
                return TYPE_SEND_TXT;
            }else if (msg.getContentType().equals("image")){
                return TYPE_SEND_IMAGE;
            }else if (msg.getContentType().equals("voice")){
                return TYPE_SEND_VOICE;
            }
        }
        return TYPE_RECEIVE_TXT;
    }

    public int getViewTypeCount() {
        return 7;
    }

    private View createViewByType(Message msg, int position) {
        // 根据会话类型获取相应的view
        int viewType = getItemViewType(position);//根据代码来判断，需要的都是发送的layout
        if(viewType == TYPE_SEND_IMAGE){
            return mInflater.inflate(R.layout.chat_item_send_image,null);
        }else if (viewType == TYPE_RECEIVER_IMAGE){
            return mInflater.inflate(R.layout.chat_item_receive_image,null);
        }else if (viewType == TYPE_SEND_VOICE){
            return mInflater.inflate(R.layout.chat_item_send_voice,null);
        }else if (viewType == TYPE_RECEIVER_VOICE){
            return mInflater.inflate(R.layout.chat_item_receive_voice,null);
        }else if (viewType == TYPE_SEND_TXT){
            return mInflater.inflate(R.layout.chat_item_send_text,null);
        }else if (viewType == TYPE_RECEIVE_TXT){
            return mInflater.inflate(R.layout.chat_item_receive_text,null);
        }

//        if(viewType == TYPE_SEND_IMAGE){
//            return mInflater.inflate(R.layout.chat_item_send_image,null);
//        }else if (viewType == TYPE_RECEIVER_IMAGE){
//            return mInflater.inflate(R.layout.chat_item_receive_image,null);
//        }else if (viewType == TYPE_SEND_VOICE){
//            return mInflater.inflate(R.layout.chat_item_receive_image,null);
//        }else if (viewType == TYPE_RECEIVER_VOICE){
//            return mInflater.inflate(R.layout.chat_item_receive_image,null);
//        }else if (viewType == TYPE_SEND_TXT){
//            return mInflater.inflate(R.layout.chat_item_receive_image,null);
//        }else if (viewType == TYPE_RECEIVE_TXT){
//            return mInflater.inflate(R.layout.chat_item_receive_image,null);
//        }

        return mInflater.inflate(R.layout.chat_item_send_image,null);
    }

    @Override
    public Message getItem(int position) {
        return mMsgList.get(position);
    }

    public void setAudioPlayByEarPhone(int state) {
        AudioManager audioManager = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        if (state == 0) {
            mIsEarPhoneOn = false;
            audioManager.setSpeakerphoneOn(true);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL);
            Log.i(TAG, "set SpeakerphoneOn true!");
        } else {
            mIsEarPhoneOn = true;
            audioManager.setSpeakerphoneOn(false);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                    AudioManager.STREAM_VOICE_CALL);
            Log.i(TAG, "set SpeakerphoneOn false!");
        }
    }

    public void clearMsgList() {
        mMsgList.clear();
        mStart = 0;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Message msg = mMsgList.get(position);
        final UserInfo userInfo = msg.getFromUser();
        final ViewHolder holder;

        holder = new ViewHolder();
        int viewType = getItemViewType(position);
        convertView = createViewByType(msg, position);
        if(viewType == TYPE_SEND_TXT){
            holder.headIcon = (CircleImageView) convertView
                    .findViewById(R.id.avatar_iv);
            holder.displayName = (TextView) convertView
                    .findViewById(R.id.display_name_tv);
            holder.txtContent = (TextView) convertView
                    .findViewById(R.id.msg_content);
            holder.sendingIv = (ImageView) convertView
                    .findViewById(R.id.sending_iv);
            holder.resend = (ImageButton) convertView
                    .findViewById(R.id.fail_resend_ib);
        }else if (viewType == TYPE_SEND_IMAGE){
            holder.headIcon = (CircleImageView) convertView
                    .findViewById(R.id.avatar_iv);
            holder.displayName = (TextView) convertView
                    .findViewById(R.id.display_name_tv);
            holder.picture = (ImageView) convertView
                    .findViewById(R.id.picture_iv);
            holder.sendingIv = (ImageView) convertView
                    .findViewById(R.id.sending_iv);
            holder.progressTv = (TextView) convertView
                    .findViewById((R.id.progress_tv));
            holder.resend = (ImageButton) convertView
                    .findViewById(R.id.fail_resend_ib);
        }else if (viewType == TYPE_SEND_VOICE){
            holder.headIcon = (CircleImageView) convertView
                    .findViewById(R.id.avatar_iv);
            holder.displayName = (TextView) convertView
                    .findViewById(R.id.display_name_tv);
            holder.txtContent = (TextView) convertView
                    .findViewById(R.id.msg_content);
            holder.voice = ((ImageView) convertView
                    .findViewById(R.id.voice_iv));
            holder.sendingIv = (ImageView) convertView
                    .findViewById(R.id.sending_iv);
            holder.voiceLength = (TextView) convertView
                    .findViewById(R.id.voice_length_tv);
            holder.readStatus = (ImageView) convertView
                    .findViewById(R.id.read_status_iv);
            holder.resend = (ImageButton) convertView
                    .findViewById(R.id.fail_resend_ib);
        } else if(viewType == TYPE_RECEIVE_TXT){
            holder.headIcon = (CircleImageView) convertView
                    .findViewById(R.id.avatar_iv);
            holder.displayName = (TextView) convertView
                    .findViewById(R.id.display_name_tv);
            holder.txtContent = (TextView) convertView
                    .findViewById(R.id.msg_content);
            holder.sendingIv = (ImageView) convertView
                    .findViewById(R.id.sending_iv);
            holder.resend = (ImageButton) convertView
                    .findViewById(R.id.fail_resend_ib);
        }else if (viewType == TYPE_RECEIVER_IMAGE){
            holder.headIcon = (CircleImageView) convertView
                    .findViewById(R.id.avatar_iv);
            holder.displayName = (TextView) convertView
                    .findViewById(R.id.display_name_tv);
            holder.picture = (ImageView) convertView
                    .findViewById(R.id.picture_iv);
            holder.sendingIv = (ImageView) convertView
                    .findViewById(R.id.sending_iv);
            holder.progressTv = (TextView) convertView
                    .findViewById((R.id.progress_tv));
            holder.resend = (ImageButton) convertView
                    .findViewById(R.id.fail_resend_ib);
        }else if (viewType == TYPE_RECEIVER_VOICE){
            holder.headIcon = (CircleImageView) convertView
                    .findViewById(R.id.avatar_iv);
            holder.displayName = (TextView) convertView
                    .findViewById(R.id.display_name_tv);
            holder.txtContent = (TextView) convertView
                    .findViewById(R.id.msg_content);
            holder.voice = ((ImageView) convertView
                    .findViewById(R.id.voice_iv));
            holder.sendingIv = (ImageView) convertView
                    .findViewById(R.id.sending_iv);
            holder.voiceLength = (TextView) convertView
                    .findViewById(R.id.voice_length_tv);
            holder.readStatus = (ImageView) convertView
                    .findViewById(R.id.read_status_iv);
            holder.resend = (ImageButton) convertView
                    .findViewById(R.id.fail_resend_ib);
        }
        //显示时间
        holder.msgTime = (TextView) convertView
                .findViewById(R.id.send_time_txt);
        long nowDate = msg.getCreateTime();
        if (mOffset == 18) {
            if (position == 0 || position % 18 == 0) {
                TimeFormat timeFormat = new TimeFormat(mContext, nowDate);
                holder.msgTime.setText(timeFormat.getDetailTime());
                holder.msgTime.setVisibility(View.VISIBLE);
            } else {
                long lastDate = mMsgList.get(position - 1).getCreateTime();
                // 如果两条消息之间的间隔超过十分钟则显示时间
                if (nowDate - lastDate > 600000) {
                    TimeFormat timeFormat = new TimeFormat(mContext, nowDate);
                    holder.msgTime.setText(timeFormat.getDetailTime());
                    holder.msgTime.setVisibility(View.VISIBLE);
                } else {
                    holder.msgTime.setVisibility(View.GONE);
                }
            }
        } else {
            if (position == 0 || position == mOffset
                    || (position - mOffset) % 18 == 0) {
                TimeFormat timeFormat = new TimeFormat(mContext, nowDate);
                holder.msgTime.setText(timeFormat.getDetailTime());
                holder.msgTime.setVisibility(View.VISIBLE);
            } else {
                long lastDate = mMsgList.get(position - 1).getCreateTime();
                // 如果两条消息之间的间隔超过十分钟则显示时间
                if (nowDate - lastDate > 600000) {
                    TimeFormat timeFormat = new TimeFormat(mContext, nowDate);
                    holder.msgTime.setText(timeFormat.getDetailTime());
                    holder.msgTime.setVisibility(View.VISIBLE);
                } else {
                    holder.msgTime.setVisibility(View.GONE);
                }
            }
        }

        //处理消息
        if(msg.getDirect() == MessageDirect.receive){
            //处理收到的消息
            if(getItemViewType(position) == TYPE_RECEIVE_TXT){
                handleChunyuTextMsg(msg,holder,position);
            }
        }else{
            //处理发送的消息
            switch (msg.getContentType()) {
                case text:
                    handleTextMsg(msg, holder, position);
                    break;
                case image:
                    handleImgMsg(msg, holder, position);
                    break;
                case voice:
                    handleVoiceMsg(msg, holder, position);
                    break;

                default:
                    handleImgMsg(msg, holder,position);
            }
        }
        return convertView;
    }

    private void handleGroupChangeMsg(Message msg, ViewHolder holder, TextView msgTime) {
        UserInfo myInfo = JMessageClient.getMyInfo();
        GroupInfo groupInfo = (GroupInfo) msg.getTargetInfo();
        String content = ((EventNotificationContent) msg.getContent()).getEventText();
        EventNotificationContent.EventNotificationType type = ((EventNotificationContent) msg
                .getContent()).getEventNotificationType();
        switch (type) {
            case group_member_added:
                holder.groupChange.setText(content);
                holder.groupChange.setVisibility(View.VISIBLE);
                break;
            case group_member_exit:
                holder.groupChange.setVisibility(View.GONE);
                msgTime.setVisibility(View.GONE);
                break;
            case group_member_removed:
                List<String> userNames = ((EventNotificationContent) msg.getContent()).getUserNames();
                //被删除的人显示EventNotification
                if (userNames.contains(myInfo.getNickname()) || userNames.contains(myInfo.getUserName())) {
                    holder.groupChange.setText(content);
                    holder.groupChange.setVisibility(View.VISIBLE);
                    //群主亦显示
                } else if (myInfo.getUserName().equals(groupInfo.getGroupOwner())) {
                    holder.groupChange.setText(content);
                    holder.groupChange.setVisibility(View.VISIBLE);
                } else {
                    holder.groupChange.setVisibility(View.GONE);
                    msgTime.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void handleCustomMsg(Message msg, ViewHolder holder) {
        CustomContent content = (CustomContent) msg.getContent();
        Boolean isBlackListHint = content.getBooleanValue("blackList");
        if (isBlackListHint != null && isBlackListHint) {
            holder.groupChange.setText(mContext.getString(R.string.server_803008));
        } else {
            holder.groupChange.setVisibility(View.GONE);
        }
    }

    private void handleTextMsg(final Message msg, final ViewHolder holder, int position) {
        final String content = ((TextContent) msg.getContent()).getText();
        holder.txtContent.setText(content);
        holder.txtContent.setTag(position);
        holder.txtContent.setOnLongClickListener(mLongClickListener);
        // 检查发送状态，发送方有重发机制
        if (msg.getDirect() == MessageDirect.send) {
            switch (msg.getStatus()) {
                case send_success:
                    if(holder.sendingIv!=null){
                        holder.sendingIv.clearAnimation();
                        holder.sendingIv.setVisibility(View.GONE);
                    }
                    if(holder.resend!=null){
                        holder.resend.setVisibility(View.GONE);
                    }
                    break;
                case send_fail:
                    if(holder.sendingIv!=null){
                        holder.sendingIv.clearAnimation();
                        holder.sendingIv.setVisibility(View.GONE);
                    }
                    if(holder.resend!=null){
                        holder.resend.setVisibility(View.GONE);
                    }
                    break;
                case send_going:
                    sendingTextOrVoice(holder, msg);
                    break;
                default:
            }
            // 点击重发按钮，重发消息
            holder.resend.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showResendDialog(holder, msg);
                }
            });

        } else {
            if (mIsGroup) {
                holder.displayName.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                    holder.displayName.setText(msg.getFromUser().getUserName());
                } else {
                    holder.displayName.setText(msg.getFromUser().getNickname());
                }
            }
        }
    }

    private void handleChunyuTextMsg(final Message msg, final ViewHolder holder, int position) {
        final String content = ((TextContent) msg.getContent()).getText();
        holder.txtContent.setText(content);
        holder.txtContent.setTag(position);
        holder.txtContent.setOnLongClickListener(mLongClickListener);
    }

    //正在发送文字或语音
    private void sendingTextOrVoice(final ViewHolder holder, Message msg) {
        holder.sendingIv.setVisibility(View.VISIBLE);
        holder.sendingIv.startAnimation(mSendingAnim);
        holder.resend.setVisibility(View.GONE);
        //消息正在发送，重新注册一个监听消息发送完成的Callback
        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.sendingIv.clearAnimation();
                    if (status == 803008) {
                        CustomContent customContent = new CustomContent();
                        customContent.setBooleanValue("blackList", true);
                        Message customMsg = mConv.createSendMessage(customContent);
                        addMsgToList(customMsg);
                    } else if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                        holder.resend.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    //重发对话框
    private void showResendDialog(final ViewHolder holder, final Message msg) {
        /*OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.cancel_btn:
                        mDialog.dismiss();
                        break;
                    case R.id.commit_btn:
                        mDialog.dismiss();
                        if (msg.getContentType() == ContentType.image) {
                            resendImage(holder, msg);
                        } else {
                            resendTextOrVoice(holder, msg);
                        }
                        break;
                }
            }
        };*/

        if (msg.getContentType() == ContentType.image) {
            resendImage(holder, msg);
        } else {
            resendTextOrVoice(holder, msg);
        }
    }

    // 处理图片
    private void handleImgMsg(final Message msg, final ViewHolder holder, final int position) {
        final ImageContent imgContent = (ImageContent) msg.getContent();
        // 先拿本地缩略图
        final String path = imgContent.getLocalThumbnailPath();
        // 接收图片
        if (msg.getDirect() == MessageDirect.receive) {
            if (path == null) {
                //从服务器上拿缩略图
                imgContent.downloadThumbnailImage(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int status, String desc, File file) {
                        if (status == 0) {
                            Picasso.with(mContext).load(file).into(holder.picture);
                        }
                    }
                });
            } else {
                setPictureScale(path, holder.picture);
                Picasso.with(mContext).load(new File(path)).into(holder.picture);
            }
            //群聊中显示昵称
            if (mIsGroup) {
                holder.displayName.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                    holder.displayName.setText(msg.getFromUser().getUserName());
                } else {
                    holder.displayName.setText(msg.getFromUser().getNickname());
                }
            }

            switch (msg.getStatus()) {
                case receive_fail:
                    holder.picture.setImageResource(R.drawable.fetch_failed);
                    break;
                default:
            }
            // 发送图片方，直接加载缩略图
        } else {
            try {
                setPictureScale(path, holder.picture);
                Picasso.with(mContext).load(new File(path))
                        .into(holder.picture);
            } catch (NullPointerException e) {
                Picasso.with(mContext).load(R.drawable.picture_not_found)
                        .into(holder.picture);
            }
            //检查状态
            switch (msg.getStatus()) {
                case send_success:
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.picture.setAlpha(1.0f);
                    holder.progressTv.setVisibility(View.GONE);
                    holder.resend.setVisibility(View.GONE);
                    break;
                case send_fail:
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.picture.setAlpha(1.0f);
                    holder.progressTv.setVisibility(View.GONE);
                    holder.resend.setVisibility(View.VISIBLE);
                    break;
                case send_going:
                    sendingImage(msg, holder);
                    break;
                default:
                    holder.picture.setAlpha(0.75f);
                    holder.sendingIv.setVisibility(View.VISIBLE);
                    holder.sendingIv.startAnimation(mSendingAnim);
                    holder.progressTv.setVisibility(View.VISIBLE);
                    holder.progressTv.setText("0%");
                    holder.resend.setVisibility(View.GONE);
                    //从别的界面返回聊天界面，继续发送
                    if (!mMsgQueue.isEmpty()) {
                        Message message = mMsgQueue.element();
                        if (message.getId() == msg.getId()) {
                            Log.d(TAG, "Start sending message");
                            JMessageClient.sendMessage(message);
                            mSendMsgId = message.getId();
                            sendingImage(message, holder);
                        }
                    }
            }
            // 点击重发按钮，重发图片
            holder.resend.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    showResendDialog(holder, msg);
                }
            });
        }
        if (holder.picture != null) {
            // 点击预览图片
            holder.picture.setOnClickListener(new BtnOrTxtListener(position, holder));
            holder.picture.setTag(position);
            holder.picture.setOnLongClickListener(mLongClickListener);
        }
    }

    private void sendingImage(final Message msg, final ViewHolder holder) {
        holder.picture.setAlpha(0.75f);
        holder.sendingIv.setVisibility(View.VISIBLE);
        holder.sendingIv.startAnimation(mSendingAnim);
        holder.progressTv.setVisibility(View.VISIBLE);
        holder.resend.setVisibility(View.GONE);
        //如果图片正在发送，重新注册上传进度Callback
        if (!msg.isContentUploadProgressCallbackExists()) {
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(double v) {
                    String progressStr = (int) (v * 100) + "%";
                    Log.d(TAG, "msg.getId: " + msg.getId() + " progress: " + progressStr);
                    holder.progressTv.setText(progressStr);
                }
            });
        }
        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, String desc) {
                    Log.d(TAG, "Got result status: " + status);
                    if (!mMsgQueue.isEmpty() && mMsgQueue.element().getId() == mSendMsgId) {
                        mMsgQueue.poll();
                        if (!mMsgQueue.isEmpty()) {
                            Message nextMsg = mMsgQueue.element();
                            JMessageClient.sendMessage(nextMsg);
                            mSendMsgId = nextMsg.getId();
                        }
                    }
                    holder.picture.setAlpha(1.0f);
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.progressTv.setVisibility(View.GONE);
                    if (status == 803008) {
                        CustomContent customContent = new CustomContent();
                        customContent.setBooleanValue("blackList", true);
                        Message customMsg = mConv.createSendMessage(customContent);
                        addMsgToList(customMsg);
                    } else if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                        holder.resend.setVisibility(View.VISIBLE);
                    }

                    Message message = mConv.getMessage(msg.getId());
                    mMsgList.set(mMsgList.indexOf(msg), message);
                    Log.d(TAG, "msg.getId " + msg.getId() + " msg.getStatus " + msg.getStatus());
                    Log.d(TAG, "message.getId " + message.getId() + " message.getStatus " + message.getStatus());
//                    notifyDataSetChanged();
                }
            });

        }
    }

    private ArrayList<Integer> getImgMsgIDList() {
        ArrayList<Integer> imgMsgIDList = new ArrayList<Integer>();
        for (Message msg : mMsgList) {
            if (msg.getContentType() == ContentType.image) {
                imgMsgIDList.add(msg.getId());
            }
        }
        return imgMsgIDList;
    }

    private void resendTextOrVoice(final ViewHolder holder, Message msg) {
        holder.resend.setVisibility(View.GONE);
        holder.sendingIv.setVisibility(View.VISIBLE);
        holder.sendingIv.startAnimation(mSendingAnim);

        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, String desc) {
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                        holder.resend.setVisibility(View.VISIBLE);
                        Log.i(TAG, "Resend message failed!");
                    }
                }
            });
        }

        JMessageClient.sendMessage(msg);
    }

    /**
     * 设置图片最小宽高
     *
     * @param path      图片路径
     * @param imageView 显示图片的View
     */
    private void setPictureScale(String path, ImageView imageView) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        //计算图片缩放比例
        double imageWidth = opts.outWidth;
        double imageHeight = opts.outHeight;
        if (imageWidth < 100 * mDensity) {
            imageHeight = imageHeight * (100 * mDensity / imageWidth);
            imageWidth = 100 * mDensity;
        }
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = (int) imageWidth;
        params.height = (int) imageHeight;
        imageView.setLayoutParams(params);
    }

    private void resendImage(final ViewHolder holder, Message msg) {
        holder.sendingIv.setVisibility(View.VISIBLE);
        holder.sendingIv.startAnimation(mSendingAnim);
        holder.picture.setAlpha(0.75f);
        holder.resend.setVisibility(View.GONE);
        holder.progressTv.setVisibility(View.VISIBLE);
        try {
            // 显示上传进度
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(final double progress) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String progressStr = (int) (progress * 100) + "%";
                            holder.progressTv.setText(progressStr);
                        }
                    });
                }
            });
            if (!msg.isSendCompleteCallbackExists()) {
                msg.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(final int status, String desc) {
                        holder.sendingIv.clearAnimation();
                        holder.sendingIv.setVisibility(View.GONE);
                        holder.progressTv.setVisibility(View.GONE);
                        holder.picture.setAlpha(1.0f);
                        if (status != 0) {
                            HandleResponseCode.onHandle(mContext, status, false);
                            holder.resend.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            JMessageClient.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleVoiceMsg(final Message msg, final ViewHolder holder, final int position) {
        final VoiceContent content = (VoiceContent) msg.getContent();
        final MessageDirect msgDirect = msg.getDirect();
        int length = content.getDuration();
        String voiceLength = length + mContext.getString(R.string.symbol_second);
        holder.voiceLength.setText(voiceLength);
        //控制语音长度显示，长度增幅随语音长度逐渐缩小
        int width = (int) (-0.04 * length * length + 4.526 * length + 75.214);
        holder.txtContent.setWidth((int) (width * mDensity));
        holder.txtContent.setTag(position);
        holder.txtContent.setOnLongClickListener(mLongClickListener);
        if (msgDirect == MessageDirect.send) {
            holder.voice.setImageResource(R.drawable.ic_voice_all_left);
            switch (msg.getStatus()) {
                case send_success:
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.resend.setVisibility(View.GONE);
                    break;
                case send_fail:
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.resend.setVisibility(View.VISIBLE);
                    break;
                case send_going:
                    sendingTextOrVoice(holder, msg);
                    break;
                default:
            }

            holder.resend.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (msg.getContent() != null) {
                        showResendDialog(holder, msg);
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.sdcard_not_exist_toast),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else switch (msg.getStatus()) {
            case receive_success:
                if (mIsGroup) {
                    holder.displayName.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                        holder.displayName.setText(msg.getFromUser().getUserName());
                    } else {
                        holder.displayName.setText(msg.getFromUser().getNickname());
                    }
                }
                holder.voice.setImageResource(R.drawable.ic_voice_all_right);
                // 收到语音，设置未读
                if (msg.getContent().getBooleanExtra("isReaded") == null
                        || !msg.getContent().getBooleanExtra("isReaded")) {
                    mConv.updateMessageExtra(msg, "isReaded", false);
                    holder.readStatus.setVisibility(View.VISIBLE);
                    if (mIndexList.size() > 0) {
                        if (!mIndexList.contains(position)) {
                            addTolistAndSort(position);
                        }
                    } else {
                        addTolistAndSort(position);
                    }
                    if (nextPlayPosition == position && autoPlay) {
                        playVoice(position, holder, false);
                    }
                } else if (msg.getContent().getBooleanExtra("isReaded").equals(true)) {
                    holder.readStatus.setVisibility(View.GONE);
                }
                break;
            case receive_fail:
                holder.voice.setImageResource(R.drawable.ic_voice_all_right);
                // 接收失败，从服务器上下载
                content.downloadVoiceFile(msg,
                        new DownloadCompletionCallback() {
                            @Override
                            public void onComplete(int status, String desc, File file) {
                                if (status != 0) {
                                    Toast.makeText(mContext, mContext.getString(R.string.voice_fetch_failed_toast),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.i("VoiceMessage", "reload success");
                                }
                            }
                        });
                break;
            case receive_going:
                break;
            default:
        }
        holder.txtContent.setOnClickListener(new BtnOrTxtListener(position, holder));
    }

    private void playVoice(final int position, final ViewHolder holder, final boolean isSender) {
        // 记录播放录音的位置
        mPosition = position;
        Message msg = mMsgList.get(position);
        if (autoPlay) {
            mConv.updateMessageExtra(msg, "isReaded", true);
            holder.readStatus.setVisibility(View.GONE);
            if (mVoiceAnimation != null) {
                mVoiceAnimation.stop();
                mVoiceAnimation = null;
            }
            holder.voice.setImageResource(R.drawable.voice_receive);
            mVoiceAnimation = (AnimationDrawable) holder.voice.getDrawable();
        }
        try {
            mp.reset();
            VoiceContent vc = (VoiceContent) msg.getContent();
            Log.i(TAG, "content.getLocalPath:" + vc.getLocalPath());
            mFIS = new FileInputStream(vc.getLocalPath());
            mFD = mFIS.getFD();
            mp.setDataSource(mFD);
            if (mIsEarPhoneOn) {
                mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            } else {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            mp.prepare();
            mp.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mVoiceAnimation.start();
                    mp.start();
                }
            });
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mVoiceAnimation.stop();
                    mp.reset();
                    mSetData = false;
                    if (isSender) {
                        holder.voice.setImageResource(R.drawable.ic_voice_all_left);
                    } else {
                        holder.voice.setImageResource(R.drawable.ic_voice_all_right);
                    }
                    if (autoPlay) {
                        int curCount = mIndexList.indexOf(position);
                        Log.d(TAG, "curCount = " + curCount);
                        if (curCount + 1 >= mIndexList.size()) {
                            nextPlayPosition = -1;
                            autoPlay = false;
                        } else {
                            nextPlayPosition = mIndexList.get(curCount + 1);
                            notifyDataSetChanged();
                        }
                        mIndexList.remove(curCount);
                    }
                }
            });
        } catch (FileNotFoundException e) {
            Toast.makeText(mContext, mContext.getString(R.string.file_not_found_toast),
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(mActivity, mContext.getString(R.string.file_not_found_toast),
                    Toast.LENGTH_SHORT).show();
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

    private void pauseVoice() {
        mp.pause();
        mSetData = true;
    }

    private void addTolistAndSort(int position) {
        mIndexList.add(position);
        Collections.sort(mIndexList);
    }

    private void handleLocationMsg(Message msg, ViewHolder holder, int position) {

    }

    public void stopMediaPlayer() {
        if (mp.isPlaying())
            mp.stop();
    }

    public static abstract class ContentLongClickListener implements OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            onContentLongClick((Integer) v.getTag(), v);
            return true;
        }

        public abstract void onContentLongClick(int position, View view);
    }

    class BtnOrTxtListener implements OnClickListener {

        private int position;
        private ViewHolder holder;

        public BtnOrTxtListener(int index, ViewHolder viewHolder) {
            this.position = index;
            this.holder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            Message msg = mMsgList.get(position);
            MessageDirect msgDirect = msg.getDirect();
            if (holder.txtContent != null && v.getId() == holder.txtContent.getId()) {
                if (msg.getContentType() == ContentType.voice) {
                    if (!FileHelper.isSdCardExist() && msg.getDirect() == MessageDirect.send) {
                        Toast.makeText(mContext, mContext.getString(R.string.sdcard_not_exist_toast),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 如果之前存在播放动画，无论这次点击触发的是暂停还是播放，停止上次播放的动画
                    if (mVoiceAnimation != null) {
                        mVoiceAnimation.stop();
                    }
                    // 播放中点击了正在播放的Item 则暂停播放
                    if (mp.isPlaying() && mPosition == position) {
                        if (msgDirect == MessageDirect.send) {
                            holder.voice.setImageResource(R.drawable.voice_send);
                        } else {
                            holder.voice.setImageResource(R.drawable.voice_receive);
                        }
                        mVoiceAnimation = (AnimationDrawable) holder.voice.getDrawable();
                        pauseVoice();
                        mVoiceAnimation.stop();
                        // 开始播放录音
                    } else if (msgDirect == MessageDirect.send) {
                        holder.voice.setImageResource(R.drawable.voice_send);
                        mVoiceAnimation = (AnimationDrawable) holder.voice.getDrawable();

                        // 继续播放之前暂停的录音
                        if (mSetData && mPosition == position) {
                            mVoiceAnimation.start();
                            mp.start();
                            // 否则重新播放该录音或者其他录音
                        } else {
                            playVoice(position, holder, true);
                        }
                        // 语音接收方特殊处理，自动连续播放未读语音
                    } else {
                        try {
                            // 继续播放之前暂停的录音
                            if (mSetData && mPosition == position) {
                                if (mVoiceAnimation != null) {
                                    mVoiceAnimation.start();
                                }
                                mp.start();
                                // 否则开始播放另一条录音
                            } else {
                                // 选中的录音是否已经播放过，如果未播放，自动连续播放这条语音之后未播放的语音
                                if (msg.getContent().getBooleanExtra("isReaded") == null
                                        || !msg.getContent().getBooleanExtra("isReaded")) {
                                    autoPlay = true;
                                    playVoice(position, holder, false);
                                    // 否则直接播放选中的语音
                                } else {
                                    holder.voice.setImageResource(R.drawable.voice_receive);
                                    mVoiceAnimation = (AnimationDrawable) holder.voice.getDrawable();
                                    playVoice(position, holder, false);
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (holder.picture != null && v.getId() == holder.picture.getId()) {
                ImageContent content = (ImageContent)msg.getContent();
                String[] photos = new String[1];
                if(!TextUtils.isEmpty(content.getLocalPath())){
                    photos[0] = content.getLocalPath();
                }else{
                    photos[0] = content.getLocalThumbnailPath();
                }
                QuanjiakanDialog.getInstance().initImageDialog(photos,mContext);
            }
        }
    }

    public static class ViewHolder {
        CircleImageView headIcon;
        TextView displayName;
        TextView txtContent;
        ImageView picture;
        TextView progressTv;
        ImageButton resend;
        TextView voiceLength;
        ImageView voice;
        // 录音是否播放过的标志
        ImageView readStatus;
        TextView location;
        TextView groupChange;
        ImageView sendingIv;

        TextView msgTime;
    }
}