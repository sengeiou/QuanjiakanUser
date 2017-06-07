package com.androidquanjiakan.activity.index;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.main.MainActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.presenter.IPresenterBusinessCode;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.EditTextFilter;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.MD5Util;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.view_util.OrderClickSpan;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

public class SignupActivity extends BaseActivity implements OnClickListener {

    private Context context;
    private EditText et_username, et_password, et_code, et_confirmpassword,et_name;
    private Button btn_submit;
    private TextView btn_yanzhengma;
    private TextView tv_title;
    private ImageButton ibtn_back;

    private LinearLayout ll_signup_clause_ck;
    private View signup_clause_ck;
    private TextView signup_clause_text;


    private int total = 120;

    private boolean read_flag = false;

    private String lastSMSPhone;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int arg = msg.arg1;
            if (msg.what == 1) {
                if (arg < 1) {
                    btn_yanzhengma.setEnabled(true);
                    btn_yanzhengma.setText("获取验证码");
                } else {
                    btn_yanzhengma.setEnabled(false);
                    btn_yanzhengma.setText("剩余" + arg + "s");
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.layout_signup);
        initTitle();
        initView();
        resetFlag();
    }

    public void initTitle(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.signup_title);
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
    }

    protected void initView() {

        et_name = (EditText) findViewById(R.id.et_name);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_code = (EditText) findViewById(R.id.et_code);
        et_confirmpassword = (EditText) findViewById(R.id.et_confirmpassword);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        btn_yanzhengma = (TextView) findViewById(R.id.btn_yanzhengma);
        btn_yanzhengma.setOnClickListener(this);

        ll_signup_clause_ck = (LinearLayout) findViewById(R.id.ll_signup_clause_ck);
        ll_signup_clause_ck.setOnClickListener(this);
        signup_clause_ck = findViewById(R.id.signup_clause_ck);
        signup_clause_text = (TextView) findViewById(R.id.signup_clause_text);
        initFlag();
    }

    private void initFlag(){
        signup_clause_ck.setBackgroundResource(R.drawable.doctor_pay_list_nor);
        signup_clause_text.setText(R.string.singup_clause_text1);
        String string = getResources().getString(R.string.singup_clause_text2);
        SpannableString spannableString = new SpannableString(string);
        OrderClickSpan orderClickSpan = new OrderClickSpan(string, getResources().getColor(R.color.color_title_green), new OnClickListener() {
            @Override
            public void onClick(View viewss) {
                showDialog();
            }
        });

        spannableString.setSpan(orderClickSpan,0,string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        signup_clause_text.append(spannableString);
        signup_clause_text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showDialog(){
        /**
         * 对话框样式《注册声明》
         */
        final Dialog detailDialog = QuanjiakanDialog.getInstance().getDialog(SignupActivity.this);
        View view = LayoutInflater.from(SignupActivity.this).inflate(R.layout.dialog_medicine_introduce,null);
        /**
         * 数据赋值
         */
        RelativeLayout rl_exit = (RelativeLayout) view.findViewById(R.id.rl_exit);
        rl_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                detailDialog.dismiss();
                signup_clause_text.invalidate();
            }
        });
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
//        title.setText("全家康免责条款");//软件许可及服务协议
        title.setText("软件许可及服务协议");//
        TextView include_value = (TextView) view.findViewById(R.id.indroduce);
        include_value.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        include_value.setText(R.string.signup_rules_new);
        WindowManager.LayoutParams lp = detailDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(context, 300);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        detailDialog.setContentView(view, lp);
        detailDialog.setCanceledOnTouchOutside(false);
        detailDialog.show();
    }

    private void resetFlag(){
        read_flag = !read_flag;
        if(read_flag){
            signup_clause_ck.setBackgroundResource(R.drawable.doctor_pay_list_light);
        }else {
            signup_clause_ck.setBackgroundResource(R.drawable.doctor_pay_list_nor);
        }
    }

    /**
     * 获取短信验证码
     */
    protected void getSMSCode() {
        if (!EditTextFilter.isPhoneLegal(et_username.getText().toString())) {
            Toast.makeText(context, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            //        HashMap<String, String> params = new HashMap<>();
//        params.put("mobile", et_username.getText().toString());

            StringBuilder sb = new StringBuilder();
            sb.append(1).append("@");
            sb.append("signup").append("@");
            sb.append(et_username.getText().toString()).append("@");
            LogUtil.e("encodeString:"+sb.toString());

            //JSON
            JSONObject jsonData = new JSONObject();
            jsonData.put("client",1);
            jsonData.put("type","signup");
            jsonData.put("mobile",et_username.getText().toString());
            jsonData.put("sign", MD5Util.getMD5String(sb.toString()));

            HashMap<String, String> params = new HashMap<>();
            params.put("data", jsonData.toString());
            MyHandler.putTask(this,new Task(new HttpResponseInterface() {
                @Override
                public void handMsg(String val) {
                    if(val!=null && val.length()>0){
                        HttpResponseResult result = new HttpResponseResult(val);
                        if (result.getCode().equals("200")) {
                            //获取验证码
                            showSmsCodeTime();
                            lastSMSPhone = et_username.getText().toString();
                            et_code.setTag(result.getMessage());
                        } else {
                            if(result.getMessage()!=null && result.getMessage().length()>0 && (result.getMessage().contains("发送上限") || result.getMessage().contains("验证码超出"))){
                                Toast.makeText(context, "超出验证码当天发送上限"/*"获取验证码失败，请重试！"*/, Toast.LENGTH_SHORT).show();
                            }else {
//                            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                                String msg = result.getMessage();
                                if (msg.contains("=")){
                                    Toast.makeText(context, msg.substring(msg.lastIndexOf("=")+1,msg.lastIndexOf("}")), Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }else{
//                    Toast.makeText(context, "接口异常!", Toast.LENGTH_SHORT).show();
                    }

                }
            }, HttpUrls.getSMSCode2()/*+"&data="+jsonData.toString().replace("\"","%22")*/, params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(this)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void signup() {
        //先判断一系列条件
        if(CheckUtil.isEmpty(et_username.getText().toString())){
            Toast.makeText(context, getResources().getString(R.string.setting_password_check6), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!CheckUtil.isPhoneNumber(et_username.getText().toString())) {
            Toast.makeText(context, getResources().getString(R.string.setting_password_check7), Toast.LENGTH_SHORT).show();
            return;
        }

        if (CheckUtil.isEmpty(et_code.getText().toString())) {
            Toast.makeText(context, getResources().getString(R.string.setting_password_check8), Toast.LENGTH_SHORT).show();
            return;
        }

        if (CheckUtil.isEmpty(et_code.getTag().toString()) || !et_code.getTag().toString().equals(et_code.getText().toString())) {
            Toast.makeText(context, getResources().getString(R.string.setting_password_check9), Toast.LENGTH_SHORT).show();
            return;
        }

        if(lastSMSPhone!=null && !lastSMSPhone.equals(et_username.getText().toString())){
            Toast.makeText(context, getResources().getString(R.string.setting_password_check10), Toast.LENGTH_SHORT).show();
            return;
        }

        //et_name
        if(CheckUtil.isEmpty(et_name.getText().toString())){
            Toast.makeText(context, getResources().getString(R.string.setting_password_check14), Toast.LENGTH_SHORT).show();
            return;
        }

        if (CheckUtil.isEmpty(et_password.getText().toString()) || CheckUtil.isEmpty(et_confirmpassword.getText().toString())) {
            Toast.makeText(context, getResources().getString(R.string.setting_password_check12), Toast.LENGTH_SHORT).show();
            return;
        }


        if(et_password.getText().toString().trim().length()<6||et_password.getText().toString().trim().length()>15) {
            Toast.makeText(context, "新密码位数不对", Toast.LENGTH_SHORT).show();
            return;
        }

        if(et_confirmpassword.getText().toString().trim().length()<6||et_confirmpassword.getText().toString().trim().length()>15) {
            Toast.makeText(context, "确认密码位数不对", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!et_password.getText().toString().equals(et_confirmpassword.getText().toString())) {
            Toast.makeText(context, getResources().getString(R.string.setting_password_check11), Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * 提升账号安全措施
         */
        if(!CheckUtil.checkStringType(et_password.getText().toString())){
            Toast.makeText(context, getResources().getString(R.string.setting_password_check5), Toast.LENGTH_SHORT).show();
            return;
        }

        if(!read_flag){
            Toast.makeText(context, getResources().getString(R.string.singup_clause_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        //m=member&a=signup&mobile=lp&password=7CCFA0856955C1499924FA7665B74EF9&client=1
        HashMap<String, String> params = new HashMap<>();
        params.put("mobile", et_username.getText().toString());
        params.put("password", BaseApplication.getInstances().getFormatPWString(et_password.getText().toString()));
//        params.put("password", et_password.getText().toString());
        params.put("c_password", et_confirmpassword.getText().toString());
        params.put("nickname", et_name.getText().toString());
        params.put("client","1");
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {


                if(val!=null && val.length()>0){
                    HttpResponseResult result = new HttpResponseResult(val);
                    if (result.getCode().equals("200")) {
                        QuanjiakanSetting.getInstance().setUserId(Integer.parseInt(result.getMessage()));
                        registerOnJpush(result.getMessage());
                    } else {
                        //result.getMessage()
//                    Toast.makeText(context, getResources().getString(R.string.signup_error), Toast.LENGTH_SHORT).show();
                        if(result.getMessage()!=null && result.getMessage().length()>0){
                            Toast.makeText(context, result.getMessage() , Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "该号码已被注册!" , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }, HttpUrls.getSignup(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
    }

    protected void showSmsCodeTime() {
        total = 120;
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


    /**
     * 账号注册到极光推送中
     *
     * @param user_id
     */
    protected void registerOnJpush(final String user_id) {
        JMessageClient.register(CommonRequestCode.JMESSAGE_PREFIX+user_id, CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {

            @Override
            public void gotResult(final int status, final String desc) {
                if (status == 0 || status == 898001) {
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(context, "账号注册成功，请登录！", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
//        if(JPushInterface.isPushStopped(BaseApplication.getInstances())){
//            JPushInterface.resumePush(BaseApplication.getInstances());
//        }
        JPushInterface.stopPush(BaseApplication.getInstances());
        JPushInterface.clearAllNotifications(BaseApplication.getInstances());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_yanzhengma:
                if (((TextView) arg0).getText().toString().equals("获取验证码")) {
                    getSMSCode();
                }
                break;
            case R.id.btn_submit:
                signup();
                break;
            case R.id.ibtn_back:
                this.finish();
                break;
            case R.id.ll_signup_clause_ck:
                resetFlag();
                break;
            default:
                break;
        }
    }

}
