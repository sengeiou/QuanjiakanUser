package com.androidquanjiakan.activity.index.missing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquanjiakan.adapter.NoticeMissingAdapter;
import com.androidquanjiakan.adapter.SearchPublishAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.entity.MissingPersonInfo;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonArray;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/11/18.
 */

public class SearchPublishRecordActivity extends BaseActivity {

    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.menu_text)
    TextView menuText;
    @BindView(R.id.list)
    PullToRefreshListView list;

    private List<MissingPersonInfo> mData = new ArrayList<MissingPersonInfo>();
    private SearchPublishAdapter adapter;
    private int pageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_record);
        ButterKnife.bind(this);
        initView();
        initData();

    }


    private void initData() {
        list.setMode(PullToRefreshBase.Mode.BOTH);//可上拉刷新，下拉加载
        adapter = new SearchPublishAdapter(mData, this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchPublishRecordActivity.this, SearchPublishDetailActivity.class);
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

        list.setAdapter(adapter);
        loadPage(pageIndex);


    }
    private void loadPage(final int page) {
        if (mData!=null){
            mData.clear();
        }
        final ArrayList<MissingPersonInfo> temp = new ArrayList<>();
//        &data={"rows":20,"page":1}
//        String reqStr = "{\"rows\":" +20+",\"page\":"+page+"}";
        String reqStr = "{\"status\":" + 0 + ",\"userId\":" + QuanjiakanSetting.getInstance().getUserId() + ",\"rows\":" +20+",\"page\":"+page+"}";
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if (null!=val&&val.length()>0){
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    LogUtil.e("xinxi+++++++++"+jsonObject.toString());
                    if ("200".equals(jsonObject.get("code").getAsString())&&null!=jsonObject.get("rows").getAsJsonArray()){
                        JsonArray list = jsonObject.get("rows").getAsJsonArray();
                        for (int i = 0;i<list.size();i++){
                            JsonObject object = list.get(i).getAsJsonObject();

                            MissingPersonInfo info = new MissingPersonInfo();
                            if (object.has("name")) {
                                info.setName(object.get("name").getAsString());
                            }
                            if (object.has("age")) {
                                info.setAge(object.get("age").getAsString());
                            }
                            if (object.has("missingTime")) {
                                info.setMissingTime(object.get("missingTime").getAsLong());
                            }if (object.has("images")) {
                                info.setImag(object.get("images").getAsString());
                            }if (object.has("sex")) {
                                info.setGender(object.get("sex").getAsString());
                            }if (object.has("weight")) {
                                info.setWeight(object.get("weight").getAsString());
                            }
                            if (object.has("publishTime")) {
                                info.setPublishTime(object.get("publishTime").getAsLong());
                            }

                            if (object.has("height")){
                                info.setHeight(object.get("height").getAsString());
                            }else{
                                info.setHeight("无");
                            }

                            if (object.has("missingAddress")) {
                                info.setMissingAddress(object.get("missingAddress").getAsString());
                            }

                            if (object.has("contacts")) {
                                info.setContacts(object.get("contacts").getAsString());
                            }

                            if (object.has("contactsPhone")) {
                                info.setContactsPhone(object.get("contactsPhone").getAsString());
                            }

                            if (object.has("clothes")) {
                                info.setClothes(object.get("clothes").getAsString());
                            }

                            if (object.has("status")) {
                                info.setStatus(object.get("status").getAsInt());
                            }

                            if (object.has("orderid")) {
                                info.setOrderid(object.get("orderid").getAsString());
                            }

                            if (object.has("missingUserId")) {
                                info.setMissingUserId(object.get("missingUserId").getAsString());
                            }

                            if (object.has("findTime")) {
                                info.setFindTime(object.get("findTime").getAsString());
                            }

                            if (object.has("findLongitude")) {
                                info.setFindLongitude(object.get("findLongitude").getAsString());
                            }

                            if (object.has("findLatitude")) {
                                info.setFindLatitude(object.get("findLatitude").getAsString());
                            }

                            temp.add(info);
                        }
                    }
                    if (page==1){
                        mData.clear();
                    }
                    mData.addAll(temp);
                    if (mData.size()<((page)*20)){
                        list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }else {
                        list.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                    adapter.notifyDataSetChanged();
                }
                list.onRefreshComplete();
            }
        }, HttpUrls.getMisiingPersonInfo()+ "&data=" + reqStr,null, Task.TYPE_GET_STRING_NOCACHE,null));


    }

    private void initView() {
        ibtnBack.setVisibility(View.VISIBLE);
        tvTitle.setText("寻人发布记录");
//        menuText.setText("记录");
//        menuText.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.ibtn_back, R.id.menu_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
//            case R.id.menu_text:
//                BaseApplication.getInstances().toast("发布");
//                break;
        }
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
        loadPage(pageIndex);
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
    }
}
