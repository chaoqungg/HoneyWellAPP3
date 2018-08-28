package com.honeywell.honeywellproject.BleTaskModule.AddressDownLoad.widge;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.honeywell.honeywellproject.BleTaskModule.AddressDownLoad.RightTop.AddressDownLoadComparator;
import com.honeywell.honeywellproject.BleTaskModule.AddressDownLoad.RightTop.RightTopAdapter;
import com.honeywell.honeywellproject.BleTaskModule.AddressDownLoad.RightTop.RightTopBean;
import com.honeywell.honeywellproject.BleTaskModule.AddressDownLoad.adapter.AddressDownlaodAdapterNewUI;
import com.honeywell.honeywellproject.BleTaskModule.ControllerTask.data.ControllerBean;
import com.honeywell.honeywellproject.BleTaskModule.FatherTask.data.FatherTaskBean;
import com.honeywell.honeywellproject.BleTaskModule.ProjectTask.data.ProjectBean;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;
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
import com.honeywell.honeywellproject.Util.LogUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.RegexUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;
import com.honeywell.honeywellproject.Util.ZipUtil;
import com.honeywell.honeywellproject.Util.mail.SendMailUtil;
import com.honeywell.honeywellproject.Util.me.zhouzhuo.zzexcelcreator.ZzExcelCreator;
import com.honeywell.honeywellproject.Util.me.zhouzhuo.zzexcelcreator.ZzFormatCreator;
import com.honeywell.honeywellproject.WidgeView.SwitchButton;
import com.honeywell.honeywellproject.WidgeView.indicatordialog.IndicatorBuilder;
import com.honeywell.honeywellproject.WidgeView.indicatordialog.IndicatorDialog;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;

public class AddressDownLoadActivityNewUI extends ToolBarActivity {

    @BindView(R.id.rv_addressdownload)
    RecyclerView   rvAddressdownload;
    @BindView(R.id.iv_singleaddress_blestate2)
    ImageView      ivSingleaddressBlestate2;
    @BindView(R.id.iv_singleaddress_blebattery2)
    ImageView      ivSingleaddressBlebattery2;
    @BindView(R.id.iv_singleaddress_blerssi2)
    ImageView      ivSingleaddressBlerssi2;
    @BindView(R.id.tv_progress1)
    TextView       tvProgress1;
    @BindView(R.id.tv_progress2)
    TextView       tvProgress2;
    @BindView(R.id.progress)
    ProgressBar    progress;
    @BindView(R.id.rl_progress)
    RelativeLayout rlProgress;
    @BindView(R.id.ll_singleaddress_top)
    LinearLayout   llSingleAddressTop;
    @BindView(R.id.bottom_select)
    LinearLayout   bottomSelect;
    @BindView(R.id.bottom_mail1)
    TextView       bottomMail1;
    @BindView(R.id.bottom_mail2)
    EditText       bottomMail2;
    @BindView(R.id.bottom_mail)
    LinearLayout   bottomMail;
    @BindView(R.id.ll_gray)
    View   ll_gray;
    @BindView(R.id.ll_output)
    LinearLayout   llOutput;
    /**
     * 编址类型，CLIP 10X(101,102)\DLIP 20X(201,202)\FlashScan 30X
     * 且当ADDRESSTYPE==0 是代表没有任何读写操作，空闲状态
     */
    private static       int ADDRESSTYPE     = 0;
    /**
     * 写电池命令
     */
    private static final int LOOPCARDBATTERY = 401;
    /**
     * 写序列号命令
     */
    private static final int SERIES          = 501;
    /**
     * 输出文件完成
     */
    private static final int EXECLFINISH     = 601;

    private List<Integer>     listfail = new ArrayList<Integer>(); //记录失败任务
    private List<SonTaskBean> list     = new ArrayList<SonTaskBean>();
    private ProgressDialog progressdialog;
    private String         fathernumber = "-/-", logininfo_id, project_id, controller_id, fathertask_id;
    private AddressDownlaodAdapterNewUI addressAdapter;
    private BleDevice                   bleDevice;
    private              String             rssis          = "0";
    private final static int                messageOK      = 1;
    private final static int                messageError   = 2;
    private final static int                loopPowerOK    = 3;
    private final static int                loopPowerError = 4;
    private              List<RightTopBean> rightTopList   = new ArrayList<>();
    private IndicatorDialog dialog;
    private Thread          LoopCardBatteryThread;
    private boolean         isAppExit;
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode      = TtsMode.MIX;
    protected String  offlineVoice = OfflineResource.VOICE_MALE;
    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================
    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;
    private   CommonBleUtil commonBleUtil;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initlist();
        initCommonBleUtil();
        initSort();
        try {
            initialTts();
        } catch (Exception e) {
        }
        initBle();
    }


    @Override
    public int getContentViewId() {
        return R.layout.activity_address_download;
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fathernumber = bundle.getString("fathernumber");
            logininfo_id = bundle.getString("logininfo_id");
            project_id = bundle.getString("project_id");
            controller_id = bundle.getString("controller_id");
            fathertask_id = bundle.getString("fathertask_id");
        }
        getToolbarTitle().setText("回路编址进度");
        rlProgress.setVisibility(View.GONE);
        progressdialog = new ProgressDialog(this, R.style.progressDialog);
        rvAddressdownload.setLayoutManager(new LinearLayoutManager(this));
        addressAdapter = new AddressDownlaodAdapterNewUI(list);
        rvAddressdownload.setAdapter(addressAdapter);
    }

    private void initlist() {
        list.clear();
        List<SonTaskBean> list2 = DataSupport.where("logininfo_id= ? and project_id = ? and controller_id= ? and fathertask_id= ?", logininfo_id, project_id, controller_id, fathertask_id).find(SonTaskBean.class);
        list.addAll(list2);
        list.get(0).setIsexpand(true);
        for (int i = 1; i < list.size(); i++) {
            list.get(i).setIsexpand(false);
        }
        addressAdapter.notifyDataSetChanged();
    }

    private void initSort() {
        if (SharePreferenceUtil.getIntSP("sort") <= 0) {
            //默认按编号排序
            AddressDownLoadComparator.SortAsNumber(list);
        } else if (SharePreferenceUtil.getIntSP("sort") == 1) {
            //按地址排序
            AddressDownLoadComparator.SortAsAddres(list);
        } else if (SharePreferenceUtil.getIntSP("sort") == 2) {
            //按状态排序
            AddressDownLoadComparator.SortAsCompleState(list);
        }
        list.get(0).setIsexpand(true);
        for (int i = 1; i < list.size(); i++) {
            list.get(i).setIsexpand(false);
        }
        addressAdapter.notifyDataSetChanged();
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
        if (SharePreferenceUtil.getIntSP("sort") <= 0) {
            //默认
            rightTopList.add(new RightTopBean("按编号排序", true));
            rightTopList.add(new RightTopBean("按地址排序", false));
            rightTopList.add(new RightTopBean("按状态排序", false));
        } else if (SharePreferenceUtil.getIntSP("sort") == 1) {
            rightTopList.add(new RightTopBean("按编号排序", false));
            rightTopList.add(new RightTopBean("按地址排序", true));
            rightTopList.add(new RightTopBean("按状态排序", false));
        } else if (SharePreferenceUtil.getIntSP("sort") == 2) {
            rightTopList.add(new RightTopBean("按编号排序", false));
            rightTopList.add(new RightTopBean("按地址排序", false));
            rightTopList.add(new RightTopBean("按状态排序", true));
        }
        if (rightTopAdapter == null) {
            rightTopAdapter = new RightTopAdapter(rightTopList, AddressDownLoadActivityNewUI.this);
        }
        if (dialog == null) {
            dialog = new IndicatorBuilder(AddressDownLoadActivityNewUI.this)
                    .width(PhoneUtil.getScreenWidth(AddressDownLoadActivityNewUI.this) * 3 / 5)
                    .height(PhoneUtil.getScreenHeight(AddressDownLoadActivityNewUI.this) / 3)
                    .ArrowDirection(IndicatorBuilder.TOP)
                    .bgColor(getResources().getColor(R.color.white))
                    .dimEnabled(true)
                    .gravity(IndicatorBuilder.GRAVITY_RIGHT)
                    .radius(10)
                    .ArrowRectage(0.9f)
                    .layoutManager(new LinearLayoutManager(AddressDownLoadActivityNewUI.this, LinearLayoutManager.VERTICAL, false))
                    .adapter(rightTopAdapter).create();
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.show(getMenuView());
        rightTopAdapter.setRightTopAudioOnCheckedChangeListener(new RightTopAdapter.RightTopAudioOnCheckedChangeListener() {
            @Override
            public void check(SwitchButton button, boolean isChecked, int position) {
                if (isChecked) {
                    for (int i = 0; i < rightTopList.size(); i++) {
                        rightTopList.get(i).setSelect(false);
                    }
                    rightTopList.get(position).setSelect(true);
                    rightTopAdapter.notifyDataSetChanged();
                    SharePreferenceUtil.setIntSP("sort", position);
                    initSort();
                } else {
                    //只有一种情况下的关闭需要被考虑：（全部关闭）
                    int j = 0;
                    for (int i = 0; i < rightTopList.size(); i++) {
                        if (position != i) {
                            if (!rightTopList.get(i).select()) {
                                j++;
                            }
                        }
                    }
                    if (j == 2) {
                        //如果其他两个都是关闭状态，则置默认值
                        SharePreferenceUtil.setIntSP("sort", 0);
                        initSort();
                    }
                }
            }
        });
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
     * 使用handler对下载结果进行处理
     */

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case messageOK:
                    tvProgress1.setText(list.size() - listfail.size() + currentPosition + 1 + "");
                    progress.setProgress(list.size() - listfail.size() + currentPosition + 1);
                    ADDRESSTYPE = 0;
                    list.get(listfail.get(currentPosition)).setProcess(true);
                    break;
                case messageError:
                    tvProgress1.setText(list.size() - listfail.size() + currentPosition + 1 + "");
                    progress.setProgress(list.size() - listfail.size() + currentPosition + 1);
                    ADDRESSTYPE = 0;
                    list.get(listfail.get(currentPosition)).setProcess(false);
                    break;
                case LOOPCARDBATTERY:
                    ADDRESSTYPE = 401;
                    commonBleUtil.writeDevice(bleDevice, DataHandler.LOOPCARDBattery_WRITE());
                    return;
                case EXECLFINISH:
                    if(!isSendMail){
                        //不是发送邮件的压缩，而是单独的保存到手机
                        ToastUtil.showToastShort("文件输出完成");
                    }
                    if(zipListener!=null){
                        zipListener.finish(true);
                    }
                    return;
                default:
                    break;
            }
            addressAdapter.notifyItemChanged(listfail.get(currentPosition));
            rvAddressdownload.scrollToPosition(listfail.get(currentPosition));
            currentPosition++;
            if (currentPosition < listfail.size()) {
                ble_downaddress2(currentPosition);
            } else {
                checkFail(false);
                DataSupport.saveAll(list);
            }
        }
    };

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
            }
        });
    }

    /**
     * 检查上传的地址是否有未成功的，若有，则移动到这个位置
     */
    private boolean checkFail(boolean isInit) {
        //这两个变量的目的是：全成功或者全失败都不显示失败重传按钮，其余情况则显示
        boolean hassuccess = false, hasfail = false;
        //统计子任务地址下载失败的个数，计算百分比存入父任务表。
        int failcount = 0;
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isProcess()) {
                hasfail = true;
                failcount++;
                rvAddressdownload.scrollToPosition(i + 1);
            } else {
                hassuccess = true;
            }
        }
        //初始化去检查fail的时候不需要保存进度
        if (!isInit) {
            int size = list.size();
            if (size == failcount) {
                saveFatherProgressPercent("0.00%");
            } else {
                double failpencent = (size - failcount) * 100 / size;
                DecimalFormat df = new DecimalFormat("#.00");
                saveFatherProgressPercent(df.format(failpencent) + "%");
            }
        }
        return hassuccess & hasfail;
    }

    private void saveFatherProgressPercent(String failpencent) {
        FatherTaskBean fatherbean =DataSupport.find(FatherTaskBean.class,Integer.parseInt(fathertask_id));
        fatherbean.setProgressnpercent(failpencent);
        fatherbean.update(fatherbean.getId());
    }

    /**
     * 下载地址使用的方法
     */
    private void ble_downaddress2(final int i) {
        LogUtil.e("第"+i+"次");
        ConstantUtil.NOTIFYSUCCESS = false;
        int k = listfail.get(i);
        String data = DataHandler.DLIPSeries(list.get(k));
        if (TextUtil.isEmpty(data)) {
            LogUtil.e("序列号异常,无法写命令");
            return;
        }
        LogUtil.e("发送：----->"+data);
        currentPosition = i;
        ADDRESSTYPE = 501;
        commonBleUtil.writeDevice(bleDevice, data);
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

    boolean mailSelectFirst;
    boolean isSendMail;
      @OnClick({R.id.ll_output, R.id.ll_download, R.id.ll_singleaddress_top,
            R.id.bottom_select1, R.id.bottom_select2, R.id.bottom_select3,
              R.id.bottom_mail_select_sure, R.id.bottom_mail_select_cacel,R.id.ll_gray})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_download:
                listfail.clear();
                for (int i = 0; i < list.size(); i++) {
                    if (!list.get(i).isProcess()) {
                        //记录失败条目
                        listfail.add(i);
                    }
                }
                if (listfail.size() == 0) {
                    ToastUtil.showToastShort("序列号已全部下载完成");
                } else {
                    if (!BleManager.getInstance().isConnected(bleDevice)) {
                        ToastUtil.showToastShort("蓝牙未连接");
                        return;
                    }
                    ble_downaddress2(0);
                    rlProgress.setVisibility(View.VISIBLE);
                    progress.setMax(list.size());
                    progress.setProgress(list.size() - listfail.size());
                    tvProgress1.setText(list.size() - listfail.size() + "");
                    tvProgress2.setText("/" + list.size() + "");
                }
                break;
            case R.id.ll_singleaddress_top:
                //失败重连
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    Scan();
                }
                break;
            case R.id.ll_output:
                if(mailSelectFirst){
                    return;
                }
                mailSelectFirst=true;
                AnimatorSet animatorSet1 = new AnimatorSet();
                animatorSet1.play(trantranslationY_show(bottomSelect)).with(alphaShow(ll_gray));
                animatorSet1.start();
                ll_gray.setVisibility(View.VISIBLE);
                break;
            case R.id.bottom_select1:
                //保存至手机
                buildFlodThread();
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.play(trantranslationY_hide(bottomSelect)).with(alphaHide(ll_gray));
                animatorSet2.start();
                ll_gray.setVisibility(View.GONE);
                mailSelectFirst=false;
                break;
            case R.id.bottom_select2:
                //选择邮件发送
                AnimatorSet animatorSet3 = new AnimatorSet();
                animatorSet3.play(trantranslationY_show(bottomMail)).with(trantranslationY_hide(bottomSelect));
                animatorSet3.start();
                mailSelectFirst=false;
                break;
            case R.id.bottom_select3:
                //取消
                AnimatorSet animatorSet4 = new AnimatorSet();
                animatorSet4.play(trantranslationY_hide(bottomSelect)).with(alphaHide(ll_gray));
                animatorSet4.start();
                ll_gray.setVisibility(View.GONE);
                mailSelectFirst=false;
                PhoneUtil.hideInputWindow(AddressDownLoadActivityNewUI.this,bottomMail2);
                break;
            case R.id.bottom_mail_select_sure:
                if(RegexUtil.matchEmail(bottomMail2.getText().toString())){
                    AnimatorSet animatorSet5 = new AnimatorSet();
                    animatorSet5.play(trantranslationY_hide(bottomMail)).with(alphaHide(ll_gray));
                    animatorSet5.start();
                    ll_gray.setVisibility(View.GONE);
                    mailSelectFirst=false;
                    isSendMail=true;
                    PhoneUtil.hideInputWindow(AddressDownLoadActivityNewUI.this,bottomMail2);
                    sendMail(bottomMail2.getText().toString());
                }else{
                    ToastUtil.showToastShort("邮箱格式不正确");
                }
                break;
            case R.id.bottom_mail_select_cacel:
                AnimatorSet animatorSet6 = new AnimatorSet();
                animatorSet6.play(trantranslationY_hide(bottomMail)).with(alphaHide(ll_gray));
                animatorSet6.start();
                ll_gray.setVisibility(View.GONE);
                mailSelectFirst=false;
                break;
            case R.id.ll_gray:
                if(mailSelectFirst){
                    AnimatorSet animatorSet7 = new AnimatorSet();
                    animatorSet7.play(trantranslationY_hide(bottomSelect)).with(alphaHide(ll_gray));
                    animatorSet7.start();
                    ll_gray.setVisibility(View.GONE);
                }else{
                    AnimatorSet animatorSet7 = new AnimatorSet();
                    animatorSet7.play(trantranslationY_hide(bottomMail)).with(alphaHide(ll_gray));
                    animatorSet7.start();
                    ll_gray.setVisibility(View.GONE);
                }
                mailSelectFirst=false;
                break;
            default:
        }
    }


    private ObjectAnimator alphaShow(View view){
        //透明度起始为1，结束时为0
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        animator.setDuration(300);//时间1s
        return animator;
    }
    private ObjectAnimator alphaHide(final View view){
        //透明度起始为1，结束时为0
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        animator.setDuration(300);
        return animator;

    }
    private ObjectAnimator trantranslationY_show(final View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0,-view.getMeasuredHeight());
        animator.setDuration(300);
       return animator;
    }
    public ObjectAnimator trantranslationY_hide(final View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", -view.getMeasuredHeight(),0);
        animator.setDuration(300);
        return animator;
    }

    ZzExcelCreator creator;
    private void buildFlodThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    buildFlod();
                    handler.sendEmptyMessage(EXECLFINISH);
                } catch (IOException | WriteException | BiffException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    String ZipTime;
    private void buildFlod() throws IOException, WriteException, BiffException {
        SimpleDateFormat simple=new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        ZipTime=simple.format(new Date());
        creator = ZzExcelCreator.getInstance();
        List<ProjectBean> listProject = DataSupport.where("logininfo_id = ?", logininfo_id).find(ProjectBean.class);
        for (int i = 0; i < listProject.size(); i++) {
            String pathProject = Environment.getExternalStorageDirectory() + "/" + "蓝牙编址器APP批量编址记录_"+ ZipTime + "/" + listProject.get(i).getProjectname() + "_" + listProject.get(i).getProjectdata();
            List<ControllerBean> listController = listProject.get(i).getControllerlist();
            for (int j = 0; j < listController.size(); j++) {
                String filename = "控制器" + listController.get(j).getTasknumber() + "_" + listController.get(j).getControllerdata();
                List<FatherTaskBean> listFather = listController.get(j).getFathertasklist();
                for (int k = 0; k < listFather.size(); k++) {
                    List<SonTaskBean> listSon = listFather.get(k).getSontasklist();
                    creator.createExcel(pathProject, filename)
                            .createSheet(listFather.get(k).getTasknumber() + "_" + listFather.get(k).getTaskdate(), k);
                    WriteContent(k, listSon, pathProject, filename);
                }
                creator.close();
            }
        }
        if(isSendMail){
            //只有发送邮件才进行压缩
            try {
                ZipUtil.zipFolder(Environment.getExternalStorageDirectory() + "/" + "蓝牙编址器APP批量编址记录_"+ZipTime,
                        Environment.getExternalStorageDirectory() + "/" + "蓝牙编址器APP批量编址记录_" +ZipTime+ ".zip");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    OnZipListtener zipListener;
    public interface OnZipListtener{
        void finish(boolean result);
        void error(boolean result);
    }

    private void sendMail(final String toAddr) {
        //先压缩，再发送
        buildFlodThread();
        zipListener=new OnZipListtener() {
            @Override
            public void finish(boolean result) {
                //ZIP 压缩完成，回调到这儿，再进行发送邮件
                File file = new File(Environment.getExternalStorageDirectory()+File.separator+"蓝牙编址器APP批量编址记录_"+ ZipTime+".zip");
                SendMailUtil.send(file,toAddr);
                ToastUtil.showToastShort("邮件发送成功");
                isSendMail=false;
                zipListener=null;
                }
            @Override
            public void error(boolean result) {
                ToastUtil.showToastShort("ZIP压缩失败");
                zipListener=null;
                }
        };
    }
    private void WriteContent(int sheet, List<SonTaskBean> listSon, String pathProject, String filename) throws WriteException, IOException, BiffException {
        WritableCellFormat format = ZzFormatCreator
                .getInstance()
                .createCellFont(WritableFont.ARIAL)
                .setAlignment(Alignment.CENTRE, VerticalAlignment.CENTRE)
                .setFontSize(14)
                .setFontColor(Colour.DARK_GREEN)
                .getCellFormat();
        try {
            creator
                    .openExcel(new File(pathProject + "/" + filename + ".xls"))
                    .openSheet(sheet)
                    .setColumnWidth(0, 20)
                    .setRowHeight(0, 400)
                    .fillContent(0, 0, "编号", format)
                    .fillContent(1, 0, "序列号", format)
                    .fillContent(2, 0, "地址", format)
                    .fillContent(3, 0, "类型", format)
                    .fillContent(4, 0, "描述", format)
                    .fillContent(5, 0, "编址状态", format);
        } catch (IOException | BiffException e) {
            LogUtil.e(e.getMessage());
        }
        LogUtil.e("WriteContent");
        for (int i = 0; i < listSon.size(); i++) {
            creator
                    .openExcel(new File(pathProject + "/" + filename + ".xls"))
                    .openSheet(sheet)
                    .setColumnWidth(0, 25)
                    .setRowHeight(i + 1, 400)
                    .fillContent(0, i + 1, listSon.get(i).getTasknumber() + "", format);
            creator
                    .openExcel(new File(pathProject + "/" + filename + ".xls"))
                    .openSheet(sheet)
                    .setColumnWidth(1, 25)
                    .setRowHeight(i + 1, 400)
                    .fillContent(1, i + 1, listSon.get(i).getTaskserialnumber(), format);
            creator
                    .openExcel(new File(pathProject + "/" + filename + ".xls"))
                    .openSheet(sheet)
                    .setColumnWidth(2, 25)
                    .setRowHeight(i + 1, 400)
                    .fillContent(2, i + 1, listSon.get(i).getTaskdigitaladdress() + "", format);
            creator
                    .openExcel(new File(pathProject + "/" + filename + ".xls"))
                    .openSheet(sheet)
                    .setColumnWidth(3, 25)
                    .setRowHeight(i + 1, 400)
                    .fillContent(3, i + 1, listSon.get(i).getAddressingtype(), format);
            creator
                    .openExcel(new File(pathProject + "/" + filename + ".xls"))
                    .openSheet(sheet)
                    .setColumnWidth(4, 25)
                    .setRowHeight(i + 1, 400)
                    .fillContent(4, i + 1, listSon.get(i).getSondescription(), format);
            creator
                    .openExcel(new File(pathProject + "/" + filename + ".xls"))
                    .openSheet(sheet)
                    .setColumnWidth(5, 25)
                    .setRowHeight(i + 1, 400)
                    .fillContent(5, i + 1, listSon.get(i).isProcess() ? "完成" : "未完成", format);
        }
    }

    /**
     * 开始生成CSV文件
     */
//    private void OutPutExecl() {
//        final String url = OutPutExeclUtil.addressmodel2csv(list);
//        if (TextUtil.isEmpty(url)) {
//            return;
//        }
//        DialogUtil.showAlertDialog(AddressDownLoadActivityNewUI.this, "提示", "文件保存成功，位于:\n" + url + ",\n点击确定查看", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                File file = new File(url);
//                //获取父目录
////              File parentFlie = new File(file.getParent());
////              Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//这个是调用父file，帮助打开手机自带文件管理器，还需手动去寻找文件
////              intent.addCategory(Intent.CATEGORY_OPENABLE);
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);//这种是点击直接查看这个文档，小米、华为手机版本都没问题
////                csv对应的MIME类型:text/comma-separated-values
//                intent.setDataAndType(Uri.fromFile(file), "text/comma-separated-values");
//                startActivity(intent);
//            }
//        }, null);
//    }

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

    private void closeTts() {
        if (synthesizer != null) {
            synthesizer.release();
            synthesizer = null;
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

}
