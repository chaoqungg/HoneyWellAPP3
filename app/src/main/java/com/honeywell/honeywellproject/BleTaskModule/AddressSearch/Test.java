package com.honeywell.honeywellproject.BleTaskModule.AddressSearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.honeywell.honeywellproject.BaseActivity.BaseApplication;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.AddressSearch.Adapter.ItemAdapter;
import com.honeywell.honeywellproject.BleTaskModule.AddressSearch.SeRightTop.RightTopAdapter;
import com.honeywell.honeywellproject.BleTaskModule.AddressSearch.SeRightTop.RightTopBean;
import com.honeywell.honeywellproject.BleTaskModule.SingleAddress.SingleFreeAddressing.SingleFreeAddressActivity;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.AudioUtil;
import com.honeywell.honeywellproject.Util.BiduTTS.control.InitConfig;
import com.honeywell.honeywellproject.Util.BiduTTS.control.MySyntherizer;
import com.honeywell.honeywellproject.Util.BiduTTS.control.NonBlockSyntherizer;
import com.honeywell.honeywellproject.Util.BiduTTS.listener.MessageListener;
import com.honeywell.honeywellproject.Util.CommonBleUtil;
import com.honeywell.honeywellproject.Util.ConstantUtil;
import com.honeywell.honeywellproject.Util.DataHandler;
import com.honeywell.honeywellproject.Util.LogUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.Util.SystemUtil;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.WidgeView.SwitchButton;
import com.honeywell.honeywellproject.WidgeView.indicatordialog.IndicatorBuilder;
import com.honeywell.honeywellproject.WidgeView.indicatordialog.IndicatorDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.honeywell.honeywellproject.Util.DataHandler.Alone2Hex;
import static com.honeywell.honeywellproject.Util.DataHandler.CheckVB;
import static java.lang.Integer.parseInt;


public class Test extends  ToolBarActivity {

    private List<RightTopBean> rightTopList = new ArrayList<>();

    private ProgressDialog progressdialog;
    private IndicatorDialog dialog;
    private BleDevice bleDevice;
    private CommonBleUtil commonBleUtil;
    private Thread          LoopCardBatteryThread;
    private boolean         isAppExit;
    protected MySyntherizer synthesizer;


    private RecyclerView MyRecyclerView;
    private ItemAdapter mMyAdapter;
    private List<Integer> intInfos = new ArrayList<Integer>();;

    /**
     * 编址类型，CLIP 10X(101,102)\DLIP 20X(201,202)\FlashScan 30X
     * 且当ADDRESSTYPE==0 是代表没有任何读写操作，空闲状态
     */
    private static       int ADDRESSTYPE     = 0;
    private int currentPosition = 0;
    /**
     * 写电池命令
     */
    private static final int LOOPCARDBATTERY = 401;
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode      = TtsMode.MIX;
    private final static int                messageOK      = 1;
    private final static int                messageError   = 2;

    /**
     * CLIP协议轮询状态码
     */
    private static final  int  CIP_POLL  = 1000;
    private static final  int  addressNoExist  = 1001;
    private static final  int  addressExist  = 1002;
    private static final  int  addressRepeat  = 1003;
    private static final  int  over=9999;

    /**
     * 重置
     */
    private static final  int  Reset  = 119;

    /*
    * 轮询灯颜色显示
    */
    private static final  int  gray  = 200;
    private static final  int  green  = 201;
    private static final  int  yellow  = 202;

    //地址

    public  int  addrstr=0;
    public  int  onaddressnum=0;//在线数量显示
    public  int  repeataddressnum=0;//重码数量显示

    public int  lampnum=0;

    //查询速度
    public  int speed=50;

    @BindView(R.id.iv_singleaddress_blestate2)
    ImageView      ivSingleaddressBlestate2;
    @BindView(R.id.iv_singleaddress_blerssi2)
    ImageView      ivSingleaddressBlerssi2;
    @BindView(R.id.iv_singleaddress_blebattery2)
    ImageView      ivSingleaddressBlebattery2;
    //扫描按钮
    private ToggleButton toggleButton=null;
    private Button button1=null;
    private TextView onnum=null;
    private TextView renum=null;

    //private String textArray[]={"0","10","20","30","40","50","60","70","80","90",
           // "100","110","120","130","140","150","160","170","180","190"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolbarTitle().setText("地址搜索");
        initView();
        try {
            initialTts();
        } catch (Exception e) {
        }
        initBle();
    }

    @Override
    public int getContentViewId() {
        return R.layout.circle;
    }

    private void initView() {
        getToolbarTitle().setText("地址搜索");
        getSubTitle().setVisibility(View.INVISIBLE);
        initCommonBleUtil();
        progressdialog = new ProgressDialog(this, R.style.progressDialog);  //转圈


       /* GridView gridView2= (GridView) findViewById(R.id.girdview2);
        Test.ImageAdapter ia =new Test.ImageAdapter(this);
        gridView2.setAdapter(ia);*/

        MyRecyclerView =  (RecyclerView) findViewById(R.id.rv_test);
        MyRecyclerView.setLayoutManager(new GridLayoutManager(this,11));
        onnum= (TextView) findViewById(R.id.onnum);//在线设备
        renum= (TextView) findViewById(R.id.renum);//重码设备

        intInfos();
        mMyAdapter = new ItemAdapter(intInfos);
        MyRecyclerView.setAdapter(mMyAdapter);

        //toggleButton= (ToggleButton) findViewById(R.id.togglebutton);//扫描按钮
        ////////////////////////////////////////////////////////////////////////////点击开始扫描
        button1= (Button) findViewById(R.id.btn_scan);
        button1.setText("扫描");
        button1.setTag(false);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = (boolean) button1.getTag();
                if (!flag) {
                    //开始轮询
                    reset();
                    //speed=(SharePreferenceUtil.getIntSP("progressData"));
                    button1.setText("停止");//按钮上变为停止
                    button1.setTag(true);
                    initWrite();
                } else {
                    button1.setText("扫描");//按钮上变为扫描
                    handler.removeMessages(addressExist);
                    handler.removeMessages(addressNoExist);
                    handler.removeMessages(addressRepeat);
                    handler.removeCallbacksAndMessages(null);
                    button1.setTag(false);
                    intInfos();
                    mMyAdapter.notifyDataSetChanged();
//                    handler.sendEmptyMessage(Reset);
                    reset();//重置变量

                }
            }
        });
    }

    //重置变量
    private  void reset(){
        addrstr=0;
        lampnum=0;
        onaddressnum=0;
        repeataddressnum=0;
        onnum.setText(onaddressnum+"");
        renum.setText(repeataddressnum+"");
    }

    //数组赋值
    private void intInfos() {
        intInfos.clear();
        for (int i = 0; i < 220; i++) {
                intInfos.add(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_menu:
                TopRightClick();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_menu).setVisible(true);
        return true;
    }

    private RightTopAdapter rightTopAdapter;

    private void TopRightClick() {
        rightTopList.clear();
        rightTopList.add(new RightTopBean(1));
        //rightTopList.add(new RightTopBean(2));
        rightTopList.add(new RightTopBean(2));
        if (rightTopAdapter == null) {
            rightTopAdapter = new RightTopAdapter(rightTopList, Test.this);
        }
        if (dialog == null) {
            dialog = new IndicatorBuilder(Test.this)  //must be activity
                    .width(PhoneUtil.getScreenWidth(Test.this) * 3 / 5)                           // the dialog width in px
                    .height(PhoneUtil.getScreenHeight(Test.this) / 3)                          // the dialog max height in px or -1 (means auto fit)
                    .ArrowDirection(IndicatorBuilder.TOP)
                    .bgColor(getResources().getColor(R.color.white))
                    .dimEnabled(true)
                    .gravity(IndicatorBuilder.GRAVITY_RIGHT)
                    .radius(10)
                    .ArrowRectage(0.9f)
                    .layoutManager(new LinearLayoutManager(Test.this, LinearLayoutManager.VERTICAL, false))
                    .adapter(rightTopAdapter).create();
        }
        dialog.setCanceledOnTouchOutside(true);
//        dialog.show(getMenuView());
        dialog.show(getMenuView());
    }

    /**
     * 初始化蓝牙连接操作
     */
    private void initBle() {
        bleDevice = BaseApplication.bleDevice;
        if (!BleManager.getInstance().isBlueEnable()) {
            BleManager.getInstance().enableBluetooth();
        }
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        checkPermissions(permissions);
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
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void checkPermissions(String[] permissions) {

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

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Scan();
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                OutPutCSV();
                break;
            default:
        }
    }

    /**
     * 扫描
     */
    private void Scan() {
        if (BleManager.getInstance().isConnected(bleDevice)) {
            ivSingleaddressBlestate2.setImageResource(R.drawable.connect_50);
            OpenNotify(true);
            getLoopCardBatteryThread();
            BleManager.getInstance().readRssi(bleDevice, new BleRssiCallback() {
                @Override
                public void onRssiFailure(BleException exception) {

                }

                @Override
                public void onRssiSuccess(int rssi) {
                    ivSingleaddressBlerssi2.setImageResource(R.drawable.xinhao_3);
                }
            });
            return;
        }
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                try {
                    if (progressdialog != null && !progressdialog.isShowing()) {
                        progressdialog.setMessage("蓝牙打开和设备扫描中...");
                        progressdialog.show();
                    }
                } catch (Exception e) {
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
                if (scanResultList == null || scanResultList.size() == 0) {
                    return;
                }
                for (BleDevice device : scanResultList) {
                    if (device.getName() == null) {
                        continue;
                    }
                    if (device.getName().equals(ConstantUtil.BLE_NAME)) {
                        bleDevice = BaseApplication.bleDevice = device;
                        break;
                    }
                }
                BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
                    @Override
                    public void onStartConnect() {
                        try {
                            if (progressdialog != null && !progressdialog.isShowing()) {
                                progressdialog.setMessage("编址器连接中...");
                                progressdialog.show();
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onConnectFail(BleException exception) {
                        try {
                            if (progressdialog != null && progressdialog.isShowing()) {
                                progressdialog.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onConnectSuccess(final BleDevice bleDevice, BluetoothGatt gatt, int status) {
                        try {
                            if (progressdialog != null && progressdialog.isShowing()) {
                                progressdialog.dismiss();
                            }
                        } catch (Exception e) {
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                OpenNotify(true);
                                getLoopCardBatteryThread();
                                ivSingleaddressBlestate2.setImageResource(R.drawable.connect_50);
                                BleManager.getInstance().readRssi(bleDevice, new BleRssiCallback() {
                                    @Override
                                    public void onRssiFailure(BleException exception) {
                                    }

                                    @Override
                                    public void onRssiSuccess(int rssi) {
                                        ivSingleaddressBlerssi2.setImageResource(R.drawable.xinhao_3);
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                        if (progressdialog != null) {
                            progressdialog.dismiss();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                BleManager.getInstance().cancelScan();
                                ivSingleaddressBlerssi2.setImageResource(R.drawable.xinhao_wu);
                                ivSingleaddressBlestate2.setImageResource(R.drawable.unconnect_50);
                                ivSingleaddressBlebattery2.setImageResource(R.drawable.battery_null);
                            }
                        });
                    }
                });
            }
        });
    }

    private void OpenNotify(boolean isDelay) {
        if (isDelay) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        commonBleUtil.notifyDevice(bleDevice);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取电量线程  5min一次
     */
    private boolean getLoopCarded = false;

    private void getLoopCardBatteryThread() {
        LoopCardBatteryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isAppExit) {
                    //ADDRESSTYPE ==0 空闲状态，没有写或者读命令
                    try {
                        //因为下位机蓝牙连接后需要1-2S的延迟，所以第一次电池命令需要延迟2S左右
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (ADDRESSTYPE == 0) {
                        if (!getLoopCarded) {
                            handler.sendEmptyMessage(LOOPCARDBATTERY);
                            getLoopCarded = true;
                            try {
                                //休眠5min，然后继续下一次读电池
                                Thread.sleep(5 * 60 * 1000);
                                getLoopCarded = false;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        LoopCardBatteryThread.start();
    }


    /**
     * 电池电量最大9V，显示的时候可以分成4个档位；
     * 0.00=<电压<7.20v，提醒没电，4格全空；
     * 7.20=<电压<7.65, 1格电；
     * 7.65=<电压<8.10, 2格电；
     * 8.10=<电压<8.55, 3格电；
     * 8.55=<电压       4格电；
     *
     * @param batteryValue 为0.00是代表获取失败
     */
    private void setBatteryIcon(final double batteryValue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (batteryValue < 7.2) {
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_null));
                } else if (7.2 <= batteryValue && batteryValue < 7.65) {
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_1));
                } else if (7.65 <= batteryValue && batteryValue < 8.10) {
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_2));
                } else if (8.10 <= batteryValue && batteryValue < 8.55) {
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_3));
                } else if (8.55 <= batteryValue && batteryValue <= 12.00) {
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_4));
                }
                if (batteryValue < 7.8) {
                    speak("电池电量不足", false);
                }
                if (batteryValue < 7.3) {
                    speak("请更换电池", false);
                }
                ADDRESSTYPE = 0;
            }
        });
    }

    public void initialTts() {
        LoggerProxy.printable(true); // 日志打印在logcat中
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        SpeechSynthesizerListener listener = new MessageListener();
        Map<String, String> params = AudioUtil.getParams(this);

        InitConfig initConfig = new InitConfig(ConstantUtil.baiduappId, ConstantUtil.baiduappKey,
                ConstantUtil.baidusecretKey, ttsMode, params, listener);
        synthesizer = new NonBlockSyntherizer(this, initConfig, null); // 此处可以改为MySyntherizer 了解调用过程
    }

    /**
     * 使用百度的SDK播放语音
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     *
     * @param isFlush 是否需要清空队列，直播放最新加入的声音
     */
    private void speak(String content, boolean isFlush) {
        if (synthesizer != null) {
            if (isFlush) {
                synthesizer.stop();
            }
            synthesizer.speak(content);
        }
    }

    @Override
    protected void onDestroy() {
        ADDRESSTYPE = 0;
        if (synthesizer != null) {
            synthesizer.release();
        }
        handler.removeCallbacksAndMessages(null);
        isAppExit = true;
        LoopCardBatteryThread = null;
        System.gc();
        super.onDestroy();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick({R.id.btn_scan,R.id.ll_singleaddress_top})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            /*case R.id.togglebutton:
                //编址
                initWrite();
                break;*/
            case R.id.ll_singleaddress_top:
                //失败重连
                if (!BleManager.getInstance().isConnected(bleDevice)){
                    Scan();
                }
                break;
            default:
        }
    }

    /**
     * 发送的每一条命令都在这里实时监听
     */
    private void initCommonBleUtil() {
        commonBleUtil = new CommonBleUtil();
        commonBleUtil.setResultListener(new CommonBleUtil.OnResultListtener() {

            @Override
            public void writeOnResult(boolean result) {
            }

            @Override
            public void notifyOnResult(boolean result) {
                LogUtil.e("Notify--->" + result);
            }

            @Override
            public void notifyOnSuccess(String values, String UUID) {
                boolean result;
                int poll;

                //speed=(SharePreferenceUtil.getIntSP("progressData"));
                if (ADDRESSTYPE == 401) {
                    setBatteryIcon(DataHandler.LOOPCARDBattery_READ(values));
                } else if (ADDRESSTYPE == 501) {
                    //写序列号成功
                    result = DataHandler.Series_READ(values);
                    if (result) {
                        handler.sendEmptyMessage(messageOK);
                    } else {
                        handler.sendEmptyMessage(messageError);
                    }
                }
                //轮询读
                else if(ADDRESSTYPE == 1000){
                    poll=CLIP_READLOOP(values);
                    lampnum++;
                    speed=Integer.parseInt(SharePreferenceUtil.getStringSP("progressData","50"));
                    if (poll==green) {
                        handler.sendEmptyMessageDelayed(addressExist,speed);
//                        handler.sendEmptyMessage(addressExist);
                    } else if (poll==gray){
                        handler.sendEmptyMessageDelayed(addressNoExist,speed);
                    }else if(poll==yellow){
                        handler.sendEmptyMessageDelayed(addressRepeat,speed);
                    }

                    if(lampnum==200){
                        handler.sendEmptyMessage(over);
                    }

                }
            }
        });
    }

    /**
     * 开始编址1
     */
    private void initWrite() {

            ADDRESSTYPE=1000;
            Writing("CLIP", ADDRESSTYPE, 1);


    }

    /**
     * 开始编址2
     */
    public void Writing(String protocolType, int type, int index) {

        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        if ("LOOPCARD".equals(protocolType)) {
//            //获取回路卡电池电量
//            ADDRESSTYPE = type;
//            commonBleUtil.writeDevice(bleDevice,DataHandler.LOOPCARDBattery_WRITE());
//            return;
//        }
        commonBleUtil.writeDevice(bleDevice, CLIP_WRITE(lampnum));
        }

    public String CLIP_WRITE(int addr) {
        String Y = null;
        String addr2 = null;
        String[] strs;
        //编址命令
        addr2 = Alone2Hex(Integer.toHexString(addr));
        strs = new String[]{"55", "AA", "01", "80", "FF", addr2, "05", "00", "55", "5A"};
        if (addr >= 0 && addr <= 99) {
            addr2 = Alone2Hex(Integer.toHexString(addr));
            Y = "00";
        } else if (addr >= 100 && addr <= 199) {
            addr2 = Alone2Hex(Integer.toHexString(addr % 100));
            Y = "01";
        } else {
            return null;
        }
        strs = new String[]{"55", "AA", "01", "80", Y, addr2, "05", "00", "55", "5A"};

        //检验位
        strs[7]=CheckVB(2,6,strs);
        StringBuffer sb = new StringBuffer();
            for(int k = 0; k < strs.length; k++){
            sb. append(strs[k]);
            //再判断里面是否含有0x55，若有需要后面补00
            if(k>=2 && k<=7){
                if("55".equals(strs[k])){
                    sb. append("00");
                }
            }
        }
            return sb.toString();
    }

    /**
     * CLIP 连续编址的读
     * */
    public static int CLIP_READLOOP(String values){


        String[] datas=new String[values.length()/2];
        //数据长度不固定，注意处理0x55的情况
        for(int i=0,j=0;i<values.length();i+=2,j++){
            datas[j]=values.substring(i,i+2);
            if(j!=0 && "00".equals(datas[j]) && "55".equals(datas[j-1])){
                //出现0x55 0x00的情况则干掉00保留55,这样能保证数据长度不变，便于取值
                j--;
            }
        }
        //故障位
        String malfunction=datas[4];
        String pW1 = datas[5];
        //暂时修改一下，5位有一个不为00就行
        int H = Integer.parseInt(pW1, 16);
        if ((H * 256) > 100 && "00".equals(malfunction)) {
            return green;
        }else if("01".equals(malfunction)){
            return yellow;
        }
            return gray;
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(false==(Boolean) button1.getTag())
                return;

            if(lampnum==201)
                return;

            if(addrstr%11==0){
                addrstr++;
            }
            if(addrstr==220){
                return;
            }
            switch (msg.what) {
                case addressExist:
                    intInfos.set(addrstr,1);
                    mMyAdapter.notifyItemChanged(addrstr);
                    addrstr++;
                    onaddressnum++;
                    Writing("",0,0);
                    break;
                case addressNoExist:
                    intInfos.set(addrstr,0);
                    mMyAdapter.notifyItemChanged(addrstr);
                    addrstr++;
                    Writing("",0,0);
                    break;
                case addressRepeat:
                    intInfos.set(addrstr,2);
                    mMyAdapter.notifyItemChanged(addrstr);
                    addrstr++;
                    repeataddressnum++;
                    Writing("",0,0);
                    break;
                case over:
                    button1.setText("完成");
                    break;
                default:
            }
            //显示在线数量
            onnum.setText(onaddressnum+"");
            //显示重码数量
            renum.setText(repeataddressnum+"");


        }
    } ;
}