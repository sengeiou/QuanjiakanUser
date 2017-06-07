package com.androidquanjiakan.activity.setting.wallet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.util.BankInfo;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.EditTextFilter;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by Gin on 2016/7/25.
 */
public class AddBankcardStep2Activity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private EditText et_bank;
    private EditText et_cardtype;
    private EditText et_phonenumber;
    private EditText et_bank_branch;
    private CheckBox cb_addcard;
    private TextView tv_xieyi;
    private Button btn_next;
    private TextView tv_title;
    private ImageButton ibtn_back;
    private String name;
    private String card_no;

    private RadioGroup rgp;
    private RadioButton rbtn_1,rbtn_2,rbtn_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=AddBankcardStep2Activity.this;
        setContentView(R.layout.layout_addcardstep2);
        initTitle();
        initData();
        initView();
    }

    private void initTitle(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText("添加银行卡");
        ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        card_no = bundle.getString("card_no");

//        if(BankInfo.getNameOfBank(cardNumber.substring(0,6).toCharArray(),0)==null){
//            Toast.makeText(AddBankcardStep1Activity.this, "请输入正确银行卡号", Toast.LENGTH_SHORT).show();
//            return;
//        }

    }

    private String type = "储蓄卡";
    private void switchWatchModel(String string){
        type = string;
    }

    private void initView() {

        rgp = (RadioGroup)findViewById(R.id.rgp);
        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                RadioButton rbtn = (RadioButton)findViewById(arg1);
                switchWatchModel(rbtn.getTag().toString());
            }
        });
        rbtn_1 = (RadioButton)findViewById(R.id.rbtn_1);
        rbtn_2 = (RadioButton)findViewById(R.id.rbtn_2);
        rbtn_3 = (RadioButton)findViewById(R.id.rbtn_3);
        rbtn_1.setTag("储蓄卡");
        rbtn_2.setTag("信用卡");
        rbtn_3.setTag("其他");

        et_bank = (EditText)findViewById(R.id.et_bank);
        et_bank.setEnabled(false);
        et_bank_branch = (EditText) findViewById(R.id.et_bank_branch);

        et_cardtype = (EditText)findViewById(R.id.et_cardtype);

        et_phonenumber = (EditText)findViewById(R.id.et_phonenumber);
        cb_addcard = (CheckBox)findViewById(R.id.cb_addcard);
        if(cb_addcard.isChecked()) {
            cb_addcard.setChecked(false);
        }else {
            cb_addcard.setChecked(true);
        }
        tv_xieyi = (TextView)findViewById(R.id.tv_xieyi);
        tv_xieyi.setText("用户协议");
        tv_xieyi.setOnClickListener(this);

        btn_next = (Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

        if(BankInfo.getNameOfBank(card_no.substring(0,6).toCharArray(),0)==null){
            et_bank.setText("通用");
        }else{
            et_bank.setText(BankInfo.getNameOfBank(card_no.substring(0,6).toCharArray(),0));
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
                finish();
                break;
            case R.id.btn_next:
                toStep3();
                break;
            case R.id.tv_xieyi:
                showXieYiDailog();
                break;
        }

    }

    private void showXieYiDailog() {
        final Dialog introduceDialog = QuanjiakanDialog.getInstance().getDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_medicine_introduce,null);

        //设置关闭对话框
        ImageView exit = (ImageView) view.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                introduceDialog.dismiss();
            }
        });

        //设置对话框标题
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
        title.setText("全家康-银行卡快捷签约协议");

        //设置条款内容
        TextView include_value = (TextView) view.findViewById(R.id.indroduce);
        include_value.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        include_value.setText(R.string.signup_card_rules);

        WindowManager.LayoutParams params = introduceDialog.getWindow().getAttributes();
        params.width= QuanjiakanUtil.dip2px(context,300);
        params.height=params.WRAP_CONTENT;
        params.gravity= Gravity.CENTER;
        introduceDialog.setContentView(view,params);
        introduceDialog.setCanceledOnTouchOutside(false);
        introduceDialog.show();

    }

    private boolean isBank(){
        String bank = et_bank.getText().toString().trim();
        switch (bank) {
            case "工商银行" :
            case "广发银行" :
            case "交通银行" :
            case "华夏银行" :
            case "招商银行" :
            case "浦发银行" :
            case "广州银行" :
            case "平安银行" :
            case "中国银行" :
            case "中信银行" :
            case "建设银行" :
            case "民生银行" :
            case "农业银行" :
            case "兴业银行" :
            case "光大银行" :
            case "通用" :
            return true;
            default:
                return false;

        }
    }

    private void toStep3() {
        //先判断所有情况
        if(et_bank.length()==0/*||et_cardtype.length()==0*/) {
            Toast.makeText(AddBankcardStep2Activity.this, "请输入银行名称"/* 和银行卡类型  */, Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isBank()) {
            Toast.makeText(AddBankcardStep2Activity.this, "请输入正确的银行名称,例如:农业银行", Toast.LENGTH_SHORT).show();
            return;
        }

//        String cardtype = et_cardtype.getText().toString().trim().replaceAll(" ", "");
//        if(!(cardtype .equals("储蓄卡")||cardtype .equals("信用卡")||cardtype .equals("其他"))) {
//            Toast.makeText(AddBankcardStep2Activity.this, "请输入正确的银行卡类型", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if(et_bank_branch.length()<1 || et_bank_branch.getText().toString().trim().replace(" ", "").length()<1){
            Toast.makeText(AddBankcardStep2Activity.this, "请输入银行卡所属的支行!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!CheckUtil.isPhoneNumber(et_phonenumber.getText().toString())) {
            Toast.makeText(context, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!EditTextFilter.isPhoneLegal(et_phonenumber.getText().toString())) {
            Toast.makeText(context, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!cb_addcard.isChecked()) {
            Toast.makeText(AddBankcardStep2Activity.this, "请勾选用户协议", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(context, AddBankcardStep3Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        bundle.putString("card_no",card_no);
        bundle.putString("mobile",et_phonenumber.getText().toString());
        bundle.putString("bank",et_bank.getText().toString());
        bundle.putString("bank_branch",et_bank_branch.getText().toString());
        bundle.putString("card_type",/*et_cardtype.getText().toString()*/type);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
