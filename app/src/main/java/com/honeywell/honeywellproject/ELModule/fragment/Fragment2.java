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

public class Fragment2 extends BaseFragment {
    public List<bigLightState> bigLightStateList =new ArrayList<bigLightState>();
    HashMap<Integer, Boolean> ImageMap = new HashMap<>();
    @BindView(R.id.f1_dark)
    ImageView               f1Dark;
    @BindView(R.id.ll_light_1)
    unInterceptLinearLayout llLight1;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_2;
    }

    @Override
    public void initView(View view) {
        if(!ImageMap.isEmpty()){
            return;
        }
        ImageMap.put(R.id.f1_dark, false);
        bigLightStateList.clear();
        bigLightStateList.add(new bigLightState(true));
        llLight1.setIntercept(false);
        llLight1.setBackgroundColor(ResourceUtil.getColor(R.color.blue_shadow));
    }

    int clickCountCenter = 0;
    @OnClick({R.id.f1_dark,R.id.ll_light_1})
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
            case R.id.f1_dark:
                clickCountCenter++;
                if (clickCountCenter % 3 == 1) {
                    //常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "05"),"04FB05FA"));
                    openImage(ImageMap,f1Dark,R.id.f1_dark,R.drawable.f1_light);
                }else if (clickCountCenter % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "01"),"04FB01FE"));
                    doCenterGlintRunable();
                }else if (clickCountCenter % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "09"),"04FB09F6"));
                    closeImage(ImageMap,f1Dark,R.id.f1_dark,R.drawable.f1_dark);
                    stopCenterGlintRunable();
                }
                break;
//            case   R.id.ll_light_1:
//                if(bigLightStateList.get(0).isSelect()){
//                    return;
//                }
//                resetBigLight(0);
//                break;
            default:
        }
    }
//    private void resetBigLight(int m){
//        closeAllImage();
//        bigLightStateList.get(0).setSelect(true);
//        llLight1.setIntercept(false);
//        // 改变第一个灯板的图片
//        llLight1.setBackgroundColor(ResourceUtil.getColor(R.color.blue_shadow));
//    }
    private  Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    /**
     * 中灯闪烁的定时任务
     */
    private int     centerGlientCount = 0;
    private Runnable centerGlientRunable;
    private void doCenterGlintRunable() {
        centerGlientRunable = new Runnable() {
            @Override
            public void run() {
                if (centerGlientCount % 2 == 0) {
                    f1Dark.setImageDrawable(ResourceUtil.getDrawable(R.drawable.f1_light));
                } else {
                    f1Dark.setImageDrawable(ResourceUtil.getDrawable(R.drawable.f1_dark));
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
        closeImage(ImageMap,f1Dark,R.id.f1_dark,R.drawable.f1_dark);

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
