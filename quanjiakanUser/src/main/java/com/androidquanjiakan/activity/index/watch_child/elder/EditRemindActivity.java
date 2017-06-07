package com.androidquanjiakan.activity.index.watch_child.elder;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.androidquanjiakan.activity.setting.contact.WatchContactesActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.CareRemindBean;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.LogUtil;
import com.example.greendao.dao.CareRemindBeanDao;
import com.quanjiakan.main.R;
import com.quanjiakanuser.widget.AbstractWheelTextAdapter;
import com.quanjiakanuser.widget.OnWheelChangedListener;
import com.quanjiakanuser.widget.OnWheelScrollListener;
import com.quanjiakanuser.widget.WheelView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 作者：Administrator on 2017/4/6 11:25
 * <p>   添加、编辑关爱提醒
 * 邮箱：liuzj@hi-board.com
 */


public class EditRemindActivity extends BaseActivity implements View.OnClickListener {

    private Context context;

    public static final String TYPE = "type";

    /**
     * 标题栏
     */
    private ImageButton btn_back;
    private TextView tv_title;
    private TextView menu_text;
    private String type;
    private CareRemindBeanDao dao;
    private ImageButton add_time;
    private LinearLayout llt_container;


    private TextView tv_mon;
    private TextView tv_tue;
    private TextView tv_wed;
    private TextView tv_thur;
    private TextView tv_fri;
    private TextView tv_sat;
    private TextView tv_sun;

    private boolean isMonSelect = false;
    private boolean isTueSelect = false;
    private boolean isWedSelect = false;
    private boolean isThurSelect = false;
    private boolean isFriSelect = false;
    private boolean isSatSelect = false;
    private boolean isSunSelect = false;
    private boolean isOpen = false;
    private RelativeLayout remind_title;
    private Dialog remindDialog;
    private String content;
    private TextView tv_remind_name;
    private ToggleButton button;
    private Button btn_save;
    private CareRemindBean careRemindBean = null;
    private int position = -1;
    private LinearLayout set_time;
    private String time = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_remind);
        BaseApplication.getInstances().setCurrentActivity(this);
        context = this;
        type = getIntent().getStringExtra(TYPE);
        if (type.equals("edit")) {
            position = getIntent().getIntExtra("position", -1);
        }
        dao = getCareRemindBeanDao();


        initTitle();
        initView();
    }

    private TextView btn_sure;
    private TextView btn_cancel;

    private void initView() {
        add_time = (ImageButton) findViewById(R.id.ibt_add_time);
        add_time.setOnClickListener(this);
        llt_container = (LinearLayout) findViewById(R.id.llt_container);

        TextView textView = new TextView(EditRemindActivity.this);
        textView.setText("--:--");
        textView.setTextColor(EditRemindActivity.this.getResources().getColor(R.color.color_xun_ing));
        textView.setTextSize(QuanjiakanUtil.px2sp(EditRemindActivity.this, 40));
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.selecter_btn_bac_green_small);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(146, 58);
        lp.setMargins(0, 0, 18, 0);
//        textView.setPadding(10,0,10,0);
        textView.setLayoutParams(lp);
        llt_container.addView(textView);

        remind_title = (RelativeLayout) findViewById(R.id.rl_plan);
        remind_title.setOnClickListener(this);

        tv_remind_name = (TextView) findViewById(R.id.tv_remind_name);
        button = (ToggleButton) findViewById(R.id.btn_on_line);
        button.setOnClickListener(this);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);


        tv_mon = (TextView) findViewById(R.id.tv_mon);
        tv_tue = (TextView) findViewById(R.id.tv_tue);
        tv_wed = (TextView) findViewById(R.id.tv_wed);
        tv_thur = (TextView) findViewById(R.id.tv_thur);
        tv_fri = (TextView) findViewById(R.id.tv_fri);
        tv_sat = (TextView) findViewById(R.id.tv_sat);
        tv_sun = (TextView) findViewById(R.id.tv_sun);
        tv_mon.setOnClickListener(this);
        tv_tue.setOnClickListener(this);
        tv_wed.setOnClickListener(this);
        tv_thur.setOnClickListener(this);
        tv_fri.setOnClickListener(this);
        tv_sat.setOnClickListener(this);
        tv_sun.setOnClickListener(this);


        //设置时间的内部控件
        set_time = (LinearLayout) findViewById(R.id.llt_set_time);//设置时间的
        set_time.setVisibility(View.GONE);
        btn_sure = (TextView) findViewById(R.id.btn_sure);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

    }

    private WheelView wvTime;
    private WheelView wvHour;
    private WheelView wvMinute;


    private int maxTextSize = 18;
    private int minTextSize = 14;
    private CalendarTextAdapter mTimeAdapter;

    //    private int curruntTime = 0;
//    private int curruntHour = 0;
//    private int curruntMinute = 0;
    private ArrayList<String> arr_time = new ArrayList<String>();
    private ArrayList<String> arr_hour = new ArrayList<String>();
    private ArrayList<String> arr_minute = new ArrayList<String>();
    private CalendarTextAdapter mHourAdapter;
    private CalendarTextAdapter mMinuteAdapter;
    private String selectTime = "上午";
    private String selectHour = "1";
    private String selectMinute = "00";

    private void initWheelView(int curruntTime, int curruntHour, int curruntMinute) {
        wvTime = (WheelView) findViewById(R.id.wv_am_pm);
        wvHour = (WheelView) findViewById(R.id.wv_hour);
        wvMinute = (WheelView) findViewById(R.id.wv_minute);
//        wvTime.setCyclic(true);
        wvHour.setCyclic(true);
        wvMinute.setCyclic(true);

        getTimeData();
        mTimeAdapter = new CalendarTextAdapter(context, arr_time, curruntTime, maxTextSize, minTextSize);
//        wvTime.setVisibleItems(2);
        wvTime.setViewAdapter(mTimeAdapter);
        wvTime.setCurrentItem(curruntTime);


        getHourData();
        mHourAdapter = new CalendarTextAdapter(context, arr_hour, curruntHour, maxTextSize, minTextSize);
        wvHour.setVisibleItems(5);
        wvHour.setViewAdapter(mHourAdapter);
        wvHour.setCurrentItem(curruntHour);

        getMinuteData();
        mMinuteAdapter = new CalendarTextAdapter(context, arr_minute, curruntMinute, maxTextSize, minTextSize);
        wvMinute.setVisibleItems(5);
        wvMinute.setViewAdapter(mMinuteAdapter);
        wvMinute.setCurrentItem(curruntMinute);


        wvTime.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mTimeAdapter.getItemText(wheel.getCurrentItem());
                selectTime = currentText;
                setTextviewSize(currentText, mTimeAdapter);

            }
        });

        wvTime.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mTimeAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mTimeAdapter);
            }
        });


        wvHour.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mHourAdapter.getItemText(wheel.getCurrentItem());
                selectHour = currentText;
                setTextviewSize(currentText, mHourAdapter);
//                curruntHour = Integer.parseInt(currentText);

            }
        });

        wvHour.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mHourAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mHourAdapter);
            }
        });


        wvMinute.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mMinuteAdapter.getItemText(wheel.getCurrentItem());
                selectMinute = currentText;
                setTextviewSize(currentText, mMinuteAdapter);
//                curruntMinute = Integer.parseInt(currentText);

            }
        });

        wvMinute.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mMinuteAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mMinuteAdapter);
            }
        });


    }


    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(maxTextSize);
            } else {
                textvew.setTextSize(minTextSize);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
        initData();
    }


    private void initData() {
        if (position != -1) {
            careRemindBean = dao.loadAll().get(position);
            if (careRemindBean.getButton().equals("1")) {
                isOpen = true;
            } else {
                isOpen = false;
            }

            time = careRemindBean.getTime();
            String[] split = time.split(",");
            for (int i = 0; i < split.length; i++) {
                TextView textView = new TextView(EditRemindActivity.this);
                textView.setText(split[i]);
                textView.setTextColor(EditRemindActivity.this.getResources().getColor(R.color.color_xun_ing));
                textView.setTextSize(QuanjiakanUtil.px2sp(EditRemindActivity.this, 40));
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundResource(R.drawable.selecter_btn_bac_green_small);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(146, 58);
//                lp.height = QuanjiakanUtil.px2dip(EditRemindActivity.this,58);
//                lp.width = QuanjiakanUtil.px2dip(EditRemindActivity.this,146);
                lp.setMargins(0, 0, 18, 0);
//                textView.setPadding(10,0,10,0);
                textView.setLayoutParams(lp);
                llt_container.addView(textView);
            }
            content = careRemindBean.getTitle();

            tv_remind_name.setText(content);

            String[] weeks = careRemindBean.getWeek().split("/");
            for (String week : weeks) {
                if (week.equals("周一")) {
                    isMonSelect = true;
                } else if (week.equals("周二")) {
                    isTueSelect = true;
                } else if (week.equals("周三")) {
                    isWedSelect = true;
                } else if (week.equals("周四")) {
                    isThurSelect = true;
                } else if (week.equals("周五")) {
                    isFriSelect = true;
                } else if (week.equals("周六")) {
                    isSatSelect = true;
                } else if (week.equals("周日")) {
                    isSunSelect = true;
                }
            }
        }


        if (isMonSelect) {
            tv_mon.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_mon.setTextColor(getResources().getColor(R.color.white));
        }
        if (isTueSelect) {
            tv_tue.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_tue.setTextColor(getResources().getColor(R.color.white));
        }
        if (isWedSelect) {
            tv_wed.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_wed.setTextColor(getResources().getColor(R.color.white));
        }
        if (isThurSelect) {
            tv_thur.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_thur.setTextColor(getResources().getColor(R.color.white));
        }
        if (isFriSelect) {
            tv_fri.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_fri.setTextColor(getResources().getColor(R.color.white));
        }
        if (isSatSelect) {
            tv_sat.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_sat.setTextColor(getResources().getColor(R.color.white));
        }

        if (isSunSelect) {
            tv_sun.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
            tv_sun.setTextColor(getResources().getColor(R.color.white));
        }

        if (isOpen) {
            button.setBackgroundResource(R.drawable.edit_btn_open);
        } else {
            button.setBackgroundResource(R.drawable.edit_btn_close);
        }


        for (int i = 0; i < llt_container.getChildCount(); i++) {
            if (i > 0) {

                final int child = i;

                //长按删除
                llt_container.getChildAt(child).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDelManagerDialog(child);

                        return true;
                    }
                });


                llt_container.getChildAt(child).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView childAt = (TextView) llt_container.getChildAt(child);
                        String s = childAt.getText().toString();
                        String[] split = s.split(":");
                        int i1 = Integer.parseInt(split[0]);
                        int i2 = Integer.parseInt(split[1]);
                        selectMinute = split[1];

                        if (i1 > 11) {
                            showEditTimeLayout(1, i1 - 12, i2, childAt, s);
                            selectTime = "下午";
                            selectHour = (i1 - 12) + "";
                        } else {
                            showEditTimeLayout(0, i1, i2, childAt, s);
                            selectTime = "上午";

                            if (i1 > 9) {
                                selectHour = i1 + "";
                            } else {
                                selectHour = (i1 + "").replace("0", "");
                            }

                        }


                    }
                });
            }

        }
    }

    private void showDelManagerDialog(final int child) {

        final Dialog dialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_manager, null);

        TextView title = (TextView) view.findViewById(R.id.tv_dialog_title);
        title.setText("删除");

        view.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                TextView childAt = (TextView) llt_container.getChildAt(child);
                time = time.replace(childAt.getText().toString() + ",", "");
                LogUtil.e("--------------" + time);
                llt_container.removeViewAt(child);

            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                QuanjiakanUtil.showToast(EditRemindActivity.this, getString(R.string.cancel));

            }
        });
//        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        // TODO: 2017/2/23 这里要做处理
        content.setText("确定删除该时间设置吗？");


        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setContentView(view, lp);
        dialog.show();
    }

    private void showDelManagerDialog(final TextView textView, final String text) {

        final Dialog dialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_manager, null);

        TextView title = (TextView) view.findViewById(R.id.tv_dialog_title);
        title.setText("删除");

        view.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                time = time.replace(text + ",", "");
                LogUtil.e("--------------" + time);
                llt_container.removeView(textView);


            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                QuanjiakanUtil.showToast(EditRemindActivity.this, getString(R.string.cancel));

            }
        });
//        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        // TODO: 2017/2/23 这里要做处理
        content.setText("确定删除该时间设置吗？");


        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setContentView(view, lp);
        dialog.show();
    }

    private CareRemindBeanDao getCareRemindBeanDao() {
        return BaseApplication.getInstances().getDaoInstant().getCareRemindBeanDao();
    }

    private void initTitle() {
        btn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        menu_text = (TextView) findViewById(R.id.menu_text);
        menu_text.setOnClickListener(this);
        btn_back.setVisibility(View.VISIBLE);

        btn_back.setOnClickListener(this);

        if (type.equals("add")) {
            tv_title.setText("提醒添加");
            menu_text.setVisibility(View.GONE);
        } else if (type.equals("edit")) {
            tv_title.setText("提醒编辑");
            menu_text.setText("删除");
            menu_text.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ibtn_back:
                finish();
                break;

            case R.id.ibt_add_time:

                int count = llt_container.getChildCount();
                if (count > 4) {
                    BaseApplication.getInstances().toast(EditRemindActivity.this,"最多只能添加4个提醒时间！");
                    return;
                } else {
                    showAddTimeLayout(0, 0, 0);
                }

                break;

            case R.id.tv_mon:
                if (isMonSelect) {
                    tv_mon.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_mon.setTextColor(getResources().getColor(R.color.font_color_333333));
                } else {
                    tv_mon.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_mon.setTextColor(getResources().getColor(R.color.white));
                }
                isMonSelect = !isMonSelect;
                break;
            case R.id.tv_tue:
                if (isTueSelect) {
                    tv_tue.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_tue.setTextColor(getResources().getColor(R.color.font_color_333333));
                } else {
                    tv_tue.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_tue.setTextColor(getResources().getColor(R.color.white));
                }
                isTueSelect = !isTueSelect;
                break;
            case R.id.tv_wed:
                if (isWedSelect) {
                    tv_wed.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_wed.setTextColor(getResources().getColor(R.color.font_color_333333));
                } else {
                    tv_wed.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_wed.setTextColor(getResources().getColor(R.color.white));
                }
                isWedSelect = !isWedSelect;
                break;
            case R.id.tv_thur:
                if (isThurSelect) {
                    tv_thur.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_thur.setTextColor(getResources().getColor(R.color.font_color_333333));
                } else {
                    tv_thur.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_thur.setTextColor(getResources().getColor(R.color.white));
                }
                isThurSelect = !isThurSelect;
                break;
            case R.id.tv_fri:
                if (isFriSelect) {
                    tv_fri.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_fri.setTextColor(getResources().getColor(R.color.font_color_333333));
                } else {
                    tv_fri.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_fri.setTextColor(getResources().getColor(R.color.white));
                }
                isFriSelect = !isFriSelect;
                break;
            case R.id.tv_sat:
                if (isSatSelect) {
                    tv_sat.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_sat.setTextColor(getResources().getColor(R.color.font_color_333333));
                } else {
                    tv_sat.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_sat.setTextColor(getResources().getColor(R.color.white));
                }
                isSatSelect = !isSatSelect;
                break;
            case R.id.tv_sun:
                if (isSunSelect) {
                    tv_sun.setBackground(getResources().getDrawable(R.drawable.shape_week_nor));
                    tv_sun.setTextColor(getResources().getColor(R.color.font_color_333333));
                } else {
                    tv_sun.setBackground(getResources().getDrawable(R.drawable.shape_week_select));
                    tv_sun.setTextColor(getResources().getColor(R.color.white));
                }
                isSunSelect = !isSunSelect;
                break;

            case R.id.rl_plan:
                showEditRemindDialog();
                break;

            case R.id.btn_on_line:
                if (isOpen) {
                    button.setBackgroundResource(R.drawable.edit_btn_close);

                } else {
                    button.setBackgroundResource(R.drawable.edit_btn_open);
                }
                isOpen = !isOpen;
                break;

            case R.id.btn_save:
                toSaveData();

                break;

            case R.id.menu_text:
                //数据库删除数据
                deleteData();
                break;
        }


    }

    Handler mHandler = new Handler();

    private void showAddTimeLayout(int t, int hour, int minute) {
        btn_save.setVisibility(View.GONE);
        set_time.setVisibility(View.VISIBLE);
        initWheelView(t, hour, minute);


        wvHour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        wvTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        wvMinute.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);

            }
        });

        btn_sure.setOnClickListener(new View.OnClickListener() {

            private String text;

            @Override
            public void onClick(View v) {
                btn_save.setVisibility(View.VISIBLE);
                set_time.setVisibility(View.GONE);

                if (selectTime.equals("上午")) {
                    if (Integer.parseInt(selectHour) > 9) {
                        text = selectHour + ":" + selectMinute;
                    } else {
                        text = "0" + selectHour + ":" + selectMinute;
                    }

                } else {
                    text = (Integer.parseInt(selectHour) + 12) + ":" + selectMinute;
                }

                if (time.contains(text)) {
                    BaseApplication.getInstances().toast(EditRemindActivity.this,"时间设置重复！");
                    return;
                } else {

                    final TextView textView = new TextView(EditRemindActivity.this);

                    textView.setText(text);
                    textView.setTextColor(EditRemindActivity.this.getResources().getColor(R.color.color_xun_ing));
                    textView.setTextSize(QuanjiakanUtil.px2sp(EditRemindActivity.this, 40));
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackgroundResource(R.drawable.selecter_btn_bac_green_small);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(146, 58);
                    lp.setMargins(0, 0, 18, 0);
                    textView.setLayoutParams(lp);
                    llt_container.addView(textView);
                    StringBuilder sb = new StringBuilder(time);
                    sb.append(text).append(",");
                    time = sb.toString();

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String[] split = text.split(":");
                            int i1 = Integer.parseInt(split[0]);
                            int i2 = Integer.parseInt(split[1]);
                            selectMinute = split[1];
                            if (i1 > 11) {
                                showEditTimeLayout(1, i1 - 12, i2, textView, text);
                                selectTime = "下午";
                                selectHour = (i1 - 12) + "";
                            } else {
                                showEditTimeLayout(0, i1, i2, textView, text);
                                selectTime = "上午";
                                selectHour = i1 + "";
                            }
                        }
                    });

                    textView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showDelManagerDialog(textView,text);

                            return true;
                        }
                    });
                }


            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_save.setVisibility(View.VISIBLE);
                set_time.setVisibility(View.GONE);
            }
        });
    }

    private void showEditTimeLayout(int t, final int hour, final int minute, final TextView textView, final String s) {

        btn_save.setVisibility(View.GONE);
        set_time.setVisibility(View.VISIBLE);
        initWheelView(t, hour, minute);


        wvHour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        wvTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        wvMinute.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);

            }
        });

        btn_sure.setOnClickListener(new View.OnClickListener() {

            private String text;

            @Override
            public void onClick(View v) {
                btn_save.setVisibility(View.VISIBLE);
                set_time.setVisibility(View.GONE);

                if (selectTime.equals("上午")) {
                    if (Integer.parseInt(selectHour) > 9) {
                        text = selectHour + ":" + selectMinute;
                    } else {
                        text = "0" + selectHour + ":" + selectMinute;
                    }

                } else {
                    text = (Integer.parseInt(selectHour) + 12) + ":" + selectMinute;
                }

                textView.setText(text);
                if (!time.contains(textView.getText())) {
                    time = time.replace(s, textView.getText());
                }


            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_save.setVisibility(View.VISIBLE);
                set_time.setVisibility(View.GONE);
            }
        });


    }

    private void deleteData() {

        if (position != -1) {
            dao.delete(dao.loadAll().get(position));
            finish();
        }
    }

    private void toSaveData() {
        String weekStr = getWeekStr();
        String turn;
        if (isOpen) {
            turn = "1";
        } else {
            turn = "0";
        }

//        String time = "08:00,09:00,14:00";

        if (!isSelectedWeek()) {
            Toast.makeText(EditRemindActivity.this, "您未设置日期", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(content)) {
            BaseApplication.getInstances().toast(EditRemindActivity.this,"请设置计划名称");
            return;
        }

        if ((CheckUtil.isAllChineseChar(content) && content.length() > 4)) {
            Toast.makeText(EditRemindActivity.this, "请输入正确的计划名称", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.length() > 8) {
            Toast.makeText(EditRemindActivity.this, "请输入正确的计划名称", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(time) || time.equals("")) {
            BaseApplication.getInstances().toast(EditRemindActivity.this,"请设置提醒时间");
        }

        //save2Db
        if (position != -1) {
            careRemindBean.setWeek(weekStr);
            careRemindBean.setTitle(content);
            careRemindBean.setTime(time);
            careRemindBean.setButton(turn);
            dao.update(careRemindBean);
        } else {
            CareRemindBean bean = new CareRemindBean(null, content, weekStr, time, turn);
            dao.insert(bean);
        }

        BaseApplication.getInstances().toast(EditRemindActivity.this,"保存成功！");
        finish();

    }


    //判断是否有选择日期
    private boolean isSelectedWeek() {

        if (isMonSelect) {
            return true;
        } else if (isTueSelect) {
            return true;
        } else if (isWedSelect) {
            return true;
        } else if (isThurSelect) {
            return true;
        } else if (isFriSelect) {

            return true;
        } else if (isSatSelect) {

            return true;
        } else if (isSunSelect) {

            return true;
        } else {
            return false;
        }

    }

    private String getWeekStr() {

        StringBuilder sb = new StringBuilder();
        if (isMonSelect) {
            sb.append("周一").append("/");
        }
        if (isTueSelect) {
            sb.append("周二").append("/");
        }

        if (isWedSelect) {
            sb.append("周三").append("/");
        }
        if (isThurSelect) {
            sb.append("周四").append("/");
        }
        if (isFriSelect) {
            sb.append("周五").append("/");
        }
        if (isSatSelect) {
            sb.append("周六").append("/");
        }

        if (isSunSelect) {
            sb.append("周日").append("/");
        }

        return sb.toString();
    }

    private void showEditRemindDialog() {
        remindDialog = new Dialog(this, R.style.ShareDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_child_plan_edit, null);
        final EditText tv_content = (EditText) view.findViewById(R.id.tv_content);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("输入提醒名称");
        TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_content.length() < 1) {
                    BaseApplication.getInstances().toast(EditRemindActivity.this,"请输入相关数据!");
                    return;
                }

                tv_remind_name.setText(tv_content.getText().toString());
                content = tv_content.getText().toString();

                if ((CheckUtil.isAllChineseChar(content) && content.length() > 4)) {
                    Toast.makeText(EditRemindActivity.this, "请输入正确的计划名称", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (content.length() > 8) {
                    Toast.makeText(EditRemindActivity.this, "请输入正确的计划名称", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (remindDialog != null) {
                    remindDialog.dismiss();
                }


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

    /*****************************
     * 数据模块
     ***************************/

    private void getTimeData() {
        arr_time.clear();
        arr_time.add("上午");
        arr_time.add("下午");
    }

    private void getHourData() {
        arr_hour.clear();
        for (int i = 0; i < 12; i++) {
            arr_hour.add(i + "");
        }

    }

    private void getMinuteData() {
        arr_minute.clear();
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                arr_minute.add("0" + i);
            } else {
                arr_minute.add(i + "");
            }

        }

    }

    private ScrollView scrollView;


    /**********************
     * adapter
     *********************/

    private class CalendarTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize,
                                      int minsize) {
            super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
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
