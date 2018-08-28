package com.honeywell.honeywellproject.BleTaskModule.Log.widge;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.Log.adapter.LogAdapter;
import com.honeywell.honeywellproject.BleTaskModule.Log.data.LogBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * @author QHT
 */
public class LogActivity extends ToolBarActivity {

    @BindView(R.id.rv_logtask)
    RecyclerView rvLogtask;
    @BindView(R.id.view_empty)
    TextView     viewEmpty;
    private LogAdapter madapter;
    private List<LogBean> logBeanList = new ArrayList<LogBean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initLogList();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_log;
    }

    private void initView() {
        getToolbarTitle().setText("历史记录");
        rvLogtask.setLayoutManager(new LinearLayoutManager(this));
        madapter = new LogAdapter(logBeanList);
        rvLogtask.setAdapter(madapter);
    }

    private void initLogList() {
        logBeanList.clear();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String data = simpleDateFormat.format(new Date());
        //清除历史的编址信息，只保留当天的
        DataSupport.deleteAll(LogBean.class, "substr(data,1,10)  != ? ", data);
        List<LogBean>  logBeanList2 = DataSupport.where("user = ? ", SharePreferenceUtil.getStringSP("currentusername", "")).find(LogBean.class);
        logBeanList.addAll(logBeanList2);
        if (logBeanList.size() == 0) {
            viewEmpty.setVisibility(View.VISIBLE);
        } else {
            madapter.notifyDataSetChanged();
        }
    }
}
