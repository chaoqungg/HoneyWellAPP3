package com.honeywell.honeywellproject.BleTaskModule.SingleAddress.MemoryAddressing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.honeywellproject.BleTaskModule.FatherTask.data.FatherTaskBean;
import com.honeywell.honeywellproject.R;
import java.util.List;

/**
 * Created by QHT on 2017-12-02.
 */
public class MemoryTaskAdapter extends BaseQuickAdapter<FatherTaskBean,BaseViewHolder> {

    private Context context;

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = new BaseViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_memorytask_item, parent, false));
        return viewHolder;
    }
    @Override
    protected void convert(BaseViewHolder helper, FatherTaskBean fatherTaskBean) {
        helper.setText(R.id.tv_memory_loopitem,"回路"+fatherTaskBean.getTasknumber());
    }

    public MemoryTaskAdapter(List<FatherTaskBean> list, Context context) {
        super(list);
        this.context=context;
    }
}
