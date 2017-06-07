package com.androidquanjiakan.activity.index.watch;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.setting.contact.bean.ClassDisableEvent;
import com.androidquanjiakan.activity.setting.contact.bean.ResultBean;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.CourseBean;
import com.androidquanjiakan.entity.DisableEntity;
import com.androidquanjiakan.entity.DisableResultsEntity;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.util.GsonUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.view.ClassDisableSelectTimeDialog;
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

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gin on 2017/2/21.
 */

public class ClassDisableActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibtn_back;
    private TextView tv_title;
    private ImageView iv_morning_switch;
    private RelativeLayout rl_morning_begin;
    private TextView tv_morning_begin;
    private RelativeLayout rl_morning_finish;
    private TextView tv_morning_finish;
    private ImageView iv_afternoon_switch;
    private RelativeLayout rl_afternoon_begin;
    private TextView tv_afternoon_begin;
    private RelativeLayout rl_afternoon_finish;
    private TextView tv_afternoon_finish;
    private boolean isMorningOpen=false;
    private boolean isAfternoonOpen=false;
    private TextView tv_mon;
    private TextView tv_tue;
    private TextView tv_wed;
    private TextView tv_thur;
    private TextView tv_fri;
    private TextView tv_sat;
    private TextView tv_sun;
    private String deviceid;

    private boolean isMonSelect;
    private boolean isTueSelect;
    private boolean isWedSelect;
    private boolean isThurSelect;
    private boolean isFriSelect;
    private boolean isSatSelect;
    private boolean isSunSelect;
    private Button btn_save;
    private int morbeginH;
    private int morbeginM;
    private int morfinishH;
    private int morfinishM;
    private int aftbeginH;
    private int aftbeginM;
    private int afterfinishH;
    private int afterfinishM;
    private DisableResultsEntity disableResultsEntity;
    private String morningStartTime;
    private String morningEndTime;
    private String afternoonEndTime;
    private String afternoonStartTime;
    private CourseBean courseBean;
    private DisableEntity disableEntity;
    private ArrayList<DisableEntity> disableEntities;
    private DisableResultsEntity.results results;
    private DisableEntity.Morning morning;
    private DisableEntity.Afternoon afternoon;
    private String daily;
    private Dialog dialogSave;
    private boolean isUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_disable);
        EventBus.getDefault().register(this);
        deviceid=getIntent().getStringExtra("device_id");
        LogUtil.e("deviceid:"+deviceid);
        initTitle();
        initView();


    }

    private void initView() {
        iv_morning_switch = (ImageView) findViewById(R.id.iv_morning_switch);
        iv_morning_switch.setOnClickListener(this);

        rl_morning_begin = (RelativeLayout) findViewById(R.id.rl_morning_begin);
        rl_morning_begin.setOnClickListener(this);
        tv_morning_begin = (TextView) findViewById(R.id.tv_morning_begin);


        rl_morning_finish = (RelativeLayout) findViewById(R.id.rl_morning_finish);
        rl_morning_finish.setOnClickListener(this);
        tv_morning_finish = (TextView) findViewById(R.id.tv_morning_finish);


        iv_afternoon_switch = (ImageView) findViewById(R.id.iv_afternoon_switch);
        iv_afternoon_switch.setOnClickListener(this);

        rl_afternoon_begin = (RelativeLayout) findViewById(R.id.rl_afternoon_begin);
        rl_afternoon_begin.setOnClickListener(this);
        tv_afternoon_begin = (TextView) findViewById(R.id.tv_afternoon_begin);

        rl_afternoon_finish = (RelativeLayout) findViewById(R.id.rl_afternoon_finish);
        rl_afternoon_finish.setOnClickListener(this);
        tv_afternoon_finish = (TextView) findViewById(R.id.tv_afternoon_finish);

        tv_mon = (TextView) findViewById(R.id.tv_mon);
        tv_tue = (TextView) findViewById(R.id.tv_tue);
        tv_wed = (TextView) findViewById(R.id.tv_wed);
        tv_thur = (TextView) findViewById(R.id.tv_thur);
        tv_fri = (TextView) findViewById(R.id.tv_fri);
        tv_sat = (TextView) findViewById(R.id.tv_sat);
        tv_sun = (TextView) findViewById(R.id.tv_sun);

        tv_mon.setOnClickListener(this);
        tv_tue.setOnClickListener(this);
        tv_wed.setOnClickListener(this);
        tv_thur.setOnClickListener(this);
        tv_fri.setOnClickListener(this);
        tv_sat.setOnClickListener(this);
        tv_sun.setOnClickListener(this);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);



        initDisable();



    }


    private void initUi(){
        if(isMorningOpen) {
            iv_morning_switch.setImageDrawable(getResources().getDrawable(R.drawable.edit_btn_open));
            rl_morning_begin.setVisibility(View.VISIBLE);
            rl_morning_finish.setVisibility(View.VISIBLE);
        }else {
            iv_morning_switch.setImageDrawable(getResources().getDrawable(R.drawable.edit_btn_close));
            rl_morning_begin.setVisibility(View.GONE);
            rl_morning_finish.setVisibility(View.GONE);
        }

        if(isAfternoonOpen) {
            iv_afternoon_switch.setImageDrawable(getResources().getDrawable(R.drawable.edit_btn_open));
            rl_afternoon_begin.setVisibility(View.VISIBLE);
            rl_afternoon_finish.setVisibility(View.VISIBLE);
        }else {
            iv_afternoon_switch.setImageDrawable(getResources().getDrawable(R.drawable.edit_btn_close));
            rl_afternoon_begin.setVisibility(View.GONE);
            rl_afternoon_finish.setVisibility(View.GONE);
        }



        if(isMonSelect) {
            //tv_mon.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_mon.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
            tv_mon.setTextColor(getResources().getColor(R.color.white));
        }
        if(isTueSelect) {
            tv_tue.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
            tv_tue.setTextColor(getResources().getColor(R.color.white));
        }
        if(isWedSelect) {
            tv_wed.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
            tv_wed.setTextColor(getResources().getColor(R.color.white));
        }
        if(isThurSelect) {
            tv_thur.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
            tv_thur.setTextColor(getResources().getColor(R.color.white));
        }
        if(isFriSelect) {
            tv_fri.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
            tv_fri.setTextColor(getResources().getColor(R.color.white));
        }
        if(isSatSelect) {
            tv_sat.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
            tv_sat.setTextColor(getResources().getColor(R.color.white));
        }
        if(isSunSelect) {
            tv_sun.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
            tv_sun.setTextColor(getResources().getColor(R.color.white));
        }


        if(morningStartTime!=null) {
            tv_morning_begin.setText(morningStartTime);
        }
        if(morningEndTime!=null) {
            tv_morning_finish.setText(morningEndTime);
        }
        if(afternoonStartTime!=null) {
            tv_afternoon_begin.setText(afternoonStartTime);
        }
        if(afternoonEndTime!=null) {
            tv_afternoon_finish.setText(afternoonEndTime);
        }




    }
    private void initDisable() {
        if(!NetUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "网络连接不可用!", Toast.LENGTH_SHORT).show();
            // 以后做 第一次进来 没数据时请求网络
            if(BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().queryBuilder().list().size()==0) {

            }else {
                List<CourseBean> list = BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().queryBuilder().list();
                for (int i=0;i<list.size();i++){
                    String imei = BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().queryBuilder().list().get(i).getImei();
                    if(deviceid.equals(list.get(i).getImei())) {
                        CourseBean courseBean = BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().queryBuilder().list().get(i);
                        String json = courseBean.getJson();
                        //{"Results":{"IMEI":"352315052834187","Category":"TimeTables","TimeTables":[{"Id":"0","Morning":{"Status":"0","StartTime":"08:00","EndTime":"12:00"},"Afternoon":{"Status":"0","StartTime":"14:00","EndTime":"17:00"}}]}}
                        disableResultsEntity = GsonUtil.fromJson(json, DisableResultsEntity.class);
                        handleResult();
                        initUi();
                        break;
                    }else {
                        if(i==list.size()-1) {

                        }

                    }
                }

            }
            return;
        }
        getReq();
    }

    private void getReq() {
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                //{"Results":{"Category":"TimeTables","IMEI":"240207489224233264","TimeTables":[{"Afternoon":{"EndTime":"09:00:00","StartTime":"08:00:00","Status":"1"},"Daily":"(null)","Morning":{"EndTime":"09:00:00","StartTime":"08:00:00","Status":"1"}}]}}
                if(val!=null&&val.length()>0) {
                    try {
                        JSONObject jsonObject = new JSONObject(val);
                        if(jsonObject.has(ConstantClassFunction.ERR_MES)&&jsonObject.getString(ConstantClassFunction.ERR_MES).equals("结果集为空")) {
                            disableResultsEntity = new DisableResultsEntity();
                            results=disableResultsEntity.getResults();
                            disableEntity = new DisableEntity();
                            DisableEntity.Morning morning = new DisableEntity.Morning();
                            DisableEntity.Afternoon afternoon = new DisableEntity.Afternoon();
                            disableEntity.setMorning(morning);
                            disableEntity.setAfternoon(afternoon);


                            initUi();
                            Toast.makeText(ClassDisableActivity.this, "没有设置", Toast.LENGTH_SHORT).show();
                        }else if(jsonObject.has(ConstantClassFunction.getRESULTS())&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).has(ConstantClassFunction.getCATEGORY())&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).getString(ConstantClassFunction.getCATEGORY()).equals(ConstantClassFunction.TIMETABLES)) {
                            //保存数据库(以后再做)
                            DisableResultsEntity disable =new DisableResultsEntity();
                            DisableResultsEntity disableResultsEntity1 = GsonUtil.fromJson(val, DisableResultsEntity.class);
                            String imei = disableResultsEntity1.getResults().getIMEI();
                            CourseBean courseBean = new CourseBean();
                            courseBean.setImei(imei);
                            courseBean.setJson(val);
                            LogUtil.e("val:"+val);

                            //{"Results":{"Category":"TimeTables","IMEI":"355637053995130","TimeTables":[{"Afternoon":{"EndTime":"09:00:00","StartTime":"08:00:00","Status":"1"},"Daily":"Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday","Morning":{"EndTime":"09:00:00","StartTime":"08:00:00","Status":"1"}}]}}

                            List<CourseBean> list = BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().queryBuilder().list();
                            if(list.size()==0) {  //数据库不存在数据插入
                                BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().insert(courseBean);
                            }else {
                                for (int i=0;i<list.size();i++){
                                    if(deviceid.equals(list.get(i).getImei())) {    //存在该deviceid时更新
                                         CourseBean courseBean1 = BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().queryBuilder().list().get(i);
                                         courseBean1.setJson(val);
                                         courseBean1.setImei(imei);
                                         BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().update(courseBean1);
                                        //{"Results":{"IMEI":"352315052834187","Category":"TimeTables","TimeTables":[{"Id":"0","Morning":{"Status":"0","StartTime":"08:00","EndTime":"12:00"},"Afternoon":{"Status":"0","StartTime":"14:00","EndTime":"17:00"}}]}}
                                        break;
                                    }else {
                                        if(i==list.size()-1) {  //不存在该deviceid时插入
                                            BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().insert(courseBean);
                                        }

                                    }
                                }
                            }

                            //展示
                            disableResultsEntity = GsonUtil.fromJson(val, DisableResultsEntity.class);
                            handleResult();
                            initUi();


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        }, HttpUrls.getTimeTablesData()+"&imei="+deviceid,null,Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(this)));

    }


    private void handleResult() {
        /*List<CourseBean> list = BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().queryBuilder().list();
        for (int i=0;i<list.size();i++){
            if((deviceid).equals(list.get(i).getImei())) {
                results=disableResultsEntity.getResults();
                disableEntity = disableResultsEntity.getResults().getTimeTables().get(0);
                daily = disableEntity.getDaily();
                handleDaily(daily);
                morning = disableEntity.getMorning();
                handleMorning(morning);
                afternoon = disableEntity.getAfternoon();
                handleAfternoon(afternoon);

            }

        }*/

        results=disableResultsEntity.getResults();
        disableEntity = disableResultsEntity.getResults().getTimeTables().get(0);
        daily = disableEntity.getDaily();
        handleDaily(daily);
        morning = disableEntity.getMorning();
        handleMorning(morning);
        afternoon = disableEntity.getAfternoon();
        handleAfternoon(afternoon);

    }

    private void handleAfternoon(DisableEntity.Afternoon afternoon) {
        String status = afternoon.getStatus();
        if(status.equals("1")) {
            isAfternoonOpen=true;
        }else if(status.equals("0")) {
            isAfternoonOpen=false;
        }

        if(afternoon.getStartTime().length()>5) {
            afternoonStartTime = afternoon.getStartTime().substring(0,5);
        }else {
            afternoonStartTime = afternoon.getStartTime();
        }

        if(afternoon.getEndTime().length()>5) {
            afternoonStartTime = afternoon.getEndTime().substring(0,5);
        }else {
            afternoonEndTime = afternoon.getEndTime();
        }

    }

    private void handleMorning(DisableEntity.Morning morning) {
        String status = morning.getStatus();
        if(status.equals("1")) {
            isMorningOpen=true;
        }else if(status.equals("0")) {
            isMorningOpen=false;
        }
        if(morning.getStartTime().length()>5) {
            morningStartTime = morning.getStartTime().substring(0,5);
        }else {
            morningStartTime = morning.getStartTime();
        }

        if(morning.getEndTime().length()>5) {
            morningEndTime = morning.getEndTime().substring(0,5);
        }else {
            morningEndTime = morning.getEndTime();
        }


    }

    private void handleDaily(String daily) {
       if(daily.contains("Monday")) {
           isMonSelect=true;
       }
       if(daily.contains("Tuesday")) {
           isTueSelect=true;
       }
       if(daily.contains("Wednesday")) {
           isWedSelect=true;
       }
       if(daily.contains("Thursday")) {
           isThurSelect=true;
       }
       if(daily.contains("Friday")) {
           isFriSelect=true;
       }
       if(daily.contains("Saturday")) {
           isSatSelect=true;
       }
       if(daily.contains("Sunday")) {
           isSunSelect=true;
       }



    }



    private void initTitle() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("上课禁用");

    }

    public int mbt=1;
    public int mft=2;
    public int abt=3;
    public int aft=4;
    @Override
    public void onClick(View view) {
        if(!NetUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "网络连接不可用!", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()){
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.iv_morning_switch:
                if(isMorningOpen) {
                    iv_morning_switch.setImageDrawable(getResources().getDrawable(R.drawable.edit_btn_close));
                    rl_morning_begin.setVisibility(View.GONE);
                    rl_morning_finish.setVisibility(View.GONE);
                }else {
                    iv_morning_switch.setImageDrawable(getResources().getDrawable(R.drawable.edit_btn_open));
                    rl_morning_begin.setVisibility(View.VISIBLE);
                    rl_morning_finish.setVisibility(View.VISIBLE);
                }
                isMorningOpen=!isMorningOpen;
                break;
            case R.id.rl_morning_begin:
                //弹出选择上午开始时间的对话框
                showSelectDialog(mbt,true);
                break;
            case R.id.rl_morning_finish:
                //弹出选择上午结束时间的对话框
                showSelectDialog(mft,true);

                break;
            case R.id.iv_afternoon_switch:
                if(isAfternoonOpen) {
                    iv_afternoon_switch.setImageDrawable(getResources().getDrawable(R.drawable.edit_btn_close));
                    rl_afternoon_begin.setVisibility(View.GONE);
                    rl_afternoon_finish.setVisibility(View.GONE);
                }else {
                    iv_afternoon_switch.setImageDrawable(getResources().getDrawable(R.drawable.edit_btn_open));
                    rl_afternoon_begin.setVisibility(View.VISIBLE);
                    rl_afternoon_finish.setVisibility(View.VISIBLE);
                }
                isAfternoonOpen=!isAfternoonOpen;
                break;

            case R.id.rl_afternoon_begin:
                //弹出选择下午开始时间的对话框
                showSelectDialog(abt,false);
                break;
            case R.id.rl_afternoon_finish:
                //弹出选择下午结束时间的对话框
                showSelectDialog(aft,false);
                break;
            case R.id.tv_mon:
                if(isMonSelect) {
                    tv_mon.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_mon.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_mon.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
                    tv_mon.setTextColor(getResources().getColor(R.color.white));
                }
                isMonSelect=!isMonSelect;
                break;
            case R.id.tv_tue:
                if(isTueSelect) {
                    tv_tue.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_tue.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_tue.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
                    tv_tue.setTextColor(getResources().getColor(R.color.white));
                }
                isTueSelect=!isTueSelect;
                break;
            case R.id.tv_wed:
                if(isWedSelect) {
                    tv_wed.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_wed.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_wed.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
                    tv_wed.setTextColor(getResources().getColor(R.color.white));
                }
                isWedSelect=!isWedSelect;
                break;
            case R.id.tv_thur:
                if(isThurSelect) {
                    tv_thur.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_thur.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_thur.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
                    tv_thur.setTextColor(getResources().getColor(R.color.white));
                }
                isThurSelect=!isThurSelect;
                break;
            case R.id.tv_fri:
                if(isFriSelect) {
                    tv_fri.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_fri.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_fri.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
                    tv_fri.setTextColor(getResources().getColor(R.color.white));
                }
                isFriSelect=!isFriSelect;
                break;
            case R.id.tv_sat:
                if(isSatSelect) {
                    tv_sat.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_sat.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_sat.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
                    tv_sat.setTextColor(getResources().getColor(R.color.white));
                }
                isSatSelect=!isSatSelect;
                break;
            case R.id.tv_sun:
                if(isSunSelect) {
                    tv_sun.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_sun.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_sun.setBackground(getResources().getDrawable(R.drawable.shape_disable_week));
                    tv_sun.setTextColor(getResources().getColor(R.color.white));
                }
                isSunSelect=!isSunSelect;
                break;
            case R.id.btn_save:
                save();
                break;

        }
    }


    //判断是否有选择日期
    private boolean isSelectedWeek(){
        if(isMonSelect) {
            return true;
        }else if(isTueSelect) {
            return true;
        }else if(isWedSelect) {
            return true;
        }else if(isThurSelect) {
            return true;
        }else if(isFriSelect) {
            return true;
        }else if(isSatSelect) {
            return true;
        }else if(isSunSelect) {
            return true;
        }else {
            return false;
        }

    }

    private void save() {
        if(isMorningOpen&&Integer.valueOf(tv_morning_begin.getText().toString().substring(0,2))>Integer.valueOf(tv_morning_finish.getText().toString().substring(0,2))) {
            Toast.makeText(ClassDisableActivity.this, "请您设置正确的日期", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isMorningOpen&&tv_morning_begin.getText().toString().substring(0,2).equals(tv_morning_finish.getText().toString().substring(0,2))&&Integer.valueOf(tv_morning_begin.getText().toString().substring(3,5))>Integer.valueOf(tv_morning_finish.getText().toString().substring(3,5))){
            Toast.makeText(ClassDisableActivity.this, "请您设置正确的日期", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isAfternoonOpen&&Integer.valueOf(tv_afternoon_begin.getText().toString().substring(0,2))>Integer.valueOf(tv_afternoon_finish.getText().toString().substring(0,2))) {
            Toast.makeText(ClassDisableActivity.this, "请您设置正确的日期", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isAfternoonOpen&&tv_afternoon_begin.getText().toString().substring(0,2).equals(tv_afternoon_finish.getText().toString().substring(0,2))&&Integer.valueOf(tv_afternoon_begin.getText().toString().substring(3,5))>Integer.valueOf(tv_afternoon_finish.getText().toString().substring(3,5))){
            Toast.makeText(ClassDisableActivity.this, "请您设置正确的日期", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isSelectedWeek()) {
            Toast.makeText(this, "您未设置日期", Toast.LENGTH_SHORT).show();
            return;
        }


        isUpdate=true;
        LogUtil.e("reqJson"+getJson());
        //{"Results":{"Category":"TimeTables","IMEI":"355637053995130","TimeTables":[{"Afternoon":{"EndTime":"09:00:00","StartTime":"08:00:00","Status":"1"},"Daily":"Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday","Morning":{"EndTime":"09:00:00","StartTime":"08:00:00","Status":"1"}}]}}
        //{"Category":"TimeTables","IMEI":"355637053995130","TimeTables":[{"Afternoon":{"EndTime":"16:30:00","StartTime":"09:00:00","Status":"0"},"Daily":"Monday|Tuesday|Friday|Saturday|Sunday","Morning":{"EndTime":"09:00:00","StartTime":"08:00:00","Status":"1"}}]}
        //请求网络
        if(BaseApplication.getInstances().isSDKConnected()) {
            dialogSave = QuanjiakanDialog.getInstance().getDialog(this);
            dialogSave.show();
            BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(deviceid,16),getJson().getBytes(),getJson().length());
        }else {
            Toast.makeText(ClassDisableActivity.this, ConstantClassFunction.CONNECTED_ERROR, Toast.LENGTH_SHORT).show();
            finish();
        }


    }



    //处理自己修改的数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleNativeDataResult(ClassDisableEvent event){

        if(event.getJson()!=null) {
            ResultBean resultBean = GsonUtil.fromJson(event.getJson(), ResultBean.class);
            if(resultBean.getResult().getCode().equals("200")&&resultBean.getResult().getMessage().equals("Success")) {
                    if(dialogSave!=null) {
                        dialogSave.dismiss();
                    }
                //更新数据库(以后做)
                //updateDisabeTab();
                Toast.makeText(ClassDisableActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                //finish();
            }else if(resultBean.getResult().getCode().equals("10001")) {
                if(dialogSave!=null) {
                    dialogSave.dismiss();
                }
                Toast.makeText(ClassDisableActivity.this, "设备不在线", Toast.LENGTH_SHORT).show();
            } else {
                if(dialogSave!=null) {
                    dialogSave.dismiss();
                }
                //finish();
                Toast.makeText(ClassDisableActivity.this, "设置不成功", Toast.LENGTH_SHORT).show();
            }

        }else {
            if(dialogSave!=null) {
                dialogSave.dismiss();
            }
            Toast.makeText(ClassDisableActivity.this, "设置不成功", Toast.LENGTH_SHORT).show();
            //finish();
        }

    }

    //处理别人修改的数据
   /* @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlentyNativeCommonBoradCastResult(ClassDisableEvent event){
        if(event.getJson()!=null) {
            //更新
            if(BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().loadAll().size()>0) {
                CourseBean courseBean = BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().queryBuilder().list().get(0);
                courseBean.setJson(event.getJson());
                BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().update(courseBean);
            }else {
                CourseBean courseBean = new CourseBean();
                courseBean.setJson(event.getJson());
                BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().insert(courseBean);
            }
        }

    }*/



    //发网络请求的json
    private String getJson(){
        if(isMorningOpen) {
            disableEntity.getMorning().setStatus("1");
        }else {
            disableEntity.getMorning().setStatus("0");
        }
        disableEntity.getMorning().setStartTime(tv_morning_begin.getText().toString().trim());
        disableEntity.getMorning().setEndTime(tv_morning_finish.getText().toString().trim());

        if(isAfternoonOpen) {
            disableEntity.getAfternoon().setStatus("1");
        }else {
            disableEntity.getAfternoon().setStatus("0");
        }
        disableEntity.getAfternoon().setStartTime(tv_afternoon_begin.getText().toString().trim());
        disableEntity.getAfternoon().setEndTime(tv_afternoon_finish.getText().toString().trim());

        String week = handleWeek();
        disableEntity.setDaily(week);

        disableEntities=new ArrayList<>();
        disableEntities.add(disableEntity);

        DisableResultsEntity.results results = new DisableResultsEntity.results(deviceid, ConstantClassFunction.TIMETABLES, disableEntities);
        return GsonUtil.toJson(results);

    }



    //更新数据库
    private void updateDisabeTab() {
        //disableResultsEntity.setResults(new DisableResultsEntity.results());
        if(BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().queryBuilder().list().size()>0) {
            List<CourseBean> list = BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().queryBuilder().list();
            for (int i=0;i<list.size();i++){
                if(deviceid.equals(list.get(i).getImei())&&isUpdate) {
                    CourseBean courseBean = BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().queryBuilder().list().get(i);
                    LogUtil.e("updateJson:"+updateJson());
                    courseBean.setJson(updateJson());
                    BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().update(courseBean);
                    break;
                }else {
                    //到最后一条都没有只有插入一条
                    if(i==list.size()-1) {
                        CourseBean courseBean = new CourseBean();
                        courseBean.setId(null);
                        courseBean.setImei(deviceid);
                        courseBean.setJson(updateJson());
                        BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().insert(courseBean);

                    }

                }
            }
        }else {
            CourseBean courseBean = new CourseBean();
            courseBean.setId(null);
            courseBean.setImei(deviceid);
            courseBean.setJson(updateJson());
            BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().insert(courseBean);

        }
    }

    //更新数据库的Json
    private String updateJson(){
        if(isMorningOpen) {
            disableEntity.getMorning().setStatus("1");
        }else {
            disableEntity.getMorning().setStatus("0");
        }
        disableEntity.getMorning().setStartTime(tv_morning_begin.getText().toString().trim());
        disableEntity.getMorning().setEndTime(tv_morning_finish.getText().toString().trim());

        if(isAfternoonOpen) {
            disableEntity.getAfternoon().setStatus("1");
        }else {
            disableEntity.getAfternoon().setStatus("0");
        }
        disableEntity.getAfternoon().setStartTime(tv_afternoon_begin.getText().toString().trim());
        disableEntity.getAfternoon().setEndTime(tv_afternoon_finish.getText().toString().trim());

        String week = handleWeek();
        disableEntity.setDaily(week);

        disableEntities=new ArrayList<>();
        disableEntities.add(disableEntity);

        DisableResultsEntity disableResultsEntity = new DisableResultsEntity();
        disableResultsEntity.setResults(new DisableResultsEntity.results(deviceid,ConstantClassFunction.TIMETABLES,disableEntities));
        return GsonUtil.toJson(disableResultsEntity);
    }



    private String handleWeek() {
        StringBuilder sb = new StringBuilder();
        if(isMonSelect) {
            sb.append("Monday|");
        }
        if(isTueSelect) {
            sb.append("Tuesday|");
        }
        if(isWedSelect) {
            sb.append("Wednesday|");
        }
        if(isThurSelect) {
            sb.append("Thursday|");
        }
        if(isFriSelect) {
            sb.append("Friday|");
        }
        if(isSatSelect) {
            sb.append("Saturday|");
        }
        if(isSunSelect) {
            sb.append("Sunday|");
        }
        String s = sb.toString();
        return s.substring(0, s.length() - 1);
    }

    private void showSelectDialog(final int type, boolean isMorning) {
        ClassDisableSelectTimeDialog dialog = new ClassDisableSelectTimeDialog(this, isMorning);
        dialog.show();
        dialog.setonDisableTimeListener(new ClassDisableSelectTimeDialog.OnDisableTimeListener() {
            @Override
            public void onClick(String hour, String minute) {
                switch (type){
                    case 1:
                        morbeginH=Integer.valueOf(hour.substring(0,2));
                        morbeginM=Integer.valueOf(minute.substring(0,2));
                        tv_morning_begin.setText(hour.substring(0,2)+":"+minute.substring(0,2));
                        break;
                    case 2:
                        morfinishH=Integer.valueOf(hour.substring(0,2));
                        morbeginM=Integer.valueOf(minute.substring(0,2));
                        tv_morning_finish.setText(hour.substring(0,2)+":"+minute.substring(0,2));
                        break;
                    case 3:
                        aftbeginH=Integer.valueOf(hour.substring(0,2));
                        aftbeginM=Integer.valueOf(minute.substring(0,2));
                        tv_afternoon_begin.setText(hour.substring(0,2)+":"+minute.substring(0,2));
                        break;
                    case 4:
                        aftbeginH=Integer.valueOf(hour.substring(0,2));
                        aftbeginM=Integer.valueOf(minute.substring(0,2));
                        tv_afternoon_finish.setText(hour.substring(0,2)+":"+minute.substring(0,2));
                        break;
                }
            }
        });


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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
    }
}
