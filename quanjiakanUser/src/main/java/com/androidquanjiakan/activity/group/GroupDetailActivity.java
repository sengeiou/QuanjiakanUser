package com.androidquanjiakan.activity.group;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquanjiakan.adapter.GroupMemberGridAdapter;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.entity.GroupInfoEntity;
import com.androidquanjiakan.entity.GroupMemberEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.interfaces.INetRequestCodeInterface;
import com.androidquanjiakan.interfaces.IRemoveGroupMemberInterface;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpClientUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.util.SignatureUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("NewApi")
public class GroupDetailActivity extends Activity implements OnClickListener {


    private final int TYPE_LOAD_GROUP_INFO = 100;
    private final int TYPE_CHANGE_GROUP_NAME = 101;
    private long mGroupId;
    private int responseResultCode;
    private boolean flag = false;
    private boolean removeable = false;
    private String changeGroupName;

    private TextView title;
    private ImageButton ibtn_back;
    private TextView dialog_content;
    private GridView grid;
    private TextView group_number;
    private TextView group_name_value;
    private ImageView to_top_status;
    private TextView record_history;
    private TextView remove_record_history;
    private TextView exit;
    private LinearLayout group_name_line;
    private Dialog changeNameDialog;
    private View contentView;
    private TextView dialog_title;
    private TextView dialog_confirm;
    private TextView dialog_cancel;
    private EditText dialog_group_name;

    private GroupMemberGridAdapter adapter;
    private List<GroupMemberEntity> mList;
    private GroupInfoEntity groupInfo;

    private IRemoveGroupMemberInterface removeGroupMemberInterface = new IRemoveGroupMemberInterface() {
        @Override
        public void removeGroupMember(String memberID) {
            /**
             * 删除指定的ID的成员
             */
            LogUtil.e("memberID:" + memberID);
            removeGroupMemberDialog(memberID);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_group_detail);
        mGroupId = this.getIntent().getLongExtra(BaseConstants.PARAMS_GROUPID, 0);

        initView();
        new LoadGroupInfo(TYPE_LOAD_GROUP_INFO).execute("");
    }

    public void initView() {
        title = (TextView) findViewById(R.id.tv_title);
        title.setText("群聊信息");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        exit = (TextView) findViewById(R.id.exit);
        exit.setOnClickListener(this);

        grid = (GridView) findViewById(R.id.grid);
        mList = new ArrayList<GroupMemberEntity>();
        adapter = new GroupMemberGridAdapter(this, mList, removeable, removeGroupMemberInterface);
        grid.setAdapter(adapter);

        group_number = (TextView) findViewById(R.id.group_number);
        group_name_line = (LinearLayout) findViewById(R.id.group_name_line);
        group_name_line.setOnClickListener(this);
        group_name_value = (TextView) findViewById(R.id.group_name_value);
        to_top_status = (ImageView) findViewById(R.id.to_top_status);
        if (flag) {
            to_top_status.setBackgroundResource(R.drawable.group_top_open);
        } else {
            to_top_status.setBackgroundResource(R.drawable.group_top_close);
        }
        to_top_status.setOnClickListener(this);
        record_history = (TextView) findViewById(R.id.record_history);
        remove_record_history = (TextView) findViewById(R.id.remove_record_history);
    }

    public void switchToTopStatus() {
        if (!flag) {
            flag = true;
            to_top_status.setBackgroundResource(R.drawable.group_top_open);
        } else {
            flag = false;
            to_top_status.setBackgroundResource(R.drawable.group_top_close);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.ibtn_back:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.to_top_status:
                switchToTopStatus();
                break;
            case R.id.group_name_line:
                changeGroupName();
                break;
            case R.id.exit:
                exitConfirmDialog();
                break;
            default:
                break;
        }
    }

    public void changeGroupName() {
        changeNameDialog = QuanjiakanDialog.getInstance().getDialog(this);
        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_change_group_name, null);
        dialog_title = (TextView) contentView.findViewById(R.id.tv_title);
        dialog_group_name = (EditText) contentView.findViewById(R.id.tv_content);
        dialog_confirm = (TextView) contentView.findViewById(R.id.btn_confirm);
        dialog_cancel = (TextView) contentView.findViewById(R.id.btn_cancel);
        dialog_title.setText("修改群组名称");
        dialog_group_name.setText(groupInfo.getName());
        dialog_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeGroupName = dialog_group_name.getText().toString().trim();
                if (changeGroupName != null && changeGroupName.length() > 0 && !groupInfo.getName().endsWith(changeGroupName)) {
                    new LoadGroupInfo(TYPE_CHANGE_GROUP_NAME).execute("");
                } else {
                    if (groupInfo != null && groupInfo.getName() != null && groupInfo.getName().endsWith(changeGroupName)) {
                        BaseApplication.getInstances().toast(GroupDetailActivity.this,"请修改为不同的名称!");
                    } else {
                        BaseApplication.getInstances().toast(GroupDetailActivity.this,"请填入想要的名称!");
                    }
                }
                changeNameDialog.dismiss();
            }
        });
        dialog_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNameDialog.dismiss();
            }
        });
        WindowManager.LayoutParams lp = changeNameDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(GroupDetailActivity.this, 300);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        changeNameDialog.setContentView(contentView, lp);
        changeNameDialog.setCanceledOnTouchOutside(false);
        changeNameDialog.show();
    }



    public void exitConfirmDialog() {
        changeNameDialog = QuanjiakanDialog.getInstance().getDialog(this);
        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_common_confirm, null);
        dialog_title = (TextView) contentView.findViewById(R.id.tv_title);
        dialog_content = (TextView) contentView.findViewById(R.id.tv_content);
        dialog_confirm = (TextView) contentView.findViewById(R.id.btn_confirm);
        dialog_cancel = (TextView) contentView.findViewById(R.id.btn_cancel);
        dialog_title.setText("群组退出提示");
        dialog_content.setText("确认退出群组：" + groupInfo.getName() + "?");
        dialog_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//				new LoadGroupInfo(TYPE_CHANGE_GROUP_NAME).execute("");
                changeNameDialog.dismiss();
                BaseApplication.getInstances().toast(GroupDetailActivity.this,"执行退出的请求!");
            }
        });
        dialog_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNameDialog.dismiss();
            }
        });
        WindowManager.LayoutParams lp = changeNameDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(GroupDetailActivity.this, 300);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        changeNameDialog.setContentView(contentView, lp);
        changeNameDialog.setCanceledOnTouchOutside(false);
        changeNameDialog.show();
    }

    public void removeGroupMemberDialog(String member) {
        changeNameDialog = QuanjiakanDialog.getInstance().getDialog(this);
        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_common_confirm, null);
        dialog_title = (TextView) contentView.findViewById(R.id.tv_title);
        dialog_content = (TextView) contentView.findViewById(R.id.tv_content);
        dialog_confirm = (TextView) contentView.findViewById(R.id.btn_confirm);
        dialog_cancel = (TextView) contentView.findViewById(R.id.btn_cancel);
        dialog_title.setText("群组踢人提示");
        dialog_content.setText("确认将 " + member + " 移出群组 ?");
        dialog_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//				new LoadGroupInfo(TYPE_CHANGE_GROUP_NAME).execute("");
                changeNameDialog.dismiss();
                BaseApplication.getInstances().toast(GroupDetailActivity.this,"踢人的请求!");
            }
        });
        dialog_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNameDialog.dismiss();
            }
        });
        WindowManager.LayoutParams lp = changeNameDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(GroupDetailActivity.this, 300);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        changeNameDialog.setContentView(contentView, lp);
        changeNameDialog.setCanceledOnTouchOutside(false);
        changeNameDialog.show();
    }

    protected void toggleKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    class LoadGroupInfo extends AsyncTask<String, Void, Void> {
        Dialog dialog;
        String result;
        int type;
        public LoadGroupInfo(int typeValue) {
            this.type = typeValue;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (TYPE_LOAD_GROUP_INFO == type) {
                dialog = QuanjiakanDialog.getInstance().getDialog(GroupDetailActivity.this, "正在获取群组信息...");
            } else if (TYPE_CHANGE_GROUP_NAME == type) {
                dialog = QuanjiakanDialog.getInstance().getDialog(GroupDetailActivity.this, "正在修改群组信息...");
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            String base64 = SignatureUtil.toBase64(BaseConstants.jpush_patient_appkey + ":" + BaseConstants.jpush_patient_masterSecret);
            Map<String, String> params = new HashMap<String, String>();
            params.put("Content-Type", "application/json;charset=utf-8");
            params.put("Authorization", "Basic " + base64);

            if (TYPE_LOAD_GROUP_INFO == type) {
                result = HttpClientUtil.getRequest(params, "https://api.im.jpush.cn/v1/groups/", mGroupId + "");
                LogUtil.e("群组信息:" + result);
                if (result != null && result.startsWith("{") && result.endsWith("}")) {
                    groupInfo = (GroupInfoEntity) SerialUtil.jsonToObject(result, new TypeToken<GroupInfoEntity>() {
                    }.getType());
                } else {
                    return null;
                }
                result = HttpClientUtil.getRequest(params, "https://api.im.jpush.cn/v1/groups/", mGroupId + "/members");
                if (result != null && result.startsWith("[") && result.endsWith("]")) {
                    LogUtil.e("群组信息:" + result);
                    List<GroupMemberEntity> list = (List<GroupMemberEntity>) SerialUtil.jsonToObject(result, new TypeToken<List<GroupMemberEntity>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        GroupMemberEntity addFlag = new GroupMemberEntity();
                        addFlag.setUsername("ADD");
                        if (list != null && list.size() > 0) {
                            adapter.getData().addAll(list);
                        }
                        adapter.getData().add(addFlag);
                        for (GroupMemberEntity entity : list) {
                            if (entity.getUsername().endsWith(BaseApplication.getInstances().getUser_id())) {
                                if (entity.getFlag() == 1) {
                                    removeable = true;
                                } else {
                                    removeable = false;
                                }
                                break;
                            }
                        }
                        adapter.resetRemoveTag(removeable);
                    }
                } else {
                    return null;
                }

            } else if (TYPE_CHANGE_GROUP_NAME == type) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", changeGroupName);
                    String result = HttpClientUtil.putRequest(params, "https://api.im.jpush.cn/v1/groups/", groupInfo.getGid() + "", jsonObject.toString(), new INetRequestCodeInterface() {
                        @Override
                        public void getResponseCode(int responseCode) {
                            responseResultCode = responseCode;
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if (TYPE_LOAD_GROUP_INFO == type && result != null && result.startsWith("[")) {
                group_number.setText("全部群成员(" + (adapter.getData().size() - 1) + ")");
                group_name_value.setText(groupInfo.getName());
                adapter.resetRemoveTag(removeable);
                adapter.notifyDataSetChanged();
            } else if (TYPE_CHANGE_GROUP_NAME == type) {
                if (responseResultCode == HttpClientUtil.NO_CONTENT || responseResultCode == HttpClientUtil.CREATED || responseResultCode == HttpURLConnection.HTTP_OK) {
                    groupInfo.setName(changeGroupName);
                    group_name_value.setText(changeGroupName);
                    BaseApplication.getInstances().toast(GroupDetailActivity.this,"修改群组信息成功!");
                } else {
                    BaseApplication.getInstances().toast(GroupDetailActivity.this,"修改群组信息失败!");
                }

            }

        }

    }

}