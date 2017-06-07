package com.androidquanjiakan.activity.index.bind;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.watch_child.WatchChildEntryActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.entity.WatchMapDevice;
import com.androidquanjiakan.entity.WatchMapDevice_DeviceInfo;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DevicesBindStateActivity extends BaseActivity {


    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.state_img)
    ImageView stateImg;
    @BindView(R.id.state_hint)
    TextView stateHint;
    @BindView(R.id.state_hint_subtext)
    TextView stateHintSubtext;
    @BindView(R.id.state_btn)
    TextView stateBtn;

    public static final int WAIT = 1;
    public static final int ACCESS = 2;
    public static final int REJECT = 3;
    public static final int NET_ERROR = 4;

    private int state = 0;
    private String device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_device_bind_state);
        Intent intent = getIntent();
        state = intent.getIntExtra(BaseConstants.PARAMS_STATE, 0);
        device_id = intent.getStringExtra(BaseConstants.PARAMS_ID);
        if(device_id==null || device_id.length()!=15){//非16进制设备ID
            BaseApplication.getInstances().toast(DevicesBindStateActivity.this,"传入参数有误!");
            finish();
            return;
        }
        ButterKnife.bind(this);
        initTitle();
        if(state==ACCESS){
            getWatchList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //TODO 根据当前状态，展示对应的状态显示
    public void initTitle() {
        ibtnBack.setVisibility(View.VISIBLE);
        switch (state) {
            case WAIT:
                tvTitle.setText("等待审核");
                stateHint.setText("稍等片刻");
                stateHintSubtext.setVisibility(View.VISIBLE);
                stateHintSubtext.setText("您的申请已发送,请等待管理员审核确认!");
                stateBtn.setVisibility(View.GONE);
                stateImg.setImageResource(R.drawable.wait_pic_moment);
                break;
            case ACCESS:
                tvTitle.setText("审核通过");
                stateHint.setText("恭喜,您的申请已通过!");
                stateHintSubtext.setVisibility(View.GONE);
                stateBtn.setVisibility(View.VISIBLE);
                stateBtn.setText("点击进入");
                stateBtn.setTextColor(getResources().getColor(R.color.color_00b2b2));
                stateImg.setImageResource(R.drawable.wait_pic_ok);
                break;
            case REJECT:
                tvTitle.setText("等待审核");
                stateHint.setText("很遗憾,您的申请被管理员拒绝");
                stateHintSubtext.setVisibility(View.GONE);
                stateBtn.setVisibility(View.VISIBLE);
                stateBtn.setText("点击屏幕重试");
                stateBtn.setTextColor(getResources().getColor(R.color.color_00b2b2));
                stateImg.setImageResource(R.drawable.wait_pic_no);
                break;
            case NET_ERROR:
                tvTitle.setText("等待审核");
                stateHint.setText("网络无法连接");
                stateHintSubtext.setVisibility(View.GONE);
                stateBtn.setVisibility(View.VISIBLE);
                stateBtn.setText("重新申请");
                stateBtn.setTextColor(getResources().getColor(R.color.font_color_CCCCCC));
                stateImg.setImageResource(R.drawable.wait_pic_connect);
                break;
            default:
                tvTitle.setText("等待审核");
                stateHint.setText("稍等片刻");
                stateHintSubtext.setVisibility(View.VISIBLE);
                stateHintSubtext.setText("您的申请已发送,请等待管理员审核确认!");
                stateBtn.setVisibility(View.GONE);
                stateImg.setImageResource(R.drawable.wait_pic_moment);
                break;
        }
    }

    WatchMapDevice_DeviceInfo temp = new WatchMapDevice_DeviceInfo();
    //TODO 拉取当前用户的绑定设备列表，为跳转至详情页做准备
    public void getWatchList(){
        MyHandler.putTask(this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {

//                    {"Result":{"Category":"WatchList","WatchList":[{"IMEI":"240207489224233264","Location":"113.2409402,23.1326885","Name":"爸爸","Online":"0","Picture":"image","Time":"2017-01-01 00:00:00"}]}}
                if(val!=null && val.contains("Result") && val.contains("WatchList")){
                    WatchMapDevice list = (WatchMapDevice) SerialUtil.jsonToObject(val, new TypeToken<WatchMapDevice>() {
                    }.getType());
                    if (list != null && list.getResults()!=null && list.getResults().getWatchList()!=null &&
                            list.getResults().getWatchList().size() > 0) {
                        //需要存的
//                        List<BindDeviceEntity> insertList = new ArrayList<BindDeviceEntity>();
                        //需要更新进数据库的
                        List<BindDeviceEntity> saveList = new ArrayList<BindDeviceEntity>();
                        for(WatchMapDevice_DeviceInfo tempInfo:list.getResults().getWatchList()){
                            if(device_id.equals(tempInfo.getIMEI())){
                                temp = tempInfo;
                                break;
                            }
                        }
                    } else {

                    }
                }
                //{"code":202,"message":"结果集为空"}
                else if(val!=null && val.contains("code") && val.contains("202")){
                }
                else{
                }
                //TODO new netlink

            }
        }, HttpUrls.getBindDevices_new(), null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(this)));
    }

    public void jumpTo(){
        Intent intent = new Intent(DevicesBindStateActivity.this, WatchChildEntryActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);//TODO 收到16进制
        intent.putExtra(BaseConstants.PARAMS_DEVICE_PHONE, temp.getPhoneNum());
        if(temp.getLocation().length()>3 && temp.getLocation().contains(",") &&
                temp.getLocation().split(",").length==2){
            intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, Double.parseDouble(temp.getLocation().split(",")[1]));
            intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, Double.parseDouble(temp.getLocation().split(",")[0]));
        }
        intent.putExtra(BaseConstants.PARAMS_DEVICE_TYPE, temp.getType());
        intent.putExtra(BaseConstants.PARAMS_DEVICE_IMAGEPATH, temp.getPicture());
        intent.putExtra(BaseConstants.PARAMS_DEVICE_NAME, temp.getName());
        startActivityForResult(intent, CommonRequestCode.REQUEST_ENTRY);
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
        BaseApplication.getInstances().setCurrentActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @OnClick({R.id.ibtn_back, R.id.state_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                switch (state) {
                    case WAIT:
                        setResult(RESULT_CANCELED);
                        break;
                    case ACCESS:
                        setResult(RESULT_OK);
                        break;
                    case REJECT:
                        setResult(RESULT_CANCELED);
                        break;
                    case NET_ERROR:
                        setResult(RESULT_CANCELED);
                        break;
                    default:
                        setResult(RESULT_CANCELED);
                        break;
                }
                finish();
                break;
            case R.id.state_btn:
                switch (state) {
                    case WAIT:
                        setResult(RESULT_CANCELED);
                        break;
                    case ACCESS:
                        setResult(RESULT_OK);
                        jumpTo();
                        break;
                    case REJECT:
                        setResult(RESULT_CANCELED);
                        break;
                    case NET_ERROR:
                        setResult(RESULT_CANCELED);
                        break;
                    default:
                        setResult(RESULT_CANCELED);
                        break;
                }
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CommonRequestCode.REQUEST_ENTRY:
                if (resultCode == RESULT_OK) {
                    //TODO 更新该点的位置信息
                    setResult(RESULT_OK,data);
                }
                finish();
                break;
        }
    }


    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonNattyData(CommonNattyData msg){
        switch (msg.getType()) {
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_COMMON_BROADCAST:
                //{"IMEI":"355637053995130","Category": "BindConfirmReq","Proposer":"","Answer":"Agree"}}
                String stringData = msg.getData();
                try{
                    if(stringData!=null && stringData.contains(device_id) &&  (stringData.contains("BindConfirmReq") || stringData.contains("BindConfirm"))  && stringData.contains(BaseApplication.getInstances().getUsername())){
                        if((stringData.contains("Agree") ||stringData.toLowerCase().contains("agree"))){
                            state = ACCESS;
                            initTitle();
                            getWatchList();
                        }else if(( stringData.contains("Reject") || stringData.toLowerCase().contains("reject"))){
                            state = REJECT;
                            initTitle();
                            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "绑定请求已被管理员拒绝!");
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case NattyProtocolFilter.DISPLAY_UPDATE_CONTROL_BIND_RESULT:
                String res = msg.getData();
                if ("0".equals(res)) {
                } else if ("1".equals(res)) {
                } else if ("2".equals(res)) {
                } else if ("3".equals(res)) {
                } else if ("4".equals(res)) {
                } else if ("5".equals(res)) {//第一个绑定
                } else {
                    state = REJECT;
                    initTitle();
                    BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "绑定请求已被管理员拒绝!");
                }
                break;
        }
    }
}
