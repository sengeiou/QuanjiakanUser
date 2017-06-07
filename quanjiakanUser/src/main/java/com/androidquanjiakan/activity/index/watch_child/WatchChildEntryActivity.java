package com.androidquanjiakan.activity.index.watch_child;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.index.devices.DNAInfoActivity;
import com.androidquanjiakan.activity.index.watch.CallsTrafficQueryActivity;
import com.androidquanjiakan.activity.index.watch.ClassDisableActivity;
import com.androidquanjiakan.activity.index.watch.HealthStepsActivity;
import com.androidquanjiakan.activity.index.watch.WorkRestPlanActivity;
import com.androidquanjiakan.activity.setting.contact.MessageRecordActivity;
import com.androidquanjiakan.activity.setting.contact.WatchContactesActivity;
import com.androidquanjiakan.activity.setting.contact.WatchManagerActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.entity.CommonVoiceData;
import com.androidquanjiakan.entity.DNAInfoEntity;
import com.androidquanjiakan.entity.DNAInfoEntity_new;
import com.androidquanjiakan.entity.DisableEntity;
import com.androidquanjiakan.entity.DisableResultsEntity;
import com.androidquanjiakan.entity.WatchCommonResult;
import com.androidquanjiakan.entity.WatchGetConfig_results;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.GsonUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.view.MaterialBadgeTextView;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

import static com.androidquanjiakan.activity.index.MainEntryMapActivity.GET_BATTERY;
import static com.androidquanjiakan.activity.index.MainEntryMapActivity.GET_CONFIG;
import static com.androidquanjiakan.activity.index.MainEntryMapActivity.GET_LOCATION;
import static com.androidquanjiakan.activity.index.MainEntryMapActivity.GET_SIGNAL;

public class WatchChildEntryActivity extends BaseActivity implements View.OnClickListener {

    private String device_id;
    private String phone;

    private double mLat;
    private double mLon;

    private double mLatParam;
    private double mLonParam;

    private ImageButton ibtn_back;
    private ImageButton ibtn_menu;
    private TextView tv_title;
    private LinearLayout disconnected;
    private SwipeRefreshLayout fresh;

    private String deviceType;
    private String headimagePath;
    private String name;
    private static final int CLASS_DISABLE_STATUAS = 100;

    public static final String RESULT_TYPE = "TYPE";
    public static final int REQUEST_UNBIND = 1200;

    public static final int REQUEST_CHANGEDEFAULT = 1202;

    public static final int REQUEST_MANAGE = 1201;

    public static final int REQUEST_FRESH = 1202;
    public static final int REQUEST_CHILD_VOICE_CHAT = 1203;


    private int currentCommand = 0;
    private String watch_phoneNum;
    private MaterialBadgeTextView notice;
    private final int MSG_GET_CONFIG = 1000;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_GET_CONFIG:
                    getConfig();
                    break;
            }
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_watch_child_entry);
        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        phone = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_PHONE);
        mLat = getIntent().getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, -200);
        mLon = getIntent().getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, -200);
        deviceType = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_TYPE);
        headimagePath = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_IMAGEPATH);
        name = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_NAME);
        mLatParam = mLat;
        mLonParam = mLon;

        if (device_id == null || deviceType == null) {
            BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "传入参数异常!");
            finish();
            return;
        }

        initTitleBar();

        // 状态信息
        initStatusLine();

        initItems();
        //刷新上课禁用状态
        initClassDisable();
        initFunction();
        initBottomFunction();

        getDNA_FilePath();
    }


    private Timer configTimer;
    private TimerTask configTask;

    public void startConfigTimer(){
        stopConfigTimer();
        configTimer = new Timer();
        configTask = new TimerTask() {
            @Override
            public void run() {
                LogUtil.e("Timer Config");
                mHandler.sendEmptyMessage(MSG_GET_CONFIG);
            }
        };
        configTimer.schedule(configTask,0,60000);//TODO 每10分钟主动拉一次
    }

    public void stopConfigTimer(){
        if(configTimer!=null){
            configTimer.cancel();
            configTimer = null;
        }
    }


    public void getConfig() {
        try {
            if (!BaseApplication.getInstances().isSDKConnected()) {
                LogUtil.e("SDK 断联");
                BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "已与手表服务器断开连接");
                return;
            }
            currentCommand = GET_CONFIG;
            JSONObject jsonObject = new JSONObject();
            long devid = Long.parseLong(device_id, 16);
            jsonObject.put("IMEI", device_id);
            jsonObject.put("Action", "Get");
            jsonObject.put("Category", "Config");
            LogUtil.e("Config:" + jsonObject.toString());
            int result  = BaseApplication.getInstances().getNattyClient().ntyDataRouteClient(devid, jsonObject.toString().getBytes(), jsonObject.toString().length());
            LogUtil.e("Config Result:"+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initClassDisable() {
        getClassDisableReq();
    }

    private void getClassDisableReq() {
        if (!NetUtil.isNetworkAvailable(this)) {
            return;
        }
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                //{"Results":{"Category":"TimeTables","IMEI":"240207489224233264","TimeTables":[{"Afternoon":{"EndTime":"09:00:00","StartTime":"08:00:00","Status":"1"},"Daily":"(null)","Morning":{"EndTime":"09:00:00","StartTime":"08:00:00","Status":"1"}}]}}
                if (val != null && val.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(val);
                        if (jsonObject.has(ConstantClassFunction.ERR_MES) && jsonObject.getString(ConstantClassFunction.ERR_MES).equals("结果集为空")) {
                            ban_value.setText("关闭");
                        } else if (jsonObject.has(ConstantClassFunction.getRESULTS()) && jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).has(ConstantClassFunction.getCATEGORY()) && jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).getString(ConstantClassFunction.getCATEGORY()).equals(ConstantClassFunction.TIMETABLES)) {
                            DisableResultsEntity disableResultsEntity = GsonUtil.fromJson(val, DisableResultsEntity.class);
                            DisableEntity.Morning morning = disableResultsEntity.getResults().getTimeTables().get(0).getMorning();
                            DisableEntity.Afternoon afternoon = disableResultsEntity.getResults().getTimeTables().get(0).getAfternoon();
                            if (morning.getStatus().equals("1") || afternoon.getStatus().equals("1")) {
                                ban_value.setText("开启");
                            } else if (afternoon.getStatus().equals("0") || afternoon.getStatus().equals("0")) {
                                ban_value.setText("关闭");
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        }, HttpUrls.getTimeTablesData() + "&imei=" + device_id, null, Task.TYPE_GET_STRING_NOCACHE, null));

    }

    public void initTitleBar() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        if (name != null && name.contains("%")) {
            try {
                tv_title.setText(URLDecoder.decode(name, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            tv_title.setText(name);
        }


        ibtn_menu = (ImageButton) findViewById(R.id.ibtn_menu);
        ibtn_menu.setVisibility(View.VISIBLE);
        ibtn_menu.setImageResource(R.drawable.baby_icon_message);
        ibtn_menu.setOnClickListener(this);

        fresh = (SwipeRefreshLayout) findViewById(R.id.fresh);
        fresh.setColorSchemeResources(R.color.color_title_green, R.color.holo_green_light, R.color.holo_orange_light);
        fresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getConfig();
                fresh.setRefreshing(false);
            }
        });
    }

    public void getLocation() {
        try {
            if (!BaseApplication.getInstances().isSDKConnected()) {
                LogUtil.e("SDK 断联");
                BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "已与手表服务器断开连接");
                return;
            }
            currentCommand = GET_LOCATION;
            JSONObject jsonObject = new JSONObject();
            long devid = Long.parseLong(device_id, 16);
            jsonObject.put("IMEI", device_id);
            jsonObject.put("Action", "Get");
            jsonObject.put("Category", "Location");
            LogUtil.e("Location:" + jsonObject.toString());
            BaseApplication.getInstances().getNattyClient().ntyDataRouteClient(devid, jsonObject.toString().getBytes(), jsonObject.toString().length());
            LogUtil.e("SDK 配置");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSignal() {
        try {
            if (!BaseApplication.getInstances().isSDKConnected()) {
                LogUtil.e("SDK 断联");
                BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "已与手表服务器断开连接");
                return;
            }
            currentCommand = GET_SIGNAL;
            JSONObject jsonObject = new JSONObject();
            long devid = Long.parseLong(device_id, 16);
            jsonObject.put("IMEI", device_id);
            jsonObject.put("Action", "Get");
            jsonObject.put("Category", "Signal");
            LogUtil.e("Signal:" + jsonObject.toString());
            BaseApplication.getInstances().getNattyClient().ntyDataRouteClient(devid, jsonObject.toString().getBytes(), jsonObject.toString().length());
            LogUtil.e("SDK 配置");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getPower() {
        try {
            if (!BaseApplication.getInstances().isSDKConnected()) {
                LogUtil.e("SDK 断联");
                BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "已与手表服务器断开连接");
                return;
            }
            currentCommand = GET_BATTERY;
            JSONObject jsonObject = new JSONObject();
            long devid = Long.parseLong(device_id, 16);
            jsonObject.put("IMEI", device_id);
            jsonObject.put("Action", "Get");
            jsonObject.put("Category", "Power");
            LogUtil.e("Power:" + jsonObject.toString());
            BaseApplication.getInstances().getNattyClient().ntyDataRouteClient(devid, jsonObject.toString().getBytes(), jsonObject.toString().length());
            LogUtil.e("SDK 配置");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 刷新展示设备用户当前信息：信号，是否佩戴【手环、跌倒器】，电量
     */
    public void freshDeviceData() {

    }
    private Dialog mDialog;
    public void showDisconnectedDialog() {
        if(mDialog!=null && mDialog.isShowing()){
            return;
        }
        mDialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_watch_disconnect_hint, null);
        TextView cancel = (TextView) view.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(view, lp);
        mDialog.show();
    }

    /**
     * TODO 刷新获取设备步数
     */
    public void freshStepNumber() {

    }

    //********************************************************************

    private ImageView user_header_img;//头像

    //http://picture.quanjiakan.com:9080/quanjiakan/resources/doctor/20170110095209_invzijlm0sir8ggr931c.png

    public void chageHeaderImage(String path) {
        Picasso.with(this).load(path).fit().into(user_header_img);
    }

    private ImageView signal_img;//信号
    private TextView signal_text;

    private ImageView battery_img;//电池
    private TextView battery_text;

    private ImageView wear_img;//佩戴状态
    private TextView wear_text;

    private ImageView link_img;//连接状态
    private TextView link_text;

    public void initStatusLine() {

        disconnected = (LinearLayout) findViewById(R.id.disconnected);
        user_header_img = (ImageView) findViewById(R.id.user_header_img);
        user_header_img.setOnClickListener(this);
        if ("0".equals(deviceType) && (headimagePath == null || headimagePath.length() < 1 || !headimagePath.toLowerCase().startsWith("http"))) {//老人
            user_header_img.setImageResource(R.drawable.index_portrait_old);
        } else if ("1".equals(deviceType) && (headimagePath == null || headimagePath.length() < 1 || !headimagePath.toLowerCase().startsWith("http"))) {//儿童
            user_header_img.setImageResource(R.drawable.index_portrait_children);
        } else if ((headimagePath != null && headimagePath.length() > 0 && headimagePath.toLowerCase().startsWith("http"))) {
            chageHeaderImage(headimagePath);
        } else {
            user_header_img.setImageResource(R.drawable.index_portrait_old);
        }

        signal_img = (ImageView) findViewById(R.id.signal_img);
        signal_text = (TextView) findViewById(R.id.signal_text);

        battery_img = (ImageView) findViewById(R.id.battery_img);
        battery_text = (TextView) findViewById(R.id.battery_text);

        wear_img = (ImageView) findViewById(R.id.wear_img);
        wear_text = (TextView) findViewById(R.id.wear_text);
//        if ((STATUS_WEAR + "").equals(BaseApplication.getInstances().getKeyValue(device_id + "STATUS_WEAR"))) {
//            seWear(STATUS_WEAR);
//        } else {
//            seWear(STATUS_UNWEAR);
//        }
        seWear(STATUS_UNWEAR);

        link_img = (ImageView) findViewById(R.id.link_img);
        link_text = (TextView) findViewById(R.id.link_text);
//        if ((STATUS_LINK + "").equals(BaseApplication.getInstances().getKeyValue(device_id + "STATUS_LINK"))) {
//            seLink(STATUS_LINK);
//        } else {
//            seLink(STATUS_UNLINK);
//        }
        seLink(STATUS_UNLINK);
        //TODO 设置初始值
        seSign(-1);//
        seBattery(BATTERY1);
//		chageHeaderImage("http://picture.quanjiakan.com:9080/quanjiakan/resources/doctor/20170110095209_invzijlm0sir8ggr931c.png");
    }

    private final int SIGNAL_GOOD = 1;
    private final int SIGNAL_WELL = 2;
    private final int SIGNAL_NO_BAD = 3;
    private final int SIGNAL_BAD = 4;

    public void seSign(int status) {
        if(status>=0){//TODO 保存上一次的有效值
            signal_text.setTag(status);
        }
        if (status >= 80) {
            signal_img.setImageResource(R.drawable.baby_signal04_iocn);
            signal_text.setText("优");
        } else if (status >= 60) {
            signal_img.setImageResource(R.drawable.baby_signal03_iocn);
            signal_text.setText("良好");
        } else if (status >= 40) {
            signal_img.setImageResource(R.drawable.baby_signal02_iocn);
            signal_text.setText("较弱");
        } else if (status >= 0) {
            signal_img.setImageResource(R.drawable.baby_signal01_iocn);
            signal_text.setText("微弱");
        } else {
            signal_img.setImageResource(R.drawable.baby_signal00_iocn);
            signal_text.setText("无信号");
        }
    }

    private final int BATTERY1 = 1;
    private final int BATTERY2 = 2;
    private final int BATTERY3 = 3;
    private final int BATTERY4 = 4;
    private final int BATTERY5 = 5;
    private final int BATTERY6 = 6;

    public void seBattery(int status) {
        switch (status) {
            case BATTERY1:
                battery_img.setImageResource(R.drawable.baby_dainchi01_iocn);
                battery_text.setText("17%");
                break;
            case BATTERY2:
                battery_img.setImageResource(R.drawable.baby_dainchi02_iocn);
                battery_text.setText("33%");
                break;
            case BATTERY3:
                battery_img.setImageResource(R.drawable.baby_dainchi03_iocn);
                battery_text.setText("50%");
                break;
            case BATTERY4:
                battery_img.setImageResource(R.drawable.baby_dainchi04_iocn);
                battery_text.setText("67%");
                break;
            case BATTERY5:
                battery_img.setImageResource(R.drawable.baby_dainchi05_iocn);
                battery_text.setText("83%");
                break;
            case BATTERY6:
                battery_img.setImageResource(R.drawable.baby_dainchi06_iocn);
                battery_text.setText("100%");
                break;
            default:
                battery_img.setImageResource(R.drawable.baby_dainchi02_iocn);
                battery_text.setText("很差");
                break;
        }
    }

    private final int STATUS_WEAR = 1;
    private final int STATUS_UNWEAR = 2;

    public void seWear(int status) {
        switch (status) {
            case STATUS_WEAR:
                wear_img.setImageResource(R.drawable.baby_wear_iocn);
                wear_text.setText("已佩戴");
                break;
            case STATUS_UNWEAR:
                wear_img.setImageResource(R.drawable.baby_nowear_iocn);
                wear_text.setText("未佩戴");
                break;
            default:
                wear_img.setImageResource(R.drawable.baby_nowear_iocn);
                wear_text.setText("未佩戴");
                break;
        }
    }

    private final int STATUS_LINK = 1;
    private final int STATUS_UNLINK = 2;

    public void seLink(int status) {
        switch (status) {
            case STATUS_LINK:
                disconnected.setVisibility(View.GONE);
                link_img.setImageResource(R.drawable.baby_sonnected_iocn);
                link_text.setText("已连接");
                if(signal_text.getTag()!=null){//TODO 有
                    seSign(Integer.parseInt((signal_text.getTag().toString())));
                }else{
                    getConfig();
                }
                break;
            case STATUS_UNLINK:
                disconnected.setVisibility(View.VISIBLE);
                link_img.setImageResource(R.drawable.baby_notsonnected_iocn);
                link_text.setText("未连接");
                seSign(-1);
                break;
            default:
                disconnected.setVisibility(View.VISIBLE);
                link_img.setImageResource(R.drawable.baby_notsonnected_iocn);
                link_text.setText("未连接");
                seSign(-1);
                break;
        }
    }

    //********************************************************************
    private LinearLayout step_line;
    private TextView step_value;
    private LinearLayout health_line;
    private TextView health_value;
    private LinearLayout ban_line;
    private TextView ban_value;
    private LinearLayout word_line;
    private TextView word_value;

    private void initItems() {
        step_line = (LinearLayout) findViewById(R.id.step_line);
        health_line = (LinearLayout) findViewById(R.id.health_line);
        ban_line = (LinearLayout) findViewById(R.id.ban_line);
        word_line = (LinearLayout) findViewById(R.id.word_line);

        step_line.setOnClickListener(this);
        health_line.setOnClickListener(this);
        ban_line.setOnClickListener(this);
        word_line.setOnClickListener(this);

        step_value = (TextView) findViewById(R.id.step_value);
        health_value = (TextView) findViewById(R.id.health_value);
        ban_value = (TextView) findViewById(R.id.ban_value);
        word_value = (TextView) findViewById(R.id.word_value);
        word_value.setText("暂无");
    }

    //TODO 跳转到步数页面
    public void toStep() {
        //Toast.makeText(this, "toStep", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, HealthStepsActivity.class);
        intent.putExtra("device_id", device_id);
        startActivity(intent);
    }

    //TODO 跳转到健康档案页
    public void toHealth() {
//		Toast.makeText(this, "toHealth", Toast.LENGTH_SHORT).show();

        Intent health = new Intent(this, WatchCaseHistoryPicActivity.class);
        health.putExtra(WatchCaseHistoryPicActivity.PARAMS_DEVICEIS, device_id);
        startActivity(health);
    }

    //TODO 跳转到上课禁用
    public void toBan() {
        //Toast.makeText(this, "toBan", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ClassDisableActivity.class);
        intent.putExtra("device_id", device_id);
        startActivityForResult(intent, CLASS_DISABLE_STATUAS);
    }

    //TODO 跳转到文件下载
    public void toWordDownload() {
//		Toast.makeText(this, "toWordDownload", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DNAInfoActivity.class);
        intent.putExtra("device_id", device_id);
        startActivity(intent);
    }


    private List<DNAInfoEntity> dataList;
    private DNAInfoEntity_new data;

    //TODO 需要确认该接口是否变动
    public void getDNA_FilePath() {
        HashMap<String, String> params = new HashMap<>();
//		params.put("memberId",BaseApplication.getInstances().getUser_id());//TODO Old Net Interface parameter
        MyHandler.putTask(this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                /**
                 * "id": "0",
                 "title": "123",
                 "url": "http://dna.quanjiakan.com:6080/quanjiakan/resources/dna/20161116163527_dujx37.docx",
                 "createTime": "1479285231369",
                 "IMEI": "240207489164313140"
                 */
//                if (val != null && val.length() > 0 && !"null".equals(val)) {
//                    BaseHttpResultEntity_List<DNAInfoEntity> result = (BaseHttpResultEntity_List<DNAInfoEntity>) SerialUtil.jsonToObject(val, new TypeToken<BaseHttpResultEntity_List<DNAInfoEntity>>() {
//                    }.getType());
//                    dataList = result.getRows();
//                    Collections.reverse(dataList);
//                    for (DNAInfoEntity temp : dataList) {
//                        if (/*temp.getIMEI().equals(device_id)*/Long.parseLong(device_id, 16) == temp.getIMEI() || (device_id.equals(Long.toHexString(temp.getIMEI())))) {
//                            data = temp;
//                            word_value.setText("下载");
//                            break;
//                        }
//                    }
//                }

                //TODO New Handle
                //{"code":"200","message":"返回成功","object":{"IMEI":240207489215702897,"createtime":1.492758623424E12,"id":4,"title":"恩","url":"http://dna.quanjiakan.com:6080/quanjiakan/resources/dna/20170421151012_ii3f6o.doc","userid":2}}
                if (val != null && val.length() > 0 && !"null".equals(val)) {
                    HttpResponseResult result = new HttpResponseResult(val);
                    if ("200".equals(result.getCode())) {
                        if (result.getObject() != null && result.getObject().toString().length() > 0) {
                            DNAInfoEntity_new temp = (DNAInfoEntity_new) SerialUtil.jsonToObject(result.getObject().toString(), new TypeToken<DNAInfoEntity_new>() {
                            }.getType());
                            long imei = Long.parseLong(device_id, 16);
                            if (temp != null && temp.getIMEI() != null && (Long.parseLong(device_id, 16) == Long.parseLong(temp.getIMEI()) || device_id.equals(Long.toHexString(Long.parseLong(temp.getIMEI()))))) {
                                word_value.setText("下载");
                            } else {
                                word_value.setText("暂无");
                            }
                        } else {
                            word_value.setText("暂无");
                        }
                    } else {
                        word_value.setText("暂无");
                    }
                } else {
                    word_value.setText("暂无");
                }
            }
        }, HttpUrls.getDNA(device_id),//TODO 这里最好再次校验一次，对接接口虽然变更了，但由于没有数据，无法判断返回数据的格式是否与原来一致
                params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(this)));
    }

    //********************************************************************

    private LinearLayout plan_line;
    private LinearLayout contact_line;
    private LinearLayout fare_line;
    private LinearLayout fance_line;
    private LinearLayout manage_line;

    private void initFunction() {
        plan_line = (LinearLayout) findViewById(R.id.plan_line);
        contact_line = (LinearLayout) findViewById(R.id.contact_line);
        fare_line = (LinearLayout) findViewById(R.id.fare_line);
        fance_line = (LinearLayout) findViewById(R.id.fance_line);
        manage_line = (LinearLayout) findViewById(R.id.manage_line);

        plan_line.setOnClickListener(this);
        contact_line.setOnClickListener(this);
        fare_line.setOnClickListener(this);
        fance_line.setOnClickListener(this);
        manage_line.setOnClickListener(this);
    }

    public void toPlan() {
        //Toast.makeText(this, "toPlan", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WorkRestPlanActivity.class);
        intent.putExtra("device_id", device_id);
        startActivity(intent);
    }

    public void toContact() {
        Intent intent = new Intent(this, WatchContactesActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra(WatchContactesActivity.TYPE, "child");
        intent.putExtra(WatchContactesActivity.WATCH_PHONE_NUM, watch_phoneNum ==null?phone:watch_phoneNum);
        intent.putExtra(WatchContactesActivity.WATCH_PHONE_NAME, name);
        startActivity(intent);

    }

    public void toFare() {
        //Toast.makeText(this, "toFare", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, CallsTrafficQueryActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        startActivity(intent);
    }

    public void toFance() {
        Intent intent = new Intent(this, DigitalFanceActivity_new.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_TYPE, deviceType);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, mLat);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, mLon);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_PHONE, watch_phoneNum ==null?phone:watch_phoneNum);
        startActivity(intent);

    }

    public void toManage() {
        Intent intent = new Intent(this, WatchManagerActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_NAME, name);
        intent.putExtra(WatchManagerActivity.TYPE, "child");
        intent.putExtra(WatchManagerActivity.WATCH_PHONENUM, watch_phoneNum ==null?phone:watch_phoneNum);
        startActivityForResult(intent, REQUEST_MANAGE);
    }

    //********************************************************************
    private ImageView location;
    private ImageView call;
    private ImageView chat;

    private void initBottomFunction() {
        location = (ImageView) findViewById(R.id.location);
        call = (ImageView) findViewById(R.id.call);
        chat = (ImageView) findViewById(R.id.chat);
        notice = (MaterialBadgeTextView) findViewById(R.id.notice);


        location.setOnClickListener(this);
        call.setOnClickListener(this);
        chat.setOnClickListener(this);
    }

    /**
     * TODO 刷地址，如果不同，需要向上一页传递数据过去，更新地址信息
     */
    private String locationPoint;

    public void refreshLocation() {
        Intent intent = new Intent(this, WatchChildFreshLocationActivity_new.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_TYPE, deviceType);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_LAT, mLat);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_LNG, mLon);
        startActivityForResult(intent, REQUEST_FRESH);
    }

    public void callPhone() {
        if (call.getTag() != null && CheckUtil.isPhoneNumber(call.getTag().toString().trim())) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + call.getTag().toString().trim()));
            startActivity(intent);
        } else if (phone != null && CheckUtil.isPhoneNumber(phone)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
            startActivity(intent);
        } else {
            BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "配置信息中未获取到电话号码!");
        }
    }

    public void voiceChat() {
        //TODO 重置未读数量

        //TODO 变更聊天图标（使未读标志消失）


        Intent intent = new Intent(this, ChildVoiceChatActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        startActivityForResult(intent,REQUEST_CHILD_VOICE_CHAT);
    }
    //********************************************************************

    private void toMessage() {
        Intent intent = new Intent(this, MessageRecordActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_IMAGEPATH, headimagePath);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_NAME, name);
        startActivity(intent);
    }

    public void toCard() {
        Intent intent = new Intent(this, ModifyBindInfoActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra("name", name);
        intent.putExtra("imag", headimagePath);

        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_header_img:
                //TODO 跳转到宝贝名片
                toCard();
                break;
            case R.id.ibtn_menu:
                toMessage();
                break;
            case R.id.ibtn_back:
                if (mLatParam != mLat || mLonParam != mLon) {
                    Intent intent = new Intent();
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, mLat);
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, mLon);
                    if (locationPoint != null && locationPoint.length() > 0 && locationPoint.contains(",")) {
                        intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_POINT, locationPoint);
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    finish();
                }
                break;
            //*************************************
            case R.id.step_line:
                toStep();
                break;
            case R.id.health_line:
                toHealth();
                break;
            case R.id.ban_line:
                toBan();
                break;
            case R.id.word_line:
                toWordDownload();
                break;
            //*************************************
            case R.id.plan_line:
                toPlan();
                break;
            case R.id.contact_line:
                toContact();
                break;
            case R.id.fare_line:
                toFare();
                break;
            case R.id.fance_line:
                toFance();
                break;
            case R.id.manage_line:
                toManage();
                break;
            //*************************************
            case R.id.location:
                refreshLocation();
                break;
            case R.id.call:
                callPhone();
                break;
            case R.id.chat:
                // TODO: 2017/5/5 消息提示
                hideVoiceNotice();

                voiceChat();
                break;
        }
    }

    private void hideVoiceNotice() {
        notice.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!NetUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "网络连接不可用!", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode) {
            case REQUEST_MANAGE:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent();
                    intent.putExtra(WatchChildEntryActivity.RESULT_TYPE, WatchChildEntryActivity.REQUEST_UNBIND);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case CLASS_DISABLE_STATUAS:
                getClassDisableReq();
                break;
            case REQUEST_FRESH:
                if (resultCode == RESULT_OK) {
                    double tempLat = data.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LAT, mLat);
                    double tempLng = data.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LNG, mLon);
                    mLatParam = mLat;
                    mLonParam = mLon;
                }
                break;

            case REQUEST_CHILD_VOICE_CHAT:
                if (resultCode==RESULT_OK) {
                    BaseApplication.getInstances().setKeyNumberValue(BaseApplication.getInstances().getUser_id() + "_" + device_id + "_unread", 0);
                    notice.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @SuppressWarnings("deprecation")
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
        int value = BaseApplication.getInstances().getKeyNumberValue(BaseApplication.getInstances().getUser_id() + "_" + device_id + "_unread");
        showVoiceNotice(value);

        startConfigTimer();
    }

    private void showVoiceNotice(int value) {

        if (value>0) {
            notice.setBadgeCount(value);
            notice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        stopConfigTimer();
    }

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonVoiceData(CommonVoiceData msg) {
        switch (msg.getType()) {
            case NattyProtocolFilter.DISPLAY_VOICE_PLAY: {
                String fromId = msg.getFromId();
                String longDeviceId = Long.parseLong(device_id, 16) + "";
                if ((device_id.equals(fromId) || longDeviceId.equals(fromId))) {
                    int value = BaseApplication.getInstances().getKeyNumberValue(BaseApplication.getInstances().getUser_id() + "_" + device_id + "_unread");
                    showVoiceNotice(value+1);

                } else if (BaseApplication.getInstances().getUser_id().equals(fromId)) {
                    BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "设备未开机!");
                }
            }
            break;
        }


    }

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonNattyData(CommonNattyData msg) {
        switch (msg.getType()) {
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_COMMON_BROADCAST: {
                /**
                 * {
                 "Results": {
                 "IMEI": "355637052788650",
                 "Category": "WearStatus",
                 "WearStatus": "On",
                 }
                 }
                 */
                String stringData = msg.getData();
                if (stringData != null
                        && stringData.contains(device_id)
                        && stringData.contains("WearStatus")
                        && (stringData.contains("On") || stringData.toLowerCase().contains("on"))
                        ) {
                    seWear(STATUS_WEAR);
                    BaseApplication.getInstances().setKeyValue(device_id + "STATUS_WEAR", STATUS_WEAR + "");
                } else if (stringData != null
                        && stringData.contains(device_id)
                        && stringData.contains("WearStatus")
                        && (stringData.contains("Off") || stringData.toLowerCase().contains("off"))
                        ) {
                    seWear(STATUS_UNWEAR);
                    BaseApplication.getInstances().setKeyValue(device_id + "STATUS_WEAR", STATUS_UNWEAR + "");
                }else if (stringData!=null&&stringData.contains("IMEI")&&stringData.contains("StepsReport")){
                    //处理推送过来的步数
                    try {
                        JSONObject jsonObject = new JSONObject(stringData);
                        String stepsReport = jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).getString("StepsReport");
                        String imei = jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).getString("IMEI");
                        if (stepsReport!=null&&device_id.equals(imei)){
                            step_value.setText(stepsReport);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_CONTROL_LOCATION_NEW: {

                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_RESULT: {
                //需要自己根据发送的命令去判断
                String data = msg.getData();
                switch (currentCommand) {
                    case GET_CONFIG: {
                        WatchCommonResult result = (WatchCommonResult) SerialUtil.jsonToObject(data, new TypeToken<WatchCommonResult>() {
                        }.getType());
                        if (result != null && "200".equals(result.getResult().getCode())) {
                            disconnected.setVisibility(View.GONE);
                        } else if (result != null && "10001".equals(result.getResult().getCode())) {
                            BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "设备不在线");
                            showDisconnectedDialog();
                            seLink(STATUS_UNLINK);
                            BaseApplication.getInstances().setKeyValue(device_id + "STATUS_LINK", STATUS_UNLINK + "");
                            disconnected.setVisibility(View.VISIBLE);
                        } else {
                            BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "获取配置失败");
                            disconnected.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                    case GET_LOCATION: {
                        WatchCommonResult result = (WatchCommonResult) SerialUtil.jsonToObject(data, new TypeToken<WatchCommonResult>() {
                        }.getType());
                        if (result != null && "200".equals(result.getResult().getCode())) {
                            disconnected.setVisibility(View.GONE);
                        } else if (result != null && "10001".equals(result.getResult().getCode())) {
                            BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "设备不在线");
                            showDisconnectedDialog();
                            seLink(STATUS_UNLINK);
                            BaseApplication.getInstances().setKeyValue(device_id + "STATUS_LINK", STATUS_UNLINK + "");
                            disconnected.setVisibility(View.VISIBLE);
                        } else {
                            BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "获取地址失败");
                            disconnected.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                }
                currentCommand = 0;
                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_ROUTE: {
                //需要自己根据发送的命令去判断
                String data = msg.getData();
                //{"Results":{"IMEI":"355637052203973","Category": "Config","Config":{"Power":"7","Signal":"100","Steps":"0","WearStatus":"On","PhoneNum":"18665748244","Location":","}}}
                if (data != null && !(data.contains("Fare") || data.contains("Flow"))) {
                    if (data.contains("results") && data.contains("Config")) {
                        WatchGetConfig_results result = (WatchGetConfig_results) SerialUtil.jsonToObject(data, new TypeToken<WatchGetConfig_results>() {
                        }.getType());
                        if (result != null && result.getResults() != null
                                && device_id.equals(result.getResults().getIMEI())
                                && "Config".equals(result.getResults().getCategory())) {

                            seBattery(Integer.parseInt(result.getResults().getConfig().getPower()));
                            seSign(Integer.parseInt(result.getResults().getConfig().getSignal()));
                            String wearStatus = result.getResults().getConfig().getWearStatus();
                            if (wearStatus.equals("On") || wearStatus.equals("on")) {
                                seWear(STATUS_WEAR);
                                BaseApplication.getInstances().setKeyValue(device_id + "STATUS_WEAR", STATUS_WEAR + "");
                            } else if (wearStatus.equals("Off") || wearStatus.equals("off")) {
                                seWear(STATUS_UNWEAR);
                                BaseApplication.getInstances().setKeyValue(device_id + "STATUS_WEAR", STATUS_UNWEAR + "");
                            }
                            seLink(STATUS_LINK);

                            BaseApplication.getInstances().setKeyValue(device_id + "STATUS_LINK", STATUS_LINK + "");
                            step_value.setText(result.getResults().getConfig().getSteps());
                            call.setTag(result.getResults().getConfig().getPhoneNum());
                            watch_phoneNum = result.getResults().getConfig().getPhoneNum();

                        } else {
                            BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "获取配置失败");
                            BaseApplication.getInstances().setKeyValue(device_id + "STATUS_LINK", STATUS_UNLINK + "");
                        }
                    } else if (data.contains("Results") && data.contains("Config")) {
                        //{"Results":{"IMEI":"355637052203973","Category": "Config","Config":{"Power":"6","Signal":"100","Steps":"0","WearStatus":"On","PhoneNum":"18665748244","Location":","}}}
                        WatchGetConfig_results result = (WatchGetConfig_results) SerialUtil.jsonToObject(data, new TypeToken<WatchGetConfig_results>() {
                        }.getType());
                        LogUtil.e(result.toString());
                        if (result != null && result.getResults() != null
                                && device_id.equals(result.getResults().getIMEI()) && "Config".equals(result.getResults().getCategory())) {
                            seBattery(Integer.parseInt(result.getResults().getConfig().getPower()));
                            seSign(Integer.parseInt(result.getResults().getConfig().getSignal()));
                            String wearStatus = result.getResults().getConfig().getWearStatus();
                            seWear(STATUS_WEAR);
                            if (wearStatus.equals("On")) {
                                seWear(STATUS_WEAR);
                                BaseApplication.getInstances().setKeyValue(device_id + "STATUS_WEAR", STATUS_WEAR + "");
                            } else if (wearStatus.equals("Off")) {
                                seWear(STATUS_UNWEAR);
                                BaseApplication.getInstances().setKeyValue(device_id + "STATUS_WEAR", STATUS_UNWEAR + "");
                            }
                            seLink(STATUS_LINK);

                            BaseApplication.getInstances().setKeyValue(device_id + "STATUS_LINK", STATUS_LINK + "");
                            step_value.setText(result.getResults().getConfig().getSteps());

                            call.setTag(result.getResults().getConfig().getPhoneNum());
                            watch_phoneNum = result.getResults().getConfig().getPhoneNum();
                        } else {
                            BaseApplication.getInstances().toast(WatchChildEntryActivity.this, "获取配置失败");
                            BaseApplication.getInstances().setKeyValue(device_id + "STATUS_LINK", STATUS_UNLINK + "");
                        }
                    } else if ((data.contains("results") || data.contains("Results")) && data.contains("Location")) {
                        //TODO 收到定位
                        //{"results":{"IMEI":"352315052834187","Category": "Location","Type":"WIFI","Radius":"35","Location":"113.241652,23.1320961"}}}
                        try {
                            JSONObject json = new JSONObject(data);
                            if (json.has("results") && json.getJSONObject("results") != null

                                    && json.getJSONObject("results").has("Category")
                                    && json.getJSONObject("results").getString("Category") != null
                                    && "Location".equals(json.getJSONObject("results").getString("Category"))

                                    && json.getJSONObject("results").has("IMEI")
                                    && json.getJSONObject("results").getString("IMEI") != null
                                    && json.getJSONObject("results").getString("IMEI").length() > 0

                                    && json.getJSONObject("results").has("Location")
                                    && json.getJSONObject("results").getString("Location") != null
                                    && json.getJSONObject("results").getString("Location").length() > 0
                                    && json.getJSONObject("results").getString("Location").contains(",")
                                    ) {

                                if (json.getJSONObject("results").getString("IMEI").equals(device_id)) {
                                    seLink(STATUS_LINK);
                                    BaseApplication.getInstances().setKeyValue(device_id + "STATUS_LINK", STATUS_LINK + "");
                                    locationPoint = json.getJSONObject("results").getString("Location");
                                }
                            } else if (
                                    json.has("Results") && json.getJSONObject("Results") != null

                                            && json.getJSONObject("Results").has("Category")
                                            && json.getJSONObject("Results").getString("Category") != null
                                            && "Location".equals(json.getJSONObject("Results").getString("Category"))

                                            && json.getJSONObject("Results").has("IMEI")
                                            && json.getJSONObject("Results").getString("IMEI") != null
                                            && json.getJSONObject("Results").getString("IMEI").length() > 0

                                            && json.getJSONObject("Results").has("Location")
                                            && json.getJSONObject("Results").getString("Location") != null
                                            && json.getJSONObject("Results").getString("Location").length() > 0
                                            && json.getJSONObject("Results").getString("Location").contains(",")
                                    ) {
                                if (json.getJSONObject("Results").getString("IMEI").equals(device_id)) {
                                    seLink(STATUS_LINK);
                                    BaseApplication.getInstances().setKeyValue(device_id + "STATUS_LINK", STATUS_LINK + "");
                                    locationPoint = json.getJSONObject("Results").getString("Location");
                                }
                            } else {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
        }
    }
}
