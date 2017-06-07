package com.androidquanjiakan.activity.index.watch;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.watch_child.WatchChildEntryActivity;
import com.androidquanjiakan.activity.setting.contact.bean.DataResultEvent;
import com.androidquanjiakan.activity.setting.contact.bean.FareEvent;
import com.androidquanjiakan.adapter.CallsTrafficQueryAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.CallsTrafficReqEntity;
import com.androidquanjiakan.entity.FareDataBean;
import com.androidquanjiakan.entity.QueryCallsEntity;
import com.androidquanjiakan.result.FlowDataBean;
import com.androidquanjiakan.util.GsonUtil;
import com.androidquanjiakan.util.LogUtil;
import com.example.greendao.dao.FareandFlowDataBeanDao;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gin on 2017/2/23.
 */

public class CallsTrafficQueryActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibtn_back;
    private TextView tv_title;
    private ListView listview;
    private ImageView nonedata;
    private TextView nonedatahint;
    private TextView tv_querycalls;
    private TextView tv_querytraffic;
    private List<QueryCallsEntity> list;
    private Context context;
    private CallsTrafficQueryAdapter callsTrafficQueryAdapter;
    private String deviceid;
    private static String ACTION = "Get";
    private static String CATEGORY_FARE = "Fare";
    private static String CATEGORY_FLOW = "Flow";
    private String fare;
    private String flow;
    private FareandFlowDataBeanDao dao;
    private int button = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calls_traffic_query);
        context = CallsTrafficQueryActivity.this;
        deviceid = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        LogUtil.e("device_id----" + deviceid);
        if (deviceid == null || deviceid.length() < 1) {
            BaseApplication.getInstances().toast(CallsTrafficQueryActivity.this,"传入参数异常!");
            finish();
            return;
        }
        dao = getFareandFlowDataBeanDao();
        initTitle();
        initView();
        initData();

    }

    private void initData() {

        if (dao.loadAll().size() > 0) {
            for (int i = 0; i < dao.loadAll().size(); i++) {
                if (deviceid.equals(dao.loadAll().get(i).getImei())) {
                    String json = dao.loadAll().get(i).getJson();
                    if (json.contains("Fare")) {
                        try {
                            FareDataBean fareDataBean = GsonUtil.fromJson(json, FareDataBean.class);
                            FareDataBean.ResultsBean results = fareDataBean.getResults();
                            fare = results.getDetails();

                            QueryCallsEntity queryCallsEntity1 = new QueryCallsEntity();
                            queryCallsEntity1.setTime("");
                            queryCallsEntity1.setType(1);
                            queryCallsEntity1.setName("宝贝手表");
                            queryCallsEntity1.setMessage(fare);
                            list.add(queryCallsEntity1);

                        } catch (Exception e) {
                            LogUtil.e("json解析异常");
                        }


                    } else if (json.contains("Flow")) {
                        try {
                            FlowDataBean flowDataBean = GsonUtil.fromJson(json, FlowDataBean.class);
                            FlowDataBean.ResultsBean results = flowDataBean.getResults();
                            flow = results.getDetails();
                            QueryCallsEntity queryCallsEntity3 = new QueryCallsEntity();
                            queryCallsEntity3.setTime("");
                            queryCallsEntity3.setType(1);
                            queryCallsEntity3.setName("宝贝手表");
                            queryCallsEntity3.setMessage(flow);
                            list.add(queryCallsEntity3);
                        } catch (Exception e) {
                            LogUtil.e("json解析异常");
                        }


                    }
                }

            }

            callsTrafficQueryAdapter.resetdata(list);
            callsTrafficQueryAdapter.notifyDataSetChanged();
//            listview.setSelection(listview.getBottom());
            showNoneDataImage(false);
        }else {
            showNoneDataImage(true);
        }

        listview.setSelection(list.size()-1);

    }

    private FareandFlowDataBeanDao getFareandFlowDataBeanDao() {
        return BaseApplication.getInstances().getDaoInstant().getFareandFlowDataBeanDao();
    }

    private void initView() {
        listview = (ListView) findViewById(R.id.listview);
        nonedata = (ImageView) findViewById(R.id.nonedata);
        nonedatahint = (TextView) findViewById(R.id.nonedatahint);
        tv_querycalls = (TextView) findViewById(R.id.tv_querycalls);
        tv_querytraffic = (TextView) findViewById(R.id.tv_querytraffic);
        tv_querycalls.setOnClickListener(this);
        tv_querytraffic.setOnClickListener(this);
        tv_querycalls.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        tv_querycalls.setTextColor(getResources().getColor(R.color.font_color_333333));
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                    case MotionEvent.ACTION_DOWN:
                        tv_querycalls.setTextColor(getResources().getColor(R.color.color_btn_green));
                        break;
                }
                return false;
            }
        });

        tv_querytraffic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        tv_querytraffic.setTextColor(getResources().getColor(R.color.font_color_333333));
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                    case MotionEvent.ACTION_DOWN:
                        tv_querytraffic.setTextColor(getResources().getColor(R.color.color_btn_green));
                        break;
                }
                return false;
            }
        });

        list = new ArrayList<>();
        callsTrafficQueryAdapter = new CallsTrafficQueryAdapter(context, list);
        listview.setAdapter(callsTrafficQueryAdapter);
        listview.setSelection(listview.getBottom());


    }

    private void initTitle() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("查询手表话费");

    }

    @Override
    protected void onResume() {
        super.onResume();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_querycalls:
                tv_querycalls.setEnabled(false);
                tv_querytraffic.setEnabled(false);
                // TODO: 2017/3/1 这里发送请求查询话费
                if (BaseApplication.getInstances().isSDKConnected()) {
                    button = 1;
                    CallsTrafficReqEntity callsTrafficReqEntity = new CallsTrafficReqEntity();
                    callsTrafficReqEntity.setAction(ACTION);
                    callsTrafficReqEntity.setIMEI(deviceid);
                    callsTrafficReqEntity.setCategory(CATEGORY_FARE);
                    String json = GsonUtil.toJson(callsTrafficReqEntity);
                    LogUtil.e(json);
                    BaseApplication.getInstances().getNattyClient().ntyDataRouteClient(Long.parseLong(deviceid, 16), json.getBytes(), json.length());


                } else {

                    if (list.size() == 0) {
                        showNoneDataImage(true);
                    } /*else if (list.size() > 0) {
                        list.remove(list.size() - 1);
                        callsTrafficQueryAdapter.resetdata(list);
                        callsTrafficQueryAdapter.notifyDataSetChanged();
                    }*/
                    BaseApplication.getInstances().toast(CallsTrafficQueryActivity.this,"已与手表服务器断开连接!");
                    tv_querycalls.setEnabled(true);
                    tv_querytraffic.setEnabled(true);
                }
                break;
            case R.id.tv_querytraffic:
                tv_querycalls.setEnabled(false);
                tv_querytraffic.setEnabled(false);
                if (BaseApplication.getInstances().isSDKConnected()) {
                    button = 2;
                    CallsTrafficReqEntity callsTrafficReqEntity = new CallsTrafficReqEntity();
                    callsTrafficReqEntity.setAction(ACTION);
                    callsTrafficReqEntity.setIMEI(deviceid);
                    callsTrafficReqEntity.setCategory(CATEGORY_FLOW);
                    String json1 = GsonUtil.toJson(callsTrafficReqEntity);
                    LogUtil.e(json1);
                    BaseApplication.getInstances().getNattyClient().ntyDataRouteClient(Long.parseLong(deviceid, 16), json1.getBytes(), json1.length());


                } else {
                    if (list.size() == 0) {
                        showNoneDataImage(true);
                    } /*else if (list.size() > 0) {
                        list.remove(list.size() - 1);
                        callsTrafficQueryAdapter.resetdata(list);
                        callsTrafficQueryAdapter.notifyDataSetChanged();
                    }*/
                    BaseApplication.getInstances().toast(CallsTrafficQueryActivity.this,"已与手表服务器断开连接!");
                    tv_querycalls.setEnabled(true);
                    tv_querytraffic.setEnabled(true);
                }
                break;

        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataResultEvent(DataResultEvent event) {
        String json = event.getJson();
//        {"Results":{"Code":"200","Message":"success"}}
        JsonObject jsonObject = new GsonParseUtil(json).getJsonObject();
        LogUtil.e("dataResult-----------------");

        if (jsonObject.has(ConstantClassFunction.getRESULTS())) {
            JsonObject object = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());


            if (object.has(ConstantClassFunction.CODE) && "200".equals(object.get(ConstantClassFunction.CODE).getAsString())) {
                showNoneDataImage(false);
                tv_querycalls.setEnabled(false);
                tv_querytraffic.setEnabled(false);
                switch (button) {
                    case 1:

                        /**
                         * 发送请求  这里做请求消息的展示
                         */
                        QueryCallsEntity queryCallsEntity = new QueryCallsEntity();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String format = sdf.format(System.currentTimeMillis());
                        queryCallsEntity.setTime(format);
                        queryCallsEntity.setType(0);
                        queryCallsEntity.setName("宝贝手表");
                        queryCallsEntity.setMessage("话费查询请求已提交,  请稍后...");

                        list.add(queryCallsEntity);

                        break;

                    case 2:
                        // TODO: 2017/3/1 这里发送请求查询流量
                        showNoneDataImage(false);
                        QueryCallsEntity queryCallsEntity2 = new QueryCallsEntity();
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String format1 = sdf1.format(System.currentTimeMillis());
                        queryCallsEntity2.setTime(format1);
                        queryCallsEntity2.setType(0);
                        queryCallsEntity2.setName("宝贝手表");
                        queryCallsEntity2.setMessage("流量查询请求已提交,  请稍后...");
                        list.add(queryCallsEntity2);
                        break;

                }

                callsTrafficQueryAdapter.resetdata(list);
                callsTrafficQueryAdapter.notifyDataSetChanged();
                listview.setSelection(listview.getBottom());
                button = -1;
            } else {
                button = -1;
                BaseApplication.getInstances().toast(CallsTrafficQueryActivity.this,"请求发送失败");
                tv_querycalls.setEnabled(true);
                tv_querytraffic.setEnabled(true);
            }


        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFareEvent(FareEvent event) {
        String data = event.getJson();
        tv_querycalls.setEnabled(true);
        tv_querytraffic.setEnabled(true);
        if (list.size()>=1&&list.get(list.size()-1).getType()==0) {
            list.remove(list.size()-1);
            callsTrafficQueryAdapter.resetdata(list);
            callsTrafficQueryAdapter.notifyDataSetChanged();
        }

        if (data.contains("Fare")) {
            LogUtil.e("FareData--------------=====" + data +"---"+data.length());
            try {
                FareDataBean fareDataBean = GsonUtil.fromJson(data, FareDataBean.class);
                FareDataBean.ResultsBean results = fareDataBean.getResults();
                fare = results.getDetails();
                LogUtil.e("fare-----------" + fare);


                QueryCallsEntity queryCallsEntity1 = new QueryCallsEntity();
                queryCallsEntity1.setTime("");
                queryCallsEntity1.setType(1);
                queryCallsEntity1.setName("宝贝手表");
                queryCallsEntity1.setMessage(fare);
                list.add(queryCallsEntity1);
                callsTrafficQueryAdapter.resetdata(list);
                callsTrafficQueryAdapter.notifyDataSetChanged();
                listview.setSelection(listview.getBottom());
                showNoneDataImage(false);

            } catch (Exception e) {
                list.remove(list.size() - 1);
                callsTrafficQueryAdapter.resetdata(list);
                callsTrafficQueryAdapter.notifyDataSetChanged();
                LogUtil.e("json解析异常");
            }


        } else if (data.contains("Flow")) {
            LogUtil.e("FlowData-----------------" + data);
            try {
                FlowDataBean flowDataBean = GsonUtil.fromJson(data, FlowDataBean.class);
                FlowDataBean.ResultsBean results = flowDataBean.getResults();
                flow = results.getDetails();
                LogUtil.e("Flow-----------" + flow);


                // TODO: 2017/3/1  如果查询成功  返回短信内容并显示  查询指令消失


                QueryCallsEntity queryCallsEntity3 = new QueryCallsEntity();
                queryCallsEntity3.setTime("");
                queryCallsEntity3.setType(1);
                queryCallsEntity3.setName("宝贝手表");
                queryCallsEntity3.setMessage(flow);
                list.add(queryCallsEntity3);
                callsTrafficQueryAdapter.resetdata(list);
                callsTrafficQueryAdapter.notifyDataSetChanged();
                listview.setSelection(listview.getBottom());
                showNoneDataImage(false);

            } catch (Exception e) {
                LogUtil.e("json解析异常");
            }


        }
    }

    public void showNoneDataImage(boolean isShow) {
        if (isShow) {
            nonedata.setVisibility(View.VISIBLE);
            nonedatahint.setVisibility(View.VISIBLE);
        } else {
            nonedata.setVisibility(View.GONE);
            nonedatahint.setVisibility(View.GONE);
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


}
