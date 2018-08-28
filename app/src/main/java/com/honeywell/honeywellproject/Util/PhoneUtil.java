package com.honeywell.honeywellproject.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by QHT on 2017-02-27.
 */

public  class PhoneUtil {


        /**
         * 获取屏幕高度
         */
        public static int getScreenHeight(Activity activity) {
        // TODO Auto-generated method stub
        DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        return displayHeight;
        }
    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Activity activity) {
        // TODO Auto-generated method stub
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        return displayWidth;
    }
        /**
         * 强制隐藏键盘
         */
        public static void hideInputWindow(Context context,View view) {
         InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
         imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    /**
     * 显示键盘
     */
    public static void showInputWindow(Context context,View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
     }

    /**
     * 旋转屏幕
     */
    public static void ConvertScreen(boolean isSelect,Activity context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Settings.System.putInt(context.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,1);
            }
        }
        if (isSelect) {
            if (context.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {//未指定，此为默认值。由Android系统自己选择合适的方向。
                ToastUtil.showToastShort("错误：无法改变屏幕方向。");
            } else {
                //android 屏幕竖屏正方向旋转180度
                if (context.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                }
            }
        } else {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //指定屏幕方向为竖屏
        }
    }
    }
