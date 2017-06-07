package com.androidquanjiakan.activity.setting.wallet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.androidquanjiakan.entity.BankCardMes;
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
public class AddBankcardStep3Activity extends BaseActivity implements View.OnClickListener {

    private String name;
    private String card_no;
    private String mobile;
    private String bank;
    private String bank_branch;
    private String card_type;
    private Context context;
    private TextView tv_title;
    private ImageButton ibtn_back;
    private EditText et_code;
    private TextView tv_yanzhengma;
    private Button btn_next;

    private String tempCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=AddBankcardStep3Activity.this;
        setContentView(R.layout.layout_addcardstep3);
        initTiltle();
        initData();
        initView();

    }
    private void initTiltle(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText("添加银行卡");
        ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
    }

    private void initView() {
        et_code = (EditText)findViewById(R.id.et_code);
        tv_yanzhengma = (TextView)findViewById(R.id.tv_yanzhengma);
        tv_yanzhengma.setText("获取验证码");
        tv_yanzhengma.setOnClickListener(this);
        btn_next = (Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        card_no = bundle.getString("card_no");
        mobile = bundle.getString("mobile");
        bank = bundle.getString("bank");
        bank_branch = bundle.getString("bank_branch");
        card_type = bundle.getString("card_type");
    }

    private int changeCardtype(){

        if(card_type.equals("储蓄卡")) {
            return 1;
        }else if(card_type.equals("信用卡")){
            return 2;
        }
        return 3;
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
                finish();
                break;
            case R.id.tv_yanzhengma:
                getSMSCode();
                break;
            case R.id.btn_next:
                addCard();
                break;
        }
    }

    private void addCard() {
        if(et_code.length()<1){
            Toast.makeText(context, "请填写验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(et_code.getTag() == null || !et_code.getTag().equals(et_code.getText().toString().trim())){
            Toast.makeText(context, "验证码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String,String> params=new HashMap<>();
//        params.put("user_id", QuanjiakanSetting.getInstance().getUserId()+"");
//        params.put("token",QuanjiakanSetting.getInstance().getToken());
        params.put("name",name);
        params.put("card_no",card_no);
        params.put("mobile",mobile);
        params.put("bank",bank);
        params.put("subbranch",bank_branch);
        int card = changeCardtype();
        params.put("card_type",card+"");

        MyHandler.putTask(AddBankcardStep3Activity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                HttpResponseResult result = new HttpResponseResult(val);
                if(result.getCode().equals("200")) {
                    Toast.makeText(AddBankcardStep3Activity.this, "银行卡绑定成功,即将跳转到银行卡列表界面", Toast.LENGTH_SHORT).show();
                    showBankCard();
//                    finish();
                }else {
                    Toast.makeText(AddBankcardStep3Activity.this, "银行卡添加失败,请重新绑定", Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.addBankcard(),params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
    }

    //获取银行卡列表
    private void showBankCard() {
        HashMap<String, String> params = new HashMap<>();
        params.put("member_id", BaseApplication.getInstances().getUser_id()+"");
        params.put("token", BaseApplication.getInstances().getSessionID());
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                    //[{"id":"1","name":"张三","card_no":"6212264000044715726","mobile":"15889492686","bank":"工商银行","card_type":"1","user_id":"0","status":"1","createtime":"2016-08-04 20:02:20.0"}]
                    try {
                        List<BankCardMes> cards =new ArrayList<>();
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
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }, HttpUrls.getBankcardList(),params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));

    }

    private void getSMSCode() {
        try{
//            HashMap<String, String> params = new HashMap<>();
//            params.put("mobile",mobile);

            StringBuilder sb = new StringBuilder();
            sb.append(1).append("@");
            sb.append("deposit").append("@");
            sb.append(mobile).append("@");
            LogUtil.e("encodeString:"+sb.toString());

            //JSON
            JSONObject jsonData = new JSONObject();
            jsonData.put("client",1);
            jsonData.put("type","deposit");
            jsonData.put("mobile",mobile);
            jsonData.put("sign", MD5Util.getMD5String(sb.toString()));
            //STRING
            String stringParams = "{\"client\":1,\"type\":\"forget\"," +
                    "\"mobile\":\""+mobile+"\"" +
                    "\"sign\":\""+ SignatureUtil.getMD5String(sb.toString())+"\"" +
                    "}";

            HashMap<String, String> params = new HashMap<>();
            params.put("data", jsonData.toString());
            MyHandler.putTask(new Task(new HttpResponseInterface() {

                @Override
                public void handMsg(String val) {
                    // TODO Auto-generated method stub
                    HttpResponseResult result = new HttpResponseResult(val);
                    if(result.getCode().equals("200")){

                        //获取验证码
                        showSmsCodeTime();
                        //为验证验证码是否正确
                        et_code.setTag(result.getMessage());
                    }else {
                        if(result.getMessage()!=null && result.getMessage().length()>0 && (result.getMessage().contains("发送上限") || result.getMessage().contains("验证码超出"))){
                            Toast.makeText(context, "超出验证码当天发送上限"/*"获取验证码失败，请重试！"*/, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        //et_code.setTag("2222");
                    }
                }
            }, HttpUrls.getSMSCode2(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(this)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //将银行卡数字类型转换成文字
    public String setCardType(String card_type){
        if(card_type.equals("1")) {
            return "储蓄卡";
        }else if(card_type.equals("2")){
            return "信用卡";
        }
        return "其他";
    }

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int arg = msg.arg1;
            if(msg.what == 1){
                if(arg < 1){
                    tv_yanzhengma.setEnabled(true);
                    tv_yanzhengma.setText("重新获取");
                    tv_yanzhengma.setBackgroundResource(R.color.maincolor);
                    total=120;
                }else {
                    tv_yanzhengma.setText("剩余"+arg + "s");
                    tv_yanzhengma.setBackgroundColor(Color.GRAY);
                    tv_yanzhengma.setEnabled(false);
                }
            }
        }

    };

    int total = 120;
    private void showSmsCodeTime() {
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
                        handler.sendMessage(msg);
                    }while (total>0);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
