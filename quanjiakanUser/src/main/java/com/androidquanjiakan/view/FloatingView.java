package com.androidquanjiakan.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.quanjiakan.main.R;
import com.androidquanjiakan.util.LogUtil;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/7/7 0007.
 */
public class FloatingView extends View implements View.OnTouchListener {

    private static final float CLICK_BOUND = 130;
    private Bitmap bitmap;
    private float screenWidth;
    private float screenHeight;
    private float floatingX;
    private float floatingY;
    private float initMarginRight;
    private float initMarginBottom;
    private float halfWidth;
    private float halfHeight;
    private float initX;
    private float initY;
    private float initTitleBarHeight;
    private Paint imagePaint;
    private int navibar;
    private Shader shader;
    private OnClickListener onClickListener;

    public FloatingView(Context context) {
        super(context);
        getScreenWithdHeight(context);
    }

    public FloatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getScreenWithdHeight(context);
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void getScreenWithdHeight(Context context) {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        navibar = getNavigationBarHeight(context);
        initMarginRight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        initMarginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, navibar + 20, getResources().getDisplayMetrics());
        initTitleBarHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

        floatingX = -1;
        floatingY = -1;

        imagePaint = new Paint();
        imagePaint.setColor(getResources().getColor(R.color.colorPureBlack));
        imagePaint.setStyle(Paint.Style.FILL);
        imagePaint.setAntiAlias(true);
        imagePaint.setDither(false);

        if (bitmap != null) {
            bitmap.recycle();
        }

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.personal_doctor);

        halfWidth = bitmap.getWidth() / 2.0f;
        halfHeight = bitmap.getHeight() / 2.0f;
        initX = screenWidth - initMarginRight - bitmap.getWidth();
        if (navibar > 0) {
            initY = screenHeight - initMarginBottom;
        } else {
            initY = screenHeight - 2 * initMarginBottom - bitmap.getWidth();
        }
        setOnTouchListener(this);
    }

    public void recycle() {
        if (bitmap != null) {
            bitmap.recycle();
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (floatingX == -1 && floatingY == -1) {
            canvas.drawBitmap(bitmap, initX, initY, imagePaint);
        } else {
            canvas.drawBitmap(bitmap, floatingX, floatingY, imagePaint);
            initX = floatingX;
            initY = floatingY;
        }

    }

    float tempX;
    float tempY;

    float lastX;
    float lastY;
    boolean isOnTouch;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = motionEvent.getX();
                lastY = motionEvent.getY();
                if ((lastX > initX && lastX < (initX + bitmap.getWidth())) &&
                        (lastY > initY && (lastY < initY + bitmap.getHeight()))) {
                    isOnTouch = true;
                } else {
                    isOnTouch = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isOnTouch) {
                    tempX = motionEvent.getX() - halfWidth;
                    tempY = motionEvent.getY() - halfHeight;

                    if (tempX < initMarginRight) {
                        tempX = initMarginRight;
                    } else if (tempX > screenWidth - initMarginRight - bitmap.getWidth()) {
                        tempX = screenWidth - initMarginRight - bitmap.getWidth();
                    }
                    if (navibar > 0) {
                        if (tempY > screenHeight - initMarginBottom) {
                            tempY = screenHeight - initMarginBottom;
                        } else if (tempY < initTitleBarHeight + initMarginRight) {
                            tempY = initTitleBarHeight + initMarginRight;
                        }
                    } else {
                        if (tempY > screenHeight - 2 * initMarginBottom - bitmap.getWidth()) {
                            tempY = screenHeight - 2 * initMarginBottom - bitmap.getWidth();
                        } else if (tempY < initTitleBarHeight + initMarginRight) {
                            tempY = initTitleBarHeight + initMarginRight;
                        }
                    }

                    floatingX = tempX;
                    floatingY = tempY;

                    invalidate();
                } else {

                }
                break;
            case MotionEvent.ACTION_UP:
                tempX = motionEvent.getX() - halfWidth;
                tempY = motionEvent.getY() - halfHeight;
                if (isOnTouch) {

                    if (Math.abs(lastX - tempX) < CLICK_BOUND && Math.abs(lastY - tempY) < CLICK_BOUND) {
                        if (onClickListener != null) {
                            onClickListener.onClick(view);
                        }
                    }
                    isOnTouch = false;
                    return true;
                } else {
                    return false;
                }
        }
        return isOnTouch;
    }

    private boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            LogUtil.w(e.getMessage());
        }
        return hasNavigationBar;
    }

    private int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar(context)) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }
}
