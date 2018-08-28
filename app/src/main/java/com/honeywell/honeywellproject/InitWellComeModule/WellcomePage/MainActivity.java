package com.honeywell.honeywellproject.InitWellComeModule.WellcomePage;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.honeywell.honeywellproject.InitWellComeModule.BleOrElSelectActivity;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.DialogUtil;
import com.honeywell.honeywellproject.Util.TextUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity {
    @BindView(R.id.skip)
    LinearLayout skip;
    private PageFrameLayout contentFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        contentFrameLayout = (PageFrameLayout) findViewById(R.id.contentFrameLayout);
        // 设置资源文件和选中圆点
        contentFrameLayout.setUpViews(new int[]{
                R.layout.page_tab1,
                R.layout.page_tab2,
                R.layout.page_tab3}, R.mipmap.banner_on, R.mipmap.banner_off);
        checkPermissions();
    }
    @OnClick(R.id.skip)
    public void onViewClicked() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                    skip.post(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(MainActivity.this, BleOrElSelectActivity.class));
                            finish();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE};
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
                    StringBuilder sb=new StringBuilder();
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }else{
                            if(permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                                sb.append("位置信息权限\n");
                            }
                            if(permissions[i].equals(Manifest.permission.MODIFY_AUDIO_SETTINGS)){
                                sb.append("录音信息权限\n");
                            }if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                sb.append("写内存卡权限\n");
                            }
                        }
                    }
                    if(!TextUtil.isEmpty(sb.toString())){
                        DialogUtil.showAlertDialog(MainActivity.this,
                                "提示", "请在设置中打开如下权限：\n" + sb.toString() + "全部打开后使用APP", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                },  new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
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
                break;
            default:
        }
    }
}
