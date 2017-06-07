package com.androidquanjiakan.activity.index.watch_child.elder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidquanjiakan.base.BaseActivity;
import com.quanjiakan.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：Administrator on 2017/4/18 15:52
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class ShortcutKeyActivity extends BaseActivity {
/*   标题  */
    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    /*   控件  */
    @BindView(R.id.tv_electricity)
    TextView tvElectricity;
    @BindView(R.id.btn_electricity)
    ToggleButton btnElectricity;
    @BindView(R.id.rlt_electricity)
    RelativeLayout rltElectricity;
    @BindView(R.id.tv_auto_connect)
    TextView tvAutoConnect;
    @BindView(R.id.btn_on_line)
    ToggleButton btnOnLine;
    @BindView(R.id.rlt_auto_connect)
    RelativeLayout rltAutoConnect;
    @BindView(R.id.tv_phone_miss)
    TextView tvPhoneMiss;
    @BindView(R.id.btn_watch_miss)
    ToggleButton btnWatchMiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut_key);
        ButterKnife.bind(this);

        initTitle();

        initData();


    }

    private boolean isSOS = false;
    private boolean isOK = false;
    private boolean isLoop = false;

    private void initData() {

        if (isSOS){
            btnElectricity.setBackgroundResource(R.drawable.edit_btn_open);
        }else {
            btnElectricity.setBackgroundResource(R.drawable.edit_btn_close);
        }

        if (isOK){
            btnOnLine.setBackgroundResource(R.drawable.edit_btn_open);
        }else {
            btnOnLine.setBackgroundResource(R.drawable.edit_btn_close);
        }


        if (isLoop){
            btnWatchMiss.setBackgroundResource(R.drawable.edit_btn_open);
        }else {
            btnWatchMiss.setBackgroundResource(R.drawable.edit_btn_close);
        }
    }

    private void initTitle() {

        ibtnBack.setVisibility(View.VISIBLE);
        tvTitle.setText("手表快捷键设置");
    }

    @OnClick({R.id.ibtn_back, R.id.btn_electricity, R.id.btn_on_line, R.id.btn_watch_miss})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.btn_electricity:
                if (isSOS) {
                    btnElectricity.setBackgroundResource(R.drawable.edit_btn_close);
                }else {
                    btnElectricity.setBackgroundResource(R.drawable.edit_btn_open);
                }
                isSOS = !isSOS;
                break;
            case R.id.btn_on_line:

                if (isOK) {
                    btnOnLine.setBackgroundResource(R.drawable.edit_btn_close);
                }else {
                    btnOnLine.setBackgroundResource(R.drawable.edit_btn_open);
                }
                isOK = !isOK;

                break;
            case R.id.btn_watch_miss:
                if (isLoop) {
                    btnWatchMiss.setBackgroundResource(R.drawable.edit_btn_close);
                }else {
                    btnWatchMiss.setBackgroundResource(R.drawable.edit_btn_open);
                }
                isLoop = !isLoop;
                break;
        }
    }
}
