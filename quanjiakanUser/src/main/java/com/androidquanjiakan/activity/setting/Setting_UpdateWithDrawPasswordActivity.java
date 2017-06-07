package com.androidquanjiakan.activity.setting;

import android.os.Bundle;
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
import com.androidquanjiakan.util.LogUtil;
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

import static com.quanjiakan.main.R.id.et_confirmnewpassword;



/**
 * Created by Gin on 2016/10/24.
 */

public class Setting_UpdateWithDrawPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_mobile;
    private EditText et_password;
    private EditText et_confirm;
    private Button btn_submit;
    private ImageButton ibtn_back;
    private TextView tv_title;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_password_modify);
        initData();
        //initView();
    }

    private void initData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("member_id", QuanjiakanSetting.getInstance().getUserId()+"");
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                //Toast.makeText(Setting_UpdateWithDrawPasswordActivity.this, val, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(val);
                    password = jsonObject.getString("password");
                    LogUtil.e("pas---------"+password);
                    initView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, HttpUrls.getPaymentPwd(),params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(Setting_UpdateWithDrawPasswordActivity.this)));

    }

    private void initView() {
        et_mobile = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_newpassword);
        et_confirm = (EditText) findViewById(et_confirmnewpassword);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        TextView tv_notice = (TextView) findViewById(R.id.tv_notice);
        tv_notice.setText("请输入数字,长度限制为6位");
        btn_submit.setOnClickListener(this);

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("修改提现密码");


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
        if(et_mobile.length() == 0 || et_password.length() == 0 || et_confirm.length() == 0){
            Toast.makeText(this, getResources().getString(R.string.setting_password_check1), Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(BaseApplication.getInstances().getFormatPWString(et_mobile.getText().toString().trim()))){
            Toast.makeText(this, getResources().getString(R.string.setting_password_origin_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if(password==null) {
            Toast.makeText(Setting_UpdateWithDrawPasswordActivity.this, "还没有设置提现密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(et_mobile.getText().toString().trim().equals(et_password.getText().toString().trim())) {
            Toast.makeText(Setting_UpdateWithDrawPasswordActivity.this, "原密码和新密码不能一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isNumeric(et_password.getText().toString().trim())) {
            Toast.makeText(Setting_UpdateWithDrawPasswordActivity.this, "提现密码只能为数字", Toast.LENGTH_SHORT).show();
            return;
        }

        if(et_password.getText().toString().trim().length()!=6) {
            Toast.makeText(Setting_UpdateWithDrawPasswordActivity.this, "提现密码必须为6位", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!et_password.getText().toString().equals(et_confirm.getText().toString())){
            Toast.makeText(this, getResources().getString(R.string.setting_password_check2), Toast.LENGTH_SHORT).show();
            return;
        }

        if(et_password.getText().toString().equals(et_mobile.getText().toString())){
            Toast.makeText(this, getResources().getString(R.string.setting_password_check13), Toast.LENGTH_SHORT).show();
            return;
        }
        
        if(!et_confirm.getText().toString().trim().equals(et_password.getText().toString().trim())) {
            Toast.makeText(Setting_UpdateWithDrawPasswordActivity.this, "新密码和确认密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        //请求网络
        HashMap<String, String> params = new HashMap<>();
        params.put("member_id", QuanjiakanSetting.getInstance().getUserId()+"");
        params.put("password", BaseApplication.getInstances().getFormatPWString(et_confirm.getText().toString().trim()));
        //请求网络
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                HttpResponseResult result = new HttpResponseResult(val);
                LogUtil.e("xiugaimima---------"+val);
                if(result.isResultOk()) {
                    Toast.makeText(Setting_UpdateWithDrawPasswordActivity.this, "提现密码修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(Setting_UpdateWithDrawPasswordActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.updatePaymentPwd(),params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(Setting_UpdateWithDrawPasswordActivity.this)));
    }

    //全是数字
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

}
