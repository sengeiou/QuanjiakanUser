package com.androidquanjiakan.activity.index.watch_child;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquanjiakan.activity.common.ImageViewerActivity;
import com.androidquanjiakan.adapter.HealthDocumentAdapter;
import com.androidquanjiakan.adapter.WatchHealthDocumentAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.entity.HealthDocumentEntity;
import com.androidquanjiakan.entity.WatchCaseHistoryEntity;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class WatchCaseHistoryPicActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title;
    private ImageButton ibtn_back;
    private TextView menu_text;

    private String mCurrentPhotoPath;
    private Dialog dialog;

    private PullToRefreshListView dataList;
    private WatchHealthDocumentAdapter mAdapter;
    private List<WatchCaseHistoryEntity> mData;

    public static final String PARAMS_DEVICEIS = "deviceid";

    public static final int REQUEST_VIEWER = 1001;
    public static final int REQUEST_ADD = 1002;
    public String device_id;

    private int pageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_health_document_list);
        device_id = getIntent().getStringExtra(PARAMS_DEVICEIS);
        if(device_id==null || device_id.length()<1){
            BaseApplication.getInstances().toast(WatchCaseHistoryPicActivity.this,"传入参数异常!");
            finish();
            return;
        }
        initTitleBar();
        initView();
    }

    private ImageView nonedata;
    private TextView nonedatahint;
    public void showNoneData(boolean isShow){
        if(isShow){
            nonedatahint.setText("暂无数据");
            nonedata.setVisibility(View.VISIBLE);
            nonedatahint.setVisibility(View.VISIBLE);
        }else{
            nonedata.setVisibility(View.GONE);
            nonedatahint.setVisibility(View.GONE);
        }
    }

    public void initTitleBar(){

        nonedata = (ImageView) findViewById(R.id.nonedata);
        nonedatahint = (TextView) findViewById(R.id.nonedatahint);
        showNoneData(false);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("健康档案");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        menu_text = (TextView) findViewById(R.id.menu_text);
        menu_text.setText("添加");
        menu_text.setVisibility(View.VISIBLE);
        menu_text.setOnClickListener(this);
    }

    public void getNetData(final int page) {
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(val!=null && val.length()>0){
                    JsonArray jsonObject = new GsonParseUtil(val).getJsonArray();
                    if(jsonObject!=null && jsonObject.size()>0){
                        List<WatchCaseHistoryEntity> temp = (List<WatchCaseHistoryEntity>)SerialUtil.jsonToObject(val,new TypeToken<List<WatchCaseHistoryEntity>>(){}.getType());
                        if(temp!=null && temp.size()>0){
                            if(page==1){
                                mAdapter.getData().clear();
                            }
//                            mData.addAll(temp);//TODO 直接保存

                            //TODO 倒序排列
//                            for(WatchCaseHistoryEntity item:temp){
//                                mData.add(0,item);
//                            }
                            mData.addAll(temp);
                            Collections.sort(mData, new Comparator<WatchCaseHistoryEntity>() {
                                @Override
                                public int compare(WatchCaseHistoryEntity lhs, WatchCaseHistoryEntity rhs) {
                                    long left = Long.parseLong(lhs.getCreatetime().replace("-","").replace(" ","").replace(".","").replace(":",""));
                                    long right = Long.parseLong(rhs.getCreatetime().replace("-","").replace(" ","").replace(".","").replace(":",""));
                                    if(left<right){
                                        return 1;
                                    }else if(left==right){
                                        return 0;
                                    }else{
                                        return -1;
                                    }
                                }
                            });

                            if(temp!=null && temp.size()==10){
                                dataList.setMode(PullToRefreshBase.Mode.BOTH);
                            }else{
                                dataList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }
                            mAdapter.notifyDataSetChanged();
                        }else{
                            dataList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }
                    }else{
                        dataList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }

                    if(mData!=null && mData.size()>0){
                        showNoneData(false);
                    }else{
                        showNoneData(true);
                    }
                    //
                }else{
                    if(!NetUtil.isNetworkAvailable(BaseApplication.getInstances())){
//                        BaseApplication.getInstances().toast(WatchCaseHistoryPicActivity.this,"网络连接不可用!");
                    }else{
                        BaseApplication.getInstances().toast(WatchCaseHistoryPicActivity.this,"获取数据失败，请刷新重试!");
                    }

                    if(mData!=null && mData.size()>0){
                        showNoneData(false);
                    }else{
                        showNoneData(true);
                    }
                }

                dataList.onRefreshComplete();
            }
        }, HttpUrls.getWatchHealthData_User()+"&deviceid="+device_id+"&page="+page+"&rows=10", null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(WatchCaseHistoryPicActivity.this, "获取数据中...")));
    }

    /**
     * TODO 网络获取数据-----------病历图像
     */
    private void initView() {
//        this.listview = (PullToRefreshListView)findViewById(R.id.listview);
        dataList = (PullToRefreshListView) findViewById(R.id.dataList);
        dataList.setMode(PullToRefreshBase.Mode.BOTH);
        dataList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageIndex = 1;
                getNetData(1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageIndex++;
                getNetData(pageIndex);
            }
        });
        mData = new ArrayList<>();
        mAdapter = new WatchHealthDocumentAdapter(WatchCaseHistoryPicActivity.this,mData);
        dataList.setAdapter(mAdapter);
        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO 由于在图片上已经设置了点击事件，所以暂时将这里屏蔽，若需求变更，将图片的点击事件屏蔽，将这里放开
//                int itemPosition = (int)id;
//                Intent intent = new Intent(WatchCaseHistoryPicActivity.this, ImageViewerActivity.class);
//                intent.putExtra(BaseConstants.PARAMS_URL,mData.get(itemPosition).getMedical_record());
//                startActivity(intent);
            }
        });

        pageIndex = 1;
        getNetData(1);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.menu_text:
                Intent intent = new Intent(WatchCaseHistoryPicActivity.this,WatchCaseHistoryAddPicActivity.class);
                intent.putExtra(PARAMS_DEVICEIS,device_id);
                startActivityForResult(intent,REQUEST_ADD);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADD:
                //重新获取数据
                pageIndex = 1;
                getNetData(pageIndex);
                break;
            case REQUEST_VIEWER:
                if (resultCode == RESULT_OK) {
                }
                break;
            case CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA:
                /**
                 * TODO 拍完图片后上传，展示到界面上，同时添加到病历列表中
                 */
                if (resultCode == RESULT_OK && mCurrentPhotoPath != null) {
                    ImageCropHandler.beginCrop(WatchCaseHistoryPicActivity.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                }
                break;
            case CommonRequestCode.REQUEST_CROP:
                ImageCropHandler.handleCrop(null, resultCode, data, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        dialog = QuanjiakanDialog.getInstance().getDialog(WatchCaseHistoryPicActivity.this);
                        final File file = new File(mCurrentPhotoPath);
                        HashMap<String, String> params = new HashMap<>();
                        params.put("file", mCurrentPhotoPath.toString());
                        params.put("filename", file.getName());
                        params.put("image", file.getAbsolutePath());
                        Task task = new Task(new HttpResponseInterface() {

                            @Override
                            public void handMsg(String val) {
                                dialog.dismiss();
                                if (!val.equals("")) {
                                    //上传文件成功
                                    try {
                                        JSONObject json = new JSONObject(val);
                                        if (json.has("code") && "200".equals(json.getString("code"))) {
                                            uploadCaseHistoryInfo(json.getString("message"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    BaseApplication.getInstances().toast(WatchCaseHistoryPicActivity.this,"上传失败，请重试!");
                                }
                                if(file.exists()){
                                    file.delete();
                                }
                            }
                        }, HttpUrls.postFile()+"&storage=15", params, Task.TYPE_POST_FILE, null);
                        MyHandler.putTask(WatchCaseHistoryPicActivity.this,task);
                    }
                });
            default:
                break;
        }
    }

    /**
     * 上传档案信息
     * @param urlPath
     */
    public void uploadCaseHistoryInfo(final String urlPath) {
    }
}
