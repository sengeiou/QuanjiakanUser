package com.androidquanjiakan.activity.index.missing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.adapter.NoticeMissingAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.MissingPersonInfo;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 寻人启事
 * Created by Administrator on 2016/11/17.
 */

public class NoticeMissingActivity extends BaseActivity {

    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ibtn_menu)
    ImageButton ibtnMenu;
    @BindView(R.id.menu_text)
    TextView menuText;
    @BindView(R.id.list)
    PullToRefreshListView list;


    private List<MissingPersonInfo> mData = new ArrayList<MissingPersonInfo>();
    private NoticeMissingAdapter adapter;
    private int pageIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_missing);
        ButterKnife.bind(this);
        initView();
        showNoneData(false);
        initData();
    }

    private void initData() {
        list.setMode(PullToRefreshBase.Mode.BOTH);//可上拉刷新，下拉加载


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NoticeMissingActivity.this, NoticeMissingDetailActivity.class);
                intent.putExtra("info",adapter.getData().get((int)id));
                startActivity(intent);
            }
        });
        //设置刷新监听
        list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mData.clear();
                pageIndex = 1;
                loadPage(pageIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if(adapter.getCount()<((pageIndex)*20)){
                    list.onRefreshComplete();
                }else{
                    ++pageIndex;
                    loadPage(pageIndex);
                }

            }
        });

        adapter = new NoticeMissingAdapter(this, mData);

        list.setAdapter(adapter);
        pageIndex = 1;
        loadPage(pageIndex);


    }
    private void loadPage(final int page) {

        final ArrayList<MissingPersonInfo> temp = new ArrayList<>();
        if(!NetUtil.isNetworkAvailable(this)) {
            android.widget.Toast.makeText(this, "请检查网络连接", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
//        &data={"status":0,"userId":0,"rows":20,"page":1}
        String reqStr = "{\"rows\":" +20+",\"page\":"+page+"}";

        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                list.onRefreshComplete();
                if (null!=val&&val.length()>0) {
                    showNoneData(false);
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
//                    if (null != jsonObject &&
//                            jsonObject.has("rows") &&
//                            !(jsonObject.get("rows") instanceof JsonNull) &&
//                            jsonObject.get("rows").getAsJsonArray() != null) {
                    if ("200".equals(jsonObject.get("code").getAsString())&&null!=jsonObject.get("rows").getAsJsonArray()){
                        JsonArray list = jsonObject.get("rows").getAsJsonArray();
                        int size = list.size();
                        for (int i = 0; i < size; i++) {
                            JsonObject object = list.get(i).getAsJsonObject();
                            /**
                             * init Data
                             */
                            MissingPersonInfo info = new MissingPersonInfo();

                            if (object != null && object.has("name") && !(object.get("name") instanceof JsonNull)) {
                                info.setName(object.get("name").getAsString());
                            } else {
                                info.setName("");
                            }

                            if (object != null && object.has("age") && !(object.get("age") instanceof JsonNull)) {
                                info.setAge(object.get("age").getAsString());
                            } else {
                                info.setAge("");
                            }

                            if (object != null && object.has("missingTime") && !(object.get("missingTime") instanceof JsonNull)) {
                                info.setMissingTime(object.get("missingTime").getAsLong());
                            } else {
                                info.setMissingTime(new Date().getTime());
                            }

                            if (object != null && object.has("images") && !(object.get("images") instanceof JsonNull)) {
                                info.setImag(object.get("images").getAsString());
                            } else {
                                info.setImag("");
                            }

                            if (object != null && object.has("sex") && !(object.get("sex") instanceof JsonNull)) {
                                info.setGender(object.get("sex").getAsString());
                            } else {
                                info.setGender("0");
                            }

                            if (object != null && object.has("weight") && !(object.get("weight") instanceof JsonNull)) {
                                info.setWeight(object.get("weight").getAsString());
                            } else {
                                info.setWeight("20");
                            }

                            if (object != null && object.has("publishTime") && !(object.get("publishTime") instanceof JsonNull)) {
                                info.setPublishTime(object.get("publishTime").getAsLong());
                            } else {
                                info.setPublishTime(new Date().getTime());
                            }

                            if (object != null && object.has("height") && !(object.get("height") instanceof JsonNull)) {
                                info.setHeight(object.get("height").getAsString());
                            } else {
                                info.setHeight("无");
                            }

                            if (object != null && object.has("missingAddress") && !(object.get("missingAddress") instanceof JsonNull)) {
                                info.setMissingAddress(object.get("missingAddress").getAsString());
                            } else {
                                info.setMissingAddress("无");
                            }

                            if (object != null && object.has("missingUserId") && !(object.get("missingUserId") instanceof JsonNull)) {
                                info.setMissingUserId(object.get("missingUserId").getAsString());
                            } else {
                                info.setMissingUserId("无");
                            }

                            if (object != null && object.has("contacts") && !(object.get("contacts") instanceof JsonNull)) {
                                info.setContacts(object.get("contacts").getAsString());
                            } else {
                                info.setContacts("无");
                            }

                            if (object != null && object.has("contactsPhone") && !(object.get("contactsPhone") instanceof JsonNull)) {
                                info.setContactsPhone(object.get("contactsPhone").getAsString());
                            } else {
                                info.setContactsPhone("无");
                            }

                            if (object != null && object.has("clothes") && !(object.get("clothes") instanceof JsonNull)) {
                                info.setClothes(object.get("clothes").getAsString());
                            } else {
                                info.setClothes("无");
                            }

                            if (object != null && object.has("status") && !(object.get("status") instanceof JsonNull)) {
                                info.setStatus(object.get("status").getAsInt());
                            } else {
                                info.setStatus(3);
                            }
                            temp.add(info);
                        }
                    }
                    if (pageIndex==1){
                        mData.clear();
                    }

                    if(page==1 && (temp==null || temp.size() < 1)){
                        showNoneData(true);
                    }else{
                        showNoneData(false);
                    }
                    mData.addAll(temp);
                    if (mData.size()<((page)*20)){
                        list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }else {
                        list.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                    list.onRefreshComplete();

//                    if (page==1){
//                        mData.clear();
//                    }
//                    mData.addAll(temp);
//                    if (adapter == null) {
//                        adapter = new NoticeMissingAdapter(getApplicationContext(), mData);
//                        list.setAdapter(adapter);
//                    } else {
//                        adapter.notifyDataSetChanged();
//                    }
//                    if (mData.size()<((page)*20)){
//                        list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//                    }else {
//                        list.setMode(PullToRefreshBase.Mode.BOTH);
//                    }
//                    adapter.notifyDataSetChanged();
//
//                    if (mData != null && mData.size() > 0) {
//                        showNoneData(false);
//                    } else {
//                        showNoneData(true);
//                    }
                }else{
                    if(page==1){
                        showNoneData(true);
                    }else{
                        showNoneData(false);
                    }
                }
                LogUtil.e("最后一页——————————————————————————————");
                adapter.notifyDataSetChanged();
            }
        }, HttpUrls.getPublishInfo() + "&data=" + reqStr,null, Task.TYPE_GET_STRING_NOCACHE,null));


    }
    @BindView(R.id.nonedata)
    ImageView nonedata;
    @BindView(R.id.nonedatahint)
    TextView nonedatahint;
    public void showNoneData(boolean isShow){
        if(isShow){
            nonedatahint.setText("暂无数据");
            nonedata.setVisibility(View.VISIBLE);
            nonedatahint.setVisibility(View.VISIBLE);
        }else{
            nonedata.setVisibility(View.GONE);
            nonedatahint.setVisibility(View.GONE);
        }
    }

    private void initView() {
        ibtnBack.setVisibility(View.VISIBLE);
        tvTitle.setText("寻人启事");
        menuText.setText("发布");
        menuText.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.ibtn_back, R.id.menu_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.menu_text:
                Intent intent = new Intent(this, PublishNoticeActivity.class);
                startActivity(intent);

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPage(1);
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
}
