package com.androidquanjiakan.activity.setting.contact;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.result.ContactsResultBean;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.List;


/**
 * 作者：Administrator on 2017/2/21 09:52
 * <p>
 * 邮箱：liuzj@hi-board.com
 */
public class BindingAndUnbundActivity extends BaseActivity implements View.OnClickListener {

    private TextView unbund;
    private TextView tv_bund_num;
    private TextView tv_2;
    private String device_id;
    private String watchName;
    private ImageView iv_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bund_unbund);
        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        watchName = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_NAME);
        if (device_id == null || device_id.length() < 1) {
            BaseApplication.getInstances().toast(BindingAndUnbundActivity.this, "传入参数异常!");
            finish();
            return;
        }
        initTitle();
        initView();
        loadContactsData();
    }

    private void initView() {

        unbund = (TextView) findViewById(R.id.tv_unbund);
        tv_bund_num = (TextView) findViewById(R.id.tv_bund_num);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        iv_scan = (ImageView) findViewById(R.id.iv_img);
        tv_bund_num.setText("绑定号:" + device_id);
        if (watchName.contains("%")) {
            try {
                tv_2.setText(URLDecoder.decode(watchName, "utf-8") + "的二维码");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            tv_2.setText(watchName + "的二维码");
        }

        unbund.setOnClickListener(this);
        // TODO: 2017/4/20 xianxiesi  
        String uri = "http://app.quanjiakan.com/familycare/activate?IMEI=" + device_id;
        try {
            Bitmap bitmap = createQRCode(uri, QuanjiakanUtil.dip2px(this, 150));
            if (bitmap != null) {
                iv_scan.setImageBitmap(bitmap);
            }


        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    private List<ContactsResultBean.ResultsBean.ContactsBean> contacts;
    private boolean isAdmin;
    private int appUserNumber;
    private void loadContactsData() {
        /**
         * 数据库没有数据时请求网络
         */
        appUserNumber = 0;
        isAdmin = false;
        MyHandler.putTask(BindingAndUnbundActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("contacts-----------------" + val);
                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                if (!jsonObject.has("code")&&val.length()>2) {
                    JsonObject result1 = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
                    if (device_id.equals(result1.get("IMEI").getAsString())) {
                        JsonArray jsonArray = result1.getAsJsonArray(ConstantClassFunction.CONTACTS);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject object = jsonArray.get(i).getAsJsonObject();
                            ContactsResultBean.ResultsBean.ContactsBean contactsBean = new ContactsResultBean.ResultsBean.ContactsBean();
                            if (object.has("Admin")) {
                                contactsBean.setAdmin(object.get("Admin").getAsString());
                            }

                            if (object.has("App")) {
                                contactsBean.setApp(object.get("App").getAsString());
                                if("1".equals(object.get("App").getAsString())){
                                    appUserNumber++;
                                }
                            }

                            if (object.has("Id")) {
                                contactsBean.setId(object.get("Id").getAsString());
                            }


                            if (object.has("Image")) {
                                contactsBean.setImage(object.get("Image").getAsString());
                            }

                            if (object.has("Name")) {
                                contactsBean.setName(object.get("Name").getAsString());
                            }

                            if (object.has("Tel")) {
                                contactsBean.setTel(object.get("Tel").getAsString());
                            }

                            if (object.has("Userid")) {
                                contactsBean.setUserid(object.get("Userid").getAsString());
                            }
//                            contacts.add(contactsBean);
                            if(object.has("Admin") && "1".equals(object.get("Admin").getAsString()) &&
                                    object.has("Userid") &&
                                    BaseApplication.getInstances().getUser_id().equals(object.get("Userid").getAsString())){
                                isAdmin = true;
                            }
                        }
                    }
                } else {
                    BaseApplication.getInstances().toast(BindingAndUnbundActivity.this, "未查询到数据");
                }


            }
        }, HttpUrls.getContactsList() + "&imei=" + device_id, null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(BindingAndUnbundActivity.this)));
    }

    //    private static final int BLACK = 0xff000000;
//    private Bitmap createQRCode(String str, int widthAndHeight)
//            throws WriterException {
//        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
//        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//        BitMatrix matrix = new MultiFormatWriter().encode(str,
//                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
//        int width = widthAndHeight;
//        int height = widthAndHeight;
//        int[] pixels = new int[width * height];
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                if (matrix.get(x, y)) {
//                    pixels[y * width + x] = BLACK;
//                }
//            }
//        }
//        Bitmap bitmap = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//        return bitmap;
//    }
    private static final int BLACK = 0xff000000;

    private static final int PADDING_SIZE_MIN = 5; // 最小留白长度, 单位: px

    public static Bitmap createQRCode(String str, int widthAndHeight) throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        boolean isFirstBlackPoint = false;
        int startX = 0;
        int startY = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    if (isFirstBlackPoint == false) {
                        isFirstBlackPoint = true;
                        startX = x;
                        startY = y;
//                        Log.d("createQRCode", "x y = " + x + " " + y);
                    }
                    pixels[y * width + x] = BLACK;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        // 剪切中间的二维码区域，减少padding区域
        if (startX <= PADDING_SIZE_MIN) return bitmap;

        int x1 = startX - PADDING_SIZE_MIN;
        int y1 = startY - PADDING_SIZE_MIN;
        if (x1 < 0 || y1 < 0) return bitmap;

        int w1 = width - x1 * 2;
        int h1 = height - y1 * 2;

        Bitmap bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);

        return bitmapQR;
    }


    private ImageButton ibtn_back;
    private TextView tv_title;

    private void initTitle() {

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title.setText("绑定与解绑");

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;

            case R.id.tv_unbund:
                /**
                 * 显示解除绑定的dialog
                 */
                //TODO 检查是否应该进行管理员权限的移交
                if(isAdmin && appUserNumber>1){
                    BaseApplication.getInstances().toast(BindingAndUnbundActivity.this,"请在手表通讯录中将管理员权限移交给其他用户后再进行解绑!");
                    return ;
                }

                showUnbundDialog();
                break;
        }

    }

    private void showUnbundDialog() {

        final Dialog dialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_manager, null);

        TextView title = (TextView) view.findViewById(R.id.tv_dialog_title);
        title.setText("解除绑定");

        view.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                QuanjiakanUtil.showToast(BindingAndUnbundActivity.this,"确定");
                //TODO 执行解绑操作
                if (BaseApplication.getInstances().isSDKConnected()) {
                    //***解绑

                    long devid = Long.parseLong(device_id, 16);
                    BaseApplication.getInstances().getNattyClient().ntyUnBindClient(devid);
                    if(isAdmin && appUserNumber==1){
                        //TODO 最后一个APP用户重置
                        try{
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("IMEI",device_id);
                            jsonObject.put("Category","Reset");
                            BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(devid,jsonObject.toString().getBytes(),jsonObject.toString().length());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                } else {
                    BaseApplication.getInstances().toast(BindingAndUnbundActivity.this, "已与手表服务器断开连接");
                }
            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView content = (TextView) view.findViewById(R.id.tv_content);
        content.setText(R.string.dialog_unbund_content);


        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setContentView(view, lp);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //TODO 根据指定文本创建二维码，后续需要显示到图片上
    private Bitmap createImage(String text) {
        try {
            int QR_WIDTH = QuanjiakanUtil.dip2px(BindingAndUnbundActivity.this, 150);
            int QR_HEIGHT = QuanjiakanUtil.dip2px(BindingAndUnbundActivity.this, 150);
            if (TextUtils.isEmpty(text)) {
                return null;
            }
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,
                    QR_WIDTH, QR_HEIGHT);

            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonNattyData(CommonNattyData msg) {
        switch (msg.getType()) {
            case NattyProtocolFilter.DISPLAY_UPDATE_CONTROL_UNBIND_RESULT:
                //0：代表成功，1：代表 UserId不存在，2：代表DeviceId不存在，3：代表UserId与DeviceId已经绑定过了
                String res = msg.getData();
                LogUtil.e("BindingAndUnbundActivity UNBIND_RESULT " + res);
                if ("0".equals(res)) {

                    setResult(RESULT_OK);
                    BindingAndUnbundActivity.this.finish();
//                        bindDevice(bind_device_2dcode_value.getText().toString().trim());
                    /**
                     * 确认解绑成功后
                     */
//                    BindDeviceHandler.remove(device_id);
//                    HashMap<String, String> params = new HashMap<>();
//                    params.put("user_id", BaseApplication.getInstances().getUser_id());
//                    params.put("did", device_id);
//                    params.put("type", "0");
//                    params.put("IMEI", device_id);
//                    MyHandler.putTask(BindingAndUnbundActivity.this, new Task(new HttpResponseInterface() {
//                        @Override
//                        public void handMsg(String val) {
//                            LogUtil.e("--------unbind-------"+val);
//
//                        }
//                    }, HttpUrls.bindedWatch(), params, Task.TYPE_POST_DATA_PARAMS, null));
                } else {
                    BaseApplication.getInstances().toast(BindingAndUnbundActivity.this, "解除绑定失败!");
                }
                break;

//            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_RESULT:
//                String s = msg.getData();
//                LogUtil.e("the last unbind " + s);
//
//                /*
//                * {"Results":{"Code":"200","Message":"Success"}}
//                * */
//
//                JsonObject jsonObject = new GsonParseUtil(s).getJsonObject();
//                if (jsonObject.has(ConstantClassFunction.getRESULTS())) {
//                    JsonObject result = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
//                    if (result.get(ConstantClassFunction.getRESULTS()).getAsString().equals("200")) {
//                        BaseApplication.getInstances().toast(BindingAndUnbundActivity.this, "解除绑定成功!");
//                        finish();
//                    }else {
//                        BaseApplication.getInstances().toast(BindingAndUnbundActivity.this, "解除绑定失败!");
//                    }
//                }
//                break;
        }
    }
}
