package com.honeywell.honeywellproject.BleTaskModule.SingleAddress.SingleTaskAddressing.widge;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.honeywell.honeywellproject.BaseActivity.BaseApplication;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.Log.widge.LogActivity;
import com.honeywell.honeywellproject.BleTaskModule.Log.data.LogBean;
import com.honeywell.honeywellproject.BleTaskModule.SingleAddress.SingleTaskAddressing.adapter.AddrAdapter;
import com.honeywell.honeywellproject.BleTaskModule.SingleAddress.SingleTaskAddressing.data.AddrBean;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.CommonBleUtil;
import com.honeywell.honeywellproject.Util.ConstantUtil;
import com.honeywell.honeywellproject.Util.DataHandler;
import com.honeywell.honeywellproject.Util.DialogUtil;
import com.honeywell.honeywellproject.Util.LogUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;
import com.honeywell.honeywellproject.WidgeView.LongClickButton;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static java.lang.Integer.parseInt;

/**
 * @author QHT
 * @since Created by QHT on 2018-01-05.
 */
public class SingleTaskAddressActivity extends ToolBarActivity {


    @BindView(R.id.iv_singleaddress_blestate2)
    ImageView       ivSingleaddressBlestate2;
    @BindView(R.id.iv_singleaddress_blerssi2)
    ImageView       ivSingleaddressBlerssi2;
    @BindView(R.id.iv_singleaddress_blebattery2)
    ImageView       ivSingleaddressBlebattery2;
    @BindView(R.id.tv_devicetype)
    TextView        tvDevicetype;
    @BindView(R.id.top1)
    LinearLayout    top1;
    @BindView(R.id.tv_protocoltype)
    TextView        tvProtocoltype;
    @BindView(R.id.top2)
    LinearLayout    top2;
    @BindView(R.id.tv_addressaddnum)
    TextView        tvAddressaddnum;
    @BindView(R.id.top3)
    LinearLayout    top3;
    @BindView(R.id.item_add1)
    TextView        itemAdd1;
    @BindView(R.id.ll_index1)
    LinearLayout    llIndex1;
    @BindView(R.id.item_add2)
    TextView        itemAdd2;
    @BindView(R.id.ll_index2)
    LinearLayout    llIndex2;
    @BindView(R.id.item_add3)
    TextView        itemAdd3;
    @BindView(R.id.ll_index3)
    LinearLayout    llIndex3;
    @BindView(R.id.ll_gray)
    View            llGray;
    @BindView(R.id.item_line2)
    View            itemLine2;
    @BindView(R.id.line_singleaddress_arrow)
    ImageView       lineSingleaddressArrow;
    @BindView(R.id.root_dialog)
    LinearLayout    rootDialog;
    @BindView(R.id.iv_top1_arrow)
    ImageView       ivTop1Arrow;
    @BindView(R.id.iv_top2_arrow)
    ImageView       ivTop2Arrow;
    @BindView(R.id.iv_top3_arrow)
    ImageView       ivTop3Arrow;
    @BindView(R.id.tv_repeat_tips)
    TextView        tvRepeatTips;
    @BindView(R.id.iv_less)
    LongClickButton ivLess;
    @BindView(R.id.ledview_clock_bg)
    TextView        ledviewClockBg;
    @BindView(R.id.ledview_clock_time)
    EditText        ledviewClockTime;
    @BindView(R.id.iv_add)
    LongClickButton ivAdd;
    @BindView(R.id.btn_singleaddr_read)
    LinearLayout    btnSingleaddrRead;
    @BindView(R.id.btn_singleaddr_write)
    LinearLayout    btnSingleaddrWrite;
    @BindView(R.id.ll_rv)
    LinearLayout    llRv;
    @BindView(R.id.rv_addr)
    RecyclerView    rvAddr;
    @BindView(R.id.btn1)
    RadioButton     btn1;
    @BindView(R.id.btn2)
    RadioButton     btn2;
    @BindView(R.id.btn3)
    RadioButton     btn3;
    @BindView(R.id.btn4)
    RadioButton     btn4;
    @BindView(R.id.btn5)
    RadioButton     btn5;
    @BindView(R.id.radiogroup)
    RadioGroup radiogroup;
    /**
     * 小三角箭头的Y坐标
     */
    int linearrow_location_Y;
    /**
     * 当前点击的类型，
     * 1 设备类型； 2 协议类型； 3 地址累加数
     */
    int currentType;
    /**
     * Notify 如果字节长度大于20.分包接收，暂存前一个包
     */
    private String beforeString;

    /**
     * 编址类型，CLIP 10X(101,102)\DLIP 20X(201,202)\FlashScan 30X
     * 且当ADDRESSTYPE==0 是代表没有任何读写操作，空闲状态
     */
    private static          int     ADDRESSTYPE            = 0;
    /**
     * 写回路卡命令
     */
    private static final    int     LOOPCARDBATTERY        = 401;
    /**
     * Notify CLIP解码完成 第1条指令
     */
    private static final    int     NOTIFYDECODE_CLIP1     = 101;
    /**
     * Notify CLIP解码完成 第2条指令
     */
    private static final    int     NOTIFYDECODE_CLIP2     = 102;
    /**
     * Notify DLIP解码完成 第1条指令
     */
    private static final    int     NOTIFYDECODE_DLIP1     = 201;
    /**
     * Notify DLIP解码完成 第2条指令
     */
    private static final    int     NOTIFYDECODE_DLIP2     = 202;
    /**
     * DLIP 读取设备类型
     */
    private static final    int     NOTIFYDECODE_DLIP3     = 203;
    /**
     * Notify FlashScan解码完成
     */
    private static final    int     NOTIFYDECODE_FLASHSCAN = 301;
    /**
     * CLIP读址超时标志
     */
    private static volatile boolean TimeOutCLip            = false;
    /**
     * 所有命令超时标志
     */
    private static volatile boolean TimeOutAll             = false;
    /**
     * CLIP读址超时时间
     */
    private static final    long    TIMEOUT_CLIPTIME       = 10000;
    /**
     * 所有命令超时时间
     */
    private static final    long    TIMEOUT_AllTIME        = 10000;
    /**
     * Clip 超时起点时间戳
     */
    private long timeClip;
    /**
     * all 命令超时起点时间戳
     */
    private long timeAll;

    /**
     * 最近的一次操作是读址成功
     */
    private boolean readingfinish;
    /**
     * 从上一个页面传递过来的，查询和保存需要
     */
    private String  logininfo_id, project_id, controller_id, fathertask_id;
    /**
     * 作为recycleview的数据list
     */
    private List<AddrBean> addrlist;
    /**
     * 记录了红色的即已经用过的地址
     */
    private List<Integer> usedAddrList = new ArrayList<>();
    /**
     * 记录地址累加值
     */
    private int                  AccumulateValue;
    private AddrAdapter          madapter;
    private SimpleDateFormat     simpleDateFormat;
    private ProgressDialog       progressdialog;
    private boolean              isAppExit;
    private Thread               LoopCardBatteryThread;
    private BleDevice            bleDevice;
    private CommonBleUtil        commonBleUtil;
    private List<SonTaskBean>    sonList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initSonList();
        initBle();
    }

    private void initSonList() {
        usedAddrList.clear();
        sonList = DataSupport.where("logininfo_id= ? and project_id = ? and controller_id= ? and fathertask_id= ?", logininfo_id, project_id, controller_id, fathertask_id).find(SonTaskBean.class);
        if (sonList.size() == 0) {
            //新建的回路，地址全可用

        } else {
            for (SonTaskBean bean : sonList) {
                usedAddrList.add(bean.getTaskdigitaladdress());
            }
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_single_address_task_newui;
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            logininfo_id = bundle.getString("logininfo_id");
            project_id = bundle.getString("project_id");
            controller_id = bundle.getString("controller_id");
            fathertask_id = bundle.getString("fathertask_id");
        }
        getToolbarTitle().setText("工程回路编址");
        getSubTitle().setVisibility(View.VISIBLE);
        getSubTitle().setText("历史记录");
        getSubTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SingleTaskAddressActivity.this, LogActivity.class));
            }
        });
        initCommonBleUtil();
        progressdialog = new ProgressDialog(this, R.style.progressDialog);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        addrlist = new ArrayList<AddrBean>();
        ledviewClockTime.addTextChangedListener(textChangeListener);
        rvAddr.postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        (int) (llRv.getWidth() / 5), (int) (llRv.getHeight() / 5));
                rvAddr.setLayoutManager(new GridLayoutManager(SingleTaskAddressActivity.this, 5, GridLayoutManager.VERTICAL, false));
                madapter = new AddrAdapter(addrlist, SingleTaskAddressActivity.this, params);
                rvAddr.addOnItemTouchListener(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        ledviewClockTime.setText(addrlist.get(position).getAddr());
                        ledviewClockTime.setSelection(ledviewClockTime.getText().length());
                    }
                });
                rvAddr.setAdapter(madapter);
                ledviewClockTime.setWidth(ledviewClockBg.getWidth());
                //对于刚跳到一个新的 界面就要弹出软键盘的情况可能由于界面为加载完全而无法弹出软键盘。此时应该适当的延迟弹出软键盘
                PhoneUtil.showInputWindow(SingleTaskAddressActivity.this, ledviewClockTime);
            }
        }, 200);

        ivLess.setLongClickRepeatListener(new LongClickButton.LongClickRepeatListener() {
            @Override
            public void repeatAction() {
                String addrLess = ledviewClockTime.getText().toString();
                if (addrLess.isEmpty()) {
                    return;
                }
//                if("3".equals(tvAddressaddnum.getText().toString())){
//                    if(Integer.parseInt(addrLess)<=3){
//                        ledviewClockTime.setText("1");
//                    }else{
//                        ledviewClockTime.setText(String.valueOf(Integer.parseInt(addrLess) - 3));
//                    }
//                }else if("2".equals(tvAddressaddnum.getText().toString())){
//                    if(Integer.parseInt(addrLess)<=2){
//                        ledviewClockTime.setText("1");
//                    }else{
//                        ledviewClockTime.setText(String.valueOf(Integer.parseInt(addrLess) - 2));
//                    }
//                }
//                else {
                if (Integer.parseInt(addrLess) <= 1) {
                    return;
                } else {
                    ledviewClockTime.setText(String.valueOf(Integer.parseInt(addrLess) - 1));
                }
//                }
                ledviewClockTime.setSelection(ledviewClockTime.getText().length());
            }
        }, 50);
        //连续加
        ivAdd.setLongClickRepeatListener(new LongClickButton.LongClickRepeatListener() {
            @Override
            public void repeatAction() {
                String addrAdd = ledviewClockTime.getText().toString();
                if (addrAdd.isEmpty() || Integer.parseInt(addrAdd) >= 239) {
                    return;
                }
//                if("3".equals(tvAddressaddnum.getText().toString())){
//                    ledviewClockTime.setText(String.valueOf(Integer.parseInt(addrAdd) + 3));
//                }else if("2".equals(tvAddressaddnum.getText().toString())){
//                    ledviewClockTime.setText(String.valueOf(Integer.parseInt(addrAdd) + 2));
//                } else {
                ledviewClockTime.setText(String.valueOf(Integer.parseInt(addrAdd) + 1));
//                }
                ledviewClockTime.setSelection(ledviewClockTime.getText().length());
            }
        }, 50);
    }

    TextWatcher textChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(final Editable editable) {
            setTips("");
            String s2 = ledviewClockTime.getText().toString();
            int strLength = s2.length();
            if(strLength<=0){
                return;
            }
            if ("CLIP".equals(tvProtocoltype.getText().toString())) {
                if (tvDevicetype.getText().toString().equals("探头")) {
                    if (Integer.parseInt(s2) > 99 || Integer.parseInt(s2) < 1) {
                        setTips("地址超出可用范围");
                    }
                } else if (tvDevicetype.getText().toString().equals("模块")) {
                    if (Integer.parseInt(s2) > 199 || Integer.parseInt(s2) < 100) {
                        setTips("地址超出可用范围");
                    }
                }else{
                    if (Integer.parseInt(s2) > 199 || Integer.parseInt(s2) < 1) {
                        setTips("地址超出可用范围");
                    }
                }
            } else if ("DLIP".equals(tvProtocoltype.getText().toString())) {
                if (Integer.parseInt(s2) > 239 || Integer.parseInt(s2) < 1) {
                    setTips("地址超出可用范围");
                }
            }else if ("FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
                if (Integer.parseInt(s2) > 1158 || Integer.parseInt(s2) < 1 ||
                        (Integer.parseInt(s2) < 1001 && Integer.parseInt(s2) > 158)) {
                    setTips("地址超出可用范围");
                }
            }
            if("DLIP".equals(tvProtocoltype.getText().toString()) ||
                    "CLIP".equals(tvProtocoltype.getText().toString())){
                int selectionStart = ledviewClockTime.getSelectionStart();
                if (strLength > 3) {
                    editable.delete(selectionStart - 1, selectionStart);
                }
            }
            ledviewClockTime.post(new Runnable() {
                @Override
                public void run() {
                    ledviewClockTime.removeTextChangedListener(textChangeListener);
                    ledviewClockTime.setText(editable);
                    ledviewClockTime.setSelection(editable.toString().length());
                    ledviewClockTime.addTextChangedListener(textChangeListener);
                }
            });
        }
    };

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

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Scan();
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
                if (progressdialog != null && !progressdialog.isShowing()) {
                    progressdialog.setMessage("蓝牙打开和设备扫描中...");
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
                        if (progressdialog != null && !progressdialog.isShowing()) {
                            progressdialog.setMessage("编址器连接中...");
                            progressdialog.show();
                        }
                    }

                    @Override
                    public void onConnectFail(BleException exception) {
                        if (progressdialog != null && progressdialog.isShowing()) {
                            progressdialog.dismiss();
                        }
                    }

                    @Override
                    public void onConnectSuccess(final BleDevice bleDevice, BluetoothGatt gatt, int status) {
                        if (progressdialog != null && progressdialog.isShowing()) {
                            progressdialog.dismiss();
                        }
                        OpenNotify(true);
                        getLoopCardBatteryThread();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ivSingleaddressBlestate2.setImageResource(R.drawable.connect_50);
                                PhoneUtil.showInputWindow(SingleTaskAddressActivity.this, ledviewClockTime);
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
                                ivSingleaddressBlestate2.setImageResource(R.drawable.unconnect_50);
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
    }

    @OnClick({R.id.top1, R.id.top2, R.id.top3, R.id.ll_index1, R.id.ll_index2, R.id.ll_index3, R.id.ll_gray, R.id.root_dialog,
            R.id.iv_less, R.id.iv_add, R.id.btn_singleaddr_read, R.id.btn_singleaddr_write,
            R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.top1:
                currentType = 1;
                ivTop1Arrow.setImageResource(R.drawable.arrow_down_red_50);
                ivTop2Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop3Arrow.setImageResource(R.drawable.arrow_up_black_50);
                lineSingleaddressArrow.setImageResource(R.drawable.arrow_line_left);
                itemAdd3.setVisibility(View.GONE);
                itemLine2.setVisibility(View.GONE);
                itemAdd1.setGravity(Gravity.LEFT);
                itemAdd2.setGravity(Gravity.LEFT);
                itemAdd1.setPadding(tvDevicetype.getLeft(), itemAdd1.getPaddingTop(), itemAdd1.getPaddingRight(), itemAdd1.getPaddingBottom());
                itemAdd2.setPadding(tvDevicetype.getLeft(), itemAdd2.getPaddingTop(), itemAdd2.getPaddingRight(), itemAdd2.getPaddingBottom());
                itemAdd3.setPadding(tvDevicetype.getLeft(), itemAdd3.getPaddingTop(), itemAdd3.getPaddingRight(), itemAdd3.getPaddingBottom());
                itemAdd1.setText("模块");
                itemAdd2.setText("探头");
                if (itemAdd1.getText().toString().equals(tvDevicetype.getText().toString())) {
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                } else if (itemAdd2.getText().toString().equals(tvDevicetype.getText().toString())) {
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                } else {
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }
                setDialogMargin();
                break;
            case R.id.top2:
                currentType = 2;
                ivTop1Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop2Arrow.setImageResource(R.drawable.arrow_down_red_50);
                ivTop3Arrow.setImageResource(R.drawable.arrow_up_black_50);
                lineSingleaddressArrow.setImageResource(R.drawable.arrow_line_center);
                itemAdd3.setVisibility(View.VISIBLE);
                itemLine2.setVisibility(View.VISIBLE);
                itemAdd1.setGravity(Gravity.CENTER);
                itemAdd2.setGravity(Gravity.CENTER);
                itemAdd3.setGravity(Gravity.CENTER);
                itemAdd1.setPadding(0, itemAdd1.getPaddingTop(), itemAdd1.getPaddingRight(), itemAdd1.getPaddingBottom());
                itemAdd2.setPadding(0, itemAdd2.getPaddingTop(), itemAdd2.getPaddingRight(), itemAdd2.getPaddingBottom());
                itemAdd3.setPadding(0, itemAdd3.getPaddingTop(), itemAdd3.getPaddingRight(), itemAdd3.getPaddingBottom());
                itemAdd1.setText("CLIP");
                itemAdd2.setText("DLIP");
                itemAdd3.setText("FLASHSCAN");
                if (itemAdd1.getText().toString().equals(tvProtocoltype.getText().toString())) {
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                } else if (itemAdd2.getText().toString().equals(tvProtocoltype.getText().toString())) {
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                } else if (itemAdd3.getText().toString().equals(tvProtocoltype.getText().toString())) {
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                } else {
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }
                setDialogMargin();
                break;
            case R.id.top3:
                currentType = 3;
                ivTop1Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop2Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop3Arrow.setImageResource(R.drawable.arrow_down_red_50);
                lineSingleaddressArrow.setImageResource(R.drawable.arrow_line_right);
                itemAdd3.setVisibility(View.VISIBLE);
                itemLine2.setVisibility(View.VISIBLE);
                itemAdd1.setGravity(Gravity.LEFT);
                itemAdd2.setGravity(Gravity.LEFT);
                itemAdd3.setGravity(Gravity.LEFT);
                int[] location = new int[2];
                tvAddressaddnum.getLocationInWindow(location);
                itemAdd1.setPadding((int) location[0], itemAdd1.getPaddingTop(), 0, itemAdd1.getPaddingBottom());
                itemAdd2.setPadding((int) location[0], itemAdd2.getPaddingTop(), 0, itemAdd2.getPaddingBottom());
                itemAdd3.setPadding((int) location[0], itemAdd3.getPaddingTop(), 0, itemAdd3.getPaddingBottom());
                itemAdd1.setText("0");
                itemAdd2.setText("1");
                itemAdd3.setText("2");
                if (itemAdd1.getText().toString().equals(tvAddressaddnum.getText().toString())) {
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                } else if (itemAdd2.getText().toString().equals(tvAddressaddnum.getText().toString())) {
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                } else if (itemAdd3.getText().toString().equals(tvAddressaddnum.getText().toString())) {
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                } else {
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }
                setDialogMargin();
                break;
            case R.id.ll_index1:  //第一行（模块）
                setTips("");
                String addrM=ledviewClockTime.getText().toString();
                if (currentType == 1) { //设备类型（top1）
                    tvDevicetype.setText(itemAdd1.getText());
                    if ("CLIP".equals(tvProtocoltype.getText().toString())) {
                        refreshData(3);
                        if(!TextUtil.isEmpty(addrM) && (Integer.parseInt(addrM)<100 ||Integer.parseInt(addrM)>199)){
                            setTips("地址超出可用范围");
                        }
                    } else if ("DLIP".equals(tvProtocoltype.getText().toString())) {
                        refreshData(1);
                        if(!TextUtil.isEmpty(addrM) && (Integer.parseInt(addrM)>239 ||Integer.parseInt(addrM)<1)){
                            setTips("地址超出可用范围");
                        }
                    }else if("FLASHSCAN".equals(tvProtocoltype.getText().toString())){
                        String addr=ledviewClockTime.getText().toString();
                        if(!TextUtil.isEmpty(addr)){
                            if (Integer.parseInt(addr) > 1158 || Integer.parseInt(addr) < 1 ||
                                    (Integer.parseInt(addr) < 1001 && Integer.parseInt(addr) > 158)) {
                                setTips("地址超出可用范围");
                            }
                        }
                    }
                } else if (currentType == 2) {//协议类型（CLIP）top2
                    tvProtocoltype.setText(itemAdd1.getText());
                    if ("模块".equals(tvDevicetype.getText().toString())) {
                        refreshData(3);
                        if(!TextUtil.isEmpty(addrM) && (Integer.parseInt(addrM)<100 ||Integer.parseInt(addrM)>199)){
                            setTips("地址超出可用范围");
                        }
                    } else if ("探头".equals(tvDevicetype.getText().toString())) {
                        refreshData(1);
                        if(!TextUtil.isEmpty(addrM) && (Integer.parseInt(addrM)<1 ||Integer.parseInt(addrM)>99)){
                            setTips("地址超出可用范围");
                        }
                    }
                    String clip = ledviewClockTime.getText().toString();
                    if (clip.length() == 4) {
                        clip = clip.substring(0, 3);
                        ledviewClockTime.setText(clip);
                    }
                } else if (currentType == 3) {
                    tvAddressaddnum.setText(itemAdd1.getText());
                    AccumulateValue = Integer.parseInt(itemAdd1.getText().toString());
                }
                rootDialog.setVisibility(View.GONE);
                lineSingleaddressArrow.setImageResource(R.drawable.arrow_line);
                currentType = 0;
                restoreArrow();
                break;
            case R.id.ll_index2:
                setTips("");
                String addrT=ledviewClockTime.getText().toString();
                if (currentType == 1) {
                    tvDevicetype.setText(itemAdd2.getText());
                    if ("CLIP".equals(tvProtocoltype.getText().toString())) {
                        refreshData(1);
                        if(!TextUtil.isEmpty(addrT) && (Integer.parseInt(addrT)>99 ||Integer.parseInt(addrT)<1)){
                            setTips("地址超出可用范围");
                        }
                    } else if ("DLIP".equals(tvProtocoltype.getText().toString())) {
                        refreshData(1);
                        if(!TextUtil.isEmpty(addrT) && (Integer.parseInt(addrT)>239 ||Integer.parseInt(addrT)<1)){
                            setTips("地址超出可用范围");
                        }
                    }else if("FLASHSCAN".equals(tvProtocoltype.getText().toString())){
                        String addr=ledviewClockTime.getText().toString();
                        if(!TextUtil.isEmpty(addr)){
                            if (Integer.parseInt(addr) > 1158 || Integer.parseInt(addr) < 1 ||
                                    (Integer.parseInt(addr) < 1001 && Integer.parseInt(addr) > 158)) {
                                setTips("地址超出可用范围");
                            }
                        }
                    }
                } else if (currentType == 2) {
                    tvProtocoltype.setText(itemAdd2.getText());
                    if ("模块".equals(tvDevicetype.getText().toString())) {
                        refreshData(1);
                        if(!TextUtil.isEmpty(addrT) && (Integer.parseInt(addrT)<1 ||Integer.parseInt(addrT)>239)){
                            setTips("地址超出可用范围");
                        }
                    } else if ("探头".equals(tvDevicetype.getText().toString())) {
                        refreshData(1);
                        if(!TextUtil.isEmpty(addrT) && (Integer.parseInt(addrT)<1 ||Integer.parseInt(addrT)>239)){
                            setTips("地址超出可用范围");
                        }
                    }
                    String clip = ledviewClockTime.getText().toString();
                    if (clip.length() == 4) {
                        clip = clip.substring(0, 3);
                        ledviewClockTime.setText(clip);
                    }
                } else if (currentType == 3) {
                    tvAddressaddnum.setText(itemAdd2.getText());
                    AccumulateValue = Integer.parseInt(itemAdd2.getText().toString());
                }
                rootDialog.setVisibility(View.GONE);
                lineSingleaddressArrow.setImageResource(R.drawable.arrow_line);
                currentType = 0;
                restoreArrow();
                break;
            case R.id.ll_index3:
                setTips("");
                if (currentType == 1) {
                    tvDevicetype.setText(itemAdd3.getText());
                } else if (currentType == 2) {
                    tvProtocoltype.setText(itemAdd3.getText());
                    String addr=ledviewClockTime.getText().toString();
                    if(!TextUtil.isEmpty(addr)){
                        if (Integer.parseInt(addr) > 1158 || Integer.parseInt(addr) < 1 ||
                                (Integer.parseInt(addr) < 1001 && Integer.parseInt(addr) > 158)) {
                            setTips("地址超出可用范围");
                        }
                    }
                } else if (currentType == 3) {
                    tvAddressaddnum.setText(itemAdd3.getText());
                    AccumulateValue = Integer.parseInt(itemAdd3.getText().toString());
                }
                rootDialog.setVisibility(View.GONE);
                lineSingleaddressArrow.setImageResource(R.drawable.arrow_line);
                currentType = 0;
                restoreArrow();
                break;
            case R.id.ll_gray:
                rootDialog.setVisibility(View.GONE);
                lineSingleaddressArrow.setImageResource(R.drawable.arrow_line);
                currentType = 0;
                restoreArrow();
                break;
            case R.id.root_dialog:
                break;
            case R.id.iv_less:
                String addrless = ledviewClockTime.getText().toString();
                if (TextUtils.isEmpty(addrless)) {
                    return;
                }
                int less = parseInt(addrless);
                int lessNext = 0;
//                if("3".equals(tvAddressaddnum.getText().toString())){
//                    if ( less > 3) {
//                        lessNext = less - 3;
//                    }else{
//                        return;
//                    }
//                }else if("2".equals(tvAddressaddnum.getText().toString())){
//                    if ( less > 2) {
//                        lessNext = less - 2;
//                    }else{
//                        return;
//                    }
//                }
//                else {
                if (less > 1) {
                    lessNext = less - 1;
                } else {
                    return;
                }
//                }
                ledviewClockTime.setText(String.valueOf(lessNext));
                ledviewClockTime.setSelection(ledviewClockTime.getText().length());
                break;
            case R.id.iv_add:
                String addradd = ledviewClockTime.getText().toString();
                if (TextUtils.isEmpty(addradd) || Integer.parseInt(addradd) >= 239) {
                    return;
                }
                int add = parseInt(addradd);
                int addNext = 0;
//                if("3".equals(tvAddressaddnum.getText().toString())){
//                    addNext = add + 3;
//                }else if("2".equals(tvAddressaddnum.getText().toString())){
//                    addNext = add + 2;
//                } else{
                addNext = add + 1;
//                }
                ledviewClockTime.setText(String.valueOf(addNext));
                ledviewClockTime.setSelection(ledviewClockTime.getText().length());
                break;
            case R.id.btn_singleaddr_read:
                //在每次点击按钮时清空上次tips内容
                setTips("");
                //读址
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    //  setTips("编址器未连接，点击顶部红色区域重连");
                    return;
                }
                readingfinish = false;
                initRead();
                break;
            case R.id.btn_singleaddr_write:
                //在每次点击按钮时清空上次tips内容
                setTips("");
                //编址
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    // setTips("编址器未连接，点击顶部红色区域重连");
                    return;
                }
                readingfinish = false;
                initWrite();
                break;
            case R.id.btn1:
                //在每次点击按钮时清空上次tips内容
                setTips("");
                if (!"模块".equals(tvDevicetype.getText().toString()) &&
                        !"探头".equals(tvDevicetype.getText().toString())) {
                    setTips("请选择设备类型");
                    return;
                }
                if (!"CLIP".equals(tvProtocoltype.getText().toString()) &&
                        !"DLIP".equals(tvProtocoltype.getText().toString()) &&
                        !"FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
                    setTips("请选择协议类型");
                    return;
                }
                refreshData(1);
                break;
            case R.id.btn2:
                //在每次点击按钮时清空上次tips内容
                setTips("");
                if (!"模块".equals(tvDevicetype.getText().toString()) &&
                        !"探头".equals(tvDevicetype.getText().toString())) {
                    setTips("请选择设备类型");
                    return;
                }
                if (!"CLIP".equals(tvProtocoltype.getText().toString()) &&
                        !"DLIP".equals(tvProtocoltype.getText().toString()) &&
                        !"FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
                    setTips("请选择协议类型");
                    return;
                }
                refreshData(2);
                break;
            case R.id.btn3:
                //在每次点击按钮时清空上次tips内容
                setTips("");
                if (!"模块".equals(tvDevicetype.getText().toString()) &&
                        !"探头".equals(tvDevicetype.getText().toString())) {
                    setTips("请选择设备类型");
                    return;
                }
                if (!"CLIP".equals(tvProtocoltype.getText().toString()) &&
                        !"DLIP".equals(tvProtocoltype.getText().toString()) &&
                        !"FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
                    setTips("请选择协议类型");
                    return;
                }
                refreshData(3);
                break;
            case R.id.btn4:
                //在每次点击按钮时清空上次tips内容
                setTips("");
                if (!"模块".equals(tvDevicetype.getText().toString()) &&
                        !"探头".equals(tvDevicetype.getText().toString())) {
                    setTips("请选择设备类型");
                    return;
                }
                if (!"CLIP".equals(tvProtocoltype.getText().toString()) &&
                        !"DLIP".equals(tvProtocoltype.getText().toString()) &&
                        !"FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
                    setTips("请选择协议类型");
                    return;
                }
                refreshData(4);
                break;
            case R.id.btn5:
                //在每次点击按钮时清空上次tips内容
                setTips("");
                if (!"模块".equals(tvDevicetype.getText().toString()) &&
                        !"探头".equals(tvDevicetype.getText().toString())) {
                    setTips("请选择设备类型");
                    return;
                }
                if (!"CLIP".equals(tvProtocoltype.getText().toString()) &&
                        !"DLIP".equals(tvProtocoltype.getText().toString()) &&
                        !"FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
                    setTips("请选择协议类型");
                    return;
                }
                refreshData(5);
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
                LogUtil.e("Write--->" + result);
            }

            @Override
            public void notifyOnResult(boolean result) {
                LogUtil.e("Notify--->" + result);
            }

            @Override
            public void notifyOnSuccess(String values, String UUID) {
                TimeOutAll = false;
                boolean result = false;
                //数据长度大于20字节且不是以0x55 0x5A结尾则分包接收了，下一个包为剩下的数据
                if (values.length() == 40 && !"555A".equals(values.substring(36, 50))) {
                    beforeString = values;
                    return;
                }
                //数据不是以55 AA开头，则为剩下的数据
                if ("55AA".equals(values.substring(0, 4))) {
                    values = beforeString + values;
                    beforeString = null;
                }
                if (ADDRESSTYPE == 101) {
                    result = DataHandler.CLIP_READ(values, 1);
                    if (result) {
                        handler.sendEmptyMessage(NOTIFYDECODE_CLIP1);
                    } else {
                        ADDRESSTYPE = 0;
                        setTips("写地址"+ledviewClockTime.getText().toString()+"失败");
                        String detail = simpleDateFormat.format(new Date()) + "  "
                                + "CLIP写地址" + ledviewClockTime.getText().toString() + "失败" + "；\n";
                        saveLog("write", ledviewClockTime.getText().toString(), "CLIP", "失败", detail);
                    }
                } else if (ADDRESSTYPE == 102) {
                    //收到消息，则未超时，正常执行解码操作
                    TimeOutCLip = false;
                    result = DataHandler.CLIP_READ(values, 2);
                    if (!result) {
                        //  setTips("CLIP验证命令失败");
                    }
                    Message msg = handler.obtainMessage();
                    msg.what = NOTIFYDECODE_CLIP2;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", result);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    if (progressdialog != null && progressdialog.isShowing()) {
                        progressdialog.dismiss();
                    }
                } else if (ADDRESSTYPE == 201) {
                    result = DataHandler.DLIP_READ(values, 1);
                    if (result) {
                        handler.sendEmptyMessage(NOTIFYDECODE_DLIP1);
                    } else {
                        ADDRESSTYPE = 0;
                        setTips("写地址"+ledviewClockTime.getText().toString()+"失败");
                        String detail = simpleDateFormat.format(new Date()) + "  "
                                + "DLIP写地址" + ledviewClockTime.getText().toString() + "失败" + "；\n";
                        saveLog("write", ledviewClockTime.getText().toString(), "DLIP", "失败", detail);
                    }
                } else if (ADDRESSTYPE == 202) {
                    result = DataHandler.DLIP_READ(values, 2);
                    if (!result) {
                        //   setTips("DLIP验证命令失败");
                    }
                    Message msg = handler.obtainMessage();
                    msg.what = NOTIFYDECODE_DLIP2;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", result);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } else if (ADDRESSTYPE == 203) {
                    Message msg = handler.obtainMessage();
                    msg.what = NOTIFYDECODE_DLIP3;
                    Bundle bundle = new Bundle();
                    String results = DataHandler.DLIP_READDeviceType(values);
                    bundle.putString("results", results);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    if (progressdialog != null && progressdialog.isShowing()) {
                        progressdialog.dismiss();
                    }
                } else if (ADDRESSTYPE == 301) {

                } else if (ADDRESSTYPE == 401) {
                    setBatteryIcon(DataHandler.LOOPCARDBattery_READ(values));
                }
            }
        });
    }

    /**
     * 开始编址
     */
    private void initWrite() {
        if (!"模块".equals(tvDevicetype.getText().toString()) &&
                !"探头".equals(tvDevicetype.getText().toString())) {
            setTips("请选择设备类型");
            return;
        }
        if (!"CLIP".equals(tvProtocoltype.getText().toString()) &&
                !"DLIP".equals(tvProtocoltype.getText().toString()) &&
                !"FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
            setTips("请选择协议类型");
            return;
        }
        String addrstr = ledviewClockTime.getText().toString();
        if (TextUtils.isEmpty(addrstr)) {
            // setTips("地址不能为空");
            return;
        }
        if ("CLIP".equals(tvProtocoltype.getText().toString())) {
            ADDRESSTYPE = 101;
            Writing("CLIP", ADDRESSTYPE, 1);
        } else if ("DLIP".equals(tvProtocoltype.getText().toString())) {
            ADDRESSTYPE = 201;
            Writing("DLIP", ADDRESSTYPE, 1);
        } else if ("FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
            ADDRESSTYPE = 301;
            Writing("FLASHSCAN", ADDRESSTYPE, 1);
        }
    }

    /**
     * 开始编址
     */
    private void Writing(String protocolType, int type, int index) {
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ("LOOPCARD".equals(protocolType)) {
            //获取回路卡电池电量
            ADDRESSTYPE = type;
            commonBleUtil.writeDevice(bleDevice, DataHandler.LOOPCARDBattery_WRITE());
            return;
        }
        String addrstr = ledviewClockTime.getText().toString();
//        OpenNotify(true);
        String command = null;
        if ("CLIP".equals(protocolType)) {
            if (tvDevicetype.getText().toString().equals("探头")) {
                if (Integer.parseInt(addrstr) > 99 || Integer.parseInt(addrstr) < 1) {
                    //  setTips("CLIP 探头地址范围应为1-99");
                    setTips("地址超出可用范围");
                    return;
                }
            } else if (tvDevicetype.getText().toString().equals("模块")) {
                if (Integer.parseInt(addrstr) > 199 || Integer.parseInt(addrstr) < 100) {
                    // setTips("CLIP 探头地址范围应为100-199");
                    setTips("地址超出可用范围");
                    return;
                }
            }
            //CLIP,第index条命令
            command = DataHandler.CLIP_WRITE(addrstr, index, false);
            if (TextUtils.isEmpty(command)) {
                //   setTips("CLIP地址超过199，不支持");
                setTips("地址超出可用范围");
                return;
            }
            // 如果是第一条写并且被使用过的地址，强制写，而第二条就不用弹出了，未使用过也不用弹出
                if (index==1 && usedAddrList.contains(Integer.parseInt(addrstr))) {
                    //地址被使用过了
                    writeEnforce(type, command);
                } else {
                    writefinal(type, command);
                }
        } else if ("DLIP".equals(protocolType)) {
            if (Integer.parseInt(addrstr) > 239 || Integer.parseInt(addrstr) < 1) {
                // setTips("DLIP 地址范围应为1-239");
                setTips("地址超出可用范围");
                return;
            }
            //DLIP,第index条命令
            if (index == 1) {
                DataHandler.DLIP_ADDR_NEW = DataHandler.Alone2Hex(Integer.toHexString(parseInt(addrstr)));
            }
            command = DataHandler.DLIP_WRITE("00", addrstr, index);
            if (TextUtils.isEmpty(command)) {
                //  setTips("DLIP地址超过239，不支持");
                setTips("地址超出可用范围");
                return;
            }
            // 如果是第一条写并且被使用过的地址，强制写，而第二条就不用弹出了，未使用过也不用弹出
            if (index==1 && usedAddrList.contains(Integer.parseInt(addrstr))) {
                //地址被使用过了
                writeEnforce(type, command);
            } else {
                writefinal(type, command);
            }
        } else if ("FLASHSCAN".equals(protocolType)) {
            //FlashScan,第index条命令
        }
    }

    /**
     * 地址重复了，是否强制写
     */
    public void writeEnforce(final int type, final String command) {
        DialogUtil.showAlertDialog(SingleTaskAddressActivity.this, "提示", "此地址已被使用，是否还要继续编写？",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                writefinal(type, command);
                dialogInterface.dismiss();
            }
        }, null);
    }

    /**
     * 最终的写命令
     */
    public void writefinal(int type, String command) {
        ADDRESSTYPE = type;
        startCommandTimeOut("WRITE");
        commonBleUtil.writeDevice(bleDevice, command);
    }

    /**
     * 单独读址
     */
    private void initRead() {
        if (!"CLIP".equals(tvProtocoltype.getText().toString()) &&
                !"DLIP".equals(tvProtocoltype.getText().toString()) &&
                !"FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
            setTips("请选择协议类型");
            return;
        }
//        OpenNotify(false);
//        try {
//            //必须延迟100ms以上，太短容易导致Write失败
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if ("CLIP".equals(tvProtocoltype.getText().toString())) {
            Reading("CLIP");
        } else if ("DLIP".equals(tvProtocoltype.getText().toString())) {
            Reading("DLIP");
        } else if ("FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
            Reading("FLASHSCAN");
        }
    }

    /**
     * 单独读址
     * <p/>
     * CLIP读取地址（轮询1～199）:
     * 55 AA 01 80 FE 00 05 7A 55 5A
     * 回复:
     * 55 AA 01 80 00 10 91 55 5A  //10表示地址16
     * 55 AA 01 80 00 63 E2 55 5A  //63表示地址99
     */
    private void Reading(String protocolType) {
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String command = null;
        DataHandler.singleReading = true;
        if ("CLIP".equals(protocolType)) {
            //1-199循环读，哪个匹配上就是哪个。Suck协议，还有这种操作？
            //12-07终于把这傻逼操作改了，能好点了
            //开始记录超时时间
            TimeOutCLip = true;
            timeClip = System.currentTimeMillis();
//            startClipTimeOut();
            ADDRESSTYPE = 102;
            startCommandTimeOut("READ");
            //00 无意义
            command = DataHandler.CLIP_WRITE("00", 2, true);
            commonBleUtil.writeDevice(bleDevice, command);
        } else if ("DLIP".equals(protocolType)) {
            ADDRESSTYPE = 202;
            startCommandTimeOut("READ");
            //"FF"  ==  255 十进制
            command = DataHandler.DLIP_WRITE("00", "255", 2);
            commonBleUtil.writeDevice(bleDevice, command);
        } else if ("DLIPTYPE".equals(protocolType)) {
            //读取设备类型命令
            ADDRESSTYPE = 203;
            //"FF"  ==  255 十进制
            command = DataHandler.DLIP_WRITE("00", "00", 3);
            commonBleUtil.writeDevice(bleDevice, command);

        } else if ("FLASHSCAN".equals(protocolType)) {
            //FlashScan,第index条命令
        }
    }

    /**
     * CLIP轮询
     * 单元地址返回读取成功的1～199之间的地址值；
     * 轮询结果，地址越小返回越快（用地址99测试需要3s，地址199预计在8s内）
     */
//    private void startClipTimeOut() {
//        Runnable mRunable = new Runnable() {
//            @Override
//            public void run() {
//                if (System.currentTimeMillis() - timeClip > TIMEOUT_CLIPTIME) {
//                    //如果Clip超时则去FLASHSCAN读址
//                    if (progressdialog != null && progressdialog.isShowing()) {
//                        progressdialog.dismiss();
//                    }
////                    handler.sendEmptyMessage(NOTIFYDECODE_FLASHSCAN);
//                    handler.removeCallbacks(this);
//                } else {
//                    if (!TimeOutCLip) {
//                        if (progressdialog != null && progressdialog.isShowing()) {
//                            progressdialog.dismiss();
//                        }
//                        handler.removeCallbacks(this);
//                    } else {
//                        handler.postDelayed(this, 100);
//                    }
//                }
//            }
//        };
//        handler.post(mRunable);
//    }
    private void writtingSuccess() {
        setTips("写地址"+ledviewClockTime.getText().toString()+"成功");
        final String successAddr = ledviewClockTime.getText().toString();
        buildSonTask(successAddr);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ledviewClockTime.setText(String.valueOf(Integer.parseInt(successAddr) + AccumulateValue));
                ledviewClockTime.setSelection(ledviewClockTime.getText().length());
            }
        }, 1000);
    }

    /**
     * 新建子任务
     */
    private void buildSonTask(String successAddr) {
        SonTaskBean sb = new SonTaskBean();
        if (sonList.size() == 0 || sonList.isEmpty()) {
            sb.setTasknumber(1);
        } else {
            sb.setTasknumber(sonList.get(sonList.size() - 1).getTasknumber() + 1);
        }
        sb.setTaskdigitaladdress(Integer.parseInt(successAddr));
        sb.setLogininfo_id(logininfo_id);
        sb.setProject_id(project_id);
        sb.setController_id(controller_id);
        sb.setFathertask_id(fathertask_id);
        sb.setProcess(true);
        sb.setAddressingtype(tvDevicetype.getText().toString());
        sb.setTaskdate(simpleDateFormat.format(new Date()));
        sb.save();
        initSonList();
        if (radiogroup.getCheckedRadioButtonId()==R.id.btn1) {
            refreshData(1);
        }else if (radiogroup.getCheckedRadioButtonId()==R.id.btn2) {
            refreshData(2);
        }else if (radiogroup.getCheckedRadioButtonId()==R.id.btn3) {
            refreshData(3);
        }else if (radiogroup.getCheckedRadioButtonId()==R.id.btn4) {
            refreshData(4);
        }else if (radiogroup.getCheckedRadioButtonId()==R.id.btn5) {
            refreshData(5);
        }
    }

    private void readingSuccess() {
        setTips("读地址成功");
        String show = parseInt(DataHandler.DLIP_ADDR_NEW, 16) + "";
//        if (show.length() == 1) {
//            show = "00" + show;
//        } else if (show.length() == 2) {
//            show = "0" + show;
//        }
        ledviewClockTime.setText(show);
        ledviewClockTime.setSelection(ledviewClockTime.getText().length());
        readingfinish = true;
    }

    /**
     * 针对所有命令的超时动作，只要下发，如果10s未收到结果则超时，提示失败
     *
     * @param CommandType 命令类型，读或写
     */
    private void startCommandTimeOut(final String CommandType) {
        TimeOutAll = true;
        timeAll = System.currentTimeMillis();
        if (progressdialog != null && !progressdialog.isShowing()) {
            if ("READ".equals(CommandType)) {
                progressdialog.setMessage("读址中...");
            } else {
                progressdialog.setMessage("编址中...");
            }
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.show();
        }
        Runnable mRunable = new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - timeAll > TIMEOUT_AllTIME) {
                    if (progressdialog != null && progressdialog.isShowing()) {
                        progressdialog.dismiss();
                    }
                    if ("READ".equals(CommandType)) {
                        setTips("读地址失败");
                        String detail = simpleDateFormat.format(new Date()) + "  "
                                + tvProtocoltype.getText().toString()
                                + "读取地址超时" + "；\n";
                        saveLog("read", "", tvProtocoltype.getText().toString(), "超时", detail);
                    } else {
                        setTips("写地址"+ledviewClockTime.getText().toString()+"失败");
                        String detail = simpleDateFormat.format(new Date()) + "  "
                                + tvProtocoltype.getText().toString()
                                + "写地址" + ledviewClockTime.getText().toString() + "超时" + "；\n";
                        saveLog("write", ledviewClockTime.getText().toString(), tvProtocoltype.getText().toString(), "超时", detail);
                    }
                    handler.removeCallbacks(this);
                    ADDRESSTYPE = 0;
                } else {
                    if (!TimeOutAll) {
                        if (progressdialog != null && progressdialog.isShowing()) {
                            progressdialog.dismiss();
                        }
                        handler.removeCallbacks(this);
                    } else {
                        handler.postDelayed(this, 100);
                    }
                }
            }
        };
        handler.post(mRunable);
    }

    private void restoreArrow() {
        ivTop1Arrow.setImageResource(R.drawable.arrow_up_black_50);
        ivTop2Arrow.setImageResource(R.drawable.arrow_up_black_50);
        ivTop3Arrow.setImageResource(R.drawable.arrow_up_black_50);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NOTIFYDECODE_CLIP1:
                    Writing("CLIP", 102, 2);
                    break;
                case NOTIFYDECODE_CLIP2:
                    ADDRESSTYPE = 0;
                    boolean result = msg.getData().getBoolean("result");
                    //如果是单独读，则失败循环，成功返回
                    if (DataHandler.singleReading) {
                        DataHandler.singleReading = false;
                        if (!result) {
                            String detail = simpleDateFormat.format(new Date()) + "  "
                                    + "CLIP读取地址失败" + "；\n";
                            setTips("读地址失败");
                            saveLog("read", ledviewClockTime.getText().toString(), "CLIP", "失败", detail);
                        } else {
                            readingSuccess();
                            String detail = simpleDateFormat.format(new Date()) + "  "
                                    + "CLIP读取地址" + ledviewClockTime.getText().toString() + "成功" + "；\n";
                            saveLog("read", ledviewClockTime.getText().toString(), "CLIP", "成功", detail);

                        }
                    } else {
                        if (!result) {
                            setTips("写地址"+ledviewClockTime.getText().toString()+"失败");
                            String detail = simpleDateFormat.format(new Date()) + "  "
                                    + "CLIP写地址" + ledviewClockTime.getText().toString() + "失败" + "；\n";
                            saveLog("write", ledviewClockTime.getText().toString(), "CLIP", "失败", detail);
                        } else {
                            writtingSuccess();
                            String detail = simpleDateFormat.format(new Date()) + "  "
                                    + "CLIP写地址" + ledviewClockTime.getText().toString() + "成功" + "；\n";
                            saveLog("write", ledviewClockTime.getText().toString(), "CLIP", "成功", detail);
                        }
                    }
                    break;
                case NOTIFYDECODE_DLIP1:
                    Writing("DLIP", 202, 2);
                    break;
                case NOTIFYDECODE_DLIP2:
                    ADDRESSTYPE = 0;
                    boolean result2 = msg.getData().getBoolean("result");
                    if (DataHandler.singleReading) {
                        DataHandler.singleReading = false;
                        if (!result2) {
                            setTips("读地址失败");
                            String detail = simpleDateFormat.format(new Date()) + "  "
                                    + "DLIP读取地址失败" + "；\n";
                            saveLog("read", ledviewClockTime.getText().toString(), "DLIP", "失败", detail);
                        } else {
                            readingSuccess();
                            String detail = simpleDateFormat.format(new Date()) + "  "
                                    + "DLIP读取地址" + ledviewClockTime.getText().toString() + "成功" + "；\n";
                            saveLog("read", ledviewClockTime.getText().toString(), "DLIP", "成功", detail);
                            //DLIP读完地址后，再紧接着下发一条读取类型命令
                            Reading("DLIPTYPE");
                        }
                    } else {
                        if (!result2) {
                            setTips("写地址"+ledviewClockTime.getText().toString()+"失败");
                            String detail = simpleDateFormat.format(new Date()) + "  "
                                    + "DLIP写地址" + ledviewClockTime.getText().toString() + "失败" + "；\n";
                            saveLog("write", ledviewClockTime.getText().toString(), "DLIP", "失败", detail);
                        } else {
                            writtingSuccess();
                            String detail = simpleDateFormat.format(new Date()) + "  "
                                    + "DLIP写地址" + ledviewClockTime.getText().toString() + "成功" + "；\n";
                            saveLog("write", ledviewClockTime.getText().toString(), "DLIP", "成功", detail);
                        }
                    }
                    break;
                case NOTIFYDECODE_DLIP3:
                    ADDRESSTYPE = 0;
                    String results = msg.getData().getString("results");
                    if (DataHandler.singleReading) {
                        DataHandler.singleReading = false;
                    }
                    if ("T".equals(results)) {
                        tvDevicetype.setText("探头");
                        String detail = simpleDateFormat.format(new Date()) + "  "
                                + "DLIP读取类型成功，类型为探头" + "；\n";
                        saveLog("read", ledviewClockTime.getText().toString(), "DLIP", "探头", detail);
                    } else if ("M".equals(results)) {
                        tvDevicetype.setText("模块");
                        String detail = simpleDateFormat.format(new Date()) + "  "
                                + "DLIP读取类型成功，类型为模块" + "；\n";
                        saveLog("read", ledviewClockTime.getText().toString(), "DLIP", "模块", detail);
                    } else if ("N".equals(results)) {
                        //未定义
                        String detail = simpleDateFormat.format(new Date()) + "  "
                                + "DLIP读取类型失败，类型未定义" + "；\n";
                        saveLog("read", ledviewClockTime.getText().toString(), "DLIP", "未定义", detail);
                    } else {
                        String detail = simpleDateFormat.format(new Date()) + "  "
                                + "DLIP读设备类型失败" + "；\n";
                        saveLog("read", ledviewClockTime.getText().toString(), "DLIP", "失败", detail);
                    }
                    break;
                case NOTIFYDECODE_FLASHSCAN:
                    DataHandler.singleReading = false;
                    ToastUtil.showToastShort("独立编址所有协议流程执行完");
                    break;
                case LOOPCARDBATTERY:
                    Writing("LOOPCARD", 401, 0);
                    break;
                default:
            }
        }
    };

    /**
     * @param index 1-5哪个按钮
     */
    private void refreshData(int index) {

        switch (index) {
            case 1:
                btn1.toggle();
                addrlist.clear();
                for (int i = 1; i <= 50; i++) {
                    AddrBean bean = new AddrBean();
                    bean.setAddr(i + "");
                    if (usedAddrList.contains(i)) {
                        bean.setUsed(true);
                    }
                    addrlist.add(bean);
                }
                if ("CLIP".equals(tvProtocoltype.getText().toString()) &&
                        "模块".equals(tvDevicetype.getText().toString())) {
                    addrlist.clear();
                }
                break;
            case 2:
                btn2.toggle();
                addrlist.clear();
                for (int i = 51; i <= 100; i++) {
                    AddrBean bean = new AddrBean();
                    bean.setAddr(i + "");
                    if (usedAddrList.contains(i)) {
                        bean.setUsed(true);
                    }
                    addrlist.add(bean);
                }
                if ("CLIP".equals(tvProtocoltype.getText().toString()) &&
                        "模块".equals(tvDevicetype.getText().toString())) {
                    addrlist.clear();
                }
                break;
            case 3:
                btn3.toggle();
                addrlist.clear();
                for (int i = 101; i <= 150; i++) {
                    AddrBean bean = new AddrBean();
                    bean.setAddr(i + "");
                    if (usedAddrList.contains(i)) {
                        bean.setUsed(true);
                    }
                    addrlist.add(bean);
                }
                if ("CLIP".equals(tvProtocoltype.getText().toString()) &&
                        "探头".equals(tvDevicetype.getText().toString())) {
                    addrlist.clear();
                }
                break;
            case 4:
                btn4.toggle();
                addrlist.clear();
                for (int i = 151; i <= 200; i++) {
                    AddrBean bean = new AddrBean();
                    bean.setAddr(i + "");
                    if (usedAddrList.contains(i)) {
                        bean.setUsed(true);
                    }
                    addrlist.add(bean);
                }
                if ("CLIP".equals(tvProtocoltype.getText().toString()) &&
                        "探头".equals(tvDevicetype.getText().toString())) {
                    addrlist.clear();
                }
                break;
            case 5:
                btn5.toggle();
                addrlist.clear();
                for (int i = 201; i <= 239; i++) {
                    AddrBean bean = new AddrBean();
                    bean.setAddr(i + "");
                    if (usedAddrList.contains(i)) {
                        bean.setUsed(true);
                    }
                    addrlist.add(bean);
                }
                if ("CLIP".equals(tvProtocoltype.getText().toString())) {
                    addrlist.clear();
                }
                break;
            default:
        }
        if (madapter != null) {
            madapter.notifyDataSetChanged();
        }
    }

    private void setDialogMargin() {
        rootDialog.post(new Runnable() {
            @Override
            public void run() {
                //如果直接设置LayoutParams的话，此时line还是线条，没有小三角，所以显示不出来，得延迟一下
                final int[] location = new int[2];
                lineSingleaddressArrow.getLocationInWindow(location);
                linearrow_location_Y = location[1];
                rootDialog.getLayoutParams();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rootDialog.getLayoutParams();
                lp.width = PhoneUtil.getScreenWidth(SingleTaskAddressActivity.this);
                lp.topMargin = linearrow_location_Y + lineSingleaddressArrow.getHeight();
                rootDialog.setLayoutParams(lp);
                rootDialog.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveLog(String Operatetype, String Address, String Protocoltype, String result, String detail) {
        LogBean bean = new LogBean();
        bean.setUser(SharePreferenceUtil.getStringSP("currentusername", ""));
        bean.setData(simpleDateFormat.format(new Date()));
        bean.setOperatetype(Operatetype);
        bean.setAddress(Address);
        bean.setProtocoltype(Protocoltype);
        bean.setResult(result);
        bean.setDetail(detail);
        bean.save();
    }

    private void setTips(String content) {
        tvRepeatTips.setVisibility(View.VISIBLE);
        tvRepeatTips.setText(content);
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
                    ivSingleaddressBlebattery2.setImageDrawable(getDrawable(R.drawable.battery_null));
                } else if (7.2 <= batteryValue && batteryValue < 7.65) {
                    ivSingleaddressBlebattery2.setImageDrawable(getDrawable(R.drawable.battery_1));
                } else if (7.65 <= batteryValue && batteryValue < 8.10) {
                    ivSingleaddressBlebattery2.setImageDrawable(getDrawable(R.drawable.battery_2));
                } else if (8.10 <= batteryValue && batteryValue < 8.55) {
                    ivSingleaddressBlebattery2.setImageDrawable(getDrawable(R.drawable.battery_3));
                } else if (8.55 <= batteryValue && batteryValue <= 9.00) {
                    ivSingleaddressBlebattery2.setImageDrawable(getDrawable(R.drawable.battery_4));
                }
                ADDRESSTYPE = 0;
            }
        });
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

    @Override
    protected void onDestroy() {
        ADDRESSTYPE = 0;
        handler.removeCallbacksAndMessages(null);
        isAppExit = true;
        LoopCardBatteryThread = null;
        System.gc();
        super.onDestroy();
    }
}
