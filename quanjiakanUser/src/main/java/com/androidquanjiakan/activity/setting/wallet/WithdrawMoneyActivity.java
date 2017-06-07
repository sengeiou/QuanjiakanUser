package com.androidquanjiakan.activity.setting.wallet;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.entity.BankCardMes;
import com.androidquanjiakan.entity_util.NetUtil;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
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

import static com.androidquanjiakan.entity_util.NetUtil.isNetworkAvailable;

/**
 * Created by Gin on 2016/9/12.
 */
public class WithdrawMoneyActivity  extends BaseActivity implements View.OnClickListener {

    private Context context;
    private ImageButton ibtn_back;
    private TextView tv_title;
    private TextView menu_text;

    private TextView tv_totalmoney;

    private TextView tv_bankcard;
    private EditText et_withdrawmoney;
    private TextView tv_carnumber;
    private TextView et_password;
    private Button btn_dopositmoney;
    private String password;
    private LinearLayout ll_select_bank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=WithdrawMoneyActivity.this;
        setContentView(R.layout.layout_activity_withdrawmoney);
        initTitle();
        initPwd();
        //initView();
        //initData();
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
                    initData();
                } catch (JSONException e) {
                    e.printStackTrace();
                    MobclickAgent.reportError(WithdrawMoneyActivity.this,e);
                }

            }
        }, HttpUrls.getPaymentPwd(),params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
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

    private void initTitle(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText("提现");
        ibtn_back =(ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        menu_text = (TextView) findViewById(R.id.menu_text);
        menu_text.setVisibility(View.VISIBLE);
        menu_text.setText("银行卡");
        menu_text.setOnClickListener(this);
    }

    private void initView() {

        tv_totalmoney = (TextView) findViewById(R.id.tv_totalmoney);
        et_withdrawmoney = (EditText)findViewById(R.id.et_withdrawmoney);
        tv_carnumber = (TextView)findViewById(R.id.tv_carnumber);
        ll_select_bank = (LinearLayout)findViewById(R.id.ll_select_bank);
        ll_select_bank.setOnClickListener(this);

        et_password = (TextView)findViewById(R.id.et_password);

        btn_dopositmoney = (Button)findViewById(R.id.btn_dopositmoney);
        btn_dopositmoney.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.menu_text:
            case R.id.ll_select_bank:
                showBankCard();
                break;
            case R.id.btn_dopositmoney:
                doposit();
                break;
        }
    }

    private void showBankCard() {

        HashMap<String, String> params = new HashMap<>();
        params.put("member_id", BaseApplication.getInstances().getUser_id()+"");
        params.put("token", BaseApplication.getInstances().getSessionID());

        MyHandler.putTask(WithdrawMoneyActivity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(!NetUtil.isNetworkAvailable(BaseApplication.getInstances())) {
                    BaseApplication.getInstances().toast(WithdrawMoneyActivity.this,"网络连接不可用!");
                }else{
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
                            Intent intent = new Intent(context, GetBandcardListActivity.class);
                            intent.putExtra("cards",(Serializable)cards);//将集合传递到activity
                            intent.putExtra(GetBandcardListActivity.PARAMS_TYPE,GetBandcardListActivity.PARAMS_TYPE_GET_CARD);
                            startActivityForResult(intent,CommonRequestCode.REQUEST_CARD);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MobclickAgent.reportError(WithdrawMoneyActivity.this,e);
                        }
                    }else {
                        Intent intent = new Intent(context, AddBankcardActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }, HttpUrls.getBankcardList()/*+"&doctor_id="+BaseApplication.getInstances().getUser_id()*/,params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
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

    private void doposit(){
        if(et_withdrawmoney.length()<1){
            BaseApplication.getInstances().toast(WithdrawMoneyActivity.this,"请输入要提取的金额!");
            return;
        }

        if(et_withdrawmoney.getText().toString().startsWith(".")){
            BaseApplication.getInstances().toast(WithdrawMoneyActivity.this,"请输入正确的金额!");
            return;
        }
        String string = et_withdrawmoney.getText().toString();
        if(string.indexOf(".")>0 && string.substring(string.indexOf(".")).length()>3){
            BaseApplication.getInstances().toast(WithdrawMoneyActivity.this,"请输入最多两位小数!");
            return;
        }

        if(string.equals("0")||string.equals("0.0")||string.equals("0.00")) {
            BaseApplication.getInstances().toast(WithdrawMoneyActivity.this,"请输入正确的金额");
            return;
        }

        if(Double.parseDouble(et_withdrawmoney.getText().toString())>Double.parseDouble(doctor_total_money)){
            BaseApplication.getInstances().toast(WithdrawMoneyActivity.this,"提取金额超出限制!");
            return;
        }

        if(tv_carnumber.getTag()==null || tv_carnumber.getTag().toString().length()<1){
            BaseApplication.getInstances().toast(WithdrawMoneyActivity.this,"请选择要转入的银行卡!");
            return;
        }

        if(et_password.length()<1){
            BaseApplication.getInstances().toast(WithdrawMoneyActivity.this,"请输入提取密码!");
            return;
        }

        if(!BaseApplication.getInstances().getFormatPWString(et_password.getText().toString().trim()).equals(password)){
            BaseApplication.getInstances().toast(WithdrawMoneyActivity.this,"提取密码不正确!");
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        MyHandler.putTask(WithdrawMoneyActivity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                try {
                    if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
                        JSONObject jsonObject = new JSONObject(val);
                        if(jsonObject.has("code") && "200".equals(jsonObject.get("code").toString())){
                            BaseApplication.getInstances().toast(WithdrawMoneyActivity.this,"提现成功!");
                            setResult(RESULT_OK);
                            finish();
                        }else{
                            if(jsonObject.has("message")){
                                BaseApplication.getInstances().toast(WithdrawMoneyActivity.this,jsonObject.getString("message"));
                            }else{
                                BaseApplication.getInstances().toast(WithdrawMoneyActivity.this,"提现失败!");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MobclickAgent.reportError(WithdrawMoneyActivity.this,e);
                }
            }
        }, HttpUrls.getWalletDoposit(BaseApplication.getInstances().getUser_id(),et_withdrawmoney.getText().toString())+
                "&card_no="+tv_carnumber.getTag().toString()+
                "&password="+ BaseApplication.getInstances().getFormatPWString(et_password.getText().toString()),params,Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(context)));

    }

    String doctor_total_money;
    private void initData() {
        HashMap<String, String> params = new HashMap<>();
        MyHandler.putTask(WithdrawMoneyActivity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                try {
                    if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
                        JSONObject jsonObject = new JSONObject(val);
                        doctor_total_money = jsonObject.getString("member_wallet");
                        tv_totalmoney.setText(""+doctor_total_money);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MobclickAgent.reportError(WithdrawMoneyActivity.this,e);
                }
            }
        },HttpUrls.getWalletNumber()+"&member_id="+BaseApplication.getInstances().getUser_id(),params,Task.TYPE_GET_STRING_NOCACHE,null));
    }
    public static final String PARAMS_NO = "card_number";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CommonRequestCode.REQUEST_CARD:
                if(resultCode==RESULT_OK){
                    String card = data.getStringExtra(PARAMS_NO);
                    tv_carnumber.setTag(card);
                    tv_carnumber.setText(card.substring(card.length()-4));
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);

        initPwd();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }
}
