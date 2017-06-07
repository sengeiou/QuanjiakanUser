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

import com.alibaba.fastjson.JSONObject;
import com.androidquanjiakan.adapter.DescribleAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.LeavingMessageInfo;
import com.androidquanjiakan.entity.MissingPersonInfo;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.view.RoundImageView;
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
 * Created by Administrator on 2016/11/18.
 */

public class NoticeMissingDetailActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;

//    @BindView(R.id.iv_photo)
//    RoundedImageView ivPhoto;
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
    @BindView(R.id.tv_gender_missing)//联系人
            TextView tvGenderMissing;
    @BindView(R.id.tv_phone)//联系电话
            TextView tvPhone;
    @BindView(R.id.tv_detail)
    TextView tvDetail;
    @BindView(R.id.listview)
    PullToRefreshListView listview;
    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.llt_leaving_msg)
    LinearLayout lltLeavingMsg;
    @BindView(R.id.missing_call)
    ImageView missingCall;
    @BindView(R.id.tv_msg_num)
    TextView tvMsgNum;

    private List<LeavingMessageInfo> mData = new ArrayList<LeavingMessageInfo>();
    private DescribleAdapter adapter;
    private int pageIndex;
    private MissingPersonInfo info;
    private RoundImageView ivPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
        ButterKnife.bind(this);
        initTitle();
        initData();


    }

    private void initData() {
        listview.setMode(PullToRefreshBase.Mode.BOTH);//可上拉刷新，下拉加载
        adapter = new DescribleAdapter(this, mData);
        //点击留言列表
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

//        String strGet = "{\"missingUserId\":" + "\"" + info.getMissingUserId() + "\"" + ",\"rows\":" + "\"" + "20" + "\"" + ",\"page\":" + "\"" + page + "\"" + "}";

        MyHandler.putTask(this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if (null != val && val.length() > 0) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if ("200".equals(jsonObject.get("code").getAsString()) && null != jsonObject.get("rows").getAsJsonArray()) {
//                        total = jsonObject.get("total").getAsInt();
//                        if (total==0){
//                            tvMsgNum.setText("(暂无留言)");
//                        }else {
//                            tvMsgNum.setText("(共"+total+"条留言)");
//                        }

                        JsonArray list = jsonObject.get("rows").getAsJsonArray();
                        if(list.size()==0){
                            tvMsgNum.setText("(暂无留言)");
                        }else{
                            tvMsgNum.setText("(共"+list.size()+"条留言)");
                        }
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
                        listview.setAdapter(adapter);
                        listview.onRefreshComplete();
                    } else {
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
        }, HttpUrls.getLeavingMessage() + "&data=" + strGet, null, Task.TYPE_GET_STRING_NOCACHE, null));





    }

    private void initTitle() {
        ivPhoto = (RoundImageView) findViewById(R.id.iv_photo);
//        ivPhoto.mutateBackground(true);

        info = (MissingPersonInfo) getIntent().getSerializableExtra("info");
        ibtnBack.setVisibility(View.VISIBLE);
        tvTitle.setText("寻人启事详情");
        tvTitle.setVisibility(View.VISIBLE);
        tvName.setText(info.getName());
        LogUtil.e("xingbie----- "+info.getGender());
        tvGender.setText("0".equals(info.getGender()) ? "男" : "女");
        tvAge.setText(info.getAge()+" 岁");
        tvHeight.setText(info.getHeight()+" cm");
        tvWeight.setText(info.getWeight()+" Kg");
        LogUtil.e("shijian----------"+info.getMissingTime());
        LogUtil.e("zhonguoshijian----------"+ DateUtils.getStrTime(info.getMissingTime().toString()));
        tvMissTime.setText(DateUtils.getStrTime(info.getMissingTime().toString()));
        tvMissPlace.setText(info.getMissingAddress());
        tvGenderMissing.setText(info.getContacts());
        tvPhone.setText(info.getContactsPhone());
        tvDetail.setText(info.getClothes());

        if (null != info.getImag() && info.getImag().length() > 0) {
            Picasso.with(this).load(info.getImag()).error(R.drawable.record_pic_portrait).fit().into(ivPhoto);
        } else {
            Picasso.with(this).load(R.drawable.record_pic_portrait).into(ivPhoto);
        }
        if (info.getStatus() == 3) {
            ivFlag.setText("已找到");
            ivFlag.setTextColor(getResources().getColor(R.color.colorflag));
            missingCall.setVisibility(View.GONE);

        }

    }

    @OnClick({R.id.ibtn_back, R.id.llt_leaving_msg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.llt_leaving_msg:
                Intent intent = new Intent(this, LeavingMsgActivity.class);
                intent.putExtra("missingUserId", info.getMissingUserId());
                startActivity(intent);
                break;
        }
    }

    @OnClick(R.id.missing_call)
    public void onClick() {
        if (null != tvPhone.getText().toString().trim() && tvPhone.getText().toString().trim().length() > 0) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvPhone.getText().toString().trim()));
            startActivity(intent);
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
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
    }
}
