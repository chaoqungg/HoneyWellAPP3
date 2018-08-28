package com.honeywell.honeywellproject.BleTaskModule.SingleAddress;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
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
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.honeywell.honeywellproject.BaseActivity.BaseApplication;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.FatherTask.data.FatherTaskBean;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.CommonBleUtil;
import com.honeywell.honeywellproject.Util.ConstantUtil;
import com.honeywell.honeywellproject.Util.DataHandler;
import com.honeywell.honeywellproject.Util.LogUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;
import com.honeywell.honeywellproject.WidgeView.DividerGridItemDecoration;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static java.lang.Integer.parseInt;

/**
 * 贼他妈，事真多！
 * 12-07又他妈改协议了，垃圾协议还一天改改改，SB!
 *
 * @author QHT
 * @since 2017-12-04
 */
public class SingleAddressActivity extends ToolBarActivity {

    @BindView(R.id.ledview_clock_time)
    EditText     ledviewClockTime;
    @BindView(R.id.ledview_clock_bg)
    TextView     ledviewClockBg;
    @BindView(R.id.btn_el_password_write)
    Button       btnElPasswordWrite;
    @BindView(R.id.btn_el_password_read)
    Button       btnElPasswordRead;
    @BindView(R.id.tv_enbleaddr2)
    TextView     tvEnbleaddr2;
    @BindView(R.id. tv_tips)
    TextView      tvTips;
    @BindView(R.id.iv_less)
    ImageButton  ivLess;
    @BindView(R.id.iv_add)
    ImageButton  ivAdd;
    @BindView(R.id.tv_singleaddress_blestate2)
    TextView     tvSingleaddressBlestate2;
    @BindView(R.id.tv_singleaddress_blerssi2)
    TextView     tvSingleaddressBlerssi2;
    @BindView(R.id.ll_singleaddress_top)
    LinearLayout llSingleAddressTop;
    @BindView(R.id.rv_enbleaddr)
    RecyclerView rvEnbleaddr;


    private ProgressDialog       progressdialog;
    private String               position, loginId, memorytask, prefatherListSize;
    /**
     * Notify 如果字节长度大于20.分包接收，暂存前一个包
     */
    private String   beforeString;

    /**
     * 编址类型，CLIP 10X(101,102)\DLIP 20X(201,202)\FlashScan 30X
     */
    private static          int     ADDRESSTYPE            = 0;
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
     * 是否点击了删除键
     */
    private boolean KeyMapDelete;
    /**
     * 是否点击了加减号，主要是textchange的变化
     */
    private boolean ClickAdd, ClickLess;
    /**
     * 可用地址List
     */
    private List<Integer> enbleDigitalList;
    private BleDevice bleDevice;
    private CommonBleUtil        commonBleUtil;
    private List<FatherTaskBean> fatherList;
    private List<SonTaskBean>    sonList;
    private FatherTaskBean       bean;
    private SingleAddressAdapter enableAddressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initBle();
        getEnbleAddr();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_single_address;
    }


    private void initView() {
        initCommonBleUtil();
        enbleDigitalList=new ArrayList<Integer>();
        progressdialog = new ProgressDialog(this, R.style.progressDialog);
        getToolbarTitle().setText("单个编址");
        Bundle bundle = getIntent().getExtras();
        rvEnbleaddr.setLayoutManager(new GridLayoutManager(this,7));
        enableAddressAdapter = new SingleAddressAdapter(enbleDigitalList, this);
        rvEnbleaddr.addItemDecoration(new DividerGridItemDecoration(this));
        rvEnbleaddr.setAdapter(enableAddressAdapter);
        enableAddressAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        if (bundle != null) {
            memorytask = bundle.getString(ConstantUtil.MEMORYTASK);
            if ("3".equals(memorytask)) {
                //3  保存到回路
                position = bundle.getString("position");
                loginId = bundle.getString("loginId");
                fatherList = DataSupport.where("logininfo_id = ?", loginId + "").find(FatherTaskBean.class);
                tvEnbleaddr2.setVisibility(View.GONE);
                rvEnbleaddr.setVisibility(View.VISIBLE);
            } else if ("2".equals(memorytask)) {
                //2  新建父，新建子
                prefatherListSize = bundle.getString("prefatherListSize");
                loginId = bundle.getString("loginId");
                tvEnbleaddr2.setVisibility(View.GONE);
                rvEnbleaddr.setVisibility(View.VISIBLE);
            } else if ("1".equals(memorytask)) {
                tvEnbleaddr2.setVisibility(View.VISIBLE);
                rvEnbleaddr.setVisibility(View.INVISIBLE);
                tvTips.setText(ResourceUtil.getString(R.string.tips2));
            }
        }
        //TYPE_TEXT_FLAG_NO_SUGGESTIONS 避免afterTextChanged方法卡死
        int inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        ledviewClockTime.setInputType(inputType);
        ledviewClockTime.setSelection(ledviewClockTime.getText().length());
        ledviewClockTime.setSelection(ledviewClockTime.getText().length());
        ledviewClockTime.postDelayed(new Runnable() {
            @Override
            public void run() {
                //对于刚跳到一个新的 界面就要弹出软键盘的情况可能由于界面为加载完全而无法弹出软键盘。此时应该适当的延迟弹出软键盘
                PhoneUtil.showInputWindow(SingleAddressActivity.this, ledviewClockTime);
            }
        }, 200);
        if (SharePreferenceUtil.getStringSP("maxInputLength", "0").equals("4")) {
            ledviewClockBg.setText("8888");
        } else {
            ledviewClockBg.setText("888");
        }
        ledviewClockTime.setCursorVisible(false);
        ledviewClockTime.addTextChangedListener(textChangeListener);
        ledviewClockTime.setKeyListener(keyListener);
        ledviewClockTime.setOnKeyListener(onKeylistener);
        ivLess.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //设置图片透明度0~255，0完全透明，255不透明
                    ivLess.getBackground().setAlpha(50);
                    ivLess.invalidate();
                } else {
                    //还原图片
                    ivLess.getBackground().setAlpha(255);
                    ivLess.invalidate();
                }
                return false;
            }
        });
        ivAdd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //设置图片透明度0~255，0完全透明，255不透明
                    ivAdd.getBackground().setAlpha(50);
                    ivAdd.invalidate();
                } else {
                    //还原图片
                    ivAdd.getBackground().setAlpha(255);
                    ivAdd.invalidate();
                }
                return false;
            }
        });
    }

    View.OnKeyListener onKeylistener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            //点击了删除键
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                KeyMapDelete = true;
            }
            return false;
        }
    };
    KeyListener        keyListener   = new NumberKeyListener() {
        /**
         * @return ：返回哪些希望可以被输入的字符,默认不允许输入
         */
        @Override
        protected char[] getAcceptedChars() {
            char[] chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
            return chars;
        }

        /**
         * 0：无键盘,键盘弹不出来 1：英文键盘 2：模拟键盘 3：数字键盘
         */
        @Override
        public int getInputType() {
            return 3;
        }
    };

    TextWatcher textChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s2 = ledviewClockTime.getText().toString();
            int strLength = s2.length();
            //如果是读完再输入的话就直接清空之前的，不循环
            if (SharePreferenceUtil.getStringSP("maxInputLength", "0").equals("4")) {
                if (readingfinish) {
                    if (!ClickAdd && !ClickLess) {
                        if (KeyMapDelete) {
                            s2 = s2;
                            KeyMapDelete = false;
                        } else {
                            s2 = s2.substring(strLength - 1, strLength);
                            readingfinish = false;
                            ClickAdd = false;
                            ClickAdd = false;
                        }
                    }
                } else {
                    if (strLength >= 4 && (strLength % 4 == 0 || strLength % 5 == 0 || strLength % 6 == 0)) {
                        s2 = s2.substring(strLength - 4, strLength);
                    }
                }
            } else {
                if (readingfinish) {
                    if (!ClickAdd && !ClickLess) {
                        if (KeyMapDelete) {
                            s2 = s2;
                            KeyMapDelete = false;
                        } else {
                            s2 = s2.substring(strLength - 1, strLength);
                            readingfinish = false;
                            ClickAdd = false;
                            ClickAdd = false;
                        }
                    }
                } else {
                    if (strLength >= 3 && (strLength % 3 == 0 || strLength % 4 == 0 || strLength % 5 == 0)) {
                        s2 = s2.substring(strLength - 3, strLength);
                    }
                }
            }
            if ("3333".equals(s2)) {
                ledviewClockBg.setText("888");
                s2 = "";
                SharePreferenceUtil.setStringSP("maxInputLength", "3");
            } else if ("444".equals(s2)) {
                ledviewClockBg.setText("8888");
                s2 = "";
                SharePreferenceUtil.setStringSP("maxInputLength", "4");
            }
            final String finalS = s2;
            ledviewClockTime.post(new Runnable() {
                @Override
                public void run() {
                    ledviewClockTime.removeTextChangedListener(textChangeListener);
                    ledviewClockTime.setText(finalS);
                    ledviewClockTime.setSelection(ledviewClockTime.getText().length());
                    ledviewClockTime.addTextChangedListener(textChangeListener);
                }
            });
        }
    };

    /**
     * 初始化蓝牙连接操作
     */
    private void initBle() {
        bleDevice=BaseApplication.bleDevice;
        if (! BleManager.getInstance().isBlueEnable()) {
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
            tvSingleaddressBlestate2.setText("已连接");
            llSingleAddressTop.setBackgroundResource((R.color.orange_half));
            BleManager.getInstance().readRssi(bleDevice,new BleRssiCallback() {
                @Override
                public void onRssiFailure(BleException exception) {

                }

                @Override
                public void onRssiSuccess(int rssi) {
                    tvSingleaddressBlerssi2.setText(rssi + "");
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
                if(scanResultList==null || scanResultList.size()==0)
                {return;}
                for(BleDevice device : scanResultList){
                    if(device.getName()==null){
                        continue;
                    }
                    if(device.getName().equals(ConstantUtil.BLE_NAME)){
                        bleDevice=BaseApplication.bleDevice=device;
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
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvSingleaddressBlestate2.setText("已连接");
                                llSingleAddressTop.setBackgroundResource((R.color.orange_half));
                                PhoneUtil.showInputWindow(SingleAddressActivity.this, ledviewClockTime);
                                ToastUtil.showToastShort("编址器连接成功");
                                BleManager.getInstance().readRssi(bleDevice,new BleRssiCallback() {
                                    @Override
                                    public void onRssiFailure(BleException exception) {
                                    }
                                    @Override
                                    public void onRssiSuccess(int rssi) {
                                        tvSingleaddressBlerssi2.setText(rssi + "");
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
                                tvSingleaddressBlestate2.setText("未连接，点击重连");
                                llSingleAddressTop.setBackgroundResource((R.color.red));
                            }
                        });
                    }
                });
            }
        });
    }

    @OnClick({R.id.btn_el_password_write, R.id.btn_el_password_read,
            R.id.iv_less, R.id.iv_add, R.id.ll_singleaddress_top})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_el_password_write:
                //编址
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    ToastUtil.showToastShort("编址器未连接，点击顶部红色区域重连");
                    return;
                }
                readingfinish = false;
                initWrite();
                break;
            case R.id.btn_el_password_read:
                //读址
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    ToastUtil.showToastShort("编址器未连接，点击顶部红色区域重连");
                    return;
                }
                readingfinish = false;
                initRead();
                break;
            case R.id.ll_singleaddress_top:
                //顶部蓝牙状态,若未连接成功，执行扫描，链接成功，检查信号状态
                Scan();
                break;
            case R.id.iv_less:
                //减号，从可用地址中一个一个加
                if (TextUtils.isEmpty(ledviewClockTime.getText().toString())) {
                    return;
                }
                ClickLess = true;
                int less = 0;
                if (enbleDigitalList.size() > 0) {
                    less = parseInt(ledviewClockTime.getText().toString());
                }
                if (less <= 1) {
                    return;
                }
                int lessNext = less - 1;
                while (true) {
                    if (!enbleDigitalList.contains((Integer) lessNext)) {
                        lessNext--;
                    } else {
                        break;
                    }
                }
                String strNext = String.valueOf(lessNext);
                if (strNext.length() == 1) {
                    strNext = "00" + strNext;
                } else if (strNext.length() == 2) {
                    strNext = "0" + strNext;
                }
                ledviewClockTime.setText(strNext);
                break;
            case R.id.iv_add:
                //加号，从可用地址中一个一个加
                if (TextUtils.isEmpty(ledviewClockTime.getText().toString())) {
                    return;
                }
                ClickAdd = true;
                int add = 0;
                if ( enbleDigitalList.size() > 0) {
                    add = parseInt(ledviewClockTime.getText().toString());
                }
                if (add >= enbleDigitalList.size()) {
                    return;
                }
                int addNext = add + 1;
                while (true) {
                    if (!enbleDigitalList.contains((Integer) addNext)) {
                        addNext++;
                    } else {
                        break;
                    }
                }
                String strNextAdd = String.valueOf(addNext);
                if (strNextAdd.length() == 1) {
                    strNextAdd = "00" + strNextAdd;
                } else if (strNextAdd.length() == 2) {
                    strNextAdd = "0" + strNextAdd;
                }
                ledviewClockTime.setText(strNextAdd);
                break;
            default:
        }
    }

    /**
     * 获取可用地址，从数据库中遍历239以内剩余的地址
     */
    private void getEnbleAddr() {
        if (fatherList == null || fatherList.size() <= 0) {
            enbleDigitalList.clear();
            for (int i = 1; i <= 239; i++) {
                enbleDigitalList.add(i);
            }
            tvEnbleaddr2.setText("可用地址范围：1-239");
            return;
        }
        //地址范围：1-239  只针对 3，position有值
        bean = fatherList.get(parseInt(position));
        sonList = bean.getSontasklist();
        if (sonList == null || sonList.size() <= 0) {
            //只建立了父任务，缺少子任务，在此新建一个子任务
            buildSonTask();
            enbleDigitalList.clear();
            for (int i = 1; i <= 239; i++) {
                enbleDigitalList.add(i);
            }
            tvEnbleaddr2.setText("可用地址范围：1-239");
            return;
        }
        enbleDigitalList.clear();
        for (int i = 1; i <= 239; i++) {
            enbleDigitalList.add(i);
        }
        for (int i = 0; i < sonList.size(); i++) {
            int used = sonList.get(i).getTaskdigitaladdress();
            if (used != 0 && enbleDigitalList.contains(used)) {
                enbleDigitalList.remove((Integer) used);
            }
        }
//        StringBuilder sb = new StringBuilder();
//        String show = "";
//        for (int i = 0; i < enbleDigitalList.size(); i++) {
//            sb.append(String.valueOf(enbleDigitalList.get(i)) + "  ,");
//        }
//        tvEnbleaddr2.setText(sb.toString());
        enableAddressAdapter.notifyDataSetChanged();
    }

    /**
     * 获取可用地址，从数据库中遍历239以内剩余的地址
     */
    private void refreshEnableAddr() {
        int addr = Integer.parseInt(ledviewClockTime.getText().toString());
        for (int i = 0; i < enbleDigitalList.size(); i++) {
            if (enbleDigitalList.contains(addr)) {
                enbleDigitalList.remove((Integer) addr);
            }
        }
        enableAddressAdapter.notifyDataSetChanged();
    }

    /**
     * 新建子任务
     */
    private void buildSonTask() {
        SonTaskBean sb = new SonTaskBean();
        sb.setTasknumber(1);
        sb.setTaskdigitaladdress(1);
        sb.setFathertask_id(String.valueOf(bean.getId()));
        sb.setLogininfo_id(bean.getLogininfo_id());
        sb.save();
    }

    /**
     * 新建一条父任务，同时新建父任务的第一条子任务,目前策略是编址成功才新建，并保存，编制不成功不新建
     */
    private void buildFatherAndSonTask() {
        FatherTaskBean fb = new FatherTaskBean();
        fb.setTasknumber(parseInt(prefatherListSize) + 1);
        fb.setTaskdate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        fb.setLogininfo_id(loginId);
        fb.setProgressnpercent("0.00%");
        fb.save();
        SonTaskBean sb = new SonTaskBean();
        //知道这是啥意思么，SB HoneyWell!!
        sb.setTasknumber(1);
        sb.setTaskdigitaladdress(parseInt(ledviewClockTime.getText().toString()));
        sb.setProcess(true);
        sb.setFathertask_id(String.valueOf(fb.getId()));
        sb.setLogininfo_id(loginId);
        sb.save();
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
                    boolean result = msg.getData().getBoolean("result");
                    //如果是单独读，则失败循环，成功返回
                    if (DataHandler.singleReading) {
                        if (!result) {
                            Reading("FLASHSCAN");
                        } else {
                            readingSuccess();
                        }
                    } else {
                        if (!result) {
                            Writing("FLASHSCAN", 301, 1);
                        } else {
                            writtingSuccess();
                        }
                    }
                    break;
                case NOTIFYDECODE_DLIP1:
                    Writing("DLIP", 202, 2);
                    break;
                case NOTIFYDECODE_DLIP2:
                    boolean result2 = msg.getData().getBoolean("result");
                    if (DataHandler.singleReading) {
                        if (!result2) {
                            Reading("CLIP");
                        } else {
                            readingSuccess();
                        }
                    } else {
                        if (!result2) {
                            Writing("CLIP", 101, 1);
                        } else {
                            writtingSuccess();
                        }
                    }
                    break;
                case NOTIFYDECODE_FLASHSCAN:
                    DataHandler.singleReading = false;
                    ToastUtil.showToastShort("独立编址所有协议流程执行完");
                    break;
                default:
            }
        }
    };

    private void writtingSuccess() {
        if ("3".equals(memorytask)) {
            //保存到历史任务
            bean = fatherList.get(parseInt(position));
            sonList = bean.getSontasklist();
            if (sonList == null || sonList.size() <= 0) {
                ToastUtil.showToastShort("writtingSuccess,但sonList为空,请联系开发者");
                return;
            }
            for (int i = 0; i < sonList.size(); i++) {
                //将这条单独编址编到第一条失败的探头上面
                if (sonList.get(i).getTaskdigitaladdress() != 0 && !sonList.get(i).isProcess()) {
                    sonList.get(i).setProcess(true);
                    sonList.get(i).setTaskdigitaladdress(parseInt(ledviewClockTime.getText().toString()));
                    sonList.get(i).update(sonList.get(i).getId());
                    break;
                }
            }
        } else if ("2".equals(memorytask)) {
            //新建父任务和第一条子任务
            buildFatherAndSonTask();
        }
        ADDRESSTYPE = 0;
        ToastUtil.showImageToastShort("编址成功", R.drawable.progress_ok);
        if (!"1".equals(memorytask)) {
            refreshEnableAddr();
        }
    }

    private void readingSuccess() {
        DataHandler.singleReading = false;
        ToastUtil.showImageToastShort("读址成功", R.drawable.progress_ok);
        String show = parseInt(DataHandler.DLIP_ADDR_NEW, 16) + "";
        if (show.length() == 1) {
            show = "00" + show;
        } else if (show.length() == 2) {
            show = "0" + show;
        }
        ledviewClockTime.setText(show);
        ADDRESSTYPE = 0;
        readingfinish = true;
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
                        //直接跳转FlashScan
                        ShutDown2FlashScan(false);
                        ToastUtil.showToastShort("CLIP写命令失败，尝试FLASHSCAN");
                    }
                } else if (ADDRESSTYPE == 102) {
                    //收到消息，则未超时，正常执行解码操作
                    TimeOutCLip = false;
                    result = DataHandler.CLIP_READ(values, 2);
                    if (!result) {
                        ToastUtil.showToastShort("CLIP验证命令失败,尝试FLASHSCAN");
                    }
                    if (progressdialog != null && progressdialog.isShowing()) {
                        progressdialog.dismiss();
                    }
                    ShutDown2FlashScan(result);
                } else if (ADDRESSTYPE == 201) {
                    result = DataHandler.DLIP_READ(values, 1);
                    if (result) {
                        handler.sendEmptyMessage(NOTIFYDECODE_DLIP1);
                    } else {
                        //直接跳转Clip
                        ShutDown2Clip(false);
                        ToastUtil.showToastShort("DLIP写命令失败,尝试CLIP");
                    }
                } else if (ADDRESSTYPE == 202) {
                    result = DataHandler.DLIP_READ(values, 2);
                    if (!result) {
                        ToastUtil.showToastShort("DLIP验证命令失败,尝试CLIP");
                    }
                    ShutDown2Clip(result);
                } else if (ADDRESSTYPE == 301) {
                }
            }
        });
    }

    /**
     * 开始编址
     */
    private void initWrite() {
        String addrstr = ledviewClockTime.getText().toString();
        if (TextUtils.isEmpty(addrstr)) {
            ToastUtil.showToastShort("地址不能为空");
            return;
        }
        if (enbleDigitalList == null || enbleDigitalList.size() <= 0) {
            ToastUtil.showToastShort("无可用地址");
            return;
        }
        if (!enbleDigitalList.contains(parseInt(addrstr))) {
            if (parseInt(addrstr) <= 239 && parseInt(addrstr) >= 1) {
                ToastUtil.showToastShort("地址已经使用");
            } else {
                ToastUtil.showToastShort("地址超出可用范围");
            }
            return;
        }
        commonBleUtil.notifyDevice(bleDevice);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (addrstr.length() <= 3) {
            if (parseInt(addrstr) >= 1 && parseInt(addrstr) <= 239) {
                //1-239 DLIP
                ADDRESSTYPE = 201;
                Writing("DLIP", ADDRESSTYPE, 1);
            } else if (parseInt(addrstr) >= 1001 && parseInt(addrstr) <= 1158) {
                //FlashScan
                ADDRESSTYPE = 301;
                Writing("FLASHSCAN", ADDRESSTYPE, 1);
            } else {
                ToastUtil.showToastShort("地址不在可用范围内");
            }
        } else {
            //即FlashScan协议
        }
    }

    /**
     * 开始编址
     */
    private void Writing(String protocolType, int type, int index) {
        startCommandTimeOut("WRITE");
        String addrstr = ledviewClockTime.getText().toString();
        if (addrstr == null || "".equals(addrstr)) {
            return;
        }
        String command = null;
        if ("CLIP".equals(protocolType)) {
            //CLIP,第index条命令
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ADDRESSTYPE = type;
            command = DataHandler.CLIP_WRITE(addrstr, index, false);
            if (TextUtils.isEmpty(command)) {
                ToastUtil.showToastShort("CLIP地址超过199，不支持");
                return;
            }
            commonBleUtil.writeDevice(bleDevice,command);
        } else if ("DLIP".equals(protocolType)) {
            //DLIP,第index条命令
            ADDRESSTYPE = type;
            if (index == 1) {
                DataHandler.DLIP_ADDR_NEW = DataHandler.Alone2Hex(Integer.toHexString(parseInt(addrstr)));
            }
            command = DataHandler.DLIP_WRITE("00", addrstr, index);
            if (TextUtils.isEmpty(command)) {
                ToastUtil.showToastShort("DLIP地址超过239，不支持");
                return;
            }
            commonBleUtil.writeDevice(bleDevice,command);
        } else if ("FLASHSCAN".equals(protocolType)) {
            //FlashScan,第index条命令
        }
    }

    /**
     * 单独读址
     */
    private void initRead() {
        commonBleUtil.notifyDevice(bleDevice);
        try {
            //必须延迟100ms以上，太短容易导致Write失败
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Reading("DLIP");
    }

    /**
     * 单独读址
     * <p/>
     * CLIP读地址（轮询1～199）:
     * 55 AA 01 80 FE 00 05 7A 55 5A
     * 回复:
     * 55 AA 01 80 00 10 91 55 5A  //10表示地址16
     * 55 AA 01 80 00 63 E2 55 5A  //63表示地址99
     */
    private void Reading(String protocolType) {
        String command = null;
        DataHandler.singleReading = true;
        startCommandTimeOut("READ");
        if ("CLIP".equals(protocolType)) {
            //1-199循环读，哪个匹配上就是哪个。Suck协议，还有这种操作？
            //12-07终于把这傻逼操作改了，能好点了
            if (progressdialog != null && !progressdialog.isShowing()) {
                progressdialog.setMessage("读址中...");
                progressdialog.setCanceledOnTouchOutside(false);
                progressdialog.show();
            }
            //开始记录超时时间
            TimeOutCLip = true;
            timeClip = System.currentTimeMillis();
            startClipTimeOut();
            ADDRESSTYPE = 102;
            //00 无意义
            command = DataHandler.CLIP_WRITE("00", 2, true);
            commonBleUtil.writeDevice(bleDevice,command);
        } else if ("DLIP".equals(protocolType)) {
            ADDRESSTYPE = 202;
            //"FF"  ==  255 十进制
            command = DataHandler.DLIP_WRITE("00", "255", 2);
            commonBleUtil.writeDevice(bleDevice,command);
        } else if ("FLASHSCAN".equals(protocolType)) {
            //FlashScan,第index条命令
        }
    }

    /**
     * CLIP轮询
     * 单元地址返回读取成功的1～199之间的地址值；
     * 轮询结果，地址越小返回越快（用地址99测试需要3s，地址199预计在8s内）
     */
    private void startClipTimeOut() {
        Runnable mRunable = new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - timeClip > TIMEOUT_CLIPTIME) {
                    //如果Clip超时则去FLASHSCAN读址
                    LogUtil.e("Clip 超时-----已经发送DLIP读");
                    if (progressdialog != null && progressdialog.isShowing()) {
                        progressdialog.dismiss();
                    }
                    handler.sendEmptyMessage(NOTIFYDECODE_FLASHSCAN);
                    handler.removeCallbacks(this);
                } else {
                    if (!TimeOutCLip) {
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
                        ToastUtil.showToastShort("读址失败");
                    } else {
                        ToastUtil.showToastShort("编址失败");
                    }
                    handler.removeCallbacks(this);
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

    /**
     * Dlip编址失败，跳转CLIP
     */
    private void ShutDown2Clip(boolean result) {
        Message msg = handler.obtainMessage();
        msg.what = NOTIFYDECODE_DLIP2;
        Bundle bundle = new Bundle();
        bundle.putBoolean("result", result);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    /**
     * Clip编址失败，跳转FLASHSCAN
     */
    private void ShutDown2FlashScan(boolean result) {
        Message msg = handler.obtainMessage();
        msg.what = NOTIFYDECODE_CLIP2;
        Bundle bundle = new Bundle();
        bundle.putBoolean("result", result);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

}
