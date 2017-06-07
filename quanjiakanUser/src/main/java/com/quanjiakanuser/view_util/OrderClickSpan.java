package com.quanjiakanuser.view_util;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Administrator on 2016/6/14 0014.
 */
public class OrderClickSpan extends ClickableSpan{
    private String text;
    private int color;
    private View.OnClickListener listener;

    /**
     *
     * @param text
     * @param color use getResource().getColor(R.color.?);Or Color.?
     * @param listener
     */
    public OrderClickSpan(String text, int color, View.OnClickListener listener){
        super();
        this.text = text;
        this.color = color;
        this.listener = listener;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
//        super.updateDrawState(ds);
        ds.setColor(color);
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view);
    }
}
