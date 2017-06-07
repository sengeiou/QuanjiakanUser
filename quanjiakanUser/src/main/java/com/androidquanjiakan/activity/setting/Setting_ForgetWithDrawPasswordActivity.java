package com.androidquanjiakan.activity.setting;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.EditTextFilter;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.MD5Util;
import com.androidquanjiakan.util.SignatureUtil;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Pattern;


/**
 * Created by Gin on 2016/10/24.
 */

public class Setting_ForgetWithDrawPasswordActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibtn_back;
    private TextView tv_titile;
    private EditText et_mobile;
    private EditText et_code;
    private TextView tv_yanzhengma;
    private EditText et_password;
    private EditText et_confirm;
    private Button btn_submit;
    private Context context;

    private int total = 120;
    private String lastSMSPhone;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=Setting_ForgetWithDrawPasswordActivity.this;
        setContentView(R.layout.layout_findpassword);
        initData();

    }

    private void initData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("member_id", QuanjiakanSetting.getInstance().getUserId()+"");
        MyHandler.putTask(Setting_ForgetWithDrawPasswordActivity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                //Toast.makeText(Setting_UpdateWithDrawPasswordActivity.this, val, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(val);
                    password = jsonObject.getString("password");
                    initView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, HttpUrls.getPaymentPwd(),params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(Setting_ForgetWithDrawPasswordActivity.this)));

    }

    private void initView() {
        ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_titile = (TextView) findViewById(R.id.tv_title);
        TextView tv_notice = (TextView) findViewById(R.id.tv_notice);
        tv_notice.setText("请输入数字,长度限制为6位");
        tv_titile.setText("忘记提现密码");

        et_mobile = (EditText) findViewById(R.id.et_username);
        et_code = (EditText) findViewById(R.id.et_code);
        et_code.setTag("");
        tv_yanzhengma = (TextView) findViewById(R.id.btn_yanzhengma);
        tv_yanzhengma.setOnClickListener(this);
        et_password = (EditText) findViewById(R.id.et_newpassword);
        et_confirm = (EditText) findViewById(R.id.et_confirmnewpassword);
        btn_submit = (Button) findViewById(R.id.btn_submit);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_yanzhengma:
                getSMSCode();
                break;
            case R.id.btn_submit:
                findPassword();
                break;
            case R.id.ibtn_back:
                finish();
                break;
            default:
                break;
        }


    }

    private void findPassword() {
        if(et_mobile.length() == 0 || et_code.length() == 0 || et_password.length() == 0 || et_confirm.length() == 0){
            Toast.makeText(context, getResources().getString(R.string.setting_password_check1), Toast.LENGTH_SHORT).show();
            return;
        }

        if(password==null) {
            Toast.makeText(Setting_ForgetWithDrawPasswordActivity.this, "还没有设置提现密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!CheckUtil.isPhoneNumber(et_mobile.getText().toString())){
            Toast.makeText(Setting_ForgetWithDrawPasswordActivity.this, "请输入正确的手机号码!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!BaseApplication.getInstances().getUsername().endsWith(et_mobile.getText().toString())){
            Toast.makeText(context, "手机号码与登录手机不一致!", Toast.LENGTH_SHORT).show();
            return;
        }


        if(!et_code.getText().toString().equals(et_code.getTag().toString())){
            Toast.makeText(context, "验证码不一致，请重试！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(lastSMSPhone!=null && !lastSMSPhone.equals(et_mobile.getText().toString())){
            Toast.makeText(context, "手机号码与获取验证码的手机号码不一致!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isNumeric(et_password.getText().toString().trim())) {
            Toast.makeText(Setting_ForgetWithDrawPasswordActivity.this, "提现密码必须是数字", Toast.LENGTH_SHORT).show();
            return;
        }

        if(et_password.getText().toString().trim().length()!=6) {
            Toast.makeText(Setting_ForgetWithDrawPasswordActivity.this, "提现密码必须是6位", Toast.LENGTH_SHORT).show();
            return;
        }

        if(et_confirm.getText().toString().trim().length()!=6) {
            Toast.makeText(Setting_ForgetWithDrawPasswordActivity.this, "确认密码必须是6位", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!et_password.getText().toString().equals(et_confirm.getText().toString())){
            Toast.makeText(context, getResources().getString(R.string.setting_password_check11), Toast.LENGTH_SHORT).show();
            return;
        }

        //请求网络
        HashMap<String, String> params = new HashMap<>();
        params.put("member_id", QuanjiakanSetting.getInstance().getUserId()+"");
        params.put("password", BaseApplication.getInstances().getFormatPWString(et_confirm.getText().toString().trim()));

        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                HttpResponseResult result = new HttpResponseResult(val);
                if(result.isResultOk()) {
                    Toast.makeText(Setting_ForgetWithDrawPasswordActivity.this, "提现密码修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(Setting_ForgetWithDrawPasswordActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.updatePaymentPwd(),params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(Setting_ForgetWithDrawPasswordActivity.this)));


    }

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int arg = msg.arg1;
            if(msg.what == 1){
                if(arg < 1){
                    tv_yanzhengma.setEnabled(true);
                    tv_yanzhengma.setText("获取验证码");
                }else {
                    tv_yanzhengma.setEnabled(false);
                    tv_yanzhengma.setText("剩余"+arg + "s");
                }
            }
        }

    };


    /**
     * 获取短信验证码
     */
    protected void getSMSCode(){
        if (!EditTextFilter.isPhoneLegal(et_mobile.getText().toString())) {
            Toast.makeText(context, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(1).append("@");
            sb.append("forget").append("@");
            sb.append(et_mobile.getText().toString()).append("@");
            LogUtil.e("encodeString:"+sb.toString());

            //JSON
            JSONObject jsonData = new JSONObject();
            jsonData.put("client",1);
            jsonData.put("type","forget");
            jsonData.put("mobile",et_mobile.getText().toString());
            jsonData.put("sign", MD5Util.getMD5String(sb.toString()));
            //STRING
            String stringParams = "{\"client\":1,\"type\":\"forget\"," +
                    "\"mobile\":\""+et_mobile.getText().toString()+"\"" +
                    "\"sign\":\""+SignatureUtil.getMD5String(sb.toString())+"\"" +
                    "}";

            HashMap<String, String> params = new HashMap<>();
            params.put("data", jsonData.toString());
            MyHandler.putTask(this,new Task(new HttpResponseInterface() {

                @Override
                public void handMsg(String val) {
                    if(val!=null && val.length()>0){
                        HttpResponseResult result = new HttpResponseResult(val);
                        if(result.getCode().equals("200")){
                            //获取验证码
                            showSmsCodeTime();
                            et_code.setTag(result.getMessage());
                            lastSMSPhone = et_mobile.getText().toString();
                        }else {
                            if(result.getMessage()!=null && result.getMessage().length()>0 && (result.getMessage().contains("发送上限") || result.getMessage().contains("验证码超出"))){
                                Toast.makeText(context, "超出验证码当天发送上限"/*"获取验证码失败，请重试！"*/, Toast.LENGTH_SHORT).show();
                            }else if(result.getMessage()!=null && result.getMessage().length()>0){
                                Toast.makeText(context, result.getMessage()/*"获取验证码失败，请重试！"*/, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "获取验证码失败，请重试！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }, HttpUrls.getSMSCode2()/*+"&type=forget"*/, params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(this)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void showSmsCodeTime(){
        tv_yanzhengma.setEnabled(false);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    do {
                        Thread.sleep(1000);
                        total--;
                        Message msg = new Message();
                        msg.what = 1;
                        msg.arg1 = total;
                        mHandler.sendMessage(msg);
                    } while (total > 0);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //全是数字
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

}
