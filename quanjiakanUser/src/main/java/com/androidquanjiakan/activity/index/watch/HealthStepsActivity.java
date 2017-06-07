package com.androidquanjiakan.activity.index.watch;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.setting.contact.bean.ResultBean;
import com.androidquanjiakan.activity.setting.contact.bean.TagetStepEvent;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.ConfigReqEntity;
import com.androidquanjiakan.entity.ConfigResultEntity;
import com.androidquanjiakan.entity.RunTimeCategoryBean;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.result.RuntimeResultBean;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.EditTextFilter;
import com.androidquanjiakan.util.GsonUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.view.StepArcView;
import com.example.greendao.dao.RunTimeCategoryBeanDao;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by Gin on 2017/2/20.
 */

public class HealthStepsActivity extends BaseActivity implements View.OnClickListener {


    private ImageButton ibtn_back;
    private TextView tv_title;
    private StepArcView stepview;
    private Dialog dialog;
    private TextView tv_update_step;
    private String deviceid;
    private RunTimeCategoryBeanDao runTimeCategoryBeanDao;
    private String totalStep;
    private String currentStep;
    private RunTimeCategoryBean runTimeCategoryBean;

    private List<RunTimeCategoryBean> list;
    private RuntimeResultBean runtimeResultBean;

    private Context context;
    private Dialog dialogEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_steps);
        context=HealthStepsActivity.this;


        deviceid=getIntent().getStringExtra("device_id");
        //deviceid = "355637053995130";
        EventBus.getDefault().register(this);

        initTitle();
        initView();
    }

    private void initTitle() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("健康计步");

        tv_update_step = (TextView) findViewById(R.id.tv_update_step);
        tv_update_step.setOnClickListener(this);


    }

    private void initView() {
        stepview = (StepArcView) findViewById(R.id.stepview);
        //拿到当前目标步数
        initStep();
        //拿到当前已走步数
        ConfigReqEntity configReqEntity = new ConfigReqEntity();
        configReqEntity.setAction("Get");
        configReqEntity.setIMEI(deviceid);
        configReqEntity.setCategory("Config");
        String json = GsonUtil.toJson(configReqEntity);

        //请求SDK
        if(BaseApplication.getInstances().isSDKConnected()) {
            BaseApplication.getInstances().getNattyClient().ntyDataRouteClient(Long.parseLong(deviceid,16),json.getBytes(),json.length());
        }else {
            BaseApplication.getInstances().toast(HealthStepsActivity.this,ConstantClassFunction.CONNECTED_ERROR);
        }



        stepview.setOnClickListener(new StepArcView.OnClickListener() {
            @Override
            public void click() {
                //弹出设置目标步数的dialog
                showInputDialog();
            }
        });


    }

    private void initStep() {
        if(!NetUtil.isNetworkAvailable(this)) {
            Toast.makeText(HealthStepsActivity.this, "网络连接不可用!", Toast.LENGTH_SHORT).show();
            setStepFromDB();
            return;
        }
        getReq();
    }

    //从数据库里取出数据设置步数
    private void setStepFromDB(){
        runTimeCategoryBeanDao = BaseApplication.getInstances().getDaoInstant().getRunTimeCategoryBeanDao();
        list = runTimeCategoryBeanDao.queryBuilder().list();
        if(list.size()==0) {
            //getReq();
            stepview.setCurrentCount(10000,0);
        }else {
            for (int i=0;i<list.size();i++){
                if(deviceid.equals(list.get(i).getImei())) {
                    runTimeCategoryBean = list.get(i);
                    String json = runTimeCategoryBean.getJson();
                    runtimeResultBean = GsonUtil.fromJson(json, RuntimeResultBean.class);
                    totalStep=runtimeResultBean.getResults().getRunTime().getTagetStep();
                    stepview.setCurrentCount(Integer.valueOf(totalStep),0);
                    break;
                }else {
                    if(i==list.size()-1) {
                        stepview.setCurrentCount(10000,0);
                    }
                }

            }

        }

    }

    //请求网络
    public void getReq(){
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e(val);
                try {
                    JSONObject jsonObject = new JSONObject(val);
                    if(jsonObject.has(ConstantClassFunction.ERR_MES)&&jsonObject.getString(ConstantClassFunction.ERR_MES).equals("结果集为空")) {
                        RunTimeCategoryBeanDao runTimeCategoryBeanDao = BaseApplication.getInstances().getDaoInstant().getRunTimeCategoryBeanDao();
                        //没有
                        totalStep = "10000";
                        runtimeResultBean = new RuntimeResultBean();
                        String json = insertJson(totalStep);
                        runTimeCategoryBean = new RunTimeCategoryBean(null,json,deviceid);
                        runTimeCategoryBeanDao.insert(runTimeCategoryBean);
                    }else if(jsonObject.has(ConstantClassFunction.getRESULTS())&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).has(ConstantClassFunction.getCATEGORY())&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).getString(ConstantClassFunction.getCATEGORY()).equals(ConstantClassFunction.RUNTIME)) {
                        RuntimeResultBean runtimeResultBean = GsonUtil.fromJson(val, RuntimeResultBean.class);
                        if(runtimeResultBean.getResults().getRunTime().getTagetStep()!=null) {
                            totalStep=runtimeResultBean.getResults().getRunTime().getTagetStep();
                        }else {
                            totalStep="10000";
                        }
                        RunTimeCategoryBeanDao runTimeCategoryBeanDao = BaseApplication.getInstances().getDaoInstant().getRunTimeCategoryBeanDao();
                        RunTimeCategoryBean runTimeCategoryBean = new RunTimeCategoryBean(null,val,deviceid);
                        List<RunTimeCategoryBean> list = runTimeCategoryBeanDao.queryBuilder().list();
                        if(list.size()==0) {    //数据库不存在数据插入
                            runTimeCategoryBeanDao.insert(runTimeCategoryBean);
                        }else {
                            for (int i=0;i<list.size();i++){  //存在该deviceid时更新
                                if(deviceid.equals(list.get(i).getImei())) {
                                    RunTimeCategoryBean runTimeCategoryBean1 = list.get(i);
                                    runTimeCategoryBean1.setJson(val);
                                    runTimeCategoryBean1.setImei(deviceid);
                                    runTimeCategoryBeanDao.update(runTimeCategoryBean1);
                                    break;
                                }else {
                                    if(i==list.size()-1) {  //不存在该deviceid时插入
                                        runTimeCategoryBeanDao.insert(runTimeCategoryBean);
                                    }
                                }

                            }


                        }

                        if(currentStep!=null) {
                            stepview.setCurrentCount(Integer.valueOf(totalStep),Integer.valueOf(currentStep));
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //{"Result":{"Category":"RunTime","IMEI":"240207489224233264","RunTime":{"AutoConnection":"1","LightPanel":"40","LossReport":"1","TagetStep":"8009","WatchBell":"10,10"}}}

            }
        }, HttpUrls.getRunTimeData()+"&imei="+deviceid,null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(context)));

    }

    //处理自己的步数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleNativeDataResult(TagetStepEvent event) {
        if(event.getJson()!=null) {
            try {
                JSONObject jsonObject = new JSONObject(event.getJson());
                LogUtil.e(event.getJson());
                //处理设置目标步数
                if(jsonObject.has(ConstantClassFunction.getRESULTS())&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).has(ConstantClassFunction.CODE)&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).has(ConstantClassFunction.MESSAGE)) {
                    ResultBean resultBean = GsonUtil.fromJson(event.getJson(), ResultBean.class);
                    if(resultBean.getResult().getCode().equals("200")&&resultBean.getResult().getMessage().equals("Success")) {
                        setStep();
                    }else if(resultBean.getResult().getCode().equals("10001")) {
                        Toast.makeText(HealthStepsActivity.this, "设备不在线", Toast.LENGTH_SHORT).show();
                        setStepFromDB();
                    } else {
                        if(dialogEdit!=null) {
                            dialogEdit.dismiss();
                        }
                        Toast.makeText(HealthStepsActivity.this, "手表设置不成功", Toast.LENGTH_SHORT).show();
                        setStepFromDB();
                    }

                }else if(jsonObject.has(ConstantClassFunction.getRESULTS())&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).has(ConstantClassFunction.getCATEGORY())&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).getString(ConstantClassFunction.getCATEGORY()).equals(ConstantClassFunction.CONFIG)) {
                    //处理请求的实时步数
                    ConfigResultEntity configResultEntity = GsonUtil.fromJson(event.getJson(), ConfigResultEntity.class);
                    currentStep=configResultEntity.getResult().getConfig().getSteps();
                    if(totalStep!=null&&currentStep!=null) {
                        stepview.setCurrentCount(Integer.valueOf(totalStep),Integer.valueOf(currentStep));
                    }
                }else if (jsonObject.has(ConstantClassFunction.getRESULTS())&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).has(ConstantClassFunction.getCATEGORY())&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).getString(ConstantClassFunction.getCATEGORY()).equals("StepsReport")&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).has("StepsReport")){
                    //处理推送过来的步数
                    LogUtil.e("推送步数json:"+event.getJson());
                    String stepsReport = jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).getString("StepsReport");
                    String imei = jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).getString("IMEI");
                    if (stepsReport!=null&&deviceid.equals(imei)){
                        if (stepsReport.equals(currentStep)){
                            //如果一样不做处理
                            return;
                        }else {
                            currentStep=stepsReport;
                        }
                        if(totalStep!=null&&currentStep!=null) {
                            stepview.setCurrentCount(Integer.valueOf(totalStep),Integer.valueOf(currentStep));
                        }

                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
                Toast.makeText(HealthStepsActivity.this, "手表设置不成功", Toast.LENGTH_SHORT).show();
                setStepFromDB();
        }

    }

    //修改目标步数的dialog
    private void showInputDialog() {
        dialog = new Dialog(this, R.style.ShareDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_totalstep, null);
        final EditText tv_content = (EditText) view.findViewById(R.id.tv_content);
        EditTextFilter.setEditTextInhibitInputSpace(tv_content);
        TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!NetUtil.isNetworkAvailable(HealthStepsActivity.this)){
                    Toast.makeText(HealthStepsActivity.this, "网络连接不可用!", Toast.LENGTH_SHORT).show();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    return;
                }
                //请求网络,设置成功后
                if(!CheckUtil.isNumberChar(tv_content.getText().toString())) {
                    Toast.makeText(HealthStepsActivity.this, "请输入数字", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(tv_content.getText().toString().length()>6) {
                    Toast.makeText(HealthStepsActivity.this, "请输入小于六位", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dialog != null) {
                    dialog.dismiss();
                }

                if (tv_content.getText().toString().length() > 0) {
                    totalStep = tv_content.getText().toString();
                    String json=getJson(ConstantClassFunction.RUNTIME,deviceid,totalStep);
                    //发送请求
                    if(BaseApplication.getInstances().isSDKConnected()) {
                        dialogEdit = QuanjiakanDialog.getInstance().getDialog(context);
                        dialogEdit.show();
                        BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(deviceid,16), json.getBytes(),json.length());
                    }else {
                        Toast.makeText(HealthStepsActivity.this, ConstantClassFunction.CONNECTED_ERROR, Toast.LENGTH_SHORT).show();
                        if(dialogEdit!=null) {
                            dialogEdit.dismiss();
                        }
                    }
                    //setStep();
                } else {
                    Toast.makeText(HealthStepsActivity.this, "请设置相关数据", Toast.LENGTH_SHORT).show();
                }


            }
        });

        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();

        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setContentView(view);
        dialog.getWindow().setLayout(lp.width, lp.height);
        dialog.show();
    }


    //请求网络的json
    public String getJson(String catergory,String deviceid,String totalStep){
        RuntimeResultBean.ResultsBean resultBean = new RuntimeResultBean.ResultsBean();
        resultBean.setIMEI(deviceid);
        resultBean.setCategory(catergory);
        RuntimeResultBean.ResultsBean.RunTimeBean runTimeBean = new RuntimeResultBean.ResultsBean.RunTimeBean();
        /*runTimeBean.setAutoConnection("");
        runTimeBean.setLightPanel("");
        runTimeBean.setLossReport("");
        runTimeBean.setWatchBell("");*/
        runTimeBean.setTagetStep(totalStep);
        resultBean.setRunTime(runTimeBean);
        return GsonUtil.toJson(resultBean);



    }

    //用于更新数据库的json
    public String updateJson(String totalStep){
        String json = BaseApplication.getDaoInstant().getRunTimeCategoryBeanDao().queryBuilder().list().get(0).getJson();
        runtimeResultBean = GsonUtil.fromJson(json, RuntimeResultBean.class);
        runtimeResultBean.getResults().getRunTime().setTagetStep(totalStep);
        return GsonUtil.toJson(runtimeResultBean);


    }

    //用于插入数据库的json
    public String insertJson(String tagetStep){
        RuntimeResultBean runtimeResultBean = new RuntimeResultBean();

        RuntimeResultBean.ResultsBean resultBean = new RuntimeResultBean.ResultsBean();
        resultBean.setIMEI(deviceid);
        resultBean.setCategory(ConstantClassFunction.RUNTIME);
        RuntimeResultBean.ResultsBean.RunTimeBean runTimeBean = new RuntimeResultBean.ResultsBean.RunTimeBean();
        runTimeBean.setTagetStep(tagetStep);
        resultBean.setRunTime(runTimeBean);

        runtimeResultBean.setResults(resultBean);

        return GsonUtil.toJson(runtimeResultBean);

    }

    private void setStep() {
        if(dialogEdit!=null) {
            dialogEdit.dismiss();
        }
        if(totalStep!=null&&currentStep!=null) {
            stepview.setCurrentCount(Integer.valueOf(totalStep), Integer.valueOf(currentStep));
        }


        //Toast.makeText(HealthStepsActivity.this, "手表设置成功", Toast.LENGTH_SHORT).show();

        //以后做
        /*if (BaseApplication.getInstances().getDaoInstant().getRunTimeCategoryBeanDao().queryBuilder().list().size() > 0) {
            //更新
            List<RunTimeCategoryBean> list = BaseApplication.getInstances().getDaoInstant().getRunTimeCategoryBeanDao().queryBuilder().list();
            for (int i=0;i<list.size();i++){
                if(deviceid.equals(list.get(i).getImei())) {
                    RunTimeCategoryBean runTimeCategoryBean = BaseApplication.getInstances().getDaoInstant().getRunTimeCategoryBeanDao().queryBuilder().list().get(0);
                    runTimeCategoryBean.setJson(updateJson(totalStep));
                    BaseApplication.getInstances().getDaoInstant().getRunTimeCategoryBeanDao().update(runTimeCategoryBean);
                    stepview.setCurrentCount(Integer.valueOf(totalStep), Integer.valueOf(currentStep));
                    break;
                }else {
                    if(i==list.size()-1) {
                        RunTimeCategoryBean runTimeCategoryBean = new RunTimeCategoryBean();
                        runTimeCategoryBean.setJson(insertJson(totalStep));
                        BaseApplication.getInstances().getDaoInstant().getRunTimeCategoryBeanDao().insert(runTimeCategoryBean);
                        stepview.setCurrentCount(Integer.valueOf(totalStep), Integer.valueOf(currentStep));
                    }
                }
            }
        } else {
            //插入
            RunTimeCategoryBean runTimeCategoryBean = new RunTimeCategoryBean();
            runTimeCategoryBean.setJson(insertJson(totalStep));
            BaseApplication.getInstances().getDaoInstant().getRunTimeCategoryBeanDao().insert(runTimeCategoryBean);
            stepview.setCurrentCount(Integer.valueOf(totalStep), Integer.valueOf(currentStep));
        }*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_update_step:
                showInputDialog();
                break;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
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
}
