package com.androidquanjiakan.activity.setting.contact;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quanjiakan.main.R;
import com.quanjiakanuser.widget.AbstractWheelTextAdapter2;
import com.quanjiakanuser.widget.OnWheelChangedListener;
import com.quanjiakanuser.widget.OnWheelScrollListener;
import com.quanjiakanuser.widget.WheelView;

import java.util.ArrayList;

/**
 * 作者：Administrator on 2017/2/21 14:15
 * <p>
 * 邮箱：liuzj@hi-board.com
 */
public class BrightScreenTimeDialog  extends Dialog implements View.OnClickListener {


    private Context context;
    private WheelView wv_time;
    private TextView btnSure;
    private TextView btnCancel;

    private int total=60;
    private String time;
    private int currentTimeIndex = 1;
    private int maxsize = 20;
    private int minsize = 17;

    private ArrayList<String> arry_time = new ArrayList<String>();
    private TimeTextAdapter timeTextAdapter;

    public BrightScreenTimeDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_screen_time);

        initView();
    }

    private void initView() {

        wv_time = (WheelView) findViewById(R.id.wv_time);
        wv_time.setCyclic(true);



        btnSure = (TextView) findViewById(R.id.btn_sure);
        btnCancel = (TextView) findViewById(R.id.btn_cancel);

        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        
        initTime();
        timeTextAdapter = new TimeTextAdapter(context, arry_time, setTime(currentTimeIndex), maxsize, minsize);
        wv_time.setVisibleItems(5);
        wv_time.setViewAdapter(timeTextAdapter);
        wv_time.setCurrentItem(setTime(currentTimeIndex));
        time = "5";


        wv_time.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) timeTextAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText.replace("秒",""), timeTextAdapter);

                time = currentText.replace("秒","");
                setTime(Integer.parseInt(currentText.replace("秒",""))/5);

            }
        });

        wv_time.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) timeTextAdapter.getItemText(wheel.getCurrentItem());
                time = currentText.replace("秒","");
                setData(Integer.parseInt(time));
                timeTextAdapter.notifyDataChangedEvent();
            }
        });


    }

    private int setTime(int currentIndex) {

        int timeIndex = 0;

        for (int i = 1; i < total/5; i++) {
            if (currentIndex == i) {
                return timeIndex;
            } else {
                timeIndex++;
            }
        }
        return timeIndex;

    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, TimeTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if ((curriteItemText+"秒").equals(currentText)) {
                textvew.setTextSize(minsize);
                textvew.setTextColor(context.getResources().getColor(R.color.color_xun_ing));
            } else {
                textvew.setTextSize(maxsize);
                textvew.setTextColor(context.getResources().getColor(R.color.font_color_999999));
            }
        }
    }

    public void setData(int index) {

        arry_time.clear();
        for (int i=5;i<=total;i=i+5){
            if (i==index){
                arry_time.add(i+"秒");
            }else {
                arry_time.add(i+"");
            }

        }

    }

    private class TimeTextAdapter extends AbstractWheelTextAdapter2 {
        ArrayList<String> list;

        protected TimeTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize,
                                      int minsize) {
            super(context, R.layout.item_screen_time, NO_RESOURCE, currentItem, maxsize, minsize);
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
//初始化时间数据
    private void initTime() {
        arry_time.clear();
        for (int i=5;i<=total;i=i+5){
            if (i==5){
                arry_time.add(i+"秒");
            }else {
                arry_time.add(i+"");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:

                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick(time);
                }

                break;
            case R.id.btn_cancel:

                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }

                break;
        }
    }




















    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    /**
     * 设置取消按钮的显示内容和监听
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(onNoOnclickListener onNoOnclickListener) {

        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(onYesOnclickListener onYesOnclickListener) {
        this.yesOnclickListener = onYesOnclickListener;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick(String time);
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }
}
