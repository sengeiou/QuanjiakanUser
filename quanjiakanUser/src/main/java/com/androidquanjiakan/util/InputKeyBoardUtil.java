package com.androidquanjiakan.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Administrator on 2017/4/13 0013.
 */

public class InputKeyBoardUtil {
    private static InputMethodManager imm;
    public static void showKeyBoard(Activity context , View view){
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (imm == null) {
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void hideSoftInput(Activity context,View view) {
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (imm == null) {
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
