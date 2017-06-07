package com.androidquanjiakan.activity.setting.giftrecord;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquanjiakan.activity.setting.ebean.EBeanChargeHistoryActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 充值页
 */
public class GiftRecordActivity extends BaseActivity implements OnClickListener {

    private ImageButton ibtn_back;
    private TextView tv_title;
    private TextView menu_text;

    private LinearLayout condition_line_left;
    private TextView condition_left;
    private View condition_left_div;

    private LinearLayout condition_line_right;
    private TextView condition_right;
    private View condition_right_div;

    /**
     * 用户指定进入后默认加载哪个页面
     */
    public static final String PARAMS_LOAD_TYPE = "loadtype";
    private int defaultLoadType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gift_record);
        defaultLoadType = getIntent().getIntExtra(PARAMS_LOAD_TYPE,TYPE_LEFT);
        initTitleBar();

        initTabLine();
    }

    private void initTabLine(){
        condition_line_left = (LinearLayout) findViewById(R.id.condition_line_left);
        condition_line_left.setOnClickListener(this);
        condition_left = (TextView) findViewById(R.id.condition_left);
        condition_left.setText("送出礼物");
        condition_left_div = findViewById(R.id.condition_left_div);
        condition_left_div.setBackgroundResource(R.color.color_title_green);


        condition_line_right = (LinearLayout) findViewById(R.id.condition_line_right);
        condition_line_right.setOnClickListener(this);
        condition_right = (TextView) findViewById(R.id.condition_right);
        condition_right.setText("充值记录");
        condition_right_div = findViewById(R.id.condition_right_div);
        condition_right_div.setBackgroundResource(R.color.color_title_green);

        clickTab(defaultLoadType);
        loadFragment(defaultLoadType);
    }

    private final int TYPE_LEFT = 1;
    private final int TYPE_RIGHT = 2;
    public void clickTab(int orientation){
        if(orientation==TYPE_LEFT){
            condition_left.setTextColor(getResources().getColor(R.color.color_title_green));
            condition_left_div.setVisibility(View.VISIBLE);

            condition_right.setTextColor(getResources().getColor(R.color.colorAlphaBlack_BB));
            condition_right_div.setVisibility(View.GONE);
        }else{
            condition_left.setTextColor(getResources().getColor(R.color.colorAlphaBlack_BB));
            condition_left_div.setVisibility(View.GONE);

            condition_right.setTextColor(getResources().getColor(R.color.color_title_green));
            condition_right_div.setVisibility(View.VISIBLE);
        }
    }

    //fragment container
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

//    private GiftSendRecordFragment giftSendRecordFragment;
    private GiftSendRecordFragment_new giftSendRecordFragment;
//    private SendGiftFragment sendGiftFragment;
    private GiftChargeRecordFragment giftChargeRecordFragment;

    public void loadFragment(int type){
        if (fragmentManager == null) {
            fragmentManager = getFragmentManager();
        }
        transaction = fragmentManager.beginTransaction();
        if(type==TYPE_LEFT){
            giftSendRecordFragment = new GiftSendRecordFragment_new();
            transaction.replace(R.id.fragment_container, giftSendRecordFragment);
            transaction.commit();
        }else{
            giftChargeRecordFragment = new GiftChargeRecordFragment();
            transaction.replace(R.id.fragment_container, giftChargeRecordFragment);
            transaction.commit();
        }
    }

    public void initTitleBar() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("礼物账单");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        menu_text = (TextView) findViewById(R.id.menu_text);
        menu_text.setVisibility(View.GONE);
        menu_text.setText("充值记录");
        menu_text.setOnClickListener(this);
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
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.menu_text:
                Intent intentChargeHistory = new Intent(GiftRecordActivity.this, EBeanChargeHistoryActivity.class);
                startActivity(intentChargeHistory);
                break;
            case R.id.condition_line_left:
                clickTab(TYPE_LEFT);
                loadFragment(TYPE_LEFT);
                break;
            case R.id.condition_line_right:
                clickTab(TYPE_RIGHT);
                loadFragment(TYPE_RIGHT);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

        }
    }
}
