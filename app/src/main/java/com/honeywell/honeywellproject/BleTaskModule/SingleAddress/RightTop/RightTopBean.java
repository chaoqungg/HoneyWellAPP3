package com.honeywell.honeywellproject.BleTaskModule.SingleAddress.RightTop;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by QHT on 2018-01-20.
 */

public class RightTopBean implements MultiItemEntity {
    public static final int history= 1;
    public static final int audio = 2;
    public static final int degree = 3;

    private int itemType;

    public RightTopBean(int itemType) {
        this.itemType = itemType;
    }

    public RightTopBean() {

    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
