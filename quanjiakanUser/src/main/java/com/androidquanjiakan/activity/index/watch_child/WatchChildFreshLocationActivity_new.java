package com.androidquanjiakan.activity.index.watch_child;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WatchChildFreshLocationActivity_new extends BaseActivity implements OnClickListener {

//    public static String PARAMS_DEVICE_TYPE = "deviceType";
//    public static String PARAMS_DEVICE_LAT = "devicelat";
//    public static String PARAMS_DEVICE_LNG = "devicelng";

    private String device_id;
    private String device_type;

    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private double lat;
    private double lng;

    private Handler myHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_watch_child_fresh_locate_new);
        ButterKnife.bind(this);

        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        device_type = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_TYPE);
        lat = getIntent().getDoubleExtra(BaseConstants.PARAMS_DEVICE_LAT,-200);
        lng = getIntent().getDoubleExtra(BaseConstants.PARAMS_DEVICE_LNG,-200);
        if (device_id == null || device_id.length() < 0 || device_id.length() != 15 || device_type == null || device_type.length() < 1) {
            Toast.makeText(this, "传入参数异常!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initTitleBar();

        setFragmentEmpty();
    }


    public void initTitleBar() {
        ibtnBack.setVisibility(View.VISIBLE);
        ibtnBack.setOnClickListener(this);

        tvTitle.setText("地图");

    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
    }



    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private WatchChildFreshLocationFragment fragment;
    private WatchChildFreshLocationFragment_white fragment2;
    public void setFragment(){
        if(fragmentManager==null){
            fragmentManager = getFragmentManager();
        }
        fragmentTransaction = fragmentManager.beginTransaction();
        if(fragment==null){
            fragment = new WatchChildFreshLocationFragment();
        }
        //**********************
        //TODO
        fragment.setDevice_id(device_id);
        fragment.setDevice_type(device_type);
        fragment.setLat(lat);
        fragment.setLng(lng);
        //**********************
        if(!fragment.isAdded()){
            fragmentTransaction.replace(R.id.fl_container,fragment);
            fragmentTransaction.commit();
        }else{
            fragmentTransaction.show(fragment);
        }
    }

    public void setFragmentEmpty(){
        if(fragmentManager==null){
            fragmentManager = getFragmentManager();
        }
        fragmentTransaction = fragmentManager.beginTransaction();
        if(fragment2==null){
            fragment2 = new WatchChildFreshLocationFragment_white();
        }
        if(!fragment2.isAdded()){
            fragmentTransaction.replace(R.id.fl_container,fragment2);
            fragmentTransaction.commit();
        }else{
            fragmentTransaction.show(fragment2);
        }

        //ddd 进来的 好的
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setFragment();
            }
        },120);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                Intent intent = new Intent();
                intent.putExtra(BaseConstants.PARAMS_DEVICE_LAT,fragment.getLat());
                intent.putExtra(BaseConstants.PARAMS_DEVICE_LNG,fragment.getLng());
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }
}
