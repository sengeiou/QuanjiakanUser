package com.androidquanjiakan.activity.index.devices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import uk.co.senab.photoview.PhotoViewAttacher;

public class CaseHistoryPicViewerActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_title;
    private ImageButton ibtn_back;
    private ImageView viewer;
    private ImageView delete;
    private PhotoViewAttacher mAttacher;

    private String url;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_case_history_viewer);
        position = this.getIntent().getIntExtra(BaseConstants.PARAMS_POSITION,-1);
        url = this.getIntent().getStringExtra(BaseConstants.PARAMS_URL);
        if(position<0){
            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"参数异常!");
            finish();
            return;
        }
        initTitleBar();
        initView();
    }

    public void  initTitleBar(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("浏览病历");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
    }

    protected void initView() {
        viewer = (ImageView) findViewById(R.id.viewer);
        delete = (ImageView) findViewById(R.id.delete);
        mAttacher = new PhotoViewAttacher(viewer);
        delete.setOnClickListener(this);
        ImageLoadUtil.loadImage(viewer,url,null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtn_back:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.delete:
                Intent intent = new Intent();
                intent.putExtra(BaseConstants.PARAMS_POSITION,position);
                setResult(RESULT_OK,intent);
                finish();
                break;
            default:
                break;
        }
    }
}
