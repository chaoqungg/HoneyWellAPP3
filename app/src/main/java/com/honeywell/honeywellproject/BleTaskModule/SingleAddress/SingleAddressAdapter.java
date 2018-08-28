package com.honeywell.honeywellproject.BleTaskModule.SingleAddress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.honeywellproject.R;

import java.util.List;

/**
 * Created by QHT on 2017-12-02.
 */
public class SingleAddressAdapter extends BaseQuickAdapter<Integer,BaseViewHolder> {

    private Context context;

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = new BaseViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_singleaddress_item, parent, false));
        return viewHolder;
    }
    @Override
    protected void convert(BaseViewHolder helper, Integer intValue) {
        helper.setText(R.id.tv_enableaddress,intValue+"");
    }

    public SingleAddressAdapter(List<Integer> list, Context context) {
        super(list);
        this.context=context;
    }
}
