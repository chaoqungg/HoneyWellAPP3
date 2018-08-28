package com.honeywell.honeywellproject.BleTaskModule.AddressDownLoad.RightTop;

/**
 * Created by QHT on 2018-01-20.
 */

public class RightTopBean  {
    private String name;
    private boolean select;
 public RightTopBean(String name,boolean select){
     this.name=name;
     this.select=select;
 }

    public boolean select() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
