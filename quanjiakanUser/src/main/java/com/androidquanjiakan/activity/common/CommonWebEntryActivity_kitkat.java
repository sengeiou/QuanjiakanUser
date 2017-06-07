package com.androidquanjiakan.activity.common;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.androidquanjiakan.util.BitmapUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.SignatureUtil;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.quanjiakanuser.util.ImageUtils;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;


public class CommonWebEntryActivity_kitkat extends BaseActivity implements OnClickListener {

    private ImageButton back;
    private TextView tv_title;

    private ProgressBar progress;
    private SwipeRefreshLayout refresh;
    private String param_url;
    private String last_url;
    private String specificTitle;
    private String bind_json;
    private String device_id;

    private WebView mWebView;

    private String url;//拍照后返回的图片路径
    private final Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //此处将拍照或选择相册返回的url传给js
                    Log.e("debug", "-----pic----" + url);
                    mWebView.loadUrl("javascript:acceptUrl('" + url + "')");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common_web);
        param_url = getIntent().getStringExtra(BaseConstants.PARAMS_URL);
        bind_json = getIntent().getStringExtra(BaseConstants.PARAMS_DATA);
        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_ID);
        specificTitle = getIntent().getStringExtra(BaseConstants.PARAMS_TITLE);//TODO 自定义的标题
        if (param_url == null || device_id == null || bind_json == null) {
            BaseApplication.getInstances().toast(CommonWebEntryActivity_kitkat.this, getResources().getString(R.string.error_params));
            finish();
        }

        initTitle();
        initView();
        initWebView();
    }

    public void initTitle() {
        back = (ImageButton) findViewById(R.id.ibtn_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        if (specificTitle != null && specificTitle.length() > 0) {
            tv_title.setText(specificTitle);
        } else {
            tv_title.setText(getString(R.string.title_web));
        }
    }

    protected void initView() {
        last_url = null;

        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5, 0));
        progress.setMax(100);

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setColorSchemeResources(R.color.holo_green_light);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
                if (last_url != null) {
                    mWebView.loadUrl(last_url);
                } else {
                    mWebView.loadUrl(param_url);
                }

            }
        });

    }

    private final Handler mHandler = new Handler();

    public void initWebView() {

        mWebView = (WebView) findViewById(R.id.webview);


        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

        mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setLightTouchEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);

        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        mWebView.loadUrl(param_url + "&version=1");
        LogUtil.e("------------kit2-----------------");

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    mWebView.clearCache(true);
                    progress.setVisibility(View.INVISIBLE);
                } else {
                    // 加载中
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(newProgress);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        //注册js接口 JSInterface是自定义的类 里面放的方法必须与js中的方法一致 参数二也是与js协定的，必须与js保持一致
        mWebView.addJavascriptInterface(new JSInterface(), "hgj");
//        mWebView.addJavascriptInterface(new Object() {
//            @JavascriptInterface
//            public void clickOnAndroid() {
//
//                mHandler.post(new Runnable() {
//
//                    public void run() {
//
//                        showImageDialog();
//
//                    }
//
//                });
//
//            }
//
//        }, "jugui");




        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//                http://static.quanjiakan.com/familycare-static/dashboard/activate_success.html  激活成功后的页面
                last_url = url;
                if (url.contains("activate_success.html")) {
                    BaseApplication.getInstances().toast(CommonWebEntryActivity_kitkat.this, "成功");
                    if (!BaseApplication.getInstances().isSDKConnected()) {
                        BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "已与手表服务器断开连接");
                    } else {
                        long devid = Long.parseLong(device_id, 16);
                        int size = bind_json.getBytes().length;
                        BaseApplication.getInstances().getNattyClient().ntyBindClient(devid, bind_json.getBytes(), size);

                    }
                }else if (url.contains("hgj://take/photo")){
                    showImageDialog();
                    return true;
                }
                view.loadUrl(url);


                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });


    }

    private String mLastNetPath = null;
    private String mCurrentPhotoPath = null;

    private Dialog imageDialog;

    public void showImageDialog() {
        imageDialog = new Dialog(this, R.style.AlbumDialogStyle);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_modify_info_red, null);

        TextView camera = (TextView) view.findViewById(R.id.camera);
        camera.setVisibility(View.VISIBLE);
        camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageDialog != null) {
                    imageDialog.dismiss();
                }
                //从相机拍摄照片
                ImageCropHandler.getImageFromCamera(CommonWebEntryActivity_kitkat.this, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        LogUtil.e("获取从照相机拍摄的照片路径:" + path);
                    }
                });
            }
        });

        TextView album = (TextView) view.findViewById(R.id.album);
        album.setVisibility(View.VISIBLE);
        album.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageDialog != null) {
                    imageDialog.dismiss();
                }
                //从相册选取照片
                ImageCropHandler.pickImage(CommonWebEntryActivity_kitkat.this);
            }
        });

        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setVisibility(View.VISIBLE);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageDialog != null) {
                    imageDialog.dismiss();
                }
            }
        });

        WindowManager.LayoutParams lp = imageDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        imageDialog.setContentView(view, lp);
        imageDialog.show();
    }

    private Bitmap temp;
    private String sourcePath;

    public void save2SmallImage(Uri data) {
        if (data == null) {
            return;
        }
        sourcePath = ImageUtils.uri2Path(data, CommonWebEntryActivity_kitkat.this);
        String smallImagePath = ImageUtils.saveBitmapToStorage(SignatureUtil.getMD5String(sourcePath) + ".jpg", BitmapUtil.getSmallBitmap(sourcePath));
        if (temp != null) {
            temp.recycle();
            temp = null;
        }
        temp = BitmapFactory.decodeFile(smallImagePath);
    }

    private final int REQUEST_INFO = 1024;

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final String IMAGE_FILE_NAME = "qjk" + String.valueOf(System.currentTimeMillis()).substring(4) + ".jpg";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(CommonWebEntryActivity_kitkat.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA:
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        ImageCropHandler.beginCrop(CommonWebEntryActivity_kitkat.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                    }
                }
                break;
            case CommonRequestCode.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    ImageCropHandler.beginCrop(CommonWebEntryActivity_kitkat.this, data.getData());
                }
            case CommonRequestCode.REQUEST_CROP:
                ImageCropHandler.handleCrop(null, resultCode, data, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        Bitmap smallBitmap = BitmapUtil.getSmallBitmap(mCurrentPhotoPath);
                        String filename = System.currentTimeMillis() + ".png";
                        String file_path = ImageUtils.saveBitmapToStorage(filename, smallBitmap);
                        uploadImage(file_path, filename);
                    }
                });
                break;
        }
    }


    protected void uploadImage(final String path, String filename) {
        HashMap<String, String> params = new HashMap<>();
        params.put("file", path.toString());
        params.put("filename", filename);
        params.put("image", path);
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                if (jsonObject.has("code") && "200".equals(jsonObject.get("code").getAsString())) {
                    String strImag = jsonObject.get("message").getAsString();
                    mLastNetPath = strImag;
                    upload(strImag);
                } else {
                    Toast.makeText(CommonWebEntryActivity_kitkat.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                }
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }

                File file2 = new File(mCurrentPhotoPath);
                if (file2.exists()) {
                    file2.delete();
                }
            }
        }, HttpUrls.postFile() + "&storage=12", params, Task.TYPE_POST_FILE, QuanjiakanDialog.getInstance().getDialog(CommonWebEntryActivity_kitkat.this, "正在上传图片！")));
    }

    private void upload(String path) {
        //此处可以写入上传图片的方法  这里就直接将拍照和选择相册得到的本地路径返回
        url = path;
        Message msg = new Message();
        msg.what = 0;
        mHandler1.sendMessage(msg);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        if (id == R.id.ibtn_back) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                Intent intent = new Intent();
                intent.putExtra(BaseConstants.PARAMS_BIND_RESULT, received_result);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == keyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, keyEvent);

    }


    private String received_result = null;

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonNattyData(CommonNattyData msg) {
        switch (msg.getType()) {
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_RESULT: {
                //需要自己根据发送的命令去判断
                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_CONTROL_BIND_RESULT:
                //0：代表成功，1：代表 UserId不存在，2：代表DeviceId不存在，3：代表UserId与DeviceId已经绑定过了  4: 设备未激活  5:管理员
                String res = msg.getData();
//                if (count > 0) {//控制多次收到信息的情况
//                    break;
//                }
                if ("0".equals(res)) {
                    /**
                     * 确认绑定成功后
                     */
                } else if ("1".equals(res)) {
                } else if ("2".equals(res)) {
                } else if ("3".equals(res)) {
                } else if ("4".equals(res)) {
                } else if ("5".equals(res)) {//第一个绑定
//                    Intent intent = new Intent(this, DevicesBindStateActivity.class);
//                    intent.putExtra(BaseConstants.PARAMS_STATE, DevicesBindStateActivity.ACCESS);
//                    intent.putExtra(BaseConstants.PARAMS_ID, device_id);
//                    startActivityForResult(intent, CommonRequestCode.REQUEST_STATE);
//                    BaseApplication.getInstances().toastLong(this, "设备绑定成功,您已成为管理员!");
                    received_result = "5";
                } else {
                }
                break;
        }
    }

    private class JSInterface {

        @JavascriptInterface
        public void acceptUrl(String imgUrl) {//此方法是将android端获取的url返给js

        }
    }
}
