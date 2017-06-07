package com.androidquanjiakan.activity.index.doctor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquanjiakan.activity.common.ImageViewerActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.androidquanjiakan.view.CircleTransformation;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.androidquanjiakan.util.LogUtil;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 医生评价
 */
@SuppressLint("NewApi")
public class DoctorEvaluateActivity extends Activity implements OnClickListener{

	public static final String PARAMS_ID = "id";
	public static final String PARAMS_INFO = "info";
	public static final String PARAMS_INFO2 = "info2";
	private String id;
	private String info;
	private String info2;
	private JsonObject doctor;
	private TextView title;
	private ImageButton ibtn_back;
	private ImageView header;
	private TextView name;
	private TextView hospital;
	private TextView section;
	private TextView rank;
	private LinearLayout rate_great_line;
	private ImageView rate_great_img;
	private TextView rate_great_text;
	private LinearLayout rate_good_line;
	private ImageView rate_good_img;
	private TextView rate_good_text;
	private LinearLayout rate_bad_line;
	private ImageView rate_bad_img;
	private TextView rate_bad_text;
	private EditText evaluate;
	private Button btn_submit;

	private RatingBar rbar;

	private Dialog dialog;
	private final int MSG_SUBMIT = 10000;
	private final int GREAT = 3;
	private final int GOOD = 2;
	private final int BAD = 1;
	private int rate;

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case MSG_SUBMIT:
					BaseApplication.getInstances().toast(DoctorEvaluateActivity.this,"提交评价成功!");
					finish();
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_doctor_evaluate);
		id = getIntent().getStringExtra(PARAMS_ID);
		if(id==null){
			BaseApplication.getInstances().toast(DoctorEvaluateActivity.this,"传入参数异常!");
			finish();
			return;
		}

		initTitle();
		info = getIntent().getStringExtra(PARAMS_INFO);
		info2 = getIntent().getStringExtra(PARAMS_INFO2);
		if(info!=null && info.length()>0){
			doctor =  new GsonParseUtil(info).getJsonObject();
			LogUtil.e("评价 info **"+doctor);
		}else{
			doctor =  new GsonParseUtil(info2).getJsonObject();
			LogUtil.e("评价 info2 **"+doctor);
		}
		initView();

		loadData();
	}

	public void initView(){
		header = (ImageView) findViewById(R.id.header);
		header.setOnClickListener(this);
		name = (TextView) findViewById(R.id.name);
		hospital = (TextView) findViewById(R.id.hospital);
		section = (TextView) findViewById(R.id.section);
		rank = (TextView) findViewById(R.id.rank);
		rbar = (RatingBar) findViewById(R.id.rbar);

		rate_great_line = (LinearLayout) findViewById(R.id.rate_great_line);
		rate_great_img = (ImageView) findViewById(R.id.rate_great_img);
		rate_great_text = (TextView) findViewById(R.id.rate_great_text);
		rate_great_line.setOnClickListener(this);

		rate_good_line = (LinearLayout) findViewById(R.id.rate_good_line);
		rate_good_img = (ImageView) findViewById(R.id.rate_good_img);
		rate_good_text = (TextView) findViewById(R.id.rate_good_text);
		rate_good_line.setOnClickListener(this);

		rate_bad_line = (LinearLayout) findViewById(R.id.rate_bad_line);
		rate_bad_img = (ImageView) findViewById(R.id.rate_bad_img);
		rate_bad_text = (TextView) findViewById(R.id.rate_bad_text);
		rate_bad_line.setOnClickListener(this);

		evaluate = (EditText) findViewById(R.id.evaluate);

		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);

		rate = GREAT;
		rateDoctor(GREAT);
	};

	public void initTitle(){
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("评价");
		ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
	}

	public void loadData(){
		if(info!=null && info.startsWith("{")){
			doctor = new GsonParseUtil(info).getJsonObject();
			if (doctor.has("name")) {
				name.setText(doctor.get("name").getAsString());
				section.setText(doctor.get("clinic_name").getAsString());
				rank.setText(doctor.get("title").getAsString());
				hospital.setVisibility(View.VISIBLE);
				hospital.setText(doctor.get("hospital_name").getAsString());//去掉了这个字段hospital_name
				ImageLoadUtil.picassoLoad(header,doctor.get("icon").getAsString(),ImageLoadUtil.TYPE_CYCLE);
			}
		}else{
			HashMap<String, String> params = new HashMap<>();
			params.put("id", id);
			MyHandler.putTask(this,new Task(new HttpResponseInterface() {

				@Override
				public void handMsg(String val) {
					// TODO Auto-generated method stub
					doctor = new GsonParseUtil(val).getJsonObject();
					if (doctor!=null) {
						name.setText(doctor.get("name").getAsString());
						section.setText(doctor.get("clinic_name").getAsString());
						rank.setText(doctor.get("title").getAsString());
						hospital.setVisibility(View.VISIBLE);
						hospital.setText(doctor.get("hospital_name").getAsString());//去掉了这个字段hospital_name

						if(doctor.has("icon") && doctor.get("icon")!=null && !(doctor.get("icon") instanceof JsonNull) &&
								doctor.get("icon").getAsString().toLowerCase().startsWith("http")){
							Picasso.with(BaseApplication.getInstances()).load(doctor.get("icon").getAsString()).transform(new CircleTransformation()).into(header);
						}else{
							header.setImageResource(R.drawable.ic_patient);
						}

						if(doctor.has("recommend_rate") && !(doctor.get("recommend_rate") instanceof JsonNull)){
							rbar.setRating(doctor.get("recommend_rate").getAsInt());
						}else{
							rbar.setRating(5);
						}
					}
				}
			}, HttpUrls.getDoctorDetail(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(this)));
		}
	}

	public void submitEvaluate(){
		String text = evaluate.getText().toString();
		if(text==null || text.length()<1){
			BaseApplication.getInstances().toast(DoctorEvaluateActivity.this,"请输入评价内容!");
			return;
		}
		HashMap<String, String> params = new HashMap<>();
		params.put("doctor_id", doctor.get("id").getAsString());
		params.put("user_id", BaseApplication.getInstances().getUser_id());
		params.put("content", text);
		params.put("star", rate+"");//测试
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				if(val!=null){
					mHandler.sendEmptyMessage(MSG_SUBMIT);
				}
			}
		}, HttpUrls.doctorEvaluate(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(DoctorEvaluateActivity.this)));
	}

	public void rateDoctor(int rateVale){
		rate = rateVale;
		switch (rateVale){
			case GREAT:
				rate_great_img.setImageResource(R.drawable.evaluate_excellent_light);
				rate_great_text.setTextColor(getResources().getColor(R.color.font_color_rate_great));

				rate_good_img.setImageResource(R.drawable.evaluate_good);
				rate_good_text.setTextColor(getResources().getColor(R.color.font_color_rate_normal));

				rate_bad_img.setImageResource(R.drawable.evaluate_bad);
				rate_bad_text.setTextColor(getResources().getColor(R.color.font_color_rate_normal));

				break;
			case GOOD:
				rate_great_img.setImageResource(R.drawable.evaluate_excellent);
				rate_great_text.setTextColor(getResources().getColor(R.color.font_color_rate_normal));

				rate_good_img.setImageResource(R.drawable.evaluate_good_light);
				rate_good_text.setTextColor(getResources().getColor(R.color.font_color_rate_good));

				rate_bad_img.setImageResource(R.drawable.evaluate_bad);
				rate_bad_text.setTextColor(getResources().getColor(R.color.font_color_rate_normal));
				break;
			case BAD:
				rate_great_img.setImageResource(R.drawable.evaluate_excellent);
				rate_great_text.setTextColor(getResources().getColor(R.color.font_color_rate_normal));

				rate_good_img.setImageResource(R.drawable.evaluate_good);
				rate_good_text.setTextColor(getResources().getColor(R.color.font_color_rate_normal));

				rate_bad_img.setImageResource(R.drawable.evaluate_bad_light);
				rate_bad_text.setTextColor(getResources().getColor(R.color.font_color_rate_bad));
				break;
		}
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
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()){
			case R.id.header:
				if(doctor!=null &&
						doctor.has("icon") &&
						doctor.get("icon").getAsString()!=null &&
						doctor.get("icon").getAsString().toLowerCase().startsWith("http")){
					Intent intent = new Intent(this, ImageViewerActivity.class);
					intent.putExtra(BaseConstants.PARAMS_URL,doctor.get("icon").getAsString());
					startActivity(intent);
				}else{
					BaseApplication.getInstances().toast(DoctorEvaluateActivity.this,"该医护人员未设置头像!");
				}
				break;
			case R.id.ibtn_back:
				finish();
				break;
			case R.id.btn_submit:
				submitEvaluate();
				break;
			case R.id.rate_great_line:
				if(rate!=GREAT){
					rateDoctor(GREAT);
				}
				break;
			case R.id.rate_good_line:
				if(rate!=GOOD){
					rateDoctor(GOOD);
				}
				break;
			case R.id.rate_bad_line:
				if(rate!=BAD) {
					rateDoctor(BAD);
				}
				break;
			default:
				break;
		}
	}
}