package com.honeywell.honeywellproject.WidgeView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by QHT on 2018-03-06.
 */
public class GuestLinearLayout extends LinearLayout{
    public GuestLinearLayout(Context context) {
        super(context);
    }

    public GuestLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GuestLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GuestLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //重写这个方法，并且在方法里面请求所有的父控件都不要拦截他的事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}
