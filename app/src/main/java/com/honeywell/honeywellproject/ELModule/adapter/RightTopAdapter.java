package com.honeywell.honeywellproject.ELModule.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.honeywellproject.ELModule.Log.widge.LogELActivity;
import com.honeywell.honeywellproject.ELModule.data.RightTopBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.WidgeView.SwitchButton;
import com.honeywell.honeywellproject.WidgeView.SwitchButton.OnCheckedChangeListener;

import java.util.List;

/**
 * Created by QHT on 2018-01-20.
 */
public class RightTopAdapter extends BaseMultiItemQuickAdapter<RightTopBean, BaseViewHolder> {

    public interface  RightTopAudioOnCheckedChangeListener{
        void check(SwitchButton button, boolean isSelect);
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
    public RightTopAdapter(List<RightTopBean> data, Context context) {
        super(data);


        addItemType(RightTopBean.history, R.layout.activity_freeaddr_history_topright_item);
        addItemType(RightTopBean.audio, R.layout.activity_freeaddr_audio_topright_item);
        this.context=context;
    }

    @Override
    protected void convert(BaseViewHolder helper, RightTopBean item) {
        switch (helper.getItemViewType()) {
            case RightTopBean.history:
                helper.setOnClickListener(R.id.ll_root_history, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(context, LogELActivity.class);
                        context.startActivity(intent);
                    }
                });
                break;
            case RightTopBean.audio:
                SwitchButton sb = ((SwitchButton)helper.getView(R.id.switch_button));
                if (SharePreferenceUtil.getBooleanSP("audioEL")) {
                    sb.setChecked(true);
                }else{
                    sb.setChecked(false);
                }

                    sb.setOnCheckedChangeListener (new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                            if(onCheckedChangeListener!=null){
                                onCheckedChangeListener.check(view,isChecked);
                            }
                        }
                });
                break;
            default:
        }
    }
}

