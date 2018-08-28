package com.honeywell.honeywellproject.BleTaskModule.Log.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.honeywellproject.BleTaskModule.Log.data.LogBean;
import com.honeywell.honeywellproject.R;

import java.util.List;

/**
 * Created by QHT on 2017-12-26.
 */
public class LogAdapter extends BaseQuickAdapter<LogBean,BaseViewHolder> {

    private Context context;

    public LogAdapter(@Nullable List<LogBean> data) {
        super(data);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder = new BaseViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_log_item, parent, false));
        //add holder
        return baseViewHolder;
    }
    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final LogBean logTaskBean) {
        baseViewHolder.setText(R.id.tv_log_time,logTaskBean.getData());
        baseViewHolder.setText(R.id.tv_log_protocoltype,logTaskBean.getProtocoltype());
        if("read".equals(logTaskBean.getOperatetype())){
            baseViewHolder.setText(R.id.tv_log_opertype,"读取");
        }else{
            baseViewHolder.setText(R.id.tv_log_opertype,"编写");
        }
        baseViewHolder.setText(R.id.tv_log_detail,logTaskBean.getDetail());
        }
}
