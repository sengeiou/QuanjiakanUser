package com.androidquanjiakan.activity.setting.contact;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.adapter.MsgRecordAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.BroadCastBean;
import com.androidquanjiakan.entity.WatchMsgRecordEntity;
import com.androidquanjiakan.result.ContactsResultBean;
import com.androidquanjiakan.util.LogUtil;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.greendao.dao.BroadCastBeanDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 作者：Administrator on 2017/2/20 16:55
 * <p>
 * 邮箱：liuzj@hi-board.com
 */
public class MessageRecordActivity extends BaseActivity implements View.OnClickListener {

    private SwipeMenuListView listView;
    private ImageView nonedata;
    private TextView nonedatahint;

    private List<WatchMsgRecordEntity> datas;
    private List<BroadCastBean> list = new ArrayList<>();
    private MsgRecordAdapter adapter;

    private SimpleDateFormat formatter;
    private BroadCastBeanDao dao;
    private String device_id;
    private String imagepath;
    private String watchName;


//    private ContactsResultBean.ResultsBean resultBean;
    private List<ContactsResultBean.ResultsBean.ContactsBean> contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_record);

        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        imagepath = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_IMAGEPATH);
        watchName = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_NAME);
        if(device_id==null){
            BaseApplication.getInstances().toast(MessageRecordActivity.this,"传入参数异常!");
            finish();
            return;
        }
        dao = getBroadCastBeanDao();
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        initTitle();
        initView();


    }

    private void getContacts() {

        if (contacts!=null) {
            contacts.clear();
        }

        MyHandler.putTask(MessageRecordActivity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("contacts-----------------"+val);
                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                if (!jsonObject.has("code")&&val.length()>2) {
                    JsonObject result1 = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
                    if (device_id.equals(result1.get("IMEI").getAsString())) {
                        JsonArray jsonArray = result1.getAsJsonArray(ConstantClassFunction.CONTACTS);
                        for (int i =0;i<jsonArray.size();i++) {
                            JsonObject object = jsonArray.get(i).getAsJsonObject();
                            ContactsResultBean.ResultsBean.ContactsBean contactsBean = new ContactsResultBean.ResultsBean.ContactsBean();
                            if (object.has("Admin")){
                                contactsBean.setAdmin(object.get("Admin").getAsString());
                            }

                            if (object.has("App")){
                                contactsBean.setApp(object.get("App").getAsString());
                            }

                            if (object.has("Id")){
                                contactsBean.setId(object.get("Id").getAsString());
                            }


                            if (object.has("Image")){
                                contactsBean.setImage(object.get("Image").getAsString());
                            }

                            if (object.has("Name")){
                                contactsBean.setName(object.get("Name").getAsString());
                            }

                            if (object.has("Tel")){
                                contactsBean.setTel(object.get("Tel").getAsString());
                            }

                            if (object.has("Userid")) {
                                contactsBean.setUserid(object.get("Userid").getAsString());
                            }

                            contacts.add(contactsBean);

                        }
                    }

                }else {

                    BaseApplication.getInstances().toast(MessageRecordActivity.this,"未查询到数据");
                }


            }
        }, HttpUrls.getContactsList() + "&imei=" +device_id,null,Task.TYPE_GET_STRING_NOCACHE, null));
    }

    private BroadCastBeanDao getBroadCastBeanDao() {
        return BaseApplication.getInstances().getDaoInstant().getBroadCastBeanDao();
    }

    private ImageButton ibtn_back;
    private TextView tv_title;

    private void initTitle() {

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title.setText("消息记录");


    }


    private void initView() {
        listView = (SwipeMenuListView) findViewById(R.id.listView);
        nonedata = (ImageView) findViewById(R.id.nonedata);
        nonedatahint = (TextView) findViewById(R.id.nonedatahint);

        datas = new ArrayList<WatchMsgRecordEntity>();

        adapter = new MsgRecordAdapter(datas, MessageRecordActivity.this);
        listView.setAdapter(adapter);

        /***********************************      侧滑菜单      ************************************/

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                createMenu(menu);
            }

            private void createMenu(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xda,
                        0x26, 0x26)));
                deleteItem.setWidth(QuanjiakanUtil.dip2px(MessageRecordActivity.this, 75));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);

            }

        };

        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                // TODO: 2017/2/20 这里做删除操作
                datas.remove(position);
                adapter.notifyDataSetChanged();
//                dao.delete(contacts.get());
                dao.delete(list.get(position));
                if (list.size()>0) {
                    showNoneDataImage(false);
                }else {
                    showNoneDataImage(true);
                }
                return false;
            }
        });
    }

    /**
     * 消息记录数据
     */
    private void initData() {
        if (list!=null) {
            list.clear();
        }

        if (dao.loadAll().size() > 0) {
            for (int i = dao.loadAll().size()-1; i >=0 ; i--) {

                LogUtil.e("-------BroadCastBean" + dao.loadAll().get(i).getJson() +"--------" + dao.loadAll().get(i).getFromId());
                if (device_id.equals(dao.loadAll().get(i).getImei())) {
                    BroadCastBean broadCastBean = dao.loadAll().get(i);
                    list.add(broadCastBean);
                    String fromId = broadCastBean.getFromId();
                    String name = getNameByFromId(fromId);
                    String time = broadCastBean.getTime();
                    Date date = new Date(Long.parseLong(time));
                    /**
                     * 根据fromId查询姓名
                     */

                    String json = broadCastBean.getJson();
                    /**
                     * 解析json {"Results":{"IMEI":"355637053995130","Category":"RunTime","RunTime":{"AutoConnection":"0"}}}
                     * {"Results":{"IMEI":"355637053995130","Category":"RunTime","RunTime":{"AutoConnection":"1"}}}
                     */

                    JsonObject jsonObject = new GsonParseUtil(json).getJsonObject();
                    WatchMsgRecordEntity bean = new WatchMsgRecordEntity();
                    // TODO: 2017/3/25 这里要设置宝贝的名字头像
                    bean.setImage(imagepath);
                    if (watchName.contains("%")) {
                        try {
                            String name_ = URLDecoder.decode(watchName, "utf-8");
                            bean.setTitle(name_ + "手表设置变更");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }else {
                        bean.setTitle(watchName + "手表设置变更");
                    }

                    bean.setTime(formatter.format(date));
                    if (jsonObject.has(ConstantClassFunction.getRESULTS())) {
                        JsonObject result = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
                        if (result.has(ConstantClassFunction.getCATEGORY())) {
                            String category = result.get(ConstantClassFunction.getCATEGORY()).getAsString();

                            switch (category) {

                                case ConstantClassFunction.RUNTIME://运行时参数

                                    JsonObject runTime = result.getAsJsonObject(ConstantClassFunction.RUNTIME);
                                    if (runTime.has(ConstantClassFunction.AUTOCONNECTION)) {
                                        if (runTime.get(ConstantClassFunction.AUTOCONNECTION).getAsString().equals("0")) {
                                            bean.setMsg(name + "关闭了自动接通功能");
                                        } else {
                                            bean.setMsg(name + "开启了自动接通功能");
                                        }
                                    }

                                    if (runTime.has(ConstantClassFunction.LOSSREPORT)) {
                                        if (runTime.get(ConstantClassFunction.LOSSREPORT).getAsString().equals("0")) {
                                            bean.setMsg(name + "关闭了手机挂丢功能");
                                        } else {
                                            bean.setMsg(name + "开启了手机挂丢功能");
                                        }
                                    }

                                    if (runTime.has(ConstantClassFunction.LIGHTPANEL)) {

                                        bean.setMsg(name + "修改了亮屏时间");
                                    }

                                    if (runTime.has(ConstantClassFunction.TAGETSTEP)) {

                                        bean.setMsg(name + "设置健康步数为" +runTime.get(ConstantClassFunction.TAGETSTEP).getAsString());
                                    }

                                    if (runTime.has(ConstantClassFunction.WATCHBELL)) {
                                        if (runTime.get(ConstantClassFunction.WATCHBELL).getAsString().equals("00,00")){
                                            bean.setMsg(name+"关闭了手表声音设置");
                                        }else {
                                            bean.setMsg(name+"修改了了手表声音设置");
                                        }
                                    }
                                    break;

                                case ConstantClassFunction.CONTACTS://联系人
                                    JsonObject contact = result.getAsJsonObject(ConstantClassFunction.CONTACT);
                                    if (ConstantClassFunction.ADD.equals(result.get(ConstantClassFunction.ACTION).getAsString())){
                                        bean.setMsg(name+"添加了联系人");


                                    }else if (ConstantClassFunction.UPDATE.equals(result.get(ConstantClassFunction.ACTION).getAsString())) {
                                        bean.setMsg(name+"修改了联系人");

                                    }else if (ConstantClassFunction.DELETE.equals(result.get(ConstantClassFunction.ACTION).getAsString())) {
                                        bean.setMsg(name+"删除了联系人");
                                    }

                                    break;

                                case ConstantClassFunction.TURN://开关机
                                    JsonObject turn = result.getAsJsonObject(ConstantClassFunction.TURN);
                                    if (turn.get(ConstantClassFunction.STATUS).getAsString().equals("0")){
                                        bean.setMsg(name + "关闭了定时开关机");
                                    }else {
                                        bean.setMsg(name + "开启了定时开关机");
                                    }

                                    break;

                                case ConstantClassFunction.SCHEDULE://作息时间
                                    bean.setMsg(name + "修改了作息计划");

                                    break;

                                case ConstantClassFunction.TIMETABLES://上课禁用
                                    bean.setMsg(name + "设置了上课禁用");
                                    break;

                                case ConstantClassFunction.EFENCE://电子围栏
                                    bean.setMsg(name + "设置了电子围栏");
                                    break;

                                default:

                                    break;
                            }


                        }
                    }

                    datas.add(bean);

                }else {
                    showNoneDataImage(true);
                }

            }
            if (datas.size()>0) {
                showNoneDataImage(false);
            }else {
                showNoneDataImage(true);
            }
            adapter.setmDatas(datas);
            adapter.notifyDataSetChanged();
        }else {
            showNoneDataImage(true);
        }


    }

    private String getNameByFromId(String fromId) {
        for (int i=0;i<contacts.size();i++) {
            if (contacts.get(i).getApp().equals("1")&&contacts.get(i).getUserid().equals(fromId)){
                try {
                    return URLDecoder.decode(contacts.get(i).getName(),"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return fromId;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
        }

    }


    public void showNoneDataImage(boolean isShow) {
        if (isShow) {
            nonedata.setVisibility(View.VISIBLE);
            nonedatahint.setVisibility(View.VISIBLE);
        } else {
            nonedata.setVisibility(View.GONE);
            nonedatahint.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContacts();
        initData();

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
    protected void onDestroy() {
        super.onDestroy();
    }
}
