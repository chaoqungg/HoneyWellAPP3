package com.honeywell.honeywellproject.Util;

import android.os.Environment;

import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;
import com.honeywell.honeywellproject.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by QHT on 2017-10-25.
 */
public class OutPutExeclUtil {


    public static String addressmodel2csv(List<SonTaskBean> list){
        StringBuffer buffer = new StringBuffer();
        buffer.append(ResourceUtil.getString(R.string.backspace1)+"编号"
                +","+
                        "二维码序列号"
                +","+
                        "下载状态"
                +"\n");
        for(SonTaskBean u:list){
            buffer.append(ResourceUtil.getString(R.string.backspace1)+u.getTasknumber()
                    +","+
                          u.getTaskserialnumber()
                    +","+
                          ResourceUtil.getString(R.string.backspace1)+(u.isProcess()?"成功":"失败")
                    +"\n");
        }
        try {
//          String data =new String(buffer.toString().getBytes("utf-8"), "ansi") ;
            String data = buffer.toString();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String filename = "编址地址下载表_"+SharePreferenceUtil.getStringSP("currentusername","无效用户")+simpleDateFormat.format(new Date())+".csv";

            String path = Environment.getExternalStorageDirectory()+"/HoneyWell_AddressTables";
            if (!new File(path).exists()) {
                new File(path).mkdirs();
            }
            File file = new File(path, filename);
            OutputStream out=new FileOutputStream(file);
            //防止乱码。头部加BOM签名，BOM签名能否让excel认识这个文件时utf-8编码的。
            byte b[] = {(byte)0xEF, (byte)0xBB, (byte)0xBF};
            out.write(b);
            out.write(data.getBytes());
            out.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String addressmodel2execl(){
        return "";
    }
}
