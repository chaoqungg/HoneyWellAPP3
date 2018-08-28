package com.honeywell.honeywellproject.ELModule;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.ConsumerIrManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.honeywell.honeywellproject.BaseActivity.BaseApplication;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.ELModule.adapter.MenuAdapter;
import com.honeywell.honeywellproject.ELModule.adapter.ViewPagerAdapter;
import com.honeywell.honeywellproject.ELModule.data.LightEvent;
import com.honeywell.honeywellproject.ELModule.data.MenuBean;
import com.honeywell.honeywellproject.ELModule.data.USBEvent;
import com.honeywell.honeywellproject.ELModule.data.bigLightState;
import com.honeywell.honeywellproject.ELModule.fragment.Fragment1;
import com.honeywell.honeywellproject.ELModule.fragment.Fragment2;
import com.honeywell.honeywellproject.ELModule.fragment.Fragment3;
import com.honeywell.honeywellproject.ELModule.fragment.Fragment4;
import com.honeywell.honeywellproject.ELModule.fragment.Fragment5;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.ConstantUtil;
import com.honeywell.honeywellproject.Util.DialogUtil;
import com.honeywell.honeywellproject.Util.ELUtil;
import com.honeywell.honeywellproject.Util.EventBusUtil;
import com.honeywell.honeywellproject.Util.LogUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;
import com.honeywell.honeywellproject.WidgeView.NoPreloadViewPager;
import com.honeywell.honeywellproject.WidgeView.SwitchButton;
import com.honeywell.honeywellproject.WidgeView.indicatordialog.IndicatorBuilder;
import com.honeywell.honeywellproject.WidgeView.indicatordialog.IndicatorDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.OnClick;

/**
 * 引导页
 *
 * @author fangood163@163.com
 * @version V1.0
 * @since 创建时间：2015年4月7日 下午2:11:09
 */
public class ELActivityNewUI extends ToolBarActivity {

    private NoPreloadViewPager    viewPage;
    private Fragment1    mFragment1;
    private Fragment2    mFragment2;
    private Fragment3    mFragment3;
    private Fragment4    mFragment4;
    private Fragment5    mFragment5;
    private PagerAdapter mPgAdapter;
    private RadioGroup   dotLayout;
    private List<Fragment> mListFragment = new ArrayList<Fragment>();
    private MenuAdapter rightTopAdapter;
    private List<MenuBean> rightTopList = new ArrayList<>();
    private IndicatorDialog dialog;
    /**
     * Android4.4之后 红外遥控ConsumerIrManager，可以被小米4调用
     */
    protected ConsumerIrManager mCIR;
    /**
     * 手机是否自带红外
     */
    public boolean inIR = false;
    private ProgressDialog       progressdialog;
    private BleDevice            elDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        checkUsb();
        viewPage.setOnPageChangeListener(new MyPagerChangeListener());
    }
    @Override
    public int getContentViewId() {
        return R.layout.activity_guide;
    }

    private void initView() {
        initIR();
        getToolbarTitle().setText("红外调试器");
        dotLayout = (RadioGroup) findViewById(R.id.advertise_point_group);
        viewPage = (NoPreloadViewPager) findViewById(R.id.viewpager);
        mFragment1 = new Fragment1();
        mFragment2 = new Fragment2();
        mFragment3 = new Fragment3();
        mFragment4 = new Fragment4();
        mFragment5 = new Fragment5();
        mListFragment.add(mFragment1);
        mListFragment.add(mFragment2);
        mListFragment.add(mFragment3);
        mListFragment.add(mFragment4);
        mListFragment.add(mFragment5);
        mPgAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                mListFragment);
        viewPage.setAdapter(mPgAdapter);
        if (SharePreferenceUtil.getBooleanSP("fanzhuan")) {
            PhoneUtil.ConvertScreen(true,ELActivityNewUI.this);
        }
        progressdialog = new ProgressDialog(this, R.style.progressDialog);
    }

    /**
     * 初始化蓝牙连接操作
     */
    private void initBle() {
        elDevice= BaseApplication.elDevice;
        if (! BleManager.getInstance().isBlueEnable()) {
            BleManager.getInstance().enableBluetooth();
        }
        checkPermissions();
    }
    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, 12);
        }
    }
    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 12:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }else{
                            if(permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                                DialogUtil.showAlertDialog(ELActivityNewUI.this,
                                        "提示","蓝牙要求位置权限未开启，请在设置中打开位置权限",null,null);
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
    }
    /**
     * 扫描
     */
    private void Scan() {
        if (BleManager.getInstance().isConnected(elDevice)) {
            //已经连接后在进入的话 viewBleState 的初始化比较慢，有可能为空，所以延迟一下
            viewPage.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(viewBleState!=null){
                        ((ImageView)(viewBleState.getChildAt(0))).setImageDrawable(ResourceUtil.getDrawable(R.drawable.connect_50));
                    }
                }
            }, 100);
             return;
        }
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                if (progressdialog != null && !progressdialog.isShowing()) {
                    progressdialog.setMessage("设备连接中...");
                    progressdialog.show();
                }
            }
            @Override
            public void onScanning(BleDevice result) {
            }
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                if (progressdialog != null) {
                    progressdialog.dismiss();
                }
                if(scanResultList==null || scanResultList.size()==0)
                {return;}
                for(BleDevice device : scanResultList){
                    if(device.getName()==null){
                        continue;
                    }
                    if(device.getName().equals(ConstantUtil.EL_NAME)){
                        elDevice=BaseApplication.elDevice=device;
                        break;
                    }
                }
                BleManager.getInstance().connect(elDevice, new BleGattCallback() {
                    @Override
                    public void onStartConnect() {
                    }

                    @Override
                    public void onConnectFail(BleException exception) {
                        if (progressdialog != null && progressdialog.isShowing()) {
                            progressdialog.dismiss();
                        }
                        ToastUtil.showToastShort("连接失败");
                    }

                    @Override
                    public void onConnectSuccess(final BleDevice bleDevice, BluetoothGatt gatt, int status) {
                        if (progressdialog != null && progressdialog.isShowing()) {
                            progressdialog.dismiss();
                        }
                        ToastUtil.showToastShort("连接成功");
                        if(viewBleState!=null){
                        ((ImageView)(viewBleState.getChildAt(0))).setImageDrawable(ResourceUtil.getDrawable(R.drawable.connect_50));
                        }
                    }

                    @Override
                    public void onDisConnected(boolean isActiveDisConnected, final BleDevice device, BluetoothGatt gatt, int status) {
                        if (progressdialog != null) {
                            progressdialog.dismiss();
                        }
                        if(viewBleState!=null){
                            ((ImageView)(viewBleState.getChildAt(0))).setImageDrawable(ResourceUtil.getDrawable(R.drawable.connect_50));
                        }
                        final BleGattCallback callback=this;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //断开重连
                                BleManager.getInstance().connect(device,callback);
                            }
                        },200);
                    }
                });
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                default:
            }
        }
    } ;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_menu:
                TopRightClick();
                break;
            case R.id.action_blestate:
                if (! BleManager.getInstance().isBlueEnable()) {
                    BleManager.getInstance().enableBluetooth();
                }else {

                    Scan();
                }
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
    LinearLayout viewBleState;
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_menu).setVisible(true);
        menu.findItem(R.id.action_blestate).setVisible(true);
        viewBleState = (LinearLayout) menu.findItem(R.id.action_blestate).getActionView();
        if (BleManager.getInstance().isConnected(elDevice)) {
            ((ImageView)(viewBleState.getChildAt(0))).setImageDrawable(ResourceUtil.getDrawable(R.drawable.connect_50));
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        initBle();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initRightTop();
            }
        },50);
    }

    private void initRightTop() {
        rightTopList.clear();
        rightTopList.add(new MenuBean(1, "节" + ResourceUtil.getString(R.string.backspace_hanzi2) + "能", R.drawable.jieneng_40));
        rightTopList.add(new MenuBean(1, "翻转屏幕", R.drawable.fanzhuan_40));
        rightTopList.add(new MenuBean(1, "全部关闭", R.drawable.quanbuguanbi_40));
       //初始化页面显示无驱动
        if(ConstantUtil.selectDriveItem==-1){
            if(BleManager.getInstance().isConnected(BaseApplication.elDevice)){
                rightTopList.add(new MenuBean(2, "当前驱动", "外接usb",R.drawable.guanyu_40));
                ConstantUtil.selectDriveItem=1;
            } /*else if(inIR){
                rightTopList.add(new MenuBean(2, "当前驱动", "内置红外",R.drawable.guanyu_40));
                //隐藏蓝牙连接图标
                viewBleState.setVisibility(View.INVISIBLE);
                ConstantUtil.selectDriveItem=0;
            }*/else{
                rightTopList.add(new MenuBean(2, "当前驱动", "无驱动",R.drawable.guanyu_40));
                ConstantUtil.selectDriveItem=-1;
            }
        }/*else if(ConstantUtil.selectDriveItem==0){
            rightTopList.add(new MenuBean(2, "当前驱动", "内置红外",R.drawable.guanyu_40));
            //隐藏蓝牙连接图标
            viewBleState.setVisibility(View.INVISIBLE);
        }*/else {
            rightTopList.add(new MenuBean(2, "当前驱动", "外接usb",R.drawable.guanyu_40));
        }
    }

    private void TopRightClick() {
        initRightTop();
        if (rightTopAdapter == null) {
            rightTopAdapter = new MenuAdapter(rightTopList, ELActivityNewUI.this);
        }
        rightTopAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                view.playSoundEffect(SoundEffectConstants.CLICK);  // 触感(震动)反馈和声音反馈的效果实现
                switch (position) {
                    case 0:
                        //节能
                        showTips("使用此命令将所有灯板亮度减半，以达到省电的目的");
                        break;
                    case 1:
                        //翻转屏幕
                        showTips("发射器插在手机下方的用户将屏幕反转180°");
                        break;
                    case 2:
                        //全部关闭
                        showTips("发送全部关闭命令，关闭全部的灯板");
                        break;
                    case 3:
                        //当前驱动
//                        showDriveSelect();
                        break;
                    default:
                }
            }
        });
//        rightTopAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
//            @Override
//            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
//                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
//                view.playSoundEffect(SoundEffectConstants.CLICK);
//                switch (position) {
//                    case 0:
//                        //节能
//                        showTips("使用此命令将所有灯板亮度减半，以达到省电的目的");
//                        view.setOnTouchListener(new View.OnTouchListener() {
//                            @Override
//                            public boolean onTouch(View view, MotionEvent motionEvent) {
//                                view.getParent().requestDisallowInterceptTouchEvent(true);
//                                switch (motionEvent.getActionMasked()) {
//                                    //自定义了LinearLayout的拦截，让父View不拦截up和move事件
//                                    case MotionEvent.ACTION_UP:
//                                        showTips("节能抬起");
//                                        view.setOnTouchListener(null);
//                                        break;
//                                    default:
//                                }
//                                return false;
//                            }
//                        });
//                        break;
//                    case 1:
//                        //翻转屏幕
//                        showTips("发射器插在手机下方的用户将屏幕反转180°");
//                        view.setOnTouchListener(new View.OnTouchListener() {
//                            @Override
//                            public boolean onTouch(View view, MotionEvent motionEvent) {
//                                view.getParent().requestDisallowInterceptTouchEvent(true);
//                                switch (motionEvent.getActionMasked()) {
//                                    //自定义了LinearLayout的拦截，让父View不拦截up和move事件
//                                    case MotionEvent.ACTION_UP:
//                                        LogUtil.e("ACTION_UP");
//                                        //避免继续监听了单击事件的up
//                                        view.setOnTouchListener(null);
//                                        break;
//                                    default:
//                                }
//                                return false;
//                            }
//                        });
//                        break;
//                    case 2:
//                        //全部关闭
//                        showTips("长按全部关闭");
//                        view.setOnTouchListener(new View.OnTouchListener() {
//                            @Override
//                            public boolean onTouch(View view, MotionEvent motionEvent) {
//                                view.getParent().requestDisallowInterceptTouchEvent(true);
//                                switch (motionEvent.getActionMasked()) {
//                                    //自定义了LinearLayout的拦截，让父View不拦截up和move事件
//                                    case MotionEvent.ACTION_UP:
//                                        showTips("发送全部关闭命令，关闭全部的灯板");
//                                        LogUtil.e("ACTION_UP");
//                                        view.setOnTouchListener(null);
//                                        break;
//                                    default:
//                                }
//                                return false;
//                            }
//                        });
//                        break;
//                    default:
//                }
//                return false;
//            }
//        });
        if (dialog == null) {
            dialog = new IndicatorBuilder(ELActivityNewUI.this)  //must be activity
                    .width(PhoneUtil.getScreenWidth(ELActivityNewUI.this) * 3 / 5)                           // the dialog width in px
                    .height(PhoneUtil.getScreenHeight(ELActivityNewUI.this) * 2 / 5)                          // the dialog max height in px or -1 (means auto fit)
                    .ArrowDirection(IndicatorBuilder.TOP)
                    .bgColor(getResources().getColor(R.color.white))
                    .dimEnabled(true)
                    .gravity(IndicatorBuilder.GRAVITY_RIGHT)
                    .radius(10)
                    .ArrowRectage(0.9f)
                    .layoutManager(new LinearLayoutManager(ELActivityNewUI.this, LinearLayoutManager.VERTICAL, false))
                    .adapter(rightTopAdapter).create();
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.show(getMenuView());
        rightTopAdapter.setOnCheckedChangeListener(new MenuAdapter.OnCheckedChangeListener() {
            @Override
            public void check(int i, final SwitchButton button, boolean isSelect) {
                switch (i) {
                    case 0:
                        //节能
                        SharePreferenceUtil.setBooleanSP("jieneng", isSelect);
                        if (isSelect) {
                           EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "0D"),"04FB0DF2"));
                        }else{
                           EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "0E"),"04FB0EF1"));
                        }
                        break;
                    case 1:
                        //翻转屏幕
                        SharePreferenceUtil.setBooleanSP("fanzhuan", isSelect);
                        //反转屏幕
                        PhoneUtil.ConvertScreen(isSelect,ELActivityNewUI.this);
                        break;
                    case 2:
                        //全部关闭
                        if (isSelect) {
                          EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "00"),"04FB00FF"));
                            mFragment1.closeAllImage();
                            mFragment2.closeAllImage();
                            mFragment3.closeAllImage();
                            mFragment4.closeAllImage();
                            mFragment5.closeAllImage();
                            mFragment2.onViewClicked(view);
                            mFragment1.onViewClicked(view);
                            mFragment3.onViewClicked(view);
                            mFragment4.onViewClicked(view);
                            mFragment5.onViewClicked(view);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(button!=null){
                                        button.toggle();
                                    }
                                }
                            }, 800);
                        }

                        break;
                    default:
                }
            }
        });
    }

   /* private void showDriveSelect() {
        if(ConstantUtil.selectDriveItem==-1){
            DialogUtil.showTipsDialog(this, "您的手机无内置红外且没有外接usb红外设备，请插入外接usb红外设备", new DialogUtil.OnTipsClick() {
                @Override
                public void click(Dialog dialog) {
                    dialog.dismiss();
                }
                @Override
                public void clickCancel(Dialog dialog) {
                }
            });
        }else{
            String[] items=new String[]{"手机内置红外","外接usb红外"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this,3);
            builder.setSingleChoiceItems(items, ConstantUtil.selectDriveItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which==0){
                        if(inIR){
                            ConstantUtil.selectDriveItem=0;
                            rightTopList.get(3).setDrive("内置红外");
                            rightTopAdapter.notifyItemChanged(3);
                            //隐藏蓝牙连接图标
                            viewBleState.setVisibility(View.INVISIBLE);
                            dialog.dismiss();
                        }else{
                            //没有内置红外
                    ToastUtil.showToastShort("您的手机无内置红外");
                        }
                    }else if(which==1){
                        viewBleState.setVisibility(View.VISIBLE);
                        ConstantUtil.selectDriveItem=1;
                        rightTopList.get(3).setDrive("外接usb");
                        rightTopAdapter.notifyItemChanged(3);
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }*/

    private void showTips(String tips) {
        DialogUtil.showTipsDialog(this, tips, new DialogUtil.OnTipsClick() {
            @Override
            public void click(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void clickCancel(Dialog dialog) {

            }
        });
    }

    @OnClick(R.id.bianzhi_bottom)
    public void onViewClicked() {
        gotoSingleAddress();
    }

    private void gotoSingleAddress(){
        List<bigLightState> bigLightStateList = new ArrayList<>();
        Intent intent=new Intent(ELActivityNewUI.this,El_SingleFreeActivity.class);
        Bundle bundle=new Bundle();
        switch (viewPage.getCurrentItem()){
            case 0:
                bigLightStateList=mFragment1.bigLightStateList;
                bundle.putInt("fragment",1);
                break;
            case 1:
                bigLightStateList=mFragment2.bigLightStateList;
                bundle.putInt("fragment",2);
                break;
            case 2:
                bigLightStateList=mFragment3.bigLightStateList;
                bundle.putInt("fragment",3);
                break;
            case 3:
                bigLightStateList=mFragment4.bigLightStateList;
                bundle.putInt("fragment",4);
                break;
            case 4:
                bigLightStateList=mFragment5.bigLightStateList;
                bundle.putInt("fragment",5);
                break;
            default:
        }
            //fragment  3个灯板，按照顺序，1-2-3
            int m=0;
            for (int i = 0; i < bigLightStateList.size() ; i++) {
                if( bigLightStateList.get(i).isSelect()){
                    m=i;
                    break;
                }
            }
            bundle.putInt("lamps",m+1);
            //哪个被选中，则是哪个
            intent.putExtras(bundle);
            startActivity(intent);
    }

    public class MyPagerChangeListener implements NoPreloadViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < 5; i++) {
                ((RadioButton) dotLayout.getChildAt(i)).setChecked(false);
            }
            ((RadioButton) dotLayout.getChildAt(position)).setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }
    }

    private void initIR() {
        // 获取系统的红外遥控服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mCIR = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);
            if (mCIR.hasIrEmitter()) {
                inIR = true;
            }
        }
    }

    /**
     * 优先内置红外
     **/
    protected void transmit(int[] pattern,String commandstr) {
        if (BleManager.getInstance().isConnected(BaseApplication.elDevice) ) {
            write(commandstr);

        } else {
            DialogUtil.showTipsDialog(ELActivityNewUI.this, "您的手机无自带红外功能，请插入USB红外发射装置", new DialogUtil.OnTipsClick() {
                @Override
                public void click(Dialog dialog) {
                    dialog.dismiss();
                }
                @Override
                public void clickCancel(Dialog dialog) {
                }
            });
        }
    }

    private void write(String datas){
        BleManager.getInstance().write(elDevice,ConstantUtil.SERVER_UUID_EL, ConstantUtil.Write_UUID_EL, HexUtil.hexStringToBytes(datas), new BleWriteCallback() {
            @Override
            public void onWriteSuccess() {
                LogUtil.e("write -----> success");
            }

            @Override
            public void onWriteFailure(BleException exception) {
                LogUtil.e("write -----> exception:"+exception.getDescription());
            }
        });
    }

    /**
     * 由fragment1-5 post到这儿进行红外命令发送
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLight(LightEvent response) {
        transmit(response.command,response.commandstr);
    }
    /**
     * Applicaiton 发送的USB插拔消息
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUSBLight(USBEvent response) {
        if(BleManager.getInstance().isConnected(BaseApplication.elDevice)){
            return;
        }
        if(response.Insert){
            DialogUtil.showTwoButtonDialog(ELActivityNewUI.this, "系统检测到您已经插入了USB设备，此设备为HoneyWell的设备吗？\n" +
                    "点击“确定”系统将开始连接设备。" +
                    "", new DialogUtil.OnTipsClick() {
                @Override
                public void click(Dialog dialog) {
                    dialog.dismiss();
                    Scan();
                }
                @Override
                public void clickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        }else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.showTipsDialog(ELActivityNewUI.this, "设备已移除，请重新插入USB红外发射装置", new DialogUtil.OnTipsClick() {
                        @Override
                        public void click(Dialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void clickCancel(Dialog dialog) {
                        }
                    });
                }
            }, 2000);
            if (viewBleState != null) {
                ((ImageView) (viewBleState.getChildAt(0))).setImageDrawable(ResourceUtil.getDrawable(R.drawable.unconnect_50));

            }
        }
    }




    @Override
    public void onStart() {
        super.onStart();
        EventBusUtil.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }
    private void checkUsb() {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
//        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        if(!deviceList.isEmpty()){
            ConstantUtil.USBInsert=true;
            onUSBLight(new USBEvent(true));

        }
//        String i = "";
//        while (deviceIterator.hasNext()) {
//            UsbDevice device = deviceIterator.next();
//            if(device.getVendorId()==4292 && device.getProductId()==33896){
//                ConstantUtil.USBInsert=true;
//                onUSBLight(new USBEvent(true));
//            }
//        }
    }
}
