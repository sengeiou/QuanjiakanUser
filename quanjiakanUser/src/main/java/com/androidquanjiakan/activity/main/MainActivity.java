package com.androidquanjiakan.activity.main;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.common.CommonWebEntryActivity;
import com.androidquanjiakan.activity.index.MainEntryMapActivity;
import com.androidquanjiakan.activity.index.doctor.ConsultantEnterActivity_Classify;
import com.androidquanjiakan.activity.index.free.FreeInquiryActivity_CreatePatientProblem;
import com.androidquanjiakan.activity.index.housekeeper.HouseKeeperListActivity;
import com.androidquanjiakan.activity.index.missing.NoticeMissingActivity;
import com.androidquanjiakan.activity.index.phonedocter.ConsultantActivity_PhoneDoctor;
import com.androidquanjiakan.activity.setting.SettingEntryActivity;
import com.androidquanjiakan.activity.setting.message.ProblemListActivity;
import com.androidquanjiakan.activity.video.VideoEntryActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.business.IWatchBroadcaseSaver;
import com.androidquanjiakan.business.WatchBroadcaseBindImpl;
import com.androidquanjiakan.business.WatchBroadcaseSOSImpl;
import com.androidquanjiakan.business.WatchBroadcaseUnwearImpl;
import com.androidquanjiakan.business.WatchEfenceBroadcaseDeleteImpl;
import com.androidquanjiakan.business.WatchEfenceBroadcaseOutBoundImpl;
import com.androidquanjiakan.business.WatchEfenceBroadcaseSaverImpl;
import com.androidquanjiakan.entity.CommonBindResult;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.entity.VersionInfoEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.interfaces.IDownloadCallback;
import com.androidquanjiakan.service.DevicesService;
import com.androidquanjiakan.service.DownloadService;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.MultiThreadAsyncTask;
import com.androidquanjiakan.view.SlidingMenu;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.api.BasicCallback;

public class MainActivity extends TabActivity implements View.OnClickListener {

    //	private TextView tv_title;
    private TabHost tabHost = null;
    private RelativeLayout rl_title;

    private final String MAIN = "首页";
    private final String CHAT = "消息";
    private final String SETTING = "我的";
    private final String VIDEO = "视频";

    public final static String PARAMS_STOP = "stop";
    public final static String PARAMS_FORCE = "force";
    private int stopDownloadService = 0;
    private boolean forceUpdate = false;
    private final int STOP = 0;
    private final int DO_NOTHING = 1;

    private SlidingMenu mSm;
    private RelativeLayout main_menu;
    private LinearLayout docter;
    private LinearLayout inquiry;
    private LinearLayout child_docter;
    private LinearLayout housekeeper;
    private LinearLayout child_missing;
    private LinearLayout shop;
    private LinearLayout go_home;

    private boolean onShotMap;
    private String currentID;
    private double currentLat;
    private double currentLng;

    private IWatchBroadcaseSaver watchEfenceSaver = new WatchEfenceBroadcaseSaverImpl();
    private IWatchBroadcaseSaver watchEfenceDelete = new WatchEfenceBroadcaseDeleteImpl();
    private IWatchBroadcaseSaver watchEfenceOutBound = new WatchEfenceBroadcaseOutBoundImpl();
    private IWatchBroadcaseSaver watchSos = new WatchBroadcaseSOSImpl();
    private IWatchBroadcaseSaver watchUnwear = new WatchBroadcaseUnwearImpl();
    private IWatchBroadcaseSaver watchBind = new WatchBroadcaseBindImpl();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    private Dialog bindComfirmDialog;

    public void showComfirmBindDialog(final long fromid,
                                      final String imei,
                                      final String phoneNumber,
                                      final String name,
                                      final String msgID) {
        //TODO 控制Dialog的弹出
        if (bindComfirmDialog != null && bindComfirmDialog.isShowing()) {
            return;
        }
        LogUtil.e("------------bind---------------------");
        bindComfirmDialog = new Dialog(BaseApplication.getInstances().getCurrentActivity(), R.style.dialog_loading);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bind_confirm, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("绑定申请");

        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setText(phoneNumber + "用户申请绑定" + imei + "设备");

        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        btn_cancel.setText("拒绝");
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindComfirmDialog.dismiss();
                try {//拒绝同样需要走一次该消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("IMEI", imei);
                    jsonObject.put("Category", "BindConfirmReq");
                    jsonObject.put("MsgId", msgID);
                    jsonObject.put("Answer", "Reject");
                    long devid = Long.parseLong(imei, 16);
                    BaseApplication.getInstances().getNattyClient().ntyBindConfirmReqClient(fromid,
                            devid, Integer.parseInt(msgID), jsonObject.toString().getBytes(), jsonObject.toString().getBytes().length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
        btn_confirm.setText("同意");
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindComfirmDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("IMEI", imei);
                    jsonObject.put("Category", "BindConfirmReq");
                    jsonObject.put("MsgId", msgID);
                    jsonObject.put("Answer", "Agree");
                    long devid = Long.parseLong(imei, 16);
                    BaseApplication.getInstances().getNattyClient().ntyBindConfirmReqClient(fromid,
                            devid, Integer.parseInt(msgID), jsonObject.toString().getBytes(), jsonObject.toString().getBytes().length);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        WindowManager.LayoutParams lp = bindComfirmDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(BaseApplication.getInstances(), 280);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        bindComfirmDialog.setContentView(view, lp);
        bindComfirmDialog.setCanceledOnTouchOutside(false);
        bindComfirmDialog.show();
    }

    public void toggleMenu() {
        if (mSm != null) {
            mSm.toggleMenu();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        BaseApplication.getInstances().setCurrentActivity(this);
        BaseApplication.getInstances().setMainActivity(this);

        initSlideMenu();
        init();

        JMessageClient.registerEventReceiver(this);
        /**
         * 恢复Push通知
         */
        JPushInterface.resumePush(BaseApplication.getInstances());
        /**
         * 清除原有的通知
         */
        JPushInterface.clearAllNotifications(BaseApplication.getInstances());
//        EventBus.getDefault().register(this);
        EventBus.getDefault().register(this);

        startNDKService();
    }

    public void checkNetAndLogin() {
        JMessageLogin();
        checkUpdate();
    }

    private static boolean hasStart = false;

    public void startNDKService() {
        if (LogUtil.BINDDEVICE_OFF) {

        } else {
            if (!hasStart) {
                Intent intent = new Intent(MainActivity.this, DevicesService.class);
                startService(intent);
                hasStart = true;
            } else if (hasStart) {
                Intent intent = new Intent(MainActivity.this, DevicesService.class);
                stopService(intent);
                startService(intent);
            }
        }
    }

    protected void JMessageLogin() {
        JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + QuanjiakanSetting.getInstance().getUserId() + "", CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
            @Override
            public void gotResult(int arg0, String arg1) {
                LogUtil.i("Jmessage_login   " + arg0);
            }
        });
        retryCount = 0;
        setAlias();
    }

    private int retryCount = 0;

    public void setAlias() {
        if (retryCount == 4) {
            return;
        }
        retryCount++;
        JPushInterface.setAlias(MainActivity.this, CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), new TagAliasCallback() {
            @Override
            public void gotResult(int result, String s, Set<String> set) {
                LogUtil.e("JPushInterface.setAlias result:" + result + "    message:" + s);
                if (result == 0) {

                } else {
                    setAlias();
                }
            }
        });
    }

    public void resetAlias() {
        if (retryCount == 4) {
            return;
        }
        retryCount++;
        JPushInterface.setAlias(MainActivity.this, "", new TagAliasCallback() {
            @Override
            public void gotResult(int result, String s, Set<String> set) {
                LogUtil.e("JPushInterface.setAlias result:" + result + "    message:" + s);
                if (result == 0) {

                } else {
                    setAlias();
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());

        JPushInterface.onPause(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);

        JPushInterface.onResume(this);

        stopDownloadService = getIntent().getIntExtra(PARAMS_STOP, 0);
        forceUpdate = getIntent().getBooleanExtra(PARAMS_FORCE, false);

        if (stopDownloadService == STOP) {
            Intent intent = new Intent(MainActivity.this, DownloadService.class);
            stopService(intent);
        }

        checkNetAndLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//		unregisterReceiver(receiver);
        JMessageClient.unRegisterEventReceiver(this);
        retryCount = 0;
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void onEventMainThread(LoginStateChangeEvent event) {
        LoginStateChangeEvent.Reason reason = event.getReason();
        switch (reason) {
            case user_logout:
                QuanjiakanDialog.getInstance().getLogoutDialog(this);
                break;
        }
    }

    public void onEvent(MessageEvent event) {

    }

    private void initSlideMenu() {
        mSm = (SlidingMenu) findViewById(R.id.sliding_menu);
        mSm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        main_menu = (RelativeLayout) findViewById(R.id.main_menu);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) main_menu.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels / 2;
        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        main_menu.setLayoutParams(params);

//        docter = (LinearLayout) findViewById(R.id.docter);
        inquiry = (LinearLayout) findViewById(R.id.inquiry);
        child_docter = (LinearLayout) findViewById(R.id.child_docter);
        housekeeper = (LinearLayout) findViewById(R.id.housekeeper);
        child_missing = (LinearLayout) findViewById(R.id.child_missing);
        shop = (LinearLayout) findViewById(R.id.shop);
        go_home = (LinearLayout) findViewById(R.id.go_home);
//        docter.setOnClickListener(this);
        inquiry.setOnClickListener(this);
        child_docter.setOnClickListener(this);
        housekeeper.setOnClickListener(this);
        child_missing.setOnClickListener(this);
        shop.setOnClickListener(this);
        go_home.setOnClickListener(this);
        //********************************************************
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.docter: {
                Intent intent = new Intent(this, ConsultantEnterActivity_Classify.class);
                startActivity(intent);
                break;
            }
            case R.id.inquiry: {
                Intent intent = new Intent(this, FreeInquiryActivity_CreatePatientProblem.class);
                startActivity(intent);
                break;
            }
            case R.id.child_docter: {
                Intent phone = new Intent(this, ConsultantActivity_PhoneDoctor.class);
                phone.putExtra(ConsultantActivity_PhoneDoctor.PARAMS_CLASS, "0");
                startActivity(phone);
                break;
            }
            case R.id.housekeeper: {
                Intent intent = new Intent(this, HouseKeeperListActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.child_missing: {
                Intent intent = new Intent(this, NoticeMissingActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.shop: {
                //跳转到网页版商城
                Intent intent = new Intent(MainActivity.this, CommonWebEntryActivity.class);
                intent.putExtra(BaseConstants.PARAMS_URL, "http://www.quanjiakan.net/");
                intent.putExtra(BaseConstants.PARAMS_TITLE, "商城");
                startActivity(intent);
                break;
            }
            case R.id.go_home: {
                Intent intent = new Intent(MainActivity.this, CommonWebEntryActivity.class);
                intent.putExtra(BaseConstants.PARAMS_URL, "http://www.baobeihuijia.com/");
                intent.putExtra(BaseConstants.PARAMS_TITLE, "宝贝回家");
                startActivity(intent);
                break;
            }
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String jumpType = intent.getStringExtra(BaseConstants.PARAMS_JUMP_TYPE);
        currentID = intent.getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        currentLat = intent.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, -200);
        currentLng = intent.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, -200);
        if (currentID != null &&
                currentID.length() == 15 &&
                currentLat != -200 &&
                currentLng != -200) {
            onShotMap = true;
            init();

            if (BaseApplication.getInstances().getMainEntryMapActivity() != null) {
                Intent in1 = new Intent(this, MainEntryMapActivity.class);
                in1.putExtra(BaseConstants.PARAMS_DEVICE_ID, currentID);
                in1.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, currentLat);
                in1.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, currentLng);
                BaseApplication.getInstances().getMainEntryMapActivity().SpecificPoint(intent);
            }

        } else if (jumpType != null && BaseConstants.PARAMS_JUMP_TYPE_BIND_REQUEST.equals(jumpType)) {
            final long fromid = intent.getLongExtra(BaseConstants.PARAMS_JUMP_TYPE_BIND_REQUEST_FROMID, -1);
            final String imei = intent.getStringExtra(BaseConstants.PARAMS_JUMP_TYPE_BIND_REQUEST_IMEI);
            final String phone = intent.getStringExtra(BaseConstants.PARAMS_JUMP_TYPE_BIND_REQUEST_PROPOSER);
            final String name = intent.getStringExtra(BaseConstants.PARAMS_JUMP_TYPE_BIND_REQUEST_USERNAME);
            final String msgID = intent.getStringExtra(BaseConstants.PARAMS_JUMP_TYPE_BIND_REQUEST_MSGID);
            if (fromid != -1 &&
                    imei != null &&
                    phone != null &&
                    name != null &&
                    msgID != null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //TODO 不增加延迟执行发现其实还是走了调用，但是Dialog没有显示出来
                        ((WatchBroadcaseBindImpl) watchBind).showComfirmBindDialog2(fromid, imei, phone, name, msgID);
                    }
                },1000);
            }
        }
    }

    /**
     *
     */
    protected void init() {

        if (tabHost == null) {
            tabHost = getTabHost();
        } else {
            tabHost.clearAllTabs();
        }
        /**
         *
         */
        View view1 = this.getLayoutInflater().inflate(R.layout.layout_tabhost, null);
        final TextView tv1 = (TextView) view1.findViewById(R.id.ft_tv);
        tv1.setText(MAIN);
        tv1.setTextColor(getResources().getColor(R.color.color_title_green));
        tv1.setTextSize(11);
        ImageView ic1 = (ImageView) view1.findViewById(R.id.ft_ic);
        ic1.setImageResource(R.drawable.selector_health);

        TabSpec spec1 = tabHost.newTabSpec(MAIN);
        spec1.setIndicator(view1);
//		Intent in1 = new Intent(this, HealthActivity.class);
        Intent in1 = new Intent(this, MainEntryMapActivity.class);
        if (onShotMap) {
            onShotMap = false;
            in1.putExtra(BaseConstants.PARAMS_DEVICE_ID, currentID);
            in1.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, currentLat);
            in1.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, currentLng);
        }
        spec1.setContent(in1);

        /**
         *
         */
        View view4 = this.getLayoutInflater().inflate(R.layout.layout_tabhost, null);
        final TextView tv4 = (TextView) view4.findViewById(R.id.ft_tv);
        tv4.setText(VIDEO);
        tv4.setTextSize(11);
        ImageView ic4 = (ImageView) view4.findViewById(R.id.ft_ic);
        ic4.setImageResource(R.drawable.selector_video);

        TabSpec spec4 = tabHost.newTabSpec(VIDEO);
        spec4.setIndicator(view4);
        Intent in4 = new Intent(this, VideoEntryActivity.class);
        spec4.setContent(in4);

        /**
         *
         */
        View view2 = this.getLayoutInflater().inflate(R.layout.layout_tabhost, null);
        final TextView tv2 = (TextView) view2.findViewById(R.id.ft_tv);
        tv2.setText(CHAT);
        tv2.setTextSize(11);
        ImageView ic2 = (ImageView) view2.findViewById(R.id.ft_ic);
        ic2.setImageResource(R.drawable.selector_services);

        TabSpec spec2 = tabHost.newTabSpec(CHAT);
        spec2.setIndicator(view2);
//		Intent in2 = new Intent(this, ServicesActivity.class);
        Intent in2 = new Intent(this, ProblemListActivity.class);
        in2.putExtra(ProblemListActivity.PARAMS_ENTRY_TYPE, 1);
        spec2.setContent(in2);
        /**
         *
         */
        View view3 = this.getLayoutInflater().inflate(R.layout.layout_tabhost, null);
        final TextView tv3 = (TextView) view3.findViewById(R.id.ft_tv);
        tv3.setText(SETTING);
        tv3.setTextSize(11);
        ImageView ic3 = (ImageView) view3.findViewById(R.id.ft_ic);
        ic3.setImageResource(R.drawable.selector_me);

        TabSpec spec3 = tabHost.newTabSpec(SETTING);
        spec3.setIndicator(view3);
        Intent in3 = new Intent(this, SettingEntryActivity.class);
        spec3.setContent(in3);
        tabHost.addTab(spec1);
        tabHost.addTab(spec4);//视频
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String arg0) {
//				tv_title.setText(arg0);
                setTextColorLine(arg0, tv1, tv2, tv3, tv4);
            }
        });
    }

    public void setTextColorLine(String name, TextView textView1, TextView textView2, TextView textView3) {
        if (SETTING.equals(name)) {
            textView3.setTextColor(getResources().getColor(R.color.color_title_green));
            textView2.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView1.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
        } else if (CHAT.equals(name)) {
            textView3.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView2.setTextColor(getResources().getColor(R.color.color_title_green));
            textView1.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
        } else if (MAIN.equals(name)) {
            textView3.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView2.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView1.setTextColor(getResources().getColor(R.color.color_title_green));
        }
    }

    public void setTextColorLine(String name, TextView textView1, TextView textView2, TextView textView3, TextView textView4) {
        if (SETTING.equals(name)) {
            textView4.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView3.setTextColor(getResources().getColor(R.color.color_title_green));
            textView2.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView1.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
        } else if (CHAT.equals(name)) {
            textView4.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView3.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView2.setTextColor(getResources().getColor(R.color.color_title_green));
            textView1.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
        } else if (MAIN.equals(name)) {
            textView4.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView3.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView2.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView1.setTextColor(getResources().getColor(R.color.color_title_green));
        } else if (VIDEO.equals(name)) {
            textView4.setTextColor(getResources().getColor(R.color.color_title_green));
            textView3.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView2.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
            textView1.setTextColor(getResources().getColor(R.color.colorAlphaBlack_77));
        }
    }

    long lastBack = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                long span = System.currentTimeMillis() - lastBack;
                if (span > 1500) {
                    Toast.makeText(MainActivity.this, "再次点击退出", Toast.LENGTH_SHORT).show();
                    lastBack = System.currentTimeMillis();
                    return false;
                }
                //退出
                finish();
            }
            return false;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    private VersionInfoEntity versionInfoEntity;
    /**
     * 检查当前版本
     */
    PackageManager packageManager = null;
    PackageInfo packageInfo = null;

    public void checkUpdate() {
        /**
         * 检查今天是否检查了更新，未检查更新则调用更新接口
         */
        if (!BaseApplication.getInstances().isCheckedUpdateToday()) {
            packageManager = getPackageManager();
            try {
                packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            MyHandler.putTask(MainActivity.this, new Task(new HttpResponseInterface() {
                @Override
                public void handMsg(String val) {
                    //
                    if (val != null && val.length() > 0 && !"null".equals(val)) {
                        versionInfoEntity = (VersionInfoEntity) SerialUtil.jsonToObject(val, new TypeToken<VersionInfoEntity>() {
                        }.getType());
                        if (versionInfoEntity != null) {
                            LogUtil.e(versionInfoEntity.getUrl());
                            if (Integer.parseInt(versionInfoEntity.getCode()) > packageInfo.versionCode) {
                                /**
                                 * 弹出更新对话框
                                 */
                                if (versionInfoEntity.getForced_update() != null && "1".equals(versionInfoEntity.getForced_update())) {
                                    //强制更新
                                    showUpdateDialog(true);
                                } else {
                                    //非强制更新
                                    showUpdateDialog(false);
                                }
                            } else {
                                BaseApplication.getInstances().updateCheckTime();
                            }
                        } else {

                        }
                    } else {
                    }
                }
            }, HttpUrls.getVersion(), null, Task.TYPE_GET_STRING_NOCACHE, null));
        } else {
            LogUtil.e("今天已经检查过更新了!");
        }
    }


    private Dialog updateDialog;
    private TextView title;
    private TextView content;
    private ProgressBar progressBar;
    private TextView rate;
    private TextView cancel;
    private TextView confirm;

    public void showUpdateDialog(final boolean isForce) {
        updateDialog = new Dialog(this, R.style.ShareDialog);
        updateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (updateDialog != null && updateDialog.isShowing()) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_new, null);
        view.setBackgroundResource(R.drawable.selecter_hollow_white);
        title = (TextView) view.findViewById(R.id.tv_title);
        title.setText("版本更新");

        content = (TextView) view.findViewById(R.id.tv_content);
        content.setVisibility(View.VISIBLE);
        if (isForce) {
            SpannableString spannableString = new SpannableString("当前版本:" + packageInfo.versionName + "\n\n更新版本:" + versionInfoEntity.getVersion() + "\n\t(" + "该版本为重要更新版本,若不更新将无法使用" + ")");
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 0,
                    ("当前版本:" + packageInfo.versionName + "\n\n更新版本:" + versionInfoEntity.getVersion() + "\n\t(").length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                    ("当前版本:" + packageInfo.versionName + "\n\n更新版本:" + versionInfoEntity.getVersion() + "\n\t(").length(),
                    ("当前版本:" + packageInfo.versionName + "\n\n更新版本:" + versionInfoEntity.getVersion() + "\n\t(" + "该版本为重要更新版本,若不更新将无法使用").length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")),
                    ("当前版本:" + packageInfo.versionName + "\n\n更新版本:" + versionInfoEntity.getVersion() + "\n\t(" + "该版本为重要更新版本,若不更新将无法使用").length(),
                    ("当前版本:" + packageInfo.versionName + "\n\n更新版本:" + versionInfoEntity.getVersion() + "\n\t(" + "该版本为重要更新版本,若不更新将无法使用" + ")").length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//			content.setText("当前版本:"+packageInfo.versionName+"\n\n更新版本:"+versionInfoEntity.getVersion()+ "\n\t(该版本为重要更新版本,若不更新将无法使用)");
            content.setText(spannableString);
        } else {
            content.setText("当前版本:" + packageInfo.versionName + "\n\n更新版本:" + versionInfoEntity.getVersion());
        }

        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 15, 0));
        progressBar.setMax(100);

        rate = (TextView) view.findViewById(R.id.rate);
        rate.setVisibility(View.GONE);

        cancel = (TextView) view.findViewById(R.id.btn_cancel);
        cancel.setText(getString(R.string.cancel));
        cancel.setVisibility(View.VISIBLE);

        confirm = (TextView) view.findViewById(R.id.btn_confirm);
        confirm.setText("确定");
        confirm.setVisibility(View.VISIBLE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateTask != null) {
                    updateTask.stopSubThread();
                    updateTask = null;
                }

                if (isForce) {
                    updateDialog.dismiss();
                    System.exit(0);
                } else {
                    updateDialog.dismiss();
                    BaseApplication.getInstances().updateCheckTime();
                }
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始下载
//				updateDialog.dismiss();
                /**
                 * 切换显示
                 */
//				content.setVisibility(View.GONE);
//				progressBar.setVisibility(View.VISIBLE);

                confirm.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);

                if (updateTask != null) {
                    updateTask.stopSubThread();
                    updateTask = null;
                }
                updateTask = new MultiThreadAsyncTask(MainActivity.this,
                        versionInfoEntity.getUrl(),
//						"http://quanjiakan.com/app/quanjiakanUser-release.apk",
                        callback, updateDialog);

//                content.setVisibility(View.GONE);
                content.setText("正在更新版本:" + versionInfoEntity.getVersion() + "\n");
                progressBar.setVisibility(View.VISIBLE);
                rate.setVisibility(View.VISIBLE);
                updateTask.execute("");
            }
        });
        //
        WindowManager.LayoutParams lp = updateDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(BaseApplication.getInstances(), 300);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        updateDialog.setContentView(view, lp);
        updateDialog.setCanceledOnTouchOutside(false);

        updateDialog.show();
    }

    /**
     * APP更新
     * <p>
     * 弹出对话框进行提示，根据是否是强制更新，
     */
    MultiThreadAsyncTask updateTask;
    IDownloadCallback callback = new IDownloadCallback() {
        @Override
        public void updateProgress(int progress, String rate) {
            if (progressBar != null && MainActivity.this.rate != null) {
                progressBar.setProgress(progress);
                MainActivity.this.rate.setText(rate);
            }
        }
    };

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonNattyData(CommonNattyData msg) {
        switch (msg.getType()) {
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_COMMON_BROADCAST: {
                //{"Id":"2","Results":{"IMEI":"355637053995130","Category":"Efence","Action":"Add","Index":"2","Efence":{"Num":"4","Points":["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]}}}
                String data = msg.getData();
                if (data.length() > (data.lastIndexOf("}") + 1)) {
                    data = data.substring(0, data.lastIndexOf("}") + 1);
                }
                watchEfenceSaver.doBusiness(data, BaseApplication.getInstances().getCurrentActivity());
                watchEfenceDelete.doBusiness(data, BaseApplication.getInstances().getCurrentActivity());
                watchEfenceOutBound.doBusiness(data, BaseApplication.getInstances().getCurrentActivity());
                watchSos.doBusiness(data, BaseApplication.getInstances().getCurrentActivity());
                watchUnwear.doBusiness(data, BaseApplication.getInstances().getCurrentActivity());
                break;
            }
        }
    }

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonBindResult(CommonBindResult msg) {
        switch (msg.getType()) {
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_ADMIN_BIND_INFO: {
                try {
                    long fromid = msg.getFromid();
                    String jsonString = msg.getJson();

                    if (fromid == 0 || jsonString == null || jsonString.length() < 1) {

                    } else {
                        ////{"Results":{"IMEI":"352315052834187","Proposer":"18011935659","UserName":"爸爸","MsgId":"40"}}
                        JSONObject jsonObject = new JSONObject(jsonString);
                        if (jsonObject.has("Results") && jsonObject.getJSONObject("Results") != null) {
                            if (jsonObject.getJSONObject("Results").has("IMEI") &&
                                    jsonObject.getJSONObject("Results").has("Proposer") &&
                                    jsonObject.getJSONObject("Results").has("UserName") &&
                                    jsonObject.getJSONObject("Results").has("MsgId")) {
//                                showComfirmBindDialog(fromid,
//                                        jsonObject.getJSONObject("Results").getString("IMEI"),
//                                        jsonObject.getJSONObject("Results").getString("Proposer"),
//                                        jsonObject.getJSONObject("Results").getString("UserName"),
//                                        jsonObject.getJSONObject("Results").getString("MsgId"));

                                watchBind.doBusiness(jsonString + fromid, BaseApplication.getInstances().getCurrentActivity());
                            }
                        } else if (jsonObject.has("results") && jsonObject.getJSONObject("results") != null) {
                            if (jsonObject.getJSONObject("results").has("IMEI") &&
                                    jsonObject.getJSONObject("results").has("Proposer") &&
                                    jsonObject.getJSONObject("results").has("UserName") &&
                                    jsonObject.getJSONObject("results").has("MsgId")) {
//                                showComfirmBindDialog(fromid,
//                                        jsonObject.getJSONObject("results").getString("IMEI"),
//                                        jsonObject.getJSONObject("results").getString("Proposer"),
//                                        jsonObject.getJSONObject("results").getString("UserName"),
//                                        jsonObject.getJSONObject("results").getString("MsgId"));

                                watchBind.doBusiness(jsonString + fromid, BaseApplication.getInstances().getCurrentActivity());
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

}
