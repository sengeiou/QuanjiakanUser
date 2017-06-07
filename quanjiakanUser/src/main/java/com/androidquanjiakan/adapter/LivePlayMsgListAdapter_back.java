package com.androidquanjiakan.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.util.LogUtil;
import com.quanjiakan.main.R;
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

import cn.jmessage.android.uikit.chatting.CircleImageView;
import cn.jmessage.android.uikit.chatting.utils.DialogCreator;
import cn.jmessage.android.uikit.chatting.utils.FileHelper;
import cn.jmessage.android.uikit.chatting.utils.HandleResponseCode;
import cn.jmessage.android.uikit.chatting.utils.IdHelper;
import cn.jmessage.android.uikit.chatting.utils.TimeFormat;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
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
public class LivePlayMsgListAdapter_back extends BaseAdapter {

    private static final String TAG = "MsgListAdapter";
    private static final int PAGE_MESSAGE_COUNT = 18;
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
    // 位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    // 语音
    private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVER_VOICE = 7;
    //群成员变动
    private final int TYPE_GROUP_CHANGE = 8;
    //自定义消息
    private final int TYPE_CUSTOM_TXT = 9;
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
    private int mOffset = PAGE_MESSAGE_COUNT;
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


    private MessageNameClickListener nameClickListener;

    public LivePlayMsgListAdapter_back(Context context, String targetId, String appKey,
                                       ContentLongClickListener longClickListener) {
        initData(context);
        this.mTargetId = targetId;
        if (appKey != null) {
            mTargetAppKey = appKey;
            this.mConv = JMessageClient.getSingleConversation(mTargetId, appKey);
        } else {
            this.mConv = JMessageClient.getSingleConversation(mTargetId);
        }
        this.mLongClickListener = longClickListener;
        this.mMsgList = mConv.getMessagesFromNewest(0, mOffset);
        reverse(mMsgList);
        mStart = mOffset;
        UserInfo userInfo = (UserInfo) mConv.getTargetInfo();
        if (!TextUtils.isEmpty(userInfo.getAvatar())) {
            userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int status, String desc, Bitmap bitmap) {
                    if (status == 0) {
                        notifyDataSetChanged();
                    } else {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });
        }
        checkSendingImgMsg();
    }

    private void reverse(List<Message> list) {
        if (list.size() >0 ){
            Collections.reverse(list);
        }
    }

    /**
     * 群组
     *
     * @param context
     * @param groupId
     * @param longClickListener
     */
    public LivePlayMsgListAdapter_back(Context context, long groupId, ContentLongClickListener longClickListener) {
        initData(context);
        this.mGroupId = groupId;
        this.mIsGroup = true;
        this.mLongClickListener = longClickListener;
        this.mConv = JMessageClient.getGroupConversation(groupId);
        if(this.mConv==null){
            this.mConv = Conversation.createGroupConversation(groupId);
        }
        if(this.mConv!=null){
            this.mMsgList = mConv.getMessagesFromNewest(0, mOffset);
            reverse(mMsgList);
            mStart = mOffset;
            checkSendingImgMsg();
        }
    }

    public LivePlayMsgListAdapter_back(Context context, long groupId, ContentLongClickListener longClickListener,MessageNameClickListener contentClickListener) {
        initData(context);
        this.mGroupId = groupId;
        this.mIsGroup = true;
        this.mLongClickListener = longClickListener;
        this.nameClickListener = contentClickListener;
        this.mConv = JMessageClient.getGroupConversation(groupId);
        if(this.mConv==null){
            this.mConv = Conversation.createGroupConversation(groupId);
        }
        if(this.mConv!=null){
            this.mMsgList = mConv.getMessagesFromNewest(0, mOffset);
            reverse(mMsgList);
            mStart = mOffset;
            checkSendingImgMsg();
        }
    }

    private void initData(Context context) {
        this.mContext = context;
        mActivity = (Activity) context;
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;
        mDensity = dm.density;
        mInflater = LayoutInflater.from(mContext);

        mSendingAnim = AnimationUtils.loadAnimation(mContext, IdHelper.getAnim(mContext, "jmui_rotate"));
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
            List<Message> msgList = mConv.getMessagesFromNewest(mStart, PAGE_MESSAGE_COUNT);
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
        //是文字类型或者自定义类型（用来显示群成员变化消息）
        switch (msg.getContentType()) {
            case text:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_TXT
                        : TYPE_RECEIVE_TXT;
            case image:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_IMAGE
                        : TYPE_RECEIVER_IMAGE;
            case voice:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_VOICE
                        : TYPE_RECEIVER_VOICE;
            case location:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_LOCATION
                        : TYPE_RECEIVER_LOCATION;
            case eventNotification:
                return TYPE_GROUP_CHANGE;
            default:
                return TYPE_CUSTOM_TXT;
        }
    }

    public int getViewTypeCount() {
        return 11;
    }

    private View createViewByType(Message msg, int position) {
        // 会话类型
        switch (msg.getContentType()) {
            case image:
                return getItemViewType(position) == TYPE_SEND_IMAGE ? mInflater
                        .inflate(IdHelper.getLayout(mContext, "jmui_chat_item_receive_image"), null) : mInflater
                        .inflate(IdHelper.getLayout(mContext, "jmui_chat_item_receive_image"), null);
            case voice:
                return getItemViewType(position) == TYPE_SEND_VOICE ? mInflater
                        .inflate(IdHelper.getLayout(mContext, "jmui_chat_item_receive_voice"), null) : mInflater
                        .inflate(IdHelper.getLayout(mContext, "jmui_chat_item_receive_voice"), null);
            case location:
                return getItemViewType(position) == TYPE_SEND_LOCATION ? mInflater
                        .inflate(IdHelper.getLayout(mContext, "jmui_chat_item_receive_location"), null) : mInflater
                        .inflate(IdHelper.getLayout(mContext, "jmui_chat_item_receive_location"), null);
            case eventNotification:
                if (getItemViewType(position) == TYPE_GROUP_CHANGE)
                    return mInflater.inflate(IdHelper.getLayout(mContext, "jmui_chat_item_group_change"), null);
            case text:
                return getItemViewType(position) == TYPE_SEND_TXT ? mInflater
                        .inflate(IdHelper.getLayout(mContext, "jmui_chat_item_send_text_play_new"), null) : mInflater
                        .inflate(IdHelper.getLayout(mContext, "jmui_chat_item_receive_text_play_new"), null);
            default:
                return mInflater.inflate(IdHelper.getLayout(mContext, "jmui_chat_item_group_change"), null);
        }
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = createViewByType(msg, position);
            switch (msg.getContentType()) {
                case text:

                    holder.headIcon = (CircleImageView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_avatar_iv"));
                    holder.userLevel = (TextView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_display_level"));
                    holder.displayName = (TextView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_display_name_tv"));
                    holder.txtContent = (TextView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_msg_content"));
                    holder.sendingIv = (ImageView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_sending_iv"));
                    holder.resend = (ImageButton) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_fail_resend_ib"));
                    break;
                case image:
                    holder.headIcon = (CircleImageView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_avatar_iv"));
                    holder.displayName = (TextView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_display_name_tv"));
                    holder.picture = (ImageView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_picture_iv"));
                    holder.sendingIv = (ImageView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_sending_iv"));
                    holder.progressTv = (TextView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_progress_tv"));
                    holder.resend = (ImageButton) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_fail_resend_ib"));
                    break;
                case voice:
                    holder.headIcon = (CircleImageView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_avatar_iv"));
                    holder.displayName = (TextView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_display_name_tv"));
                    holder.txtContent = (TextView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_msg_content"));
                    holder.voice = (ImageView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_voice_iv"));
                    holder.sendingIv = (ImageView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_sending_iv"));
                    holder.voiceLength = (TextView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_voice_length_tv"));
                    holder.readStatus = (ImageView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_read_status_iv"));
                    holder.resend = (ImageButton) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_fail_resend_ib"));
                    break;
                case location:
                    holder.headIcon = (CircleImageView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_avatar_iv"));
                    holder.displayName = (TextView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_display_name_tv"));
                    holder.txtContent = (TextView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_msg_content"));
                    holder.sendingIv = (ImageView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_sending_iv"));
                    holder.resend = (ImageButton) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_fail_resend_ib"));
                    break;
                case eventNotification:
                    holder.groupChange = (TextView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_group_content"));
                    break;
                default:
                    holder.groupChange = (TextView) convertView
                            .findViewById(IdHelper.getViewID(mContext, "jmui_group_content"));
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //显示时间
        TextView msgTime = (TextView) convertView
                .findViewById(IdHelper.getViewID(mContext, "jmui_send_time_txt"));
        msgTime.setVisibility(View.GONE);
        long nowDate = msg.getCreateTime();
        if (mOffset == 18) {
            if (position == 0 || position % 18 == 0) {
                TimeFormat timeFormat = new TimeFormat(mContext, nowDate);
                msgTime.setText(timeFormat.getDetailTime());
                msgTime.setVisibility(View.GONE);
            } else {
                long lastDate = mMsgList.get(position - 1).getCreateTime();
                // 如果两条消息之间的间隔超过十分钟则显示时间
                if (nowDate - lastDate > 600000) {
                    TimeFormat timeFormat = new TimeFormat(mContext, nowDate);
                    msgTime.setText(timeFormat.getDetailTime());
                    msgTime.setVisibility(View.GONE);
                } else {
                    msgTime.setVisibility(View.GONE);
                }
            }
        } else {
            if (position == 0 || position == mOffset
                    || (position - mOffset) % 18 == 0) {
                TimeFormat timeFormat = new TimeFormat(mContext, nowDate);

                msgTime.setText(timeFormat.getDetailTime());
                msgTime.setVisibility(View.GONE);
            } else {
                long lastDate = mMsgList.get(position - 1).getCreateTime();
                // 如果两条消息之间的间隔超过十分钟则显示时间
                if (nowDate - lastDate > 600000) {
                    TimeFormat timeFormat = new TimeFormat(mContext, nowDate);
                    msgTime.setText(timeFormat.getDetailTime());
                    msgTime.setVisibility(View.GONE);
                } else {
                    msgTime.setVisibility(View.GONE);
                }
            }
        }

        //显示头像
        if (holder.headIcon != null) {
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getAvatar())) {
                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int status, String desc, Bitmap bitmap) {
                        if (status == 0) {
                            holder.headIcon.setImageBitmap(bitmap);
                        } else {
                            holder.headIcon.setImageResource(IdHelper.getDrawable(mContext,
                                    "jmui_head_icon"));
                            HandleResponseCode.onHandle(mContext, status, false);
                        }
                    }
                });
            } else {
                holder.headIcon.setImageResource(IdHelper.getDrawable(mContext, "jmui_head_icon"));
            }

            // 点击头像跳转到个人信息界面
            holder.headIcon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    //TODO jump to InfoActivity
                }
            });
        }

        switch (msg.getContentType()) {
            case text:
                handleTextMsg_oneLine(msg, holder, position);
                break;
            case image:
                handleImgMsg(msg, holder, position);
                break;
            case voice:
                handleVoiceMsg(msg, holder, position);
                break;
            case location:
                handleLocationMsg(msg, holder, position);
                break;
            case eventNotification:
                handleGroupChangeMsg(msg, holder, msgTime);
                break;
            default:
                handleCustomMsg(msg, holder);
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
                holder.groupChange.setVisibility(View.GONE);
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
                    holder.groupChange.setVisibility(View.GONE);
                    //群主亦显示
                } else if (myInfo.getUserName().equals(groupInfo.getGroupOwner())) {
                    holder.groupChange.setText(content);
                    holder.groupChange.setVisibility(View.GONE);
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
            holder.groupChange.setText(IdHelper.getString(mContext, "server_803008"));
            holder.groupChange.setVisibility(View.GONE);
        } else {
            holder.groupChange.setVisibility(View.GONE);
        }
    }

    private final long birth = 6400000000l;
    private final long divNumber = 35200000l;
    private final long div = 100;
    private String tempBirth;

    private final String level_1 = "1000000000";
    private final String level_2 = "2000000000";
    private final String level_3 = "3000000000";
    private final String level_4 = "4000000000";
    private final String level_5 = "5000000000";

    private static final String contentLevel_0 = "[//**--++^^0^^++--**//]";
    private static final String contentLevel_1 = "[//**--++^^1^^++--**//]";
    private static final String contentLevel_2 = "[//**--++^^2^^++--**//]";
    private static final String contentLevel_3 = "[//**--++^^3^^++--**//]";
    private static final String contentLevel_4 = "[//**--++^^4^^++--**//]";
    private static final String contentLevel_5 = "[//**--++^^5^^++--**//]";


    private static final String contentLevel_gift = "[//**--++^^gift^^++--**//]";
    private static final String contentLevel_concern = "[//**--++^^concern^^++--**//]";
    private static final String contentLevel_gag = "[//**--++^^gag^^++--**//]";

    private final int specificConcern = 1;
    private final int specificGag = 2;
    private final int specificGag2 = 3;
    private final int specificGift = 4;

    private void setSpecificText(int type,TextView container,String name,String red,String white){
        SpannableString stringText;
        switch (type){
            case specificConcern://关注
                stringText = new SpannableString(red);
                stringText.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.red)),0,red.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                container.setText(stringText);
                break;
            case specificGag://禁言  主播禁言了【红】某人【黄】
                stringText = new SpannableString(red+name);
                stringText.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.red)),0,red.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                stringText.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.live_play_yellow_name)),red.length(),red.length()+name.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                container.setText(stringText);
                break;
            case specificGag2://禁言  某人【黄】被主播禁言【红】
                stringText = new SpannableString(name+red);
                stringText.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.live_play_yellow_name)),0,name.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                stringText.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.red)),name.length(),red.length()+name.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                container.setText(stringText);
                break;
            case specificGift://送礼
                stringText = new SpannableString(name+red+white);
                stringText.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.live_play_yellow_name)),0,name.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                stringText.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.red)),name.length(),red.length()+name.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                stringText.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),red.length()+name.length(),red.length()+name.length()+white.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                container.setText(stringText);
                break;
        }
    }

    //******************************************************************************************************************************************************
    /**
     * 需要将等级，名称，消息显示在同一个TextView中
     * @param msg
     * @param holder
     * @param position
     */
    private void handleTextMsg_oneLine(final Message msg, final ViewHolder holder, int position) {
        final String content = ((TextContent) msg.getContent()).getText();
        /**
         * 一下两个东西需要隐藏
         * jmui_display_level
         * jmui_display_name_tv
         */
        holder.displayName.setVisibility(View.GONE);
        holder.userLevel.setVisibility(View.GONE);
        /**
         * 将等级信息放在消息中进行发送
         *
         * TODO 根据用户发送的消息中的特定用户等级信息[去除等级字符串]
         */
        final String content_new;
        //TODO 设置显示内容
        if(content.contains(contentLevel_1)){
            content_new = content.replace(contentLevel_1,"");
            setTextMessageOnOneLine(holder.txtContent,content,contentLevel_1,getUserName(msg),content_new,"");
        }else if(content.contains(contentLevel_2)){
            content_new = content.replace(contentLevel_2,"");
            setTextMessageOnOneLine(holder.txtContent,content,contentLevel_2,getUserName(msg),content_new,"");
        }else if(content.contains(contentLevel_3)){
            content_new = content.replace(contentLevel_3,"");
            setTextMessageOnOneLine(holder.txtContent,content,contentLevel_3,getUserName(msg),content_new,"");
        }else if(content.contains(contentLevel_4)){
            content_new = content.replace(contentLevel_4,"");
            setTextMessageOnOneLine(holder.txtContent,content,contentLevel_4,getUserName(msg),content_new,"");
        }else if(content.contains(contentLevel_5)){
            content_new = content.replace(contentLevel_5,"");
            setTextMessageOnOneLine(holder.txtContent,content,contentLevel_5,getUserName(msg),content_new,"");
        }else if(content.contains(contentLevel_0)){
            content_new = content.replace(contentLevel_0,"");
            setTextMessageOnOneLine(holder.txtContent,content,contentLevel_0,getUserName(msg),content_new,"");
        }
        else if(content.contains(contentLevel_gift)){// TODO 礼物  不需要发出去
            LogUtil.e("礼物："+content);
            setTextMessageOnOneLine(holder.txtContent,content,contentLevel_gift,getUserName(msg),"","");
        }
        else{
            content_new = content;
            setTextMessageOnOneLine(holder.txtContent,content,"",getUserName(msg),content_new,"");
        }
        //TODO 将等级信息放在消息中时，显示的消息

        //TODO Old 不将等级信息放在消息中时，显示的消息
//        holder.txtContent.setText(content);
        holder.txtContent.setTag(position);
        holder.txtContent.setShadowLayer(2f,3f,3f, Color.BLACK);
        holder.txtContent.setOnLongClickListener(mLongClickListener);
        /**
         * 由于现在名字与内容在相同的位置，
         */
        holder.txtContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameClickListener!=null){
                    nameClickListener.onContentClick(msg.getFromID());
                    nameClickListener.onClick(v);
                }
            }
        });
        /**
         * 现在是隐藏的
         */
        holder.displayName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameClickListener!=null){
                    nameClickListener.onContentClick(msg.getFromID());
                    nameClickListener.onClick(v);
                }
            }
        });

        /**
         * TODO 根据用户在极光的信息获取用户等级信息----等级保存在用户信息中
         */
//        tempBirth = msg.getFromUser().getSignature();
//        // 检查发送状态，发送方有重发机制
        if (msg.getDirect() == MessageDirect.send) {
//            holder.displayName.setText("自己:");//TODO 区分
            if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                holder.displayName.setText(msg.getFromUser().getUserName());
            } else {
                holder.displayName.setText(msg.getFromUser().getNickname());
            }
            holder.displayName.setShadowLayer(2f,3f,3f, Color.BLACK);
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
                case created:
                    /**
                     * 这是自定义的消息，不显示等级
                     */
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
                /**
                 * 设置等级可见性
                 */
                if(msg.getFromID().startsWith(CommonRequestCode.JMESSAGE_PREFIX) || msg.getFromID().startsWith(CommonRequestCode.JMESSAGE_PREFIX_DOCTOR)){
                    holder.userLevel.setVisibility(View.VISIBLE);
                }else{
                    holder.userLevel.setVisibility(View.GONE);
                }

                /**
                 * TODO 将等级信息携带在发送的消息中时，显示等级
                 */
                holder.txtContent.setTextColor(mContext.getResources().getColor(R.color.white));
                String content_news;
                if(content.contains(contentLevel_1)){
                    content_news = content.replace(contentLevel_1,"");
                    setTextMessageOnOneLine(holder.txtContent,content,contentLevel_1,getUserName(msg),content_news,"");
                }else if(content.contains(contentLevel_2)){
                    content_news = content.replace(contentLevel_2,"");
                    setTextMessageOnOneLine(holder.txtContent,content,contentLevel_2,getUserName(msg),content_news,"");
                }else if(content.contains(contentLevel_3)){
                    content_news = content.replace(contentLevel_3,"");
                    setTextMessageOnOneLine(holder.txtContent,content,contentLevel_3,getUserName(msg),content_news,"");
                }else if(content.contains(contentLevel_4)){
                    content_news = content.replace(contentLevel_4,"");
                    setTextMessageOnOneLine(holder.txtContent,content,contentLevel_4,getUserName(msg),content_news,"");
                }else if(content.contains(contentLevel_5)){
                    content_news = content.replace(contentLevel_5,"");
                    setTextMessageOnOneLine(holder.txtContent,content,contentLevel_5,getUserName(msg),content_news,"");
                }else if(content.contains(contentLevel_0)){
                    content_news = content.replace(contentLevel_0,"");
                    setTextMessageOnOneLine(holder.txtContent,content,contentLevel_0,getUserName(msg),content_news,"");
                }
                else if(content.contains(contentLevel_gift)){// TODO 礼物  不需要发出去
                    LogUtil.e("礼物："+content);
                    setTextMessageOnOneLine(holder.txtContent,content,contentLevel_gift,getUserName(msg),"","");
                }
                else{
                    content_news = content;
                    setTextMessageOnOneLine(holder.txtContent,content,"",getUserName(msg),content_news,"");
                }
                holder.userLevel.setVisibility(View.GONE);
                holder.displayName.setVisibility(View.GONE);
                holder.displayName.setShadowLayer(2f,3f,3f, Color.BLACK);
                if (msg!=null && msg.getFromUser()!=null && TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                    holder.displayName.setText(msg.getFromUser().getUserName());
                } else if( msg!=null && msg.getFromUser()!=null && !TextUtils.isEmpty(msg.getFromUser().getNickname()) ){
                    holder.displayName.setText(msg.getFromUser().getNickname());
                }else{
                    holder.displayName.setText("游客");
                }
            }else{
                holder.displayName.setVisibility(View.GONE);
                /*holder.displayName.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                    holder.displayName.setText(msg.getFromUser().getUserName());
                } else {
                    holder.displayName.setText(msg.getFromUser().getNickname());
                }*/
            }
        }
    }

    /**
     *
     * @param container 用于显示的容器
     * @param content 实际内容
     * @param level level
     * @param name
     * @param red
     * @param white
     */
    public void setTextMessageOnOneLine(TextView container,String content,String level,String name,String red,String white){
        SpannableString spannableString;
        name = name+"";//增加间距
        level = level + "";
        red = red + "";
        white = white + "";
        if(content.contains(contentLevel_1)){
            level = level+" ";//增加间距
            spannableString = new SpannableString(level+" "+name+" "+red+white);
            //等级图
            spannableString.setSpan(getImageSpan(R.drawable.one_level),0,level.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //黄色名字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.live_play_yellow_name)),
                    (" "+level).length(),(level+" "+name).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //白色文字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),
                    (level+" "+name+" ").length(),(level+" "+name+" "+red+white).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }else if(content.contains(contentLevel_2)){
            level = level+" ";//增加间距
            spannableString = new SpannableString(level+" "+name+" "+red+white);
            //等级图
            spannableString.setSpan(getImageSpan(R.drawable.two_level),0,level.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //黄色名字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.live_play_yellow_name)),
                    (" "+level).length(),(level+" "+name).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //白色文字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),
                    (level+" "+name+" ").length(),(level+" "+name+" "+red+white).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }else if(content.contains(contentLevel_3)){
            level = level+" ";//增加间距
            spannableString = new SpannableString(level+" "+name+" "+red+white);
            //等级图
            spannableString.setSpan(getImageSpan(R.drawable.three_level),0,level.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //黄色名字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.live_play_yellow_name)),
                    (" "+level).length(),(level+" "+name).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //白色文字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),
                    (level+" "+name+" ").length(),(level+" "+name+" "+red+white).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }else if(content.contains(contentLevel_4)){
            level = level+" ";//增加间距
            spannableString = new SpannableString(level+" "+name+" "+red+white);
            //等级图
            spannableString.setSpan(getImageSpan(R.drawable.four_level),0,level.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //黄色名字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.live_play_yellow_name)),
                    (" "+level).length(),(level+" "+name).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //白色文字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),
                    (level+" "+name+" ").length(),(level+" "+name+" "+red+white).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }else if(content.contains(contentLevel_5)){
            level = level+" ";//增加间距
            spannableString = new SpannableString(level+" "+name+" "+red+white);
            //等级图
            spannableString.setSpan(getImageSpan(R.drawable.five_level),0,level.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //黄色名字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.live_play_yellow_name)),
                    (" "+level).length(),(level+" "+name).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //白色文字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),
                    (level+" "+name+" ").length(),(level+" "+name+" "+red+white).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }else if(content.contains(contentLevel_0)){
            level = level+" ";//增加间距
            spannableString = new SpannableString(level+" "+name+" "+red+white);
            //等级图
            spannableString.setSpan(getImageSpan(R.drawable.zero_level),0,level.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //黄色名字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.live_play_yellow_name)),
                    (" "+level).length(),(level+" "+name).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //白色文字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),
                    (level+" "+name+" ").length(),(level+" "+name+" "+red+white).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        else if(content.contains(contentLevel_gift)){// TODO 礼物  不需要发出去
            String[] parts = new String[3];
            parts[0] = content.substring(0,content.indexOf(contentLevel_gift));//名字
            parts[1] = content.substring(content.indexOf(contentLevel_gift)+
                    contentLevel_gift.length(),content.lastIndexOf(contentLevel_gift));//红
            parts[2] = content.substring(content.lastIndexOf(contentLevel_gift)+contentLevel_gift.length());//白
            spannableString = new SpannableString(parts[0]+"  "+parts[1]+parts[2]);
            //黄色名字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.live_play_yellow_name)),
                    0,(parts[0]).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //红色文字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.red)),
                    (parts[0]+"  ").length(),(parts[0]+"  "+parts[1]).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //白色文字
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),
                    (parts[0]+"  "+parts[1]).length(),(parts[0]+"  "+parts[1]+parts[2]).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        else{
            spannableString = new SpannableString(name+" "+red);
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.live_play_yellow_name)),
                    0,(name).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.red)),
                    (name+" ").length(),(name+" "+red).length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        container.setText(spannableString);
    }

    public ImageSpan getImageSpan(int imgID){
        Drawable drawable = mContext.getResources().getDrawable(imgID);
        int fontH = (int) (mContext.getResources().getDimension(
                R.dimen.font_16) * 1.2);
        int height = fontH;
        int width = (int) (drawable.getIntrinsicWidth() * 1.0 / drawable.getIntrinsicHeight() * fontH);
        drawable.setBounds(0, 0, width, height);
//        span.setSpan(new ImageSpan(drawable), part1, part2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return new ImageSpan(drawable);
    }


    //******************************************************************************************************************************************************

    private void handleTextMsg(final Message msg, final ViewHolder holder, int position) {
        final String content = ((TextContent) msg.getContent()).getText();

        /**
         * 将等级信息放在消息中进行发送
         *
         * TODO 根据用户发送的消息中的特定用户等级信息[去除等级字符串]
         */
        final String content_new;
        if(content.contains(contentLevel_1)){
            content_new = content.replace(contentLevel_1,"");
        }else if(content.contains(contentLevel_2)){
            content_new = content.replace(contentLevel_2,"");
        }else if(content.contains(contentLevel_3)){
            content_new = content.replace(contentLevel_3,"");
        }else if(content.contains(contentLevel_4)){
            content_new = content.replace(contentLevel_4,"");
        }else if(content.contains(contentLevel_5)){
            content_new = content.replace(contentLevel_5,"");
        }else if(content.contains(contentLevel_0)){
            content_new = content.replace(contentLevel_0,"");
        }else{
            content_new = content;
        }

        //TODO 需要将

        //TODO 将等级信息放在消息中时，显示的消息
        holder.txtContent.setText(content_new);
        //TODO Old 不将等级信息放在消息中时，显示的消息
//        holder.txtContent.setText(content);
        holder.txtContent.setTag(position);
        holder.txtContent.setShadowLayer(2f,3f,3f, Color.BLACK);
         holder.txtContent.setOnLongClickListener(mLongClickListener);
        holder.txtContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameClickListener!=null){
                    nameClickListener.onContentClick(msg.getFromID());
                    nameClickListener.onClick(v);
                }
            }
        });
        holder.displayName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameClickListener!=null){
                    nameClickListener.onContentClick(msg.getFromID());
                    nameClickListener.onClick(v);
                }
            }
        });
        /**
         *
         */
        holder.userLevel.setVisibility(View.VISIBLE);
        /**
         * 统一文字颜色
         */
        holder.txtContent.setTextColor(mContext.getResources().getColor(R.color.white));//默认设置字为白色

        /**
         * TODO 根据用户在极光的信息获取用户等级信息----等级保存在用户信息中
         */
//        tempBirth = msg.getFromUser().getSignature();
//        // 检查发送状态，发送方有重发机制
        if (msg.getDirect() == MessageDirect.send) {
//            holder.displayName.setText("自己:");//TODO 区分
            if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                holder.displayName.setText(msg.getFromUser().getUserName());
            } else {
                holder.displayName.setText(msg.getFromUser().getNickname());
            }
            holder.displayName.setShadowLayer(2f,3f,3f, Color.BLACK);


            /**
             * TODO 将等级信息保存在极光用户信息中时显示等级信息
             * 实际验证时发现通信时，对方拿不到实时更新的信息
             * tempBirth
             */
            /**
             * TODO 将等级信息携带在发送的消息中时，显示等级
             */
            if(content.contains(contentLevel_1)){
                setUserName(holder,msg,NORMAL);
                holder.userLevel.setBackgroundResource(R.drawable.one_level);
                holder.userLevel.setText("");
            }else if(content.contains(contentLevel_2)){
                setUserName(holder,msg,NORMAL);
                holder.userLevel.setBackgroundResource(R.drawable.two_level);
                holder.userLevel.setText("");
            }else if(content.contains(contentLevel_3)){
                setUserName(holder,msg,NORMAL);
                holder.userLevel.setBackgroundResource(R.drawable.three_level);
                holder.userLevel.setText("");
            }else if(content.contains(contentLevel_4)){
                setUserName(holder,msg,NORMAL);
                holder.userLevel.setBackgroundResource(R.drawable.four_level);
                holder.userLevel.setText("");
            }else if(content.contains(contentLevel_5)){
                setUserName(holder,msg,NORMAL);
                holder.userLevel.setBackgroundResource(R.drawable.five_level);
                holder.userLevel.setText("");
            }else if(content.contains(contentLevel_0)){
                setUserName(holder,msg,NORMAL);
                holder.userLevel.setBackgroundResource(R.drawable.zero_level);
                holder.userLevel.setText("");
            }

            else if(content.contains(contentLevel_gift)){// TODO 礼物  不需要发出去
                LogUtil.e("礼物："+content);
                String[] parts = new String[3];
                parts[0] = content.substring(0,content.indexOf(contentLevel_gift));
                parts[1] = content.substring(content.indexOf(contentLevel_gift)+contentLevel_gift.length(),content.lastIndexOf(contentLevel_gift));
                parts[2] = content.substring(content.lastIndexOf(contentLevel_gift)+contentLevel_gift.length());
//                setUserName(holder,msg,NORMAL);
                holder.displayName.setText(parts[0]);
                holder.userLevel.setVisibility(View.GONE);
                holder.userLevel.setBackgroundResource(R.drawable.zero_level);
                holder.userLevel.setText("");
                setSpecificText(specificGift,holder.txtContent,"",parts[1],parts[2]);
            }

            else if(content.contains(contentLevel_gag)){// TODO 禁言  需要发出去
                setUserName(holder,msg,NORMAL);
                holder.userLevel.setVisibility(View.GONE);
                holder.userLevel.setBackgroundResource(R.drawable.zero_level);
                holder.userLevel.setText("");
            }

            else if(content.contains(contentLevel_concern)){// TODO 关注  需要发出去
                setUserName(holder,msg,NORMAL);
                holder.userLevel.setVisibility(View.GONE);
                holder.userLevel.setBackgroundResource(R.drawable.zero_level);
                holder.userLevel.setText("");
            }

            else{
                holder.userLevel.setVisibility(View.GONE);
                holder.userLevel.setBackgroundResource(R.drawable.zero_level);
                holder.userLevel.setText("");
                holder.txtContent.setTextColor(mContext.getResources().getColor(R.color.red));
                /**
                 * TODO 区分各个【系统】消息类型
                 */
                //1、关注消息      名字【黄】   关注了主播【红】

                //2、禁言消息      主播禁言了【红】  某人【黄】

                //3、禁言消息      某人【黄】   被主播禁言【红】

                //4、送礼消息      某人【黄】   送给主播【红】  礼物【白】

            }

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
                case created:
                    /**
                     * 这是自定义的消息，不显示等级
                     */
//                    holder.userLevel.setVisibility(View.GONE);
//                    holder.displayName.setText(msg.getFromUser().getNickname());
////                    holder.displayName.setText("系统消息");
//                    holder.txtContent.setText("关注了主播");
//                    holder.txtContent.setTextColor(mContext.getResources().getColor(R.color.red));
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
                /**
                 * 设置等级可见性
                 */
                if(msg.getFromID().startsWith(CommonRequestCode.JMESSAGE_PREFIX) || msg.getFromID().startsWith(CommonRequestCode.JMESSAGE_PREFIX_DOCTOR)){
                    holder.userLevel.setVisibility(View.VISIBLE);
                }else{
                    holder.userLevel.setVisibility(View.GONE);
                }
                /**
                 * TODO 将等级信息携带在发送的消息中时，显示等级
                 */
                holder.txtContent.setTextColor(mContext.getResources().getColor(R.color.white));

                if(content.contains(contentLevel_1)){
                    setUserName(holder,msg,NORMAL);
                    holder.userLevel.setBackgroundResource(R.drawable.one_level);
                    holder.userLevel.setText("");
                }else if(content.contains(contentLevel_2)){
                    setUserName(holder,msg,NORMAL);
                    holder.userLevel.setBackgroundResource(R.drawable.two_level);
                    holder.userLevel.setText("");
                }else if(content.contains(contentLevel_3)){
                    setUserName(holder,msg,NORMAL);
                    holder.userLevel.setBackgroundResource(R.drawable.three_level);
                    holder.userLevel.setText("");
                }else if(content.contains(contentLevel_4)){
                    setUserName(holder,msg,NORMAL);
                    holder.userLevel.setBackgroundResource(R.drawable.four_level);
                    holder.userLevel.setText("");
                }else if(content.contains(contentLevel_5)){
                    setUserName(holder,msg,NORMAL);
                    holder.userLevel.setBackgroundResource(R.drawable.five_level);
                    holder.userLevel.setText("");
                }else if(content.contains(contentLevel_0)){
                    setUserName(holder,msg,NORMAL);
                    holder.userLevel.setBackgroundResource(R.drawable.zero_level);
                    holder.userLevel.setText("");
                }

                else if(content.contains(contentLevel_gift)){// TODO 礼物  不需要发出去
                    String[] parts = new String[3];
                    parts[0] = content.substring(0,content.indexOf(contentLevel_gift));
                    parts[1] = content.substring(content.indexOf(contentLevel_gift)+contentLevel_gift.length(),content.lastIndexOf(contentLevel_gift));
                    parts[2] = content.substring(content.lastIndexOf(contentLevel_gift)+contentLevel_gift.length());
                    setUserName(holder,msg,NORMAL);
                    holder.userLevel.setVisibility(View.GONE);
                    holder.userLevel.setBackgroundResource(R.drawable.zero_level);
                    holder.userLevel.setText("");
                    setSpecificText(specificGift,holder.txtContent,"",parts[1],parts[2]);
                }

                else{
                    holder.userLevel.setVisibility(View.GONE);
                    holder.userLevel.setBackgroundResource(R.drawable.zero_level);
                    holder.userLevel.setText("");
                    holder.txtContent.setTextColor(mContext.getResources().getColor(R.color.red));
                }

                holder.displayName.setVisibility(View.VISIBLE);
                holder.displayName.setShadowLayer(2f,3f,3f, Color.BLACK);
                if (msg!=null && msg.getFromUser()!=null && TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                    holder.displayName.setText(msg.getFromUser().getUserName());
                } else if( msg!=null && msg.getFromUser()!=null && !TextUtils.isEmpty(msg.getFromUser().getNickname()) ){
                    holder.displayName.setText(msg.getFromUser().getNickname());
                }else{
                    holder.displayName.setText("游客");
                }
            }else{
                holder.displayName.setVisibility(View.GONE);
                /*holder.displayName.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                    holder.displayName.setText(msg.getFromUser().getUserName());
                } else {
                    holder.displayName.setText(msg.getFromUser().getNickname());
                }*/
            }
        }
    }

    private final int COLON = 1;
    private final int NORMAL = 2;
    public void setUserName(final ViewHolder holder,Message msg,int type){
        if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
            holder.displayName.setText(msg.getFromUser().getUserName());
        } else {
            if(type==COLON){
                holder.displayName.setText(msg.getFromUser().getNickname()+":");
            }else{
                holder.displayName.setText(msg.getFromUser().getNickname());
            }
        }
    }

    public String getUserName(Message msg){
        if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
            return msg.getFromUser().getUserName();
        } else {
            return msg.getFromUser().getNickname();
        }
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
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == IdHelper.getViewID(mContext, "jmui_cancel_btn")) {
                    mDialog.dismiss();
                } else {
                    mDialog.dismiss();
                    if (msg.getContentType() == ContentType.image) {
                        resendImage(holder, msg);
                    } else {
                        resendTextOrVoice(holder, msg);
                    }
                }
            }
        };
        mDialog = DialogCreator.createResendDialog(mContext, listener);
        mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
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
                    holder.picture.setImageResource(IdHelper.getDrawable(mContext, "jmui_fetch_failed"));
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
                Picasso.with(mContext).load(IdHelper.getDrawable(mContext, "jmui_picture_not_found"))
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
        holder.progressTv.setText("0%");
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

//    private ArrayList<String> getImgMsgPathList() {
//        ArrayList<String> imgMsgIDList = new ArrayList<String>();
//        for (Message msg : mMsgList) {
//            if (msg.getContentType() == ContentType.image) {
//                final ImageContent imgContent = (ImageContent) msg.getContent();
//                imgMsgIDList.add(imgContent.getImg_link());
//            }
//        }
//        return imgMsgIDList;
//    }

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
        String voiceLength = length + mContext.getString(IdHelper.getString(mContext, "jmui_symbol_second"));
        holder.voiceLength.setText(voiceLength);
        //控制语音长度显示，长度增幅随语音长度逐渐缩小
        int width = (int) (-0.04 * length * length + 4.526 * length + 75.214);
        holder.txtContent.setWidth((int) (width * mDensity));
        holder.txtContent.setTag(position);
        holder.txtContent.setOnLongClickListener(mLongClickListener);
        if (msgDirect == MessageDirect.send) {
            holder.voice.setImageResource(IdHelper.getDrawable(mContext, "jmui_send_3"));
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
                        Toast.makeText(mContext, mContext.getString(IdHelper.getString(mContext,
                                        "jmui_sdcard_not_exist_toast")),
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
                holder.voice.setImageResource(IdHelper.getDrawable(mContext, "jmui_receive_3"));
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
                holder.voice.setImageResource(IdHelper.getDrawable(mContext, "jmui_receive_3"));
                // 接收失败，从服务器上下载
                content.downloadVoiceFile(msg,
                        new DownloadCompletionCallback() {
                            @Override
                            public void onComplete(int status, String desc, File file) {
                                if (status != 0) {
                                    Toast.makeText(mContext, IdHelper.getString(mContext,
                                                    "jmui_voice_fetch_failed_toast"),
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
            holder.voice.setImageResource(IdHelper.getAnim(mContext, "jmui_voice_receive"));
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
                        holder.voice.setImageResource(IdHelper.getDrawable(mContext, "jmui_send_3"));
                    } else {
                        holder.voice.setImageResource(IdHelper.getDrawable(mContext, "jmui_receive_3"));
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
            Toast.makeText(mContext, IdHelper.getString(mContext, "jmui_file_not_found_toast"),
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(mContext, IdHelper.getString(mContext, "jmui_file_not_found_toast"),
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
            onContentLongClick((Integer)v.getTag(), v);
            return true;
        }

        public abstract void onContentLongClick(int position, View view);
    }

    public static abstract class MessageNameClickListener implements OnClickListener {
        public abstract void onContentClick(String userID);
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
                        Toast.makeText(mContext, IdHelper.getString(mContext, "jmui_sdcard_not_exist_toast"),
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
                            holder.voice.setImageResource(IdHelper.getAnim(mContext, "jmui_voice_send"));
                        } else {
                            holder.voice.setImageResource(IdHelper.getAnim(mContext, "jmui_voice_receive"));
                        }
                        mVoiceAnimation = (AnimationDrawable) holder.voice.getDrawable();
                        pauseVoice();
                        mVoiceAnimation.stop();
                        // 开始播放录音
                    } else if (msgDirect == MessageDirect.send) {
                        holder.voice.setImageResource(IdHelper.getAnim(mContext, "jmui_voice_send"));
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
                                    holder.voice.setImageResource(IdHelper.getAnim(mContext, "jmui_voice_receive"));
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
                //TODO jump to BrowserViewPagerActivity
                ImageContent imageContent = (ImageContent) msg.getContent();
                imageContent.downloadOriginImage(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        if(i==0){
                        }
                    }
                });

            }
        }
    }

    public static class ViewHolder {
        CircleImageView headIcon;
        TextView userLevel;
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
    }
}