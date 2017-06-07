package com.androidquanjiakan.activity.setting.other;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.SigninActivity_mvp;
import com.androidquanjiakan.activity.main.MainActivity;
import com.androidquanjiakan.activity.setting.Setting_PasswordModifyActivity;
import com.androidquanjiakan.activity.setting.WithDraw_PasswordActivity;
import com.androidquanjiakan.activity.setting.delivery.DeliveryAddressActivity_Entry;
import com.androidquanjiakan.activity.setting.modify_info.ModifyInfoActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.VersionInfoEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.interfaces.IDownloadCallback;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.MultiThreadAsyncTask;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;

import org.json.JSONObject;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.IntegerCallback;
import cn.jpush.im.api.BasicCallback;

public class SettingActivity extends BaseActivity implements OnClickListener{

	protected TextView tv_xiugaimima,tv_banben ,tv_fankui,tv_dizhi,tv_title,tv_quit;
	protected ImageButton ibtn_back;

	private RelativeLayout rl_update;

	private TextView tv_banben_value;

	private View tv_fenge4;
	private TextView tv_tixianguangli;

	private TextView tv_perfect;
	private View tv_div;

	private CheckBox ck_disturb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting);
		initTitleBar();
		initView();
	}

	public void initTitleBar(){
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);

		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("更多设置");
	}
	
	/**
	 * 初始化界面
	 */
	protected void initView(){
		tv_banben_value = (TextView) findViewById(R.id.tv_banben_value);
		tv_banben_value.setOnClickListener(this);
		tv_xiugaimima = (TextView)findViewById(R.id.tv_mimaxiugai);
		tv_xiugaimima.setOnClickListener(this);

		tv_tixianguangli = (TextView)findViewById(R.id.tv_tixianguangli);
		tv_tixianguangli.setOnClickListener(this);

		ck_disturb = (CheckBox) findViewById(R.id.ck_disturb);
//		if(BaseApplication.getInstances().getKeyValue(BaseApplication.getInstances().getUser_id()+"disturb")!=null &&
//				!"".equals(BaseApplication.getInstances().getKeyValue(BaseApplication.getInstances().getUser_id()+"disturb"))){
//			ck_disturb.setChecked(true);
//		}else{
//			ck_disturb.setChecked(false);
//		}
		JMessageClient.getNoDisturbGlobal(new IntegerCallback() {
			@Override
			public void gotResult(int i, String s, Integer integer) {
				LogUtil.e("int value:"+i+"\nString message:"+s+"\nInteger value:"+integer.intValue());
				if(i==0){
					if(integer.intValue()==1){
						ck_disturb.setChecked(true);
					}else{
						ck_disturb.setChecked(false);
					}
				}else{
					ck_disturb.setChecked(false);
				}
			}
		});
		if(BaseApplication.getInstances().getKeyValue(BaseApplication.getInstances().getUser_id()+"disturb")!=null &&
				!"".equals(BaseApplication.getInstances().getKeyValue(BaseApplication.getInstances().getUser_id()+"disturb"))){
//					JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_NOTIFICATION);//不展示通知
//					JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_SOUND);//展示通知，无声音有震动
//					JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_VIBRATE);//展示通知，有声音无震动
			JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_SILENCE);//展示通知，无声音无震动
		}else{
			JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);//展示通知，有声音有震动
		}
		ck_disturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					BaseApplication.getInstances().setKeyValue(BaseApplication.getInstances().getUser_id()+"disturb","disturb");
//					JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_NOTIFICATION);//不展示通知
//					JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_SOUND);//展示通知，无声音有震动
//					JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_VIBRATE);//展示通知，有声音无震动
					JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_SILENCE);//展示通知，无声音无震动

					JMessageClient.setNoDisturbGlobal(1, new BasicCallback() {
						@Override
						public void gotResult(int i, String s) {
							LogUtil.e("setNoDisturbGlobal*******************\n int value:"+i+"\nString message:"+s);
						}
					});
				}else{
					BaseApplication.getInstances().setKeyValue(BaseApplication.getInstances().getUser_id()+"disturb","");
					JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);//展示通知，有声音有震动

					JMessageClient.setNoDisturbGlobal(0, new BasicCallback() {
						@Override
						public void gotResult(int i, String s) {
							LogUtil.e("setNoDisturbGlobal*******************\n int value:"+i+"\nString message:"+s);
						}
					});
				}
			}
		});
		tv_dizhi = (TextView)findViewById(R.id.tv_dizhi);
		tv_dizhi.setVisibility(View.GONE);
		tv_dizhi.setOnClickListener(this);
		tv_fenge4 = findViewById(R.id.tv_fenge4);
		tv_fenge4.setVisibility(View.GONE);

		rl_update = (RelativeLayout) findViewById(R.id.rl_update);
		rl_update.setOnClickListener(this);
		tv_banben = (TextView)findViewById(R.id.tv_banben);
		tv_banben.setOnClickListener(this);
		tv_fankui = (TextView)findViewById(R.id.tv_fankui);
		tv_fankui.setOnClickListener(this);

		tv_quit = (TextView)findViewById(R.id.tv_quit);
		tv_quit.setOnClickListener(this);

		tv_banben_value.setText(QuanjiakanUtil.getCurrVersion(getApplicationContext()));


		//完善资料
		tv_perfect = (TextView) findViewById(R.id.tv_perfect);
		tv_div = findViewById(R.id.tv_div);
		tv_perfect.setOnClickListener(this);
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

	protected void fankui(){
		Intent intent = new Intent(this, FeedbackActivity.class);
		startActivity(intent);
	}
	
	protected void banben(){
		
	}
	
	protected void mimaxiugai(){
		Intent intent = new Intent(this, Setting_PasswordModifyActivity.class);
		startActivity(intent);
	}
	
	protected void dizhi(){
		Intent intent = new Intent(this, DeliveryAddressActivity_Entry.class);
		startActivity(intent);
	}
	
	protected void quit(){
		Intent intent = new Intent(this,SigninActivity_mvp.class);
		startActivity(intent);
		QuanjiakanSetting.getInstance().logout();
		if(isUpdata){
			setResult(RESULT_OK);
		}
		finish();
	}

	private void tixianguangli() {
		Intent intent = new Intent(this, WithDraw_PasswordActivity.class);
		startActivity(intent);
	}


	MultiThreadAsyncTask updateTask;
	IDownloadCallback callback = new IDownloadCallback() {
		@Override
		public void updateProgress(int progress, String rate) {
			progressBar.setProgress(progress);
			SettingActivity.this.rate.setText(rate);
		}
	};
	private VersionInfoEntity versionInfoEntity;
	PackageManager packageManager = null;
	PackageInfo packageInfo = null;
	protected void update(){
		packageManager = getPackageManager();
		try {
			packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		MyHandler.putTask(SettingActivity.this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				//
				if(val!=null && val.length()>0 && !"null".equals(val)){
					versionInfoEntity = (VersionInfoEntity) SerialUtil.jsonToObject(val,new TypeToken<VersionInfoEntity>(){}.getType());
					if(versionInfoEntity!=null){
						if(Integer.parseInt(versionInfoEntity.getCode())>packageInfo.versionCode){
							/**
							 * 弹出更新对话框
							 */
							if(versionInfoEntity.getForced_update()!=null && "1".equals(versionInfoEntity.getForced_update())){
								//强制更新
								showUpdateDialog(true);
							}else{
								//非强制更新
								showUpdateDialog(false);
							}
						}else{
							BaseApplication.getInstances().toast(SettingActivity.this,"已是最新版!");
							BaseApplication.getInstances().updateCheckTime();
						}
					}else{
						BaseApplication.getInstances().toast(SettingActivity.this,"获取版本信息失败!");
					}
				}else{
//					BaseApplication.getInstances().toast("获取版本信息失败!");
				}
			}
		}, HttpUrls.getVersion(),null,Task.TYPE_GET_STRING_NOCACHE,null));
	}

	private Dialog updateDialog;
	private TextView title;
	private TextView content;
	private ProgressBar progressBar;
	private TextView rate;
	private TextView cancel;
	private TextView confirm;

	public void showUpdateDialog(final boolean isForce){
		updateDialog = new Dialog(this, R.style.ShareDialog);
		updateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (updateDialog != null && updateDialog.isShowing()) {
					return true;
				} else {
					return false;
				}
			}
		});
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_new, null);
		view.setBackgroundResource(R.drawable.selecter_hollow_white);
		title = (TextView) view.findViewById(R.id.tv_title);
		title.setText("版本更新");

		content = (TextView) view.findViewById(R.id.tv_content);
		content.setVisibility(View.VISIBLE);
		if (isForce) {
			SpannableString spannableString = new SpannableString("当前版本:"+packageInfo.versionName+"\n\n更新版本:"+versionInfoEntity.getVersion()+ "\n\t("+"该版本为重要更新版本,若不更新将无法使用"+")");
			spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")),0,
					("当前版本:"+packageInfo.versionName+"\n\n更新版本:"+versionInfoEntity.getVersion()+ "\n\t(").length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(Color.RED),
					("当前版本:"+packageInfo.versionName+"\n\n更新版本:"+versionInfoEntity.getVersion()+ "\n\t(").length(),
					("当前版本:"+packageInfo.versionName+"\n\n更新版本:"+versionInfoEntity.getVersion()+ "\n\t("+"该版本为重要更新版本,若不更新将无法使用").length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")),
					("当前版本:"+packageInfo.versionName+"\n\n更新版本:"+versionInfoEntity.getVersion()+ "\n\t("+"该版本为重要更新版本,若不更新将无法使用").length(),
					("当前版本:"+packageInfo.versionName+"\n\n更新版本:"+versionInfoEntity.getVersion()+ "\n\t("+"该版本为重要更新版本,若不更新将无法使用"+")").length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//			content.setText("当前版本:"+packageInfo.versionName+"\n\n更新版本:"+versionInfoEntity.getVersion()+ "\n\t(该版本为重要更新版本,若不更新将无法使用)");
			content.setText(spannableString);
		} else {
			content.setText("当前版本:" + packageInfo.versionName + "\n\n更新版本:" + versionInfoEntity.getVersion());
		}

		progressBar = (ProgressBar) view.findViewById(R.id.progress);
		progressBar.setVisibility(View.GONE);
		progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 15, 0));
		progressBar.setMax(100);

		rate = (TextView) view.findViewById(R.id.rate);
		rate.setVisibility(View.GONE);

		cancel = (TextView) view.findViewById(R.id.btn_cancel);
		cancel.setText(getString(R.string.cancel));
		cancel.setVisibility(View.VISIBLE);

		confirm = (TextView) view.findViewById(R.id.btn_confirm);
		confirm.setText("确定");
		confirm.setVisibility(View.VISIBLE);

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (updateTask != null) {
					updateTask.stopSubThread();
					updateTask = null;
				}

				if (isForce) {
					updateDialog.dismiss();
					System.exit(0);
				} else {
					updateDialog.dismiss();
					BaseApplication.getInstances().updateCheckTime();
				}
			}
		});
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//开始下载
//				updateDialog.dismiss();
				/**
				 * 切换显示
				 */
//				content.setVisibility(View.GONE);
//				progressBar.setVisibility(View.VISIBLE);

				confirm.setVisibility(View.GONE);
				cancel.setVisibility(View.GONE);

				if (updateTask != null) {
					updateTask.stopSubThread();
					updateTask = null;
				}
				updateTask = new MultiThreadAsyncTask(SettingActivity.this,
						versionInfoEntity.getUrl(),
//						"http://quanjiakan.com/app/quanjiakanUser-release.apk",
						callback, updateDialog);

//                content.setVisibility(View.GONE);
				content.setText("正在更新版本:"+versionInfoEntity.getVersion()+"\n");
				progressBar.setVisibility(View.VISIBLE);
				rate.setVisibility(View.VISIBLE);
				updateTask.execute("");
			}
		});
		//
		WindowManager.LayoutParams lp = updateDialog.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(BaseApplication.getInstances(), 300);
		lp.height = lp.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;

		updateDialog.setContentView(view, lp);
		updateDialog.setCanceledOnTouchOutside(false);

		updateDialog.show();
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();		
		if(id == R.id.tv_mimaxiugai){
			mimaxiugai();
		}else if (id == R.id.tv_banben) {
			banben();
		}else if (id == R.id.tv_dizhi) {
			dizhi();
		}else if (id == R.id.tv_fankui) {
			fankui();
		}else if (id == R.id.ibtn_back) {
			if(isUpdata){
				setResult(RESULT_OK);
			}
			finish();
		}else if (id == R.id.tv_quit) {
			quit();
		}else if(id == R.id.rl_update){
			update();
		}else if(id==R.id.tv_tixianguangli) {
		    tixianguangli();
		}else if(id==R.id.tv_perfect){
			toPerfectInfo();
		}else if(id==R.id.tv_banben_value){
//			testSos();
		}
	}

	public void testSos(){
		try{
			JSONObject jsonObject = new JSONObject();
			JSONObject jsonObject1 = new JSONObject();
			JSONObject jsonObject2 = new JSONObject();
			jsonObject2.put("Type","LAB");
			jsonObject2.put("Radius","550");
			jsonObject2.put("Location","113.2451085,23.1325716");

			jsonObject1.put("IMEI","355637052238805");
			jsonObject1.put("Category","SOSReport");
			jsonObject1.put("SOSReport",jsonObject2);

			jsonObject.put("Results",jsonObject1);

			NattyProtocolFilter.ntyCommonBroadcastResult(1,jsonObject.toString());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void toTest(){
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, "355637052238805");
		intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, 23.1325716);
		intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, 113.2451085);
		startActivity(intent);
	}

	/**
	 * TODO 修改个人信息资料
	 */
	public void toPerfectInfo(){
		Intent intent = new Intent(SettingActivity.this, ModifyInfoActivity.class);
		startActivityForResult(intent,REQUEST_UPDATA);
	}

	private boolean isUpdata = false;
	private final int REQUEST_UPDATA = 1024;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case REQUEST_UPDATA:
				if(resultCode==RESULT_OK){
					isUpdata = true;
				}else{
					isUpdata = false;
				}
				break;
		}
	}
}
