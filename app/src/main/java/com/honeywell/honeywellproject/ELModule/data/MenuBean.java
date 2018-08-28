package com.honeywell.honeywellproject.ELModule.data;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by QHT on 2018-01-20.
 */

public class MenuBean implements MultiItemEntity {
    public static final int swichButtonitem = 1;
    public static final int normalitem = 2;

    private int itemType;

    public MenuBean(int itemType,String name,int image ) {
        this.itemType = itemType;
        this.name = name;
        this.image = image;
    }

    public MenuBean(int itemType,String name,String drive,int image ) {
        this.itemType = itemType;
        this.name = name;
        this.drive = drive;
        this.image = image;
    }

    public MenuBean(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    private boolean open;
    private String name;
    private int    image;

    public String getDrive() {
        return drive;
    }

    public void setDrive(String drive) {
        this.drive = drive;
    }

    private String drive;
}
