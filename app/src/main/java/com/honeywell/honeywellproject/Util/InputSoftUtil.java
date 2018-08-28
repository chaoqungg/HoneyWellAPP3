package com.honeywell.honeywellproject.Util;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by QHT on 2017-10-17.
 */
public class InputSoftUtil {



    public static void showInputSoft(Activity context) {
        final View v = context.getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(v.getWindowToken(), 0, 0);
        }
    }

    public static void hideInputSoft(Activity context){
        final View v = context.getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(context.INPUT_METHOD_SERVICE);
            if(imm.isActive()){
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }

//        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        //切换软键盘的显示与隐藏
//        imm.toggleSoftInputFromWindow(v.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
//        //或者
//      imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
