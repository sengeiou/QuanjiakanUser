package com.androidquanjiakan.activity.setting.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;


/**
 * 作者：Administrator on 2017/2/21 11:14
 * <p>
 * 邮箱：liuzj@hi-board.com
 */
public class SoundSettingsActivity extends BaseActivity implements View.OnClickListener {

    private ToggleButton btn_voice;
    private ToggleButton btn_vibration;
    private ToggleButton btn_msg_voice;
    private ToggleButton btn_msg_vibration;
    private String phone_voic;
    private String phone_vibration;
    private String msg_voic;
    private String msg_vibration;

    public static final String WATCHBELL = "watchbell";
    private String a1;
    private String a2;
    private String a3;
    private String a4;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sound_setting);
        type = getIntent().getStringExtra("type");

        initTitle();

        initView();

        initData();
    }

    private void initData() {
        if (type.equals("child")) {
            String s = getIntent().getStringExtra(WATCHBELL);
//        BaseApplication.getInstances().toast(s);
            char[] array = s.replace(",","").toCharArray();
            for (int i=0;i<4;i++) {
                a1 = array[0] + "";
                a2 = array[1]+"";
                a3 = array[2]+"";
                a4 = array[3]+"";
            }
            phone_voic = a1;
            phone_vibration = a2;
            msg_voic = a3;
            msg_vibration = a4;

            if ("1".equals(a1)){
                btn_voice.setChecked(true);
                btn_voice.setBackgroundResource(R.drawable.edit_btn_open);
            }else if ("0".equals(a1)){
                btn_voice.setChecked(false);
                btn_voice.setBackgroundResource(R.drawable.edit_btn_close);
            }

            if ("1".equals(a2)){
                btn_vibration.setChecked(true);
                btn_vibration.setBackgroundResource(R.drawable.edit_btn_open);
            }else if ("0".equals(a2)){
                btn_vibration.setChecked(false);
                btn_vibration.setBackgroundResource(R.drawable.edit_btn_close);
            }

            if ("1".equals(a3)){
                btn_msg_voice.setChecked(true);
                btn_msg_voice.setBackgroundResource(R.drawable.edit_btn_open);
            }else if ("0".equals(a3)){
                btn_msg_voice.setChecked(false);
                btn_msg_voice.setBackgroundResource(R.drawable.edit_btn_close);
            }

            if ("1".equals(a4)){
                btn_msg_vibration.setChecked(true);
                btn_msg_vibration.setBackgroundResource(R.drawable.edit_btn_open);
            }else if ("0".equals(a4)){
                btn_msg_vibration.setChecked(false);
                btn_msg_vibration.setBackgroundResource(R.drawable.edit_btn_close);
            }
        }

    }

    private void initView() {
        // TODO: 2017/2/21  声音设置
        btn_voice = (ToggleButton) findViewById(R.id.btn_voice);
        btn_vibration = (ToggleButton) findViewById(R.id.btn_vibration);

        btn_msg_voice = (ToggleButton) findViewById(R.id.btn_msg_voice);
        btn_msg_vibration = (ToggleButton) findViewById(R.id.btn_msg_vibration);

        btn_voice.setOnClickListener(this);
        btn_vibration.setOnClickListener(this);
        btn_msg_voice.setOnClickListener(this);
        btn_msg_vibration.setOnClickListener(this);
    }


    private ImageButton ibtn_back;
    private TextView tv_title;

    private void initTitle() {

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title.setText("声音设置");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibtn_back:
                if (a1!=phone_voic||a2!=phone_vibration||a3!=msg_voic||a4!=msg_vibration) {
                    sendData();
                }

                finish();
                break;

            case R.id.btn_voice:
                if (btn_voice.isChecked()){
                    phone_voic = "0";
                    btn_voice.setBackgroundResource(R.drawable.edit_btn_close);
                }else {
                    phone_voic = "1";
                    btn_voice.setBackgroundResource(R.drawable.edit_btn_open);
                }
                break;

            case R.id.btn_vibration:

                if (btn_vibration.isChecked()){
                    phone_vibration = "0";
                    btn_vibration.setBackgroundResource(R.drawable.edit_btn_close);
                }else {
                    phone_vibration = "1";
                    btn_vibration.setBackgroundResource(R.drawable.edit_btn_open);
                }

                break;

            case R.id.btn_msg_voice:

                if (btn_msg_voice.isChecked()){
                    msg_voic = "0";
                    btn_msg_voice.setBackgroundResource(R.drawable.edit_btn_close);
                }else {
                    msg_voic = "1";
                    btn_msg_voice.setBackgroundResource(R.drawable.edit_btn_open);
                }

                break;

            case R.id.btn_msg_vibration:

                if (btn_msg_vibration.isChecked()){
                    msg_vibration = "0";
                    btn_msg_vibration.setBackgroundResource(R.drawable.edit_btn_close);
                }else {
                    msg_vibration = "1";
                    btn_msg_vibration.setBackgroundResource(R.drawable.edit_btn_open);
                }

                break;

        }

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

//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if (buttonView.equals(btn_voice)){
//            if (isChecked){
//                btn_voice.setBackgroundResource(R.drawable.edit_btn_open);
//            }else {
//                btn_voice.setBackgroundResource(R.drawable.edit_btn_close);
//            }
//        }else if (buttonView.equals(btn_vibration)){
//            if (isChecked){
//                btn_vibration.setBackgroundResource(R.drawable.edit_btn_open);
//            }else {
//                btn_vibration.setBackgroundResource(R.drawable.edit_btn_close);
//            }
//        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            if (a1!=phone_voic||a2!=phone_vibration||a3!=msg_voic||a4!=msg_vibration) {
                sendData();
            }
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void sendData() {
        if (type.equals("child")) {
            Intent intent = new Intent();

            if (a1 != phone_voic){
                intent.putExtra("phone_voic",phone_voic);
            }else {
                intent.putExtra("phone_voic",a1);
            }

            if (a2 != phone_vibration){
                intent.putExtra("phone_vibration",phone_vibration);
            }else {
                intent.putExtra("phone_vibration",a2);
            }

            if (a3 != msg_voic){
                intent.putExtra("msg_voic",msg_voic);
            }else {
                intent.putExtra("msg_voic",a3);
            }

            if (a4 != msg_vibration){
                intent.putExtra("msg_vibration",msg_vibration);
            }else {
                intent.putExtra("msg_vibration",a4);
            }
            setResult(RESULT_OK,intent);
        }else {
            finish();
        }

    }
}
