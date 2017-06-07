package com.androidquanjiakan.activity.index.doctor;

import java.util.HashMap;

import com.androidquanjiakan.activity.common.ImageViewerActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.util.StringUtil;
import com.androidquanjiakan.view.CircleTransformation;
import com.google.gson.JsonNull;
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
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.quanjiakanuser.util.WeixinPayHandler;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.wxapi.entity.DoctorDetailParamEntity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import cn.jmessage.android.uikit.chatting.ChatActivity;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * TODO 医生详情
 */
public class DoctorDetailActivity extends BaseActivity implements OnClickListener{

    private TextView tv_book, tv_name, tv_clinic_title, tv_hospital, tv_good_at, tv_content, tv_title;
    private TextView tv_rank_name;
    private ImageView image;
    private Context mContext;
    private ImageButton ibtn_back;
    private RatingBar rBar;

    public static boolean wxPayJumpFlag = false;
    public static int wxPayStatus = 0;

    public static final int WX_PAY_INIT = 0;
    public static final int WX_PAY_OK = 1;
    public static final int WX_PAY_COMM = -1;
    public static final int WX_PAY_CANCEL = -2;
    public static final int WX_PAY_OTHER = -3;
    public static final String PARAMS_PAYNUMBER = "pay_number";

    public static final int PAY_TYPE_ALI = 1;
    public static final int PAY_TYPE_WECHAT = 2;
    public static final int PAY_TYPE_WALLET = 0;

    private Dialog payDialog;
    private Dialog typeDialog;
    private String tempDoctor_id;
    private int position = -1;
    private int pay_number = 0;
    private int pay_type = -1;
    private final int REQUEST_CHAT = 12058;
    private JsonObject price;
    private JsonObject doctor;
    private JsonObject status;
    private String isPay;
    private String type;
    private Long millisecond;
    private JsonObject paidinfo;
    private float servicePrice;
    private float platformPrice;
    private float doctorPrice;
    private JsonObject object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = DoctorDetailActivity.this;
        setContentView(R.layout.layout_doctor_detail);
        pay_number = getIntent().getIntExtra(PARAMS_PAYNUMBER, 0);
        LogUtil.e("传入的支付次数参数：" + pay_number);
        initTitleBar();
        initView();
        loadDoctorDetail();
        loadPrice();
        checkStatus();
    }

    /**
     * 返回详情界面，检查状态
     *
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        checkStatus();
    }

    /**
     * 检查聊天状态
     */
    protected void checkStatus() {
        HashMap<String, String> params = new HashMap<>();
        String entity = "&user_id="+BaseApplication.getInstances().getUser_id()+"&doctor_id="+tempDoctor_id;
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(null==val||"".equals(val)){
                    BaseApplication.getInstances().toast(DoctorDetailActivity.this,"网络连接错误！");
                    return;
                }else {
                    status = new GsonParseUtil(val).getJsonObject();
                    isPay = status.get("isPay").getAsString();
                    /**
                     * single price
                     */
                    servicePrice = status.get("servicePrice").getAsFloat();
                    platformPrice = status.get("platformPrice").getAsFloat();
                    doctorPrice = status.get("doctorPrice").getAsFloat();
                    if("1".equals(isPay)){
                        type = status.get("type").getAsString();//状态 0未支付  1已支付  2付费未回复 3开始聊天
                        millisecond = status.get("millisecond").getAsLong();//聊天剩余时间
                    }
                }
            }
        },HttpUrls.checkChatStatus()+entity,params,Task.TYPE_GET_STRING_NOCACHE,null));

    }

    public void initTitleBar(){
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("医护详情");
    }

    /**
     *
     */
    protected void initView() {
        tv_book = (TextView) findViewById(R.id.tv_book);
        tv_book.setOnClickListener(this);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_clinic_title = (TextView) findViewById(R.id.tv_clinic_title);
        tv_hospital = (TextView) findViewById(R.id.hospital);
        tv_good_at = (TextView) findViewById(R.id.tv_good_at);
        tv_good_at.setVisibility(View.GONE);
        tv_content = (TextView) findViewById(R.id.tv_content);
        image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(this);
        tv_rank_name = (TextView) findViewById(R.id.tv_rank_name);
        rBar = (RatingBar) findViewById(R.id.rbar);
    }

    protected void loadDoctorDetail() {
        tempDoctor_id = getIntent().getStringExtra("doctor_id");
        HashMap<String, String> params = new HashMap<>();
        params.put("id", tempDoctor_id);
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                doctor = new GsonParseUtil(val).getJsonObject();
                if (doctor.has("name")) {

                    if(doctor.has("name") && !(doctor.get("name") instanceof JsonNull)){
                        tv_name.setText(doctor.get("name").getAsString());
                    }else{
                        tv_name.setText("");
                    }

                    if(doctor.has("title") && !(doctor.get("title") instanceof JsonNull)){
                        tv_rank_name.setText(doctor.get("title").getAsString());
                    }else{
                        tv_rank_name.setText("");
                    }

                    if(doctor.has("clinic_name") && !(doctor.get("clinic_name") instanceof JsonNull)){
                        tv_clinic_title.setText(doctor.get("clinic_name").getAsString());
                    }else{
                        tv_clinic_title.setText("");
                    }

                    tv_hospital.setVisibility(View.VISIBLE);
                    if(doctor.has("hospital_name") && !(doctor.get("hospital_name") instanceof JsonNull)){
                        tv_hospital.setText(doctor.get("hospital_name").getAsString());//去掉了这个字段hospital_name
                    }else{
                        tv_hospital.setText("");//去掉了这个字段hospital_name
                    }

                    if(doctor.has("description") && !(doctor.get("description") instanceof JsonNull)){
                        tv_content.setText(doctor.get("description").getAsString());
                    }else{
                        tv_content.setText("");
                    }

                    tv_book.setTag(doctor.get("id").getAsString());


                    if(doctor.has("good_at") && !(doctor.get("good_at") instanceof JsonNull)){
                        tv_good_at.setText(doctor.get("good_at").getAsString());
                    }else{
                        tv_good_at.setText("");
                    }

                    if(doctor.has("icon") && doctor.get("icon")!=null && !(doctor.get("icon") instanceof JsonNull) &&
                            doctor.get("icon").getAsString().toLowerCase().startsWith("http")){
                        Picasso.with(BaseApplication.getInstances()).load(doctor.get("icon").getAsString()).transform(new CircleTransformation()).into(image);
                    }else{
                        image.setImageResource(R.drawable.ic_patient);
                    }

                    if(doctor.has("recommend_rate") && !(doctor.get("recommend_rate") instanceof JsonNull)){
                        rBar.setRating(doctor.get("recommend_rate").getAsInt());
                    }else{
                        rBar.setRating(5);
                    }
                }
            }
        }, HttpUrls.getDoctorDetail(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(mContext)));

    }

    private void loadPrice() {
        HashMap<String, String> params = new HashMap<>();
        /**
         * 获取价格列表【根据】
         */
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                if (val != null && val.length() > 0) {
                    price = new GsonParseUtil(val).getJsonObject();
                }
            }
        }, HttpUrls.getDoctorPrice(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(mContext)));
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);

        if (wxPayJumpFlag) {//从微信支付返回
            wxPayJumpFlag = false;
            if (wxPayStatus == WX_PAY_OK) {
                LogUtil.e("微信支付状态---WX_PAY_OK ");//微信支付与支付宝相同，都是传入订单ID即可
                vertifyWechatPayment(wxOrderID);
            } else {
                BaseApplication.getInstances().toast(DoctorDetailActivity.this,"微信支付失败!");
                LogUtil.e("微信支付状态---WX_PAY_FAIL ----订单ID：" + wxOrderID);
            }
            wxPayStatus = WX_PAY_INIT;
        }
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        int id = arg0.getId();
        if (id == R.id.tv_book) {
            /*if (pay_number > 0 || Integer.parseInt(doctor.get("paid").getAsString()) > 0) {
                Toast.makeText(DoctorDetailActivity.this, "该医生已支付过!", Toast.LENGTH_SHORT).show();
                jumpToChat();
            } else {
                position = 1;
                //点击咨询后弹出的dialog
//                showTypeDialog();//测试支付宝流程通过--微信测试通过
                showPayDialog();
            }*/
            if("1".equals(isPay)){
                jumpToChat(status.get("orderid").getAsString());
            }else if("0.00".equals(status.get("servicePrice").getAsString())){
                jumpToChat();
            }else {
                position = 1;
                //点击咨询后弹出支付dialog
//                showTypeDialog();//测试支付宝流程通过--微信测试通过
                showPayDialog();
            }
        } else if (id == R.id.ibtn_back) {
            finish();
        } else if(id == R.id.image){
            if(doctor!=null &&
                    doctor.has("icon") &&
                    doctor.get("icon").getAsString()!=null &&
                    doctor.get("icon").getAsString().toLowerCase().startsWith("http")){
                Intent intent = new Intent(this, ImageViewerActivity.class);
                intent.putExtra(BaseConstants.PARAMS_URL,doctor.get("icon").getAsString());
                startActivity(intent);
            }else{
                BaseApplication.getInstances().toast(DoctorDetailActivity.this,"该医护人员未设置头像!");
            }
        }
    }

    /**
     * 实例化类型选择对话框---周、月、季、年等
     */

    /**
     * 支付类型选择对话框
     */
    public void showPayDialog() {
        pay_type = PAY_TYPE_WALLET;//钱包
        payDialog = QuanjiakanDialog.getInstance().getDialog(DoctorDetailActivity.this);
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
        type_line.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (payDialog != null) {
                    payDialog.dismiss();
                }
//                showTypeDialog();
            }
        });
        TextView content = (TextView) view.findViewById(R.id.content);
        /**
         * 根据选择的位置显示对应的价格数据
         */
        String rank = "expert";
        if ("主任医师（教授）".equals(tv_rank_name.getText().toString()) || tv_rank_name.getText().toString().contains("（教授）") || tv_rank_name.getText().toString().contains("(教授)")) {
            rank = "director";
        } else if ("专家".equals(tv_rank_name.getText().toString())  || tv_rank_name.getText().toString().contains("专家")) {
            rank = "expert";
        } else if ("副主任医师（副教授）".equals(tv_rank_name.getText().toString()) || tv_rank_name.getText().toString().contains("副教授")) {
            rank = "vicedirector";
        }
        JsonObject temp;
        switch (position) {
            case 1:
                temp = new GsonParseUtil(price.get(rank).getAsJsonObject().get("week").getAsString()).getJsonObject();
//                SpannableString string = new SpannableString(temp.get("total_price").getAsString() + "/医护咨询");
                SpannableString string = new SpannableString(StringUtil.getTwoBitDecimalString(servicePrice) + "  元/次(每次限时60分钟)");
                string.setSpan(new ForegroundColorSpan(Color.RED), 0, (StringUtil.getTwoBitDecimalString(servicePrice) + "").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.setText(string);
                break;
            default:
//                content.setText("300/医护咨询");
                temp = new GsonParseUtil(price.get(rank).getAsJsonObject().get("week").getAsString()).getJsonObject();
//                SpannableString stringDefault = new SpannableString(temp.get("total_price").getAsString() + "/医护咨询");
                SpannableString stringDefault = new SpannableString(StringUtil.getTwoBitDecimalString(servicePrice) + "  元/次(每次限时60分钟)");
                stringDefault.setSpan(new ForegroundColorSpan(Color.RED), 0, (StringUtil.getTwoBitDecimalString(servicePrice) + "").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.setText(stringDefault);
                break;
        }

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
                    startPayProgress();
                }
                /**
                 * 跳过支付，
                 */
//                BaseApplication.getInstances().toast("跳过支付流程,直接进入聊天!");
//                jumpToChat();
            }
        });
        payDialog.setContentView(view);
    }

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
                BaseApplication.getInstances().toast(DoctorDetailActivity.this,"已取消支付!");
            }
        });
        /**
         *
         */
        TextView hint = (TextView) view.findViewById(R.id.hint);
        hint.setText("账户余额不足，请更换微信或支付宝支付!");
        if(Double.parseDouble(servicePrice+"")<=Double.parseDouble(payNumber)){
            hint.setVisibility(View.GONE);
        }else{
            hint.setVisibility(View.VISIBLE);
        }
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("支付明细");
        //real_pay_value
        TextView remaining_sum_value = (TextView) view.findViewById(R.id.remaining_sum_value);
        remaining_sum_value.setText(""+payNumber);

        TextView real_pay_value = (TextView) view.findViewById(R.id.real_pay_value);

        //------------------------
        real_pay_value.setText(servicePrice+"");

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
                if(Double.parseDouble(servicePrice+"")<=Double.parseDouble(payNumber)){
                    startPayProgress();
                }else{
//                    showLowDialog();
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

    private Dialog lowMoneyDialog;
    public void showLowDialog() {
        lowMoneyDialog =  new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_common_confirm, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("提示");
        //real_pay_value
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setText("账户余额不足，请更换微信或支付宝支付!");

        TextView confirm = (TextView) view.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lowMoneyDialog != null) {
                    lowMoneyDialog.dismiss();
                }
            }
        });
        TextView cancel = (TextView) view.findViewById(R.id.btn_cancel);
        cancel.setVisibility(View.GONE);
        WindowManager.LayoutParams lp = lowMoneyDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        lowMoneyDialog.setContentView(view, lp);
        lowMoneyDialog.show();
    }


    private String wxOrderID;

    public void startPayProgress() {
        String service_typeValue;
        String rank = "expert";
        if ("主任医师（教授）".equals(tv_rank_name.getText().toString()) || tv_rank_name.getText().toString().contains("（教授）") || tv_rank_name.getText().toString().contains("(教授)")) {
            rank = "director";
        } else if ("专家".equals(tv_rank_name.getText().toString())  || tv_rank_name.getText().toString().contains("专家")) {
            rank = "expert";
        } else if ("副主任医师（副教授）".equals(tv_rank_name.getText().toString()) || tv_rank_name.getText().toString().contains("副教授")) {
            rank = "vicedirector";
        }

        float memory = 0;
        JsonObject temp;
        switch (position) {
            case 1:
                temp = new GsonParseUtil(price.get(rank).getAsJsonObject().get("week").getAsString()).getJsonObject();
                memory = temp.get("total_price").getAsFloat();//周
                break;
            case 2:
                temp = new GsonParseUtil(price.get(rank).getAsJsonObject().get("month").getAsString()).getJsonObject();
                memory = temp.get("total_price").getAsFloat();//月
                break;
            case 3:
                temp = new GsonParseUtil(price.get(rank).getAsJsonObject().get("quarter").getAsString()).getJsonObject();
                memory = temp.get("total_price").getAsFloat();//季
                break;
            case 4:
                temp = new GsonParseUtil(price.get(rank).getAsJsonObject().get("halfyear").getAsString()).getJsonObject();
                memory = temp.get("total_price").getAsFloat();//半年
                break;
            case 5:
                temp = new GsonParseUtil(price.get(rank).getAsJsonObject().get("year").getAsString()).getJsonObject();
                memory = temp.get("total_price").getAsFloat();//年
                break;
            default:
                temp = new GsonParseUtil(price.get(rank).getAsJsonObject().get("week").getAsString()).getJsonObject();
                position = 1;
                memory = temp.get("total_price").getAsFloat();//周
                break;
        }

        HashMap<String, String> params = new HashMap<>();
        final double total_fee = memory;
        String jsonString = "{\"doctorId\":"+ Integer.parseInt(tempDoctor_id)+",\"userId\":"+QuanjiakanSetting.getInstance().getUserId()+
                ",\"totalPrice\":"+servicePrice+",\"realPrice\":"+doctorPrice+",\"platformPrice\":"+platformPrice+",\"paymentChannel\":"+pay_type+"}";
        /**
         *
         * TODO 修改支付验证信息，与支付验证接口地址
         */
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                if (val != null && val.length() > 0) {
                    HttpResponseResult result = new HttpResponseResult(val);
                    if (result.isResultOk()) {
                        JsonObject object1 = result.getObject();
                        LogUtil.e("生成订单-----------"+object1.toString());
                        String message = object1.get("message").getAsString();
                        object = new GsonParseUtil(message).getJsonObject();
                        if (pay_type!=PAY_TYPE_WALLET){
                            paidinfo = object.get("paidinfo").getAsJsonObject();
                        }
                        if (pay_type == PAY_TYPE_ALI) {

                            //支付宝支付
                            aliPay(paidinfo.get("out_trade_no").getAsString(), total_fee, paidinfo, "1");
                        } else if (pay_type == PAY_TYPE_WECHAT) {
                            //微信支付
                            wxPayJumpFlag = true;
                            wxOrderID = object.get("orderid").getAsString();

                            DoctorDetailParamEntity entity = new DoctorDetailParamEntity();
                            entity.setDoctorID(tempDoctor_id);
                            new WeixinPayHandler(DoctorDetailActivity.this, WXPayEntryActivity.FROM_DOCTOR_PAY, entity).pay(paidinfo);
                        }else if(pay_type == PAY_TYPE_WALLET){
//                            vertifyWalletPayment(paidinfo.get("out_trade_no").getAsString());
                            vertifyWalletPayment(object.get("orderid").getAsString());
//                            vertifyWalletPayment(object.get("orderid").getAsString());
//                            BaseApplication.getInstances().toast("进入钱包支付,后续步骤需要确定后完善!");
                        }
                    } else {
                        Toast.makeText(DoctorDetailActivity.this, "服务器生成订单失败!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, HttpUrls.getConsultOrderInfo()+"&data="+jsonString, params, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(DoctorDetailActivity.this)));
    }

    protected void aliPay(final String orderid, final double total_fee, JsonObject paidinfo, final String pay_channel) {
        //支付总额
        new AlipayHandler(this, new AlipayHandler.AlipayCallback() {

            @Override
            public void paidComplete(int type, String msg, String result) {
                // TODO Auto-generated method stub
                LogUtil.e("type  [PAID_SUCCESS==1]:" + type + "   ---  msg:" + msg + "    --- result:" + result + "          ***[走支付宝SDK进行支付]");
                if (type == AlipayHandler.PAID_SUCCESS) {
                    //支付成功，并将支付提交提交到后台
                    JsonObject object = new GsonParseUtil(result).getJsonObject();
                    vertifyAliPayment(object);
                } else if (type == AlipayHandler.PAID_FAILED) {
                    //支付失败
                    Toast.makeText(DoctorDetailActivity.this, "支付失败，请重试", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(DoctorDetailActivity.this, "支付失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }).pay(paidinfo);
    }

    /**
     * 变更微信支付的订单结果
     *返回结果
     * @param orderid
     */
    protected void vertifyWechatPayment(final String orderid) {
        HashMap<String, String> params = new HashMap<>();
//        params.put("orderid", orderid);//订单id
//        params.put("paidinfo", "");//支付宝，返回的result
//        params.put("payment_channel", "2");//1、支付宝；2微信
//        {"userId":10041,"orderid":"QJKFAMOUS2016110904313242116"}
//        String jsonString =  "{\"userId\":10041,\"orderid\":\"QJKFAMOUS2016110904313242116\"}";
        String jsonString = "{\"userId\":"+ QuanjiakanSetting.getInstance().getUserId()+",\"orderid\":"+"\""+orderid+"\""+"}";
//        params.put("data",jsonString);
        LogUtil.e("jsonString-------------"+jsonString);
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                HttpResponseResult result = new HttpResponseResult(val);
                if (result.getCode().equals("200")) {
                    //支付成功
                    LogUtil.e("[走自己的接口进行支付校验---结果成功]");
//                    Toast.makeText(DoctorDetailActivity.this, "支付校验成功!", Toast.LENGTH_SHORT).show();
                    pay_number++;
                    jumpToChat(orderid);
                } else {
                    LogUtil.e("[走自己的接口进行支付校验---结果失败]");
                    Toast.makeText(DoctorDetailActivity.this, "支付校验失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.publishConsultOrderInfo()+"&data="+jsonString, params, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    protected void vertifyWalletPayment(final String orderid) {
        HashMap<String, String> params = new HashMap<>();
//        params.put("orderid", orderid);//订单id
//        params.put("paidinfo", "");//支付宝，返回的result
//        params.put("payment_channel", "0");//1、支付宝；2微信
//        String jsonString =  "{\"userId\":10041,\"orderid\":\"QJKFAMOUS2016110904313242116\"}";
        String jsonString = "{\"userId\":"+ QuanjiakanSetting.getInstance().getUserId()+",\"orderid\":"+"\""+orderid+"\""+"}";
//        params.put("data",jsonString);
        LogUtil.e("jsonString-------------"+jsonString);
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                HttpResponseResult result = new HttpResponseResult(val);
                if (result.getCode().equals("200")) {
                    //支付成功
                    LogUtil.e("[走自己的接口进行支付校验---结果成功]");
//                    Toast.makeText(DoctorDetailActivity.this, "支付校验成功!", Toast.LENGTH_SHORT).show();
                    pay_number++;
                    jumpToChat(orderid);
                } else {
                    LogUtil.e("[走自己的接口进行支付校验---结果失败]");
                    Toast.makeText(DoctorDetailActivity.this, "支付校验失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.publishConsultOrderInfo()+"&data="+jsonString, params, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    /**
     * 变更支付宝支付订单结果
     *
     * @param payInfo
     */
    protected void vertifyAliPayment(final JsonObject payInfo) {
        HashMap<String, String> params = new HashMap<>();
//        params.put("orderid", payInfo.get("orderid").getAsString());//订单id
//        params.put("paidinfo", payInfo.toString());//支付宝，返回的result
//        params.put("payment_channel", "1");//1、支付宝；2微信
//        String jsonString =  "{\"userId\":10041,\"orderid\":\"QJKFAMOUS2016110904313242116\"}";
        String jsonString = "{\"userId\":"+ QuanjiakanSetting.getInstance().getUserId()+",\"orderid\":"+"\""+payInfo.get("orderid").getAsString()+"\""+"}";
//        params.put("data",jsonString);
        LogUtil.e("jsonString-------------"+jsonString);
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                HttpResponseResult result = new HttpResponseResult(val);
                if (result.getCode().equals("200")) {
                    //支付成功
                    LogUtil.e("[走自己的接口进行支付校验---结果成功]");
//                    Toast.makeText(DoctorDetailActivity.this, "支付校验成功!", Toast.LENGTH_SHORT).show();
                    pay_number++;
                    jumpToChat(payInfo.get("orderid").getAsString());
                } else {
                    LogUtil.e("[走自己的接口进行支付校验---结果失败]");
                    Toast.makeText(DoctorDetailActivity.this, "支付校验失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.publishConsultOrderInfo()+"&data="+jsonString, params, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    public void jumpToChat(final String orderid) {//进入聊天页面

        tv_book.setEnabled(false);
        if (JMessageClient.getMyInfo() != null) {
            Intent intent = new Intent(DoctorDetailActivity.this, ChatActivity.class);
            intent.putExtra(ChatActivity.TARGET_ID, "Doc"+tempDoctor_id + "" /*"10043"*/);//实际传入的ID
            intent.putExtra(ChatActivity.TARGET_APP_KEY, BaseConstants.jpush_doctor_appkey);//实际传入的KEY
            intent.putExtra(ChatActivity.ORDER_ID,orderid);
            startActivityForResult(intent, REQUEST_CHAT);
            tv_book.setEnabled(true);
        } else {
            JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX+BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                @Override
                public void gotResult(int status, String desc) {
                    tv_book.setEnabled(true);
                    if (status == 0) {
                        Intent intent = new Intent(DoctorDetailActivity.this, ChatActivity.class);
                        intent.putExtra(ChatActivity.TARGET_ID, "Doc"+tempDoctor_id + "" /*"10043"*/);//实际传入的ID
                        intent.putExtra(ChatActivity.TARGET_APP_KEY, BaseConstants.jpush_doctor_appkey);//实际传入的KEY
                        intent.putExtra(ChatActivity.ORDER_ID,orderid);
                        startActivityForResult(intent, REQUEST_CHAT);
                        tv_book.setEnabled(true);
                    } else {
                        tv_book.setEnabled(true);
                    }
                }
            });
        }
    }

    public void jumpToChat() {//进入聊天页面

        tv_book.setEnabled(false);
        if (JMessageClient.getMyInfo() != null) {
            Intent intent = new Intent(DoctorDetailActivity.this, ChatActivity.class);
            intent.putExtra(ChatActivity.TARGET_ID, "Doc"+tempDoctor_id + "" /*"10043"*/);//实际传入的ID
            intent.putExtra(ChatActivity.TARGET_APP_KEY, BaseConstants.jpush_doctor_appkey);//实际传入的KEY
            startActivityForResult(intent, REQUEST_CHAT);
            tv_book.setEnabled(true);
        } else {
            JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX+BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                @Override
                public void gotResult(int status, String desc) {
                    tv_book.setEnabled(true);
                    if (status == 0) {
                        Intent intent = new Intent(DoctorDetailActivity.this, ChatActivity.class);
                        intent.putExtra(ChatActivity.TARGET_ID, "Doc"+tempDoctor_id + "" /*"10043"*/);//实际传入的ID
                        intent.putExtra(ChatActivity.TARGET_APP_KEY, BaseConstants.jpush_doctor_appkey);//实际传入的KEY
                        startActivityForResult(intent, REQUEST_CHAT);
                        tv_book.setEnabled(true);
                    } else {
                        tv_book.setEnabled(true);
                    }
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CommonRequestCode.REQUEST_PAY:
                if (resultCode == CommonRequestCode.RESULT_BACK_TO_MAIN) {
                    setResult(CommonRequestCode.RESULT_BACK_TO_MAIN);
                    finish();
                }
                break;
            case REQUEST_CHAT:
                if (pay_number < 1) {
                    loadDoctorDetail();
                }
                break;
            default:
                break;
        }
    }
}
