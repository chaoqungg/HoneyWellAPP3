package com.honeywell.honeywellproject.ELModule;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.honeywell.honeywellproject.BaseActivity.BaseApplication;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BaseAdapter.BaseListView.CommonAdapter;
import com.honeywell.honeywellproject.BaseAdapter.BaseListView.CommonViewHolder;
import com.honeywell.honeywellproject.ELModule.Log.data.LogELBean;
import com.honeywell.honeywellproject.ELModule.adapter.RightTopAdapter;
import com.honeywell.honeywellproject.ELModule.data.RightTopBean;
import com.honeywell.honeywellproject.ELModule.data.USBEvent;
import com.honeywell.honeywellproject.ELModule.data.equipmentBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.AudioUtil;
import com.honeywell.honeywellproject.Util.BiduTTS.control.InitConfig;
import com.honeywell.honeywellproject.Util.BiduTTS.control.MySyntherizer;
import com.honeywell.honeywellproject.Util.BiduTTS.control.NonBlockSyntherizer;
import com.honeywell.honeywellproject.Util.BiduTTS.listener.MessageListener;
import com.honeywell.honeywellproject.Util.BuildModuleUtil;
import com.honeywell.honeywellproject.Util.ConstantUtil;
import com.honeywell.honeywellproject.Util.DialogUtil;
import com.honeywell.honeywellproject.Util.ELUtil;
import com.honeywell.honeywellproject.Util.EventBusUtil;
import com.honeywell.honeywellproject.Util.LogUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.Util.SystemUtil;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;
import com.honeywell.honeywellproject.WidgeView.LongClickButton;
import com.honeywell.honeywellproject.WidgeView.SwitchButton;
import com.honeywell.honeywellproject.WidgeView.indicatordialog.IndicatorBuilder;
import com.honeywell.honeywellproject.WidgeView.indicatordialog.IndicatorDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static java.lang.Integer.parseInt;

/**
 * @author QHT
 */
public class El_SingleFreeActivity extends ToolBarActivity {

    @BindView(R.id.tv_lamptype)
    TextView        tvLamptype;
    @BindView(R.id.iv_top_arrow)
    ImageView       ivTopArrow;
    @BindView(R.id.tophave)
    LinearLayout    tophave;
    @BindView(R.id.line_arrow)
    ImageView       lineArrow;
    @BindView(R.id.iv_less)
    LongClickButton ivLess;
    @BindView(R.id.ledview_clock_time)
    EditText        ledviewClockTime;
    @BindView(R.id.ledview_clock_bg)
    TextView        ledviewClockBg;
    @BindView(R.id.iv_add)
    LongClickButton ivAdd;
    @BindView(R.id.btn_singleaddr_write)
    LinearLayout    btnSingleaddrWrite;
    @BindView(R.id.tv_enbleaddr2)
    TextView        tvEnbleaddr2;
    @BindView(R.id.tv_singleaddr_log)
    TextView        tvSingleaddrLog;
    @BindView(R.id.scroll_singleaddr_log)
    ScrollView      scrollSingleaddrLog;
    @BindView(R.id.lv_select)
    ListView        lvSelect;
    @BindView(R.id.ll_gray)
    View            llGray;
    @BindView(R.id.root_dialog)
    LinearLayout    rootDialog;
    @BindView(R.id.tv_repeat_tips)
    TextView        tvRepeatTips;

    /**
     * 小三角箭头的Y坐标
     */
    int linearrow_location_Y;
    boolean topHaveClick;
    int topHavePosition;
    private ProgressDialog   progressdialog;
    private SimpleDateFormat simpleDateFormat;
    private BleDevice        elDevice;
    LinearLayout        viewBleState;
    CommonAdapter       madapter;
    List<equipmentBean> dropList;
    private StringBuilder        sbuilder;
    private List<RightTopBean> rightTopList =new ArrayList<>();
    private IndicatorDialog dialog;
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode      = TtsMode.MIX;

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================
    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer     synthesizer;
    protected     ConsumerIrManager mCIR;
    /**
     * 手机是否自带红外
     */
    public boolean inIR = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initialTts();
        }catch (Exception e){
        }
        initView();
        checkUsb();

    }

    private  void noBle(){

        if (!BleManager.getInstance().isBlueEnable()) {


            BleManager.getInstance().enableBluetooth();

        }
        checkPermissions();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initBle();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ConstantUtil.selectDriveItem==0){
                    //内置红外，隐藏蓝牙连接图标
                    viewBleState.setVisibility(View.INVISIBLE);
                }
            }
        },50);
    }
    @Override
    public int getContentViewId() {
        return R.layout.activity_el_single_free;
    }

    private void initView() {
       initIR();

        if (SharePreferenceUtil.getBooleanSP("fanzhuan")) {
            PhoneUtil.ConvertScreen(true, El_SingleFreeActivity.this);
        }
        getToolbarTitle().setText("红外编址");
        Bundle bundle = getIntent().getExtras();
        int fragment = bundle.getInt("fragment");
        int lamps = bundle.getInt("lamps");
        initDropList(fragment, lamps);
        initLedText();
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
    private void initLedText() {
        progressdialog = new ProgressDialog(this, R.style.progressDialog);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sbuilder=new StringBuilder();
        sbuilder=new StringBuilder();
        tvSingleaddrLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        ledviewClockTime.addTextChangedListener(textChangeListener);
        ledviewClockTime.postDelayed(new Runnable() {
            @Override
            public void run() {
                ledviewClockTime.setWidth(ledviewClockBg.getWidth());
                //对于刚跳到一个新的界面就要弹出软键盘的情况可能由于界面为加载完全而无法弹出软键盘。此时应该适当的延迟弹出软键盘
                PhoneUtil.showInputWindow(El_SingleFreeActivity.this,  tvEnbleaddr2);
            }
        }, 200);
        //连续减
        ivLess.setLongClickRepeatListener(new LongClickButton.LongClickRepeatListener() {
            @Override
            public void repeatAction() {
                String addrLess=ledviewClockTime.getText().toString();
                if(TextUtil.isEmpty(addrLess)){
                    return;
                }
                if(Integer.parseInt(addrLess)<1){
                    return;
                }else{
                    ledviewClockTime.setText(String.valueOf(Integer.parseInt(addrLess) - 1));
                }
                ledviewClockTime.setSelection(ledviewClockTime.getText().length());
            }
        }, 50);
        //连续加
        ivAdd.setLongClickRepeatListener(new LongClickButton.LongClickRepeatListener() {
            @Override
            public void repeatAction() {
                String addrAdd=ledviewClockTime.getText().toString();
                if(TextUtil.isEmpty(addrAdd)){
                    return;
                }
                if(Integer.parseInt(addrAdd)>=239){
                    return;
                }else{
                    ledviewClockTime.setText(String.valueOf(Integer.parseInt(addrAdd) + 1));
                }
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
            String s2 = ledviewClockTime.getText().toString();
            int strLength = s2.length();
            if(strLength<=0){return;}
            //从1开始
            if(Integer.parseInt(s2)==0){   editable.delete(0, 1);}
            if(strLength==2){
                if(s2.charAt(0)=='0'){
                    editable.delete(0, 1);
                }
            }else if(strLength==3){
                if(Integer.parseInt(s2)>239){
                    editable.delete(2, 3);
                }
            }else if(strLength==4){
                editable.delete(3, 4);
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
        elDevice = BaseApplication.elDevice;
        if (!BleManager.getInstance().isBlueEnable()) {

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
                        } else {
                            if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                DialogUtil.showAlertDialog(El_SingleFreeActivity.this,
                                        "提示", "蓝牙要求位置权限未开启，请在设置中打开位置权限", null, null);
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
            ledviewClockTime.postDelayed(new Runnable() {
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
                if (scanResultList == null || scanResultList.size() == 0) {
                    return;
                }
                for (BleDevice device : scanResultList) {
                    if (device.getName() == null) {
                        continue;
                    }
                    if (device.getName().equals(ConstantUtil.EL_NAME)) {
                        elDevice = BaseApplication.elDevice = device;
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
                    }

                    @Override
                    public void onDisConnected(boolean isActiveDisConnected, final BleDevice device, BluetoothGatt gatt, int status) {
                        if (progressdialog != null) {
                            progressdialog.dismiss();
                        }
                        final BleGattCallback callback = this;

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //断开重连


                                BleManager.getInstance().connect(device, callback);
                            }
                        }, 200);
                    }
                });
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_menu:
                TopRightClick();
                break;
            case R.id.action_blestate:
                if (!BleManager.getInstance().isBlueEnable()) {
                    BleManager.getInstance().enableBluetooth();

                }else {
                    Scan();
                }
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

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
    private RightTopAdapter rightTopAdapter;
    private void TopRightClick(){
        rightTopList.clear();
        rightTopList.add(new RightTopBean(1));
        rightTopList.add(new RightTopBean(2));

        if(rightTopAdapter==null){
            rightTopAdapter= new RightTopAdapter(rightTopList,El_SingleFreeActivity.this);
        }
        if(dialog==null){
            dialog = new IndicatorBuilder(El_SingleFreeActivity.this)  //must be activity
                    .width(PhoneUtil.getScreenWidth(El_SingleFreeActivity.this)*3/5)                           // the dialog width in px
                    .height(PhoneUtil.getScreenHeight(El_SingleFreeActivity.this)/3)                          // the dialog max height in px or -1 (means auto fit)
                    .ArrowDirection(IndicatorBuilder.TOP)
                    .bgColor(getResources().getColor(R.color.white))
                    .dimEnabled(true)
                    .gravity(IndicatorBuilder.GRAVITY_RIGHT)
                    .radius(10)
                    .ArrowRectage(0.9f)
                    .layoutManager(new LinearLayoutManager(El_SingleFreeActivity.this, LinearLayoutManager.VERTICAL, false))
                    .adapter(rightTopAdapter).create();
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.show(getMenuView());
        rightTopAdapter.setRightTopAudioOnCheckedChangeListener(new RightTopAdapter.RightTopAudioOnCheckedChangeListener() {
            @Override
            public void check(SwitchButton button, boolean isChecked) {
                if(isChecked){
                    SharePreferenceUtil.setBooleanSP("audioEL",true);
                }else{
                    SharePreferenceUtil.setBooleanSP("audioEL",false);
                }
            }
        });
    }


    private void initDropList(int fragment, int lamps) {
        switch (fragment) {
            case 1:
                //选择的fragment1 的第lamps个灯板
                if(lamps==1){
                    dropList= BuildModuleUtil.buildModuleBean().get(0);
                }else  if(lamps==2){
                    dropList= BuildModuleUtil.buildModuleBean().get(1);
                }else  if(lamps==3){
                    dropList= BuildModuleUtil.buildModuleBean().get(2);
                }
                break;
            case 2:
                dropList= BuildModuleUtil.buildModuleBean().get(3);
                break;
            case 3:
                dropList= BuildModuleUtil.buildModuleBean().get(4);
                break;
            case 4:
                if(lamps==1){
                    dropList= BuildModuleUtil.buildModuleBean().get(5);
                }else  if(lamps==2){
                    dropList= BuildModuleUtil.buildModuleBean().get(6);
                }
                break;
            case 5:
                if(lamps==1){
                    dropList= BuildModuleUtil.buildModuleBean().get(7);
                }else  if(lamps==2){
                    dropList= BuildModuleUtil.buildModuleBean().get(8);
                }
                break;
            default:
        }
        lvSelect.setAdapter(madapter=new CommonAdapter<equipmentBean>(El_SingleFreeActivity.this,dropList,R.layout.activity_el_dropselect_item) {
            @Override
            public void convert(CommonViewHolder vh, equipmentBean item, int position) {
                TextView tv =((TextView)vh.getView(R.id.textview));
                tv.setText(item.getModuleName());
                if(tvLamptype.getText().toString().equals(item.getModuleName())){
                    tv.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                }else{
                    tv.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }
            }
        });
        lvSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                topHavePosition=i;
                tvLamptype.setText(dropList.get(i).getModuleName());
                madapter.notifyDataSetChanged();
                resetDrop();
            }
        });
        //默认值为第一个类型
        tvLamptype.setText(dropList.get(0).getModuleName());
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    break;
                default:
            }
        }
    };


    @OnClick({R.id.tophave, R.id.iv_less, R.id.iv_add, R.id.btn_singleaddr_write, R.id.ll_gray})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tophave:
                if(topHaveClick){
                    resetDrop();
                    return;
                }
                topHaveClick=true;
                ivTopArrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_down_red_50));
                lineArrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_line_center));
                setDialogMargin();
                break;
            case R.id.iv_less:
                String addrless=ledviewClockTime.getText().toString();
                if(TextUtil.isEmpty(addrless)){
                    return;
                }
                int  less = parseInt(addrless);
                if ( less > 1) {
                    less = less - 1;
                }else{
                    return;
                }
                ledviewClockTime.setText(String.valueOf(less));
                ledviewClockTime.setSelection(ledviewClockTime.getText().length());
                break;
            case R.id.iv_add:
                String addradd=ledviewClockTime.getText().toString();
                if(TextUtil.isEmpty(addradd)){
                    return;
                }
                int  add = parseInt(addradd);
                if (add>=239) {
                    return;
                }
                add = add + 1;
                ledviewClockTime.setText(String.valueOf(add));
                ledviewClockTime.setSelection(ledviewClockTime.getText().length());
                break;
            case R.id.btn_singleaddr_write:
                if(TextUtil.isEmpty(ledviewClockTime.getText().toString())){
                    return;
                }
                SystemUtil.vibrate(El_SingleFreeActivity.this,100);
                transmit(dropList.get(topHavePosition).getLamptypeID(), dropList.get(topHavePosition).getModuleID(),
                        ledviewClockTime.getText().toString());
                break;
            case R.id.ll_gray:
                resetDrop();
                break;
            default:
        }
    }
    private void write(String datas){
        BleManager.getInstance().write(elDevice,ConstantUtil.SERVER_UUID_EL, ConstantUtil.Write_UUID_EL, HexUtil.hexStringToBytes(datas), new BleWriteCallback() {
            @Override
            public void onWriteSuccess() {
                LogUtil.e("write -----> success");
                setTips("编写地址"+ledviewClockTime.getText().toString()+"完成\n请注意灯板是否闪烁三次");
                String detail="编写地址"+ledviewClockTime.getText().toString()+"完成"+"；\n";
                sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                saveLog(ledviewClockTime.getText().toString(),"完成",detail);
                tvSingleaddrLog.setText(sbuilder.toString());
                scrollToBottom(scrollSingleaddrLog,tvSingleaddrLog);
              speak("编写地址"+ledviewClockTime.getText().toString()+",完成",true);
            }

            @Override
            public void onWriteFailure(BleException exception) {
                LogUtil.e("write -----> exception:"+exception.getDescription());
            }
        });
    }
    private void saveLog(String Address,String result,String detail){
        LogELBean bean=new LogELBean();
        bean.setUser(SharePreferenceUtil.getStringSP("currentusername_el",""));
        bean.setData(simpleDateFormat.format(new Date()));
        bean.setModuleName(tvLamptype.getText().toString());
        bean.setTypeID(dropList.get(topHavePosition).getLamptypeID()+"");
        bean.setModuleID(dropList.get(topHavePosition).getModuleID()+"");
        bean.setAddress(Address);
        bean.setResult(result);
        bean.setDetail(detail);
        bean.save();
    }
    private void setTips(String content){
        tvRepeatTips.setVisibility(View.VISIBLE);
        tvRepeatTips.setText(content);
    }

    private void resetDrop() {
        topHaveClick=false;
        rootDialog.setVisibility(View.GONE);
        lineArrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_line));
        ivTopArrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_up_black_50));
    }

    private void setDialogMargin() {
        rootDialog.post(new Runnable() {
            @Override
            public void run() {
                //如果直接设置LayoutParams的话，此时line还是线条，没有小三角，所以显示不出来，得延迟一下
                final int[] location = new int[2];
                lineArrow.getLocationInWindow(location);
                linearrow_location_Y = location[1];
                rootDialog.getLayoutParams();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rootDialog.getLayoutParams();
                lp.width = PhoneUtil.getScreenWidth(El_SingleFreeActivity.this);
                lp.topMargin = linearrow_location_Y + lineArrow.getHeight();
                rootDialog.setLayoutParams(lp);
                rootDialog.setVisibility(View.VISIBLE);
            }
        });
    }
    /**
     * 根据scrolview 和子view去测量滑动的位置
     *
     * @param scrollView
     * @param view
     */
    int lastline;
    private void scrollToBottom(final ScrollView scrollView, final TextView view) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (scrollView == null || view == null) {
                    return;
                }
                if(lastline==view.getLineCount()){
                    //如果行数未增加，意味着没更换头没插头，则不执行下面的runable
                    return;
                }
                lastline=view.getLineCount();
                // offset偏移量。是指当textview中内容超出 scrollview的高度，那么超出部分就是偏移量
                int offset = view.getMeasuredHeight()
                        - scrollView.getMeasuredHeight();
                if (offset < 0) {
                    offset = 0;
                }
                //scrollview开始滚动
                scrollView.scrollTo(0, offset);
            }
        });
    }

    public  void initialTts() {
        LoggerProxy.printable(false); // 日志打印在logcat中
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        SpeechSynthesizerListener listener = new MessageListener();
        Map<String, String> params = AudioUtil.getParams(this);

        InitConfig initConfig = new InitConfig(ConstantUtil.baiduappId, ConstantUtil.baiduappKey,
                ConstantUtil.baidusecretKey, ttsMode, params, listener);

        // 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
        // 上线时请删除AutoCheck的调用
//        AutoCheck.getInstance(BaseApplication.getInstance()).check(initConfig, new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == 100) {
//                    AutoCheck autoCheck = (AutoCheck) msg.obj;
//                    synchronized (autoCheck) {
//                        String message = autoCheck.obtainDebugMessage();
//                        Log.e("TAG", message);
//                    }
//                }
//            }
//        });
        synthesizer = new NonBlockSyntherizer(this, initConfig, handler); // 此处可以改为MySyntherizer 了解调用过程
    }
    /**
     * @param isFlush  是否需要清空队列，直播放最新加入的声音
     */
    private void speak(String content,boolean isFlush) {
        if(SharePreferenceUtil.getBooleanSP("audioEL")){
            if(synthesizer!=null)
            { if(isFlush){
                synthesizer.stop();
            }
                synthesizer.speak(content);
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBusUtil.register(this);
    }
    /**
     * 优先内置红外
     **/
    protected void transmit(int typeid,int moduleid,String addr) {
        if (BleManager.getInstance().isConnected(BaseApplication.elDevice) ) {
            write(ELUtil.EL_WRITE(typeid,moduleid,addr));
        } else  if (inIR) {
            mCIR.transmit(38000, ELUtil.getParamIR(typeid,moduleid,addr));
            setTips("编写地址"+ledviewClockTime.getText().toString()+"完成\n请注意灯板是否闪烁三次");
            String detail="编写地址"+ledviewClockTime.getText().toString()+"完成"+"；\n";
            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
            saveLog(ledviewClockTime.getText().toString(),"完成",detail);
            tvSingleaddrLog.setText(sbuilder.toString());
            scrollToBottom(scrollSingleaddrLog,tvSingleaddrLog);
            speak("编写地址"+ledviewClockTime.getText().toString()+",完成",true);
        } else {
            DialogUtil.showTipsDialog(El_SingleFreeActivity.this, "您的手机无自带红外功能，请插入USB红外发射装置", new DialogUtil.OnTipsClick() {
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
    /**
     * Applicaiton 发送的USB插拔消息
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUSBLight(USBEvent response) {
        if(BleManager.getInstance().isConnected(BaseApplication.elDevice)){
            return;
        }
        if(response.Insert){
            DialogUtil.showTwoButtonDialog(El_SingleFreeActivity.this, "系统检测到您已经插入了USB设备，此设备为HoneyWell的设备吗？\n" +
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
                    DialogUtil.showTipsDialog(El_SingleFreeActivity.this, "设备已移除，请重新插入USB红外发射装置", new DialogUtil.OnTipsClick() {
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
    private void checkUsb() {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
//        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        if (!deviceList.isEmpty()) {
            ConstantUtil.USBInsert = true;
            onUSBLight(new USBEvent(true));
        }
    }

    @Override
    protected void onDestroy() {
        if(synthesizer!=null){
            synthesizer.release();
        }
        handler.removeCallbacksAndMessages(null);
        if(progressdialog!=null){
            progressdialog.dismiss();
            progressdialog=null;
        }
        EventBusUtil.unregister(this);
        System.gc();
        super.onDestroy();
    }
}
