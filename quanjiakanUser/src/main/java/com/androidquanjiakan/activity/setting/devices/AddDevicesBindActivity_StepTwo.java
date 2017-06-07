package com.androidquanjiakan.activity.setting.devices;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.datahandler.BindDeviceHandler;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.SignatureUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpClientUtil;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.ImageUtils;
import com.umeng.analytics.MobclickAgent;
import com.zxing.qrcode.BindDeviceActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddDevicesBindActivity_StepTwo extends BaseActivity implements OnClickListener {

    protected ImageButton ibtn_back;
    protected TextView tv_title;

    private EditText bind_device_name_value;
    private EditText bind_device_image;

    private boolean uploadStatus = false;

    private ImageView bind_device_headimg;
    private Button btn_submit;

    private static final int REQUEST_SCAN = 1010;

    private static final int MSG_UPLOAD_RESULT = 1020;

    private String deviceID;

    private DisplayImageOptions options;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPLOAD_RESULT:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_device_bind_two);
        deviceID = getIntent().getStringExtra(AddDevicesBindActivity_StepOne.PARAM_ID);
        if(deviceID==null){
            BaseApplication.getInstances().toast(AddDevicesBindActivity_StepTwo.this,"传入参数异常!");
            finish();
            return;
        }
        initView();
    }

    protected void initView() {

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("更新绑定信息");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        bind_device_name_value = (EditText) findViewById(R.id.bind_device_name_value);
        bind_device_image = (EditText) findViewById(R.id.bind_device_image);
        bind_device_headimg = (ImageView) findViewById(R.id.bind_device_headimg);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        bind_device_image.setOnClickListener(this);
        btn_submit.setOnClickListener(this);

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
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.ibtn_back:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btn_submit:
                /**
                 * 提交设备绑定
                 */
                if(uploadStatus && bind_device_image.getText().toString().toLowerCase().startsWith("http")){
                    updateDeviceInfo(deviceID,
                            bind_device_name_value.getText().toString(),
                            bind_device_image.getText().toString());
                }else{
                    BaseApplication.getInstances().toast(AddDevicesBindActivity_StepTwo.this,"文件上传失败，请重试后再次尝试提交!");
                }
                break;
            case R.id.bind_device_scan_2dcode:
                /**
                 * 扫描获取二维码
                 */
                Intent intent = new Intent(this, BindDeviceActivity.class);
                intent.putExtra(BaseConstants.PARAMS_SHOW_HINT,true);
                startActivityForResult(intent, REQUEST_SCAN);
                break;
            case R.id.bind_device_image:
                /**
                 * 拍照、从相册获取？
                 */
                showOptionsDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    save2SmallImage(data.getData());
                }
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (QuanjiakanUtil.hasSdcard()) {
                        File tempFile = new File(BaseConstants.imageDir + IMAGE_FILE_NAME);
                        try {
                            Uri u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
                                    tempFile.getAbsolutePath(), null, null));
                            // u就是拍摄获得的原始图片的uri，剩下的你想干神马坏事请便……
                            save2SmallImage(u);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            MobclickAgent.reportError(AddDevicesBindActivity_StepTwo.this,e);
                        }
                    } else {
                        Toast.makeText(AddDevicesBindActivity_StepTwo.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA:
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        ImageCropHandler.beginCrop(AddDevicesBindActivity_StepTwo.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                    }
                }
                break;
            case CommonRequestCode.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    ImageCropHandler.beginCrop(AddDevicesBindActivity_StepTwo.this, data.getData());
                }
            case CommonRequestCode.REQUEST_CROP:
                ImageCropHandler.handleCrop(null, resultCode, data, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        LogUtil.e("最后展示图片的地址:" + path);
                        bind_device_image.setText(path);
                        /**
                         * 将地址展示在界面上
                         */
//                        new FileAsync().execute(path);

                        /**
                         * {"code":"500","message":"error"}
                         */
                        dialog = QuanjiakanDialog.getInstance().getDialog(AddDevicesBindActivity_StepTwo.this,"正在上传头像!");
                        final File file = new File(path);
                        HashMap<String, String> params = new HashMap<>();
                        params.put("file", path.toString());
                        params.put("filename", file.getName());
                        params.put("image", file.getAbsolutePath());
                        Task task = new Task(new HttpResponseInterface() {

                            @Override
                            public void handMsg(String val) {
                                dialog.dismiss();
                                LogUtil.e("VAL-----"+val);
                                // TODO Auto-generated method stub
                                if(val!=null && !val.equals("") && val.toLowerCase().startsWith("{")){
                                    //上传文件成功
                                    uploadStatus = true;
                                    try {
                                        LogUtil.e("result++++++++++++++++++++++++++:"+val);
                                        JSONObject json = new JSONObject(val);
                                        if(json.has("code") && "200".equals(json.getString("code"))){
                                            bind_device_image.setText(json.getString("message"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        MobclickAgent.reportError(AddDevicesBindActivity_StepTwo.this,e);
                                    }

                                }else {
                                    //文件上传失败
                                    uploadStatus = false;
                                }
                                if(file.exists()){
                                    file.delete();
                                }
                            }
                        }, HttpUrls.postFile()+"&storage=16", params, Task.TYPE_POST_FILE, null);
                        MyHandler.putTask(AddDevicesBindActivity_StepTwo.this,task);
                    }
                });
                break;
            default:
                break;
        }
    }

    Dialog dialog;
    private String mCurrentPhotoPath = null;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final String IMAGE_FILE_NAME = "qjk" + String.valueOf(System.currentTimeMillis()).substring(4) + ".jpg";

    protected void showOptionsDialog() {
        List<String> strings = new ArrayList<>();
        strings.add("相册");
        strings.add("拍照");
        QuanjiakanDialog.getInstance().getListDialogDefineHeight(AddDevicesBindActivity_StepTwo.this, strings, "选择照片", new QuanjiakanDialog.MyDialogCallback() {

            @Override
            public void onItemClick(int position, Object object) {
                // TODO Auto-generated method stub
                switch (position) {
                    case 0:
                        ImageCropHandler.pickImage(AddDevicesBindActivity_StepTwo.this);
                        break;
                    case 1:
                        ImageCropHandler.getImageFromCamera(AddDevicesBindActivity_StepTwo.this, new IImageCropInterface() {
                            @Override
                            public void getFilePath(String path) {
                                mCurrentPhotoPath = path;
                                LogUtil.e("获取从照相机拍摄的照片路径:" + path);
                            }
                        });
                        break;
                }
            }
        }, null);
    }

    private Bitmap temp;
    private String sourcePath;

    public void save2SmallImage(Uri data) {
        if (data == null) {
            return;
        }
        sourcePath = ImageUtils.uri2Path(data, AddDevicesBindActivity_StepTwo.this);
        String smallImagePath = ImageUtils.saveBitmapToStorage(SignatureUtil.getMD5String(sourcePath) + ".jpg", getSmallBitmap(sourcePath));
        bind_device_image.setText(smallImagePath);
        if (temp != null) {
            temp.recycle();
            temp = null;
        }
        temp = BitmapFactory.decodeFile(smallImagePath);
        bind_device_headimg.setImageBitmap(temp);
    }

    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = ImageUtils.calculateInSampleSize(options, 160, 160);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static final String DEVICEID = "deviceid";

    protected void updateDeviceInfo(final String deviceid,final String name,final String icon) {
        /**
         * 优化点：是否需要检查用户名存在 或 头像是否存在
         */
        if (CheckUtil.isEmpty(deviceid)) {
            BaseApplication.getInstances().toast(AddDevicesBindActivity_StepTwo.this,"无效的设备ID,请重新输入或获取!");
            return;
        }
        //TODO Old Net Interface
//        HashMap<String, String> params = new HashMap<>();
//        params.put("user_id", QuanjiakanSetting.getInstance().getUserId() + "");
//        params.put("deviceid", deviceid);
//        params.put("name", name);
//        params.put("icon", icon);
//        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
//
//            @Override
//            public void handMsg(String val) {
//                // TODO Auto-generated method stub
//                HttpResponseResult result = new HttpResponseResult(val);
//                if (result.getCode().equals("200")) {
//                    /**
//                     * 在数据库中保存绑定的设备ID与头像地址
//                     */
//                    Toast.makeText(AddDevicesBindActivity_StepTwo.this, "更新绑定信息成功", Toast.LENGTH_SHORT).show();
//                    String name = bind_device_name_value.getText().toString().trim();
//                    if (CheckUtil.isEmpty(name)) {
//                        name = "";
//                    }
//                    String headimg = bind_device_image.getText().toString().trim();
//                    if (CheckUtil.isEmpty(headimg)) {
//                        headimg = "";
//                    }
//                    BindDeviceHandler.insertValue(deviceid, name, headimg);
//                    setResult(RESULT_OK);
//                    finish();
//                } else {
////                    Toast.makeText(AddDevicesBindActivity_StepTwo.this, "更新绑定信息出现问题，请重试！", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(AddDevicesBindActivity_StepTwo.this, result.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, HttpUrls.updateWatchInfo(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(AddDevicesBindActivity_StepTwo.this, "绑定中...")));
        //TODO New


        MyHandler.putTask(this,new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                HttpResponseResult result = new HttpResponseResult(val);
                if (result.getCode().equals("200")) {
                    /**
                     * 在数据库中保存绑定的设备ID与头像地址
                     */
                    Toast.makeText(AddDevicesBindActivity_StepTwo.this, "更新绑定信息成功", Toast.LENGTH_SHORT).show();
                    String name = bind_device_name_value.getText().toString().trim();
                    if (CheckUtil.isEmpty(name)) {
                        name = "";
                    }
                    String headimg = bind_device_image.getText().toString().trim();
                    if (CheckUtil.isEmpty(headimg)) {
                        headimg = "";
                    }
                    BindDeviceHandler.insertValue(deviceid, name, headimg);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddDevicesBindActivity_StepTwo.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.updateWatchInfo_new(deviceid,icon,name,""), null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(AddDevicesBindActivity_StepTwo.this, "正在更新信息...")));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (temp != null) {
            temp.recycle();
        }
    }

}
