package com.androidquanjiakan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquanjiakan.adapter.NumericWheelAdapter;
import com.quanjiakan.main.R;
import com.quanjiakanuser.widget.AbstractWheelTextAdapter;
import com.quanjiakanuser.widget.AbstractWheelTextAdapter2;
import com.quanjiakanuser.widget.OnWheelChangedListener;
import com.quanjiakanuser.widget.OnWheelScrollListener;
import com.quanjiakanuser.widget.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 作者：Administrator on 2017/2/21 14:15
 * <p>
 * 邮箱：liuzj@hi-board.com
 */
public class Select_Time_Dialog extends Dialog implements View.OnClickListener {


    private Context context;
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private WheelView min;
    private WheelView sec;

    private int mYear=1996;
    private int mMonth=0;
    private int mDay=1;


    private TextView btnSure;
    private TextView btnCancel;
    private OnTimeChangeCListener onTimeChangeCListener;
    private String time;
    private int curDate;

    public Select_Time_Dialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_publish_time);

        Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH);
        curDate = c.get(Calendar.DAY_OF_MONTH);
        int curHour = c.get(Calendar.HOUR_OF_DAY);
        int curMinute = c.get(Calendar.MINUTE);

//        int curYear = mYear;
//        int curMonth =mMonth+1;
//        int curDate = mDay;
        year = (WheelView)findViewById(R.id.wv_year);
        month = (WheelView)findViewById(R.id.wv_month);
        day = (WheelView)findViewById(R.id.wv_day);
        min = (WheelView) findViewById(R.id.wv_hour);
        sec = (WheelView) findViewById(R.id.wv_minute);
        

        btnSure = (TextView) findViewById(R.id.btn_myinfo_sure);
        btnSure.setOnClickListener(this);
        btnCancel = (TextView) findViewById(R.id.btn_myinfo_cancel);
        btnCancel.setOnClickListener(this);

        NumericWheelAdapter numericWheelAdapter1=new NumericWheelAdapter(context,1950, curYear,null,curYear - 1950,18,14);
        numericWheelAdapter1.setLabel("年");
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(true);//是否可循环滑动
        year.addScrollingListener(scrollListener);

        NumericWheelAdapter numericWheelAdapter2=new NumericWheelAdapter(context,1, 12, "%02d",curMonth,18,14);
        numericWheelAdapter2.setLabel("月");
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(true);
        month.addScrollingListener(scrollListener);

        initDay(curYear,curMonth);
        day.setCyclic(true);
        day.addScrollingListener(scrollListener);


        NumericWheelAdapter numericWheelAdapter3=new NumericWheelAdapter(context,0, 23, "%02d",curHour,18,14);
        numericWheelAdapter3.setLabel("时");
        min.setViewAdapter(numericWheelAdapter3);
        min.setCyclic(true);
        min.addScrollingListener(scrollListener);

        NumericWheelAdapter numericWheelAdapter4=new NumericWheelAdapter(context,0, 59, "%02d",curMinute,18,14);
        numericWheelAdapter4.setLabel("分");
        sec.setViewAdapter(numericWheelAdapter4);
        sec.setCyclic(true);
        sec.addScrollingListener(scrollListener);


        year.setVisibleItems(5);//设置显示行数
        month.setVisibleItems(5);
        day.setVisibleItems(5);
        min.setVisibleItems(5);
        sec.setVisibleItems(5);

        year.setCurrentItem(curYear - 1950);
        month.setCurrentItem(curMonth);
        day.setCurrentItem(curDate - 1);
        min.setCurrentItem(curHour);
        sec.setCurrentItem(curMinute);

        time = new StringBuilder().append((year.getCurrentItem()+1950)).
                append("-").append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1))
                .append("-").append(((day.getCurrentItem()+1) < 10) ? "0" + (day.getCurrentItem()+1) : (day.getCurrentItem()+1))
                .append("-").append(((min.getCurrentItem()+1) < 10) ? "0" + (min.getCurrentItem()) : (min.getCurrentItem()))
                .append("-").append(((sec.getCurrentItem()+1) < 10) ? "0" + (sec.getCurrentItem()) : (sec.getCurrentItem())).toString();





    }

    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = year.getCurrentItem() + 1950;//年
            int n_month = month.getCurrentItem() + 1;//月

            initDay(n_year,n_month);

            time = new StringBuilder().append((year.getCurrentItem()+1950)).append("-").
                    append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1)).
                    append("-").append(((day.getCurrentItem()+1) < 10) ? "0" + (day.getCurrentItem()+1) : (day.getCurrentItem()+1)).
                    append("-").append(((min.getCurrentItem()+1) < 10) ? "0" + (min.getCurrentItem()) : (min.getCurrentItem())).
                    append("-").append(((sec.getCurrentItem()+1) < 10) ? "0" + (sec.getCurrentItem()) : (sec.getCurrentItem())).toString();

        }
    };

    /**
     */
    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(context,1, getDay(arg1, arg2), "%02d",curDate - 1,18,14);
        numericWheelAdapter.setLabel("日");
        day.setViewAdapter(numericWheelAdapter);

    }


    /**
     *
     * @param year
     * @param month
     * @return
     */
    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }





    @Override
    public void onClick(View view) {
        if (view == btnSure) {
            if (onTimeChangeCListener != null) {
                if (time!=null&&time.length()>0) {
                    onTimeChangeCListener.onClick(time);
                }

            }
        } else if (view == btnCancel) {

        } else {
            dismiss();
        }
        dismiss();
    }

    //回调接口
    public interface OnTimeChangeCListener {
        public void onClick(String time);
    }

    //设置回调
    public void setOnTimeChangeCListener(OnTimeChangeCListener onTimeChangeCListener) {
        this.onTimeChangeCListener = onTimeChangeCListener;
    }

}