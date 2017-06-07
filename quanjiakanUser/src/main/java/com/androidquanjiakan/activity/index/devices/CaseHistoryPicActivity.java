package com.androidquanjiakan.activity.index.devices;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidquanjiakan.adapter.ImageSelectAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.entity.BindUserCaseHistoryEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.androidquanjiakan.entity.ImageSelectEntity;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CaseHistoryPicActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title;
    private ImageButton ibtn_back;
    private GridView grid;
    private TextView menu_text;

    private List<ImageSelectEntity> data;
    private ImageSelectAdapter adapter;
    private String mCurrentPhotoPath;
    private List<BindUserCaseHistoryEntity> entity;
    private Dialog dialog;

    public static final int REQUEST_VIEWER = 1001;
    public static final String PARAMS_DEVICEIS = "params_device_id";
    public String device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_case_history_list);
        device_id = getIntent().getStringExtra(PARAMS_DEVICEIS);
        if (CheckUtil.isEmpty(device_id)) {
            finish();
            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"传入参数异常!");
            return;
        }
        initTitleBar();
        initGrid();
        getNetData();
    }

    public void initTitleBar(){
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
    private void initGrid() {

        grid = (GridView) findViewById(R.id.grid);
        data = new ArrayList<ImageSelectEntity>();
        adapter = new ImageSelectAdapter(this, data, false, false);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /**
                 * 展示确定的某一个图片（是否需要进行缩放）
                 */
                Intent intent = new Intent(CaseHistoryPicActivity.this, CaseHistoryPicViewerActivity.class);
                intent.putExtra(BaseConstants.PARAMS_URL, adapter.getData().get(i).getImgUrl());
                intent.putExtra(BaseConstants.PARAMS_POSITION, i);
                startActivityForResult(intent, REQUEST_VIEWER);
            }
        });
    }

    public void getNetData() {
        /**
         * 网络获取数据，得到病历
         *
         * 获取手表档案列表
         platform=2&m=watch&a=getwatchuserinfo
         user_id  用户id
         deviceid 设备id


            ************************上传接口
         platform=2&m=watch&a=addMedicalRecord

         user_id
         deviceid
         medical_record  图片地址
         medical_name  图片描述
         */
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(val!=null && !"[]".endsWith(val) && val.length()>2){
                    entity = (List<BindUserCaseHistoryEntity>) SerialUtil.jsonToObject(val, new TypeToken<List<BindUserCaseHistoryEntity>>() {
                    }.getType());
                    setNetData();
                }
            }
        }, HttpUrls.getHealthData_CaseHistory(device_id), null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(CaseHistoryPicActivity.this, "获取数据中...")));
    }

    public void setNetData() {
        if (entity != null &&
                entity.size()>0) {
            adapter.getData().clear();
            for(BindUserCaseHistoryEntity tempData: entity){
                if(device_id.equals(tempData.getDeviceid())){
                    ImageSelectEntity temp = new ImageSelectEntity();
                    temp.setImgUrl(tempData.getMedical_record());
                    adapter.getData().add(temp);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    protected void initView() {

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.menu_text:
                mCurrentPhotoPath = null;
                ImageCropHandler.getImageFromCamera(CaseHistoryPicActivity.this, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_VIEWER:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra(BaseConstants.PARAMS_POSITION, -1);
                    if (position >= 0) {
                        adapter.getData().remove(position);
                        adapter.notifyDataSetChanged();
                    } else {
                        BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"返回参数异常!");
                    }
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
                    public void getFilePath(final String path) {
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
                                        if(file.exists()){
                                            file.delete();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"上传失败，请重试!");
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

    public void uploadCaseHistoryInfo(final String urlPath) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("deviceid", device_id);
        params.put("medical_record", urlPath);
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                ImageSelectEntity entity = new ImageSelectEntity();
                entity.setImgUrl(urlPath);
                adapter.getData().add(entity);
                adapter.notifyDataSetChanged();
            }
        }, HttpUrls.getHealthData_UploadCaseHistory(), params, Task.TYPE_POST_FILE, QuanjiakanDialog.getInstance().getDialog(CaseHistoryPicActivity.this)));
    }
}
