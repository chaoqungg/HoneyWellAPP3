package com.honeywell.honeywellproject.Util.mail;

import android.support.annotation.NonNull;

import com.honeywell.honeywellproject.Util.SharePreferenceUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/10.
 */

public class SendMailUtil {

    //qq
    private static final String HOST = "smtp.qq.com";
    private static final String PORT = "587";
//    private static final String FROM_ADD = "teprinciple@foxmail.com";
//    private static final String FROM_PSW = "lfrlpganzjrwbeci";
    private static final String FROM_ADD = "1003077897@qq.com";
    private static final String FROM_PSW = "petxpcheenmpbefe";//POP3授权{验证}码

//    //163
//    private static final String HOST = "smtp.163.com";
//    private static final String PORT = "465"; //或者465  994
//    private static final String FROM_ADD = "teprinciple@163.com";
//    private static final String FROM_PSW = "teprinciple163";
////    private static final String TO_ADD = "2584770373@qq.com";


    public static void send(final File file,String toAdd){
        final MailInfo mailInfo = creatMail(toAdd);
        mailInfo.setAttachFileNames(file);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendFileMail(mailInfo,file);
            }
        }).start();
    }


    public static void send(String toAdd){
        final MailInfo mailInfo = creatMail(toAdd);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendTextMail(mailInfo);
            }
        }).start();
    }

    @NonNull
    private static MailInfo creatMail(String toAdd) {
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(HOST);
        mailInfo.setMailServerPort(PORT);
        mailInfo.setValidate(true);
        mailInfo.setUserName(FROM_ADD); //  你的邮箱地址
        mailInfo.setPassword(FROM_PSW);//   SMTP授权码
        mailInfo.setFromAddress(FROM_ADD); // 发送的邮箱
        mailInfo.setToAddress(toAdd); // 发到哪个邮件去
        mailInfo.setSubject("APP批量编址记录_日期时间"+new SimpleDateFormat("yyyy-MM-dd_HH:mm").format(new Date())); // 邮件主题
        mailInfo.setContent("此邮件由蓝牙编址器APP发送，请勿回复。  \n" +
                "编址操作人员："+ SharePreferenceUtil.getStringSP("currentusername","未注册")+"  \n"+
                "联系方式："+ SharePreferenceUtil.getStringSP("currentphone","未注册"));
//        mailInfo.setCCs(new String[]{"Cui.Xuanke@Honeywell.com","Wenfang.Liu@systemsensor.com.cn"});
        return mailInfo;
    }

}
