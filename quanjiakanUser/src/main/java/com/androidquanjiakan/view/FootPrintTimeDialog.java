package com.androidquanjiakan.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.watch_child.elder.EditRemindActivity;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.quanjiakan.main.R;
import com.quanjiakanuser.widget.AbstractWheelTextAdapter;
import com.quanjiakanuser.widget.OnWheelChangedListener;
import com.quanjiakanuser.widget.OnWheelScrollListener;
import com.quanjiakanuser.widget.WheelView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 日期选择对话框
 * 
 * @author ywl
 *
 */
public class FootPrintTimeDialog extends Dialog implements View.OnClickListener {

	private Context context;
	private TextView btn_sure;
	private TextView btn_cancel;
	private TextView tv_share_title;

	private View contentView;
	private int type;

	public FootPrintTimeDialog(Context context,int type) {
		super(context, R.style.ShareDialog);
		this.context = context;
		this.type = type;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(context, 350);
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		contentView = LayoutInflater.from(context).inflate(R.layout.dialog_foot_select_time, null);
		btn_sure = (TextView) contentView.findViewById(R.id.btn_sure);
		btn_cancel = (TextView) contentView.findViewById(R.id.btn_cancel);
		tv_share_title = (TextView) contentView.findViewById(R.id.tv_share_title);
		if (type==0) {
			tv_share_title.setText("起始时间");
		}else if (type==1) {
			tv_share_title.setText("结束时间");
		}else {
			tv_share_title.setText("足迹时间");
		}
		btn_sure.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		setContentView(contentView, lp);
		initWheelView(0,0,0);

	}
	@Override
	public void onClick(View v) {

		if (v == btn_sure) {
			if (onBirthListener != null) {
				String text;
				if (selectTime.equals("上午")) {
					if (Integer.parseInt(selectHour) > 9) {
						text = selectHour + ":" + selectMinute;
					} else {
						text = "0" + selectHour + ":" + selectMinute;
					}

				} else {
					text = (Integer.parseInt(selectHour) + 12) + ":" + selectMinute;
				}

				onBirthListener.onClick(text);
			}
		}
		dismiss();

	}
	private OnBirthListener onBirthListener;

	public void setBirthdayListener(OnBirthListener onBirthListener) {
		this.onBirthListener = onBirthListener;
	}

	public interface OnBirthListener {
		public void onClick(String text);
	}

	/*****************************
	 * 数据模块
	 ***************************/

	private void getTimeData() {
		arr_time.clear();
		arr_time.add("上午");
		arr_time.add("下午");
	}

	private void getHourData() {
		arr_hour.clear();
		for (int i = 0; i < 12; i++) {
			arr_hour.add(i + "");
		}

	}

	private void getMinuteData() {
		arr_minute.clear();
		for (int i = 0; i < 60; i++) {
			if (i < 10) {
				arr_minute.add("0" + i);
			} else {
				arr_minute.add(i + "");
			}

		}

	}

	private WheelView wvTime;
	private WheelView wvHour;
	private WheelView wvMinute;


	private int maxTextSize = 18;
	private int minTextSize = 14;
	private CalendarTextAdapter mTimeAdapter;

	//    private int curruntTime = 0;
//    private int curruntHour = 0;
//    private int curruntMinute = 0;
	private ArrayList<String> arr_time = new ArrayList<String>();
	private ArrayList<String> arr_hour = new ArrayList<String>();
	private ArrayList<String> arr_minute = new ArrayList<String>();
	private CalendarTextAdapter mHourAdapter;
	private CalendarTextAdapter mMinuteAdapter;
	private String selectTime = "上午";
	private String selectHour = "1";
	private String selectMinute = "00";

	private void initWheelView(int curruntTime, int curruntHour, int curruntMinute) {
		wvTime = (WheelView) contentView.findViewById(R.id.wv_am_pm);
		wvHour = (WheelView) contentView.findViewById(R.id.wv_hour);
		wvMinute = (WheelView) contentView.findViewById(R.id.wv_minute);
//        wvTime.setCyclic(true);
		wvHour.setCyclic(true);
		wvMinute.setCyclic(true);

		getTimeData();
		mTimeAdapter = new CalendarTextAdapter(context, arr_time, curruntTime, maxTextSize, minTextSize);
//        wvTime.setVisibleItems(2);
		wvTime.setViewAdapter(mTimeAdapter);
		wvTime.setCurrentItem(curruntTime);


		getHourData();
		mHourAdapter = new CalendarTextAdapter(context, arr_hour, curruntHour, maxTextSize, minTextSize);
		wvHour.setVisibleItems(5);
		wvHour.setViewAdapter(mHourAdapter);
		wvHour.setCurrentItem(curruntHour);

		getMinuteData();
		mMinuteAdapter = new CalendarTextAdapter(context, arr_minute, curruntMinute, maxTextSize, minTextSize);
		wvMinute.setVisibleItems(5);
		wvMinute.setViewAdapter(mMinuteAdapter);
		wvMinute.setCurrentItem(curruntMinute);


		wvTime.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String currentText = (String) mTimeAdapter.getItemText(wheel.getCurrentItem());
				selectTime = currentText;
				setTextviewSize(currentText, mTimeAdapter);

			}
		});

		wvTime.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				String currentText = (String) mTimeAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, mTimeAdapter);
			}
		});


		wvHour.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String currentText = (String) mHourAdapter.getItemText(wheel.getCurrentItem());
				selectHour = currentText;
				setTextviewSize(currentText, mHourAdapter);
//                curruntHour = Integer.parseInt(currentText);

			}
		});

		wvHour.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				String currentText = (String) mHourAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, mHourAdapter);
			}
		});


		wvMinute.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String currentText = (String) mMinuteAdapter.getItemText(wheel.getCurrentItem());
				selectMinute = currentText;
				setTextviewSize(currentText, mMinuteAdapter);
//                curruntMinute = Integer.parseInt(currentText);

			}
		});

		wvMinute.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				String currentText = (String) mMinuteAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, mMinuteAdapter);
			}
		});


	}


	/**
	 * 设置字体大小
	 *
	 * @param curriteItemText
	 * @param adapter
	 */
	public void setTextviewSize(String curriteItemText, CalendarTextAdapter adapter) {
		ArrayList<View> arrayList = adapter.getTestViews();
		int size = arrayList.size();
		String currentText;
		for (int i = 0; i < size; i++) {
			TextView textvew = (TextView) arrayList.get(i);
			currentText = textvew.getText().toString();
			if (curriteItemText.equals(currentText)) {
				textvew.setTextSize(maxTextSize);
			} else {
				textvew.setTextSize(minTextSize);
			}
		}
	}

	/**********************
	 * adapter
	 *********************/

	private class CalendarTextAdapter extends AbstractWheelTextAdapter {
		ArrayList<String> list;

		protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize,
									  int minsize) {
			super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, minsize);
			this.list = list;
			setItemTextResource(R.id.tempValue);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return list.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return list.get(index) + "";
		}
	}

}