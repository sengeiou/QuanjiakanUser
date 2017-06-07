package com.androidquanjiakan.activity.setting.contact;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.index.watch.ChangeAuthorityListActivity;
import com.androidquanjiakan.activity.index.watch_child.elder.OldAddContactActivity;
import com.androidquanjiakan.activity.setting.contact.bean.ChangeContactsEvent;
import com.androidquanjiakan.activity.setting.contact.bean.ContactsChangeResultEvent;
import com.androidquanjiakan.activity.setting.contact.bean.DeleteContactBean;
import com.androidquanjiakan.activity.setting.contact.bean.UpdateContactBean;
import com.androidquanjiakan.adapter.ContactsAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.entity.BabyCardEntity;
import com.androidquanjiakan.entity.BindingRequestBean;
import com.androidquanjiakan.entity.ContactsBean;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.androidquanjiakan.result.ContactsResultBean;
import com.androidquanjiakan.util.BitmapUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.SignatureUtil;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.greendao.dao.BindingRequestBeanDao;
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
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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


/**
 * 作者：Administrator on 2017/2/17 18:21
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class WatchContactesActivity extends BaseActivity implements View.OnClickListener {

    private SwipeMenuListView listView;
    private Button btn_add;
    private ContactsAdapter contactsAdapter;
    //    private String[] arr_name = {"爸爸", "妈妈", "爷爷", "奶奶", "外公", "外婆", "哥哥", "姐姐"};
    private int[] arr_image = {R.drawable.contact_pic_portrait, R.drawable.contacts_pic_mom, R.drawable.contacts_pic_grandpa,
            R.drawable.contacts_pic_grandma, R.drawable.contacts_pic_grandpa2, R.drawable.contacts_pic_grandma2
            , R.drawable.contacts_pic_sister, R.drawable.contacts_pic_borther, R.drawable.contacts_pic_custom};
    private ImageView icon;

    public static final int TYPE_RELATION = 1;
    public static final int TYPE_ADD = 2;

    private static final int REQUEST_CODE_MOVE = 3;
    private static final int REQUEST_CODE_CHANGE = 4;
    private int pos = -1;
    private ContactsBeanDao dao;
    private ContactsBean contactsBean;
    private Gson gson;
    private ContactsResultBean contactsResultBean;
    private ContactsResultBean.ResultsBean resultBean;
    private List<ContactsResultBean.ResultsBean.ContactsBean> contacts;
    private List<ContactsBean> list;
    //    private ContactsResultBean.ResultBean.ContactsBean bean;
    private int position;
    private String device_id;
    private int button = -1;
    private String changeName;
    private String changeIcon;
    private TextView tv_watch_name;
    private TextView tv_watch_number;
    private boolean isUpdate = false;
    private int line = -1;
    private boolean isAdmin = false;
    private BindingRequestBeanDao bindingRequestBeanDao;
    private String admin;
    public static final String TYPE = "type";
    public static final String WATCH_PHONE_NUM = "num";
    public static final String WATCH_PHONE_NAME = "name";
    private String type;
    private String watch_phone_num;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_contact);

        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        type = getIntent().getStringExtra(TYPE);
        watch_phone_num = getIntent().getStringExtra(WATCH_PHONE_NUM);
        name = getIntent().getStringExtra(WATCH_PHONE_NAME);
        LogUtil.e("device_id----" + device_id);
        if (device_id == null || device_id.length() < 1) {
            BaseApplication.getInstances().toast(WatchContactesActivity.this, "传入参数异常!");
            finish();
            return;
        }
        dao = getContactsBeanDao();
        bindingRequestBeanDao = getBindingRequestBeanDao();
        gson = new Gson();
        initTitle();
        initView();


    }

    private BindingRequestBeanDao getBindingRequestBeanDao() {
        return BaseApplication.getInstances().getDaoInstant().getBindingRequestBeanDao();
    }

    private ContactsBeanDao getContactsBeanDao() {
        return BaseApplication.getInstances().getDaoInstant().getContactsBeanDao();
    }

    private void initView() {

        tv_watch_name = (TextView) findViewById(R.id.tv_watch_name);
        tv_watch_number = (TextView) findViewById(R.id.tv_watch_number);
        tv_watch_number.setText(watch_phone_num);
        if (name != null && name.contains("%")) {
            try {
                tv_watch_name.setText(URLDecoder.decode(name, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            tv_watch_name.setText(name);
        }
        btn_add = (Button) findViewById(R.id.btn_add);

        btn_add.setOnClickListener(this);

        contacts = new ArrayList<>();

        initListView();

    }

    private void initListView() {

        listView = (SwipeMenuListView) findViewById(R.id.listView);
        contactsAdapter = new ContactsAdapter(contacts, WatchContactesActivity.this);

        listView.setAdapter(contactsAdapter);


        /***********************************      侧滑菜单      ************************************/

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                switch (menu.getViewType()) {
                    case 0:
                        if (isAdmin) {
                            createMenu1(menu);
                        }

                        break;

                    case 1:
                        if (isAdmin) {
                            createMenu2(menu);
                        } else {
                            createMenu(menu);
                        }

                        break;

                    case 2:
                        createMenu(menu);
                        break;
                }

            }

            private void createMenu(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xda,
                        0x26, 0x26)));
                deleteItem.setWidth(QuanjiakanUtil.dip2px(WatchContactesActivity.this, 90));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);

            }

            private void createMenu1(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getApplicationContext());
                item1.setBackground(new ColorDrawable(Color.rgb(0x00, 0x66,
                        0xcc)));
                item1.setWidth(QuanjiakanUtil.dip2px(WatchContactesActivity.this, 100));
                item1.setTitle("移交管理员权限");
                item1.setTitleSize(18);
                item1.setTitleColor(Color.WHITE);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                item2.setBackground(new ColorDrawable(Color.rgb(0xda,
                        0x26, 0x26)));
                item2.setWidth(QuanjiakanUtil.dip2px(WatchContactesActivity.this, 90));
                item2.setTitle("删除");
                item2.setTitleSize(18);
                item2.setTitleColor(Color.WHITE);
                menu.addMenuItem(item2);
            }

            private void createMenu2(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getApplicationContext());
                item1.setBackground(new ColorDrawable(Color.rgb(0x00, 0x66,
                        0xcc)));
                item1.setWidth(QuanjiakanUtil.dip2px(WatchContactesActivity.this, 100));
                item1.setTitle("设为管理员");
                item1.setTitleSize(18);
                item1.setTitleColor(Color.WHITE);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                item2.setBackground(new ColorDrawable(Color.rgb(0xda,
                        0x26, 0x26)));
                item2.setWidth(QuanjiakanUtil.dip2px(WatchContactesActivity.this, 90));
                item2.setTitle("删除");
                item2.setTitleSize(18);
                item2.setTitleColor(Color.WHITE);
                menu.addMenuItem(item2);
            }

        };
        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ContactsResultBean.ResultsBean.ContactsBean item = contacts.get(position);
                button = 1;
                pos = position;
//
//                if (!isAdmin&&line==position) {
//
//                    BaseApplication.getInstances().toast(WatchContactesActivity.this,"您不是管理员，不能执行此操作");
//
//                }else if (isAdmin&&"1".equals(item.getAdmin())) {
//                    showDelManagerDialog();
//                } else {
//                    sendDeleteReq(item.getId());
//                }
                switch (menu.getViewType()) {
                    case 0:
                        if (isAdmin) {
                            if (index == 0) {
                                BaseApplication.getInstances().toast(WatchContactesActivity.this, "移交管理员权限");
                                toChangeAuthorityListActivity("1");
                            } else {
                                showDelManagerDialog();

//                                if (!isAdmin && line == position) {
//
//                                    BaseApplication.getInstances().toast(WatchContactesActivity.this, "您不是管理员，不能执行此操作");
//
//                                } else if (isAdmin && "1".equals(item.getAdmin())) {
//                                    showDelManagerDialog();
//                                } else {
//                                    sendDeleteReq(item.getId());
//                                }
                            }
                        }

                        break;

                    case 1:
//                        if (index == 0) {
//                            BaseApplication.getInstances().toast(WatchContactesActivity.this, "设为管理员");
//                        } else {
////                            ContactsResultBean.ResultsBean.ContactsBean item = contacts.get(position);
////                            button = 1;
////                            pos = position;
//
//                            if (!isAdmin && line == position) {
//
//                                BaseApplication.getInstances().toast(WatchContactesActivity.this, "您不是管理员，不能执行此操作");
//
//                            } else if (isAdmin && "1".equals(item.getAdmin())) {
//                                showDelManagerDialog();
//                            } else {
//                                sendDeleteReq(item.getId());
//                            }
//                        }
                        if (isAdmin) {
                            if (index == 0) {
                                MoveAdminister(item.getUserid());
                            } else {

//                                if (!isAdmin && line == position) {
//
//                                    BaseApplication.getInstances().toast(WatchContactesActivity.this, "您不是管理员，不能执行此操作");
//
//                                } else if (isAdmin && "1".equals(item.getAdmin())) {
//                                    showDelManagerDialog();
//                                } else {
//                                    sendDeleteReq(item.getId());
//                                }
                                sendDeleteReq(item.getId());
                            }
                        }else {
                            sendDeleteReq(item.getId());
                        }

                        break;

                    case 2:
                        sendDeleteReq(item.getId());

//                        if (!isAdmin && line == position) {
//
//                            BaseApplication.getInstances().toast(WatchContactesActivity.this, "您不是管理员，不能执行此操作");
//
//                        } else if (isAdmin && "1".equals(item.getAdmin())) {
//                            showDelManagerDialog();
//                        } else {
//                            sendDeleteReq(item.getId());
//                        }

                        break;
                }

                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                ContactsResultBean.ResultsBean.ContactsBean cbean = (ContactsResultBean.ResultsBean.ContactsBean) contactsAdapter.getItem(position);
                pos = position;
                String name = null;
                try {
                    name = URLDecoder.decode(cbean.getName(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                showEditDialog(name);
            }
        });

        contactsAdapter.setmItemOnClickListener(new ContactsAdapter.ItemOnClickListener() {

            @Override
            public void itemOnClickListener(View view, int pos) {
                icon = (ImageView) view;
                position = pos;
                showImageDialog();
            }
        });
    }

    private void MoveAdminister(final String userId) {
        String str_req = "";
        str_req = "&userid=" + userId + "&adminuserid=" + QuanjiakanSetting.getInstance().getUserId() + "&imei=" + device_id;

        MyHandler.putTask(WatchContactesActivity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {

                if (val!=null) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (jsonObject.has("code")&&"200".equals(jsonObject.get("code").getAsString())) {
                        //移交成功
                        isAdmin = false;
                        BaseApplication.getInstances().toast(WatchContactesActivity.this,"权限移交成功!");
                        initData();
                        initListView();

                    }else {
                        BaseApplication.getInstances().toast(WatchContactesActivity.this,"权限移交失败!");
                    }
                }


            }
        }, HttpUrls.MoveAdmin() + str_req,null,Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(WatchContactesActivity.this)));

    }


    private void sendDeleteReq(String id) {

        DeleteContactBean deleteContactBean = new DeleteContactBean();
        deleteContactBean.setIMEI(device_id);
        deleteContactBean.setCategory("Contacts");
        deleteContactBean.setAction("Delete");
        deleteContactBean.setId(id);
        String json = gson.toJson(deleteContactBean);
        LogUtil.e("Delete-----req---------" + json);

        if (!BaseApplication.getInstances().isSDKConnected()) {
            BaseApplication.getInstances().toastLong(WatchContactesActivity.this, "已与手表服务器断开连接!");
            return;
        }
        BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json.getBytes(), json.length());
    }

    /**
     * 绑定申请审核的dialog
     * 当有系统消息通知过来时显示  并把数据添加到通讯录
     */

    // TODO: 2017/3/1  当有系统消息通知过来时显示  并把数据添加到通讯录
    private void showApplyAuditDialog(final String fromId, String userName, String phone, final Long id, final int msgId, final String imei) {

        final Dialog dialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_manager, null);

        TextView title = (TextView) view.findViewById(R.id.tv_dialog_title);
        title.setText("绑定申请审核");

        view.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!BaseApplication.getInstances().isSDKConnected()) {
                    BaseApplication.getInstances().toastLong(WatchContactesActivity.this, "已与手表服务器断开连接!");
                    dialog.dismiss();
                    return;
                }
                /*
                {
                   "IMEI":"355637052788450",
                       "Category": "BindConfirmReq",
                       " Answer" : "Agree"
                   "MsgId":"13453"
                }
                 */
                dialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("IMEI", imei);
                    jsonObject.put("Category", "BindConfirmReq");
                    jsonObject.put("MsgId", msgId);
                    jsonObject.put("Answer", "Agree");
                    String json = jsonObject.toString();
                    BaseApplication.getInstances().getNattyClient().ntyBindConfirmReqClient(Long.parseLong(fromId),
                            Long.parseLong(imei, 16),
                            msgId,
                            json.getBytes(),
                            json.length());
                    bindingRequestBeanDao.deleteByKey(id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BaseApplication.getInstances().isSDKConnected()) {
                    BaseApplication.getInstances().toastLong(WatchContactesActivity.this, "已与手表服务器断开连接!");
                    dialog.dismiss();
                    return;
                }
                dialog.dismiss();
/**
 * {
 "IMEI":"355637052788450",
 "Category": "BindConfirmReq",
 "Answer":"Reject"
 "MsgId":"13453"
 }
 */
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("IMEI", imei);
                    jsonObject.put("Category", "BindConfirmReq");
                    jsonObject.put("MsgId", msgId);
                    jsonObject.put("Answer", "Reject");
                    String json = jsonObject.toString();
                    BaseApplication.getInstances().getNattyClient().ntyBindConfirmReqClient(Long.parseLong(fromId),
                            Long.parseLong(imei, 16),
                            msgId,
                            json.getBytes(),
                            json.length());
                    bindingRequestBeanDao.deleteByKey(id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        // TODO: 2017/2/23 这里要做处理
        if (userName.contains("%")) {
            try {
                content.setText(URLDecoder.decode(userName, "utf-8") + "(" + phone + ")" + "通过扫码申请绑定手机设备,是否确定审核通过?");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            content.setText(userName + "(" + phone + ")" + "通过扫码申请绑定手机设备,是否确定审核通过?");
        }


        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setContentView(view, lp);
        dialog.show();
    }

    /**
     * 成功移交管理员权限之后显示的dialog
     */
    private void showDelSuccessDialog(String userId) {

        final Dialog dialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_manager, null);

        TextView title = (TextView) view.findViewById(R.id.tv_dialog_title);
        title.setText("管理员删除");

        view.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDeleteReq(contacts.get(pos).getId());
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        // TODO: 2017/2/23 这里要做处理
        for (ContactsResultBean.ResultsBean.ContactsBean con : contacts) {
            if (userId.equals(con.getUserid())) {
                if (con.getName().contains("%")) {
                    try {
                        String name = URLDecoder.decode(con.getName(), "utf-8");
                        admin = name + "(" + con.getTel() + "),";

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    admin = con.getName() + "(" + con.getTel() + "),";

                }
                con.setAdmin("1");
                content.setText("您已将管理员权限移交至" + admin + "您已被删除!");


            }
        }


        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setContentView(view, lp);
        dialog.show();
    }

    private void showDelManagerDialog() {

        final Dialog dialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_manager, null);

        TextView title = (TextView) view.findViewById(R.id.tv_dialog_title);
        title.setText("管理员删除");

        view.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                toChangeAuthorityListActivity("0");
                //todo 如果移交了权限

            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                QuanjiakanUtil.showToast(WatchContactesActivity.this, getString(R.string.cancel));

            }
        });
        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        // TODO: 2017/2/23 这里要做处理
        content.setText(R.string.dialog_del_mag);


        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setContentView(view, lp);
        dialog.show();
    }

    private void toChangeAuthorityListActivity(String type) {
        Intent intent = new Intent(WatchContactesActivity.this, ChangeAuthorityListActivity.class);
        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, device_id);
        intent.putExtra("type",type);
        startActivityForResult(intent, REQUEST_CODE_MOVE);
    }

    private void showEditDialog(String name) {

        final Dialog dialog = new Dialog(WatchContactesActivity.this, R.style.MyDialogStyle);
        View view = LayoutInflater.from(WatchContactesActivity.this).inflate(R.layout.dialog_edit, null);

        TextView title = (TextView) view.findViewById(R.id.name);
        title.setText(name);

        //编辑关系
        view.findViewById(R.id.pinglun_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                toAddContactActivity(TYPE_RELATION);
            }
        });
        view.findViewById(R.id.pinglun_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //将布局设置给Dialog
        dialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    /**
     * 联系人数据
     */
    private void initData() {
        if (type.equals("child")) {
            if (contacts != null) {
                contacts.clear();
            }
            if (NetUtil.isNetworkAvailable(this)) {
                //网络请求
//                setInitValue();
                loadContactsData();
            } else {
                BaseApplication.getInstances().toast(WatchContactesActivity.this, "网络连接错误，请设置网络连接");
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
                                getAdminister();
                                contactsAdapter.setmData(contacts);
                                contactsAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                }
            }

        }
        //网络请求
//        setInitValue();
//
//
//        if (contacts != null) {
//            contacts.clear();
//        }
//        loadContactsData();

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
//                        contactsAdapter.setmData(contacts);
//                        contactsAdapter.notifyDataSetChanged();
//                    }
//                    isUpdate = true;
//                }
//            }
//
//            if (!isUpdate) {
//                LogUtil.e("有数据---------------------");
//                loadContactsData();
//            }
//
//        } else {
//            LogUtil.e("无数据---------------------");
//            loadContactsData();
//        }


    }

    /**
     * 判断当前用户是不是管理员
     */
    private void getAdminister() {

        if (contacts != null) {
            for (int i = 0; i < contacts.size(); i++) {
                if (contacts.get(i).getAdmin().equals("1")) {
                    LogUtil.e("jinlail---------1-------" + QuanjiakanSetting.getInstance().getUserId() + "");
                    line = i;
                    if (contacts.get(line).getUserid() != null && contacts.get(line).getUserid().equals(QuanjiakanSetting.getInstance().getUserId() + "")) {
                        LogUtil.e("jinlail---------2-------");
                        isAdmin = true;

                    }
                }
            }
        }

        //获取所有绑定申请数据
//        if (isAdmin && bindingRequestBeanDao.loadAll().size() > 0) {
//            for (int i = 0; i < bindingRequestBeanDao.loadAll().size(); i++) {
//                LogUtil.e("jinlail---------2-------");
//                BindingRequestBean bindingRequestBean = bindingRequestBeanDao.loadAll().get(i);
//                if (bindingRequestBean.getImei().equals(device_id)) {
//                    String json = bindingRequestBean.getJson();
//                    JsonObject jsonObject = new GsonParseUtil(json).getJsonObject();
//                    JsonObject result = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
//                    String userName = result.get("UserName").getAsString();
//                    String phone = result.get("Proposer").getAsString();
//                    int msgId = result.get("MsgId").getAsInt();
//                    //long proposerId, long devId, int msgId, byte[] json, int length
//                    String imei = result.get("IMEI").getAsString();
//                    Long id = bindingRequestBean.getId();
//                    String fromId = bindingRequestBean.getFromId();
//                    showApplyAuditDialog(fromId, userName, phone, id, msgId, imei);
//                }
//            }
//        }

    }

//    public void setInitValue() {
//
//        /**
//         * 请求网络获取
//         */
//        MyHandler.putTask(WatchContactesActivity.this, new Task(new HttpResponseInterface() {
//            @Override
//            public void handMsg(String val) {
//                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
//                if (!jsonObject.has("code")&&val.length()>2) {
//                    BabyCardEntity babyCardEntity = new Gson().fromJson(val, BabyCardEntity.class);
//                    BabyCardEntity.ResultsBean results = babyCardEntity.getResults();
//                    BabyCardEntity.ResultsBean.ChildrencardBean childrencard = results.getChildrencard();
//                    String name_ = childrencard.getName();
//                    if (name_ != null && name_.contains("%")) {
//                        try {
//                            tv_watch_name.setText(URLDecoder.decode(name_, "utf-8"));
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        tv_watch_name.setText(name_);
//                    }
////
////                    String phonenumber_ = childrencard.getPhonenumber();
////                    tv_watch_number.setText(phonenumber_);
//
//                }
//
//
//            }
//        }, HttpUrls.getBabyCard() + "&imei=" + device_id +
//                "&userid=" + QuanjiakanSetting.getInstance().getUserId(), null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(WatchContactesActivity.this, "正在加载数据...")
//        ));
//
//    }

    private void loadContactsData() {
        /**
         * 数据库没有数据时请求网络
         */

        MyHandler.putTask(WatchContactesActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("contacts-----------------" + val);
                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                if (!jsonObject.has("code")&&val.length()>2) {
                    JsonObject result1 = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
                    if (device_id.equals(result1.get("IMEI").getAsString())) {
                        contactsBean = new ContactsBean(null, "", device_id);
//                        contactsResultBean = new ContactsResultBean();
//                        resultBean = new ContactsResultBean.ResultsBean();
//                        resultBean.setIMEI(device_id);
//                        resultBean.setCategory(ConstantClassFunction.CONTACTS);
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
                        getAdminister();
                        contactsAdapter.setmData(contacts);
                        contactsAdapter.notifyDataSetChanged();

//                        saveData();
                        contactsBean.setJson(val);
                        if (dao.loadAll().size() > 0) {
                            for (int i = 0; i < dao.loadAll().size(); i++) {
                                ContactsBean bean = dao.loadAll().get(i);
                                if (bean.getImei().equals(device_id)) {
                                    dao.delete(bean);
                                }
                            }
                        }

                        dao.insert(contactsBean);
                    }

                } else {

                    BaseApplication.getInstances().toast(WatchContactesActivity.this, "未查询到数据");
                }


            }
        }, HttpUrls.getContactsList() + "&imei=" + device_id, null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(WatchContactesActivity.this, "正在获取联系人数据")));
    }

    private ImageButton ibtn_back;
    private TextView tv_title;

    private void initTitle() {

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title.setText("手表通讯录");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.btn_add:
                toAddContactActivity(TYPE_ADD);
                break;
        }
    }

    private void toAddContactActivity(int t) {
        Intent intent = new Intent();
        if (type.equals("child")) {
            intent.setClass(WatchContactesActivity.this, AddContactActivity.class);
        } else {
            intent.setClass(WatchContactesActivity.this, OldAddContactActivity.class);
        }

        intent.putExtra(AddContactActivity.ID, device_id);
//        intent.putExtra(AddContactActivity.WATCH,type);
        switch (t) {
            case TYPE_ADD:
                intent.putExtra(AddContactActivity.TYPE, 2);
                startActivity(intent);
                break;
            case TYPE_RELATION:
                intent.putExtra(AddContactActivity.TYPE, 1);
                startActivityForResult(intent, REQUEST_CODE_CHANGE);
                break;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initListView();

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


    private String mCurrentPhotoPath;

    private void showImageDialog() {

        final Dialog dialog = new Dialog(this, R.style.MyDialogStyle);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_watch_imag, null);
        //拍照
        view.findViewById(R.id.paizhao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                //从相机拍摄照片
                ImageCropHandler.getImageFromCamera(WatchContactesActivity.this, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        LogUtil.e("获取从照相机拍摄的照片路径:" + path);
                    }
                });
            }
        });

        //默认头像
//        view.findViewById(R.id.default_icon).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//
//            }
//        });

        //相册选择
        view.findViewById(R.id.select_xiance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog != null) {
                    dialog.dismiss();
                }
                //从相册选取照片
                ImageCropHandler.pickImage(WatchContactesActivity.this);
            }
        });

        view.findViewById(R.id.pinglun_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //将布局设置给Dialog
        dialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }


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
                            Uri u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
                                    tempFile.getAbsolutePath(), null, null));
                            // u就是拍摄获得的原始图片的uri，剩下的你想干神马坏事请便……
                            save2SmallImage(u);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(WatchContactesActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA:
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        ImageCropHandler.beginCrop(WatchContactesActivity.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                    }
                }
                break;
            case CommonRequestCode.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    ImageCropHandler.beginCrop(WatchContactesActivity.this, data.getData());
                }
            case CommonRequestCode.REQUEST_CROP:
                ImageCropHandler.handleCrop(null, resultCode, data, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        Bitmap smallBitmap = BitmapUtil.getSmallBitmap(mCurrentPhotoPath);
                        String filename = System.currentTimeMillis() + ".png";
                        String file_path = ImageUtils.saveBitmapToStorage(filename, smallBitmap);
                        uploadImage(icon, file_path, filename);
                    }
                });
                break;

            case REQUEST_CODE_MOVE:
                //移交权限
                if (resultCode == ChangeAuthorityListActivity.RESULT_DEL) {
                    // TODO: 2017/3/1  这里更之后审核进行对接
                    isAdmin = false;
                    String userId = data.getStringExtra("userId");

                    showDelSuccessDialog(userId);
                }
                break;

            case REQUEST_CODE_CHANGE://修改关系
                if (resultCode == RESULT_OK) {
                    changeName = data.getStringExtra("changename");
                    changeIcon = data.getStringExtra("changeicon");
                    button = 2;
                    ContactsResultBean.ResultsBean.ContactsBean bean = contacts.get(pos);
                    UpdateContactBean updateContactBean = new UpdateContactBean();
                    updateContactBean.setAction("Update");
                    updateContactBean.setCategory("Contacts");
                    updateContactBean.setIMEI(device_id);
                    UpdateContactBean.ContactBean contactBean = new UpdateContactBean.ContactBean();
                    contactBean.setApp(bean.getApp());
                    contactBean.setAdmin(bean.getAdmin());
                    contactBean.setId(bean.getId());
                    contactBean.setTel(bean.getTel());
                    if (changeName != null && changeName.contains("%")) {
                        contactBean.setName(changeName);
                    }
//                    try {
//                        contactBean.setName(URLEncoder.encode(changeName, "UTF-8"));
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
                    contactBean.setImage(changeIcon);


                    updateContactBean.setContact(contactBean);
                    String json = gson.toJson(updateContactBean);
                    LogUtil.e("Update----------更改关系----------" + json);

                    if (!BaseApplication.getInstances().isSDKConnected()) {
                        BaseApplication.getInstances().toastLong(WatchContactesActivity.this, "已与手表服务器断开连接!");
                        return;
                    }
                    BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json.getBytes(), json.length());
                }

                break;

        }
    }


    private Bitmap temp;
    private String sourcePath;
    private String mLastNetPath = null;

    /**
     * 图片压缩并保存为临时文件
     *
     * @param data
     */
    public void save2SmallImage(Uri data) {
        if (data == null) {
            return;
        }
        sourcePath = ImageUtils.uri2Path(data, WatchContactesActivity.this);
        String smallImagePath = ImageUtils.saveBitmapToStorage(SignatureUtil.getMD5String(sourcePath) + ".jpg", BitmapUtil.getSmallBitmap(sourcePath));
        if (temp != null) {
            temp.recycle();
            temp = null;
        }
        temp = BitmapFactory.decodeFile(smallImagePath);
    }

    /**
     * 上传图片
     *
     * @param head_image
     * @param path
     * @param filename
     */

    protected void uploadImage(final ImageView head_image, final String path, String filename) {
        HashMap<String, String> params = new HashMap<>();
        params.put("file", path.toString());
        params.put("filename", filename);
        params.put("image", path);
        MyHandler.putTask(new Task(new HttpResponseInterface() {


            @Override
            public void handMsg(String val) {
                LogUtil.e("url--------"+val);
                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                if (jsonObject.has("code") && "200".equals(jsonObject.get("code").getAsString())) {
                    String strImag = jsonObject.get("message").getAsString();
                    mLastNetPath = strImag;
                    if (head_image != null) {
                        head_image.setTag(strImag);
                        //圆形图片
//                        Picasso.with(getApplicationContext()).load(strImag).transform(new CircleTransformation())
//                                .into(head_image);

                        ContactsResultBean.ResultsBean.ContactsBean bean = contacts.get(position);
                        button = 3;

                        UpdateContactBean updateContactBean = new UpdateContactBean();
                        updateContactBean.setAction("Update");
                        updateContactBean.setCategory("Contacts");
                        updateContactBean.setIMEI(device_id);
                        UpdateContactBean.ContactBean contactBean = new UpdateContactBean.ContactBean();
                        if (!bean.getName().contains("%")) {
                            try {
                                contactBean.setName(URLEncoder.encode(bean.getName(), "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            contactBean.setName(bean.getName());
                        }

                        contactBean.setId(bean.getId());
                        contactBean.setTel(bean.getTel());
                        contactBean.setImage(mLastNetPath);
                        updateContactBean.setContact(contactBean);
                        String json = gson.toJson(updateContactBean);

                        LogUtil.e("Update-----头像---------" + json);

                        if (!BaseApplication.getInstances().isSDKConnected()) {
                            BaseApplication.getInstances().toastLong(WatchContactesActivity.this, "已与手表服务器断开连接!");
                            return;
                        }
                        BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(Long.parseLong(device_id, 16), json.getBytes(), json.length());


                    }
                } else {
                    Toast.makeText(WatchContactesActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
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
        }, HttpUrls.postFile() + "&storage=2", params, Task.TYPE_POST_FILE, QuanjiakanDialog.getInstance().getDialog(WatchContactesActivity.this, "正在上传图片！")));
    }


    //上传数据到服务器，保存数据....
//    private void saveData() {
//        //保存到数据库
//        resultBean.setContacts(contacts);
//        resultBean.setNum(contacts.size());
//        contactsResultBean.setResults(resultBean);
//        String json = gson.toJson(contactsResultBean);
//        LogUtil.e("Contactsjson---------" + json);
//        contactsBean.setJson(json);
//        if (dao.loadAll().size()>0) {
//            for (int i=0;i<dao.loadAll().size();i++) {
//                ContactsBean bean = dao.loadAll().get(i);
//                if (bean.getImei().equals(device_id)) {
//                    dao.delete(bean);
//                }
//            }
//        }
//
//        dao.insert(contactsBean);
//
//
//    }

    private void upDateData() {
        resultBean.setContacts(contacts);
        resultBean.setNum(contacts.size());
        contactsResultBean.setResults(resultBean);
        String json = gson.toJson(contactsResultBean);
        LogUtil.e("Contactsjson---------" + json);
        contactsBean.setJson(json);
        dao.update(contactsBean);

        button = -1;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeContactsEvent(ChangeContactsEvent event) {
        String json = event.getJson();
        JsonObject obj = new GsonParseUtil(json).getJsonObject();
        if (obj.has(ConstantClassFunction.getRESULTS())) {
            JsonObject object = obj.getAsJsonObject(ConstantClassFunction.getRESULTS());
            if (object.has(ConstantClassFunction.CODE) && "200".equals(object.get(ConstantClassFunction.CODE).getAsString())) {
                switch (button) {
                    case 1://删除

                        LogUtil.e("主动——————删除——————");
                        contacts.remove(pos);
                        contactsAdapter.setmData(contacts);
                        contactsAdapter.notifyDataSetChanged();
//                        upDateData();
                        break;

                    case 2://更改关系

                        LogUtil.e("主动——————更改关系——————");
                        ContactsResultBean.ResultsBean.ContactsBean bean = contacts.get(pos);
                        bean.setImage(changeIcon);
                        bean.setName(changeName);

                        contactsAdapter.setmData(contacts);
                        contactsAdapter.notifyDataSetChanged();
//                        upDateData();
                        break;

                    case 3://更改头像
                        LogUtil.e("主动——————更改头像——————");
                        ContactsResultBean.ResultsBean.ContactsBean contact = contacts.get(position);
                        contact.setImage(mLastNetPath);

                        contactsAdapter.setmData(contacts);
                        contactsAdapter.notifyDataSetChanged();
//                        upDateData();
                        break;


                }
            } else {
                switch (button) {
                    case 1://删除
                        BaseApplication.getInstances().toast(WatchContactesActivity.this, "联系人删除失败！");
                        break;

                    case 2://更改关系
                        BaseApplication.getInstances().toast(WatchContactesActivity.this, "更改关系失败！");
                        break;

                    case 3://更改头像
                        BaseApplication.getInstances().toast(WatchContactesActivity.this, "更改头像失败！");
                        break;


                }

                button = -1;

            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onContactsChangeResultEvent(ContactsChangeResultEvent event) {

        String data = event.getJson();
        LogUtil.e("contacts__________别人修改______" + data);
//  {"Results":{"IMEI":"355637053995130","Category":"Contacts","Action":"Add","Contacts":{"Id":"271","Name":"%E7%81%B6%E5%86%9B","Image":"8","Tel":"13042078849"}}}
        JsonObject jsonObject = new GsonParseUtil(data).getJsonObject();
        if (!jsonObject.has(ConstantClassFunction.getRESULTS())) {


//            if (jsonObject.has(ConstantClassFunction.getCATEGORY())){
//                switch (jsonObject.get(ConstantClassFunction.getCATEGORY()).getAsString()) {
//                    case ConstantClassFunction.CONTACTS:
//                        if (jsonObject.get(ConstantClassFunction.ACTION).getAsString().equals(ConstantClassFunction.DELETE)){
//                            for (ContactsResultBean.ResultsBean.ContactsBean c : contacts){
//                                if (c.getId().equals(jsonObject.get("Id").getAsString())){
//                                    contacts.remove(c);
//                                }
//                            }
//
//                        }
//                        break;
//
//
//
//
//                    default:
//                        break;
//                }
//            }
//        }else {
            JsonObject result = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
            if (device_id.equals(result.get("IMEI").getAsString())) {
                String category = result.get(ConstantClassFunction.getCATEGORY()).getAsString();
                switch (category) {
                    case ConstantClassFunction.CONTACTS:
                        JsonObject contact = result.getAsJsonObject(ConstantClassFunction.CONTACT);
                        if (ConstantClassFunction.ADD.equals(result.get(ConstantClassFunction.ACTION).getAsString())) {

                            ContactsResultBean.ResultsBean.ContactsBean bean = new ContactsResultBean.ResultsBean.ContactsBean();

                            bean.setApp("0");
                            bean.setAdmin("0");
                            bean.setImage(contact.get("Image").getAsString());
                            bean.setName(contact.get("Name").getAsString());
                            bean.setTel(contact.get("Tel").getAsString());
                            bean.setId(contact.get("Id").getAsString());

                            contacts.add(bean);

                        } else if (ConstantClassFunction.UPDATE.equals(result.get(ConstantClassFunction.ACTION).getAsString())) {
                            for (ContactsResultBean.ResultsBean.ContactsBean c : contacts) {
                                if (c.getId().equals(contact.get("Id").getAsString())) {

                                    c.setImage(contact.get("Image").getAsString());
                                    c.setName(contact.get("Name").getAsString());
                                    c.setTel(contact.get("Tel").getAsString());

                                }
                            }

                        } else if (ConstantClassFunction.DELETE.equals(result.get(ConstantClassFunction.ACTION).getAsString())) {
                            for (ContactsResultBean.ResultsBean.ContactsBean c : contacts) {
                                if (c.getId().equals(jsonObject.get("Id").getAsString())) {
                                    contacts.remove(c);
                                }
                            }

                        }

                        contactsAdapter.setmData(contacts);
                        contactsAdapter.notifyDataSetChanged();
                        break;


                }
            }

        }


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
