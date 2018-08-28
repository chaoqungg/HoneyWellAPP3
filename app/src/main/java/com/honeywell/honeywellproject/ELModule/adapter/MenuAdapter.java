package com.honeywell.honeywellproject.ELModule.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.honeywellproject.ELModule.data.MenuBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.WidgeView.SwitchButton;

import java.util.List;

/**
 *
 * @author QHT
 * @date 2018-01-20
 */
public class MenuAdapter extends BaseMultiItemQuickAdapter<MenuBean, BaseViewHolder> {
    public interface  OnCheckedChangeListener{
        void check(int i,SwitchButton button,boolean isSelect);
    }
    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener){
        this.onCheckedChangeListener=onCheckedChangeListener;
    }
    private OnCheckedChangeListener onCheckedChangeListener;
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    private Context context;
    public MenuAdapter(List<MenuBean> data, Context context) {
        super(data);
        addItemType(MenuBean.swichButtonitem, R.layout.activity_menu_switchbutton_item);
        addItemType(MenuBean.normalitem, R.layout.activity_menu_normal_item);
        this.context=context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, MenuBean item) {
        helper.setText(R.id.name,item.getName());
        helper.setImageDrawable(R.id.image, ResourceUtil.getDrawable(item.getImage()));
        switch (helper.getItemViewType()) {
            case MenuBean.swichButtonitem:
                //蓝色感叹号的提示按钮
                helper.addOnClickListener(R.id.tips);
//                    helper.addOnLongClickListener(R.id.tips);
                    SwitchButton sb = ((SwitchButton)helper.getView(R.id.switch_button));
                if(helper.getAdapterPosition()==0){
                        sb.setChecked(SharePreferenceUtil.getBooleanSP("jieneng"));
                }else if(helper.getAdapterPosition()==1){
                    sb.setChecked(SharePreferenceUtil.getBooleanSP("fanzhuan"));
                }
                    sb.setOnCheckedChangeListener (new SwitchButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                            if(onCheckedChangeListener!=null){
                                onCheckedChangeListener.check(helper.getAdapterPosition(),view,isChecked);
                            }
                        }
                    });
                break;
            case MenuBean.normalitem:
                helper.setText(R.id.tv_drive,item.getDrive());
                //根布局添加id，点击事件
               helper.addOnClickListener(R.id.ll_root);
                break;
            default:
        }
    }
}

