package com.honeywell.honeywellproject.InitWellComeModule.WellcomePage;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.honeywell.honeywellproject.InitWellComeModule.BleOrElSelectActivity;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.LogUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;

import java.util.List;

public class WellcomeActivity extends Activity {

    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals("com.honeywell.honeywellproject") &&
                    info.baseActivity.getPackageName().equals("com.honeywell.honeywellproject")&&
                    (info.baseActivity.getShortClassName().equals(".InitWellComeModule.BleOrElSelectActivity")
                    ||info.baseActivity.getShortClassName().equals(".InitWellComeModule.WellcomePage.MainActivity")) ) {
                LogUtil.e("WellcomeActivity第二次启动");
                finish();
                return;
            }
        }
        setContentView(R.layout.activity_wellcome);
        ll=(LinearLayout)findViewById(R.id.ll);
        checkLogin();
    }
    private void checkLogin() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(SharePreferenceUtil.getBooleanSP("notfirstusebleapp")) {
                        try {
                            Thread.sleep(3000);
                            ll.post(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(WellcomeActivity.this, BleOrElSelectActivity.class));
                                    finish();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            Thread.sleep(3000);
                            ll.post(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(WellcomeActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
    }
}
