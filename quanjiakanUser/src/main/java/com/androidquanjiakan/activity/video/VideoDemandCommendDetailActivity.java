package com.androidquanjiakan.activity.video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.adapter.VideoDemandCommendDetailAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.VideoCommendListEntity;
import com.androidquanjiakan.entity.VideoCommendSubItemEntity;
import com.androidquanjiakan.entity.VideoDemandListEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.view.CircleTransformation;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.quanjiakanuser.util.KeyBoardUtils;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 点播评论
 */
public class VideoDemandCommendDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title;
    private ImageButton ibtn_back;
    private ImageButton ibtn_menu;
    private TextView menu_text;

    private LinearLayout container;

    //
    private RelativeLayout content_line;
    private ImageView user_header;
    private TextView name;
    private TextView time;
    private TextView floor;
    private ImageView more;

    //
    private TextView content;
    //
    private View commend_div;

    private ListView more_commend;

    private SwipeRefreshLayout refresh;

    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");

    //
    private EditText mInput;
    private LinearLayout apprise_line;
    private ImageView apprise_img;
    private TextView apprise_number;
    private TextView tv_sendtext;

    public static final String PARAMS_VID = "video_id";
    public static final String PARAMS_CID = "commend_id";
    public static final String PARAMS_FID = "floor_id";

    private VideoDemandListEntity entity;

    private String vid = null;
    private String cid = null;
    private String fid = null;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.layout_video_demand_single_commend_detail);
        vid = getIntent().getStringExtra(PARAMS_VID);
        cid = getIntent().getStringExtra(PARAMS_CID);
        fid = getIntent().getStringExtra(PARAMS_FID);
        entity = (VideoDemandListEntity) getIntent().getSerializableExtra(VideoDemandCommendListActivity.PARAMS_VIDEO_ENTITY);
        if (vid == null || cid == null || entity == null) {
            BaseApplication.getInstances().toast(VideoDemandCommendDetailActivity.this,"传入参数有误!");
            Intent intent = new Intent();
            intent.putExtra(VideoDemandCommendListActivity.PARAMS_VIDEO_ENTITY, entity);
            setResult(RESULT_CANCELED, intent);
            finish();
            return;
        }

        initTitleBar();
        initView();
        initInput();
    }

    public void initInput() {
        mInput = (EditText) findViewById(R.id.et_comment);
        /**
         * TODO 看是否需要处理
         */
        apprise_line = (LinearLayout) findViewById(R.id.apprise_line);
        apprise_line.setOnClickListener(this);
        apprise_img = (ImageView) findViewById(R.id.apprise_img);
        apprise_number = (TextView) findViewById(R.id.apprise_number);

        apprise_number.setText(entity.getLikeCount() + "");
        if (entity.getLikes() == 0) {
            apprise_img.setImageResource(R.drawable.icon_streaming_details_like_nor);
        } else {
            apprise_img.setImageResource(R.drawable.icon_streaming_details_like_sel_c);
        }

        tv_sendtext = (TextView) findViewById(R.id.tv_sendtext);
        tv_sendtext.setOnClickListener(this);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() < 1) {
                    tv_sendtext.setVisibility(View.GONE);
                    apprise_line.setVisibility(View.VISIBLE);
                } else {
                    tv_sendtext.setVisibility(View.VISIBLE);
                    apprise_line.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initIDs();
    }

    private String root_id;
    private String parent_id;
    private String isFloor;

    public void initIDs() {
        root_id = cid;
        parent_id = cid;
        isFloor = "1";
        mInput.setText("");
        mInput.setHint("我也说一句...");
    }

    /**
     * 加载信息
     */
    private List<VideoCommendListEntity> dataList = new ArrayList<>();
    private VideoDemandCommendDetailAdapter mAdapter;

    public void loadInfo() {
        if (mAdapter == null) {
            mAdapter = new VideoDemandCommendDetailAdapter(this, new ArrayList<VideoCommendSubItemEntity>());
            more_commend.setAdapter(mAdapter);
        } else {
        }
        //TODO 存在分页时****理论上应该有分页，但接口没做分页
        if (dataList == null) {
            dataList = new ArrayList<>();
        } else {
            dataList.clear();
        }
//        //无分页时
//        dataList.clear();
        HashMap<String, String> map = new HashMap<>();
        MyHandler.putTask(VideoDemandCommendDetailActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if (val != null && !"".equals(val) &&
                        val.startsWith("{") && val.endsWith("}")) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull)) {
                        List<VideoCommendListEntity> tempList = (List<VideoCommendListEntity>) SerialUtil.jsonToObject(jsonObject.get("object").
                                getAsJsonArray().toString(), new TypeToken<List<VideoCommendListEntity>>() {
                        }.getType());
                        if (tempList != null && tempList.size() > 0) {
                            content_line.setVisibility(View.VISIBLE);
                            LogUtil.e("获取到的该评论的子评论数量:" + ((tempList.get(0).getChildAppraises() != null) ? tempList.get(0).getChildAppraises().size() : 0));
                            if (tempList.get(0).getChildAppraises() != null && tempList.get(0).getChildAppraises().size() > 0) {
                                commend_div.setVisibility(View.VISIBLE);
                                more_commend.setVisibility(View.VISIBLE);
                                more_commend.setDivider(null);
                                more_commend.setDividerHeight(0);

                                dataList.addAll(tempList);
                                mAdapter.setData(dataList.get(0).getChildAppraises());
                                mAdapter.notifyDataSetChanged();
                            } else {
                                commend_div.setVisibility(View.GONE);
                                more_commend.setVisibility(View.GONE);
                                more_commend.setDivider(null);
                                more_commend.setDividerHeight(0);
                            }
//                            TODO 加载进View内
                            setInfo(dataList.get(0));
//                            addMoreCommend(dataList.get(0).getChildAppraises());
                            resetWholeHeight();
                        } else {
                            content_line.setVisibility(View.GONE);
                            commend_div.setVisibility(View.GONE);
                            more_commend.setVisibility(View.GONE);
                            more_commend.setDivider(null);
                            more_commend.setDividerHeight(0);
                        }
                    }

                    initIDs();
                } else {
                    content_line.setVisibility(View.GONE);
                }
                refresh.setRefreshing(false);
            }
        }, HttpUrls.getVideoCommendDetail() + "&ideoDemandId=" + vid
                + "&id=" + cid, map, Task.TYPE_GET_STRING_NOCACHE,
                QuanjiakanDialog.getInstance().getDialog(this)));
    }

    public void setInfo(final VideoCommendListEntity entity) {
        more.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.touxiang_big_icon).transform(new CircleTransformation()).into(user_header);

        name.setText(entity.getName());
        time.setText(sdf.format(new Date(entity.getCreatetime())));
        floor.setText((Integer.parseInt(fid) + 1) + "楼");
        content.setText(entity.getContent());
        content_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击回复该评论人
                callCommend(entity.getName(), entity.getId() + "", entity.getId() + "", "1");
            }
        });
    }

    public void addMoreCommend(final List<VideoCommendSubItemEntity> data) {
        if (data != null) {
            more_commend.removeAllViews();//清除原子View，防止复用造成的数据混乱
            int size = 0;
            size = data.size();
            if (size > 0) {
                commend_div.setVisibility(View.VISIBLE);
            } else {
                commend_div.setVisibility(View.GONE);
            }
            for (int i = 0; i < size; i++) {
                final int position = i;
                View moreCommend = LayoutInflater.from(VideoDemandCommendDetailActivity.this).inflate(R.layout.item_video_demand_more_commend, null);
                TextView content = (TextView) moreCommend.findViewById(R.id.more_commend_content);
                inputMoreCommendContent(content, data.get(i));
                moreCommend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击回复该评论的发送人
                        callCommend(data.get(position).getName(), data.get(position).getRootAppraiseId() + "", data.get(position).getId() + "", "1");
                    }
                });
                more_commend.addView(moreCommend);
            }
        }
    }

    private InputMethodManager imm;

    public void showSoftInput(View view) {
//        isOpenSoftInput();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (imm == null) {
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public void hideSoftInput(View view) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (imm == null) {
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void callCommend(String name, String rootID, String parentID, String floor) {
        mInput.setHint("@" + name);
        root_id = rootID;
        parent_id = parentID;
        isFloor = floor;
        KeyBoardUtils.openKeybord(mInput,VideoDemandCommendDetailActivity.this);
    }

    /**
     * 展示评论的实际内容
     *
     * @param container
     * @param inputData
     */
    public void inputMoreCommendContent(TextView container, VideoCommendSubItemEntity inputData) {
        //需要根据关系来进行判断
        //判断是否是楼主，若是则添加一个楼主标识
        //回复内容格式  谁[根据是否是楼主添加标识图] 回复 @谁[根据是否是楼主添加标识图]: 回复内容
        String string = (inputData.getName() + "") + " 回复 " + "@" + (inputData.getToname() + "") + ": " + inputData.getContent();
        SpannableString span = new SpannableString(string);
        /**
         * TODO 若要区分点击，此处需要更换类型
         */
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#999999")), 0, (inputData.getName() + "").length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), (inputData.getName() + "").length(), ((inputData.getName() + "") + " 回复 ").length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        /**
         * TODO 若要区分点击，此处需要更换类型
         */
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_title_green)), ((inputData.getName() + "") + " 回复 ").length(),
                ((inputData.getName() + "") + " 回复 " + "@" + (inputData.getToname() + "")).length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), ((inputData.getName() + "") + " 回复 " + "@" + (inputData.getToname() + "")).length(),
                string.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        container.setText(span);
    }

    private Handler mHandler = new Handler();

    public void resetWholeHeight() {
        // calculate height of all items.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ListAdapter la = more_commend.getAdapter();
                if (null == la) {
                    return;
                }
                // calculate height of all items.
                int h = 0;
                final int cnt = la.getCount();
                for (int i = 0; i < cnt; i++) {
                    View item = la.getView(i, null, more_commend);

                    int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    item.measure(width, height);
                    LogUtil.e("Item " + i + "     Height: " + item.getMeasuredHeight());
                    h += item.getMeasuredHeight();
                }
                // reset ListView height
                ViewGroup.LayoutParams lp = more_commend.getLayoutParams();
                lp.height = h + (more_commend.getDividerHeight() * (cnt - 1)) +
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics());
                more_commend.setLayoutParams(lp);
                LogUtil.e("Resize Height:" + lp.height + "");
            }
        }, 1000);
    }

    public void initView() {
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setColorSchemeResources(R.color.holo_blue_light, R.color.holo_orange_light, R.color.holo_green_light);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadInfo();
            }
        });

        content_line = (RelativeLayout) findViewById(R.id.content_line);
        user_header = (ImageView) findViewById(R.id.user_header);
        name = (TextView) findViewById(R.id.name);
        time = (TextView) findViewById(R.id.time);

        floor = (TextView) findViewById(R.id.floor);

        more = (ImageView) findViewById(R.id.more);
        more.setVisibility(View.GONE);
        content = (TextView) findViewById(R.id.content);
        //更多评论的分割条
        commend_div = findViewById(R.id.commend_div);
        //更多评论的容器
        more_commend = (ListView) findViewById(R.id.more_commend);

        loadInfo();
    }

    public void initTitleBar() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("更多评论");

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        ibtn_menu = (ImageButton) findViewById(R.id.ibtn_menu);
        ibtn_menu.setVisibility(View.GONE);
        ibtn_menu.setOnClickListener(this);

        menu_text = (TextView) findViewById(R.id.menu_text);
        menu_text.setVisibility(View.GONE);
        menu_text.setOnClickListener(this);
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

    public void sendCommend() {
//        hideSoftInput(mInput);
        KeyBoardUtils.closeKeybord(mInput,VideoDemandCommendDetailActivity.this);
        if (mInput.getText().length() < 1) {
            BaseApplication.getInstances().toast(VideoDemandCommendDetailActivity.this,"请输入评论!");
            return;
        }
        if (root_id == null) {
            root_id = "0";
        }
        if (isFloor == null) {
            isFloor = "0";
        }
        if (parent_id == null) {
            parent_id = "0";
        }

        String toname = "";
        if (mInput.getHint() == null || !mInput.getHint().toString().contains("@")) {
            toname = BaseApplication.getInstances().getKeyValue("nickname").replace("'", "");
        } else {
            toname = mInput.getHint().toString().replace("@", "");
        }

        String content = " ";
        try {
            content = URLEncoder.encode(mInput.getText().toString(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, String> map = new HashMap<>();
        MyHandler.putTask(VideoDemandCommendDetailActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                //TODO 成功后刷新数据
                if (val != null && !"".equals(val) &&
                        val.startsWith("{") && val.endsWith("}")) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (jsonObject != null && jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull)) {
                        if ("200".equals(jsonObject.get("code").getAsString())) {
                            loadInfo();

                            //重置所有信息
                        }
                    }
                }
                tv_sendtext.setEnabled(true);
            }
        }, HttpUrls.sendVideoCommend() + "&ideoDemandId=" + vid
                + "&rootAppraiseId=" + root_id
                + "&content=" + content
                + "&isFloor=" + isFloor
                + "&parentId=" + parent_id
                + "&toname=" + toname, map, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apprise_line: {
                apprise_line.setEnabled(false);
                if (entity.getLikes() == 0) {
                    MyHandler.putTask(this, new Task(new HttpResponseInterface() {
                        @Override
                        public void handMsg(String val) {
                            apprise_line.setEnabled(true);
                            if (val != null && val.length() > 0) {
                                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                                if (jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull)
                                        && "200".equals(jsonObject.get("code").getAsString())) {
                                    apprise_img.setImageResource(R.drawable.icon_streaming_details_like_sel_c);
                                    entity.setLikes(1);
                                    entity.setMemberId(Integer.parseInt(BaseApplication.getInstances().getUser_id()));
                                    /**
                                     * 重置显示数量
                                     */
                                    if (jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull) &&
                                            jsonObject.get("object").getAsJsonObject().has("likeCount")) {
                                        entity.setLikeCount(jsonObject.get("object").getAsJsonObject().get("likeCount").getAsInt());
                                    } else {
                                        entity.setLikeCount(entity.getLikeCount() + 1);
                                    }
                                    apprise_number.setText("" + entity.getLikeCount());
                                }
                            }
                        }
                    }, HttpUrls.getDemandVideoAddPraiseNumber() + "&memberId=" +
                            BaseApplication.getInstances().getUser_id() +
                            "&id=" + entity.getId() + "&likes=1", null, Task.TYPE_GET_STRING_NOCACHE, null));
                } else {
                    MyHandler.putTask(this, new Task(new HttpResponseInterface() {
                        @Override
                        public void handMsg(String val) {
                            apprise_line.setEnabled(true);
                            if (val != null && val.length() > 0) {
                                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                                if (jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull)
                                        && "200".equals(jsonObject.get("code").getAsString())) {
                                    apprise_img.setImageResource(R.drawable.icon_streaming_details_like_nor);
                                    entity.setLikes(0);
                                    entity.setMemberId(Integer.parseInt(BaseApplication.getInstances().getUser_id()));
                                    /**
                                     * 重置显示数量
                                     */
                                    if (jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull) &&
                                            jsonObject.get("object").getAsJsonObject().has("likeCount")) {
                                        entity.setLikeCount(jsonObject.get("object").getAsJsonObject().get("likeCount").getAsInt());
                                    } else {
                                        entity.setLikeCount(entity.getLikeCount() - 1);
                                    }
                                    apprise_number.setText("" + entity.getLikeCount());
                                }
                            }
                        }
                    }, HttpUrls.getDemandVideoAddPraiseNumber() + "&memberId=" +
                            BaseApplication.getInstances().getUser_id() +
                            "&id=" + entity.getId() + "&likes=0", null, Task.TYPE_GET_STRING_NOCACHE, null));
                }
                break;
            }
            case R.id.tv_sendtext: {
                //TODO 调用发送
                tv_sendtext.setEnabled(false);
                sendCommend();
                break;
            }
            case R.id.ibtn_back: {
                Intent intent = new Intent();
                intent.putExtra(VideoDemandCommendListActivity.PARAMS_VIDEO_ENTITY, entity);
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            }
            case R.id.ibtn_menu: {
                break;
            }
            case R.id.menu_text: {
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra(VideoDemandCommendListActivity.PARAMS_VIDEO_ENTITY, entity);
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
