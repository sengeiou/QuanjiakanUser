package com.androidquanjiakan.activity.setting.contact.bean;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.quanjiakan.main.R;

/**
 * 作者：Administrator on 2017/3/25 11:31
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class WatchInfoActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_info);

        initTitle();
    }


    private ImageButton ibtn_back;
    private TextView tv_title;

    private void initTitle() {

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title.setText("手表信息");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;

        }
    }
}
