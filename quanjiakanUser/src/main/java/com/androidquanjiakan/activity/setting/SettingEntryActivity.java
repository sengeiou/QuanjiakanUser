package com.androidquanjiakan.activity.setting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.watch_child.ChildWatchSettingEntryActivity;
import com.androidquanjiakan.activity.index.watch_child.WatchChildEntryActivity;
import com.androidquanjiakan.activity.index.watch_old.HealthDynamicsActivity;
import com.androidquanjiakan.activity.setting.concern.ConcernListActivity;
import com.androidquanjiakan.activity.setting.ebean.EBeanChargeActivity;
import com.androidquanjiakan.activity.setting.giftrecord.GiftRecordActivity;
import com.androidquanjiakan.activity.setting.health.CaseHistoryPicActivity;
import com.androidquanjiakan.activity.setting.insure.InsuranceListActivity;
import com.androidquanjiakan.activity.setting.message.VolunteerMessageActivity;
import com.androidquanjiakan.activity.setting.order.HouseKeeperOrderHistoryListActivity;
import com.androidquanjiakan.activity.setting.order.OutInquiryListActivity;
import com.androidquanjiakan.activity.setting.order.PhoneDoctorOrderListActivity;
import com.androidquanjiakan.activity.setting.other.AboutUsActivity;
import com.androidquanjiakan.activity.setting.other.FamilyPhoneNumberActivity;
import com.androidquanjiakan.activity.setting.other.SceneModuleDeviceListActivity;
import com.androidquanjiakan.activity.setting.other.SettingActivity;
import com.androidquanjiakan.activity.setting.wallet.MyMoneyActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.util.NetworkUtils;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;


public class SettingEntryActivity extends BaseActivity implements OnClickListener{

	protected TextView tv_aboutus,tv_device,tv_setting,tv_qingjing,tv_haoma;

	private View tv_device_div;
	private View tv_qingjingmoshi_div;
	private View tv_qinqinghaoma_div;
	private View tv_aboutus_div;

	private TextView mVolunteer;
	private View mVolunteerDiv;

	//个人信息
	private TextView modify;
	private ImageView user_header_img;
	private TextView user_name;
	private TextView user_level;


	private TextView tv_missing;
	private View tv_missing_div;

	private LinearLayout ll_consult;
	private LinearLayout ll_phonedocter;

	private LinearLayout ll_inquiry;
	private LinearLayout ll_housekeeper;
	private LinearLayout ll_insure;

	private RelativeLayout rl_bean;//E豆
	private TextView tv_ebean_arrow;
	private View tv_ebean_div;

	private TextView tv_device_setting;
	private View tv_device_setting_div;

	private TextView tv_device_child;
	private View tv_device_child_div;

	private RelativeLayout rl_level;//E豆
	private TextView tv_level_arrow;
	private View tv_level_div;
	private int ebeans;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting_entry_backup);
		initTitleBar();
		initView();

		initEBean();
		initLevel();

		initDeviceSetting();
		initDeviceChild();
	}


	private TextView tv_title;
	private TextView menu_text;
	public void initTitleBar(){
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("我的");
		menu_text = (TextView) findViewById(R.id.menu_text);
		menu_text.setVisibility(View.VISIBLE);
		menu_text.setText("我的钱包");
		menu_text.setOnClickListener(this);
	}

	private void initEBean(){
		rl_bean = (RelativeLayout) findViewById(R.id.rl_bean);
		rl_bean.setOnClickListener(this);

		tv_ebean_arrow = (TextView) findViewById(R.id.tv_ebean_arrow);

		tv_ebean_div = findViewById(R.id.tv_ebean_div);
		tv_ebean_div.setVisibility(View.VISIBLE);

		/**
		 * 如果不显示出来
		 */
//		rl_bean.setVisibility(GONE);
//		tv_ebean_div.setVisibility(GONE);

		/**
		 * TODO 获取当前用户的E豆数量
		 */
		getEBeanValue();
	}

	private void initDeviceSetting(){
		tv_device_setting = (TextView) findViewById(R.id.tv_device_setting);
		tv_device_setting_div = findViewById(R.id.tv_device_setting_div);
		tv_device_setting.setOnClickListener(this);

//		tv_device_setting.setVisibility(View.VISIBLE);
//		tv_device_setting_div.setVisibility(View.VISIBLE);

		tv_device_setting.setVisibility(View.GONE);
		tv_device_setting_div.setVisibility(View.GONE);
	}


	private void initDeviceChild(){
		tv_device_child = (TextView) findViewById(R.id.tv_device_child);
		tv_device_child_div = findViewById(R.id.tv_device_div);
		tv_device_child.setOnClickListener(this);

//		tv_device_child.setVisibility(View.VISIBLE);
//		tv_device_child_div.setVisibility(View.VISIBLE);

		tv_device_child.setVisibility(View.GONE);
		tv_device_child_div.setVisibility(View.GONE);
	}

	public void getEBeanValue(){

	}

	private void initLevel(){
		rl_level = (RelativeLayout) findViewById(R.id.rl_level);
		rl_level.setOnClickListener(this);

		tv_level_arrow = (TextView) findViewById(R.id.tv_level_arrow);

		tv_level_div = findViewById(R.id.tv_level_div);
		tv_level_div.setVisibility(View.VISIBLE);

		/**
		 * 如果不显示出来
		 */
//		rl_level.setVisibility(GONE);
//		tv_level_div.setVisibility(GONE);
		getLevel();
	}

	public void getLevel(){

	}

	protected void initView(){

		ll_phonedocter = (LinearLayout) findViewById(R.id.ll_phonedocter);
		ll_insure = (LinearLayout) findViewById(R.id.ll_insure);
		ll_consult = (LinearLayout)findViewById(R.id.ll_consult);
		ll_inquiry = (LinearLayout)findViewById(R.id.ll_inquiry);
		ll_housekeeper  = (LinearLayout)findViewById(R.id.ll_housekeeper);

		tv_aboutus = (TextView)findViewById(R.id.tv_aboutus);
		tv_aboutus_div = findViewById(R.id.tv_aboutus_div);

		tv_device = (TextView)findViewById(R.id.tv_device);
		tv_device_div = findViewById(R.id.tv_device_div);

		tv_setting = (TextView)findViewById(R.id.tv_setting);

		tv_qingjing = (TextView)findViewById(R.id.tv_qingjingmoshi);
		tv_qingjingmoshi_div = findViewById(R.id.tv_qingjingmoshi_div);

		tv_haoma = (TextView)findViewById(R.id.tv_qinqinghaoma);
		tv_qinqinghaoma_div = findViewById(R.id.tv_qinqinghaoma_div);

		mVolunteer = (TextView) findViewById(R.id.tv_volunteer);
		mVolunteerDiv = findViewById(R.id.tv_volunteer_div);

		mVolunteer.setVisibility(View.VISIBLE);
		mVolunteerDiv.setVisibility(View.VISIBLE);


		tv_missing = (TextView) findViewById(R.id.tv_missing);
		tv_missing_div = findViewById(R.id.tv_missing_div);

		//个人头像、姓名

		user_header_img = (ImageView) findViewById(R.id.user_header_img);
		user_name = (TextView) findViewById(R.id.user_name);
		user_level = (TextView) findViewById(R.id.user_level);
		modify = (TextView) findViewById(R.id.modify);
		modify.setText("关注的人");
		modify.setOnClickListener(this);


		//健康档案
		tv_missing.setVisibility(View.VISIBLE);
		tv_missing_div.setVisibility(View.VISIBLE);

		//设备绑定
//		if(LogUtil.BINDDEVICE_OFF==true){
//			tv_device.setVisibility(View.GONE);
//			tv_device_div.setVisibility(View.GONE);
//		}else{
//			tv_device.setVisibility(View.VISIBLE);
//			tv_device_div.setVisibility(View.VISIBLE);
//		}

		tv_device.setVisibility(View.GONE);
		tv_device_div.setVisibility(View.GONE);


		//义工
		mVolunteer.setVisibility(View.GONE);
		mVolunteerDiv.setVisibility(View.GONE);
		/**
		 * 隐藏暂时没有实现的部分
		 */
		//情景模式
		tv_qingjing.setVisibility(View.GONE);
		tv_qingjingmoshi_div.setVisibility(View.GONE);
		//亲情号码
		tv_haoma.setVisibility(View.GONE);
		tv_qinqinghaoma_div.setVisibility(View.GONE);
		//保险
		ll_insure.setVisibility(View.GONE);

		//*************************************需要变更位置的部分
		//我的咨询
		ll_consult.setVisibility(View.GONE);

		ll_consult.setOnClickListener(this);
		ll_inquiry.setOnClickListener(this);
		ll_housekeeper.setOnClickListener(this);
		tv_aboutus.setOnClickListener(this);
		tv_device.setOnClickListener(this);
		tv_setting.setOnClickListener(this);
		tv_qingjing.setOnClickListener(this);
		tv_haoma.setOnClickListener(this);
		ll_phonedocter.setOnClickListener(this);
		ll_insure.setOnClickListener(this);

		mVolunteer.setOnClickListener(this);

		tv_missing.setOnClickListener(this);

		loadNameAndImage();
//		setUserLevel("1");
	}

	public void setUserLevel(String level){
		SpannableString span = new SpannableString(level);
		span.setSpan(new ForegroundColorSpan(Color.parseColor("#35cbcb")), 0, level.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, level.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		user_level.setText(span);
	}

	public void getLevelInfo(){
		HashMap<String, String> params = new HashMap<>();
		Task task = new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				JSONObject json;
				// TODO Auto-generated method stub
				if (val != null && !val.equals("") && val.toLowerCase().startsWith("{")) {
					//上传文件成功
					try {
						json = new JSONObject(val);
						if(json.has("code") && "200".equals(json.getString("code"))
								&& json.has("object") && json.getJSONObject("object")!=null
								){
							if(json.getJSONObject("object").has("grade")){

								setUserLevel(json.getJSONObject("object").getString("grade"));
							}else{
								setUserLevel("0");
							}
						}else{
							setUserLevel("0");
						}
						return;
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else if(val==null || "".equals(val)){
					/**
					 * TODO 设置 用户等级
					 */
					setUserLevel("0");
				} else {
					/**
					 * TODO 设置 用户等级
					 */
					setUserLevel("0");
				}
			}
		}, HttpUrls.getUserLevelInfo(), params, Task.TYPE_GET_STRING_NOCACHE, null);
		MyHandler.putTask(SettingEntryActivity.this, task);
	}

	private UserInfo info;
	public void loadInfo(){
		if(!NetUtil.isNetworkAvailable(this)){
			user_name.setText(BaseApplication.getInstances().getKeyValue("nickname"));
			return;
		}
		info = null;
		info = JMessageClient.getMyInfo();
		if(info!=null){
			if(info.getAvatar()!=null){
				File file = info.getAvatarFile();
				ImageLoadUtil.loadImage(user_header_img,file.getAbsolutePath(),ImageLoadUtil.optionsCircle);
			}else{
				user_header_img.setImageResource(R.drawable.touxiang_big_icon);
			}

			if(BaseApplication.getInstances().getKeyValue("nickname")!=null){
				user_name.setText(BaseApplication.getInstances().getKeyValue("nickname").replace("'",""));
			}else{
				if(info.getNickname()!=null){
					user_name.setText(info.getNickname());
				}else{
					user_name.setText(info.getUserName());
				}
			}
		}else{
			JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX+BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
				@Override
				public void gotResult(int i, String s) {
					if(i==0){
						info = JMessageClient.getMyInfo();
						if(info.getAvatar()!=null){
							File file = info.getAvatarFile();
							ImageLoadUtil.loadImage(user_header_img,file.getAbsolutePath(),ImageLoadUtil.optionsCircle);
						}else{
							user_header_img.setImageResource(R.drawable.touxiang_big_icon);
						}
						if(BaseApplication.getInstances().getKeyValue("nickname")!=null){
							user_name.setText(BaseApplication.getInstances().getKeyValue("nickname").replace("'",""));
						}else{
							if(info.getNickname()!=null){
								user_name.setText(info.getNickname());
							}else{
								user_name.setText(info.getUserName());
							}
						}
					}else{
						user_header_img.setImageResource(R.drawable.touxiang_big_icon);
						user_name.setText(BaseApplication.getInstances().getUser_id());
					}
				}
			});
		}
	}



	public void reloadInfo(){
		info = null;
		info = JMessageClient.getMyInfo();
		if(info!=null){
			if(info.getAvatar()!=null){
				File file = info.getAvatarFile();
				ImageLoadUtil.loadImage(user_header_img,file.getAbsolutePath(),ImageLoadUtil.optionsCircle);
			}else{
				user_header_img.setImageResource(R.drawable.touxiang_big_icon);
			}

			if(BaseApplication.getInstances().getKeyValue("nickname")!=null){
				user_name.setText(BaseApplication.getInstances().getKeyValue("nickname").replace("'",""));
			}else{
				if(info.getNickname()!=null){
					user_name.setText(info.getNickname());
				}else{
					user_name.setText(info.getUserName());
				}
			}
		}else{
			JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX+BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
				@Override
				public void gotResult(int i, String s) {
					if(i==0){
						info = JMessageClient.getMyInfo();
						if(info.getAvatar()!=null){
							File file = info.getAvatarFile();
							ImageLoadUtil.loadImage(user_header_img,file.getAbsolutePath(),ImageLoadUtil.optionsCircle);
						}else{
							user_header_img.setImageResource(R.drawable.touxiang_big_icon);
						}
						if(BaseApplication.getInstances().getKeyValue("nickname")!=null){
							user_name.setText(BaseApplication.getInstances().getKeyValue("nickname").replace("'",""));
						}else{
							if(info.getNickname()!=null){
								user_name.setText(info.getNickname());
							}else{
								user_name.setText(info.getUserName());
							}
						}
					}else{
						user_header_img.setImageResource(R.drawable.touxiang_big_icon);
						user_name.setText(BaseApplication.getInstances().getUser_id());
					}
				}
			});
		}
	}

	public void loadNameAndImage(){
		HashMap<String, String> params = new HashMap<>();
		Task task = new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				JSONObject json;
				// TODO Auto-generated method stub
				if (val != null && !val.equals("") && val.toLowerCase().startsWith("{")) {
					//上传文件成功
					try {
						json = new JSONObject(val);
						if (json.has("nickname") && json.get("nickname")!=null) {
							BaseApplication.getInstances().setKeyValue("nickname",json.get("nickname").toString());
							user_name.setText(json.get("nickname").toString());
						}else{
							if(BaseApplication.getInstances().getKeyValue("nickname")!=null){
								user_name.setText(BaseApplication.getInstances().getKeyValue("nickname"));
							}else{
								user_name.setText(BaseApplication.getInstances().getUser_id());
							}
						}
						//
						if (json.has("picture") && json.get("picture")!=null && json.get("picture").toString().toLowerCase().startsWith("http")) {
							ImageLoadUtil.loadImage(user_header_img,json.get("picture").toString(),ImageLoadUtil.optionsCircle);
						}else{
							user_header_img.setImageResource(R.drawable.touxiang_big_icon);
						}
						return;
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else if(val==null || "".equals(val)){
					loadInfo();
				} else {
					loadInfo();
				}
			}
		}, HttpUrls.getNameAndHeadIcon(), params, Task.TYPE_GET_STRING_NOCACHE, null);
		MyHandler.putTask(SettingEntryActivity.this, task);
	}

//	public void reloadNameAndImage(){
//		HashMap<String, String> params = new HashMap<>();
//		Task task = new Task(new HttpResponseInterface() {
//			@Override
//			public void handMsg(String val) {
//				JSONObject json;
//				// TODO Auto-generated method stub
//				if (val != null && !val.equals("") && val.toLowerCase().startsWith("{")) {
//					//上传文件成功
//					try {
//						json = new JSONObject(val);
//						if (json.has("nickname") && json.get("nickname")!=null) {
//							BaseApplication.getInstances().setKeyValue("nickname",json.get("nickname").toString());
//							user_name.setText(json.get("nickname").toString());
//						}else{
//							if(BaseApplication.getInstances().getKeyValue("nickname")!=null){
//								user_name.setText(BaseApplication.getInstances().getKeyValue("nickname"));
//							}else{
//								user_name.setText(BaseApplication.getInstances().getUser_id());
//							}
//						}
//						//
//						if (json.has("picture") && json.get("picture")!=null && json.get("picture").toString().toLowerCase().startsWith("http")) {
//							ImageLoadUtil.loadImage(user_header_img,json.get("picture").toString(),ImageLoadUtil.optionsCircle);
//						}else{
//							user_header_img.setImageResource(R.drawable.touxiang_big_icon);
//						}
//						return;
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				} else if(val==null || "".equals(val)){
//					reloadInfo();
//				} else {
//					reloadInfo();
//				}
//			}
//		}, HttpUrls.getNameAndHeadIcon(), params, Task.TYPE_GET_STRING_NOCACHE, null);
//		MyHandler.putTask(SettingEntryActivity.this, task);
//	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);

//		loadNameAndImage();
		getLevelInfo();
		getMyInfo();
	}

	private void getMyInfo() {
		if (!NetworkUtils.isNetworkAvalible(SettingEntryActivity.this)) {
			QuanjiakanUtil.showToast(SettingEntryActivity.this, "网络连接不可用!");
			return;
		}
		//Toast.makeText(VideoLivePlayActivity.this, "应该到了", Toast.LENGTH_SHORT).show();
		MyHandler.putTask(new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
                /*Toast.makeText(VideoLivePlayActivity.this, val, Toast.LENGTH_SHORT).show();
                LogUtil.e("info----------" + val);
                if (val != null && val.length() > 0) {
                    LevelInfo levelInfo = JSON.parseObject(val, LevelInfo.class);
                    Toast.makeText(VideoLivePlayActivity.this, levelInfo.toString(), Toast.LENGTH_SHORT).show();
                    if (levelInfo!= null&&"200".equals(levelInfo.getCode())){
                        LevelInfo.ObjectBean objectBean = levelInfo.getObject();
                        *//**
				 * currentGradeExperience : 2
				 * ebeans : 999955757
				 * ebeansConvertWallet : 8.9996016E7
				 * experience : 2
				 * money : 0
				 * nextGradeExperience : 998
				 * recharge : 0
				 * totalGive : 44252
				 * totalRecipient : 10
				 *//*
                        if (objectBean != null) {
                            ebeans = objectBean.getEbeans();//e豆
                           *//* Toast.makeText(VideoLivePlayActivity.this, ""+ebeans, Toast.LENGTH_SHORT).show();
                            Log.e("ceshi",ebeans+"");*//*
                        }
                    }
                }*/
				try {
					JSONObject jsonObject = new JSONObject(val);
					if("200".equals(jsonObject.getString("code"))) {
						JSONObject object = jsonObject.getJSONObject("object");
						ebeans=object.getInt("ebeans");
						tv_ebean_arrow.setText(ebeans+"豆");


					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, HttpUrls.getMyLevelInfo(), null, Task.TYPE_GET_STRING_NOCACHE, null));

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
	}

	/**
	 * 药品订单
	 */
	protected void toMedcineOrderList(){
//		Intent intent = new Intent(this,MedcineOrderListActivity.class);
//		startActivity(intent);
		BaseApplication.getInstances().toast(SettingEntryActivity.this,"由于政策原因，该功能暂不开放!");
	}

	/**
	 * 咨询列表
	 */
	protected void toConsultantList(){
		Intent intent = new Intent(this,HealthDynamicsActivity.class);
		startActivity(intent);
	}

	/**
	 * 家政服务订单
	 */
	protected void toHousekeeperOrderList(){
		Intent intent = new Intent(this,HouseKeeperOrderHistoryListActivity.class);
		startActivity(intent);
	}

	/**
	 * 关于我们
	 */
	protected void toAboutUs(){
		Intent intent = new Intent(this,AboutUsActivity.class);
		startActivity(intent);
	}

	/**
	 * 绑定设备
	 */
	protected void toBindDevice(){

	}

	/**
	 * 亲情号码
	 */
	protected void toQinqingHaoma(){
		Intent intent = new Intent(this,FamilyPhoneNumberActivity.class);
		startActivity(intent);
	}

	/**
	 * 情景模式
	 */
	protected void toQingjingMoshi(){
		Intent intent = new Intent(this,SceneModuleDeviceListActivity.class);
		startActivity(intent);
	}

	protected void toSetting(){
		Intent intent = new Intent(this, SettingActivity.class);
		startActivityForResult(intent,REQUEST_INFO);
	}

	/**
	 * 保险
	 */
	protected void toInSure(){
		Intent intent = new Intent(this, InsuranceListActivity.class);
		startActivity(intent);
	}

	/**
	 *
	 */
	protected void toInquiry(){
		Intent intent = new Intent(this, OutInquiryListActivity.class);
		startActivity(intent);
	}

	/**
	 *
	 */
	protected void toPhoneDocter(){
		Intent intent = new Intent(this, PhoneDoctorOrderListActivity.class);
		startActivity(intent);
	}

	public void toMyWallet(){
		Intent intent = new Intent(this, MyMoneyActivity.class);
		startActivity(intent);
	}

	public void toVolunteer(){
		Intent intent = new Intent(this, VolunteerMessageActivity.class);
		startActivity(intent);
	}

	public void toHealthCase(){
		//健康档案---老人健康档案
		Intent intent = new Intent(this, CaseHistoryPicActivity.class);
		startActivity(intent);
	}

	/**
	 * 我的关注
	 */
	public void toModify(){
		Intent intent = new Intent(this, ConcernListActivity.class);
		startActivity(intent);
	}

	public void toEBean(){
		Intent intent = new Intent(this, EBeanChargeActivity.class);
		startActivity(intent);
	}

	public void toGiftRecord(){
		Intent intent = new Intent(this, GiftRecordActivity.class);
		startActivity(intent);
	}

	public void toWatchSetting(){
		Intent intent = new Intent(this, ChildWatchSettingEntryActivity.class);
		startActivity(intent);

	}
	public void toWatchChild(){

		Intent intent = new Intent(this, WatchChildEntryActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
			case R.id.tv_device_child:
				toWatchChild();
				break;
			case R.id.tv_device_setting:
				toWatchSetting();
				break;
			case R.id.rl_level:
				toGiftRecord();
				break;
			case R.id.rl_bean:
				/**
				 * TODO E豆充值页面
				 */
				toEBean();
				break;
			case R.id.modify:
				toModify();
				break;
			case R.id.menu_text:
				toMyWallet();
				break;
			case R.id.tv_aboutus:
				//关于我们
				toAboutUs();
				break;
			case R.id.tv_device:
				//绑定设备
				toBindDevice();
				break;
			case  R.id.tv_setting:
				//更多设置
				toSetting();
				break;
			case R.id.tv_qingjingmoshi:
				//情景模式
				toQingjingMoshi();
				break;
			case R.id.tv_qinqinghaoma:
				//亲情号码设置
				toQinqingHaoma();
				break;
			//---------------------------------------------------

			case  R.id.ll_consult:
				//咨询
				//toConsultantList();
//				Toast.makeText(SettingEntryActivity_backup.this, "咨询", Toast.LENGTH_SHORT).show();
				break;
			case R.id.ll_inquiry:
				//问诊
				toInquiry();
				break;
			case R.id.ll_phonedocter:
				//电话医生
				toPhoneDocter();
				break;
			case R.id.ll_housekeeper:
				//家政预约订单
				toHousekeeperOrderList();
				break;
			case R.id.ll_insure:
				toInSure();
				break;
			case R.id.tv_volunteer:
				toVolunteer();
				break;
			case R.id.tv_missing://寻人启事--->健康档案
				toHealthCase();
				break;
		}
	}


	private final int REQUEST_INFO = 1024;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case REQUEST_INFO:
				if(resultCode==RESULT_OK){
					loadNameAndImage();
				}
				break;
		}
	}
}
