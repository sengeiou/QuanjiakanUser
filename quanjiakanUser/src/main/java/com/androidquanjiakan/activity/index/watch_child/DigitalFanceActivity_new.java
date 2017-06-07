package com.androidquanjiakan.activity.index.watch_child;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.util.LogUtil;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

public class DigitalFanceActivity_new extends BaseActivity{

    private static String device_id;
    private static String phoneNumberString;
    public static String mAddress = "";
    private static int currentCommand;
    public static double mLat = 0x0;
    public static double mLon = 0x0;

    public static final int FIRST = 1;
    public static final int SECOND = 2;
    public static final int THIRD = 3;

    public static final int STATUS_SHOW = 1;
    public static final int STATUS_EDIT = 2;
    public static final int WHICH_DEFAULT = 0;

    private static int pageStatus = STATUS_SHOW;
    private static int pageWhich = FIRST;

    private int lineStatus;



    private Handler myHandler = new Handler();
    private Dialog noticeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_child_digital_fance_new);
        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        mLat = getIntent().getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, -200);
        mLon = getIntent().getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, -200);
        phoneNumberString = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_PHONE);
        pageStatus = getIntent().getIntExtra(BaseConstants.PARAMS_DEVICE_PAGE_STATE,STATUS_SHOW);
        pageWhich = getIntent().getIntExtra(BaseConstants.PARAMS_DEVICE_PAGE_WHICH,FIRST);
        LogUtil.e("mLat:" + mLat + "     mLon:" + mLon);
        if (device_id == null ||
                mLat == -200 || mLon == -200
                ) {
            BaseApplication.getInstances().toast(DigitalFanceActivity_new.this,"传入参数异常!");
            finish();
            return;
        }
        setFragmentEmpty();
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
    protected void onStop() {
        super.onStop();
    }

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private DigitalFanceFragment fragment;
    private DigitalFanceEmptyFragment fragment2;
    public void setFragment(){
        if(fragmentManager==null){
            fragmentManager = getFragmentManager();
        }
        fragmentTransaction = fragmentManager.beginTransaction();
        if(fragment==null){
            fragment = new DigitalFanceFragment();
        }
        //**********************
        //TODO
        fragment.setDevice_id(device_id);
        fragment.setmLat(mLat);
        fragment.setmLon(mLon);
        fragment.setPageStatus(pageStatus);
        fragment.setPageWhich(pageWhich);
        fragment.setPhoneNumberString(phoneNumberString);
        //**********************
        if(!fragment.isAdded()){
            fragmentTransaction.replace(R.id.container,fragment);
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
            fragment2 = new DigitalFanceEmptyFragment();
        }
        if(!fragment2.isAdded()){
            fragmentTransaction.replace(R.id.container,fragment2);
            fragmentTransaction.commit();
        }else{
            fragmentTransaction.show(fragment2);
        }

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setFragment();
            }
        },120);
    }

}
