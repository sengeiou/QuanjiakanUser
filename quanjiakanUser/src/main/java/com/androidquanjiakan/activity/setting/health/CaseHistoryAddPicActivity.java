package com.androidquanjiakan.activity.setting.health;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.setting.modify_info.ModifyInfoActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.androidquanjiakan.util.BitmapUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.SignatureUtil;
import com.androidquanjiakan.view.CircleTransformation;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.quanjiakanuser.util.ImageUtils;
import com.soundcloud.android.crop.util.Log;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CaseHistoryAddPicActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title;
    private ImageButton ibtn_back;
    private TextView menu_text;
    private TextView publish_time;
    private ImageView headimg;
    private EditText input;
    private TextView tv_book;
    private Bitmap temp;

    private Dialog dialog;

    private String sourcePath;
    private String mLastNetPath;
    private String mCurrentPhotoPath;
    private String device_id;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_health_document_add);
        initTitleBar();
        initView();
    }

    public void initTitleBar() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("添加");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        menu_text = (TextView) findViewById(R.id.menu_text);
        menu_text.setText("档案");
        menu_text.setVisibility(View.GONE);
        menu_text.setOnClickListener(this);
    }

    /**
     * TODO 网络获取数据-----------病历图像
     */
    private Date publishData;

    private void initView() {
        publishData = new Date();
        publish_time = (TextView) findViewById(R.id.publish_time);
        publish_time.setText("发布时间： " + sdf.format(publishData));

        headimg = (ImageView) findViewById(R.id.headimg);
        headimg.setOnClickListener(this);

        input = (EditText) findViewById(R.id.input);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s != null && s.length() > 100) {
                    BaseApplication.getInstances().toast(CaseHistoryAddPicActivity.this,"输入字数超出100限制!");
                    s = s.subSequence(0, 100);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tv_book = (TextView) findViewById(R.id.tv_book);
        tv_book.setOnClickListener(this);
    }

    public void getNetData(int page) {
//        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
//            @Override
//            public void handMsg(String val) {
//                if(val!=null && !"[]".endsWith(val) && val.length()>2){
//                    entity = (List<BindUserCaseHistoryEntity>) SerialUtil.jsonToObject(val, new TypeToken<List<BindUserCaseHistoryEntity>>() {
//                    }.getType());
//                    setNetData();
//                }
//            }
//        }, HttpUrls.getHealthData_CaseHistory(device_id), null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(CaseHistoryPicActivity.this, "获取数据中...")));
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

                break;
            case R.id.headimg:
                showOptionsDialog();
                break;
            case R.id.tv_book:
                //
                submitData();
                break;
            default:
                break;
        }
    }

    protected void showOptionsDialog() {
        List<String> strings = new ArrayList<>();
        //TODO 提示顺序与操作顺序不一致
        strings.add(getString(R.string.common_album));
        strings.add(getString(R.string.common_shot));
        QuanjiakanDialog.getInstance().getListDialogDefineHeight(CaseHistoryAddPicActivity.this, strings, getString(R.string.common_album_select), new QuanjiakanDialog.MyDialogCallback() {

            @Override
            public void onItemClick(int position, Object object) {
                // TODO Auto-generated method stub
                switch (position) {
                    case 0:
                        ImageCropHandler.pickImage(CaseHistoryAddPicActivity.this);
                        break;
                    case 1:
                        ImageCropHandler.getImageFromCamera(CaseHistoryAddPicActivity.this, new IImageCropInterface() {
                            @Override
                            public void getFilePath(String path) {
                                mCurrentPhotoPath = path;
                            }
                        });
                        break;
                }
            }
        }, null);
    }

    public void save2SmallImage(Uri data) {
        if (data == null) {
            return;
        }
        sourcePath = ImageUtils.uri2Path(data, CaseHistoryAddPicActivity.this);
        String smallImagePath = ImageUtils.saveBitmapToStorage(SignatureUtil.getMD5String(sourcePath) + ".jpg", BitmapUtil.getSmallBitmap(sourcePath));
        if (temp != null) {
            temp.recycle();
            temp = null;
        }
        temp = BitmapFactory.decodeFile(smallImagePath);
    }

    public void submitData() {
        if(mLastNetPath==null){
            Toast.makeText(this, getString(R.string.hint_add_pic_data), Toast.LENGTH_SHORT).show();
            return;
        }

        if(input.getText()==null || input.getText().toString().length()<1){
            Toast.makeText(this, getString(R.string.hint_add_pic_desc), Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", BaseApplication.getInstances().getUser_id());
        params.put("medical_name", input.getText().toString());
        params.put("medical_record", mLastNetPath);//
        params.put("deviceid", 0+"");//
        Task task = new Task(new HttpResponseInterface() {

            @Override
            public void handMsg(String val) {
                if(dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
                // TODO Auto-generated method stub
                if (val != null && !val.equals("") && val.toLowerCase().startsWith("{")) {
                    //上传文件成功
                    uploadStatus = true;
                    try {
                        json = new JSONObject(val);
                        if (json.has("code") && "200".equals(json.getString("code"))) {
                            Toast.makeText(CaseHistoryAddPicActivity.this, getString(R.string.hint_add_success), Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }else{
                            Toast.makeText(CaseHistoryAddPicActivity.this, getString(R.string.hint_add_fail_and_retry), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(CaseHistoryAddPicActivity.this, "接口无数据返回，请重试!", Toast.LENGTH_SHORT).show();
                }
            }
        }, HttpUrls.addUserHealthDocument(), params, Task.TYPE_POST_DATA_PARAMS, null);
        MyHandler.putTask(CaseHistoryAddPicActivity.this, task);
    }

    private boolean uploadStatus = false;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final String IMAGE_FILE_NAME = "qjk" + String.valueOf(System.currentTimeMillis()).substring(4) + ".jpg";
    private JSONObject json;
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
                            save2SmallImage(u);
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(CaseHistoryAddPicActivity.this, getString(R.string.error_no_sdcard_for_save), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA:
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        ImageCropHandler.beginCrop(CaseHistoryAddPicActivity.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                    }
                }
                break;
            case CommonRequestCode.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    ImageCropHandler.beginCrop(CaseHistoryAddPicActivity.this, data.getData());
                }
            case CommonRequestCode.REQUEST_CROP:
                ImageCropHandler.handleCrop(null, resultCode, data, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        Bitmap smallBitmap = BitmapUtil.getSmallBitmap(mCurrentPhotoPath,800,800);
                        String filename = System.currentTimeMillis() + ".png";
                        String file_path = ImageUtils.saveBitmapToStorage(filename, smallBitmap);
                        uploadImage(headimg,file_path, filename);
                    }
                });
                break;
            default:
                break;
        }
    }

    protected void uploadImage(final ImageView head_image,final String path, String filename) {
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
                    uploadStatus = true;
                    if(head_image!=null){
                        head_image.setTag(strImag);
                        //方形铺满
                        Picasso.with(getApplicationContext()).load(strImag).fit()
                                .into(head_image);
                    }
                } else {
                    Toast.makeText(CaseHistoryAddPicActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
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
        }, HttpUrls.postFile() + "&storage=2", params, Task.TYPE_POST_FILE, QuanjiakanDialog.getInstance().getDialog(CaseHistoryAddPicActivity.this, "正在上传图片！")));
    }

    /**
     * 上传档案信息
     *
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
