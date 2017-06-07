package com.androidquanjiakan.activity.group;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.FriendsBean;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupMembersCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by Gin on 2016/8/19.
 */
public class GroupActivity extends BaseActivity implements View.OnClickListener {

    private String groupName;
    private long groupId;
    private TextView tv_groupName;
    private Context context;
    private TextView tv_groupId;
    private LinearLayout layout_members;
    private LinearLayout updataGroup;
    private TextView tv_clean;
    private String groupDescription;
    private TextView tv_groupDesc;
    private TextView tv_title;
    private ImageButton ibtn_back;
    private String groupOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         context= GroupActivity.this;
        setContentView(R.layout.layout_group_info);
        initData();
        initView();

    }

    private List<FriendsBean> friendList=new ArrayList<>();
    private List<String> itemId=new ArrayList<>();
    private void initData() {
        groupName = getIntent().getStringExtra("groupName");
        groupId = getIntent().getLongExtra("groupId", 0);
        groupDescription = getIntent().getStringExtra("groupDescription");
        groupOwner = getIntent().getStringExtra("groupOwner");//创建群组的群主id


        JMessageClient.getGroupMembers(groupId, new GetGroupMembersCallback() {

            @Override
            public void gotResult(int i, String s, final List<UserInfo> list) {
                for (int t=0;t<list.size();t++){
                    FriendsBean friendsBean = new FriendsBean();
                    UserInfo userInfo = list.get(t);
                    String nickname = userInfo.getNickname();
                    friendsBean.setFriendName(nickname);
                    itemId.add(list.get(t).getUserName());
                    friendList.add(friendsBean);
                }

            }
        });

    }

    private void initView() {

        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.title_group_info));
        ibtn_back =(ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(this);


        tv_groupName = (TextView)findViewById(R.id.tv_groupName);
        tv_groupName.setText(groupName);
        tv_groupId = (TextView)findViewById(R.id.tv_groupId);
        tv_groupId.setText(""+groupId);

        tv_groupDesc = (TextView)findViewById(R.id.tv_groupDesc);
        tv_groupDesc.setText(groupDescription);


        layout_members = (LinearLayout)findViewById(R.id.layout_members);
        layout_members.setOnClickListener(this);

        updataGroup = (LinearLayout) findViewById(R.id.updataGroup);
        updataGroup.setOnClickListener(this);


        tv_clean = (TextView)findViewById(R.id.tv_clean);
        tv_clean.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //查看群成员
            case R.id.layout_members:
                Intent intent = new Intent(context, GroupMembersActivity.class);
                intent.putExtra("members",(Serializable)friendList);
                intent.putExtra("groupId",groupId);
                intent.putExtra("itemId",(Serializable)itemId);
                intent.putExtra("grouperId",groupOwner);
                startActivity(intent);
                break;

            //修改资料
            case R.id.updataGroup:
                /*if(groupOwner.equals(userName)) {
                    showUpdateDialog();
                }else {
                    Toast.makeText(GroupActivity.this, "你不是群主,不能修改群资料", Toast.LENGTH_SHORT).show();
                }*/
                showUpdateDialog();
                break;

            //清空聊天
            case R.id.tv_clean:
                boolean b = JMessageClient.deleteGroupConversation(groupId);
                if(b) {
                    Toast.makeText(GroupActivity.this, getString(R.string.hint_group_clear_msg), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(GroupActivity.this, getString(R.string.hint_group_clear_msg_fail), Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            case R.id.ibtn_back:
                finish();
                break;
        }

    }

    private void showUpdateDialog() {
        final Dialog dialog = QuanjiakanDialog.getInstance().getDialog(context);
        final View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_creat_group, null);
        TextView miaoshu = (TextView) inflate.findViewById(R.id.tv_miaoshu);
        miaoshu.setText(getString(R.string.group_modify_info));
        TextView biaoti = (TextView) inflate.findViewById(R.id.tv_biaoti);
        biaoti.setText(getString(R.string.group_modify_name));
        TextView desc = (TextView) inflate.findViewById(R.id.tv_desc);
        desc.setText(getString(R.string.group_modify_desc));

        final EditText et_group_name = (EditText) inflate.findViewById(R.id.et_group_name);
        final EditText et_group_desc = (EditText) inflate.findViewById(R.id.et_group_desc);


        inflate.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        inflate.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               JMessageClient.updateGroupName(groupId, et_group_name.getText().toString().trim(), new BasicCallback() {
                   @Override
                   public void gotResult(int i, String s) {
                       Toast.makeText(GroupActivity.this, ""+i, Toast.LENGTH_SHORT).show();
                       if(i==0) {
                           tv_groupName.setText(et_group_name.getText().toString().trim());
                           JMessageClient.updateGroupDescription(groupId, et_group_desc.getText().toString().trim(), new BasicCallback() {
                               @Override
                               public void gotResult(int i, String s) {
                                   if(i==0) {
                                       tv_groupDesc.setText(et_group_desc.getText().toString().trim());
                                       dialog.dismiss();
                                   }
                               }
                           });
                       }
                   }
               });
            }
        });

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = context.getResources().getDisplayMetrics().widthPixels * 3 / 4;
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setContentView(inflate,lp);
        dialog.show();

    }
}
