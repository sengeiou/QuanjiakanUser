package com.androidquanjiakan.spans;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Administrator on 2016/12/16 0016.
 */

public class SeftClickBlueSpan extends ClickableSpan {
    private Context context;
    private String textColor = "#ff999999";
    private int bgColor;
    private boolean showUnderline;
    private View.OnClickListener clickListener;

    public SeftClickBlueSpan(){

    }

    public SeftClickBlueSpan(Context context, String textColor, int bgColor, boolean showUnderline, View.OnClickListener clickListener) {
        this.context = context;
        this.textColor = textColor;
        this.bgColor = bgColor;
        this.showUnderline = showUnderline;
        this.clickListener = clickListener;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        //
        if(context!=null){
            ds.setColor(Color.parseColor(textColor));
            ds.bgColor = context.getResources().getColor(bgColor);
            ds.setUnderlineText(showUnderline);
        }else{
            ds.setColor(Color.parseColor("#ff33b5e5"));
            ds.bgColor = 0x00ffffff;
            ds.setUnderlineText(false);
        }
//        ds.clearShadowLayer();
    }

    @Override
    public void onClick(View widget) {
        if(clickListener!=null){
            clickListener.onClick(widget);
        }
    }
}
