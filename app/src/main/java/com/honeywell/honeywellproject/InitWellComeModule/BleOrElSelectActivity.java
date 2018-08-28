package com.honeywell.honeywellproject.InitWellComeModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.clj.fastble.BleManager;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.LoginModule.widge.LoginActivity;
import com.honeywell.honeywellproject.LoginModule.widge.LoginELActivity;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BleOrElSelectActivity extends ToolBarActivity {

    @BindView(R.id.btn_el)
    ImageView btn_el;
    @BindView(R.id.btn_ble)
    ImageView btn_ble;

    private long mExitTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_ble_el_select;
    }

    private void initView() {
        getToolbarTitle().setText("操作类型");
    }


    @OnClick({R.id.btn_el, R.id.btn_ble})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_el:
                SharePreferenceUtil.setStringSP("apptype","el");
                startActivity(new Intent(this, LoginELActivity.class));
                break;
            case R.id.btn_ble:
                SharePreferenceUtil.setStringSP("apptype","ble");
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
        }
    }

    @Override
    protected boolean isShowBacking() {
        return false;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    ToastUtil.showToastShort(ResourceUtil.getString(R.string.exit));
                    mExitTime = System.currentTimeMillis();
                } else {
                    BleManager.getInstance().disconnectAllDevice();
                    BleManager.getInstance().disableBluetooth();
                    BleManager.getInstance().destroy();
                    finish();
                }
                return true;
            }
        return super.onKeyDown(keyCode, event);
    }
}
