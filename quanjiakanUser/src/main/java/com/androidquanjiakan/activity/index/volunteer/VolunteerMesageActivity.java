package com.androidquanjiakan.activity.index.volunteer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.androidquanjiakan.activity.setting.message.ProblemListActivity;
import com.androidquanjiakan.adapter.VolunteerMesAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.VolunteerMesEntity;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.receiver.NotificationClickEventReceiver;
import com.quanjiakanuser.swipemenulistview.SwipeMenuListView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jmessage.android.uikit.chatting.ChatActivity;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;

/**
 * Created by Gin on 2016/11/23.
 */

public class VolunteerMesageActivity extends BaseActivity implements View.OnClickListener {

    private SwipeRefreshLayout fresh;
    private SwipeMenuListView listview;
    //private List<Conversation> list;
    private List<VolunteerMesEntity>datas;
    private List<VolunteerMesEntity> readList=new ArrayList<>();
    private List<VolunteerMesEntity> unReadList=new ArrayList<>();
    private Context context;
    private VolunteerMesAdapter adapter;
    private ImageButton mBack;
    private TextView mTitle;
    private TextView mMenu;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=VolunteerMesageActivity.this;
        setContentView(R.layout.activity_volunteer_message);
        initTitle();
        initView();
        NotificationClickEventReceiver.registerMessageCallback(ProblemListActivity.class.getName(), new NotificationClickEventReceiver.MessageReceiveCallBack() {

            @Override
            public void messageReceived(MessageEvent event) {
                // TODO Auto-generated method stub
                reloadData();
            }
        });


    }

    private void initTitle() {
        mBack=(ImageButton) findViewById(R.id.ibtn_back);
        mBack.setOnClickListener(this);
        mBack.setVisibility(View.VISIBLE);
        mTitle =(TextView) findViewById(R.id.tv_title);
        mTitle.setText("我的消息");
        mMenu= (TextView) findViewById(R.id.menu_text);
        mMenu.setVisibility(View.VISIBLE);
        mMenu.setText("管理");
        mMenu.setOnClickListener(this);
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

    private void initView() {
        datas=new ArrayList<>();
        fresh = (SwipeRefreshLayout) findViewById(R.id.fresh);
        fresh.setColorSchemeResources(R.color.holo_blue_light,R.color.holo_green_light,R.color.holo_orange_light,R.color.holo_red_light);

        listview = (SwipeMenuListView) findViewById(R.id.listview);
        datas =new ArrayList<>();
        adapter = new VolunteerMesAdapter(context, datas);
        listview.setAdapter(adapter);
        fresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                datas.get(i).setRead(true);
                unReadList.remove(i);
                readList.add(adapter.getDatas().get(i));
                adapter.notifyDataSetChanged();
                //跳转到聊天
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(ChatActivity.TARGET_ID,adapter.getDatas().get(i).getTargetId());
                intent.putExtra(ChatActivity.TARGET_APP_KEY,adapter.getDatas().get(i).getTargetAppKey());
                startActivity(intent);

            }
        });
    }

    private void loadData() {
        if(JMessageClient.getMyInfo()!=null) {
            datas.clear();
            List<Conversation> conversationList = JMessageClient.getConversationList();
            //final List<VolunteerMesEntity>volunteers =new ArrayList<>();
            if(conversationList==null) {
                conversationList=new ArrayList<>();
            }
            for (int i=conversationList.size()-1;i>=0;i--){
                Conversation conversation = conversationList.get(i);
                if(conversation.getLatestMessage()==null) {
                    conversationList.remove(conversation);
                }

                final String targetId = conversation.getTargetId();
                final String targetAppKey = conversation.getTargetAppKey();
                HashMap<String, String> params = new HashMap<>();
                MyHandler.putTask(VolunteerMesageActivity.this,new Task(new HttpResponseInterface() {
                    @Override
                    public void handMsg(String val) {
                        HttpResponseResult result = new HttpResponseResult(val);
                        if(result.isResultOk()) {
                            try {
                                VolunteerMesEntity volunteerMesEntity = new VolunteerMesEntity();
                                JSONObject jsonObject = new JSONObject(val);
                                String name = jsonObject.getString("name");
                                String picture = jsonObject.getString("picture");
                                String createtime = jsonObject.getString("createtime");
                                volunteerMesEntity.setName(name);
                                volunteerMesEntity.setPicture(picture);
                                volunteerMesEntity.setCreatetime(createtime);
                                volunteerMesEntity.setRead(false);//默认false
                                volunteerMesEntity.setTargetId(targetId);
                                volunteerMesEntity.setTargetAppKey(targetAppKey);

                                datas.add(volunteerMesEntity);
                                unReadList.add(volunteerMesEntity);
                                adapter.setDatas(datas);
                                adapter.notifyDataSetChanged();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, HttpUrls.getVolunteerMes(targetId),params,Task.TYPE_GET_STRING_NOCACHE,null));

            }

        }





    }

    private void reloadData() {
        if(JMessageClient.getMyInfo()!=null) {
            List<Conversation> conversationList = JMessageClient.getConversationList();
           // final List<VolunteerMesEntity>list =new ArrayList<>();
            if(conversationList==null) {
                conversationList=new ArrayList<>();
            }
            for (int i=conversationList.size()-1;i>=0;i--){
                Conversation conversation = conversationList.get(i);
                if(conversation.getLatestMessage()==null) {
                    conversationList.remove(conversation);
                }

                final String targetId = conversation.getTargetId();
                final String targetAppKey = conversation.getTargetAppKey();
                HashMap<String, String> params = new HashMap<>();
                MyHandler.putTask(VolunteerMesageActivity.this,new Task(new HttpResponseInterface() {
                    @Override
                    public void handMsg(String val) {
                        HttpResponseResult result = new HttpResponseResult(val);
                        if(result.isResultOk()) {
                            try {
                                VolunteerMesEntity volunteerMesEntity = new VolunteerMesEntity();
                                JSONObject jsonObject = new JSONObject(val);
                                String name = jsonObject.getString("name");
                                String picture = jsonObject.getString("picture");
                                String createtime = jsonObject.getString("createtime");
                                volunteerMesEntity.setName(name);
                                volunteerMesEntity.setPicture(picture);
                                volunteerMesEntity.setCreatetime(createtime);
                                volunteerMesEntity.setRead(false);
                                volunteerMesEntity.setTargetId(targetId);
                                volunteerMesEntity.setTargetAppKey(targetAppKey);

                                datas.add(volunteerMesEntity);
                                unReadList.add(volunteerMesEntity);
                                adapter.setDatas(datas);
                                adapter.notifyDataSetChanged();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, HttpUrls.getVolunteerMes(targetId),params,Task.TYPE_GET_STRING_NOCACHE,null));

            }

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.menu_text:
                if(popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }else{
                    showDevicesInfoWindow(mMenu);
                }
                break;
        }

    }

    private void showDevicesInfoWindow(View root) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog__volunteer_menu,null);
        final TextView unread = (TextView) view.findViewById(R.id.unread);
        final TextView read = (TextView) view.findViewById(R.id.read);
        //未阅读
        unread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                //未阅读列表
                showUnReadList();


            }
        });

        //已阅读
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                showReadList();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
//		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.selecter_common_dialog_bg));
        popupWindow.showAsDropDown(root);//设置在某个指定View的下方

    }

    private void showReadList() {
        datas.clear();
        adapter.setDatas(readList);
        adapter.notifyDataSetChanged();
    }

    private void showUnReadList() {
        datas.clear();
        adapter.setDatas(unReadList);
        adapter.notifyDataSetChanged();
    }
}
