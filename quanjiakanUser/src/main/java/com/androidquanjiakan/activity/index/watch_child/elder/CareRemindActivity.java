package com.androidquanjiakan.activity.index.watch_child.elder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquanjiakan.adapter.CareRemindAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.CareRemindBean;
import com.androidquanjiakan.entity.CareRemindEntity;
import com.androidquanjiakan.util.LogUtil;
import com.example.greendao.dao.CareRemindBeanDao;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Administrator on 2017/4/6 10:31
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class CareRemindActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 标题栏
     */
    private ImageButton btn_back;
    private TextView tv_title;


    private ListView listView;
    private ImageView nonedata;
    private TextView nonedatahint;
    private TextView nonedatabtn;

    private List<CareRemindEntity> list = new ArrayList<CareRemindEntity>();
    private CareRemindAdapter adapter;
    private CareRemindBeanDao dao;
    private ImageButton ibtn_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_watch);
        dao = getCareRemindBeanDao();
        initTitle();

        initView();


    }

    private CareRemindBeanDao getCareRemindBeanDao() {
        return BaseApplication.getInstances().getDaoInstant().getCareRemindBeanDao();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    private void initData() {
        if (list != null) {
            list.clear();
        }
        if (dao.loadAll().size() > 0) {
            showNoneDataView(false);
            ibtn_menu.setVisibility(View.VISIBLE);

            for (int i = 0; i < dao.loadAll().size(); i++) {
                CareRemindBean careRemindBean = dao.loadAll().get(i);
                CareRemindEntity entity = new CareRemindEntity();
                entity.setButton(careRemindBean.getButton());
                entity.setTime(careRemindBean.getTime());
                entity.setTitle(careRemindBean.getTitle());
                entity.setWeek(careRemindBean.getWeek());
                list.add(entity);
            }
            adapter.setmData(list);
            adapter.notifyDataSetChanged();
        } else {
            showNoneDataView(true);
            ibtn_menu.setVisibility(View.GONE);
        }


    }

    private void initView() {
        listView = (ListView) findViewById(R.id.list);

        nonedata = (ImageView) findViewById(R.id.nonedata);
        nonedatahint = (TextView) findViewById(R.id.nonedatahint);
        nonedatabtn = (TextView) findViewById(R.id.nonedatabtn);

        nonedatabtn.setOnClickListener(this);

        adapter = new CareRemindAdapter(this, list);
        listView.setAdapter(adapter);


    }

    //编辑提醒
    private void toEditRemind(int i) {

        Intent intent = new Intent(CareRemindActivity.this, EditRemindActivity.class);
        intent.putExtra(EditRemindActivity.TYPE, "edit");
        intent.putExtra("position", i);
        startActivity(intent);
    }

    private void initTitle() {
        btn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ibtn_menu = (ImageButton) findViewById(R.id.ibtn_menu);
        ibtn_menu.setImageDrawable(getResources().getDrawable(R.drawable.icon_add));
        ibtn_menu.setOnClickListener(this);

        btn_back.setVisibility(View.VISIBLE);

        btn_back.setOnClickListener(this);
        tv_title.setText("关爱提醒");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;

            case R.id.nonedatabtn:
                toAddRemind();
                break;

            case R.id.ibtn_menu:
                toAddRemind();
                break;


        }

    }

    //添加提醒
    private void toAddRemind() {
        Intent intent = new Intent();
        intent.setClass(this, EditRemindActivity.class);
        intent.putExtra(EditRemindActivity.TYPE, "add");
        startActivity(intent);
    }

    private void showNoneDataView(boolean b) {
        if (b) {
            nonedata.setVisibility(View.VISIBLE);
            nonedatahint.setVisibility(View.VISIBLE);
            nonedatabtn.setVisibility(View.VISIBLE);
        } else {
            nonedata.setVisibility(View.GONE);
            nonedatahint.setVisibility(View.GONE);
            nonedatabtn.setVisibility(View.GONE);
        }
    }
}
