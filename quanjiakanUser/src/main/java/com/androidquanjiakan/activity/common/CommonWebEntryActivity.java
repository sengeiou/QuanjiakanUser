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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.util.LogUtil;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;


/**
 * 通用网页浏览入口
 */
public class CommonWebEntryActivity extends BaseActivity implements OnClickListener {

    private ImageButton back;
    private TextView tv_title;
    private WebView mWebview;
    private ProgressBar progress;
    private SwipeRefreshLayout refresh;
    private String param_url;
    private String last_url;
    private String specificTitle;

    private ImageView nonedata;
    private TextView nonedatahint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common_web_new);
        param_url = getIntent().getStringExtra(BaseConstants.PARAMS_URL);
        specificTitle = getIntent().getStringExtra(BaseConstants.PARAMS_TITLE);//TODO 自定义的标题
        if (param_url == null) {
            BaseApplication.getInstances().toast(CommonWebEntryActivity.this, getResources().getString(R.string.error_params));
            finish();
        }

        initTitle();
        initView();


        initWebView();
    }

    public void initTitle() {

        nonedata = (ImageView) findViewById(R.id.nonedata);
        nonedatahint = (TextView) findViewById(R.id.nonedatahint);


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

    public void showNoneData(boolean isShow){
        if(isShow){
            nonedatahint.setText("无法联网");
            nonedata.setVisibility(View.VISIBLE);
            nonedatahint.setVisibility(View.VISIBLE);
        }else{
            nonedata.setVisibility(View.GONE);
            nonedatahint.setVisibility(View.GONE);
        }
    }

    protected void initView() {
        last_url = null;

        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5,0));
        progress.setMax(100);

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setColorSchemeResources(R.color.holo_green_light);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);

                if(!NetUtil.isNetworkAvailable(CommonWebEntryActivity.this)){
                    return;
                }
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
                LogUtil.e("---------url-----"+url);
				last_url = url;
                if (last_url!=null&&last_url.contains("goods.php?id=301")) {  //这个网页存在问题特殊处理
                    refresh.setEnabled(false);
                }else {
                    refresh.setEnabled(true);
                }
//				view.loadUrl(last_url);
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
        if(NetUtil.isNetworkAvailable(this)){
            mWebview.loadUrl(param_url);
            showNoneData(false);
        }else{
            mWebview.setVisibility(View.GONE);
            showNoneData(true);
        }

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
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        if (id == R.id.ibtn_back) {
            if (mWebview.canGoBack()) {
                mWebview.goBack();
            } else {
                setResult(RESULT_OK);
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
            LogUtil.e("------------result4.0---------"+result);

        } else if (requestCode == REQUEST_SELECT_FILE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (uploadMessage == null) return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
                LogUtil.e("------------result5.0---------"+intent.getData());
            }
        }
    }
}
