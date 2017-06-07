package com.androidquanjiakan.activity.index.out;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConsultantActivity_GoOut_Order_Take extends BaseActivity implements OnClickListener {

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
    private TextView good_at;

    public static final String PARAMS_FROM = "from";
    public static final String PARAMS_DATA = "data";

    public static final int FROM_PAY = 1;
    public static final int FROM_LIST = 0;

    public static final int TYPE_HURRY = 1;
    public static final int TYPE_NORMAL = 0;

    private int from;

    public static final String PARAMS_TYPE = "type";
    public static final String PARAMS_ORDERID = "order_id";
    private String order_id;
    private String typevalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_doctor_go_out_order_take);

        order_id = getIntent().getStringExtra(PARAMS_ORDERID);
        typevalue = getIntent().getStringExtra(PARAMS_TYPE);
        if(order_id==null || typevalue==null){
            BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order_Take.this,"传入参数异常!");
            finish();
            return;
        }

        initTitleBar();

        initInfo();

        loadOrderInfo();
    }

    public void initTitleBar(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("订单详情");
        menu_text = (TextView) findViewById(R.id.menu_text);
        if("1".equals(typevalue)){
            menu_text.setVisibility(View.VISIBLE);
        }else{
            menu_text.setVisibility(View.GONE);
        }
        menu_text.setText("取消");
        menu_text.setOnClickListener(this);

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
    }

    public void initInfo(){
        type = (TextView) findViewById(R.id.type);
        if("1".equals(typevalue)){
            type.setText("缓");
        }else{
            type.setText("急");
        }
        orderid = (TextView) findViewById(R.id.orderid);
        patient_name = (TextView) findViewById(R.id.patient_name);
        patient_phone = (TextView) findViewById(R.id.patient_phone);

        /**
         * 这里不需要---
         */
        patient_addr = (TextView) findViewById(R.id.patient_addr);
        patient_addr.setVisibility(View.GONE);

        patient_order_time = (TextView) findViewById(R.id.patient_order_time);
        description = (TextView) findViewById(R.id.description);
        good_at = (TextView) findViewById(R.id.good_at);
    }

    public void loadOrderInfo(){
        /**
         {
         "rows": [
         {
         "id": "144",
         "orderid": "QJKGRAB20160905084848418874",
         "medical_user_id": "144",
         "user_id": "10678",
         "doctor_id": "10689",
         "status": "2",
         "payment": "2",
         "total_fee": "0.01",
         "producetime": "2016-09-05 20:48:48",
         "publishtime": null,
         "grabtime": "2016-09-05 21:23:05",
         "canceltime": null,
         "finishtime": null,
         "name": "小张",
         "title": "副主任医师（副教授）",
         "hospital_name": "wqewqe",
         "good_at": "胃炎",
         "description": "呃呃呃r",
         "phone": "15889492686"
         }
         ],
         "total": 1
         }
         */
        //----网络获取订单数据，然后加载进来
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(val!=null && val.length()>0 && val.contains("rows")){
                    try {
                        JSONObject jsonObject = new JSONObject(val);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        JSONObject data = jsonArray.getJSONObject(0);

                        patient_name.setText(data.get("name").toString());
                        patient_phone.setText(data.get("phone").toString());
                        patient_order_time.setText(data.get("publishtime").toString());

                        description.setText(data.get("description").toString());
                        good_at.setText(data.get("good_at").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, HttpUrls.getPulishOrderDetail(order_id),null,Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(this)));


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
        params.put("orderid",order_id);
        params.put("user_id",BaseApplication.getInstances().getUser_id());
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(val!=null){
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }, HttpUrls.getCancelOrder(order_id),params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(this)));
    }
}
