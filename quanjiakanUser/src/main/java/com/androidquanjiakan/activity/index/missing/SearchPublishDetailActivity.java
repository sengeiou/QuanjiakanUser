package com.androidquanjiakan.activity.index.missing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquanjiakan.adapter.DescribleAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.LeavingMessageInfo;
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
import com.quanjiakanuser.util.DateUtils;
import com.quanjiakanuser.util.GsonParseUtil;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/11/21.
 */

public class SearchPublishDetailActivity extends BaseActivity {

    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.iv_flag)
    TextView ivFlag;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_height)
    TextView tvHeight;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.tv_miss_time)
    TextView tvMissTime;
    @BindView(R.id.tv_miss_place)
    TextView tvMissPlace;
    @BindView(R.id.tv_gender_missing)
    TextView tvGenderMissing;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_detail)
    TextView tvDetail;
    @BindView(R.id.tv_buy)
    TextView tvBuy;
    @BindView(R.id.tv_msg_num)
    TextView tvMsgNum;
    @BindView(R.id.listview)
    PullToRefreshListView list;
    @BindView(R.id.llt_leaving_msg)
    LinearLayout lltLeavingMsg;
    @BindView(R.id.iv_call)
    ImageView ivCall;


    //    private List<String> mData = new ArrayList<String>();
    private List<LeavingMessageInfo> mData = new ArrayList<LeavingMessageInfo>();
    private DescribleAdapter adapter;
    private int pageIndex;
    private MissingPersonInfo info;
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_status_detail);
        ButterKnife.bind(this);
        initTitle();
        initData();
    }

/*    private void initData() {
        list.setMode(PullToRefreshBase.Mode.BOTH);//可上拉刷新，下拉加载
        adapter = new DescribleAdapter(this, mData);
        //点击留言列表
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BaseApplication.getInstances().toast("留言列表" + position);
            }
        });
        //设置刷新监听
        list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageIndex = 1;
                loadPage(pageIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageIndex++;
                loadPage(pageIndex);
            }
        });

        list.setAdapter(adapter);

        loadPage(1);


    }*/

   /* private void loadPage(int page) {
        if (page == 1) {
            mData.clear();
        }

        final int size = mData.size();
        final ArrayList<String> temp = new ArrayList<>();
        *//**
     * 模拟数据加载
     *//*
        for (int i = size; i < size + 20; i++) {
            temp.add(i + "");
        }

        if (page == 1) {
            mData.addAll(temp);
            list.onRefreshComplete();
            adapter.notifyDataSetChanged();
        } else {
            list.onRefreshComplete();
            list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mData.addAll(temp);
                    adapter.notifyDataSetChanged();
                }
            }, 500);
        }


        */

    /**
     * 当一页数据不足时，判断为无更多数据
     *//*
        if (temp.size() >= 20) {
            list.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
    }*/
    private void initData() {
        list.setMode(PullToRefreshBase.Mode.BOTH);//可上拉刷新，下拉加载
        adapter = new DescribleAdapter(this, mData);
        //点击留言列表
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                BaseApplication.getInstances().toast("留言列表" + position);
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
                ++pageIndex;
                loadPage(pageIndex);
            }
        });

        list.setAdapter(adapter);
        pageIndex = 1;
        loadPage(pageIndex);

    }

    private void loadPage(int page) {
        final ArrayList<LeavingMessageInfo> temp = new ArrayList<>();
//        http://192.168.0.100:8080/familycare/health/api_get?token=&code=missing&action=findbymissinguserid
// &data={“missingUserId”:”2”,”rows”:”20”,”page”:”0”}

        String strGet = "{\"missingUserId\":" + "\"" + info.getMissingUserId() + "\"" + ",\"rows\":" + "\"" + "20" + "\"" + ",\"page\":" + "\"" + page + "\"" + "}";

        MyHandler.putTask(this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if (null != val && val.length() > 0) {
                    LogUtil.e("val666-----" + val);
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if ("200".equals(jsonObject.get("code").getAsString()) && jsonObject.get("rows").getAsJsonArray().size() > 0) {
                        total = jsonObject.get("total").getAsInt();
                        LogUtil.e("total---" + total);
                        if (total == 0) {
                            tvMsgNum.setText("(暂无留言)");
                        } else {
                            tvMsgNum.setText("(共" + total + "条留言)");
                        }
                        JsonArray list = jsonObject.get("rows").getAsJsonArray();

                        for (int i = 0; i < list.size(); i++) {
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
                    if (adapter == null) {
                        adapter = new DescribleAdapter(getApplicationContext(), mData);
                        list.setAdapter(adapter);
                        list.onRefreshComplete();
                    } else {
                        adapter.notifyDataSetChanged();
                        list.onRefreshComplete();
                    }
                    /**
                     * 当一页数据不足时，判断为无更多数据
                     */
                    if (temp.size() >= 20) {
                        list.setMode(PullToRefreshBase.Mode.BOTH);
                    } else {
                        list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                }
            }
        }, HttpUrls.getLeavingMessage() + "&data=" + strGet, null, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    private void initTitle() {
        /**
         * 这里需要加判断
         */
        info = (MissingPersonInfo) getIntent().getSerializableExtra("info");
        ibtnBack.setVisibility(View.VISIBLE);

        tvTitle.setVisibility(View.VISIBLE);
        tvName.setText(info.getName());
        tvGender.setText("0".equals(info.getGender()) ? "男" : "女");
        tvAge.setText(info.getAge() + "岁");
        tvHeight.setText(info.getHeight() + "cm");
        tvWeight.setText(info.getWeight() + "Kg");
        tvMissTime.setText(DateUtils.getStrTime(info.getMissingTime().toString()));
        tvMissPlace.setText(info.getMissingAddress());
        tvGenderMissing.setText(info.getContacts());
        tvPhone.setText(info.getContactsPhone());
        tvDetail.setText(info.getClothes());
//        ImageView call = (ImageView) findViewById(R.id.iv_call);
        if (null != info.getImag() && info.getImag().length() > 0) {
            Picasso.with(this).load(info.getImag()).error(R.drawable.record_pic_portrait).fit().into(ivPhoto);
        } else {
            Picasso.with(this).load(R.drawable.record_pic_portrait).into(ivPhoto);
        }
        if (info.getStatus() == 3) {
            ivFlag.setText("已找到");
            ivFlag.setTextColor(getResources().getColor(R.color.colorflag));
            tvTitle.setText("已找到");
            tvBuy.setTextColor(getResources().getColor(R.color.color_change));
            tvBuy.setBackgroundResource(R.drawable.check_btn_abolish);
            ivCall.setVisibility(View.GONE);
        } else if (info.getStatus() == 2) {
            ivFlag.setText("寻找中");
            ivFlag.setTextColor(getResources().getColor(R.color.color_xun_ing));
            tvTitle.setText("寻找中");
            tvBuy.setText("已找到");
            tvBuy.setTextColor(getResources().getColor(R.color.color_btn_look_ing));

            tvBuy.setBackgroundResource(R.drawable.check_btn_find);
        } else if (info.getStatus() == 4) {
            ivFlag.setText("放弃");
            ivFlag.setTextColor(getResources().getColor(R.color.color_change));
            tvTitle.setText("放弃");
            tvBuy.setText("放弃");
            tvBuy.setTextColor(getResources().getColor(R.color.color_change));
            tvBuy.setBackgroundResource(R.drawable.check_btn_abolish);
        } else if (info.getStatus() == 5) {
            ivFlag.setText("未通过");
            ivFlag.setTextColor(getResources().getColor(R.color.color_change));
            tvTitle.setText("未通过");
            tvBuy.setText("未通过");
            tvBuy.setTextColor(getResources().getColor(R.color.color_change));
            tvBuy.setBackgroundResource(R.drawable.check_btn_abolish);
        } else if (info.getStatus() == 1) {
            ivFlag.setText("审核中");
            ivFlag.setTextColor(getResources().getColor(R.color.color_xun_ing));
            tvTitle.setText("审核中");
            tvBuy.setText(getString(R.string.cancel));
            tvBuy.setTextColor(getResources().getColor(R.color.color_btn_look_ing));
            tvBuy.setBackgroundResource(R.drawable.check_btn_find);
        }

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != tvPhone.getText().toString().trim() && tvPhone.getText().toString().trim().length() > 0) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvPhone.getText().toString().trim()));
                    startActivity(intent);
                }
            }
        });


    }


    @OnClick({R.id.ibtn_back, R.id.llt_leaving_msg, R.id.tv_buy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.llt_leaving_msg:
                Intent intent = new Intent(this, LeavingMsgActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_buy:
                ChangeStatus();

                break;
        }
    }

    /**
     * 更改寻找状态
     */
    private void ChangeStatus() {
        //{"orderId":"QJKMIS20161124115155810130",missingUserModel:
        // {"findTime":"1479122871963","findAddress":"findAddress","findLongitude":"","findLatitude":""}}
        final String reqStr1 = "{\"orderId\":" + "\"" + info.getOrderid() + "\"" + "}";
        final String reqStr2 = "{\"orderId\":" + "\"" + info.getOrderid() + "\"" + ",missingUserModel:{" + "\"findTime\":" + "\"" + info.getFindTime() + "\"" +
                ",\"findAddress\":" + "\"" + "findAddress" + "\"" + ",\"findLongitude\":" + "\"" + info.getFindLongitude() + "\"" + ",\"findLatitude\":" + "\"" + info.getFindLatitude() + "\"" + "}}";
        if (info.getStatus() == 2) {
            MyHandler.putTask(new Task(new HttpResponseInterface() {

                @Override
                public void handMsg(String val) {
                    LogUtil.e("val-----------11---" + HttpUrls.CancelSearchStatus() + "&data=" + reqStr2);
                    if (null != val && val.length() > 0) {
                        JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                        if ("200".equals(jsonObject.get("code").getAsString())) {
                            BaseApplication.getInstances().toast(SearchPublishDetailActivity.this, "修改状态成功");
                            finish();
                        }
                    }
                }
            }, HttpUrls.ChangeSearchStatus() + "&data=" + reqStr2, null, Task.TYPE_GET_STRING_NOCACHE, null));
        } else if (info.getStatus() == 1) {
            MyHandler.putTask(new Task(new HttpResponseInterface() {

                @Override
                public void handMsg(String val) {
                    if (null != val && val.length() > 0) {
                        LogUtil.e("val-----------00---" + HttpUrls.CancelSearchStatus() + "&data=" + reqStr1);
                        JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                        if ("200".equals(jsonObject.get("code").getAsString())) {
                            BaseApplication.getInstances().toast(SearchPublishDetailActivity.this, "修改状态成功");
                            finish();
                        }
                    }
                }
            }, HttpUrls.CancelSearchStatus() + "&data=" + reqStr1, null, Task.TYPE_GET_STRING_NOCACHE, null));
        }

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
}
