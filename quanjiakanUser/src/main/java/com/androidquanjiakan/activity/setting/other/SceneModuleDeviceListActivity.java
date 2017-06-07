package com.androidquanjiakan.activity.setting.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquanjiakan.adapter.DeviceListAdapter_Scene;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.datahandler.BindDeviceHandler;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class SceneModuleDeviceListActivity extends BaseActivity implements OnClickListener {

	protected TextView tv_title;
	protected ImageButton ibtn_back;

	private ListView listview;
	private DeviceListAdapter_Scene adapter;
	private List<BindDeviceEntity> data;

	private final int REQUEST_SCENE = 1000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_qingjingmoshi_device_list);
		initView();

		/**
		 * TODO 设备列表----指定设备后再切换该指定设备的情景模式
		 */
	}

	/**
	 * 初始化界面
	 */
	protected void initView(){
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("情景模式 设备选择");

		listview = (ListView) findViewById(R.id.listview);
		data = BindDeviceHandler.getAllValue();
		adapter = new DeviceListAdapter_Scene(this,data);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Intent intent = new Intent(SceneModuleDeviceListActivity.this,SceneModuleActivity.class);
				intent.putExtra(BaseConstants.PARAMS_DEVICE_ID,data.get((int)l).getDeviceid());
				startActivityForResult(intent,REQUEST_SCENE);
			}
		});
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		if(id == R.id.ibtn_back){
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
		switch (requestCode){
			case REQUEST_SCENE:
				data = BindDeviceHandler.getAllValue();
				adapter.setData(data);
				adapter.notifyDataSetChanged();
				break;
		}
	}
}
