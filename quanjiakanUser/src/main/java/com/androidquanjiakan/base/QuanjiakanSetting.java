package com.androidquanjiakan.base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import com.androidquanjiakan.activity.main.MainActivity;
import com.androidquanjiakan.service.DevicesService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.quanjiakanuser.util.GsonParseUtil;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;

public class QuanjiakanSetting {
	
	private final String TAG = "LOGUTIL "+QuanjiakanSetting.class.getSimpleName();
	private static QuanjiakanSetting instance = null;
	private static boolean bInited = false;
	private Context context = null;
	private SharedPreferences sharedPreferences;
	private final String PREFERENCES_FILE = "quanjiakang_setting";
	private ArrayList<String> list;
	private JsonObject userinfos = new JsonObject();
	private static final String key_userinfos = "key_userinfos";
	
	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
	}

	private boolean mbShortcutCreated = false;	
	private int userId;
	private String phone;
	private String userName;
	private String appid;
	private JsonObject json_accountinfo;
	private String userSig;	
	private final String USER_ID_KEY = "user_id";
	private final String PHONE_KEY = "phone";
	private final String USER_NAME = "user_name";
	private final String UPDATE_TIME = "update_time";
	private final String DEVICE = "device";
	private final String SHORTCUT_CREATED = "shortcut_created";
	private final String SDK_SERVER_STATUS = "sdk_server_status";

	public static final String KEY_SIGNAL = "key_signal";
	public static final String KEY_SIGNAL_P = "key_signal_p";

	public static void init(Context context) {
		if (context == null) {
			throw new NullPointerException();
		}
		if (!bInited) {
			instance = new QuanjiakanSetting(context);
			bInited = true;
		}
	}
	
	public static boolean isInitialized(){
		return (instance != null);
	}
	
	private QuanjiakanSetting(Context con) {
		context = con;
		sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE);
		sharedPreferences
				.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
					public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
							String key) {
						Log.v(TAG, "onSharedPreferenceChanged==>" + key);
					}
				});
		read(con);
	}
	
	private void read(Context context) {
		userId = sharedPreferences.getInt(USER_ID_KEY, 0);
		phone = sharedPreferences.getString(PHONE_KEY, null);
		userName = sharedPreferences.getString(USER_NAME, null);
		appid = sharedPreferences.getString("appid", "");
		userSig = sharedPreferences.getString("usersig", "");
		this.json_accountinfo = new JsonParser().parse(sharedPreferences.getString("accountinfo", "{}")).getAsJsonObject();
	}
	
	public int getUserId() {
		return context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).getInt(USER_ID_KEY,0);
	}

	public void setUserId(int userId) {
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).edit();
		editor.putInt(USER_ID_KEY, userId);
		editor.commit();
	}

	/**
	 *************************************************************
     */
	public int getSDKServerStatus() {
		return context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).getInt(SDK_SERVER_STATUS,-1);
	}

	public void setSDKServerStatus(int userId) {
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).edit();
		editor.putInt(SDK_SERVER_STATUS, userId);
		editor.commit();
	}
	/**
	 ************************************************************
	 */



	public String getDevice() {
		return context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).getString(DEVICE,"");
	}

	public void setDevice(String userId) {
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).edit();
		editor.putString(DEVICE, userId);
		editor.commit();
	}
	
	public String getUserName() {
		return context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).getString(USER_NAME,"");
	}

	public void setUserName(String userName) {
		this.userName = userName;
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).edit();
		editor.putString(USER_NAME, userName);
		editor.commit();
	}

	public String getUpdateTime() {
		return context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).getString(UPDATE_TIME,"");
	}

	public void setUpdateTime(String userName) {
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).edit();
		editor.putString(UPDATE_TIME, userName);
		editor.commit();
	}

	public String getPhone() {
		return context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).getString(PHONE_KEY,"");
	}

	public void setPhone(String phone) {
		this.phone = phone;
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).edit();
		editor.putString(PHONE_KEY, phone);
		editor.commit();
	}
	
	public boolean isShortcutCreated() {
		return context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).getBoolean(SHORTCUT_CREATED,false);
	}
	
	public String getSourceCode() {
		return "1";
	}
	
	public static QuanjiakanSetting getInstance() {
		return instance;
	}

	public void setShortcutCreated(boolean isShortcutCreated) {
		mbShortcutCreated = isShortcutCreated;
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).edit();
		editor.putBoolean(SHORTCUT_CREATED, mbShortcutCreated);
		editor.commit();
	}

	
	/** 清除本地，个人信�?*/
	public void logout() {
		Log.i(TAG, "logout and clear all info...");
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).edit();
		editor.putInt(USER_ID_KEY, 0);
		editor.putString(PHONE_KEY, null);
		editor.putString(USER_NAME, null);
		editor.putString("appid", null);
		editor.commit();
		userId = 0;
		phone = null;
		userName = null;
		/**
		 * 停止Push通知
		 */
		JPushInterface.stopPush(BaseApplication.getInstances());
		BaseApplication.getInstances().showdownNatty();

		if(BaseApplication.getInstances().getMainActivity() != null && !BaseApplication.getInstances().getMainActivity().isFinishing()){
			BaseApplication.getInstances().getMainActivity().stopService(new Intent(BaseApplication.getInstances().getMainActivity(), DevicesService.class));
			BaseApplication.getInstances().getMainActivity().finish();
			BaseApplication.getInstances().setMainActivity(null);
		}

		if(BaseApplication.getInstances().getCurrentActivity() != null && !BaseApplication.getInstances().getCurrentActivity().isFinishing()){
			BaseApplication.getInstances().getCurrentActivity().finish();
			BaseApplication.getInstances().setCurrentActivity(null);
		}
		setUserId(0);
		JMessageClient.logout();
	}
	
	public void setAppId(String appid){
		this.appid = appid;
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).edit();
		editor.putString("appid", appid);
		editor.commit();
	}
	
	public String getAppId(){
		return this.appid;
	}
	
	public void setAccountInfo(JsonObject object){
		this.json_accountinfo = object;
		setSharedPreference("accountinfo", object.toString());
	}
	
	public JsonObject getAccountInfo(){
		return this.json_accountinfo;
	}
	
	public void setUserSig(String usersig){
		setSharedPreference("usersig", usersig);
	}
	
	public String getUserSig(){
		return getValue("usersig");
	}
	
	private void setSharedPreference(String key,String value){
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();
	}	
	
	public void setValue(String key,String value){
		setSharedPreference(key, value);
	}
	
	public String getValue(String key){
		return sharedPreferences.getString(key, "");
	}
	
	public JsonObject getUserInfos(){
		String string = getValue(key_userinfos);
		userinfos = new GsonParseUtil(string).getJsonObject();
		return this.userinfos;
	}	
	
	public void setUserInfo(String user_id,JsonObject userinfo){
		this.userinfos.add(user_id, userinfo);
		setValue(key_userinfos, this.userinfos+"");
	}

	/**
	 * 获取咨询问题列表
	 * @return
	 */
	public List<JsonObject> getProblemList(){
		JsonArray array = new GsonParseUtil(getValue("problem_list"+getUserId())).getJsonArray();
		return QuanjiakanUtil.array2list(array);
	}

	/**
	 * 保存问题到本地文件中
	 * @param problem
	 */
	public void addProblem(JsonObject problem){
		List<JsonObject> list = getProblemList();
		if(list.size() == 0){
			list.add(problem);
		}else{
			for (int i=0;i<list.size();i++){
				if(!list.get(i).get("target").getAsString().equals(problem.get("target").getAsString())){
					list.add(problem);
				}else{
					list.set(i,problem);
				}
			}
		}
		setValue("problem_list"+getUserId(),QuanjiakanUtil.list2array(list)+"");
	}


	public String getKeyValue(String key) {
		return context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).getString(key,"");
	}

	public void setKeyValue(String key,String value) {
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public int getKeyNumberValue(String key) {
		return context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).getInt(key,0);
	}

	public void setKeyNumberValue(String key,int value) {
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE).edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
}
