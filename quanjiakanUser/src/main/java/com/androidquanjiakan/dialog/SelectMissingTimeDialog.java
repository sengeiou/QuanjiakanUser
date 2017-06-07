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
import java.util.List;

/**
 * Created by Gin on 2016/11/17.
 */


public class SelectMissingTimeDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private WheelView wv_year;
    private WheelView wv_month;
    private WheelView wv_day;
    private WheelView wv_hour;
    private WheelView wv_minute;
    private Calendar calendar;
    private List<String> yearList;
    private List<String> monthList;
    private List<String> dayList;
    private List<String> hourList;
    private List<String> minuteList;
    private String strYear=String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    private String strMonth=String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1);
    private String strDay=String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    private String strHour=String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
    private String strMinute=String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
    private int maxsize = 16;
    private int minsize = 12;
    private SelectVolunteerTimeAdapter yearAdapter;

    private SelectVolunteerTimeAdapter monthAdapter;
    private SelectVolunteerTimeAdapter dayAdapter;
    private SelectVolunteerTimeAdapter hourAdapter;
    private SelectVolunteerTimeAdapter minuteAdapter;
    private TextView btnSure;
    private TextView btnCancel;
    private OnTimeChangeCListener onTimeChangeCListener;

    public SelectMissingTimeDialog(Context context) {
        super(context , R.style.ShareDialog);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_volunteer_time);
        wv_year = (WheelView)findViewById(R.id.wv_year);
        wv_month = (WheelView)findViewById(R.id.wv_month);
        wv_day = (WheelView)findViewById(R.id.wv_day);
        wv_hour = (WheelView) findViewById(R.id.wv_hour);
        wv_minute = (WheelView) findViewById(R.id.wv_minute);
        calendar=Calendar.getInstance();

        btnSure = (TextView) findViewById(R.id.btn_myinfo_sure);
        btnSure.setOnClickListener(this);
        btnCancel = (TextView) findViewById(R.id.btn_myinfo_cancel);
        btnCancel.setOnClickListener(this);


        //初始化年
        initYear();
        yearAdapter=new SelectVolunteerTimeAdapter(context,yearList,getYearItem(strYear),maxsize,minsize);
        wv_year.setVisibleItems(3);
        wv_year.setViewAdapter(yearAdapter);

        //初始化月
        initMonth();
        monthAdapter = new SelectVolunteerTimeAdapter(context, monthList, getMonthItem(strMonth), maxsize, minsize);
        wv_month.setVisibleItems(3);
        wv_month.setViewAdapter(monthAdapter);

        //初始化天
        initDay();
        dayAdapter = new SelectVolunteerTimeAdapter(context, dayList, getDayItem(strDay), maxsize, minsize);
        wv_day.setVisibleItems(3);
        wv_day.setViewAdapter(dayAdapter);

        //初始化小时
        initHour();
        hourAdapter = new SelectVolunteerTimeAdapter(context, hourList, getHourItem(strHour), maxsize, minsize);
        wv_hour.setVisibleItems(3);
        wv_hour.setViewAdapter(hourAdapter);

        //初始化分钟
        initMinute();
        minuteAdapter = new SelectVolunteerTimeAdapter(context, minuteList, getMinuteItem(strMinute), maxsize, minsize);
        wv_minute.setVisibleItems(3);
        wv_minute.setViewAdapter(minuteAdapter);


        wv_year.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) yearAdapter.getItemText(wheel.getCurrentItem());
                strYear=currentText;
                setTextviewSize(currentText,yearAdapter);

                monthList.clear();
                for (int i=1;i<13;i++){
                    if(i<10) {
                        monthList.add("0"+i);
                    }else {
                        monthList.add(""+i);
                    }
                }
                monthAdapter=new SelectVolunteerTimeAdapter(context,monthList,0,maxsize,minsize);
                wv_month.setVisibleItems(3);
                wv_month.setViewAdapter(monthAdapter);

                dayList.clear();
                for (int i=1;i<32;i++){
                    if(i<10) {
                        dayList.add("0"+i);
                    }else {
                        dayList.add(""+i);
                    }

                }
                dayAdapter=new SelectVolunteerTimeAdapter(context,dayList,0,maxsize,minsize);
                wv_day.setVisibleItems(3);
                wv_day.setViewAdapter(dayAdapter);

                hourList.clear();
                for (int i=0;i<24;i++){
                    if(i<10) {
                        hourList.add("0"+i);
                    }else {
                        hourList.add(""+i);
                    }
                }
                hourAdapter=new SelectVolunteerTimeAdapter(context,hourList,0,maxsize,minsize);
                wv_hour.setVisibleItems(3);
                wv_hour.setViewAdapter(hourAdapter);

                minuteList.clear();
                for (int i=0;i<60;i++){
                    if(i<10) {
                        minuteList.add("0"+i);
                    }else {
                        minuteList.add(""+i);
                    }
                }
                minuteAdapter=new SelectVolunteerTimeAdapter(context,minuteList,0,maxsize,minsize);
                wv_minute.setVisibleItems(3);
                wv_minute.setViewAdapter(minuteAdapter);

            }
        });
        wv_year.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) yearAdapter.getItemText(wheel.getCurrentItem());
                strYear=currentText;
                strMonth="01";
                strDay="01";
                strHour="00";
                strMinute="00";

                setTextviewSize(strYear,yearAdapter);
            }
        });

        wv_month.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) monthAdapter.getItemText(wheel.getCurrentItem());
                strMonth=currentText;
                setTextviewSize(strMonth,monthAdapter);

                dayList.clear();
                if(currentText.startsWith("0")) {
                    currentText.substring(1,currentText.length());
                }
                int daysByYearMonth = getDaysByYearMonth(Integer.valueOf(strYear), Integer.valueOf(currentText));
                for (int i=1;i<=daysByYearMonth;i++){
                    if(i<10) {
                        dayList.add("0"+i);
                    }else {
                        dayList.add(""+i);
                    }

                }
                dayAdapter=new SelectVolunteerTimeAdapter(context,dayList,0,maxsize,minsize);
                wv_day.setVisibleItems(3);
                wv_day.setViewAdapter(dayAdapter);

                hourList.clear();
                for (int i=0;i<24;i++){
                    if(i<10) {
                        hourList.add("0"+i);
                    }else {
                        hourList.add(""+i);
                    }
                }
                hourAdapter=new SelectVolunteerTimeAdapter(context,hourList,0,maxsize,minsize);
                wv_hour.setVisibleItems(3);
                wv_hour.setViewAdapter(hourAdapter);

                minuteList.clear();
                for (int i=0;i<60;i++){
                    if(i<10) {
                        minuteList.add("0"+i);
                    }else {
                        minuteList.add(""+i);
                    }
                }
                minuteAdapter=new SelectVolunteerTimeAdapter(context,minuteList,0,maxsize,minsize);
                wv_minute.setVisibleItems(3);
                wv_minute.setViewAdapter(minuteAdapter);

            }
        });
        wv_month.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) monthAdapter.getItemText(wheel.getCurrentItem());
                strMonth=currentText;
                strDay="01";
                strHour="00";
                strMinute="00";
                setTextviewSize(strMonth,monthAdapter);

            }
        });

        wv_day.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) dayAdapter.getItemText(wheel.getCurrentItem());
                strDay=currentText;
                setTextviewSize(strDay,dayAdapter);

                hourList.clear();
                for (int i=0;i<24;i++){
                    if(i<10) {
                        hourList.add("0"+i);
                    }else {
                        hourList.add(""+i);
                    }
                }
                hourAdapter=new SelectVolunteerTimeAdapter(context,hourList,0,maxsize,minsize);
                wv_hour.setVisibleItems(3);
                wv_hour.setViewAdapter(hourAdapter);

                minuteList.clear();
                for (int i=0;i<60;i++){
                    if(i<10) {
                        minuteList.add("0"+i);
                    }else {
                        minuteList.add(""+i);
                    }
                }
                minuteAdapter=new SelectVolunteerTimeAdapter(context,minuteList,0,maxsize,minsize);
                wv_minute.setVisibleItems(3);
                wv_minute.setViewAdapter(minuteAdapter);


            }
        });
        wv_day.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) dayAdapter.getItemText(wheel.getCurrentItem());
                strDay=currentText;
                strHour="00";
                strMinute="00";
                setTextviewSize(strDay,dayAdapter);
            }
        });

        wv_hour.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) hourAdapter.getItemText(wheel.getCurrentItem());
                strHour=currentText;
                setTextviewSize(strHour,hourAdapter);

                minuteList.clear();
                for (int i=0;i<60;i++){
                    if(i<10) {
                        minuteList.add("0"+i);
                    }else {
                        minuteList.add(""+i);
                    }
                }
                minuteAdapter=new SelectVolunteerTimeAdapter(context,minuteList,0,maxsize,minsize);
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
                String currentText = (String) hourAdapter.getItemText(wheel.getCurrentItem());
                strHour=currentText;
                strMinute="00";
                setTextviewSize(strHour,hourAdapter);
            }
        });

        wv_minute.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) minuteAdapter.getItemText(wheel.getCurrentItem());
                strMinute=currentText;
                setTextviewSize(strMinute,minuteAdapter);
            }
        });
        wv_minute.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) minuteAdapter.getItemText(wheel.getCurrentItem());
                strMinute=currentText;
                setTextviewSize(strMinute,minuteAdapter);

            }
        });



    }



    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, SelectVolunteerTimeAdapter adapter) {
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

    @Override
    public void onClick(View view) {
        if (view == btnSure) {
            if (onTimeChangeCListener != null) {
                onTimeChangeCListener.onClick(strYear, strMonth,strDay,strHour,strMinute);
            }
        } else if (view == btnCancel) {

        } else {
            dismiss();
        }
        dismiss();
    }


    //Wheel的适配器
    private class SelectVolunteerTimeAdapter extends AbstractWheelTextAdapter {

        List list;
        protected SelectVolunteerTimeAdapter(Context context,List<String> list, int currentItem, int maxsize,
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
            return list.get(index)+"";
        }
    }

    private void initMinute() {
        int minute = calendar.get(Calendar.MINUTE);
        minuteList=new ArrayList<>();
        for (int i=0;i<60-minute;i++){
            minuteList.add(getMinute(minute+i));
        }

    }

    public int getMinuteItem(String minute){
        int size =minuteList.size();
        int minuteIndex = 0;
        boolean nodate=true;
        for (int i = 0; i < size; i++) {
            if(Integer.valueOf(minute)<10) {
                minute="0"+minute;
            }
            if (minute.equals(minuteList.get(i))) {
                nodate = false;
                return minuteIndex;
            } else {
                minuteIndex++;
            }
        }
        return minuteIndex;
    }






    private void initHour() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        hourList=new ArrayList<>();
        for (int i=0;i<24-hour;i++){
            hourList.add(getHour(hour+i));
        }
    }

    public int getHourItem(String hour){
        int size =hourList.size();
        int hourIndex = 0;
        boolean nodate=true;
        for (int i = 0; i < size; i++) {
            if(Integer.valueOf(hour)<10) {
                hour="0"+hour;
            }
            if (hour.equals(hourList.get(i))) {
                nodate = false;
                return hourIndex;
            } else {
                hourIndex++;
            }
        }
        return hourIndex;
    }




    private void initDay() {
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int length=Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)-day+1;
        dayList=new ArrayList<>();
        for (int i=0;i<length;i++){
            dayList.add(getDay(day+i));
        }

    }

    public int getDayItem(String day){
        int size =dayList.size();
        int dayIndex = 0;
        boolean nodate=true;
        for (int i = 0; i < size; i++) {
            if(Integer.valueOf(day)<10) {
                day="0"+day;
            }
            if (day.equals(dayList.get(i))) {
                nodate = false;
                return dayIndex;
            } else {
                dayIndex++;
            }
        }
        return dayIndex;
    }

    private void initMonth() {
        int month = calendar.get(Calendar.MONTH) + 1;

        monthList = new ArrayList<>();
        for (int i=0;i<=12-month;i++){
            monthList.add(getMonth(month+i));
        }

    }

    public int getMonthItem(String month){
        int size =monthList.size();
        int monthIndex = 0;
        boolean nodate=true;
        for (int i = 0; i < size; i++) {
            if(Integer.valueOf(month)<10) {
                month="0"+month;
            }
            if (month.equals(monthList.get(i))) {
                nodate = false;
                return monthIndex;
            } else {
                monthIndex++;
            }
        }
        return monthIndex;
    }


    private void initYear() {
        int year = 1970;
        yearList = new ArrayList<>();
        for (int i=0;i<1000;i++){
            yearList.add(getYear(year+i));
        }

    }

    public int getYearItem(String year){
        int size =yearList.size();
        int yearIndex = 0;
        boolean nodate=true;
        for (int i = 0; i < size; i++) {
            if (year.equals(yearList.get(i))) {
                nodate = false;
                return yearIndex;
            } else {
                yearIndex++;
            }
        }
        return yearIndex;
    }



    private int year ;
    private int month;
    private int day;
    private int week;
    private int hour;
    private int minute;

   /* //返回正确的日期参数
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
    }*/

    /*
    * 一系列初始化
    *
    */
    public String getYear(int year){
        this.year=year;
        return String.valueOf(year);
    }

    public String getMonth(int month){
        this.month=month;
        if(month<10) {
            return "0"+month;
        }else {
            return String.valueOf(month);
        }

    }

    public String getDay(int day){
        this.day=day;
        if(day<10) {
            return "0"+day;
        }else {
            return String.valueOf(day);
        }

    }

    public String getHour(int hour){
        this.hour=hour;
        if(hour<10) {
            return "0"+hour;
        }else {
            return String.valueOf(hour);
        }

    }

    private String getMinute(int minute) {
        this.minute=minute;
        if(minute<10) {
            return "0"+minute;
        }else {
            return String.valueOf(minute);
        }

    }

    /**
     * 根据年 月 获取对应的月份 天数
     * */
    public static int getDaysByYearMonth(int year, int month) {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    //回调接口
    public interface OnTimeChangeCListener {
        public void onClick(String year, String month, String day,String hour,String minute);
    }

    //设置回调
    public void setOnTimeChangeCListener(OnTimeChangeCListener onTimeChangeCListener) {
        this.onTimeChangeCListener = onTimeChangeCListener;
    }

}

