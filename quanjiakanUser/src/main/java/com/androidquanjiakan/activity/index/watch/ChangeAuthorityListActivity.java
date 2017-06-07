package com.androidquanjiakan.activity.index.watch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.watch_child.WatchChildEntryActivity;
import com.androidquanjiakan.activity.setting.contact.AddContactActivity;
import com.androidquanjiakan.adapter.ChangeAuthorityAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.ChangeAuthorityEntity;
import com.androidquanjiakan.entity.ContactsBean;
import com.androidquanjiakan.result.ContactsResultBean;
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

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gin on 2017/2/23.
 */

public class ChangeAuthorityListActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibtn_back;
    private ListView listView;
    private ArrayList<ChangeAuthorityEntity> list;
    private Context context;
    private ChangeAuthorityAdapter changeAuthorityAdapter;
    private Button btn_comfirm;
    private Gson gson;
    private ContactsBeanDao dao;
    private String device_id;
    private ContactsBean contactsBean;
    private ContactsResultBean contactsResultBean;
    private ContactsResultBean.ResultsBean resultBean;
    private List<ContactsResultBean.ResultsBean.ContactsBean> contacts = new ArrayList<>();
    private String userId;
    private String type;
    public static final int RESULT_DEL = 1001;
    public static final int RESULT_MOVE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_authority);
        context = ChangeAuthorityListActivity.this;
        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        type = getIntent().getStringExtra("type");
        LogUtil.e("device_id------------"+device_id);
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
        if (list != null) {
            list.clear();
        }

        loadContactsData();


//        if (dao.loadAll().size() > 0) {
//            for (int i = 0; i < dao.loadAll().size(); i++) {
//                LogUtil.e("imei----------------"+dao.loadAll().get(i).getImei());
//                if (device_id.equals(dao.loadAll().get(i).getImei())) {
//                    contactsBean = dao.loadAll().get(i);
//                    String json = dao.loadAll().get(i).getJson();
//                    LogUtil.e("json---------" + json);
//                    contactsResultBean = gson.fromJson(json, ContactsResultBean.class);
//                    resultBean = contactsResultBean.getResults();
//                    if ("Contacts".equals(resultBean.getCategory())) {
//                        contacts = resultBean.getContacts();
//                        //遍历联系人
//                        for (ContactsResultBean.ResultsBean.ContactsBean con : contacts) {
//                            if ("1".equals(con.getApp())&&!"1".equals(con.getAdmin())) {
//                                ChangeAuthorityEntity entity = new ChangeAuthorityEntity();
//                                entity.setName(con.getName());
//                                entity.setApp(true);
//                                entity.setNumber(con.getTel());
//                                entity.setSelect(false);
//                                entity.setUserId(con.getUserid());
//                                list.add(entity);
//                            }
//
//                        }
//
//                    }
//                    changeAuthorityAdapter.setList(list);
//                    changeAuthorityAdapter.notifyDataSetChanged();
//                }
//            }
//
//        }
    }

    private void loadContactsData() {

        if (contacts!=null) {
            contacts.clear();
        }
        /**
         * 数据库没有数据时请求网络
         */

        MyHandler.putTask(ChangeAuthorityListActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("contacts-----------------" + val);
                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                if (!jsonObject.has("code")) {
                    JsonObject result1 = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
                    if (device_id.equals(result1.get("IMEI").getAsString())) {
                        JsonArray jsonArray = result1.getAsJsonArray(ConstantClassFunction.CONTACTS);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject object = jsonArray.get(i).getAsJsonObject();

                            if ("1".equals(object.get("App").getAsString())&&"0".equals(object.get("Admin").getAsString())) {
                                ChangeAuthorityEntity entity = new ChangeAuthorityEntity();
                                LogUtil.e("app----------------");

                                entity.setName(object.get("Name").getAsString());
                                entity.setApp(true);
                                entity.setNumber(object.get("Tel").getAsString());
                                entity.setSelect(false);
                                entity.setUserId(object.get("Userid").getAsString());
                                list.add(entity);
                            }

                        }
                        changeAuthorityAdapter.setList(list);
                        changeAuthorityAdapter.notifyDataSetChanged();
                    }

                } else {

                    BaseApplication.getInstances().toast(ChangeAuthorityListActivity.this,"未查询到数据");
                }


            }
        }, HttpUrls.getContactsList() + "&imei=" + device_id, null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(ChangeAuthorityListActivity.this, "正在获取联系人数据")));
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


    private void initView() {
        listView = (ListView) findViewById(R.id.listview);
        list = new ArrayList<>();
//        for (int i=0;i<10;i++){
//            if(i%2==0) {
//                ChangeAuthorityEntity entity = new ChangeAuthorityEntity("232323242", true, false, "妈妈");
//                list.add(entity);
//            }else {
//                ChangeAuthorityEntity entity = new ChangeAuthorityEntity("132323242", false, false, "妈妈");
//                list.add(entity);
//            }
//        }

        changeAuthorityAdapter = new ChangeAuthorityAdapter(context, list);
        listView.setAdapter(changeAuthorityAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                for (int t = 0; t < list.size(); t++) {
                    if ((int) l == t) {

                        list.get((int) l).setSelect(true);
                        userId = list.get((int) l).getUserId();
                    } else {
                        list.get(t).setSelect(false);
                    }
                    changeAuthorityAdapter.notifyDataSetChanged();
                }
            }
        });

        btn_comfirm = (Button) findViewById(R.id.btn_comfirm);
        btn_comfirm.setOnClickListener(this);


    }

    private void initTitle() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("移交管理权限");


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.btn_comfirm:
                //请求网络
                MoveAdminister();


                break;
        }
    }

    private void MoveAdminister() {
        String str_req = "";

//        &userid=11107&adminuserid=11029&imei=352315052834187
        str_req = "&userid=" + userId + "&adminuserid=" + QuanjiakanSetting.getInstance().getUserId() + "&imei=" + device_id;

        MyHandler.putTask(ChangeAuthorityListActivity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {

                if (val!=null) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (jsonObject.has("code")&&"200".equals(jsonObject.get("code").getAsString())) {
                        //移交成功
                        BaseApplication.getInstances().toast(ChangeAuthorityListActivity.this,"权限移交成功!");

                        Intent intent = new Intent();
                        intent.putExtra("userId",userId);
                        if (type.equals("0")) {
                            setResult(RESULT_DEL, intent);
                        }else {
                            setResult(RESULT_MOVE, intent);
                        }

                        finish();

                    }else {
//                        userId = "";
                        BaseApplication.getInstances().toast(ChangeAuthorityListActivity.this,"权限移交失败!");
                    }
                }


            }
        }, HttpUrls.MoveAdmin() + str_req,null,Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(context)));

    }
}
