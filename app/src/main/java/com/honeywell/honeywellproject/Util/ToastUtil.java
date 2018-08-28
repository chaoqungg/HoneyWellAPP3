package com.honeywell.honeywellproject.Util;

import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.honeywellproject.BaseActivity.BaseApplication;


/**
 * Created by QHT on 2017-02-27.
 */
public class ToastUtil {

    private static Toast toast;
    /**
     * 自定义Toast
     *
     * @param message
     */
    public static void showToastShort(CharSequence message) {
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getInstance(),
                    message,
                    Toast.LENGTH_SHORT);
            TextView textview=(TextView) toast.getView().findViewById(android.R.id.message);
//            toast.getView().setBackgroundColor(ResourceUtil.getColor(R.color.gray_shadow));
//            textview.setTextColor(ResourceUtil.getColor(R.color.black));
            textview.setTextSize(ResourceUtil.sp2px(BaseApplication.getInstance(),5));
            try{
                toast.setText(message);
            }catch (RuntimeException ex){
                return;
            }
        } else {
            try{
                toast.setText(message);
            }catch (RuntimeException ex){
                return;
            }
            LinearLayout toastView = (LinearLayout) toast.getView();
            if(toastView.getChildAt(1)!=null){
                toastView.removeViewAt(0);
            }
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

      public static void  showToastLong(CharSequence message) {
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getInstance(),
                    message,
                    Toast.LENGTH_LONG);
            TextView textview=(TextView) toast.getView().findViewById(android.R.id.message);
//            toast.getView().setBackgroundColor(ResourceUtil.getColor(R.color.gray_shadow));
//            textview.setTextColor(ResourceUtil.getColor(R.color.black));
            textview.setTextSize(ResourceUtil.sp2px(BaseApplication.getInstance(),5));
        } else {
              try{
                  toast.setText(message);
              }catch (RuntimeException ex){
                  return;
              }
              LinearLayout toastView = (LinearLayout) toast.getView();
              if(toastView.getChildAt(1)!=null){
                  toastView.removeViewAt(0);
              }
          }
          toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * @param resID  图片资源  R.drawable.progress_ok
     * */
    public static void showImageToastShort(CharSequence message,int resID){
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getInstance(),
                    message,
                    Toast.LENGTH_SHORT);
            TextView textview=(TextView) toast.getView().findViewById(android.R.id.message);
//            toast.getView().setBackgroundColor(ResourceUtil.getColor(R.color.gray_shadow));
//            textview.setTextColor(ResourceUtil.getColor(R.color.black));
            textview.setTextSize(ResourceUtil.sp2px(BaseApplication.getInstance(),5));
        }
        try{
            toast.setText(message);
        }catch (RuntimeException ex){
            return;
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) toast.getView();
        //0 是图片，1 是TextView
        if(toastView.getChildAt(1)==null) {
            ImageView imageCodeProject = new ImageView(BaseApplication.getInstance());
            imageCodeProject.setImageResource(resID);
            toastView.addView(imageCodeProject, 0);
        }

        toast.show();
    }
}
