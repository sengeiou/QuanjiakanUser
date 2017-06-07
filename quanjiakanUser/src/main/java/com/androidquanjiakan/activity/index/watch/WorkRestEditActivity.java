package com.androidquanjiakan.activity.index.watch;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.setting.contact.bean.ResultBean;
import com.androidquanjiakan.activity.setting.contact.bean.WorKRestEvent;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.ScheduleAddEntity;
import com.androidquanjiakan.entity.ScheduleBean;
import com.androidquanjiakan.entity.ScheduleDeleteEntity;
import com.androidquanjiakan.entity.ScheduleEntity;
import com.androidquanjiakan.entity.ScheduleListEntity;
import com.androidquanjiakan.entity.ScheduleResultsEntity;
import com.androidquanjiakan.entity.ScheduleUpdateEntity;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.EditTextFilter;
import com.androidquanjiakan.util.GsonUtil;
import com.androidquanjiakan.util.LogUtil;
import com.quanjiakan.main.R;
import com.quanjiakanuser.widget.AbstractWheelTextAdapter1;
import com.quanjiakanuser.widget.OnWheelChangedListener;
import com.quanjiakanuser.widget.OnWheelScrollListener;
import com.quanjiakanuser.widget.WheelView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


/**
 * Created by Gin on 2017/2/21.
 */

public class WorkRestEditActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibtn_back;
    private TextView tv_title;
    private TextView menu_text;
    private WheelView wv_hour;
    private WheelView wv_minute;
    private ArrayList<String> hourList ;
    private ArrayList<String> minuteList;
    private Calendar calendar;
    private Context context;
    private String strHour;
    private String strMinute;
    private int maxsize = 24;
    private int minsize = 14;
    private SelectTimeAdapter hourAdapter;
    private SelectTimeAdapter minuteAdapter;
    private TextView tv_mon;
    private TextView tv_tue;
    private TextView tv_wed;
    private TextView tv_thur;
    private TextView tv_fri;
    private TextView tv_sat;
    private TextView tv_sun;
    private boolean isMonSelect=false;
    private boolean isTueSelect=false;
    private boolean isWedSelect=false;
    private boolean isThurSelect=false;
    private boolean isFriSelect=false;
    private boolean isSatSelect=false;
    private boolean isSunSelect=false;
    private RelativeLayout rl_plan;
    private TextView tv_plan_name;
    private Dialog planDialog;
    private boolean isOpen=true;
    private ImageView iv_close_open;
    private Button btn_save;
    private String content;
    private StringBuilder sb=null;
    private String click_type;
    public static final String WEEK = "week";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String CONTENT = "content";
    public static final String TYPE = "type";
    public static final int ADD_RESULT_OK=300;
    public static final int EDIT_RESULT_OK=400;
    public static final int DELETE_RESULT_OK=500;
    private String weekStr;
    private String hourStr;
    private String minuteStr;
    private String contentStr;
    private String type;
    private String currentHour ;
    private String currentMinu ;
    private String Id;
    private ScheduleResultsEntity scheduleResultsEntity;
    private ScheduleEntity scheduleEntity;
    private static String ADD_SCHEDULE="添加";
    private static String DELETE_SCHEDULE="删除";
    private static String UPDATE_SCHEDULE="更新";
    private static String CATEGORY="Schedule";
    private String content1;
    private List<ScheduleEntity> list;
    private String deviceid;
    private Dialog dialogEdit;
    private Dialog dialogDelete;
    private Dialog dialogAdd;
    private boolean booleanFour;
    private int morningSize;
    private int afternoonSize;
    private int eveningSize;
    private boolean isClose;
    private String num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workrest_edit);
        context=WorkRestEditActivity.this;
        calendar = Calendar.getInstance();
        type = getIntent().getStringExtra(TYPE);
        EventBus.getDefault().register(this);

        if ("1".equals(type)){  //添加计划
            int hour= calendar.get(Calendar.HOUR_OF_DAY);
            if(hour<10) {
                currentHour="0"+hour;
            }else {
                currentHour=hour+"";
            }

            int minute = calendar.get(Calendar.MINUTE);
            if(minute<10) {
                currentMinu="0"+minute;
            }else {
                currentMinu=minute+"";
            }
            deviceid=getIntent().getStringExtra("device_id");
            num = getIntent().getStringExtra("num");
            LogUtil.e(num);
            LogUtil.e(deviceid);

            ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
            ibtn_back.setVisibility(View.VISIBLE);
            ibtn_back.setOnClickListener(this);

            tv_title = (TextView) findViewById(R.id.tv_title);
            tv_title.setText("添加计划");

            menu_text = (TextView) findViewById(R.id.menu_text);
            menu_text.setVisibility(View.GONE);
            menu_text.setText("删除");
            menu_text.setOnClickListener(this);

        }else if ("2".equals(type)){    //修改和删除计划
            weekStr = getIntent().getStringExtra(WEEK);
            hourStr = getIntent().getStringExtra(HOUR);
            minuteStr = getIntent().getStringExtra(MINUTE);
            contentStr = getIntent().getStringExtra(CONTENT);
            Id = getIntent().getStringExtra("item");
            isClose = getIntent().getBooleanExtra("isClose", false);

            LogUtil.e(weekStr+","+hourStr+","+minuteStr+","+contentStr+","+Id);
            //这里要删除和更新id好点  3-4-5-6-7-,06,59,吃,43

            deviceid=getIntent().getStringExtra("device_id");
            LogUtil.e(deviceid);

            if (weekStr!=null){
                String[] split = weekStr.split("-");
                for (int i=0;i<split.length;i++){
                    switch (Integer.parseInt(split[i])){
                        case 1:
                            isMonSelect = true;
                            break;
                        case 2:
                            isTueSelect = true;
                            break;

                        case 3:
                            isWedSelect = true;
                            break;

                        case 4:
                            isThurSelect = true;
                            break;

                        case 5:
                            isFriSelect = true;
                            break;

                        case 6:
                            isSatSelect = true;
                            break;

                        case 7:
                            isSunSelect = true;
                            break;

                    }

                }
            }

            currentHour = hourStr;
            currentMinu = minuteStr;

            ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
            ibtn_back.setVisibility(View.VISIBLE);
            ibtn_back.setOnClickListener(this);

            tv_title = (TextView) findViewById(R.id.tv_title);
            tv_title.setText("编辑计划");

            menu_text = (TextView) findViewById(R.id.menu_text);
            menu_text.setVisibility(View.VISIBLE);
            menu_text.setText("删除");
            menu_text.setOnClickListener(this);


            if(isClose) {
                isOpen=false;
            }else {
                isOpen=true;
            }
        }



        //initTitle();
        initView();
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

    private void initView() {
        wv_hour = (WheelView) findViewById(R.id.wv_hour);
        wv_minute = (WheelView) findViewById(R.id.wv_minute);
        wv_hour.setCyclic(true);
        wv_minute.setCyclic(true);



        //strHour=calendar.get(Calendar.HOUR_OF_DAY)+"";
        strHour=getHour(Integer.valueOf(currentHour));
        //strMinute=calendar.get(Calendar.MINUTE)+"";
        strMinute=getMinute(Integer.valueOf(currentMinu));

        initClock();//初始化时间

        hourAdapter = new SelectTimeAdapter(context, hourList, Integer.valueOf(currentHour), maxsize, minsize,getResources().getColor(R.color.color_xun_ing),getResources().getColor(R.color.font_color_666666));
        wv_hour.setVisibleItems(5);
        wv_hour.setViewAdapter(hourAdapter);
        wv_hour.setCurrentItem(getHourItem(strHour));



        //初始化分钟
        initMinute();

        minuteAdapter = new SelectTimeAdapter(context, minuteList, Integer.valueOf(currentMinu), maxsize, minsize,getResources().getColor(R.color.color_xun_ing),getResources().getColor(R.color.font_color_666666));
        wv_minute.setVisibleItems(5);
        wv_minute.setViewAdapter(minuteAdapter);
        wv_minute.setCurrentItem(getMinuteItem(strMinute));


        wv_hour.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) hourAdapter.getItemText(wheel.getCurrentItem());
                strHour=currentText;
                currentHour=strHour;
                setTextViewSizeColor(strHour,hourAdapter);

            }
        });


        wv_hour.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) hourAdapter.getItemText(wheel.getCurrentItem());
                setTextViewSizeColor(currentText,hourAdapter);

            }
        });


        wv_minute.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) minuteAdapter.getItemText(wheel.getCurrentItem());
                strMinute=currentText;
                currentMinu=strMinute;
                setTextViewSizeColor(currentText,minuteAdapter);
            }
        });

        wv_minute.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) minuteAdapter.getItemText(wheel.getCurrentItem());
                setTextViewSizeColor(currentText,minuteAdapter);
            }
        });


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


        if(isMonSelect) {
            tv_mon.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_mon.setTextColor(getResources().getColor(R.color.white));
        }
        if(isTueSelect) {
            tv_tue.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_tue.setTextColor(getResources().getColor(R.color.white));
        }
        if(isWedSelect) {
            tv_wed.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_wed.setTextColor(getResources().getColor(R.color.white));
        }
        if(isThurSelect) {
            tv_thur.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_thur.setTextColor(getResources().getColor(R.color.white));
        }
        if(isFriSelect) {
            tv_fri.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_fri.setTextColor(getResources().getColor(R.color.white));
        }
        if(isSatSelect) {
            tv_sat.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_sat.setTextColor(getResources().getColor(R.color.white));
        }

        if(isSunSelect) {
            tv_sun.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_sun.setTextColor(getResources().getColor(R.color.white));
        }

        rl_plan = (RelativeLayout) findViewById(R.id.rl_plan);
        tv_plan_name = (TextView) findViewById(R.id.tv_plan_name);
        if ("2".equals(type)){
            tv_plan_name.setText(contentStr);
            content=contentStr;
        }else {
            tv_plan_name.setText("");
        }
        rl_plan.setOnClickListener(this);

        iv_close_open = (ImageView) findViewById(R.id.iv_close_open);

        iv_close_open.setOnClickListener(this);

        if(isOpen) {
            iv_close_open.setImageDrawable(getResources().getDrawable(R.drawable.edit_btn_open));
        }else {
            iv_close_open.setImageDrawable(getResources().getDrawable(R.drawable.edit_btn_close));
        }

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);


    }

    private void initMinute() {
        int minute = calendar.get(Calendar.MINUTE);
        minuteList=new ArrayList<>();
        for (int i=0;i<60;i++){

            if(i<minute) {
                minuteList.add(getMinute(i));
            }else {
                if(i>59) {
                    return;
                }
                minuteList.add(getMinute(i));
            }


        }
    }

    //初始化返回正确的分钟参数
    public String getMinute(int minute2 ){

        if(minute2>60) {
            this.minute=minute2%60;
        }else {
            this.minute=minute2;
        }

        if(minute<10) {
            return "0"+this.minute+"分";
        }
        return this.minute+"分";


    }

    private int hour;
    private int minute;
    //初始化时间
    private void initClock() {
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        hourList=new ArrayList<>();

        for (int i = 0; i <24; i++) {

            if(i<hour) {
                hourList.add(getHour(i));
            }else {
                if(i>23) {
                    return;
                }
                hourList.add(getHour(i));
            }

        }
    }



    public int getMinuteItem(String minute){
        int size=minuteList.size();
        int minuteIndex=0;
        for (int i=0;i<size;i++){
            if(minute.equals(minuteList.get(i))){
                return minuteIndex;
            }else {
                minuteIndex++;

            }
        }
        return minuteIndex;

    }

    //初始化返回正确的小时参数
    public String getHour(int hour2){
        if(hour2!=-1) {
            if(hour2 > 24){
                this.hour = hour2 % 24;
            }else
                this.hour = hour2;
        }

        if(hour<10) {
            return "0"+this.hour+"时";
        }
        return this.hour+"时";
    }

    public int getHourItem(String hour){
        int size=hourList.size();
        int hourIndex=0;
        for (int i = 0; i < size; i++) {
            if (hour.equals(hourList.get(i))) {
                return hourIndex;
            } else {
                hourIndex++;
            }
        }
        return hourIndex;

    }


    /**
     * 设置字体大小和颜色
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextViewSizeColor(String curriteItemText, SelectTimeAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(maxsize);
                textvew.setTextColor(getResources().getColor(R.color.color_xun_ing));
            } else {
                textvew.setTextSize(minsize);
                textvew.setTextColor(getResources().getColor(R.color.font_color_666666));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.menu_text:
                if(!NetUtil.isNetworkAvailable(this)) {
                    Toast.makeText(this, "网络连接不可用!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //请求网络成功之后,关闭界面
                deletePlan();
                break;
            case R.id.tv_mon:
                if(isMonSelect) {
                    tv_mon.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_mon.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_mon.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_mon.setTextColor(getResources().getColor(R.color.white));
                }
                isMonSelect=!isMonSelect;
                break;
            case R.id.tv_tue:
                if(isTueSelect) {
                    tv_tue.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_tue.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_tue.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_tue.setTextColor(getResources().getColor(R.color.white));
                }
                isTueSelect=!isTueSelect;
                break;
            case R.id.tv_wed:
                if(isWedSelect) {
                    tv_wed.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_wed.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_wed.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_wed.setTextColor(getResources().getColor(R.color.white));
                }
                isWedSelect=!isWedSelect;
                break;
            case R.id.tv_thur:
                if(isThurSelect) {
                    tv_thur.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_thur.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_thur.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_thur.setTextColor(getResources().getColor(R.color.white));
                }
                isThurSelect=!isThurSelect;
                break;
            case R.id.tv_fri:
                if(isFriSelect) {
                    tv_fri.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_fri.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_fri.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_fri.setTextColor(getResources().getColor(R.color.white));
                }
                isFriSelect=!isFriSelect;
                break;
            case R.id.tv_sat:
                if(isSatSelect) {
                    tv_sat.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_sat.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_sat.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_sat.setTextColor(getResources().getColor(R.color.white));
                }
                isSatSelect=!isSatSelect;
                break;
            case R.id.tv_sun:
                if(isSunSelect) {
                    tv_sun.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_sun.setTextColor(getResources().getColor(R.color.font_color_333333));
                }else {
                    tv_sun.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_sun.setTextColor(getResources().getColor(R.color.white));
                }
                isSunSelect=!isSunSelect;
                break;
            case R.id.rl_plan:
                showEditPlanDialog();
                break;
            case R.id.iv_close_open:
                if(isOpen) {
                    iv_close_open.setImageDrawable(getResources().getDrawable(R.drawable.edit_btn_close));
                }else {
                    iv_close_open.setImageDrawable(getResources().getDrawable(R.drawable.edit_btn_open));
                }
                isOpen=!isOpen;
                LogUtil.e("开关:"+isOpen);
                break;
            case R.id.btn_save:
                if(!NetUtil.isNetworkAvailable(this)) {
                    Toast.makeText(this, "网络连接不可用!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if("1".equals(type)) {
                    addPlan();
                }else if("2".equals(type)) {
                    editPlan();
                }
                break;
        }

    }

    //删除的请求
    private void deletePlan() {
        click_type=DELETE_SCHEDULE;
        if(BaseApplication.getInstances().isSDKConnected()) {
            //进行相关网络请求
            ScheduleDeleteEntity scheduleDeleteEntity = new ScheduleDeleteEntity();
            scheduleDeleteEntity.setIMEI(deviceid);
            scheduleDeleteEntity.setAction(ConstantClassFunction.DELETE);
            scheduleDeleteEntity.setCategory(ConstantClassFunction.SCHEDULE);
            scheduleDeleteEntity.setId(Id);

            String json = GsonUtil.toJson(scheduleDeleteEntity);
            dialogDelete = QuanjiakanDialog.getInstance().getDialog(this);
            dialogDelete.show();
            Intent intent = new Intent();
            intent.putExtra("type","3");
            setResult(DELETE_RESULT_OK,intent);

            BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(deviceid,16),json.getBytes(),json.length());

        }else {
            Toast.makeText(WorkRestEditActivity.this, ConstantClassFunction.CONNECTED_ERROR, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    //更改的请求
    private void editPlan() {
        if(!isSelectedWeek()) {
            Toast.makeText(WorkRestEditActivity.this, "您未设置日期", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(content)){
            BaseApplication.getInstances().toast(WorkRestEditActivity.this,"请设置计划名称");
            return;
        }

        if((CheckUtil.isAllChineseChar(content)&&content.length()>4)) {
            Toast.makeText(WorkRestEditActivity.this, "请输入正确的计划名称", Toast.LENGTH_SHORT).show();
            return;
        }

        if(content.length()>8) {
            Toast.makeText(WorkRestEditActivity.this, "请输入正确的计划名称", Toast.LENGTH_SHORT).show();
            return;
        }
        click_type=UPDATE_SCHEDULE;
        String json = handEditJson();
        LogUtil.e("Edit"+json);
        if(BaseApplication.getInstances().isSDKConnected()) {
            dialogEdit = QuanjiakanDialog.getInstance().getDialog(this);
            dialogEdit.show();
            Intent intent=new Intent();
            intent.putExtra("type","2");
            setResult(EDIT_RESULT_OK,intent);
            BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(deviceid,16),json.getBytes(),json.length());

        }else {
            Toast.makeText(WorkRestEditActivity.this, ConstantClassFunction.CONNECTED_ERROR, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    //发更改数据库的json
    private String handEditJson() {
        String rightHour;
        String rightMin;
        ScheduleUpdateEntity scheduleUpdateEntity = new ScheduleUpdateEntity();

        scheduleUpdateEntity.setId(Id);
        scheduleUpdateEntity.setCategory(ConstantClassFunction.SCHEDULE);
        scheduleUpdateEntity.setAction(ConstantClassFunction.UPDATE);
        scheduleUpdateEntity.setIMEI(deviceid);

        ScheduleEntity scheduleEntity = new ScheduleEntity();
        scheduleEntity.setDaily(handleWeek());
        if(currentHour.contains("时")) {
            rightHour=currentHour.substring(0,2);
        }else {
            rightHour=currentHour;
        }
        if(currentMinu.contains("分")) {
            rightMin=currentMinu.substring(0,2);
        }else {
            rightMin=currentMinu;
        }
        scheduleEntity.setTime(rightHour+":"+rightMin);
        if(isOpen) {
            scheduleEntity.setStatus("1");
        }else {
            scheduleEntity.setStatus("0");
        }
        try {
            String encode = URLEncoder.encode(tv_plan_name.getText().toString().trim(), "utf-8");
            scheduleEntity.setDetails(encode);
            LogUtil.e(encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        scheduleUpdateEntity.setSchedule(scheduleEntity);

        return GsonUtil.toJson(scheduleUpdateEntity);

    }

    //判断是否有选择日期
    private boolean isSelectedWeek(){

        if(isMonSelect) {
            return true;
        }else if(isTueSelect) {
            return true;
        }else if(isWedSelect) {
            return true;
        }else if(isThurSelect){
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

    private String getWeekStr() {

        sb = new StringBuilder();
        if (isMonSelect){
            sb.append("Monday").append(",");
        }
        if (isTueSelect){
            sb.append("2").append(",");
        }

        if(isWedSelect){
            sb.append("3").append(",");
        }
        if(isThurSelect){
            sb.append("4").append(",");
        }
        if(isFriSelect){
            sb.append("5").append(",");
        }
        if(isSatSelect){
            sb.append("6").append(",");
        }

        if(isSunSelect){
            sb.append("7").append(",");
        }

        return sb.toString();
    }

    //添加计划
    private void addPlan() {
        if (!isSelectedWeek()) {
            Toast.makeText(WorkRestEditActivity.this, "您未设置日期", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(content)) {
            BaseApplication.getInstances().toast(WorkRestEditActivity.this,"请设置计划名称");
            return;
        }

        if((CheckUtil.isAllChineseChar(content)&&content.length()>4)) {
            Toast.makeText(WorkRestEditActivity.this, "请输入正确的计划名称", Toast.LENGTH_SHORT).show();
            return;
        }

        if(content.length()>8) {
            Toast.makeText(WorkRestEditActivity.this, "请输入正确的计划名称", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtil.e("content:"+content.length());

        //以后做
        /*if(!isBooleanSix()) {
            Toast.makeText(WorkRestEditActivity.this, "最多添加六条", Toast.LENGTH_SHORT).show();
            return;
        }*/

        if(num!=null&&"6".equals(num)) {
            Toast.makeText(WorkRestEditActivity.this, "最多添加六条", Toast.LENGTH_SHORT).show();
            return;
        }

        click_type = ADD_SCHEDULE;
        String json = handAddJson();
        LogUtil.e("ADD" + json);
        //{"Results":{"Category":"Schedule","IMEI":"352315052834187","Num":2,"Schedule":[{"Daily":"Monday","Details":"%E5%8A%A0%E7%8F%AD","Id":113,"Time":"12:14:08"},{"Daily":"Wednesday","Details":"%E4%B8%8A%E7%8F%AD","Id":114,"Time":"11:14:35"}]}}

        if (BaseApplication.getInstances().isSDKConnected()) {
            dialogAdd = QuanjiakanDialog.getInstance().getDialog(this);
            dialogAdd.show();
            BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(deviceid, 16), json.getBytes(), json.length());
            Intent intent = new Intent();
            intent.putExtra("type", "1");
            setResult(ADD_RESULT_OK, intent);
        } else {
            Toast.makeText(WorkRestEditActivity.this, ConstantClassFunction.CONNECTED_ERROR, Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    //处理自己修改的数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleNativeDataResult(WorKRestEvent event) {
        if(event.getJson()!=null) {
            try {
                JSONObject jsonObject = new JSONObject(event.getJson());
                if(jsonObject.has(ConstantClassFunction.getRESULTS())&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).has(ConstantClassFunction.CODE)&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).has(ConstantClassFunction.MESSAGE)) {
                    ResultBean resultBean = GsonUtil.fromJson(event.getJson(), ResultBean.class);
                    if(resultBean.getResult().getCode().equals("200")&&resultBean.getResult().getMessage().equals("Success")) {
                        if(click_type!=null&&click_type.equals(ADD_SCHEDULE)) {
                            //添加
                            if(dialogAdd!=null) {
                                dialogAdd.dismiss();
                            }
                            LogUtil.e(event.getJson()+"dddfdfsfsdf");
                            LogUtil.e(jsonObject.toString()+"fffffffff");
                            if(jsonObject.has("Id")) {
                                Toast.makeText(WorkRestEditActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                finish();

                                //添加数据库(不要做)
                                //addTabInfo(id);
                                //finish();
                               // WorkRestEditActivity.this.finish();
                            }else {
                                Toast.makeText(WorkRestEditActivity.this, "手表设置不成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }else if(click_type!=null&&click_type.equals(UPDATE_SCHEDULE)) {
                            //更新
                            if(dialogEdit!=null) {
                                dialogEdit.dismiss();
                            }
                            //更新数据库(不要做)
                            Toast.makeText(WorkRestEditActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                            //updateTabInfo();
                            finish();

                        }else if(click_type!=null&&click_type.equals(DELETE_SCHEDULE)) {
                            //删除
                            if(dialogDelete!=null) {
                                dialogDelete.dismiss();
                            }
                            //删除数据库操作(不要做)
                            //deleteTabInfo();
                            Toast.makeText(WorkRestEditActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }else if(resultBean.getResult().getCode().equals("10001")) {
                        if(dialogAdd!=null) {
                            dialogAdd.dismiss();
                        }
                        if(dialogEdit!=null) {
                            dialogEdit.dismiss();
                        }
                        if(dialogDelete!=null) {
                            dialogDelete.dismiss();
                        }
                        Toast.makeText(WorkRestEditActivity.this, "手表不在线", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Toast.makeText(WorkRestEditActivity.this, "手表设置不成功", Toast.LENGTH_SHORT).show();
                        if(dialogAdd!=null) {
                            dialogAdd.dismiss();
                        }
                        if(dialogEdit!=null) {
                            dialogEdit.dismiss();
                        }
                        if(dialogDelete!=null) {
                            dialogDelete.dismiss();
                        }
                        finish();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            Toast.makeText(WorkRestEditActivity.this, "手表设置不成功", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    //增加数据库操作
    private void addTabInfo(String id) {
         if(BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list().size()>0) {
             List<ScheduleBean> list = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list();
             for (int i=0;i<list.size();i++){
                 if(deviceid.equals(list.get(i).getImei())) {
                     ScheduleBean scheduleBean = list.get(i);
                     String json = saveJson(i, id);
                     scheduleBean.setJson(json);//id会返回过来
                     BaseApplication.getDaoInstant().getScheduleBeanDao().update(scheduleBean);
                     finish();
                     break;
                 }else {
                     if(i==list.size()-1) {
                         String json = saveJson(-1, id);
                         ScheduleBean scheduleBean = new ScheduleBean(null,json,deviceid);
                         BaseApplication.getDaoInstant().getScheduleBeanDao().insert(scheduleBean);
                         finish();
                     }
                 }
             }
        }else {
             String json = saveJson(-1, id);
             ScheduleBean scheduleBean = new ScheduleBean(null,json,deviceid);
             BaseApplication.getDaoInstant().getScheduleBeanDao().insert(scheduleBean);
             finish();
        }
    }

    //更新数据库操作
    private void updateTabInfo() {
        List<ScheduleBean> list = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list();
        for (int i=0;i<list.size();i++){
            if(deviceid.equals(list.get(i).getImei())) {
                ScheduleBean scheduleBean = list.get(i);
                String json = scheduleBean.getJson();
                ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(json, ScheduleResultsEntity.class);
                ScheduleResultsEntity.results results = scheduleResultsEntity.getResults();
                results.setSchedule(getUpdateList(i));


                scheduleResultsEntity.setResults(results);
                String toJson = GsonUtil.toJson(scheduleResultsEntity);

                scheduleBean.setJson(toJson);
                BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().update(scheduleBean);

            }

        }



    }

    //删除数据库操作
    public void deleteTabInfo(){
        List<ScheduleBean> list = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list();
        for (int i=0;i<list.size();i++){
            if(deviceid.equals(list.get(i).getImei())) {
                LogUtil.e("IDDDD:"+i);

                ScheduleBean scheduleBean =list.get(i);
                String json = scheduleBean.getJson();
                ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(json, ScheduleResultsEntity.class);
                ScheduleResultsEntity.results results = scheduleResultsEntity.getResults();
                if(getDeleteList(i)==null) {
                    LogUtil.e("null进啦吧");
                    BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().delete(scheduleBean);
                    List<ScheduleBean> list1 = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list();

                    break;
                }else {
                    results.setSchedule(getDeleteList(i));
                }

                scheduleResultsEntity.setResults(results);
                String toJson = GsonUtil.toJson(scheduleResultsEntity);

                scheduleBean.setJson(toJson);
                BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().update(scheduleBean);
            }
            
        }



    }

    //获取要删除对应Id的ScheduleListEntity列表
    private List<ScheduleListEntity> getDeleteList(int t){

        ScheduleBean scheduleBean = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list().get(t);
        String json = scheduleBean.getJson();
        ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(json, ScheduleResultsEntity.class);

        List<ScheduleListEntity> schedule = scheduleResultsEntity.getResults().getSchedule();
        if(schedule.size()==1) {
            return null;
        }
        for (int i=0;i<schedule.size();i++){
            ScheduleListEntity scheduleListEntity = schedule.get(i);
            if(Id.equals(scheduleListEntity.getId())) {
                schedule.remove(i);
                return schedule;
            }

        }
        return new ArrayList<>();
    }

    //获取要更新对应imei和Id的ScheduleListEntity列表
    private List<ScheduleListEntity> getUpdateList(int t){
        String rightHour;
        String rightMin;
        ScheduleBean scheduleBean = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list().get(t);
        String json = scheduleBean.getJson();
        ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(json, ScheduleResultsEntity.class);

        List<ScheduleListEntity> schedule = scheduleResultsEntity.getResults().getSchedule();

        for (int i=0;i<schedule.size();i++){
            ScheduleListEntity scheduleListEntity = schedule.get(i);
            if(Id.equals(scheduleListEntity.getId())) {
                ScheduleListEntity scheduleListEntity1 = schedule.get(i);
                try {
                    String encode = URLEncoder.encode(tv_plan_name.getText().toString(), "utf-8");
                    scheduleListEntity1.setDetails(encode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if(currentHour.contains("时")) {
                    rightHour=currentHour.substring(0,2);
                }else {
                    rightHour=currentHour;
                }
                if(currentMinu.contains("分")) {
                    rightMin=currentMinu.substring(0,2);
                }else {
                    rightMin=currentMinu;
                }
                scheduleListEntity1.setTime(rightHour+":"+rightMin);
                scheduleListEntity1.setId(Id);
                scheduleListEntity1.setDaily(handleWeek());
                if(isOpen) {
                    scheduleListEntity1.setStatus("1");
                }else {
                    scheduleListEntity1.setStatus("0");
                }
                return schedule;
            }

        }
        return schedule;
    }


    private String saveJson(int i,String id){
        String rightHour;
        String rightMin;
        if(currentHour.contains("时")) {
            rightHour=currentHour.substring(0,2);
        }else {
            rightHour=currentHour;
        }
        if(currentMinu.contains("分")) {
            rightMin=currentMinu.substring(0,2);
        }else {
            rightMin=currentMinu;
        }
        /*scheduleEntity = new ScheduleEntity();
        scheduleEntity.setDaily(handleWeek());
        scheduleEntity.setTime(currentHour+":"+currentMinu);
        scheduleEntity.setDetails(tv_plan_name.getText().toString().trim());*/
        ScheduleListEntity scheduleListEntity = new ScheduleListEntity();
        scheduleListEntity.setDaily(handleWeek());
        scheduleListEntity.setId(id);
        if(isOpen) {
            scheduleListEntity.setStatus("1");
        }else {
            scheduleListEntity.setStatus("0");
        }
        scheduleListEntity.setTime(rightHour+":"+rightMin);
        try {
            String encode = URLEncoder.encode(tv_plan_name.getText().toString().trim(), "utf-8");
            scheduleListEntity.setDetails(encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list().size()>0&&i>=0) {
            //直接添加实体到集合中
            ScheduleBean scheduleBean = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list().get(i);
            String json = scheduleBean.getJson();
            ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(json, ScheduleResultsEntity.class);
            ScheduleResultsEntity.results results = GsonUtil.fromJson(json, ScheduleResultsEntity.class).getResults();
            List<ScheduleListEntity> scheduleList = GsonUtil.fromJson(json, ScheduleResultsEntity.class).getResults().getSchedule();
            scheduleList.add(scheduleListEntity);
            //更新数据库
            Collections.sort(scheduleList);//排序
            results.setSchedule(scheduleList);
            scheduleResultsEntity.setResults(results);
            return GsonUtil.toJson(scheduleResultsEntity);
        }else {
            List<ScheduleListEntity> scheduleList=new ArrayList<>();
            scheduleList.add(scheduleListEntity);
            ScheduleResultsEntity scheduleResultsEntity = new ScheduleResultsEntity();
            scheduleResultsEntity.setResults(new ScheduleResultsEntity.results(deviceid, ConstantClassFunction.SCHEDULE,"",scheduleList));
            return GsonUtil.toJson(scheduleResultsEntity);
        }



    }


    //发送增加作息计划的json
    private String  handAddJson() {
        String rightHour;
        String rightMin;
        scheduleEntity = new ScheduleEntity();
        scheduleEntity.setDaily(handleWeek());
        if(currentHour.contains("时")) {
            rightHour=currentHour.substring(0,2);
        }else {
            rightHour=currentHour;
        }
        if(currentMinu.contains("分")) {
            rightMin=currentMinu.substring(0,2);
        }else {
            rightMin=currentMinu;
        }
        scheduleEntity.setTime(rightHour+":"+rightMin);
        if(isOpen) {
            scheduleEntity.setStatus("1");
        }else {
            scheduleEntity.setStatus("0");
        }
        LogUtil.e(isOpen+","+scheduleEntity.getStatus());
        try {
            String encode = URLEncoder.encode(tv_plan_name.getText().toString().trim(), "utf-8");
            scheduleEntity.setDetails(encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ScheduleAddEntity scheduleAddEntity = new ScheduleAddEntity();
        scheduleAddEntity.setAction(ConstantClassFunction.ADD);
        scheduleAddEntity.setCategory(ConstantClassFunction.SCHEDULE);
        scheduleAddEntity.setIMEI(deviceid);
        scheduleAddEntity.setSchedule(scheduleEntity);

        return GsonUtil.toJson(scheduleAddEntity);

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

    private void showEditPlanDialog() {
        planDialog = new Dialog(this, R.style.ShareDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_child_plan_edit, null);
        final EditText tv_content = (EditText) view.findViewById(R.id.tv_content);
        EditTextFilter.setEditTextInhibitInputSpace(tv_content);
        TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_content.length()<1){
                    BaseApplication.getInstances().toast(WorkRestEditActivity.this,"请输入相关数据!");
                    return;
                }




                tv_plan_name.setText(tv_content.getText().toString());
                content = tv_content.getText().toString();

                if((CheckUtil.isAllChineseChar(content)&&content.length()>4)) {
                    Toast.makeText(WorkRestEditActivity.this, "请输入正确的计划名称", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(content.length()>8) {
                    Toast.makeText(WorkRestEditActivity.this, "请输入正确的计划名称", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(planDialog!=null){
                    planDialog.dismiss();
                }


            }
        });

        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(planDialog!=null){
                    planDialog.dismiss();
                }
            }
        });

        WindowManager.LayoutParams lp = planDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        planDialog.setContentView(view, lp);
        planDialog.show();
    }

    public boolean isBooleanSix() {
        List<ScheduleBean> list = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list();
        LogUtil.e("LIST:"+list.size());
        if(list.size()>0) {
            for (int t = 0; t < list.size(); t++) {
                if (deviceid.equals(list.get(t).getImei())) {
                    ScheduleBean scheduleBean = list.get(t);
                    String json = scheduleBean.getJson();
                    ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(json, ScheduleResultsEntity.class);
                    List<ScheduleListEntity> schedule = scheduleResultsEntity.getResults().getSchedule();
                    if (schedule.size() >= 6) {
                        return false;
                    }
                }
            }
        }
       return true;
    }


    private class SelectTimeAdapter extends AbstractWheelTextAdapter1 {

        ArrayList list;
        protected SelectTimeAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize,
                                    int minsize,int currentColor,int oldColor) {
            super(context, R.layout.item_hour_minute, NO_RESOURCE, currentItem, maxsize, minsize,currentColor,oldColor);
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
    }
}
