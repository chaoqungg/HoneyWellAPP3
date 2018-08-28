package com.honeywell.honeywellproject.BleSuperModule.widge;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.AddressSearch.Test;
import com.honeywell.honeywellproject.BleTaskModule.BleAddressingTypeActivity;

import com.honeywell.honeywellproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by QHT on 2017-10-16.
 */
public class BleSuperActivity extends ToolBarActivity {
    @BindView(R.id.ll_super_top1)
    LinearLayout llSuperTop1;
    @BindView(R.id.ll_super_top2)
    LinearLayout llSuperTop2;
    @BindView(R.id.ll_super_top3)
    LinearLayout llSuperTop3;
    @BindView(R.id.ll_super_top4)
    LinearLayout llSuperTop4;
    @BindView(R.id.ll_super_11)
    LinearLayout llSuper11;
    @BindView(R.id.ll_super_12)
    LinearLayout llSuper12;
    @BindView(R.id.ll_super_13)
    LinearLayout llSuper13;
    @BindView(R.id.ll_super_14)
    LinearLayout llSuper14;
    @BindView(R.id.ll_super_21)
    LinearLayout llSuper21;
    @BindView(R.id.ll_super_22)
    LinearLayout llSuper22;
    @BindView(R.id.ll_super_23)
    LinearLayout llSuper23;
    @BindView(R.id.ll_super_24)
    LinearLayout llSuper24;
    private long mExitTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_super;
    }

    @OnClick({R.id.ll_super_top1, R.id.ll_super_top2, R.id.ll_super_top3, R.id.ll_super_top4, R.id.ll_super_11, R.id.ll_super_12, R.id.ll_super_13, R.id.ll_super_14, R.id.ll_super_21, R.id.ll_super_22, R.id.ll_super_23, R.id.ll_super_24})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_super_top1:
                //编址
                startActivity(new Intent(this, BleAddressingTypeActivity.class));
                break;
            case R.id.ll_super_top2:
                //地址搜索
                Intent intent=new Intent(this, Test.class);
                startActivity(intent);
                break;
            case R.id.ll_super_top3:
                break;
            case R.id.ll_super_top4:
                break;
            case R.id.ll_super_11:
                break;
            case R.id.ll_super_12:
                break;
            case R.id.ll_super_13:
                break;
            case R.id.ll_super_14:
                break;
            case R.id.ll_super_21:
                break;
            case R.id.ll_super_22:
                break;
            case R.id.ll_super_23:
                break;
            case R.id.ll_super_24:
                break;
            default:
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_menu).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected boolean isShowBacking() {
//        showBigBack();
        return false;
    }
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if ((System.currentTimeMillis() - mExitTime) > 2000) {
//                ToastUtil.showToastShort(ResourceUtil.getString(R.string.exit));
//                mExitTime = System.currentTimeMillis();
//            } else {
//                finish();
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
