package com.androidquanjiakan.activity.index.watch_child;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.adapter.DeviceVoiceAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.datahandler.DeviceVoiceHandler;
import com.androidquanjiakan.datahandler.TempHandler;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.entity.CommonVoiceData;
import com.androidquanjiakan.entity.DeviceVoiceEntity;
import com.androidquanjiakan.entity.WatchCommonResult;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.result.ContactsResultBean;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;
import com.wbj.ui.recorder.AmrRecorder;
import com.wbj.ui.recorder.AudioFileFunc;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jmessage.android.uikit.chatting.utils.FileHelper;
import cn.jmessage.android.uikit.chatting.utils.HandleResponseCode;
import cn.jmessage.android.uikit.chatting.utils.IdHelper;

/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class ChildVoiceChatActivity extends BaseActivity {

    private PullToRefreshListView listView;
    private Button send_bg;
    private Button send;

    float mTouchY1, mTouchY2, mTouchY;
    private long startTime, time1, time2;
    private final float CANCEL_DISTANCE = 300f;
    private Dialog recordIndicator;
    private boolean mIsPressed = false;
    private Timer timer = new Timer();


    public List<DeviceVoiceEntity> dataList = new ArrayList<>();
    DeviceVoiceAdapter adapter;
    private AudioManager mAm;
    private MyOnAudioFocusChangeListener mListener;
    private boolean vIsActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_child_voice);
        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        if (device_id == null
                ) {
            BaseApplication.getInstances().toast(ChildVoiceChatActivity.this, "传入参数异常!");
            finish();
            return;
        }
        initTitleBar();
        initView();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        device_id = intent.getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        if (device_id == null
                ) {
            BaseApplication.getInstances().toast(ChildVoiceChatActivity.this, "传入参数异常!");
            finish();
            return;
        }
        initTitleBar();
        initView();
    }

    public void initView() {
        listView = (PullToRefreshListView) findViewById(R.id.listview);
        if (adapter == null) {
            adapter = new DeviceVoiceAdapter(getApplicationContext(), dataList,device_id);
            adapter.setListView(listView);
            listView.setAdapter(adapter);
        }

        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadIntoList();
                listView.onRefreshComplete();
            }
        });

        send_bg = (Button) findViewById(R.id.send_bg);
        send = (Button) findViewById(R.id.send);
        send.setText("");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        send.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                send_bg.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //检查sd卡是否存在
                        mIsPressed = true;
                        time1 = System.currentTimeMillis();
                        mTouchY1 = event.getY();
                        if (FileHelper.isSdCardExist()) {
                            if (isTimerCanceled) {
                                timer = createTimer();
                            }
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    android.os.Message msg = myHandler.obtainMessage();
                                    msg.what = START_RECORD;
                                    msg.sendToTarget();
                                }
                            }, 500);
                        } else {
                            send.setPressed(false);
                            mIsPressed = false;
                            return false;
                        }
                        break;

                    }
                    case MotionEvent.ACTION_MOVE: {
                        mTouchY = event.getY();
                        //手指上滑到超出限定后，显示松开取消发送提示
                        if (mTouchY1 - mTouchY > CANCEL_DISTANCE) {
                            mVolumeHandler.sendEmptyMessage(CANCEL_RECORD);
                            if (mThread != null) {
                                mThread.exit();
                            }
                            mThread = null;
                        } else {
                            if (mThread == null) {
                                mThread = new ObtainDecibelThread();
                                mThread.start();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {

                        mIsPressed = false;
                        send.setPressed(false);
                        mTouchY2 = event.getY();
                        time2 = System.currentTimeMillis();
                        if (time2 - time1 < 500) {
                            cancelTimer();
                            return true;
                        } else if (time2 - time1 < 1000) {
                            cancelRecord();
                        } else if (mTouchY1 - mTouchY2 > CANCEL_DISTANCE) {
                            cancelRecord();
                        } else if (time2 - time1 < 60000)
                            finishRecord();
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        cancelRecord();
                        break;
                    }
                }
                return false;
            }
        });
        //**********************************
        myHandler = new MyHandler(send);

        mVolumeHandler = new ShowVolumeHandler(send);
        res = new int[]{IdHelper.getDrawable(this, "voice_bg_upglide_1"),
                IdHelper.getDrawable(this, "voice_bg_upglide_2"),
                IdHelper.getDrawable(this, "voice_bg_upglide_3"),
                IdHelper.getDrawable(this, "voice_bg_upglide_4"),
                IdHelper.getDrawable(this, "voice_bg_upglide_5"),
                IdHelper.getDrawable(this, "voice_bg_upglide_6"),
                IdHelper.getDrawable(this, "voice_bg_upglide_7"),
                IdHelper.getDrawable(this, "data_ico_cancel")};
    }

    public class MyOnAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            // TODO Auto-generated method stub
        }
    }

    private ImageButton ibtn_back;
    private TextView tv_title;
    private TextView menu_text;
    private ImageButton ibtn_menu;

    public void initTitleBar() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        menu_text = (TextView) findViewById(R.id.menu_text);
        ibtn_menu = (ImageButton) findViewById(R.id.ibtn_menu);

        ibtn_back.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.VISIBLE);
        menu_text.setVisibility(View.GONE);
        ibtn_menu.setVisibility(View.GONE);

        tv_title.setText("语音微聊");
        ibtn_back.setImageResource(R.drawable.back);//返回
        ibtn_menu.setImageResource(R.drawable.ic_navigation_drawer);//菜单
        menu_text.setText("文字菜单");

        ibtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
        loadIntoList();
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
        EventBus.getDefault().unregister(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private int[] res;

    private MyHandler myHandler;
    private Handler mHandler = new Handler();

//    private File myRecAudioFile;

    private ImageView mVolumeIv;
//    private TextView mRecordHintTv;

    private final int MIN_INTERVAL_TIME = 1000;

    private final int CANCEL_RECORD = 7;
    private final static int START_RECORD = 8;
    private final int RECORD_DENIED_STATUS = 1000;

//    private MediaRecorder recorder;

    private ObtainDecibelThread mThread;

    private Timer mCountTimer;
    private boolean isTimerCanceled = false;
    private boolean mTimeUp = false;
    private Handler mVolumeHandler;

    //取消录音，清除计时
    private void cancelRecord() {
        if (vIsActive) {
            int i = mAm.abandonAudioFocus(mListener);
            LogUtil.e("fffff" + i);
        }

        //可能在消息队列中还存在HandlerMessage，移除剩余消息
        mVolumeHandler.removeMessages(56, null);
        mVolumeHandler.removeMessages(57, null);
        mVolumeHandler.removeMessages(58, null);
        mVolumeHandler.removeMessages(59, null);
        mTimeUp = false;
        cancelTimer();
        stopRecording();
        if (recordIndicator != null) {
            recordIndicator.dismiss();
        }
//        if (myRecAudioFile != null) {
//            myRecAudioFile.delete();
//        }


        //******
        AmrRecorder.getInstance().stopRecordAndFile();
    }

    //TODO 取消定时器
    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            isTimerCanceled = true;
        }
        if (mCountTimer != null) {
            mCountTimer.cancel();
            mCountTimer.purge();
        }
    }

    //TODO 停止录音
    private void stopRecording() {
        if (mThread != null) {
            mThread.exit();
            mThread = null;
        }
//        releaseRecorder();
    }

    public void releaseRecorder() {
//        if (recorder != null) {
//            try {
//                recorder.stop();
//            }catch (Exception e){
//                Log.d("RecordVoice", "Catch exception: stop recorder failed!");
//            }finally {
//                recorder.release();
//                recorder = null;
//            }
//        }
    }

    private String device_id;
    private String lastFilePath;

    //TODO 录音完成
    private void finishRecord() {
        LogUtil.e("yyyyy" + vIsActive);
        if (vIsActive) {
            int i = mAm.abandonAudioFocus(mListener);
            LogUtil.e("fffff" + i);
        }

        cancelTimer();
        stopRecording();
        if (recordIndicator != null) {
            recordIndicator.dismiss();
        }
        //*****
        AmrRecorder.getInstance().stopRecordAndFile();
        //*****
        long intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            Toast.makeText(this, this.getString(IdHelper.getString(this,
                    "jmui_time_too_short_toast")), Toast.LENGTH_SHORT).show();
//            myRecAudioFile.delete();
        } else {

            File file = new File(AudioFileFunc.getRecordFilePath());
            if (file != null && file.exists()) {
                //TODO 屏蔽重复发送的情况
                if (lastFilePath != null && lastFilePath.equals(file.getAbsolutePath())) {
                    //TODO to avoid resend same file twice
                    LogUtil.e("出现重复发送的情况");
                    return;
                }
                lastFilePath = file.getAbsolutePath();
                MediaPlayer mp = new MediaPlayer();
                try {
                    FileInputStream fis = new FileInputStream(file);
                    mp.setDataSource(fis.getFD());
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //某些手机会限制录音，如果用户拒接使用录音，则需判断mp是否存在
                if (mp != null) {
                    int duration = mp.getDuration() / 1000;//即为时长 是s
                    if (duration < 1) {
                        duration = 1;
                    } else if (duration > 60) {
                        duration = 60;
                    }
                    try {
                        /**
                         * 发送语音消息，并将列表滚动到最底层
                         */
                        //******  Test Remove Cache File And Release Device Reources
//                        myRecAudioFile.delete();
                        mp.stop();
                        mp.release();

                        //**************** SDK Send Voice
                        AmrRecorder mRecord = AmrRecorder.getInstance();
                        long mSize = mRecord.getRecordFileSize(this);

                        byte[] data = null;
                        try {
                            data = readfile();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        if (data == null) {
                            BaseApplication.getInstances().toast(ChildVoiceChatActivity.this, "音频文件录制失败!");
                        } else {

                            if (!BaseApplication.getInstances().isSDKConnected()) {
                                Toast.makeText(this, "已与手表服务器断开连接,请在重连成功后重试", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String strDev = device_id;
                            long devid = Long.parseLong(strDev, 16);
                            int returnCode = BaseApplication.getInstances().getNattyClient().ntyVoiceDataReqClient(devid, data, (int) mSize);
                            LogUtil.e("returnCode:" + returnCode);

                            //TODO **********测试*****发送   播放  以下的步骤需要在发送成功的回调到达后再调用
//                            DeviceVoiceEntity entity = new DeviceVoiceEntity();
//                            entity.setDevice_id(device_id);
//                            entity.setUserid(BaseApplication.getInstances().getUser_id());
//                            entity.setDirection(TableInfo_ValueStub.DEVICE_VOICE_INFO_DIRECTION_SEND);
//                            entity.setTime(AudioFileFunc.getTime() + "");
//                            entity.setVoice_path(AudioFileFunc.getRecordFilePath());
//
//                            DeviceVoiceHandler.insertValue(entity);
//
//                            dataList.add(entity);
//                            //刷新Adapter
//
//                            adapter.notifyDataSetChanged();
//                            toListBottom();

                        }
                        //****************
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, this.getString(IdHelper.getString(this,
                            "jmui_record_voice_permission_request")), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public byte[] readfile() throws IOException {
        String filepath = AudioFileFunc.getRecordFilePath();
        byte[] buffer = new byte[32 * 1024];

        File file = new File(filepath);
        if (file.exists()) {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            int len = dis.read(buffer);
            dis.close();
            return buffer;
        }
        return null;
    }

    private Timer createTimer() {
        timer = new Timer();
        isTimerCanceled = false;
        return timer;
    }

    private void initDialogAndStartRecord() {
        //存放录音文件目录
        File rootDir = this.getFilesDir();
        String fileDir = rootDir.getAbsolutePath() + this.getPackageName() + "/device/voice";
        File destDir = new File(fileDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        //录音文件的命名格式
//        myRecAudioFile = new File(fileDir,
//                new DateFormat().format("child_voice_yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".amr");
//        if (myRecAudioFile == null) {
//            cancelTimer();
//            stopRecording();
//            Toast.makeText(getActivity(), getActivity().getString(IdHelper.getString(getActivity(), "jmui_create_file_failed")),
//                    Toast.LENGTH_SHORT).show();
//        }
        recordIndicator = new Dialog(this, IdHelper.getStyle(this, "jmui_record_voice_dialog"));
        recordIndicator.setContentView(IdHelper.getLayout(this, "dialog_record_voice"));
        mVolumeIv = (ImageView) recordIndicator.findViewById(IdHelper.getViewID(this, "jmui_volume_hint_iv"));
//        mRecordHintTv = (TextView) recordIndicator.findViewById(IdHelper.getViewID(getActivity(), "jmui_record_voice_tv"));
//        mRecordHintTv.setText(getActivity().getString(IdHelper.getString(getActivity(), "jmui_move_to_cancel_hint")));
        startRecording();
        recordIndicator.show();
    }

    private void startRecording() {
        try {
//            recorder = new MediaRecorder();
//            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//            recorder.setOutputFile(myRecAudioFile.getAbsolutePath());
//            myRecAudioFile.createNewFile();
//            recorder.prepare();
//            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
//                @Override
//                public void onError(MediaRecorder mediaRecorder, int i, int i2) {
//                    Log.i("RecordVoiceController", "recorder prepare failed!");
//                }
//            });
//            recorder.start();
            //*******
            AmrRecorder.getInstance().startRecordAndFile(this);
            startTime = System.currentTimeMillis();
            mCountTimer = new Timer();
            mCountTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTimeUp = true;
                    android.os.Message msg = mVolumeHandler.obtainMessage();
                    msg.what = 15;//TODO 限制时长为
                    Bundle bundle = new Bundle();
                    bundle.putInt("restTime", 5);
                    msg.setData(bundle);
                    msg.sendToTarget();
                    mCountTimer.cancel();
                }
            }, 10000);

//        } catch (Exception e) {
//            e.printStackTrace();
        } catch (RuntimeException e) {
            HandleResponseCode.onHandle(this, RECORD_DENIED_STATUS, false);
            cancelTimer();
            dismissDialog();
            if (mThread != null) {
                mThread.exit();
                mThread = null;
            }
//            if (myRecAudioFile != null) {
//                myRecAudioFile.delete();
//            }
//            recorder.release();
//            recorder = null;
        }


        mThread = new ObtainDecibelThread();
        mThread.start();

    }

    public void dismissDialog() {
        if (recordIndicator != null) {
            recordIndicator.dismiss();
        }
    }


    public class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (AmrRecorder.getInstance().isRecordNull() || !running) {
                    break;
                }
                try {
                    //根据音量大小发送并显示对应的
                    int x = AmrRecorder.getInstance().getMaxAmplitude();
                    if (x != 0) {
                        int f = (int) (10 * Math.log(x) / Math.log(10));
                        if (f < 6) {
                            mVolumeHandler.sendEmptyMessage(0);
                        } else if (f < 12) {
                            mVolumeHandler.sendEmptyMessage(1);
                        } else if (f < 18) {
                            mVolumeHandler.sendEmptyMessage(2);
                        } else if (f < 24) {
                            mVolumeHandler.sendEmptyMessage(3);
                        } else if (f < 30) {
                            mVolumeHandler.sendEmptyMessage(4);
                        } else if (f < 36) {
                            mVolumeHandler.sendEmptyMessage(5);
                        } else {
                            mVolumeHandler.sendEmptyMessage(6);
                        }
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class MyHandler extends Handler {
        private final WeakReference<View> lButton;

        public MyHandler(View button) {
            lButton = new WeakReference<>(button);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            View controller = lButton.get();
            if (controller != null) {
                switch (msg.what) {
                    case START_RECORD:
                        mAm = (AudioManager) getApplicationContext().getSystemService(
                                Context.AUDIO_SERVICE);
                        vIsActive = mAm.isMusicActive();
                        if (!vIsActive) {
                            if (mIsPressed) {
                                initDialogAndStartRecord();
                            }
                        } else {
                            mListener = new MyOnAudioFocusChangeListener();
                            int result = mAm.requestAudioFocus(mListener,
                                    AudioManager.STREAM_MUSIC,
                                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                            if (mIsPressed) {
                                initDialogAndStartRecord();
                            }
                        }

                        break;
                }
            }
        }
    }

    public class ShowVolumeHandler extends Handler {

        private final WeakReference<View> mButton;

        public ShowVolumeHandler(View button) {
            mButton = new WeakReference<>(button);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            View controller = mButton.get();
            if (controller != null) {
                int restTime = msg.getData().getInt("restTime", -1);
                // 若restTime>0, 进入倒计时
                if (restTime > 0) {
                    mTimeUp = true;
                    android.os.Message msg1 = mVolumeHandler.obtainMessage();
                    msg1.what = 60 - restTime + 1;
                    Bundle bundle = new Bundle();
                    bundle.putInt("restTime", restTime - 1);
                    msg1.setData(bundle);
                    //创建一个延迟一秒执行的HandlerMessage，用于倒计时
                    mVolumeHandler.sendMessageDelayed(msg1, 1000);
                    // 倒计时结束，发送语音, 重置状态
                } else if (restTime == 0) {
                    finishRecord();
                    send.setPressed(false);
                    mTimeUp = false;
                    // restTime = -1, 一般情况
                } else {
                    // 没有进入倒计时状态
                    if (!mTimeUp) {
                        // 进入倒计时
                    } else {
                        if (msg.what == CANCEL_RECORD) {
                            if (!mIsPressed) {
                                cancelRecord();
                            }
                        }
                    }
                    mVolumeIv.setImageResource(res[msg.what]);
                }
            }
        }
    }

    public void toListBottom() {
        listView.getRefreshableView().setSelection(listView.getBottom());

    }

    //TODO 加载设备语音信息
    public void loadIntoList() {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        dataList.clear();

        dataList = DeviceVoiceHandler.getAllValue(device_id);
        LogUtil.e("device:" + device_id + "      " + dataList.size()+"**********************************************");
        if (dataList.size() > 5) {
            listView.getRefreshableView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            listView.getRefreshableView().setStackFromBottom(true);
        }
        for (DeviceVoiceEntity entity:
        dataList) {
            LogUtil.e(""+entity.toString());
        }
        LogUtil.e("**********************************************");
        adapter.setDataList(dataList);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.getRefreshableView().setSmoothScrollbarEnabled(true);
        //TODO 加载完成后 获取对应的用户名称，刷新显示
        getContacts();

    }


    private List<ContactsResultBean.ResultsBean.ContactsBean> contacts = new ArrayList<>();

    private HashMap<String, String> nameMap = new HashMap<>();

    private void getContacts() {
        if(!NetUtil.isNetworkAvailable(this)){
            Toast.makeText(this, "网络连接不可用!", Toast.LENGTH_SHORT).show();
            //TODO 若保存有临时数据，使用临时数据
            String val = TempHandler.getValue("contacts");
            if(val!=null && val.startsWith("{") && val.endsWith("}")){
                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                if (!jsonObject.has("code") && val.length() > 2) {
                    JsonObject result1 = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
                    if (device_id.equals(result1.get("IMEI").getAsString())) {
                        JsonArray jsonArray = result1.getAsJsonArray(ConstantClassFunction.CONTACTS);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject object = jsonArray.get(i).getAsJsonObject();
                            ContactsResultBean.ResultsBean.ContactsBean contactsBean = new ContactsResultBean.ResultsBean.ContactsBean();

                            if (object.has("Name")) {
                                contactsBean.setName(object.get("Name").getAsString());
                            }

                            if (object.has("Userid")) {
                                contactsBean.setUserid(object.get("Userid").getAsString());
                            }
                            //
                            if (object.has("App") && "1".equals(object.get("App").getAsString())) {

                                nameMap.put(object.get("Userid").getAsString() + "Name",
                                        (object.get("Name") != null ? object.get("Name").getAsString() : object.get("Userid").getAsString()
                                                //TODO 若存在不含名字这个字段，则使用Userid进行替代
                                        ));
                                nameMap.put(object.get("Userid").getAsString() + "Image",
                                        (object.get("Image") != null ? object.get("Image").getAsString() : "8"//TODO
                                                //TODO 若存在不含图像这个字段，则使用 默认的 自定义头像 进行替代*****

                                                //TODO 2017-05-05发现存在Image字段不存在的情况
                                                //TODO 请求链接 http://app.quanjiakan.com/device/service?code=childWatch&type=contracts&imei=355637050066828
                                                //TODO 返回的数据 {"Results":{"Category":"Contacts","Contacts":[{"Admin":"0","App":"1","Id":"984","Image":"8","Name":"%E5%86%AF%E5%B7%A9","Tel":"13650703987","Userid":"11178"},{"Admin":"0","App":"1","Id":"986","Image":"0","Name":"%E7%88%B8%E7%88%B8","Tel":"15218293347","Userid":"13469"},{"Admin":"0","App":"1","Id":"1015","Image":"1","Name":"%E5%A6%88%E5%A6%88","Tel":"15820233638","Userid":"11825"},{"Admin":"1","App":"1","Id":"1025","Image":"8","Name":"%E5%B0%8FA","Tel":"13432992552","Userid":"13625"},{"Admin":"0","App":"0","Id":"1038","Image":"8","Name":"%E5%B0%8F%E6%98%8E","Tel":"18718717141","Userid":"13469"},{"Admin":"0","App":"1","Id":"1050","Name":"%E7%88%B7%E7%88%B7","Tel":"13802735616","Userid":"11931"}],"IMEI":"355637050066828","Num":6}}
                                        ));
                            }
                        }
                        adapter.setNameMap(nameMap);

                        adapter.notifyDataSetChanged();

//                                listView.onRefreshComplete();
                    }
                }
            }
            return ;
        }
        nameMap.clear();
        com.quanjiakanuser.http.MyHandler.putTask(ChildVoiceChatActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                TempHandler.insertValue("contacts",val);
                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                if (!jsonObject.has("code") && val.length() > 2) {
                    JsonObject result1 = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
                    if (device_id.equals(result1.get("IMEI").getAsString())) {
                        JsonArray jsonArray = result1.getAsJsonArray(ConstantClassFunction.CONTACTS);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject object = jsonArray.get(i).getAsJsonObject();
                            ContactsResultBean.ResultsBean.ContactsBean contactsBean = new ContactsResultBean.ResultsBean.ContactsBean();

                            if (object.has("Name")) {
                                contactsBean.setName(object.get("Name").getAsString());
                            }

                            if (object.has("Userid")) {
                                contactsBean.setUserid(object.get("Userid").getAsString());
                            }
                            //
                            if (object.has("App") && "1".equals(object.get("App").getAsString())) {

                                nameMap.put(object.get("Userid").getAsString() + "Name",
                                        (object.get("Name") != null ? object.get("Name").getAsString() : object.get("Userid").getAsString()
                                                //TODO 若存在不含名字这个字段，则使用Userid进行替代
                                        ));
                                nameMap.put(object.get("Userid").getAsString() + "Image",
                                        (object.get("Image") != null ? object.get("Image").getAsString() : "8"//TODO
                                                //TODO 若存在不含图像这个字段，则使用 默认的 自定义头像 进行替代*****

                                                //TODO 2017-05-05发现存在Image字段不存在的情况
                                                //TODO 请求链接 http://app.quanjiakan.com/device/service?code=childWatch&type=contracts&imei=355637050066828
                                                //TODO 返回的数据 {"Results":{"Category":"Contacts","Contacts":[{"Admin":"0","App":"1","Id":"984","Image":"8","Name":"%E5%86%AF%E5%B7%A9","Tel":"13650703987","Userid":"11178"},{"Admin":"0","App":"1","Id":"986","Image":"0","Name":"%E7%88%B8%E7%88%B8","Tel":"15218293347","Userid":"13469"},{"Admin":"0","App":"1","Id":"1015","Image":"1","Name":"%E5%A6%88%E5%A6%88","Tel":"15820233638","Userid":"11825"},{"Admin":"1","App":"1","Id":"1025","Image":"8","Name":"%E5%B0%8FA","Tel":"13432992552","Userid":"13625"},{"Admin":"0","App":"0","Id":"1038","Image":"8","Name":"%E5%B0%8F%E6%98%8E","Tel":"18718717141","Userid":"13469"},{"Admin":"0","App":"1","Id":"1050","Name":"%E7%88%B7%E7%88%B7","Tel":"13802735616","Userid":"11931"}],"IMEI":"355637050066828","Num":6}}
                                        ));
                            }
                        }
                        adapter.setNameMap(nameMap);

                        adapter.notifyDataSetChanged();

//                                listView.onRefreshComplete();
                    }
                } else {
                    BaseApplication.getInstances().toast(ChildVoiceChatActivity.this, "未查询到数据");
                }
            }
        }, HttpUrls.getContactsList() + "&imei=" + device_id, null, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonVoiceData(CommonVoiceData msg) {
        switch (msg.getType()) {
            case NattyProtocolFilter.DISPLAY_VOICE_PLAY: {
                try {
                    byte[] data = msg.getData();
                    String len = msg.getLen();
                    String fromId = msg.getFromId();
                    String longDeviceId = Long.parseLong(device_id, 16) + "";
                    if ((device_id.equals(fromId) || longDeviceId.equals(fromId)) &&
                            Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        loadIntoList();
                    } else if (BaseApplication.getInstances().getUser_id().equals(fromId)) {
                        BaseApplication.getInstances().toast(ChildVoiceChatActivity.this, "设备未开机!");
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonNattyData(CommonNattyData msg) {
        switch (msg.getType()) {
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_RESULT: {
                String data = msg.getData();
                WatchCommonResult result = (WatchCommonResult) SerialUtil.jsonToObject(data, new TypeToken<WatchCommonResult>() {
                }.getType());
                if (result != null && "200".equals(result.getResult().getCode()) && AudioFileFunc.getRecordFilePath() != null) {
                    LogUtil.e("发送语音成功");
                    //TODO 当语音发送成功后，保存该次记录
                    DeviceVoiceEntity entity = new DeviceVoiceEntity();
                    entity.setDevice_id(device_id);
                    entity.setUserid(BaseApplication.getInstances().getUser_id());
                    entity.setDirection(TableInfo_ValueStub.DEVICE_VOICE_INFO_DIRECTION_SEND);
                    entity.setTime(AudioFileFunc.getTime() + "");
                    entity.setVoice_path(AudioFileFunc.getRecordFilePath());
                    entity.setReadFlag();//设置为已读（自己发送的）
                    DeviceVoiceHandler.insertValue(entity);

                    dataList.add(entity);
                    //刷新Adapter

                    adapter.notifyDataSetChanged();
                    toListBottom();
                } else if (result != null && "10001".equals(result.getResult().getCode())) {
                    BaseApplication.getInstances().toast(ChildVoiceChatActivity.this, "设备不在线");
                } else {
                    LogUtil.e("发送语音失败");
                }
                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_CONTROL_STEP: {
                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_ERROR: {
                break;
            }
            case NattyProtocolFilter.DISPLAY_VOICE_SEND_RESULT: {
                ////0 Success , 1 Failed
                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_SUCCESS: {
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == keyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
        }
        return super.onKeyDown(keyCode, keyEvent);

    }
}
