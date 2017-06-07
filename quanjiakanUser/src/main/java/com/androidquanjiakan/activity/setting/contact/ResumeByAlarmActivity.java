package com.androidquanjiakan.activity.setting.contact;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidquanjiakan.activity.setting.contact.bean.RunTimeEvent;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.rusumeBean;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.result.ResumeResultBean;
import com.androidquanjiakan.util.LogUtil;
import com.example.greendao.dao.rusumeBeanDao;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.quanjiakanuser.widget.AbstractWheelTextAdapter;
import com.quanjiakanuser.widget.OnWheelChangedListener;
import com.quanjiakanuser.widget.OnWheelScrollListener;
import com.quanjiakanuser.widget.WheelView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


/**
 * 作者：Administrator on 2017/2/21 10:49
 * <p>
 * 邮箱：liuzj@hi-board.com
 */
public class ResumeByAlarmActivity extends BaseActivity implements View.OnClickListener {

    private Context context;

    private RelativeLayout open_time;
    private RelativeLayout close_time;
    private TextView tv_ps;
    private LinearLayout set_time;
    private TextView title;
    private TextView btn_sure;
    private TextView btn_cancel;
    private TextView show_open;
    private TextView show_close;
    private WheelView wvTime;
    private WheelView wvHour;
    private WheelView wvMinute;


    private int maxTextSize = 16;
    private int minTextSize = 14;
    private CalendarTextAdapter mTimeAdapter;

    private int curruntTime = 0;
    private int curruntHour = 0;
    private int curruntMinute = 0;
    private ArrayList<String> arr_time = new ArrayList<String>();
    private ArrayList<String> arr_hour = new ArrayList<String>();
    private ArrayList<String> arr_minute = new ArrayList<String>();
    private CalendarTextAdapter mHourAdapter;
    private CalendarTextAdapter mMinuteAdapter;
    private String selectTime = "上午";
    private String selectHour = "1";
    private String selectMinute = "00";
    private ScrollView scrollView;

    //    public static final String OFF_TIME = "off_time";
//    public static final String ON_TIME = "on_time";
    public static final String TYPE = "type";
    private String on_time;
    private String off_time;
    private String off;
    private String on;
    private String type;
    private rusumeBeanDao rsumeDao;
    //    private List<rusumeBean> list1;
    private com.androidquanjiakan.entity.rusumeBean rusume_Bean;
    private String rusumeBeanJson;
    private ResumeResultBean.ResultsBean.TurnBean turnBean;
    private ResumeResultBean.ResultsBean.TurnBean.OffBean offBean;
    private ResumeResultBean.ResultsBean.TurnBean.OnBean onBean;
    private ResumeResultBean resumeResultBean;
    private ResumeResultBean.ResultsBean result;
    private String offTime = "";
    private String onTime = "";
    private Gson gson;
    private String device_id;
    private String status;


    private ResumeResultBean.ResultsBean result1;
    private ResumeResultBean.ResultsBean.TurnBean turnBean1;
    private ResumeResultBean.ResultsBean.TurnBean.OffBean offBean1;
    private ResumeResultBean.ResultsBean.TurnBean.OnBean onBean1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_byalarm);
        context = this;
//        on_time = getIntent().getStringExtra(ON_TIME);
//        off_time = getIntent().getStringExtra(OFF_TIME);
        type = getIntent().getStringExtra(TYPE);
        device_id = getIntent().getStringExtra("deviceId");
        rsumeDao = getRsumeDao();
        gson = new Gson();
        initTitle();
        initView();

    }

    private rusumeBeanDao getRsumeDao() {

        return BaseApplication.getInstances().getDaoInstant().getRusumeBeanDao();
    }

    private ToggleButton btn_open_close;
    private void initView() {
        // TODO: 2017/2/21  时间设定选择控件
        /**
         *开关机时间设置  点击
         */
        btn_open_close = (ToggleButton) findViewById(R.id.btn_open_close);
        btn_open_close.setOnClickListener(this);
        open_time = (RelativeLayout) findViewById(R.id.rlt_open_time);
        close_time = (RelativeLayout) findViewById(R.id.rlt_close_time);
        open_time.setOnClickListener(this);
        close_time.setOnClickListener(this);


        tv_ps = (TextView) findViewById(R.id.textView2);
        set_time = (LinearLayout) findViewById(R.id.llt_set_time);
        tv_ps.setVisibility(View.VISIBLE);
        set_time.setVisibility(View.GONE);

        //设置时间的内部控件
        title = (TextView) findViewById(R.id.tv_share_title);
        btn_sure = (TextView) findViewById(R.id.btn_sure);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);


        //开关机时间展示
        show_open = (TextView) findViewById(R.id.tv_show_open);
        show_close = (TextView) findViewById(R.id.tv_show_close);



        scrollView = (ScrollView) findViewById(R.id.scrollView);

        initWheelView();


    }

    private void getResumData() {
        if (type.equals("child")) {
            if (NetUtil.isNetworkAvailable(this)) {
                loadResumeData();
            } else {

                if (rsumeDao.loadAll().size() > 0) {//如果数据库有数据  拿出来展示
//取出json
                    for (int i = 0; i < rsumeDao.loadAll().size(); i++) {
                        if (device_id.equals(rsumeDao.loadAll().get(i).getImei())) {
                            rusume_Bean = rsumeDao.loadAll().get(i);
                            rusumeBeanJson = rsumeDao.loadAll().get(i).getJson();
                            LogUtil.e("rusumeBeanJson--------" + rusumeBeanJson);
                            resumeResultBean = gson.fromJson(rusumeBeanJson, ResumeResultBean.class);
                            result = resumeResultBean.getResults();
                            if (ConstantClassFunction.TURN.equals(this.result.getCategory())) {
                                turnBean = result.getTurn();
                                offBean = turnBean.getOff();
                                onBean = turnBean.getOn();
                                status = turnBean.getStatus();
                                if ("0".equals(this.status)) {
                                    /**
                                     * 定制开关
                                     */
                                    btn_open_close.setChecked(false);
                                    btn_open_close.setBackgroundResource(R.drawable.edit_btn_close);
                                } else {
                                    /**
                                     * 定制开关
                                     */
                                    btn_open_close.setChecked(true);
                                    btn_open_close.setBackgroundResource(R.drawable.edit_btn_open);
                                }
                                offTime = offBean.getTime();
                                onTime = onBean.getTime();
//                                show_open.setText(onTime);
//                                show_close.setText(offTime);
                                if (offTime.length()==8) {
                                    show_close.setText(offTime.substring(0,5));
                                }else {
                                    show_close.setText(offTime);
                                }
                                if (onTime.length()==8) {
                                    show_open.setText(onTime.substring(0,5));
                                }else {
                                    show_open.setText(onTime);
                                }
                            }

                        }
                    }

                }
            }
        }
    }

    private void loadResumeData() {


        /**
         * 请求网络拉取数据
         */
        MyHandler.putTask(this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("turn------------" + val);
                if (val != null && val.length() > 0) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (!jsonObject.has("code")) {
                        JsonObject results = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());

                        if (ConstantClassFunction.TURN.equals(results.get(ConstantClassFunction.getCATEGORY()).getAsString()) && device_id.equals(results.get("IMEI").getAsString())) {
                            rusume_Bean = new rusumeBean(null, "", device_id);
                            resumeResultBean = gson.fromJson(val, ResumeResultBean.class);
                            result = resumeResultBean.getResults();
                            turnBean = result.getTurn();
                            offBean = turnBean.getOff();
                            onBean = turnBean.getOn();
                            status = turnBean.getStatus();
                            if ("0".equals(status)) {
                                /**
                                 * 定制开关
                                 */
                                btn_open_close.setChecked(false);
                                btn_open_close.setBackgroundResource(R.drawable.edit_btn_close);
                            } else {
                                /**
                                 * 定制开关
                                 */
                                btn_open_close.setChecked(true);
                                btn_open_close.setBackgroundResource(R.drawable.edit_btn_open);
                            }
                            offTime = offBean.getTime();
                            onTime = onBean.getTime();
                            if (offTime.length()==8) {
                                show_close.setText(offTime.substring(0,5));
                            }else {
                                show_close.setText(offTime);
                            }
                            if (onTime.length()==8) {
                                show_open.setText(onTime.substring(0,5));
                            }else {
                                show_open.setText(onTime);
                            }


                            /**
                             * 保存到数据库
                             */
                            rusume_Bean.setJson(val);

                            if (rsumeDao.loadAll().size() > 0) {
                                for (int i = 0; i < rsumeDao.loadAll().size(); i++) {
                                    rusumeBean rbean = rsumeDao.loadAll().get(i);
                                    if (rbean.getImei().equals(device_id)) {
                                        rsumeDao.delete(rbean);
                                    }
                                }
                            }
                            rsumeDao.insert(rusume_Bean);

                        } else {
                            rusume_Bean = new rusumeBean(null, "", device_id);
                            resumeResultBean = new ResumeResultBean();
                            result = new ResumeResultBean.ResultsBean();

                            turnBean = new ResumeResultBean.ResultsBean.TurnBean();
                            offBean = new ResumeResultBean.ResultsBean.TurnBean.OffBean();
                            onBean = new ResumeResultBean.ResultsBean.TurnBean.OnBean();
                            result.setIMEI(device_id);
                            result.setCategory(ConstantClassFunction.TURN);

                            status = "0";
                            /**
                             * 定制开关
                             */
                            btn_open_close.setChecked(false);
                            btn_open_close.setBackgroundResource(R.drawable.edit_btn_close);
                            offTime = "";
                            onTime = "";
                        }
                    }


                }
            }

        }, HttpUrls.getTurnData() + "&imei=" + device_id, null, Task.TYPE_GET_STRING_NOCACHE, null));
    }


    private void initWheelView() {
        wvTime = (WheelView) findViewById(R.id.wv_am_pm);
        wvHour = (WheelView) findViewById(R.id.wv_hour);
        wvMinute = (WheelView) findViewById(R.id.wv_minute);
//        wvTime.setCyclic(true);
        wvHour.setCyclic(true);
        wvMinute.setCyclic(true);

        getTimeData();
        mTimeAdapter = new CalendarTextAdapter(context, arr_time, curruntTime, maxTextSize, minTextSize);
//        wvTime.setVisibleItems(2);
        wvTime.setViewAdapter(mTimeAdapter);
        wvTime.setCurrentItem(curruntTime);


        getHourData();
        mHourAdapter = new CalendarTextAdapter(context, arr_hour, curruntHour, maxTextSize, minTextSize);
        wvHour.setVisibleItems(7);
        wvHour.setViewAdapter(mHourAdapter);
        wvHour.setCurrentItem(curruntHour);

        getMinuteData();
        mMinuteAdapter = new CalendarTextAdapter(context, arr_minute, curruntMinute, maxTextSize, minTextSize);
        wvMinute.setVisibleItems(7);
        wvMinute.setViewAdapter(mMinuteAdapter);
        wvMinute.setCurrentItem(curruntMinute);


        wvTime.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mTimeAdapter.getItemText(wheel.getCurrentItem());
                selectTime = currentText;
                setTextviewSize(currentText, mTimeAdapter);

            }
        });

        wvTime.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mTimeAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mTimeAdapter);
            }
        });


        wvHour.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mHourAdapter.getItemText(wheel.getCurrentItem());
                selectHour = currentText;
                setTextviewSize(currentText, mHourAdapter);
                curruntHour = Integer.parseInt(currentText);

            }
        });

        wvHour.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mHourAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mHourAdapter);
            }
        });


        wvMinute.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mMinuteAdapter.getItemText(wheel.getCurrentItem());
                selectMinute = currentText;
                setTextviewSize(currentText, mMinuteAdapter);
                curruntMinute = Integer.parseInt(currentText);

            }
        });

        wvMinute.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mMinuteAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mMinuteAdapter);
            }
        });


    }


    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(maxTextSize);
            } else {
                textvew.setTextSize(minTextSize);
            }
        }
    }


    private ImageButton ibtn_back;
    private TextView tv_title;

    private void initTitle() {

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title.setText("定时开关机");

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ibtn_back:
//                upData();
                finish();
                break;

            case R.id.rlt_open_time:

                showSettingOpen();

                break;
            case R.id.rlt_close_time:

                showSettingClose();
                break;
            case R.id.btn_open_close://开关机
                offTime = show_close.getText().toString().trim();
                onTime = show_open.getText().toString().trim();

                if ("".equals(onTime) && "".equals(offTime)) {
                    BaseApplication.getInstances().toast(ResumeByAlarmActivity.this,"请设置时间！");

                } else {
                    result1 = new ResumeResultBean.ResultsBean();
                    turnBean1 = new ResumeResultBean.ResultsBean.TurnBean();
                    offBean1 = new ResumeResultBean.ResultsBean.TurnBean.OffBean();
                    onBean1 = new ResumeResultBean.ResultsBean.TurnBean.OnBean();

                    if ("0".equals(status)) {
                        turnBean1.setStatus("1");
                    } else {
                        turnBean1.setStatus("0");
                    }
                    if (!onTime.equals("")){
                        onBean1.setTime(onTime);
                        turnBean1.setOn(onBean1);
                    }

                    if (!offTime.equals("")) {
                        offBean1.setTime(offTime);
                        turnBean1.setOff(offBean1);
                    }

                    result1.setIMEI(device_id); // TODO: 2017/3/10
                    result1.setCategory(ConstantClassFunction.TURN);

                    result1.setTurn(turnBean1);
                    String json2 = gson.toJson(result1);

                    LogUtil.e("json2-------" + json2);

                    if (!BaseApplication.getInstances().isSDKConnected()) {
                        BaseApplication.getInstances().toastLong(ResumeByAlarmActivity.this,"已与手表服务器断开连接!");
                        return;
                    }
                    //提交到服务器
                    BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json2.getBytes(), json2.length());
                }
                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        //取消EventBus
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRunTimeEvent(RunTimeEvent event) {

        String json = event.getJson();

        JsonObject jsonObject = new GsonParseUtil(json).getJsonObject();
        if (jsonObject.has(ConstantClassFunction.getRESULTS())) {
            JsonObject object = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
            if (object.has("Code") && "200".equals(object.get("Code").getAsString())) {

                LogUtil.e("Turn----------------" + turnBean1.getStatus());
                if ("0".equals(turnBean1.getStatus())) {
                    /**
                     * 定制开关
                     */
                    status = "0";
                    btn_open_close.setChecked(false);
                    btn_open_close.setBackgroundResource(R.drawable.edit_btn_close);

                } else {
                    status = "1";
                    btn_open_close.setChecked(true);
                    btn_open_close.setBackgroundResource(R.drawable.edit_btn_open);
                }


            } else if (object.has("Code") && "10001".equals(object.get("Code").getAsString())) {
                BaseApplication.getInstances().toast(ResumeByAlarmActivity.this,"设备不在线");
            }

        }
    }

    private void upData() {

        off = show_close.getText().toString().trim();
        on = show_open.getText().toString().trim();
        if (!(on.equals(on_time) && off.equals(off_time))) {
            Intent intent = new Intent();
            intent.putExtra("on", on);
            intent.putExtra("off", off);
            setResult(RESULT_OK, intent);
        }

        finish();


    }

//    private void upData() {
//
//        turnBean.setOff(offBean);
//        turnBean.setOn(onBean);
//        turnBean.setStatus("0");
//        result.setCategory("Turn");
//        result.setIMEI("352315052834187"); // TODO: 2017/3/10
//        result.setTurn(turnBean);
//        String json = gson.toJson(result);
//        LogUtil.e("turnjson-------"+json);
//
//        BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong("352315052834187"), json.getBytes(), json.length());
//
//        //提交到数据库
//        resumeResultBean.setResult(result);
//        String json1 = gson.toJson(resumeResultBean);
//        bean.setJson(json1);
//        if (list.size()>0){
//            dao.update(bean);
//        }else {
//            dao.insert(bean);
//        }
//        if (!BaseApplication.getInstances().isSDKConnected()) {
//            BaseApplication.getInstances().toastLong("已与手表服务器断开连接!");
//            return;
//        }
//
//
//
//    }

    Handler mHandler = new Handler();

    private void showSettingClose() {


        tv_ps.setVisibility(View.GONE);
        set_time.setVisibility(View.VISIBLE);

        wvHour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        wvTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        wvMinute.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        title.setText("手表关机时间");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);

            }
        });

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_ps.setVisibility(View.VISIBLE);
                set_time.setVisibility(View.GONE);
                if (selectTime.equals("上午")) {
                    if (Integer.parseInt(selectHour) > 9) {
                        show_close.setText(selectHour + ":" + selectMinute);
                    } else {
                        show_close.setText("0" + selectHour + ":" + selectMinute);
                    }

//                    offBean.setTime(selectHour + ":" + selectMinute);

                } else {
                    show_close.setText((Integer.parseInt(selectHour) + 12) + ":" + selectMinute);
//                    offBean.setTime((Integer.parseInt(selectHour) + 12) + ":" + selectMinute);
                }

                if (status.equals("1")) {
                    result1 = new ResumeResultBean.ResultsBean();
                    turnBean1 = new ResumeResultBean.ResultsBean.TurnBean();
                    offBean1 = new ResumeResultBean.ResultsBean.TurnBean.OffBean();
                    onBean1 = new ResumeResultBean.ResultsBean.TurnBean.OnBean();

                    turnBean1.setStatus(status);
                    offTime = show_close.getText().toString().trim();
                    if (!offTime.equals("")) {
                        offBean1.setTime(offTime);
                        turnBean1.setOff(offBean1);
                    }

                    if (!onTime.equals("")) {
                        onBean1.setTime(onTime);
                        turnBean1.setOn(onBean1);
                    }

                    result1.setIMEI(device_id); // TODO: 2017/3/10
                    result1.setCategory(ConstantClassFunction.TURN);

                    result1.setTurn(turnBean1);
                    String json2 = gson.toJson(result1);

                    LogUtil.e("json2-------" + json2);

                    if (!BaseApplication.getInstances().isSDKConnected()) {
                        BaseApplication.getInstances().toastLong(ResumeByAlarmActivity.this,"已与手表服务器断开连接!");
                        return;
                    }
                    //提交到服务器
                    BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json2.getBytes(), json2.length());
                }


            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_ps.setVisibility(View.VISIBLE);
                set_time.setVisibility(View.GONE);
            }
        });

    }

    private void showSettingOpen() {

        tv_ps.setVisibility(View.GONE);
        set_time.setVisibility(View.VISIBLE);


        wvHour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        wvTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        wvMinute.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        title.setText("手表开机时间");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);

            }
        });

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_ps.setVisibility(View.VISIBLE);
                set_time.setVisibility(View.GONE);
                if (selectTime.equals("上午")) {
                    if (Integer.parseInt(selectHour) > 9) {
                        show_open.setText(selectHour + ":" + selectMinute);
                    } else {
                        show_open.setText("0" + selectHour + ":" + selectMinute);
                    }

//                    onBean.setTime(selectHour + ":" + selectMinute);

                } else {
                    show_open.setText((Integer.parseInt(selectHour) + 12) + ":" + selectMinute);
//                    onBean.setTime((Integer.parseInt(selectHour) + 12) + ":" + selectMinute);
                }

                if (status.equals("1")) {
                    result1 = new ResumeResultBean.ResultsBean();
                    turnBean1 = new ResumeResultBean.ResultsBean.TurnBean();
                    offBean1 = new ResumeResultBean.ResultsBean.TurnBean.OffBean();
                    onBean1 = new ResumeResultBean.ResultsBean.TurnBean.OnBean();

                    turnBean1.setStatus(status);
                    onTime = show_open.getText().toString().trim();
                    if (!onTime.equals("")) {
                        onBean1.setTime(onTime);
                        turnBean1.setOn(onBean1);
                    }

                    if (!offTime.equals("")) {
                        offBean1.setTime(offTime);
                        turnBean1.setOff(offBean1);
                    }

                    result1.setIMEI(device_id); // TODO: 2017/3/10
                    result1.setCategory(ConstantClassFunction.TURN);

                    result1.setTurn(turnBean1);
                    String json2 = gson.toJson(result1);

                    LogUtil.e("json2-------" + json2);

                    if (!BaseApplication.getInstances().isSDKConnected()) {
                        BaseApplication.getInstances().toastLong(ResumeByAlarmActivity.this,"已与手表服务器断开连接!");
                        return;
                    }
                    //提交到服务器
                    BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json2.getBytes(), json2.length());
                }


            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_ps.setVisibility(View.VISIBLE);
                set_time.setVisibility(View.GONE);
            }
        });

    }

    /****************************
     * 数据模块
     ***************************/

    private void getTimeData() {
        arr_time.clear();
        arr_time.add("上午");
        arr_time.add("下午");
    }

    private void getHourData() {
        arr_hour.clear();
        for (int i = 0; i < 12; i++) {
            arr_hour.add(i + "");
        }

    }

    private void getMinuteData() {
        arr_minute.clear();
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                arr_minute.add("0" + i);
            } else {
                arr_minute.add(i + "");
            }

        }

    }


    /*********************
     * adapter
     *********************/

    private class CalendarTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize,
                                      int minsize) {
            super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getResumData();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            upData();
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
