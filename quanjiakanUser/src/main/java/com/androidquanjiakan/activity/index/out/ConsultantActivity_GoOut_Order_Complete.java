package com.androidquanjiakan.activity.index.out;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

public class ConsultantActivity_GoOut_Order_Complete extends BaseActivity implements OnClickListener {

    private TextView tv_title;
    private ImageButton ibtn_back;

    private TextView back_main;
    private TextView back;

    public static final String PARAMS_TIME = "time";
    private String timeValue;
    private TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_doctor_go_out_order_complete);
        timeValue = getIntent().getStringExtra(PARAMS_TIME);
        if(timeValue==null){
            BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order_Complete.this,"传入参数异常!");
            finish();
            return;
        }
        initTitleBar();
        initInfoLine();
    }

    public void initTitleBar(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("订单详情");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
    }

    public void initInfoLine(){
        /**
         *
         */
        back_main = (TextView) findViewById(R.id.back_main);
        back = (TextView) findViewById(R.id.back);
        back_main.setOnClickListener(this);
        back.setOnClickListener(this);
        time = (TextView) findViewById(R.id.time);
        time.setText(timeValue);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    public void loadData(){
        /**
         * 接口获取金额
         */
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.back_main://返回首页
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.back://我的订单
                finish();
                break;
            default:
                break;
        }
    }
}
