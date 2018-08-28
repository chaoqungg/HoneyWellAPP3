package com.honeywell.honeywellproject.BleTaskModule.AddressDownLoad.adapter;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.ResourceUtil;

import java.util.List;

/**
 * Created by QHT on 2017-10-14.
 */
public class AddressDownlaodAdapterNewUI extends BaseQuickAdapter<SonTaskBean,BaseViewHolder> {


    public AddressDownlaodAdapterNewUI(@Nullable List<SonTaskBean> data) {
        super(data);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder oneSlideViewHolder = new BaseViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_address_download_item, parent, false));
        return oneSlideViewHolder;
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final SonTaskBean sonTaskBean) {
        baseViewHolder.setText(R.id.tv_name1,"编"+ResourceUtil.getString(R.string.backspace_hanzi1)+"号：");
        baseViewHolder.setText(R.id.tv_addr1,"地"+ResourceUtil.getString(R.string.backspace_hanzi1)+"址：");
        baseViewHolder.setText(R.id.tv_description1,"描"+ResourceUtil.getString(R.string.backspace_hanzi1)+"述：");
        baseViewHolder.setText(R.id.tv_leixing2,sonTaskBean.getDevicetype());
        baseViewHolder.setText(R.id.tv_name2,sonTaskBean.getTasknumber()+"");
        baseViewHolder.setText(R.id.tv_series2,sonTaskBean.getTaskserialnumber());
        baseViewHolder.setText(R.id.tv_top2_1_2,sonTaskBean.getTaskserialnumber());
        baseViewHolder.setText(R.id.tv_addr2,sonTaskBean.getTaskdigitaladdress()+"");
        baseViewHolder.setText(R.id.tv_top2_2_2,sonTaskBean.getTaskdigitaladdress()+"");
        baseViewHolder.setText(R.id.tv_description2,sonTaskBean.getSondescription());
        //进度
        if(sonTaskBean.isProcess()){
            baseViewHolder.getView(R.id.ll_right_uncomple).setVisibility(View.GONE);
            baseViewHolder.getView(R.id.ll_right_comple).setVisibility(View.VISIBLE);
        }else{
            baseViewHolder.getView(R.id.ll_right_uncomple).setVisibility(View.VISIBLE);
            baseViewHolder.setText(R.id.tv_right_uncomple,"未完成");
            baseViewHolder.getView(R.id.ll_right_comple).setVisibility(View.GONE);
        }
        if(sonTaskBean.isexpand()){
            baseViewHolder.setText(R.id.tv_name1,"编"+ResourceUtil.getString(R.string.backspace_hanzi1)+"号：");
            baseViewHolder.getView(R.id.rl_sontask_fold).setVisibility(View.VISIBLE);
            baseViewHolder.getView(R.id.ll_top2).setVisibility(View.INVISIBLE);
            baseViewHolder.setImageDrawable(R.id.iv_arrow, ResourceUtil.getDrawable(R.drawable.arrow_up_34));
        }else{
            baseViewHolder.setText(R.id.tv_name1,"编号：");
            baseViewHolder.getView(R.id.rl_sontask_fold).setVisibility(View.GONE);
            baseViewHolder.getView(R.id.ll_top2).setVisibility(View.VISIBLE);
            baseViewHolder.setImageDrawable(R.id.iv_arrow, ResourceUtil.getDrawable(R.drawable.arrow_down_34));
        }
        baseViewHolder.setOnClickListener(R.id.rl_sontask_top, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sonTaskBean.isexpand()){
                    baseViewHolder.setText(R.id.tv_name1,"编号：");
                    baseViewHolder.getView(R.id.rl_sontask_fold).setVisibility(View.GONE);
                    baseViewHolder.getView(R.id.ll_top2).setVisibility(View.VISIBLE);
                    sonTaskBean.setIsexpand(false);
                    baseViewHolder.setImageDrawable(R.id.iv_arrow, ResourceUtil.getDrawable(R.drawable.arrow_down_34));
                }else{
                    baseViewHolder.setText(R.id.tv_name1,"编"+ResourceUtil.getString(R.string.backspace_hanzi1)+"号：");
                    baseViewHolder.getView(R.id.rl_sontask_fold).setVisibility(View.VISIBLE);
                    baseViewHolder.getView(R.id.ll_top2).setVisibility(View.GONE);
                    sonTaskBean.setIsexpand(true);
                    baseViewHolder.setImageDrawable(R.id.iv_arrow, ResourceUtil.getDrawable(R.drawable.arrow_up_34));
                }
            }
        });
        baseViewHolder.setOnClickListener(R.id.ll_arrow, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sonTaskBean.isexpand()){
                    baseViewHolder.setText(R.id.tv_name1,"编号：");
                    baseViewHolder.getView(R.id.rl_sontask_fold).setVisibility(View.GONE);
                    baseViewHolder.getView(R.id.ll_top2).setVisibility(View.VISIBLE);
                    sonTaskBean.setIsexpand(false);
                    baseViewHolder.setImageDrawable(R.id.iv_arrow, ResourceUtil.getDrawable(R.drawable.arrow_down_34));
                }else{
                    baseViewHolder.setText(R.id.tv_name1,"编"+ResourceUtil.getString(R.string.backspace_hanzi1)+"号：");
                    baseViewHolder.getView(R.id.rl_sontask_fold).setVisibility(View.VISIBLE);
                    baseViewHolder.getView(R.id.ll_top2).setVisibility(View.GONE);
                    sonTaskBean.setIsexpand(true);
                    baseViewHolder.setImageDrawable(R.id.iv_arrow, ResourceUtil.getDrawable(R.drawable.arrow_up_34));
                }
            }
        });
    }
}
