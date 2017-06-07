package com.androidquanjiakan.activity.index.watch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.Course;
import com.androidquanjiakan.entity.ScheduleBean;
import com.androidquanjiakan.entity.ScheduleListEntity;
import com.androidquanjiakan.entity.ScheduleResultsEntity;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.util.GsonUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.view.CourseTableView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Gin on 2017/2/24.
 */

public class WorkRestPlanActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibtn_back;
    private TextView tv_title;
    private ImageButton ibtn_menu;

    private CourseTableView courseTableView;

    public static final int ADD_PLAN=100;
    public static final int EDIT_PLAN=200;
    private int jiechi;
    private int jiechiM=0;
    private int jiechiA=4;
    private int jiechiE=8;
    private List<Course> list;
    private String type;
    private String deviceid;
    private HashMap<String ,String> map;
    private String num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workrest_plan);
        deviceid=getIntent().getStringExtra("device_id");
        LogUtil.e(deviceid);
        courseTableView = (CourseTableView) findViewById(R.id.ctv);
        list = new ArrayList<>();
        map=new HashMap<>();

        initTitle();
        initData();

    }


    public String changeDaily(String daily){
        if(daily.contains(" ")) {
            daily.replace(" ","");
        }
        String s = daily + "|";
        String replace = s.replace("|", ",");
        String[] split = replace.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<split.length;i++){
            String s1 = split[i];
            if(s1.equals("Monday")) {
                s1="1";
            } else if(s1.equals("Tuesday")) {
                s1="2";
            } else if(s1.equals("Wednesday")) {
                s1="3";
            } else if(s1.equals("Thursday")) {
                s1="4";
            } else if(s1.equals("Friday")) {
                s1="5";
            }else if(s1.equals("Saturday")) {
                s1="6";
            }else if(s1.equals("Sunday")) {
                s1="7";
            }
            sb.append(s1+",");
        }
        return sb.toString();


    }

    private void initData() {
        if(!NetUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "网络连接不可用!", Toast.LENGTH_SHORT).show();
            setDataFromDB();
            return;
        }
        getReq();

    }

    //从数据库拿出数据展示
    private void setDataFromDB(){
        if(itemRows(0)==5||itemRows(1)==5||itemRows(2)==5) {
            courseTableView.setTotalJC(15,5,5,list);
            jiechiM=0;
            jiechiA=5;
            jiechiE=10;
            list.clear();
        }else if(itemRows(0)==6||itemRows(1)==6||itemRows(2)==6) {
            courseTableView.setTotalJC(18,6,6,list);
            jiechiM=0;
            jiechiA=6;
            jiechiE=12;
            list.clear();
        }else {
            courseTableView.setTotalJC(12,4,4,list);
            jiechiM=0;
            jiechiA=4;
            jiechiE=8;
            list.clear();
        }
        List<ScheduleBean> scheduleBeen = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list();
        if(scheduleBeen.size()==0) {

        }else {
            if(!map.isEmpty()) {
                map.clear();
            }
            for (int i=0;i<scheduleBeen.size();i++){
                if(deviceid.equals(scheduleBeen.get(i).getImei())) {
                    ScheduleBean scheduleBean = scheduleBeen.get(i);
                    String json = scheduleBean.getJson();
                    LogUtil.e(json);
                    ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(json, ScheduleResultsEntity.class);
                    List<ScheduleListEntity> schedule = scheduleResultsEntity.getResults().getSchedule();

                    for (int t=0;t<schedule.size();t++){
                        ScheduleListEntity scheduleListEntity = schedule.get(t);

                        try {
                            String daily = scheduleListEntity.getDaily();
                            String week = changeDaily(daily);
                            String details = scheduleListEntity.getDetails();
                            String id = scheduleListEntity.getId();
                            String status = scheduleListEntity.getStatus();
                            String[] split = scheduleListEntity.getTime().split(":");
                            String hour=split[0];
                            String minute=split[1];
                            String detail = URLDecoder.decode(details,"utf-8");
                            map.put(hour+detail+minute,id);
                            showRestTable(status,week,hour,minute,detail);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }else {
                    if(i==scheduleBeen.size()-1) {

                    }
                }

            }
        }

    }


    //请求网络
    private void getReq() {
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(val!=null&&val.length()>0) {
                    try {
                        //{"Results":{"Category":"Schedule","IMEI":"240207489224233264","Num":1,"Schedule":[{"Daily":"Wednesday|Thursday|Friday","Details":"åƒé¥­","Id":10,"Time":"14:47:26"}]}}
                        JSONObject jsonObject = new JSONObject(val);
                        if(jsonObject.has(ConstantClassFunction.ERR_MES)&&jsonObject.getString(ConstantClassFunction.ERR_MES).equals("结果集为空")) {
                            Toast.makeText(WorkRestPlanActivity.this, "还没有作息计划，请添加", Toast.LENGTH_SHORT).show();
                        }else if(jsonObject.has(ConstantClassFunction.getRESULTS())&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).has(ConstantClassFunction.getCATEGORY())&&jsonObject.getJSONObject(ConstantClassFunction.getRESULTS()).getString(ConstantClassFunction.getCATEGORY()).equals(ConstantClassFunction.SCHEDULE)) {
                            //保存数据库
                            ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(val, ScheduleResultsEntity.class);
                            String imei = scheduleResultsEntity.getResults().getIMEI();


                            ScheduleBean scheduleBean = new ScheduleBean();
                            scheduleBean.setJson(val);
                            scheduleBean.setImei(imei);
                            List<ScheduleBean> scheduleBeenList = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list();
                            if(scheduleBeenList.size()==0) {    //数据库不存在数据插入
                                BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().insert(scheduleBean);
                            }else {
                                for (int i=0;i<scheduleBeenList.size();i++) {   //存在该deviceid时更新
                                    if (deviceid.equals(scheduleBeenList.get(i).getImei())) {
                                        ScheduleBean schdeuleItem = scheduleBeenList.get(i);
                                        schdeuleItem.setJson(val);
                                        schdeuleItem.setImei(imei);
                                        BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().update(schdeuleItem);
                                        break;
                                    }else {
                                        if(i==scheduleBeenList.size()-1) {  //不存在该deviceid时插入
                                            BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().insert(scheduleBean);
                                        }
                                    }
                                }
                            }

                            num = scheduleResultsEntity.getResults().getNum();
                            if(itemReqRows(val,0)==5||itemReqRows(val,1)==5||itemReqRows(val,2)==5) {
                                courseTableView.setTotalJC(15,5,5,list);
                                jiechiM=0;
                                jiechiA=5;
                                jiechiE=10;
                                list.clear();
                            }else if(itemReqRows(val,0)==6||itemReqRows(val,1)==6||itemReqRows(val,2)==6) {
                                courseTableView.setTotalJC(18,6,6,list);
                                jiechiM=0;
                                jiechiA=6;
                                jiechiE=12;
                                list.clear();
                            }else {
                                courseTableView.setTotalJC(12,4,4,list);
                                jiechiM=0;
                                jiechiA=4;
                                jiechiE=8;
                                list.clear();
                            }




                            if(!map.isEmpty()) {
                                map.clear();
                            }
                            List<ScheduleListEntity> schedule = scheduleResultsEntity.getResults().getSchedule();
                            for (int i=0;i<schedule.size();i++){
                                ScheduleListEntity scheduleListEntity = schedule.get(i);
                                try {
                                    String daily = scheduleListEntity.getDaily();
                                    String week = changeDaily(daily);
                                    String details = scheduleListEntity.getDetails();
                                    String id = scheduleListEntity.getId();
                                    String status = scheduleListEntity.getStatus();
                                    String[] split = scheduleListEntity.getTime().split(":");
                                    String hour=split[0];
                                    String minute=split[1];
                                    String detail = URLDecoder.decode(details,"utf-8");
                                    map.put(hour+detail+minute,id);
                                    showRestTable(status,week,hour,minute,detail);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, HttpUrls.getScheduleData()+"&imei="+deviceid,null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(this)));


    }




    private int itemReqRows(String val,int type) {
        ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(val, ScheduleResultsEntity.class);
        List<ScheduleListEntity> schedule = scheduleResultsEntity.getResults().getSchedule();
        ArrayList<Integer> morning = new ArrayList<>();
        ArrayList<Integer> afternoon = new ArrayList<>();
        ArrayList<Integer> evening = new ArrayList<>();
        for (int i=0;i<schedule.size();i++){


            String time = schedule.get(i).getTime().substring(0, 2);
            if(Integer.valueOf(time)<12) {
                morning.add(Integer.valueOf(time));
            }

            if (Integer.valueOf(time) >= 12 && Integer.valueOf(time) < 18) {
                afternoon.add(Integer.valueOf(time));
            }

            if (Integer.valueOf(time) >= 18 && Integer.valueOf(time) < 24) {
                evening.add(Integer.valueOf(time));
            }

        }
        if(type==0) {
            return morning.size();
        }else if(type==1) {
            return afternoon.size();
        }else if(type==2) {
            return evening.size();
        }

        return 0;
    }

    private void showRestTable(String status, String week, String hour, String minute, final String content){

        if (week != null && hour != null && minute != null && content != null) {

            if(Integer.valueOf(hour)<12) {
                jiechiM++;
                jiechi=jiechiM;
            }else if(Integer.valueOf(hour)>=12&&Integer.valueOf(hour)<18) {
                jiechiA++;
                jiechi=jiechiA;
            }else {
                jiechiE++;
                jiechi=jiechiE;
            }
            String[] arr_week = week.split(",");
            int[] arr = new int[arr_week.length];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arr_week.length; i++) {
                arr[i] = Integer.parseInt(arr_week[i]);
                sb.append(arr[i]).append("-");
            }
            String change = change(arr);

            String[] arr2 = change.split(",");//把不连续的切出来
            for (int k = 0; k < arr2.length; k++) {
                Course course = new Course();


                String[] arr3 = arr2[k].split("-");
                if (arr3.length > 1) {
                    course.setSpanNum(Integer.parseInt(arr3[1]) - Integer.parseInt(arr3[0]) + 1);
                } else {
                    course.setSpanNum(1);
                }
                course.setDay(Integer.parseInt(arr3[0]));
                course.setJieci(jiechi);
                course.setDes(content);
                course.setTime(hour + ":" + minute);
                course.setWeekStr(sb.toString());
                if("0".equals(status)) {
                    course.setClose(true);
                }else {
                    course.setClose(false);
                }

                list.add(course);
            }


            courseTableView.updateCourseViews(list);
            courseTableView.setOnCourseItemClickListener(new CourseTableView.OnCourseItemClickListener() {
                @Override
                public void onCourseItemClick(boolean isClose,String time, int jieci, String week, String des) {
                    String[] split = time.split(":");
                    Intent intent = new Intent(WorkRestPlanActivity.this, WorkRestEditActivity.class);
                    intent.putExtra(WorkRestEditActivity.CONTENT,des);
                    intent.putExtra(WorkRestEditActivity.HOUR,split[0]);
                    intent.putExtra(WorkRestEditActivity.MINUTE,split[1]);
                    intent.putExtra(WorkRestEditActivity.WEEK,week);
                    intent.putExtra(WorkRestEditActivity.TYPE,"2");
                    intent.putExtra("device_id",deviceid);
                    intent.putExtra("item",getRightId(split[0]+des+split[1]));
                    intent.putExtra("isClose",isClose);
                    //Toast.makeText(WorkRestPlanActivity.this, t+"个", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent,EDIT_PLAN);
                }
            });

        }

    }


    //返回对应时段的item数目
    public int itemRows(int a) {
        List<ScheduleBean> list = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list();
        if(list.size()>0){
            for (int t = 0; t < list.size(); t++) {
                if (deviceid.equals(list.get(t).getImei())) {
                    ScheduleBean scheduleBean = list.get(t);
                    String json = scheduleBean.getJson();
                    ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(json, ScheduleResultsEntity.class);
                    List<ScheduleListEntity> schedule = scheduleResultsEntity.getResults().getSchedule();


                        ArrayList<Integer> morning = new ArrayList<>();
                        ArrayList<Integer> afternoon = new ArrayList<>();
                        ArrayList<Integer> evening = new ArrayList<>();

                        for (int i = 0; i < schedule.size(); i++) {
                            ScheduleListEntity scheduleListEntity = schedule.get(i);
                            String s = scheduleListEntity.getTime().substring(0,2);
                            if (Integer.valueOf(s) < 12) {
                                morning.add(Integer.valueOf(s));
                            }
                            if (Integer.valueOf(s) >= 12 && Integer.valueOf(s) < 18) {
                                afternoon.add(Integer.valueOf(s));
                            }

                            if (Integer.valueOf(s) >= 18 && Integer.valueOf(s) < 24) {
                                evening.add(Integer.valueOf(s));
                            }


                        }

                    if(a==0) {
                        return morning.size();
                    }else if(a==1) {
                        return afternoon.size();
                    }else if(a==2) {
                        return evening.size();
                    }

                }

            }
            return 0;
        }
       return 0;
    }



    private String getRightId(String des){
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = iterator.next();
            if(entry.getKey().equals(des)) {
                return entry.getValue();
            }

        }
        return "";

    }


    private void initTitle() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("作息计划");

        ibtn_menu = (ImageButton) findViewById(R.id.ibtn_menu);
        ibtn_menu.setImageDrawable(getResources().getDrawable(R.drawable.icon_add));
        ibtn_menu.setVisibility(View.VISIBLE);
        ibtn_menu.setOnClickListener(this);


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
            case R.id.ibtn_menu:
                Intent intent = new Intent(this, WorkRestEditActivity.class);
                intent.putExtra(WorkRestEditActivity.TYPE,"1");
                intent.putExtra("device_id",deviceid);
                intent.putExtra("num",num);
                startActivityForResult(intent,ADD_PLAN);
                break;
        }

    }

    // 连续的数字就用-连起来
    private String change(int[] j) {
        StringBuffer sb = new StringBuffer();

        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < j.length; i++) {
            list.add(j[i]);
        }
        int index = 0;
        int end;
        for (int x = 0; x < j.length; x++) {
            int c = j[x] + 1;
            if (list.contains(c)) {
                continue;
            } else {
                end = x;

                if (index == x) {
                    sb.append(j[index] + ",");
                    index++;
                    continue;
                }
                sb.append(j[index] + "-" + j[end] + ",");
                index = end + 1;
                continue;
            }
        }
        return sb.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ADD_PLAN:
                if(resultCode== WorkRestEditActivity.ADD_RESULT_OK) {
                    type = data.getStringExtra("type");
                    initData();
                }
                break;
            case EDIT_PLAN:
                if(resultCode== WorkRestEditActivity.EDIT_RESULT_OK) {
                    type = data.getStringExtra("type");
                    initData();
                }else if(resultCode== WorkRestEditActivity.DELETE_RESULT_OK) {
                    type = data.getStringExtra("type");
                    //initData();

                    list.clear();
                    courseTableView.updateCourseViews(list);
                    getReq();

                }
        }
    }
}
