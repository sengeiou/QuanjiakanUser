package com.androidquanjiakan.activity.index.out;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.PublishInfoEntity;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

public class ConsultantActivity_GoOut_Order_Publish extends BaseActivity implements OnClickListener {

    private TextView tv_title;
    private ImageButton ibtn_back;
    private TextView menu_text;

    private TextView type;
    private TextView orderid;

    private TextView patient_name;
    private TextView patient_phone;
    private TextView patient_addr;
    private TextView patient_order_time;
    private TextView description;

    public static final String PARAMS_FROM = "from";
    public static final String PARAMS_DATA = "data";

    public static final int FROM_PAY = 1;
    public static final int FROM_LIST = 0;

    public static final int TYPE_HURRY = 1;
    public static final int TYPE_NORMAL = 0;

    private int from;

    PublishInfoEntity info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_doctor_go_out_order_publish);

        from = getIntent().getIntExtra(PARAMS_FROM,FROM_LIST);
        info = (PublishInfoEntity) getIntent().getSerializableExtra(PARAMS_DATA);
        initTitleBar();

        initInfo();

        loadOrderInfo();
    }

    public void initTitleBar(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("订单详情");
        menu_text = (TextView) findViewById(R.id.menu_text);

        if(from==FROM_PAY){
            menu_text.setVisibility(View.VISIBLE);
        }else{
            menu_text.setVisibility(View.GONE);
        }
        menu_text.setText(getString(R.string.cancel));
        menu_text.setOnClickListener(this);
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
    }

    public void initInfo(){
         type = (TextView) findViewById(R.id.type);
         orderid = (TextView) findViewById(R.id.orderid);

         patient_name = (TextView) findViewById(R.id.patient_name);
         patient_phone = (TextView) findViewById(R.id.patient_phone);
         patient_addr = (TextView) findViewById(R.id.patient_addr);
         patient_order_time = (TextView) findViewById(R.id.patient_order_time);
         description = (TextView) findViewById(R.id.description);
    }

    public void loadOrderInfo(){
        if(from==FROM_PAY){
            //支付完成获取相关信息
            if(info.getType()==TYPE_NORMAL){
                type.setText("缓");
            }else{
                type.setText("急");
            }

            orderid.setText("订单号:"+info.getOrderid());

            patient_name.setText(info.getName()+"  "+info.getGander()+"  "+info.getAge()+"岁");
            patient_phone.setText(info.getPhone());
            patient_addr.setText(info.getAddr());
            patient_order_time.setText(info.getPublish_time());

            description.setText(info.getDescription());

        }else{
            //----网络获取订单数据，然后加载进来
            PublishInfoEntity info = (PublishInfoEntity) getIntent().getSerializableExtra(PARAMS_DATA);

            if(info.getType()==TYPE_NORMAL){
                type.setText("缓");
            }else{
                type.setText("急");
            }

            orderid.setText("订单号:"+info.getOrderid());

            patient_name.setText(info.getName()+"  "+info.getGander()+"  "+info.getAge()+"岁");
            patient_phone.setText(info.getPhone());
            patient_addr.setText(info.getAddr());
            patient_order_time.setText(info.getPublish_time());

            description.setText(info.getDescription());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.menu_text:
                //---取消订单操作
                cancelOrder();
                break;
            default:
                break;
        }
    }

    public void cancelOrder(){
        Map<String,String> params = new HashMap<String,String>();
        params.put("orderid",info.getOrderid());
        params.put("user_id", BaseApplication.getInstances().getUser_id());
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(val!=null){
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }, HttpUrls.getCancelOrder(info.getOrderid()),params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(this)));
    }
}
