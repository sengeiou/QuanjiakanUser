package com.androidquanjiakan.activity.index.watch_child;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.datahandler.ImageCropHandler;
import com.androidquanjiakan.entity.BabyCardEntity;
import com.androidquanjiakan.entity.WatchCardBean;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.androidquanjiakan.util.BitmapUtil;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.EditTextFilter;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.SignatureUtil;
import com.androidquanjiakan.view.CircleTransformation;
import com.androidquanjiakan.view.PullDownMenu;
import com.androidquanjiakan.view.WatchBirthDaySelecterDialog;
import com.example.greendao.dao.WatchCardBeanDao;
import com.google.gson.Gson;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChildWatchCardActivity extends BaseActivity implements OnClickListener {

    private TextView tv_title;
    private TextView menu_text;
    private ImageButton ibtn_back;

    private RelativeLayout phone;
    private RelativeLayout gander;
    private RelativeLayout birthday;
    private RelativeLayout grade;
    private RelativeLayout school;

    private TextView phonenumber;
    private TextView birthday_value;
//    private TextView grade_value;
    private TextView school_value;
    private TextView tv_save;

    private RadioButton male;
    private RadioButton female;

    private LinearLayout header_line;
    private ImageView header;

    private TextView name;
    private TextView relation;
    private String device_id;
    private String icon;
    private String type;
    private String watchName;
    private WatchCardBeanDao dao;
    private Gson gson;
    public static final String WATCH_PHONENUM = "watch_phonenum";
    private String watch_phonenum;
    private String birthday1;
    private String gender1;
    private String grade1;
    private String imag1;
    private String name1;
    private String school1;
    private PullDownMenu sp1;
    private PullDownMenu sp2;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_child_watch_card);
        context = this;
        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        type = getIntent().getStringExtra("type");
        watch_phonenum = getIntent().getStringExtra(WATCH_PHONENUM);
        watchName = getIntent().getStringExtra("name");
        if (device_id == null || device_id.length() < 1) {
            BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"传入参数异常!");
            finish();
            return;
        }

        dao = getWatchCardBeanDao();
        gson = new Gson();
        initTitleBar();
        initView();
    }

    private WatchCardBeanDao getWatchCardBeanDao() {
        return BaseApplication.getInstances().getDaoInstant().getWatchCardBeanDao();
    }


    public void initTitleBar() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        if (watchName.contains("%")) {
            try {
                tv_title.setText(URLDecoder.decode(watchName, "utf-8") + "名片");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            tv_title.setText(watchName + "名片");
        }


        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        menu_text = (TextView) findViewById(R.id.menu_text);
        menu_text.setVisibility(View.GONE);
    }

    private List<String> list = new ArrayList<>();
    private List<String> list_youer = new ArrayList<>();
    private List<String> list_xiaoxue = new ArrayList<>();
    private List<String> list_chuzhon = new ArrayList<>();
    private List<String> list_gaozhon = new ArrayList<>();
    private List<String> list_daxue = new ArrayList<>();

    private String[] grad1 = new String[]{"幼儿园", "小学", "初中","高中","大学"};
    private String[] grad_youer = new String[]{"托班", "小班", "中班", "大班","学前班"};
    private String[] grad_xiaoxue = new String[]{"1", "2","3", "4","5", "6"};
    private String[] grad_chuzhon = new String[]{"1", "2","3"};
    private String[] grad_gaozhon = new String[]{"1", "2","3"};
    private String[] grad_daxue = new String[]{"1", "2","3","4"};
    private String[][] grad2 = new String[][]{{"托班", "小班", "中班", "大班","学前班"}
            , {"1", "2","3", "4","5", "6"}
            , {"1", "2","3"}
            , {"1", "2","3"}
            , {"1", "2","3","4"}};

    private ArrayAdapter<String> adapter;

    private ArrayAdapter<String> adapter2;

    protected void initView() {
        /**
         * 头像
         */
        header_line = (LinearLayout) findViewById(R.id.header_line);
//        header_line.setOnClickListener(this);
        header = (ImageView) findViewById(R.id.header);
        /**
         * 名字，关系
         */
        name = (TextView) findViewById(R.id.name);
//        name.setOnClickListener(this);
        relation = (TextView) findViewById(R.id.relation);
        /**
         * 电话
         */
        phone = (RelativeLayout) findViewById(R.id.phone);
//        phone.setOnClickListener(this);
        phonenumber = (TextView) findViewById(R.id.phonenumber);
//        phonenumber.setText("");
        phonenumber.setText(watch_phonenum);
        /**
         * 性别
         */
        gander = (RelativeLayout) findViewById(R.id.gander);
        gander.setOnClickListener(this);
        male = (RadioButton) findViewById(R.id.rbtn_1);
        female = (RadioButton) findViewById(R.id.rbtn_2);
        male.setChecked(true);
        female.setChecked(false);
        /**
         * 生日
         */
        birthday = (RelativeLayout) findViewById(R.id.birthday);
        birthday.setOnClickListener(this);
        birthday_value = (TextView) findViewById(R.id.birthday_value);
        /**
         * 年级
         */
//        grade = (RelativeLayout) findViewById(R.id.grade);
//        grade.setOnClickListener(this);
//        grade_value = (TextView) findViewById(R.id.grade_value);
//        grade_value.setText("");
        sp1 = (PullDownMenu) findViewById(R.id.spinner1);
        sp2 = (PullDownMenu) findViewById(R.id.spinner2);
        initList();
        sp1.setOnItemSelectedListener(new PullDownMenu.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        sp1.setTopTitle(grad1[0]);
                        sp2.setData(list_youer,false);
                        sp2.setTopTitle(grad_youer[0]);
                        break;

                    case 1:
                        sp1.setTopTitle(grad1[1]);
                        sp2.setData(list_xiaoxue,false);
                        sp2.setTopTitle(grad_xiaoxue[0]);
                        break;

                    case 2:
                        sp1.setTopTitle(grad1[2]);
                        sp2.setData(list_chuzhon,false);
                        sp2.setTopTitle(grad_chuzhon[0]);
                        break;

                    case 3:
                        sp1.setTopTitle(grad1[3]);
                        sp2.setData(list_gaozhon,false);
                        sp2.setTopTitle(grad_gaozhon[0]);
                        break;

                    case 4:
                        sp1.setTopTitle(grad1[4]);
                        sp2.setData(list_daxue,false);
                        sp2.setTopTitle(grad_daxue[0]);
                        break;

                }

            }
        });

        sp2.setOnItemSelectedListener(new PullDownMenu.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (sp1.getTopTitle()) {
                    case "幼儿园":
                        sp2.setTopTitle(grad_youer[position]);
                        break;

                    case "小学":
                        sp2.setTopTitle(grad_xiaoxue[position]);
                        break;

                    case "初中":
                        sp2.setTopTitle(grad_chuzhon[position]);
                        break;

                    case "高中":
                        sp2.setTopTitle(grad_gaozhon[position]);
                        break;

                    case "大学":
                        sp2.setTopTitle(grad_daxue[position]);
                        break;

                }
            }
        });


//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, grad1);
//        sp1.setAdapter(adapter);
//        sp1.setOnItemSelectedListener(selectListener);

//        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, grad_xiaoxue);
//        sp2.setAdapter(adapter2);

        /**
         * 学校
         */
        school = (RelativeLayout) findViewById(R.id.school);
        school.setOnClickListener(this);
        school_value = (TextView) findViewById(R.id.school_value);
//        school_value.setText("");
        if (type.equals("old")) {
            grade.setVisibility(View.GONE);
            school.setVisibility(View.GONE);
        }


        //提交按钮
        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);


        //初始化数据
        setInitValue();


    }

    private void initList() {
        for (int i = 0;i<grad1.length;i++) {
            list.add(grad1[i]);
        }

        for (int i = 0;i<grad_youer.length;i++) {
            list_youer.add(grad_youer[i]);
        }

        for (int i = 0;i<grad_xiaoxue.length;i++) {
            list_xiaoxue.add(grad_xiaoxue[i]);
        }

        for (int i = 0;i<grad_chuzhon.length;i++) {
            list_chuzhon.add(grad_chuzhon[i]);
        }

        for (int i = 0;i<grad_gaozhon.length;i++) {
            list_gaozhon.add(grad_gaozhon[i]);
        }

        for (int i = 0;i<grad_daxue.length;i++) {
            list_daxue.add(grad_daxue[i]);
        }
    }

//    private AdapterView.OnItemSelectedListener selectListener = new AdapterView.OnItemSelectedListener() {
//        public void onItemSelected(AdapterView parent, View v, int position, long id) {
//            int pos = sp1.getSelectedItemPosition();
//            adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, grad2[pos]);
//            sp2.setAdapter(adapter2);
//            LogUtil.e("----------------1");
//
//        }
//
//
//        public void onNothingSelected(AdapterView arg0) {
//
//        }
//
//    };

    //TODO 这些状态值都需要从网络上进行获取
    public void setInitValue() {

        if (type.equals("child")) {
            if (!NetUtil.isNetworkAvailable(this)) {
                BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"网络连接不可用！");

                if (dao.loadAll().size() > 0) {
                    for (int i = 0; i < dao.loadAll().size(); i++) {
                        WatchCardBean bean = dao.loadAll().get(i);
                        if (bean.getImei().equals(device_id)) {
                            String json = bean.getJson();
                            BabyCardEntity babyCardEntity = gson.fromJson(json, BabyCardEntity.class);
                            BabyCardEntity.ResultsBean results = babyCardEntity.getResults();
                            BabyCardEntity.ResultsBean.ChildrencardBean childrencard = results.getChildrencard();
                            birthday1 = childrencard.getBirthday();
                            birthday_value.setText(birthday1);

                            gender1 = childrencard.getGender();
                            if (gender1.contains("%")) {
                                try {
                                    if (URLDecoder.decode(gender1,"utf-8").equals("男")) {
                                        male.setChecked(true);
                                        female.setChecked(false);
                                    } else {
                                        male.setChecked(false);
                                        female.setChecked(true);
                                    }
                                }catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            }else {
                                if (gender1.equals("男")) {
                                    male.setChecked(true);
                                    female.setChecked(false);
                                    try {
                                        gender1 = URLEncoder.encode("男", "utf-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    male.setChecked(false);
                                    female.setChecked(true);

                                    try {
                                        gender1 = URLEncoder.encode("女", "utf-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            String grade = childrencard.getGrade();
                            grade1 = grade;
                            initSpiner(grade);
//                            if (grade.contains("%")) {
//                                try {
//                                    grade_value.setText(URLDecoder.decode(grade, "utf-8"));
//                                } catch (UnsupportedEncodingException e) {
//                                    e.printStackTrace();
//                                }
//                            } else {
//                                grade_value.setText(grade);
//                            }
//                            grade1 = grade_value.getText().toString();

                            icon = childrencard.getIcon();
                            if (icon.contains("http")) {
                                Picasso.with(ChildWatchCardActivity.this).load(icon).transform(new CircleTransformation()).into(header);
                            } else {
                                Picasso.with(ChildWatchCardActivity.this).load(R.drawable.ic_launcher).transform(new CircleTransformation()).into(header);
                            }

                            imag1 = icon;

                            String name_ = childrencard.getName();
                            if (name_ != null && name_.contains("%")) {
                                try {
                                    name.setText(URLDecoder.decode(name_, "utf-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                name.setText(name_);
                            }
                            name1 = name.getText().toString();

                            String relation_ = childrencard.getRelation();
                            if (relation_.contains("%")) {
                                try {
                                    String decode_rel = URLDecoder.decode(relation_, "utf-8");
                                    relation.setText("我与宝贝的关系:" + decode_rel);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                relation.setText("我与宝贝的关系:" + relation_);
                            }
                            String school = childrencard.getSchool();
                            if (school.contains("%")) {
                                try {
                                    school_value.setText(URLDecoder.decode(school, "utf-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                school_value.setText(school);
                            }

                            school1 = school_value.getText().toString();

                        }
                    }
                }
            } else {
                /**
                 * 请求网络获取
                 */

                MyHandler.putTask(ChildWatchCardActivity.this, new Task(new HttpResponseInterface() {
                    @Override
                    public void handMsg(String val) {
                        if (val != null && val.length() > 0) {

                            JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                            if (!jsonObject.has("code")&&val.length()>2) {
                                WatchCardBean watchCardBean = new WatchCardBean(null, "", device_id);
                                LogUtil.e("------------card-----------"+val);
                                BabyCardEntity babyCardEntity = gson.fromJson(val, BabyCardEntity.class);
                                BabyCardEntity.ResultsBean results = babyCardEntity.getResults();
                                BabyCardEntity.ResultsBean.ChildrencardBean childrencard = results.getChildrencard();
                                birthday1 = childrencard.getBirthday();
                                birthday_value.setText(birthday1);

                                gender1 = childrencard.getGender();
                                if (gender1.contains("%")) {
                                    try {
                                        if (URLDecoder.decode(gender1,"utf-8").equals("男")) {
                                            male.setChecked(true);
                                            female.setChecked(false);
                                        } else {
                                            male.setChecked(false);
                                            female.setChecked(true);
                                        }
                                    }catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                }else {
                                    if (gender1.equals("男")) {
                                        male.setChecked(true);
                                        female.setChecked(false);
                                        try {
                                            gender1 = URLEncoder.encode("男", "utf-8");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        male.setChecked(false);
                                        female.setChecked(true);

                                        try {
                                            gender1 = URLEncoder.encode("女", "utf-8");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }


                                String grade = childrencard.getGrade();
                                grade1 = grade;
                                initSpiner(grade);
//                                if (grade.contains("%")) {
//                                    try {
//                                        grade_value.setText(URLDecoder.decode(grade, "utf-8"));
//                                    } catch (UnsupportedEncodingException e) {
//                                        e.printStackTrace();
//                                    }
//                                } else {
//                                    grade_value.setText(grade);
//                                }
//                                grade1 = grade_value.getText().toString();

                                icon = childrencard.getIcon();
                                if (icon.contains("http")) {
                                    Picasso.with(ChildWatchCardActivity.this).load(icon).transform(new CircleTransformation()).into(header);
                                } else {
                                    Picasso.with(ChildWatchCardActivity.this).load(R.drawable.ic_launcher).transform(new CircleTransformation()).into(header);
                                }

                                imag1 = icon;
                                String name_ = childrencard.getName();
                                if (name_ != null && name_.contains("%")) {
                                    try {
                                        name.setText(URLDecoder.decode(name_, "utf-8"));
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    name.setText(name_);
                                }

                                name1 = name.getText().toString();

                                String relation_ = childrencard.getRelation();
                                if (relation_.contains("%")) {
                                    try {
                                        String decode_rel = URLDecoder.decode(relation_, "utf-8");
                                        relation.setText("我与宝贝的关系:" + decode_rel);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    relation.setText("我与宝贝的关系:" + relation_);
                                }
                                String school = childrencard.getSchool();
                                if (school.contains("%")) {
                                    try {
                                        school_value.setText(URLDecoder.decode(school, "utf-8"));
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    school_value.setText(school);
                                }
                                school1 = school_value.getText().toString();

                                watchCardBean.setJson(val);
                                if (dao.loadAll().size() > 0) {
                                    for (int i = 0; i < dao.loadAll().size(); i++) {
                                        WatchCardBean bean = dao.loadAll().get(i);
                                        if (bean.getImei().equals(device_id)) {
                                            dao.delete(bean);
                                        }
                                    }
                                }

                                dao.insert(watchCardBean);

                            }
                        }


                    }
                }, HttpUrls.getBabyCard() + "&imei=" + device_id +
                        "&userid=" + QuanjiakanSetting.getInstance().getUserId(), null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(ChildWatchCardActivity.this, "正在加载数据...")
                ));


            }
        }

    }

    private void initSpiner(String grade) {
        if (grade!=null) {
            String[] split = grade.split(":");
            String substring = split[0];
            String s = split[1];

            switch (substring) {
                case "幼儿园":
                    sp1.setData(list,false);
                    sp1.setTopTitle(grad1[0]);
                    for (int i=0;i<grad_youer.length;i++) {
                        if (s.equals(grad_youer[i])) {
                            sp2.setData(list_youer,false);
                            sp2.setTopTitle(grad_youer[i]);

                        }
                    }
                    break;

                case "小学":
                    sp1.setData(list,false);
                    sp1.setTopTitle(grad1[1]);
                    for (int i=0;i<grad_xiaoxue.length;i++) {
                        if (s.equals(grad_xiaoxue[i])) {
                            sp2.setData(list_xiaoxue,false);
                            sp2.setTopTitle(grad_xiaoxue[i]);
                        }
                    }
                    break;

                case "初中":
                    sp1.setData(list,false);
                    sp1.setTopTitle(grad1[2]);
                    for (int i=0;i<grad_chuzhon.length;i++) {
                        if (s.equals(grad_chuzhon[i])) {
                            sp2.setData(list_chuzhon,false);
                            sp2.setTopTitle(grad_chuzhon[i]);
                        }
                    }
                    break;

                case "高中":
                    sp1.setData(list,false);
                    sp1.setTopTitle(grad1[3]);
                    for (int i=0;i<grad_gaozhon.length;i++) {
                        if (s.equals(grad_gaozhon[i])) {
                            sp2.setData(list_gaozhon,false);
                            sp2.setTopTitle(grad_gaozhon[i]);
                        }
                    }
                    break;

                case "大学":
                    sp1.setData(list,false);
                    sp1.setTopTitle(grad1[4]);
                    for (int i=0;i<grad_daxue.length;i++) {
                        if (s.equals(grad_daxue[i])) {
                            sp2.setData(list_daxue,false);
                            sp2.setTopTitle(grad_daxue[i]);
                        }
                    }
                    break;

                default:
                    sp1.setData(list,false);
                    sp1.setTopTitle(grad1[0]);
                    sp2.setData(list_youer,false);
                    sp2.setTopTitle(grad_youer[0]);
                    break;
            }
        }else {
            sp1.setData(list,false);
            sp1.setTopTitle(grad1[0]);
            sp2.setData(list_youer,false);
            sp2.setTopTitle(grad_youer[0]);
        }


    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.header_line: {
                showImageDialog();
                break;
            }
            case R.id.name: {
                getInputDialog(TYPE_PHONE);
                break;
            }
            /*case R.id.gander: {

                break;
            }*/
            case R.id.birthday: {
                showBirthDayDialog(birthday_value);
                break;
            }
            /*case R.id.grade: {
//                getInputDialog(TYPE_GRADE);
                break;
            }*/
            case R.id.school: {
                getInputDialog(TYPE_SCHOOL);
                break;
            }
            case R.id.ibtn_back: {
                finish();
                break;
            }

            case R.id.tv_save:
                doSaveInfo();
                break;
        }
    }

    public void doSaveInfo() {

        String name2 = name.getText().toString();

//        String grade2 = grade_value.getText().toString();
        String birthday2 = birthday_value.getText().toString();
        String school2 = school_value.getText().toString();
        String gender2 = "";
        if (male.isChecked()) {

            try {
                gender2 = URLEncoder.encode("男", "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {
            try {
                gender2 = URLEncoder.encode("女", "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        String imag2 = "";
        if (mLastNetPath==null) {
            imag2 = imag1;
        }else {
            imag2 = mLastNetPath;
        }
        if (name1.equals(name2)&& grade1.equals(sp1.getTopTitle()+":"+sp2.getTopTitle())&&birthday1.equals(birthday2)&&school1.equals(school2)&&gender1.equals(gender2)&&imag1.equals(imag2)) {
            BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"请修改相关数据");
            return;
        }


        String sch = "";

        try {
            sch = URLEncoder.encode(school2, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String name_ = "";
        try {
            name_ = URLEncoder.encode(name2, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        http://app.quanjiakan.com/device/service?code=childWatch&type=addchildcard&imei=355637051751824&userid=11780&name=%E6%9D%A8%E5%BB%BA%E5%8D%8E&gender=男&birthday=1990-08-07&grade=小学:1&school=哦里PK&icon=http://picture.quanjiakan.com:9080/quanjiakan/resources/doctor/20170511180507_h8wtzi.png
//

        //TODO 进行数据保存
//&imei=355637053995130&userid=11931&name=%E8%B4%9D%E8%B4%9D&gender=%E5%A5%B3&birthday=2014&grade=3&school=%E6%B1%9F%E5%8D%97&icon=image
        String str_req = "&imei=" + device_id + "&userid=" + QuanjiakanSetting.getInstance().getUserId() + "&name=" + name_
                + "&gender=" + gender2 + "&birthday=" + birthday2 + "&grade=" + sp1.getTopTitle()+":"+sp2.getTopTitle()
                + "&school=" + sch + "&icon=" + imag2;
        LogUtil.e("save---------------" + str_req);

        if (type.equals("child")) {
            MyHandler.putTask(ChildWatchCardActivity.this, new Task(new HttpResponseInterface() {
                @Override
                public void handMsg(String val) {
                    LogUtil.e("save---------------" + val);
                    if (val != null) {
                        JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();

                        if (jsonObject.has("code") && "200".equals(jsonObject.get("code").getAsString())) {
                            //提交成功
                            BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"保存成功");
                        }
                    }


                }
            }, HttpUrls.saveBabyCard() + str_req, null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(ChildWatchCardActivity.this, "正在保存数据")));

        }

    }

    protected void showBirthDayDialog(final TextView tv) {
        WatchBirthDaySelecterDialog day_dialog = new WatchBirthDaySelecterDialog(this, true);
        day_dialog.show();
        day_dialog.setBirthdayListener(new WatchBirthDaySelecterDialog.OnBirthListener() {

            @Override
            public void onClick(String year, String month, String day) {
                // TODO Auto-generated method stub
                if (Integer.parseInt(month) < 10) {
                    month = "0" + month;
                }
                if (Integer.parseInt(day) < 10) {
                    day = "0" + day;
                }
                tv.setText(year + "-" + month + "-" + day);
            }
        });
    }


    private Dialog phoneDialog;
    private final int TYPE_PHONE = 1;
    private final int TYPE_SCHOOL = 2;
    private final int TYPE_GRADE = 3;

    private void getInputDialog(final int type) {
        phoneDialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_watch_child_modify, null);
        final EditText tv_content = (EditText) view.findViewById(R.id.tv_content);
//        EditTextFilter.setEditTextInhibitInputSpace(tv_content);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        switch (type) {
            case TYPE_PHONE: {
                tv_title.setText("输入宝贝名字");
                tv_content.setHint("请输入宝贝名字");
                tv_content.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            }
            case TYPE_SCHOOL: {
                tv_title.setText("输入学校名称");
                tv_content.setHint("请输入宝贝学校名称");
                tv_content.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            }
//            case TYPE_GRADE: {
//                tv_title.setText("输入年级");
//                tv_content.setHint("请输入宝贝的年级");
//                tv_content.setInputType(InputType.TYPE_CLASS_TEXT);
//                break;
//            }
            default:
                tv_title.setText("提示");
                tv_content.setHint("请输入");
                tv_content.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }


        TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneDialog != null) {
                    phoneDialog.dismiss();
                }
                if (tv_content.length() < 1) {
                    BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"请输入相关数据!");
                    return;
                }
                /**
                 * TODO 需要从网络确定该项数据修改成功后再进行展示
                 *  1、可以考虑使用Handler将需要修改的数据传递过去
                 *  2、回调的网络访问接口
                 */
                switch (type) {
                    case TYPE_PHONE: {
//                        if (TextUtils.isEmpty(tv_content.getText().toString())) {
//                            BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"请输入名字");
//                            return;
//                        }

                        if ((CheckUtil.isAllChineseChar(tv_content.getText().toString().trim()) && tv_content.getText().toString().trim().length() > 4)) {
                            Toast.makeText(ChildWatchCardActivity.this, "超过限定长度，最多能输入四个中文或者八个英文字母", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (tv_content.getText().toString().trim().length() > 8) {
                            Toast.makeText(ChildWatchCardActivity.this, "超过限定长度，最多能输入四个中文或者八个英文字母", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (EditTextFilter.containsSpace(tv_content.getText().toString())||EditTextFilter.containsUnChar(tv_content.getText().toString().trim())||EditTextFilter.containsEmoji(tv_content.getText().toString().trim())) {
                            BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"含有非法字符");
                            name.setText("");
                        } /*else if (tv_content.getText().toString().length() > 4) {
                            BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"超过限定字符");
                            name.setText("");
                        }*/ else {
                            if (name != null) {
                                name.setText(tv_content.getText().toString());
                            }
                        }


                        break;
                    }
                    case TYPE_SCHOOL: {

//                        if (TextUtils.isEmpty(tv_content.getText().toString())) {
//                            BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"请输入学校名称");
//                            return;
//                        }

                        if ((CheckUtil.isAllChineseChar(tv_content.getText().toString().trim()) && tv_content.getText().toString().trim().length() > 12)) {
                            Toast.makeText(ChildWatchCardActivity.this, "超过限定长度，最多能输入12个字符", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (tv_content.getText().toString().trim().length() > 12) {
                            Toast.makeText(ChildWatchCardActivity.this, "超过限定长度，最多能输入12个字符", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (EditTextFilter.containsSpace(tv_content.getText().toString())||EditTextFilter.containsUnChar(tv_content.getText().toString().trim())||EditTextFilter.containsEmoji(tv_content.getText().toString().trim())) {
                            BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"含有非法字符");
                            school_value.setText("");
                        } /*else if (tv_content.getText().toString().length() > 12) {
                            BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"超过限定字符");
                        } */else {
                            if (school_value != null) {
                                school_value.setText(tv_content.getText().toString());
                            }
                        }

                        break;
                    }
//                    case TYPE_GRADE: {
//
////                        if (TextUtils.isEmpty(tv_content.getText().toString())) {
////                            BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"请输入年级");
////                            return;
////                        }
//
//                        if ((CheckUtil.isAllChineseChar(tv_content.getText().toString().trim()) && tv_content.getText().toString().trim().length() > 8)) {
//                            Toast.makeText(ChildWatchCardActivity.this, "超过限定长度，请输入四个中文或者八个英文字母", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        if (tv_content.getText().toString().trim().length() > 8) {
//                            Toast.makeText(ChildWatchCardActivity.this, "超过限定长度，请输入四个中文或者八个英文字母", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        if (EditTextFilter.containsSpace(tv_content.getText().toString())||EditTextFilter.containsUnChar(tv_content.getText().toString().trim())||EditTextFilter.containsEmoji(tv_content.getText().toString().trim())) {
//                            BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"含有非法字符");
//                            grade_value.setText("");
//                        } /*else if (tv_content.getText().toString().length() > 4) {
//                            BaseApplication.getInstances().toast(ChildWatchCardActivity.this,"超过限定字符");
//                        } */else {
//                            if (grade_value != null) {
//                                grade_value.setText(tv_content.getText().toString());
//                            }
//                        }
//
//
//                        break;
//                    }
                    default:
                        break;
                }
            }
        });
        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneDialog != null) {
                    phoneDialog.dismiss();
                }
            }
        });

        WindowManager.LayoutParams lp = phoneDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        phoneDialog.setContentView(view, lp);
        phoneDialog.show();
    }

    ;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
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
                ImageCropHandler.getImageFromCamera(ChildWatchCardActivity.this, new IImageCropInterface() {
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
                ImageCropHandler.pickImage(ChildWatchCardActivity.this);
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


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }


    private Bitmap temp;
    private String sourcePath;

    public void save2SmallImage(Uri data) {
        if (data == null) {
            return;
        }
        sourcePath = ImageUtils.uri2Path(data, ChildWatchCardActivity.this);
        String smallImagePath = ImageUtils.saveBitmapToStorage(SignatureUtil.getMD5String(sourcePath) + ".jpg", BitmapUtil.getSmallBitmap(sourcePath));
        if (temp != null) {
            temp.recycle();
            temp = null;
        }
        temp = BitmapFactory.decodeFile(smallImagePath);
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
                            Uri u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
                                    tempFile.getAbsolutePath(), null, null));
                            // u就是拍摄获得的原始图片的uri，剩下的你想干神马坏事请便……
                            save2SmallImage(u);
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ChildWatchCardActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA:
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        ImageCropHandler.beginCrop(ChildWatchCardActivity.this, Uri.fromFile(new File(mCurrentPhotoPath)));
                    }
                }
                break;
            case CommonRequestCode.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    ImageCropHandler.beginCrop(ChildWatchCardActivity.this, data.getData());
                }
            case CommonRequestCode.REQUEST_CROP:
                ImageCropHandler.handleCrop(null, resultCode, data, new IImageCropInterface() {
                    @Override
                    public void getFilePath(String path) {
                        mCurrentPhotoPath = path;
                        Bitmap smallBitmap = BitmapUtil.getSmallBitmap(mCurrentPhotoPath);
                        String filename = System.currentTimeMillis() + ".png";
                        String file_path = ImageUtils.saveBitmapToStorage(filename, smallBitmap);
                        uploadImage(header, file_path, filename);
                    }
                });
                break;
        }
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
                        Picasso.with(getApplicationContext()).load(strImag).transform(new CircleTransformation())
                                .into(head_image);
                    }
                } else {
                    Toast.makeText(ChildWatchCardActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
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
        }, HttpUrls.postFile() + "&storage=2", params, Task.TYPE_POST_FILE, QuanjiakanDialog.getInstance().getDialog(ChildWatchCardActivity.this, "正在上传图片！")));
    }
}
