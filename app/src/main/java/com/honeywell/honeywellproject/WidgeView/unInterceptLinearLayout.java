package com.honeywell.honeywellproject.WidgeView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by QHT on 2018-03-06.
 */
public class unInterceptLinearLayout extends LinearLayout{
    public unInterceptLinearLayout(Context context) {
        super(context);
    }

    public unInterceptLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public unInterceptLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public unInterceptLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isIntercept){
            return true;
        }
        return false;
    }
    boolean isIntercept=true;
    public void setIntercept(boolean isIntercept){
        this.isIntercept=isIntercept;
    }
}
