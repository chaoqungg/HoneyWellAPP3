package com.honeywell.honeywellproject.ELModule.data;

/**
 * Created by QHT on 2018-03-07.
 */
public class bigLightState {

    boolean isSelect;
    public bigLightState(boolean isSelect){
        this.isSelect=isSelect;
    }
    public boolean isSelect() {
        return isSelect;
    }
    public void setSelect(boolean select) {
        isSelect = select;
    }
}
