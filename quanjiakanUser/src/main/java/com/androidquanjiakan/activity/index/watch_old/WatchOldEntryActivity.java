package com.androidquanjiakan.activity.index.watch_old;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.watch.CallsTrafficQueryActivity;
import com.androidquanjiakan.activity.index.watch_child.ChildWatchCardActivity;
import com.androidquanjiakan.activity.index.watch_child.WatchCaseHistoryPicActivity;
import com.androidquanjiakan.activity.index.watch_child.WatchChildFreshLocationActivity;
import com.androidquanjiakan.activity.index.watch_child.elder.CareRemindActivity;
import com.androidquanjiakan.activity.setting.contact.MessageRecordActivity;
import com.androidquanjiakan.activity.setting.contact.WatchContactesActivity;
import com.androidquanjiakan.activity.setting.contact.WatchManagerActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.entity.DNAInfoEntity;
import com.androidquanjiakan.entity.WatchCommonResult;
import com.androidquanjiakan.entity.WatchGetConfig;
import com.androidquanjiakan.entity.WatchGetConfig_results;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.view.CircleTransformation;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

import static com.androidquanjiakan.activity.index.MainEntryMapActivity.GET_CONFIG;
import static com.androidquanjiakan.activity.index.MainEntryMapActivity.GET_LOCATION;

public class WatchOldEntryActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ibtn_menu)
    ImageButton ibtnMenu;
    @BindView(R.id.menu_text)
    TextView menuText;
    @BindView(R.id.disconnected)
    LinearLayout disconnected;
    @BindView(R.id.status_line_4)
    ImageView statusLine4;
    @BindView(R.id.watch_battery_img)
    ImageView watchBatteryImg;
    @BindView(R.id.watch_battery_text)
    TextView watchBatteryText;
    @BindView(R.id.watch_battery_line)
    LinearLayout watchBatteryLine;
    @BindView(R.id.fall_battery_img)
    ImageView fallBatteryImg;
    @BindView(R.id.fall_battery_text)
    TextView fallBatteryText;
    @BindView(R.id.fall_battery_line)
    LinearLayout fallBatteryLine;
    @BindView(R.id.state_text)
    TextView stateText;
    @BindView(R.id.state_img)
    ImageView stateImg;
    @BindView(R.id.state_line)
    LinearLayout stateLine;
    @BindView(R.id.user_header_img)
    ImageView userHeaderImg;
    @BindView(R.id.status)
    RelativeLayout status;
    @BindView(R.id.step_value)
    TextView stepValue;
    @BindView(R.id.health_dynamic_line)
    LinearLayout healthDynamicLine;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.health_value)
    TextView healthValue;
    @BindView(R.id.health_line)
    LinearLayout healthLine;
    @BindView(R.id.functionline1)
    LinearLayout functionline1;
    @BindView(R.id.wear_state_line)
    LinearLayout wearStateLine;
    @BindView(R.id.plan_line)
    LinearLayout planLine;
    @BindView(R.id.contact_line)
    LinearLayout contactLine;
    @BindView(R.id.fare_line)
    LinearLayout fareLine;
    @BindView(R.id.manage_line)
    LinearLayout manageLine;
    @BindView(R.id.function_items)
    LinearLayout functionItems;
    @BindView(R.id.location)
    ImageView location;
    @BindView(R.id.call)
    ImageView call;
    //    @BindView(R.id.efence)
//    ImageView efence;
    @BindView(R.id.fresh)
    SwipeRefreshLayout fresh;
    @BindView(R.id.disconnect_img)
    ImageView disconnectImg;

    public static final String RESULT_TYPE = "TYPE";

    public static final int REQUEST_UNBIND = 1200;
    public static final int REQUEST_MANAGE = 1201;
    public static final int REQUEST_FRESH = 1202;
    @BindView(R.id.fance_line)
    LinearLayout fanceLine;
    @BindView(R.id.chat)
    ImageView chat;

    private List<DNAInfoEntity> dataList;
    private DNAInfoEntity data;

    /**
     * TODO 刷地址，如果不同，需要向上一页传递数据过去，更新地址信息
     */
    private String locationPoint;
    private String device_id;
    private String phone;
    private String deviceType;
    private String headimagePath;
    private String name;

    private double mLat;
    private double mLon;
    private double mLatParam;
    private double mLonParam;
    private int currentCommand = 0;

    private final int STATUS_WEAR = 1;
    private final int STATUS_UNWEAR = 2;
    private final int BATTERY1 = 1;
    private final int BATTERY2 = 2;
    private final int BATTERY3 = 3;
    private final int BATTERY4 = 4;
    private final int BATTERY5 = 5;
    private final int BATTERY6 = 6;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_watch_old_entry);
        BaseApplication.getInstances().setCurrentActivity(this);
        ButterKnife.bind(this);
        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        phone = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_PHONE);
        mLat = getIntent().getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, -200);
        mLon = getIntent().getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, -200);
        deviceType = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_TYPE);
        headimagePath = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_IMAGEPATH);
        name = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_NAME);
        mLatParam = mLat;
        mLonParam = mLon;
        if (device_id == null || deviceType == null
                ) {
            BaseApplication.getInstances().toast(WatchOldEntryActivity.this, "传入参数异常!");
            finish();
            return;
        }
        initTitleBar();

        // 状态信息
        initStatusLine();

        getConfig();
    }

    public void initTitleBar() {
        ibtnBack.setVisibility(View.VISIBLE);
        ibtnBack.setOnClickListener(this);
        if (name.contains("%")) {
            try {
                tvTitle.setText(URLDecoder.decode(name, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            tvTitle.setText(name);
        }


        ibtnMenu.setVisibility(View.VISIBLE);
        ibtnMenu.setImageResource(R.drawable.baby_icon_message);
        ibtnMenu.setOnClickListener(this);

        fresh.setColorSchemeResources(R.color.color_title_green, R.color.holo_green_light, R.color.holo_orange_light);
        fresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getConfig();
                fresh.setRefreshing(false);
            }
        });
    }

    public void getConfig() {
        try {
            if (!BaseApplication.getInstances().isSDKConnected()) {
                LogUtil.e("SDK 断联");
                BaseApplication.getInstances().toast(WatchOldEntryActivity.this, "已与手表服务器断开连接");
                return;
            }
            currentCommand = GET_CONFIG;
            JSONObject jsonObject = new JSONObject();
            long devid = Long.parseLong(device_id, 16);
            jsonObject.put("IMEI", device_id);
            jsonObject.put("Action", "Get");
            jsonObject.put("Category", "Config");
            LogUtil.e("Config:" + jsonObject.toString());
            BaseApplication.getInstances().getNattyClient().ntyDataRouteClient(devid, jsonObject.toString().getBytes(), jsonObject.toString().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLocation() {
        try {
            if (!BaseApplication.getInstances().isSDKConnected()) {
                LogUtil.e("SDK 断联");
                BaseApplication.getInstances().toast(WatchOldEntryActivity.this, "已与手表服务器断开连接");
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

    //********************************************************************
    //http://picture.quanjiakan.com:9080/quanjiakan/resources/doctor/20170110095209_invzijlm0sir8ggr931c.png

    public void chageHeaderImage(String path) {
        Picasso.with(this).load(path).transform(new CircleTransformation()).into(userHeaderImg);
    }

    public void initStatusLine() {
        userHeaderImg.setOnClickListener(this);
        if ("0".equals(deviceType) && (headimagePath == null || headimagePath.length() < 1 || !headimagePath.toLowerCase().startsWith("http"))) {//老人
            userHeaderImg.setImageResource(R.drawable.old_pic_portrait);
        } else if ("1".equals(deviceType) && (headimagePath == null || headimagePath.length() < 1 || !headimagePath.toLowerCase().startsWith("http"))) {//儿童
            userHeaderImg.setImageResource(R.drawable.old_pic_portrait);
        } else if ((headimagePath != null && headimagePath.length() > 0 && headimagePath.toLowerCase().startsWith("http"))) {
            chageHeaderImage(headimagePath);
        } else {
            userHeaderImg.setImageResource(R.drawable.old_pic_portrait);
        }
        setWear(STATUS_UNWEAR);
        //TODO 设置初始值
        setBattery(BATTERY1);
        setBatteryWear(BATTERY1);
//		chageHeaderImage("http://picture.quanjiakan.com:9080/quanjiakan/resources/doctor/20170110095209_invzijlm0sir8ggr931c.png");
    }

    public void setBattery(int status) {
        switch (status) {
            case BATTERY1:
                watchBatteryImg.setImageResource(R.drawable.baby_dainchi01_iocn);
                watchBatteryText.setText("17%");
                break;
            case BATTERY2:
                watchBatteryImg.setImageResource(R.drawable.baby_dainchi02_iocn);
                watchBatteryText.setText("33%");
                break;
            case BATTERY3:
                watchBatteryImg.setImageResource(R.drawable.baby_dainchi03_iocn);
                watchBatteryText.setText("50%");
                break;
            case BATTERY4:
                watchBatteryImg.setImageResource(R.drawable.baby_dainchi04_iocn);
                watchBatteryText.setText("67%");
                break;
            case BATTERY5:
                watchBatteryImg.setImageResource(R.drawable.baby_dainchi05_iocn);
                watchBatteryText.setText("83%");
                break;
            case BATTERY6:
                watchBatteryImg.setImageResource(R.drawable.baby_dainchi06_iocn);
                watchBatteryText.setText("100%");
                break;
            default:
                watchBatteryImg.setImageResource(R.drawable.baby_dainchi02_iocn);
                watchBatteryText.setText("很差");
                break;
        }
    }

    public void setBatteryWear(int status) {
        switch (status) {
            case BATTERY1:
                fallBatteryImg.setImageResource(R.drawable.baby_dainchi01_iocn);
                fallBatteryText.setText("17%");
                break;
            case BATTERY2:
                fallBatteryImg.setImageResource(R.drawable.baby_dainchi02_iocn);
                fallBatteryText.setText("33%");
                break;
            case BATTERY3:
                fallBatteryImg.setImageResource(R.drawable.baby_dainchi03_iocn);
                fallBatteryText.setText("50%");
                break;
            case BATTERY4:
                fallBatteryImg.setImageResource(R.drawable.baby_dainchi04_iocn);
                fallBatteryText.setText("67%");
                break;
            case BATTERY5:
                fallBatteryImg.setImageResource(R.drawable.baby_dainchi05_iocn);
                fallBatteryText.setText("83%");
                break;
            case BATTERY6:
                fallBatteryImg.setImageResource(R.drawable.baby_dainchi06_iocn);
                fallBatteryText.setText("100%");
                break;
            default:
                fallBatteryImg.setImageResource(R.drawable.baby_dainchi02_iocn);
                fallBatteryText.setText("很差");
                break;
        }
    }

    public void setWear(int status) {
        switch (status) {
            case STATUS_WEAR:
                disconnectImg.setVisibility(View.VISIBLE);
                disconnectImg.setImageResource(R.drawable.old_ico_connect_down);
                break;
            case STATUS_UNWEAR:
                disconnectImg.setVisibility(View.VISIBLE);
                disconnectImg.setImageResource(R.drawable.old_ico_no_connect_down);
                break;
            default:
                disconnectImg.setVisibility(View.VISIBLE);
                disconnectImg.setImageResource(R.drawable.old_ico_no_connect_down);
                break;
        }
    }
    //********************************************************************

    //TODO 跳转到健康档案页
    public void toHealth() {
//		Toast.makeText(this, "toHealth", Toast.LENGTH_SHORT).show();

        Intent health = new Intent(this, WatchCaseHistoryPicActivity.class);
        health.putExtra(WatchCaseHistoryPicActivity.PARAMS_DEVICEIS, device_id);
        startActivity(health);
    }

    //********************************************************************

    public void toPlan() {
        //Toast.makeText(this, "toPlan", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, CareRemindActivity.class);
        startActivity(intent);
    }

    public void toContact() {
        Intent intent = new Intent(this, WatchContactesActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra(WatchContactesActivity.TYPE, "old");
        startActivity(intent);

    }

    public void toFare() {
        //Toast.makeText(this, "toFare", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, CallsTrafficQueryActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        startActivity(intent);
    }

    public void toFance() {
        Intent intent = new Intent(this, DigitalFanceOldActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_TYPE, deviceType);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, mLat);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, mLon);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_PHONE, phone);
        startActivity(intent);
    }

    public void toManage() {
        Intent intent = new Intent(this, WatchManagerActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_NAME, name);
        intent.putExtra(WatchManagerActivity.TYPE, "old");
        startActivityForResult(intent, REQUEST_MANAGE);
    }

    //********************************************************************

    public void refreshLocation() {
        Intent intent = new Intent(this, WatchChildFreshLocationActivity.class);
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
            BaseApplication.getInstances().toast(WatchOldEntryActivity.this, "配置信息中未获取到电话号码!");
        }
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
        Intent intent = new Intent(this, ChildWatchCardActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra("type", "old");
        intent.putExtra("name", name);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MANAGE:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent();
                    intent.putExtra(WatchOldEntryActivity.RESULT_TYPE, WatchOldEntryActivity.REQUEST_UNBIND);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case REQUEST_FRESH:
                if (resultCode == RESULT_OK) {
                    double tempLat = data.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LAT, mLat);
                    double tempLng = data.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LNG, mLon);
                    mLatParam = mLat;
                    mLonParam = mLon;
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        JPushInterface.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
        JPushInterface.onResume(this);
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

    @OnClick({R.id.ibtn_back, R.id.ibtn_menu, R.id.user_header_img,
            R.id.health_dynamic_line, R.id.health_line,
            R.id.wear_state_line, R.id.plan_line, R.id.contact_line,
            R.id.fare_line, R.id.manage_line, R.id.location,
            R.id.call, R.id.fance_line,R.id.chat})
    public void onClick(View view) {
        switch (view.getId()) {
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
            case R.id.ibtn_menu:
                toMessage();
                break;
            case R.id.user_header_img:
                toCard();
                break;
            case R.id.health_dynamic_line:
                toHealthDynamic();
                break;
            case R.id.health_line:
                toHealth();
                break;
            case R.id.wear_state_line:
                break;
            case R.id.plan_line:
                toPlan();
                break;
            case R.id.contact_line:
                toContact();
                break;
            case R.id.fare_line:
                toFare();
                break;
            case R.id.manage_line:
                toManage();
                break;
            case R.id.location:
                refreshLocation();
                break;
            case R.id.call:
                callPhone();
                break;
            case R.id.fance_line:
                toFance();
                break;

            case R.id.chat:
                // TODO: 2017/4/19 语音微聊
                BaseApplication.getInstances().toast(this,"语音微聊");
                break;


        }
    }

    private void toHealthDynamic() {
        Intent intent = new Intent(this, HealthDynamicsActivity.class);
        intent.putExtra("device_id", device_id);
        startActivity(intent);
    }

    public void showDisconnectedDialog() {
        final Dialog mDialog = new Dialog(this, R.style.dialog_loading);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
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
                    setWear(STATUS_WEAR);
                } else if (stringData != null
                        && stringData.contains(device_id)
                        && stringData.contains("WearStatus")
                        && (stringData.contains("Off") || stringData.toLowerCase().contains("off"))
                        ) {
                    setWear(STATUS_UNWEAR);
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
                            showDisconnectedDialog();
                            disconnected.setVisibility(View.VISIBLE);
                        } else {
                            BaseApplication.getInstances().toast(WatchOldEntryActivity.this, "获取配置失败");
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
                if (data != null && !(data.contains("Fare") || data.contains("Flow"))) {
                    if (data.contains("results") && data.contains("Config")) {
                        WatchGetConfig_results result = (WatchGetConfig_results) SerialUtil.jsonToObject(data, new TypeToken<WatchGetConfig_results>() {
                        }.getType());
                        if (result != null && "Config".equals(result.getResults().getCategory())) {
                            disconnected.setVisibility(View.GONE);
                            setBattery(Integer.parseInt(result.getResults().getConfig().getPower()));
                            setBatteryWear(Integer.parseInt(result.getResults().getConfig().getPower()));
                            setWear(STATUS_WEAR);

                            call.setTag(result.getResults().getConfig().getPhoneNum());
                        } else {
                            BaseApplication.getInstances().toast(WatchOldEntryActivity.this, "获取配置失败");
                        }
                    } else if (data.contains("Results") && data.contains("Config")) {
                        WatchGetConfig result = (WatchGetConfig) SerialUtil.jsonToObject(data, new TypeToken<WatchGetConfig>() {
                        }.getType());
                        if (result != null && "Config".equals(result.getResults().getCategory())) {
                            disconnected.setVisibility(View.GONE);
                            setBattery(Integer.parseInt(result.getResults().getConfig().getPower()));
                            setBatteryWear(Integer.parseInt(result.getResults().getConfig().getPower()));
                            setWear(STATUS_WEAR);

                            call.setTag(result.getResults().getConfig().getPhoneNum());
                        } else {
                            BaseApplication.getInstances().toast(WatchOldEntryActivity.this, "获取配置失败");
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
