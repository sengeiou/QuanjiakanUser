package com.androidquanjiakan.activity.setting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.setting.wallet.CheckPasswordActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.androidquanjiakan.entity_util.NetUtil.isNetworkAvailable;


/**
 * Created by Gin on 2016/10/24.
 */

public class WithDraw_PasswordActivity extends BaseActivity {
    private Context context;
    private TextView tv_updatepwd;
    private TextView tv_forgetpwd;
    private ImageButton ibtn_back;
    private TextView tv_title;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = WithDraw_PasswordActivity.this;
        setContentView(R.layout.layout_withdraw_password);
        initPwd();

        //initView();
    }

    private void initPwd() {

        if(!isNetworkAvailable(context)) {
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            initView();
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("member_id", QuanjiakanSetting.getInstance().getUserId()+"");
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                try {
                    JSONObject jsonObject = new JSONObject(val);
                    if(jsonObject.has("code")) {
                        if(jsonObject.getString("code").equals("500")) {
                            password="null";
                        }
                    }else {
                        password = jsonObject.getString("password");
                    }
                    if(password.equals("null")) {
                        //弹出 去设置密码的对话框
                        showSureCancel();
                    }
                    initView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, HttpUrls.getPaymentPwd(),params, Task.TYPE_POST_DATA_PARAMS,null));




    }

    private void showSureCancel() {
        final Dialog dialog = QuanjiakanDialog.getInstance().getDialog(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_set_withdrawmoney_password, null);
        TextView biaoti = (TextView)inflate.findViewById(R.id.tv_biaoti);
        biaoti.setText("您暂未设置提现密码,是否现在前去设置");
        inflate.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳到设置密码的界面
                Intent intent = new Intent(context, CheckPasswordActivity.class);
                intent.putExtra("flag","fromWithdrawMoneyActivity");
                startActivity(intent);
            }
        });

        inflate.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = this.getResources().getDisplayMetrics().widthPixels * 3 / 4;
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setContentView(inflate,lp);
        dialog.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    private void initView() {
       /* ibtn_back = (ImageButton)findViewById(ibtn_back);
        ibtn_back.setOnClickListener(this);
        ibtn_back.setVisibility(View.VISIBLE);
        tv_title = (TextView)findViewById(tv_title);
        tv_title.setText(R.string.setting_password_modify);*/
        ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("提现密码管理");

        tv_updatepwd = (TextView)findViewById(R.id.tv_updatepwd);
        tv_updatepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WithDraw_PasswordActivity.this, Setting_UpdateWithDrawPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tv_forgetpwd = (TextView)findViewById(R.id.tv_forgetpwd);
        tv_forgetpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WithDraw_PasswordActivity.this, Setting_ForgetWithDrawPasswordActivity.class);
                startActivity(intent);
                finish();


            }
        });



    }
}
