package com.androidquanjiakan.activity.index.phonedocter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.out.ConsultantActivity_GoOut_Order_Complete;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

public class ConsultantActivity_PhoneCall extends BaseActivity implements OnClickListener {

    private ImageButton ibtn_back;
    private TextView tv_title;
    private TextView tv_book;

    public static final String PARAMS_PHONE_NUMBER = "phone_number";
    private String phoneNumber;

    private JsonObject phoneNumberJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_doctor_detail_phone_call);
        initTitleBar();
        initView();
    }

    public void initTitleBar(){
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("拨号");
    }

    /**
     *
     */
    protected void initView() {
        tv_book = (TextView) findViewById(R.id.tv_book);
        tv_book.setOnClickListener(this);
        loadPhoneDoctorDetail();
    }

    protected void loadPhoneDoctorDetail() {
        /**
         * 获取历史订单信息----保证用户能看到可以打通电话的号码
         */
        HashMap<String, String> params = new HashMap<>();
//        params.put("id", "10000");
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                if(val!=null && val.length()>0){
                    phoneNumberJson = new GsonParseUtil(val).getJsonObject();
                    if(phoneNumberJson.has("message")){
                        tv_book.setTag(phoneNumberJson.get("message").getAsString());
                    }
                }
            }
        }, HttpUrls.getPhoneDoctorDetail(), params, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(this)));

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
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()){
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_book:
                String phone = null;
                if(tv_book.getTag()!=null){
                    phone = tv_book.getTag().toString();
                }
                if(phone!=null && phone.length()>0){
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+tv_book.getTag().toString()));
                    startActivity(intent);
                }else{
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneCall.this,"接口获取电话失败!");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CommonRequestCode.REQUEST_PAY:
                if(resultCode==CommonRequestCode.RESULT_BACK_TO_MAIN){
                    setResult(CommonRequestCode.RESULT_BACK_TO_MAIN);
                    finish();
                }
                break;
            default:
                break;
        }
    }

}
