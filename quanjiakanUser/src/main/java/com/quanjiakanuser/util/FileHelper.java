package com.quanjiakanuser.util;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by Ken on 2015/11/13.
 */
public class FileHelper {

    private static FileHelper mInstance = new FileHelper();

    public static FileHelper getInstance() {
        return mInstance;
    }

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String createAvatarPath(String userName) {
        String dir = BaseConstants.imageDir;
        File destDir = new File(dir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File file;
        if (userName != null) {
            file = new File(dir, userName + ".png");
        }else {
            file = new File(dir, new DateFormat().format("yyyy_MMdd_hhmmss",
                    Calendar.getInstance(Locale.CHINA)) + ".png");
        }
        return file.getAbsolutePath();
    }

    public static String getUserAvatarPath(String userName) {
        return BaseConstants.imageDir + userName + ".png";
    }


    public interface CopyFileCallback {
        public void copyCallback(Uri uri);
    }

    /**
     * 复制后裁剪文件
     * @param file 要复制的文件
     */
    public void copyAndCrop(final File file, final Activity context, final CopyFileCallback callback) {
        if (isSdCardExist()) {
            final Dialog dialog = QuanjiakanDialog.getInstance().getDialog(context);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        String path = createAvatarPath(JMessageClient.getMyInfo().getUserName());
                        final File tempFile = new File(path);
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        byte[] bt = new byte[1024];
                        int c;
                        while((c = fis.read(bt)) > 0) {
                            fos.write(bt,0,c);
                        }
                        //关闭输入、输出流
                        fis.close();
                        fos.close();

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.copyCallback(Uri.fromFile(tempFile));
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
            thread.start();
        }else {
            Toast.makeText(context, "未找到SD卡", Toast.LENGTH_SHORT).show();
        }
    }
}
