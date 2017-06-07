package com.androidquanjiakan.activity.setting.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.EditTextFilter;
import com.androidquanjiakan.view.HPEditText;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by Gin on 2016/7/25.
 */
public class AddBankcardStep1Activity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title;
    private ImageButton ibtn_back;
    private EditText et_name;
    private HPEditText et_cardnumber;
    private Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_addcardstep1);
        initTitle();
        initView();
    }

    private void initTitle(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText("添加银行卡");
        ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
    }

    private void initView() {
        et_name = (EditText)findViewById(R.id.et_name);
        et_cardnumber = (HPEditText)findViewById(R.id.et_cardnumber);
        btn_next = (Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.btn_next:
                toStep2();
                break;
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

    private void toStep2() {
        String cardNumber = et_cardnumber.getText().toString().replaceAll(" ", "");

        if(et_name.getText().toString().trim().length()==0||et_cardnumber.getText().toString().trim().length()==0) {
            Toast.makeText(AddBankcardStep1Activity.this, "请输入持卡人和银行卡号", Toast.LENGTH_SHORT).show();
            return;
        }

        if(EditTextFilter.containsUnChar(et_name.getText().toString().trim())) {
            Toast.makeText(AddBankcardStep1Activity.this, "请输入正确名字", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!CheckUtil.isNumberChar(cardNumber)){
            Toast.makeText(AddBankcardStep1Activity.this, "请输入正确银行卡号", Toast.LENGTH_SHORT).show();
            return;
        }

        if(cardNumber.length()<15||cardNumber.length()>19) {
            Toast.makeText(AddBankcardStep1Activity.this, "请输入正确银行卡号位数", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, AddBankcardStep2Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name",et_name.getText().toString());
        bundle.putString("card_no",cardNumber);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
