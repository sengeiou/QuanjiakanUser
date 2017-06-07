package com.androidquanjiakan.activity.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;


import android.view.View;

import android.widget.AdapterView;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.androidquanjiakan.adapter.GroupChatListAdapter;
import com.androidquanjiakan.base.BaseActivity;


import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.entity.GroupChatListEntity;

import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jmessage.android.uikit.chatting.ChatActivity;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;


public class GroupChatListActivity extends BaseActivity {


    private Context context;
    private TextView tv_title;
    private ImageView iv_nomes;
    private TextView tv_nomes;

    private ImageView iv_pic;
    private ListView listview;
    private List<GroupChatListEntity> groupChatListEntities;
    private GroupChatListAdapter groupChatListAdapter;
    private List<String> gidList;
    private RelativeLayout rl_all;


    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==2) {
                final List<GroupChatListEntity> groupChatListEntities = (List<GroupChatListEntity>) msg.obj;
                if(groupChatListAdapter==null) {
                    groupChatListAdapter=new GroupChatListAdapter(context,groupChatListEntities);
                }else {
                    groupChatListAdapter.resetData(groupChatListEntities);
                }
                groupChatListAdapter.notifyDataSetChanged();
                listview.setAdapter(groupChatListAdapter);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        GroupChatListEntity groupChatListEntity = groupChatListAdapter.getData().get((int)l);
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(ChatActivity.GROUP_ID,groupChatListEntity.getGid());
                        intent.putExtra("biaozhi","group");
                        startActivity(intent);
                    }
                });

            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=GroupChatListActivity.this;
        setContentView(R.layout.layout_group_chat_list);
        initView();
        initData();

    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("消息");
        iv_nomes = (ImageView) findViewById(R.id.iv_nomes);
        tv_nomes = (TextView) findViewById(R.id.tv_nomes);

        rl_all = (RelativeLayout) findViewById(R.id.rl_all);

        iv_pic = (ImageView) findViewById(R.id.iv_pic);
        listview = (ListView) findViewById(R.id.listview);

        JMessageLogin();


    }

    protected void JMessageLogin(){
        JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX+QuanjiakanSetting.getInstance().getUserId()+"", CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {

            @Override
            public void gotResult(int arg0, String arg1) {
            }
        });
    }

    private void initData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("aid", QuanjiakanSetting.getInstance().getUserId()+"");
        //请求网络群组列表
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if (null==val||"".equals(val)){
                    Toast.makeText(GroupChatListActivity.this, getString(R.string.error_net_connection), Toast.LENGTH_SHORT).show();
                }else{
                    if(val.equals("[]")) {

                        //没有消息
                        Toast.makeText(GroupChatListActivity.this, getString(R.string.hint_group_no_bind_group), Toast.LENGTH_SHORT).show();
                        //加载没有消息布局
                        getNoChatList();
                        return;
                    }else {
                        //加载消息布局
                        getHasChatList();
                        //获取数据
                        try {
                            gidList=new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(val);
                            for (int i=0;i<jsonArray.length();i++){
                                String gid = jsonArray.getJSONObject(i).getString("gid");
                                gidList.add(gid);
                            }
                            Message msg = new Message();
                            msg.what=1;
                            msg.obj=gidList;
                            handler.sendMessage(msg);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }, HttpUrls.getGroupChatList(),params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    final List<String> gidList = (List<String>) msg.obj;
                    groupChatListEntities=new ArrayList<>();
                    for (int i=0;i<gidList.size();i++){
                        final GroupChatListEntity groupChatListEntity=new GroupChatListEntity();
                        groupChatListEntity.setGid(Long.valueOf(gidList.get(i)));
                        JMessageClient.getGroupInfo(Long.valueOf(gidList.get(i)), new GetGroupInfoCallback() {
                            @Override
                            public void gotResult(int i, String s, GroupInfo groupInfo) {
                                if(i==0) {
                                    String groupName = groupInfo.getGroupName();
                                    List<UserInfo> groupMembers = groupInfo.getGroupMembers();
                                    int groupSize = groupMembers.size();

                                    groupChatListEntity.setName(groupName);
                                    groupChatListEntity.setNumber(groupSize);
                                    groupChatListEntities.add(groupChatListEntity);
                                    groupChatListAdapter.notifyDataSetChanged();

                                }
                            }
                        });
                    }

                    Message message = new Message();
                    message.what=2;
                    message.obj=groupChatListEntities;
                    mHandler.sendMessage(message);

            }

        }
    };

    //显示正确加载界面
    private void getHasChatList() {
        iv_nomes.setVisibility(View.GONE);
        tv_nomes.setVisibility(View.GONE);
        rl_all.setVisibility(View.VISIBLE);

    }
    //显示错误加载界面
    private void getNoChatList(){
        rl_all.setVisibility(View.GONE);
        iv_nomes.setVisibility(View.VISIBLE);
        tv_nomes.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }
}
