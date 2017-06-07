package com.quanjiakanuser.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore;

import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.util.SignatureUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.quanjiakan.main.R;

public class ImageUtils {

	private static DisplayImageOptions options;

	static{
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_patient)          // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.touxiang_empty)  // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_error)       // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中
				.displayer(new RoundedBitmapDisplayer(20))  // 设置成圆角图片
				.build();                                   // 创建配置过得DisplayImageOption对象
	}

	public static String saveBitmapToStorage(String filename,Bitmap bitmap){
		File director = new File(BaseConstants.imageDir);
		if(!director.exists()){
			director.mkdirs();
		}
		String path = BaseConstants.imageDir+filename;
		FileOutputStream fOut = null;  
		if(bitmap == null)
			return "";
		
        try {  
        	File f = new File(path);  
        	f.createNewFile();
        	fOut = new FileOutputStream(f);  
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);  
            fOut.flush(); 
            return path;    
        } catch (FileNotFoundException e) {  
                e.printStackTrace();  
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "";
	}
	
	
	public static Bitmap decodeUriAsBitmap(Uri uri,Context context){
		 Bitmap bitmap = null;
		 try {
		  bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
		 } catch (FileNotFoundException e) {
		  e.printStackTrace();
		  return null;
		 }
		 return bitmap;
	}
	
	public static int[] getImageWidthHeight(String path){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(path,options);
		int[] args = new int[2];
		args[0] = options.outWidth;
		args[1] = options.outHeight;
		return args;
	}
	
	public static int[] getHeightByWith(int dstWidth,int dstHeight){
		int[] args = new int[2];
		if(dstWidth >720 ){
			args[0] = 320;
			args[1] = 320*dstHeight/dstWidth;
			return args;
		}
		
		if(dstHeight > 1280){
			args[0] = 480*dstWidth/dstHeight;
			args[1] = 480;
			return args;
		}
		
		args[0] = dstWidth;
		args[1] = dstHeight;
		return args;
	}
	
	public static String getImagePathByImageUrl(String url){
		return BaseConstants.imageDir+ SignatureUtil.getMD5String(url);
	}
	
	public static Bitmap getBitmapFromFile(File dst, int width, int height) {
	    if (null != dst && dst.exists()) {
	        BitmapFactory.Options opts = null;
	        if (width > 0 && height > 0) {
	            opts = new BitmapFactory.Options();         //����inJustDecodeBoundsΪtrue��decodeFile��������ռ䣬��ʱ����ԭʼͼƬ�ĳ��ȺͿ��
	            opts.inJustDecodeBounds = true;
	            BitmapFactory.decodeFile(dst.getPath(), opts);
	            // ����ͼƬ���ű���
	            final int minSideLength = Math.min(width, height);
	            opts.inSampleSize = computeSampleSize(opts, minSideLength,
	                    width * height);            //����һ��Ҫ�������û�false����Ϊ֮ǰ���ǽ������ó���true
	            opts.inJustDecodeBounds = false;
	            opts.inInputShareable = true;
	            opts.inPurgeable = true;
	        }
	        try {
	            return BitmapFactory.decodeFile(dst.getPath(), opts);
	        } catch (OutOfMemoryError e) {
	            e.printStackTrace();
	        }
	    }
	    return null;
	}
	
	public static int computeSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength,
	            maxNumOfPixels);	 
	    int roundedSize;
	    if (initialSize <= 8) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }	 
	    return roundedSize;
	    
	}
	 
	private static int computeInitialSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;
	 
	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
	            .sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
	            .floor(w / minSideLength), Math.floor(h / minSideLength));
	 
	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }
	 
	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	}
	
	
	public static String uri2Path(Uri uri,Context context)
    {
       int actual_image_column_index;
       String img_path;
       String[] proj = { MediaStore.Images.Media.DATA };
       Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
       actual_image_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
       cursor.moveToFirst();
       img_path = cursor.getString(actual_image_column_index);
       return img_path;
    }
	
	public String saveImageViewToSdCard(Bitmap bitmap){
		FileOutputStream outputStream = null;
		String filepath = BaseConstants.imageDir+System.currentTimeMillis()+".jpg";
		try {
			outputStream = new FileOutputStream(filepath);
			bitmap.compress(CompressFormat.JPEG, 100, outputStream);
			outputStream.flush();
			outputStream.close();
			return filepath;
		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options,  
            int reqWidth, int reqHeight) {  
          
        final int height = options.outHeight;  
        final int width = options.outWidth;  
        int inSampleSize = 1;  
  
        if (height > reqHeight || width > reqWidth) {  
  
            final int heightRatio = Math.round((float) height  
                    / (float) reqHeight);  
            final int widthRatio = Math.round((float) width / (float) reqWidth);  
  
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;  
        }  
  
        return inSampleSize;  
    }
	
	
	public String saveImageToDefineSize(int width,int height,File file){
		String scaledFilePath = BaseConstants.imageDir +"sacled"+SignatureUtil.getMD5String(file.getAbsolutePath())+".jpg";
		try {
			Options options1 = new Options();  
			options1.inJustDecodeBounds = true;    
			BitmapFactory.decodeFile(file.getAbsolutePath(), options1);    
			options1.inSampleSize = calculateInSampleSize(options1, width, height);  //
			options1.inJustDecodeBounds = false;    
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options1);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(scaledFilePath)));
			return scaledFilePath;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public static Bitmap getSmallBitmap(String filePath,int width,int height) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = ImageUtils.calculateInSampleSize(options, width, height);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}



	
}
