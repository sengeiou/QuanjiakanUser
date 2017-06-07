package com.androidquanjiakan.activity.setting.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
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

/**
 * Created by Gin on 2016/7/25.
 */
public class MyMoneyActivity extends BaseActivity implements View.OnClickListener {

    private Context context;

    private Button btn_sure;
    private TextView tv_money;

    private ImageButton ibtn_back;
    private TextView menu_text;
    private TextView tv_title;

    public static final int  MONEY=1;
    private String doctor_total_money;
    private String id;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=MyMoneyActivity.this;
        setContentView(R.layout.layout_mymoney);
        initTitleBar();
        initView();
        initData();
    }

    private void initTitleBar() {
        ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的钱包");

        menu_text = (TextView)findViewById(R.id.menu_text);
        menu_text.setText("银行卡");
        menu_text.setVisibility(View.VISIBLE);
        menu_text.setOnClickListener(this);
    }

    private void initView(){
        tv_money = (TextView)findViewById(R.id.tv_money);

        btn_sure = (Button)findViewById(R.id.btn_sure);
        btn_sure.setVisibility(View.VISIBLE);
        btn_sure.setOnClickListener(this);
    }

    private void initData() {
        HashMap<String, String> params = new HashMap<>();
        MyHandler.putTask(MyMoneyActivity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                try {
                    if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
                        JSONObject jsonObject = new JSONObject(val);
                        doctor_total_money = jsonObject.getString("member_wallet");
                        tv_money.setText("¥ "+doctor_total_money);
                    }else{
                        doctor_total_money = "0";
                        tv_money.setText("¥ 0");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },HttpUrls.getWalletNumber()+"&member_id="+BaseApplication.getInstances().getUser_id(),params,Task.TYPE_GET_STRING_NOCACHE,QuanjiakanDialog.getInstance().getDialog(context)));
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
            case R.id.btn_sure:
                Intent intent = new Intent(this, WithdrawMoneyActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.menu_text:
                showBankCard();
                break;
        }

    }

    private void showBankCard() {

        HashMap<String, String> params = new HashMap<>();
        params.put("member_id", BaseApplication.getInstances().getUser_id()+"");
        params.put("token", BaseApplication.getInstances().getSessionID());

        MyHandler.putTask(MyMoneyActivity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(!NetUtil.isNetworkAvailable(BaseApplication.getInstances())) {
                    BaseApplication.getInstances().toast(MyMoneyActivity.this,"网络连接不可用!");
                }else{
                    if(!val.equals("[]")) {
                        //[{"id":"1","name":"","card_no":"6212264000044715726","mobile":"15889492686","bank":"工商银行","card_type":"1","user_id":"0","status":"1","createtime":"2016-08-04 20:02:20.0"}]
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    initData();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
