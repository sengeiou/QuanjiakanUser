package com.androidquanjiakan.activity.index;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.main.MainActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.presenter.IPresenterBusinessCode;
import com.androidquanjiakan.presenter.SigninPresenter;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class SigninActivity_mvp extends BaseActivity{

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.tv_signup)
    TextView tvSignup;
    @BindView(R.id.tv_findpassword)
    TextView tvFindpassword;

    private Dialog dialog;

    private SigninPresenter signinPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signin);
        ButterKnife.bind(this);
        initView();
        signinPresenter = new SigninPresenter();
    }

    protected void initView() {
        tvTitle.setText(R.string.login_title);
    }

    protected void signup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    protected void findPassword() {
        Intent intent = new Intent(this, FindPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        JPushInterface.stopPush(BaseApplication.getInstances());
        JPushInterface.clearAllNotifications(BaseApplication.getInstances());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @OnClick({R.id.btn_submit, R.id.tv_signup, R.id.tv_findpassword})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                signinPresenter.doLogin(this);
                break;
            case R.id.tv_signup:
                signup();
                break;
            case R.id.tv_findpassword:
                findPassword();
                break;
        }
    }

    public String getUsername(){
        return etUsername.getText().toString();
    }

    public String getPassword(){
        return etPassword.getText().toString();
    }

    //********************************************************************************************

    public Object getParamter(int type) {
        switch (type){
            case IPresenterBusinessCode.LOGIN:
                if (etUsername.getText().toString().trim().equals("") || etPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(SigninActivity_mvp.this, getString(R.string.common_hint_login_params_error), Toast.LENGTH_SHORT).show();
                    return null;
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile", etUsername.getText().toString());
                params.put("password", BaseApplication.getInstances().getFormatPWString(etPassword.getText().toString()));
                params.put("client", "1");
                return params;
            default:
                break;
        }
        return null;
    }

    public void showMyDialog(int type) {
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
        dialog = QuanjiakanDialog.getInstance().getDialog(SigninActivity_mvp.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    /**
                     * 正式环境需要在这里开放
                     */
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public void dismissMyDialog(int type) {
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public void onSuccess(int type, int httpResponseCode, Object result) {
        switch (type){
            case IPresenterBusinessCode.LOGIN:
                Intent intent = new Intent(SigninActivity_mvp.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    public void onError(int type, int httpResponseCode, Object errorMsg) {
        switch (type){
            case IPresenterBusinessCode.LOGIN:
                Toast.makeText(this, "" + errorMsg.toString(), Toast.LENGTH_SHORT).show();//
                break;
            case IPresenterBusinessCode.NONE:
                //TODO 不做任何事情，在获取参数为空时调用并返回，调用前会有对应参数相关的提示
                break;
        }
    }
}
