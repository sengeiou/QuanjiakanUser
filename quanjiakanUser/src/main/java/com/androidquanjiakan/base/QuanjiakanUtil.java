
package com.androidquanjiakan.base;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.http.util.EncodingUtils;










import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

public class QuanjiakanUtil {

	private static Context mContext;

	public static void setCurrentContext(Context context) {
		mContext = context;
	}

	public static Context getCurrentoContext() {
		return mContext;
	}

	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	public static String getCurrVersion(Context context) {
		String appVersion = null;
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			appVersion = info.versionName; // 版本�?
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return appVersion;
	}

	/**
	 * �?��是否存在SDCard
	 * 
	 * @return
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static int dip2px(Context context, float dipValue) {
		if (dipValue == 0) {
			return 0;
		} else {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dipValue * scale + 0.5f);
		}
	}

	public static int px2dip(Context context, float pxValue) {
		if (pxValue == 0) {
			return 0;
		} else {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (pxValue / scale + 0.5f);
		}
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param pxValue
	 * @param pxValue
	 *            （DisplayMetrics类中属�?scaledDensity�?
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue
	 * @param spValue
	 *            （DisplayMetrics类中属�?scaledDensity�?
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static void showToast(Context con, String str) {
		Toast.makeText(con.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}

	public static void showLongToast(Context con, String str) {
		Toast.makeText(con.getApplicationContext(), str, Toast.LENGTH_LONG).show();
	}

	public static void showToast(Context con, int strResId) {
		Toast.makeText(con.getApplicationContext(), strResId, Toast.LENGTH_SHORT).show();
	}

	public static void showLongToast(Context con, int strResId) {
		Toast.makeText(con.getApplicationContext(), strResId, Toast.LENGTH_LONG).show();
	}

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^1\\d{10}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static String getCmdString(String type, long sendUid, long recUid) {
		String str = "{\"cmd\":\"" + type + "\",\"send_uid\":" + sendUid + ",\"rec_uid\":" + recUid + "}";
		str = str + "\r\n";
		return str;
	}

	public static void saveSharedPreference(String key, String value, Context context) {
		SharedPreferences.Editor editor = context.getSharedPreferences("talkface", Activity.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getSharedPreference(String key, Context context) {
		return context.getSharedPreferences("talkface", Activity.MODE_PRIVATE).getString(key, "");
	}

	public static String gmt2Timespan(String gmt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
		try {
			return getDateTimeFromLong(sdf.parse(gmt).getTime() + "");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String getDateTimeyyyyMMddHHmmss(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(time));
	}
	
	public static String getDateTimePattern(long time,String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date(time));
	}

	public static String getDateTimeFromLong(String time_long) {
		// InfoPrinter.printLog("getDatetime:"+getDateTime(timespan));
		long time = Long.parseLong(time_long);
		long cha = System.currentTimeMillis() - time;
		if (cha <= 60 * 1000) {
			return "�ո�";
		} else if (cha > 60 * 1000 && cha <= 60 * 60 * 1000) {
			return cha / (60 * 1000) + "����ǰ";
		} else if (cha > 60 * 60 * 1000 && cha <= 60 * 60 * 24 * 1000) {
			return cha / (60 * 60 * 1000) + "Сʱǰ";
		} else if (cha > 60 * 60 * 24 * 1000 && cha <= 60 * 60 * 24 * 30 * 1000) {
			return cha / 60 * 60 * 24 * 1000 + "��ǰ";
		} else {
			SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
			Date d = new Date(Long.parseLong(time_long));
			return formater.format(d);
		}
	}

	public static int birthday2Age(String birthday) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = format.parse(birthday);
			Date now = new Date();
			return now.getYear() - date.getYear();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static void wakeUpAndUnlock(Context context) {
		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
		// 解锁
		kl.disableKeyguard();
		// 获取电源管理器对�?
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		// 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个�?�?��的是LogCat里用的Tag
		PowerManager.WakeLock wl = pm
				.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
		// 点亮屏幕
		wl.acquire();
		// 释放
		wl.release();
	}


	public static String getDayFromGMT(String gmt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
		try {
			Date date = sdf.parse(gmt);
			return date.getDay() + "";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}


	public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {

		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int temp = 256 * divsum;
		int dv[] = new int[temp];
		for (i = 0; i < temp; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return (bitmap);
	}

	public static Bitmap cutBitmap2FitScreen(Bitmap bm) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;// 获取屏幕的宽和高

		int widthDrawable = bm.getWidth();
		int heightDrawable = bm.getHeight();// 获取背景图片的宽和高

		float scaleWidth = (float) width / widthDrawable;
		float scaleHeight = (float) height / heightDrawable;// 宽高�?

		Bitmap resizeBmp;
		Matrix matrix = new Matrix();
		if (scaleWidth < scaleHeight) {
			float scale = scaleHeight;// 取大�?
			matrix.postScale(scale, scale);// 缩放比例
			int xStart = (int) (widthDrawable - widthDrawable / scale) / 2;

			resizeBmp = Bitmap.createBitmap(bm, xStart, 0, (int) (widthDrawable / scale), heightDrawable, matrix,
					false);
		} else {
			float scale = scaleWidth;
			matrix.postScale(scale, scale);
			int yStart = (int) (scaleHeight - scaleHeight / scale) / 2;
			resizeBmp = Bitmap.createBitmap(bm, 0, yStart, widthDrawable, (int) (heightDrawable / scale), matrix,
					false);
		}

		return resizeBmp;
	}
	
	
	public static String getRawTextAsString(String filename,Context context){
		String result = "";
		String ENCODING="utf-8";
		try {  
            InputStream in =context.getResources().getAssets().open(filename);  
            //获取文件的字节数  
            int lenght = in.available();  
            //创建byte数组  
            byte[]  buffer = new byte[lenght];  
            //将文件中的数据读到byte数组�? 
            in.read(buffer);  
            result = EncodingUtils.getString(buffer, ENCODING);  
            return result;
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return "";  
	}
	

	public static String stringFilter(String str) throws PatternSyntaxException {
		// 只允许字母�?数字
		String regEx = "[^a-zA-Z0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	/**
	 * 获取设备ID
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context){
		String androidId = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);  
		return androidId;
	}
	
	
	/**
	 * 获取设备名称
	 * @param context
	 * @return
	 */
	public static String getDeviceName(Context context){
		String androidId = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);  
		return Build.DEVICE;
	}

	/**
	 * JsonArray convert to ArrayList;
	 * @param array
	 * @return
	 */
	public static List<JsonObject> array2list(JsonArray array){
		List<JsonObject> list = new ArrayList<>();
		if(array == null){
			return list;
		}else {
			for (int i = 0; i < array.size(); i++) {
				list.add(array.get(i).getAsJsonObject());
			}
			return list;
		}
	}
	
	/**
	 * @param mlist
	 * @return
	 */
	public static JsonArray list2array(List<JsonObject> mlist){
		JsonArray array = new JsonArray();
		for (int i = 0; i < mlist.size(); i++) {
			array.add(mlist.get(i));
		}
		return array;
	}
	
   /**
	 * @param date1
	 * @param date2
	 * @return
	 * @throws ParseException
	 */
	@SuppressLint("SimpleDateFormat")
	public static int getMonthSpace(String date1, String date2){
        int result = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
			c1.setTime(sdf.parse(date1));
			c2.setTime(sdf.parse(date2));
			result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
			return result == 0 ? 1 : Math.abs(result);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1;
		}
    }

}
