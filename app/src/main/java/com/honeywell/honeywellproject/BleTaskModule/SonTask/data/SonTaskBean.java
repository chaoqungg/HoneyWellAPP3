package com.honeywell.honeywellproject.BleTaskModule.SonTask.data;

import org.litepal.crud.DataSupport;

/**
 * 数据返回的基类
 *
 */



public class SonTaskBean extends DataSupport {

        private int     id;                     //自增主键
        private boolean isselect=false;
        public boolean isexpand=false;         //是否展开,默认展开  public 表示不映射此字段
        private int     tasknumber;             //任务编号
        private String  taskdate="";               //任务创建日期
        private String  taskaddress="";            //任务地址
        private int     taskdigitaladdress;     //数字地址
        private String  taskserialnumber="";       //序列号
        private String  taskloopnumber="";         //回路地址
        private boolean process =false;         //地址下载进度
        private String  logininfo_id;
        private String project_id;
        private String controller_id;
        private String fathertask_id;

    public String getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    private String devicetype     ="";
    private String addressingtype ="";  //编址类型，CLIP\DLIP\FlashScan
    private String sondescription ="";

        public String getAddressingtype() {
        return addressingtype;
    }

    public void setAddressingtype(String addressingtype) {
        this.addressingtype = addressingtype;
    }

    public String getFathertask_id() {
        return fathertask_id;
    }

    public void setFathertask_id(String fathertask_id) {
        this.fathertask_id = fathertask_id;
    }

    public String getLogininfo_id() {
        return logininfo_id;
    }

    public void setLogininfo_id(String logininfo_id) {
        this.logininfo_id = logininfo_id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getController_id() {
        return controller_id;
    }

    public void setController_id(String controller_id) {
        this.controller_id = controller_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isselect() {
        return isselect;
    }

    public void setIsselect(boolean isselect) {
        this.isselect = isselect;
    }

    public boolean isexpand() {
        return isexpand;
    }

    public void setIsexpand(boolean isexpand) {
        this.isexpand = isexpand;
    }

    public int getTasknumber() {
        return tasknumber;
    }

    public void setTasknumber(int tasknumber) {
        this.tasknumber = tasknumber;
    }

    public String getTaskdate() {
        return taskdate;
    }

    public void setTaskdate(String taskdate) {
        this.taskdate = taskdate;
    }


    public String getTaskaddress() {
        return taskaddress;
    }

    public void setTaskaddress(String taskaddress) {
        this.taskaddress = taskaddress;
    }

    public int getTaskdigitaladdress() {
        return taskdigitaladdress;
    }

    public void setTaskdigitaladdress(int taskdigitaladdress) {
        this.taskdigitaladdress = taskdigitaladdress;
    }

    public String getTaskserialnumber() {
        return taskserialnumber;
    }

    public void setTaskserialnumber(String taskserialnumber) {
        this.taskserialnumber = taskserialnumber;
    }

    public String getTaskloopnumber() {
        return taskloopnumber;
    }

    public void setTaskloopnumber(String taskloopnumber) {
        this.taskloopnumber = taskloopnumber;
    }

    public boolean isProcess() {
        return process;
    }

    public void setProcess(boolean process) {
        this.process = process;
    }

    public String getSondescription() {
        return sondescription;
    }

    public void setSondescription(String sondescription) {
        this.sondescription = sondescription;
    }
}
