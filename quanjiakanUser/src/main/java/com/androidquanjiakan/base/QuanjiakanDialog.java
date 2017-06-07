package com.androidquanjiakan.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.SigninActivity_mvp;
import com.androidquanjiakan.interfaces.IDialogCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.quanjiakan.main.R;
import com.quanjiakanuser.util.AnimationDialog;
import com.quanjiakanuser.util.InfoPrinter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class QuanjiakanDialog {
	
	private static QuanjiakanDialog instance;
	private View dialogView;

	public static QuanjiakanDialog getInstance() {
		if (instance == null) {
			instance = new QuanjiakanDialog();
		}
		return instance;
	}

	private QuanjiakanDialog() {

	}

	private void initDialog(Context context) {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = new Dialog(context, R.style.dialog_loading);
	}

	private Dialog mDialog;
	/*
	* 获取默认的对话框*/
	public Dialog getDialog(Context context) {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = new Dialog(context, R.style.dialog_loading);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
		this.dialogView = view;
		LayoutParams lp = mDialog.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(context, 80);
		lp.height = QuanjiakanUtil.dip2px(context, 80);
		lp.gravity = Gravity.CENTER;
		mDialog.setContentView(view, lp);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
		return mDialog;
	}

	public Dialog getLogoutDialog(final Context context) {
		final Dialog dialog = new Dialog(context, R.style.dialog_loading);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_common_confirm, null);
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(context.getResources().getString(R.string.jmui_user_logout_dialog_title));
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(context.getResources().getString(R.string.jmui_user_logout_dialog_message));

		TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
		btn_cancel.setVisibility(View.INVISIBLE);

		TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
		btn_confirm.setVisibility(View.VISIBLE);
		btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
                Intent intent = new Intent(context, SigninActivity_mvp.class);
				context.startActivity(intent);
                QuanjiakanSetting.getInstance().logout();
				((Activity)context).finish();
			}
		});

		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(context, 300);
		lp.height = lp.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		dialog.setContentView(view, lp);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		return dialog;
	}

	public Dialog getCardDialog(Context context) {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = new Dialog(context,R.style.dialog_loading);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
		this.dialogView = view;
		LayoutParams lp = mDialog.getWindow().getAttributes();
		lp.width = context.getResources().getDisplayMetrics().widthPixels;
		lp.height = lp.MATCH_PARENT;
		lp.gravity = Gravity.BOTTOM;
		mDialog.setContentView(view, lp);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
		return mDialog;
	}

	/*
	* 获取默认的对话框*/
	public Dialog getUnShowDialog(Context context) {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = new Dialog(context, R.style.dialog_loading);
		return mDialog;
	}

	public Dialog getCommonConfirmDialog(Context context,String title,String content,final OnClickListener confirm) {
//		initDialog(context);
		final Dialog mDialogTemp = new Dialog(context, R.style.dialog_loading);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_common_confirm, null);
		this.dialogView = view;
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(title);

		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(content);

		TextView btn_cancel =  (TextView) view.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mDialogTemp.dismiss();
			}
		});

		TextView btn_confirm =  (TextView) view.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialogTemp.dismiss();
				if(confirm!=null){
					confirm.onClick(v);
				}
			}
		});


		LayoutParams lp = mDialogTemp.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(context, 80);
		lp.height = QuanjiakanUtil.dip2px(context, 80);
		lp.gravity = Gravity.CENTER;

		mDialogTemp.setContentView(view, lp);
		mDialogTemp.setCanceledOnTouchOutside(false);
		mDialogTemp.show();
		return mDialogTemp;
	}

	public Dialog getCommonConfirmDialog(Context context,String title,String content,
										 String cancelText,String confirmText,final OnClickListener confirm) {
//		initDialog(context);
		final Dialog mDialogTemp = new Dialog(context, R.style.dialog_loading);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_common_confirm, null);
		this.dialogView = view;
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(title);

		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(content);

		TextView btn_cancel =  (TextView) view.findViewById(R.id.btn_cancel);
		btn_cancel.setText(cancelText);
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mDialogTemp.dismiss();
			}
		});

		TextView btn_confirm =  (TextView) view.findViewById(R.id.btn_confirm);
		btn_confirm.setText(confirmText);
		btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialogTemp.dismiss();
				if(confirm!=null){
					confirm.onClick(v);
				}
			}
		});


		LayoutParams lp = mDialogTemp.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(context, 80);
		lp.height = QuanjiakanUtil.dip2px(context, 80);
		lp.gravity = Gravity.CENTER;

		mDialogTemp.setContentView(view, lp);
		mDialogTemp.setCanceledOnTouchOutside(false);
		mDialogTemp.show();
		return mDialogTemp;
	}

	public Dialog getCommonConfirmDialog(Context context,String title,String content,
										 String cancelText,boolean isShowCancel,String confirmText,boolean isShowConfirm,final OnClickListener confirm) {
		final Dialog mDialogTemp = new Dialog(context, R.style.dialog_loading);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_common_confirm, null);
		this.dialogView = view;
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(title);

		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(content);

		TextView btn_cancel =  (TextView) view.findViewById(R.id.btn_cancel);
		if(isShowCancel){
			btn_cancel.setVisibility(View.VISIBLE);
		}else{
			btn_cancel.setVisibility(View.GONE);
		}
		btn_cancel.setText(cancelText);
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mDialogTemp.dismiss();
			}
		});

		TextView btn_confirm =  (TextView) view.findViewById(R.id.btn_confirm);
		if(isShowConfirm){
			btn_confirm.setVisibility(View.VISIBLE);
		}else{
			btn_confirm.setVisibility(View.GONE);
		}
		btn_confirm.setText(confirmText);
		btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialogTemp.dismiss();
				if(confirm!=null){
					confirm.onClick(v);
				}
			}
		});


		LayoutParams lp = mDialogTemp.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(context, 80);
		lp.height = QuanjiakanUtil.dip2px(context, 80);
		lp.gravity = Gravity.CENTER;

		mDialogTemp.setContentView(view, lp);
		mDialogTemp.setCanceledOnTouchOutside(false);
		mDialogTemp.show();
		return mDialogTemp;
	}

	public Dialog getCommonConfirmDialog(Context context,String title,String content,
										 String cancelText,boolean isShowCancel,final OnClickListener cancel,
										 String confirmText,boolean isShowConfirm,final OnClickListener confirm) {
//		initDialog(context);
		final Dialog mDialogTemp = new Dialog(context, R.style.dialog_loading);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_common_confirm, null);
		this.dialogView = view;
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(title);

		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(content);

		TextView btn_cancel =  (TextView) view.findViewById(R.id.btn_cancel);
		if(isShowCancel){
			btn_cancel.setVisibility(View.VISIBLE);
		}else{
			btn_cancel.setVisibility(View.GONE);
		}
		btn_cancel.setText(cancelText);
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mDialogTemp.dismiss();
				if(cancel!=null){
					cancel.onClick(view);
				}
			}
		});

		TextView btn_confirm =  (TextView) view.findViewById(R.id.btn_confirm);
		if(isShowConfirm){
			btn_confirm.setVisibility(View.VISIBLE);
		}else{
			btn_confirm.setVisibility(View.GONE);
		}
		btn_confirm.setText(confirmText);
		btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialogTemp.dismiss();
				if(confirm!=null){
					confirm.onClick(v);
				}
			}
		});


		LayoutParams lp = mDialogTemp.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(context, 80);
		lp.height = QuanjiakanUtil.dip2px(context, 80);
		lp.gravity = Gravity.CENTER;

		mDialogTemp.setContentView(view, lp);
		mDialogTemp.setCanceledOnTouchOutside(false);
		mDialogTemp.show();
		return mDialogTemp;
	}

	public Dialog getCommonModifyDialog(Context context,String title,String hint,final IDialogCallback confirm) {
//		initDialog(context);
		final Dialog mDialogTemp = new Dialog(context, R.style.dialog_loading);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_common_modify, null);
		this.dialogView = view;

		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(title);

		final EditText tv_content = (EditText) view.findViewById(R.id.tv_content);
		tv_content.setHint(hint);

		TextView btn_cancel =  (TextView) view.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mDialogTemp.dismiss();
			}
		});

		TextView btn_confirm =  (TextView) view.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialogTemp.dismiss();
				confirm.getString(tv_content.getText().toString());
			}
		});

		LayoutParams lp = mDialogTemp.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(context, 80);
		lp.height = QuanjiakanUtil.dip2px(context, 80);
		lp.gravity = Gravity.CENTER;

		mDialogTemp.setContentView(view, lp);
		mDialogTemp.setCanceledOnTouchOutside(false);
		mDialogTemp.show();
		return mDialogTemp;
	}

	public void resizeDialog(Activity context, Dialog dialog,View contentView){
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(context, 300);
		lp.height = lp.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;

	}

	/*
	* 获取对话框+标题
	* */
	public Dialog getDialog(Context context, String message) {
		initDialog(context);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
		this.dialogView = view;
		TextView tv = (TextView) view.findViewById(R.id.tv);
		tv.setText(message);
		tv.setVisibility(View.VISIBLE);
		LayoutParams lp = mDialog.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(context, 80);
		lp.height = QuanjiakanUtil.dip2px(context, 80);
		lp.gravity = Gravity.CENTER;
		mDialog.setContentView(view, lp);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
		return mDialog;
	}

	/*
	* 获取对话框列表
	* */
	public Dialog getListDialogDefineHeight(Context context, List<String> strings, String title,
			final MyDialogCallback callback, final Object object) {
		initDialog(context);

		View view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null);
		ListView listView = (ListView) view.findViewById(R.id.listview);
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(title);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_simple_textview, strings);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				callback.onItemClick(arg2, object);
				mDialog.dismiss();
			}
		});
		LayoutParams lp = mDialog.getWindow().getAttributes();
		lp.width = context.getResources().getDisplayMetrics().widthPixels * 3 / 4;
		lp.height = context.getResources().getDisplayMetrics().widthPixels;
		lp.gravity = Gravity.CENTER;
		mDialog.setContentView(view);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.getWindow().setLayout(lp.width, lp.height);
		mDialog.show();
		return mDialog;
	}	

	/*
	* 获取录音对话框
	* */
	public Dialog getVoiceRecordingDialog(Context context, String title,
			final MyDialogCallback callback, final Object object) {
//		initDialog(context);
		final Dialog mDialogTemp = new Dialog(context, R.style.dialog_loading);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_recording, null);
		TextView tv_stop = (TextView)view.findViewById(R.id.tv_stop);
		LayoutParams lp = mDialogTemp.getWindow().getAttributes();
		lp.width = context.getResources().getDisplayMetrics().widthPixels * 3 / 4;
		lp.height = context.getResources().getDisplayMetrics().widthPixels;
		lp.gravity = Gravity.CENTER;
		tv_stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				callback.onItemClick(0, null);
				mDialogTemp.dismiss();
			}
		});
		mDialogTemp.setContentView(view);
		mDialogTemp.setCanceledOnTouchOutside(false);
		mDialogTemp.getWindow().setLayout(lp.width, lp.height);
		mDialogTemp.show();
		return mDialogTemp;
	}

	// 弹出全屏图片的dialog
	public static void initImageDialog(final String[] photos, Context context) {
		final int currentItem =0;
		final DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_image).showImageOnFail(R.drawable.ic_image)
				.cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.ARGB_8888).build();

		final ArrayList<String> highDpiList = new ArrayList<>();
		final AnimationDialog imageDialog = new AnimationDialog(context, R.style.dialogFullScreen);
		imageDialog.setContentView(R.layout.image_full_screen_dialog);
		imageDialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		TextView delete = (TextView) imageDialog.findViewById(R.id.imageDialog_delete);
		delete.setVisibility(View.GONE);
		final TextView cancel = (TextView) imageDialog.findViewById(R.id.imageDialog_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imageDialog.dismiss();
			}
		});

		for (int i = 0; i < photos.length; i++) {
			String url = photos[i];
			String[] arrays = url.split("/");
			url = "";
			for (int j = 0; j < arrays.length; j++) {
				if (j != 4 && j != 5) {
					url = url + arrays[j] + "/";
				} else if (j == 5) {
					url = url + arrays[j];
				}
				InfoPrinter.printLog(url);
			}
			highDpiList.add(url);
		}
		final List<ImageView> ivs = new ArrayList<>();

		for (int i = 0; i < highDpiList.size(); i++) {
			PhotoView iv = new PhotoView(context);
			iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			iv.setImageResource(R.drawable.ic_image);
			ivs.add(iv);
		}

		ViewPager vp = (ViewPager) imageDialog.findViewById(R.id.imageDialog_viewPager);

		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public int getCount() {

				if (photos != null && photos.length > 0) {
					return photos.length;
				} else {
					return 0;
				}
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView((View) object);
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(ivs.get(position));
				return ivs.get(position);
			}
		};

		vp.setAdapter(mPagerAdapter);
		vp.setCurrentItem(currentItem);
		vp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		/*if (currentItem > 0) {
			ImageLoader.getInstance().displayImage(highDpiList.get(currentItem - 1), ivs.get(currentItem - 1), options);
		}

		ImageLoader.getInstance().displayImage(highDpiList.get(currentItem), ivs.get(currentItem), options);

		if (currentItem < photos.length - 1) {
			ImageLoader.getInstance().displayImage(highDpiList.get(currentItem + 1), ivs.get(currentItem + 1), options);

		}*/
		ivs.get(currentItem).setImageURI(Uri.fromFile(new File(photos[0])));

//		cancel.setText(currentItem + 1 + "/" + photos.length);
		cancel.setText("");
		// ImageLoader.getInstance().displayImage(highDpiList.get(currentItem),
		// ivs.get(currentItem), options);

		vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// currentItem = arg0;
				cancel.setText(arg0 + 1 + "/" + photos.length);
				ivs.get(currentItem).setImageURI(Uri.fromFile(new File(photos[0])));
				// ImageLoader.getInstance().displayImage(highDpiList.get(arg0),
				// ivs.get(arg0), options);
				if (arg0 > 0) {
//					ImageLoader.getInstance().displayImage(highDpiList.get(arg0 - 1), ivs.get(arg0 - 1), options);
				}
//				ImageLoader.getInstance().displayImage(highDpiList.get(arg0), ivs.get(arg0), options);
				if (arg0 < photos.length - 1) {
//					ImageLoader.getInstance().displayImage(highDpiList.get(arg0 + 1), ivs.get(arg0 + 1), options);

				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		imageDialog.showDialog(0, 0, R.style.dialogWindowAnim);
	}

	public View getDialogView() {
		return this.dialogView;
	}

	public interface MyDialogCallback {
		public void onItemClick(int position, Object object);
	}

}
