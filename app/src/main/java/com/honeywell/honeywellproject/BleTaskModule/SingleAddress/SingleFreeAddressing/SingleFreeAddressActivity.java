package com.honeywell.honeywellproject.BleTaskModule.SingleAddress.SingleFreeAddressing;

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
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.honeywell.honeywellproject.BaseActivity.BaseApplication;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.Log.data.LogBean;
import com.honeywell.honeywellproject.BleTaskModule.SingleAddress.RightTop.RightTopAdapter;
import com.honeywell.honeywellproject.BleTaskModule.SingleAddress.RightTop.RightTopBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.AudioUtil;
import com.honeywell.honeywellproject.Util.BiduTTS.control.InitConfig;
import com.honeywell.honeywellproject.Util.BiduTTS.control.MySyntherizer;
import com.honeywell.honeywellproject.Util.BiduTTS.control.NonBlockSyntherizer;
import com.honeywell.honeywellproject.Util.BiduTTS.listener.MessageListener;
import com.honeywell.honeywellproject.Util.BiduTTS.util.OfflineResource;
import com.honeywell.honeywellproject.Util.CommonBleUtil;
import com.honeywell.honeywellproject.Util.ConstantUtil;
import com.honeywell.honeywellproject.Util.DataHandler;
import com.honeywell.honeywellproject.Util.DialogUtil;
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
import com.nineoldandroids.animation.ObjectAnimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static java.lang.Integer.parseInt;

/**
 * @author QHT
 * @since Created by QHT on 2018-01-05.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑   永无BUG   需求稳定           #
 * #                                                   #
 */
public class SingleFreeAddressActivity extends ToolBarActivity {

    @BindView(R.id. ll_singleaddress_top)
    LinearLayout  llSingleAddressTop;
    @BindView(R.id.iv_singleaddress_blestate2)
    ImageView    ivSingleaddressBlestate2;
    @BindView(R.id.iv_singleaddress_blerssi2)
    ImageView    ivSingleaddressBlerssi2;
    @BindView(R.id.iv_singleaddress_blebattery2)
    ImageView    ivSingleaddressBlebattery2;
    @BindView(R.id.tv_devicetype)
    TextView     tvDevicetype;
    @BindView(R.id.top1)
    LinearLayout top1;
    @BindView(R.id.tv_protocoltype)
    TextView     tvProtocoltype;
    @BindView(R.id.tv_addresstype)
    TextView     tvAddresstype;
    @BindView(R.id.top2)
    LinearLayout top2;
    @BindView(R.id.tv_addressaddnum)
    TextView     tvAddressaddnum;
    @BindView(R.id.top3)
    LinearLayout top3;
    @BindView(R.id.top4)
    LinearLayout top4;
    @BindView(R.id.tophave3)
    LinearLayout topHave3;
    @BindView(R.id.item_add1)
    TextView     itemAdd1;
    @BindView(R.id.ll_index1)
    LinearLayout llIndex1;
    @BindView(R.id.item_add2)
    TextView     itemAdd2;
    @BindView(R.id.ll_index2)
    LinearLayout llIndex2;
    @BindView(R.id.item_add3)
    TextView     itemAdd3;
    @BindView(R.id.ll_index3)
    LinearLayout llIndex3;
    @BindView(R.id.item_add4)
    TextView     itemAdd4;
    @BindView(R.id.ll_index4)
    LinearLayout llIndex4;
    @BindView(R.id.ll_gray)
    View         llGray;
    @BindView(R.id.item_line2)
    View         itemLine2;
    @BindView(R.id.item_line3)
    View         itemLine3;
    @BindView(R.id.line_singleaddress_arrow)
    ImageView    lineSingleaddressArrow;
    @BindView(R.id.root_dialog)
    LinearLayout    rootDialog;
    @BindView(R.id.iv_top1_arrow)
    ImageView       ivTop1Arrow;
    @BindView(R.id.iv_top2_arrow)
    ImageView       ivTop2Arrow;
    @BindView(R.id.iv_top3_arrow)
    ImageView       ivTop3Arrow;
    @BindView(R.id.iv_top4_arrow)
    ImageView       ivTop4Arrow;
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
    @BindView(R.id.tv_enbleaddr2)
    TextView        tvEnbleaddr2;
    @BindView(R.id.tv_singleaddr_log)
    TextView        tvSingleaddrLog;
    @BindView(R.id.scroll_singleaddr_log)
    ScrollView        scrollSingleaddrLog;
    @BindView(R.id.rootfreeaddress)
    RelativeLayout  rootFreeAddress;

    /**
     * 小三角箭头的Y坐标
     */
    int linearrow_location_Y;
    /**
     * 当前点击的类型，
     * 1 设备类型； 2 协议类型； 3 地址累加数
     */
    int currentType;
    private ProgressDialog       progressdialog;
    /**
     * Notify 如果字节长度大于20.分包接收，暂存前一个包
     */
    private String   beforeString;

    /**
     * 编址类型，CLIP 10X(101,102)\DLIP 20X(201,202)\FlashScan 30X
     * 且当ADDRESSTYPE==0 是代表没有任何读写操作，空闲状态
     */
    private static          int     ADDRESSTYPE            = 0;
    /**
     * 写回路卡命令
     */
    private static final    int     LOOPCARDBATTERY     = 401;
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
//    /**
//     * DLIP 读取设备类型  1.25会议暂时去掉读取类型
//     */
//    private static final    int     NOTIFYDECODE_DLIP3     = 203;
    /**
     * Notify FlashScan解码完成
     */
    private static final    int     NOTIFYDECODE_FLASHSCAN = 301;
    /**
     * 循环读Clip第一条写
     */
    private static final    int     CONSEQUENCE_CLIP1     = 501;
    /**
     * 循环读Clip第二条验证
     */
    private static final    int     CONSEQUENCE_CLIP2     = 502;
    /**
     * 503（开始循环读） 因为循环读的规则和第二次验证读不一样，需要单独处理
     */
    private static final    int     CONSEQUENCE_CLIP3     = 503;
    /**
     * 循环读Dlip第一条写
     */
    private static final    int     CONSEQUENCE_DLIP1     = 601;
    /**
     * 循环读Dlip第二条验证
     */
    private static final    int     CONSEQUENCE_DLIP2     = 602;
    /**
     * 603 因为第二次验证读和循环读的规则不一样，需要单独处理
     */
    private static final    int     CONSEQUENCE_DLIP3     = 603;
    /**
     * 所有命令超时标志
     */
    private static volatile boolean TimeOutAll             = false;
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
    private boolean readingfinish=false;
    /**
     * 连续编址
     * consequent=0，连续编址停止状态,
     * consequent=1，连续编址开始状态,
     * consequent=2，连续编址暂停状态,
     * consequent=3，连续编址继续状态,
     */
    private int consequent=0;
    /**
     * 记录地址累加值
     */
    private int                  AccumulateValue;
    private TextView             left,right;
    private ImageView             leftIcon,rightIcon;
    private Thread               LoopCardBatteryThread;
    private BleDevice            bleDevice;
    private CommonBleUtil        commonBleUtil;
    private StringBuilder        sbuilder;
    private SimpleDateFormat     simpleDateFormat;
    private boolean              isAppExit;
    private List<RightTopBean> rightTopList=new ArrayList<>();
    private IndicatorDialog dialog;
    private int WriteTranslationX;
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode      = TtsMode.MIX;
    protected String  offlineVoice = OfflineResource.VOICE_MALE;

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================
    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;
    /**
     * DLIPtag==0  单独编址
     * DLIPtag==1  单独读址
     * DLIPtag==2  连续编址
     */
    protected int DLIPtag=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initialTts();
        }catch (Exception e){
        }
        initView();
        initBle();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_single_address_free_newui;
    }

    private void initView() {
        getToolbarTitle().setText("自由编址");
        getSubTitle().setVisibility(View.INVISIBLE);
        initCommonBleUtil();
        tvSingleaddrLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        left = ((TextView)btnSingleaddrRead.getChildAt(1));
        right = ((TextView)btnSingleaddrWrite.getChildAt(1));
        leftIcon = ((ImageView)btnSingleaddrRead.getChildAt(0));
        rightIcon = ((ImageView)btnSingleaddrWrite.getChildAt(0));
        tvProtocoltype.setText(SharePreferenceUtil.getStringSP("tvProtocoltype","协议类型"));
        tvDevicetype.setText(SharePreferenceUtil.getStringSP("tvDevicetype","设备类型"));
        tvAddresstype.setText(SharePreferenceUtil.getStringSP("tvAddresstype","编址类型"));
        tvAddressaddnum.setText(SharePreferenceUtil.getStringSP("tvAddressaddnum","地址累加"));
        JudgmentAddrExceed();
        if("连续编址".equals(SharePreferenceUtil.getStringSP("tvAddresstype","编址类型")))
        {showTop3();}
        if("DLIP".equals(tvProtocoltype.getText().toString())){
            //当选择DLIP时，不可点击
            top1.setClickable(false);
            tvDevicetype.setTextColor(ResourceUtil.getColor(R.color.gray_shadow));
        }
        progressdialog = new ProgressDialog(this, R.style.progressDialog);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sbuilder=new StringBuilder();
        ledviewClockTime.addTextChangedListener(textChangeListener);
        ledviewClockTime.postDelayed(new Runnable() {
            @Override
            public void run() {
                ledviewClockTime.setWidth(ledviewClockBg.getWidth());
                //对于刚跳到一个新的 界面就要弹出软键盘的情况可能由于界面为加载完全而无法弹出软键盘。此时应该适当的延迟弹出软键盘
                PhoneUtil.showInputWindow(SingleFreeAddressActivity.this,  tvEnbleaddr2);
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
                if (JudgmentAddrExceed().isFirst()) {
                    return;
                }
                if(Integer.parseInt(addrLess)<1){
                    return;
                }else{
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
                String addrAdd=ledviewClockTime.getText().toString();
                if(TextUtil.isEmpty(addrAdd)){
                    return;
                }
                if (JudgmentAddrExceed().isLast()) {
                    return;
                }
                ledviewClockTime.setText(String.valueOf(Integer.parseInt(addrAdd) + 1));
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
            if( readingfinish ){
                readingfinish = false;
            }else{
                setTips("");
            }
            String s2 = ledviewClockTime.getText().toString();
            int strLength = s2.length();
            int selectionEnd = ledviewClockTime.getSelectionEnd();
            JudgmentAddrExceed();
            if(strLength<=0){return;}
            if ("CLIP".equals(tvProtocoltype.getText().toString())) {
                if (tvDevicetype.getText().toString().equals("探头")) {
//                    setTips("地址范围：0-99");
                    if(strLength==2){
                        if(s2.charAt(0)=='0'){
                            editable.delete(0, 1);
                        }
                    }else if(strLength==3){
                        editable.delete(2, 3);
                    }
                } else if (tvDevicetype.getText().toString().equals("模块")) {
//                    setTips("地址范围：100-199");
                    if(strLength==1){
                        if(!"1".equals(s2)){
                            editable.delete(0, 1);
                        }
                    }else if(strLength==2){
                        if(s2.charAt(0)=='0'){
                            editable.delete(0, 1);
                        }
                        setTips("无效地址");
                    }else if(strLength==3){
                        if(Integer.parseInt(s2)>199){
                            editable.delete(2, 3);
                        }
                    }else if(strLength==4){
                        editable.delete(3, 4);
                    }
                }else if (tvDevicetype.getText().toString().equals("混编")) {
//                    setTips("地址范围：0-199");
                    if(strLength==2){
                        if(s2.charAt(0)=='0'){
                            editable.delete(0, 1);
                        }
                    }else if(strLength==3){
                        if(Integer.parseInt(s2)>199){
                            editable.delete(2, 3);
                        }
                    }else if(strLength==4){
                        editable.delete(3, 4);
                    }
                }
            } else if ("DLIP".equals(tvProtocoltype.getText().toString())) {
//                setTips("地址范围：1-239");
                //DLIP 从1开始
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
            }else if ("FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
                if (tvDevicetype.getText().toString().equals("探头")) {
//                    setTips("地址范围：0000-0159");
                    if(strLength==3){
                        if(Integer.parseInt(s2)>159){
                            editable.delete(2, 3);
                        }
                    }else if(strLength==4){
                        if(Integer.parseInt(s2)>159){
                            editable.delete(3, 4);
                        }
                    }
                } else if (tvDevicetype.getText().toString().equals("模块")) {
//                    setTips("地址范围：1000-1159");
                    if (strLength == 1) {
                        if (Integer.parseInt(s2) != 1) {
                            editable.delete(0, 1);
                        }
                    } else if (strLength == 2) {
                        if (Integer.parseInt(s2) > 11) {
                            editable.delete(1, 2);
                        }
                    } else if (strLength == 3) {
                        if (Integer.parseInt(s2) > 115) {
                            editable.delete(2, 3);
                        }
                    } else if (strLength == 4) {
                        if (Integer.parseInt(s2) > 1159) {
                            editable.delete(3, 4);
                        }
                    }
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
    private void TopRightClick(){
        rightTopList.clear();
        rightTopList.add(new RightTopBean(1));
        rightTopList.add(new RightTopBean(2));
        rightTopList.add(new RightTopBean(3));
        if(rightTopAdapter==null){
            rightTopAdapter= new RightTopAdapter(rightTopList,SingleFreeAddressActivity.this);
        }
        if(dialog==null){
            dialog = new IndicatorBuilder(SingleFreeAddressActivity.this)  //must be activity
                    .width(PhoneUtil.getScreenWidth(SingleFreeAddressActivity.this)*3/5)                           // the dialog width in px
                    .height(PhoneUtil.getScreenHeight(SingleFreeAddressActivity.this)/3)                          // the dialog max height in px or -1 (means auto fit)
                    .ArrowDirection(IndicatorBuilder.TOP)
                    .bgColor(getResources().getColor(R.color.white))
                    .dimEnabled(true)
                    .gravity(IndicatorBuilder.GRAVITY_RIGHT)
                    .radius(10)
                    .ArrowRectage(0.9f)
                    .layoutManager(new LinearLayoutManager(SingleFreeAddressActivity.this, LinearLayoutManager.VERTICAL, false))
                    .adapter(rightTopAdapter).create();
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.show(getMenuView());
        rightTopAdapter.setRightTopAudioOnCheckedChangeListener(new RightTopAdapter.RightTopAudioOnCheckedChangeListener() {
            @Override
            public void check(SwitchButton button, boolean isChecked) {
                if(isChecked){
                    SharePreferenceUtil.setBooleanSP("audio",true);
                }else{
                    SharePreferenceUtil.setBooleanSP("audio",false);
                }
            }
        });
    }
    /**
     * 初始化蓝牙连接操作
     */
    private void initBle() {
        bleDevice= BaseApplication.bleDevice;
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
                                DialogUtil.showAlertDialog(SingleFreeAddressActivity.this,
                                        "提示","蓝牙要求位置权限未开启，请在设置中打开位置权限",null,null);
                            }else if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                DialogUtil.showAlertDialog(SingleFreeAddressActivity.this,
                                        "提示","需要打开文件读写权限，请在设置中打开相关权限",null,null);
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
            BleManager.getInstance().readRssi(bleDevice,new BleRssiCallback() {
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
                                OpenNotify(true);
                                getLoopCardBatteryThread();
                                ivSingleaddressBlestate2.setImageResource(R.drawable.connect_50);
                                PhoneUtil.showInputWindow(SingleFreeAddressActivity.this, ledviewClockTime);
                                BleManager.getInstance().readRssi(bleDevice,new BleRssiCallback() {
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
        if(isDelay){
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

    @OnClick({R.id.top1, R.id.top2, R.id.top3, R.id.top4,R.id.ll_index1, R.id.ll_index2, R.id.ll_index3,R.id.ll_index4, R.id.ll_gray, R.id.root_dialog,
            R.id.iv_less, R.id.iv_add, R.id.btn_singleaddr_read, R.id.btn_singleaddr_write,
            R.id.ll_singleaddress_top})
    public void onViewClicked(View view) {
        if(view.getId()==R.id.top3){
            llIndex4.setVisibility(View.VISIBLE);
            itemLine3.setVisibility(View.VISIBLE);
        }else{
            llIndex4.setVisibility(View.GONE);
            itemLine3.setVisibility(View.GONE);
        }
        switch (view.getId()) {
            case R.id.ll_singleaddress_top:
                //失败重连
                if (!BleManager.getInstance().isConnected(bleDevice)){
                    Scan();
                }
                break;
            case R.id.top1:
                currentType = 1;
                ivTop1Arrow.setImageResource(R.drawable.arrow_down_red_50);
                ivTop2Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop3Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop4Arrow.setImageResource(R.drawable.arrow_up_black_50);
                if("连续编址".equals(tvAddresstype.getText().toString())){
                    lineSingleaddressArrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_line_2));
                }else{
                    lineSingleaddressArrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_line_center));
                }
                itemAdd3.setVisibility(View.VISIBLE);
                itemLine2.setVisibility(View.VISIBLE);
                itemAdd1.setGravity(Gravity.LEFT);
                itemAdd2.setGravity(Gravity.LEFT);
                itemAdd3.setGravity(Gravity.LEFT);
                int[] location1=new int[2];
                tvDevicetype.getLocationInWindow(location1);
                itemAdd1.setPadding(location1[0], itemAdd1.getPaddingTop(), 0, itemAdd1.getPaddingBottom());
                itemAdd2.setPadding(location1[0], itemAdd2.getPaddingTop(), 0, itemAdd2.getPaddingBottom());
                itemAdd3.setPadding(location1[0], itemAdd3.getPaddingTop(), 0, itemAdd3.getPaddingBottom());
                itemAdd1.setText("探头");
                itemAdd2.setText("模块");
                itemAdd3.setText("混编");
                if(itemAdd1.getText().toString().equals(tvDevicetype.getText().toString())){
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }else if(itemAdd2.getText().toString().equals(tvDevicetype.getText().toString())){
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }else if(itemAdd3.getText().toString().equals(tvDevicetype.getText().toString())){
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                }else{
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }
                if("FLASHSCAN".equals(tvProtocoltype.getText().toString())){
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_shadow));
                    llIndex3.setClickable(false);
                }else{
                    if(itemAdd3.getText().toString().equals(tvDevicetype.getText().toString())){
                        itemAdd3.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    }else{
                        itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    }
                    llIndex3.setClickable(true);
                }
                setDialogMargin();
                break;
            case R.id.top2:
                currentType = 2;
                ivTop1Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop2Arrow.setImageResource(R.drawable.arrow_down_red_50);
                ivTop3Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop4Arrow.setImageResource(R.drawable.arrow_up_black_50);
                if("连续编址".equals(tvAddresstype.getText().toString())){
                    lineSingleaddressArrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_line_1));
                }else{
                    lineSingleaddressArrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_line_left));
                }
                itemAdd3.setVisibility(View.VISIBLE);
                itemLine2.setVisibility(View.VISIBLE);
                itemAdd1.setGravity(Gravity.LEFT);
                itemAdd2.setGravity(Gravity.LEFT);
                itemAdd3.setGravity(Gravity.LEFT);
                int[] location2=new int[2];
                tvProtocoltype.getLocationInWindow(location2);
                itemAdd1.setPadding(location2[0], itemAdd1.getPaddingTop(), 0, itemAdd1.getPaddingBottom());
                itemAdd2.setPadding(location2[0], itemAdd2.getPaddingTop(), 0, itemAdd2.getPaddingBottom());
                itemAdd3.setPadding(location2[0], itemAdd3.getPaddingTop(), 0, itemAdd3.getPaddingBottom());
                itemAdd1.setText("CLIP");
                itemAdd2.setText("DLIP");
                itemAdd3.setText("FLASHSCAN");
                if(itemAdd1.getText().toString().equals(tvProtocoltype.getText().toString())){
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }else if(itemAdd2.getText().toString().equals(tvProtocoltype.getText().toString())){
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }else if(itemAdd3.getText().toString().equals(tvProtocoltype.getText().toString())){
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }else{
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }
                if("混编".equals(tvDevicetype.getText().toString())){
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_shadow));
                    llIndex3.setClickable(false);
                }else{
                    if(itemAdd3.getText().toString().equals(tvProtocoltype.getText().toString())){
                        itemAdd3.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    }else{
                        itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    }
                    llIndex3.setClickable(true);
                }
                setDialogMargin();
                break;
            case R.id.top3:
                currentType = 3;
                ivTop1Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop2Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop3Arrow.setImageResource(R.drawable.arrow_down_red_50);
                ivTop4Arrow.setImageResource(R.drawable.arrow_up_black_50);
                if("连续编址".equals(tvAddresstype.getText().toString())){
                    lineSingleaddressArrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_line_4));
                }else{
                    lineSingleaddressArrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_line_right));
                }
                itemAdd3.setVisibility(View.VISIBLE);
                itemAdd4.setVisibility(View.VISIBLE);
                itemLine2.setVisibility(View.VISIBLE);
                itemLine3.setVisibility(View.VISIBLE);
                itemAdd1.setGravity(Gravity.LEFT);
                itemAdd2.setGravity(Gravity.LEFT);
                itemAdd3.setGravity(Gravity.LEFT);
                itemAdd4.setGravity(Gravity.LEFT);
                int[] location=new int[2];
                tvAddressaddnum.getLocationInWindow(location);
                itemAdd1.setPadding((int)location[0], itemAdd1.getPaddingTop(), 0, itemAdd1.getPaddingBottom());
                itemAdd2.setPadding((int)location[0], itemAdd2.getPaddingTop(), 0, itemAdd2.getPaddingBottom());
                itemAdd3.setPadding((int)location[0], itemAdd3.getPaddingTop(), 0, itemAdd3.getPaddingBottom());
                itemAdd4.setPadding((int)location[0], itemAdd4.getPaddingTop(), 0, itemAdd4.getPaddingBottom());
                itemAdd1.setText("-2");
                itemAdd2.setText("-1");
                itemAdd3.setText("+1");
                itemAdd4.setText("+2");
                if(itemAdd1.getText().toString().equals(tvAddressaddnum.getText().toString())){
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd4.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }else if(itemAdd2.getText().toString().equals(tvAddressaddnum.getText().toString())){
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd4.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }else if(itemAdd3.getText().toString().equals(tvAddressaddnum.getText().toString())){
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd4.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }else if(itemAdd4.getText().toString().equals(tvAddressaddnum.getText().toString())){
                    itemAdd4.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }else{
                    itemAdd4.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd3.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }
                llIndex3.setClickable(true);
                setDialogMargin();
                break;
            case R.id.top4:
                currentType = 4;
                ivTop1Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop2Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop3Arrow.setImageResource(R.drawable.arrow_up_black_50);
                ivTop4Arrow.setImageResource(R.drawable.arrow_down_red_50);
                if("连续编址".equals(tvAddresstype.getText().toString())){
                    lineSingleaddressArrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_line_3));
                }else{
                    lineSingleaddressArrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_line_right));
                }
                itemAdd3.setVisibility(View.GONE);
                itemLine2.setVisibility(View.GONE);
                itemAdd1.setGravity(Gravity.LEFT);
                itemAdd2.setGravity(Gravity.LEFT);
                itemAdd3.setGravity(Gravity.LEFT);
                int[] location4=new int[2];
                tvAddresstype.getLocationInWindow(location4);
                itemAdd1.setPadding((int)location4[0], itemAdd1.getPaddingTop(), 0, itemAdd1.getPaddingBottom());
                itemAdd2.setPadding((int)location4[0], itemAdd2.getPaddingTop(), 0, itemAdd2.getPaddingBottom());
                itemAdd3.setPadding((int)location4[0], itemAdd3.getPaddingTop(), 0, itemAdd3.getPaddingBottom());
                itemAdd1.setText("单次编址");
                itemAdd2.setText("连续编址");
                if(itemAdd1.getText().toString().equals(tvAddresstype.getText().toString())){
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }else if(itemAdd2.getText().toString().equals(tvAddresstype.getText().toString())){
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }else{
                    itemAdd1.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    itemAdd2.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                }
                setDialogMargin();
                break;
            case R.id.ll_index1:
                setTips("");
                if (currentType == 1) {
                    //探头
                    tvDevicetype.setText(itemAdd1.getText());
                    SharePreferenceUtil.setStringSP("tvDevicetype",itemAdd1.getText().toString());
                } else if (currentType == 2) {
                    //当选择CLIP时，可点击
                    top1.setClickable(true);
                    tvDevicetype.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    tvProtocoltype.setText(itemAdd1.getText());
                    SharePreferenceUtil.setStringSP("tvProtocoltype",itemAdd1.getText().toString());
                    String clip=ledviewClockTime.getText().toString();
                    if(clip.length()==4){
                        clip=clip.substring(0,3);
                        ledviewClockTime.setText(clip);
                    }
                } else if (currentType == 3) {
                    tvAddressaddnum.setText(itemAdd1.getText());
                    SharePreferenceUtil.setStringSP("tvAddressaddnum",itemAdd1.getText().toString());
                    AccumulateValue=Integer.parseInt(itemAdd1.getText().toString());
                }else if (currentType == 4) {
                    tvAddresstype.setText(itemAdd1.getText());
                    SharePreferenceUtil.setStringSP("tvAddresstype",itemAdd1.getText().toString());
                    hideTop3();
                }
                rootDialog.setVisibility(View.GONE);
                lineSingleaddressArrow.setImageResource(R.drawable.arrow_line);
                currentType = 0;
                JudgmentAddrExceed();
                restoreArrow();
                break;
            case R.id.ll_index2:
                setTips("");
                if (currentType == 1) {
                    //模块
                    tvDevicetype.setText(itemAdd2.getText());
                    SharePreferenceUtil.setStringSP("tvDevicetype",itemAdd2.getText().toString());
                    JudgmentAddrExceed();
                } else if (currentType == 2) {
                    //当选择DLIP时，不可点击
                    top1.setClickable(false);
                    tvDevicetype.setTextColor(ResourceUtil.getColor(R.color.gray_shadow));
                    tvProtocoltype.setText(itemAdd2.getText());
                    SharePreferenceUtil.setStringSP("tvProtocoltype",itemAdd2.getText().toString());
                    String clip=ledviewClockTime.getText().toString();
                    if(clip.length()==4){
                        clip=clip.substring(0,3);
                        ledviewClockTime.setText(clip);
                    }
                    JudgmentAddrExceed();
                } else if (currentType == 3) {
                    tvAddressaddnum.setText(itemAdd2.getText());
                    SharePreferenceUtil.setStringSP("tvAddressaddnum",itemAdd2.getText().toString());
                    AccumulateValue=Integer.parseInt(itemAdd2.getText().toString());
                    JudgmentAddrExceed();
                }else if (currentType == 4){
                    tvAddresstype.setText(itemAdd2.getText());
                    SharePreferenceUtil.setStringSP("tvAddresstype",itemAdd2.getText().toString());
                    showTop3();
                }
                rootDialog.setVisibility(View.GONE);
                lineSingleaddressArrow.setImageResource(R.drawable.arrow_line);
                currentType = 0;
                restoreArrow();
                break;
            case R.id.ll_index3:
                setTips("");
                if (currentType == 1) {
                    //混编
                    tvDevicetype.setText(itemAdd3.getText());
                    SharePreferenceUtil.setStringSP("tvDevicetype",itemAdd3.getText().toString());
                } else if (currentType == 2) {
                    //FLASHSCAN，可点击
                    top1.setClickable(true);
                    tvDevicetype.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    tvProtocoltype.setText(itemAdd3.getText());
                    SharePreferenceUtil.setStringSP("tvProtocoltype",itemAdd3.getText().toString());
                    String addr=ledviewClockTime.getText().toString();

                } else if (currentType == 3) {
                    tvAddressaddnum.setText(itemAdd3.getText());
                    SharePreferenceUtil.setStringSP("tvAddressaddnum",itemAdd3.getText().toString());
                    AccumulateValue=Integer.parseInt(itemAdd3.getText().toString());
                }
                rootDialog.setVisibility(View.GONE);
                lineSingleaddressArrow.setImageResource(R.drawable.arrow_line);
                currentType = 0;
                JudgmentAddrExceed();
                restoreArrow();
                break;
            case R.id.ll_index4:
                tvAddressaddnum.setText(itemAdd4.getText());
                SharePreferenceUtil.setStringSP("tvAddressaddnum",itemAdd4.getText().toString());
                AccumulateValue=Integer.parseInt(itemAdd4.getText().toString());
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
                String addrless=ledviewClockTime.getText().toString();
                if(TextUtil.isEmpty(addrless)){
                    return;
                }
                if (JudgmentAddrExceed().isFirst()) {
                    return;
                }
                int  less = parseInt(addrless);
                int lessNext = 0;
                //先别动，刘说地址累加与加减号没关系
//                if("3".equals(tvAddressaddnum.getText().toString())){
//                    if ( less > 3) {
//                        lessNext = less - 3;
//                    }else{
//                        return;
//                    }
//                }else if("2".equals(tvAddressaddnum.getText().toString())){
//                        if ( less > 2) {
//                            lessNext = less - 2;
//                        }else{
//                            return;
//                        }
//                    }
//                else {
                if ( less > 0) {
                    lessNext = less - 1;
                }else{
                    return;
                }
//            }
                ledviewClockTime.setText(String.valueOf(lessNext));
                ledviewClockTime.setSelection(ledviewClockTime.getText().length());
                break;
            case R.id.iv_add:
                String addradd=ledviewClockTime.getText().toString();
                if(TextUtil.isEmpty(addradd)){
                    return;
                }
                if (JudgmentAddrExceed().isLast()) {
                    return;
                }
                int  add = parseInt(addradd);
                int addNext=0;
//                if("3".equals(tvAddressaddnum.getText().toString())){
//                    addNext = add + 3;
//                }else if("2".equals(tvAddressaddnum.getText().toString())){
//                    addNext = add + 2;
//                } else{
                addNext = add + 1;
//                    }
                ledviewClockTime.setText(String.valueOf(addNext));
                ledviewClockTime.setSelection(ledviewClockTime.getText().length());
                break;
            case R.id.btn_singleaddr_read:
                //读址
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    //  setTips("编址器未连接，点击顶部红色区域重连");
                    return;
                }
                //在每次点击按钮时清空上次tips内容
                setTips("");
                if("停止".equals(right.getText().toString())){
                    if("暂停".equals(left.getText().toString())){
                        left.setText("继续");
                        leftIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_kaishi_40));
                        //暂停连续编址
                        pauseConsequent(false);
                    }else if("继续".equals(left.getText().toString())){
                        left.setText("暂停");
                        leftIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_zanting_40));
                        //继续连续编址
                        continueConsequent();
                    }
                }
//                else if((left.getTag()==null ? "" :(String)left.getTag()).equals("stop") && "继续".equals(left.getText().toString())){
//                    left.setTag(null);
//                    right.setText("开始");
//                    rightIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_kaishi_40));
//                    //初始化为连续编址的初始状态
//                    btnSingleaddrRead.setVisibility(View.INVISIBLE);
//                    openTranslationWrite();
//                    ledviewClockTime.setText("");
//                    speak("请输入起始地址",true);
//                    setTips("请输入起始地址");
//                }
                else if("读取地址".equals(left.getText().toString())){
                    initRead();
                }
                break;
            case R.id.btn_singleaddr_write:
                //编址
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    // setTips("编址器未连接，点击顶部红色区域重连");
                    return;
                }
                //初始化顶部每项的选择
                if(!InitTopParams()){
                    return;
                }
                //在每次点击按钮时清空上次tips内容
                if(JudgmentAddrExceed().isExceed()){
                    return;
                }
                if("连续编址".equals(tvAddresstype.getText().toString())){
                    if(TextUtil.isEmpty(ledviewClockTime.getText().toString())){
                        setTips("请输入起始地址");
                        speak("请输入起始地址",true);
                        return;
                    }

                    if("开始".equals(right.getText().toString())){
                        btnSingleaddrRead.setVisibility(View.VISIBLE);
                        right.setText("停止");
                        left.setText("暂停");
                        rightIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_tingzhi_40));
                        leftIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_zanting_40));
                        //开始连续编址
                        beginConsequent();
                        buttonLimit();
                        PhoneUtil.hideInputWindow(SingleFreeAddressActivity.this,ledviewClockTime);
                        closeTranslationWrite();
                    }else if("停止".equals(right.getText().toString())){
                        //停止连续编址
                        stopConsequent("连续编址停止",true);
                        right.setText("结束");
                        rightIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_jieshu_40));
                        btnSingleaddrRead.setVisibility(View.INVISIBLE);
                        openTranslationWrite();
                    }else if("结束".equals(right.getText().toString())){
                        //停止连续编址
                        stopConsequent("连续编址结束",true);
                        right.setText("开始");
                        rightIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_kaishi_40));
                        //初始化为连续编址的初始状态
                        ledviewClockTime.setText("");
                        speak("请输入起始地址",false);
                        setTips("请输入起始地址");
                    }
                }else{
                    //单次编址
                    initWrite();
                }
                break;
            default:
        }
    }

    private boolean InitTopParams() {
        if("协议类型".equals(tvProtocoltype.getText().toString())){
            setTips("请选择协议类型");
            return false;
        }
        //CLIP 要选择设备类型
        if("CLIP".equals(tvProtocoltype.getText().toString())){
            if("设备类型".equals(tvDevicetype.getText().toString())){
                setTips("请选择设备类型");
                return false;
            }
        }
        if("编址类型".equals(tvAddresstype.getText().toString())){
            setTips("请选择编址类型");
            return false;
        }
        if("连续编址".equals(tvAddresstype.getText().toString())){
            if("地址累加".equals(tvAddressaddnum.getText().toString())){
                setTips("请选择地址累加值");
                return false;
            }
        }
        return true;
    }

    private void showTop3() {
        final int ScreenWidth=PhoneUtil.getScreenWidth(SingleFreeAddressActivity.this);
        LinearLayout.LayoutParams ly=(LinearLayout.LayoutParams)topHave3.getLayoutParams();
        ly.width=ScreenWidth*3/4;
        topHave3.setLayoutParams(ly);
        btnSingleaddrRead.setVisibility(View.INVISIBLE);
        if(TextUtil.isEmpty(ledviewClockTime.getText().toString())){
            setTips("请输入起始地址");
            speak("请输入起始地址",true);
        }else{
            ledviewClockTime.setText("");
        }
        right.setText("开始");
        rightIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_kaishi_40));
        // 偶尔有时候获取的topHave3_location_Y不带状态栏高度，导致高度小大约60，
        // 因此导致top3显示出来偏离了想要的位置
        //所以为了更大限度的降低这种情况出现的概率，此处获取两次高度，选用更大的那一个
        final int[] location = new int[2];
        topHave3.getLocationOnScreen(location);
        int topHave3_location_Y = location[1];
        final int[] location_Ys=new int[2];
        location_Ys[0]=topHave3_location_Y;
        topHave3.postDelayed(new Runnable() {
            @Override
            public void run() {
                //如果直接设置的话，此时topHave3宽度还没变，so post
                final int[] location = new int[2];
                topHave3.getLocationOnScreen(location);
                int topHave3_location_X = location[0];
                int topHave3_location_Y = location[1];
                location_Ys[1]=topHave3_location_Y;
                LogUtil.e("topHave3_location_Y:"+topHave3_location_Y);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) top3.getLayoutParams();
                lp.width=ScreenWidth/4;
                lp.height=topHave3.getHeight();
                lp.topMargin =  Math.max(location_Ys[0],location_Ys[1]);
                lp.leftMargin = topHave3_location_X+topHave3.getWidth();
                top3.setLayoutParams(lp);
                top3.setVisibility(View.VISIBLE);
                //左移到中间位置
                openTranslationWrite();
            }
        },50);
    }
    private void hideTop3() {
        LinearLayout.LayoutParams ly=(LinearLayout.LayoutParams)topHave3.getLayoutParams();
        ly.width=PhoneUtil.getScreenWidth(SingleFreeAddressActivity.this);
        topHave3.setLayoutParams(ly);
        RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(0,0);
        top3.setVisibility(View.GONE);
        top3.setLayoutParams(lp);
        btnSingleaddrRead.setVisibility(View.VISIBLE);
        tvAddresstype.setText("单次编址");
        left.setText("读取地址");
        leftIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.duqu_40));
        right.setText("编写地址");
        rightIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.bianxie_40));
        closeTranslationWrite();
    }

    public void openTranslationWrite(){
        final int[] locationWrite = new int[2];
        btnSingleaddrWrite.getLocationInWindow(locationWrite);
        int WriteX = locationWrite[0];
        if(WriteX>PhoneUtil.getScreenWidth(SingleFreeAddressActivity.this)/2){
            WriteTranslationX=(WriteX-PhoneUtil.getScreenWidth(SingleFreeAddressActivity.this)/2+btnSingleaddrWrite.getWidth()/2);
            ObjectAnimator animator = ObjectAnimator.ofFloat(btnSingleaddrWrite, "translationX", 0f, -WriteTranslationX);
            animator.setDuration(10);
            animator.start();
        }
    }
    public void closeTranslationWrite(){
        final int[] locationWrite = new int[2];
        btnSingleaddrWrite.getLocationInWindow(locationWrite);
        int WriteX = locationWrite[0];
        if(WriteX<PhoneUtil.getScreenWidth(SingleFreeAddressActivity.this)/2){
            ObjectAnimator animator = ObjectAnimator.ofFloat(btnSingleaddrWrite, "translationX", -WriteTranslationX,0f);
            animator.setDuration(10);
            animator.start();
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
                if(ADDRESSTYPE==401)
                {
                    ToastUtil.showToastShort("发送电池电量命令:"+result);

                }
                LogUtil.e("Write--->" + result);
            }

            @Override
            public void notifyOnResult(boolean result) {
                LogUtil.e("Notify--->" + result);
            }

            @Override
            public void notifyOnSuccess(String values, String UUID) {
                TimeOutAll = false;
                boolean result ;
                //数据长度大于20字节且不是以0x55 0x5A结尾则分包接收了，下一个包为剩下的数据
                if (values.length() == 40 && !"555A".equals(values.substring(36, 50))) {
                    beforeString = values;
                    return;
                }
                //数据不是以55 AA开头，则为剩下的数据
                if (!TextUtil.isEmpty(beforeString)) {
                    values = beforeString + values;
                    beforeString = null;
                }
                if (ADDRESSTYPE == 101) {
                    result = DataHandler.CLIP_READ(values, 1);
                    if (result) {
                        handler.sendEmptyMessage(NOTIFYDECODE_CLIP1);
                    } else {
                        ADDRESSTYPE = 0;
                        setTips("编写地址"+ledviewClockTime.getText().toString()+"失败");
                        String detail="CLIP编写地址"+ledviewClockTime.getText().toString()+"失败"+"；\n";
                        sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                        tvSingleaddrLog.setText(sbuilder.toString());
                        scrollToBottom(scrollSingleaddrLog,tvSingleaddrLog);
                        saveLog("write",ledviewClockTime.getText().toString(),"CLIP","失败",detail);
                        speak("编写地址"+ledviewClockTime.getText().toString()+",失败",true);
                    }
                } else if (ADDRESSTYPE == 102) {
                    //收到消息，则未超时，正常执行解码操作
                    result = DataHandler.CLIP_READ(values, 2);
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
                        setTips("编写地址"+ledviewClockTime.getText().toString()+"失败");
                        String detail="DLIP编写地址"+ledviewClockTime.getText().toString()+"失败"+"；\n";
                        sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                        tvSingleaddrLog.setText(sbuilder.toString());
                        scrollToBottom(scrollSingleaddrLog,tvSingleaddrLog);
                        saveLog("write",ledviewClockTime.getText().toString(),"DLIP","失败",detail);
                        speak("编写地址"+ledviewClockTime.getText().toString()+",失败",true);
                    }
                } else if (ADDRESSTYPE == 202) {
                    result = DataHandler.DLIP_READ(values, 2);
                    Message msg = handler.obtainMessage();
                    msg.what = NOTIFYDECODE_DLIP2;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", result);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
//                else if (ADDRESSTYPE == 203) {
//                    Message msg = handler.obtainMessage();
//                    msg.what = NOTIFYDECODE_DLIP3;
//                    Bundle bundle = new Bundle();
//                    String results = DataHandler.DLIP_READDeviceType(values);
//                    bundle.putString("result", results);
//                    msg.setData(bundle);
//                    handler.sendMessage(msg);
//                    if (progressdialog != null && progressdialog.isShowing()) {
//                        progressdialog.dismiss();
//                    }
//                }
                else if (ADDRESSTYPE == 301) {

                }else if (ADDRESSTYPE == 401) {
                    ToastUtil.showToastShort("接收电池电量:"+DataHandler.LOOPCARDBattery_READ(values));
                    setBatteryIcon(DataHandler.LOOPCARDBattery_READ(values));
                }else if (ADDRESSTYPE == 501) {
                    result = DataHandler.CLIP_READLOOP(values,1);
                    Message msg = handler.obtainMessage();
                    msg.what = CONSEQUENCE_CLIP1;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", result);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }else if (ADDRESSTYPE == 502) {
                    result = DataHandler.CLIP_READLOOP(values,2);
                    Message msg = handler.obtainMessage();
                    msg.what = CONSEQUENCE_CLIP2;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", result);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }else if (ADDRESSTYPE == 503) {
                    result = DataHandler.CLIP_READLOOP(values,3);
                    Message msg = handler.obtainMessage();
                    msg.what = CONSEQUENCE_CLIP3;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", result);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    LogUtil.e(values);
                }else if (ADDRESSTYPE == 601) {
                    result = DataHandler.DLIP_READLOOP(values,1);
                    Message msg = handler.obtainMessage();
                    msg.what = CONSEQUENCE_DLIP1;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", result);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }else if (ADDRESSTYPE == 602) {
                    result = DataHandler.DLIP_READLOOP(values,2);
                    Message msg = handler.obtainMessage();
                    msg.what = CONSEQUENCE_DLIP2;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", result);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }else if (ADDRESSTYPE == 603) {
                    result = DataHandler.DLIP_READLOOP(values,3);
                    Message msg = handler.obtainMessage();
                    msg.what = CONSEQUENCE_DLIP3;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", result);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    //分组事件搜索
                }else if (ADDRESSTYPE == 1000) {
                    sbuilder.append("收到命令一："+values+"\n");
                    tvSingleaddrLog.setText(sbuilder.toString());
                    scrollToBottom(scrollSingleaddrLog,tvSingleaddrLog);
                    int resultCode = DataHandler.DLIP_READGroupQuery(values);
                    if(resultCode==-1){
                        if(DLIPtag==2){
                            //连续编址情况下存在多个组，需要停止编址
                            //停止连续编址
                            stopConsequent("连续编址停止",true);
                            right.setText("结束");
                            rightIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_jieshu_40));
                            btnSingleaddrRead.setVisibility(View.INVISIBLE);
                            openTranslationWrite();
                        }
                        setTips("存在多个探头，无法编址");
                    }else if(resultCode==-3){
                        //全0 无探头，请接上探头再编址
                        setTips("无探头，请接上探头再编址");
                    }else if(resultCode==-2){
                        //发生故障
                        ToastUtil.showToastShort("故障，命令错误");
                    }else{
                        ADDRESSTYPE = 1001;
                        String command = DataHandler.groupQuery(Integer.toHexString(resultCode));
                        sbuilder.append("发送命令二："+command+"\n");
                        tvSingleaddrLog.setText(sbuilder.toString());
                        scrollToBottom(scrollSingleaddrLog,tvSingleaddrLog);
                        commonBleUtil.writeDevice(bleDevice,command);
                    }
                }else if (ADDRESSTYPE == 1001) {
                    sbuilder.append("收到命令二："+values+"\n");
                    tvSingleaddrLog.setText(sbuilder.toString());
                    scrollToBottom(scrollSingleaddrLog,tvSingleaddrLog);
                    int resultCode = DataHandler.DLIP_READGroupQuery(values);
                    if(resultCode==-1){
                        if(DLIPtag==2){
                            //连续编址情况下存在多个探头，需要停止编址
                            //停止连续编址
                            stopConsequent("连续编址停止",true);
                            right.setText("结束");
                            rightIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_jieshu_40));
                            btnSingleaddrRead.setVisibility(View.INVISIBLE);
                            openTranslationWrite();
                        }
                        //存在多个探头
                        setTips("存在多个探头，无法编址");
                    }else if(resultCode==-3){
                        //全0 无探头，请接上探头再编址
                        setTips("无探头，请接上探头再编址");
                    }else if(resultCode==-2){
                        //发生故障
                        ToastUtil.showToastShort("故障，命令错误");
                    }else{
                        //Dlip正式开始
                        if(DLIPtag==0){
                            //DLIP单独编址
                            DLIPWrite(201,DLIPcommand);
                        }else if(DLIPtag==1){
                            //DLIP单独读址
                            DLIPREAD(202,DLIPcommand);
                        }else if(DLIPtag==2){
                            //DLIP连续编址
                            DLIPLOOPWrite(DLIPcommand);
                        }
                    }
                }
            }
        });
    }
    /**
     * 开始编址
     */
    private void initWrite() {

        if ("CLIP".equals(tvProtocoltype.getText().toString())) {
            ADDRESSTYPE=101;
            Writing("CLIP", ADDRESSTYPE, 1);
        }else  if ("DLIP".equals(tvProtocoltype.getText().toString())) {
            ADDRESSTYPE=201;
            Writing("DLIP", ADDRESSTYPE, 1);
        }else  if ("FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
            ADDRESSTYPE=301;
            Writing("FLASHSCAN", ADDRESSTYPE, 1);
        }
    }
    /**
     * 开始编址
     */
    String DLIPcommand = null;
    private void Writing(String protocolType, int type, int index) {
        SystemUtil.vibrate(SingleFreeAddressActivity.this,100);
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ("LOOPCARD".equals(protocolType)) {
            //获取回路卡电池电量
            ADDRESSTYPE = type;
            commonBleUtil.writeDevice(bleDevice,DataHandler.LOOPCARDBattery_WRITE());
            return;
        }
        String addrstr = ledviewClockTime.getText().toString();
        if (addrstr == null || "".equals(addrstr)) {
            return;
        }
//        OpenNotify(true);
        String command = null;
        if ("CLIP".equals(protocolType)) {
            if(tvDevicetype.getText().toString().equals("探头")){
                if(Integer.parseInt(addrstr)>99 || Integer.parseInt(addrstr)<1){
                    setTips("地址超出可用范围");
                    return;
                }
            }else  if(tvDevicetype.getText().toString().equals("模块")){
                if (Integer.parseInt(addrstr) > 199 ) {
                    setTips("地址超出可用范围");
                    return;
                }else  if (Integer.parseInt(addrstr) < 100 ){
                    setTips("地址低于可用范围");
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
            ADDRESSTYPE = type;
            startCommandTimeOut("WRITE");
            commonBleUtil.writeDevice(bleDevice,command);
        } else if ("DLIP".equals(protocolType)) {
            if(Integer.parseInt(addrstr)>239 || Integer.parseInt(addrstr)<1){
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
            if(index==1){
                DLIPtag=0;
                DLIPcommand=command;
                groupQuery();
            }else{
                ADDRESSTYPE = type;
                startCommandTimeOut("WRITE");
                commonBleUtil.writeDevice(bleDevice,command);
            }
        } else if ("FLASHSCAN".equals(protocolType)) {
            //FlashScan,第index条命令
        }
    }
    /**
     * 分组事件搜索，避免单独编址时有可能悬挂多个探头
     */
    private void groupQuery(){
        ADDRESSTYPE=1000;
        String command = DataHandler.groupQuery("0F");
        sbuilder.append("发送命令一："+command+"\n");
        tvSingleaddrLog.setText(sbuilder.toString());
        scrollToBottom(scrollSingleaddrLog,tvSingleaddrLog);
        commonBleUtil.writeDevice(bleDevice,command);
    }
    /**
     * 由于DLIP的特殊性，在编址前需要进行分组事件查询，因此需要把编址单独提取出来
     */
    private void DLIPWrite(int type,String command){
        ADDRESSTYPE = type;
        startCommandTimeOut("WRITE");
        commonBleUtil.writeDevice(bleDevice,command);
    }
    /**
     * 由于DLIP的特殊性，在编址前需要进行分组事件查询，因此需要把读址单独提取出来
     */
    private void DLIPREAD(int type,String command){
        ADDRESSTYPE = 202;
        startCommandTimeOut("READ");
        commonBleUtil.writeDevice(bleDevice,command);
    }
    /**
     * 由于DLIP的特殊性，在编址前需要进行分组事件查询，因此需要把读址单独提取出来
     */
    private void DLIPLOOPWrite(String command){
        ADDRESSTYPE = 601;
        commonBleUtil.writeDevice(bleDevice,command);
    }

    /**
     * 单独读址
     */
    private void initRead() {
        if(!"CLIP".equals(tvProtocoltype.getText().toString()) &&
                !"DLIP".equals(tvProtocoltype.getText().toString()) &&
                !"FLASHSCAN".equals(tvProtocoltype.getText().toString())){
            setTips("请选择协议类型");
            return;
        }
        if ("CLIP".equals(tvProtocoltype.getText().toString())) {
            Reading("CLIP");
        }else  if ("DLIP".equals(tvProtocoltype.getText().toString())) {
            Reading("DLIP");
        }else  if ("FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
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
        SystemUtil.vibrate(SingleFreeAddressActivity.this,100);
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
            timeClip = System.currentTimeMillis();
//            startClipTimeOut();
            ADDRESSTYPE = 102;
            startCommandTimeOut("READ");
            //00 无意义
            command = DataHandler.CLIP_WRITE("00", 2, true);
            commonBleUtil.writeDevice(bleDevice,command);
        } else if ("DLIP".equals(protocolType)) {

            //"FF"  ==  255 十进制
            command = DataHandler.DLIP_WRITE("00", "255", 2);
            DLIPcommand=command;
            DLIPtag=1;
            groupQuery();
        }
//        else if ("DLIPTYPE".equals(protocolType)) {
//            //读取设备类型命令
//            ADDRESSTYPE = 203;
//            //"FF"  ==  255 十进制
//            command = DataHandler.DLIP_WRITE("00", "00",3);
//            commonBleUtil.writeDevice(bleDevice,command);
//
//        }
        else if ("FLASHSCAN".equals(protocolType)) {
            //FlashScan,第index条命令
        }
    }

    public JudgementExceedStruts JudgmentAddrExceed() {
        JudgementExceedStruts jes=new JudgementExceedStruts();
        String protocol = tvProtocoltype.getText().toString();
        String addrstr = ledviewClockTime.getText().toString();
        if ("CLIP".equals(protocol)) {
            if (tvDevicetype.getText().toString().equals("探头")) {
                if(TextUtil.isEmpty(addrstr)){
                    setTips("地址范围：0-99");
                }else{
                    if (Integer.parseInt(addrstr) > 99 || Integer.parseInt(addrstr) < 0) {
                        setTips("地址超出可用范围：0-99");
                        jes.setExceed(true);
                    }else if(Integer.parseInt(addrstr)==99){
                        jes.setLast(true);
                        setTips("地址范围：0-99");
                    }else if(Integer.parseInt(addrstr)==0){
                        jes.setFirst(true);
                        setTips("地址范围：0-99");
                    } else{
                        setTips("地址范围：0-99");
                    }
                }
            } else if (tvDevicetype.getText().toString().equals("模块")) {
                if(TextUtil.isEmpty(addrstr)){
                    setTips("地址范围：100-199");
                }else {
                    if (Integer.parseInt(addrstr) > 199) {
                        setTips("地址超出可用范围：100-199");
                        jes.setExceed(true);
                    } else if (Integer.parseInt(addrstr) < 100) {
                        setTips("地址低于可用范围：100-199");
                        jes.setExceed(true);
                    } else if(Integer.parseInt(addrstr)==100){
                        jes.setFirst(true);
                        setTips("地址范围：100-199");
                    }else if(Integer.parseInt(addrstr)==199){
                        jes.setLast(true);
                        setTips("地址范围：100-199");
                    }else {
                        setTips("地址范围：100-199");
                    }
                }
            } else if (tvDevicetype.getText().toString().equals("混编")) {
                if(TextUtil.isEmpty(addrstr)){
                    setTips("地址范围：0-199");
                }else {
                    if (Integer.parseInt(addrstr) > 199 || Integer.parseInt(addrstr) < 0) {
                        setTips("地址超出可用范围：0-199");
                        jes.setExceed(true);
                    } else if(Integer.parseInt(addrstr)==199){
                        jes.setLast(true);
                        setTips("地址范围：0-199");
                    }else if(Integer.parseInt(addrstr)==0){
                        jes.setFirst(true);
                        setTips("地址范围：0-199");
                    }else {
                        setTips("地址范围：0-199");
                    }
                }
            }
        }else if ("DLIP".equals(protocol)) {
            if(TextUtil.isEmpty(addrstr)){
                setTips("地址范围：1-239");
            }else {
                if (Integer.parseInt(addrstr) > 239 || Integer.parseInt(addrstr) < 1) {
                    setTips("地址超出可用范围：1-239");
                    jes.setExceed(true);
                } else if(Integer.parseInt(addrstr)==1){
                    jes.setFirst(true);
                    setTips("地址范围：1-239");
                }else if(Integer.parseInt(addrstr)==239){
                    jes.setLast(true);
                    setTips("地址范围：1-239");
                }else {
                    setTips("地址范围：1-239");
                }
            }
        }else if ("FLASHSCAN".equals(protocol)) {
            if (tvDevicetype.getText().toString().equals("探头")) {
                if(TextUtil.isEmpty(addrstr)){
                    setTips("地址范围：0000-0159");
                }else{
                    if (Integer.parseInt(addrstr) > 159 || Integer.parseInt(addrstr) < 0) {
                        setTips("地址超出可用范围：0000-0159");
                        jes.setExceed(true);
                    } else if(Integer.parseInt(addrstr)==0){
                        jes.setFirst(true);
                        setTips("地址范围：0000-0159");
                    }else if(Integer.parseInt(addrstr)==159){
                        jes.setLast(true);
                        setTips("地址范围：0000-0159");
                    }else{
                        setTips("地址范围：0000-0159");
                    }
                }
            } else if (tvDevicetype.getText().toString().equals("模块")) {
                if(TextUtil.isEmpty(addrstr)){
                    setTips("地址范围：1000-1159");
                }else {
                    if (Integer.parseInt(addrstr) > 1159) {
                        setTips("地址超出可用范围：1000-1159");
                        jes.setExceed(true);
                    } else if (Integer.parseInt(addrstr) < 1000) {
                        setTips("地址低于可用范围：1000-1159");
                        jes.setExceed(true);
                    } else if(Integer.parseInt(addrstr)==1000){
                        jes.setFirst(true);
                        setTips("地址范围：1000-1159");
                    }else if(Integer.parseInt(addrstr)==1159){
                        jes.setLast(true);
                        setTips("地址范围：1000-1159");
                    }else {
                        setTips("地址范围：1000-1159");
                    }
                }
            }
        }
        return jes;
    }

    /**
     * 开始编址
     */
    private void WritingLoop(String protocolType,int type) {
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String addrstr = ledviewClockTime.getText().toString();
        if(TextUtil.isEmpty(addrstr)){
            return;
        }
        String command;
        if ("CLIP".equals(protocolType)) {
//            if(JudgmentAddrExceed().isExceed()){
//              return;
//            }
            if(type==1){
                ADDRESSTYPE = 501;
            }else if(type==2) {
                //验证写入是否成功
                ADDRESSTYPE = 502;
            }else{
                //503 的具体规则见连续编址流程的“循环每隔2S读取读址”这块
                ADDRESSTYPE = 503;
            }
            command = DataHandler.CLIP_WRITE(addrstr, type, false);
            commonBleUtil.writeDevice(bleDevice,command);
        } else if ("DLIP".equals(protocolType)) {
//            if(JudgmentAddrExceed().isExceed()){
//                return;
//            }
            if(type==1){
                ADDRESSTYPE = 601;
            }else if(type==2) {
                //验证写入是否成功
                ADDRESSTYPE = 602;
            }else{
                ADDRESSTYPE = 603;
            }
            command = DataHandler.DLIP_WRITE("00", addrstr, type);
            //DLIP,第index条命令
            if (type == 1) {
                DataHandler.DLIP_ADDR_NEW = DataHandler.Alone2Hex(Integer.toHexString(parseInt(addrstr)));
                DLIPtag=2;
                DLIPcommand=command;
                groupQuery();
            }else{
                commonBleUtil.writeDevice(bleDevice,command);
            }
        }
        else if ("FLASHSCAN".equals(protocolType)) {
            //FlashScan,第index条命令
        }
    }
    /**
     * Reading的类型（1为写，2为验证读,3为纯读）
     * */
    private void ReadingLoop(String protocolType) {
        WritingLoop(protocolType,3);
    }
    /**
     * 开始连续编址
     * */
    private void beginConsequent() {
        if(TextUtil.isEmpty(ledviewClockTime.getText().toString())) {
            setTips("请输入起始地址");
            speak("请输入起始地址",true);
            return;
        }
        consequent=1;
        speak("开始连续编址",true);
        setTips("连续编址中...");
        startConsequent(tvProtocoltype.getText().toString(),"WRITE");
    }
    private long StartingConsequentTimeWrite20min;
    private long StartingConsequentTimeRead20min;
    private long  StartingConsequentTimeWrite1min;
    private long  StartingConsequentTimeRead1min;
    private Runnable ConsequentRunableRead;
    private Runnable ConsequentRunableWrite;
    private String currentState="";
    /**
     * 如果发生中途更换协议，需要置空runable，不然参数不改变
     * */
    private void startConsequent(final String protocol, final String writeOrread){
        currentState=writeOrread;
        //runable 循环的过程中StartingConsequentTimeWrite值不会被更新，
        // 只在startConsequent方法调用时才更新为最新值。而startConsequent方法只有编址成功
        // 或者点击继续的时候才会执行，意味着是新开始的动作
        StartingConsequentTimeWrite20min=System.currentTimeMillis();
        StartingConsequentTimeRead20min=System.currentTimeMillis();
        StartingConsequentTimeWrite1min=System.currentTimeMillis();
        StartingConsequentTimeRead1min=System.currentTimeMillis();
        if("WRITE".equals(writeOrread)){
            if(ConsequentRunableWrite!=null){
                handler.post(ConsequentRunableWrite);
                return;}
            ConsequentRunableWrite = new Runnable() {
                @Override
                public void run() {
                    if(System.currentTimeMillis()-StartingConsequentTimeWrite1min>=60*1000){
                        //若未更换，每隔一分钟提示一次，直到20分钟暂停
                        setTips("请更换设备");
                        speak("请更换设备",true);
                        //更新时间戳
                        StartingConsequentTimeWrite1min=System.currentTimeMillis();
                    }
                    if(System.currentTimeMillis()-StartingConsequentTimeWrite20min>=20*60*1000){
                        pauseConsequent(true);
                        return;
                    }
                    WritingLoop(protocol,1);
                    LogUtil.e("连续写中...");
//                    handler.postDelayed(this, 180*1000);
                    handler.postDelayed(this, Double.valueOf(SharePreferenceUtil.getStringSP("degree","2")).intValue()*1000);
                }
            };
            handler.post(ConsequentRunableWrite);
        }else{
            if(ConsequentRunableRead!=null){
                handler.post(ConsequentRunableRead);
                return;}
            ConsequentRunableRead = new Runnable() {
                @Override
                public void run() {
                    if(System.currentTimeMillis()-StartingConsequentTimeRead1min>=60*1000){
                        //若未更换，每隔一分钟提示一次，直到20分钟暂停
                        setTips("请更换设备");
                        speak("请更换设备",true);
                        //更新时间戳
                        StartingConsequentTimeRead1min=System.currentTimeMillis();
                    }
                    if(System.currentTimeMillis()-StartingConsequentTimeRead20min>=20*60*1000){
                        pauseConsequent(true);
                        return;}
                    ReadingLoop(protocol);
                    LogUtil.e("连续读中...");
                    handler.postDelayed(this, Double.valueOf(SharePreferenceUtil.getStringSP("degree","2")).intValue()*1000);
                }
            };
            handler.post(ConsequentRunableRead);
        }

    }
    private void stopConsequentRunableWrite(){
        handler.removeCallbacks(ConsequentRunableWrite);
        ConsequentRunableWrite=null;
    }
    private void stopConsequentRunableRead(){
        handler.removeCallbacks(ConsequentRunableRead);
        ConsequentRunableRead=null;
    }
    /**
     * 暂停连续编址
     * @param isTimeOut  是否是因为超时，即长时间无人坚守引起的暂停
     * */
    private void pauseConsequent(boolean isTimeOut) {
        if(ConsequentRunableWrite==null && ConsequentRunableRead==null){
            //意味着连续编址已经停止
            return;
        }
        if(isTimeOut){
            setTips("长时间无操作，暂停连续编址");
            speak("长时间无操作，连续编址暂停",true);
        }else{
            setTips("暂停连续编址");
            speak("连续编址暂停",true);
        }
        consequent=2;
        handler.removeCallbacks(ConsequentRunableRead);
        handler.removeCallbacks(ConsequentRunableWrite);
        ConsequentRunableRead=null;
        ConsequentRunableWrite=null;
    }
    /**
     * 继续连续编址
     * */
    private void continueConsequent() {
        setTips("连续编址中...");
        speak("连续编址继续",true);
        consequent=3;
        startConsequent(tvProtocoltype.getText().toString(),currentState);
    }
    /**
     * 停止连续编址
     * */
    private void stopConsequent(String content,boolean isFlush) {
        setTips(content);
        speak(content,isFlush);
        consequent=0;
        buttonLimit();
//        hideTop3();
        handler.removeCallbacks(ConsequentRunableRead);
        handler.removeCallbacks(ConsequentRunableWrite);
        ConsequentRunableRead=null;
        ConsequentRunableWrite=null;
    }

    private void writtingSuccess() {
        setTips("编写地址"+ledviewClockTime.getText().toString()+"成功");
    }

    private void readingSuccess() {
        setTips("读地址"+ledviewClockTime.getText().toString()+"成功");
        String show = parseInt(DataHandler.DLIP_ADDR_NEW, 16) + "";
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
                        setTips("读取地址超时");
                        String detail=tvProtocoltype.getText().toString()
                                +"读取地址超时"+"；\n";
                        sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                        saveLog("read","",tvProtocoltype.getText().toString(),"超时",detail);
                        speak("读取地址"+ledviewClockTime.getText().toString()+",超时",true);
                        DataHandler.singleReading=false;
                    } else {
                        setTips("编写地址"+ledviewClockTime.getText().toString()+"超时");
                        String detail=tvProtocoltype.getText().toString()
                                +"编写地址"+ledviewClockTime.getText().toString()+"超时"+"；\n";
                        sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                        tvSingleaddrLog.setText(sbuilder.toString());
                        scrollToBottom(scrollSingleaddrLog,tvSingleaddrLog);
                        saveLog("write",ledviewClockTime.getText().toString(),tvProtocoltype.getText().toString(),"超时",detail);
                        speak("编写地址"+ledviewClockTime.getText().toString()+",超时",true);
                    }
                    handler.removeCallbacks(this);
                    ADDRESSTYPE=0;
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
        ivTop1Arrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_up_black_50));
        ivTop2Arrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_up_black_50));
        ivTop3Arrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_up_black_50));
        ivTop4Arrow.setImageDrawable(ResourceUtil.getDrawable(R.drawable.arrow_up_black_50));
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
                lp.width = PhoneUtil.getScreenWidth(SingleFreeAddressActivity.this);
                lp.topMargin = linearrow_location_Y + lineSingleaddressArrow.getHeight();
                rootDialog.setLayoutParams(lp);
                rootDialog.setVisibility(View.VISIBLE);
            }
        });
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    //百度语音打印日志
                    String message = (String) msg.obj;
                    LogUtil.e(message);
                    break;
                case NOTIFYDECODE_CLIP1:
                    Writing("CLIP",102,2);
                    break;
                case NOTIFYDECODE_CLIP2:
                    ADDRESSTYPE = 0;
                    boolean result = msg.getData().getBoolean("result");
                    //如果是单独读，则失败循环，成功返回
                    if (DataHandler.singleReading) {
                        DataHandler.singleReading=false;
                        if (!result) {
                            setTips("读地址失败");
                            String detail="CLIP读取地址失败"+"；\n";
                            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                            saveLog("read",ledviewClockTime.getText().toString(),"CLIP","失败",detail);
                            speak("读取地址失败",true);
                        } else {
                            readingSuccess();
                            String detail="CLIP读取地址"+ledviewClockTime.getText().toString()+"成功"+"；\n";
                            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                            saveLog("read",ledviewClockTime.getText().toString(),"CLIP","成功",detail);
                            speak("读取地址"+ledviewClockTime.getText().toString()+",成功",true);
                        }
                    } else {
                        if (!result) {
                            setTips("编写地址"+ledviewClockTime.getText().toString()+"失败");
                            String detail="CLIP编写地址"+ledviewClockTime.getText().toString()+"失败"+"；\n";
                            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                            saveLog("write",ledviewClockTime.getText().toString(),"CLIP","失败",detail);
                            speak("编写地址"+ledviewClockTime.getText().toString()+",失败",true);
                        } else {
                            writtingSuccess();
                            String detail="CLIP编写地址"+ledviewClockTime.getText().toString()+"成功"+"；\n";
                            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                            saveLog("write",ledviewClockTime.getText().toString(),"CLIP","成功",detail);
                            speak("编写地址"+ledviewClockTime.getText().toString()+",成功",true);
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
                        DataHandler.singleReading=false;
                        if (!result2) {
                            setTips("读地址失败");
                            String detail="DLIP读取地址失败"+"；\n";
                            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                            saveLog("read",ledviewClockTime.getText().toString(),"DLIP","失败",detail);
                            speak("读取地址失败",true);
                        } else {
                            readingSuccess();
                            String detail="DLIP读取地址"+ledviewClockTime.getText().toString()+"成功"+"；\n";
                            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                            saveLog("read",ledviewClockTime.getText().toString(),"DLIP","成功",detail);
                            speak("读取地址"+ledviewClockTime.getText().toString()+",成功",true);
                            //DLIP读完地址后，再紧接着下发一条读取类型命令
//                            Reading("DLIPTYPE");
                        }
                    } else {
                        if (!result2) {
                            setTips("编写地址"+ledviewClockTime.getText().toString()+"失败");
                            String detail="DLIP编写地址"+ledviewClockTime.getText().toString()+"失败"+"；\n";
                            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                            saveLog("write",ledviewClockTime.getText().toString(),"DLIP","失败",detail);
                            speak("编写地址"+ledviewClockTime.getText().toString()+",失败",true);
                        } else {
                            writtingSuccess();
                            String detail="DLIP编写地址"+ledviewClockTime.getText().toString()+"成功"+"；\n";
                            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                            saveLog("write",ledviewClockTime.getText().toString(),"DLIP","成功",detail);
                            speak("编写地址"+ledviewClockTime.getText().toString()+",成功",true);
                        }
                    }
                    break;
//                case NOTIFYDECODE_DLIP3:
//                    ADDRESSTYPE = 0;
//                    String results = msg.getData().getString("result");
//                    if (DataHandler.singleReading) {
//                        DataHandler.singleReading=false;
//                    }
//                    if ("T".equals(results)) {
//                        tvDevicetype.setText("探头");
//                        String detail="DLIP读取类型成功，类型为探头"+"；\n";
//                        sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
//                        saveLog("read",ledviewClockTime.getText().toString(),"DLIP","探头",detail);
//                    }else  if ("M".equals(results)) {
//                        tvDevicetype.setText("模块");
//                        String detail="DLIP读取类型成功，类型为模块"+"；\n";
//                        sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
//                        saveLog("read",ledviewClockTime.getText().toString(),"DLIP","模块",detail);
//                    }else  if ("N".equals(results)) {
//                        //未定义
//                        String detail="DLIP读取类型失败，类型未定义"+"；\n";
//                        sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
//                        saveLog("read",ledviewClockTime.getText().toString(),"DLIP","未定义",detail);
//                    }else{
//                        String detail="DLIP读取类型失败"+"；\n";
//                        sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
//                        saveLog("read",ledviewClockTime.getText().toString(),"DLIP","失败",detail);
//                    }
//                    break;
                case NOTIFYDECODE_FLASHSCAN:
                    DataHandler.singleReading = false;
                    ToastUtil.showToastShort("独立编址所有协议流程执行完");
                    break;
                case LOOPCARDBATTERY:
                    Writing("LOOPCARD", 401, 0);
                    break;
                case CONSEQUENCE_CLIP1:
                    WritingLoop("CLIP",2);
                    break;
                case CONSEQUENCE_CLIP2:
                    ADDRESSTYPE = 0;
                    boolean resultclip2 = msg.getData().getBoolean("result");
                    if(resultclip2){
                        if(!checkAddislast()){
                            setTips("编写地址"+ledviewClockTime.getText().toString()+"成功，请更换设备");
                            String detail="CLIP编写地址"+ledviewClockTime.getText().toString()+"成功"+"；\n";
                            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                            saveLog("write",ledviewClockTime.getText().toString(),"CLIP","成功",detail);
                            speak("编写地址"+ledviewClockTime.getText().toString()+",成功，请更换设备",true);
                            //先停止连续写地址的runable，下面开启的是连续读
                            stopConsequentRunableWrite();
                            startConsequent("CLIP","READ");
                        }else{
                            setTips("编写地址"+ledviewClockTime.getText().toString()+"成功");
                            String detail="CLIP编写地址"+ledviewClockTime.getText().toString()+"成功"+"；\n";
                            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                            saveLog("write",ledviewClockTime.getText().toString(),"CLIP","成功",detail);
                            speak("编写地址"+ledviewClockTime.getText().toString()+",成功",true);
                            stopConsequent("连续编址完成",false);
                            //停止连续编址
                            right.setText("结束");
                            rightIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_jieshu_40));
                            //停止连续编址
                            rightIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_jieshu_40));
                            btnSingleaddrRead.setVisibility(View.INVISIBLE);
                            openTranslationWrite();
                        }
                    }else{
                        //写地址失败，继续2s循环写（不用管，runable只要不手动停止，就一直在执行）
                    }
                    break;
                case CONSEQUENCE_CLIP3:
                    ADDRESSTYPE = 0;
                    boolean resultclip3 = msg.getData().getBoolean("result");
                    if(resultclip3){
                        //设备已经更换，自动累加
                        setTips("设备移除成功，请更换新设备");
                        speak("设备移除成功，请更换新设备",true);
                        stopConsequentRunableRead();
                        //地址累加
                        autoAddAddr();
                        startConsequent("CLIP","WRITE");
                    }else{
                        //设备还未更换，继续2s循环读（不用管，runable只要不手动停止，就一直在执行）
                    }
                    break;
                case CONSEQUENCE_DLIP1:
                    WritingLoop("DLIP",2);
                    break;
                case CONSEQUENCE_DLIP2:
                    ADDRESSTYPE = 0;
                    boolean resultdlip2 = msg.getData().getBoolean("result");
                    if(resultdlip2){
                        if(!checkAddislast()){
                            setTips("编写地址"+ledviewClockTime.getText().toString()+"成功，请更换设备");
                            String detail="DLIP编写地址"+ledviewClockTime.getText().toString()+"成功"+"；\n";
                            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                            saveLog("write",ledviewClockTime.getText().toString(),"DLIP","成功",detail);
                            speak("编写地址"+ledviewClockTime.getText().toString()+",成功，请更换设备",true);
                            //先停止连续写地址的runable，下面开启的是连续读
                            stopConsequentRunableWrite();
                            startConsequent("DLIP","READ");
                        }else{
                            setTips("编写地址"+ledviewClockTime.getText().toString()+"成功");
                            String detail="DLIP编写地址"+ledviewClockTime.getText().toString()+"成功"+"；\n";
                            sbuilder.append(simpleDateFormat.format(new Date())+"  " +detail);
                            saveLog("write",ledviewClockTime.getText().toString(),"DLIP","成功",detail);
                            speak("编写地址"+ledviewClockTime.getText().toString()+",成功",true);
                            stopConsequent("连续编址完成",false);
                            //停止连续编址
                            right.setText("结束");
                            rightIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_jieshu_40));
                            //停止连续编址
                            rightIcon.setImageDrawable(ResourceUtil.getDrawable(R.drawable.single_jieshu_40));
                            btnSingleaddrRead.setVisibility(View.INVISIBLE);
                            openTranslationWrite();
                        }
                    } else{
                        //写地址失败，继续2s循环写（不用管，runable只要不手动停止，就一直在执行）
                    }
                    break;
                case CONSEQUENCE_DLIP3:
                    ADDRESSTYPE = 0;
                    boolean resultdlip3 = msg.getData().getBoolean("result");
                    if(resultdlip3){
                        //设备已经更换，自动累加
                        setTips("设备移除成功，请更换新设备");
                        speak("设备移除成功，请更换新设备",true);
                        stopConsequentRunableRead();
                        //地址累加
                        autoAddAddr();
                        startConsequent("DLIP","WRITE");
                    }else{
                        //设备还未更换，继续2s循环读（不用管，runable只要不手动停止，就一直在执行）
                    }
                    break;
                default:
            }
            tvSingleaddrLog.setText(sbuilder.toString());
            scrollToBottom(scrollSingleaddrLog,tvSingleaddrLog);
        }
    };

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

    /**
     * 自动地址累加
     * */
    private void autoAddAddr() {
        int old = Integer.parseInt(ledviewClockTime.getText().toString());
        int now = old + Integer.parseInt(tvAddressaddnum.getText().toString());
        ledviewClockTime.setText(now+"");
    }

    /**
     * 检查是不是在设置地址累加值的情况下协议可以编的最后一位地址
     * */
    private boolean checkAddislast() {
        int old = Integer.parseInt(ledviewClockTime.getText().toString());
        int now = old + Integer.parseInt(tvAddressaddnum.getText().toString());
        if ("CLIP".equals(tvProtocoltype.getText().toString())) {
            if (tvDevicetype.getText().toString().equals("探头")) {
                if (now < 0 || now > 99) {
                    //如果下一次的编址范围超出0-99，则这个地址为最后一个地址
                    return true;
                }
            } else if (tvDevicetype.getText().toString().equals("模块")) {
                if (now < 100 || now > 199) {
                    return true;
                }
            } else if (tvDevicetype.getText().toString().equals("混编")) {
                if (now < 0 || now > 199) {
                    return true;
                }
            }
        }else if ("DLIP".equals(tvProtocoltype.getText().toString())) {
            if(now<1 || now >239){
                return true;
            }
            //DLIP 从1开始
        } else if ("FLASHSCAN".equals(tvProtocoltype.getText().toString())) {
            if (tvDevicetype.getText().toString().equals("探头")) {
                if(now<0 || now >159){
                    return true;
                }
            } else if (tvDevicetype.getText().toString().equals("模块")) {
                if(now<1000 || now >1159){
                    return true;
                }
            }
        }
        return false;
    }

    private void saveLog(String Operatetype,String Address,String Protocoltype,String result,String detail){
        LogBean bean=new LogBean();
        bean.setUser(SharePreferenceUtil.getStringSP("currentusername",""));
        bean.setData(simpleDateFormat.format(new Date()));
        bean.setOperatetype(Operatetype);
        bean.setAddress(Address);
        bean.setProtocoltype(Protocoltype);
        bean.setResult(result);
        bean.setDetail(detail);
        bean.save();
    }
    private void setTips(String content){
        tvRepeatTips.setVisibility(View.VISIBLE);
        tvRepeatTips.setText(content);
    }
    /**
     * 连续编址过程中禁止使能按钮
     * */
    private void buttonLimit(){
        if(consequent!=0){
            //开始和继续
            tvProtocoltype.setTextColor(ResourceUtil.getColor(R.color.gray_shadow));
            tvDevicetype.setTextColor(ResourceUtil.getColor(R.color.gray_shadow));
            tvAddresstype.setTextColor(ResourceUtil.getColor(R.color.gray_shadow));
            tvAddressaddnum.setTextColor(ResourceUtil.getColor(R.color.gray_shadow));
            top1.setClickable(false);
            top2.setClickable(false);
            top3.setClickable(false);
            top4.setClickable(false);
            ivLess.setClickable(false);
            ivLess.setEnabled(false);
            ivAdd.setClickable(false);
            ivAdd.setEnabled(false);
            ledviewClockTime.setEnabled(false);
            PhoneUtil.hideInputWindow(SingleFreeAddressActivity.this,ledviewClockTime);
        }else{
            tvProtocoltype.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
            tvDevicetype.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
            tvAddresstype.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
            tvAddressaddnum.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
            top1.setClickable(true);
            top2.setClickable(true);
            top3.setClickable(true);
            top4.setClickable(true);
            ivLess.setClickable(true);
            ivAdd.setClickable(true);
            ivLess.setEnabled(true);
            ivAdd.setEnabled(true);
            ledviewClockTime.setEnabled(true);
        }

    }
    /**
     *  电池电量最大9V，显示的时候可以分成4个档位；
     *  0.00=<电压<7.20v，提醒没电，4格全空；
     *  7.20=<电压<7.65, 1格电；
     *  7.65=<电压<8.10, 2格电；
     *  8.10=<电压<8.55, 3格电；
     *  8.55=<电压       4格电；
     @param batteryValue  为0.00是代表获取失败
      * */
    private void setBatteryIcon(final double batteryValue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(batteryValue<7.2){
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_null));
                }else if(7.2<=batteryValue && batteryValue<7.65){
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_1));
                }else if(7.65<=batteryValue && batteryValue<8.10){
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_2));
                }else if(8.10<=batteryValue && batteryValue<8.55){
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_3));
                }else if(8.55<=batteryValue && batteryValue<=12.00){
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_4));
                }
                if( batteryValue<7.8){
                    speak("电池电量不足",false);
                }
                if( batteryValue<7.3){
                    speak("请更换电池",false);
                }
                ADDRESSTYPE=0;
            }
        });
    }

    /**
     * 获取电量线程  5min一次
     * */
    private boolean getLoopCarded=false;
    private void getLoopCardBatteryThread(){
        LoopCardBatteryThread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isAppExit){
                    //ADDRESSTYPE ==0 空闲状态，没有写或者读命令
                    try {
                        //因为下位机蓝牙连接后需要1-2S的延迟，所以第一次电池命令需要延迟2S左右
                        Thread.sleep(2*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(ADDRESSTYPE == 0){
                        if(!getLoopCarded){
                            handler.sendEmptyMessage(LOOPCARDBATTERY);
                            getLoopCarded = true;
                            try {
                                //休眠5min，然后继续下一次读电池
                                Thread.sleep(5*60*1000);
                                getLoopCarded=false;
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


    public  void initialTts() {
        LoggerProxy.printable(true); // 日志打印在logcat中
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
    private void closeTts(){
        if(synthesizer!=null){
            synthesizer.release();
            synthesizer=null;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 使用百度的SDK播放语音
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     * @param isFlush  是否需要清空队列，直播放最新加入的声音
     */
    private void speak(String content,boolean isFlush) {
        if(SharePreferenceUtil.getBooleanSP("audio")){
            if(synthesizer!=null)
            { if(isFlush){
                synthesizer.stop();
            }
                synthesizer.speak(content);
            }
        }
    }


    @Override
    protected void onDestroy() {
        ADDRESSTYPE=0;
        if(synthesizer!=null){
            synthesizer.release();
        }
        handler.removeCallbacksAndMessages(null);
        isAppExit=true;
        LoopCardBatteryThread=null;
        if(progressdialog!=null){
            progressdialog.dismiss();
            progressdialog=null;
        }
        System.gc();
        super.onDestroy();
    }



    /**
     * 定义一个针对加减号的数据结构
     * 如果是第一个地址，不能点击减号
     * 如果是最后一个地址，不能点击加号
     * */
    class JudgementExceedStruts{
        boolean Exceed;
        boolean isFirst;
        boolean isLast;
        public boolean isLast() {
            return isLast;
        }

        public void setLast(boolean last) {
            isLast = last;
        }

        public boolean isFirst() {
            return isFirst;
        }

        public void setFirst(boolean first) {
            isFirst = first;
        }

        public boolean isExceed() {
            return Exceed;
        }

        public void setExceed(boolean exceed) {
            Exceed = exceed;
        }
    }
}
