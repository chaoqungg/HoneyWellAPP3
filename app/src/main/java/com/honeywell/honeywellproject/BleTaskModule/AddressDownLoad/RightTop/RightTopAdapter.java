package com.honeywell.honeywellproject.BleTaskModule.AddressDownLoad.RightTop;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.WidgeView.SwitchButton;
import com.honeywell.honeywellproject.WidgeView.SwitchButton.OnCheckedChangeListener;

import java.util.List;

/**
 * Created by QHT on 2018-01-20.
 */
public class RightTopAdapter extends BaseQuickAdapter<RightTopBean, BaseViewHolder> {


    public interface  RightTopAudioOnCheckedChangeListener{
        void check(SwitchButton button, boolean isSelect,int position);
    }
    public void setRightTopAudioOnCheckedChangeListener(RightTopAudioOnCheckedChangeListener onCheckedChangeListener){
        this.onCheckedChangeListener=onCheckedChangeListener;
    }
    private RightTopAudioOnCheckedChangeListener onCheckedChangeListener;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    private Context context;

    public RightTopAdapter(@LayoutRes int layoutResId, @Nullable List<RightTopBean> data) {
        super(layoutResId, data);
    }
    public RightTopAdapter(List<RightTopBean> data, Context context) {
        super(R.layout.activity_address_download_topright_item, data);
        this.context=context;
    }
    @Override
    protected void convert(final BaseViewHolder helper, RightTopBean item) {
                SwitchButton sb = ((SwitchButton)helper.getView(R.id.switch_button));
                helper.setText(R.id.tv_name,item.getName());
                if (item.select()){
                    sb.setChecked(true);
                }else{
                    sb.setChecked(false);
                }
                    sb.setOnCheckedChangeListener (new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                            if(onCheckedChangeListener!=null){
                                onCheckedChangeListener.check(view,isChecked,helper.getAdapterPosition());
                            }
                        }
                });
    }
}

