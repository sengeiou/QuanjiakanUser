package com.androidquanjiakan.datahandler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.interfaces.IImageCropInterface;
import com.androidquanjiakan.view.CircleTransformation;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Administrator on 2016/6/17 0017.
 */
public class ImageCropHandler {

    private static File mTempDir;

    public static void pickImage(Activity context){
        Crop.pickImage(context);
    }

    public static void getImageFromCamera(Activity activity, IImageCropInterface imageCropInterface) {
        // create Intent to take a picture and return control to the calling
        // application
        if(mTempDir==null){
            mTempDir = new File(Environment.getExternalStorageDirectory(),activity.getPackageName()+".crop");
            if(!mTempDir.exists()){
                mTempDir.mkdirs();
            }
        }else{
            if(!mTempDir.exists()){
                mTempDir.mkdirs();
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = "temp_camera" + String.valueOf( System.currentTimeMillis())+".png";
        File cropFile = new File( mTempDir, fileName);
        Uri fileUri = Uri.fromFile( cropFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name
//        mCurrentPhotoPath = fileUri.getPath();
        imageCropInterface.getFilePath(fileUri.getEncodedPath());
        // start the image capture Intent
        activity.startActivityForResult(intent, CommonRequestCode.REQUEST_CODE_CAPTURE_CAMEIA);
    }

    public static Bitmap getCircleBitmap(Uri uri) {
        Bitmap src =  BitmapFactory.decodeFile( uri.getPath());
        Bitmap output = Bitmap.createBitmap( src.getWidth(), src.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas( output);

        Paint paint = new Paint();
        Rect rect = new Rect( 0, 0, src.getWidth(), src.getHeight());

        paint.setAntiAlias( true);
        paint.setFilterBitmap( true);
        paint.setDither( true);
        canvas.drawARGB( 0, 0, 0, 0);
        canvas.drawCircle( src.getWidth() / 2, src.getWidth() / 2, src.getWidth() / 2, paint);
        paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap( src, rect, rect, paint);
        return output;
    }

    public static void handleCrop(ImageView mImageView , int resultCode, Intent result, IImageCropInterface imageCropInterface) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            if(uri!=null){
                if(mImageView!=null){
                    mImageView.setImageURI(uri);
                }
                imageCropInterface.getFilePath(Uri.decode(uri.getEncodedPath()));
            }
//            mCircleView.setImageBitmap( getCircleBitmap(Crop.getOutput(result)));
        } else if (resultCode == Crop.RESULT_ERROR) {
//            Toast.makeText(getActivity(), Crop.getError(result).getMessage(),
//                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void handleCycleCrop(ImageView mImageView , int resultCode, Intent result, IImageCropInterface imageCropInterface) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            if(uri!=null){
                if(mImageView!=null){
                    Picasso.with(BaseApplication.getInstances()).load(uri)
                            .transform(new CircleTransformation())
                            .into(mImageView);
                }
                imageCropInterface.getFilePath(Uri.decode(uri.getEncodedPath()));
            }
//            mCircleView.setImageBitmap( getCircleBitmap(Crop.getOutput(result)));
        } else if (resultCode == Crop.RESULT_ERROR) {
//            Toast.makeText(getActivity(), Crop.getError(result).getMessage(),
//                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void beginCrop(Activity activity, Uri source) {
        if(mTempDir==null){
            mTempDir = new File(Environment.getExternalStorageDirectory(),activity.getPackageName()+".crop");
            if(!mTempDir.exists()){
                mTempDir.mkdirs();
            }
        }else{
            if(!mTempDir.exists()){
                mTempDir.mkdirs();
            }
        }
        String fileName = "temp_" + String.valueOf( System.currentTimeMillis())+".png";
        File cropFile = new File( mTempDir, fileName);
        Uri outputUri = Uri.fromFile( cropFile);
        new Crop( source).output( outputUri).setCropType(false).start(activity);
    }

    public void beginCropCircle(Activity activity, Uri source) {
        if(mTempDir==null){
            mTempDir = new File(Environment.getExternalStorageDirectory(),activity.getPackageName()+".crop");
            if(!mTempDir.exists()){
                mTempDir.mkdirs();
            }
        }else{
            if(!mTempDir.exists()){
                mTempDir.mkdirs();
            }
        }
        String fileName = "temp_" + String.valueOf( System.currentTimeMillis())+".png";
        File cropFile = new File( mTempDir, fileName);
        Uri outputUri = Uri.fromFile( cropFile);
        new Crop( source).output( outputUri).setCropType(true).start(activity);
    }
}

