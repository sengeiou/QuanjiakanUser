package com.androidquanjiakan.activity.index.volunteer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.PublishVolunteerEntity;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;



/**
 * Created by Gin on 2016/11/19.
 */

public class VoluteerOrderDetailActivity extends BaseActivity {

    private ImageView iv_head_icon;
    private TextView tv_id;
    private ImageView iv_help;
    private TextView tv_thing_value;
    private TextView tv_reason_value;
    private TextView tv_time_value;
    private TextView tv_location_value;
    private TextView tv_other_value;
    private TextView mTitle;
    private ImageButton mBack;
    private PublishVolunteerEntity volunteerEntity;
    private String id;
    private String level;
    private String title;
    private String info;
    private String begintime;
    private String address;
    private Context context;
    private String status;
    private Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=VoluteerOrderDetailActivity.this;
        setContentView(R.layout.activity_voluteer_order_detail);
        initTitle();
        initData();
    }

    private void initTitle() {
        mBack = (ImageButton) findViewById(R.id.ibtn_back);
        mBack.setVisibility(View.VISIBLE);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTitle = (TextView) findViewById(R.id.tv_title);
        mTitle.setText("求助详情");

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
        iv_head_icon = (ImageView) findViewById(R.id.head_icon);
        Picasso.with(BaseApplication.getInstances()).load(R.drawable.ic_launcher).transform(new CircleTransformation()).into(iv_head_icon);
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_id.setText("编号:"+id);
        iv_help = (ImageView)findViewById(R.id.iv_help);
        if(level.equals("1")) {
            Picasso.with(context).load(R.drawable.record_icon_help).into(iv_help);
        }else if(level.equals("2")) {
            Picasso.with(context).load(R.drawable.record_icon_urgent).into(iv_help);
        }
        tv_thing_value = (TextView)findViewById(R.id.tv_thing_value);
        tv_thing_value.setText(title);
        tv_reason_value = (TextView)findViewById(R.id.tv_reason_value);
        tv_reason_value.setText(info);
        tv_time_value = (TextView)findViewById(R.id.tv_time_value);
        tv_time_value.setText(begintime);
        tv_location_value = (TextView) findViewById(R.id.tv_location_value);
        tv_location_value.setText(address);
        tv_other_value = (TextView)findViewById(R.id.tv_other_value);
        tv_other_value.setText(info);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        if(status.equals("1")) {
            //进来表示要完成
            btn_submit.setBackgroundResource(R.drawable.cancel_border);
            btn_submit.setText("完成");
            btn_submit.setTextColor(getResources().getColor(R.color.volunteercom));
        }else if(status.equals("2")) {
            //进来表示已完成
            btn_submit.setText("已完成");
        }
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_submit.getText().toString().trim().equals("完成")) {
                    //请求网络确定完成
                    HashMap<String, String> params = new HashMap<>();
                    MyHandler.putTask(new Task(new HttpResponseInterface() {
                        @Override
                        public void handMsg(String val) {
                            HttpResponseResult result = new HttpResponseResult(val);
                            if(result.isResultOk()) {
                                Toast.makeText(VoluteerOrderDetailActivity.this, "完成成功", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            }else {
                                BaseApplication.getInstances().toast(VoluteerOrderDetailActivity.this,"完成失败");
                            }

                        }
                    }, HttpUrls.finishVoluteerOrder(QuanjiakanSetting.getInstance().getUserId()+"",id),params,Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(context)));

                }else if(btn_submit.getText().toString().trim().equals("已完成")) {
                    finish();
                }
            }
        });


    }

    private void initData() {
        volunteerEntity = (PublishVolunteerEntity) getIntent().getSerializableExtra("itemdata");
        id = volunteerEntity.getId();
        level = volunteerEntity.getLevel();//急
        title = volunteerEntity.getTitle();  //事务
        info = volunteerEntity.getInfo(); //原因
        begintime = volunteerEntity.getBegintime();//服务时间
        address = volunteerEntity.getAddress();
        status = volunteerEntity.getStatus();

        initView();
    }


    public class CircleTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap bitmap) {
            int size = Math.min(bitmap.getWidth(),bitmap.getHeight());
            int x= (bitmap.getWidth()-size)/2;
            int y= (bitmap.getHeight()-size)/2;

            Bitmap squaredBitmap  = Bitmap.createBitmap(bitmap, x, y, size, size);
            if (squaredBitmap!=bitmap){
                bitmap.recycle();
            }

            Bitmap bm = Bitmap.createBitmap(size, size, bitmap.getConfig());
            Canvas canvas = new Canvas(bm);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);//设置去锯齿
            float r = size/2f;
            float r1 = (size- QuanjiakanUtil.dip2px(BaseApplication.getInstances(),5))/2f;
            canvas.drawCircle(r,r,r1,paint);
            squaredBitmap.recycle();
            return bm;

        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
