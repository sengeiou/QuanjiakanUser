package com.androidquanjiakan.activity.index.bind;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.common.CommonWebEntryActivity;
import com.androidquanjiakan.activity.common.CommonWebEntryActivity_activate;
import com.androidquanjiakan.activity.common.CommonWebEntryActivity_kitkat;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.datahandler.BindDeviceHandler;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.entity.ContactsBean;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.androidquanjiakan.result.ContactsResultBean;
import com.androidquanjiakan.util.BitmapUtil;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.SignatureUtil;
import com.androidquanjiakan.view.CircleTransformation;
import com.example.greendao.dao.ContactsBeanDao;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.quanjiakanuser.util.ImageUtils;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;
import com.zxing.qrcode.BindDeviceActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DevicesBindActivity extends BaseActivity {


    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.image_hint)
    TextView imageHint;
    @BindView(R.id.bind_device_scan_2dcode)
    TextView bindDeviceScan2dcode;
    @BindView(R.id.bind_device_2dcode_value)
    EditText bindDevice2dcodeValue;
    @BindView(R.id.relation_daddy)
    TextView relationDaddy;
    @BindView(R.id.relation_mammy)
    TextView relationMammy;
    @BindView(R.id.relation_grandpa)
    TextView relationGrandpa;
    @BindView(R.id.relation_grandma)
    TextView relationGrandma;
    @BindView(R.id.relation_grandfa)
    TextView relationGrandfa;
    @BindView(R.id.relation_grandmama)
    TextView relationGrandmama;
    @BindView(R.id.relation_sister)
    TextView relationSister;
    @BindView(R.id.relation_brother)
    TextView relationBrother;
    @BindView(R.id.relation_design)
    TextView relationDesign;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.nickname)
    EditText nickname;


    private Dialog remindDialog;
    private String content;

    private String type;
    private List<ContactsResultBean.ResultsBean.ContactsBean> contacts = new ArrayList<>();
    private ContactsBeanDao dao;
    private ContactsBean contactsBean;
    private Gson gson;
    private ContactsResultBean contactsResultBean;
    private ContactsResultBean.ResultsBean resultBean;
    private String sendName;
    private HashMap<String, String> nameMap = new HashMap<>();
    private int count = 0;
    private String deviceid;
    private boolean isSuccess = false;
    public static final String TYPE = "type";
    private Bitmap temp;
    private String sourcePath;
    private Dialog dialog;
    private String mCurrentPhotoPath = null;
    private static final String IMAGE_FILE_NAME = "qjk" + String.valueOf(System.currentTimeMillis()).substring(4) + ".jpg";
    private Dialog imageDialog;
    private String bind_device_image;
    public static final String DEVICEID = "deviceid";
    private final int DADDY = 0;
    private final int MAMMY = 1;
    private final int GRANDPA = 2;
    private final int GRANDMA = 3;
    private final int GRANDFA = 4;

    private final int GRANDMAMA = 5;
    private final int SISTER = 7;
    private final int BROTHER = 6;
    private final int DESIGN = 8;
    private int currentRelation = 0;
    private String currentRelationName;
    private String bindData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_device_bind_common);
        type = getIntent().getStringExtra(TYPE);
        dao = getContactsBeanDao();
        gson = new Gson();
        ButterKnife.bind(this);
        count = 0;
        initTitle();
//        initData();
        initSelected();
    }

    private ContactsBeanDao getContactsBeanDao() {
        return BaseApplication.getInstances().getDaoInstant().getContactsBeanDao();
    }


    //TODO 检查是否存在重名的情况
    private void initData(String device_id) {
        nameMap.clear();
        if (contacts != null) {
            contacts.clear();
        }
        if (NetUtil.isNetworkAvailable(DevicesBindActivity.this)) {
            loadContactsData(device_id);
        } else {
            if (dao.loadAll().size() > 0) {
                for (int i = 0; i < dao.loadAll().size(); i++) {
                    if (device_id.equals(dao.loadAll().get(i).getImei())) {
                        contactsBean = dao.loadAll().get(i);
                        String json = contactsBean.getJson();
                        LogUtil.e("json---------" + json);
                        contactsResultBean = gson.fromJson(json, ContactsResultBean.class);
                        resultBean = contactsResultBean.getResults();
                        if ("Contacts".equals(resultBean.getCategory())) {
                            contacts = resultBean.getContacts();
                            for (ContactsResultBean.ResultsBean.ContactsBean object : contacts) {
                                if ("1".equals(object.getApp())) {
                                    nameMap.put(object.getUserid() + "Name", object.getName());
                                    nameMap.put(object.getUserid() + "Image", object.getImage());
                                }
                            }
                        }
                    }
                }

            }

            try {
                sendName = URLEncoder.encode(currentRelationName, "utf-8");
                if (contacts.size() > 0) {
                    for (int i = 0; i < contacts.size(); i++) {
                        if (sendName.equals(contacts.get(i).getName()) ||
                                currentRelationName.equals(contacts.get(i).getName()) ||
                                currentRelationName.equals(URLDecoder.decode(contacts.get(i).getName(), "utf-8"))
                                ) {
                            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "该联系人名字已存在！");
                            break;
                        }
                        if (i == contacts.size() - 1) {
                            bindDevice(bindDevice2dcodeValue.getText().toString().trim());
                        }

                    }
                } else {
                    bindDevice(bindDevice2dcodeValue.getText().toString().trim());
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    private void getContacts(final String device_id, final String name) {
        nameMap.clear();
        com.quanjiakanuser.http.MyHandler.putTask(DevicesBindActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                if (!jsonObject.has("code")) {
                    JsonObject result1 = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
                    if (device_id.equals(result1.get("IMEI").getAsString())) {
                        JsonArray jsonArray = result1.getAsJsonArray(ConstantClassFunction.CONTACTS);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject object = jsonArray.get(i).getAsJsonObject();
                            ContactsResultBean.ResultsBean.ContactsBean contactsBean = new ContactsResultBean.ResultsBean.ContactsBean();

                            if (object.has("Name")) {
                                contactsBean.setName(object.get("Name").getAsString());
                            }

                            if (object.has("Userid")) {
                                contactsBean.setUserid(object.get("Userid").getAsString());
                            }
                            //
                            if (object.has("App") && "1".equals(object.get("App").getAsString())) {
                                nameMap.put(object.get("Userid").getAsString() + "Name", object.get("Name").getAsString());
                                nameMap.put(object.get("Userid").getAsString() + "Image", object.get("Image").getAsString());
                            }
                        }
                    }
                } else {
                    BaseApplication.getInstances().toast(DevicesBindActivity.this, "未查询到数据");
                }
            }
        }, HttpUrls.getContactsList() + "&imei=" + device_id, null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(DevicesBindActivity.this)));
    }

    //TODO 获取指定设备联系人数据，保证关系名称不会出现重复的情况出现
    private void loadContactsData(final String device_id) {
        /**
         * 数据库没有数据时请求网络
         */

        MyHandler.putTask(DevicesBindActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("contacts-----------------" + val);

                if (val!=null&&val.length()>2) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (!jsonObject.has("code")) {
                        JsonObject result1 = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
                        if (result1.has("IMEI") &&
                                device_id.equals(result1.get("IMEI").getAsString())) {//TODO 仅含该条件时，当返回的空数据的Json对象，将报空指针异常
                            JsonArray jsonArray = result1.getAsJsonArray(ConstantClassFunction.CONTACTS);
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject object = jsonArray.get(i).getAsJsonObject();
                                ContactsResultBean.ResultsBean.ContactsBean contactsBean = new ContactsResultBean.ResultsBean.ContactsBean();
                                if (object.has("Admin")) {
                                    contactsBean.setAdmin(object.get("Admin").getAsString());
                                }

                                if (object.has("App")) {
                                    contactsBean.setApp(object.get("App").getAsString());
                                }
                                if (object.has("App") && "1".equals(object.get("App").getAsString()) && object.has("Userid")) {
                                    nameMap.put(object.get("Userid").getAsString() + "Name",
                                            (object.get("Name")!=null? object.get("Name").getAsString(): object.get("Userid").getAsString()
                                                    //TODO 若存在不含名字这个字段，则使用Userid进行替代
                                            ));
                                    nameMap.put(object.get("Userid").getAsString() + "Image",
                                            (object.get("Image")!=null? object.get("Image").getAsString():"8"//TODO
                                                    //TODO 若存在不含图像这个字段，则使用 默认的 自定义头像 进行替代*****

                                                    //TODO 2017-05-05发现存在Image字段不存在的情况
                                                    //TODO 请求链接 http://app.quanjiakan.com/device/service?code=childWatch&type=contracts&imei=355637050066828
                                                    //TODO 返回的数据 {"Results":{"Category":"Contacts","Contacts":[{"Admin":"0","App":"1","Id":"984","Image":"8","Name":"%E5%86%AF%E5%B7%A9","Tel":"13650703987","Userid":"11178"},{"Admin":"0","App":"1","Id":"986","Image":"0","Name":"%E7%88%B8%E7%88%B8","Tel":"15218293347","Userid":"13469"},{"Admin":"0","App":"1","Id":"1015","Image":"1","Name":"%E5%A6%88%E5%A6%88","Tel":"15820233638","Userid":"11825"},{"Admin":"1","App":"1","Id":"1025","Image":"8","Name":"%E5%B0%8FA","Tel":"13432992552","Userid":"13625"},{"Admin":"0","App":"0","Id":"1038","Image":"8","Name":"%E5%B0%8F%E6%98%8E","Tel":"18718717141","Userid":"13469"},{"Admin":"0","App":"1","Id":"1050","Name":"%E7%88%B7%E7%88%B7","Tel":"13802735616","Userid":"11931"}],"IMEI":"355637050066828","Num":6}}
                                            ));
                                }

                                if (object.has("Id")) {
                                    contactsBean.setId(object.get("Id").getAsString());
                                }

                                if (object.has("Image")) {
                                    contactsBean.setImage(object.get("Image").getAsString());
                                }

                                if (object.has("Name")) {
                                    contactsBean.setName(object.get("Name").getAsString());
                                }

                                if (object.has("Tel")) {
                                    contactsBean.setTel(object.get("Tel").getAsString());
                                }

                                if (object.has("Userid")) {
                                    contactsBean.setUserid(object.get("Userid").getAsString());
                                }
                                contacts.add(contactsBean);
                            }


                        }
                    }

                }
                String tempName;
                String decodeName;
                try {
                    sendName = URLEncoder.encode(currentRelationName, "utf-8");
                    if (contacts.size() > 0) {
                        for (int i = 0; i < contacts.size(); i++) {
                            tempName = contacts.get(i).getName();
                            decodeName = URLDecoder.decode(tempName, "utf-8");
                            if (sendName.equals(tempName) || val.contains(sendName)
                                    || currentRelationName.equals(tempName) ||
                                    currentRelationName.equals(decodeName)
                                    ) {
                                BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "该联系人名字已存在！");
                                break;
                            }
                            if (i == contacts.size() - 1) {
                                bindDevice(bindDevice2dcodeValue.getText().toString().trim());
                            }
                        }
                    } else {
                        bindDevice(bindDevice2dcodeValue.getText().toString().trim());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }, HttpUrls.getContactsList() + "&imei=" + device_id, null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(DevicesBindActivity.this, "正在获取联系人数据")));
    }


    public void initTitle() {
        tvTitle.setText("绑定ID");

        ibtnBack.setVisibility(View.VISIBLE);
    }

    public void initSelected() {
        setSelectedRelation(DADDY);
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
        count = 0;
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @OnClick({R.id.ibtn_back, R.id.image, R.id.bind_device_scan_2dcode, R.id.relation_daddy, R.id.relation_mammy, R.id.relation_grandpa, R.id.relation_grandma, R.id.relation_grandfa, R.id.relation_grandmama, R.id.relation_sister, R.id.relation_brother, R.id.relation_design, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                if (isSuccess) {
                    setResult(RESULT_OK);
                }
                finish();
                break;
            case R.id.image:
                showOptionsDialog();
                break;
            case R.id.bind_device_scan_2dcode:
                Intent intent = new Intent(this, BindDeviceActivity.class);
                intent.putExtra(BaseConstants.PARAMS_SHOW_HINT, true);
                startActivityForResult(intent, CommonRequestCode.REQUEST_SCAN);
                break;
            case R.id.relation_daddy:
                setSelectedRelation(DADDY);
                break;
            case R.id.relation_mammy:
                setSelectedRelation(MAMMY);
                break;
            case R.id.relation_grandpa:
                setSelectedRelation(GRANDPA);
                break;
            case R.id.relation_grandma:
                setSelectedRelation(GRANDMA);
                break;
            case R.id.relation_grandfa:
                setSelectedRelation(GRANDFA);
                break;
            case R.id.relation_grandmama:
                setSelectedRelation(GRANDMAMA);
                break;
            case R.id.relation_sister:
                setSelectedRelation(SISTER);
                break;
            case R.id.relation_brother:
                setSelectedRelation(BROTHER);
                break;
            case R.id.relation_design:
                showEditRemindDialog();
                break;
            case R.id.btn_submit:
                submitBind();
                break;
        }
    }

    //TODO 修改名称对话框----自定义名称
    private void showEditRemindDialog() {
        remindDialog = new Dialog(this, R.style.ShareDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_child_plan_edit, null);
        final EditText tv_content = (EditText) view.findViewById(R.id.tv_content);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("输入自定义名称");
        TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_content.length() < 1) {
                    BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "请输入名称!");
                    return;
                }

                if ((CheckUtil.isAllChineseChar(tv_content.getText().toString()) && tv_content.getText().toString().length() > 4)) {
                    Toast.makeText(DevicesBindActivity.this, "请输入正确的名称", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!CheckUtil.isChar(tv_content.getText().toString())) {
                    Toast.makeText(DevicesBindActivity.this, "名称中含有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tv_content.getText().toString().length() > 8) {
                    Toast.makeText(DevicesBindActivity.this, "请输入正确的名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                relationDesign.setText(tv_content.getText().toString());
                content = tv_content.getText().toString();


                if (remindDialog != null) {
                    remindDialog.dismiss();
                }
                setSelectedRelation(DESIGN);


            }
        });

        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remindDialog != null) {
                    remindDialog.dismiss();
                }
            }
        });

        WindowManager.LayoutParams lp = remindDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        remindDialog.setContentView(view, lp);
        remindDialog.show();
    }

    //TODO 头像来源选择Dialog
    protected void showOptionsDialog() {
        imageDialog = new Dialog(this, R.style.AlbumDialogStyle);
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
                ImageCropHandler.getImageFromCamera(DevicesBindActivity.this, new IImageCropInterface() {
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
                ImageCropHandler.pickImage(DevicesBindActivity.this);
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

    public void save2SmallImage(Uri data) {
        if (data == null) {
            return;
        }
        sourcePath = ImageUtils.uri2Path(data, DevicesBindActivity.this);
        String smallImagePath = ImageUtils.saveBitmapToStorage(SignatureUtil.getMD5String(sourcePath) + ".jpg", BitmapUtil.getSmallBitmap(sourcePath));
        if (temp != null) {
            temp.recycle();
            temp = null;
        }
        temp = BitmapFactory.decodeFile(smallImagePath);
        image.setImageBitmap(temp);
    }

    //TODO 绑定设备
    public void submitBind() {
        if (bindDevice2dcodeValue.length() < 1) {
            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "请输入ID，或扫描二维码获取!");
            return;
        }
        if (bindDevice2dcodeValue.getText().toString().trim().length() != 15) {
            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "ID格式不符合规范,应为15位!");
            return;
        }
        if (!CheckUtil.isNumberChar(bindDevice2dcodeValue.getText().toString().trim())) {
            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "ID格式不符合规范,应为数字!");
            return;
        }

        if (nickname.getText().toString() == null || nickname.getText().toString().length() < 1) {
            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "未设置使用者昵称!");
            return;
        }
        if (nickname.getText().toString().contains("%") || !CheckUtil.isChar(nickname.getText().toString())) {
            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "昵称中含有非法字符!");
            return;
        }

        if (bind_device_image == null || !bind_device_image.toLowerCase().startsWith("http")) {
            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "未上传头像或上传头像失败!");
            return;
        }
        initData(bindDevice2dcodeValue.getText().toString().trim());
    }

    //TODO 根据类型参数设置关系名
    public void setSelectedRelation(int type) {
        currentRelation = type;
        resetAllRelation();
        switch (type) {
            case DADDY:
                currentRelationName = "爸爸";
                selectedRelation(relationDaddy);
                break;
            case MAMMY:
                currentRelationName = "妈妈";
                selectedRelation(relationMammy);
                break;
            case GRANDPA:
                currentRelationName = "爷爷";
                selectedRelation(relationGrandpa);
                break;
            case GRANDMA:
                currentRelationName = "奶奶";
                selectedRelation(relationGrandma);
                break;
            case GRANDFA:
                currentRelationName = "外公";
                selectedRelation(relationGrandfa);
                break;
            case GRANDMAMA:
                currentRelationName = "外婆";
                selectedRelation(relationGrandmama);
                break;

            case SISTER:
                currentRelationName = "姐姐";
                selectedRelation(relationSister);
                break;
            case BROTHER:
                currentRelationName = "哥哥";
                selectedRelation(relationBrother);
                break;
            case DESIGN:
                currentRelationName = content;
                selectedRelation(relationDesign);
                break;
            default:
                currentRelationName = "爸爸";
                currentRelation = DADDY;
                selectedRelation(relationDaddy);
                break;
        }
    }

    public void resetAllRelation() {
        unselectedRelation(relationDaddy);
        unselectedRelation(relationMammy);
        unselectedRelation(relationGrandpa);
        unselectedRelation(relationGrandma);
        unselectedRelation(relationGrandfa);

        unselectedRelation(relationGrandmama);
        unselectedRelation(relationSister);
        unselectedRelation(relationBrother);
        unselectedRelation(relationDesign);
    }

    public void unselectedRelation(TextView relation) {
        relation.setBackgroundResource(R.drawable.selecter_device_bind_unselected);
        relation.setTextColor(getResources().getColor(R.color.font_color_999999));
    }

    public void selectedRelation(TextView relation) {
        relation.setBackgroundResource(R.drawable.selecter_device_bind_selected);
        relation.setTextColor(getResources().getColor(R.color.font_color_15A9A9));
    }

    //TODO 通过SDK调用，发送绑定申请
    protected void bindDevice(final String deviceid) {
        if (CheckUtil.isEmpty(deviceid) || deviceid.length() != 15) {
            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "无效的设备ID,请重新输入或获取!");
            return;
        }
        if (!BaseApplication.getInstances().isSDKConnected()) {
            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "已与手表服务器断开连接");
        } else {
            try {
                //TODO Old
                this.deviceid = deviceid;//
                long devid = Long.parseLong(deviceid, 16);
                JSONObject jsonObject = new JSONObject();
                JSONObject jsonObjectSub = new JSONObject();
                jsonObjectSub.put("WatchName", URLEncoder.encode(nickname.getText().toString(), "utf-8"));
                jsonObjectSub.put("WatchImage", bind_device_image);
                jsonObjectSub.put("UserName", URLEncoder.encode(currentRelationName, "utf-8"));
                jsonObjectSub.put("UserImage", currentRelation + "");
                jsonObject.put("IMEI", deviceid);
                jsonObject.put("Category", "BindReq");
                jsonObject.put("BindReq", jsonObjectSub);
                bindData = jsonObject.toString();
                LogUtil.e("BindData:" + jsonObject.toString());
                int size = jsonObject.toString().getBytes().length;
                BaseApplication.getInstances().getNattyClient().ntyBindClient(devid, jsonObject.toString().getBytes(), size);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CommonRequestCode.REQUEST_NET:
                //TODO 若进行设备激活时，不会收到绑定通过结果，则放开这里的提示
                if(isHintResubmit){//TODO 根据是否是激活设备，来判断是否需要提示重新提交绑定申请
                    isHintResubmit = false;
//                    Toast.makeText(this, "若已激活设备，请重新提交绑定申请数据!", Toast.LENGTH_SHORT).show();
                }
                if(data==null){//TODO 控制非空
                    return;
                }
                String res = data.getStringExtra(BaseConstants.PARAMS_BIND_RESULT);
                if(res!=null && "5".equals(res)){
                    //TODO 激活网页处收到了绑定通过的结果（该人为第一个绑定的人），走与在当前页收到5时相同的处理流程
                    Intent intent = new Intent(DevicesBindActivity.this, DevicesBindStateActivity.class);
                    intent.putExtra(BaseConstants.PARAMS_STATE, DevicesBindStateActivity.ACCESS);
                    intent.putExtra(BaseConstants.PARAMS_ID, bindDevice2dcodeValue.getText().toString().trim());
                    startActivityForResult(intent, CommonRequestCode.REQUEST_STATE);
                    BaseApplication.getInstances().toastLong(DevicesBindActivity.this, "设备绑定成功,您已成为管理员!");
                }
                break;
            case CommonRequestCode.REQUEST_FINISH:
                if (resultCode == RESULT_OK) {
                    String deviceid = data.getStringExtra(DEVICEID);
                    if (deviceid != null && deviceid.length() > 0) {
                        /**
                         * TODO 根据传入的网址，截取其中的设备ID【ID形式暂时没有定下来】
                         */
                        if (deviceid.indexOf("IMEI=") > 0 || deviceid.toLowerCase().indexOf("imei=") > 0) {
                            if (deviceid.indexOf("IMEI=") > 0) {
                                bindDevice2dcodeValue.setText(deviceid.substring(deviceid.indexOf("IMEI=") + 5, deviceid.indexOf("IMEI=") + 20));
                            } else {
                                bindDevice2dcodeValue.setText(deviceid.substring(deviceid.indexOf("imei=") + 5, deviceid.indexOf("imei=") + 20));
                            }
                        } else {
//                            bindDevice2dcodeValue.setText(deviceid);
                        }
                        LogUtil.w("二维码设备ID:" + deviceid);
                    } else {
                        Toast.makeText(DevicesBindActivity.this, "二维码解析失败,请重试!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case CommonRequestCode.REQUEST_SCAN:
                if (resultCode == RESULT_OK) {
                    String deviceid = data.getStringExtra(DEVICEID);
                    if (deviceid != null && deviceid.length() > 0) {
                        /**
                         * TODO 根据传入的网址，截取其中的设备ID【ID形式暂时没有定下来】
                         */
                        if (deviceid.indexOf("IMEI=") > 0 || deviceid.toLowerCase().indexOf("imei=") > 0) {
                            if (deviceid.indexOf("IMEI=") > 0) {
                                bindDevice2dcodeValue.setText(deviceid.substring(deviceid.indexOf("IMEI=") + 5, deviceid.indexOf("IMEI=") + 20));
                            } else {
                                bindDevice2dcodeValue.setText(deviceid.substring(deviceid.indexOf("imei=") + 5, deviceid.indexOf("imei=") + 20));
                            }
                        } else {
//                            bind_device_2dcode_value.setText(deviceid);
                        }
                        LogUtil.w("二维码设备ID:" + deviceid);
                    } else {
                        Toast.makeText(DevicesBindActivity.this, "二维码解析失败,请重试!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case CommonRequestCode.IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    save2SmallImage(data.getData());
                }
            case CommonRequestCode.CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (QuanjiakanUtil.hasSdcard()) {
                        File tempFile = new File(BaseConstants.imageDir + IMAGE_FILE_NAME);
                        try {
                            Uri u = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
                                    tempFile.getAbsolutePath(), null, null));
                            save2SmallImage(u);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            MobclickAgent.reportError(DevicesBindActivity.this, e);
                        }
                    } else {
                        Toast.makeText(DevicesBindActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA:
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        ImageCropHandler.beginCrop(DevicesBindActivity.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                    }
                }
                break;
            case CommonRequestCode.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    ImageCropHandler.beginCrop(DevicesBindActivity.this, data.getData());
                }
            case CommonRequestCode.REQUEST_CROP:
                ImageCropHandler.handleCrop(null, resultCode, data, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        LogUtil.e("最后展示图片的地址:" + path);
                        /**
                         * {"code":"500","message":"error"}
                         */
                        dialog = QuanjiakanDialog.getInstance().getDialog(DevicesBindActivity.this, "正在上传头像!");
//                        final File file = new File(path);
                        //将截取的图像重新缩放，转存，控制需要上传的文件大小
                        Bitmap smallBitmap = BitmapUtil.getSmallBitmap(mCurrentPhotoPath);
                        String filename = System.currentTimeMillis() + ".png";
                        String file_path = ImageUtils.saveBitmapToStorage(filename, smallBitmap);
                        HashMap<String, String> params = new HashMap<>();
                        params.put("file", file_path.toString());
                        params.put("filename", filename);
                        params.put("image", file_path);
                        Task task = new Task(new HttpResponseInterface() {
                            @Override
                            public void handMsg(String val) {
                                dialog.dismiss();
                                // TODO Auto-generated method stub
                                if (val != null && !val.equals("") && val.toLowerCase().startsWith("{")) {
                                    try {
                                        JSONObject json = new JSONObject(val);
                                        if (json.has("code") && "200".equals(json.getString("code"))) {
                                            bind_device_image = json.getString("message");
                                            Uri uri = Crop.getOutput(data);
                                            Picasso.with(getApplicationContext()).load(uri)
                                                    .transform(new CircleTransformation())
                                                    .into(image);
                                            Toast.makeText(DevicesBindActivity.this, "头像上传成功!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(DevicesBindActivity.this, "头像上传失败!", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        MobclickAgent.reportError(DevicesBindActivity.this, e);
                                    }
                                } else {
                                    Toast.makeText(DevicesBindActivity.this, "接口调用失败!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, HttpUrls.postFile() + "&storage=16", params, Task.TYPE_POST_FILE, null);
                        MyHandler.putTask(DevicesBindActivity.this, task);
                    }
                });
                break;
            case CommonRequestCode.REQUEST_STATE:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            default:
                break;
        }
    }

    boolean isHintResubmit = false;
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
                    isSuccess = true;
                    BindDeviceHandler.insertValue(deviceid, "", "");
                    /**
                     * 调用创建/添加群 TODO 暂时不
                     */
                    count++;
                    Intent intent = new Intent(DevicesBindActivity.this, DevicesBindStateActivity.class);
                    intent.putExtra(BaseConstants.PARAMS_STATE, DevicesBindStateActivity.WAIT);
                    intent.putExtra(BaseConstants.PARAMS_ID, bindDevice2dcodeValue.getText().toString().trim());
                    startActivityForResult(intent, CommonRequestCode.REQUEST_STATE);
                    BaseApplication.getInstances().toastLong(DevicesBindActivity.this, "绑定申请发送成功!");
                } else if ("1".equals(res)) {
                    isSuccess = false;
                    BaseApplication.getInstances().toastLong(DevicesBindActivity.this, "用户ID不存在!");
                } else if ("2".equals(res)) {
                    isSuccess = false;
                    BaseApplication.getInstances().toastLong(DevicesBindActivity.this, "设备ID不存在!");
                } else if ("3".equals(res)) {
                    //TODO 已经绑定过，直接修改头像与名称
                    isSuccess = true;
                    count++;
                    //TODO 2017-04-10 王博靖邮件确认
                    Intent intent = new Intent(DevicesBindActivity.this, CommonWebEntryActivity.class);
                    intent.putExtra(BaseConstants.PARAMS_URL, "http://app.quanjiakan.com/familycare/activate?IMEI=" + bindDevice2dcodeValue.getText().toString().trim());
//                    startActivityForResult(intent, CommonRequestCode.REQUEST_STATE);
                    startActivityForResult(intent,CommonRequestCode.REQUEST_NET);//TODO 返回需要返回到这个界面,而不是首页----经 谢东 确认 2017-04-24
                    BaseApplication.getInstances().toastLong(DevicesBindActivity.this, "已发送过绑定申请!");
                } else if ("4".equals(res)) {
                    isSuccess = false;
                    isHintResubmit = false;

                    //TODO 2017-04-10 王博靖邮件确认
                    Intent intent = new Intent();
                    intent.setClass(DevicesBindActivity.this, CommonWebEntryActivity_kitkat.class);
                    intent.putExtra(BaseConstants.PARAMS_URL, "http://app.quanjiakan.com/familycare/activate?IMEI=" + bindDevice2dcodeValue.getText().toString().trim());
//                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//
//                        intent.setClass(DevicesBindActivity.this, CommonWebEntryActivity_kitkat.class);
//                        LogUtil.e("--------------kitkat--------------");
//                        // TODO: 2017/5/4 这里的url要等后台重新做 加一个参数----&version=1
//                        intent.putExtra(BaseConstants.PARAMS_URL, "http://app.quanjiakan.com/familycare/activate?IMEI=" + bindDevice2dcodeValue.getText().toString().trim());
//
//                    }else {
//                        intent.setClass(DevicesBindActivity.this, CommonWebEntryActivity_activate.class);
//                        intent.putExtra(BaseConstants.PARAMS_URL, "http://app.quanjiakan.com/familycare/activate?IMEI=" + bindDevice2dcodeValue.getText().toString().trim());
//                    }

//                    http://app.quanjiakan.com/familycare/activate?IMEI=
                    intent.putExtra(BaseConstants.PARAMS_DATA,bindData);
                    intent.putExtra(BaseConstants.PARAMS_ID, bindDevice2dcodeValue.getText().toString().trim());
                    
//                    startActivityForResult(intent, CommonRequestCode.REQUEST_STATE);
                    startActivityForResult(intent,CommonRequestCode.REQUEST_NET);//TODO 返回需要返回到这个界面,而不是首页----经 谢东 确认 2017-04-24
                    BaseApplication.getInstances().toastLong(DevicesBindActivity.this, "设备未激活!");
                    isHintResubmit = true;

                } else if ("5".equals(res)) {//第一个绑定
                    isSuccess = true;
                    count++;
                    Intent intent = new Intent(DevicesBindActivity.this, DevicesBindStateActivity.class);
                    intent.putExtra(BaseConstants.PARAMS_STATE, DevicesBindStateActivity.ACCESS);
                    intent.putExtra(BaseConstants.PARAMS_ID, bindDevice2dcodeValue.getText().toString().trim());
                    startActivityForResult(intent, CommonRequestCode.REQUEST_STATE);
                    BaseApplication.getInstances().toastLong(DevicesBindActivity.this, "设备绑定成功,您已成为管理员!");
                } else {
                    isSuccess = false;
//                    BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(), "未知的返回结果,返回码(" + res + ")!");
                }
                break;
        }
    }
}
