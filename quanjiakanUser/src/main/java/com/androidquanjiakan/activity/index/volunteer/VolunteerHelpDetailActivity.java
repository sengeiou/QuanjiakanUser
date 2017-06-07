package com.androidquanjiakan.activity.index.volunteer;

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
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.umeng.analytics.MobclickAgent;

public class VolunteerHelpDetailActivity extends BaseActivity implements View.OnClickListener{


	private ImageButton mBack;
	private TextView mTitle;
	private TextView mMenu;


	private ImageView mImage;
	private TextView mName;
	private ImageView mType;

	private TextView mPerson;
	private TextView mThing;
	private TextView mReason;
	private TextView mTime;
	private TextView mAddress;
	private TextView mInfo;

	private Button mCall;

	public static final String PARAMS_DETAIL_ID = "detail_id";
	private String mDetailID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_volunteer_help_detail);
		mDetailID = getIntent().getStringExtra(PARAMS_DETAIL_ID);
		if(mDetailID==null){
			BaseApplication.getInstances().toast(VolunteerHelpDetailActivity.this,"传入参数异常!");
			finish();
			return;
		}
		initTitle();
		initContent();
		loadInfo();
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


	public void initTitle(){
		mBack = (ImageButton) findViewById(R.id.ibtn_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(this);
		mTitle = (TextView) findViewById(R.id.tv_title);
		mTitle.setText("求助详情");
		mMenu = (TextView) findViewById(R.id.menu_text);
		mMenu.setVisibility(View.GONE);
	}

	public void initContent(){
		mImage = (ImageView) findViewById(R.id.image);
		mName = (TextView) findViewById(R.id.name);
		mType = (ImageView) findViewById(R.id.help_type);
		setHelpType(HELP_NORMAL);

		mPerson = (TextView) findViewById(R.id.person);
		mThing = (TextView) findViewById(R.id.thing);
		mReason = (TextView) findViewById(R.id.thing);
		mTime = (TextView) findViewById(R.id.time);
		mAddress = (TextView) findViewById(R.id.address);
		mInfo = (TextView) findViewById(R.id.other_info);


		mCall = (Button) findViewById(R.id.call);
		mCall.setOnClickListener(this);
		setButtonType(BTN_TO_CANCEL);
	}

	final int HELP_NORMAL = 1;
	final int HELP_HURRY = 2;
	public void setHelpType(int type){
		if(type==HELP_HURRY){
//			mType.setText("急助");
		}else{
//			mType.setText("助");
		}
	}

	final int BTN_TO_CANCEL = 1;
	final int BTN_CANCELLED = 2;
	final int BTN_FINISHED = 3;
	public void setButtonType(int type){
		if(type == BTN_TO_CANCEL){
			mCall.setBackgroundResource(R.drawable.selecter_hollow_transport_0e7b7b);
			mCall.setText(getString(R.string.cancel));
			mCall.setTextColor(getResources().getColor(R.color.font_color_0e7b7b));//@color/font_color_666666
		}else if(type == BTN_CANCELLED){
			mCall.setBackgroundResource(R.drawable.selecter_hollow_transport_666666);
			mCall.setText("已取消");
			mCall.setTextColor(getResources().getColor(R.color.font_color_666666));//@color/font_color_666666
		}else if(type == BTN_FINISHED){
			mCall.setBackgroundResource(R.drawable.selecter_hollow_transport_666666);
			mCall.setText("已完成");
			mCall.setTextColor(getResources().getColor(R.color.font_color_666666));//@color/font_color_666666
		}
	}

	public void loadInfo(){
		/**
		 * 从网络获取特定服务站的数据信息
		 */
		Picasso.with(this).load(R.drawable.ic_launcher).transform(new CircleTransformation()).into(mImage);
		mName.setText("编号:0810");
		mPerson.setText("人名");
		mThing.setText("求助的事情");
		mReason.setText("求助人求助原因");
		mTime.setText("帮助时间");
		mAddress.setText("帮助地址");
		mInfo.setText("补充的信息");
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.ibtn_back:
				finish();
				break;
			case R.id.call:
				Toast.makeText(this, mCall.getText().toString(), Toast.LENGTH_SHORT).show();
				break;
		}
	}

	class CircleTransformation implements Transformation {
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
