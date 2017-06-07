package com.androidquanjiakan.activity.setting.devices;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.datahandler.BindDeviceHandler;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.LogUtil;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;
import com.zxing.qrcode.BindDeviceActivity;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class AddDevicesBindActivity_StepOne extends BaseActivity implements OnClickListener {

    protected ImageButton ibtn_back;
    protected TextView tv_title;


    private ImageView image;
    private EditText bind_device_2dcode_value;
    private TextView bind_device_scan_2dcode;

    private Button btn_submit;

    private static final int REQUEST_SCAN = 1010;
    private static final int REQUEST_FINISH = 1011;

    private static final int MSG_CANCEL = 1020;
    private static final int MSG_CANCEL_UNBIND = 1021;

    private boolean bindSuccess;

    private String deviceid;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NattyProtocolFilter.DISPLAY_UPDATE_CONTROL_BIND_RESULT:
                    //0：代表成功，1：代表 UserId不存在，2：代表DeviceId不存在，3：代表UserId与DeviceId已经绑定过了
                    String res = msg.getData().getString("Bind");
                    bindSuccess = false;
                    LogUtil.e(" 绑定流程第一步：RESULT -- "+res);
                    if("0".equals(res)){
//                        bindDevice(bind_device_2dcode_value.getText().toString().trim());
                        /**
                         * 确认绑定成功后
                         */
                        bindSuccess = true;
                        BindDeviceHandler.insertValue(deviceid, "", "");
                        final Intent intent = new Intent(AddDevicesBindActivity_StepOne.this,AddDevicesBindActivity_StepTwo.class);
                        intent.putExtra(AddDevicesBindActivity_StepOne.PARAM_ID,deviceid);
                        /**
                         * 调用创建/添加群
                         */

                        HashMap<String, String> params = new HashMap<>();
                        params.put("user_id",BaseApplication.getInstances().getUser_id());
                        params.put("did",deviceid);
                        params.put("type","1");
                        MyHandler.putTask(new Task(new HttpResponseInterface() {
                            @Override
                            public void handMsg(String val) {
                                Toast.makeText(AddDevicesBindActivity_StepOne.this, "绑定设备成功", Toast.LENGTH_SHORT).show();
                                startActivityForResult(intent,REQUEST_FINISH);
                            }
                        },HttpUrls.joinGroupAfterBindWatch(),params,Task.TYPE_POST_DATA_PARAMS,null));

                    }else if("1".equals(res)){
                        BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"该用户ID不存在!");
                    }else if("2".equals(res)){
                        BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"该设备ID不存在!");
                    }else if("3".equals(res)){
                        isSuccess = true;
//                        bindDevice(bind_device_2dcode_value.getText().toString().trim());

                        final Intent intent = new Intent(AddDevicesBindActivity_StepOne.this,AddDevicesBindActivity_StepTwo.class);
                        intent.putExtra(AddDevicesBindActivity_StepOne.PARAM_ID,deviceid);
                        HashMap<String, String> params = new HashMap<>();
                        params.put("user_id",BaseApplication.getInstances().getUser_id());
                        params.put("did",deviceid);
                        params.put("type","1");
                        MyHandler.putTask(new Task(new HttpResponseInterface() {
                            @Override
                            public void handMsg(String val) {
                                BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"该设备已绑定过,无需重新绑定!");
                                startActivityForResult(intent,REQUEST_FINISH);
                            }
                        },HttpUrls.joinGroupAfterBindWatch(),params,Task.TYPE_POST_DATA_PARAMS,null));


                    }else if("4".equals(res)){
                        BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"设备没有激活!");
                    }else{
                        BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"未知的返回结果!");
                    }

                    /**
                     * 取消定时器
                     *
                     * 关闭等待对话框
                     */
                    if(timer!=null){
                        timer.cancel();
                    }
                    if(wait!=null){
                        wait.dismiss();
                    }
                    break;
                case MSG_CANCEL:
                    /**
                     * 取消定时器
                     *
                     * 关闭等待对话框
                     */
                    LogUtil.e(" 绑定流程第一步：RESULT -- MSG_CANCEL");
                    if(timer!=null){
                        timer.cancel();
                    }

                    if(wait!=null){
                        wait.dismiss();
                    }

                    BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"数据请求失败,请检查手机网络连接状态!");
//                    startUnbindTimer();
                    break;
                case MSG_CANCEL_UNBIND:

                    /**
                     * 取消定时器
                     *
                     * 关闭等待对话框
                     */
                    if(cancelTimer!=null){
                        cancelTimer.cancel();
                    }
                    if(mDialog!=null){
                        mDialog.dismiss();
                    }
                    LogUtil.e("*******取消绑定服务器无响应!");
                    break;
                case NattyProtocolFilter.DISPLAY_UPDATE_CONTROL_RECONNECTED:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_device_bind_one);

        initView();
    }

    protected void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("绑定ID");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);


        image = (ImageView) findViewById(R.id.image);//352315052834187
        bind_device_2dcode_value = (EditText) findViewById(R.id.bind_device_2dcode_value);
//        bind_device_2dcode_value.setText("352315052834187");
        bind_device_scan_2dcode = (TextView) findViewById(R.id.bind_device_scan_2dcode);

        btn_submit = (Button) findViewById(R.id.btn_submit);
        bind_device_scan_2dcode.setOnClickListener(this);
        btn_submit.setOnClickListener(this);

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
    protected void onDestroy() {
        super.onDestroy();
        dismissAll();
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.ibtn_back:
                if(bindSuccess){
                    setResult(RESULT_OK);
                }
                finish();
                break;
            case R.id.btn_submit:
                /**
                 * TODO 提交设备绑定---ID是否有特点，是否需要进行初步的验证
                 */
                if(isSuccess){
                    if(bind_device_2dcode_value.length()<1){
                        BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"请输入ID，或扫描二维码获取!");
                        return;
                    }
                    if(bind_device_2dcode_value.getText().toString().trim().length()!=15){
                        BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"ID格式不符合规范,应为15位!");
                        return;
                    }
                    if(!CheckUtil.isNumberChar(bind_device_2dcode_value.getText().toString().trim())){
                        BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"ID格式不符合规范,应为数字!");
                        return;
                    }
                    bindDevice(bind_device_2dcode_value.getText().toString().trim());
                }else{
                    if(bind_device_2dcode_value.length()<1){
                        BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"请输入ID，或扫描二维码获取!");
                        return;
                    }
                    if(bind_device_2dcode_value.getText().toString().trim().length()!=15){
                        BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"ID格式不符合规范,应为15位!");
                        return;
                    }
                    if(!CheckUtil.isNumberChar(bind_device_2dcode_value.getText().toString().trim())){
                        BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"ID格式不符合规范,应为数字!");
                        return;
                    }
                    bindDevice(bind_device_2dcode_value.getText().toString().trim());
                }
                break;
            case R.id.bind_device_scan_2dcode:
                /**
                 * 扫描获取二维码
                 */
                Intent intent = new Intent(this, BindDeviceActivity.class);
                intent.putExtra(BaseConstants.PARAMS_SHOW_HINT,true);
                startActivityForResult(intent, REQUEST_SCAN);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SCAN:
                if (resultCode == RESULT_OK) {
                    String deviceid = data.getStringExtra(DEVICEID);
                    if (deviceid != null && deviceid.length() > 0) {
                        /**
                         * TODO 根据传入的网址，截取其中的设备ID【ID形式暂时没有定下来】
                         */
                        if(deviceid.indexOf("IMEI=")>0 || deviceid.toLowerCase().indexOf("imei=")>0){
                            if(deviceid.indexOf("IMEI=")>0){
                                bind_device_2dcode_value.setText(deviceid.substring(deviceid.indexOf("IMEI=")+5));
                            }else{
                                bind_device_2dcode_value.setText(deviceid.substring(deviceid.indexOf("imei=")+5));
                            }

                        }else{
//                            bind_device_2dcode_value.setText(deviceid);
                        }
                        LogUtil.w("二维码设备ID:" + deviceid);
                    } else {
                        Toast.makeText(AddDevicesBindActivity_StepOne.this, "二维码解析失败,请重试!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case REQUEST_FINISH:
                if(resultCode==RESULT_OK){
                    //TODO 必要时需要进行数据的刷新
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public static final String DEVICEID = "deviceid";

    protected void bindDevice(final String deviceid) {
        if(isSuccess){
            BindDeviceHandler.insertValue(deviceid, "", "");
            Intent intent = new Intent(AddDevicesBindActivity_StepOne.this,AddDevicesBindActivity_StepTwo.class);
            intent.putExtra(AddDevicesBindActivity_StepOne.PARAM_ID,deviceid);
            startActivityForResult(intent,REQUEST_FINISH);
        }else{
            if (CheckUtil.isEmpty(deviceid)) {
                BaseApplication.getInstances().toast(AddDevicesBindActivity_StepOne.this,"无效的设备ID,请重新输入或获取!");
                return;
            }
            if(!BaseApplication.getInstances().isSDKConnected()){
                BaseApplication.getInstances().toastLong(AddDevicesBindActivity_StepOne.this,"已与手表服务器断开连接!");
                long devid = Long.parseLong(deviceid, 16);
//                BaseApplication.getInstances().getNattyClient().ntyBindClient(devid);
            }else{
                this.deviceid = deviceid;//
//                this.deviceid = "352315052834187";
//                long devid = Long.parseLong(deviceid, 16);
//                BaseApplication.getInstances().getNattyClient().ntyBindClient(devid);

                /**
                 * 开定时器重复发送请求，
                 */
                startBindTimer();

            }
        }
    }

    private Dialog wait;
    private Dialog mDialog;
    private boolean isSuccess = false;
    public static final String PARAM_ID = "id";

    private int timeCount = 0;
    private Timer timer = null;
    private TimerTask timerTask = null;

    private int untimeCount = 0;
    private Timer cancelTimer = null;
    private TimerTask cancelTimerTask = null;

    public void startBindTimer(){
        if (wait != null && wait.isShowing()) {
            wait.dismiss();
            wait = null;
        }
        wait = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
        WindowManager.LayoutParams lp = wait.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 80);
        lp.height = QuanjiakanUtil.dip2px(this, 80);
        lp.gravity = Gravity.CENTER;
        wait.setContentView(view, lp);
        wait.setCanceledOnTouchOutside(false);
        wait.show();

        timeCount = 0;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                LogUtil.e("绑定设备 TimerTask:"+timeCount);
                if(timeCount<4){
                    timeCount++;
                    long devid = Long.parseLong(deviceid, 16);
//                    BaseApplication.getInstances().getNattyClient().ntyBindClient(devid);
                }else{
                    mHandler.sendEmptyMessage(MSG_CANCEL);
                }
            }
        };
        timer.schedule(timerTask,0,10000);
    }

    public void startUnbindTimer(){
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 80);
        lp.height = QuanjiakanUtil.dip2px(this, 80);
        lp.gravity = Gravity.CENTER;
        mDialog.setContentView(view, lp);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        untimeCount = 0;
        cancelTimer = new Timer();
        cancelTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(untimeCount<4){
                    untimeCount++;
                    long devid = Long.parseLong(deviceid, 16);
                    BaseApplication.getInstances().getNattyClient().ntyUnBindClient(devid);
                }else{
                    mHandler.sendEmptyMessage(MSG_CANCEL_UNBIND);
                }
            }
        };
        cancelTimer.schedule(cancelTimerTask,0,10000);
    }


    public void dismissAll(){
        if(wait!=null){
            wait.dismiss();
            wait = null;
        }

        if(mDialog!=null){
            mDialog.dismiss();
            mDialog = null;
        }

        if(cancelTimer!=null){
            cancelTimer.cancel();
        }

        if(timer!=null){
            timer.cancel();
        }
    }


}
