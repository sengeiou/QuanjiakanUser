package com.androidquanjiakan.activity.index.out;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.PublishInfoEntity;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakan.main.wxapi.WXPayEntryActivity;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.AlipayHandler;
import com.quanjiakanuser.util.GsonParseUtil;
import com.quanjiakanuser.util.WeixinPayHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConsultantActivity_GoOut_Order extends BaseActivity implements OnClickListener {

    private final int MALE = 1;
    private final int FEMALE = 2;
    private final int HURRY = 2;
    private final int NORMAL = 1;

    private TextView tv_title;
    private ImageButton ibtn_back;

    private EditText value_name;

    private ImageView value_gander_male;
    private ImageView value_gander_female;
    private int gander;

    private EditText value_age;

    private EditText value_phone;

    private EditText value_addr;//需要保存获取的坐标的
    private Double lantitude;
    private Double longitude;
    private ImageView map_locate;

    private LinearLayout ll_addr;

    private ImageView type_hurry;
    private ImageView type_normal;
    private int type;

    private EditText description;

    private TextView order;



    public static final int REQUEST_PUSH = 1001;

    public static final String PARAMS_HURRY_MENORY = "hurry";
    public static final String PARAMS_NORMAL_MENORY = "normal";
    public static final String PARAMS_HURRY_MENORY_REAL = "hurry_real";
    public static final String PARAMS_NORMAL_MENORY_REAL = "normal_real";
    public static final String PARAMS_HURRY_MENORY_PLATFORM = "hurry_platform";
    public static final String PARAMS_NORMAL_MENORY_PLATFORM = "normal_platform";

    private String hurryMenory;
    private String normalMenory;
    private String hurryMenoryReal;
    private String normalMenoryReal;
    private String hurryMenoryPlatform;
    private String normalMenoryPlatform;
    private JsonObject object;
    private JsonObject paidinfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_doctor_go_out_order);

        hurryMenory = getIntent().getStringExtra(PARAMS_HURRY_MENORY);
        normalMenory = getIntent().getStringExtra(PARAMS_NORMAL_MENORY);
        hurryMenoryReal = getIntent().getStringExtra(PARAMS_HURRY_MENORY_REAL);
        hurryMenoryPlatform = getIntent().getStringExtra(PARAMS_HURRY_MENORY_PLATFORM);
        normalMenoryReal = getIntent().getStringExtra(PARAMS_NORMAL_MENORY_REAL);
        normalMenoryPlatform = getIntent().getStringExtra(PARAMS_NORMAL_MENORY_PLATFORM);

        initTitleBar();

        initView();

        defaultSelect();
    }

    public void initTitleBar(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("上门问诊");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
    }

    public void initView(){
        value_name = (EditText) findViewById(R.id.value_name);

        value_gander_male = (ImageView) findViewById(R.id.value_gander_male);
        value_gander_female = (ImageView) findViewById(R.id.value_gander_female);

        value_age = (EditText) findViewById(R.id.value_age);
        value_phone = (EditText) findViewById(R.id.value_phone);
        value_addr = (EditText) findViewById(R.id.value_addr);
        ll_addr = (LinearLayout) findViewById(R.id.ll_addr);
        map_locate = (ImageView) findViewById(R.id.map_locate);
        type_hurry = (ImageView) findViewById(R.id.type_hurry);
        type_normal = (ImageView) findViewById(R.id.type_normal);

        description = (EditText) findViewById(R.id.description);

        order = (TextView) findViewById(R.id.order);

        //
        value_gander_male.setOnClickListener(this);
        value_gander_female.setOnClickListener(this);
        type_hurry.setOnClickListener(this);
        type_normal.setOnClickListener(this);
        order.setOnClickListener(this);
        map_locate.setOnClickListener(this);
        value_addr.setOnClickListener(this);
        ll_addr.setOnClickListener(this);
    }

    public void defaultSelect(){
        gander = MALE;
        type = HURRY;
        selectGander(R.id.value_gander_male);
        selectType(R.id.type_hurry);
    }

    public void selectGander(int id){
        switch (id){
            case R.id.value_gander_male:
                if(gander!=MALE){
                    gander = MALE;
                    value_gander_male.setImageResource(R.drawable.choice_light);
                    value_gander_female.setImageResource(R.drawable.choice);
                }else{

                }
                break;
            case R.id.value_gander_female:
                if(gander!=FEMALE){
                    gander = FEMALE;
                    value_gander_male.setImageResource(R.drawable.choice);
                    value_gander_female.setImageResource(R.drawable.choice_light);
                }else{

                }
                break;
        }
    }

    public void selectType(int id){
        switch (id){
            case R.id.type_hurry:
                type = HURRY;
                type_hurry.setImageResource(R.drawable.list_female_light);
                type_normal.setImageResource(R.drawable.choice);
                break;
            case R.id.type_normal:
                type = NORMAL;
                type_hurry.setImageResource(R.drawable.choice);
                type_normal.setImageResource(R.drawable.list_male_light);
                break;
        }
    }

    public static final int PAY_TYPE_ALI = 1;
    public static final int PAY_TYPE_WECHAT = 2;
    public static final int PAY_TYPE_WALLET = 0;

    private Dialog payDialog;
    private int pay_type = -1;
    //支付的弹框
    public void showPayDialog() {
        pay_type = PAY_TYPE_ALI;//支付宝
        payDialog = QuanjiakanDialog.getInstance().getDialog(ConsultantActivity_GoOut_Order.this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_doctor_type_selecter_pay, null);
        RelativeLayout rl_exit = (RelativeLayout) view.findViewById(R.id.rl_exit);
        rl_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (payDialog != null) {
                    payDialog.dismiss();
                }
            }
        });
        LinearLayout type_line = (LinearLayout) view.findViewById(R.id.type_line);
        type_line.setVisibility(View.GONE);


        final ImageView item_choice2 = (ImageView) view.findViewById(R.id.item_choice2);
        final ImageView item_choice1 = (ImageView) view.findViewById(R.id.item_choice1);
        final ImageView item_choice3 = (ImageView) view.findViewById(R.id.item_choice3);
        RelativeLayout pay_wallet_line = (RelativeLayout) view.findViewById(R.id.pay_wallet_line);
        RelativeLayout pay_ali_line = (RelativeLayout) view.findViewById(R.id.pay_ali_line);
        RelativeLayout pay_wechat_line = (RelativeLayout) view.findViewById(R.id.pay_wechat_line);
        pay_ali_line.setOnClickListener(new OnClickListener() {//支付宝
            @Override
            public void onClick(View view) {
                item_choice2.setBackgroundResource(R.drawable.doctor_pay_list_light);
                item_choice1.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice3.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                pay_type = PAY_TYPE_ALI;
            }
        });
        pay_wechat_line.setOnClickListener(new OnClickListener() {//微信支付
            @Override
            public void onClick(View view) {
                item_choice2.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice1.setBackgroundResource(R.drawable.doctor_pay_list_light);
                item_choice3.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                pay_type = PAY_TYPE_WECHAT;
            }
        });
        pay_wallet_line.setOnClickListener(new OnClickListener() {//微信支付
            @Override
            public void onClick(View view) {
                item_choice2.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice1.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice3.setBackgroundResource(R.drawable.doctor_pay_list_light);
                pay_type = PAY_TYPE_WALLET;
            }
        });
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (payDialog != null) {
                    payDialog.dismiss();
                }
                /**
                 * 开始支付
                 */
                if(pay_type == PAY_TYPE_WALLET){
                    getRemainSum();
                }else{
                    startPayProgress();//微信支付，支付宝支付
                }
                /**
                 * 跳过支付，
                 */
            }
        });
        payDialog.setContentView(view);
    }
    //获取钱包数据。用于在钱包的dialog做显示
    public void getRemainSum(){
        HashMap<String, String> params = new HashMap<>();
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                try {
                    if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
                        JSONObject jsonObject = new JSONObject(val);
                        showMyWalletDialog(jsonObject.getString("member_wallet"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },HttpUrls.getWalletNumber()+"&member_id="+BaseApplication.getInstances().getUser_id(),params,Task.TYPE_GET_STRING_NOCACHE,null));
    }

    /**
     * 显示自己的钱包的对话框
     */
    Dialog myWalletDialog;
    public void showMyWalletDialog(final String payNumber) {
        myWalletDialog =  new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_doctor_mywallet_pay, null);
        RelativeLayout rl_exit = (RelativeLayout) view.findViewById(R.id.rl_exit);
        rl_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myWalletDialog != null) {
                    myWalletDialog.dismiss();
                }
                BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"已取消支付!");
            }
        });
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("支付明细");
        //real_pay_value
        TextView remaining_sum_value = (TextView) view.findViewById(R.id.remaining_sum_value);
        remaining_sum_value.setText(""+payNumber);

        TextView real_pay_value = (TextView) view.findViewById(R.id.real_pay_value);
        if(type==HURRY){
            real_pay_value.setText(hurryMenory);
        }else{
            real_pay_value.setText(normalMenory);
        }

        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myWalletDialog != null) {
                    myWalletDialog.dismiss();
                }
                /**
                 * 开始支付
                 */
                if(type==HURRY){
                    if(Double.parseDouble(hurryMenory)<=Double.parseDouble(payNumber)){
                        startPayProgress();
                    }else{
                        BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"余额不足，请换微信或支付宝支付!");
                    }
                }else{
                    if(Double.parseDouble(normalMenory)<=Double.parseDouble(payNumber)){
                        startPayProgress();
                    }else{
                        BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"余额不足，请换微信或支付宝支付!");
                    }
                }
                /**
                 * 跳过支付，
                 */
            }
        });
        WindowManager.LayoutParams lp = myWalletDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;

        myWalletDialog.setContentView(view, lp);
        myWalletDialog.show();
    }

    public void submitOrder(){
        if(value_name.length()<1){
            BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"请输入名称!");
            return;
        }
        if (value_name.length()>10){
            BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"输入名字过长！");
            return;
        }

        if(value_age.length()<1){
            BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"请输入年龄!");
            return;
        }

        if(Integer.parseInt(value_age.getText().toString())<1 || Integer.parseInt(value_age.getText().toString())>130 ){
            BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"请输入合理的年龄!");
            return;
        }

        if(value_phone.length()<1){
            BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"请输入手机号码!");
            return;
        }
        if(!CheckUtil.isPhoneNumber(value_phone.getText().toString())){
            BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"请输入正确的手机号码!");
            return;
        }
        if(value_age.length()<1 || lantitude==null || longitude==null){
            BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"请确定地址!");
            return;
        }
        if(description.length()<1){
            BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"请输入病情描述!");
            return;
        }

        showPayDialog();
    }

    /**
     * 跳转订单发布页面
     * @param orderid
     */
    public void jumpToPublish(String orderid) {
        PublishInfoEntity entity = new PublishInfoEntity();
        entity.setOrderid(orderid);
        if(type==HURRY){
            entity.setType(ConsultantActivity_GoOut_Order_Publish.TYPE_HURRY);
        }else{
            entity.setType(ConsultantActivity_GoOut_Order_Publish.TYPE_NORMAL);
        }
        entity.setName(value_name.getText().toString());
        if(gander==MALE){
            entity.setGander("男");
        }else{
            entity.setGander("女");
        }
        entity.setAge(value_age.getText().toString());
        entity.setAddr(value_addr.getText().toString());
        entity.setPhone(value_phone.getText().toString());
        entity.setPublish_time(System.currentTimeMillis()+"");
        entity.setDescription(description.getText().toString());

        Intent intent = new Intent(ConsultantActivity_GoOut_Order.this,ConsultantActivity_GoOut_Order_Publish.class);
        intent.putExtra(ConsultantActivity_GoOut_Order_Publish.PARAMS_FROM,ConsultantActivity_GoOut_Order_Publish.FROM_PAY);
        intent.putExtra(ConsultantActivity_GoOut_Order_Publish.PARAMS_DATA,entity);
        startActivityForResult(intent,REQUEST_PUSH);

    }

    public void startPayProgress(){
        /**
         * 提交信息获取订单信息
         */
        Map<String,String> params = new HashMap<String,String>();
//        params.put("subject", "滴滴医生");
//        params.put("body", "支付滴滴医生费用");
//
//        params.put("name", value_name.getText().toString());
//        params.put("sex",gander+"");
//        params.put("age",value_age.getText().toString());
//        params.put("describe",description.getText().toString());
//        params.put("phone",value_phone.getText().toString());
//        params.put("longitude",""+longitude.doubleValue());
//        params.put("latitude",""+lantitude.doubleValue());
//        params.put("address",""+value_addr.getText().toString());
//        params.put("patient_type",""+type);
//        params.put("payment_channel",(pay_type/*==PAY_TYPE_ALI?PAY_TYPE_ALI:PAY_TYPE_WECHAT*/)+"");
//
//        params.put("total_price",(type==HURRY?hurryMenory:normalMenory));
//        params.put("real_price",(type==HURRY?hurryMenoryReal:normalMenoryReal));
//        params.put("platform_price",(type==HURRY?hurryMenoryPlatform:normalMenoryPlatform));
        final double total_fee = Double.parseDouble((type==HURRY?hurryMenory:normalMenory));
//        {"userId":10041,"paymentChannel":1,"totalPrice":0.01,"realPrice":0,"platformPrice":0,"name":"dsd","sex":1,"age":1,"phone":"123123","describe":"toutong,jiangtong","longitude":113.243092,"latitude":23.130658,"address":"tianhe","patientType":1}
        String jsonString = "{\"userId\":"+ QuanjiakanSetting.getInstance().getUserId()+",\"paymentChannel\":"+pay_type+",\"totalPrice\":"+(type==HURRY?hurryMenory:normalMenory)+",\"realPrice\":"+(type==HURRY?hurryMenoryReal:normalMenoryReal)+
                ",\"platformPrice\":"+(type==HURRY?hurryMenoryPlatform:normalMenoryPlatform)+",\"name\":"+"\""+value_name.getText().toString()+"\""+",\"sex\":"+gander+",\"age\":"+value_age.getText().toString()+",\"phone\":"+"\""+value_phone.getText().toString()+"\""+",\"describe\":"+"\""+description.getText().toString()+"\""+",\"longitude\":"+longitude.doubleValue()+
                ",\"latitude\":"+lantitude.doubleValue()+",\"address\":"+"\""+value_addr.getText().toString()+"\""+",\"patientType\":"+type+"}";

        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(val!=null && val.length()>0  && !"null".equals(val.toLowerCase())){
                    HttpResponseResult result = new HttpResponseResult(val);
                    if(result.isResultOk()){
                        /**
                         * 订单生成成功
                         */
                        JsonObject object1 = result.getObject();
                        LogUtil.e("生成就近寻医订单-----------"+object1.toString());
                        String message = object1.get("message").getAsString();
                        object = new GsonParseUtil(message).getJsonObject();

                        if(pay_type != PAY_TYPE_WALLET){
                            paidinfo = object.get("paidinfo").getAsJsonObject();
                        }

                        /**
                         * 赋值订单信息
                         */
                        if(pay_type==PAY_TYPE_ALI){
                            //支付宝支付
                            aliPay(paidinfo.get("out_trade_no").getAsString(), total_fee, paidinfo, "1");
                        }else if (pay_type==PAY_TYPE_WECHAT) {

                            wxPayJumpFlag = true;
                            wxOrderID = object.get("orderid").getAsString();
                            //根据订单信息调用微信支付
                            new WeixinPayHandler(ConsultantActivity_GoOut_Order.this, WXPayEntryActivity.FROM_DOCTOR_OUT_GO_PAY,null).pay(paidinfo);
                        }else if(pay_type == PAY_TYPE_WALLET){
//                            vertifyWalletPayment(paidinfo.get("out_trade_no").getAsString());
                            vertifyWalletPayment(object.get("orderid").getAsString());
//                            BaseApplication.getInstances().toast("进入钱包支付,后续步骤需要确定后完善!");
                        }
                    }else {
                        Toast.makeText(ConsultantActivity_GoOut_Order.this,/*"预约失败!"*/result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ConsultantActivity_GoOut_Order.this,"生成订单失败失败!", Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.outGoCreateOrder()+"&data="+jsonString,params,Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(this)));
    }

    /**
     * 支付宝支付
     * @param orderid
     * @param total_fee
     * @param paidinfo
     * @param pay_channel
     */
    protected void aliPay(final String orderid,final double total_fee,JsonObject paidinfo,final String pay_channel){
        //支付总额
        new AlipayHandler(this, new AlipayHandler.AlipayCallback() {
            @Override
            public void paidComplete(int type, String msg, String result) {
                // TODO Auto-generated method stub
                if(type == AlipayHandler.PAID_SUCCESS){
                    //支付成功，并将支付提交提交到后台
                    vertifyAliPayment(orderid);
                }else if (type == AlipayHandler.PAID_FAILED) {
                    //支付失败
                    BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"支付失败!");
                }
            }
        }).pay(paidinfo);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        if(wxPayJumpFlag){//从微信支付返回
            LogUtil.e("从微信支付返回!");
            wxPayJumpFlag = false;
            if(wxPayStatus == WX_PAY_OK){
                /**
                 * 刷新数据状态
                 */
                LogUtil.e("微信支付状态---WX_PAY_OK ");//微信支付与支付宝相同，都是传入订单ID即可
                vertifyWechatPayment(wxOrderID);
                /**
                 * 下面几种状态处理都相同，仅支付成功后需要刷新当前页面数据，以获得最新数据状态
                 */
            }else {
                BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"微信支付失败!");
                LogUtil.e("微信支付状态---WX_PAY_FAIL ----订单ID："+wxOrderID);
            }
            wxPayStatus = WX_PAY_INIT;
        }else{
            initPay();
        }
    }

    public void initPay(){
        wxPayStatus = 0;
        wxPayJumpFlag = false;
    }

    private String wxOrderID;
    public static int wxPayStatus = 0;
    public static boolean wxPayJumpFlag = false;
    public static final int WX_PAY_INIT = 0;
    public static final int WX_PAY_OK = 1;
    /**
     * 支付宝支付成功，返回结果给客户端
     * 提交支付结果到客户端，并验证
     * @param orderid
     */
    protected void vertifyAliPayment(final String orderid){

        HashMap<String, String> params = new HashMap<>();
//        params.put("orderid", orderid);
        String jsonString = "{\"userId\":"+ QuanjiakanSetting.getInstance().getUserId()+",\"orderid\":"+"\""+orderid+"\""+"}";

        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
                    //支付成功后跳转
                    HttpResponseResult result = new HttpResponseResult(val);
                    if(result.isResultOk()){
                        jumpToPublish(orderid);
                    }else{
                        BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,result.getMessage());
                    }
                }
            }
        }, HttpUrls.outGoPublishOrder()+"&data="+jsonString, params, Task.TYPE_GET_STRING_NOCACHE, null));
    }
    /**
     * 微信的支付成功返回结果
     * 提交支付结果到客户端，并验证
     * @param orderid
     */
    protected void vertifyWechatPayment(final String orderid){

        HashMap<String, String> params = new HashMap<>();
//        params.put("orderid", orderid);
        String jsonString = "{\"userId\":"+ QuanjiakanSetting.getInstance().getUserId()+",\"orderid\":"+"\""+orderid+"\""+"}";
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                if(val!=null && val.length()>0){
                    HttpResponseResult result = new HttpResponseResult(val);
                    if(result.isResultOk()){
                        jumpToPublish(orderid);
                    }else{
                        BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,result.getMessage());
                    }
                }
            }
        }, HttpUrls.outGoPublishOrder()+"&data="+jsonString, params, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    /**
     * 钱包的返回结果
     * @param orderid
     */
    protected void vertifyWalletPayment(final String orderid){

        HashMap<String, String> params = new HashMap<>();
//        params.put("orderid", orderid);

        String jsonString = "{\"userId\":"+ QuanjiakanSetting.getInstance().getUserId()+",\"orderid\":"+"\""+orderid+"\""+"}";
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                if(val!=null && val.length()>0){
                    HttpResponseResult result = new HttpResponseResult(val);
                    if(result.isResultOk()){
                        jumpToPublish(orderid);
                    }else{
                        BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,result.getMessage());
                    }
                }
            }
        }, HttpUrls.outGoPublishOrder()+"&data="+jsonString, params, Task.TYPE_GET_STRING_NOCACHE, null));
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
            case R.id.value_gander_male:
                selectGander(R.id.value_gander_male);
                break;
            case R.id.value_gander_female:
                selectGander(R.id.value_gander_female);
                break;
            case R.id.type_hurry:
                selectType(R.id.type_hurry);
                break;
            case R.id.type_normal:
                selectType(R.id.type_normal);
                break;
            case R.id.order:
                submitOrder();
                break;
            case R.id.map_locate:
                /**
                 * 跳转到地图定位
                 */
                Intent map_loc = new Intent(ConsultantActivity_GoOut_Order.this,ConsultantActivity_GoOut_Locate_backup.class);
                startActivityForResult(map_loc,REQUEST_MAP);
                break;
            case R.id.value_addr:
//                Intent map_locs2 = new Intent(ConsultantActivity_GoOut_Order.this,ConsultantActivity_GoOut_Locate_backup.class);
//                startActivityForResult(map_locs2,REQUEST_MAP);
                break;
            case R.id.ll_addr:
                /**
                 * 跳转到地图定位
                 */
                Intent map_locs = new Intent(ConsultantActivity_GoOut_Order.this,ConsultantActivity_GoOut_Locate_backup.class);
                startActivityForResult(map_locs,REQUEST_MAP);
                break;
            default:
                break;
        }
    }

    private final int REQUEST_MAP = 1000;

    private final String PARAMS_ADDR = "addr";
    private final String PARAMS_LAT = "lat";
    private final String PARAMS_LON = "lon";

    String address;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_MAP:
                if(resultCode == RESULT_OK){
                    //获取到用户指定的位置---需要包含经纬度信息
                    address =data.getStringExtra(PARAMS_ADDR);
                    lantitude = data.getDoubleExtra(PARAMS_LAT,-1);
                    longitude = data.getDoubleExtra(PARAMS_LON,-1);
                    if(address!=null && address.length()>0 && lantitude!=null && lantitude!=-1  && longitude!=null && longitude!=-1){
                        value_addr.setText(address);
                    }else{
                        BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Order.this,"定位数据异常!");
                    }
                }
                break;
            case REQUEST_PUSH:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
