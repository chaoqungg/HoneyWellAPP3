package com.honeywell.honeywellproject.BleTaskModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.ProjectTask.widge.ProjectTaskActivity;
import com.honeywell.honeywellproject.BleTaskModule.SingleAddress.SingleFreeAddressing.SingleFreeAddressActivity;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.ConstantUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class BleAddressingTypeActivity extends ToolBarActivity {


    @BindView(R.id.btn_batch)
    ImageView    btnBatch;
    @BindView(R.id.btn_unmemory)
    ImageView btnUnmemory;
    @BindView(R.id.btn_memory)
    ImageView btnMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_ble_addressingtype;
    }

    private void initView() {
        getToolbarTitle().setText("编址类型");
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @OnClick({R.id.btn_batch,R.id.btn_unmemory, R.id.btn_memory})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_batch:
                //批量编址
                Intent intent1=new Intent(this, ProjectTaskActivity.class);
                intent1.putExtra("addresstype","piliang");
                startActivity(intent1);
                break;
            case R.id.btn_unmemory:
                //自由编址
                Intent intent=new Intent(this, SingleFreeAddressActivity.class);
                intent.putExtra(ConstantUtil.MEMORYTASK,"1");
                startActivity(intent);
                break;
            case R.id.btn_memory:
                //工程编址
                Intent intent2=new Intent(this, ProjectTaskActivity.class);
                intent2.putExtra("addresstype","gongcheng");
                startActivity(intent2);
                break;
            default:
        }
    }
}
