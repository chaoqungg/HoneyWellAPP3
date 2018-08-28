package com.honeywell.honeywellproject.ELModule.Log.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.honeywellproject.ELModule.Log.data.LogELBean;
import com.honeywell.honeywellproject.R;

import java.util.List;

/**
 *
 * @author QHT
 * @date 2017-12-26
 */
public class LogELAdapter extends BaseQuickAdapter<LogELBean,BaseViewHolder> {

    private Context context;

    public LogELAdapter(@Nullable List<LogELBean> data) {
        super(data);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder = new BaseViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_log_el_item, parent, false));
        //add holder
        return baseViewHolder;
    }
    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final LogELBean logTaskBean) {
        baseViewHolder.setText(R.id.tv_log_time,logTaskBean.getData());
        baseViewHolder.setText(R.id.tv_log_detail,logTaskBean.getDetail());
        }
}
