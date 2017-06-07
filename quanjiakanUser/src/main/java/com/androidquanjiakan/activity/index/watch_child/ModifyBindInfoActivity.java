package com.androidquanjiakan.activity.index.watch_child;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.quanjiakanuser.util.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：Administrator on 2017/5/27 14:11
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class ModifyBindInfoActivity extends BaseActivity {

    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rlt_modify_icon)
    RelativeLayout rltModifyIcon;
    @BindView(R.id.rlt_modify_name)
    RelativeLayout rltModifyName;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.menu_text)
    TextView menuText;

    private String device_id;
    private String name;
    private String imag_path;
    private Dialog imageDialog;

    public static final int REQUEST_MODIFY_NAME = 3;
    private String update_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_bind);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        device_id = intent.getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        name = intent.getStringExtra("name");
        imag_path = intent.getStringExtra("imag");

        if (imag_path != null && imag_path.contains("http")) {
            Picasso.with(this).load(imag_path).into(ivIcon);
        }

        if (name != null) {
            if (name.contains("%")) {
                try {
                    tvName.setText(URLDecoder.decode(name, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                tvName.setText(name);
            }
        }
        ibtnBack.setVisibility(View.VISIBLE);
        menuText.setVisibility(View.VISIBLE);
        menuText.setText("保存");
        menuText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 /*
        * 保存操作
        * */

                if (mLastNetPath!=null||update_name!=null) {

                    if (mLastNetPath!=null) {
                        imag_path = mLastNetPath;
                    }

                    if (update_name!=null) {
                        name = update_name;
                    }
                    if (!name.contains("%")) {
                        try {
                            name = URLEncoder.encode(name,"utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    MyHandler.putTask(ModifyBindInfoActivity.this,new Task(new HttpResponseInterface() {
                        @Override
                        public void handMsg(String val) {
                            /*  {"code":200,"message":"成功"} */
                            JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                            if (jsonObject.has("code")&&jsonObject.get("code").getAsString().equals("200")) {
                                BaseApplication.getInstances().toast(ModifyBindInfoActivity.this,"保存成功");
                                finish();
                            }else {
                                BaseApplication.getInstances().toast(ModifyBindInfoActivity.this,"保存失败");
                            }

                        }
                /*  &imei=355637053837001&userid=11824&icon=111&nickname=%E5%93%A5%E5%93%A5*/
                    },HttpUrls.updateBindInfo() + "&imei=" + device_id +
                            "&userid=" + BaseApplication.getInstances().getUser_id() +
                            "&icon=" + imag_path +
                            "&nickname=" + name,null,Task.TYPE_GET_STRING_NOCACHE,null
                    ));

                }else {
                    BaseApplication.getInstances().toast(ModifyBindInfoActivity.this,"您未改动数据！");
                }
            }
        });
        tvTitle.setText("绑定修改");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.ibtn_back, R.id.rlt_modify_icon, R.id.rlt_modify_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.rlt_modify_icon:
                showImageDialog();

                break;
            case R.id.rlt_modify_name:
                toModifyName();
                break;
        }
    }

    private void toModifyName() {
        Intent intent = new Intent(this, ModifyNameActivity.class);
        intent.putExtra("name", name);
        startActivityForResult(intent, REQUEST_MODIFY_NAME);
    }

    private String mLastNetPath = null;
    private String mCurrentPhotoPath = null;

    public void showImageDialog() {
        imageDialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_modify_info, null);

        TextView camera = (TextView) view.findViewById(R.id.camera);
        camera.setVisibility(View.VISIBLE);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageDialog != null) {
                    imageDialog.dismiss();
                }
                //从相机拍摄照片
                ImageCropHandler.getImageFromCamera(ModifyBindInfoActivity.this, new IImageCropInterface() {
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
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageDialog != null) {
                    imageDialog.dismiss();
                }
                //从相册选取照片
                ImageCropHandler.pickImage(ModifyBindInfoActivity.this);
            }
        });

        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setVisibility(View.VISIBLE);
        cancel.setOnClickListener(new View.OnClickListener() {
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
                            Uri u = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
                                    tempFile.getAbsolutePath(), null, null));
                            // u就是拍摄获得的原始图片的uri，剩下的你想干神马坏事请便……
                            save2SmallImage(u);
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ModifyBindInfoActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA:
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        ImageCropHandler.beginCrop(ModifyBindInfoActivity.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                    }
                }
                break;
            case CommonRequestCode.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    ImageCropHandler.beginCrop(ModifyBindInfoActivity.this, data.getData());
                }
            case CommonRequestCode.REQUEST_CROP:
                ImageCropHandler.handleCrop(null, resultCode, data, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        Bitmap smallBitmap = BitmapUtil.getSmallBitmap(mCurrentPhotoPath);
                        String filename = System.currentTimeMillis() + ".png";
                        String file_path = ImageUtils.saveBitmapToStorage(filename, smallBitmap);
                        uploadImage(ivIcon, file_path, filename);
                    }
                });
                break;

            case REQUEST_MODIFY_NAME:

                LogUtil.e("---------------name----------"+data.getStringExtra("update"));
                if (resultCode == RESULT_OK) {
                    update_name = data.getStringExtra("update");
                    if (update_name != null) {
                        tvName.setText(update_name);
                    }

                }
                break;
        }
    }

    private Bitmap temp;
    private String sourcePath;

    public void save2SmallImage(Uri data) {
        if (data == null) {
            return;
        }
        sourcePath = ImageUtils.uri2Path(data, ModifyBindInfoActivity.this);
        String smallImagePath = ImageUtils.saveBitmapToStorage(SignatureUtil.getMD5String(sourcePath) + ".jpg", BitmapUtil.getSmallBitmap(sourcePath));
        if (temp != null) {
            temp.recycle();
            temp = null;
        }
        temp = BitmapFactory.decodeFile(smallImagePath);
    }

    protected void uploadImage(final ImageView head_image, final String path, String filename) {
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
//                    uploadStatus = true;
                    if (head_image != null) {
                        head_image.setTag(strImag);
                        //圆形图片
                        Picasso.with(getApplicationContext()).load(strImag)/*.transform(new CircleTransformation())*/
                                .into(head_image);
                    }
                } else {
                    Toast.makeText(ModifyBindInfoActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
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
        }, HttpUrls.postFile() + "&storage=2", params, Task.TYPE_POST_FILE, QuanjiakanDialog.getInstance().getDialog(ModifyBindInfoActivity.this, "正在上传图片！")));
    }

}
