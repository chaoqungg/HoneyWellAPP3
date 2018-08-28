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

public class Fragment5 extends BaseFragment {
    public List<bigLightState> bigLightStateList =new ArrayList<bigLightState>();
    HashMap<Integer, Boolean> ImageMap = new HashMap<>();
    @BindView(R.id.single)
    ImageView               single;
    @BindView(R.id.double_left)
    ImageView               doubleLeft;
    @BindView(R.id.double_right)
    ImageView               doubleRight;
    @BindView(R.id.ll_light_1)
    unInterceptLinearLayout llLight1;
    @BindView(R.id.ll_light_2)
    unInterceptLinearLayout llLight2;
    private Runnable leftGlientRunable, rightGlientRunable, centerGlientRunable;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_5;
    }

    @Override
    public void initView(View view) {
        if(!ImageMap.isEmpty()){
            return;
        }
        ImageMap.put(R.id.single, false);
        ImageMap.put(R.id.double_left, false);
        ImageMap.put(R.id.double_right, false);
        bigLightStateList.clear();
        bigLightStateList.add(new bigLightState(true));
        bigLightStateList.add(new bigLightState(false));
        llLight1.setIntercept(false);
        llLight1.setBackgroundColor(ResourceUtil.getColor(R.color.blue_shadow));
    }

    int clickCounttop   = 0;
    int clickCountbottom1 = 0;
    int clickCountbottom2 = 0;
    @OnClick({R.id.single,R.id.ll_light_1,R.id.ll_light_2,R.id.double_left,R.id.double_right})
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
            case R.id.single:
                clickCounttop++;
                if (clickCounttop % 3 == 1) {
                    //常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "07"),"04FB07F8"));
                    openImage(ImageMap, single, R.id.single, R.drawable.single_light);
                }else if (clickCounttop % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "03"),"04FB03FC"));
                    doCenterGlintRunable(single,R.drawable.single_light,R.drawable.single_dark);
                }else if (clickCounttop % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "0B"),"04FB0BF4"));
                    closeImage(ImageMap, single, R.id.single, R.drawable.single_dark);
                    stopCenterGlintRunable();
                }
                break;
            case R.id.double_left:
                //双向地埋灯的左箭头
                clickCountbottom1++;
                if (clickCountbottom1 % 3 == 1) {
                    //常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "07"),"04FB07F8"));
                    openImage(ImageMap, doubleLeft, R.id.double_left, R.drawable.double_light_left);
                }else if (clickCountbottom1 % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "03"),"04FB03FC"));
                    doLeftGlintRunable(doubleLeft,R.drawable.double_light_left,R.drawable.double_dark_left);
                }else if (clickCountbottom1 % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "0B"),"04FB0BF4"));
                    closeImage(ImageMap, doubleLeft, R.id.double_left, R.drawable.double_dark_left);
                    stopLeftGlintRunable();
                }
                break;
            case R.id.double_right:
                //双向地埋灯的右箭头
                clickCountbottom2++;
                if (clickCountbottom2 % 3 == 1) {
                    //常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "06"),"04FB06F9"));
                    openImage(ImageMap, doubleRight, R.id.double_right, R.drawable.double_light_right);
                }else if (clickCountbottom2 % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "02"),"04FB02FD"));
                    doRightGlintRunable(doubleRight, R.drawable.double_light_right,R.drawable.double_dark_right);
                }else if (clickCountbottom2 % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "0A"),"04FB0AF5"));
                    closeImage(ImageMap, doubleRight, R.id.double_right, R.drawable.double_dark_right);
                    stopRightGlintRunable();
                }
                break;
            case   R.id.ll_light_1:
                if(bigLightStateList.get(0).isSelect()){
                    return;
                }
                clickCountbottom1=0;
                clickCountbottom2=0;
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
    private Handler handler   = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
            }
            super.handleMessage(msg);
        }
    };
    /**
     * 左灯闪烁的定时任务
     */
    private int leftGlientCount = 0;

    private void doLeftGlintRunable(final ImageView leftView, final int drawableIDlight, final int drawableIDDark) {

        leftGlientRunable = new Runnable() {
            @Override
            public void run() {
                if (leftGlientCount % 2 == 0) {
                    leftView.setImageDrawable(ResourceUtil.getDrawable(drawableIDlight));
                } else {
                    leftView.setImageDrawable(ResourceUtil.getDrawable(drawableIDDark));
                }
                handler.postDelayed(this, 400);
                leftGlientCount++;
            }
        };
        handler.post(leftGlientRunable);
    }

    /**
     * 停止左灯闪烁
     */
    private void stopLeftGlintRunable() {
        leftGlientCount = 0;
        handler.removeCallbacks(leftGlientRunable);
    }

    /**
     * 右灯闪烁的定时任务
     */
    private int rightGlientCount = 0;

    private void doRightGlintRunable(final ImageView rightView, final int drawableIDlight, final int drawableIDDark) {

        rightGlientRunable = new Runnable() {
            @Override
            public void run() {
                if (rightGlientCount % 2 == 0) {
                    rightView.setImageDrawable(ResourceUtil.getDrawable(drawableIDlight));
                } else {
                    rightView.setImageDrawable(ResourceUtil.getDrawable(drawableIDDark));
                }
                handler.postDelayed(this, 400);
                rightGlientCount++;
            }
        };
        handler.post(rightGlientRunable);
    }

    /**
     * 停止右灯闪烁
     */
    private void stopRightGlintRunable() {
        rightGlientCount = 0;
        handler.removeCallbacks(rightGlientRunable);
    }

    /**
     * 中灯闪烁的定时任务
     */
    private int  centerGlientCount = 0;
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
        closeImage(ImageMap,single,R.id.single,R.drawable.single_dark);
        closeImage(ImageMap,doubleLeft,R.id.double_left,R.drawable.double_dark_left);
        closeImage(ImageMap,doubleRight,R.id.double_right,R.drawable.double_dark_right);
        stopLeftGlintRunable();
        stopCenterGlintRunable();
        stopRightGlintRunable();
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
        stopLeftGlintRunable();
        stopCenterGlintRunable();
        stopRightGlintRunable();
        ImageMap.clear();
        unbinder.unbind();
    }
}
