package com.androidquanjiakan.activity;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.AMap.OnMarkerDragListener;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.androidquanjiakan.base.BaseActivity;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MapActivity extends BaseActivity implements  OnMarkerClickListener,
OnInfoWindowClickListener, OnMarkerDragListener, OnMapLoadedListener,
OnClickListener, InfoWindowAdapter {
	
	private MapView mapView;
	private AMap aMap;
    private SoundPool mSoundPool;
    private TextView tv_call;
    private LatLng point = null;
    private Vibrator vibrator;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_map);
		final Window win = getWindow();
		 win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		 | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		 win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		 | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		Intent intent = getIntent();
		
        if (intent.hasExtra("x") && intent.hasExtra("y")) {
            // 当用intent参数时，设置中心点为指定点
            Bundle b = intent.getExtras();
            point = new LatLng(b.getDouble("y"), b.getDouble("x"));
        } 
        mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		tv_call = (TextView)findViewById(R.id.tv_call);
		tv_call.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:120"));  
                startActivity(intent); 
                if(mSoundPool != null){
        			mSoundPool.autoPause();
        			mSoundPool.release();
        			stopViber();
        		}
			}
		});
		init();
		drawMarkers(point);
        initSound();
        startViber();
	}
	
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
		aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
		aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		aMap.setInfoWindowAdapter(this);		
	}
	
	public void drawMarkers(LatLng pLng) {
		
		Marker marker = aMap.addMarker(new MarkerOptions()
				.position(pLng)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.draggable(true));
		
		marker.showInfoWindow();// 设置默认显示一个infowinfow
		
	}
	
	protected void initSound(){
		mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
		mSoundPool.load(this, R.raw.alrm, 1);
		mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

			@Override
			public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				mSoundPool.play(1, 1, 1, 0, -1, 1);
			}
		});
	}
	
	/**
	 * 启动震动
	 */
	protected void startViber(){
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);  
        long [] pattern = {400,400,400,400};   // 停止 开启 停止 开启   
        vibrator.vibrate(pattern,1); 
	}
	
	/**
	 * 取消震动
	 */
	protected void stopViber(){
		if(vibrator != null){
			vibrator.cancel(); 
		}
	}		
	
	protected void setLocation(LatLng point){
		//定义Maker坐标点  
		Marker marker = aMap.addMarker(new MarkerOptions()
        .position(new LatLng(31.238068, 121.501654))
        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
		.decodeResource(getResources(),R.drawable.ic_location)))
		.draggable(true));
		marker.showInfoWindow();	
	}


	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 25));
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
//		if (radioOption.getCheckedRadioButtonId() != R.id.custom_info_window) {
//			return null;
//		}
//		View infoWindow = getLayoutInflater().inflate(
//				R.layout.custom_info_window, null);
//		render(marker, infoWindow);
//		return infoWindow;
		return null;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());

		if(mSoundPool != null){
			mSoundPool.autoPause();
			mSoundPool.release();
		}
	}

		
}
