package com.honeywell.honeywellproject.BleTaskModule.SingleAddress.SingleTaskAddressing.data;

/**
 * Created by QHT on 2018-01-08.
 */
public class AddrBean {
    private String addr;
    private boolean isUsed;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
