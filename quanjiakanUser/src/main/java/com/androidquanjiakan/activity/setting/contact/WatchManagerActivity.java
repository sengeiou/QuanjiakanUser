package com.androidquanjiakan.activity.setting.contact;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidquanjiakan.activity.index.phonedocter.ConsultantActivity_PhoneDoctor;
import com.androidquanjiakan.activity.index.watch.DefaultWatcthDeviceListActivity;
import com.androidquanjiakan.activity.index.watch_child.ChildWatchCardActivity;
import com.androidquanjiakan.activity.index.watch_child.WatchChildEntryActivity;
import com.androidquanjiakan.activity.index.watch_child.elder.ShortcutKeyActivity;
import com.androidquanjiakan.activity.setting.contact.bean.RunTimeEvent;
import com.androidquanjiakan.activity.setting.contact.bean.RunTimeResultEvent;
import com.androidquanjiakan.activity.setting.contact.bean.WatchInfoActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.RunTimeCategoryBean;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.result.ResumeResultBean;
import com.androidquanjiakan.result.RuntimeResultBean;
import com.androidquanjiakan.util.LogUtil;
import com.example.greendao.dao.RunTimeCategoryBeanDao;
import com.example.greendao.dao.rusumeBeanDao;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * 作者：Administrator on 2017/2/20 17:58
 * <p>手表管理
 * 邮箱：liuzj@hi-board.com
 */
public class WatchManagerActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout bund_unbund;
    private RelativeLayout rlt_open_close;
    private RelativeLayout rlt_sound_set;
    private RelativeLayout rlt_screen_set;
    private TextView tv_screen_time;
    private TextView tv_name_card;
    private RelativeLayout rlt_baby_card;
    private RelativeLayout rlt_default_device;
    private RelativeLayout rlt_watch_info;
    private RelativeLayout rlt_electricity;
    private RelativeLayout rlt_auto_connect;
    private RelativeLayout rlt_location_mode;
    private RelativeLayout rlt_scene_mode;
    private RelativeLayout rlt_quick_key;
//    private ToggleButton btn_open_close;
    private ToggleButton btn_on_line;
    private ToggleButton btn_watch_miss;
//    private ToggleButton btn_electricity;

    //    private List<RunTimeCategoryBean> list;//数据库数据
    private RunTimeCategoryBeanDao dao;
    private RunTimeCategoryBean bean;//数据库实体
    private Gson gson;
    private RuntimeResultBean runtimeResultBean;
    private RuntimeResultBean.ResultsBean resultBean;
    private RuntimeResultBean.ResultsBean.RunTimeBean runTime;
    //352315052834187
    private String device_id;
//    private rusumeBeanDao rsumeDao;
//    //    private List<rusumeBean> list1;
//    private com.androidquanjiakan.entity.rusumeBean rusume_Bean;
//    private String rusumeBeanJson;
//    private ResumeResultBean.ResultsBean.TurnBean turnBean;
//    private ResumeResultBean.ResultsBean.TurnBean.OffBean offBean;
//    private ResumeResultBean.ResultsBean.TurnBean.OnBean onBean;
//    private ResumeResultBean resumeResultBean;
//    private ResumeResultBean.ResultsBean result;
//    private String offTime;
//    private String onTime;
//
//    private static final int REQUEST_CODE_RESUME = 0x01;
    private static final int REQUEST_CODE_WATCHBELL = 0x02;
    //    private String on;
//    private String off;
    private String status;
    private String panel;
    private String watchBell;
    private int button = -1;
    private RuntimeResultBean.ResultsBean resultBean1;
    private RuntimeResultBean.ResultsBean.RunTimeBean runTime1;
    private String watchName;
    private ResumeResultBean.ResultsBean result1;
    private ResumeResultBean.ResultsBean.TurnBean turnBean1;
    private ResumeResultBean.ResultsBean.TurnBean.OffBean offBean1;
    private ResumeResultBean.ResultsBean.TurnBean.OnBean onBean1;
    public static final String TYPE = "type";
    public static final String WATCH_PHONENUM = "watch_phonenum";
    private String type;
    private ImageView iv_old_more;
    private String watch_phonenum;
    private String mode;
    private Dialog remindDialog;
    private String text;
    private TextView tv_mode1;
    private TextView tv_mode2;
    private TextView tv_mode4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_manage);
        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        watchName = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_NAME);
        type = getIntent().getStringExtra(TYPE);
        watch_phonenum = getIntent().getStringExtra(WATCH_PHONENUM);
        LogUtil.e("device_id----" + device_id);
        if (device_id == null || device_id.length() < 1) {
            BaseApplication.getInstances().toast(WatchManagerActivity.this,"传入参数异常!");
            finish();
            return;
        }

        dao = getRuntimeDao();
//        rsumeDao = getRsumeDao();
        gson = new Gson();
        initTitle();

        initView();
//        initData();
//        getResumData();


    }

//    private void getResumData() {
//        if (type.equals("child")) {
//            if (NetUtil.isNetworkAvailable(this)) {
//                loadResumeData();
//            } else {
//
//                if (rsumeDao.loadAll().size() > 0) {//如果数据库有数据  拿出来展示
////取出json
//                    for (int i = 0; i < rsumeDao.loadAll().size(); i++) {
//                        if (device_id.equals(rsumeDao.loadAll().get(i).getImei())) {
//                            rusume_Bean = rsumeDao.loadAll().get(i);
//                            rusumeBeanJson = rsumeDao.loadAll().get(i).getJson();
//                            LogUtil.e("rusumeBeanJson--------" + rusumeBeanJson);
//                            resumeResultBean = gson.fromJson(rusumeBeanJson, ResumeResultBean.class);
//                            result = resumeResultBean.getResults();
//                            if (ConstantClassFunction.TURN.equals(this.result.getCategory())) {
//                                turnBean = result.getTurn();
//                                offBean = turnBean.getOff();
//                                onBean = turnBean.getOn();
//                                status = turnBean.getStatus();
//                                if ("0".equals(this.status)) {
//                                    /**
//                                     * 定制开关
//                                     */
//                                    btn_open_close.setChecked(false);
//                                    btn_open_close.setBackgroundResource(R.drawable.edit_btn_close);
//                                } else {
//                                    /**
//                                     * 定制开关
//                                     */
//                                    btn_open_close.setChecked(true);
//                                    btn_open_close.setBackgroundResource(R.drawable.edit_btn_open);
//                                }
//                                offTime = offBean.getTime();
//                                onTime = onBean.getTime();
//                            }
//
//                        }
//                    }
//
//                }
//            }
//        }


//        if (rsumeDao.loadAll().size() > 0) {//如果数据库有数据  拿出来展示
////取出json
//            for (int i = 0; i < rsumeDao.loadAll().size(); i++) {
//                if (device_id.equals(rsumeDao.loadAll().get(i).getImei())) {
//                    rusume_Bean = rsumeDao.loadAll().get(i);
//                    rusumeBeanJson = rsumeDao.loadAll().get(i).getJson();
//                    LogUtil.e("rusumeBeanJson--------" + rusumeBeanJson);
//                    resumeResultBean = gson.fromJson(rusumeBeanJson, ResumeResultBean.class);
//                    result = resumeResultBean.getResults();
//                    if (ConstantClassFunction.TURN.equals(this.result.getCategory())) {
//                        turnBean = result.getTurn();
//                        offBean = turnBean.getOff();
//                        onBean = turnBean.getOn();
//                        status = turnBean.getStatus();
//                        if ("0".equals(this.status)) {
//                            /**
//                             * 定制开关
//                             */
//                            btn_open_close.setChecked(false);
//                            btn_open_close.setBackgroundResource(R.drawable.edit_btn_close);
//                        } else {
//                            /**
//                             * 定制开关
//                             */
//                            btn_open_close.setChecked(true);
//                            btn_open_close.setBackgroundResource(R.drawable.edit_btn_open);
//                        }
//                        offTime = offBean.getTime();
//                        onTime = onBean.getTime();
//                    }
//                    break;
//
//                } else if (i == rsumeDao.loadAll().size() - 1) {
//                    loadResumeData();
//                }
//            }
//
//        } else {
//            /**
//             * 没有设置任何数据的情况
//             */
//            loadResumeData();
//        }
//    }

//    private void loadResumeData() {
//
//
//        /**
//         * 请求网络拉取数据
//         */
//        MyHandler.putTask(WatchManagerActivity.this, new Task(new HttpResponseInterface() {
//            @Override
//            public void handMsg(String val) {
//                LogUtil.e("turn------------" + val);
//                if (val != null && val.length() > 0) {
//                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
//                    if (!jsonObject.has("code")) {
//                        JsonObject results = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
//
//                        if (ConstantClassFunction.TURN.equals(results.get(ConstantClassFunction.getCATEGORY()).getAsString()) && device_id.equals(results.get("IMEI").getAsString())) {
//                            rusume_Bean = new rusumeBean(null, "", device_id);
//                            resumeResultBean = gson.fromJson(val, ResumeResultBean.class);
//                            result = resumeResultBean.getResults();
//                            turnBean = result.getTurn();
//                            offBean = turnBean.getOff();
//                            onBean = turnBean.getOn();
//                            status = turnBean.getStatus();
//                            if ("0".equals(status)) {
//                                /**
//                                 * 定制开关
//                                 */
//                                btn_open_close.setChecked(false);
//                                btn_open_close.setBackgroundResource(R.drawable.edit_btn_close);
//                            } else {
//                                /**
//                                 * 定制开关
//                                 */
//                                btn_open_close.setChecked(true);
//                                btn_open_close.setBackgroundResource(R.drawable.edit_btn_open);
//                            }
//                            offTime = offBean.getTime();
//                            onTime = onBean.getTime();
//                            /**
//                             * 保存到数据库
//                             */
//                            rusume_Bean.setJson(val);
//
//                            if (rsumeDao.loadAll().size() > 0) {
//                                for (int i = 0; i < rsumeDao.loadAll().size(); i++) {
//                                    rusumeBean rbean = rsumeDao.loadAll().get(i);
//                                    if (rbean.getImei().equals(device_id)) {
//                                        rsumeDao.delete(rbean);
//                                    }
//                                }
//                            }
//                            rsumeDao.insert(rusume_Bean);
//
//                        } else {
//                            rusume_Bean = new rusumeBean(null, "", device_id);
//                            resumeResultBean = new ResumeResultBean();
//                            result = new ResumeResultBean.ResultsBean();
//
//                            turnBean = new ResumeResultBean.ResultsBean.TurnBean();
//                            offBean = new ResumeResultBean.ResultsBean.TurnBean.OffBean();
//                            onBean = new ResumeResultBean.ResultsBean.TurnBean.OnBean();
//                            result.setIMEI(device_id);
//                            result.setCategory(ConstantClassFunction.TURN);
//
//                            status = "0";
//                            /**
//                             * 定制开关
//                             */
//                            btn_open_close.setChecked(false);
//                            btn_open_close.setBackgroundResource(R.drawable.edit_btn_close);
//                            offTime = "";
//                            onTime = "";
//                        }
//                    }
//
//
//                }
//            }
//
//        }, HttpUrls.getTurnData() + "&imei=" + device_id, null, Task.TYPE_GET_STRING_NOCACHE, null));
//    }

    /**
     * 获取运行时参数的数据库操作对象
     * @return
     */
    private RunTimeCategoryBeanDao getRuntimeDao() {
        return BaseApplication.getInstances().getDaoInstant().getRunTimeCategoryBeanDao();

    }


    /*****************************  初始化  *******************************/

    private void initView() {


        bund_unbund = (RelativeLayout) findViewById(R.id.rlt_bund_unbund);
        bund_unbund.setOnClickListener(this);

        rlt_open_close = (RelativeLayout) findViewById(R.id.rlt_open_close);
        rlt_open_close.setOnClickListener(this);

        rlt_sound_set = (RelativeLayout) findViewById(R.id.rlt_sound_set);
        rlt_sound_set.setOnClickListener(this);
        //亮屏
        rlt_screen_set = (RelativeLayout) findViewById(R.id.rlt_screen_set);
        rlt_screen_set.setOnClickListener(this);
        //宝贝名片
        rlt_baby_card = (RelativeLayout) findViewById(R.id.rlt_baby_card);
        rlt_baby_card.setOnClickListener(this);
        //默认手表设备
        rlt_default_device = (RelativeLayout) findViewById(R.id.rlt_default_device);
        rlt_default_device.setOnClickListener(this);
        //手表信息
        rlt_watch_info = (RelativeLayout) findViewById(R.id.rlt_watch_info);
        //定位模式
        rlt_location_mode = (RelativeLayout) findViewById(R.id.rlt_location_mode);
        rlt_location_mode.setOnClickListener(this);

        //情景模式
        rlt_scene_mode = (RelativeLayout) findViewById(R.id.rlt_scene_mode);
        rlt_scene_mode.setOnClickListener(this);
        //预留电量
        rlt_electricity = (RelativeLayout) findViewById(R.id.rlt_electricity);
        //自动接通
        rlt_auto_connect = (RelativeLayout) findViewById(R.id.rlt_auto_connect);
        //快捷键
        rlt_quick_key = (RelativeLayout) findViewById(R.id.rlt_quick_key);
        rlt_quick_key.setOnClickListener(this);


        //button系列

//        btn_open_close = (ToggleButton) findViewById(R.id.btn_open_close);
        btn_on_line = (ToggleButton) findViewById(R.id.btn_on_line);
        btn_watch_miss = (ToggleButton) findViewById(R.id.btn_watch_miss);
//        btn_electricity = (ToggleButton) findViewById(R.id.btn_electricity);
//        btn_open_close.setOnClickListener(this);
        btn_on_line.setOnClickListener(this);
        btn_watch_miss.setOnClickListener(this);


        iv_old_more = (ImageView) findViewById(R.id.iv_old_more);

        if (type.equals("child")) {
            rlt_auto_connect.setVisibility(View.VISIBLE);
            rlt_location_mode.setVisibility(View.GONE);
            rlt_electricity.setVisibility(View.GONE);
            rlt_scene_mode.setVisibility(View.VISIBLE);

//            btn_open_close.setVisibility(View.VISIBLE);
//            iv_old_more.setVisibility(View.GONE);
        } else if (type.equals("old")) {
            rlt_auto_connect.setVisibility(View.GONE);
            rlt_location_mode.setVisibility(View.VISIBLE);
            rlt_electricity.setVisibility(View.VISIBLE);
            rlt_scene_mode.setVisibility(View.GONE);

//            btn_open_close.setVisibility(View.GONE);
//            iv_old_more.setVisibility(View.VISIBLE);
        }


        rlt_watch_info.setOnClickListener(this);

        tv_screen_time = (TextView) findViewById(R.id.tv_screen_time);
        tv_name_card = (TextView) findViewById(R.id.tv_name_card);
        if (watchName.contains("%")) {
            try {
                tv_name_card.setText(URLDecoder.decode(watchName, "utf-8") + "名片");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            tv_name_card.setText(watchName + "名片");
        }


    }


    private ImageButton ibtn_back;
    private TextView tv_title;

    private void initTitle() {

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title.setText("手表管理");

    }

    /**
     * 初始化数据  （目前使用的策略是有网络情况下加载网络，网络状况不好时拿数据库里的数据）
     */
    private void initData() {

        if (type.equals("child")) {//儿童
            if (NetUtil.isNetworkAvailable(this)) {//网络可用
                loadRunTimeData();
            } else {
                BaseApplication.getInstances().toast(WatchManagerActivity.this,"网络连接不可用，数据刷新失败！");
                if (dao.loadAll().size() > 0) {
                    for (int i = 0; i < dao.loadAll().size(); i++) {
                        if (device_id.equals(dao.loadAll().get(i).getImei())) {
                            bean = dao.loadAll().get(i);
                            String json = dao.loadAll().get(i).getJson();
                            LogUtil.e("runtimeResultBean-----" + json);
                            runtimeResultBean = gson.fromJson(json, RuntimeResultBean.class);
                            resultBean = runtimeResultBean.getResults();
                            if (ConstantClassFunction.RUNTIME.equals(resultBean.getCategory())) {
                                runTime = resultBean.getRunTime();

                                if (runTime.getAutoConnection() != null && "1".equals(runTime.getAutoConnection())) {
                                    btn_on_line.setChecked(true);
                                    btn_on_line.setBackgroundResource(R.drawable.edit_btn_open);
                                } else {
                                    btn_on_line.setChecked(false);
                                    btn_on_line.setBackgroundResource(R.drawable.edit_btn_close);
                                }

                                if (runTime.getLossReport() != null && "1".equals(runTime.getLossReport())) {
                                    btn_watch_miss.setChecked(true);
                                    btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_open);
                                } else {
                                    btn_watch_miss.setChecked(false);
                                    btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_close);
                                }

                                if (runTime.getLightPanel() != null) {
                                    panel = runTime.getLightPanel();
                                    tv_screen_time.setText(panel + "s");

                                } else {
                                    panel = "";
                                    tv_screen_time.setText(panel);
                                }
                                if (runTime.getWatchBell() != null) {
                                    watchBell = runTime.getWatchBell();

                                } else {
                                    watchBell = "00,00";//默认0  关
                                }

                            }

                        }

                    }
                }


            }

//        list = dao.loadAll();
//        if (dao.loadAll().size() > 0) {
////            bean = dao.loadAll().get(0);
//
//            for (int i = 0; i < dao.loadAll().size(); i++) {
//                if (device_id.equals(dao.loadAll().get(i).getImei())) {
//                    bean = dao.loadAll().get(i);
//                    String json = dao.loadAll().get(i).getJson();
//                    LogUtil.e("runtimeResultBean-----" + json);
//                    runtimeResultBean = gson.fromJson(json, RuntimeResultBean.class);
//                    resultBean = runtimeResultBean.getResults();
//                    if (ConstantClassFunction.RUNTIME.equals(resultBean.getCategory())) {
//                        runTime = resultBean.getRunTime();
//
//                        if (runTime.getAutoConnection() != null && "1".equals(runTime.getAutoConnection())) {
//                            btn_on_line.setChecked(true);
//                            btn_on_line.setBackgroundResource(R.drawable.edit_btn_open);
//                        } else {
//                            btn_on_line.setChecked(false);
//                            btn_on_line.setBackgroundResource(R.drawable.edit_btn_close);
//                        }
//
//                        if (runTime.getLossReport() != null && "1".equals(runTime.getLossReport())) {
//                            btn_watch_miss.setChecked(true);
//                            btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_open);
//                        } else {
//                            btn_watch_miss.setChecked(false);
//                            btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_close);
//                        }
//
//                        if (runTime.getLightPanel() != null) {
//                            panel = runTime.getLightPanel();
//                            tv_screen_time.setText(panel + "s");
//
//                        } else {
//                            panel = "";
//                            tv_screen_time.setText(panel);
//                        }
//                        if (runTime.getWatchBell() != null) {
//                            watchBell = runTime.getWatchBell();
//
//                        } else {
//                            watchBell = "00,00";//默认0  关
//                        }
//
//                    }
//
//                    break;
//                } else if (i == dao.loadAll().size() - 1) {
//                    LogUtil.e("有数据------------------------------------");
//                    loadRunTimeData();
//                }
//            }
//
//        } else {//没有数据时   1.从数据库拉取  2.创建
//            LogUtil.e("无数据------------------------------------");
//            loadRunTimeData();
//
//
//        }


        }
    }


    /*****************************  初始化  *******************************/



    /**
     *
     * 网络加载数据
     */
    private void loadRunTimeData() {

        MyHandler.putTask(WatchManagerActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("runtime------------" + val);
                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();


                if (!jsonObject.has("code")) {
                    JsonObject results = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());

                    if (ConstantClassFunction.RUNTIME.equals(results.get(ConstantClassFunction.getCATEGORY()).getAsString()) && device_id.equals(results.get("IMEI").getAsString())) {
                        bean = new RunTimeCategoryBean(null, "", device_id);
                        runtimeResultBean = gson.fromJson(val, RuntimeResultBean.class);
                        resultBean = runtimeResultBean.getResults();
                        runTime = resultBean.getRunTime();
                        if (runTime != null) {
                            if (runTime.getAutoConnection() != null && "1".equals(runTime.getAutoConnection())) {
                                btn_on_line.setChecked(true);
                                btn_on_line.setBackgroundResource(R.drawable.edit_btn_open);
                            } else {
                                btn_on_line.setChecked(false);
                                btn_on_line.setBackgroundResource(R.drawable.edit_btn_close);
                            }

                            if (runTime.getLossReport() != null && "1".equals(runTime.getLossReport())) {
                                btn_watch_miss.setChecked(true);
                                btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_open);
                            } else {
                                btn_watch_miss.setChecked(false);
                                btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_close);
                            }


                            if (runTime.getLightPanel() != null) {
                                panel = runTime.getLightPanel();
                                tv_screen_time.setText(panel + "s");

                            } else {
                                panel = "";
                                tv_screen_time.setText(panel);
                            }
                            if (runTime.getWatchBell() != null) {
                                watchBell = runTime.getWatchBell();

                            } else {
                                watchBell = "00,00";//默认0  关
                            }
                        }
                        /**
                         * 保存到数据库
                         */
                        bean.setJson(val);
                        //如果数据库中已经有了就把原来的数据删除，这里只保存最新的数据
                        if (dao.loadAll().size() > 0) {
                            for (int i = 0; i < dao.loadAll().size(); i++) {
                                RunTimeCategoryBean bean1 = dao.loadAll().get(i);
                                if (bean1.getImei().equals(device_id)) {
                                    dao.delete(bean1);
                                }
                            }
                        }

                        dao.insert(bean);
                    }
                } else {
                    //第一次创建
                    bean = new RunTimeCategoryBean(null, "", device_id);
                    runtimeResultBean = new RuntimeResultBean();
                    resultBean = new RuntimeResultBean.ResultsBean();
                    resultBean.setIMEI(device_id);
                    resultBean.setCategory(ConstantClassFunction.RUNTIME);
                    runTime = new RuntimeResultBean.ResultsBean.RunTimeBean();
                    btn_on_line.setChecked(true);
                    btn_on_line.setBackgroundResource(R.drawable.edit_btn_open);
                    btn_watch_miss.setChecked(true);
                    btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_open);
                    panel = "";
                    tv_screen_time.setText(panel);
                    watchBell = "00,00";//默认0  关
                }


            }
        }, HttpUrls.getRunTimeData() + "&imei=" + device_id, null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(WatchManagerActivity.this, "获取数据中...")));
    }

    /**
     * 接收向手表发送请求后的事件（可以做到本界面的实时更新）
     param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRunTimeEvent(RunTimeEvent event) {
        String json = event.getJson();

        JsonObject jsonObject = new GsonParseUtil(json).getJsonObject();
        if (jsonObject.has(ConstantClassFunction.getRESULTS())) {
            JsonObject object = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
            if (object.has("Code") && "200".equals(object.get("Code").getAsString())) {
                switch (button) {
//                    case 1:
//
//                        LogUtil.e("Turn----------------" + turnBean1.getStatus());
//                        if ("0".equals(turnBean1.getStatus())) {
//                            /**
//                             * 定制开关
//                             */
//                            status = "0";
//                            btn_open_close.setChecked(false);
//                            btn_open_close.setBackgroundResource(R.drawable.edit_btn_close);
//
//                        } else {
//                            status = "1";
//                            btn_open_close.setChecked(true);
//                            btn_open_close.setBackgroundResource(R.drawable.edit_btn_open);
//                        }
//                        offBean.setTime(offBean1.getTime());
//                        onBean.setTime(onBean1.getTime());
//                        onTime = onBean1.getTime();
//                        offTime = offBean1.getTime();
//                        LogUtil.e("------------------------------" + onTime + "----------" + offTime);
////                        saveData2TurnDB();
//                        break;

                    case 2:
                        LogUtil.e("AutoConnection---------------------------");
                        runTime.setAutoConnection(runTime1.getAutoConnection());
                        if (runTime.getAutoConnection() != null && "1".equals(runTime.getAutoConnection())) {
                            btn_on_line.setChecked(true);
                            btn_on_line.setBackgroundResource(R.drawable.edit_btn_open);
                        } else {
                            btn_on_line.setChecked(false);
                            btn_on_line.setBackgroundResource(R.drawable.edit_btn_close);
                        }

//                        saveData2DB();
                        break;

                    case 3:
                        LogUtil.e("LossReport--------------------------");
                        runTime.setLossReport(runTime1.getLossReport());
                        if (runTime.getLossReport() != null && "1".equals(runTime.getLossReport())) {
                            btn_watch_miss.setChecked(true);
                            btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_open);
                        } else {
                            btn_watch_miss.setChecked(false);
                            btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_close);
                        }

//                        saveData2DB();
                        break;

                    case 4:
                        LogUtil.e("LightPanel-----------------------------");
                        runTime.setLightPanel(runTime1.getLightPanel());
                        tv_screen_time.setText(runTime.getLightPanel() + "s");

//                        saveData2DB();
                        break;

                    case 5:
                        LogUtil.e("WatchBell-------------------------" + runTime1.getWatchBell());

                        runTime.setWatchBell(runTime1.getWatchBell());
                        watchBell = runTime1.getWatchBell();
//                        saveData2DB();
                        break;
                }
            } else if (object.has("Code") && "10001".equals(object.get("Code").getAsString())) {
                BaseApplication.getInstances().toast(WatchManagerActivity.this,"设备不在线");
            }

            button = -1;
        }
    }

    /**
     * 广播接收
     *这里处理广播数据  （当别人进行设置时本界面可以实时刷新，注意要对广播进行区分）
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRunTimeResultEvent(RunTimeResultEvent event) {
//        {"Results":{"IMEI":"355637053995130","Category":"TimeTables","TimeTables":[{"Morning":{"Status":"Off","StartTime":"08:30","EndTime":"11:30"},"Afternoon":{"Status":"Off","StartTime":"14:00","EndTime":"16:30"}}]}}
        LogUtil.e("----广播数据------" + event.getJson());
        JsonObject jsonObject = new GsonParseUtil(event.getJson()).getJsonObject();
        if (jsonObject.has(ConstantClassFunction.getRESULTS())) {
            JsonObject result = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
            if (device_id.equals(result.get("IMEI").getAsString())) {
                String category = result.get(ConstantClassFunction.getCATEGORY()).getAsString();
                if (ConstantClassFunction.RUNTIME.equals(category)) {
                    RuntimeResultBean runtimeResultBean2 = gson.fromJson(event.getJson(), RuntimeResultBean.class);
                    RuntimeResultBean.ResultsBean result2 = runtimeResultBean2.getResults();
                    RuntimeResultBean.ResultsBean.RunTimeBean runTime2 = result2.getRunTime();
                    String autoConnection = runTime2.getAutoConnection();

                    /**
                     * 自动接通
                     */
                    if (autoConnection != null) {
                        if ("1".equals(autoConnection)) {
                            btn_on_line.setChecked(true);
                            btn_on_line.setBackgroundResource(R.drawable.edit_btn_open);
                        } else {
                            btn_on_line.setChecked(false);
                            btn_on_line.setBackgroundResource(R.drawable.edit_btn_close);
                        }
                        runTime.setAutoConnection(autoConnection);
                    }

                    /**
                     * 亮屏时间
                     */

                    String lightPanel = runTime2.getLightPanel();
                    if (lightPanel != null) {
                        tv_screen_time.setText(lightPanel + "s");
                        runTime.setLightPanel(lightPanel);
                    }

                    /**
                     * 手机挂丢
                     */
                    String lossReport = runTime2.getLossReport();
                    if (lossReport != null) {
                        if ("1".equals(lossReport)) {
                            btn_watch_miss.setChecked(true);
                            btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_open);
                        } else {
                            btn_watch_miss.setChecked(false);
                            btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_close);
                        }
                        runTime.setLossReport(lossReport);
                    }

                    /**
                     * 手表声音
                     */

                    String watch = runTime2.getWatchBell();
                    if (watch != null) {
                        watchBell = watch;
                        runTime.setWatchBell(watch);
                    }

//                    saveData2DB();

//                } else if (ConstantClassFunction.TURN.equals(category)) {
//                    ResumeResultBean resumeResultBean = gson.fromJson(event.getJson(), ResumeResultBean.class);
//                    ResumeResultBean.ResultsBean result1 = resumeResultBean.getResults();
//                    ResumeResultBean.ResultsBean.TurnBean turn = result1.getTurn();
//                    ResumeResultBean.ResultsBean.TurnBean.OffBean off = turn.getOff();
//                    ResumeResultBean.ResultsBean.TurnBean.OnBean on = turn.getOn();
//                    String sta = turn.getStatus();
//
//                    if ("1".equals(sta)) {
//                        btn_open_close.setChecked(true);
//                        btn_open_close.setBackgroundResource(R.drawable.edit_btn_open);
//
//                    } else {
//                        btn_open_close.setChecked(false);
//                        btn_open_close.setBackgroundResource(R.drawable.edit_btn_close);
//                    }
//                    status = sta;
//                    offTime = off.getTime();
//                    onTime = on.getTime();
//                    offBean.setTime(offTime);
//                    onBean.setTime(onTime);
////                    saveData2TurnDB();
//                }


                }
            }
        }


    }

//    private void saveData2TurnDB() {
//
//        turnBean.setOff(offBean);
//        turnBean.setOn(onBean);
//        turnBean.setStatus(turnBean1.getStatus());
//        result.setTurn(turnBean);
//        //提交到数据库
//        resumeResultBean.setResults(result);
//        String json1 = gson.toJson(resumeResultBean);
//        rusume_Bean.setJson(json1);
//        LogUtil.e("保存Turn---------" + json1);
//        if (rsumeDao.loadAll().size() > 0) {
//            for (int i = 0; i < rsumeDao.loadAll().size(); i++) {
//                if (rsumeDao.loadAll().get(i).getImei().equals(device_id)) {
//                    rsumeDao.update(rusume_Bean);
//                    break;
//                } else if (i == rsumeDao.loadAll().size() - 1) {
//                    rsumeDao.insert(rusume_Bean);
//                }
//            }
//        } else {
//            rsumeDao.insert(rusume_Bean);
//        }
//
//        button = -1;
//    }


    @Override
    protected void onStart() {
        super.onStart();
        //注册EventBus
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        //取消EventBus
        EventBus.getDefault().unregister(this);
        super.onStop();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
//                upDatatoClient();
                finish();
                break;
            case R.id.rlt_location_mode:
                showLocationModeDialog();
                break;

            case R.id.rlt_scene_mode:
                showSceneModeDialog();
                break;

            case R.id.rlt_bund_unbund:
                toBindingAndUnbundActivity();
                break;

            case R.id.rlt_open_close:
                toResumeByAlarmActivity();
                break;

            case R.id.rlt_sound_set:
                toSoundSettingsActivity();
                break;

            case R.id.rlt_screen_set:
                button = 4;
                showSettinfTimeDialog(tv_screen_time);
                break;

            case R.id.rlt_baby_card:
                toChildWatchCardActivity();
                break;

            case R.id.rlt_default_device:
                toDefaultDeviceActivity();
                break;

            case R.id.rlt_watch_info:
                toWatchInfo();
                break;

            case R.id.btn_on_line://自动接通
                button = 2;
                resultBean1 = new RuntimeResultBean.ResultsBean();
                runTime1 = new RuntimeResultBean.ResultsBean.RunTimeBean();
                resultBean1.setIMEI(device_id);
                resultBean1.setCategory(ConstantClassFunction.RUNTIME);

                if (btn_on_line.isChecked()) {
                    runTime1.setAutoConnection("1");
                } else {
                    runTime1.setAutoConnection("0");
                }
                resultBean1.setRunTime(runTime1);
                String json = gson.toJson(resultBean1);

                if (!BaseApplication.getInstances().isSDKConnected()) {
                    BaseApplication.getInstances().toastLong(WatchManagerActivity.this,"已与手表服务器断开连接!");
                    return;
                }
                //上传服务器
                LogUtil.e("runtimejson----------" + json);
                BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json.getBytes(), json.length());
                break;

//            case R.id.btn_open_close://开关机
//                button = 1;
//
//                if ("".equals(onTime) && "".equals(offTime)) {
//                    BaseApplication.getInstances().toast("请设置时间！");
//
//                } else {
//                    result1 = new ResumeResultBean.ResultsBean();
//                    turnBean1 = new ResumeResultBean.ResultsBean.TurnBean();
//                    offBean1 = new ResumeResultBean.ResultsBean.TurnBean.OffBean();
//                    onBean1 = new ResumeResultBean.ResultsBean.TurnBean.OnBean();
//
//
//                    TurnSendEntity turnSendEntity = new TurnSendEntity();
//                    TurnSendEntity.TurnBean turnBean = new TurnSendEntity.TurnBean();
//                    TurnSendEntity.TurnBean.OnBean onBean = new TurnSendEntity.TurnBean.OnBean();
//                    TurnSendEntity.TurnBean.OffBean offBean = new TurnSendEntity.TurnBean.OffBean();
//                    turnSendEntity.setIMEI(device_id);
//                    turnSendEntity.setCategory(ConstantClassFunction.TURN);

//                    if ("0".equals(status)) {
//                        turnBean1.setStatus("1");
//                    } else {
//                        turnBean1.setStatus("0");
//                    }
//                    onBean1.setTime(onTime);
//                    offBean1.setTime(offTime);
//
//                    turnBean1.setOn(onBean1);
//                    turnBean1.setOff(offBean1);
//
//                    result1.setIMEI(device_id); // TODO: 2017/3/10
//                    result1.setCategory(ConstantClassFunction.TURN);
//
//                    result1.setTurn(turnBean1);
//                    String json2 = gson.toJson(result1);

//                    if ("0".equals(status)) {
//                        turnBean.setStatus("1");
//                    } else {
//                        turnBean.setStatus("0");
//                    }
//
//                    onBean.setTime(onTime);
//                    offBean.setTime(offTime);
//
//                    turnBean.setOn(onBean);
//                    turnBean.setOff(offBean);
//                    turnSendEntity.setTurn(turnBean);
//                    String json2 = JSON.toJSONString(turnSendEntity);
//                    LogUtil.e("json2-------" + json2);
//
//                    if (!BaseApplication.getInstances().isSDKConnected()) {
//                        BaseApplication.getInstances().toastLong("已与手表服务器断开连接!");
//                        return;
//                    }
//                    //提交到服务器
//                    BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json2.getBytes(), json2.length());
//                }
//                break;

            case R.id.btn_watch_miss://手机挂丢
                button = 3;
                resultBean1 = new RuntimeResultBean.ResultsBean();
                runTime1 = new RuntimeResultBean.ResultsBean.RunTimeBean();
                resultBean1.setIMEI(device_id);
                resultBean1.setCategory(ConstantClassFunction.RUNTIME);
                if (btn_watch_miss.isChecked()) {
                    runTime1.setLossReport("1");
                } else {
                    runTime1.setLossReport("0");
                }
                resultBean1.setRunTime(runTime1);
                String json1 = gson.toJson(resultBean1);

                if (!BaseApplication.getInstances().isSDKConnected()) {
                    BaseApplication.getInstances().toastLong(WatchManagerActivity.this,"已与手表服务器断开连接!");
                    return;
                }
                //上传服务器
                LogUtil.e("runtimejson----------" + json1);
                BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json1.getBytes(), json1.length());
                break;

            case R.id.rlt_quick_key:
                toShortcutKeyActivity();
                break;


        }
    }

    private void showSceneModeDialog() {
        remindDialog = new Dialog(this, R.style.ShareDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_scene_mode, null);

        TextView btnSure = (TextView) view.findViewById(R.id.btn_sure);
        TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);


        RelativeLayout rlt_mode1 = (RelativeLayout) view.findViewById(R.id.rlt_mode1);
        RelativeLayout rlt_mode2 = (RelativeLayout) view.findViewById(R.id.rlt_mode2);
//        rlt_mode3 = (RelativeLayout) findViewById(R.id.rlt_mode3);
        RelativeLayout rlt_mode4 = (RelativeLayout) view.findViewById(R.id.rlt_mode4);


        tv_mode1 = (TextView) view.findViewById(R.id.tv_mode1);
        tv_mode2 = (TextView) view.findViewById(R.id.tv_mode2);
//        tv_mode3 = (TextView) findViewById(R.id.tv_mode3);
        tv_mode4 = (TextView) view.findViewById(R.id.tv_mode4);
        mode = BaseApplication.getInstances().getMode();
        if (mode!=null) {
            if (mode.equals("1")) {
                tv_mode1.setSelected(true);
                tv_mode2.setSelected(false);
//                tv_mode3.setSelected(false);
                tv_mode4.setSelected(false);
            }else if (mode.equals("2")) {
                tv_mode1.setSelected(false);
                tv_mode2.setSelected(true);
//                tv_mode3.setSelected(false);
                tv_mode4.setSelected(false);
            }else if (mode.equals("3")) {
                tv_mode1.setSelected(false);
                tv_mode2.setSelected(false);
//                tv_mode3.setSelected(false);
                tv_mode4.setSelected(true);

            }else {
                tv_mode1.setSelected(true);
                tv_mode2.setSelected(false);
//                tv_mode3.setSelected(false);
                tv_mode4.setSelected(false);
            }
        }else {
            tv_mode1.setSelected(true);
            tv_mode2.setSelected(false);
//                tv_mode3.setSelected(false);
            tv_mode4.setSelected(false);
        }


        rlt_mode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = "1";
                tv_mode1.setSelected(true);
                tv_mode2.setSelected(false);
//                tv_mode3.setSelected(false);
                tv_mode4.setSelected(false);
            }
        });
        rlt_mode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = "2";
                tv_mode1.setSelected(false);
                tv_mode2.setSelected(true);
//                tv_mode3.setSelected(false);
                tv_mode4.setSelected(false);
            }
        });
//        rlt_mode3.setOnClickListener(this);
        rlt_mode4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mode = "3";

                tv_mode1.setSelected(false);
                tv_mode2.setSelected(false);
//                tv_mode3.setSelected(false);
                tv_mode4.setSelected(true);

            }
        });


        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (remindDialog != null) {
                    remindDialog.dismiss();
                }

                resultBean1 = new RuntimeResultBean.ResultsBean();
                runTime1 = new RuntimeResultBean.ResultsBean.RunTimeBean();
                resultBean1.setIMEI(device_id);
                resultBean1.setCategory(ConstantClassFunction.RUNTIME);
                runTime1.setModel(mode);
                resultBean1.setRunTime(runTime1);
                String json1 = gson.toJson(resultBean1);

                if (!BaseApplication.getInstances().isSDKConnected()) {
                    BaseApplication.getInstances().toastLong(WatchManagerActivity.this,"已与手表服务器断开连接!");
                    return;
                }
                //上传服务器
                LogUtil.e("runtimejson----------" + json1);
                BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json1.getBytes(), json1.length());
                BaseApplication.getInstances().setMode(mode);

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remindDialog != null) {
                    remindDialog.dismiss();
                }
            }
        });

        WindowManager.LayoutParams lp = remindDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        remindDialog.setContentView(view, lp);
        remindDialog.show();

    }

    /*private void showSceneModeDialog() {
        final SceneModeDialog dialog = new SceneModeDialog(this);
        dialog.show();
        //确定
        dialog.setYesOnclickListener(new BrightScreenTimeDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String time) {
                if (time.equals("")){
                    BaseApplication.getInstances().toast(WatchManagerActivity.this,"请选择情景模式！");
                }else {
                    if (time.equals("实时模式")) {
                        mode = "1";
                    }else if (time.equals("省电模式")) {
                        mode = "2";
                    }else if (time.equals("省电模式")) {
                        mode = "3";
                    }

                    resultBean1 = new RuntimeResultBean.ResultsBean();
                    runTime1 = new RuntimeResultBean.ResultsBean.RunTimeBean();
                    resultBean1.setIMEI(device_id);
                    resultBean1.setCategory(ConstantClassFunction.RUNTIME);
                    runTime1.setModel(mode);
                    resultBean1.setRunTime(runTime1);
                    String json1 = gson.toJson(resultBean1);

                    if (!BaseApplication.getInstances().isSDKConnected()) {
                        BaseApplication.getInstances().toastLong(WatchManagerActivity.this,"已与手表服务器断开连接!");
                        return;
                    }
                    //上传服务器
                    LogUtil.e("runtimejson----------" + json1);
                    BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json1.getBytes(), json1.length());


                }



                dialog.dismiss();
            }
        });
        //取消
        dialog.setNoOnclickListener(new BrightScreenTimeDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                dialog.dismiss();
            }
        });

    }*/

    /*******************************************     dialog     ************************************************/

    /**
     * 定位模式Dialog
     *
     */
    private void showLocationModeDialog() {
        final LocationModeDialog dialog = new LocationModeDialog(this);
        dialog.show();
        //确定
        dialog.setYesOnclickListener(new BrightScreenTimeDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String time) {
                if (time.equals("")){
                    BaseApplication.getInstances().toast(WatchManagerActivity.this,"请选择定位模式！");
                }else {
                    BaseApplication.getInstances().toast(WatchManagerActivity.this,time);
                }

                dialog.dismiss();
            }
        });
        //取消
        dialog.setNoOnclickListener(new BrightScreenTimeDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                dialog.dismiss();
            }
        });
    }


    /**
     *
     * 设置亮屏时间Dialog
     * @param tv
     */
    protected void showSettinfTimeDialog(final TextView tv) {
        final BrightScreenTimeDialog dialog = new BrightScreenTimeDialog(this);
        dialog.show();
        //确定
        dialog.setYesOnclickListener(new BrightScreenTimeDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String time) {
                resultBean1 = new RuntimeResultBean.ResultsBean();
                runTime1 = new RuntimeResultBean.ResultsBean.RunTimeBean();
                resultBean1.setIMEI(device_id);
                resultBean1.setCategory(ConstantClassFunction.RUNTIME);
                runTime1.setLightPanel(time);
                resultBean1.setRunTime(runTime1);
                String json1 = gson.toJson(resultBean1);

                if (!BaseApplication.getInstances().isSDKConnected()) {
                    BaseApplication.getInstances().toastLong(WatchManagerActivity.this,"已与手表服务器断开连接!");
                    return;
                }
                //上传服务器
                LogUtil.e("runtimejson----------" + json1);
                BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json1.getBytes(), json1.length());
                dialog.dismiss();

            }
        });
        //取消
        dialog.setNoOnclickListener(new BrightScreenTimeDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                dialog.dismiss();
            }
        });
    }

    /*******************************************     dialog     ************************************************/

    /**
     * 快捷键
     */
    private void toShortcutKeyActivity() {
        Intent intent = new Intent();
        intent.setClass(this,ShortcutKeyActivity.class);
        startActivity(intent);
    }


    /**
     * 手表信息
     */
    private void toWatchInfo() {
        Intent intent = new Intent(WatchManagerActivity.this, WatchInfoActivity.class);
        startActivity(intent);
    }




    /**
     * 声音设置
     */
    private void toSoundSettingsActivity() {
        Intent intent = new Intent(WatchManagerActivity.this, SoundSettingsActivity.class);
        intent.putExtra("type",type);
        intent.putExtra(SoundSettingsActivity.WATCHBELL, watchBell);
        startActivityForResult(intent, REQUEST_CODE_WATCHBELL);
    }

    /**
     * 定时开关机
     */
    private void toResumeByAlarmActivity() {
        Intent intent = new Intent(WatchManagerActivity.this, ResumeByAlarmActivity.class);
//        intent.putExtra(ResumeByAlarmActivity.ON_TIME, onTime);
//        intent.putExtra(ResumeByAlarmActivity.OFF_TIME, offTime);
        intent.putExtra(ResumeByAlarmActivity.TYPE, type);
        intent.putExtra("deviceId", device_id);

        startActivity(intent);
    }

    /**
     * 绑定和解绑
     */
    private void toBindingAndUnbundActivity() {
        Intent intent = new Intent(WatchManagerActivity.this, BindingAndUnbundActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_NAME, watchName);
        startActivityForResult(intent, WatchChildEntryActivity.REQUEST_UNBIND);
    }

    /**
     * 宝贝名片
     */
    private void toChildWatchCardActivity() {
        Intent intent = new Intent(this, ChildWatchCardActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra("type",type);
        intent.putExtra("name",watchName);
        intent.putExtra(ChildWatchCardActivity.WATCH_PHONENUM,watch_phonenum);
        startActivity(intent);
    }


    /**
     * 默认手表设备
     */

    private void toDefaultDeviceActivity() {
        Intent intent = new Intent(WatchManagerActivity.this, DefaultWatcthDeviceListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case WatchChildEntryActivity.REQUEST_UNBIND:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
//            case REQUEST_CODE_RESUME://开关机
//                if (resultCode == RESULT_OK) {
//
//                    button = 1;
//                    result1 = new ResumeResultBean.ResultsBean();
//                    turnBean1 = new ResumeResultBean.ResultsBean.TurnBean();
//                    offBean1 = new ResumeResultBean.ResultsBean.TurnBean.OffBean();
//                    onBean1 = new ResumeResultBean.ResultsBean.TurnBean.OnBean();
//                    offBean1.setTime(data.getStringExtra("off"));
//                    onBean1.setTime(data.getStringExtra("on"));
//
//                    turnBean1.setStatus("1");
//
//
//                    turnBean1.setOff(offBean1);
//                    turnBean1.setOn(onBean1);
//
//
//                    result1.setCategory(ConstantClassFunction.TURN);
//                    result1.setIMEI(device_id); // TODO: 2017/3/10
//                    result1.setTurn(turnBean1);
//                    String json = gson.toJson(result1);
//                    LogUtil.e("turnjson-------" + json);
//
//
//                    // TODO: 2017/3/10 数据未通暂不做处理
//                    if (!BaseApplication.getInstances().isSDKConnected()) {
//                        BaseApplication.getInstances().toastLong("已与手表服务器断开连接!");
//
//                        return;
//                    }
//                    //提交到服务器
//                    BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json.getBytes(), json.length());
//
//
//                }
//                break;
            case REQUEST_CODE_WATCHBELL://手表声音
                if (resultCode == RESULT_OK) {

                    button = 5;
                    watchBell = data.getStringExtra("phone_voic") + data.getStringExtra("phone_vibration") + "," + data.getStringExtra("msg_voic") + data.getStringExtra("msg_vibration");
                    resultBean1 = new RuntimeResultBean.ResultsBean();
                    runTime1 = new RuntimeResultBean.ResultsBean.RunTimeBean();
                    resultBean1.setIMEI(device_id);
                    resultBean1.setCategory(ConstantClassFunction.RUNTIME);
                    runTime1.setWatchBell(watchBell);
                    resultBean1.setRunTime(runTime1);
                    String json1 = gson.toJson(resultBean1);

                    if (!BaseApplication.getInstances().isSDKConnected()) {
                        BaseApplication.getInstances().toastLong(WatchManagerActivity.this,"已与手表服务器断开连接!");
                        return;
                    }
                    //上传服务器
                    LogUtil.e("runtimejson------手表声音----" + json1);
                    BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json1.getBytes(), json1.length());
                }
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
//        getResumData();
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

//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if (buttonView.equals(btn_open_close)) {
//
//            if (isChecked) {
//                btn_open_close.setChecked(true);
//                btn_open_close.setBackgroundResource(R.drawable.edit_btn_open);
//            } else {
//                btn_open_close.setChecked(false);
//                btn_open_close.setBackgroundResource(R.drawable.edit_btn_close);
//            }
//
//        } else if (buttonView.equals(btn_on_line)) {
//
//            if (isChecked) {
//                btn_on_line.setChecked(true);
//                btn_on_line.setBackgroundResource(R.drawable.edit_btn_open);
//
//
//            } else {
//                btn_on_line.setChecked(false);
//                btn_on_line.setBackgroundResource(R.drawable.edit_btn_close);
//            }
//
//        } else if (buttonView.equals(btn_watch_miss)) {
//
//            if (isChecked) {
//                btn_watch_miss.setChecked(true);
//                btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_open);
//
//            } else {
//                btn_watch_miss.setChecked(false);
//                btn_watch_miss.setBackgroundResource(R.drawable.edit_btn_close);
//            }
//
//        }
//    }

    /**
     * 上传数据到服务器
     */
//    private void upDatatoClient() {
//        BaseApplication.getInstances().toastLong("上传服务器！");
//
//        resultBean.setRunTime(runTime);
//        String json = gson.toJson(resultBean);
//
//        if(!BaseApplication.getInstances().isSDKConnected()){
//            BaseApplication.getInstances().toastLong("已与手表服务器断开连接!");
//            return;
//        }
//        //上传服务器
//        LogUtil.e("runtimejson----------"+json);
//        LogUtil.e("gid---------"+Long.parseLong(device_id,16));
//        BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id,16), json.getBytes(), json.length());
//
//
//    }
    private void saveData2DB() {
        resultBean.setRunTime(runTime);
        //保存到数据库
        runtimeResultBean.setResults(resultBean);
        String json1 = gson.toJson(runtimeResultBean);
        LogUtil.e("保存RunTime---------" + json1);
        bean.setJson(json1);
        if (dao.loadAll().size() > 0) {
            for (int i = 0; i < dao.loadAll().size(); i++) {
                if (dao.loadAll().get(i).getImei().equals(device_id)) {
                    dao.update(bean);
                    break;
                } else if (i == dao.loadAll().size() - 1) {
                    dao.insert(bean);
                }
            }
        } else {
            dao.insert(bean);
        }


        button = -1;
    }

//    private void upData() {
//
//        ResumeResultBean.ResultBean result1 = new ResumeResultBean.ResultBean();
//        ResumeResultBean.ResultBean.TurnBean turnBean1 = new ResumeResultBean.ResultBean.TurnBean();
//        ResumeResultBean.ResultBean.TurnBean.OffBean offBean1 = new ResumeResultBean.ResultBean.TurnBean.OffBean();
//        ResumeResultBean.ResultBean.TurnBean.OnBean onBean1 = new ResumeResultBean.ResultBean.TurnBean.OnBean();
//        offBean1.setTime(offTime);
//        onBean1.setTime(onTime);
//
//        if ("0".equals(status)) {
//            turnBean1.setStatus("1");
//        } else {
//            turnBean1.setStatus("0");
//        }
//
//        turnBean1.setOff(offBean1);
//        turnBean1.setOn(onBean1);
//
//
//        result1.setCategory(ConstantClassFunction.TURN);
//        result1.setIMEI(device_id); // TODO: 2017/3/10
//        result1.setTurn(turnBean1);
//        String json = gson.toJson(result1);
//        LogUtil.e("turnjson-------" + json);
//
//
//        // TODO: 2017/3/10 数据未通暂不做处理
//        if (!BaseApplication.getInstances().isSDKConnected()) {
//            BaseApplication.getInstances().toastLong("已与手表服务器断开连接!");
//
//            return;
//        }
//        //提交到服务器
//        BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json.getBytes(), json.length());
//
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            upDatatoClient();
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
