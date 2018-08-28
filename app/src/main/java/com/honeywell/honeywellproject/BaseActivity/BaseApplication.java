package com.honeywell.honeywellproject.BaseActivity;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;

import com.amitshekhar.DebugDB;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.honeywell.honeywellproject.BuildConfig;
import com.honeywell.honeywellproject.ELModule.data.USBEvent;
import com.honeywell.honeywellproject.Util.ConstantUtil;
import com.honeywell.honeywellproject.Util.EventBusUtil;
import com.honeywell.honeywellproject.Util.LogUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;


//  import com.amitshekhar.DebugDB;
// @author QHT
// @date 2017-10-09

public class BaseApplication  extends Application{

    protected static BaseApplication instance;
    public  static BleDevice bleDevice;
    public  static BleDevice elDevice;
    @Override
    public void onCreate() {
        super.onCreate();
        instance =this;
        DebugDB.initialize(this);
        LitePal.initialize(this);
        BleManager.getInstance().init(this);
        SharePreferenceUtil.initSharePreferenceUtil(this);
        LogUtil.e(DebugDB.getAddressLog());
        EventBus.builder().throwSubscriberException(BuildConfig.DEBUG).installDefaultEventBus();
        CrashReport.initCrashReport(getApplicationContext(), "80982887bc", false);
        registerReceiver(mUsbDeviceReceiver, new IntentFilter(
                UsbManager.ACTION_USB_DEVICE_ATTACHED));
        registerReceiver(mUsbDeviceReceiver, new IntentFilter(
                UsbManager.EXTRA_PERMISSION_GRANTED));
        registerReceiver(mUsbDeviceReceiver, new IntentFilter(
                UsbManager.ACTION_USB_DEVICE_DETACHED));
    }
    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            synchronized (this) {
                if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
//                    ToastUtil.showToastShort("红外设备插入");
                    EventBusUtil.postSync(new USBEvent(true));
                    ConstantUtil.USBInsert=true;
                } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
//                    ToastUtil.showToastShort("红外设备拔出");
                    // TO  ELActivityNewUI.java
                    EventBusUtil.postSync(new USBEvent(false));
                    ConstantUtil.USBInsert=false;
                }
            }

        }
    };
    public static BaseApplication getInstance() {
        return instance;
    }
}
