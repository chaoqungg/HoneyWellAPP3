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
import butterknife.Unbinder;

public class Fragment1 extends BaseFragment {


    HashMap<Integer, Boolean> ImageMap = new HashMap<>();

    public List<bigLightState> bigLightStateList =new ArrayList<bigLightState>();
    @BindView(R.id.left_dark)
    ImageView               leftDark;
    @BindView(R.id.leftrun_dark)
    ImageView               leftrunDark;
    @BindView(R.id.right_dark)
    ImageView               rightDark;
    @BindView(R.id.left_dark2)
    ImageView               leftDark2;
    @BindView(R.id.leftrun_dark2)
    ImageView               leftrunDark2;
    @BindView(R.id.rightrun_dark)
    ImageView               rightrunDark;
    @BindView(R.id.right_dark3)
    ImageView               rightDark3;
    @BindView(R.id.ll_light_1)
    unInterceptLinearLayout llLight1;
    @BindView(R.id.ll_light_2)
    unInterceptLinearLayout llLight2;
    @BindView(R.id.ll_light_3)
    unInterceptLinearLayout llLight3;
    Unbinder unbinder;
    /**
     * 左灯,中灯、右灯闪烁runable
     */
    private Runnable leftGlientRunable, rightGlientRunable, centerGlientRunable;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_1;
    }

    @Override
    public void initView(View view) {
        if (!ImageMap.isEmpty()) {
            return;
        }
        ImageMap.put(R.id.left_dark, false);
        ImageMap.put(R.id.leftrun_dark, false);
        ImageMap.put(R.id.right_dark, false);
        ImageMap.put(R.id.left_dark2, false);
        ImageMap.put(R.id.leftrun_dark2, false);
        ImageMap.put(R.id.well, false);
        ImageMap.put(R.id.no, false);
        ImageMap.put(R.id.rightrun_dark, false);
        ImageMap.put(R.id.right_dark3, false);
        bigLightStateList.clear();
        bigLightStateList.add(new bigLightState(true));
        bigLightStateList.add(new bigLightState(false));
        bigLightStateList.add(new bigLightState(false));
        llLight1.setIntercept(false);
        llLight1.setBackgroundColor(ResourceUtil.getColor(R.color.blue_shadow));
    }

    int clickCountLeft1   = 0;
    int clickCountCenter1 = 0;
    int clickCountRight1  = 0;
    int clickCountLeft2   = 0;
    int clickCountCenter2 = 0;
    int clickCountRight2 = 0;
    int clickCountCenter3 = 0;
    @OnClick({R.id.leftrun_dark, R.id.left_dark, R.id.right_dark,
            R.id.leftrun_dark2, R.id.left_dark2, R.id.rightrun_dark, R.id.right_dark3,
            R.id.ll_light_1,R.id.ll_light_2,R.id.ll_light_3})
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
            case R.id.left_dark:
                clickCountLeft1++;
                if (clickCountLeft1 % 3 == 1) {
                    //左灯常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "07"),"04FB07F8"));
                    openImage(ImageMap, leftDark, R.id.left_dark, R.drawable.left_light);
                }else if (clickCountLeft1 % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "03"),"04FB03FC"));
                    doLeftGlintRunable(leftDark);
                }else if (clickCountLeft1 % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "0B"),"04FB0BF4"));
                    closeImage(ImageMap, leftDark, R.id.left_dark, R.drawable.left_dark);
                    stopLeftGlintRunable();
                }
                break;
            case R.id.leftrun_dark:
                clickCountCenter1++;
                if (clickCountCenter1 % 3 == 1) {
                    //中灯常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "05"),"04FB05FA"));
                    openImage(ImageMap, leftrunDark, R.id.leftrun_dark, R.drawable.leftrun_light);
                }else if (clickCountCenter1 % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "01"),"04FB01FE"));
                    doCenterGlintRunable(leftrunDark,R.drawable.leftrun_light,R.drawable.leftrun_dark);
                }else if (clickCountCenter1 % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "09"),"04FB09F6"));
                    closeImage(ImageMap, leftrunDark, R.id.leftrun_dark, R.drawable.leftrun_dark);
                    stopCenterGlintRunable();
                }
                break;
            case R.id.right_dark:
                clickCountRight1++;
                if (clickCountRight1 % 3 == 1) {
                    //右灯常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "06"),"04FB06F9"));
                    openImage(ImageMap, rightDark, R.id.right_dark, R.drawable.right_light);
                }else if (clickCountRight1 % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "02"),"04FB02FD"));
                    doRightGlintRunable(rightDark);
                }else if (clickCountRight1 % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "0A"),"04FB0AF5"));
                    closeImage(ImageMap, rightDark, R.id.right_dark, R.drawable.right_dark);
                    stopRightGlintRunable();
                }
                break;
            case R.id.left_dark2:
                //第二个灯板的左箭头
                clickCountLeft2++;
                if (clickCountLeft2 % 3 == 1) {
                    //常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "07"),"04FB07F8"));
                    openImage(ImageMap, leftDark2, R.id.left_dark2, R.drawable.left_light);
                }else if (clickCountLeft2 % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "03"),"04FB03FC"));
                    doLeftGlintRunable(leftDark2);
                }else if (clickCountLeft2 % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "0B"),"04FB0BF4"));
                    closeImage(ImageMap, leftDark2, R.id.left_dark2, R.drawable.left_dark);
                    stopLeftGlintRunable();
                }
                break;
            case R.id.leftrun_dark2:
                //第二个灯板的中灯
                clickCountCenter2++;
                if (clickCountCenter2 % 3 == 1) {
                    //中灯常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "05"),"04FB05FA"));
                    openImage(ImageMap, leftrunDark2, R.id.leftrun_dark2, R.drawable.leftrun_light);
                }else if (clickCountCenter2 % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "01"),"04FB01FE"));
                    doCenterGlintRunable(leftrunDark2,R.drawable.leftrun_light,R.drawable.leftrun_dark);
                }else if (clickCountCenter2 % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "09"),"04FB09F6"));
                    closeImage(ImageMap, leftrunDark2, R.id.leftrun_dark2, R.drawable.leftrun_dark);
                    stopCenterGlintRunable();
                }
                break;
            case R.id.right_dark3:
                //第三个灯板的右箭头
                clickCountRight2++;
                if (clickCountRight2 % 3 == 1) {
                    //常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "07"),"04FB07F8"));
                    openImage(ImageMap, rightDark3, R.id.right_dark3, R.drawable.right_light);
                }else if (clickCountRight2 % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "03"),"04FB03FC"));
                    doRightGlintRunable(rightDark3);
                }else if (clickCountRight2 % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "0B"),"04FB0BF4"));
                    closeImage(ImageMap, rightDark3, R.id.right_dark3, R.drawable.right_dark);
                    stopRightGlintRunable();
                }
                break;
            case R.id.rightrun_dark:
                //第三个灯板的中灯
                clickCountCenter3++;
                if (clickCountCenter3 % 3 == 1) {
                    //中灯常亮
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "05"),"04FB05FA"));
                    openImage(ImageMap, rightrunDark, R.id.rightrun_dark, R.drawable.rightrun_light);
                }else if (clickCountCenter3 % 3 == 2) {
                    //闪烁
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "01"),"04FB01FE"));
                    doCenterGlintRunable(rightrunDark,R.drawable.rightrun_light,R.drawable.rightrun_dark);
                }else if (clickCountCenter3 % 3 == 0) {
                    //关闭
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "09"),"04FB09F6"));
                    closeImage(ImageMap, rightrunDark, R.id.rightrun_dark, R.drawable.rightrun_dark);
                    stopCenterGlintRunable();
                }
                break;
            case   R.id.ll_light_1:
                if(bigLightStateList.get(0).isSelect()){
                    return;
                }
                clickCountLeft2   = 0;
                clickCountCenter2 = 0;
                clickCountRight2 = 0;
                clickCountCenter3 = 0;
                resetBigLight(0);
                break;
            case   R.id.ll_light_2:
                if(bigLightStateList.get(1).isSelect()){
                    return;
                }
                clickCountLeft1   = 0;
                clickCountCenter1 = 0;
                clickCountRight1  = 0;
                clickCountRight2 = 0;
                clickCountCenter3 = 0;
                resetBigLight(1);
                break;
            case   R.id.ll_light_3:
                if(bigLightStateList.get(2).isSelect()){
                    return;
                }
                clickCountLeft1   = 0;
                clickCountCenter1 = 0;
                clickCountRight1  = 0;
                clickCountLeft2   = 0;
                clickCountCenter2 = 0;
                resetBigLight(2);
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
            llLight3.setIntercept(true);
            // 改变第一个灯板的图片
            llLight1.setBackgroundColor(ResourceUtil.getColor(R.color.blue_shadow));
            llLight2.setBackgroundColor(ResourceUtil.getColor(R.color.white));
            llLight3.setBackgroundColor(ResourceUtil.getColor(R.color.white));
            break;
        case 1:
            llLight1.setIntercept(true);
            llLight2.setIntercept(false);
            llLight3.setIntercept(true);
            // 改变第二个灯板的图片
            llLight1.setBackgroundColor(ResourceUtil.getColor(R.color.white));
            llLight2.setBackgroundColor(ResourceUtil.getColor(R.color.blue_shadow));
            llLight3.setBackgroundColor(ResourceUtil.getColor(R.color.white));
            break;
        case 2:
            llLight1.setIntercept(true);
            llLight2.setIntercept(true);
            llLight3.setIntercept(false);
            // 改变第三个灯板的图片
            llLight1.setBackgroundColor(ResourceUtil.getColor(R.color.white));
            llLight2.setBackgroundColor(ResourceUtil.getColor(R.color.white));
            llLight3.setBackgroundColor(ResourceUtil.getColor(R.color.blue_shadow));
            break;
        default:
    }
}
    public void closeAllImage() {
        closeImage(ImageMap, leftDark, R.id.left_dark, R.drawable.left_dark);
        closeImage(ImageMap, leftrunDark, R.id.leftrun_dark, R.drawable.leftrun_dark);
        closeImage(ImageMap, rightDark, R.id.right_dark, R.drawable.right_dark);
        closeImage(ImageMap, leftDark2, R.id.left_dark2, R.drawable.left_dark);
        closeImage(ImageMap, leftrunDark2, R.id.leftrun_dark2, R.drawable.leftrun_dark);
        closeImage(ImageMap, rightrunDark, R.id.rightrun_dark, R.drawable.rightrun_dark);
        closeImage(ImageMap, rightDark3, R.id.right_dark3, R.drawable.right_dark);
        stopLeftGlintRunable();
        stopCenterGlintRunable();
        stopRightGlintRunable();
    }


    private Handler handler = new Handler() {

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

    private void doLeftGlintRunable(final ImageView leftView) {

        leftGlientRunable = new Runnable() {
            @Override
            public void run() {
                if (leftGlientCount % 2 == 0) {
                    leftView.setImageDrawable(ResourceUtil.getDrawable(R.drawable.left_light));
                } else {
                    leftView.setImageDrawable(ResourceUtil.getDrawable(R.drawable.left_dark));
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

    private void doRightGlintRunable(final ImageView rightView) {
        rightGlientRunable = new Runnable() {
            @Override
            public void run() {
                if (rightGlientCount % 2 == 0) {
                    rightView.setImageDrawable(ResourceUtil.getDrawable(R.drawable.right_light));
                } else {
                    rightView.setImageDrawable(ResourceUtil.getDrawable(R.drawable.right_dark));
                    rightView.setImageDrawable(ResourceUtil.getDrawable(R.drawable.right_dark));
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
    private int centerGlientCount = 0;

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
