package com.androidquanjiakan.activity.setting.wallet;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.BankCardMes;
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
 * Created by Gin on 2016/7/26.
 */
public class BankcardEditActivity extends BaseActivity implements View.OnClickListener {

    private String bank;
    private String card_type;
    private String card_no;
    private ImageButton ibtn_back;
    private ImageView iv_point;
    private ImageView iv_bankbg;
    private TextView tv_bank;
    private TextView tv_cardtype;
    private TextView tv_carnumber;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=BankcardEditActivity.this;
        setContentView(R.layout.layout_bankcardedit);
        initData();
        initView();
    }

    private void initView() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(this);

        iv_point = (ImageView) findViewById(R.id.iv_point);
        iv_point.setOnClickListener(this);

        iv_bankbg = (ImageView) findViewById(R.id.iv_bankbg);
        tv_bank = (TextView) findViewById(R.id.tv_bank);
        tv_cardtype = (TextView) findViewById(R.id.tv_cardtype);
        tv_carnumber = (TextView)findViewById(R.id.tv_carnumber);
        setImageBg();
        tv_bank.setText(bank);
        tv_cardtype.setText(card_type);
        tv_carnumber.setText(card_no.substring(card_no.length()-4,card_no.length()));
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        //卡号
        card_no = bundle.getString("card_no");
        //银行卡类型
        card_type = bundle.getString("card_type");
        //银行
        bank = bundle.getString("bank");
    }

    public void setImageBg(){
        switch (bank) {
            case "工商银行":
                iv_bankbg.setImageResource(R.drawable.iv_gongshang);
                break;
            case "农业银行":
                iv_bankbg.setImageResource(R.drawable.iv_nonye);
                break;
            case "中国银行":
                iv_bankbg.setImageResource(R.drawable.iv_zhongguo);
                break;
            case "建设银行":
                iv_bankbg.setImageResource(R.drawable.iv_jianshe);
                break;
            case "交通银行":
                iv_bankbg.setImageResource(R.drawable.iv_jiaotong);
                break;
            case "广发银行":
                iv_bankbg.setImageResource(R.drawable.iv_guangfa);
                break;
            case "招商银行":
                iv_bankbg.setImageResource(R.drawable.iv_zhaoshang);
                break;
            default:
                iv_bankbg.setImageResource(R.drawable.iv_null);

        }
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
                showBankCard();
                break;
            case R.id.iv_point:
                showEditDialog();
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
                        Intent intent = new Intent(context, GetBandcardListActivity.class);
                        intent.putExtra("cards",(Serializable)cards);//将集合传递到activity
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MobclickAgent.reportError(BankcardEditActivity.this,e);
                    }
                }else {
                    Intent intent = new Intent(context, AddBankcardActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, HttpUrls.getBankcardList()/*+"&doctor_id="+BaseApplication.getInstances().getUser_id()*/,params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));

    }

    private void showEditDialog() {
        final Dialog editCardDialog = QuanjiakanDialog.getInstance().getCardDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_editcard, null);
        View cancel = view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCardDialog.dismiss();
            }
        });
        View jiechu = view.findViewById(R.id.tv_jiechubangding);
        jiechu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               getSureCancelDialog();
                editCardDialog.dismiss();
            }
        });

        WindowManager.LayoutParams params = editCardDialog.getWindow().getAttributes();
        params.width= QuanjiakanUtil.dip2px(context,360);
        params.height=params.WRAP_CONTENT;

        editCardDialog.setContentView(view,params);
        editCardDialog.setCanceledOnTouchOutside(false);
        editCardDialog.show();

    }


    private void getSureCancelDialog() {
        final Dialog dialog = QuanjiakanDialog.getInstance().getDialog(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_sure_cancel, null);
        TextView biaoti = (TextView)inflate.findViewById(R.id.tv_biaoti);
        biaoti.setText("您确定要解除银行卡");
        inflate.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                disconnectbankcard();
            }
        });

        inflate.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = context.getResources().getDisplayMetrics().widthPixels * 3 / 4;
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setContentView(inflate,lp);
        dialog.show();
    }

    private void disconnectbankcard() {
//        if(!isNetworkAvailable(context)) {
//            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
//            return;
//        }
        HashMap<String,String> params =new HashMap<>();
//        params.put("user_id", QuanjiakanSetting.getInstance().getUserId()+"");
//        params.put("token",QuanjiakanSetting.getInstance().getToken());
        params.put("card_no",card_no);
        MyHandler.putTask(BankcardEditActivity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                //Log.v("jugui",val);
                HttpResponseResult result = new HttpResponseResult(val);
                if(result.isResultOk()) {
                    showBankCard();
                }else {
                    Toast.makeText(BankcardEditActivity.this, "银行卡解除失败,请重试", Toast.LENGTH_SHORT).show();
                }

            }
        }, HttpUrls.removeBancard(),params,Task.TYPE_POST_DATA_PARAMS,QuanjiakanDialog.getInstance().getDialog(context)));

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
}
