package com.honeywell.honeywellproject.BleTaskModule.SingleAddress.SingleTaskAddressing.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.honeywellproject.BleTaskModule.SingleAddress.SingleTaskAddressing.data.AddrBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.DataHandler;

import java.util.List;

/**
 *
 * @author QHT
 * @date 2017-01-08
 */
public class AddrAdapter extends BaseQuickAdapter<AddrBean,BaseViewHolder> {

    private Context context;
    private GridLayoutManager.LayoutParams ly;
    private LinearLayout.LayoutParams params;
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = new BaseViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_singletask_item, parent, false));
        return viewHolder;
    }
    @Override
    protected void convert(BaseViewHolder helper, AddrBean bean) {
        helper.itemView.setLayoutParams(params);
        String addr = DataHandler.Alone2Hex(bean.getAddr());
        helper.setText(R.id.tv_addr,addr);
        if(bean.isUsed()){
            helper.setTextColor(R.id.tv_addr, Color.parseColor("#ee3124"));
        }else{
            helper.setTextColor(R.id.tv_addr,  Color.parseColor("#696A6E"));
        }
    }

    public AddrAdapter(List<AddrBean> list, Context context, LinearLayout.LayoutParams params) {
        super(list);
        this.context=context;
        this.params = params;
    }
}
