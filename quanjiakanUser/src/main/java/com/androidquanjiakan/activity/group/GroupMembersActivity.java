package com.androidquanjiakan.activity.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.androidquanjiakan.adapter.Friend_FriendsAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.entity.FriendsBean;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by Gin on 2016/8/19.
 */
public class GroupMembersActivity extends BaseActivity implements View.OnClickListener {
    private ListView ll_members;
    private List<FriendsBean> members;
    private Friend_FriendsAdapter friendFriendsAdapter;
    private TextView tv_title;
    private ImageButton ibtn_back;
    private Long groupId;
    private List<String> itemId;
    private String grouperId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_group_members);
        initData();

        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText("群成员列表"+"("+members.size()+")");
        ibtn_back =(ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(this);

        ll_members = (ListView)findViewById(R.id.ll_members);
        if(friendFriendsAdapter==null){
            friendFriendsAdapter = new Friend_FriendsAdapter(GroupMembersActivity.this,members);
            friendFriendsAdapter.setData(itemId,grouperId);
        }else{
            friendFriendsAdapter.resetData(members);
        }
        friendFriendsAdapter.notifyDataSetChanged();
        ll_members.setAdapter(friendFriendsAdapter);
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
    }

    private void initData() {
        members = (List<FriendsBean>) getIntent().getSerializableExtra("members");
        groupId = getIntent().getLongExtra("groupId",0);
        itemId = (List<String>) getIntent().getSerializableExtra("itemId");
        grouperId = getIntent().getStringExtra("grouperId");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtn_back:
                finish();
            break;
        }
    }
}
