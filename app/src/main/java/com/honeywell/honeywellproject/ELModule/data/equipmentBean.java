package com.honeywell.honeywellproject.ELModule.data;

/**
 * Created by QHT on 2018-03-08.
 */
public class equipmentBean {


    private int lamptypeID;
    private int moduleID;
    private String moduleName;

    public equipmentBean(int lamptypeID,int moduleID, String moduleName ) {
        this.moduleID = moduleID;
        this.moduleName = moduleName;
        this.lamptypeID = lamptypeID;
    }

    public int getLamptypeID() {
        return lamptypeID;
    }

    public void setLamptypeID(int lamptypeID) {
        this.lamptypeID = lamptypeID;
    }

    public int getModuleID() {
        return moduleID;
    }

    public void setModuleID(int moduleID) {
        this.moduleID = moduleID;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
