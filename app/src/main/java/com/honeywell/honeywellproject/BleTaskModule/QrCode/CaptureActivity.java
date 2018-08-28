package com.honeywell.honeywellproject.BleTaskModule.QrCode;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;
import com.honeywell.honeywellproject.BuildConfig;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.LogUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.RegexUtil;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;
import com.honeywell.honeywellproject.Util.io.github.xudaojie.qrcodelib.common.ActionUtils;
import com.honeywell.honeywellproject.Util.io.github.xudaojie.qrcodelib.common.QrUtils;
import com.honeywell.honeywellproject.Util.io.github.xudaojie.qrcodelib.zxing.camera.CameraManager;
import com.honeywell.honeywellproject.Util.io.github.xudaojie.qrcodelib.zxing.decoding.CaptureActivityHandler;
import com.honeywell.honeywellproject.Util.io.github.xudaojie.qrcodelib.zxing.decoding.InactivityTimer;
import com.honeywell.honeywellproject.Util.io.github.xudaojie.qrcodelib.zxing.view.ViewfinderView;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CaptureActivity extends ToolBarActivity implements Callback,View.OnClickListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSION_CAMERA = 1000;
    private static final int REQUEST_PERMISSION_PHOTO = 1001;
    private CaptureActivity mActivity;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private boolean flashLightOpen = false;
    private ImageButton flashIbtn;
    private TextView galleryTv,series_input,series_add,qr_number2;
    private String position;
    private RelativeLayout rl_series;
    private String saomiaotype,logininfo_id, project_id, controller_id, fathertask_id;
    private SonTaskBean sb;
    private List<SonTaskBean> list = new ArrayList<SonTaskBean>();
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mActivity = this;
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        CameraManager.init(getApplication());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_CAMERA);
            }
        }

        initView();
    }
    @Override
    public int getContentViewId() {
        return R.layout.qr_camera;
    }
    protected void initView() {
        getToolbarTitle().setText("扫描输入序列号");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            saomiaotype = bundle.getString("saomiaotype");
            logininfo_id = bundle.getString("logininfo_id");
            project_id = bundle.getString("project_id");
            controller_id = bundle.getString("controller_id");
            fathertask_id = bundle.getString("fathertask_id");
            position=bundle.getString("position");
        }
        rl_series = (RelativeLayout) findViewById(R.id.rl_series);
        rl_series.postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams laParams=(RelativeLayout.LayoutParams )rl_series.getLayoutParams();
                laParams.topMargin=PhoneUtil.getScreenHeight(CaptureActivity.this)*3/4+20;
                rl_series.setLayoutParams(laParams);
            }
        },100);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        flashIbtn = (ImageButton) findViewById(R.id.flash_ibtn);
        qr_number2= (TextView) findViewById(R.id.qr_number2);
        galleryTv = (TextView) findViewById(R.id.gallery_tv);
        series_input= (TextView) findViewById(R.id.series_input);
        series_add= (TextView) findViewById(R.id.series_add);
        if("0".equals(saomiaotype)){
            //"0" 代表点击的是底部的扫描，显示“新增”
            series_add.setOnClickListener(this);
        }else{
            series_add.setVisibility(View.INVISIBLE);
        }
        series_input.setOnClickListener(this);
        flashIbtn.setOnClickListener(this);
        galleryTv.setOnClickListener(this);
    }

    private void initData() {
        list.clear();
        List<SonTaskBean> list2 = DataSupport.where("logininfo_id= ? and project_id = ? and controller_id= ? and fathertask_id= ?", logininfo_id, project_id, controller_id, fathertask_id).find(SonTaskBean.class);
        list.addAll(list2);
        sb = list2.get(Integer.parseInt(position));
        qr_number2.setText(String.valueOf(sb.getTasknumber()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && data != null
                && requestCode == ActionUtils.PHOTO_REQUEST_GALLERY) {
            Uri inputUri = data.getData();
            String path = null;

            if (URLUtil.isFileUrl(inputUri.toString())) {
                // 小米手机直接返回的文件路径
                path = inputUri.getPath();
            } else {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(inputUri, proj, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                }
            }
            if (!TextUtils.isEmpty(path)) {
                Result result = QrUtils.decodeImage(path);
                if (result != null) {
                    if (BuildConfig.DEBUG) Log.d(TAG, result.getText());
                    handleDecode(result, null);
                } else {
                    new AlertDialog.Builder(CaptureActivity.this)
                            .setTitle("提示")
                            .setMessage("此图片无法识别")
                            .setPositiveButton("确定", null)
                            .show();
                }
            } else {
                if (BuildConfig.DEBUG) Log.e(TAG, "image path not found");
                Toast.makeText(mActivity, "图片路径未找到", Toast.LENGTH_SHORT).show();
            }
        }else  if (requestCode == 8 && resultCode == RESULT_OK && data != null) {
            Bundle bundle=data.getExtras();
            String series = bundle.getString("series");
            String tasknumber = bundle.getString("tasknumber");
            String digitaladdr = bundle.getString("digitaladdr");
            String description = bundle.getString("description");
            String devicetype = bundle.getString("devicetype");
            int positions = Integer.parseInt(position);
            if (list != null && list.size() > 0) {
                list.get(positions).setTaskserialnumber(series);
                list.get(positions).setDevicetype(devicetype);
                list.get(positions).setTaskdigitaladdress(Integer.parseInt(digitaladdr));
                list.get(positions).setSondescription(description);
                DataSupport.saveAll(list);
            }
            AddSon();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 未获得Camera权限
                new AlertDialog.Builder(mActivity)
                        .setTitle("提示")
                        .setMessage("请在系统设置中为App开启摄像头权限后重试")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mActivity.finish();
                            }
                        })
                        .show();
            }
        } else if (grantResults.length > 0 && requestCode == REQUEST_PERMISSION_PHOTO) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("提示")
                        .setMessage("请在系统设置中为App中开启文件权限后重试")
                        .setPositiveButton("确定", null)
                        .show();
            } else {
                ActionUtils.startActivityForGallery(mActivity, ActionUtils.PHOTO_REQUEST_GALLERY);
            }
        }
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        handleResult(resultString);
    }

    protected void handleResult(String resultString) {
        if (resultString.equals("")) {
            Toast.makeText(CaptureActivity.this, R.string.scan_failed, Toast.LENGTH_SHORT).show();
            restartPreview();
        } else if(!RegexUtil.matchNum(resultString)){
            ToastUtil.showToastShort("序列号非法："+resultString);
            restartPreview();
        }  else if(resultString.length()!=17){
            ToastUtil.showToastShort("序列号格式非法");
            restartPreview();
        }else{
            //序列号不能重复
            boolean breaks = false;
//            int id=0;
            for (SonTaskBean bean : list) {
//                id++;
//                if(id-1==Integer.parseInt(position)){
//                    //跳过自身，如果再次扫描的结果等于自身本来的序列号，则不算作重复。
//                    continue;
//                }
                if (!TextUtil.isEmpty(bean.getTaskserialnumber())  && bean.getTaskserialnumber().equals(resultString.substring(4,14))) {
                    ToastUtil.showToastShort("序列号重复,请检查二维码");
                    breaks = true;
                    break;
                }
            }
            if (breaks) {
               handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       restartPreview();
                   }
               }, 200);
                return;
            }
            Intent resultIntent = new Intent(CaptureActivity.this,QrCodeSuccessActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("series", resultString.substring(4,14));
            bundle.putString("devicetype", resultString.substring(14));
            bundle.putString("position", position);
            bundle.putString("digitaladdr", sb.getTaskdigitaladdress()+"");
            bundle.putString("tasknumber", sb.getTasknumber()+"");
            bundle.putString("fathertask_id", fathertask_id);
            bundle.putString("logininfo_id", logininfo_id);
            bundle.putString("project_id", project_id);
            bundle.putString("controller_id", controller_id);
            resultIntent.putExtras(bundle);
            startActivityForResult(resultIntent,8);
        }
    }


    protected void setViewfinderView(ViewfinderView view) {
        viewfinderView = view;
    }

    /**
     * 切换散光灯状态
     */
    public void toggleFlashLight() {
        if (flashLightOpen) {
            setFlashLightOpen(false);
        } else {
            setFlashLightOpen(true);
        }
    }

    /**
     * 设置闪光灯是否打开
     * @param open
     */
    public void setFlashLightOpen(boolean open) {
        if (flashLightOpen == open) return;

        flashLightOpen = !flashLightOpen;
        CameraManager.get().setFlashLight(open);
    }

    /**
     * 当前散光灯是否打开
     * @return
     */
    public boolean isFlashLightOpen() {
        return flashLightOpen;
    }

    /**
     * 打开相册
     */
    public void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_PHOTO);
        } else {
            ActionUtils.startActivityForGallery(mActivity, ActionUtils.PHOTO_REQUEST_GALLERY);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    protected void restartPreview() {
        // 当界面跳转时 handler 可能为null
        if (handler != null) {
            Message restartMessage = Message.obtain();
            restartMessage.what = R.id.restart_preview;
            handler.handleMessage(restartMessage);
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 100L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.series_input:
                Intent resultIntent = new Intent(CaptureActivity.this,QrCodeInputActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("position", position);
                bundle.putString("series", sb.getTaskserialnumber());
                bundle.putString("digitaladdr", sb.getTaskdigitaladdress()+"");
                bundle.putString("tasknumber", sb.getTasknumber()+"");
                bundle.putString("fathertask_id", fathertask_id);
                bundle.putString("logininfo_id", logininfo_id);
                bundle.putString("project_id", project_id);
                bundle.putString("controller_id", controller_id);
                resultIntent.putExtras(bundle);
                startActivityForResult(resultIntent,8);
                break;
            case R.id.series_add:
                //新增记录
                AddSon();
                break;
            case R.id.flash_ibtn:
                if (flashLightOpen) {
                    flashIbtn.setImageResource(R.drawable.ic_flash_off_white_24dp);
                } else {
                    flashIbtn.setImageResource(R.drawable.ic_flash_on_white_24dp);
                }
                toggleFlashLight();
                break;
            case R.id.gallery_tv:
                openGallery();
                break;
            default:
        }
    }

    private void AddSon() {
        if(list.size()>=239){
            ToastUtil.showToastShort("地址达到上限，无法新增");
            return;
        }
        if(Integer.parseInt(position)==list.size()-1){
                //最后一条，新增
            SonTaskBean sb1=new SonTaskBean();
            int digitaladdress=0;
            for (int i = list.size()-1; i >=0 ; i--) {
                //往后倒数，直到碰见第一个不为空的数字地址
                if(list.get(i).getTaskdigitaladdress()!=-1){
                    digitaladdress=list.get(i).getTaskdigitaladdress();
                    break;
                }
            }
            digitaladdress+=1;
            //再查重
            for (int i = 0; i <list.size() ; i++) {
                if(digitaladdress==list.get(i).getTaskdigitaladdress()){
                    digitaladdress++;
                    //再重头查
                    i=0;
                }
            }
            sb1.setTaskdigitaladdress(digitaladdress);
            sb1.setTasknumber(list.get(list.size() - 1).getTasknumber() + 1);
            sb1.setLogininfo_id(logininfo_id);
            sb1.setProject_id(project_id);
            sb1.setController_id(controller_id);
            sb1.setFathertask_id(fathertask_id);
            sb1.save();
            sb=sb1;
            list.add(sb1);
        }
        position=String.valueOf(Integer.parseInt(position)+1);
        qr_number2.setText(sb.getTasknumber()+"");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        Log.d(TAG, "xxxxxxxxxxxxxxxxxxxonResume");
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        final AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "xxxxxxxxxxxxxxxxxxxonPause");
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        if (flashIbtn != null) {
            flashIbtn.setImageResource(R.drawable.ic_flash_off_white_24dp);
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        LogUtil.e("CaptaureActivity-->onDestroy");
        inactivityTimer.shutdown();
        super.onDestroy();
    }
}