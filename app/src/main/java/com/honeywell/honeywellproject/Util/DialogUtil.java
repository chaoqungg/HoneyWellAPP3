package com.honeywell.honeywellproject.Util;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.honeywell.honeywellproject.R;

import static android.content.DialogInterface.OnClickListener;

/**
 * Created by QHT on 2017-02-27.
 */
public class DialogUtil {

    /**
     *普通提示对话框
     */
    public static void showAlertDialog(Context context, String title, String message, final OnClickListener listenerSure,
                                       final OnClickListener listenerCancel) {
        showTwoButtonDialog(context, message, new OnTipsClick() {
            @Override
            public void click(Dialog dialog) {
                listenerSure.onClick(dialog,1);
                dialog.dismiss();
            }

            @Override
            public void clickCancel(Dialog dialog) {
                if(listenerCancel==null){
                    dialog.dismiss();
                }else{
                    listenerCancel.onClick(dialog,0);
                    dialog.dismiss();
                }
            }
        });
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.setPositiveButton("确定", listenerSure);
//        builder.setNegativeButton("取消", listenerCancel);
//        builder.create().show();
    }
    /*----------------------------dialog---------------------------------*/

    public interface  OnTipsClick {
        void click(Dialog dialog);
        void clickCancel(Dialog dialog);
    }
    public static void showTipsDialog(Context context , String message, final OnTipsClick clicker) {
        View view = LayoutInflater.from(context).inflate(R.layout.tipsdialog, null);
        TextView confirm;    //确定按钮
        final TextView content;    //内容
        confirm = (TextView) view.findViewById(R.id.dialog_btn_comfirm);
        content = (TextView) view.findViewById(R.id.dialog_txt_content);
        content.setText(message);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicker.click(dialog);
            }
        });
        dialog.show();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.55);    //宽度设置为屏幕的0.5
        p.height = (int) (displayHeight * 0.24);    //宽度设置为屏幕的0.5
        dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);     //设置生效
    }

    public static void showTwoButtonDialog(Context context , String message,
                                           final OnTipsClick clicker) {
        View view = LayoutInflater.from(context).inflate(R.layout.twobuttondialog, null);
        TextView confirm;    //确定按钮
        TextView cancel;     //取消按钮
        final TextView content;    //内容
        confirm = (TextView) view.findViewById(R.id.dialog_btn_comfirm);
        content = (TextView) view.findViewById(R.id.dialog_txt_content);
        cancel = (TextView) view.findViewById(R.id.dialog_btn_cancel);
        content.setText(message);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicker.click(dialog);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicker.clickCancel(dialog);
            }
        });
        dialog.show();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.6);    //宽度设置为屏幕的0.5
        p.height = (int) (displayHeight * 0.3);    //宽度设置为屏幕的0.5
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);     //设置生效
    }
}
