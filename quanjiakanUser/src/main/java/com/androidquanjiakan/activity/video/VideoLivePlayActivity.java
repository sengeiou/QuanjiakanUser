package com.androidquanjiakan.activity.video;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.setting.ebean.EBeanChargeActivity;
import com.androidquanjiakan.adapter.AudienceHeadAdapter;
import com.androidquanjiakan.adapter.GiftGridViewAdpter;
import com.androidquanjiakan.adapter.GiftViewPagerAdapter;
import com.androidquanjiakan.adapter.LivePlayMsgListAdapter_back;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.GiftBean;
import com.androidquanjiakan.entity.VideoLiveListEntity;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.NetworkUtils;
import com.androidquanjiakan.util.QosThread;
import com.androidquanjiakan.view.CircleTransformation;
import com.duanqu.qupai.mediaplayer.DataSpec;
import com.duanqu.qupai.mediaplayer.QuPlayer;
import com.duanqu.qupai.mediaplayer.QuPlayerExt;
import com.google.gson.JsonObject;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.player.KSYTextureView;
import com.ksyun.media.player.misc.KSYQosInfo;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.quanjiakanuser.util.KeyBoardUtils;
import com.quanjiakanuser.util.SpaceItemDecoration;
import com.quanjiakanuser.widget.MagicTextView;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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
import de.hdodenhof.circleimageview.CircleImageView;
import me.yifeiyuan.library.PeriscopeLayout;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class VideoLivePlayActivity extends Activity
        implements  View.OnClickListener, View.OnLayoutChangeListener {
//    private static final String TAG = "QuPlayer";
//    private QuPlayerExt mQuPlayer;
//    private Surface _Surface;
//    private SurfaceView _SurfaceView;
//    private Handler _Handler;
    private boolean isGag = false;
    private int freeGift = 1;

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

    private VideoLiveListEntity entity;
    private String urlString;
    private String headImage;
    private String groupId;
    private String number;
    private String name;
    private RecyclerView recyclerView;
    private List<Integer> mDatas;
    private AudienceHeadAdapter audienceHeadAdapter;
    private String liverId;

    private int width;
    private int height;
    private ImageView iv_imag;
    private GifImageView view;
    private TextView sendInput;
    private RelativeLayout rl_edou;
    private LinearLayout llinputparent;



    /************************************************** 金山播放器 ******************************************************/
    private static final String TAG = "TextureVideoActivity";

    public static final int UPDATE_SEEKBAR = 0;
    public static final int HIDDEN_SEEKBAR = 1;
    public static final int UPDATE_QOSMESS = 2;
    public static final int UPADTE_QOSVIEW = 3;

    private SharedPreferences settings;
    private String choosedecode;
    private String choosedebug;

    private QosThread mQosThread;

    KSYTextureView mVideoView = null;
    private Handler mHandler = new Handler();


    private boolean mPlayerPanelShow = false;
    private boolean mPause = false;
    private boolean mmute = false;

    private long mStartTime = 0;
    private long mPauseStartTime = 0;
    private long mPausedTime = 0;

    private int mVideoWidth = 0;
    private int mVideoHeight = 0;

    private int mVideoScaleIndex = 0;
    boolean useHwCodec = false;

    private Timer timer1 = null;
    private TimerTask timerTask = null;
    private long bits;
//    private KSYQosInfo info;
    private String cpuUsage;
    private int pss;
    private int rotatenum = 0;

    private String mDataSource;

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            Log.d("VideoPlayer", "OnPrepared");
            mVideoWidth = mVideoView.getVideoWidth();
            mVideoHeight = mVideoView.getVideoHeight();

            // Set Video Scaling Mode
            mVideoView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

            //start player
            mVideoView.start();

            //set progress
//            setVideoProgress(0);

            if (mQosThread != null && !mQosThread.isAlive())
                mQosThread.start();


            mStartTime = System.currentTimeMillis();

        }
    };



    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangeListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
            if (mVideoWidth > 0 && mVideoHeight > 0) {
                if (width != mVideoWidth || height != mVideoHeight) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();

                    if (mVideoView != null)
                        mVideoView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                }
            }
        }
    };

    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompletedListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            Log.e(TAG, "onSeekComplete...............");
        }
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            videoPlayEnd();
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            switch (what) {
                default:
                    Log.e(TAG, "OnErrorListener, Error:" + what + ",extra:" + extra);
            }

            videoPlayEnd();

            return false;
        }
    };

    public IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            switch (i) {
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d(TAG, "Buffering Start.");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.d(TAG, "Buffering End.");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                    break;
                case KSYMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    break;
                case KSYMediaPlayer.MEDIA_INFO_SUGGEST_RELOAD:
                    if(mVideoView != null)
                        mVideoView.reload(mDataSource, false, KSYMediaPlayer.KSYReloadMode.KSY_RELOAD_MODE_ACCURATE);
                    break;
                case KSYMediaPlayer.MEDIA_INFO_RELOADED:
                    Log.d(TAG, "Succeed to reload video.");
                    return false;
            }
            return false;
        }
    };

    private View.OnClickListener mVideoScaleButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int mode = mVideoScaleIndex % 2;
            mVideoScaleIndex++;
            mHandler.removeMessages(HIDDEN_SEEKBAR);
            android.os.Message msg = new android.os.Message();
            msg.what = HIDDEN_SEEKBAR;
            mHandler.sendMessageDelayed(msg, 3000);
            if (mVideoView != null) {
                if (mode == 1)
                    mVideoView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                else
                    mVideoView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            }
        }
    };
    private Context mContext1;
    private Timer timer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.layout_live_play_chat);
        mContext = this;

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        iv_imag = (ImageView) findViewById(R.id.iv_imag);
        view = (GifImageView) findViewById(R.id.gif_test);

        entity = (VideoLiveListEntity) getIntent().getSerializableExtra(BaseConstants.PARAM_ENTITY);
        urlString = getIntent().getStringExtra(BaseConstants.PARAM_URL);
        groupId = getIntent().getStringExtra(BaseConstants.PARAM_GROUP);
        number = getIntent().getStringExtra(BaseConstants.PARAM_NUMBER);
        name = getIntent().getStringExtra(BaseConstants.PARAM_NAME);
        headImage = getIntent().getStringExtra(BaseConstants.PARAM_HEADIMG);
        liverId = getIntent().getStringExtra(BaseConstants.PARAM_LIVERID);


        if (entity == null || urlString == null || groupId == null
                || number == null
                || name == null) {
            BaseApplication.getInstances().toast(VideoLivePlayActivity.this,"传入参数异常!");
            exitGroup();
            finish();
            return;
        }

        LogUtil.e("***   是否关注了：" + (entity.getIsFollow() > 0));

//        mQuPlayer = new QuPlayerExt();
//        _SurfaceView = (SurfaceView) findViewById(R.id.surface);
//        _SurfaceView.getHolder().addCallback(this);
//        _Handler = new Handler(Looper.myLooper());
        initSurfaceView();
        initInputView();

        initChatView();

        initJPush();


        //判断是否禁言了
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if (val != null) {
                    HttpResponseResult result = new HttpResponseResult(val);
                    if (result.getCode().equals("200")) {
                        isGag = true;
                    } else if (result.getCode().equals("250")) {
                        isGag = false;
                    }

                }

            }
        }, HttpUrls.isGag() + "&memberId=" + QuanjiakanSetting.getInstance().getUserId() +
                "&groupId=" + groupId, null, Task.TYPE_GET_STRING_NOCACHE, null));

//        _Handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mQuPlayer != null) {
//                    //进入时会检查拉流地址，当不可用时，会回调该Error接口
//                    mQuPlayer.setErrorListener(_ErrorListener);
//                    mQuPlayer.setInfoListener(_InfoListener);
//                    mQuPlayer.setSurface(_Surface);
//                    LogUtil.e("直播地址:" + urlString);
//                    DataSpec spec = new DataSpec(urlString, DataSpec.MEDIA_TYPE_STREAM);
//                    mQuPlayer.setDataSource(spec);
//                    mQuPlayer.setLooping(false);
//                    mQuPlayer.prepare();
//                    mQuPlayer.start();
//                } else {
//                    mQuPlayer = new QuPlayerExt();
//                    //进入时会检查拉流地址，当不可用时，会回调该Error接口
//                    mQuPlayer.setErrorListener(_ErrorListener);
//                    mQuPlayer.setInfoListener(_InfoListener);
//                    mQuPlayer.setSurface(_Surface);
//                    LogUtil.e("直播地址:" + urlString);
//                    DataSpec spec = new DataSpec(urlString, DataSpec.MEDIA_TYPE_STREAM);
//                    mQuPlayer.setDataSource(spec);
//                    mQuPlayer.setLooping(false);
//                    mQuPlayer.prepare();
//                    mQuPlayer.start();
//                }
//            }
//        }, 300);
    }

    /**
     * 金山播放器相关
     */
    private void initSurfaceView() {

        mVideoView = (KSYTextureView) findViewById(R.id.texture_view);
        mVideoView.setKeepScreenOn(true);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        liveVod(urlString);



    }

    private void liveVod(String url){
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        android.os.Message message = new android.os.Message();
                        message.what = UPADTE_QOSVIEW;
                        if (mHandler != null && message != null) {
                            mHandler.sendMessage(message);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
            };
        }

        if (timer1 == null) {
            timer1 = new Timer(true);
        }

        timer1.schedule(timerTask, 2000, 5000);

        mQosThread = new QosThread(mContext, mHandler);

        mDataSource = url;

//        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnPreparedListener(mOnPreparedListener);
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangeListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setOnSeekCompleteListener(mOnSeekCompletedListener);
        mVideoView.setScreenOnWhilePlaying(true);
        mVideoView.setBufferTimeMax(2.0f);
        mVideoView.setTimeout(5, 30);
        mVideoView.setRotateDegree(90);

        settings = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        choosedecode = settings.getString("choose_decode", "undefind");
        mVideoView.setDecodeMode(KSYMediaPlayer.KSYDecodeMode.KSY_DECODE_MODE_AUTO);

        try {
            mVideoView.setDataSource(mDataSource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mVideoView.prepareAsync();
    }



    public void initJPush() {
        repeatCount = 0;
        LinkedHashSet<String> hashSet = new LinkedHashSet<String>();
        hashSet.add("" + groupId);
        JPushInterface.setTags(this, hashSet, new TagAliasCallback() {
            @Override
            public void gotResult(int result, String message, Set<String> set) {
                LogUtil.e("setTags result:" + result + "  message:" + message);
            }
        });

        JMessageClient.setNoDisturbGlobal(1, new BasicCallback() {
            @Override
            public void gotResult(int result, String message) {
                LogUtil.e("setNoDisturbGlobal result:" + result + "  message:" + message);
            }
        });


        /*
        TODO For Test
         */

        /**
         * 重置用户生日，暂由等级代替
         *
         * 1000 000 000 000
         *
         *  999 964 800 000
         */
//        resetLevelInfo(birth*5);
    }

//    private Handler mHandler1 = new Handler();

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

//        mContext = this;
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
                LogUtil.e("groupInfo.getOwnerAppkey():" + groupInfo.getOwnerAppkey());
//                if(groupInfo!=null){
//                    tv_watch_number.setText(groupInfo.getGroupMembers().size()+"");
//                }else{
//                    tv_watch_number.setText(number);
//                }

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
//        Log.i(TAG, event.getMessage().toString());
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

                    if (groupId == Long.parseLong(VideoLivePlayActivity.this.groupId) &&
                            msg.getContentType() == ContentType.text
                            ) {
                        Message lastMsg = mChatAdapter.getLastMsg();
                        if (lastMsg == null || msg.getId() != lastMsg.getId()) {

                            if (msg.getFromUser().getUserName().equals("admin001")) {//礼物
                                Map<String, String> map = msg.getContent().getStringExtras();
                                /*
                                {
  present_icon=http: //picture.quanjiakan.com: 9080/quanjiakan/resources/activity/20170116150048_rg45iy.png,
  present_name=包治百病,
  present_id=35,
  user_icon=http: //picture.quanjiakan.com: 9080/quanjiakan/resources/doctor/20170110095209_invzijlm0sir8ggr931c.png,
  user_name=小小莫,
  group_id=18423389,
  giver_id=11303
}
                                 */
                                LogUtil.e("map--------" + map.toString());
                                String giverId = map.get("giver_id");//送礼人id
                                String docName = map.get("doc_name");//名字   优先使用
                                String userName = map.get("user_name");//用户端名字
                                String userIcon = map.get("user_icon");//发送人头像
                                String presentIcon = map.get("present_icon");
                                String presentId = map.get("present_id");
                                String presendName = map.get("present_name");
                                String ebeans = map.get("rEbeans");
                                String version = map.get("version");
                                tv_edounumber.setText(""+ebeans);
                                int pre_id = Integer.parseInt(presentId);
                                tag = presentIcon + giverId;
                                String name = null;
                                if(version!=null && "0".equals(version)){
                                    if (docName != null) {
                                        name = docName;
                                    } else if (userName != null){
                                        name = userName;
                                    } else {
                                        name = "游客";
                                    }
                                }else if(version!=null && "1".equals(version)){
                                    name = userName;
                                }else if(version!=null && "2".equals(version)){
                                    name = docName;
                                }else{
                                    if (docName != null) {
                                        name = docName;
                                    } else {
                                        name = userName;
                                    }
                                }
                                if(name==null){
                                    name = "游客";
                                }
                                if (!name.equals(nickName)) {
                                    showGift2(name, userIcon, tag, pre_id, giverId);
                                }
                                /**
                                 * 收到的地方不需要发送该消息
                                 * 而是在发送的地方调用
                                 */
//                                if(BaseApplication.getInstances().getUser_id().equals(giverId)){
//                                    createGiftMessage(userName,presendName);//
//                                }
                                createGiftMessage(name,presendName);//
                                return;
                            }
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
            LogUtil.i("long click position" + position);
        }
    };

    /**
     * TODO 发送关注消息
     *
     * @param who
     */
    private int retryConcernCount = 0;
    public void createConcernMessage(final String who) {
        if (retryConcernCount > 5) {
            return;
        }
        retryConcernCount++;
        /**
         * 可以实现显示该消息
         */
        if (JMessageClient.getMyInfo() != null) {
            HashMap<String, String> map = new HashMap<>();
            Message msg = JMessageClient.createGroupTextMessage(Long.parseLong(groupId), "关注了主播");

            mChatAdapter.addMsgToList(msg);
            JMessageClient.sendMessage(msg);
            mChatAdapter.notifyDataSetChanged();
            mChatView.setToBottom();
        } else {
            JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        HashMap<String, String> map = new HashMap<>();
                        Message msg = JMessageClient.createGroupTextMessage(Long.parseLong(groupId), "关注了主播");

                        mChatAdapter.addMsgToList(msg);
                        JMessageClient.sendMessage(msg);
                        mChatAdapter.notifyDataSetChanged();
                        mChatView.setToBottom();
                    } else {
                        createConcernMessage(who);
                    }
                }
            });
        }
    }

    private int retryGagCount = 0;
    public void createGagMessage(final String who) {
        if (retryGagCount > 5) {
            return;
        }
        retryGagCount++;
        /**
         * 可以实现显示该消息
         */
        if (JMessageClient.getMyInfo() != null) {
            HashMap<String, String> map = new HashMap<>();
            Message msg = JMessageClient.createGroupTextMessage(Long.parseLong(groupId), "已被主播禁言");

            mChatAdapter.addMsgToList(msg);
            JMessageClient.sendMessage(msg);
            mChatAdapter.notifyDataSetChanged();
            mChatView.setToBottom();
        } else {
            JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        HashMap<String, String> map = new HashMap<>();
                        Message msg = JMessageClient.createGroupTextMessage(Long.parseLong(groupId), "已被主播禁言");

                        mChatAdapter.addMsgToList(msg);
                        JMessageClient.sendMessage(msg);
                        mChatAdapter.notifyDataSetChanged();
                        mChatView.setToBottom();
                    } else {
                        createGagMessage(who);
                    }
                }
            });
        }
    }


    private int retryConcernCountGift = 0;
    public void createGiftMessage(final String name,final String giftName) {
        Message msg = JMessageClient.createGroupTextMessage(Long.parseLong(groupId), name+contentLevel_gift+"送给主播"+contentLevel_gift+giftName);
        //TODO 这个消息只给自己看，不需要发送出去
        mChatAdapter.addMsgToList(msg);
        mChatAdapter.notifyDataSetChanged();
        mChatView.setToBottom();
    }
    /**
     */
    private String currentLevel = contentLevel_0;
    private static final String contentLevel_0 = "[//**--++^^0^^++--**//]";
    private static final String contentLevel_1 = "[//**--++^^1^^++--**//]";
    private static final String contentLevel_2 = "[//**--++^^2^^++--**//]";
    private static final String contentLevel_3 = "[//**--++^^3^^++--**//]";
    private static final String contentLevel_4 = "[//**--++^^4^^++--**//]";
    private static final String contentLevel_5 = "[//**--++^^5^^++--**//]";

    /**
     * 用于标识是礼物的消息
     */
    private static final String contentLevel_gift = "[//**--++^^gift^^++--**//]";
    private static final String contentLevel_concern = "[//**--++^^concern^^++--**//]";
    private static final String contentLevel_gag = "[//**--++^^gag^^++--**//]";


    private int repeatCount = 0;
    private UserInfo userInfo = JMessageClient.getMyInfo();
    private final long birth = 1000000000l;

    private final long divNumber = 35200000l;

    public void resetLevelInfo(final long birthNumber) {
        /**
         * TODO 设置用户，并更新用户信息   可用，但实际测试发现不太稳定
         */
        LogUtil.e("待设置的值:" + birthNumber);
        if (repeatCount > 5) {
            return;
        }
        repeatCount++;

        userInfo = JMessageClient.getMyInfo();
        if (userInfo != null) {
            userInfo.setSignature(birthNumber + "");
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
        } else {
            JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        userInfo = JMessageClient.getMyInfo();
                        userInfo.setSignature(birthNumber + "");
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
                    } else {
                        resetLevelInfo(birthNumber);
                    }
                }
            });
        }
    }

    private int loginRepeatCount = 0;

    public void relogin() {
        if (loginRepeatCount > 10) {
            return;
        }
        loginRepeatCount++;
        JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                } else {
                    relogin();
                }
            }
        });
    }

    public void doConcern() {
        //TODO 需要进行网络访问，变更数据库中的关注状态
        concern.setEnabled(false);
        if (entity.getIsFollow() > 0) {
            if (!NetworkUtils.isNetworkAvalible(VideoLivePlayActivity.this)) {
                QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "网络连接不可用！");
                return;
            }
            MyHandler.putTask(VideoLivePlayActivity.this, new Task(new HttpResponseInterface() {
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
            if (!NetworkUtils.isNetworkAvalible(VideoLivePlayActivity.this)) {
                QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "网络连接不可用！");
                return;
            }
            MyHandler.putTask(VideoLivePlayActivity.this, new Task(new HttpResponseInterface() {
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

    private Timer timerHeart;
    private TimerTask task;

    private void startRandomHeart() {
        timerHeart = new Timer();
        final Random random = new Random();
        task = new TimerTask() {
            @Override
            public void run() {
                final int size = random.nextInt(4);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO 这里向冒心的View中添加指定数量的心
                        for (int i = size; i > 0; i--) {
                            periscopeLayout.addHeart();
                        }
                    }
                });
            }
        };
        timerHeart.schedule(task, 1000, 1000);
    }

    private void endRandomHeart() {
        if (task != null) {
            task.cancel();
        }
        if (timerHeart != null) {
            timerHeart.cancel();
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
//                    iv_close.setVisibility(View.INVISIBLE);
                    KeyBoardUtils.closeKeybord(et_comment, this);
//                    et_comment.setVisibility(View.GONE);
                    //close.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
//                    iv_send_mes.setVisibility(View.GONE);
                    iv_mes.setVisibility(View.INVISIBLE);
                    iv_gift.setVisibility(View.INVISIBLE);
                    iv_love.setVisibility(View.INVISIBLE);
                    KeyBoardUtils.closeKeybord(et_comment, this);

                } else {
                    iv_sockt.setImageResource(R.drawable.icon_live_barrage_nor);
                    isShow = true;
                    mChatView.setVisibility(View.VISIBLE);
                    ll_me.setVisibility(View.VISIBLE);
//                    et_comment.setVisibility(View.VISIBLE);
//                    iv_close.setVisibility(View.VISIBLE);

                    //close.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    //iv_send_mes.setVisibility(View.VISIBLE);
                    iv_mes.setVisibility(View.VISIBLE);
                    iv_gift.setVisibility(View.VISIBLE);
                    iv_love.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.iv_camera:
                break;
//            case R.id.iv_close:
//                if (et_comment.length() < 1) {
//                    finish();
//                } else {
//                    //发送消息
//                    iv_close.setImageResource(R.drawable.icon_live_close);
//                    HidenKeyboard(view);
//                    /**
//                     * TODO 需要添加发送消息的逻辑
//                     */
//                    retryCount = 0;
//                    doSendMessage();
//                }
//                break;
            case R.id.iv_closeliving:
                finish();
                break;

            case R.id.iv_love://TODO 冒心
                periscopeLayout.addHeart();
                break;
            case R.id.sendInput://TODO 输入发送按钮

                if (et_comment.getText().toString().length() < 1) {
                    KeyBoardUtils.closeKeybord(et_comment, this);
                    //et_comment.setVisibility(View.GONE);
                    //iv_send_mes.setVisibility(View.GONE);
                    rl_edou.setVisibility(View.VISIBLE);
                    ll_me.setVisibility(View.VISIBLE);
                    iv_gift.setVisibility(View.VISIBLE);
                    iv_love.setVisibility(View.VISIBLE);
                    iv_mes.setVisibility(View.VISIBLE);
                    iv_sockt.setVisibility(View.VISIBLE);

                    llinputparent.setVisibility(View.GONE);
//                rl_send_gift.setVisibility(View.VISIBLE);
//                   rl_show_gift.setVisibility(View.VISIBLE);
                    fl_freegift.setVisibility(View.VISIBLE);
                } else {
                    KeyBoardUtils.closeKeybord(et_comment, VideoLivePlayActivity.this);

                    rl_edou.setVisibility(View.VISIBLE);
                    ll_me.setVisibility(View.VISIBLE);
                    iv_gift.setVisibility(View.VISIBLE);
                    iv_love.setVisibility(View.VISIBLE);
                    iv_mes.setVisibility(View.VISIBLE);
                    iv_sockt.setVisibility(View.VISIBLE);

                    llinputparent.setVisibility(View.GONE);
                    fl_freegift.setVisibility(View.VISIBLE);

                    switch (grade) {
                        case 0:
                            currentLevel = contentLevel_0;
                            break;
                        case 1:
                            currentLevel = contentLevel_1;
                            break;

                        case 2:
                            currentLevel = contentLevel_2;
                            break;

                        case 3:
                            currentLevel = contentLevel_3;
                            break;

                        case 4:
                            currentLevel = contentLevel_4;
                            break;

                        case 5:
                            currentLevel = contentLevel_5;
                            break;
                    }

                    String msgContent = et_comment.getText().toString();
                    //TODO 携带等级信息  现在放在 后面
                    TextContent content = new TextContent(msgContent.trim() + currentLevel);
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

                }

                break;
            case R.id.iv_mes://TODO 消息按钮
                if (isGag) {
                    KeyBoardUtils.closeKeybord(et_comment, this);
                    Toast.makeText(VideoLivePlayActivity.this, "你已经被主播禁言啦", Toast.LENGTH_SHORT).show();
                } else {
                    KeyBoardUtils.openKeybord(et_comment, this);
                    iv_gift.setVisibility(View.GONE);
                    iv_love.setVisibility(View.GONE);
                    iv_mes.setVisibility(View.GONE);
                    iv_sockt.setVisibility(View.GONE);
                    rl_edou.setVisibility(View.GONE);
//                rl_send_gift.setVisibility(View.GONE);
//                rl_show_gift.setVisibility(View.GONE);
                    ll_me.setVisibility(View.GONE);
                    fl_freegift.setVisibility(View.GONE);

                    llinputparent.setVisibility(View.VISIBLE);


                }


                break;
            case R.id.iv_gift: //TODO 礼物
                showGiftDialog(VideoLivePlayActivity.this);
                break;


        }
    }

    //礼物对话框内部控件
    private TextView add;
    private ImageView send;

    private ViewPager viewPager;
    private LinearLayout group;//圆点指示器
    private ImageView[] ivPoints;//小圆点图片的集合
    private int totalPage; //总的页数
    private int mPageSize = 10; //每页显示的最大的数量
    private List<GiftBean> listDatas = new ArrayList<GiftBean>();//数据
    private List<GiftBean> freeDatas = new ArrayList<>();//免费礼物数据
    private List<View> viewPagerList;//GridView作为一个View对象添加到ViewPager集合中
    private boolean isLian = false;
    private Dialog dialog;

    private TextView tv_price;
    private int price;
    private int ebeans;
    private int grade;
    private int giftId;
    private String icon = null;

    private TextView time_count;
    private TextView add_money;
    //礼物名字
//    private String[] proName = {"药丸", "掌声", "棒棒糖", "点赞", "医箱", "向日葵",
//            "手套", "人参果", "天使翅膀", "听诊", "妙手回春", "健康罩", "火眼金睛", "生命之水",
//            "动力摩托", "保时捷"};
    //礼物图标
//    private int[] giftList = {R.drawable.pill_icon, R.drawable.applause_icon, R.drawable.lollipop_icon, R.drawable.like_icon, R.drawable.medicine_icon, R.drawable.glove_icon,
//            R.drawable.glove_icon, R.drawable.ginseng_icon, R.drawable.wing_icon, R.drawable.stethoscope_icon, R.drawable.miaoshaohuichun_icon, R.drawable.mask_icon, R.drawable.eye_icon, R.drawable.life_icon,
//            R.drawable.motorola_icon, R.drawable.car_icon};
//    private boolean[] lian = {true, false, true, true, false, true, false, true, true, false, true, false, true, true, false, false};


    private void showGiftDialog(final Context context) {

        dialog = new Dialog(context, R.style.Theme_black_Dialog);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.gift_dialog, null);

        Window window = dialog.getWindow();

        window.setGravity(Gravity.BOTTOM);

        window.setWindowAnimations(R.style.dialogStyle);
        window.getDecorView().setPadding(0, 0, 0, 0);

        android.view.WindowManager.LayoutParams lp = window.getAttributes();

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(lp);

        dialog.setContentView(dialogView);
        viewPager = (ViewPager) dialogView.findViewById(R.id.viewpager);
        group = (LinearLayout) dialogView.findViewById(R.id.points);
        tv_price = (TextView) dialogView.findViewById(R.id.tv_price);

        tv_price.setText(ebeans + "");

        dialogView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = dialog.findViewById(R.id.rlt_gift).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dialog.dismiss();
                    }
                }
                return true;
            }
        });
//        listDatas = new ArrayList<ProdctBean>();
//        for (int i = 0; i < proName.length; i++) {
//            listDatas.add(new ProdctBean(proName[i], giftList[i], lian[i]));
//        }

        totalPage = (int) Math.ceil(listDatas.size() * 1.0 / mPageSize);
        viewPagerList = new ArrayList<View>();
        for (int i = 0; i < totalPage; i++) {
            //每个页面都是inflate出一个新实例
            final GridView gridView = (GridView) View.inflate(this, R.layout.item_gridview, null);
            gridView.setAdapter(new GiftGridViewAdpter(this, listDatas, i, mPageSize));
            //添加item点击监听
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    Object obj = gridView.getItemAtPosition(position);
                    if (obj != null && obj instanceof GiftBean) {
//                        isLian = ((ProdctBean) obj).isLian();
//                        tag = ((ProdctBean) obj).getUrl();

                        if (((GiftBean) obj).getPrice() > 0 && ((GiftBean) obj).getPrice() <= 10) {
                            isLian = true;
                        } else {
                            isLian = false;
                        }
                        tag = ((GiftBean) obj).getIcon() + QuanjiakanSetting.getInstance().getUserId();
                        giftId = ((GiftBean) obj).getId();
                        price = ((GiftBean) obj).getPrice();
                    }
                    for (int k = 0; k < arg0.getCount(); k++) {
                        View v = arg0.getChildAt(k);
                        if (position == k) {
                            v.setBackgroundResource(R.drawable.gift_select_bac);
                        } else {
                            v.setBackgroundResource(0);
                        }
                    }
                }
            });
            gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            //每一个GridView作为一个View对象添加到ViewPager集合中
            viewPagerList.add(gridView);
        }
        //设置ViewPager适配器
        viewPager.setAdapter(new GiftViewPagerAdapter(viewPagerList));

        //添加小圆点
        ivPoints = new ImageView[totalPage];
        for (int i = 0; i < totalPage; i++) {
            //循坏加入点点图片组
            ivPoints[i] = new ImageView(this);
            if (i == 0) {
                ivPoints[i].setImageResource(R.drawable.page_focuese);
            } else {
                ivPoints[i].setImageResource(R.drawable.page_unfocused);
            }
            ivPoints[i].setPadding(8, 8, 8, 8);
            group.addView(ivPoints[i]);
        }
        //设置ViewPager的滑动监听
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < totalPage; i++) {
                    if (i == position) {
                        ivPoints[i].setImageResource(R.drawable.page_focuese);
                    } else {
                        ivPoints[i].setImageResource(R.drawable.page_unfocused);
                    }
                }
            }
        });


        add = (TextView) dialogView.findViewById(R.id.add_money);

        send = (ImageView) dialogView.findViewById(R.id.send);
        time_count = (TextView) dialogView.findViewById(R.id.time_count);
        add_money = (TextView) dialogView.findViewById(R.id.add_money);

        add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(VideoLivePlayActivity.this, EBeanChargeActivity.class);
                intent.putExtra(BaseConstants.PARAM_ENTITY, entity);
                intent.putExtra(BaseConstants.PARAMS_ENTRY, BaseConstants.ENTRY_VIDEO_LIVE);
                intent.putExtra(BaseConstants.PARAM_NAME, name);
                intent.putExtra(BaseConstants.PARAM_HEADIMG, headImage);
                startActivity(intent);
//                finish();
            }
        });


        time_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkUtils.isNetworkAvalible(VideoLivePlayActivity.this)) {
                    QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "网络连接不可用！");
                    return;
                }
                if (isLian) {

                    if (price < ebeans) {
                        showGift(nickName, "", url, tag, giftId + "");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                timer.cancel();
                                time_count.setVisibility(View.VISIBLE);
                                timer.start();
                            }
                        }, 1000);
                    } else {
                        QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "E豆不足，请进行充值");
                    }

                }

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkUtils.isNetworkAvalible(VideoLivePlayActivity.this)) {
                    QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "网络连接不可用！");
                    return;
                }
                //发送礼物
                if (price < ebeans) {
                    showGift(nickName, "", url, tag, giftId + "");
                    if (isLian) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                time_count.setVisibility(View.VISIBLE);
                                send.setVisibility(View.GONE);
                                timer.start();
                            }
                        }, 1000);

                    }else {
                        dialog.dismiss();
                    }

                } else {
                    QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "E豆不足，请进行充值");
                }


            }
        });

        dialog.show();


    }


    private int retryCount = 0;

    public void doSendMessage() {
        if (retryCount > 3) {
            BaseApplication.getInstances().toast(VideoLivePlayActivity.this,"弹幕发送失败!");
            return;
        }
        retryCount++;
        if (JMessageClient.getMyInfo() != null) {
            String msgContent = et_comment.getText().toString();
            TextContent content = new TextContent(msgContent);
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
                        TextContent content = new TextContent(msgContent);
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

    private ImageView iv_mes;
    private ImageView iv_gift;
    private ImageView iv_love;
    private ImageView iv_send_mes;
    private ImageView iv_closeliving;


    /**
     * 十分钟计时和心形控件
     */

    private FrameLayout fl_freegift;
    private ImageView iv_animation;
    private PeriscopeLayout periscopeLayout;
    private TextView tv_min;
    private TextView tv_sec;
    private long mMin = 00;
    private long mSecond = 00;
    private LinearLayout freegift_time;
    private ImageView free_gift_background;
    private ImageView iv_freegift;

    private TextView tv_edounumber;


    public void initInputView() {
        tv_livinger_name = (TextView) findViewById(R.id.tv_livinger_name);
        tv_watch_number = (TextView) findViewById(R.id.tv_watch_number);
        tv_livinger_name.setText(name);
        tv_watch_number.setText(number);
        ll_me = (LinearLayout) findViewById(R.id.ll_me);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        sendInput = (TextView) findViewById(R.id.sendInput);
        sendInput.setOnClickListener(this);
        concern = (ImageView) findViewById(R.id.concern);
        rl_edou = (RelativeLayout) findViewById(R.id.rl_edou);
        tv_edounumber = (TextView) findViewById(R.id.tv_edounumber);

        llinputparent = (LinearLayout) findViewById(R.id.llinputparent);
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

//        iv_close = (ImageView) findViewById(R.id.iv_close);
//        iv_close.setOnClickListener(this);

        et_comment = (EditText) findViewById(R.id.et_comment);

       /* iv_send_mes = (ImageView) findViewById(R.id.iv_send_mes);
        iv_send_mes.setOnClickListener(this);*/

        iv_mes = (ImageView) findViewById(R.id.iv_mes);
        iv_love = (ImageView) findViewById(R.id.iv_love);
        iv_love.setOnClickListener(this);
        iv_mes.setOnClickListener(this);
        iv_closeliving = (ImageView) findViewById(R.id.iv_closeliving);
        iv_closeliving.setOnClickListener(this);
//        et_comment.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence == null || charSequence.length() == 0) {
//                    iv_close.setImageResource(R.drawable.icon_live_close);
//                } else {
//                    iv_close.setImageResource(R.drawable.gou);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
        recyclerView = (RecyclerView) findViewById(R.id.id_recyclerview_horizontal);
        getRecycleData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VideoLivePlayActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//设置横向
        recyclerView.setLayoutManager(linearLayoutManager);
        audienceHeadAdapter = new AudienceHeadAdapter(this, mDatas);
        recyclerView.addItemDecoration(new SpaceItemDecoration(5));
        recyclerView.setAdapter(audienceHeadAdapter);


        /********************   10分钟计时和心形气泡   *******************/


        iv_animation = (ImageView) findViewById(R.id.iv_animation);

//        rl_send_gift = (RelativeLayout) findViewById(R.id.rl_send_gift);
//        rl_show_gift = (RelativeLayout) findViewById(R.id.rl_show_gift);

        fl_freegift = (FrameLayout) findViewById(R.id.fl_freegift);
        free_gift_background = (ImageView) findViewById(R.id.free_gift_background);
        iv_freegift = (ImageView) findViewById(R.id.iv_freegift);


        freegift_time = (LinearLayout) findViewById(R.id.freegift_time);
        tv_min = (TextView) findViewById(R.id.tv_min);
        tv_sec = (TextView) findViewById(R.id.tv_sec);


        tv_min.setText("10");
        tv_sec.setText("00");

        mMin = Long.parseLong(tv_min.getText().toString());
        mSecond = Long.parseLong(tv_sec.getText().toString());


//礼物控件
        iv_gift = (ImageView) findViewById(R.id.iv_gift);
        periscopeLayout = (PeriscopeLayout) findViewById(R.id.periscope);
        periscopeLayout.setOnClickListener(this);
        iv_gift.setOnClickListener(this);
        llgiftcontent = (LinearLayout) findViewById(R.id.llgiftcontent);

        inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(VideoLivePlayActivity.this, R.anim.gift_in);
        outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(VideoLivePlayActivity.this, R.anim.gift_out);
        giftNumAnim = new NumAnim();


        //开始倒计时
        startCountDown();

        clearTiming();
        loadNameAndImage();//获取昵称
        loadGiftData();
        getMyInfo();
        getHostInfo();//获取主播信息
        startRandomHeart();


        //TODO  **********************
        getLevelInfo();
    }


    private void loadGiftData() {
        if (!NetworkUtils.isNetworkAvalible(VideoLivePlayActivity.this)) {
            QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "网络连接不可用！");
            return;
        }

        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                /*LogUtil.e("val----------" + HttpUrls.getGiftList());
                if (val != null && val.length() > 0) {
                    ProdctBean prodctBean = JSON.parseObject(val, ProdctBean.class);
                    if (prodctBean != null && "200".equals(prodctBean.getCode())) {
                        List<ProdctBean.RowsBean> rows = prodctBean.getRows();
                        if (listDatas != null && freeDatas != null) {
                            listDatas.clear();
                        }
                        for (int j = 0; j < rows.size(); j++) {
                            if (rows.get(j).getPrice() > 0) {
                                listDatas.add(rows.get(j));
                            } else {
                                freeDatas.add(rows.get(j));
                                Picasso.with(VideoLivePlayActivity.this).load(freeDatas.get(0).getIcon()).into(iv_freegift);
                            }
                        }
                    }

                }*/
                if(val!=null&&val.length()>0) {
                    try {
                        JSONObject jsonObject = new JSONObject(val);
                        if("200".equals(jsonObject.getString("code"))) {
                            JSONArray rows = jsonObject.getJSONArray("rows");
                            if (listDatas != null && freeDatas != null) {
                                listDatas.clear();
                            }
                            for (int j=0;j<rows.length();j++){
                                GiftBean giftBean = new GiftBean();
                                giftBean.setPrice(rows.getJSONObject(j).getInt("price"));
                                giftBean.setEffect(rows.getJSONObject(j).getInt("effect"));
                                giftBean.setExperience(rows.getJSONObject(j).getInt("experience"));
                                giftBean.setIcon(rows.getJSONObject(j).getString("icon"));
                                giftBean.setId(rows.getJSONObject(j).getInt("id"));
                                giftBean.setName(rows.getJSONObject(j).getString("name"));
                                giftBean.setStage(rows.getJSONObject(j).getInt("stage"));
                                giftBean.setState(rows.getJSONObject(j).getInt("state"));
                                giftBean.setType(rows.getJSONObject(j).getInt("type"));

                                if(rows.getJSONObject(j).getInt("price")>0) {
                                    listDatas.add(giftBean);
                                }else {
                                    freeDatas.add(giftBean);
                                    Picasso.with(VideoLivePlayActivity.this).load(freeDatas.get(0).getIcon()).into(iv_freegift);
                                }

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
        }, HttpUrls.getGiftList(), null, Task.TYPE_GET_STRING_NOCACHE, null));
    }


    /******************
     * 礼物动画start
     ***********************/

    private String tag;
    private LinearLayout llgiftcontent;
    private NumAnim giftNumAnim;
    private TranslateAnimation inAnim;
    private TranslateAnimation outAnim;


    private void showGift(final String docName, final String userName, final String userIcon, final String presentIcon, final String presentId) {

        if (!NetworkUtils.isNetworkAvalible(VideoLivePlayActivity.this)) {
            QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "网络连接不可用！");
            return;
        }
        //发送礼物
        final String params = "&giverId=" + QuanjiakanSetting.getInstance().getUserId() + "&recipientId=" + liverId + "&presentId=" + presentId +
                "&version=1";

        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("val-------" + val);
                if (val != null && val.length() > 0) {
                    try {
                        JSONObject object = new JSONObject(val);
                        if (object.has("code") && "200".equals(object.getString("code")) && object.has("object")) {
                            String ebean = object.getString("object");
                            if(tv_price!=null) {
                                tv_price.setText(ebean + "");
                            }
                            switch (Integer.parseInt(presentId)) {
                                case 33://一束玫瑰
                                    iv_imag.setVisibility(View.VISIBLE);
                                    Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(iv_imag);
                                    planAnimate();
                                    break;

                                case 34://悬壶济世
                                    iv_imag.setVisibility(View.VISIBLE);
                                    Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(iv_imag);
                                    planAnimate();
                                    break;
                                case 35://包治百病
                                    iv_imag.setVisibility(View.VISIBLE);
                                    Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(iv_imag);
                                    planAnimate();
                                    break;
                                case 36://天使的翅膀
                                    iv_imag.setVisibility(View.VISIBLE);
                                    Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(iv_imag);
                                    planAnimate();
                                    break;
                                case 37://一生一世
                                    iv_imag.setVisibility(View.VISIBLE);
                                    Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(iv_imag);
                                    planAnimate();
                                    break;
                                case 38://??
                                    iv_imag.setVisibility(View.VISIBLE);
                                    Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(iv_imag);
                                    planAnimate();
                                    break;
                                case 39://轮船
                                    view.setVisibility(View.VISIBLE);
                                    try {
                                        GifDrawable gifDrawable = new GifDrawable(VideoLivePlayActivity.this.getResources(), R.drawable.lunchuan);
                                        view.setImageDrawable(gifDrawable);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    TranslateAnimation animation = new TranslateAnimation(-width, width, -150f, -150f);
                                    animation.setDuration(5000);
                                    animation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            view.setVisibility(View.GONE);

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                    view.setAnimation(animation);


                                    break;
                                case 40://爱的诺言
                                    view.setVisibility(View.VISIBLE);
                                    try {
                                        GifDrawable gifDrawable = new GifDrawable(VideoLivePlayActivity.this.getResources(), R.drawable.diamend);
                                        view.setImageDrawable(gifDrawable);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    TranslateAnimation animation1 = new TranslateAnimation(-width / 2, width, -150f, -150f);
                                    animation1.setDuration(5000);
                                    animation1.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            view.setVisibility(View.GONE);

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                    view.setAnimation(animation1);
                                    break;
                                case 41://专机护送
                                    iv_imag.setVisibility(View.VISIBLE);
                                    Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(iv_imag);
                                    planAnimate();
                                    break;
                                case 42://法拉利
                                    iv_imag.setVisibility(View.VISIBLE);
                                    Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(iv_imag);
                                    planAnimate();
                                    break;
                                case 43://一飞冲天
                                    view.setVisibility(View.VISIBLE);
                                    try {
                                        GifDrawable gifDrawable = new GifDrawable(VideoLivePlayActivity.this.getResources(), R.drawable.feiji);
                                        view.setImageDrawable(gifDrawable);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    TranslateAnimation animation2 = new TranslateAnimation(width, -width, -150f, -150f);
                                    animation2.setDuration(5000);
                                    animation2.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            view.setVisibility(View.GONE);

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                    view.setAnimation(animation2);
                                    break;
                                case 50://心的声音
                                    iv_imag.setVisibility(View.VISIBLE);
                                    Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(iv_imag);
                                    planAnimate();
                                    break;
                                case 51://兰博基尼
                                    iv_imag.setVisibility(View.VISIBLE);
                                    Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(iv_imag);
                                    planAnimate();
                                    break;
                                case 52://嫁给我吧
                                    iv_imag.setVisibility(View.VISIBLE);
                                    Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(iv_imag);
                                    planAnimate();
                                    break;

                                case 53://爱死你啦
                                    iv_imag.setVisibility(View.VISIBLE);
                                    Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(iv_imag);
                                    planAnimate();
                                    break;

                                case 54://一路有你
                                    view.setVisibility(View.VISIBLE);
                                    try {
                                        GifDrawable gifDrawable = new GifDrawable(VideoLivePlayActivity.this.getResources(), R.drawable.bike);
                                        view.setImageDrawable(gifDrawable);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    TranslateAnimation animation3 = new TranslateAnimation(-width, width, -150f, -150f);
                                    animation3.setDuration(5000);
                                    animation3.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            view.setVisibility(View.GONE);

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                    view.setAnimation(animation3);
                                    break;


                            }
                            View giftView = llgiftcontent.findViewWithTag(presentIcon);
                            if (giftView == null) {

                                if (llgiftcontent.getChildCount() >= 2) {
                                    View giftView1 = llgiftcontent.getChildAt(0);
                                    CircleImageView picTv1 = (CircleImageView) giftView1.findViewById(R.id.iv_send_gift_icon);
                                    long lastTime1 = (Long) picTv1.getTag();
                                    View giftView2 = llgiftcontent.getChildAt(1);
                                    CircleImageView picTv2 = (CircleImageView) giftView2.findViewById(R.id.iv_send_gift_icon);
                                    long lastTime2 = (Long) picTv2.getTag();
                                    if (lastTime1 > lastTime2) {
                                        removeGiftView(1);
                                    } else {
                                        removeGiftView(0);
                                    }
                                }

                                giftView = addGiftView();
                                giftView.setTag(presentIcon);

                                CircleImageView crvheadimage = (CircleImageView) giftView.findViewById(R.id.iv_send_gift_icon);//头像
                                ImageView item_iv_gift = (ImageView) giftView.findViewById(R.id.iv_gift);//礼物
                                TextView tv_send_gift_name = (TextView) giftView.findViewById(R.id.tv_send_gift_name);//送礼物人的名字

                                final MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);
                                if (userIcon != null) {
                                    Picasso.with(VideoLivePlayActivity.this).load(userIcon).into(crvheadimage);
                                } else {
                                    Picasso.with(VideoLivePlayActivity.this).load(R.drawable.touxiang).into(crvheadimage);
                                }
                                if (docName != null) {
                                    tv_send_gift_name.setText(docName);
                                } else {
                                    tv_send_gift_name.setText(userName);
                                }
                                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(item_iv_gift);
//                item_iv_gift.setImageResource(tag);
                                giftNum.setText("x1");
                                crvheadimage.setTag(System.currentTimeMillis());
                                giftNum.setTag(1);

                                llgiftcontent.addView(giftView);
                                llgiftcontent.invalidate();
                                giftView.startAnimation(inAnim);
                                inAnim.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        giftNumAnim.start(giftNum);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                            } else if (isLian) {
                                CircleImageView crvheadimage = (CircleImageView) giftView.findViewById(R.id.iv_send_gift_icon);
                                ImageView item_iv_gift = (ImageView) giftView.findViewById(R.id.iv_gift);
                                TextView tv_send_gift_name = (TextView) giftView.findViewById(R.id.tv_send_gift_name);
                                MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);
                                int showNum = (Integer) giftNum.getTag() + 1;
//                    Picasso.with(WatchLiveActivity.this).load(tag).into(item_iv_gift);
////                item_iv_gift.setImageResource(tag);
//                    if (icon != null) {
//                        Picasso.with(WatchLiveActivity.this).load(icon).into(crvheadimage);
//                    } else {
//                        Picasso.with(WatchLiveActivity.this).load(R.drawable.touxiang).into(crvheadimage);
//                    }
//                    tv_send_gift_name.setText(QuanjiakanSetting.getInstance().getName());
                                if (userIcon != null) {
                                    Picasso.with(VideoLivePlayActivity.this).load(userIcon).into(crvheadimage);
                                } else {
                                    Picasso.with(VideoLivePlayActivity.this).load(R.drawable.touxiang).into(crvheadimage);
                                }
                                if (docName != null) {
                                    tv_send_gift_name.setText(docName);
                                } else {
                                    tv_send_gift_name.setText(userName);
                                }
                                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(QuanjiakanSetting.getInstance().getUserId() + "", "")).into(item_iv_gift);

                                giftNum.setText("x" + showNum);

                                giftNum.setTag(showNum);
                                crvheadimage.setTag(System.currentTimeMillis());
                                giftNumAnim.start(giftNum);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


            }
        }, HttpUrls.SendGift() + params, null, Task.TYPE_GET_STRING_NOCACHE, null));


    }

    private void showGift2(String sendName, String userIcon, String presentIcon, int presentId, String giverId) {

        switch (presentId) {
            case 33://一束玫瑰
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;

            case 34://悬壶济世
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
            case 35://包治百病
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
            case 36://天使的翅膀
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
            case 37://一生一世
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
            case 38://??
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
            case 39://轮船
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
            case 40://爱的诺言
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
            case 41://专机护送
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
            case 42://法拉利
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
            case 43://一飞冲天
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
            case 50://心的声音
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
            case 51://兰博基尼
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
            case 52://嫁给我吧
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;

            case 53://爱死你啦
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;

            case 54://一路有你
                iv_imag.setVisibility(View.VISIBLE);
                Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(iv_imag);
                planAnimate();
                break;
        }

        View giftView = llgiftcontent.findViewWithTag(presentIcon);

        if (giftView == null) {
            if (llgiftcontent.getChildCount() >= 2) {
                View giftView1 = llgiftcontent.getChildAt(0);
                CircleImageView picTv1 = (CircleImageView) giftView1.findViewById(R.id.iv_send_gift_icon);
                long lastTime1 = (Long) picTv1.getTag();
                View giftView2 = llgiftcontent.getChildAt(1);
                CircleImageView picTv2 = (CircleImageView) giftView2.findViewById(R.id.iv_send_gift_icon);
                long lastTime2 = (Long) picTv2.getTag();
                if (lastTime1 > lastTime2) {
                    removeGiftView(1);
                } else {
                    removeGiftView(0);
                }
            }

            giftView = addGiftView();
            giftView.setTag(presentIcon);

            CircleImageView crvheadimage = (CircleImageView) giftView.findViewById(R.id.iv_send_gift_icon);//头像
            ImageView item_iv_gift = (ImageView) giftView.findViewById(R.id.iv_gift);//礼物
            TextView tv_send_gift_name = (TextView) giftView.findViewById(R.id.tv_send_gift_name);//送礼物人的名字

            final MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);
            if (userIcon != null) {
                if (crvheadimage != null) {
                    //TODO 這裡可能會出現Target null的異常---未加if判断前
                    Picasso.with(VideoLivePlayActivity.this).load(userIcon).into(crvheadimage);
                }
            } else {
                if (crvheadimage != null) {
                    Picasso.with(VideoLivePlayActivity.this).load(R.drawable.touxiang).into(crvheadimage);
                }
            }
            if (tv_send_gift_name != null) {
                tv_send_gift_name.setText(sendName);
            }
            Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(item_iv_gift);
//                item_iv_gift.setImageResource(tag);
            giftNum.setText("x1");
            crvheadimage.setTag(System.currentTimeMillis());
            giftNum.setTag(1);
            if(giftView.getParent()!=null){
                llgiftcontent.removeView(giftView);
            }
            llgiftcontent.addView(giftView);
            llgiftcontent.invalidate();
            giftView.startAnimation(inAnim);
            inAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    giftNumAnim.start(giftNum);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {
            CircleImageView crvheadimage = (CircleImageView) giftView.findViewById(R.id.iv_send_gift_icon);
            ImageView item_iv_gift = (ImageView) giftView.findViewById(R.id.iv_gift);
            TextView tv_send_gift_name = (TextView) giftView.findViewById(R.id.tv_send_gift_name);
            MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);
            int showNum = (Integer) giftNum.getTag() + 1;
//                    Picasso.with(WatchLiveActivity.this).load(tag).into(item_iv_gift);
////                item_iv_gift.setImageResource(tag);
//                    if (icon != null) {
//                        Picasso.with(WatchLiveActivity.this).load(icon).into(crvheadimage);
//                    } else {
//                        Picasso.with(WatchLiveActivity.this).load(R.drawable.touxiang).into(crvheadimage);
//                    }
//                    tv_send_gift_name.setText(QuanjiakanSetting.getInstance().getName());
            if (userIcon != null) {
                Picasso.with(VideoLivePlayActivity.this).load(userIcon).into(crvheadimage);
            } else {
                Picasso.with(VideoLivePlayActivity.this).load(R.drawable.touxiang).into(crvheadimage);
            }

            tv_send_gift_name.setText(sendName);

            Picasso.with(VideoLivePlayActivity.this).load(presentIcon.replace(giverId, "")).into(item_iv_gift);

            giftNum.setText("x" + showNum);

            giftNum.setTag(showNum);
            crvheadimage.setTag(System.currentTimeMillis());
            giftNumAnim.start(giftNum);
        }


    }

    //礼物集合
    private List<View> giftViewCollection = new ArrayList<View>();

    /**
     * 添加礼物view
     */
    private View addGiftView() {
        View view = null;
        if (giftViewCollection.size() <= 0) {
            view = LayoutInflater.from(VideoLivePlayActivity.this).inflate(R.layout.item_gift, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 10;
            view.setLayoutParams(lp);
            llgiftcontent.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    giftViewCollection.add(view);
                }
            });
        } else {
            view = giftViewCollection.get(0);
            giftViewCollection.remove(view);
        }
        return view;
    }

    /**
     * 删除礼物view
     */
    private void removeGiftView(final int index) {
        final View removeView = llgiftcontent.getChildAt(index);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llgiftcontent.removeViewAt(index);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeView.startAnimation(outAnim);
            }
        });
    }


    /**
     * 数字放大动画
     */
    public class NumAnim {
        private Animator lastAnimator = null;

        public void start(View view) {
            if (lastAnimator != null) {
                lastAnimator.removeAllListeners();
                lastAnimator.end();
                lastAnimator.cancel();
            }
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.8f, 1.0f);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.8f, 1.0f);
            AnimatorSet animSet = new AnimatorSet();
            lastAnimator = animSet;
            animSet.setDuration(200);
            animSet.setInterpolator(new OvershootInterpolator());
            animSet.playTogether(anim1, anim2);
            animSet.start();
        }
    }


    /**
     * 定时清除礼物  3秒
     */
    private void clearTiming() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                int count = llgiftcontent.getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = llgiftcontent.getChildAt(i);
                    CircleImageView crvheadimage = (CircleImageView) view.findViewById(R.id.iv_send_gift_icon);
                    long nowtime = System.currentTimeMillis();
                    long upTime = (Long) crvheadimage.getTag();
                    if ((nowtime - upTime) >= 3000) {
                        removeGiftView(i);
                        return;
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 3000);
    }

    /*****************  礼物动画end  ***********************/


    /****************************
     * 10分钟计时
     **************************/

    private boolean CanfreeGift = false;
    private Handler countdownHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            computeTime();
            if (mMin < 10) {
                tv_min.setText("0" + mMin);
            } else {
                tv_min.setText(mMin + "");
            }

            if (mSecond < 10) {
                tv_sec.setText("0" + mSecond);
            } else {
                tv_sec.setText(mSecond + "");
            }
            if (mSecond == 0 && mMin == 0) {
                //第一次免费送的礼物
                if (freeGift == 1) {
                    freegift_time.setVisibility(View.GONE);
                    free_gift_background.setImageDrawable(getResources().getDrawable(R.drawable.mianfeilingqu_sel));
                    CanfreeGift = true;
                    if (CanfreeGift) {
                        fl_freegift.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*if (!NetworkUtils.isNetworkAvalible(VideoLivePlayActivity.this)) {
                                    QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "网络连接不可用！");
                                    return;
                                }
                                //发送礼物
                                final String params = "&giverId=" + QuanjiakanSetting.getInstance().getUserId() + "&recipientId=" + liverId + "&presentId=" + freeDatas.get(0).getId();

                                MyHandler.putTask(new Task(new HttpResponseInterface() {
                                    @Override
                                    public void handMsg(String val) {
                                        if (val != null && val.length() > 0) {
                                            free_gift_background.setImageDrawable(getResources().getDrawable(R.drawable.mianfeilingqu_nor));
                                            //设置20分钟之后的图片
                                            Picasso.with(VideoLivePlayActivity.this).load(freeDatas.get(1).getIcon()).into(iv_freegift);
                                            tv_min.setText("20");
                                            tv_sec.setText("00");

                                            mMin = Long.parseLong(tv_min.getText().toString());
                                            mSecond = Long.parseLong(tv_sec.getText().toString());

                                            total = 20 * 60;
                                            startCountDown();
                                            freegift_time.setVisibility(View.VISIBLE);
                                            CanfreeGift = false;
                                            freeGift = 2;
                                            showGift(nickName, "", url, freeDatas.get(0).getIcon(), freeDatas.get(0).getId() + "");


                                        }


                                    }
                                }, HttpUrls.SendGift() + params, null, Task.TYPE_GET_STRING_NOCACHE, null));*/
                                //点击送出礼物之后倒计时20分钟
                                free_gift_background.setImageDrawable(getResources().getDrawable(R.drawable.mianfeilingqu_nor));
                                //设置20分钟之后的图片
                                Picasso.with(VideoLivePlayActivity.this).load(freeDatas.get(1).getIcon()).into(iv_freegift);
                                tv_min.setText("20");
                                tv_sec.setText("00");

                                mMin = Long.parseLong(tv_min.getText().toString());
                                mSecond = Long.parseLong(tv_sec.getText().toString());

                                total = 20 * 60;
                                startCountDown();
                                freegift_time.setVisibility(View.VISIBLE);
                                CanfreeGift = false;
                                freeGift = 2;
                                showGift(nickName, "", url, freeDatas.get(0).getIcon(), freeDatas.get(0).getId() + "");
                                fl_freegift.setClickable(false);
                                return;

                            }
                        });

                    }
                    //第二次免费送的礼物
                } else if (freeGift == 2) {
                    freegift_time.setVisibility(View.GONE);
                    free_gift_background.setImageDrawable(getResources().getDrawable(R.drawable.mianfeilingqu_sel));
                    //20分钟之后的免费礼物

                    CanfreeGift = true;
                    fl_freegift.setClickable(true);
                    if (CanfreeGift) {
                        fl_freegift.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(WatchLiveActivity.this, "送免费礼物", Toast.LENGTH_SHORT).show();
                                //发送礼物
                               /* if (!NetworkUtils.isNetworkAvalible(VideoLivePlayActivity.this)) {
                                    QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "网络连接不可用！");
                                    return;
                                }
                                final String params = "&giverId=" + QuanjiakanSetting.getInstance().getUserId() + "&recipientId=" + liverId + "&presentId=" + freeDatas.get(1).getId();

                                MyHandler.putTask(new Task(new HttpResponseInterface() {
                                    @Override
                                    public void handMsg(String val) {
                                        if (val != null && val.length() > 0) {
                                            LogUtil.e("val-------" + val);
                                            tv_min.setText("30");
                                            tv_sec.setText("00");

                                            mMin = Long.parseLong(tv_min.getText().toString());
                                            mSecond = Long.parseLong(tv_sec.getText().toString());
                                            total = 30 * 60;
                                            startCountDown();
                                            freegift_time.setVisibility(View.VISIBLE);
                                            CanfreeGift = false;
                                            freeGift = 3;
                                            free_gift_background.setImageDrawable(getResources().getDrawable(R.drawable.mianfeilingqu_nor));
                                            Picasso.with(VideoLivePlayActivity.this).load(freeDatas.get(2).getIcon()).into(iv_freegift);
                                            showGift(nickName, "", url, freeDatas.get(1).getIcon(), freeDatas.get(1).getId() + "");


                                        }


                                    }
                                }, HttpUrls.SendGift() + params, null, Task.TYPE_GET_STRING_NOCACHE, null));*/
                                //点击送出礼物之后倒计时20分钟
                                tv_min.setText("30");
                                tv_sec.setText("00");

                                mMin = Long.parseLong(tv_min.getText().toString());
                                mSecond = Long.parseLong(tv_sec.getText().toString());
                                total =30 * 60;
                                startCountDown();
                                freegift_time.setVisibility(View.VISIBLE);
                                CanfreeGift = false;
                                freeGift = 3;
                                free_gift_background.setImageDrawable(getResources().getDrawable(R.drawable.mianfeilingqu_nor));
                                Picasso.with(VideoLivePlayActivity.this).load(freeDatas.get(2).getIcon()).into(iv_freegift);
                                showGift(nickName, "", url, freeDatas.get(1).getIcon(), freeDatas.get(1).getId() + "");
                                fl_freegift.setClickable(false);
                                return;
                            }
                        });

                    }

                } else if (freeGift == 3) {
                    fl_freegift.setClickable(true);
                    freegift_time.setVisibility(View.GONE);
                    free_gift_background.setImageDrawable(getResources().getDrawable(R.drawable.mianfeilingqu_sel));
                    CanfreeGift = true;
                    if (CanfreeGift) {
                        fl_freegift.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(WatchLiveActivity.this, "送免费礼物", Toast.LENGTH_SHORT).show();
                                //发送礼物
                                /*if (!NetworkUtils.isNetworkAvalible(VideoLivePlayActivity.this)) {
                                    QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "网络连接不可用！");
                                    return;
                                }
                                final String params = "&giverId=" + QuanjiakanSetting.getInstance().getUserId() + "&recipientId=" + liverId + "&presentId=" + freeDatas.get(2).getId();

                                MyHandler.putTask(new Task(new HttpResponseInterface() {
                                    @Override
                                    public void handMsg(String val) {
                                        LogUtil.e("val-------" + val);
                                        free_gift_background.setVisibility(View.GONE);
                                        iv_freegift.setVisibility(View.GONE);
                                        showGift(nickName, "", url, freeDatas.get(2).getIcon(), freeDatas.get(2).getId() + "");

                                    }
                                }, HttpUrls.SendGift() + params, null, Task.TYPE_GET_STRING_NOCACHE, null));*/

                                free_gift_background.setVisibility(View.GONE);
                                iv_freegift.setVisibility(View.GONE);
                                showGift(nickName, "", url, freeDatas.get(2).getIcon(), freeDatas.get(2).getId() + "");
                                return;

                            }
                        });

                    }


                }


            }
        }
    };

    private void computeTime() {
        mSecond--;
        if (mSecond < 0) {
            mSecond = 59;
            mMin--;
            if (mMin < 0) {
                mMin = 0;
            }
        }

    }

    private int total = 10*60;

    private Timer countTimer;
    private TimerTask countTimerTask;
    private void startCountDown() {
        if(countTimer!=null){
            countTimer.cancel();
        }
        countTimer = new Timer();
        countTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(total > 0){
                    total--;
                    android.os.Message message = android.os.Message.obtain();
                    message.what = 1;
                    countdownHandler.sendMessage(message);
                }
            }
        };
        countTimer.schedule(countTimerTask,0,1000);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    do {
//                        Thread.sleep(1000);
//                        total--;
//                        android.os.Message message = android.os.Message.obtain();
//                        message.what = 1;
//                        countdownHandler.sendMessage(message);
//                    } while (total > 0);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();
    }

    /**
     * 时间倒计时
     */

    private CountDownTimer timer = new CountDownTimer(3000, 100) {

        @Override
        public void onTick(long millisUntilFinished) {
            time_count.setText((millisUntilFinished / 100) + "");
        }

        @Override
        public void onFinish() {
            time_count.setVisibility(View.GONE);
            send.setVisibility(View.VISIBLE);
        }
    };

    /****************************
     * 10分钟计时
     **************************/

    public void setConcern() {
        if (entity.getIsFollow() > 0) {
            if (mChatAdapter != null && mChatView != null) {
                createConcernMessage("");
            }
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
                LogUtil.e("setNoDisturbGlobal result:" + result + "  message:" + message);
            }
        });

        JMessageClient.exitConversation();
        //退群后消息不会再收到
        JMessageClient.exitGroup(Long.parseLong(groupId), new BasicCallback() {
            @Override
            public void gotResult(int resultCode, String resultMessage) {
                LogUtil.e("resultCode:" + resultCode + "  ****   resultMessage:" + resultMessage);
            }
        });

        LinkedHashSet<String> hashSet = new LinkedHashSet<String>();
        JPushInterface.setTags(this, hashSet, new TagAliasCallback() {
            @Override
            public void gotResult(int result, String message, Set<String> set) {
                LogUtil.e("setTags result:" + result + "  message:" + message);
            }
        });
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }

//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if (isChecked) {
//            mQuPlayer.setErrorListener(_ErrorListener);
//            mQuPlayer.setInfoListener(_InfoListener);
//            mQuPlayer.setSurface(_Surface);
//            LogUtil.e("直播地址:" + urlString);
//            DataSpec spec = new DataSpec(urlString, DataSpec.MEDIA_TYPE_STREAM);
//            mQuPlayer.setDataSource(spec);
//            mQuPlayer.setLooping(false);
//            mQuPlayer.prepare();
//            mQuPlayer.start();
//
//        } else {
//            _Handler.removeCallbacks(_Restart);
//            synchronized (mQuPlayer) {
//                mQuPlayer.stop();
//            }
//        }
//    }
//
//    private Runnable _Restart = new Runnable() {
//        @Override
//        public void run() {
//            synchronized (mQuPlayer) {
//                LogUtil.d(TAG, "stop stream");
//                mQuPlayer.stop();
//                LogUtil.d(TAG, "start stream");
//                mQuPlayer.start();
//            }
//        }
//    };
//
//    private long _TestStartTime;
//
//    private QuPlayer.OnInfoListener _InfoListener = new QuPlayer.OnInfoListener() {
//        @Override
//        public void onStart() {
//            LogUtil.d("InfoListener  *****     starting ... ");
//            _TestStartTime = System.currentTimeMillis();
//            report_error("starting ... ", true);
//        }
//
//        @Override
//        public void onStop() {
//            LogUtil.d("InfoListener  *****     stoping ...");
//            report_error("stoping ... ", true);
//        }
//
//
//        @Override
//        public void onVideoStreamInfo(int width, int height) {
//            String log = String.format("Video Stream info width %d height %d \n", width, height);
//            LogUtil.d(log);
//            LogUtil.d("InfoListener  *****     onVideoStreamInfo spend time " + (System.currentTimeMillis() - _TestStartTime));
//            report_error("stoping ... ", true);
//        }
//
//        @Override
//        public void onAndroidBufferQueueCount(int count) {
//            LogUtil.d("InfoListener  *****     Android buffer Queue count " + count);
//        }
//
//        @Override
//        public void onProgress(long progress) {
//            String log = String.format("progress %d\n", progress);
//            LogUtil.d("InfoListener  *****     onProgress spend time " + (System.currentTimeMillis() - _TestStartTime));
//        }
//    };
//
//    private void report_error(String err, boolean bshow) {
////        Toast.makeText(this, ""+err, Toast.LENGTH_SHORT).show();
//        LogUtil.e("" + err);
//        return;
//    }

//    private QuPlayer.OnErrorListener _ErrorListener = new QuPlayer.OnErrorListener() {
//        @Override
//        public void onError(int errCode) {
//            switch (errCode) {
//                case MediaPlayer.ALIVC_ERR_LOADING_TIMEOUT:
//                    report_error("缓冲超时,请确认网络连接正常后重试", true);
//                    break;
//                case MediaPlayer.ALIVC_ERR_NO_INPUTFILE:
//                    report_error("no input file", true);
//                    break;
//                case MediaPlayer.ALIVC_ERR_NO_VIEW:
//                    report_error("no surface", true);
//                    break;
//                case MediaPlayer.ALIVC_ERR_INVALID_INPUTFILE:
////                    report_error("视频资源或者网络不可用", true);
//                    //直播结束
//                    exitDialog();
//                    break;
//                case MediaPlayer.ALIVC_ERR_NO_SUPPORT_CODEC:
//                    report_error("no codec", true);
//                    break;
//                case MediaPlayer.ALIVC_ERR_FUNCTION_DENIED:
//                    report_error("no priority", true);
//                    break;
//                case MediaPlayer.ALIVC_ERR_UNKNOWN:
//                    report_error("unknown error", true);
//                    break;
//                case MediaPlayer.ALIVC_ERR_NO_NETWORK:
//                    report_error("视频资源或者网络不可用", true);
//                    break;
//                case MediaPlayer.ALIVC_ERR_ILLEGALSTATUS:
//                    report_error("illegal call", true);
//                    break;
//                case MediaPlayer.ALIVC_ERR_NOTAUTH:
//                    report_error("auth failed", true);
//                    break;
//                case MediaPlayer.ALIVC_ERR_READD:
//                    report_error("资源访问失败,请重试", true);
//                    break;
//                default:
//                    break;
//
//            }
//            LogUtil.e("Live Play Error : *****    " + errCode);
//            if (errCode == 504) {//直播结束
//                exitActivityDialog();
//
//                report_error("直播结束：" + errCode, true);
//            } else {
////                exitActivityDialog();
////                report_error("错误码："+errCode, true);
//                _Handler.postDelayed(_Restart, 3000);
//            }
//
//        }
//    };

    public void exitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoLivePlayActivity.this);
        builder.setMessage("来晚了，直播已经结束");
        builder.setTitle("提示");

        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                VideoLivePlayActivity.this.finish();
            }
        });

        builder.create().show();
    }

//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        _Surface = holder.getSurface();
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.runInForeground();
        }

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

        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_NOTIFICATION);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(VideoLivePlayActivity.class.getName().equals(BaseApplication.getInstances().getCurrentActivityName())){
            BaseApplication.getInstances().setCurrentActivityName("");
//            Intent intent = new Intent(this,VideoLivePlayActivity.class);
//            intent.putExtra(PARAM_ENTITY,entity);
//            intent.putExtra(PARAM_URL,urlString);
//            intent.putExtra(PARAM_GROUP,groupId);
//            intent.putExtra(PARAM_NUMBER,number);
//            intent.putExtra(PARAM_NAME,name);
//            intent.putExtra(PARAM_HEADIMG,headImage);
//            intent.putExtra(PARAM_LIVERID,liverId);
//            startActivity(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e("Home键返回");
        //***********
//        mQuPlayer = new QuPlayerExt();
//        _SurfaceView = (SurfaceView) findViewById(R.id.surface);
//        _SurfaceView.getHolder().addCallback(this);
//        _Handler = new Handler(Looper.myLooper());

        initInputView();

        initChatView();

        initJPush();


        //判断是否禁言了
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if (val != null) {
                    HttpResponseResult result = new HttpResponseResult(val);
                    if (result.getCode().equals("200")) {
                        isGag = true;
                    } else if (result.getCode().equals("250")) {
                        isGag = false;
                    }

                }

            }
        }, HttpUrls.isGag() + "&memberId=" + QuanjiakanSetting.getInstance().getUserId() +
                "&groupId=" + groupId, null, Task.TYPE_GET_STRING_NOCACHE, null));

//        _Handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mQuPlayer != null) {
//                    //进入时会检查拉流地址，当不可用时，会回调该Error接口
//                    mQuPlayer.setErrorListener(_ErrorListener);
//                    mQuPlayer.setInfoListener(_InfoListener);
//                    mQuPlayer.setSurface(_Surface);
//                    LogUtil.e("直播地址:" + urlString);
//                    DataSpec spec = new DataSpec(urlString, DataSpec.MEDIA_TYPE_STREAM);
//                    mQuPlayer.setDataSource(spec);
//                    mQuPlayer.setLooping(false);
//                    mQuPlayer.prepare();
//                    mQuPlayer.start();
//                } else {
//                    mQuPlayer = new QuPlayerExt();
//                    //进入时会检查拉流地址，当不可用时，会回调该Error接口
//                    mQuPlayer.setErrorListener(_ErrorListener);
//                    mQuPlayer.setInfoListener(_InfoListener);
//                    mQuPlayer.setSurface(_Surface);
//                    LogUtil.e("直播地址:" + urlString);
//                    DataSpec spec = new DataSpec(urlString, DataSpec.MEDIA_TYPE_STREAM);
//                    mQuPlayer.setDataSource(spec);
//                    mQuPlayer.setLooping(false);
//                    mQuPlayer.prepare();
//                    mQuPlayer.start();
//                }
//            }
//        }, 300);
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
                } else if (messageType != null & messageType.equals("36")) {
                    //被禁言
                    isGag = true;
                    KeyBoardUtils.closeKeybord(et_comment, VideoLivePlayActivity.this);
                    Toast.makeText(VideoLivePlayActivity.this, "您已被主播禁言", Toast.LENGTH_SHORT).show();
                    //TODO 发送一条自己被禁言的消息
                    createGagMessage("");
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
                        VideoLivePlayActivity.this.finish();
                    }
                });
    }

    private Dialog dialogExit;

    public void exitActivityDialog() {
        dialogExit = QuanjiakanDialog.getInstance().getCommonConfirmDialog(this, "提示", "来晚了，直播已经结束!",
                "等待", false, "退出", true, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isReceived = false;
                        VideoLivePlayActivity.this.finish();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());

        BaseApplication.getInstances().setCurrentActivityName(VideoLivePlayActivity.class.getName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
//        if (mQuPlayer != null) {
//            mQuPlayer.stop();
//            mQuPlayer.dispose();
//        }

        endRandomHeart();

        if(BaseApplication.getInstances().getKeyValue(BaseApplication.getInstances().getUser_id()+"disturb")!=null &&
                !"".equals(BaseApplication.getInstances().getKeyValue(BaseApplication.getInstances().getUser_id()+"disturb"))){
//					JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_NOTIFICATION);//不展示通知
//					JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_SOUND);//展示通知，无声音有震动
//					JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_VIBRATE);//展示通知，有声音无震动
            JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_SILENCE);//展示通知，无声音无震动
        }else{
            JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);//展示通知，有声音有震动
        }
    }

    @Override
    protected void onDestroy() {
        exitGroup();
        super.onDestroy();
        BaseApplication.getInstances().setCurrentActivityName("");
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

    public void getLevelInfo(){
        HashMap<String, String> params = new HashMap<>();
        Task task = new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                JSONObject json;
                // TODO Auto-generated method stub
                if (val != null && !val.equals("") && val.toLowerCase().startsWith("{")) {
                    //上传文件成功
                    try {
                        json = new JSONObject(val);
                        if(json.has("code") && "200".equals(json.getString("code"))
                                && json.has("object") && json.getJSONObject("object")!=null
                                ){
                            if(json.getJSONObject("object").has("grade")){
                                grade = Integer.parseInt(json.getJSONObject("object").getString("grade"));
                            }else{
                                grade = 1;
                            }
                        }else{
                            grade = 1;
                        }
                        LogUtil.e("grade:"+grade);
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if(val==null || "".equals(val)){
                    /**
                     * TODO 设置 用户等级
                     */
                    grade = 1;
                } else {
                    /**
                     * TODO 设置 用户等级
                     */
                    grade = 1;
                }
            }
        }, HttpUrls.getUserLevelInfo(), params, Task.TYPE_GET_STRING_NOCACHE, null);
        MyHandler.putTask(VideoLivePlayActivity.this, task);
    }

    /**********
     * 获取昵称和头像
     ***********/

    private UserInfo info;
    private int touXiang;
    private File file = null;

    public void loadInfo() {
        info = null;
        info = JMessageClient.getMyInfo();
        if (info != null) {
            if (info.getAvatar() != null) {
                file = info.getAvatarFile();
//                ImageLoadUtil.loadImage(item_iv_gift,file.getAbsolutePath(),ImageLoadUtil.optionsCircle);
            } else {
                touXiang = R.drawable.touxiang_big_icon;
            }

            if (BaseApplication.getInstances().getKeyValue("nickname") != null) {
                nickName = BaseApplication.getInstances().getKeyValue("nickname").replace("'", "");
            } else {
                if (info.getNickname() != null) {
                    nickName = info.getNickname();
                } else {
                    nickName = info.getUserName();
                }
            }
        } else {
            JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        info = JMessageClient.getMyInfo();
                        if (info.getAvatar() != null) {
                            file = info.getAvatarFile();

                        } else {
                            touXiang = R.drawable.touxiang_big_icon;
                        }
                        if (BaseApplication.getInstances().getKeyValue("nickname") != null) {
                            nickName = BaseApplication.getInstances().getKeyValue("nickname").replace("'", "");
                        } else {
                            if (info.getNickname() != null) {
                                nickName = info.getNickname();
                            } else {
                                nickName = info.getUserName();
                            }
                        }
                    } else {
                        touXiang = R.drawable.touxiang_big_icon;
                        nickName = BaseApplication.getInstances().getUser_id();
                    }
                }
            });
        }
    }

    private String nickName = "";
    private String url = null;

    public void loadNameAndImage() {
        HashMap<String, String> params = new HashMap<>();
        Task task = new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                JSONObject json;
                // TODO Auto-generated method stub
                if (val != null && !val.equals("") && val.toLowerCase().startsWith("{")) {
                    //上传文件成功
                    try {
                        json = new JSONObject(val);
                        if (json.has("nickname") && json.get("nickname") != null) {
                            BaseApplication.getInstances().setKeyValue("nickname", json.get("nickname").toString());
                            nickName = json.get("nickname").toString();
                        } else {
                            if (BaseApplication.getInstances().getKeyValue("nickname") != null) {
                                nickName = BaseApplication.getInstances().getKeyValue("nickname");
                            } else {
                                nickName = BaseApplication.getInstances().getUser_id();
                            }
                        }

                        if (json.has("picture") && json.get("picture") != null && json.get("picture").toString().toLowerCase().startsWith("http")) {
//                            ImageLoadUtil.loadImage(user_header_img,json.get("picture").toString(),ImageLoadUtil.optionsCircle);
                            url = json.get("picture").toString();
                        } else {
                            touXiang = R.drawable.touxiang_big_icon;
                        }
                        return;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (val == null || "".equals(val)) {
                    loadInfo();
                } else {
                    loadInfo();
                }
            }
        }, HttpUrls.getNameAndHeadIcon(), params, Task.TYPE_GET_STRING_NOCACHE, null);
        MyHandler.putTask(VideoLivePlayActivity.this, task);
    }

    private void planAnimate() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim = ObjectAnimator.ofFloat(iv_imag, "translationX", 0f, width / 2, width);

        ObjectAnimator anim2 = ObjectAnimator.ofFloat(iv_imag, "translationY", 100f, 200f, height);

        ObjectAnimator anim3 = ObjectAnimator.ofFloat(iv_imag, "scaleX", 2, 4, 1);

        ObjectAnimator anim4 = ObjectAnimator.ofFloat(iv_imag, "scaleY", 2, 4, 1);


        set.setDuration(5000);
        set.play(anim).with(anim2).with(anim3).with(anim4);
        set.start();

    }

    //获取自己的信息  如E豆，经验

    public void getMyInfo() {
        if (!NetworkUtils.isNetworkAvalible(VideoLivePlayActivity.this)) {
            QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "网络连接不可用！");
            return;
        }
        //Toast.makeText(VideoLivePlayActivity.this, "应该到了", Toast.LENGTH_SHORT).show();
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
           public void handMsg(String val) {
                /*Toast.makeText(VideoLivePlayActivity.this, val, Toast.LENGTH_SHORT).show();
                LogUtil.e("info----------" + val);
                if (val != null && val.length() > 0) {
                    LevelInfo levelInfo = JSON.parseObject(val, LevelInfo.class);
                    Toast.makeText(VideoLivePlayActivity.this, levelInfo.toString(), Toast.LENGTH_SHORT).show();
                    if (levelInfo!= null&&"200".equals(levelInfo.getCode())){
                        LevelInfo.ObjectBean objectBean = levelInfo.getObject();
                        *//**
                         * currentGradeExperience : 2
                         * ebeans : 999955757
                         * ebeansConvertWallet : 8.9996016E7
                         * experience : 2
                         * money : 0
                         * nextGradeExperience : 998
                         * recharge : 0
                         * totalGive : 44252
                         * totalRecipient : 10
                         *//*
                        if (objectBean != null) {
                            ebeans = objectBean.getEbeans();//e豆
                           *//* Toast.makeText(VideoLivePlayActivity.this, ""+ebeans, Toast.LENGTH_SHORT).show();
                            Log.e("ceshi",ebeans+"");*//*
                        }
                    }
                }*/
                try {
                    JSONObject jsonObject = new JSONObject(val);
                    if("200".equals(jsonObject.getString("code"))) {
                        JSONObject object = jsonObject.getJSONObject("object");
                        ebeans=object.getInt("ebeans");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, HttpUrls.getMyLevelInfo(), null, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    public void getHostInfo() {
        if (!NetworkUtils.isNetworkAvalible(VideoLivePlayActivity.this)) {
            QuanjiakanUtil.showToast(VideoLivePlayActivity.this, "网络连接不可用！");
            return;
        }
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                /*if (val != null && val.length() > 0) {
                    LevelInfo levelInfo = JSON.parseObject(val, LevelInfo.class);
                    if (levelInfo != null && "200".equals(levelInfo.getCode())) {
                        LevelInfo.ObjectBean objectBean = levelInfo.getObject();
                        *//**
                         * currentGradeExperience : 2
                         * ebeans : 999955757
                         * ebeansConvertWallet : 8.9996016E7
                         * experience : 2
                         * money : 0
                         * nextGradeExperience : 998
                         * recharge : 0
                         * totalGive : 44252
                         * totalRecipient : 10
                         *//*
                        if (objectBean != null) {
                            int ebeans = objectBean.getEbeans();//e豆
                            tv_edounumber.setText(ebeans + "");
                        }
                    }
                }*/
                try {
                    JSONObject jsonObject = new JSONObject(val);
                    if("200".equals(jsonObject.getString("code"))) {
                        JSONObject object = jsonObject.getJSONObject("object");
                        tv_edounumber.setText(object.getInt("ebeans")+"");


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, HttpUrls.getSpecificInfo(liverId), null, Task.TYPE_GET_STRING_NOCACHE, null));
    }
    private final String KEY_FLAG = "HOME";
    private String keyFlag;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            videoPlayEnd();
        }

        return super.onKeyDown(keyCode, event);
    }

    /***********************             金山播放器相关       ***************************************************/
    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void videoPlayEnd() {
        if (mVideoView != null) {
            mVideoView.release();
            mVideoView = null;
        }

        if (mQosThread != null) {
            mQosThread.stopThread();
            mQosThread = null;
        }

        mHandler = null;

        finish();
    }




/***********************            金山播放器相关       ***************************************************/
}
