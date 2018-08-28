package com.honeywell.honeywellproject.BleTaskModule.SingleAddress.MemoryAddressing.widge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.FatherTask.data.FatherTaskBean;
import com.honeywell.honeywellproject.BleTaskModule.SingleAddress.MemoryAddressing.adapter.MemoryTaskAdapter;
import com.honeywell.honeywellproject.BleTaskModule.SingleAddress.SingleAddressActivity;
import com.honeywell.honeywellproject.LoginModule.data.LoginInfoBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.ConstantUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MemoryTaskActivity extends ToolBarActivity {


    @BindView(R.id.rv_memorytask)
    RecyclerView rvMemorytask;
    @BindView(R.id.view_empty)
    TextView     viewEmpty;
    @BindView(R.id.tv_mewlooptask)
    TextView     tvMewlooptask;

    private SimpleDateFormat simpleDateFormat;
    private MemoryTaskAdapter  madapter;
    private List<FatherTaskBean> list=new ArrayList<FatherTaskBean>();
    private LoginInfoBean loginbean;
    private int loginId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_memory_task;
    }

    private void initView() {
        getToolbarTitle().setText("任务编址回路列表");
        rvMemorytask.setLayoutManager(new LinearLayoutManager(this));
        madapter = new MemoryTaskAdapter(list, this);
        rvMemorytask.setAdapter(madapter);
        rvMemorytask.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent=new Intent(MemoryTaskActivity.this, SingleAddressActivity.class);
                intent.putExtra(ConstantUtil.MEMORYTASK,"3");
                intent.putExtra("position",position+"");
                intent.putExtra("loginId",loginId+"");
                startActivity(intent);
            }
        });
    }

    private void initlist() {
        String username = SharePreferenceUtil.getStringSP("currentusername", "");
        List<LoginInfoBean> loginList = DataSupport.where("username = ?", username).find(LoginInfoBean.class);
        if (loginList.size() > 1) {
            ToastUtil.showToastShort("表中存在重复用户名，请联系开发人员");
            return;
        }
        loginbean = loginList.get(0);
        loginId=loginbean.getId();
        list.clear();
        List<FatherTaskBean> list2 = DataSupport.where("logininfo_id = ?", loginId + "").find(FatherTaskBean.class);
        list.addAll(list2);
        if (list.size() <= 0) {
            viewEmpty.setVisibility(View.VISIBLE);
        }else{
            viewEmpty.setVisibility(View.GONE);
        }
        madapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initlist();
    }

    @OnClick(R.id.tv_mewlooptask)
    public void onViewClicked() {
        Intent intent=new Intent(this, SingleAddressActivity.class);
        intent.putExtra(ConstantUtil.MEMORYTASK,"2");
        intent.putExtra("prefatherListSize",list.size()+"");
        intent.putExtra("loginId",loginId+"");
        startActivity(intent);
    }
}
