package com.androidquanjiakan.activity.setting.modify_info;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.index.missing.PublishNoticeActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.androidquanjiakan.interfaces.IDialogCallback;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.androidquanjiakan.util.BitmapUtil;
import com.androidquanjiakan.util.CheckUtil;
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
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class ModifyInfoActivity extends BaseActivity implements OnClickListener {

    protected ImageButton ibtn_back;
    protected TextView tv_title;

    private RelativeLayout rl_image;
    private ImageView head_image;


    private RelativeLayout name_line;
    private TextView tv_nick_name;

    private TextView comfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_modify_info);
        initTitleBar();
        initView();
    }

    public void initTitleBar() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("修改");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
    }

    protected void initView() {
        mLastNetPath = null;

        rl_image = (RelativeLayout) findViewById(R.id.rl_image);
        rl_image.setOnClickListener(this);
        head_image = (ImageView) findViewById(R.id.head_image);

        name_line = (RelativeLayout) findViewById(R.id.name_line);
        name_line.setOnClickListener(this);
        tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);

        comfirm = (TextView) findViewById(R.id.comfirm);
        comfirm.setOnClickListener(this);

        loadNameAndImage();

        timesCount = 0;
    }

    private UserInfo info;

    //获取极光的个人信息
    public void loadInfo() {
        info = null;
        info = JMessageClient.getMyInfo();
        if (info != null) {
            if (info.getAvatar() != null) {
                File file = info.getAvatarFile();
                ImageLoadUtil.loadImage(head_image, file.getAbsolutePath(), ImageLoadUtil.optionsCircle);
            } else {
                head_image.setImageResource(R.drawable.touxiang_big_icon);
            }
            if (BaseApplication.getInstances().getKeyValue("nickname") != null) {
                tv_nick_name.setText(BaseApplication.getInstances().getKeyValue("nickname").replace("'", ""));
            } else {
                if (info.getNickname() != null) {
                    tv_nick_name.setText(info.getNickname());
                } else {
                    tv_nick_name.setText(info.getUserName());
                }
            }
        } else {
            JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        info = JMessageClient.getMyInfo();
                        if (info.getAvatar() != null) {
                            File file = info.getAvatarFile();
                            ImageLoadUtil.loadImage(head_image, file.getAbsolutePath(), ImageLoadUtil.optionsCircle);
                        } else {
                            head_image.setImageResource(R.drawable.touxiang_big_icon);
                        }
                        if (BaseApplication.getInstances().getKeyValue("nickname") != null) {
                            tv_nick_name.setText(BaseApplication.getInstances().getKeyValue("nickname").replace("'", ""));
                        } else {
                            if (info.getNickname() != null) {
                                tv_nick_name.setText(info.getNickname());
                            } else {
                                tv_nick_name.setText(info.getUserName());
                            }
                        }
                    } else {
                        head_image.setImageResource(R.drawable.touxiang_big_icon);
                        tv_nick_name.setText(BaseApplication.getInstances().getUser_id());
                    }
                }
            });
        }
    }

    private JSONObject jsonInfo;
    //接口获取跟人信息数据
    public void loadNameAndImage() {
        HashMap<String, String> params = new HashMap<>();
        Task task = new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {

                // TODO Auto-generated method stub
                if (val != null && !val.equals("") && val.toLowerCase().startsWith("{")) {
                    //上传文件成功
                    try {
                        jsonInfo = new JSONObject(val);
                        if (jsonInfo.has("nickname") && jsonInfo.get("nickname") != null) {
                            BaseApplication.getInstances().setKeyValue("nickname", jsonInfo.get("nickname").toString());
                            tv_nick_name.setText(jsonInfo.get("nickname").toString());
                        } else {
                            if (BaseApplication.getInstances().getKeyValue("nickname") != null) {
                                tv_nick_name.setText(BaseApplication.getInstances().getKeyValue("nickname"));
                            } else {
                                tv_nick_name.setText(BaseApplication.getInstances().getUser_id());
                            }
                        }
                        //
                        if (jsonInfo.has("picture") && jsonInfo.get("picture") != null && jsonInfo.get("picture").toString().toLowerCase().startsWith("http")) {
                            ImageLoadUtil.loadImage(head_image, jsonInfo.get("picture").toString(), ImageLoadUtil.optionsCircle);
                        } else {
                            head_image.setImageResource(R.drawable.touxiang_big_icon);
                        }
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (val == null || "".equals(val)) {
                    loadInfo();
                } else {
                    loadInfo();
                }
            }
        }, HttpUrls.getNameAndHeadIcon(), params, Task.TYPE_GET_STRING_NOCACHE, null);
        MyHandler.putTask(ModifyInfoActivity.this, task);
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
            case R.id.comfirm:
                //检查输入是否规范
                try{
                    if(mLastNetPath==null){
                        if (jsonInfo.has("nickname") && jsonInfo.get("nickname") != null &&
                                jsonInfo.get("nickname").toString().equals(tv_nick_name.getText().toString())) {
                            BaseApplication.getInstances().toast(ModifyInfoActivity.this,"尚未修改任何信息!");
                            return;
                        }
                    }else if(mLastNetPath.toLowerCase().startsWith("http")){//修改了头像，可以提交

                    }else{
                        BaseApplication.getInstances().toast(ModifyInfoActivity.this,"图片上传失败，请重试后提交!");
                        return;
                    }




                    dialog = QuanjiakanDialog.getInstance().getDialog(this);
                    doSave();
                    updataInfo();
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.ibtn_back:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.rl_image:
                modifyImage();
                break;
            case R.id.name_line:
                modifyName();
                break;
        }
    }

    /**
     * 保存修改
     */
    private int timesCount = 0;
    //将修改保存到极光上
    public void doSave() {
        if (timesCount > 2) {
            return;
        }
        timesCount++;

        if (mCurrentPhotoPath != null) {
            if(JMessageClient.getMyInfo()!=null){
                File files = new File(mCurrentPhotoPath);
                if (files.exists()) {
                    JMessageClient.updateUserAvatar(files, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            LogUtil.e("--------callback-------------"+s);

                            if (i == 0) {
                                final UserInfo userInfo = JMessageClient.getMyInfo();
                                userInfo.setNickname(tv_nick_name.getText().toString());
                                JMessageClient.updateMyInfo(UserInfo.Field.nickname, userInfo, new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        if (i == 0) {
//                                    loadInfo();
                                            LogUtil.e("更新用户信息成功!   Nickname：" + JMessageClient.getMyInfo().getNickname());
                                            return;
                                        } else {
                                            doSave();
                                        }
                                    }
                                });
                            } else {
                                doSave();
                            }
                        }
                    });
                }

            }
        } else {
            if(JMessageClient.getMyInfo()!=null) {
                final UserInfo userInfo = JMessageClient.getMyInfo();
                userInfo.setNickname(tv_nick_name.getText().toString());
                JMessageClient.updateMyInfo(UserInfo.Field.nickname, userInfo, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
//                        loadInfo();
                            LogUtil.e("更新用户信息成功!   Nickname：" + JMessageClient.getMyInfo().getNickname());
                            return;
                        } else {
                            doSave();
                        }
                    }
                });
            }
        }
    }

    public void updataInfo() {
        final String nick = tv_nick_name.getText().toString();
        LogUtil.e("NickName[Text]:" + nick);
        try {
            if (mLastNetPath == null) {
                //更新昵称
                HashMap<String, String> params = new HashMap<>();
                Task task = new Task(new HttpResponseInterface() {
                    @Override
                    public void handMsg(String val) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        // TODO Auto-generated method stub
                        if (val != null && !val.equals("") && val.toLowerCase().startsWith("{")) {
                            //上传文件成功
                            try {
                                json = new JSONObject(val);
                                if (json.has("code") && "200".equals(json.getString("code"))) {
                                    BaseApplication.getInstances().setKeyValue("nickname", nick);
//                                    loadNameAndImage();
                                    QuanjiakanDialog.getInstance().getCommonConfirmDialog(ModifyInfoActivity.this, "提示", "更新信息成功", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    });
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (val == null || "".equals(val)) {

                        } else {

                        }
                        QuanjiakanDialog.getInstance().getCommonConfirmDialog(ModifyInfoActivity.this, "提示", "数据提交失败，请重试", new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                }, HttpUrls.updataUserNickName(nick), params, Task.TYPE_GET_STRING_NOCACHE, null);
                MyHandler.putTask(ModifyInfoActivity.this, task);
            } else if (!mLastNetPath.toLowerCase().startsWith("http")) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                BaseApplication.getInstances().toast(ModifyInfoActivity.this,"图片上传失败，请重试后提交!");
            } else {
                HashMap<String, String> params = new HashMap<>();
                Task task = new Task(new HttpResponseInterface() {
                    @Override
                    public void handMsg(String val) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        // TODO Auto-generated method stub
                        if (val != null && !val.equals("") && val.toLowerCase().startsWith("{")) {
                            //上传文件成功
                            try {
                                json = new JSONObject(val);
                                if (json.has("code") && "200".equals(json.getString("code"))) {
                                    ImageLoadUtil.picassoLoad(head_image, mLastNetPath, ImageLoadUtil.TYPE_CYCLE);
                                    BaseApplication.getInstances().setKeyValue("nickname", nick);
//                                    loadInfo();
                                    QuanjiakanDialog.getInstance().getCommonConfirmDialog(ModifyInfoActivity.this, "提示", "更新信息成功", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    });
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (val == null || "".equals(val)) {

                        } else {

                        }
                        QuanjiakanDialog.getInstance().getCommonConfirmDialog(ModifyInfoActivity.this, "提示", "数据提交失败，请重试", new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                }, HttpUrls.updataUserInfo(nick, mLastNetPath), params, Task.TYPE_GET_STRING_NOCACHE, null);
                MyHandler.putTask(ModifyInfoActivity.this, task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改头像
     */
    public void modifyImage() {
        showImageDialog();
    }

    /**
     * 修改名称
     */
    public void modifyName() {
        QuanjiakanDialog.getInstance().getCommonModifyDialog(this, "修改昵称", "请输入您想要的昵称", new IDialogCallback() {
            @Override
            public void getString(String message) {
                if(message==null || message.trim().length()<1){
                    BaseApplication.getInstances().toast(ModifyInfoActivity.this,"无效的昵称,请重新输入!");
                }else if(!CheckUtil.isChar(message)){//检查是否存在特殊字符
                    BaseApplication.getInstances().toast(ModifyInfoActivity.this,"昵称存在特殊字符,请重新输入!");
                }else{
                    tv_nick_name.setText("" + message);
                }
            }
        });
    }

    private String mLastNetPath = null;
    private String mCurrentPhotoPath = null;
    private Dialog dialog;

    private Dialog imageDialog;

    public void showImageDialog() {
        imageDialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_modify_info, null);

        TextView camera = (TextView) view.findViewById(R.id.camera);
        camera.setVisibility(View.VISIBLE);
        camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageDialog != null) {
                    imageDialog.dismiss();
                }
                //从相机拍摄照片
                ImageCropHandler.getImageFromCamera(ModifyInfoActivity.this, new IImageCropInterface() {
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
                ImageCropHandler.pickImage(ModifyInfoActivity.this);
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

    public void showImageCommonDialog() {
        imageDialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_common_image_selecter, null);

        TextView camera = (TextView) view.findViewById(R.id.camera);
        camera.setVisibility(View.VISIBLE);
        camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageDialog != null) {
                    imageDialog.dismiss();
                }
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
        sourcePath = ImageUtils.uri2Path(data, ModifyInfoActivity.this);
        String smallImagePath = ImageUtils.saveBitmapToStorage(SignatureUtil.getMD5String(sourcePath) + ".jpg", BitmapUtil.getSmallBitmap(sourcePath));
        if (temp != null) {
            temp.recycle();
            temp = null;
        }
        temp = BitmapFactory.decodeFile(smallImagePath);
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
                            // u就是拍摄获得的原始图片的uri，剩下的你想干神马坏事请便……
                            save2SmallImage(u);
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ModifyInfoActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA:
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        ImageCropHandler.beginCrop(ModifyInfoActivity.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                    }
                }
                break;
            case CommonRequestCode.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    ImageCropHandler.beginCrop(ModifyInfoActivity.this, data.getData());
                }
            case CommonRequestCode.REQUEST_CROP:
                ImageCropHandler.handleCrop(null, resultCode, data, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        Bitmap smallBitmap = BitmapUtil.getSmallBitmap(mCurrentPhotoPath);
                        String filename = System.currentTimeMillis() + ".png";
                        String file_path = ImageUtils.saveBitmapToStorage(filename, smallBitmap);
                        uploadImage(head_image,file_path, filename);
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
                        //圆形图片
                        Picasso.with(getApplicationContext()).load(strImag).transform(new CircleTransformation())
                                .into(head_image);
                        //方形铺满
//                        Picasso.with(getApplicationContext()).load(strImag).fit()
//                                .into(head_image);
                    }
                } else {
                    Toast.makeText(ModifyInfoActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                }
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }

//                File file2 = new File(mCurrentPhotoPath);
//                if (file2.exists()) {
//                    file2.delete();
//                }
            }
        }, HttpUrls.postFile() + "&storage=2", params, Task.TYPE_POST_FILE, QuanjiakanDialog.getInstance().getDialog(ModifyInfoActivity.this, "正在上传图片！")));
    }
}
