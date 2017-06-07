package com.androidquanjiakan.activity.index.watch_old;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.watch_old.fragment.HealthHeartRateFragment;
import com.androidquanjiakan.activity.index.watch_old.fragment.HealthSleepFragment;
import com.androidquanjiakan.activity.index.watch_old.fragment.HealthStepFragment;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Gin on 2017/4/6.
 */

public class HealthDynamicsActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private ImageButton ibtn_back;
    private RadioGroup rg_health;
    private RadioButton rbt_step;
    private RadioButton rbt_heartrate;
    private RadioButton rbt_sleep;
    private FrameLayout fl_fragment;
    private final int TYPE_STEP=1;
    private final int TYPE_HEART_RATE=2;
    private final int TYPE_SLEEP=3;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HealthStepFragment healthStepFragment;
    private HealthHeartRateFragment healthHeartRateFragment;
    private HealthSleepFragment healthSleepFragment;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_dynamics);
        BaseApplication.getInstances().setCurrentActivity(this);
        deviceId = getIntent().getStringExtra("device_id");
        initTitle();
        initView();

    }

    private void initView() {
        rg_health = (RadioGroup) findViewById(R.id.rg_health);
        rg_health.setOnCheckedChangeListener(this);

        rbt_step = (RadioButton) findViewById(R.id.rbt_step);
        rbt_step.setChecked(true);

        rbt_heartrate = (RadioButton) findViewById(R.id.rbt_heartrate);
        rbt_sleep = (RadioButton) findViewById(R.id.rbt_sleep);

        fl_fragment = (FrameLayout) findViewById(R.id.fl_fragment);
        //初始化
        setFragment(TYPE_STEP);


    }

    private void setFragment(int type) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId",deviceId);
        if(fragmentManager==null) {
            fragmentManager = getFragmentManager();
        }
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (type){
            case TYPE_STEP:
                if(healthStepFragment==null) {
                    healthStepFragment = new HealthStepFragment();
                    healthStepFragment.setArguments(bundle);
                }
                if(!healthStepFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.fl_fragment,healthStepFragment);
                    fragmentTransaction.commit();
                }else {
                    fragmentTransaction.show(healthStepFragment);
                }

                break;
            case TYPE_HEART_RATE:
                if(healthHeartRateFragment==null) {
                    healthHeartRateFragment = new HealthHeartRateFragment();
                    healthHeartRateFragment.setArguments(bundle);
                }
                if(!healthHeartRateFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.fl_fragment,healthHeartRateFragment);
                    fragmentTransaction.commit();
                }else {
                    fragmentTransaction.show(healthHeartRateFragment);
                }

                break;

            case TYPE_SLEEP:
                if(healthSleepFragment==null) {
                    healthSleepFragment = new HealthSleepFragment();
                    healthSleepFragment.setArguments(bundle);
                }
                if(!healthSleepFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.fl_fragment,healthSleepFragment);
                    fragmentTransaction.commit();
                }else {
                    fragmentTransaction.show(healthSleepFragment);
                }

                break;
        }


    }

    private void initTitle() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("健康动态");

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
        switch (view.getId()){
            case R.id.ibtn_back:
                finish();
                break;
        }


    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i){
            case R.id.rbt_step:
                setFragment(TYPE_STEP);
                break;
            case R.id.rbt_heartrate:
                setFragment(TYPE_HEART_RATE);
                break;
            case R.id.rbt_sleep:
                setFragment(TYPE_SLEEP);
                break;
        }

    }
}
