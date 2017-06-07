package com.androidquanjiakan.activity.index.missing;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.androidquanjiakan.adapter.DescribleAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.LeavingMessageInfo;
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
 *
 *   留言列表
 * Created by Administrator on 2016/11/21.
 */

public class LeavingMsgActivity extends BaseActivity {

    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_msg_num)
    TextView tvMsgNum;
    @BindView(R.id.listview)
    PullToRefreshListView listview;

//    private List<String> mData = new ArrayList<String>();
    private List<LeavingMessageInfo> mData = new ArrayList<LeavingMessageInfo>();
    private DescribleAdapter adapter;
    private int pageIndex;
    private int total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_leaving_msglist);
        ButterKnife.bind(this);//关联butterKnife

        initData();
        initTitle();
    }

    private void initTitle() {
        ibtnBack.setVisibility(View.VISIBLE);
        tvTitle.setText("留言列表");
        tvTitle.setVisibility(View.VISIBLE);

    }

    /**
     * 初始化数据，展示第一页数据
     *
     */
    private void initData() {
        listview.setMode(PullToRefreshBase.Mode.BOTH);//可上拉刷新，下拉加载
        adapter = new DescribleAdapter(this, mData);
        //点击留言列表
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                BaseApplication.getInstances().toast("留言列表" + position);
            }
        });
        //设置刷新监听
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mData.clear();
                pageIndex = 1;
                loadPage(pageIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                ++pageIndex;
                loadPage(pageIndex);
            }
        });

        listview.setAdapter(adapter);
        pageIndex=1;
        loadPage(pageIndex);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //友盟统计
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);//设置当前activity
    }

    @Override
    protected void onPause() {
        super.onPause();
        //友盟统计
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    /**
     * 网络请求加载数据
     * @param page
     */
    private void loadPage(int page) {
        final ArrayList<LeavingMessageInfo> temp = new ArrayList<>();
//        http://192.168.0.100:8080/familycare/health/api_get?token=&code=missing&action=findbymissinguserid
// &data={“missingUserId”:”2”,”rows”:”20”,”page”:”0”}
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("missingUserId",getIntent().getStringExtra("missingUserId"));
        jsonObject.put("rows","20");
        jsonObject.put("page",page+"");
        LogUtil.e("--------req--------------"+jsonObject.toString());
        String strGet = jsonObject.toString();


//        String strGet = "{\"missingUserId\":" + "\"" + getIntent().getStringExtra("missingUserId") + "\"" + ",\"rows\":" + "\"" + "20" + "\"" + ",\"page\":" +"\""+page + "\""+"}";

        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {

                if (null!=val&&val.length()>0){
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if ("200".equals(jsonObject.get("code").getAsString()) && jsonObject.get("rows").getAsJsonArray().size() > 0){

                        JsonArray list = jsonObject.get("rows").getAsJsonArray();
                        if (list.size()>0){
                            tvMsgNum.setText("(共"+list.size()+"条留言)");

                        }else {
                            tvMsgNum.setText("(暂无留言)");
                        }
                        for (int i = 0;i<list.size();i++){
                            JsonObject object = list.get(i).getAsJsonObject();
                            LeavingMessageInfo info = new LeavingMessageInfo();
                            info.setContent(object.get("content").getAsString());
                            info.setMessageUserId(object.get("messageUserId").getAsLong());
                            info.setCreateTime(object.get("createTime").getAsLong());
                            info.setPicture(object.get("picture").getAsString());
                            info.setVolName(object.get("volunteerName").getAsString());
                            temp.add(info);
                        }
                    }

                    mData.addAll(temp);
                    if (adapter==null){
                        adapter = new DescribleAdapter(getApplicationContext(),mData);
                        listview.setAdapter(adapter);
                        listview.onRefreshComplete();
                    }else {
                        adapter.notifyDataSetChanged();
                        listview.onRefreshComplete();
                    }

                    /**
                     * 当一页数据不足时，判断为无更多数据
                     */
                    if (temp.size() >= 20) {
                        listview.setMode(PullToRefreshBase.Mode.BOTH);
                    } else {
                        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                }
            }
        }, HttpUrls.getLeavingMessage() + "&data=" + strGet,null,Task.TYPE_GET_STRING_NOCACHE,null));



    }

    @OnClick(R.id.ibtn_back)
    public void onClick() {
        finish();
    }
}
