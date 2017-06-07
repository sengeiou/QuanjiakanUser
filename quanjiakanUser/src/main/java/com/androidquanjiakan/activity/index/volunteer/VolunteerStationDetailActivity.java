package com.androidquanjiakan.activity.index.volunteer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.VolunteerStationEntity;
import com.androidquanjiakan.util.AmapLocationUtil;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

public class VolunteerStationDetailActivity extends BaseActivity implements View.OnClickListener{


	private ImageButton mBack;
	private TextView mTitle;
	private TextView mMenu;

	private ImageView mImage;
	private TextView mNumber;
	private TextView mName;
	private TextView mAddress;
	private TextView mTime;
	private TextView mContent;
	private TextView mSpecific;

	private Button mCall;

	public static final String PARAMS_STATION_ID = "station_id";
	private String mStationID;
//	private String name;
//	private String address;
	private String dutyDate;
	private String dutyTime;
	private String attended;
//	private List<JsonObject> dataList = new ArrayList<JsonObject>();
//
//	private String uri;
//	private String stationInfo;
	private VolunteerStationEntity stationInfo;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=VolunteerStationDetailActivity.this;
		setContentView(R.layout.layout_volunteer_station_detail);
		mStationID = getIntent().getStringExtra(PARAMS_STATION_ID);
		stationInfo = (VolunteerStationEntity)getIntent().getSerializableExtra("station_info");
		if(mStationID==null){
			BaseApplication.getInstances().toast(VolunteerStationDetailActivity.this,"传入参数异常!");
			finish();
			return;
		}
		initTitle();
		initContent();
	}

	public void initContent(){
//		LocationInfoUtil locationInfoUtil = new LocationInfoUtil(this);
		AmapLocationUtil amapLocationUtil = new AmapLocationUtil(this);
		//LogUtil.e("jingweidu+++++++++++++++"+amapLocationUtil.getLatitude()+"经度"+amapLocationUtil.getLongitude());
		mImage = (ImageView) findViewById(R.id.image);
		Picasso.with(context).load(stationInfo.getPhoto()).error(R.drawable.ic_launcher).into(mImage);
		mNumber = (TextView) findViewById(R.id.numbers);
		mNumber.setText("义工人数:"+1);//后面要完善的接口

		mName = (TextView) findViewById(R.id.name);
		mAddress = (TextView) findViewById(R.id.address);
		mTime = (TextView) findViewById(R.id.time);
		mContent = (TextView) findViewById(R.id.content);
		mSpecific = (TextView) findViewById(R.id.specific);
		mName.setText("服务站"+stationInfo.getName());
		mAddress.setText(stationInfo.getAddress()+"  "+"距离您"+amapLocationUtil.getDistance( stationInfo.getLat(),stationInfo.getLng())+"KM");
		mTime.setText(stationInfo.getDuty_date()+"  "+stationInfo.getDuty_time());//后面要完善的接口
		mContent.setText(stationInfo.getServices());
		mSpecific.setText(stationInfo.getOther_services());
		mCall = (Button) findViewById(R.id.call);
		mCall.setOnClickListener(this);
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
		mTitle.setText("服务站详情");
		mMenu = (TextView) findViewById(R.id.menu_text);
		mMenu.setText("发布记录");
		mMenu.setVisibility(View.VISIBLE);
		mMenu.setOnClickListener(this);
	}

	/*public void loadInfo(){
		*//**
		 * 从网络获取特定服务站的数据信息
		 *//*
		HashMap<String, String> params = new HashMap<>();
		params.put("member_id", "{}");
		params.put("lat", "{}");
		params.put("lng", "{}");
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {


			@Override
			public void handMsg(String val) {
				if(val!=null && val.length()>0){
					final LocationInfoUtil locationInfoUtil = new LocationInfoUtil(getApplicationContext());
					JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
					JsonArray docList = jsonObject.get("rows").getAsJsonArray();
					final JsonObject object = docList.get(0).getAsJsonObject();
					name = object.get("name").getAsString();
					address = object.get("address").getAsString();
					uri = object.get("photo").getAsString();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							Picasso.with(getApplicationContext()).load(uri).error(R.drawable.ic_launcher).into(mImage);
							mNumber.setText("义工人数:"+attended);
							mName.setText(name);
							mAddress.setText(address+"  "+"距离您"+locationInfoUtil.getDistance( Double.parseDouble(object.get("lat").getAsString()),Double.parseDouble(object.get("lng").getAsString()))+"KM");
							mTime.setText(dutyDate+"  "+dutyTime);
							mContent.setText("服务内容");
							mSpecific.setText("服务站特色服务");

						}
					});

				}

			}
		},  HttpUrls.getStationList(), params, Task.TYPE_GET_STRING_NOCACHE, null));

	}*/



	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.ibtn_back:
				finish();
				break;
			case R.id.menu_text://跳转到发布记录
				Intent publish = new Intent(VolunteerStationDetailActivity.this, VolunteerPublishHistoryActivity.class);
				startActivity(publish);
				break;
			case R.id.call:
				Intent intent =  new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + stationInfo.getPhone()));//跳转到拨号界面，同时传递电话号码
				startActivity(intent);
			    break;
		}
	}


}
