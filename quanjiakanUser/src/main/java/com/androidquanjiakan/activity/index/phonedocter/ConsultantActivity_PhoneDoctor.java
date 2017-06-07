package com.androidquanjiakan.activity.index.phonedocter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.ServicePriceEntity;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.EditTextFilter;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.StringUtil;
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
import com.wxapi.entity.DoctorDetailParamEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ConsultantActivity_PhoneDoctor extends BaseActivity implements OnClickListener {

    private TextView tv_book, tv_name, tv_clinic_title, tv_hospital, tv_good_at, tv_good_bat, tv_commant, tv_content, tv_title;
    private TextView tv_rank_name;
    private TextView tv_call;
    private ImageView image;
    private Context mContext;
    private ImageButton ibtn_back;
    private RatingBar rBar;
    private LinearLayout llt_call;

    public final int REQUEST_PHONE = 1000;

    public static final int WX_PAY_INIT = 0;
    public static final int WX_PAY_OK = 1;
    public static final int WX_PAY_COMM = -1;
    public static final int WX_PAY_CANCEL = -2;
    public static final int WX_PAY_OTHER = -3;

    public static final int PAY_TYPE_ALI = 1;
    public static final int PAY_TYPE_WECHAT = 2;
    public static final int PAY_TYPE_WALLET = 0;
    public static final int PAY_TYPE_COUPON = 3;

    public static final String PARAMS_CLASS = "params_classify";
    public static int wxPayStatus = 0;
    public static boolean wxPayJumpFlag = false;

    private int position = -1;
    private int pay_type = -1;

    private String id;
    private String phoneNumber;
    private String tempDoctor_id = "10000";
    private String name;
    private String phone;
    private String emergent_name;
    private String emergent_phone;
    private String wxOrderID;
    private JsonObject phoneNumberJson;
    private JsonObject price;

    private Dialog typeDialog;
    private Dialog payDialog;
    private JsonObject object;
    private JsonObject paidinfo;
    private Dialog remindDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = ConsultantActivity_PhoneDoctor.this;
        setContentView(R.layout.layout_doctor_detail_phone_doctor);
        id = getIntent().getStringExtra(PARAMS_CLASS);
        if (id == null) {
            BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "传入参数异常!");
            finish();
            return;
        }
        initTitleBar();
        initView();
        loadPhoneDoctorDetail();
        loadPrice();
    }

    public void initTitleBar() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("儿童医生");
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
        tv_good_bat = (TextView) findViewById(R.id.tv_good_bat);
        tv_content = (TextView) findViewById(R.id.tv_content);
        llt_call = (LinearLayout) findViewById(R.id.llt_call);
        tv_content.setText(/*getResources().getString(R.string.phone_docter_desc)*/
                /*"日常医学咨询：儿童饮食、养生、运动、疾病防治、康复建议等。\n" +
                "诊前医学咨询：儿童病情分析，就医指导。\n" +
                "日常意外急救指导：儿童烧烫伤、气管异物、跌伤、触电、交通事故、动物咬伤、溺水等。\n" +
                "重大意外白金十分钟医疗急救服务：儿科医疗领域具有5年以上临床经验的专家通过电话第一时间指导现场医疗急救。\n\n"*/
                "夜里宝宝突然高烧、不明原因突然腹痛，初为人父人母的你束手无策？" +
                        "别急！儿童电话医生帮助你！专业儿科医生7×24小时在线，" +
                        "针对儿童日常病症、意外急救，提供电话诊疗指导，" +
                        "让您从容应对宝宝的各种突发情况，不再惊慌！"
        );
        image = (ImageView) findViewById(R.id.image);
        tv_rank_name = (TextView) findViewById(R.id.tv_rank_name);
        tv_call = (TextView) findViewById(R.id.tv_call);
        tv_call.setOnClickListener(this);
        rBar = (RatingBar) findViewById(R.id.rbar);
        tv_commant = (TextView) findViewById(R.id.tv_commant);
        tv_commant.setVisibility(View.GONE);
    }


    protected void loadPhoneDoctorDetail() {
        HashMap<String, String> params2 = new HashMap<>();
        params2.put("id", tempDoctor_id);
        MyHandler.putTask(this, new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                if (val != null && val.length() > 0) {
                    phoneNumberJson = new GsonParseUtil(val).getJsonObject();
                    rBar.setRating(phoneNumberJson.get("recommend_rate").getAsFloat());
//                    tv_content.setText(getResources().getString(R.string.phone_docter_desc));
//                    ImageLoader.getInstance().displayImage(phoneNumberJson.get("icon").getAsString(),image, BaseApplication.getInstances().getNormalImageOptions(0, R.drawable.touxiang_empty));
                    if (phoneNumberJson.get("paid").getAsInt() > 0 || BaseApplication.getInstances().getKeyNumberValue("bind_flag") == 1) {
                        tv_call.setVisibility(View.VISIBLE);
                        llt_call.setVisibility(View.VISIBLE);
                        tv_book.setVisibility(View.GONE);
                    } else {
                        tv_call.setVisibility(View.GONE);
                        llt_call.setVisibility(View.GONE);
                        tv_book.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, HttpUrls.getDoctorDetail(), params2, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(mContext)));

    }

    private ServicePriceEntity entity;

    public void loadPrice() {
        HashMap<String, String> params = new HashMap<>();
        MyHandler.putTask(this, new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                if (val != null && val.length() > 0) {
                    price = new GsonParseUtil(val).getJsonObject();
                    JsonObject temp = new GsonParseUtil(price.get("phonedoctor").getAsJsonObject().get("year").getAsString()).getJsonObject();
                    tv_good_at.setText("费用:  " +
                            StringUtil.getTwoBitDecimalString(temp.get("total_price").getAsFloat())
                            + "元/年");
                }
            }
        }, HttpUrls.getDoctorPrice(), params, Task.TYPE_POST_DATA_PARAMS, null));
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
        if (wxPayJumpFlag) {//从微信支付返回
            LogUtil.e("从微信支付返回!");
            wxPayJumpFlag = false;
            if (wxPayStatus == WX_PAY_OK) {
                vertifyWechatPayment(wxOrderID);
            } else {
                BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "微信支付失败!");
            }
            wxPayStatus = WX_PAY_INIT;
        }
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        if (id == R.id.tv_book) {
//            if(phoneNumberJson.get("paid").getAsInt()>0){
//                jumpToChat_Direct();
////                jumpToChat();
//                BaseApplication.getInstances().toast("已支付过!");
//            }else{
//                showTypeDialog();//测试支付宝流程通过--微信测试通过
//            }

            showTypeDialog();//测试支付宝流程通过--微信测试通过
        } else if (id == R.id.ibtn_back) {
            finish();
        } else if (id == R.id.tv_call) {
            jumpToChat_Direct();
//                jumpToChat();
//            BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "已支付过!");
        }
    }

    public void showTypeDialog() {
        name = null;
        phone = null;
        emergent_name = null;
        emergent_phone = null;

        typeDialog = QuanjiakanDialog.getInstance().getDialog(ConsultantActivity_PhoneDoctor.this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_doctor_type_selecter_phone_doctor, null);
        RelativeLayout rl_exit = (RelativeLayout) view.findViewById(R.id.rl_exit);
        rl_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeDialog != null) {
                    typeDialog.dismiss();
                }
            }
        });
        final EditText name_value = (EditText) view.findViewById(R.id.name_value);
        final EditText phone_value = (EditText) view.findViewById(R.id.phone_value);
        final EditText identify_value = (EditText) view.findViewById(R.id.identify_value);
        final EditText emergent_person_value = (EditText) view.findViewById(R.id.emergent_person_value);
        final EditText emergent_phone_value = (EditText) view.findViewById(R.id.emergent_phone_value);

        final TextView confirm = (TextView) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name_value.getText().toString() != null && name_value.getText().toString().length() < 1) {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "请填写姓名!");
                    return;
                }

                if (!CheckUtil.isAllChineseChar(name_value.getText().toString())) {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "请填写正确的姓名!");
                    return;
                }
                if (phone_value.getText().toString() != null && phone_value.getText().toString().length() < 1) {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "订单电话请填写手机号码!");
                    return;
                }

                if (identify_value.getText().toString() != null && identify_value.getText().toString().length() < 1) {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "请填写人员身份证号!");
                    return;
                }

                //TODO 是否需要校验身份证号码的正确性，还是说仅判断位数符合要求就行
                if (identify_value.getText().toString().length() != 18) {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "请填写正确身份证号!");
                    return;
                }

                if (phone_value.getText().toString() != null && phone_value.getText().toString().length() > 0 && !CheckUtil.isPhoneNumber(phone_value.getText().toString())) {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "订单电话请填写正确的手机号码!");
                    return;
                }

                if ((emergent_person_value.length() > 0 && emergent_phone_value.length() < 1) || (emergent_phone_value.length() > 0 && emergent_person_value.length() < 1)) {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "若填写紧急联系信息，请都填写完整!");
                    return;
                }

                if (emergent_person_value.length() > 0 && !CheckUtil.isAllChineseChar(emergent_person_value.getText().toString())) {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "请填写正确的紧急联系人姓名!");
                    return;
                }

                if (emergent_phone_value.length() > 0 && !CheckUtil.isPhoneNumber(emergent_phone_value.getText().toString())) {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "紧急联系电话请填写正确的手机号码!");
                    return;
                }
                name = name_value.getText().toString();
                phone = phone_value.getText().toString();
                if (emergent_person_value.length() > 0) {
                    emergent_phone = emergent_phone_value.getText().toString();
                    emergent_name = emergent_person_value.getText().toString();
                } else {
                    emergent_phone = "";
                    emergent_name = "";
                }

                showPayDialog();
            }
        });
        typeDialog.setContentView(view);
    }


    public void showPayDialog() {
        pay_type = PAY_TYPE_WALLET;//钱包
        payDialog = QuanjiakanDialog.getInstance().getDialog(ConsultantActivity_PhoneDoctor.this);
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
        TextView content = (TextView) view.findViewById(R.id.content);
        /**
         * 根据选择的位置显示对应的价格数据
         */
        //------原
        JsonObject temp = new GsonParseUtil(price.get("phonedoctor").getAsJsonObject().get("year").getAsString()).getJsonObject();
//        tv_good_at.setText("服务费用:"+temp.get("total_price").getAsString()+"/年");
        SpannableString string = new SpannableString(temp.get("total_price").getAsString() + "/年");
        string.setSpan(new ForegroundColorSpan(Color.RED), 0, temp.get("total_price").getAsString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setText(string);

        final ImageView item_choice2 = (ImageView) view.findViewById(R.id.item_choice2);
        final ImageView item_choice1 = (ImageView) view.findViewById(R.id.item_choice1);
        final ImageView item_choice3 = (ImageView) view.findViewById(R.id.item_choice3);
        final ImageView item_choice4 = (ImageView) view.findViewById(R.id.item_choice4);
        RelativeLayout pay_wallet_line = (RelativeLayout) view.findViewById(R.id.pay_wallet_line);
        RelativeLayout pay_ali_line = (RelativeLayout) view.findViewById(R.id.pay_ali_line);
        RelativeLayout pay_wechat_line = (RelativeLayout) view.findViewById(R.id.pay_wechat_line);
        /*  优惠券*/
        RelativeLayout coupon_line = (RelativeLayout) view.findViewById(R.id.coupon_line);
        coupon_line.setVisibility(View.VISIBLE);
        pay_ali_line.setOnClickListener(new OnClickListener() {//支付宝
            @Override
            public void onClick(View view) {
                item_choice2.setBackgroundResource(R.drawable.doctor_pay_list_light);
                item_choice1.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice3.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice4.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                pay_type = PAY_TYPE_ALI;
            }
        });
        pay_wechat_line.setOnClickListener(new OnClickListener() {//微信支付
            @Override
            public void onClick(View view) {
                item_choice2.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice1.setBackgroundResource(R.drawable.doctor_pay_list_light);
                item_choice3.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice4.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                pay_type = PAY_TYPE_WECHAT;
            }
        });
        pay_wallet_line.setOnClickListener(new OnClickListener() {//微信支付
            @Override
            public void onClick(View view) {
                item_choice2.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice1.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice3.setBackgroundResource(R.drawable.doctor_pay_list_light);
                item_choice4.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                pay_type = PAY_TYPE_WALLET;
            }
        });
        /*  优惠券*/
        coupon_line.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                item_choice2.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice1.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice3.setBackgroundResource(R.drawable.doctor_pay_list_nor);
                item_choice4.setBackgroundResource(R.drawable.doctor_pay_list_light);
                pay_type = PAY_TYPE_COUPON;

            }
        });
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (payDialog != null) {
                    payDialog.dismiss();
                }
//                startPayProgress();
                if (pay_type == PAY_TYPE_WALLET) {
                    getRemainSum();//钱包
                } else if (pay_type == PAY_TYPE_COUPON) {
                    /*  优惠券*/
                    showCouponDialog();

                } else {
                    startPayProgress();//微信，支付宝
                }
            }
        });
        payDialog.setContentView(view);
    }

    /*
    * 输入优惠券
    * */
    private void showCouponDialog() {
        remindDialog = new Dialog(this, R.style.ShareDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_child_plan_edit, null);
        view.findViewById(R.id.tv_tip).setVisibility(View.GONE);
        final EditText tv_content = (EditText) view.findViewById(R.id.tv_content);
        tv_content.setGravity(Gravity.LEFT);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("请输入优惠券码");
        TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_content.getText().toString().length() < 1) {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "请输入相关数据!");
                    return;
                }

                if (tv_content.getText().toString().length() > 8) {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "超过限定字符长度!");
                    return;
                }

                if (EditTextFilter.containsUnChar(tv_content.getText().toString())||EditTextFilter.containsEmoji(tv_content.getText().toString())) {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "含有非法字符!");
                    return;
                }

                if (remindDialog != null) {
                    remindDialog.dismiss();
                }

                testCoupon(tv_content.getText().toString());

            }
        });

        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remindDialog != null) {
                    remindDialog.dismiss();
                }
            }
        });

        WindowManager.LayoutParams lp = remindDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        remindDialog.setContentView(view, lp);
        remindDialog.show();
    }

    private void testCoupon(String coupon) {

        try {
            JSONObject object = new JSONObject();
            object.put("memberId",BaseApplication.getInstances().getUser_id());
            object.put("promocode",coupon);


            MyHandler.putTask(ConsultantActivity_PhoneDoctor.this,new Task(new HttpResponseInterface() {
                @Override
                public void handMsg(String val) {
                    LogUtil.e("-----------"+val);
                    /* {"code":"200","message":"返回成功"} */
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (jsonObject.has("code")) {
                        if (jsonObject.get("code").getAsString().equals("200")) {

                            jumpToChat();
                        }else if (jsonObject.get("code").getAsString().equals("500")){
                            BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this,jsonObject.get("message").getAsString());
                        }else {
                            BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this,jsonObject.get("message").getAsString());
                        }
                    }


                }
            },HttpUrls.checkCoupon() + "&data=" + object.toString(),null,Task.TYPE_GET_STRING_NOCACHE,null));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void getRemainSum() {

        HashMap<String, String> params = new HashMap<>();
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                try {
                    if (val != null && val.length() > 0 && !"null".equals(val.toLowerCase())) {
                        JSONObject jsonObject = new JSONObject(val);
                        showMyWalletDialog(jsonObject.getString("member_wallet"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, HttpUrls.getWalletNumber() + "&member_id=" + BaseApplication.getInstances().getUser_id(), params, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    /**
     * 显示自己的钱包的对话框
     */
    Dialog myWalletDialog;

    public void showMyWalletDialog(final String payNumber) {
        myWalletDialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_doctor_mywallet_pay, null);
        RelativeLayout rl_exit = (RelativeLayout) view.findViewById(R.id.rl_exit);
        rl_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myWalletDialog != null) {
                    myWalletDialog.dismiss();
                }
                BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "已取消支付!");
            }
        });
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("支付明细");
        //real_pay_value
        TextView remaining_sum_value = (TextView) view.findViewById(R.id.remaining_sum_value);
        remaining_sum_value.setText("" + payNumber);

        TextView real_pay_value = (TextView) view.findViewById(R.id.real_pay_value);

        JsonObject temp = new GsonParseUtil(price.get("phonedoctor").getAsJsonObject().get("year").getAsString()).getJsonObject();
//        tv_good_at.setText("服务费用:"+temp.get("total_price").getAsString()+"/年");
        final String memory = temp.get("total_price").getAsString();
        //------------------------
        real_pay_value.setText(memory + "");

        TextView hint = (TextView) view.findViewById(R.id.hint);
        if (Double.parseDouble(memory + "") <= Double.parseDouble(payNumber)) {
            hint.setVisibility(View.GONE);
        } else {
            hint.setVisibility(View.VISIBLE);
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
                if (Double.parseDouble(memory + "") <= Double.parseDouble(payNumber)) {
                    startPayProgress();
                } else {
                    BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "余额不足，请换微信或支付宝支付!");
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

    public void startPayProgress() {

        /**
         * 跳转到打电话
         */

        HashMap<String, String> params = new HashMap<>();
        JsonObject temp = new GsonParseUtil(price.get("phonedoctor").getAsJsonObject().get("year").getAsString()).getJsonObject();
//        tv_good_at.setText("服务费用:"+temp.get("total_price").getAsString()+"/年");
        final double total_fee = temp.get("total_price").getAsDouble();
        final double total_real = temp.get("real_price").getAsDouble();
        final double total_platform = temp.get("platform_price").getAsDouble();
//        params.put("doctor_id", tempDoctor_id);
//        params.put("user_id", BaseApplication.getInstances().getUser_id());
//        params.put("service_type", "phonedoctor_year");//
//        params.put("total_price", total_fee + "");//测试
//        params.put("platform_price",total_platform + "");//
//        params.put("real_price",total_real + "");//
//
//        params.put("payment_channel", pay_type/*==PAY_TYPE_WECHAT? (PAY_TYPE_WECHAT+"") : (PAY_TYPE_ALI+"")*/+"");
//
//        params.put("ordername", name);//
//        params.put("ordermobile", phone);//

//        &data={"doctorId":10000,"userId":10041,"orderName":"123","orderMobile":"123","relationName":"123","relationMobile":"123","totalPrice":0.1,"realPrice":0.2,"platformPrice":0.3,"paymentChannel":2}
        String jsonString = "{\"doctorId\":" + tempDoctor_id + ",\"userId\":" + QuanjiakanSetting.getInstance().getUserId() + ",\"orderName\":" + "\"" + name + "\"" + ",\"orderMobile\":" + "\"" + phone + "\"" + ",\"relationName\":" + "\"" + emergent_name + "\"" + ",\"relationMobile\":" + "\"" + emergent_phone + "\"" +
                ",\"totalPrice\":" + total_fee + ",\"realPrice\":" + total_real + ",\"platformPrice\":" + total_platform + ",\"paymentChannel\":" + pay_type + "}";
//        if(emergent_name!=null && emergent_name.length()>0){
//            params.put("relationname", emergent_name);//
//            params.put("relationmobile", emergent_phone);//
//        }
        LogUtil.e("订单请求参数++++++" + jsonString);
        MyHandler.putTask(this, new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                if (val != null && val.length() > 0) {
                    HttpResponseResult result = new HttpResponseResult(val);
                    if (result.isResultOk()) {
                        JsonObject object1 = result.getObject();
                        LogUtil.e("生成电话订单-----------" + object1.toString());
                        //{"code":"200","message":"{\"orderid\":\"QJKFAMOUS20161111021513732556\",\"paidinfo\":{\"partner\":\"2088021868486397\",\"seller_id\":\"13510237554@163.com\",\"out_trade_no\":\"QJKFAMOUS20161111021513732556\",\"subject\":\"名医咨询\",\"body\":\"支付名医咨询费用\",\"total_fee\":\"0.1\",\"notify_url\":\"http://pay.quanjiakan.com/notify_url.jsp\",\"service\":\"mobile.securitypay.pay\",\"payment_type\":\"1\",\"_input_charset\":\"utf-8\",\"it_b_pay\":\"30m\",\"return_url\":\"m.alipay.com\",\"sign\":\"S6vDlVbNKXm57Cwwm0KBk8hkFvkISnbuwQq7RDD9WQvTHwFGFVb5E7nyxVBQ0vLlktbpjUmvdDtU5eDfZyEOzHENN4zkK0YebRpZhDOMi7frgNRXZ7vOeILmn4tLij+QpshoORAuRu0CLqiyDOWR6+rjkvRE2WvO4fz3gEYefEQ=\",\"code\":\"200\"}}"}
                        String message = object1.get("message").getAsString();
                        object = new GsonParseUtil(message).getJsonObject();

                        if (pay_type != PAY_TYPE_WALLET) {
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
                            new WeixinPayHandler(ConsultantActivity_PhoneDoctor.this, WXPayEntryActivity.FROM_DOCTOR_PHONE_PAY, entity).pay(paidinfo);
                        } else if (pay_type == PAY_TYPE_WALLET) {
//                            vertifyWalletPayment(paidinfo.get("out_trade_no").getAsString());
                            vertifyWalletPayment(object.get("orderid").getAsString());
//                            BaseApplication.getInstances().toast("进入钱包支付,后续步骤需要确定后完善!");
                        }
                    } else {
                        Toast.makeText(ConsultantActivity_PhoneDoctor.this, "服务器生成订单失败!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, HttpUrls.payForDoctorService() + "&data=" + jsonString, params, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(ConsultantActivity_PhoneDoctor.this)));

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
                    Toast.makeText(ConsultantActivity_PhoneDoctor.this, "支付失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }).pay(paidinfo);
    }

/*    protected void vertifyAliPayment(final String result) {
        HashMap<String, String> params = new HashMap<>();

        MyHandler.putTask(this,new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                HttpResponseResult result = new HttpResponseResult(val);
                if (result.getCode().equals("200")) {
                    //支付成功
                    LogUtil.e("[走自己的接口进行支付校验---结果成功]");
                    Toast.makeText(ConsultantActivity_PhoneDoctor.this, "支付校验成功!", Toast.LENGTH_SHORT).show();
                    jumpToChat();
                }else{
                    LogUtil.e("[走自己的接口进行支付校验---结果失败]");
                    Toast.makeText(ConsultantActivity_PhoneDoctor.this, "支付校验失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.updateDocterServicePayment(), params, Task.TYPE_POST_DATA_PARAMS, null));
    }*/

    protected void vertifyWechatPayment(final String orderid) {
        HashMap<String, String> params = new HashMap<>();
//        params.put("orderid", orderid);//订单id
//        params.put("paidinfo", "");//支付宝，返回的result
//        params.put("payment_channel", "2");//1、支付宝；2微信
        String jsonString = "{\"userId\":" + QuanjiakanSetting.getInstance().getUserId() + ",\"orderid\":" + "\"" + orderid + "\"" + "}";
        MyHandler.putTask(this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                HttpResponseResult result = new HttpResponseResult(val);
                if (result.getCode().equals("200")) {
                    //支付成功
                    LogUtil.e("[走自己的接口进行支付校验---结果成功]");
                    Toast.makeText(ConsultantActivity_PhoneDoctor.this, "支付校验成功!", Toast.LENGTH_SHORT).show();
                    jumpToChat();
                } else {
                    LogUtil.e("[走自己的接口进行支付校验---结果失败]");
                    Toast.makeText(ConsultantActivity_PhoneDoctor.this, "支付校验失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.updateDocterServicePayment() + "&data=" + jsonString, params, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    protected void vertifyWalletPayment(final String orderid) {
        HashMap<String, String> params = new HashMap<>();
//        params.put("orderid", orderid);//订单id
//        params.put("paidinfo", "");//支付宝，返回的result
//        params.put("payment_channel", "0");//1、支付宝；2微信

        String jsonString = "{\"userId\":" + QuanjiakanSetting.getInstance().getUserId() + ",\"orderid\":" + "\"" + orderid + "\"" + "}";

        MyHandler.putTask(this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                HttpResponseResult result = new HttpResponseResult(val);
                if (result.getCode().equals("200")) {
                    //支付成功
                    LogUtil.e("[走自己的接口进行支付校验---结果成功]");
                    Toast.makeText(ConsultantActivity_PhoneDoctor.this, "支付校验成功!", Toast.LENGTH_SHORT).show();
                    jumpToChat();
                } else {
                    LogUtil.e("[走自己的接口进行支付校验---结果失败]");
                    Toast.makeText(ConsultantActivity_PhoneDoctor.this, "支付校验失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.updateDocterServicePayment() + "&data=" + jsonString, params, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    protected void vertifyAliPayment(final JsonObject payInfo) {
        HashMap<String, String> params = new HashMap<>();
        params.put("orderid", payInfo.get("orderid").getAsString());//订单id
        params.put("paidinfo", payInfo.toString());//支付宝，返回的result
        params.put("payment_channel", "1");//1、支付宝；2微信

        String jsonString = "{\"userId\":" + QuanjiakanSetting.getInstance().getUserId() + ",\"orderid\":" + "\"" + payInfo.get("orderid").getAsString() + "\"" + "}";
        MyHandler.putTask(this, new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                HttpResponseResult result = new HttpResponseResult(val);
                if (result.getCode().equals("200")) {
                    //支付成功
                    LogUtil.e("[走自己的接口进行支付校验---结果成功]");
                    Toast.makeText(ConsultantActivity_PhoneDoctor.this, "支付校验成功!", Toast.LENGTH_SHORT).show();
                    jumpToChat();
                } else {
                    LogUtil.e("[走自己的接口进行支付校验---结果失败]");
                    Toast.makeText(ConsultantActivity_PhoneDoctor.this, "支付校验失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.updateDocterServicePayment() + "&data=" + jsonString, params, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    public void jumpToChat() {//直接进入聊天页面

        final Dialog dialog = QuanjiakanDialog.getInstance().getDialog(ConsultantActivity_PhoneDoctor.this);
        View view = LayoutInflater.from(ConsultantActivity_PhoneDoctor.this).inflate(R.layout.dialog_common_confirm, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("提示");
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setText("付款成功，请留意短信通知！该服务在激活72小时后可以使用。");

        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        btn_cancel.setVisibility(View.INVISIBLE);

        TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
        btn_confirm.setVisibility(View.VISIBLE);
        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(ConsultantActivity_PhoneDoctor.this, ConsultantActivity_PhoneCall.class);
                startActivityForResult(intent, REQUEST_PHONE);
            }
        });

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(ConsultantActivity_PhoneDoctor.this, 300);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setContentView(view, lp);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void jumpToChat_Direct() {//直接进入聊天页面
        //直接拨打电话
//        Intent intent = new Intent(ConsultantActivity_PhoneDoctor.this,ConsultantActivity_PhoneCall.class);
//        startActivityForResult(intent,REQUEST_PHONE);
        loadPhoneDoctorPhoneNumber();
    }

    private JsonObject phoneNumberJ;

    protected void loadPhoneDoctorPhoneNumber() {
        /**
         * 获取历史订单信息----保证用户能看到可以打通电话的号码
         */
        if (tv_book.getTag() != null && tv_book.getTag().toString().length() > 0) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tv_book.getTag().toString()));
            startActivity(intent);
        } else {
            HashMap<String, String> params = new HashMap<>();
            MyHandler.putTask(this, new Task(new HttpResponseInterface() {

                @Override
                public void handMsg(String val) {
                    // TODO Auto-generated method stub
                    if (val != null && val.length() > 0) {
                        phoneNumberJ = new GsonParseUtil(val).getJsonObject();
                        if (phoneNumberJ.has("message")) {
                            tv_book.setTag(phoneNumberJ.get("message").getAsString());
                        }
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tv_book.getTag().toString()));
                        startActivity(intent);
                    } else {
                        BaseApplication.getInstances().toast(ConsultantActivity_PhoneDoctor.this, "获取电话失败，请重试!");
                    }
                }
            }, HttpUrls.getPhoneDoctorDetail(), params, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(this)));
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
            case REQUEST_PHONE:
                if (phoneNumberJson.get("paid").getAsInt() < 1) {
                    loadPhoneDoctorDetail();
                }
                break;
            default:
                break;
        }
    }

}
