package com.androidquanjiakan.activity.video;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquanjiakan.adapter.AudienceHeadAdapter;
import com.androidquanjiakan.adapter.LivePlayMsgListAdapter_back;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.VideoLiveListEntity;
import com.androidquanjiakan.view.CircleTransformation;
import com.duanqu.qupai.mediaplayer.DataSpec;
import com.duanqu.qupai.mediaplayer.QuPlayer;
import com.duanqu.qupai.mediaplayer.QuPlayerExt;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.quanjiakanuser.util.SpaceItemDecoration;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cn.jmessage.android.uikit.chatting.ChatView;
import cn.jmessage.android.uikit.chatting.utils.IdHelper;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;


public class VideoLivePlayActivity_backup extends Activity
        implements CompoundButton.OnCheckedChangeListener, SurfaceHolder.Callback, View.OnClickListener, View.OnLayoutChangeListener {
    private static final String TAG = "QuPlayer";
    private QuPlayerExt mQuPlayer;
    private Surface _Surface;
    private SurfaceView _SurfaceView;
    private Handler _Handler;

    private final int[] IMGLIST = new int[]{
            R.drawable.liveicon1, R.drawable.liveicon2, R.drawable.liveicon3,
            R.drawable.liveicon4, R.drawable.liveicon5, R.drawable.liveicon6,
            R.drawable.liveicon7, R.drawable.liveicon8, R.drawable.liveicon9,
            R.drawable.liveicon10, R.drawable.liveicon11, R.drawable.liveicon12,
            R.drawable.liveicon13, R.drawable.liveicon14, R.drawable.liveicon15,
            R.drawable.liveicon16, R.drawable.liveicon17, R.drawable.liveicon18,
            R.drawable.liveicon19, R.drawable.liveicon20, R.drawable.liveicon21,
            R.drawable.liveicon22, R.drawable.liveicon23, R.drawable.liveicon24,
            R.drawable.liveicon25, R.drawable.liveicon26, R.drawable.liveicon27,
            R.drawable.liveicon28, R.drawable.liveicon29, R.drawable.liveicon30,
            R.drawable.liveicon31};

//    private RelativeLayout ll_one;

    private boolean isShow = true;
    private LinearLayout ll_me;
    private ImageView iv_icon;
    private ImageView concern;

    private TextView tv_livinger_name;
    private TextView tv_watch_number;

    public static final String PARAM_ENTITY = "entity";
    public static final String PARAM_URL = "url";
    public static final String PARAM_HEADIMG = "headimg_url";
    public static final String PARAM_GROUP = "group_id";
    public static final String PARAM_NUMBER = "number";
    public static final String PARAM_NAME = "name";

    private VideoLiveListEntity entity;
    private String urlString;
    private String headImage;
    private String groupId;
    private String number;
    private String name;
    private RecyclerView recyclerView;
    private List<Integer> mDatas;
    private AudienceHeadAdapter audienceHeadAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.layout_live_play_chat_back);

        entity = (VideoLiveListEntity) getIntent().getSerializableExtra(PARAM_ENTITY);
        urlString = getIntent().getStringExtra(PARAM_URL);
        groupId = getIntent().getStringExtra(PARAM_GROUP);
        number = getIntent().getStringExtra(PARAM_NUMBER);
        name = getIntent().getStringExtra(PARAM_NAME);
        headImage = getIntent().getStringExtra(PARAM_HEADIMG);

        if (entity == null || urlString == null || groupId == null
                || number == null
                || name == null) {
            BaseApplication.getInstances().toast(VideoLivePlayActivity_backup.this,"传入参数异常!");
            exitGroup();
            finish();
            return;
        }
        mQuPlayer = new QuPlayerExt();
        _SurfaceView = (SurfaceView) findViewById(R.id.surface);
        _SurfaceView.getHolder().addCallback(this);
        _Handler = new Handler(Looper.myLooper());

        initInputView();

        initChatView();

        initJPush();

        _Handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mQuPlayer != null) {
                    //进入时会检查拉流地址，当不可用时，会回调该Error接口
                    mQuPlayer.setErrorListener(_ErrorListener);
                    mQuPlayer.setInfoListener(_InfoListener);
                    mQuPlayer.setSurface(_Surface);
                    DataSpec spec = new DataSpec(urlString, DataSpec.MEDIA_TYPE_STREAM);
                    mQuPlayer.setDataSource(spec);
                    mQuPlayer.setLooping(false);
                    mQuPlayer.prepare();
                    mQuPlayer.start();
                } else {
                    mQuPlayer = new QuPlayerExt();
                    //进入时会检查拉流地址，当不可用时，会回调该Error接口
                    mQuPlayer.setErrorListener(_ErrorListener);
                    mQuPlayer.setInfoListener(_InfoListener);
                    mQuPlayer.setSurface(_Surface);
                    DataSpec spec = new DataSpec(urlString, DataSpec.MEDIA_TYPE_STREAM);
                    mQuPlayer.setDataSource(spec);
                    mQuPlayer.setLooping(false);
                    mQuPlayer.prepare();
                    mQuPlayer.start();
                }
            }
        }, 300);

    }

//    private Timer timerHeart;
//    private TimerTask task;
//    private void startRandomHeart(){
//        timerHeart = new Timer();
//        final Random random = new Random();
//        task = new TimerTask() {
//            @Override
//            public void run() {
//                final int size = random.nextInt(5);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //TODO 这里向冒心的View中添加指定数量的心
//                        for (int i = size;i>0;i--){
//                            periscopeLayout.addHeart();
//                        }
//                    }
//                });
//            }
//        };
//        timerHeart.schedule(task,1000,1000);
//    }
//
//    private void endRandomHeart(){
//        if(task!=null){
//            task.cancel();
//        }
//        if(timerHeart!=null){
//            timerHeart.cancel();
//        }
//    }


    public void initJPush() {
        repeatCount = 0;
        LinkedHashSet<String> hashSet = new LinkedHashSet<String>();
        hashSet.add("" + groupId);
        JPushInterface.setTags(this, hashSet, new TagAliasCallback() {
            @Override
            public void gotResult(int result, String message, Set<String> set) {
            }
        });

        JMessageClient.setNoDisturbGlobal(1, new BasicCallback() {
            @Override
            public void gotResult(int result, String message) {
            }
        });


        /*
        TODO For Test
         */
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                retryConcernCount = 0;
                createConcernMessage("小莫");
            }
        },5000);

        /**
         * 重置用户生日，暂由等级代替
         *
         * 1000 000 000 000
         *
         *  999 964 800 000
         */
        resetLevelInfo(birth*5);
    }
            private Handler mHandler = new Handler();

            private ChatView mChatView;
            private Context mContext;
            protected float mDensity;
            protected int mDensityDpi;
            private LivePlayMsgListAdapter_back mChatAdapter;
            private Conversation mConv;

            private View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

        }
    };

    public void initChatView() {

        mContext = this;
        mChatView = (ChatView) findViewById(IdHelper.getViewID(this, "jmui_chat_view"));
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mChatView.initModule(mDensity, mDensityDpi);
        mConv = JMessageClient.getGroupConversation(Long.parseLong(groupId));
        if (mConv == null) {
            mConv = Conversation.createGroupConversation(Long.parseLong(groupId));
        }
        if (mConv != null) {
            mConv.deleteAllMessage();
        }
        mChatAdapter = new LivePlayMsgListAdapter_back(mContext, Long.parseLong(groupId), longClickListener, new LivePlayMsgListAdapter_back.MessageNameClickListener() {
            @Override
            public void onContentClick(String userID) {
                /**
                 * TODO 点击用户名，点击用户发送的信息时的回调
                 */
//                Toast.makeText(mContext, "获取的非自己用户ID:"+userID, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClick(View v) {

            }
        });
        mChatView.setChatListAdapter(mChatAdapter);
        JMessageClient.registerEventReceiver(this);
        mChatView.setToBottom();
        JMessageClient.getGroupInfo(Long.parseLong(groupId), new GetGroupInfoCallback() {
            @Override
            public void gotResult(int i, String s, GroupInfo groupInfo) {

            }
        });
    }

    /**
     * 接收消息类事件
     *
     * @param event 消息事件
     */
    public void onEvent(MessageEvent event) {
        final Message msg = event.getMessage();
        //若为群聊相关事件，如添加、删除群成员
        Log.i(TAG, event.getMessage().toString());
        if (msg.getContentType() == ContentType.eventNotification) {
            GroupInfo groupInfo = (GroupInfo) msg.getTargetInfo();
            long groupId = groupInfo.getGroupID();
            UserInfo myInfo = JMessageClient.getMyInfo();
            EventNotificationContent.EventNotificationType type = ((EventNotificationContent) msg
                    .getContent()).getEventNotificationType();
            if (groupId == Long.parseLong(this.groupId)) {
                switch (type) {
                    case group_member_added:
                        //添加群成员事件
                        List<String> userNames = ((EventNotificationContent) msg.getContent()).getUserNames();
                        //群主把当前用户添加到群聊，则显示聊天详情按钮
                        refreshGroupNum();
                        if (userNames.contains(myInfo.getNickname()) || userNames.contains(myInfo.getUserName())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mChatView.showRightBtn();
                                }
                            });
                        }

                        break;
                    case group_member_removed:
                        //删除群成员事件
                        userNames = ((EventNotificationContent) msg.getContent()).getUserNames();
                        //群主删除了当前用户，则隐藏聊天详情按钮
                        if (userNames.contains(myInfo.getNickname()) || userNames.contains(myInfo.getUserName())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mChatView.dismissRightBtn();
                                    GroupInfo groupInfo = (GroupInfo) mConv.getTargetInfo();
                                    if (TextUtils.isEmpty(groupInfo.getGroupName())) {
                                        mChatView.setChatTitle(IdHelper.getString(mContext, "group"));
                                    } else {
                                        mChatView.setChatTitle(groupInfo.getGroupName());
                                    }
                                    mChatView.dismissGroupNum();
                                }
                            });
                        } else {
                            refreshGroupNum();
                        }

                        break;
                    case group_member_exit:
                        refreshGroupNum();
                        break;
                }
            }
        }


        //刷新消息
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //收到消息的类型为单聊
                if (msg.getTargetType() == ConversationType.single) {

                } else {
                    long groupId = ((GroupInfo) msg.getTargetInfo()).getGroupID();

                    if (groupId == Long.parseLong(VideoLivePlayActivity_backup.this.groupId) &&
                            msg.getContentType() == ContentType.text
                            ) {
                        Message lastMsg = mChatAdapter.getLastMsg();
                        if (lastMsg == null || msg.getId() != lastMsg.getId()) {
                            mChatAdapter.addMsgToList(msg);
                        } else {
                            mChatAdapter.notifyDataSetChanged();
                        }
                        mChatView.setToBottom();
                    }
                }
            }
        });
    }

    private void refreshGroupNum() {
        Conversation conv = JMessageClient.getGroupConversation(Long.parseLong(groupId));
        GroupInfo groupInfo = (GroupInfo) conv.getTargetInfo();
        /**
         * TODO 此处刷新群组信息----例如群组成员
         */
    }

    /**
     * TODO
     */
    private LivePlayMsgListAdapter_back.ContentLongClickListener longClickListener = new LivePlayMsgListAdapter_back.ContentLongClickListener() {
        @Override
        public void onContentLongClick(final int position, View view) {
        }
    };

    /**
     * 通过
     * @param who
     */
    private int retryConcernCount = 0;
    public void createConcernMessage(final String who) {
        if(retryConcernCount>5){
            return;
        }
        retryConcernCount++;
        /**
         * 可以实现显示该消息
         */
        if(JMessageClient.getMyInfo()!=null){
            HashMap<String, String> map = new HashMap<>();
            Message msg = JMessageClient.createGroupTextMessage(Long.parseLong(groupId),"关注了主播");

            mChatAdapter.addMsgToList(msg);
            mChatAdapter.notifyDataSetChanged();
            mChatView.setToBottom();
        }else{
            JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        HashMap<String, String> map = new HashMap<>();
                        Message msg = JMessageClient.createGroupTextMessage(Long.parseLong(groupId),"关注了主播");

                        mChatAdapter.addMsgToList(msg);
                        mChatAdapter.notifyDataSetChanged();
                        mChatView.setToBottom();
                    } else {
                        createConcernMessage(who);
                    }
                }
            });
        }

    }
    //1482940800000
    //1483000000000
    /**
     * 1480000000005
     *
     * 1479916800000
     * 1479916800000
     *
     * 1479916800000
     */
    private String currentLevel = contentLevel_1;
    private static final String contentLevel_1 = "[//**--++^^1^^++--**//]";
    private static final String contentLevel_2 = "[//**--++^^2^^++--**//]";
    private static final String contentLevel_3 = "[//**--++^^3^^++--**//]";
    private static final String contentLevel_4 = "[//**--++^^4^^++--**//]";
    private static final String contentLevel_5 = "[//**--++^^5^^++--**//]";


    private int repeatCount = 0;
    private UserInfo userInfo = JMessageClient.getMyInfo();
    private final long birth = 1000000000l;

    private final long divNumber = 35200000l;
    public void resetLevelInfo(final long birthNumber){
        /**
         * TODO 设置用户，并更新用户信息   可用，但实际测试发现不太稳定
         */
        if (repeatCount > 5) {
            return;
        }
        repeatCount++;

        userInfo = JMessageClient.getMyInfo();
        if (userInfo != null) {
            userInfo.setSignature(birthNumber+"");
            JMessageClient.updateMyInfo(UserInfo.Field.signature, userInfo, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        loginRepeatCount = 0;
                        JMessageClient.logout();
                        relogin();
                        return;
                    } else {
                        resetLevelInfo(birthNumber);
                    }
                }
            });
        }else{
            JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        userInfo = JMessageClient.getMyInfo();
                        userInfo.setSignature(birthNumber+"");
                        JMessageClient.updateMyInfo(UserInfo.Field.signature, userInfo, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0) {
                                    loginRepeatCount = 0;
                                    JMessageClient.logout();
                                    relogin();
                                    return;
                                } else {
                                    resetLevelInfo(birthNumber);
                                }
                            }
                        });
                    }else{
                        resetLevelInfo(birthNumber);
                    }
                }
            });
        }
    }

    private int loginRepeatCount = 0;
    public void relogin(){
        if(loginRepeatCount>10){
            return;
        }
        loginRepeatCount++;
        JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                }else{
                    relogin();
                }
            }
        });
    }

    public void doConcern() {
        //TODO 需要进行网络访问，变更数据库中的关注状态
        concern.setEnabled(false);
        if (entity.getIsFollow() > 0) {
            MyHandler.putTask(VideoLivePlayActivity_backup.this, new Task(new HttpResponseInterface() {
                @Override
                public void handMsg(String val) {
                    concern.setEnabled(true);
                    if (val != null && val.toLowerCase().startsWith("{") && val.toLowerCase().endsWith("}")) {
                        JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                        if (jsonObject.has("code") && "1".equals(jsonObject.get("code").getAsString())) {
                            entity.setIsFollow(0);
                            setConcern();
                        }
                    } else {

                    }
                }
            }, HttpUrls.postConcern(entity.getUserId(), "0"), null, Task.TYPE_GET_STRING_NOCACHE, null));
        } else {
            MyHandler.putTask(VideoLivePlayActivity_backup.this, new Task(new HttpResponseInterface() {
                @Override
                public void handMsg(String val) {
                    concern.setEnabled(true);
                    if (val != null && val.toLowerCase().startsWith("{") && val.toLowerCase().endsWith("}")) {
                        JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                        if (jsonObject.has("code") && "1".equals(jsonObject.get("code").getAsString())) {
                            entity.setIsFollow(1);
                            setConcern();
                        }
                    } else {

                    }
                }
            }, HttpUrls.postConcern(entity.getUserId(), "1"), null, Task.TYPE_GET_STRING_NOCACHE, null));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.concern: {
                doConcern();
                break;
            }
            case R.id.iv_sockt:
                if (isShow) {
                    iv_sockt.setImageResource(R.drawable.icon_live_barrage_sel);
                    isShow = false;
                    mChatView.setVisibility(View.INVISIBLE);
                    ll_me.setVisibility(View.INVISIBLE);
                    et_comment.setVisibility(View.INVISIBLE);
                    iv_close.setVisibility(View.INVISIBLE);
                } else {
                    iv_sockt.setImageResource(R.drawable.icon_live_barrage_nor);
                    isShow = true;
                    mChatView.setVisibility(View.VISIBLE);
                    ll_me.setVisibility(View.VISIBLE);
                    et_comment.setVisibility(View.VISIBLE);
                    iv_close.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_camera:
                break;
            case R.id.iv_close:
                if (et_comment.length() < 1) {
                    finish();
                } else {
                    //发送消息
                    iv_close.setImageResource(R.drawable.icon_live_close);
                    HidenKeyboard(view);
                    /**
                     * TODO 需要添加发送消息的逻辑
                     */
                    retryCount = 0;
                    doSendMessage();
                }
                break;
        }
    }

    private int retryCount = 0;

    public void doSendMessage() {
        if (retryCount > 3) {
            BaseApplication.getInstances().toast(VideoLivePlayActivity_backup.this,"弹幕发送失败!");
            return;
        }
        retryCount++;
        if (JMessageClient.getMyInfo() != null) {
            String msgContent = et_comment.getText().toString();
//            TextContent content = new TextContent(msgContent);//TODO 不含用户等级信息的消息

            TextContent content = new TextContent(currentLevel+msgContent);//TODO 含用户等级信息的消息
            mConv = JMessageClient.getGroupConversation(Long.parseLong(groupId));
            if (mConv == null) {
                mConv = Conversation.createGroupConversation(Long.parseLong(groupId));
            }
            if (mConv != null) {
                final Message msg = mConv.createSendMessage(content);
                mChatAdapter.addMsgToList(msg);
                JMessageClient.sendMessage(msg);
                mChatView.setToBottom();
                et_comment.setText("");
            }
        } else {
            JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                @Override
                public void gotResult(int result, String message) {
                    if (result == 0) {
                        String msgContent = et_comment.getText().toString();
//                        TextContent content = new TextContent(msgContent);//TODO 不含用户等级信息的消息

                        TextContent content = new TextContent(currentLevel+msgContent);//TODO 含用户等级信息的消息
                        mConv = JMessageClient.getGroupConversation(Long.parseLong(groupId));
                        if (mConv == null) {
                            mConv = Conversation.createGroupConversation(Long.parseLong(groupId));
                        }
                        if (mConv != null) {
                            final Message msg = mConv.createSendMessage(content);
                            mChatAdapter.addMsgToList(msg);
                            JMessageClient.sendMessage(msg);
                            mChatView.setToBottom();
                            et_comment.setText("");
                        }
                    } else {
                        doSendMessage();
                    }
                }
            });
        }
    }

    public boolean HidenKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.SHOW_FORCED);
        return true;
    }


    private ImageView iv_sockt;//iv_camera
    private ImageView iv_camera;
    private EditText et_comment;
    private ImageView iv_close;

    public void initInputView() {
        tv_livinger_name = (TextView) findViewById(R.id.tv_livinger_name);
        tv_watch_number = (TextView) findViewById(R.id.tv_watch_number);
        tv_livinger_name.setText(name);
        tv_watch_number.setText(number);
        ll_me = (LinearLayout) findViewById(R.id.ll_me);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        concern = (ImageView) findViewById(R.id.concern);
        concern.setOnClickListener(this);
        setConcern();

        if (headImage != null && headImage.toLowerCase().startsWith("http")) {
            Picasso.with(BaseApplication.getInstances()).load(headImage).transform(new CircleTransformation()).into(iv_icon);
        } else {
            iv_icon.setImageResource(R.drawable.friend_default);
        }


        iv_sockt = (ImageView) findViewById(R.id.iv_sockt);
        iv_sockt.setImageResource(R.drawable.icon_live_barrage_nor);
        iv_sockt.setOnClickListener(this);
        /**
         * 医生端该图标需要显示，用于转换摄像头
         */
        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        iv_camera.setVisibility(View.GONE);
        iv_camera.setOnClickListener(this);

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

        et_comment = (EditText) findViewById(R.id.et_comment);
        et_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence == null || charSequence.length() == 0) {
                    iv_close.setImageResource(R.drawable.icon_live_close);
                } else {
                    iv_close.setImageResource(R.drawable.gou);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.id_recyclerview_horizontal);
        getRecycleData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VideoLivePlayActivity_backup.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//设置横向
        recyclerView.setLayoutManager(linearLayoutManager);
        audienceHeadAdapter = new AudienceHeadAdapter(this, mDatas);
        recyclerView.addItemDecoration(new SpaceItemDecoration(5));
        recyclerView.setAdapter(audienceHeadAdapter);
    }

    public void setConcern() {
        if (entity.getIsFollow() > 0) {
            concern.setImageResource(R.drawable.icon_focus_on_sel);
            concern.setVisibility(View.GONE);
        } else {
            concern.setImageResource(R.drawable.icon_focus_on_nor);
            concern.setVisibility(View.VISIBLE);
        }
    }

    private void getRecycleData() {
        if (random == null) {
            random = new Random();
        }
        mDatas = new ArrayList<Integer>();
        int position = 0;
        for (int i = 0; i < 10; i++) {
            position = random.nextInt(31);
            mDatas.add(IMGLIST[position]);
        }
    }

    public void exitGroup() {
        JMessageClient.setNoDisturbGlobal(0, new BasicCallback() {
            @Override
            public void gotResult(int result, String message) {
            }
        });

        JMessageClient.exitConversation();
        //退群后消息不会再收到
        JMessageClient.exitGroup(Long.parseLong(groupId), new BasicCallback() {
            @Override
            public void gotResult(int resultCode, String resultMessage) {
            }
        });

        LinkedHashSet<String> hashSet = new LinkedHashSet<String>();
        JPushInterface.setTags(this, hashSet, new TagAliasCallback() {
            @Override
            public void gotResult(int result, String message, Set<String> set) {
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mQuPlayer.setErrorListener(_ErrorListener);
            mQuPlayer.setInfoListener(_InfoListener);
            mQuPlayer.setSurface(_Surface);
            DataSpec spec = new DataSpec(urlString, DataSpec.MEDIA_TYPE_STREAM);
            mQuPlayer.setDataSource(spec);
            mQuPlayer.setLooping(false);
            mQuPlayer.prepare();
            mQuPlayer.start();

        } else {
            _Handler.removeCallbacks(_Restart);
            synchronized (mQuPlayer) {
                mQuPlayer.stop();
            }
        }
    }

    private Runnable _Restart = new Runnable() {
        @Override
        public void run() {
            synchronized (mQuPlayer) {
                mQuPlayer.stop();
                mQuPlayer.start();
            }
        }
    };

    private long _TestStartTime;

    private QuPlayer.OnInfoListener _InfoListener = new QuPlayer.OnInfoListener() {
        @Override
        public void onStart() {
            _TestStartTime = System.currentTimeMillis();
            report_error("starting ... ", true);
        }

        @Override
        public void onStop() {
            report_error("stoping ... ", true);
        }


        @Override
        public void onVideoStreamInfo(int width, int height) {
            String log = String.format("Video Stream info width %d height %d \n", width, height);
            report_error("stoping ... ", true);
        }

        @Override
        public void onAndroidBufferQueueCount(int count) {
        }

        @Override
        public void onProgress(long progress) {
            String log = String.format("progress %d\n", progress);
        }
    };

    private void report_error(String err, boolean bshow) {
        return;
    }

    private QuPlayer.OnErrorListener _ErrorListener = new QuPlayer.OnErrorListener() {
        @Override
        public void onError(int errCode) {
            if (errCode == 504) {//直播结束
                exitDialog();
                report_error("直播结束：" + errCode, true);
            } else {
                exitDialog();
//                report_error("错误码："+errCode, true);
                _Handler.postDelayed(_Restart, 3000);
            }

        }
    };

    public void exitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoLivePlayActivity_backup.this);
        builder.setMessage("来晚了，直播已经结束");
        builder.setTitle("提示");

        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                VideoLivePlayActivity_backup.this.finish();
            }
        });

        builder.create().show();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        _Surface = holder.getSurface();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.jpush.android.intent.REGISTRATION");
        intentFilter.addAction("cn.jpush.android.intent.UNREGISTRATION");
        intentFilter.addAction("cn.jpush.android.intent.MESSAGE_RECEIVED");
        intentFilter.addAction("cn.jpush.android.intent.NOTIFICATION_RECEIVED");
        intentFilter.addAction("cn.jpush.android.intent.NOTIFICATION_OPENED");
        intentFilter.addAction("cn.jpush.android.intent.ACTION_RICHPUSH_CALLBACK");
        intentFilter.addAction("cn.jpush.android.intent.CONNECTION");
        intentFilter.addCategory("com.quanjiakan.main");
        registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * TODO 更新设备用户的的位置信息--------需要过滤非当前设备的信息 device_id
             */
            try {
                String message = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
                JSONObject jsonObject = new JSONObject(message);
                String messageType;
                String lookNum1;
                if (jsonObject != null && jsonObject.has("type")) {
                    messageType = jsonObject.getString("type");
                } else {
                    messageType = null;
                }

                if (messageType != null & messageType.equals("11")) {
                    if (jsonObject != null && jsonObject.has("lookNum")) {
                        lookNum1 = jsonObject.getString("lookNum");
                        addRandomImage();
                    } else {
                        lookNum1 = number;
                    }
                    tv_watch_number.setText(lookNum1);
                } else if (messageType != null & messageType.equals("12")) {
                    isReceivedNet = true;
                    if (!isReceived) {
                        netErrorOrExitDialog();
                    }
                } else if (messageType != null & messageType.equals("13")) {
                    isReceived = true;
                    if (!isReceivedNet) {
                        exitActivityDialog();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private boolean isReceivedNet = false;
    private boolean isReceived = false;
    private Random random;

    public void addRandomImage() {
        if (random == null) {
            random = new Random();
        }
        int number = random.nextInt(31);
        mDatas.add(0, IMGLIST[number]);
        if (mDatas.size() > 25) {
            int deleteSize = mDatas.size();
            for (int i = deleteSize - 1; i > 24; i--) {
                mDatas.remove(i);
            }
        }
        audienceHeadAdapter.notifyDataSetChanged();
    }

    private Dialog dialogWait;

    public void netErrorOrExitDialog() {
        dialogWait = QuanjiakanDialog.getInstance().getCommonConfirmDialog(this, "提示", "出现网络波动!",
                "等待", true, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isReceivedNet = false;
                    }
                }, "退出", true, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isReceivedNet = false;
                        VideoLivePlayActivity_backup.this.finish();
                    }
                });
    }

    private Dialog dialogExit;

    public void exitActivityDialog() {
        dialogExit = QuanjiakanDialog.getInstance().getCommonConfirmDialog(this, "提示", "主播已退出直播!",
                "等待", false, "退出", true, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isReceived = false;
                        VideoLivePlayActivity_backup.this.finish();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
        if (mQuPlayer != null) {
            mQuPlayer.stop();
            mQuPlayer.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        exitGroup();
        super.onDestroy();
    }


    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
    }

    public boolean isShowInput() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if (params.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
            // 隐藏软键盘
//            getWindow().setSoftInputMode(
//                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//            params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
            return true;
        } else {
            return false;
        }
    }
}
