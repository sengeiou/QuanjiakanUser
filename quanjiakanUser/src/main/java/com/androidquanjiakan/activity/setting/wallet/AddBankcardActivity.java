package com.androidquanjiakan.activity.setting.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by Gin on 2016/7/25.
 */
public class AddBankcardActivity extends BaseActivity {

    private LinearLayout ll_addbankcard;
    private TextView tv_title;
    private ImageButton ibtn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_addbankcard);
        initTitle();
        initView();
    }

    private void initTitle(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText("银行卡");
        ibtn_back =(ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

    private void initView() {
        ll_addbankcard = (LinearLayout)findViewById(R.id.ll_addbankcard);
        ll_addbankcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(AddBankcardActivity.this,AddBankcardStep1Activity.class);
//                startActivity(intent);
                Intent intent = new Intent();
                intent.setClass(AddBankcardActivity.this,CheckPasswordActivity.class);
                intent.putExtra(CheckPasswordActivity.PARAMS_FROM,CheckPasswordActivity.FROM_ADD);
                startActivity(intent);
                finish();
            }
        });

    }
}
