package com.androidquanjiakan.activity.common;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.bind.DevicesBindActivity;
import com.androidquanjiakan.activity.index.bind.DevicesBindStateActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.datahandler.BindDeviceHandler;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.util.LogUtil;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.net.URLEncoder;


/**
 * 通用网页浏览入口
 */
public class CommonWebEntryActivity_activate extends BaseActivity implements OnClickListener {

    private ImageButton back;
    private TextView tv_title;
    private WebView mWebview;
    private ProgressBar progress;
    private SwipeRefreshLayout refresh;
    private String param_url;
    private String last_url;
    private String specificTitle;
    private String bind_json;
    private String device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common_web);
        param_url = getIntent().getStringExtra(BaseConstants.PARAMS_URL);
        bind_json = getIntent().getStringExtra(BaseConstants.PARAMS_DATA);
        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_ID);
        specificTitle = getIntent().getStringExtra(BaseConstants.PARAMS_TITLE);//TODO 自定义的标题
        if (param_url == null || device_id == null || bind_json == null) {
            BaseApplication.getInstances().toast(CommonWebEntryActivity_activate.this, getResources().getString(R.string.error_params));
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
                    mWebview.loadUrl(last_url);
                } else {
                    mWebview.loadUrl(param_url);
                }

            }
        });

    }

    public void initWebView() {

        mWebview = (WebView) findViewById(R.id.webview);

        mWebview.getSettings().setJavaScriptEnabled(true);

        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.getSettings().setLoadWithOverviewMode(true);

        mWebview.getSettings().setSupportZoom(true);
        mWebview.getSettings().setBuiltInZoomControls(true);
        mWebview.getSettings().setDisplayZoomControls(false);

        mWebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

        mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.getSettings().setLightTouchEnabled(true);
        mWebview.getSettings().setDomStorageEnabled(true);
        mWebview.getSettings().setDatabaseEnabled(true);

        mWebview.getSettings().setAllowFileAccess(true);
        mWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        mWebview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    mWebview.clearCache(true);
                    progress.setVisibility(View.INVISIBLE);
                } else {
                    // 加载中
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(newProgress);
                }
            }

            //************     由于4.4WebView被系统禁止调用，需要使用JS互相调用的形式进行传递
            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

            }

            //For Android 5.0
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                // make sure there is no existing message
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                uploadMessage = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    return false;
                }
                return true;
            }

        });

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//                http://static.quanjiakan.com/familycare-static/dashboard/activate_success.html  激活成功后的页面
                last_url = url;
                if (url.contains("activate_success.html")) {
                    if (!BaseApplication.getInstances().isSDKConnected()) {
                        BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "已与手表服务器断开连接");
                    } else {
                        LogUtil.e("-------bindjson--------"+bind_json);
                        long devid = Long.parseLong(device_id, 16);
                        int size = bind_json.getBytes().length;
                        BaseApplication.getInstances().getNattyClient().ntyBindClient(devid, bind_json.getBytes(), size);

                    }
                }
                return false;//解决跳转第三方页面重定向问题，返回false交给webview处理
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

//		mWebview.addJavascriptInterface();

        mWebview.loadUrl(param_url);
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
            if (mWebview.canGoBack()) {
                mWebview.goBack();
            } else {
                Intent intent = new Intent();
                intent.putExtra(BaseConstants.PARAMS_BIND_RESULT,received_result);
                setResult(RESULT_OK,intent);
                finish();
            }
        }
    }

    //	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(event.getAction()==KeyEvent.KEYCODE_BACK){
//			if(mWebview.canGoBack()){
//				mWebview.goBack();//TODO 当网页可以回退时，进行回退操作
//			}else{
//				setResult(RESULT_OK);
//				finish();
//			}
//
//		}
//		return super.onKeyDown(keyCode, event);
//	}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == keyEvent.KEYCODE_BACK) {
            if (mWebview.canGoBack()) {
                mWebview.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, keyEvent);

    }


    public static final int REQUEST_SELECT_FILE = 100;
    public final static int FILECHOOSER_RESULTCODE = 1;
    public ValueCallback<Uri[]> uploadMessage;
    public ValueCallback<Uri> mUploadMessage;


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
            LogUtil.e("------------result4.0---------" + result);

        } else if (requestCode == REQUEST_SELECT_FILE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (uploadMessage == null) return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
                LogUtil.e("------------result5.0---------" + intent.getData());
            }
        }
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
}
