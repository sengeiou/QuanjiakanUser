package com.androidquanjiakan.activity.index.free;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.setting.modify_info.ModifyInfoActivity;
import com.androidquanjiakan.adapter.FreeInquiryListAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.entity.BaseHttpResultEntity_List;
import com.androidquanjiakan.entity.FreeInquiryProblemEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.androidquanjiakan.util.BitmapUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.view.CircleTransformation;
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
import com.quanjiakanuser.widget.ProblemDetailAdapter;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.jmessage.android.uikit.chatting.utils.FileHelper;
import cn.jmessage.android.uikit.chatting.utils.HandleResponseCode;
import cn.jmessage.android.uikit.chatting.utils.IdHelper;

public class FreeInquiryActivity_CreatePatientProblem extends BaseActivity implements OnClickListener {

    private ListView listView;
    private ImageButton ibtn_back;
    private TextView tv_title,tv_next;
    private TextView menu_text;

    private LinearLayout keyboard;
    private LinearLayout voice;
    private LinearLayout picture;


    private EditText et_text;

    private TextView input_number_hint;

    private boolean forceStatus;


    private Context context;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final String IMAGE_FILE_NAME = "quanjiakang.jpg";
    private final int REQUEST_POST_PROBLEM = 10010;
    private List<JsonObject> list = new ArrayList<>();

    /**
     *
     */
    private PullToRefreshListView listview;
    private ImageView nonedata;
    private TextView nonedatahint;
    private List<FreeInquiryProblemEntity> mData;
    private FreeInquiryListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = FreeInquiryActivity_CreatePatientProblem.this;
        setContentView(R.layout.layout_create_problem);
        initTitle();
        initView();
        initListview();
    }

    private void initListview(){
        mData = new ArrayList<>();
        nonedata = (ImageView) findViewById(R.id.nonedata);
        nonedatahint = (TextView) findViewById(R.id.nonedatahint);
        showNoneData(false);
        listview = (PullToRefreshListView) findViewById(R.id.listview);
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        mAdapter = new FreeInquiryListAdapter(FreeInquiryActivity_CreatePatientProblem.this,mData);
        listview.setAdapter(mAdapter);
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        showNoneData(true);
        loadData();
    }

    public void showNoneData(boolean isShow){
        if(isShow){
            nonedatahint.setText("暂无消息");
            nonedata.setVisibility(View.VISIBLE);
            nonedatahint.setVisibility(View.VISIBLE);
        }else{
            nonedata.setVisibility(View.GONE);
            nonedatahint.setVisibility(View.GONE);
        }
    }


    BaseHttpResultEntity_List<FreeInquiryProblemEntity> inquiryList;

    /**
     * TODO 加载最近的10条访问记录
     */
    public void loadData(){
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                listview.onRefreshComplete();
                mData.clear();
                if(val!=null && val.length()>0 &&!"null".equals(val.toLowerCase())){
                    inquiryList = (BaseHttpResultEntity_List<FreeInquiryProblemEntity>) SerialUtil.jsonToObject(val,
                            new TypeToken<BaseHttpResultEntity_List<FreeInquiryProblemEntity>>(){}.getType());
                    if(inquiryList.getRows()!=null && inquiryList.getRows().size()>0){
                        showNoneData(false);

                        List<FreeInquiryProblemEntity> tempContainer = inquiryList.getRows();
                        //TODO 由于获取的数据是顺序排列，需要进行重排序
                        Collections.sort(tempContainer, new Comparator<FreeInquiryProblemEntity>() {
                            @Override
                            public int compare(FreeInquiryProblemEntity patientProblemInfoEntity, FreeInquiryProblemEntity t1) {
                                if(Long.parseLong(patientProblemInfoEntity.getCreated_time_ms())>Long.parseLong(t1.getCreated_time_ms())){
                                    return -1;
                                }else{
                                    return 1;
                                }
                            }
                        });
                        //TODO 过滤出最新的10条数据
                        if(tempContainer.size()>10){
                            for(int i = 0;i<10;i++){
                                mData.add(tempContainer.get(i));
                            }
                        }else{
                            mData.addAll(tempContainer);
                        }

                        mAdapter.resetData(mData);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        showNoneData(true);
                        mAdapter.resetData(mData);
                        mAdapter.notifyDataSetChanged();
                    }
                }else{
                    showNoneData(true);
                    mAdapter.resetData(mData);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }, HttpUrls.getGetChunyuProblemDetail(),null,Task.TYPE_GET_STRING_NOCACHE,QuanjiakanDialog.getInstance().getDialog(this)));
    }

    public void initTitle(){
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我要咨询");
        menu_text = (TextView) findViewById(R.id.menu_text);
        menu_text.setText("下一步");
        menu_text.setVisibility(View.VISIBLE);
        menu_text.setOnClickListener(this);
    }

    protected void initView() {


        input_number_hint = (TextView) findViewById(R.id.input_number_hint);
        input_number_hint.setText("0/1000");
        et_text = (EditText) findViewById(R.id.et_text);
        et_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                forceStatus = hasFocus;
                if(hasFocus){
                    showSoftInput(et_text);
                }else{
                    hideSoftInput(et_text);
                }
            }
        });
        et_text.addTextChangedListener(new TextWatcher() {
            private CharSequence temp = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(temp==null || temp.length()<1){
                    input_number_hint.setText("0/1000");
                }else{
                    input_number_hint.setText(temp.length()+"/1000");
                }
            }
        });
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_next.setVisibility(View.GONE);
        tv_next.setText("下一步");
        tv_next.setOnClickListener(this);

        keyboard = (LinearLayout) findViewById(R.id.keyboard);
        voice = (LinearLayout) findViewById(R.id.voice);
        picture = (LinearLayout) findViewById(R.id.picture);

        mVolumeHandler = new ShowVolumeHandler(voice);
        res = new int[]{IdHelper.getDrawable(this, "jmui_mic_1"), IdHelper.getDrawable(this, "jmui_mic_2"),
                IdHelper.getDrawable(this, "jmui_mic_3"),IdHelper.getDrawable(this, "jmui_mic_4"),
                IdHelper.getDrawable(this, "jmui_mic_5"), IdHelper.getDrawable(this, "jmui_cancel_record")};

        keyboard.setOnClickListener(this);
        picture.setOnClickListener(this);
        voice.setOnTouchListener(new View.OnTouchListener() {
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
                            Toast.makeText(FreeInquiryActivity_CreatePatientProblem.this, FreeInquiryActivity_CreatePatientProblem.this.
                                    getString(IdHelper.getString(FreeInquiryActivity_CreatePatientProblem.this,
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
                                mThread = new ObtainDecibelThread();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_POST_PROBLEM:
                if (resultCode == RESULT_OK) {
//                    finish();
                    loadData();
                    et_text.setText("");
                } else {
//                    et_text.setText("");
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
                        }
                    } else {
                        Toast.makeText(FreeInquiryActivity_CreatePatientProblem.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA:
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        ImageCropHandler.beginCrop(FreeInquiryActivity_CreatePatientProblem.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                        /**
                         * 是否进行裁剪：裁剪则进行上面的操作，否则，直接进行文件上传
                         */
                    }
                }
                break;
            case CommonRequestCode.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    ImageCropHandler.beginCrop(FreeInquiryActivity_CreatePatientProblem.this, data.getData());
                    /**
                     * sourcePath = ImageUtils.uri2Path(data, FreeInquiryActivity_CreatePatientProblem.this);
                     */
                }
            case CommonRequestCode.REQUEST_CROP:
                ImageCropHandler.handleCrop(null, resultCode, data, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        imagePath = null;
                        mCurrentPhotoPath = path;
                        dialog = QuanjiakanDialog.getInstance().getDialog(FreeInquiryActivity_CreatePatientProblem.this);
                        final File file = new File(path);

                        Bitmap smallBitmap = BitmapUtil.getSmallBitmap(mCurrentPhotoPath,320,320);
                        String filename = System.currentTimeMillis() + ".png";
                        String file_path = ImageUtils.saveBitmapToStorage(filename, smallBitmap);


                        HashMap<String, String> params = new HashMap<>();
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

                                        }else{
                                            uploadStatus = false;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }else {
                                    //文件上传失败
                                    uploadStatus = false;
                                }
                                if(file.exists()){
                                    file.delete();
                                }
                            }
                        }, HttpUrls.postFile(), params, Task.TYPE_POST_FILE, null);
                        MyHandler.putTask(FreeInquiryActivity_CreatePatientProblem.this,task);
                    }
                });
                break;
            default:
                break;
        }
    }


    public void save2SmallImage(Uri data) {
        if (data == null) {
            return;
        }
        sourcePath = ImageUtils.uri2Path(data, FreeInquiryActivity_CreatePatientProblem.this);
    }

    protected void createPatientData() {
        if(et_text.getText().toString().length()<1 ){
            BaseApplication.getInstances().toast(FreeInquiryActivity_CreatePatientProblem.this,"请输入问题!");
            return ;
        }

        if(voicePath!=null && !voicePath.toLowerCase().startsWith("http")){
            BaseApplication.getInstances().toast(FreeInquiryActivity_CreatePatientProblem.this,"上传语音失败，请重试!");
            return ;
        }

        if(imagePath!=null && !imagePath.toLowerCase().startsWith("http")){
            BaseApplication.getInstances().toast(FreeInquiryActivity_CreatePatientProblem.this,"上传图片失败，请重试!");
            return ;
        }

        if(et_text.getText().toString().length()>0 && et_text.getText().toString().length()<10 ){
            BaseApplication.getInstances().toast(FreeInquiryActivity_CreatePatientProblem.this,"字数不足10,请继续补充");
            return ;
        }

        if(et_text.getText().toString().length()>1000){
            BaseApplication.getInstances().toast(FreeInquiryActivity_CreatePatientProblem.this,"字数超过1000,请适当删减");
            return ;
        }

        JsonArray content = new JsonArray();
        JsonObject object = new JsonObject();

        if(et_text.getText().toString().length()>0){
            object.addProperty("type", "text");
            object.addProperty("text", et_text.getText().toString());
        }else if(voicePath!=null){
            object.addProperty("type", "text");
            object.addProperty("file", voicePath);
        }else if(imagePath!=null){
            object.addProperty("type", "image");
            object.addProperty("file", imagePath);
        }

        content.add(object);
        Intent intent = new Intent(context, FreeInquiryActivity_SendPatientData.class);
        intent.putExtra("content", content + "");
        intent.putExtra("doctor_id", getIntent().getStringExtra("doctor_id"));
        intent.putExtra("text", et_text.getText().toString());
        intent.putExtra("clinic", /*getIntent().getStringExtra("clinic")*/"");
        startActivityForResult(intent, REQUEST_POST_PROBLEM);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()){
            case R.id.menu_text:
                createPatientData();
                break;
            case R.id.tv_next:
                createPatientData();
                break;
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.keyboard:
                et_text.requestFocus();
                showSoftInput(et_text);
                break;
            case R.id.picture:
                showOptionsDialog();
                break;
        }
    }

    protected void showOptionsDialog() {
        List<String> strings = new ArrayList<>();
        strings.add(getString(R.string.common_album));
        strings.add(getString(R.string.common_shot));
        QuanjiakanDialog.getInstance().getListDialogDefineHeight(FreeInquiryActivity_CreatePatientProblem.this, strings, getString(R.string.common_album_select), new QuanjiakanDialog.MyDialogCallback() {

            @Override
            public void onItemClick(int position, Object object) {
                // TODO Auto-generated method stub
                switch (position) {
                    case 0:
                        ImageCropHandler.pickImage(FreeInquiryActivity_CreatePatientProblem.this);
                        break;
                    case 1:
                        ImageCropHandler.getImageFromCamera(FreeInquiryActivity_CreatePatientProblem.this, new IImageCropInterface() {
                            @Override
                            public void getFilePath(String path) {
                                mCurrentPhotoPath = path;
                            }
                        });
                        break;
                }
            }
        }, null);
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
//        isOpenSoftInput();
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
        if(bool){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return bool;
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
                    mRecordHintTv.setText(String.format(FreeInquiryActivity_CreatePatientProblem.this.getString(IdHelper
                            .getString(FreeInquiryActivity_CreatePatientProblem.this,
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
                            mRecordHintTv.setText(FreeInquiryActivity_CreatePatientProblem.this.getString(IdHelper
                                    .getString(FreeInquiryActivity_CreatePatientProblem.this, "jmui_move_to_cancel_hint")));
                        }
                        else {
                            mRecordHintTv.setText(FreeInquiryActivity_CreatePatientProblem.this.getString(IdHelper
                                    .getString(FreeInquiryActivity_CreatePatientProblem.this, "jmui_cancel_record_voice_hint")));
                        }
                        // 进入倒计时
                    } else {
                        if (msg.what == CANCEL_RECORD) {
                            mRecordHintTv.setText(FreeInquiryActivity_CreatePatientProblem.this.getString(IdHelper
                                    .getString(FreeInquiryActivity_CreatePatientProblem.this, "jmui_cancel_record_voice_hint")));
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

    private void initDialogAndStartRecord() {
        //存放录音文件目录
        File rootDir = Environment.getExternalStorageDirectory();
        String fileDir = rootDir.getAbsolutePath() + File.separator + getPackageName() + "/voice";
        File destDir = new File(fileDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        //录音文件的命名格式
        myRecAudioFile = new File(fileDir,
                new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".mp3");
        if (myRecAudioFile == null) {
            cancelTimer();
            stopRecording();
            Toast.makeText(this, getString(IdHelper.getString(this, "jmui_create_file_failed")),
                    Toast.LENGTH_SHORT).show();
        }
        Log.i("FileCreate", "Create file success file path: " + myRecAudioFile.getAbsolutePath());
        recordIndicator = new Dialog(this, IdHelper.getStyle(this, "jmui_record_voice_dialog"));
        recordIndicator.setContentView(IdHelper.getLayout(this, "jmui_dialog_record_voice"));
        mVolumeIv = (ImageView) recordIndicator.findViewById(IdHelper.getViewID(this, "jmui_volume_hint_iv"));
        mRecordHintTv = (TextView) recordIndicator.findViewById(IdHelper.getViewID(this, "jmui_record_voice_tv"));
        mRecordHintTv.setText(this.getString(IdHelper.getString(this, "jmui_move_to_cancel_hint")));
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

    private void startRecording() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile(myRecAudioFile.getAbsolutePath());
            myRecAudioFile.createNewFile();
            recorder.prepare();
            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mediaRecorder, int i, int i2) {
                    Log.i("RecordVoiceController", "recorder prepare failed!");
                }
            });
            recorder.start();
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
        } catch (RuntimeException e) {
            HandleResponseCode.onHandle(this, RECORD_DENIED_STATUS, false);
            cancelTimer();
            dismissDialog();
            if (mThread != null) {
                mThread.exit();
                mThread = null;
            }
            if (myRecAudioFile != null) {
                myRecAudioFile.delete();
            }
            recorder.release();
            recorder = null;
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
                        dialog = QuanjiakanDialog.getInstance().getDialog(FreeInquiryActivity_CreatePatientProblem.this);
                        HashMap<String, String> params = new HashMap<>();
                        params.put("file", myRecAudioFile.toString());
                        params.put("filename", myRecAudioFile.getName());
                        params.put("image", myRecAudioFile.getAbsolutePath());
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
                                        }else{
                                            uploadStatus = false;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }else {
                                    //文件上传失败
                                    uploadStatus = false;
                                }
                            }
                        }, HttpUrls.postFile(), params, Task.TYPE_POST_FILE, null);
                        MyHandler.putTask(FreeInquiryActivity_CreatePatientProblem.this,task);
                    } catch (Exception e) {
                        e.printStackTrace();
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
                Log.d("RecordVoice", "Catch exception: stop recorder failed!");
            }finally {
                recorder.release();
                recorder = null;
            }
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
                        } else if (f < 26) {
                            mVolumeHandler.sendEmptyMessage(1);
                        } else if (f < 32) {
                            mVolumeHandler.sendEmptyMessage(2);
                        } else if (f < 38) {
                            mVolumeHandler.sendEmptyMessage(3);
                        } else {
                            mVolumeHandler.sendEmptyMessage(4);
                        }
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}