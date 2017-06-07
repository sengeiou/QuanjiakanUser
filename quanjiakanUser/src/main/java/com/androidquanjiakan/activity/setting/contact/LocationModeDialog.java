package com.androidquanjiakan.activity.setting.contact;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quanjiakan.main.R;
import com.quanjiakanuser.widget.WheelView;

/**
 * 作者：Administrator on 2017/4/12 14:55
 * <p>
 * 邮箱：liuzj@hi-board.com
 */

public class LocationModeDialog extends Dialog implements View.OnClickListener {


    private Context context;
    private WheelView wv_time;
    private TextView btnSure;
    private TextView btnCancel;
    private TextView tv_mode1;
    private TextView tv_mode2;
    private TextView tv_mode3;
    private TextView tv_mode4;
    private RelativeLayout rlt_mode1;
    private RelativeLayout rlt_mode2;
    private RelativeLayout rlt_mode3;
    private RelativeLayout rlt_mode4;
    private String text = "";


    public LocationModeDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_location_mode);

        initView();
    }

    private void initView() {

        btnSure = (TextView) findViewById(R.id.btn_sure);
        btnCancel = (TextView) findViewById(R.id.btn_cancel);


        rlt_mode1 = (RelativeLayout) findViewById(R.id.rlt_mode1);
        rlt_mode2 = (RelativeLayout) findViewById(R.id.rlt_mode2);
        rlt_mode4 = (RelativeLayout) findViewById(R.id.rlt_mode4);


        tv_mode1 = (TextView) findViewById(R.id.tv_mode1);
        tv_mode2 = (TextView) findViewById(R.id.tv_mode2);
        tv_mode4 = (TextView) findViewById(R.id.tv_mode4);


        rlt_mode1.setOnClickListener(this);
        rlt_mode2.setOnClickListener(this);
        rlt_mode3.setOnClickListener(this);
        rlt_mode4.setOnClickListener(this);

        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:

                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick(text);
                }

                break;
            case R.id.btn_cancel:

                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }

                break;

            case R.id.rlt_mode1:
                text = tv_mode1.getText().toString();
                tv_mode1.setSelected(true);
                tv_mode2.setSelected(false);
                tv_mode3.setSelected(false);
                tv_mode4.setSelected(false);
                break;

            case R.id.rlt_mode2:
                text = tv_mode2.getText().toString();
                tv_mode1.setSelected(false);
                tv_mode2.setSelected(true);
                tv_mode3.setSelected(false);
                tv_mode4.setSelected(false);
                break;

            case R.id.rlt_mode3:
                text = tv_mode3.getText().toString();
                tv_mode1.setSelected(false);
                tv_mode2.setSelected(false);
                tv_mode3.setSelected(true);
                tv_mode4.setSelected(false);
                break;

            case R.id.rlt_mode4:
                text = tv_mode4.getText().toString();
                tv_mode1.setSelected(false);
                tv_mode2.setSelected(false);
                tv_mode3.setSelected(false);
                tv_mode4.setSelected(true);
                break;
        }
    }


    private BrightScreenTimeDialog.onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private BrightScreenTimeDialog.onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(BrightScreenTimeDialog.onNoOnclickListener onNoOnclickListener) {

        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(BrightScreenTimeDialog.onYesOnclickListener onYesOnclickListener) {
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
