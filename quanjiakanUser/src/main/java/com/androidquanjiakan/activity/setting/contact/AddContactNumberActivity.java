package com.androidquanjiakan.activity.setting.contact;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.activity.setting.contact.bean.AddContactBean;
import com.androidquanjiakan.activity.setting.contact.bean.AddContactEvent;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.ContactsBean;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.result.ContactsResultBean;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.LogUtil;
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
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * 作者：Administrator on 2017/2/20 15:40
 * <p>
 * 邮箱：liuzj@hi-board.com
 */
public class AddContactNumberActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_name;
    private TextView tv_save;
    private EditText et_number;

    public static final String PARAMS_NAME = "name";
    public static final String PARAMS_ICON = "icon";
    public static final String ID = "id";
    public static final String TYPE = "type";
    private ImageView sys_contact;
    private ImageView iv_icon_add;
    private String usernumber;
    private String number;
    private String name;
    private String image;

    private ContactsBeanDao dao;
    //    private List<ContactsBean> list;
    private ContactsBean contactsBean;
    private Gson gson;
    private ContactsResultBean contactsResultBean;
    private ContactsResultBean.ResultsBean resultBean;
    private List<ContactsResultBean.ResultsBean.ContactsBean> contacts = new ArrayList<>();
    private String num;
    private ContactsResultBean.ResultsBean.ContactsBean contact;
    private int[] arr_image = {R.drawable.contact_pic_portrait, R.drawable.contacts_pic_mom, R.drawable.contacts_pic_grandpa,
            R.drawable.contacts_pic_grandma, R.drawable.contacts_pic_grandpa2, R.drawable.contacts_pic_grandma2
            , R.drawable.contacts_pic_sister, R.drawable.contacts_pic_borther, R.drawable.contacts_pic_custom};

    private String device_id;
    private AddContactBean.ContactBean contactBean;
    private AddContactBean addContactBean;
    private String sendName;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_add_contact_number);

        if (dao == null) {
            dao = getContactsBeanDao();
        }
        gson = new Gson();
        name = getIntent().getStringExtra(PARAMS_NAME);
        type = getIntent().getStringExtra(TYPE);
        try {
            sendName = URLEncoder.encode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        image = getIntent().getStringExtra(PARAMS_ICON);
        device_id = getIntent().getStringExtra(ID);

        initTitle();
        initData();
        initView();

    }

    private void initData() {
        if (type.equals("child")) {
            if (contacts != null) {
                contacts.clear();
            }
            if (NetUtil.isNetworkAvailable(AddContactNumberActivity.this)) {
                loadContactsData();
            } else {
                if (dao.loadAll().size() > 0) {
                    for (int i = 0; i < dao.loadAll().size(); i++) {
                        if (device_id.equals(dao.loadAll().get(i).getImei())) {
                            contactsBean = dao.loadAll().get(i);
                            String json = dao.loadAll().get(i).getJson();
                            LogUtil.e("json---------" + json);
                            contactsResultBean = gson.fromJson(json, ContactsResultBean.class);
                            resultBean = contactsResultBean.getResults();
                            if ("Contacts".equals(resultBean.getCategory())) {
                                contacts = resultBean.getContacts();
                            }
                        }
                    }

                } /*else {
                    contactsBean = new ContactsBean(null, "", device_id);//保存到数据库的
                    contactsResultBean = new ContactsResultBean();
                    resultBean = new ContactsResultBean.ResultsBean();
                    resultBean.setIMEI(device_id);
                    resultBean.setCategory(ConstantClassFunction.CONTACTS);
                    contacts = new ArrayList<>();
                }*/
            }

//            contact = new ContactsResultBean.ResultsBean.ContactsBean();
        }

//        if (dao.loadAll().size() > 0) {
//            for (int i = 0; i < dao.loadAll().size(); i++) {
//                if (device_id.equals(dao.loadAll().get(i).getImei())) {
//                    contactsBean = dao.loadAll().get(i);
//                    String json = dao.loadAll().get(i).getJson();
//                    LogUtil.e("json---------" + json);
//                    contactsResultBean = gson.fromJson(json, ContactsResultBean.class);
//                    resultBean = contactsResultBean.getResults();
//                    if ("Contacts".equals(resultBean.getCategory())) {
//                        contacts = resultBean.getContacts();
//
//
//                    }
//                    break;
//                }else if (i==dao.loadAll().size()-1) {
//                    contactsBean = new ContactsBean(null, "", device_id);//保存到数据库的
//                    contactsResultBean = new ContactsResultBean();
//
//                    resultBean = new ContactsResultBean.ResultsBean();
//                    resultBean.setIMEI(device_id);
//                    resultBean.setCategory(ConstantClassFunction.CONTACTS);
//                    contacts = new ArrayList<>();
//                }
//            }
//
//        }else {
//            contactsBean = new ContactsBean(null, "", device_id);//保存到数据库的
//            contactsResultBean = new ContactsResultBean();
//
//            resultBean = new ContactsResultBean.ResultsBean();
//            resultBean.setIMEI(device_id);
//            resultBean.setCategory(ConstantClassFunction.CONTACTS);
//            contacts = new ArrayList<>();
//        }

        /**
         * net
         */

//        loadContactsData();
//        contact = new ContactsResultBean.ResultsBean.ContactsBean();
    }

    private void loadContactsData() {
        /**
         * 数据库没有数据时请求网络
         */

        MyHandler.putTask(AddContactNumberActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("contacts-----------------" + val);
                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                if (!jsonObject.has("code")&&val.length()>2) {
                    JsonObject result1 = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
                    if (device_id.equals(result1.get("IMEI").getAsString())) {
//                        contactsBean = new ContactsBean(null, "", device_id);
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

//                } else {
//                    contactsBean = new ContactsBean(null, "", device_id);//保存到数据库的
//                    contactsResultBean = new ContactsResultBean();
//                    resultBean = new ContactsResultBean.ResultsBean();
//                    resultBean.setIMEI(device_id);
//                    resultBean.setCategory(ConstantClassFunction.CONTACTS);
//                    contacts = new ArrayList<>();
//
//                    BaseApplication.getInstances().toast("未查询到数据");
//                }


            }
        }, HttpUrls.getContactsList() + "&imei=" + device_id, null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(AddContactNumberActivity.this, "正在获取联系人数据")));
    }


    private void initView() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        et_number = (EditText) findViewById(R.id.et_number);
        tv_save = (TextView) findViewById(R.id.tv_save);
        et_number.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        sys_contact = (ImageView) findViewById(R.id.sys_contact);
        iv_icon_add = (ImageView) findViewById(R.id.iv_icon_add);

        sys_contact.setOnClickListener(this);

        tv_save.setOnClickListener(this);
        tv_name.setText(name);
        iv_icon_add.setImageResource(arr_image[Integer.parseInt(image)]);

    }

    private ImageButton ibtn_back;
    private TextView tv_title;

    private void initTitle() {

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title.setText("添加联系人号码");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
            Uri contactData = data.getData();
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);
            while (phone.moveToNext()) {
                usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                number = usernumber.replace(" ", "");
                et_number.setText(number);
                et_number.setSelection(number.length());
            }

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_save:
                // TODO: 2017/4/11 这里暂时把逻辑区分开
                if (type.equals("child")) {
                    if (!CheckUtil.isPhoneNumber(et_number.getText().toString())) {
                        BaseApplication.getInstances().toast(AddContactNumberActivity.this,"请输入正确的手机号码!");
                    } else {
                        //电话号码去重
                        if (contacts.size() > 0) {
                            for (int i = 0; i < contacts.size(); i++) {
                                if (et_number.getText().toString().equals(contacts.get(i).getTel())) {
                                    BaseApplication.getInstances().toast(AddContactNumberActivity.this,"该联系人号码已存在！");
                                    break;
                                }
                                if (sendName.equals(contacts.get(i).getName())) {
                                    BaseApplication.getInstances().toast(AddContactNumberActivity.this,"该联系人名字已存在！");
                                    break;
                                }
                                if (i == contacts.size() - 1) {
                                    sendAddContactReq();
                                }

                            }
                        } else {
                            sendAddContactReq();
                        }

                    }
                } else {
                    BaseApplication.getInstances().toast(AddContactNumberActivity.this,"老人添加联系人");
                }

                break;

            case R.id.sys_contact:
                startActivityForResult(new Intent(
                        Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);

                break;
        }

    }

    /**
     * 添加联系人请求
     */

    private void sendAddContactReq() {
        // TODO: 2017/2/24 这里做保存操作

        num = et_number.getText().toString().trim();
        addContactBean = new AddContactBean();
        addContactBean.setAction("Add");
        addContactBean.setCategory("Contacts");
        addContactBean.setIMEI(device_id);
        contactBean = new AddContactBean.ContactBean();
        contactBean.setTel(num);
        contactBean.setImage(image);
        contactBean.setName(sendName);

//        try {
//            contactBean.setName(URLEncoder.encode(name, "utf-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        addContactBean.setContact(contactBean);
        String json = gson.toJson(addContactBean);
        LogUtil.e("sendAddContactReq-------" + json + "--------long--------" + json.length());
        if (!BaseApplication.getInstances().isSDKConnected()) {
            BaseApplication.getInstances().toastLong(AddContactNumberActivity.this,"已与手表服务器断开连接!");
            return;
        }
        BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json.getBytes(), json.length());
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

    private ContactsBeanDao getContactsBeanDao() {
        return BaseApplication.getInstances().getDaoInstant().getContactsBeanDao();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddContactEvent(AddContactEvent event) {

        String json = event.getJson();
        LogUtil.e("onAddContactEvent------联系人----" + json);

        JsonObject jsonObject = new GsonParseUtil(json).getJsonObject();
        if (jsonObject.has(ConstantClassFunction.getRESULTS())) {
            if ("200".equals(jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS()).get("Code").getAsString())) {
//                saveData(jsonObject.get("Id").getAsString());
                BaseApplication.getInstances().toast(AddContactNumberActivity.this,"联系人添加成功！");

                finish();
            } else if ("10001".equals(jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS()).get("Code").getAsString())) {
                BaseApplication.getInstances().toast(AddContactNumberActivity.this,"设备不在线！");
            }

        }

    }

    private void saveData(String id) {

        contact.setId(id);
        contact.setImage(contactBean.getImage());
        contact.setTel(contactBean.getTel());
        contact.setName(sendName);
        contact.setAdmin("0");
        contact.setApp("0");
        //保存到数据库
        contacts.add(contact);
        resultBean.setContacts(contacts);
        resultBean.setNum(contacts.size());
        contactsResultBean.setResults(resultBean);
        String json = gson.toJson(contactsResultBean);
        LogUtil.e("Contactsjson---------" + json);
        contactsBean.setJson(json);

        if (dao.loadAll().size() > 0) {
            for (int i = 0; i < dao.loadAll().size(); i++) {
                if (dao.loadAll().get(i).getImei().equals(device_id)) {
                    dao.delete(contactsBean);
                }
            }
        }
        dao.insert(contactsBean);

        BaseApplication.getInstances().toast(AddContactNumberActivity.this,"联系人添加成功！");

        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        //取消EventBus
        EventBus.getDefault().unregister(this);
        super.onStop();

    }
}
