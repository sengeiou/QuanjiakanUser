package com.androidquanjiakan.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.setting.message.FreeInquiryActivity_AppendPatientProblem_new;
import com.androidquanjiakan.activity.setting.message.ProblemListActivity;
import com.androidquanjiakan.adapterholder.ConversationListHolder;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.BaseHttpResultEntity_List;
import com.androidquanjiakan.entity.FreeInquiryAnswerEntity;
import com.androidquanjiakan.entity.FreeInquiryProblemEntity;
import com.androidquanjiakan.entity.ProblemEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.entity_util.UnitUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.jmessage.android.uikit.chatting.ChatActivity;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;


/**
 * Created by Administrator on 2016/7/22 0022.
 */
public class FreeInquiryListAdapter extends BaseAdapter {

    private List<FreeInquiryProblemEntity> data;
    private Context context;

    private SimpleDateFormat sdf;

    private SimpleDateFormat timer;

    public FreeInquiryListAdapter(Context context, List<FreeInquiryProblemEntity> list) {
        data = list;
        this.context = context;
        sdf = new SimpleDateFormat("yyyy年MM月dd日");
        timer = new SimpleDateFormat("HH:mm");
    }

    public void resetData(List<FreeInquiryProblemEntity> list) {
        data = list;
    }

    public List<FreeInquiryProblemEntity> getData() {
        return data;
    }

    private final int MSG_LOADINFO = 1024;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_LOADINFO:
                    break;
            }
        }
    };

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ConversationListHolder holder;
        final FreeInquiryProblemEntity conversation = data.get(i);
        if (view == null) {
            holder = new ConversationListHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_conversation_list, null);
            holder.title_line = (LinearLayout) view.findViewById(R.id.title_line);
            holder.title = (TextView) view.findViewById(R.id.title);

            holder.last_msg = (TextView) view.findViewById(R.id.last_msg);
            holder.last_info = (TextView) view.findViewById(R.id.last_info);
            holder.conversation_time = (TextView) view.findViewById(R.id.conversation_time);
            holder.conversation_status_flag = view.findViewById(R.id.conversation_status_flag);
            holder.conversation_status = (TextView) view.findViewById(R.id.conversation_status);
            view.setTag(holder);
        } else {
            holder = (ConversationListHolder) view.getTag();
        }
        holder.title_line.setVisibility(View.GONE);

        /**
         * status
         */
        /**
         * TODO 后续此处对是否回复的逻辑需要进行修改
         */
        holder.conversation_status_flag.setVisibility(View.INVISIBLE);
        if ("0".equals(conversation.getIs_reply())) {
            holder.conversation_status.setText("未回复");
        } else if ("1".equals(conversation.getIs_reply())) {
            holder.conversation_status.setText("已回复");
        } else {
            holder.conversation_status.setText("未回复");
        }
//            holder.last_msg.setText(/*conversation.getProblemInfoEntity().get("").getAsString()*/"占位消息");
        holder.last_msg.setText(conversation.getTitle());
        holder.last_info.setText("");
        setTimeInfo(holder, conversation);

        /**
         * 设置点击响应
         */
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 限制追问是只能回复的
                 */

                if (/*"1".equals(conversation.getProblemInfoEntity().getIs_reply()) &&*/
                        (conversation.getDoctor_id() != null &&
                                !"0".endsWith(conversation.getDoctor_id()))) {
                } else {
                    Toast.makeText(context, "暂未回复!"/*+mConv.getProblemInfoEntity().toString()*/, Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(context, FreeInquiryActivity_AppendPatientProblem_new.class);
                intent.putExtra(FreeInquiryActivity_AppendPatientProblem_new.PARAMS_PROBLEM_ID, data.get(i).getId());
                intent.putExtra(FreeInquiryActivity_AppendPatientProblem_new.PARAMS_INFO, data.get(i));
                ((Activity) context).startActivityForResult(intent, ProblemListActivity.REQUEST_APPAND);
            }
        });
        return view;
    }

    int size = 0;
    public void setTimeInfo(ConversationListHolder holder, FreeInquiryProblemEntity timelong) {
        long timeLong = Long.parseLong(timelong.getCreated_time_ms());
        final Date time = new Date(timeLong);
        if (timelong.getPatientInfo().has("name")) {
            holder.last_info.setText(sdf.format(time) + "  | " + timelong.getPatientInfo().get("name").getAsString() + " | " +
                    timelong.getPatientInfo().get("sex").getAsString() + " | " +
                    timelong.getPatientInfo().get("age").getAsString() + " | 免费");
        } else if (timelong.getPatientInfo().has("Name")) {
            holder.last_info.setText(sdf.format(time) + "  | " + timelong.getPatientInfo().get("Name").getAsString() + " | " +
                    timelong.getPatientInfo().get("sex").getAsString() + " | " +
                    timelong.getPatientInfo().get("age").getAsString() + " | 免费");
        }

        if (60000 < (System.currentTimeMillis() - timeLong)) {
            holder.conversation_time.setText(timer.format(time));
        } else {
            holder.conversation_time.setText("刚刚");
        }
    }
}
