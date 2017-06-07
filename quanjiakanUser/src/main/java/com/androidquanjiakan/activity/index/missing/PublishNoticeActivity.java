package com.androidquanjiakan.activity.index.missing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.dialog.SelectMissingTimeDialog;
import com.androidquanjiakan.dialog.Select_Time_Dialog;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.EditTextFilter;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.BitmapUtil;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.DateUtils;
import com.quanjiakanuser.util.GsonParseUtil;
import com.quanjiakanuser.util.ImageUtils;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发布寻人启事
 * Created by Administrator on 2016/11/18.
 */

public class PublishNoticeActivity extends BaseActivity {

    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.menu_text)
    TextView menuText;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.et_name_missing)  //姓名
            EditText etNameMissing;
    @BindView(R.id.radioMale)  //男
            RadioButton radioMale;
    @BindView(R.id.radioFemale)//女
            RadioButton radioFemale;
    @BindView(R.id.rg_gender)//性别
            RadioGroup rgGender;
    @BindView(R.id.et_age)//年龄
            EditText etAge;
    @BindView(R.id.et_weight)//体重
            EditText etWeight;
    @BindView(R.id.et_missing_location)//走失地点
            EditText etMissingLocation;
    @BindView(R.id.et_detail)//详情描述
            EditText etDetail;
    @BindView(R.id.et_contact)//联系人
            EditText etContact;
    @BindView(R.id.et_contact_phone)//联系电话
            EditText etContactPhone;
    @BindView(R.id.bt_publish)
    Button btPublish;
    @BindView(R.id.tv_missing_time)
    TextView tvMissingTime;
    @BindView(R.id.et_height)
    EditText etHeight;

    private String mCurrentPhotoPath;
    private String name;
    private String age;
    private String weight;
    private String msTime;
    private String msLocation;
    private String detail;
    private String contact;
    private String contactPhone;
    private int sex;
    private String strImag;
    private String height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_missing);
        ButterKnife.bind(this);
        initTitle();


    }

    private void initTitle() {
        ibtnBack.setVisibility(View.VISIBLE);
        tvTitle.setText("寻人发布");
        menuText.setVisibility(View.VISIBLE);
        menuText.setText("记录");

        //设置EditText的输入类型
        etNameMissing.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        etAge.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        etWeight.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        etHeight.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        etMissingLocation.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        etDetail.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        etContact.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        etContactPhone.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        etAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etAge.getText().toString().equals("")&&Integer.parseInt(etAge.getText().toString())>150) {
                    BaseApplication.getInstances().toast(PublishNoticeActivity.this,"请输入0~150之间的数字");
                    etAge.setText("");
                }
            }
        });

        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etWeight.getText().toString().equals("")&&Integer.parseInt(etWeight.getText().toString())>250) {
                    BaseApplication.getInstances().toast(PublishNoticeActivity.this,"请输入0~250之间的数字");
                    etWeight.setText("");
                }
            }
        });
        etHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etHeight.getText().toString().equals("")&&Integer.parseInt(etHeight.getText().toString())>300) {
                    BaseApplication.getInstances().toast(PublishNoticeActivity.this,"请输入0~300之间的数字");
                    etHeight.setText("");
                }
            }
        });
        etNameMissing.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EditTextFilter.containsSpace(etNameMissing.getText().toString())
                        ||EditTextFilter.containsUnChar(etNameMissing.getText().toString())
                        ||EditTextFilter.containsEmoji(etNameMissing.getText().toString())) {
                    BaseApplication.getInstances().toast(PublishNoticeActivity.this,"含有非法字符,请重新输入");
                    etNameMissing.setText("");

                }

            }
        });

        etMissingLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EditTextFilter.containsSpace(etMissingLocation.getText().toString())
                        ||EditTextFilter.containsUnChar(etMissingLocation.getText().toString())
                        ||EditTextFilter.containsEmoji(etMissingLocation.getText().toString())) {
                    BaseApplication.getInstances().toast(PublishNoticeActivity.this,"含有非法字符,请重新输入");
                    etMissingLocation.setText("");

                }

            }
        });


        etContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EditTextFilter.containsSpace(etContact.getText().toString())
                        ||EditTextFilter.containsUnChar(etContact.getText().toString())
                        ||EditTextFilter.containsEmoji(etContact.getText().toString())) {
                    BaseApplication.getInstances().toast(PublishNoticeActivity.this,"含有非法字符,请重新输入");
                    etContact.setText("");

                }

            }
        });

        etDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EditTextFilter.containsSpace(etDetail.getText().toString())
                        ||EditTextFilter.containsUnChar(etDetail.getText().toString())
                        ||EditTextFilter.containsEmoji(etDetail.getText().toString())) {
                    BaseApplication.getInstances().toast(PublishNoticeActivity.this,"含有非法字符,请重新输入");
                    etDetail.setText("");

                }

            }
        });



    }

    @OnClick({R.id.ibtn_back, R.id.menu_text, R.id.iv_photo, R.id.tv_missing_time, R.id.et_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back://返回
                finish();
                break;
            case R.id.menu_text://记录
                Intent intent = new Intent(this, SearchPublishRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_photo://点击上传照片
                showAddImagDialog();
                break;
            case R.id.tv_missing_time:
                showSelectTimeDialog();
                break;
            case R.id.et_detail:
                break;
        }
    }


    //选择时间对话框
    private void showSelectTimeDialog() {

        Select_Time_Dialog select_time_dialog = new Select_Time_Dialog(this);
        select_time_dialog.setOnTimeChangeCListener(new Select_Time_Dialog.OnTimeChangeCListener() {
            @Override
            public void onClick(String time) {
                tvMissingTime.setText(time);
            }
        });

        select_time_dialog.show();
    }

    /**
     * 选择图片的对话框
     */
    private void showAddImagDialog() {
        List<String> strings = new ArrayList<>();
        strings.add(getString(R.string.common_album));
        strings.add(getString(R.string.common_shot));
        QuanjiakanDialog.getInstance().getListDialogDefineHeight(PublishNoticeActivity.this, strings, getString(R.string.common_album_select), new QuanjiakanDialog.MyDialogCallback() {
            @Override
            public void onItemClick(int position, Object object) {
                if (0 == position) {
                    //从相册选取
                    ImageCropHandler.pickImage(PublishNoticeActivity.this);
                } else if (1 == position) {
                    ImageCropHandler.getImageFromCamera(PublishNoticeActivity.this, new IImageCropInterface() {
                        @Override
                        public void getFilePath(String path) {
                            mCurrentPhotoPath = path;
                        }
                    });
                }

            }
        }, null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA:
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        ImageCropHandler.beginCrop(PublishNoticeActivity.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                    }
                }
                break;
            case CommonRequestCode.REQUEST_PICK://选取照片后回调
                if (resultCode == RESULT_OK) {
                    //裁剪
                    ImageCropHandler.beginCrop(PublishNoticeActivity.this, data.getData());
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
            default:
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
                HttpResponseResult result = new HttpResponseResult(val);
                if (result.isResultOk()) {
                    ivPhoto.setTag(result.getMessage());
                    Picasso.with(getApplicationContext()).load(result.getMessage()).fit()
                            .into(ivPhoto);

                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (jsonObject.has("code") && "200".equals(jsonObject.get("code").getAsString())) {
                        strImag = jsonObject.get("message").getAsString();
                    }
                } else {
                    Toast.makeText(PublishNoticeActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
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
        }, HttpUrls.postFile() + "&storage=19", params, Task.TYPE_MULTIPART_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(PublishNoticeActivity.this, "正在上传图片！")));
    }


    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**********************************
     * 与上传图片相关
     *********************************/


    private void PublishSearch() {
        if (etNameMissing.getText().toString().trim().equals("")) {
            BaseApplication.getInstances().toast(PublishNoticeActivity.this, "请输入姓名!");
            return;
        }
        if (etAge.getText().toString().trim().equals("")) {
            BaseApplication.getInstances().toast(PublishNoticeActivity.this, "请输入年龄!");
            return;
        }
        if (etWeight.getText().toString().trim().equals("")) {
            BaseApplication.getInstances().toast(PublishNoticeActivity.this, "请输入体重!");
            return;
        }
        if (tvMissingTime.getText().toString().trim().equals("")) {
            BaseApplication.getInstances().toast(PublishNoticeActivity.this, "请输入走失时间!");
            return;
        }
        if (etMissingLocation.getText().toString().trim().equals("")) {
            BaseApplication.getInstances().toast(PublishNoticeActivity.this, "请输入失踪的位置!");
            return;
        }
        if (etDetail.getText().toString().trim().equals("")) {
            BaseApplication.getInstances().toast(PublishNoticeActivity.this, "请输入详情描述!");
            return;
        }
        if (etContact.getText().toString().trim().equals("")) {
            BaseApplication.getInstances().toast(PublishNoticeActivity.this, "请输入联系人!");
            return;
        }

        if (etContactPhone.getText().toString().trim().equals("")) {
            BaseApplication.getInstances().toast(PublishNoticeActivity.this, "请输入联系人电话!");
            return;
        }

        if (!CheckUtil.isPhoneNumber(etContactPhone.getText().toString())) {
            BaseApplication.getInstances().toast(PublishNoticeActivity.this, "请输入正确的联系人电话!");
            return;
        }
        if (strImag == null || strImag.equals("")) {
            BaseApplication.getInstances().toast(PublishNoticeActivity.this, "请上传失踪人照片!");
            return;
        }
        if (etHeight.getText().toString().trim().equals("")) {
            BaseApplication.getInstances().toast(PublishNoticeActivity.this, "请上传失踪人身高!");
            return;
        }

        name = etNameMissing.getText().toString().trim();
        age = etAge.getText().toString().trim();
        weight = etWeight.getText().toString().trim();
        msTime = tvMissingTime.getText().toString().trim();
        String missTime = DateUtils.getTime(msTime);
        msLocation = etMissingLocation.getText().toString().trim();
        detail = etDetail.getText().toString().trim();
        contact = etContact.getText().toString().trim();
        contactPhone = etContactPhone.getText().toString().trim();
        height = etHeight.getText().toString().trim();
        if (radioMale.isChecked()) {
            sex = 0;
        } else if (radioFemale.isChecked()) {
            sex = 1;
        }

        //上传数据
        String reqStr = "{\"publishTime\":" + "\"" + System.currentTimeMillis() + "\"" + ",\"userId\":" + "\"" + QuanjiakanSetting.getInstance().getUserId() + "\"" + ",missingUserModel:{" + "\"name\":" + "\"" + name + "\"" + ",\"images\":" + "\"" + strImag + "\"" + ",\"sex\":" + sex + ",\"height\":" + height + ",\"age\":" + age +
                ",\"weight\":" + weight + ",\"missingTime\":" + "\"" + missTime + "\"" + ",\"missingAddress\":" + "\"" + msLocation + "\"" + ",\"missingLongitude\":" + "\"\"" + ",\"missingLatitude\":" + "\"\"" +
                ",\"clothes\":" + "\"" + detail + "\"" + ",\"contacts\":" + "\"" + contact + "\"" + ",\"contactsPhone\":" + "\"" + contactPhone + "\"" + "}}";

        MyHandler.putTask(this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if (null != val && val.length() > 0) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (jsonObject.has("code") && "200".equals(jsonObject.get("code").getAsString())) {
                        BaseApplication.getInstances().toast(PublishNoticeActivity.this, "发布成功！");
                        finish();
                    }
                }


            }
        }, HttpUrls.PublishSearchInfo() + "&data=" + reqStr, null, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    @OnClick(R.id.bt_publish)
    public void onClick() {
        PublishSearch();

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
}
