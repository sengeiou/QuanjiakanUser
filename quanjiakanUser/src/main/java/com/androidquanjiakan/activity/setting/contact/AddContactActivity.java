package com.androidquanjiakan.activity.setting.contact;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.index.watch_child.elder.EditRemindActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.result.ContactsResultBean;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.EditTextFilter;
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
import com.quanjiakanuser.util.KeyBoardUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Administrator on 2017/2/20 11:52
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class AddContactActivity extends BaseActivity implements View.OnClickListener {

    private Button next;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private ImageView image5;
    private ImageView image6;
    private ImageView image7;
    private ImageView image8;
    private ImageView image9;
    private LinearLayout llt_1;
    private LinearLayout llt_2;
    private LinearLayout llt_3;
    private LinearLayout llt_4;
    private LinearLayout llt_5;
    private LinearLayout llt_6;
    private LinearLayout llt_7;
    private LinearLayout llt_8;
    private LinearLayout llt_9;

    public static final String TYPE = "type";
    public static final String ID = "id";

    private List<ImageView> imageViews = new ArrayList<>();
    private int flag;
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private TextView text5;
    private TextView text6;
    private TextView text7;
    private TextView text8;
    private TextView text9;
    private String name;
    private EditText et_diy;
    private RelativeLayout temp;
    private TextView tv_sure;

    private int[] arr_image = {R.drawable.contact_pic_portrait, R.drawable.contacts_pic_mom, R.drawable.contacts_pic_grandpa,
            R.drawable.contacts_pic_grandma, R.drawable.contacts_pic_grandpa2, R.drawable.contacts_pic_grandma2
            , R.drawable.contacts_pic_sister, R.drawable.contacts_pic_borther, R.drawable.contacts_pic_custom};
    private String image;
    private String device_id;
    private ContactsBeanDao dao;
    private List<ContactsResultBean.ResultsBean.ContactsBean> contacts = new ArrayList<>();
    private Gson gson;
    private ContactsResultBean contactsResultBean;
    private ContactsResultBean.ResultsBean resultBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_add_contact);
        flag = getIntent().getIntExtra(TYPE, -1);
        device_id = getIntent().getStringExtra(ID);

        if (dao == null) {
            dao = getContactsBeanDao();
        }
        gson = new Gson();
        initTitle();

        initView();

        initData();
    }

    private ContactsBeanDao getContactsBeanDao() {
        return BaseApplication.getInstances().getDaoInstant().getContactsBeanDao();
    }


    private void initData() {

        if (contacts != null) {
            contacts.clear();
        }
        if (NetUtil.isNetworkAvailable(this)) {
            loadContactsData();
        } else {
            if (dao.loadAll().size() > 0) {
                for (int i = 0; i < dao.loadAll().size(); i++) {
                    if (device_id.equals(dao.loadAll().get(i).getImei())) {
                        String json = dao.loadAll().get(i).getJson();
                        LogUtil.e("json---------" + json);
                        contactsResultBean = gson.fromJson(json, ContactsResultBean.class);
                        resultBean = contactsResultBean.getResults();
                        if ("Contacts".equals(resultBean.getCategory())) {
                            contacts = resultBean.getContacts();

                        }
                    }
                }
            }
        }

//        if (dao.loadAll().size() > 0) {
//            for (int i = 0; i < dao.loadAll().size(); i++) {
//                if (device_id.equals(dao.loadAll().get(i).getImei())) {
//                    String json = dao.loadAll().get(i).getJson();
//                    LogUtil.e("json---------" + json);
//                    contactsResultBean = gson.fromJson(json, ContactsResultBean.class);
//                    resultBean = contactsResultBean.getResults();
//                    if ("Contacts".equals(resultBean.getCategory())) {
//                        contacts = resultBean.getContacts();
//
//                    }
//                }
//            }
//        }
    }

    private void loadContactsData() {
        /**
         * 数据库没有数据时请求网络
         */

        MyHandler.putTask(AddContactActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("contacts-----------------" + val);
                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                if (!jsonObject.has("code")&&val.length()>2) {
                    JsonObject result1 = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
                    if (device_id.equals(result1.get("IMEI").getAsString())) {
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
                            contacts.add(contactsBean);

                        }
//                        saveData();

                    }

                } else {

                    BaseApplication.getInstances().toast(AddContactActivity.this,"未查询到数据");
                }


            }
        }, HttpUrls.getContactsList() + "&imei=" + device_id, null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(AddContactActivity.this, "正在获取联系人数据")));
    }


    private ImageButton ibtn_back;
    private TextView tv_title;

    private void initTitle() {

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title.setText("添加手表联系人");

    }

    private void initView() {
        next = (Button) findViewById(R.id.btn_next);
        next.setOnClickListener(this);
        if (flag == 1) {
            next.setVisibility(View.GONE);
        } else {
            next.setVisibility(View.VISIBLE);
        }


        /**
         * 图标
         */
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        image4 = (ImageView) findViewById(R.id.image4);
        image5 = (ImageView) findViewById(R.id.image5);
        image6 = (ImageView) findViewById(R.id.image6);
        image7 = (ImageView) findViewById(R.id.image7);
        image8 = (ImageView) findViewById(R.id.image8);
        image9 = (ImageView) findViewById(R.id.image9);
        imageViews.add(image1);
        imageViews.add(image2);
        imageViews.add(image3);
        imageViews.add(image4);
        imageViews.add(image5);
        imageViews.add(image6);
        imageViews.add(image7);
        imageViews.add(image8);
        imageViews.add(image9);
        for (int i = 0; i < imageViews.size(); i++) {
            imageViews.get(i).setBackgroundResource(arr_image[i]);
        }


        llt_1 = (LinearLayout) findViewById(R.id.llt_1);
        llt_2 = (LinearLayout) findViewById(R.id.llt_2);
        llt_3 = (LinearLayout) findViewById(R.id.llt_3);
        llt_4 = (LinearLayout) findViewById(R.id.llt_4);
        llt_5 = (LinearLayout) findViewById(R.id.llt_5);
        llt_6 = (LinearLayout) findViewById(R.id.llt_6);
        llt_7 = (LinearLayout) findViewById(R.id.llt_7);
        llt_8 = (LinearLayout) findViewById(R.id.llt_8);
        llt_9 = (LinearLayout) findViewById(R.id.llt_9);

        llt_1.setOnClickListener(this);
        llt_2.setOnClickListener(this);
        llt_3.setOnClickListener(this);
        llt_4.setOnClickListener(this);
        llt_5.setOnClickListener(this);
        llt_6.setOnClickListener(this);
        llt_7.setOnClickListener(this);
        llt_8.setOnClickListener(this);
        llt_9.setOnClickListener(this);
        /*if (flag == 1) {
            llt_9.setEnabled(false);
        } else {
            llt_9.setEnabled(true);
        }*/

        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);
        text4 = (TextView) findViewById(R.id.text4);
        text5 = (TextView) findViewById(R.id.text5);
        text6 = (TextView) findViewById(R.id.text6);
        text7 = (TextView) findViewById(R.id.text7);
        text8 = (TextView) findViewById(R.id.text8);
        text9 = (TextView) findViewById(R.id.text9);

        et_diy = (EditText) findViewById(R.id.et_diy);
//        EditTextFilter.setEditTextInhibitInputSpace(et_diy);
        temp = (RelativeLayout) findViewById(R.id.llt_pinglun);
        tv_sure = (TextView) findViewById(R.id.tv_sure);

        temp.setVisibility(View.GONE);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.btn_next:
                toAddContactNumberActivity();
                break;

            case R.id.llt_1:
                KeyBoardUtils.closeKeybord(et_diy, AddContactActivity.this);
                temp.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 0) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                        if (flag == 1) {

                            changeRelation(text1.getText().toString(), "0");

                        }
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                name = text1.getText().toString();
                image = "0";
                break;

            case R.id.llt_2:
                KeyBoardUtils.closeKeybord(et_diy, AddContactActivity.this);
                temp.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 1) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                        if (flag == 1) {
                            changeRelation(text2.getText().toString(), "1");
                        }
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                name = text2.getText().toString();
                image = "1";
                break;

            case R.id.llt_3:
                KeyBoardUtils.closeKeybord(et_diy, AddContactActivity.this);
                temp.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 2) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                        if (flag == 1) {
                            changeRelation(text3.getText().toString(), "2");
                        }
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                name = text3.getText().toString();
                image = "2";
                break;

            case R.id.llt_4:
                KeyBoardUtils.closeKeybord(et_diy, AddContactActivity.this);
                temp.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 3) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                        if (flag == 1) {
                            changeRelation(text4.getText().toString(), "3");
                        }
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                name = text4.getText().toString();
                image = "3";
                break;

            case R.id.llt_5:
                KeyBoardUtils.closeKeybord(et_diy, AddContactActivity.this);
                temp.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 4) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                        if (flag == 1) {
                            changeRelation(text5.getText().toString(), "4");
                        }
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                name = text5.getText().toString();
                image = "4";
                break;

            case R.id.llt_6:
                KeyBoardUtils.closeKeybord(et_diy, AddContactActivity.this);
                temp.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 5) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                        if (flag == 1) {
                            changeRelation(text6.getText().toString(), "5");
                        }
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                name = text6.getText().toString();
                image = "5";
                break;

            case R.id.llt_7:
                KeyBoardUtils.closeKeybord(et_diy, AddContactActivity.this);
                temp.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 6) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                        if (flag == 1) {
                            changeRelation(text7.getText().toString(), "6");
                        }
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                name = text7.getText().toString();
                image = "6";
                break;
            case R.id.llt_8:
                KeyBoardUtils.closeKeybord(et_diy, AddContactActivity.this);
                temp.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 7) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                        if (flag == 1) {
                            changeRelation(text8.getText().toString(), "7");
                        }
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                name = text8.getText().toString();
                image = "7";
                break;
            case R.id.llt_9:
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 8) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);

                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                temp.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);
                KeyBoardUtils.openKeybord(et_diy, AddContactActivity.this);
                et_diy.setHint(text9.getText().toString());

                tv_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((CheckUtil.isAllChineseChar(et_diy.getText().toString().trim()) && et_diy.getText().toString().trim().length() > 4)) {
                            Toast.makeText(AddContactActivity.this, "超过限定长度，请输入四个中文或者八个英文字母", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (et_diy.getText().toString().trim().length() > 8) {
                            Toast.makeText(AddContactActivity.this, "超过限定长度，请输入四个中文或者八个英文字母", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(et_diy.getText().toString())) {
                            BaseApplication.getInstances().toast(AddContactActivity.this,"请输入名称");
                            return;

                        }

                        if (EditTextFilter.containsSpace(et_diy.getText().toString())||EditTextFilter.containsEmoji(et_diy.getText().toString().trim())||EditTextFilter.containsUnChar(et_diy.getText().toString().trim())) {
                            BaseApplication.getInstances().toast(AddContactActivity.this,"含有非法字符！");
                            et_diy.setText("");
                        } else if (et_diy.getText().toString().getBytes().length > 12) {
                            BaseApplication.getInstances().toast(AddContactActivity.this,"超过限定长度，请输入四个中文或者八个英文字母");
                            et_diy.setText("");
                        } else {
                            name = et_diy.getText().toString().trim().replaceAll(" ", "");
                            temp.setVisibility(View.GONE);
//                            next.setVisibility(View.VISIBLE);
                            KeyBoardUtils.closeKeybord(et_diy, AddContactActivity.this);
                            if(flag==1) {
                                changeRelation(name,"8");
                            }else {
                                toAddContactNumberActivity();
                            }

                        }


                    }
                });
                // TODO: 2017/2/23 自定义
                name = text9.getText().toString();
                image = "8";
                break;


        }

    }

    private void changeRelation(String name, String icon) {

        try {
            String change_name = URLEncoder.encode(name, "utf-8");
            for (int j = 0; j < contacts.size(); j++) {
                if (contacts.get(j).getName().equals(change_name)) {
                    BaseApplication.getInstances().toast(AddContactActivity.this,"该联系人名字已存在！");
                    break;
                } else if (j == contacts.size() - 1) {
                    Intent intent = new Intent();
                    intent.putExtra("changename", change_name);
                    intent.putExtra("changeicon", icon);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void toAddContactNumberActivity() {

        if (name != null && !TextUtils.isEmpty(name)) {
            Intent intent = new Intent(AddContactActivity.this, AddContactNumberActivity.class);

            intent.putExtra(AddContactNumberActivity.PARAMS_NAME, name);
            intent.putExtra(AddContactNumberActivity.PARAMS_ICON, image);
            intent.putExtra(AddContactNumberActivity.ID, device_id);
            intent.putExtra(AddContactNumberActivity.TYPE, "child");
            startActivity(intent);
            finish();
        }

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
