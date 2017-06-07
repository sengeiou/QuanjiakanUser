package com.androidquanjiakan.activity.setting.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.androidquanjiakan.adapter.BankCardListAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.BankCardMes;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by Gin on 2016/7/25.
 */
public class GetBandcardListActivity extends BaseActivity {

    private ListView lv_banklist;
    private LinearLayout ll_addbankcard;
    private Context context;
    private List<BankCardMes> cards;
    private TextView tv_title;
    private ImageButton ibtn_back;

    private int type = 0;
    public static final String PARAMS_TYPE = "type";
    public static final int PARAMS_TYPE_GET_CARD = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=GetBandcardListActivity.this;
        setContentView(R.layout.layout_bankcardlist);
        type = getIntent().getIntExtra(PARAMS_TYPE,0);
        initData();
        initView();
        //[{"id":"1","name":"张三","card_no":"6212264000044715726","mobile":"15889492686","bank":"工商银行","card_type":"1","user_id":"0","status":"1","createtime":"2016-08-04 20:02:20.0"}]
    }

    private void initData() {
        cards = (List<BankCardMes>) getIntent().getSerializableExtra("cards");//拿到的银行卡信息
    }

    private void initView() {
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText("银行卡");
        ibtn_back =(ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type==PARAMS_TYPE_GET_CARD) {
                    finish();
                }else{
                    startActivity(new Intent(context, MyMoneyActivity.class));
                    finish();
                }
            }
        });
        
        lv_banklist = (ListView)findViewById(R.id.lv_banklist);
        ll_addbankcard = (LinearLayout)findViewById(R.id.ll_addbankcard);
        if(type==PARAMS_TYPE_GET_CARD){
            ll_addbankcard.setVisibility(View.GONE);
        }
        ll_addbankcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(context,CheckPasswordActivity.class);
                intent.putExtra(CheckPasswordActivity.PARAMS_FROM,CheckPasswordActivity.FROM_LIST);
                startActivity(intent);
                finish();
            }
        });

        BankCardListAdapter adapter = new BankCardListAdapter(context, cards);
        lv_banklist.setAdapter(adapter);

        lv_banklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(type==PARAMS_TYPE_GET_CARD){
                    Intent intent = new Intent();
                    intent.putExtra(WithdrawMoneyActivity.PARAMS_NO,cards.get(i).getCard_no());
                    setResult(RESULT_OK,intent);
                    finish();
                }else{
                    Intent intent = new Intent(context, BankcardEditActivity.class);
                    String bank = cards.get(i).getBank();
                    String card_no = cards.get(i).getCard_no();
                    String card_type = cards.get(i).getCard_type();
                    Bundle bundle = new Bundle();
                    bundle.putString("card_no",card_no);
                    bundle.putString("card_type",card_type);
                    bundle.putString("bank",bank);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,REQUEST_EDIT);
                    finish();
                }
            }
        });

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

    private final int REQUEST_EDIT = 1000;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_EDIT:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
