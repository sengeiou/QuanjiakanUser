package com.androidquanjiakan.activity.index.watch_child;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChildWatchSettingEntryActivity extends BaseActivity implements OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_child_watch_setting_entry);
        initTitleBar();
        initView();
    }

    private TextView tv_title;
    private TextView menu_text;
    private ImageButton ibtn_back;

    private RelativeLayout baby_card;
    private RelativeLayout watch_manager;
    private RelativeLayout screen_time;
    private RelativeLayout bind_unbind;
    private RelativeLayout watch_info;

    private TextView time_set;


    public void initTitleBar(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("手表设置");

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(VISIBLE);
        ibtn_back.setOnClickListener(this);

        menu_text = (TextView) findViewById(R.id.menu_text);
        menu_text.setVisibility(GONE);
    }

    protected void initView(){
        baby_card = (RelativeLayout) findViewById(R.id.baby_card);
        baby_card.setOnClickListener(this);

        watch_manager = (RelativeLayout) findViewById(R.id.watch_manager);
        watch_manager.setOnClickListener(this);

        screen_time = (RelativeLayout) findViewById(R.id.screen_time);
        screen_time.setOnClickListener(this);
        time_set = (TextView) findViewById(R.id.time_set);

        bind_unbind = (RelativeLayout) findViewById(R.id.bind_unbind);
        bind_unbind.setOnClickListener(this);

        watch_info = (RelativeLayout) findViewById(R.id.watch_info);
        watch_info.setOnClickListener(this);
    }

    //获取设定的亮屏时间
    public void getSettingTime(){
        time_set.setText("30s");
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()){
            case R.id.baby_card:
                toBabyCard();
                break;
            case R.id.watch_manager:
                toWatchManager();
                break;
            case R.id.screen_time:
                toSetScreenTime();
                break;
            case R.id.bind_unbind:
                toBindUnbind();
                break;
            case R.id.watch_info:
                toWatchInfo();
                break;
            case R.id.ibtn_back:
                finish();
                break;
        }
    }
    // TODO 宝贝名片
    public void toBabyCard(){
        Intent intent = new Intent(this,ChildWatchCardActivity.class);
        startActivity(intent);
    }
    // TODO 手表管理
    public void toWatchManager(){

    }
    // TODO 设置屏幕时间
    public void toSetScreenTime(){

    }
    // TODO 绑定、解绑
    public void toBindUnbind(){

    }
    // TODO 手表信息
    public void toWatchInfo(){

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

    private final int REQUEST_INFO = 1024;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
        }
    }
}
