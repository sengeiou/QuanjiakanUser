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
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
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
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;


/**
 * Created by Administrator on 2016/7/22 0022.
 */
public class ConversionListAdapter extends BaseAdapter {

    private List<ProblemEntity> data;
    private Context context;

    private SimpleDateFormat sdf;

    private SimpleDateFormat timer;

    public ConversionListAdapter(Context context,List<ProblemEntity> list){
        data = list;
        this.context = context;
        sdf = new SimpleDateFormat("yyyy年MM月dd日");
        timer = new SimpleDateFormat("HH:mm");
    }

    public void resetData(List<ProblemEntity> list){
        data = list;
    }

    public List<ProblemEntity> getData(){
        return data;
    }
    private final int MSG_LOADINFO = 1024;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case MSG_LOADINFO:
                    break;
            }
        }
    };

    @Override
    public int getCount() {
        if(data!=null){
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
        final ProblemEntity conversation = data.get(i);
        if(view==null){
            holder = new ConversationListHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_conversation_list,null);
            holder.title_line = (LinearLayout) view.findViewById(R.id.title_line);
            holder.title = (TextView) view.findViewById(R.id.title);

            holder.last_msg = (TextView) view.findViewById(R.id.last_msg);
            holder.last_info = (TextView) view.findViewById(R.id.last_info);
            holder.conversation_time = (TextView) view.findViewById(R.id.conversation_time);
            holder.conversation_status_flag = view.findViewById(R.id.conversation_status_flag);
            holder.conversation_status = (TextView) view.findViewById(R.id.conversation_status);
            view.setTag(holder);
        }else {
            holder = (ConversationListHolder) view.getTag();
        }
        if(i==0){
            holder.title_line.setVisibility(View.VISIBLE);
            if(data.get(0).getProblemInfoEntity()!=null){
                //免费问诊
                holder.title.setText("在线问诊");
            }else{
                holder.title.setText("约医护");
            }
        }else{
            if((data.get(i).getProblemInfoEntity()!=null && data.get(i-1).getProblemInfoEntity()!=null)||
                    (data.get(i).getmConversation()!=null && data.get(i-1).getmConversation()!=null)){
                holder.title_line.setVisibility(View.GONE);
            }else if( (data.get(i).getProblemInfoEntity()!=null && data.get(i-1).getmConversation()!=null) ||
                    (data.get(i).getmConversation()!=null && data.get(i-1).getProblemInfoEntity()!=null)){
                holder.title_line.setVisibility(View.VISIBLE);
                if(data.get(i).getProblemInfoEntity()!=null){
                    //免费问诊
                    holder.title.setText("在线问诊");
                }else{
                    holder.title.setText("约医护");
                }
            }else{
                holder.title_line.setVisibility(View.GONE);
            }
        }


        /**
         * status
         */
        if(conversation.getmConversation()!=null){
            if(conversation.getmConversation().getUnReadMsgCnt()>0){
                setReadStatus(holder,FLAG_UNREAD,conversation.getmConversation());
            }else{
                setReadStatus(holder,FLAG_READ,conversation.getmConversation());
            }
            /**
             * message
             */
            final Message msg = conversation.getmConversation().getLatestMessage();
            if(msg!=null && ContentType.text==msg.getContentType()){
                holder.last_msg.setText(((TextContent)msg.getContent()).getText());
            }else if(msg!=null && ContentType.eventNotification==msg.getContentType()){
                holder.last_msg.setText(((EventNotificationContent)msg.getContent()).getEventText());
            }else if(msg!=null && ContentType.image==msg.getContentType()){
                holder.last_msg.setText("[图片]");
            }else if(msg!=null && ContentType.voice==msg.getContentType()){
                holder.last_msg.setText("[语音]");
            }else if(msg!=null){
                holder.last_msg.setText("未知消息类型!");
            }else{
                holder.last_msg.setText("暂未发送或接收过消息!");
            }
            /**
             * info
             */
            setTimeInfo(holder,msg,conversation);
        }else if(conversation.getProblemInfoEntity()!=null){
            /**
             * TODO 后续此处对是否回复的逻辑需要进行修改
             */
            holder.conversation_status_flag.setVisibility(View.INVISIBLE);
            if("0".equals(conversation.getProblemInfoEntity().getIs_reply())){
                holder.conversation_status.setText("未回复");
            }else if("1".equals(conversation.getProblemInfoEntity().getIs_reply())){
                holder.conversation_status.setText("已回复");
            }else{
                holder.conversation_status.setText("未回复");
            }
//            holder.last_msg.setText(/*conversation.getProblemInfoEntity().get("").getAsString()*/"占位消息");
            holder.last_msg.setText(conversation.getProblemInfoEntity().getTitle());
            holder.last_info.setText("");
            setTimeInfo(holder,conversation.getProblemInfoEntity());
        }

        /**
         * 设置点击响应
         */
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conversation.getmConversation()!=null){
                    String targetId = conversation.getmConversation().getTargetId();
                    LogUtil.e("doctorid--------------"+conversation.getmConversation().getTargetId());
                    checkStatus(targetId.replace("Doc",""),conversation);
                }else{
                    /**
                     * 限制追问是只能回复的
                     */

                    if(/*"1".equals(conversation.getProblemInfoEntity().getIs_reply()) &&*/
                            (conversation.getProblemInfoEntity().getDoctor_id()!=null &&
                                    !"0".endsWith(conversation.getProblemInfoEntity().getDoctor_id()))){
                    }else{
                        Toast.makeText(context, "暂未回复!"/*+mConv.getProblemInfoEntity().toString()*/, Toast.LENGTH_SHORT).show();
                    }

                    Intent intent = new Intent(context, FreeInquiryActivity_AppendPatientProblem_new.class);
                    intent.putExtra(FreeInquiryActivity_AppendPatientProblem_new.PARAMS_PROBLEM_ID,data.get(i).getProblemInfoEntity().getId());
                    intent.putExtra(FreeInquiryActivity_AppendPatientProblem_new.PARAMS_INFO,data.get(i).getProblemInfoEntity());
                    ((Activity)context).startActivityForResult(intent,ProblemListActivity.REQUEST_APPAND);


                }
            }
        });
        return view;
    }

    BaseHttpResultEntity_List<FreeInquiryAnswerEntity> answerEntityBaseHttpResultEntity_list;
    int size = 0;
    public void loadFreeInquiryData(final int position,String problemID,final String content){
        MyHandler.putTask((Activity) context,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
                    answerEntityBaseHttpResultEntity_list = (BaseHttpResultEntity_List<FreeInquiryAnswerEntity>)
                            SerialUtil.jsonToObject(val,new TypeToken<BaseHttpResultEntity_List<FreeInquiryAnswerEntity>>(){}.getType());
                    /**
                     * 情况一：仅显示最后一条回复数据
                     */
//                    size = answerEntityBaseHttpResultEntity_list.getRows().size()-1;
//                    if(answerEntityBaseHttpResultEntity_list!=null && answerEntityBaseHttpResultEntity_list.getRows()!=null &&
//                            answerEntityBaseHttpResultEntity_list.getRows().size()>0 &&
//                            answerEntityBaseHttpResultEntity_list.getRows().get(size).getContent()!=null &&
//                            answerEntityBaseHttpResultEntity_list.getRows().get(size).getContent().length()>0 &&
//                            !"null".equals(answerEntityBaseHttpResultEntity_list.getRows().get(size).getContent().toLowerCase())){
//
//                        showFreeInquiryDialog(position,content,answerEntityBaseHttpResultEntity_list.getRows().
//                                get(size).getContentString().//第几个回复
//                                get(0).getAsJsonObject().get("text").getAsString());//每个回复内只有一个回复文本
//                    }else{
////                        showFreeInquiryDialog(content,null);
//                        Toast.makeText(context, "暂未回复!", Toast.LENGTH_LONG).show();
//                    }
                    /**
                     * 情况二：所有回复的数据都显示出来
                     */
                    if(answerEntityBaseHttpResultEntity_list!=null && answerEntityBaseHttpResultEntity_list.getRows()!=null &&
                            answerEntityBaseHttpResultEntity_list.getRows().size()>0){
                        //单个问题--多个回答
//                        showFreeInquiryDialog(position,content,answerEntityBaseHttpResultEntity_list.getRows());//回复内包含所有回复文字信息
                        //追问，回答
                        showFreeInquiryDialog_QAList(position,content,answerEntityBaseHttpResultEntity_list.getRows());//回复内包含所有回复文字信息
                    }else{
//                        showFreeInquiryDialog(content,null);
                        Toast.makeText(context, "暂未回复!", Toast.LENGTH_LONG).show();
                    }
                }else{
//                    showFreeInquiryDialog(content,null);
                    Toast.makeText(context, "暂未回复!", Toast.LENGTH_LONG).show();
                }
            }
        }, HttpUrls.getGetChunyuProblemReply()+"&problem_id="+problemID,null,Task.TYPE_GET_STRING_NOCACHE,QuanjiakanDialog.getInstance().getDialog(context)));
    }


    private Dialog dialog;
    private TextView content_question;
    private TextView content_answer;
    private TextView appand;

    String answer = "";

    private ScrollView content;
    private LinearLayout info_container;
    public void showFreeInquiryDialog_QAList(final int position,final String question,final List<FreeInquiryAnswerEntity> answerList){
        dialog = QuanjiakanDialog.getInstance().getDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_inquiry_detail_list,null);

        content = (ScrollView) view.findViewById(R.id.content);
        info_container = (LinearLayout) view.findViewById(R.id.info_container);
        info_container.removeAllViews();
        appand = (TextView) view.findViewById(R.id.appand);

        View items = LayoutInflater.from(context).inflate(R.layout.dialog_inquiry_detail_list_item,null);
        TextView types = (TextView) items.findViewById(R.id.type);
        TextView qa_contents = (TextView) items.findViewById(R.id.content);
        types.setTextColor(context.getResources().getColor(R.color.color_title_green));
        types.setText("问");
        types.setBackgroundResource(R.drawable.selecter_hollow_green);
        qa_contents.setText(answerList.get(0).getTitle());
        info_container.addView(items);

        for(int i = 0;i<answerList.size();i++){
            FreeInquiryAnswerEntity temp = answerList.get(i);
            View item = LayoutInflater.from(context).inflate(R.layout.dialog_inquiry_detail_list_item,null);
            TextView type = (TextView) item.findViewById(R.id.type);
            TextView qa_content = (TextView) item.findViewById(R.id.content);
            if("0".equals(temp.getSponsor())){//医生  问
                type.setTextColor(context.getResources().getColor(R.color.white));
                type.setText("答");
                type.setBackgroundResource(R.drawable.selecter_inquiry_green);
                answer = temp.getContentString().get(0).getAsJsonObject().get("text").getAsString();
            }else{//病人  答
                type.setTextColor(context.getResources().getColor(R.color.color_title_green));
                type.setText("问");
                type.setBackgroundResource(R.drawable.selecter_hollow_green);
            }
            qa_content.setText(temp.getContentString().get(0).getAsJsonObject().get("text").getAsString());
            info_container.addView(item);
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) content.getLayoutParams();
//        if(answerList.size()>3){
//            //设定固定高度
//            layoutParams.height = UnitUtil.dp_To_px(context,200);
//            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
//        }else{
//            //根据内容适配高度
//            layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
//        }

        layoutParams.height = UnitUtil.dp_To_px(context,200);
        layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        content.setLayoutParams(layoutParams);

        appand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FreeInquiryActivity_AppendPatientProblem_new.class);
                intent.putExtra(FreeInquiryActivity_AppendPatientProblem_new.PARAMS_PROBLEM_ID,data.get(position).getProblemInfoEntity().getId());
                intent.putExtra(FreeInquiryActivity_AppendPatientProblem_new.PARAMS_INFO,data.get(position).getProblemInfoEntity());
                intent.putExtra(FreeInquiryActivity_AppendPatientProblem_new.PARAMS_LAST,answer);
                ((Activity)context).startActivityForResult(intent,ProblemListActivity.REQUEST_APPAND);
            }
        });
        if(answer==null || "".equals(answer.trim())){
            content_answer.setText("暂未回复!");
        }

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(context, 300);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setContentView(view, lp);

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                content.fullScroll(ScrollView.FOCUS_DOWN);
            }
        },200);
    }


    public final int FLAG_READ = 1;
    public final int FLAG_UNREAD = 2;
    public void setReadStatus(ConversationListHolder holder,int flag,Conversation conversation){
        if(flag == FLAG_READ){//灰
            holder.conversation_status_flag.setVisibility(View.INVISIBLE);
            holder.conversation_status.setTextColor(context.getResources().getColor(R.color.color_conversation_list_status_unread));
            if(conversation.getLatestMessage()!=null &&
                    BaseApplication.getInstances().getUser_id().endsWith(conversation.getLatestMessage().
                            //TODO 由于ID变更[极光]，此处的判断规则需要进行变更
                            getFromUser().getUserName().replace(CommonRequestCode.JMESSAGE_PREFIX,"").replace(CommonRequestCode.JMESSAGE_PREFIX_DOCTOR,""))){//最后一条信息是自己发出的
                holder.conversation_status.setText("未回复");
            }else if(conversation.getLatestMessage()!=null){
                holder.conversation_status.setText("已回复");
            }else{
                holder.conversation_status.setText("");
            }
        }else if(flag == FLAG_UNREAD){//红
            holder.conversation_status_flag.setVisibility(View.VISIBLE);
            holder.conversation_status.setTextColor(context.getResources().getColor(R.color.color_conversation_list_status_read));
            if(conversation.getLatestMessage()!=null && BaseApplication.getInstances().getUser_id().
                    endsWith(conversation.getLatestMessage().getFromUser().
                            //TODO 由于ID变更[极光]，此处的判断规则需要进行变更
                            getUserName().replace(CommonRequestCode.JMESSAGE_PREFIX,"").replace(CommonRequestCode.JMESSAGE_PREFIX_DOCTOR,""))){//最后一条信息是自己发出的
                holder.conversation_status.setText("未回复");
            }else if(conversation.getLatestMessage()!=null){
                holder.conversation_status.setText("已回复");
            }else{
                holder.conversation_status.setText("");
            }
        }
    }

    public void setTimeInfo(ConversationListHolder holder,Message timeLong){
        if(timeLong!=null){
            final Date time = new Date(timeLong.getCreateTime());
            if(timeLong.getFromUser()!=null && timeLong.getFromUser().getNickname()!=null && timeLong.getFromUser().getNickname().length()>0){
                holder.last_info.setText(sdf.format(time)+" | "+timeLong.getFromUser().getNickname());
            }else{
                holder.last_info.setText(sdf.format(time)+" | "+timeLong.getFromUser().getUserName());
            }

            if(60000<(System.currentTimeMillis()-timeLong.getCreateTime())){
                holder.conversation_time.setText(timer.format(time));
            }else{
                holder.conversation_time.setText("刚刚");
            }
        }else{
            holder.last_info.setText("");
            holder.conversation_time.setText("");
        }
    }

    public void setTimeInfo(ConversationListHolder holder,Message timeLong,Conversation conversation){
        if(timeLong!=null){
            final Date time = new Date(timeLong.getCreateTime());
            UserInfo info = (UserInfo) conversation.getTargetInfo();
            if(info!=null && info.getNickname()!=null && info.getNickname().length()>0){
                holder.last_info.setText(sdf.format(time)+" | 名称:"+ info.getNickname());
                LogUtil.e("有nickname");
            }else if(info!=null && (info.getNickname()==null || info.getNickname().length()<1)){
                holder.last_info.setText(sdf.format(time)+" | 名称:"+info.getUserName());
                LogUtil.e("无nickname");
            }else{
                holder.last_info.setText(sdf.format(time)+" | 名称:"+conversation.getTargetId());
                LogUtil.e("无UserInfo");
            }

            if(60000<(System.currentTimeMillis()-timeLong.getCreateTime())){
                holder.conversation_time.setText(timer.format(time));
            }else{
                holder.conversation_time.setText("刚刚");
            }
        }else{
            holder.last_info.setText("");
            holder.conversation_time.setText("");
        }
    }

    public void setTimeInfo(ConversationListHolder holder,Message timeLong,ProblemEntity conversation){
        if(timeLong!=null){
            final Date time = new Date(timeLong.getCreateTime());
            if(conversation.getmConversation().getTargetInfo() instanceof UserInfo){
                UserInfo info = (UserInfo) conversation.getmConversation().getTargetInfo();
                if(info!=null && info.getNickname()!=null && info.getNickname().length()>0){
                    holder.last_info.setText(sdf.format(time)+" | 名称:"+ info.getNickname());
                    LogUtil.e("有nickname");
                }else if(info!=null && (info.getNickname()==null || info.getNickname().length()<1)){
//                holder.last_info.setText(sdf.format(time)+" | 名称:"+info.getUserName());
                    if(conversation.getInfo()!=null && conversation.getInfo().length()>0){
                        JsonObject doctor = new GsonParseUtil(conversation.getInfo()).getJsonObject();
                        if (doctor.has("name") && doctor.get("name").getAsString()!=null && doctor.get("name").getAsString().length()>0) {
                            setTimeInfo(holder,timeLong,conversation.getmConversation(),doctor.get("name").getAsString());
                            LogUtil.e("无nickname --- jsonObject");
                        }else{
                            setTimeInfo(holder,timeLong,conversation.getmConversation());
                            LogUtil.e("无nickname --- jsonObject  Null");
                        }
                    }else{
                        LogUtil.e("无nickname");
                        loadUserInfo(conversation.getmConversation().getTargetId(),holder,timeLong,conversation.getmConversation(),conversation);
                    }
                }else{
//                holder.last_info.setText(sdf.format(time)+" | 名称:"+conversation.getmConversation().getTargetId());

                    if(conversation.getInfo()!=null && conversation.getInfo().length()>0){
                        JsonObject doctor = new GsonParseUtil(conversation.getInfo()).getJsonObject();
                        if (doctor.has("name") && doctor.get("name").getAsString()!=null && doctor.get("name").getAsString().length()>0) {
                            setTimeInfo(holder,timeLong,conversation.getmConversation(),doctor.get("name").getAsString());
                            LogUtil.e("无UserInfo --- jsonObject");
                        }else{
                            setTimeInfo(holder,timeLong,conversation.getmConversation());
                            LogUtil.e("无UserInfo --- jsonObject  Null");
                        }
                    }else{
                        loadUserInfo(conversation.getmConversation().getTargetId(),holder,timeLong,conversation.getmConversation(),conversation);
                        LogUtil.e("无UserInfo");
                    }
                }

                if(60000<(System.currentTimeMillis()-timeLong.getCreateTime())){
                    holder.conversation_time.setText(timer.format(time));
                }else{
                    holder.conversation_time.setText("刚刚");
                }
            }
        }else{
            holder.last_info.setText("");
            holder.conversation_time.setText("");
        }
    }

    public void loadUserInfo(final String mTargetId,final ConversationListHolder holder,final Message timeLong,final Conversation conversation,final ProblemEntity problemEntity){
        HashMap<String, String> params = new HashMap<>();
        params.put("id", mTargetId.replace(CommonRequestCode.JMESSAGE_PREFIX_DOCTOR,"").replace(CommonRequestCode.JMESSAGE_PREFIX,""));
        MyHandler.putTask((Activity) context,new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                JsonObject doctor = new GsonParseUtil(val).getJsonObject();
                if (doctor.has("name") && doctor.get("name").getAsString()!=null && doctor.get("name").getAsString().length()>0) {
                    setTimeInfo(holder,timeLong,conversation,doctor.get("name").getAsString());
                    problemEntity.setInfo(val);
                }else{
                    setTimeInfo(holder,timeLong,conversation);
                }
            }
        }, HttpUrls.getDoctorDetail(), params, Task.TYPE_POST_DATA_PARAMS, null));
    }

    public void setTimeInfo(ConversationListHolder holder,Message timeLong,Conversation conversation,String name){
        if(timeLong!=null){
            final Date time = new Date(timeLong.getCreateTime());
            UserInfo info = (UserInfo) conversation.getTargetInfo();
            if(info!=null && info.getNickname()!=null && info.getNickname().length()>0){
                holder.last_info.setText(sdf.format(time)+" | 名称: "+ name);
                LogUtil.e("有nickname");
            }else if(info!=null && (info.getNickname()==null || info.getNickname().length()<1)){
                holder.last_info.setText(sdf.format(time)+" | 名称: "+name);
                LogUtil.e("无nickname");
            }else{
                holder.last_info.setText(sdf.format(time)+" | 名称: "+name);
                LogUtil.e("无UserInfo");
            }

            if(60000<(System.currentTimeMillis()-timeLong.getCreateTime())){
                holder.conversation_time.setText(timer.format(time));
            }else{
                holder.conversation_time.setText("刚刚");
            }
        }else{
            holder.last_info.setText("");
            holder.conversation_time.setText("");
        }
    }

    public void setTimeInfo(ConversationListHolder holder,long timeLong){
        final Date time = new Date(timeLong);
        holder.last_info.setText(sdf.format(time)+"  |  免费");
        if(60000<(System.currentTimeMillis()-timeLong)){
           holder.conversation_time.setText(timer.format(time));
        }else{
           holder.conversation_time.setText("刚刚");
        }
    }

    public void setTimeInfo(ConversationListHolder holder,String timelong){
        long timeLong = Long.parseLong(timelong);
        final Date time = new Date(timeLong);
        holder.last_info.setText(sdf.format(time)+"  |  免费");
        if(60000<(System.currentTimeMillis()-timeLong)){
            holder.conversation_time.setText(timer.format(time));
        }else{
            holder.conversation_time.setText("刚刚");
        }
    }

    public void setTimeInfo(ConversationListHolder holder,FreeInquiryProblemEntity timelong){
        long timeLong = Long.parseLong(timelong.getCreated_time_ms());
        final Date time = new Date(timeLong);
        if(timelong.getPatientInfo().has("name")){
            holder.last_info.setText(sdf.format(time)+"  | "+timelong.getPatientInfo().get("name").getAsString()+" | "+
                    timelong.getPatientInfo().get("sex").getAsString()+" | "+
                    timelong.getPatientInfo().get("age").getAsString()+" | 免费");
        }else if(timelong.getPatientInfo().has("Name")){
            holder.last_info.setText(sdf.format(time)+"  | "+timelong.getPatientInfo().get("Name").getAsString()+" | "+
                    timelong.getPatientInfo().get("sex").getAsString()+" | "+
                    timelong.getPatientInfo().get("age").getAsString()+" | 免费");
        }

        if(60000<(System.currentTimeMillis()-timeLong)){
            holder.conversation_time.setText(timer.format(time));
        }else{
            holder.conversation_time.setText("刚刚");
        }
    }
    private JsonObject status;
    private String type;
    /**
     * 检查聊天状态
     */
    protected void checkStatus(String id,final ProblemEntity conversation) {

        HashMap<String, String> params = new HashMap<>();
        String entity = "&user_id="+BaseApplication.getInstances().getUser_id()+"&doctor_id="+id;
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(null==val||"".equals(val)){
                    BaseApplication.getInstances().toast(context,"网络连接错误！");
                    return;
                }else {
                    status = new GsonParseUtil(val).getJsonObject();
                    LogUtil.e("status------------"+status.toString());
//                    type = status.get("type").getAsString();//状态 0未支付  1已支付  2付费未回复 3开始聊天
                    if ("0.00".equals(status.get("servicePrice").getAsString())){
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(ChatActivity.TARGET_ID, conversation.getmConversation().getTargetId());//10053;10043
                        intent.putExtra(ChatActivity.TARGET_APP_KEY, conversation.getmConversation().getTargetAppKey());//10053;10043
                        intent.putExtra(ChatActivity.TARGET_PERSON_INFO, conversation.getInfo());
                        ((Activity)context).startActivityForResult(intent, ProblemListActivity.REQUEST_IM);
                    }else if ("0".equals(status.get("isPay").getAsString())){
                        QuanjiakanUtil.showToast(context,"请购买该医生服务！");

                    }else if ("1".equals(status.get("isPay").getAsString())){
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(ChatActivity.TARGET_ID, conversation.getmConversation().getTargetId());//10053;10043
                        intent.putExtra(ChatActivity.TARGET_APP_KEY, conversation.getmConversation().getTargetAppKey());//10053;10043
                        intent.putExtra(ChatActivity.TARGET_PERSON_INFO, conversation.getInfo());
                        ((Activity)context).startActivityForResult(intent, ProblemListActivity.REQUEST_IM);

                    }

                }
            }
        },HttpUrls.checkChatStatus()+entity,params,Task.TYPE_GET_STRING_NOCACHE,null));

    }
}
