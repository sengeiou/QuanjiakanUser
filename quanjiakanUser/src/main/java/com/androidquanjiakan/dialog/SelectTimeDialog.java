package com.androidquanjiakan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quanjiakan.main.R;
import com.quanjiakanuser.widget.AbstractWheelTextAdapter;
import com.quanjiakanuser.widget.OnWheelChangedListener;
import com.quanjiakanuser.widget.OnWheelScrollListener;
import com.quanjiakanuser.widget.WheelView;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by Gin on 2016/10/13.
 */

public class SelectTimeDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private WheelView wv_date;
    private WheelView wv_hour;
    private WheelView wv_minute;
    private Calendar calendar;
    private ArrayList<String> dateList ;
    private ArrayList<String> hourList ;
    private ArrayList<String> minuteList;

    private int maxsize = 16;
    private int minsize = 12;
    private SelectTimeAdapter dateAdapter;

    private OnTimeChangeCListener onTimeChangeCListener;


    private String strDate="今天";
    private String strHour="现在";
    private String strMinute="现在";
    private SelectTimeAdapter hourAdapter;
    private SelectTimeAdapter minuteAdapter;
    private TextView btnSure;
    private TextView btnCancel;

    public SelectTimeDialog(Context context) {
        super(context , R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_selecttime);
        wv_date = (WheelView)findViewById(R.id.wv_date);
        wv_hour = (WheelView)findViewById(R.id.wv_hour);
        wv_minute = (WheelView)findViewById(R.id.wv_minute);

        btnSure = (TextView) findViewById(R.id.btn_myinfo_sure);
        btnSure.setOnClickListener(this);
        btnCancel = (TextView) findViewById(R.id.btn_myinfo_cancel);
        btnCancel.setOnClickListener(this);
        calendar=Calendar.getInstance();

       /* lyChangeAddressChild = findViewById(R.id.ly_myinfo_changeaddress_child);
        lyChangeAddressChild.setOnClickListener(this);*/
        //初始化日期
        initDate();
        dateAdapter = new SelectTimeAdapter(context, dateList, getDateItem(strDate), maxsize, minsize);
        wv_date.setVisibleItems(3);
        wv_date.setViewAdapter(dateAdapter);
        //wv_date.setCurrentItem(getDateItem(strDate));

        //初始化小时
        initClock();
        hourAdapter = new SelectTimeAdapter(context, hourList, getHourItem(strHour), maxsize, minsize);
        wv_hour.setVisibleItems(3);
        wv_hour.setViewAdapter(hourAdapter);
        //wv_hour.setCurrentItem(getHourItem("01"));

        //初始化分钟
        initMinute();
        minuteAdapter = new SelectTimeAdapter(context, minuteList, getMinuteItem(strMinute), maxsize, minsize);
        wv_minute.setVisibleItems(3);
        wv_minute.setViewAdapter(minuteAdapter);


        wv_date.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String)dateAdapter.getItemText(wheel.getCurrentItem());
                strDate =currentText;
                setTextviewSize(currentText, dateAdapter);


                hourList.clear();
                for (int i=0;i<24;i++){
                    hourList.add(getHour1(i));
                }
                hourAdapter=new SelectTimeAdapter(context,hourList,0,maxsize,minsize);
                wv_hour.setVisibleItems(3);
                wv_hour.setViewAdapter(hourAdapter);


                minuteList.clear();
                for (int i=0;i<60;i++){
                    minuteList.add(getMinute1(i));
                }
                minuteAdapter=new SelectTimeAdapter(context,minuteList,0,maxsize,minsize);
                wv_minute.setVisibleItems(3);
                wv_minute.setViewAdapter(minuteAdapter);

            }
        });

        wv_date.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String)dateAdapter.getItemText(wheel.getCurrentItem());
                strDate =currentText;
                strHour="00点";
                strMinute="00分";
                setTextviewSize(currentText, dateAdapter);

            }
        });


        wv_hour.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) hourAdapter.getItemText(wheel.getCurrentItem());
                strHour=currentText;
                setTextviewSize(currentText,hourAdapter);



                minuteList.clear();
                for (int i=0;i<60;i++){
                    minuteList.add(getMinute1(i));
                }
                minuteAdapter=new SelectTimeAdapter(context,minuteList,0,maxsize,minsize);
                wv_minute.setVisibleItems(3);
                wv_minute.setViewAdapter(minuteAdapter);

            }
        });

        wv_hour.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {


            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                strMinute="00分";
                String currentText = (String) hourAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText,hourAdapter);
            }
        });


        wv_minute.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) minuteAdapter.getItemText(wheel.getCurrentItem());
                strMinute=currentText;
                setTextviewSize(currentText,minuteAdapter);
            }
        });

        wv_minute.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) minuteAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText,minuteAdapter);

            }
        });


    }


    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, SelectTimeAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(16);
            } else {
                textvew.setTextSize(12);
            }
        }
    }

    private void initDate() {

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        dateList = new ArrayList<>();
        int length=Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)-day+1;
        for (int i=0;i<length;i++){
            //dateObject = new DateObject(year, month, day+i, week+i);
            dateList.add(getDate(year,month,day+i,week+i));
        }

    }


    private void initClock() {
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        hourList=new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            if(hour+i>23) {
                return;
            }
            //dateObject = new DateObject(hour+i,-1,true);
            hourList.add(getHour(hour+i));

        }
    }

    private void initMinute() {
        //int hour=calendar.get(Calendar.M);
        int minute = calendar.get(Calendar.MINUTE);
        minuteList=new ArrayList<>();
        for (int i=0;i<60;i++){
            if(minute+i>59) {
                return;
            }
            minuteList.add(getMinute(minute+i));

        }

    }


    public int getDateItem(String date){
        int size =dateList.size();
        int dateIndex = 0;
        boolean nodate=true;
        for (int i = 0; i < size; i++) {
            if (date.equals(dateList.get(i))) {
                nodate = false;
                return dateIndex;
            } else {
                dateIndex++;
            }
        }
        return dateIndex;
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

    //取消和确定的点击事件
    @Override
    public void onClick(View v) {
        if (v == btnSure) {
            if (onTimeChangeCListener != null) {
                onTimeChangeCListener.onClick(strDate, strHour,strMinute);
            }
        } else if (v == btnCancel) {

        } else {
            dismiss();
        }
        dismiss();

    }

    public void setOnTimeChangeCListener(OnTimeChangeCListener onTimeChangeCListener) {
        this.onTimeChangeCListener = onTimeChangeCListener;
    }


    //回调接口
    public interface OnTimeChangeCListener {
        public void onClick(String date, String clock, String minute);
    }

    private class SelectTimeAdapter extends AbstractWheelTextAdapter {

        ArrayList list;
        protected SelectTimeAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize,
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

    private int year ;
    private int month;
    private int day;
    private int week;
    private int hour;
    private int minute;
    private String listItem;

    //返回正确的日期参数
    public String getDate(int year2, int month2, int day2, int week2){
        this.year = year2;
        int maxDayOfMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        if(day2 > maxDayOfMonth){
            this.month = month2 + 1;
            this.day = day2 % maxDayOfMonth;
        }else{
            this.month = month2;
            this.day = day2;
        }
        this.week = week2 % 7 == 0 ? 7 : week2 % 7;
        if(day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
            return "今天";
        }else{
            return String.format("%02d", month) +"月" + String.format("%02d", day)  +
                    "日";
        }
    }

    //初始化返回正确的小时参数
    public String getHour(int hour2){
        if(hour2!=-1) {
            if(hour2 > 24){
                this.hour = hour2 % 24;
            }else
                this.hour = hour2;
        }
        if(hour == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            return "现在";
        }else {
            if(hour<10) {
                return "0"+this.hour+"点";
            }
            return this.hour+"点";
        }

    }

    //返回正确的小时参数
    public String getHour1(int hour2){
        if(hour2!=-1) {
            if(hour2 > 24){
                this.hour = hour2 % 24;
            }else
                this.hour = hour2;
        }
        if(hour<10) {
            return "0"+this.hour+"点";
        }
        return this.hour+"点";

    }
    
    
    //初始化返回正确的分钟参数
    public String getMinute(int minute2 ){

        if(minute2>60) {
            this.minute=minute2%60;
        }else {
            this.minute=minute2;
        }
        if(minute==Calendar.getInstance().get(Calendar.MINUTE)) {
            return "现在";
        }else {
            if(minute<10) {
                return "0"+this.minute+"分";
            }
            return this.minute+"分";

        }

    }

    //返回正确的分钟参数
    public String getMinute1(int minute2){
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




}
