package com.quanjiakanuser.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

public class AnimationDialog extends Dialog {
	private Window window = null;
	private WindowManager wm;

	public AnimationDialog(Context context, int style) {
		super(context, style);
		wm = ((Activity) context).getWindowManager();

	}

	public void showDialog(int x, int y, int rId) {

		windowDeploy(x, y, rId);
		setCanceledOnTouchOutside(true); // 点击空白处，Dialog消失
		show();
	}

	public void windowDeploy(int x, int y, int rId) {
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		window = getWindow();
		window.setWindowAnimations(rId);
		// window.setBackgroundDrawableResource(R.color.vifrification);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.width = WindowManager.LayoutParams.MATCH_PARENT;
		wl.height = WindowManager.LayoutParams.MATCH_PARENT;
		wl.x = x;
		wl.y = y;
		window.setAttributes(wl);
	}
}
