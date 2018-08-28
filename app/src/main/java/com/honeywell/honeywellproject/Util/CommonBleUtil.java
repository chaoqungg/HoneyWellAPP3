package com.honeywell.honeywellproject.Util;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.util.UUID;

/**
 * @author QHT
 */
public class CommonBleUtil {

    OnResultListtener resultListener;
    public interface OnResultListtener{
        /**
         * 写蓝牙的回调，原接口的两个方法
         * @param result
         * */
        void writeOnResult(boolean result);
        /**
         * notify蓝牙回调
         * @param result
         * */
        void notifyOnResult(boolean result);
        /**
         * notify蓝牙成功回调
         * @param values
         * */
        void notifyOnSuccess(String values,String UUID);
    }
    public void setResultListener(OnResultListtener resultListener){
        this.resultListener=resultListener;
    }

    public  void  writeDevice(BleDevice bleDevice,String datas){
        BleManager.getInstance().write(bleDevice,ConstantUtil.SERVER_UUID, ConstantUtil.Write_UUID, HexUtil.hexStringToBytes(datas), new BleWriteCallback() {
            @Override
            public void onWriteSuccess() {
                if(resultListener!=null){
                    resultListener.writeOnResult(true);
                }
            }

            @Override
            public void onWriteFailure(BleException exception) {
                if(resultListener!=null){
                    resultListener.writeOnResult(false);
                }
            }
        });
    }
    /**
     * 响应Notify
     * */
    public void notifyDevice(BleDevice bleDevice) {
        final String finalNotifyUUid = BleManager.getInstance().getBluetoothGatt(bleDevice).getService(UUID.fromString(ConstantUtil.SERVER_UUID)).getCharacteristics().get(0).getUuid().toString();
        BleManager.getInstance().notify(bleDevice,ConstantUtil.SERVER_UUID, finalNotifyUUid, new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {
            }

            @Override
            public void onNotifyFailure(BleException exception) {
                ConstantUtil.NOTIFYSUCCESS=false;
            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                if(resultListener!=null) {
                    resultListener.notifyOnSuccess(HexUtil.encodeHexStr(data), finalNotifyUUid);
                }
                ConstantUtil.NOTIFYSUCCESS=true;
            }
        });
    }
}
