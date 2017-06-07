package com.androidquanjiakan.activity.setting.ebean;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.video.VideoEntryActivity;
import com.androidquanjiakan.activity.video.VideoLivePlayActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.encode.RSACoder;
import com.androidquanjiakan.encode.RSAHttp;
import com.androidquanjiakan.entity.EBeanOption;
import com.androidquanjiakan.entity.VideoLiveListEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.StringUtil;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakan.main.wxapi.WXPayEntryActivity;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.AlipayHandler;
import com.quanjiakanuser.util.GsonParseUtil;
import com.quanjiakanuser.util.WeixinPayHandler;
import com.quanjiakanuser.view_util.OrderClickSpan;
import com.umeng.analytics.MobclickAgent;
import com.wxapi.entity.EBeanParamEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * 充值页
 */
public class EBeanChargeActivity extends BaseActivity implements OnClickListener {

    private int entry_type = BaseConstants.ENTRY_NORMAL;

    private VideoLiveListEntity entity;
    private String docID;
    private String hostName;
    private String hostImg;



    private ImageButton ibtn_back;
    private TextView tv_title;
    private TextView menu_text;

    /**
     *
     */
    private LinearLayout ll_signup_clause_ck;
    private View signup_clause_ck;
    private TextView signup_clause_text;

    private Button btn_submit;
    private boolean read_flag = false;

    /**
     *
     */
    private LinearLayout type1;
    private TextView type1_name;
    private TextView type1_value;

    private LinearLayout type2;
    private TextView type2_name;
    private TextView type2_value;

    private LinearLayout type3;
    private TextView type3_name;
    private TextView type3_value;

    private LinearLayout type4;
    private TextView type4_name;
    private TextView type4_value;

    private LinearLayout type5;
    private TextView type5_name;
    private TextView type5_value;

    private LinearLayout type6;
    private TextView type6_name;
    private TextView type6_value;

    private TextView name;
    private TextView value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ebean_charge);
        entry_type = getIntent().getIntExtra(BaseConstants.PARAMS_ENTRY,BaseConstants.ENTRY_NORMAL);
        if(entry_type==BaseConstants.ENTRY_VIDEO_LIVE){
            //TODO 保存需要返回的直播页面的信息
            entity = (VideoLiveListEntity) getIntent().getSerializableExtra(BaseConstants.PARAM_ENTITY);
            hostName = getIntent().getStringExtra(BaseConstants.PARAM_NAME);
            hostImg = getIntent().getStringExtra(BaseConstants.PARAM_HEADIMG);
            if(entity==null || hostImg==null || hostName==null){
                BaseApplication.getInstances().toastLong(EBeanChargeActivity.this,"传入参数异常!");
                finish();
                return;
            }
        }

        initTitleBar();
        initView();

        initType();

        getChargeOptions();
        getMyEbeanInfo();
    }

    private String publicKey;
    private String privateKey;
    private JsonObject jsonObject;

    private void getPublicKey() {
        try {
            publicKey = null;
            HashMap<String, String> map = new HashMap<>();
            map.put("memberId", BaseApplication.getInstances().getUser_id());
            String params = URLEncoder.encode("{\"memberId\":" + BaseApplication.getInstances().getUser_id() + "}", "utf-8");
            MyHandler.putTask(EBeanChargeActivity.this, new Task(new HttpResponseInterface() {
                @Override
                public void handMsg(String val) {
                    //{"code":"200","message":"返回成功","object":{"PublicKey":
                    // "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCk3+3Nm8ByqnGduNA97/fiALQ+y2tlxHQ3cVvz\r\ng9JLU46tmNrGTTQuklrDFUXCOV7gMFlqqiwBGqPRUL8BwHaCyD2ZNzraL66vG2lbyctsAuapsLIP\r\nmAF1L7orvnX0guijDS3GJFhs0t9J7XuKaKv+N/i0YwM6MVkdZ/arHaT5WQIDAQAB\r\n"}}
                    if (val != null && val.length() > 0) {
                        jsonObject = new GsonParseUtil(val).getJsonObject();
                        if (jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull) &&
                                "200".equals(jsonObject.get("code").getAsString())) {
                            if (jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull) &&
                                    jsonObject.get("object").getAsJsonObject().has("PublicKey") &&
                                    !(jsonObject.get("object").getAsJsonObject().get("PublicKey") instanceof JsonNull)) {
                                publicKey = jsonObject.get("object").getAsJsonObject().get("PublicKey").getAsString();
                                byte[] publieKeyBytes = publicKey.getBytes();
                                showPayDialog();
                            } else {
                                BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取加密公钥失败!");
                            }
                        } else {
                            BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取加密公钥失败!");
                        }
                    } else {
                        BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取加密公钥失败!");
                    }

                }
            }, HttpUrls.getPublicKey_get() + "&data=" + params, map, Task.TYPE_GET_STRING_NOCACHE, null));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Dialog payDialog;
    private int pay_type = -1;

    public void showPayDialog() {
        pay_type = PAY_TYPE_WALLET;//支付宝
        payDialog = QuanjiakanDialog.getInstance().getDialog(EBeanChargeActivity.this);
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
                if (pay_type == PAY_TYPE_WALLET) {
                    getOrder(rechargeId, PAY_TYPE_WALLET, jsonObject);
                } else if (pay_type == PAY_TYPE_ALI) {
                    getOrder(rechargeId, PAY_TYPE_ALI, jsonObject);
                } else {
                    getOrder(rechargeId, PAY_TYPE_WECHAT, jsonObject);//生成订单
                }
            }
        });
        payDialog.setContentView(view);
    }

    public void getRemainSum() {
        HashMap<String, String> params = new HashMap<>();
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("qianbao----------"+HttpUrls.getWalletNumber() + "&member_id=" + BaseApplication.getInstances().getUser_id());
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

    private Dialog myWalletDialog;
    private double servicePrice = 0;

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
                BaseApplication.getInstances().toast(EBeanChargeActivity.this,"已取消支付!");
            }
        });
        /**
         *
         */
        TextView hint = (TextView) view.findViewById(R.id.hint);
        hint.setText("账户余额不足，请更换微信或支付宝支付!");
        if (Double.parseDouble(servicePrice + "") <= Double.parseDouble(payNumber)) {
            hint.setVisibility(View.GONE);
        } else {
            hint.setVisibility(View.VISIBLE);
        }
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("支付明细");
        //real_pay_value
        TextView remaining_sum_value = (TextView) view.findViewById(R.id.remaining_sum_value);
        remaining_sum_value.setText("" + payNumber);

        TextView real_pay_value = (TextView) view.findViewById(R.id.real_pay_value);

        //------------------------
        real_pay_value.setText(servicePrice + "");

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
                if (Double.parseDouble(servicePrice + "") <= Double.parseDouble(payNumber)) {
                    vertifyWalletPayment(globalOrderId);//TODO 开始钱包支付
                } else {
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

    /**
     * TODO 支付宝支付
     */
    protected void aliPay(final String orderid, final double total_fee, JsonObject paidinfo, final String pay_channel) {
        //支付总额
        new AlipayHandler(EBeanChargeActivity.this, new AlipayHandler.AlipayCallback() {

            @Override
            public void paidComplete(int type, String msg, String result) {
                // TODO Auto-generated method stub
                if (type == AlipayHandler.PAID_SUCCESS) {
                    //支付成功，并将支付提交提交到后台
                    JsonObject object = new GsonParseUtil(result).getJsonObject();
                    vertifyAliPayment(object.get("orderid").getAsString(), object.get("result").getAsString(), total_fee, pay_channel);
                } else if (type == AlipayHandler.PAID_FAILED) {
                    //支付失败
                    BaseApplication.getInstances().toastLong(EBeanChargeActivity.this,"支付宝支付失败!");
                }
            }
        }).pay(paidinfo);
    }

    /**
     * 支付校验
     * TODO 修改
     */
    protected void vertifyAliPayment(final String orderid, final String info, final double total, final String pay_channel) {
        HashMap<String, String> params = new HashMap<>();
        //ciphertext 需要进行加密
        try {
            JSONObject json = new JSONObject();
            json.put("orderid", orderid);
            byte[] encodeData = RSACoder.encryptByPublicKey(json.toString().getBytes(), publicKey);
            String subText = Base64.encodeToString(encodeData, Base64.DEFAULT);
//            String jsonString = "{\"ciphertext\":"+ RSAHttp.httpTransferCharacter(subText)+",\"memberId\":"+BaseApplication.getInstances().getUser_id()+"}";
            //
            JSONObject jsonData = new JSONObject();
            jsonData.put("ciphertext", RSAHttp.httpTransferCharacter(subText));
            jsonData.put("memberId", Long.parseLong(BaseApplication.getInstances().getUser_id()));
            MyHandler.putTask(this, new Task(new HttpResponseInterface() {

                @Override
                public void handMsg(String val) {
                    // TODO Auto-generated method stub
                    //{"code":"200","message":"返回成功","object":{"ebeans":"60"}}
                    if (val != null && val.length() > 0) {
                        JsonObject result = new GsonParseUtil(val).getJsonObject();
                        if (result != null && result.has("code") && !(result.get("code") instanceof JsonNull) && "200".equals(result.get("code").getAsString())
                                && result.has("object") && !(result.get("object") instanceof JsonNull)
                                ) {
                            BaseApplication.getInstances().toast(EBeanChargeActivity.this,"支付校验成功!");
                            /**
                             * TODO 刷新E豆数据
                             */
                            long number = Long.parseLong(value.getText().toString().replace("豆","").trim()) + Long.parseLong(result.get("object").getAsJsonObject().get("ebeans").getAsString());
                            value.setText(number + "豆");
                        } else {
                            BaseApplication.getInstances().toast(EBeanChargeActivity.this,"支付校验失败!");
                        }
                    } else {
                        BaseApplication.getInstances().toast(EBeanChargeActivity.this,"支付校验失败!");
                    }
                }
            }, HttpUrls.getEBeanOrderPublish() + "&data=" + jsonData.toString(), params, Task.TYPE_GET_STRING_NOCACHE, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验微信支付
     */
    public void vertifyWechatPayment(final String orderid) {
        try {
            HashMap<String, String> params = new HashMap<>();
            JSONObject json = new JSONObject();
            json.put("orderid", orderid);
            byte[] encodeData = RSACoder.encryptByPublicKey(json.toString().getBytes(), publicKey);
            String subText = Base64.encodeToString(encodeData, Base64.DEFAULT);
//            String jsonString = "{\"ciphertext\":"+ RSAHttp.httpTransferCharacter(subText)+",\"memberId\":"+BaseApplication.getInstances().getUser_id()+"}";
            //
            JSONObject jsonData = new JSONObject();
            jsonData.put("ciphertext", RSAHttp.httpTransferCharacter(subText));
            jsonData.put("memberId", Long.parseLong(BaseApplication.getInstances().getUser_id()));
            MyHandler.putTask(this, new Task(new HttpResponseInterface() {

                @Override
                public void handMsg(String val) {
                    // TODO Auto-generated method stub
                    if (val != null && val.length() > 0) {
                        JsonObject result = new GsonParseUtil(val).getJsonObject();
                        if (result != null && result.has("code") && !(result.get("code") instanceof JsonNull) && "200".equals(result.get("code").getAsString())
                                && result.has("object") && !(result.get("object") instanceof JsonNull)
                                ) {
                            BaseApplication.getInstances().toast(EBeanChargeActivity.this,"支付校验成功!");
                            /**
                             * TODO 刷新E豆数据
                             */
                            long number = Long.parseLong(value.getText().toString().replace("豆","").trim()) + Long.parseLong(result.get("object").getAsJsonObject().get("ebeans").getAsString());
                            value.setText(number + "豆");
                        } else {
                            BaseApplication.getInstances().toast(EBeanChargeActivity.this,"支付校验失败!");
                        }
                    } else {
                        BaseApplication.getInstances().toast(EBeanChargeActivity.this,"支付校验失败!");
                    }

                }
            }, HttpUrls.getEBeanOrderPublish() + "&data=" + jsonData.toString(), params, Task.TYPE_GET_STRING_NOCACHE, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void vertifyWalletPayment(final String orderid) {
        try {
            HashMap<String, String> params = new HashMap<>();
            JSONObject json = new JSONObject();
            json.put("orderid", orderid);
            byte[] encodeData = RSACoder.encryptByPublicKey(json.toString().getBytes(), publicKey);
            String subText = Base64.encodeToString(encodeData, Base64.DEFAULT);
//            String jsonString = "{\"ciphertext\":"+ RSAHttp.httpTransferCharacter(subText)+",\"memberId\":"+BaseApplication.getInstances().getUser_id()+"}";
            //
            JSONObject jsonData = new JSONObject();
            jsonData.put("ciphertext", RSAHttp.httpTransferCharacter(subText));
            jsonData.put("memberId", Long.parseLong(BaseApplication.getInstances().getUser_id()));
            MyHandler.putTask(this, new Task(new HttpResponseInterface() {

                @Override
                public void handMsg(String val) {
                    // TODO Auto-generated method stub
                    if (val != null && val.length() > 0) {
                        JsonObject result = new GsonParseUtil(val).getJsonObject();
                        if (result != null && result.has("code") && !(result.get("code") instanceof JsonNull) && "200".equals(result.get("code").getAsString())
                                && result.has("object") && !(result.get("object") instanceof JsonNull)
                                ) {
                            BaseApplication.getInstances().toast(EBeanChargeActivity.this,"支付校验成功!");
                            /**
                             * TODO 刷新E豆数据
                             */
                            long number = Long.parseLong(value.getText().toString().replace("豆","").trim()) + Long.parseLong(result.get("object").getAsJsonObject().get("ebeans").getAsString());
                            value.setText(number + "豆");
                        } else {
                            BaseApplication.getInstances().toast(EBeanChargeActivity.this,"支付校验失败!");
                        }
                    } else {
                        BaseApplication.getInstances().toast(EBeanChargeActivity.this,"支付校验失败!");
                    }

                }
            }, HttpUrls.getEBeanOrderPublish() + "&data=" + jsonData.toString(), params, Task.TYPE_GET_STRING_NOCACHE, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMyEbeanInfo() {
        HashMap<String, String> map = new HashMap<>();
        map.put("memberId", BaseApplication.getInstances().getUser_id());
        MyHandler.putTask(EBeanChargeActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                /*
                {
                  "code": "200",
                  "message": "返回成功",
                  "object": {
                    "currentGradeExperience": 2,
                    "ebeans": 0,
                    "ebeansConvertWallet": 0,
                    "experience": 2,
                    "grade": 1,
                    "money": 0,
                    "nextGradeExperience": 998,
                    "recharge": 0,
                    "totalGive": 0,
                    "totalRecipient": 0
                  }
                }
                 */
                if (val != null && val.length() > 0) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull) &&
                            "200".equals(jsonObject.get("code").getAsString())) {
                        if (jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull)) {
                            value.setText(jsonObject.get("object").getAsJsonObject().get("ebeans").getAsLong() + "豆");
                            LogUtil.e("设置现有EBean");
                        } else {
                            BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取个人信息失败!");
                        }
                    } else {
                        BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取个人信息失败!");
                    }
                } else {
                    BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取个人信息失败!");
                }

            }
        }, HttpUrls.getEBeanInfo_get(), map, Task.TYPE_GET_STRING_NOCACHE, null));
    }


    /**
     * 需要发布一个订单后再调试数据
     */
    private final int ROWS = 20;
    private int pageIndex;

    public void getHistoryOrder(final int page) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("memberId", BaseApplication.getInstances().getUser_id());
            JSONObject json = new JSONObject();
            json.put("memberId", Long.parseLong(BaseApplication.getInstances().getUser_id()));
            json.put("page", page);
            json.put("memberId", ROWS);
            String data = "&data=" + URLEncoder.encode(json.toString(), "utf-8");
            MyHandler.putTask(EBeanChargeActivity.this, new Task(new HttpResponseInterface() {
                @Override
                public void handMsg(String val) {
                /*
                 */
                    if (val != null && val.length() > 0) {
                        JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                        if (jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull) &&
                                "200".equals(jsonObject.get("code").getAsString())) {
                            if (jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull)) {
//                                BaseApplication.getInstances().toast(
//                                        "\n当前经验："+jsonObject.get("code").getAsJsonObject().get("currentGradeExperience").getAsLong()+
//                                                "\nE豆数："+jsonObject.get("code").getAsJsonObject().get("ebeans").getAsLong()+
//                                                "\n转换成钱包："+jsonObject.get("code").getAsJsonObject().get("ebeansConvertWallet").getAsDouble()+
//                                                "\n经验："+jsonObject.get("code").getAsJsonObject().get("experience").getAsLong()+
//                                                "\n等级："+jsonObject.get("code").getAsJsonObject().get("grade").getAsLong()+
//                                                "\n钱："+jsonObject.get("code").getAsJsonObject().get("money").getAsDouble()+
//                                                "\n下级经验："+jsonObject.get("code").getAsJsonObject().get("nextGradeExperience").getAsLong()+
//                                                "\n充值了："+jsonObject.get("code").getAsJsonObject().get("recharge").getAsLong()+
//                                                "\n送礼了："+jsonObject.get("code").getAsJsonObject().get("totalGive").getAsLong()+
//                                                "\n接收了："+jsonObject.get("code").getAsJsonObject().get("totalRecipient").getAsLong()
//                                );
                            } else {
                                BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取订单历史失败!");
                            }
                        } else {
                            BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取订单历史失败!");
                        }
                    } else {
                        BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取订单历史失败!");
                    }

                }
            }, HttpUrls.getEBeanHistoryOrder_get() + data, map, Task.TYPE_GET_STRING_NOCACHE, null));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取充值选项
     */
    private List<EBeanOption> optionList;

    public void getChargeOptions() {
        HashMap<String, String> map = new HashMap<>();
        map.put("memberId", BaseApplication.getInstances().getUser_id());
        String data = "&data={\"deviceType\":0}";
        MyHandler.putTask(EBeanChargeActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                /**
                 *
                 */
                if (val != null && val.length() > 0) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull) &&
                            "200".equals(jsonObject.get("code").getAsString())) {
                        if (jsonObject.has("rows") && !(jsonObject.get("rows") instanceof JsonNull)) {
                            optionList = (List<EBeanOption>) SerialUtil.jsonToObject(jsonObject.get("rows").getAsJsonArray().toString(),
                                    new TypeToken<List<EBeanOption>>() {
                                    }.getType());
                            if (optionList != null && optionList.size() > 0) {
                                for (int i = 0; i < optionList.size(); i++) {
                                    final EBeanOption temp = optionList.get(i);
                                    //"¥  "
                                    if (temp.getId() == 1) {
                                        type1_name.setText(temp.getEbeans() + "豆");
                                        type1_value.setText("¥  " + StringUtil.getTwoBitDecimalString(temp.getPrice() * temp.getDiscount()));
                                        type1_value.setTag(temp.getPrice() * temp.getDiscount());
                                        type1_name.setTag(temp.getId());
                                    } else if (temp.getId() == 2) {
                                        type2_name.setText(temp.getEbeans() + "豆");
                                        type2_value.setText("¥  " + StringUtil.getTwoBitDecimalString(temp.getPrice() * temp.getDiscount()));
                                        type2_value.setTag(temp.getPrice() * temp.getDiscount());
                                        type2_name.setTag(temp.getId());
                                    } else if (temp.getId() == 3) {
                                        type3_name.setText(temp.getEbeans() + "豆");
                                        type3_value.setText("¥  " + StringUtil.getTwoBitDecimalString(temp.getPrice() * temp.getDiscount()));
                                        type3_value.setTag(temp.getPrice() * temp.getDiscount());
                                        type3_name.setTag(temp.getId());
                                    } else if (temp.getId() == 4) {
                                        type4_name.setText(temp.getEbeans() + "豆");
                                        type4_value.setText("¥  " + StringUtil.getTwoBitDecimalString(temp.getPrice() * temp.getDiscount()));
                                        type4_value.setTag(temp.getPrice() * temp.getDiscount());
                                        type4_name.setTag(temp.getId());
                                    } else if (temp.getId() == 5) {
                                        type5_name.setText(temp.getEbeans() + "豆");
                                        type5_value.setText("¥  " + StringUtil.getTwoBitDecimalString(temp.getPrice() * temp.getDiscount()));
                                        type5_value.setTag(temp.getPrice() * temp.getDiscount());
                                        type5_name.setTag(temp.getId());
                                    } else if (temp.getId() == 6) {
                                        type6_name.setText(temp.getEbeans() + "豆");
                                        type6_value.setText("¥  " + StringUtil.getTwoBitDecimalString(temp.getPrice() * temp.getDiscount()));
                                        type6_value.setTag(temp.getPrice() * temp.getDiscount());
                                        type6_name.setTag(temp.getId());
                                    }
                                }
                            } else {
                                BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取充值选项失败!");
                            }
                        } else {
                            BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取充值选项失败!");
                        }
                    } else {
                        BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取充值选项失败!");
                    }
                } else {
                    BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取充值选项失败!");
                }

            }
        }, HttpUrls.getEBeanChargeOption_get()+data, map, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(EBeanChargeActivity.this)));
    }

    /**
     * 接口已经定义好类型代表的值
     */
    private final int PAY_TYPE_WALLET = 0;
    private final int PAY_TYPE_ALI = 1;
    private final int PAY_TYPE_WECHAT = 2;
    private byte[] encodeData;
    private JsonObject jsonData;
    //    JSONObject object;
    private String object;
    /**
     *
     */
    public static boolean wxPayJumpFlag = false;
    private String wxOrderID;
    public static int wxPayStatus = 0;

    public static final int WX_PAY_INIT = 0;
    public static final int WX_PAY_OK = 1;

    private String globalOrderId;

    public void getOrder(final long rechargeId, final int payType, JsonObject json) {
        try {
            /**
             {
             "code": "200",
             "message": "返回成功",
             "object": {
             "PrivateKey": "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK+v3coHss1/KQmiPUer5C3oYH6i\r\ntXaNopSDoyI2sQG7/KUZGBblZK6K1xJI6JUfSt0kTiyuAek5h/vFFQdkORPhD1HlONXQQi+orx2Y\r\ny3cOwOjag3wiq9e0jsq2sa3jEIvt7UkJLvsKzAhFZLrtjiaXRlOPN1LMLD7oAwvPvkq3AgMBAAEC\r\ngYBZRr4mxui2glLBQX9wZ0nphjdawqYB25FtdMM2RKKIJhYJc4oJQIJ8K7OE6CXAeYlafPN4wufX\r\ns1OFvi2kduqFdhWc8rRGk1MoEB+89dcNlEJffBLbW3ryjAHRb20zN6QhPsnJ2W57wq710Mls6q/L\r\n2Wfv5rqfIy7sz9yhR1VkQQJBAOi4rUFj00yqytVAxKFqO+PNIXmbJjBRtC/SyaGRSY/cZiLP0qfj\r\nXieALBv49tzDfRyky3vSHLsX3Jmxtn4/PFcCQQDBQrPrVbQOUhW9bLmUaN5oCoq/bpIGkQn/fi6Y\r\naGgJ/25b+euoqqNUCyuMHsCNQQWyx0evMvRj+ceDuTrIemihAkEAhcXikbv4MhHbFkpsvPx8AK5h\r\nl61/fjb5BKR+EsmZJ4DhlRExXiBsdvhOYqXyAWS9/kEcVsioi0NkI1TsOhXPcQJAVx8izKxOB0ap\r\n1kL1RhogaPaXpcTKv3sS7sKLbtliTV+A+KWpvtdt2a1PbFNQqlztnLKJdkmaMpefgZo3DblhAQJB\r\nALRV5NYbSST6hdsp01dRsbUcO53Iz3AYSL3vRXN0axALWZND39d9rv6DIwiuaV/Ve8M9VD+Nksda\r\nIpYvXpRWFcc=\r\n",
             "PublicKey": "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvr93KB7LNfykJoj1Hq+Qt6GB+orV2jaKUg6Mi\r\nNrEBu/ylGRgW5WSuitcSSOiVH0rdJE4srgHpOYf7xRUHZDkT4Q9R5TjV0EIvqK8dmMt3DsDo2oN8\r\nIqvXtI7KtrGt4xCL7e1JCS77CswIRWS67Y4ml0ZTjzdSzCw+6AMLz75KtwIDAQAB\r\n"
             }
             }
             */
            publicKey = json.get("object").getAsJsonObject().get("PublicKey").getAsString();
//            privateKey = json.get("object").getAsJsonObject().get("PrivateKey").getAsString();
            object = "{\"rechargeId\":" + rechargeId + ",\"payment\":" + payType + "}";
            encodeData = RSACoder.encryptByPublicKey(object.toString().getBytes(), publicKey);
            String subText = Base64.encodeToString(encodeData, Base64.DEFAULT);
            HashMap<String, String> map = new HashMap<>();
            map.put("ciphertext", subText);
            map.put("memberId", BaseApplication.getInstances().getUser_id());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ciphertext", RSAHttp.httpTransferCharacter(subText));
            jsonObject.put("memberId", Long.parseLong(BaseApplication.getInstances().getUser_id()));

//            String decode = new String(RSACoder.decryptByPrivateKey(Base64.decode(subText.getBytes(),Base64.DEFAULT),privateKey));
//            LogUtil.e("本地解密:"+decode);
            LogUtil.e("jsonObect:" + jsonObject.toString());
            String urlPaht = HttpUrls.getEBeanOrder() + "&data=" + jsonObject.toString()/*.replace("\\","")*/;
            MyHandler.putTask(EBeanChargeActivity.this, new Task( new HttpResponseInterface() {
                @Override
                public void handMsg(String val) {
                    /**
                     {
                     "code": "200",
                     "message": "返回成功",
                     "object": {
                     "code": "200",
                     "orderid": "jENlRWvxlX84QICInsAYthPw4S/7NFuOKhjM%2BYlrvI%2B8QcYimtFJSJQ4bG6BMbXvz5GoCif8F0h4/xpIrAzuoNZAuQoXTZdCKXLropwZVhfvAXs3KcM%2BL3nK%2BnLXTt36M5Y79brxdPLRBLwTfmtePqFBZsoVUHtY5olzQRyDMIk="
                     }
                     }
                     */
                    try {
                        //{"code":"200","message":"返回成功","object":{"code":"200","orderid":"ZDCHIAdAntB42/NwULrAEzJ4np8O05CGYwSzJ4PvLY0ojtFzIVzBq4nNATeshaoTK8drTwTf0C5zTOgak9ydRztCPRCwqVQux5SmmHJhS8wfRnKST7gNVrz2F/287EKa0uVLSnwWymf1Z6NqCbHd9jXbD4askrP1NTiv7OAVaNo="}}
                        if (val != null && val.length() > 0) {
                            jsonData = new GsonParseUtil(val).getJsonObject();
                            if (jsonData != null
                                    && jsonData.has("code")
                                    && !(jsonData.get("code") instanceof JsonNull)
                                    && "200".equals(jsonData.get("code").getAsString())

                                    && jsonData.has("object")
                                    && !(jsonData.get("object") instanceof JsonNull)
                                    && jsonData.get("object").getAsJsonObject().has("code")
                                    && !(jsonData.get("object").getAsJsonObject().get("code") instanceof JsonNull)
                                    && "200".equals(jsonData.get("object").getAsJsonObject().get("code").getAsString())
                                    ) {
                                //服务端先私钥加密，在base64编码
                                /**
                                 *
                                 */
                                String temp = jsonData.get("object").getAsJsonObject().get("orderid").getAsString();
                                //TODO Base64解码  ---  公钥解码
                                LogUtil.e("解密前的数据[原始]:" + temp);
                                //** 1  --- 解密后是乱码
                                byte[] step1 = RSACoder.decryptBASE64(temp);
////                                LogUtil.e("解密前的数据【复原】:"+RSACoder.encryptBASE64(step1));
//                                String orderID = new String(RSACoder.decryptByPublicKey(step1, publicKey));
//                                LogUtil.e("解密后的数据:"+orderID);
                                /**
                                 * ************************************************
                                 */
                                globalOrderId = RSAHttp.publicDecryptData(RSAHttp.httpPactualCharacter(temp), publicKey);
//                                String temp = jsonData.get("object").getAsJsonObject().get("orderid").getAsString();
//                                //TODO Base64解码  ---  公钥解码
//                                LogUtil.e("解密前的数据[原始]:"+temp);
//                                //** 1  --- 解密后是乱码
//                                byte[] step1 = Base64.decode(temp,Base64.NO_WRAP);
//                                LogUtil.e("解密前的数据【复原】:"+Base64.encodeToString(step1,Base64.NO_WRAP));
//                                String orderID = new String(RSACoder.decryptByPublicKey(step1, publicKey));
//                                LogUtil.e("解密后的数据:"+orderID);
                                //** 1.1  --- 解密后是乱码
//                                String data = new String(RSACoder.decryptByPublicKey(temp.getBytes(), publicKey));

                                //** 1.2  --- 解密后是乱码
//                                String datas = new String(RSACoder.decryptByPublicKey(temp.getBytes(), publicKey));
//                                String data = new String(RSACoder.decryptBASE64(datas));
                                //** 2  --- Base64 解码出现bad base-64 异常
//                                byte[] step1 = Base64.decode(temp,Base64.DEFAULT);
//                                String orderID = new String(RSACoder.decryptByPublicKey(step1, publicKey));

//                                BaseApplication.getInstances().toast("解密后的数据:" + globalOrderId);

                                switch (payType) {
                                    case PAY_TYPE_WALLET: {
                                        getRemainSum();
                                        break;
                                    }
                                    case PAY_TYPE_ALI: {
                                        //TODO 由于接口给的数据是PayInfo是String类型，需要小心这里是否会出现异常
                                        //
//                                        JsonObject payInfo = jsonData.get("object").getAsJsonObject().get("paidinfo").getAsJsonObject();
                                        //
                                        JsonObject payInfo = new GsonParseUtil(jsonData.get("object").getAsJsonObject().get("paidinfo").getAsString().replace("\\", "")).getJsonObject();
                                        aliPay(globalOrderId, 0.01, payInfo, PAY_TYPE_ALI + "");
                                        break;
                                    }
                                    case PAY_TYPE_WECHAT: {
                                        //
//                                        JsonObject payInfo = jsonData.get("object").getAsJsonObject().get("paidinfo").getAsJsonObject();
                                        //
                                        JsonObject payInfo = new GsonParseUtil(jsonData.get("object").getAsJsonObject().get("paidinfo").getAsString().replace("\\", "")).getJsonObject();
                                        wxPayJumpFlag = true;
                                        wxOrderID = globalOrderId;
                                        EBeanParamEntity entity = new EBeanParamEntity();
                                        entity.setType("微信支付");
                                        entity.setOrderid(globalOrderId);
                                        entity.setTotal_fee(0.01);
                                        new WeixinPayHandler(EBeanChargeActivity.this, WXPayEntryActivity.FROM_EBEAN_PAY, entity).pay(payInfo);
                                        break;
                                    }
                                }
                            } else {
                                BaseApplication.getInstances().toast(EBeanChargeActivity.this,"生成订单失败!");
                            }
                        } else {
                            BaseApplication.getInstances().toast(EBeanChargeActivity.this,"生成订单失败!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e("解密出现异常:" + e.getMessage());
                    }
                }
            }, urlPaht, map, Task.TYPE_GET_STRING_NOCACHE, null));

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("getOrder 出现的异常:" + e.getMessage());
        }
    }

    public void gotoPayProgress() {

    }

    public void initTitleBar() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的账户");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        menu_text = (TextView) findViewById(R.id.menu_text);
        menu_text.setVisibility(View.GONE);
        menu_text.setText("充值记录");
        menu_text.setOnClickListener(this);
    }

    public void initType() {
        type1 = (LinearLayout) findViewById(R.id.type1);
        type1.setOnClickListener(this);
        type1_name = (TextView) findViewById(R.id.type1_name);
        type1_value = (TextView) findViewById(R.id.type1_value);

        type2 = (LinearLayout) findViewById(R.id.type2);
        type2.setOnClickListener(this);
        type2_name = (TextView) findViewById(R.id.type2_name);
        type2_value = (TextView) findViewById(R.id.type2_value);

        type3 = (LinearLayout) findViewById(R.id.type3);
        type3.setOnClickListener(this);
        type3_name = (TextView) findViewById(R.id.type3_name);
        type3_value = (TextView) findViewById(R.id.type3_value);

        type4 = (LinearLayout) findViewById(R.id.type4);
        type4.setOnClickListener(this);
        type4_name = (TextView) findViewById(R.id.type4_name);
        type4_value = (TextView) findViewById(R.id.type4_value);

        type5 = (LinearLayout) findViewById(R.id.type5);
        type5.setOnClickListener(this);
        type5_name = (TextView) findViewById(R.id.type5_name);
        type5_value = (TextView) findViewById(R.id.type5_value);

        type6 = (LinearLayout) findViewById(R.id.type6);
        type6.setOnClickListener(this);
        type6_name = (TextView) findViewById(R.id.type6_name);
        type6_value = (TextView) findViewById(R.id.type6_value);

        setType(TYPE1);
    }

    protected void initView() {
        name = (TextView) findViewById(R.id.name);
        value = (TextView) findViewById(R.id.value);
        setEBean(0+"豆");

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);

        ll_signup_clause_ck = (LinearLayout) findViewById(R.id.ll_signup_clause_ck);
        ll_signup_clause_ck.setOnClickListener(this);
        signup_clause_ck = findViewById(R.id.signup_clause_ck);
        signup_clause_text = (TextView) findViewById(R.id.signup_clause_text);
        initFlag();
    }

    public void setEBean(String evalue) {
        value.setText(evalue);
    }

    /**
     * TODO 网络获取E豆的数值
     */
    public void getEValueFromNet() {
        setEBean("0");
    }

    private void initFlag() {
        signup_clause_ck.setBackgroundResource(R.drawable.doctor_pay_list_nor);
        read_flag = false;
        signup_clause_text.setText(R.string.singup_clause_text1);
        String string = "《用户充值协议》";
        SpannableString spannableString = new SpannableString(string);
        OrderClickSpan orderClickSpan = new OrderClickSpan(string, getResources().getColor(R.color.color_title_green), new OnClickListener() {
            @Override
            public void onClick(View viewss) {
                showDialog();
            }
        });
        spannableString.setSpan(orderClickSpan, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        signup_clause_text.append(spannableString);
        signup_clause_text.setMovementMethod(LinkMovementMethod.getInstance());

        resetFlag();
    }

    private void resetFlag() {
        read_flag = !read_flag;
        if (read_flag) {
            signup_clause_ck.setBackgroundResource(R.drawable.doctor_pay_list_light);
        } else {
            signup_clause_ck.setBackgroundResource(R.drawable.doctor_pay_list_nor);
        }
    }

    private void showDialog() {
        /**
         * 对话框样式《注册声明》
         */
        final Dialog detailDialog = QuanjiakanDialog.getInstance().getDialog(EBeanChargeActivity.this);
        View view = LayoutInflater.from(EBeanChargeActivity.this).inflate(R.layout.dialog_medicine_introduce, null);
        /**
         * 数据赋值
         */
        RelativeLayout rl_exit = (RelativeLayout) view.findViewById(R.id.rl_exit);
        rl_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                detailDialog.dismiss();
                signup_clause_text.invalidate();
            }
        });
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//        title.setText("全家康免责条款");//软件许可及服务协议
        title.setText("全家康充值服务协议");//
        TextView include_value = (TextView) view.findViewById(R.id.indroduce);
        include_value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        include_value.setText(R.string.ebean_rules_new);
        WindowManager.LayoutParams lp = detailDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        detailDialog.setContentView(view, lp);
        detailDialog.setCanceledOnTouchOutside(false);
        detailDialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);

        if (wxPayJumpFlag) {//从微信支付返回
            wxPayJumpFlag = false;
            if (wxPayStatus == WX_PAY_OK) {
                vertifyWechatPayment(wxOrderID);
            } else {
                BaseApplication.getInstances().toast(EBeanChargeActivity.this,"微信支付失败!");
            }
            wxPayStatus = WX_PAY_INIT;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    private final int TYPE1 = 1;
    private final int TYPE2 = 2;
    private final int TYPE3 = 3;
    private final int TYPE4 = 4;
    private final int TYPE5 = 5;
    private final int TYPE6 = 6;


    private int selectedType = 1;

    public void setType(int type) {
        selectedType = type;
        type1.setBackgroundResource(R.drawable.selecter_ebean_normal);
        type1_name.setTextColor(getResources().getColor(R.color.font_color_333333));
        type1_value.setTextColor(getResources().getColor(R.color.font_color_999999));

        type2.setBackgroundResource(R.drawable.selecter_ebean_normal);
        type2_name.setTextColor(getResources().getColor(R.color.font_color_333333));
        type2_value.setTextColor(getResources().getColor(R.color.font_color_999999));

        type3.setBackgroundResource(R.drawable.selecter_ebean_normal);
        type3_name.setTextColor(getResources().getColor(R.color.font_color_333333));
        type3_value.setTextColor(getResources().getColor(R.color.font_color_999999));

        type4.setBackgroundResource(R.drawable.selecter_ebean_normal);
        type4_name.setTextColor(getResources().getColor(R.color.font_color_333333));
        type4_value.setTextColor(getResources().getColor(R.color.font_color_999999));

        type5.setBackgroundResource(R.drawable.selecter_ebean_normal);
        type5_name.setTextColor(getResources().getColor(R.color.font_color_333333));
        type5_value.setTextColor(getResources().getColor(R.color.font_color_999999));

        type6.setBackgroundResource(R.drawable.selecter_ebean_normal);
        type6_name.setTextColor(getResources().getColor(R.color.font_color_333333));
        type6_value.setTextColor(getResources().getColor(R.color.font_color_999999));

        switch (type) {
            case TYPE1:
                type1.setBackgroundResource(R.drawable.selecter_ebean_selected);
                type1_name.setTextColor(getResources().getColor(R.color.white));
                type1_value.setTextColor(getResources().getColor(R.color.white));
                break;
            case TYPE2:
                type2.setBackgroundResource(R.drawable.selecter_ebean_selected);
                type2_name.setTextColor(getResources().getColor(R.color.white));
                type2_value.setTextColor(getResources().getColor(R.color.white));
                break;
            case TYPE3:
                type3.setBackgroundResource(R.drawable.selecter_ebean_selected);
                type3_name.setTextColor(getResources().getColor(R.color.white));
                type3_value.setTextColor(getResources().getColor(R.color.white));
                break;
            case TYPE4:
                type4.setBackgroundResource(R.drawable.selecter_ebean_selected);
                type4_name.setTextColor(getResources().getColor(R.color.white));
                type4_value.setTextColor(getResources().getColor(R.color.white));
                break;
            case TYPE5:
                type5.setBackgroundResource(R.drawable.selecter_ebean_selected);
                type5_name.setTextColor(getResources().getColor(R.color.white));
                type5_value.setTextColor(getResources().getColor(R.color.white));
                break;
            case TYPE6:
                type6.setBackgroundResource(R.drawable.selecter_ebean_selected);
                type6_name.setTextColor(getResources().getColor(R.color.white));
                type6_value.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.ibtn_back:
                /**
                 *
                 */
//                if(entry_type==BaseConstants.ENTRY_VIDEO_LIVE){
//                    intoLiveRoom(entity,entity.getUserId(),hostName,hostImg);
//                }else{
//                    finish();
//                }
                finish();
                break;
            case R.id.menu_text:
                Intent intentChargeHistory = new Intent(EBeanChargeActivity.this, EBeanChargeHistoryActivity.class);
                startActivity(intentChargeHistory);
                break;
            case R.id.ll_signup_clause_ck:
                resetFlag();
                break;
            case R.id.btn_submit:
                submit();
                break;
            case R.id.type1:
                setType(TYPE1);
                break;
            case R.id.type2:
                setType(TYPE2);
                break;
            case R.id.type3:
                setType(TYPE3);
                break;
            case R.id.type4:
                setType(TYPE4);
                break;
            case R.id.type5:
                setType(TYPE5);
                break;
            case R.id.type6:
                setType(TYPE6);
                break;
        }
    }

    /**
     * TODO 提交，购买E豆
     */
    private int rechargeId;

    public void submit() {
        if (!read_flag) {
            Toast.makeText(this, "请阅读并同意《用户充值协议》", Toast.LENGTH_SHORT).show();
            return;
        }
        if (optionList == null || optionList.size() < 1) {
            Toast.makeText(this, "获取充值选项失败，准备重试!", Toast.LENGTH_SHORT).show();
            getChargeOptions();
            return;
        }
        switch (selectedType) {
            case TYPE1:
                servicePrice = (double) type1_value.getTag();
                rechargeId = (int) type1_name.getTag();
                break;
            case TYPE2:
                servicePrice = (double) type2_value.getTag();
                rechargeId = (int) type2_name.getTag();
                break;
            case TYPE3:
                servicePrice = (double) type3_value.getTag();
                rechargeId = (int) type3_name.getTag();
                break;
            case TYPE4:
                servicePrice = (double) type4_value.getTag();
                rechargeId = (int) type4_name.getTag();
                break;
            case TYPE5:
                servicePrice = (double) type5_value.getTag();
                rechargeId = (int) type5_name.getTag();
                break;
            case TYPE6:
                servicePrice = (double) type6_value.getTag();
                rechargeId = (int) type6_name.getTag();
                break;
        }
        /**
         * TODO 需要提供用户选择进行支付的类型对话框
         */
        getPublicKey();
//        Toast.makeText(this, "\nselectedType:" + selectedType +
//                "     \n**** read_flag:" + read_flag +
//                "     \n**** Price:" + payPrice, Toast.LENGTH_SHORT).show();
        /**
         *
         * TODO 进行网络请求，充值获取E豆
         *
         * 然后刷新界面
         */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

        }
    }



    public void intoLiveRoom(final VideoLiveListEntity entity, final String docID,final String name,final String img){
        MyHandler.putTask(EBeanChargeActivity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                try{
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if(jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull)){

                        if(jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull)
                                && jsonObject.get("object").getAsJsonObject()!=null  &&
                                "1".equals(jsonObject.get("code").getAsString())){

                            if(jsonObject.get("object").getAsJsonObject().has("isFollow") && !(jsonObject.get("object").getAsJsonObject().get("isFollow") instanceof JsonNull)){
                                entity.setIsFollow(jsonObject.get("object").getAsJsonObject().get("isFollow").getAsLong());
                            }else{
                                entity.setIsFollow(0);
                            }

                            if(jsonObject.get("object").getAsJsonObject().has("state") &&
                                    !(jsonObject.get("object").getAsJsonObject().get("state") instanceof JsonNull) &&
                                    (1==jsonObject.get("object").getAsJsonObject().get("state").getAsInt())){
                                loginCount = 0;
                                intoLive(entity,jsonObject,name,img);
                            }else{
                                BaseApplication.getInstances().toast(EBeanChargeActivity.this,"来晚了，直播已经结束!");
                                finish();
                            }

                        }else{
                            if(jsonObject.has("message") && !(jsonObject.get("message") instanceof JsonNull)){
                                BaseApplication.getInstances().toast(EBeanChargeActivity.this,jsonObject.get("message").getAsString());
                            }else{
                                BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取直播地址失败!");
                            }
                            finish();
                        }
                    }else{
                        BaseApplication.getInstances().toast(EBeanChargeActivity.this,"获取直播地址失败!");
                        finish();
                    }
                }catch (Exception e){
                    LogUtil.e(e.getMessage());
                }
            }
        }, HttpUrls.getIntoLiveRoom()+"&userId="+docID+"&lookId="+BaseApplication.getInstances().getUser_id()+"&userType=0",
                null,Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(EBeanChargeActivity.this)));
    }

    private int loginCount = 0;
    public void intoLive(final VideoLiveListEntity entity, final JsonObject jsonObject, final String name, final String img){
        if(loginCount>5){
            Toast.makeText(EBeanChargeActivity.this, "进入直播聊天群失败!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loginCount++;
        if(JMessageClient.getMyInfo()!=null) {
            Intent intent = new Intent(EBeanChargeActivity.this, VideoLivePlayActivity.class);
            intent.putExtra(BaseConstants.PARAM_ENTITY, entity);
            intent.putExtra(BaseConstants.PARAM_URL, jsonObject.get("object").getAsJsonObject().get("flvUrl").getAsString());
            intent.putExtra(BaseConstants.PARAM_GROUP, jsonObject.get("object").getAsJsonObject().get("groupId").getAsString());
            intent.putExtra(BaseConstants.PARAM_NUMBER, jsonObject.get("object").getAsJsonObject().get("lookNum").getAsLong() + "");
            intent.putExtra(BaseConstants.PARAM_NAME, name);
            intent.putExtra(BaseConstants.PARAM_HEADIMG, img);
            intent.putExtra(BaseConstants.PARAM_LIVERID, jsonObject.get("object").getAsJsonObject().get("userId").getAsString());
            startActivity(intent);
            finish();
        }else{
            JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                @Override
                public void gotResult(int result, String s) {
                    if(result==0){
                        Intent intent = new Intent(EBeanChargeActivity.this, VideoLivePlayActivity.class);
                        intent.putExtra(BaseConstants.PARAM_ENTITY, entity);
                        intent.putExtra(BaseConstants.PARAM_URL, jsonObject.get("object").getAsJsonObject().get("flvUrl").getAsString());
                        intent.putExtra(BaseConstants.PARAM_GROUP, jsonObject.get("object").getAsJsonObject().get("groupId").getAsString());
                        intent.putExtra(BaseConstants.PARAM_NUMBER, jsonObject.get("object").getAsJsonObject().get("lookNum").getAsLong() + "");
                        intent.putExtra(BaseConstants.PARAM_NAME, name);
                        intent.putExtra(BaseConstants.PARAM_HEADIMG, img);
                        intent.putExtra(BaseConstants.PARAM_LIVERID, jsonObject.get("object").getAsJsonObject().get("userId").getAsString());
                        startActivity(intent);
                        finish();
                    }else{
                        intoLive(entity,jsonObject,name,img);
                    }
                }
            });
        }
    }
}
