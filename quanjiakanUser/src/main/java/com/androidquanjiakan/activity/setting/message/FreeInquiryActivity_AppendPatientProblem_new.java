package com.androidquanjiakan.activity.setting.message;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.adapter.FreeInquiryAppendAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.entity.BaseHttpResultEntity_List;
import com.androidquanjiakan.entity.FreeInquiryAnswerEntity;
import com.androidquanjiakan.entity.FreeInquiryDoctorInfoEntity;
import com.androidquanjiakan.entity.FreeInquiryProblemEntity;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.entity_util.UnitUtil;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.androidquanjiakan.util.BitmapUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.StringDecodeUtil;
import com.czt.mp3recorder.MP3Recorder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.quanjiakanuser.util.ImageUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.jmessage.android.uikit.chatting.RecordVoiceButton;
import cn.jmessage.android.uikit.chatting.utils.FileHelper;
import cn.jmessage.android.uikit.chatting.utils.HandleResponseCode;
import cn.jmessage.android.uikit.chatting.utils.IdHelper;

public class FreeInquiryActivity_AppendPatientProblem_new extends BaseActivity implements OnClickListener {

    private ImageButton ibtn_back;
    private TextView tv_title;

    public static final String PARAMS_PROBLEM_ID = "id";
    private String problem_id;

    public static final String PARAMS_INFO = "info";
    public static final String PARAMS_LAST = "last_answer";
    private FreeInquiryProblemEntity info;

    //
    private ImageView docter_info_head;
    private TextView docter_info_name;
    private TextView docter_info_hospital_value;
    private TextView docter_info_clinic_value;
    private TextView docter_info_rank_value;
    private TextView evaluate;

    //
    private ImageButton jmui_switch_voice_ib;//语音按钮
    private EditText jmui_chat_input_et;//文本输入框
    private RecordVoiceButton jmui_voice_btn;
    private ImageButton jmui_add_file_btn;//添加文件按钮---选择本体图片，拍照等
    private Button jmui_send_msg_btn;

    private TableLayout jmui_more_menu_tl;

    private ImageButton jmui_pick_from_local_btn;
    private ImageButton jmui_pick_from_camera_btn;

    private PullToRefreshListView list;
//    private SwipeRefreshLayout refresh;

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private final int REQUEST_POST_PROBLEM = 10010;
    private static final String IMAGE_FILE_NAME = "temp_image_file.jpg";

    private FreeInquiryAppendAdapter appendAdapter;
    BaseHttpResultEntity_List<FreeInquiryAnswerEntity> answerEntityBaseHttpResultEntity_list;
    List<FreeInquiryAnswerEntity> freeInquiryAnswerEntityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_free_problem_append);
        problem_id = getIntent().getStringExtra(PARAMS_PROBLEM_ID);
        info = (FreeInquiryProblemEntity) getIntent().getSerializableExtra(PARAMS_INFO);
        if(problem_id==null || info==null){
            BaseApplication.getInstances().toast(FreeInquiryActivity_AppendPatientProblem_new.this,"传入参数有误!");
            finish();
            return;
        }

        initTitle();
        initDocterInfo();
        loadHistoryData();

        initSendMsgLayout();

        initList();

        /**
         * 区分推送的通知，确定通知是免费问诊的，且是当期医生的则刷新界面数据
         */

    }

    public void initList(){
//        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
//        refresh.setColorSchemeColors(R.color.holo_blue_light,R.color.holo_green_light,R.color.holo_orange_light);
//        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refresh.setRefreshing(false);
//                appendAdapter.release();
//                //                appendAdapter.notifyDataSetChanged();
//                loadListData();
//            }
//        });
        list = (PullToRefreshListView) findViewById(R.id.list);
        list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                appendAdapter.release();
                loadListData();

                list.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                list.onRefreshComplete();
            }
        });

        FreeInquiryAnswerEntity questioin = new FreeInquiryAnswerEntity();
        questioin.setSponsor("1");
        questioin.setCreated_time_ms(info.getCreated_time_ms());
        questioin.setTitle(info.getTitle());
        freeInquiryAnswerEntityList.add(0,questioin);//单独抽出显示-------放到第一位

        appendAdapter = new FreeInquiryAppendAdapter(this,freeInquiryAnswerEntityList);
        list.setAdapter(appendAdapter);
        loadListData();
    }


    public void loadListData(){
        MyHandler.putTask(FreeInquiryActivity_AppendPatientProblem_new.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
                    answerEntityBaseHttpResultEntity_list = (BaseHttpResultEntity_List<FreeInquiryAnswerEntity>)
                            SerialUtil.jsonToObject(val,new TypeToken<BaseHttpResultEntity_List<FreeInquiryAnswerEntity>>(){}.getType());

                    if(answerEntityBaseHttpResultEntity_list!=null && answerEntityBaseHttpResultEntity_list.getRows()!=null &&
                            answerEntityBaseHttpResultEntity_list.getRows().size()>0){
                        //单个问题--多个回答
                        freeInquiryAnswerEntityList.clear();
                        freeInquiryAnswerEntityList.addAll(answerEntityBaseHttpResultEntity_list.getRows());
                        /**
                         * 单独增加一个数据项用户表示发送的第一个问题
                         */
                        FreeInquiryAnswerEntity questioin = new FreeInquiryAnswerEntity();
                        questioin.setSponsor("1");
                        questioin.setCreated_time_ms(answerEntityBaseHttpResultEntity_list.getRows().get(0).getCreated_time_ms());
                        questioin.setTitle(answerEntityBaseHttpResultEntity_list.getRows().get(0).getTitle());
                        freeInquiryAnswerEntityList.add(0,questioin);//单独抽出显示-------放到第一位

                        /**
                         * 测试用添加的数据
                         */
//                        FreeInquiryAnswerEntity voice = new FreeInquiryAnswerEntity();
//                        voice.setSponsor("0");
//                        voice.setCreated_time_ms(answerEntityBaseHttpResultEntity_list.getRows().get(0).getCreated_time_ms());
//                        voice.setContent("[{\"file\":\"http://app.quanjiakan.com/quanjiakan/resources/chunyu/audios/20161015141323_5vj5hy.mp3\",\"type\":\"audio\"}]");
////                        voice.setContent("[{\"file\":\"https://api.chunyuyisheng.com/media/audios/2016/09/26/1b8e9be7faa7.3gp.mp3\",\"type\":\"audio\"}]");
//                        freeInquiryAnswerEntityList.add(1,voice);
//
//                        FreeInquiryAnswerEntity img = new FreeInquiryAnswerEntity();
//                        img.setSponsor("0");
//                        img.setCreated_time_ms(answerEntityBaseHttpResultEntity_list.getRows().get(0).getCreated_time_ms());
//                        img.setContent("[{\"file\":\"http://familycareapi.oss-cn-shanghai.aliyuncs.com/jishlpxb7jxo.png\",\"type\":\"image\"}]");
//                        freeInquiryAnswerEntityList.add(2,img);

//                        size = freeInquiryAnswerEntityList.size();

                        appendAdapter.setData(freeInquiryAnswerEntityList);
                        appendAdapter.notifyDataSetChanged();
                        list.getRefreshableView().setSelection(list.getBottom());
//                        for(int i = 0;i<size;i++){
//                            LogUtil.e("position:"+i+" **** 1'2\""+freeInquiryAnswerEntityList.get(i).toString());
//                        }
//                        showFreeInquiryDialog(position,content,answerEntityBaseHttpResultEntity_list.getRows());//回复内包含所有回复文字信息
                        //追问，回答
//                        showFreeInquiryDialog_QAList(position,content,answerEntityBaseHttpResultEntity_list.getRows());//回复内包含所有回复文字信息
                    }else{
//                        showFreeInquiryDialog(content,null);
//                        Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, "暂未回复!", Toast.LENGTH_LONG).show();
                    }
                }else{
//                    showFreeInquiryDialog(content,null);
//                    Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, "暂未回复!", Toast.LENGTH_LONG).show();
                }

                list.onRefreshComplete();
            }
        }, HttpUrls.getGetChunyuProblemReply()+"&problem_id="+problem_id,null,Task.TYPE_GET_STRING_NOCACHE,
                /*QuanjiakanDialog.getInstance().getDialog(FreeInquiryActivity_AppendPatientProblem_new.this)*/  null
        ));
    }

    public void initTitle(){
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("追问");
    }

    protected void initDocterInfo() {
        docter_info_head = (ImageView) findViewById(R.id.docter_info_head);
        docter_info_name = (TextView) findViewById(R.id.docter_info_name);

        docter_info_hospital_value = (TextView) findViewById(R.id.docter_info_hospital_value);
        docter_info_clinic_value = (TextView) findViewById(R.id.docter_info_clinic_value);
        docter_info_rank_value = (TextView) findViewById(R.id.docter_info_rank_value);
        evaluate = (TextView) findViewById(R.id.evaluate);
        evaluate.setVisibility(View.VISIBLE);
        evaluate.setOnClickListener(this);


        loadDocterInfoData();
    }

    public void loadDocterInfoData(){
        //TODO 网络请求获取医生信息-----调用

//        docter_info_head.setImageResource(R.drawable.icon_issue_portrait);
//        docter_info_name.setText("李安安");
//        docter_info_hospital_value.setText("");
//        docter_info_clinic_value.setText("");
//        docter_info_rank_value.setText("");
        evaluate.setText("评价");

        HashMap<String,String> params = new HashMap<>();
        params.put("doctor_id",info.getDoctor_id());
        params.put("user_id",BaseApplication.getInstances().getUser_id());
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(val!=null && val.length()>0 && !"null".equalsIgnoreCase(val)){
                    FreeInquiryDoctorInfoEntity doct = (FreeInquiryDoctorInfoEntity) SerialUtil.jsonToObject(val,
                            new TypeToken<FreeInquiryDoctorInfoEntity>(){}.getType());
                    if(doct!=null){
                        ImageLoadUtil.loadImage(docter_info_head,doct.getImage(),ImageLoadUtil.optionsCircle);
                        docter_info_name.setText(doct.getName());
                        docter_info_hospital_value.setText(doct.getHospital());
                        docter_info_clinic_value.setText(doct.getClinic_name());
                        docter_info_rank_value.setText(doct.getHospital_grade());
                    }
                }
            }
        },HttpUrls.getFreeInquiryDoctorInfo(),params,Task.TYPE_POST_DATA_PARAMS,null));
    }

    public void loadHistoryData(){
        //加载历史记录，并展示到界面上


    }

    public void initSendMsgLayout(){

        jmui_switch_voice_ib = (ImageButton) findViewById(R.id.jmui_switch_voice_ib);
        jmui_chat_input_et = (EditText) findViewById(R.id.jmui_chat_input_et);
        jmui_chat_input_et.addTextChangedListener(watcher);
        jmui_voice_btn = (RecordVoiceButton) findViewById(R.id.jmui_voice_btn);
        jmui_add_file_btn = (ImageButton) findViewById(R.id.jmui_add_file_btn);
        jmui_send_msg_btn = (Button) findViewById(R.id.jmui_send_msg_btn);

        jmui_more_menu_tl = (TableLayout) findViewById(R.id.jmui_more_menu_tl);
        jmui_pick_from_local_btn = (ImageButton) findViewById(R.id.jmui_pick_from_local_btn);
        jmui_pick_from_camera_btn = (ImageButton) findViewById(R.id.jmui_pick_from_camera_btn);
        res = new int[]{
                R.drawable.jmui_mic_1,
                R.drawable.jmui_mic_2,
                R.drawable.jmui_mic_3,
                R.drawable.jmui_mic_4,
                R.drawable.jmui_mic_5,
                R.drawable.jmui_cancel_record};
        mVolumeHandler = new ShowVolumeHandler(jmui_voice_btn);


        jmui_switch_voice_ib.setOnClickListener(this);
        jmui_add_file_btn.setOnClickListener(this);
        jmui_pick_from_local_btn.setOnClickListener(this);
        jmui_pick_from_camera_btn.setOnClickListener(this);
        jmui_send_msg_btn.setOnClickListener(this);

        jmui_voice_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mIsPressed = true;
                        time1 = System.currentTimeMillis();
                        mTouchY1 = event.getY();
                        if (FileHelper.isSdCardExist()) {
                            /**
                             * 显示录音的效果Dialog
                             */
                            if (isTimerCanceled) {
                                timer = createTimer();
                            }
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    android.os.Message msg = myHandler.obtainMessage();
                                    msg.what = START_RECORD;
                                    msg.sendToTarget();
                                }
                            }, 500);
                        } else {
                            Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, FreeInquiryActivity_AppendPatientProblem_new.this.
                                    getString(IdHelper.getString(FreeInquiryActivity_AppendPatientProblem_new.this,
                                            "jmui_sdcard_not_exist_toast")), Toast.LENGTH_SHORT).show();
                            mIsPressed = false;
                            return false;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mTouchY = event.getY();
                        //手指上滑到超出限定后，显示松开取消发送提示
                        if (mTouchY1 - mTouchY > MIN_CANCEL_DISTANCE) {
                            mVolumeHandler.sendEmptyMessage(CANCEL_RECORD);
                            if (mThread != null) {
                                mThread.exit();
                            }
                            mThread = null;
                        } else {
                            if (mThread == null) {
                                mThread = new FreeInquiryActivity_AppendPatientProblem_new.ObtainDecibelThread();
                                mThread.start();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mIsPressed = false;
                        time2 = System.currentTimeMillis();
                        mTouchY2 = event.getY();

                        if (time2 - time1 < 500) {
                            cancelTimer();
                            return true;
                        } else if (time2 - time1 < 1000) {
                            cancelRecord();
                        } else if (mTouchY1 - mTouchY2 > MIN_CANCEL_DISTANCE) {
                            cancelRecord();
                        } else if (time2 - time1 < 60000) {
                            finishRecord();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        cancelRecord();
                        break;
                }
                return true;
            }
        });

    }

    protected void persistProblem() {
        if(jmui_chat_input_et.length()<1 && voicePath==null && imagePath==null){
            BaseApplication.getInstances().toast(FreeInquiryActivity_AppendPatientProblem_new.this,"请填入追问的问题!");
            return;
        }

        if(voicePath!=null && !voicePath.toLowerCase().startsWith("http")){
            BaseApplication.getInstances().toast(FreeInquiryActivity_AppendPatientProblem_new.this,"上传语音失败，请重试!");
            return ;
        }

        if(imagePath!=null && !imagePath.toLowerCase().startsWith("http")){
            BaseApplication.getInstances().toast(FreeInquiryActivity_AppendPatientProblem_new.this,"上传图片失败，请重试!");
            return ;
        }

//        if(jmui_chat_input_et.length()<10  && voicePath==null && imagePath==null){
//            BaseApplication.getInstances().toast("追问的问题字数未到达限制范围!");
//            return;
//        }
//
//        if(jmui_chat_input_et.length()>1000  && voicePath==null && imagePath==null){
//            BaseApplication.getInstances().toast("追问的问题字数超出限制范围!");
//            return;
//        }
        final JsonArray content = new JsonArray();
        JsonObject object = new JsonObject();

        if(jmui_chat_input_et.getText().toString().length()>0){
            object.addProperty("type", "text");
            object.addProperty("text", jmui_chat_input_et.getText().toString());
        }else if(voicePath!=null){
            object.addProperty("type", "audio");
            object.addProperty("file", voicePath);
        }else if(imagePath!=null){
            object.addProperty("type", "image");
            object.addProperty("file", imagePath);
        }

        content.add(object);

        HashMap<String, String> params = new HashMap<>();
        params.put("content", content + "");//内容jsonArray格式
        params.put("problem_id", info.getChunyu_id());//咨询的问题id
        params.put("p_id", info.getId());
        params.put("fromtoken", BaseApplication.getInstances().getUser_id()); //自己的UserID
        params.put("totoken", info.getDoctor_id());//咨询医生的UserID

        MyHandler.putTask(this,new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                //{"error_msg": "\u95ee\u9898ID\u9519\u8bef", "error": 1}
                JsonObject result = new GsonParseUtil(val).getJsonObject();
                if (result.has("error") && result.get("error").getAsInt()==0) {
                    BaseApplication.getInstances().toast(FreeInquiryActivity_AppendPatientProblem_new.this,"发送成功!");
                    /**
                     * 返回
                     */
//                    setResult(RESULT_OK);
//                    finish();
                    /**
                     * 刷新数据
                     */
                    FreeInquiryAnswerEntity temp = null;
                    if(appendAdapter.getData()==null || appendAdapter.getData().size()<1){
                        temp = new FreeInquiryAnswerEntity();
                        temp.setDoctor_id(info.getDoctor_id());
                        temp.setCreated_time_ms(info.getCreated_time_ms());
                        temp.setPatient(info.getPatient());

                        temp.setCreator(info.getCreator());
                        temp.setCreatetime(info.getCreatetime());
                        temp.setChunyu_id(info.getChunyu_id());
                        temp.setStatus(info.getStatus());
                        temp.setTitle(info.getTitle());

                        temp.setIs_reply("0");
                        temp.setUser_id(info.getUser_id());
                        temp.setSponsor("1");
                        temp.setContent(content + "");
                        temp.setFromtoken(info.getFromtoken());

                        temp.setTotoken(info.getTotoken());
                        temp.setProblem_content_status(info.getProblem_content_status());
                    }else{
                        temp = appendAdapter.getData().get(0);
                    }
//                    FreeInquiryAnswerEntity temp = appendAdapter.getData().get(0);
                    if(jmui_chat_input_et.getText().toString().length()>0){
                        FreeInquiryAnswerEntity add = new FreeInquiryAnswerEntity();

//                        add.setId();
//                        add.setPrice();
                        add.setDoctor_id(temp.getDoctor_id());
                        add.setCreated_time_ms(temp.getCreated_time_ms());
                        add.setPatient(temp.getPatient());

                        add.setCreator(temp.getCreator());
                        add.setCreatetime(temp.getCreatetime());
                        add.setChunyu_id(temp.getChunyu_id());
                        add.setStatus(temp.getStatus());
                        add.setTitle(temp.getTitle());

                        add.setIs_reply("0");
                        add.setUser_id(temp.getUser_id());
                        add.setSponsor("1");
                        add.setContent(content + "");
                        add.setFromtoken(temp.getFromtoken());

                        add.setTotoken(temp.getTotoken());
                        add.setProblem_content_status(temp.getProblem_content_status());

//                        appendAdapter.getData().add(add);
//                        appendAdapter.notifyDataSetChanged();
//                        list.setSelection(list.getBottom());
                    }else if(voicePath!=null){
                        FreeInquiryAnswerEntity add = new FreeInquiryAnswerEntity();

//                        add.setId();
//                        add.setPrice();
                        add.setDoctor_id(temp.getDoctor_id());
                        add.setCreated_time_ms(temp.getCreated_time_ms());
                        add.setPatient(temp.getPatient());

                        add.setCreator(temp.getCreator());
                        add.setCreatetime(temp.getCreatetime());
                        add.setChunyu_id(temp.getChunyu_id());
                        add.setStatus(temp.getStatus());
                        add.setTitle(temp.getTitle());

                        add.setIs_reply("0");
                        add.setUser_id(temp.getUser_id());
                        add.setSponsor("1");
                        add.setContent(content + "");
                        add.setFromtoken(temp.getFromtoken());

                        add.setTotoken(temp.getTotoken());
                        add.setProblem_content_status(temp.getProblem_content_status());

//                        appendAdapter.getData().add(add);
//                        appendAdapter.notifyDataSetChanged();
//                        list.setSelection(list.getBottom());
                    }else if(imagePath!=null){
                        FreeInquiryAnswerEntity add = new FreeInquiryAnswerEntity();

//                        add.setId();
//                        add.setPrice();
                        add.setDoctor_id(temp.getDoctor_id());
                        add.setCreated_time_ms(temp.getCreated_time_ms());
                        add.setPatient(temp.getPatient());

                        add.setCreator(temp.getCreator());
                        add.setCreatetime(temp.getCreatetime());
                        add.setChunyu_id(temp.getChunyu_id());
                        add.setStatus(temp.getStatus());
                        add.setTitle(temp.getTitle());

                        add.setIs_reply("0");
                        add.setUser_id(temp.getUser_id());
                        add.setSponsor("1");
                        add.setContent(content + "");
                        add.setFromtoken(temp.getFromtoken());

                        add.setTotoken(temp.getTotoken());
                        add.setProblem_content_status(temp.getProblem_content_status());

//                        appendAdapter.getData().add(add);
//                        appendAdapter.notifyDataSetChanged();
//                        list.setSelection(list.getBottom());
                    }

                    jmui_chat_input_et.setText("");
                    voicePath = null;
                    imagePath = null;

                    loadListData();
                }else{
                    if(result.has("error_msg")){
                        Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, StringDecodeUtil.decodeUnicode(result.get("error_msg").getAsString()), Toast.LENGTH_LONG).show();
                        finish();
                    }else if(result.has("code") && "200".equals(result.get("code").getAsString())){
                        Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this,
                                "追问成功!", Toast.LENGTH_LONG).show();
                        FreeInquiryAnswerEntity temp = null;
                        if(appendAdapter.getData()==null || appendAdapter.getData().size()<1){
                            temp = new FreeInquiryAnswerEntity();
                            temp.setDoctor_id(info.getDoctor_id());
                            temp.setCreated_time_ms(info.getCreated_time_ms());
                            temp.setPatient(info.getPatient());

                            temp.setCreator(info.getCreator());
                            temp.setCreatetime(info.getCreatetime());
                            temp.setChunyu_id(info.getChunyu_id());
                            temp.setStatus(info.getStatus());
                            temp.setTitle(info.getTitle());

                            temp.setIs_reply("0");
                            temp.setUser_id(info.getUser_id());
                            temp.setSponsor("1");
                            temp.setContent(content + "");
                            temp.setFromtoken(info.getFromtoken());

                            temp.setTotoken(info.getTotoken());
                            temp.setProblem_content_status(info.getProblem_content_status());
                        }else{
                            temp = appendAdapter.getData().get(0);
                        }
                        if(jmui_chat_input_et.getText().toString().length()>0){
                            FreeInquiryAnswerEntity add = new FreeInquiryAnswerEntity();

//                        add.setId();
//                        add.setPrice();
                            add.setDoctor_id(temp.getDoctor_id());
                            add.setCreated_time_ms(temp.getCreated_time_ms());
                            add.setPatient(temp.getPatient());

                            add.setCreator(temp.getCreator());
                            add.setCreatetime(temp.getCreatetime());
                            add.setChunyu_id(temp.getChunyu_id());
                            add.setStatus(temp.getStatus());
                            add.setTitle(temp.getTitle());

                            add.setIs_reply("0");
                            add.setUser_id(temp.getUser_id());
                            add.setSponsor("1");
                            add.setContent(content + "");
                            add.setFromtoken(temp.getFromtoken());

                            add.setTotoken(temp.getTotoken());
                            add.setProblem_content_status(temp.getProblem_content_status());

//                            appendAdapter.getData().add(add);
//                            appendAdapter.notifyDataSetChanged();
//                            list.setSelection(list.getBottom());
                        }else if(voicePath!=null){
                            FreeInquiryAnswerEntity add = new FreeInquiryAnswerEntity();

//                        add.setId();
//                        add.setPrice();
                            add.setDoctor_id(temp.getDoctor_id());
                            add.setCreated_time_ms(temp.getCreated_time_ms());
                            add.setPatient(temp.getPatient());

                            add.setCreator(temp.getCreator());
                            add.setCreatetime(temp.getCreatetime());
                            add.setChunyu_id(temp.getChunyu_id());
                            add.setStatus(temp.getStatus());
                            add.setTitle(temp.getTitle());

                            add.setIs_reply("0");
                            add.setUser_id(temp.getUser_id());
                            add.setSponsor("1");
                            add.setContent(content + "");
                            add.setFromtoken(temp.getFromtoken());

                            add.setTotoken(temp.getTotoken());
                            add.setProblem_content_status(temp.getProblem_content_status());

//                            appendAdapter.getData().add(add);
//                            appendAdapter.notifyDataSetChanged();
//                            list.setSelection(list.getBottom());
                        }else if(imagePath!=null){
                            FreeInquiryAnswerEntity add = new FreeInquiryAnswerEntity();

//                        add.setId();
//                        add.setPrice();
                            add.setDoctor_id(temp.getDoctor_id());
                            add.setCreated_time_ms(temp.getCreated_time_ms());
                            add.setPatient(temp.getPatient());

                            add.setCreator(temp.getCreator());
                            add.setCreatetime(temp.getCreatetime());
                            add.setChunyu_id(temp.getChunyu_id());
                            add.setStatus(temp.getStatus());
                            add.setTitle(temp.getTitle());

                            add.setIs_reply("0");
                            add.setUser_id(temp.getUser_id());
                            add.setSponsor("1");
                            add.setContent(content + "");
                            add.setFromtoken(temp.getFromtoken());

                            add.setTotoken(temp.getTotoken());
                            add.setProblem_content_status(temp.getProblem_content_status());

//                            appendAdapter.getData().add(add);
//                            appendAdapter.notifyDataSetChanged();
//                            list.setSelection(list.getBottom());
                        }

                        jmui_chat_input_et.setText("");
                        voicePath = null;
                        imagePath = null;


                        loadListData();
                    }else if(result.has("code") && !"200".equals(result.get("code").getAsString())){
                        Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, result.get("message").getAsString(), Toast.LENGTH_LONG).show();
                        finish();
                    }

                }
            }
        }, HttpUrls.getFreeInquiryAppand(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(this)));
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

        switch (arg0.getId()){
            case R.id.evaluate:
                //TODO 对医生问题进行评价----调用春雨的评价接口
                doEvaluate();
                break;
            case R.id.ibtn_back:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.jmui_switch_voice_ib://录音按钮

                jmui_more_menu_tl.setVisibility(View.GONE);

                if(jmui_chat_input_et.getVisibility()==View.VISIBLE){
                    //将该按钮背景图变成键盘，并隐藏文本输入框，显示录音按钮 jmui_keyboard
                    jmui_switch_voice_ib.setBackgroundResource(R.drawable.jmui_keyboard);
                    jmui_chat_input_et.setVisibility(View.GONE);
                    jmui_voice_btn.setVisibility(View.VISIBLE);

                    hideSoftInput(jmui_chat_input_et);
                }else{
                    jmui_switch_voice_ib.setBackgroundResource(R.drawable.jmui_voice);
                    jmui_chat_input_et.setVisibility(View.VISIBLE);
                    jmui_voice_btn.setVisibility(View.GONE);
                    //输入框获取焦点，同时软键盘显示出来
                    jmui_chat_input_et.requestFocus();

                    showSoftInput(jmui_chat_input_et);
                }
                break;
            case R.id.jmui_send_msg_btn://发送消息按钮
                persistProblem();
                break;
            case R.id.jmui_add_file_btn:
                /**
                 * 未显示则：
                 * 显示图片与拍照选项
                 *
                 * 否则：
                 * 文本输入框获取焦点，并将键盘弹出
                 */
                jmui_switch_voice_ib.setBackgroundResource(R.drawable.jmui_voice);
                jmui_chat_input_et.setVisibility(View.VISIBLE);
                jmui_voice_btn.setVisibility(View.GONE);

                if(jmui_more_menu_tl.getVisibility()==View.VISIBLE){
                    jmui_more_menu_tl.setVisibility(View.GONE);

                    jmui_chat_input_et.requestFocus();
                    showSoftInput(jmui_chat_input_et);
                }else{
                    jmui_more_menu_tl.setVisibility(View.VISIBLE);

                    hideSoftInput(jmui_chat_input_et);
                }
                break;
            case R.id.jmui_pick_from_camera_btn://使用照相机
                ImageCropHandler.getImageFromCamera(FreeInquiryActivity_AppendPatientProblem_new.this, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        LogUtil.e("获取从照相机拍摄的照片路径:" + path);
                    }
                });

                break;
            case R.id.jmui_pick_from_local_btn://使用本地相册
                ImageCropHandler.pickImage(FreeInquiryActivity_AppendPatientProblem_new.this);
                break;
        }

    }

    private Dialog evaluateDialg;
    private View view;
    public void doEvaluate(){
        if(info.getDoctor_id()==null || "0".endsWith(info.getDoctor_id())){
            Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, "问题尚未被回复，无法进行评价!", Toast.LENGTH_LONG).show();
            return ;
        }else{
            if(evaluateDialg!=null){
                evaluateDialg.dismiss();
                evaluateDialg = null;
            }
            evaluateDialg = new Dialog(this, R.style.dialog_loading);

            view = LayoutInflater.from(this).inflate(R.layout.dialog_free_inquiry_evaluate_problem,null);
            final RatingBar mRatingBar = (RatingBar) view.findViewById(R.id.rating);
            mRatingBar.setRating(5);
            mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if(rating<1){
                        mRatingBar.setRating(1);
                    }else{
                        mRatingBar.setRating(Math.round(rating));
                    }
                }
            });

            final EditText mEvaluate = (EditText) view.findViewById(R.id.evaluate);
            TextView mConfirm = (TextView) view.findViewById(R.id.btn_confirm);
            TextView mCancel = (TextView) view.findViewById(R.id.btn_cancel);
            mConfirm.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    evaluateDialg.dismiss();

                    if(mEvaluate.length()<1){
                        Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, "请输入问题评价!", Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    /**
                     * 调用评价问题接口
                     */
//                    String eval = "[{\"type\":\"text\",\"text\":\""+mEvaluate.getText().toString().trim()+"\"}]";
//                    LogUtil.e("eval:"+eval+" ---- Rating:"+mRatingBar.getRating()+" *** "+(Math.round(mRatingBar.getRating())));
//                    LogUtil.e(info.getChunyu_id());

                    /**
                     * 评价问题
                     * http://127.0.0.1:8080/quanjiakan/api?m=consultant&type=cy&a=evaluate&
                     * problem_id=421236587&user_id=10794&content=good&star=5&username=jeck
                     * @param params
                     * @return
                     */
                    String content;
                    HashMap<String,String> params = new HashMap<>();
                    params.put("problem_id",info.getChunyu_id());
                    params.put("username",BaseApplication.getInstances().getUser_id());
                    params.put("star",Math.round(mRatingBar.getRating())+"");
                    // invalid param content
//                    content = mEvaluate.getText().toString().trim()+"";
//                    params.put("content",content);
                    // 评价问题成功
                    content = "[{\"text\":\""+mEvaluate.getText().toString().trim()+""+"\",\"type\":\"text\"}]";
                    params.put("content",content);
                    //
//                    content = "{\"text\":\""+mEvaluate.getText().toString().trim()+""+"\",\"type\":\"text\"}";
//                    params.put("content",mEvaluate.getText().toString().trim()+"");
                    MyHandler.putTask(FreeInquiryActivity_AppendPatientProblem_new.this,new Task(new HttpResponseInterface() {
                        @Override
                        public void handMsg(String val) {
                            if(val!=null && val.length()>0 && !"null".equalsIgnoreCase(val)){
                                try{
                                    JSONObject jsonResult = new JSONObject(val);
                                    if(jsonResult.has("error")){
                                        if(jsonResult.getInt("error")==0){
                                            Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, "评价问题成功!", Toast.LENGTH_SHORT).show();
                                        }else{
                                            if(jsonResult.has("error_msg")){
                                                Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, jsonResult.getString("error_msg"), Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, "评价问题失败!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }else{
                                        Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, "评价接口返回数据异常!", Toast.LENGTH_SHORT).show();
                                    }
                                }catch (Exception e){
                                    MobclickAgent.reportError(FreeInquiryActivity_AppendPatientProblem_new.this,e);
                                    e.printStackTrace();
                                }
                            }else{
                                Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, "评价问题失败!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },HttpUrls.getFreeInquiryEvaluateProblem(),params,Task.TYPE_POST_DATA_PARAMS,null));
                }
            });
            mCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    evaluateDialg.dismiss();
                }
            });

            WindowManager.LayoutParams lp = evaluateDialg.getWindow().getAttributes();
            lp.width = QuanjiakanUtil.dip2px(BaseApplication.getInstances(), 280);
            lp.height = lp.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            evaluateDialg.setContentView(view,lp);
            evaluateDialg.setCanceledOnTouchOutside(false);
            evaluateDialg.show();

        }
    }

    private TextWatcher watcher = new TextWatcher() {
        private CharSequence temp = "";
        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub
            if (temp.length() > 0) {
                jmui_add_file_btn.setVisibility(View.GONE);
                jmui_send_msg_btn.setVisibility(View.VISIBLE);
            }else {
                jmui_add_file_btn.setVisibility(View.VISIBLE);
                jmui_send_msg_btn.setVisibility(View.GONE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
            temp = s;
        }

    };

    public void save2SmallImage(Uri data) {
        if (data == null) {
            return;
        }
        sourcePath = ImageUtils.uri2Path(data, FreeInquiryActivity_AppendPatientProblem_new.this);
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

    @Override
    protected void onStop() {
        super.onStop();
        if(appendAdapter!=null){
            appendAdapter.release();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_POST_PROBLEM:
                if (resultCode == RESULT_OK) {
                    finish();
                } else {
                }
                break;
            case IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    save2SmallImage(data.getData());
                }
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (QuanjiakanUtil.hasSdcard()) {
                        File tempFile = new File(BaseConstants.imageDir + IMAGE_FILE_NAME);
                        try {
                            Uri u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
                                    tempFile.getAbsolutePath(), null, null));
                            save2SmallImage(u);

                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            MobclickAgent.reportError(FreeInquiryActivity_AppendPatientProblem_new.this,e);
                        }
                    } else {
                        Toast.makeText(FreeInquiryActivity_AppendPatientProblem_new.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA:
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        ImageCropHandler.beginCrop(FreeInquiryActivity_AppendPatientProblem_new.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                        /**
                         * 是否进行裁剪：裁剪则进行上面的操作，否则，直接进行文件上传
                         */
                    }
                }
                break;
            case CommonRequestCode.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    ImageCropHandler.beginCrop(FreeInquiryActivity_AppendPatientProblem_new.this, data.getData());
                    /**
                     * sourcePath = ImageUtils.uri2Path(data, FreeInquiryActivity_AppendPatientProblem_new.this);
                     */
                }
            case CommonRequestCode.REQUEST_CROP:
                doCrop(requestCode,resultCode,data);
                break;
            default:
                break;
        }
    }


    public void doCrop(final int requestCode,final  int resultCode,final  Intent data){
        ImageCropHandler.handleCrop(null, resultCode, data, new IImageCropInterface() {
            @Override
            public void getFilePath(String path) {
                imagePath = null;
                mCurrentPhotoPath = path;
                dialog = QuanjiakanDialog.getInstance().getDialog(FreeInquiryActivity_AppendPatientProblem_new.this);

                Bitmap smallBitmap = BitmapUtil.getSmallBitmap(mCurrentPhotoPath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                smallBitmap.compress(Bitmap.CompressFormat.PNG, 25, baos);
                String filename = System.currentTimeMillis() + ".png";
                String file_path = ImageUtils.saveBitmapToStorage(filename, smallBitmap);
                HashMap<String, String> params = new HashMap<>();
                final File file = new File(file_path);
                params.put("file", file_path.toString());
                params.put("filename", file.getName());
                params.put("image", file.getAbsolutePath());
                Task task = new Task(new HttpResponseInterface() {

                    @Override
                    public void handMsg(String val) {
                        dialog.dismiss();
                        // TODO Auto-generated method stub
                        if(val!=null && !val.equals("") && val.toLowerCase().startsWith("{")){
                            //上传文件成功
                            try {
                                JSONObject json = new JSONObject(val);
                                if(json.has("code") && "200".equals(json.getString("code"))){
                                    uploadStatus = true;
                                    /**
                                     * 上传完成后获取的地址
                                     * json.getString("message")
                                     *
                                     *
                                     * 将地址传递到第二个页面
                                     */
                                    imagePath = json.getString("message");
                                    LogUtil.e("Last Image File Net Path:"+imagePath);
                                    jmui_chat_input_et.setText("");
                                    persistProblem();
                                }else{
                                    uploadStatus = false;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                MobclickAgent.reportError(FreeInquiryActivity_AppendPatientProblem_new.this,e);
                            }

                        }else {
                            //文件上传失败
                            uploadStatus = false;
                        }

                        if(file.exists()){
                            file.delete();
                        }
                    }
                }, HttpUrls.postFile()+"&storage=13", params, Task.TYPE_POST_FILE, null);
                MyHandler.putTask(FreeInquiryActivity_AppendPatientProblem_new.this,task);
            }
        });
    }


    private long startTime, time1, time2;
    float mTouchY1, mTouchY2, mTouchY;
    private final float MIN_CANCEL_DISTANCE = 300f;
    private static final int MIN_INTERVAL_TIME = 1000;// 1s

    private String voicePath;
    private ImageView mVolumeIv;
    private TextView mRecordHintTv;
    private final static int CANCEL_RECORD = 5;
    private final static int START_RECORD = 7;
    public static boolean mIsPressed = false;
    private static int[] res;
    private Timer timer = new Timer();
    private Timer mCountTimer;
    private boolean isTimerCanceled = false;
    private boolean mTimeUp = false;
    private MediaRecorder recorder;
    private ObtainDecibelThread mThread;
    private Dialog recordIndicator;
    private Handler mVolumeHandler;
    private File myRecAudioFile;
    private final static int RECORD_DENIED_STATUS = 1000;
    private Dialog dialog;
    private boolean uploadStatus;
    private String imagePath;
    private String sourcePath;
    private String mCurrentPhotoPath = null;

    private final MHandler myHandler = new MHandler();
    private class MHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_RECORD:
                    if (mIsPressed) {
                        initDialogAndStartRecord();
                    }
                    break;
            }
        }
    }

    private class ShowVolumeHandler extends Handler {

        private final WeakReference<View> mButton;

        public ShowVolumeHandler(View button) {
            mButton = new WeakReference<>(button);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            View controller = mButton.get();
            if (controller != null) {
                int restTime = msg.getData().getInt("restTime", -1);
                // 若restTime>0, 进入倒计时
                if (restTime > 0) {
                    mTimeUp = true;
                    android.os.Message msg1 = mVolumeHandler.obtainMessage();
                    msg1.what = 60 - restTime + 1;
                    Bundle bundle = new Bundle();
                    bundle.putInt("restTime", restTime - 1);
                    msg1.setData(bundle);
                    //创建一个延迟一秒执行的HandlerMessage，用于倒计时
                    mVolumeHandler.sendMessageDelayed(msg1, 1000);
                    mRecordHintTv.setText(String.format(FreeInquiryActivity_AppendPatientProblem_new.this.getString(IdHelper
                            .getString(FreeInquiryActivity_AppendPatientProblem_new.this,
                                    "jmui_rest_record_time_hint")), restTime));
                    // 倒计时结束，发送语音, 重置状态
                } else if (restTime == 0) {
                    finishRecord();
                    mTimeUp = false;
                    // restTime = -1, 一般情况
                } else {
                    // 没有进入倒计时状态
                    if (!mTimeUp) {
                        if (msg.what < CANCEL_RECORD) {
                            mRecordHintTv.setText(FreeInquiryActivity_AppendPatientProblem_new.this.getString(IdHelper
                                    .getString(FreeInquiryActivity_AppendPatientProblem_new.this, "jmui_move_to_cancel_hint")));
                        }
                        else {
                            mRecordHintTv.setText(FreeInquiryActivity_AppendPatientProblem_new.this.getString(IdHelper
                                    .getString(FreeInquiryActivity_AppendPatientProblem_new.this, "jmui_cancel_record_voice_hint")));
                        }
                        // 进入倒计时
                    } else {
                        if (msg.what == CANCEL_RECORD) {
                            mRecordHintTv.setText(FreeInquiryActivity_AppendPatientProblem_new.this.getString(IdHelper
                                    .getString(FreeInquiryActivity_AppendPatientProblem_new.this, "jmui_cancel_record_voice_hint")));
                            if (!mIsPressed) {
                                cancelRecord();
                            }
                        }
                    }
                    mVolumeIv.setImageResource(res[msg.what]);
                }
            }else{

            }
        }
    }

    File rootDir = Environment.getExternalStorageDirectory();
    private void initDialogAndStartRecord() {
        //存放录音文件目录
        if(rootDir==null){
            rootDir = Environment.getExternalStorageDirectory();
        }

        String fileDir = rootDir.getAbsolutePath() + File.separator + getPackageName() + "/voice";
        File destDir = new File(fileDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        //录音文件的命名格式
        myRecAudioFile = new File(fileDir,
                new DateFormat().format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)) + ".mp3");
        if (myRecAudioFile == null) {
            cancelTimer();
            stopRecording();
            Toast.makeText(this, getString(IdHelper.getString(this, "jmui_create_file_failed")),
                    Toast.LENGTH_SHORT).show();
        }
        LogUtil.e("Create file success file path: " + myRecAudioFile.getAbsolutePath());
        recordIndicator = new Dialog(this, R.style.jmui_record_voice_dialog);
        recordIndicator.setContentView(R.layout.jmui_dialog_record_voice);
        mVolumeIv = (ImageView) recordIndicator.findViewById(R.id.jmui_volume_hint_iv);
        mRecordHintTv = (TextView) recordIndicator.findViewById(R.id.jmui_record_voice_tv);
        mRecordHintTv.setText(this.getString(R.string.jmui_move_to_cancel_hint));
        startRecording();
        recordIndicator.show();
    }

    private void dismissDialog() {
        if (recordIndicator != null) {
            recordIndicator.dismiss();
        }
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            isTimerCanceled = true;
        }
        if (mCountTimer != null) {
            mCountTimer.cancel();
            mCountTimer.purge();
        }
    }

    private Timer createTimer() {
        timer = new Timer();
        isTimerCanceled = false;
        return timer;
    }


    private MP3Recorder mRecorder;
    private void startRecording() {
        try {
            //----------------------------------------
//            recorder = new MediaRecorder();
//            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//            recorder.setOutputFile(myRecAudioFile.getAbsolutePath());
            myRecAudioFile.createNewFile();
//            recorder.prepare();
//            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
//                @Override
//                public void onError(MediaRecorder mediaRecorder, int i, int i2) {
//                    Log.i("RecordVoiceController", "recorder prepare failed!");
//                }
//            });
//            recorder.start();

            mRecorder = new MP3Recorder(myRecAudioFile,mVolumeHandler);
            mRecorder.start();
            //----------------------------------------
            startTime = System.currentTimeMillis();
            mCountTimer = new Timer();
            mCountTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTimeUp = true;
                    android.os.Message msg = mVolumeHandler.obtainMessage();
                    msg.what = 55;
                    Bundle bundle = new Bundle();
                    bundle.putInt("restTime", 5);
                    msg.setData(bundle);
                    msg.sendToTarget();
                    mCountTimer.cancel();
                }
            }, 56000);

        } catch (IOException e) {
            e.printStackTrace();
            MobclickAgent.reportError(FreeInquiryActivity_AppendPatientProblem_new.this,e);
        } catch (RuntimeException e) {
            HandleResponseCode.onHandle(this, RECORD_DENIED_STATUS, false);
            MobclickAgent.reportError(FreeInquiryActivity_AppendPatientProblem_new.this,e);
            cancelTimer();
            dismissDialog();
            if (mThread != null) {
                mThread.exit();
                mThread = null;
            }
            if (myRecAudioFile != null) {
                myRecAudioFile.delete();
            }
            if(recorder!=null){
                recorder.release();
                recorder = null;
            }
        }

        mThread = new ObtainDecibelThread();
        mThread.start();

    }

    private void cancelRecord() {
        //可能在消息队列中还存在HandlerMessage，移除剩余消息
        mVolumeHandler.removeMessages(56, null);
        mVolumeHandler.removeMessages(57, null);
        mVolumeHandler.removeMessages(58, null);
        mVolumeHandler.removeMessages(59, null);
        mTimeUp = false;
        cancelTimer();
        stopRecording();
        if (recordIndicator != null) {
            recordIndicator.dismiss();
        }
        if (myRecAudioFile != null) {
            myRecAudioFile.delete();
        }
    }

    private void finishRecord() {
        voicePath = null;
        cancelTimer();
        stopRecording();
        if (recordIndicator != null) {
            recordIndicator.dismiss();
        }

        long intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            Toast.makeText(this, getString(IdHelper.getString(this,
                    "jmui_time_too_short_toast")), Toast.LENGTH_SHORT).show();
            myRecAudioFile.delete();
        } else {
            if (myRecAudioFile != null && myRecAudioFile.exists()) {
                MediaPlayer mp = new MediaPlayer();
                try {
                    FileInputStream fis = new FileInputStream(myRecAudioFile);
                    mp.setDataSource(fis.getFD());
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                    MobclickAgent.reportError(FreeInquiryActivity_AppendPatientProblem_new.this,e);
                }
                //某些手机会限制录音，如果用户拒接使用录音，则需判断mp是否存在
                if (mp != null) {
                    int duration = mp.getDuration() / 1000;//即为时长 是s
                    if (duration < 1) {
                        duration = 1;
                    } else if (duration > 60) {
                        duration = 60;
                    }
                    try {
                        /**
                         * 将文件上传，并将地址传递到服务器，并传递到另一个页面
                         */
                        LogUtil.e("录音完成");
                        dialog = QuanjiakanDialog.getInstance().getDialog(FreeInquiryActivity_AppendPatientProblem_new.this);
                        HashMap<String, String> params = new HashMap<>();
                        params.put("file", myRecAudioFile.toString());
                        params.put("filename", myRecAudioFile.getName());
                        params.put("audio", myRecAudioFile.getAbsolutePath());
                        Task task = new Task(new HttpResponseInterface() {

                            @Override
                            public void handMsg(String val) {
                                dialog.dismiss();
                                // TODO Auto-generated method stub
                                if(val!=null && !val.equals("") && val.toLowerCase().startsWith("{")){
                                    //上传文件成功
                                    try {
                                        JSONObject json = new JSONObject(val);
                                        if(json.has("code") && "200".equals(json.getString("code"))){
                                            uploadStatus = true;
                                            /**
                                             * 上传完成后获取的地址
                                             * json.getString("message")
                                             *
                                             *
                                             * 将地址传递到第二个页面
                                             */
                                            voicePath = json.getString("message");
                                            LogUtil.e("Last Voice File Net Path:"+voicePath);
                                            jmui_chat_input_et.setText("");


                                            /**
                                             * 不经过追问接口，直接更新UI显示
                                             */
                                            persistProblem();
                                        }else{
                                            uploadStatus = false;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        MobclickAgent.reportError(FreeInquiryActivity_AppendPatientProblem_new.this,e);
                                    }

                                }else {
                                    //文件上传失败
                                    uploadStatus = false;
                                }
                            }
                        }, HttpUrls.postFile()+"&storage=14", params, Task.TYPE_POST_FILE, null);

                        MyHandler.putTask(FreeInquiryActivity_AppendPatientProblem_new.this,task);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MobclickAgent.reportError(FreeInquiryActivity_AppendPatientProblem_new.this,e);
                    }
                } else {
                    Toast.makeText(this, this.getString(IdHelper.getString(this,
                            "jmui_record_voice_permission_request")), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    //停止录音，隐藏录音动画
    private void stopRecording() {
        if (mThread != null) {
            mThread.exit();
            mThread = null;
        }
        releaseRecorder();
    }

    public void releaseRecorder() {
        if (recorder != null) {
            try {
                recorder.stop();
            }catch (Exception e){
                MobclickAgent.reportError(FreeInquiryActivity_AppendPatientProblem_new.this,e);
                Log.d("RecordVoice", "Catch exception: stop recorder failed!");
            }finally {
                recorder.release();
                recorder = null;
            }
        }

        if(mRecorder!=null){
            mRecorder.stop();
        }
    }

    public class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    MobclickAgent.reportError(FreeInquiryActivity_AppendPatientProblem_new.this,e);
                }
                if (recorder == null || !running) {
                    break;
                }
                try {
                    int x = recorder.getMaxAmplitude();
                    if (x != 0) {
                        int f = (int) (10 * Math.log(x) / Math.log(10));
                        if (f < 20) {
                            mVolumeHandler.sendEmptyMessage(0);
//                            LogUtil.e("发送的动画------recorder.getMaxAmplitude():"+x + "f(f < 20):"+f+"    ****   sendEmptyMessage(0)");
                        } else if (f < 26) {
                            mVolumeHandler.sendEmptyMessage(1);
//                            LogUtil.e("发送的动画------recorder.getMaxAmplitude():"+x + "f(f < 26):"+f+"    ****   sendEmptyMessage(1)");
                        } else if (f < 32) {
                            mVolumeHandler.sendEmptyMessage(2);
//                            LogUtil.e("发送的动画------recorder.getMaxAmplitude():"+x + "f(f < 32):"+f+"    ****   sendEmptyMessage(2)");
                        } else if (f < 38) {
                            mVolumeHandler.sendEmptyMessage(3);
//                            LogUtil.e("发送的动画------recorder.getMaxAmplitude():"+x + "f(f < 38):"+f+"    ****   sendEmptyMessage(3)");
                        } else {
                            mVolumeHandler.sendEmptyMessage(4);
//                            LogUtil.e("发送的动画------recorder.getMaxAmplitude():"+x + "f(f >= 38):"+f+"    ****   sendEmptyMessage(4)");
                        }
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    MobclickAgent.reportError(FreeInquiryActivity_AppendPatientProblem_new.this,e);
                }

            }
        }

    }


    private InputMethodManager imm;
    public void showSoftInput(View view){
//        isOpenSoftInput();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if(imm==null){
            imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public void hideSoftInput(View view){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if(imm==null){
            imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public boolean isOpenSoftInput(){
        if(imm==null){
            imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        boolean bool = imm.isActive();
        LogUtil.e("imm.isActive():"+bool);
        if(bool){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return bool;
    }

}