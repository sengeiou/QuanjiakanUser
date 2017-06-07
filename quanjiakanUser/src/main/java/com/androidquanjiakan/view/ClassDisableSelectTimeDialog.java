package com.androidquanjiakan.view;

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
 * Created by Gin on 2017/2/22.
 */

public class ClassDisableSelectTimeDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private boolean isMorning;
    private WheelView wv_hour;
    private WheelView wv_minute;
    private TextView btn_sure;
    private TextView btn_cancel;
    private ArrayList<String> hourList ;
    private ArrayList<String> minuteList;
    private String strHour;
    private String strMinute;
    private Calendar calendar;
    private int maxsize = 24;
    private int minsize = 14;
    private SelectTimeAdapter hourAdapter;
    private SelectTimeAdapter minuteAdapter;

    public ClassDisableSelectTimeDialog(Context context) {
        super(context, R.style.ShareDialog);
    }

    public ClassDisableSelectTimeDialog(Context context, boolean isMorning) {
        super(context,R.style.ShareDialog);
        this.context=context;
        this.isMorning=isMorning;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_disable_select_time);

        wv_hour = (WheelView) findViewById(R.id.wv_hour);
        wv_minute = (WheelView) findViewById(R.id.wv_minute);
        wv_hour.setCyclic(true);
        wv_minute.setCyclic(true);

        btn_sure = (TextView) findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(this);

        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);

        calendar = Calendar.getInstance();
        strHour=getHour(calendar.get(Calendar.HOUR_OF_DAY));
        //strMinute=calendar.get(Calendar.MINUTE)+"";
        strMinute=getMinute(calendar.get(Calendar.MINUTE));

        //初始化时间
        initHour(isMorning);
        hourAdapter = new SelectTimeAdapter(context, hourList, getRightTime(calendar.get(Calendar.HOUR_OF_DAY))==-1?0:getRightTime(calendar.get(Calendar.HOUR_OF_DAY)), maxsize, minsize);
        wv_hour.setViewAdapter(hourAdapter);
        wv_hour.setVisibleItems(5);

        if(getRightTime(calendar.get(Calendar.HOUR_OF_DAY))==-1) {
            wv_hour.setCurrentItem(0);
            strHour=(String) hourAdapter.getItemText(wv_hour.getCurrentItem());
        }else {
            wv_hour.setCurrentItem(getHourItem(strHour));
        }

        //初始化分钟
        initMinute();
        minuteAdapter = new SelectTimeAdapter(context, minuteList, getRightTime(calendar.get(Calendar.HOUR_OF_DAY))==-1?0:calendar.get(Calendar.MINUTE), maxsize, minsize);
        wv_minute.setVisibleItems(5);
        wv_minute.setViewAdapter(minuteAdapter);
        if(getRightTime(calendar.get(Calendar.HOUR_OF_DAY))==-1) {
            wv_minute.setCurrentItem(0);
            strMinute=(String) hourAdapter.getItemText(wv_minute.getCurrentItem());
        }else {
            wv_minute.setCurrentItem(getMinuteItem(strMinute));
        }


        wv_hour.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) hourAdapter.getItemText(wheel.getCurrentItem());
                strHour=currentText;
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


    }

    private OnDisableTimeListener onDisableTimeListener;
    //回调接口
    public interface OnDisableTimeListener {
        public void onClick(String hour, String minute);
    }

    public void setonDisableTimeListener(OnDisableTimeListener onDisableTimeListener){
        this.onDisableTimeListener=onDisableTimeListener;
    }



    //当前时间是上午选择下午,当前时间是下午选择上午的时间,(小时)第一个默认情况
    public int getRightTime(int time){

        if(isMorning) {
            if(time>12) {
                return -1;
            }else {
                return time;
            }
        }else {
            if(time<12) {
                return -1;
            }else {
                return time-12;
            }
        }

    }


    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextViewSizeColor(String curriteItemText,SelectTimeAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(maxsize);
            } else {
                textvew.setTextSize(minsize);
            }
        }
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

    private void initHour(boolean isMorning) {
        hourList=new ArrayList<>();
        //上午时间填充
        if(isMorning) {
            for (int i=0;i<12;i++){
                hourList.add(getHour(i));
            }

        }else {
            //下午时间填充
            for (int i=0;i<12;i++){
                hourList.add(getHour(i+12));
            }

        }
    }

    private int hour;
    private int minute;
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

    @Override
    public void onClick(View view) {
        if (view ==btn_sure ) {
            if (onDisableTimeListener != null) {
                onDisableTimeListener.onClick(strHour,strMinute);
            }
        } else if (view==btn_cancel) {

        } else {
            dismiss();
        }
        dismiss();
        
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
}
