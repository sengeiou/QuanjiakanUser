package com.androidquanjiakan.activity.setting.wallet;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.entity.BankCardMes;
import com.jungly.gridpasswordview.GridPasswordView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




/**
 * Created by Gin on 2016/7/25.
 */
public class CheckPasswordActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibtn_back;
    private TextView menu_text;
    private TextView tv_title;

    private EditText check;
    private Button btn_sure;

    public static final String PARAMS_FROM = "from";
    private int from = 0;

    public static final int FROM_LIST = 1;
    public static final int FROM_ADD = 0;
    private String password;
    private TextView tv_passwordtitle;
    private GridPasswordView pswView;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mymoney_password_check);
        from = getIntent().getIntExtra(PARAMS_FROM,0);
        initIn();
        initTitleBar();
        initData();
        //initView();
    }

    private void initIn() {
        if(getIntent().getStringExtra("flag")!=null) {
            flag = getIntent().getStringExtra("flag");
            initData();
        }
    }

    private void initData() {
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
                    initView();
                } catch (JSONException e) {
                    e.printStackTrace();
                    MobclickAgent.reportError(CheckPasswordActivity.this,e);
                }

            }
        }, HttpUrls.getPaymentPwd(),params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(CheckPasswordActivity.this)));
    }

    private void initTitleBar() {
        ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("添加银行卡");

        menu_text = (TextView)findViewById(R.id.menu_text);
        menu_text.setVisibility(View.GONE);
        menu_text.setOnClickListener(this);
    }

    private void initView(){
        /*check = (EditText) findViewById(R.id.check);

        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(this);*/
        tv_passwordtitle = (TextView) findViewById(R.id.tv_passwordtitle);

        if(password.equals("null")) {
            tv_passwordtitle.setText("请设置提现密码");
        }else {
            tv_passwordtitle.setText("请输入提现密码,以验证身份");
        }

        pswView = (GridPasswordView) findViewById(R.id.pswView);
        pswView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {

            }

            @Override
            public void onInputFinish(String psw) {
                if(password.equals("null")) {
                    getCheckDialog(psw);
                }else {
                    if(BaseApplication.getInstances().getFormatPWString(psw).equals(password)){
                        Intent intent = new Intent(CheckPasswordActivity.this, AddBankcardStep1Activity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(CheckPasswordActivity.this, "输入密码失败,请重新输入", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void getCheckDialog(final String psw) {
        final Dialog dialog = QuanjiakanDialog.getInstance().getDialog(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_sure_cancel, null);
        TextView biaoti = (TextView)inflate.findViewById(R.id.tv_biaoti);
        biaoti.setText("您确定设置该密码吗");
        inflate.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                params.put("member_id",QuanjiakanSetting.getInstance().getUserId()+"");
                params.put("password",BaseApplication.getInstances().getFormatPWString(psw));

                //请求网络
                MyHandler.putTask(new Task(new HttpResponseInterface() {
                    @Override
                    public void handMsg(String val) {
                        HttpResponseResult result = new HttpResponseResult(val);
                        if(result.isResultOk()) {
                            if(flag!=null&&flag.equals("fromWithdrawMoneyActivity")) {
                                Toast.makeText(CheckPasswordActivity.this, "提现密码设置成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(CheckPasswordActivity.this, "提现密码设置成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CheckPasswordActivity.this, AddBankcardStep1Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }else {
                            Toast.makeText(CheckPasswordActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },HttpUrls.updatePaymentPwd(),params,Task.TYPE_POST_DATA_PARAMS,QuanjiakanDialog.getInstance().getDialog(CheckPasswordActivity.this)));
                dialog.dismiss();
            }
        });

        inflate.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
            case R.id.ibtn_back:
                if(from==FROM_LIST){
                    showBankCard();
                }else{
                    Intent intent = new Intent(CheckPasswordActivity.this, AddBankcardActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.menu_text:
                break;
        }
    }

    private void showBankCard() {

        HashMap<String, String> params = new HashMap<>();
        params.put("member_id", BaseApplication.getInstances().getUser_id()+"");
        params.put("token", BaseApplication.getInstances().getSessionID());

        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(!val.equals("[]")) {
                    //[{"id":"1","name":"","card_no":"6212264000044715726","mobile":"15889492686","bank":"工商银行","card_type":"1","user_id":"0","status":"1","createtime":"2016-08-04 20:02:20.0"}]
                    try {
                        List<BankCardMes> cards =new ArrayList<BankCardMes>();
                        JSONArray jsonArray = new JSONArray(val);
                        int length = jsonArray.length();
                        for (int i=0;i<length;i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            BankCardMes bankCardMes = new BankCardMes();
                            bankCardMes.setCard_no( jsonObject.getString("card_no"));
                            bankCardMes.setBank(jsonObject.getString("bank"));
                            bankCardMes.setCard_type(setCardType(jsonObject.getString("card_type"))) ;
                            cards.add(bankCardMes);
                        }
                        Intent intent = new Intent(CheckPasswordActivity.this, GetBandcardListActivity.class);
                        intent.putExtra("cards",(Serializable)cards);//将集合传递到activity
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MobclickAgent.reportError(CheckPasswordActivity.this,e);
                    }
                }else {
                    Intent intent = new Intent(CheckPasswordActivity.this, AddBankcardActivity.class);
                    startActivity(intent);
                }
            }
        }, HttpUrls.getBankcardList()/*+"&doctor_id="+BaseApplication.getInstances().getUser_id()*/,params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(CheckPasswordActivity.this)));

    }

    //将类型转换成
    public String setCardType(String card_type){
        if(card_type.equals("1")) {
            return "储蓄卡";
        }else if(card_type.equals("2")){
            return "信用卡";
        }
        return "其他";
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
