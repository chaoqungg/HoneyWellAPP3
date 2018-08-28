package com.honeywell.honeywellproject.ELModule.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.clj.fastble.BleManager;
import com.honeywell.honeywellproject.BaseActivity.BaseApplication;
import com.honeywell.honeywellproject.BaseActivity.BaseFragment;
import com.honeywell.honeywellproject.ELModule.ELActivityNewUI;
import com.honeywell.honeywellproject.ELModule.data.LightEvent;
import com.honeywell.honeywellproject.ELModule.data.bigLightState;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.ELUtil;
import com.honeywell.honeywellproject.Util.EventBusUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;
import com.honeywell.honeywellproject.WidgeView.unInterceptLinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Fragment4 extends BaseFragment {
    public List<bigLightState> bigLightStateList =new ArrayList<bigLightState>();
    HashMap<Integer, Boolean> ImageMap = new HashMap<>();
    @BindView(R.id.diaozhuang)
    ImageView               diaozhuang;
    @BindView(R.id.bigua)
    ImageView               bigua;
    @BindView(R.id.ll_light_1)
    unInterceptLinearLayout llLight1;
    @BindView(R.id.ll_light_2)
    unInterceptLinearLayout llLight2;
    private Runnable centerGlientRunable;


    @Override
    public int getContentViewId() {
        return R.layout.fragment_4;
    }

    @Override
    public void initView(View view) {
        if(!ImageMap.isEmpty()){
            return;
        }
        ImageMap.put(R.id.diaozhuang, false);
        ImageMap.put(R.id.bigua, false);
        bigLightStateList.clear();
        bigLightStateList.add(new bigLightState(true));
        bigLightStateList.add(new bigLightState(false));
        llLight1.setIntercept(false);
        llLight1.setBackgroundColor(ResourceUtil.getColor(R.color.blue_shadow));
    }
    int clickCounttop   = 0;
    int clickCountbottom = 0;
    @OnClick({R.id.diaozhuang,R.id.bigua,R.id.ll_light_1,R.id.ll_light_2})
    public void onViewClicked(View view) {
        if(!((ELActivityNewUI)getActivity()).inIR){
            //如果没有内置红外
            if(!BleManager.getInstance().isConnected(BaseApplication.elDevice)){
                //且没有连接蓝牙设备
                ToastUtil.showToastShort("红外设备未连接");
                return;
            }
        }
        switch (view.getId()) {
            case R.id.diaozhuang:
                clickCounttop++;
                if (clickCounttop % 3 == 1) {
                    //常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "05"),"04FB05FA"));
                    openImage(ImageMap, diaozhuang, R.id.diaozhuang, R.drawable.yuan_light);
                }else if (clickCounttop % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "01"),"04FB01FE"));
                    doCenterGlintRunable(diaozhuang,R.drawable.yuan_light,R.drawable.yuan_dark);
                }else if (clickCounttop % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "09"),"04FB09F6"));
                    closeImage(ImageMap, diaozhuang, R.id.diaozhuang, R.drawable.yuan_dark);
                    stopCenterGlintRunable();
                }
                break;
            case R.id.bigua:
                clickCountbottom++;
                if (clickCountbottom % 3 == 1) {
                    //常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "05"),"04FB05FA"));
                    openImage(ImageMap, bigua, R.id.bigua, R.drawable.fang_light);
                }else if (clickCountbottom % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "01"),"04FB01FE"));
                    doCenterGlintRunable(bigua,R.drawable.fang_light,R.drawable.fang_dark);
                }else if (clickCountbottom % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "09"),"04FB09F6"));
                    closeImage(ImageMap, bigua, R.id.bigua, R.drawable.fang_dark);
                    stopCenterGlintRunable();
                }
                break;
            case   R.id.ll_light_1:
                if(bigLightStateList.get(0).isSelect()){
                    return;
                }
                clickCountbottom=0;
                resetBigLight(0);
                break;
            case   R.id.ll_light_2:
                if(bigLightStateList.get(1).isSelect()){
                    return;
                }
                clickCounttop=0;
                resetBigLight(1);
                break;
            default:
        }
    }
    private void resetBigLight(int m){
        closeAllImage();
        for (int i = 0; i < bigLightStateList.size(); i++) {
            bigLightStateList.get(i).setSelect(false);
        }
        bigLightStateList.get(m).setSelect(true);
        switch (m){
            case 0:
                llLight1.setIntercept(false);
                llLight2.setIntercept(true);
                // 改变第一个灯板的图片
                llLight1.setBackgroundColor(ResourceUtil.getColor(R.color.blue_shadow));
                llLight2.setBackgroundColor(ResourceUtil.getColor(R.color.white));
                break;
            case 1:
                llLight1.setIntercept(true);
                llLight2.setIntercept(false);
                // 改变第二个灯板的图片
                llLight1.setBackgroundColor(ResourceUtil.getColor(R.color.white));
                llLight2.setBackgroundColor(ResourceUtil.getColor(R.color.blue_shadow));
                break;
            default:
        }
    }
    private Handler handler           = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
            }
            super.handleMessage(msg);
        }
    };
    /**
     * 中灯闪烁的定时任务
     */
    private int     centerGlientCount = 0;
    private void doCenterGlintRunable(final ImageView centerView, final int drawableIDlight, final int drawableIDDark) {
        centerGlientRunable = new Runnable() {
            @Override
            public void run() {
                if (centerGlientCount % 2 == 0) {
                    centerView.setImageDrawable(ResourceUtil.getDrawable(drawableIDlight));
                } else {
                    centerView.setImageDrawable(ResourceUtil.getDrawable(drawableIDDark));
                }
                handler.postDelayed(this, 400);
                centerGlientCount++;
            }
        };
        handler.post(centerGlientRunable);
    }

    /**
     * 停止中灯闪烁
     */
    private void stopCenterGlintRunable() {
        centerGlientCount = 0;
        handler.removeCallbacks(centerGlientRunable);
    }


    public void closeAllImage(){
        closeImage(ImageMap,diaozhuang,R.id.diaozhuang,R.drawable.yuan_dark);
        closeImage(ImageMap,bigua,R.id.bigua,R.drawable.fang_dark);
        stopCenterGlintRunable();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopCenterGlintRunable();
        ImageMap.clear();
        unbinder.unbind();
    }
}
