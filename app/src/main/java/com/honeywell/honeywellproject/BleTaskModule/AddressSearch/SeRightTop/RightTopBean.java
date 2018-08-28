package com.honeywell.honeywellproject.BleTaskModule.AddressSearch.SeRightTop;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by QHT on 2018-01-20.
 */

public class RightTopBean implements MultiItemEntity {
    public static final int protocol= 1;
    // public static final int audio = 2;
    public static final int searchdegree = 2;

    private int itemType;

    public RightTopBean() {

        }

    public RightTopBean(int itemType) {
        this.itemType = itemType;
    }



    @Override
    public int getItemType() {
        return itemType;
    }
}
