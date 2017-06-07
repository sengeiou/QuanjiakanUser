package com.androidquanjiakan.activity.setting.health;

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

import com.androidquanjiakan.adapter.HealthDocumentAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.entity.HealthDocumentEntity;
import com.androidquanjiakan.entity.HealthDocumentResult;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.google.gson.JsonObject;
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

public class CaseHistoryPicActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title;
    private ImageButton ibtn_back;
    private TextView menu_text;

    private String mCurrentPhotoPath;
    private Dialog dialog;

    private PullToRefreshListView dataList;
    private HealthDocumentAdapter mAdapter;
    private List<HealthDocumentEntity> mData;

    public static final int REQUEST_VIEWER = 1001;
    public static final int REQUEST_ADD = 1002;
    public String device_id;

    private int pageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_health_document_list);
        initTitleBar();
        initView();
        showNoneData(false);
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


        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("档案");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        menu_text = (TextView) findViewById(R.id.menu_text);
        menu_text.setText("添加");
        menu_text.setVisibility(View.VISIBLE);
        menu_text.setOnClickListener(this);
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
        mAdapter = new HealthDocumentAdapter(CaseHistoryPicActivity.this,mData);
        dataList.setAdapter(mAdapter);
        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO 由于在图片上已经设置了点击事件，所以暂时将这里屏蔽，若需求变更，将图片的点击事件屏蔽，将这里放开
//                int itemPosition = (int)id;
//                Intent intent = new Intent(CaseHistoryPicActivity.this, ImageViewerActivity.class);
//                intent.putExtra(BaseConstants.PARAMS_URL,mData.get(itemPosition).getMedical_record());
//                startActivity(intent);
            }
        });

        pageIndex = 1;
        getNetData(1);
    }

    public void getNetData(final int page) {
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                //{"code":"200","list":[{"createtime":"2016-11-26 14:56:59.0","deviceid":"0","id":23,"user_id":11108}],"message":"返回成功","total":1}
                if(val!=null && val.length()>0){
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if(jsonObject.has("code") && "200".equals(jsonObject.get("code").getAsString())){
                        HealthDocumentResult temp = (HealthDocumentResult)SerialUtil.jsonToObject(val,new TypeToken<HealthDocumentResult>(){}.getType());
                        if(temp!=null && temp.getRows()!=null){
                            if(page==1){
                                mAdapter.getData().clear();
                            }
                            mData.addAll(temp.getRows());
                            Collections.sort(mData, new Comparator<HealthDocumentEntity>() {
                                @Override
                                public int compare(HealthDocumentEntity lhs, HealthDocumentEntity rhs) {
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
                            if(temp!=null && temp.getRows().size()==10){
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
//                        BaseApplication.getInstances().toast("网络连接不可用!");
                    }else{
                        BaseApplication.getInstances().toast(CaseHistoryPicActivity.this,"获取数据失败，请刷新重试!");
                    }

                    if(mData!=null && mData.size()>0){
                        showNoneData(false);
                    }else{
                        showNoneData(true);
                    }
                }

                dataList.onRefreshComplete();
            }
        }, HttpUrls.getHealthData_User()+"&page="+page+"&rows=10", null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(CaseHistoryPicActivity.this, "获取数据中...")));
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
                Intent intent = new Intent(CaseHistoryPicActivity.this,CaseHistoryAddPicActivity.class);
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
                    ImageCropHandler.beginCrop(CaseHistoryPicActivity.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                }
                break;
            case CommonRequestCode.REQUEST_CROP:
                ImageCropHandler.handleCrop(null, resultCode, data, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        dialog = QuanjiakanDialog.getInstance().getDialog(CaseHistoryPicActivity.this);
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
                                    BaseApplication.getInstances().toast(CaseHistoryPicActivity.this,"上传失败，请重试!");
                                }
                                if(file.exists()){
                                    file.delete();
                                }
                            }
                        }, HttpUrls.postFile()+"&storage=15", params, Task.TYPE_POST_FILE, null);
                        MyHandler.putTask(CaseHistoryPicActivity.this,task);
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
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("deviceid", device_id);
//        params.put("medical_record", urlPath);
//        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
//            @Override
//            public void handMsg(String val) {
//                ImageSelectEntity entity = new ImageSelectEntity();
//                entity.setImgUrl(urlPath);
//                adapter.getData().add(entity);
//                adapter.notifyDataSetChanged();
//            }
//        }, HttpUrls.getHealthData_UploadCaseHistory(), params, Task.TYPE_POST_FILE, QuanjiakanDialog.getInstance().getDialog(CaseHistoryPicActivity.this)));
    }
}
