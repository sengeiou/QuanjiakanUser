package com.androidquanjiakan.entity_util;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class UnitUtil {
    public static final int dp_To_px(Context context,int dpValue){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue,context.getResources().getDisplayMetrics());
    }

    public static final int sp_To_px(Context context,int dpValue){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,dpValue,context.getResources().getDisplayMetrics());
    }

    public static final void setViewMarginEachSide(View view, int left, int top , int right, int bottom){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(left,top,right,bottom);
    }

    public static final void setViewMargin(View view, int value,int width,int height){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        if(params==null){
            params = new LinearLayout.LayoutParams(width,height);
            params.setMargins(value,value,value,value);
        }else{
            params.setMargins(value,value,value,value);
        }
        view.setLayoutParams(params);
    }

    public static final void setViewMargin(View view, int valuelr,int valuetb,int width,int height){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        if(params==null){
            params = new LinearLayout.LayoutParams(width,height);
            params.setMargins(valuelr,valuetb,valuelr,valuetb);
        }else{
            params.setMargins(valuelr,valuetb,valuelr,valuetb);
        }
        view.setLayoutParams(params);
    }

    public static final void setViewWidthHeight_LinearLayoutParams(View view,int width,int height){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        if(params==null){
            params = new LinearLayout.LayoutParams(width,height);
        }else{
            params.width = width;
            params.height = height;
        }
        view.setLayoutParams(params);
    }

    public static final void setViewWidthHeight_RelativeLayoutParams(View view,int width,int height){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if(params==null){
            params = new RelativeLayout.LayoutParams(width,height);
        }else{
            params.width = width;
            params.height = height;
        }
        view.setLayoutParams(params);
    }
}
