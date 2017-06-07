package com.quanjiakanuser.util;

import com.androidquanjiakan.util.LogUtil;

public class InfoPrinter {

	private static boolean isDebug = true;
	private static String TAG = "TAG";

	public static void printLog(String log) {
		if (isDebug) {
			if (log != null && log.length() > 0) {
				LogUtil.e(log);
			}
		}
	}
}
