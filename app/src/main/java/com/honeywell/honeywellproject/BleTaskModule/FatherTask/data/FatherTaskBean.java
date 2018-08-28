package com.honeywell.honeywellproject.BleTaskModule.FatherTask.data;

import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据返回的基类
 *
 */



public class FatherTaskBean extends DataSupport {

    private int     id;
    private boolean isselect=false;
    public boolean isexpand=false;         //是否展开,默认展开  public 表示不映射此字段
    private int     tasknumber;
    private String        progressnpercent;     //子任务进度
    private String        logininfo_id;         //登录id
    private String        project_id;           //项目id
    private String        controller_id;        //控制器id
    private String        taskdate;             //任务建立日期
    private String        taskname;             //任务名称
    private String        taskaddress;          //任务地址
    private int sontaskamount;
    private String fatherdescription;
    private List<SonTaskBean> sontasklist =new ArrayList<SonTaskBean>();


    public List<SonTaskBean> getSontasklist(){
    return DataSupport.where("fathertask_id = ? and logininfo_id= ?", String.valueOf(getId()),String.valueOf(getLogininfo_id())).find(SonTaskBean.class);
    }
    public String getLogininfo_id() {
        return logininfo_id;
    }

    public void setLogininfo_id(String logininfo_id) {
        this.logininfo_id = logininfo_id;
    }

    public String getController_id() {
        return controller_id;
    }

    public void setController_id(String controller_id) {
        this.controller_id = controller_id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public boolean isexpand() {
        return isexpand;
    }

    public void setIsexpand(boolean isexpand) {
        this.isexpand = isexpand;
    }

    public String getProgressnpercent() {
        return progressnpercent;
    }

    public void setProgressnpercent(String progressnpercent) {
        this.progressnpercent = progressnpercent;
    }
    public void setSontasklist(List<SonTaskBean> sontasklist) {
        this.sontasklist = sontasklist;
    }

    public int getSontaskamount() {
        return sontaskamount;
    }

    public void setSontaskamount(int sontaskamount) {
        this.sontaskamount = sontaskamount;
    }


    public String getTaskdate() {
        return taskdate;
    }

    public void setTaskdate(String taskdate) {
        this.taskdate = taskdate;
    }

    public int getTasknumber() {
        return tasknumber;
    }

    public void setTasknumber(int tasknumber) {
        this.tasknumber = tasknumber;
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


    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getTaskaddress() {
        return taskaddress;
    }

    public void setTaskaddress(String taskaddress) {
        this.taskaddress = taskaddress;
    }

    public String getFatherdescription() {
        return fatherdescription;
    }

    public void setFatherdescription(String fatherdescription) {
        this.fatherdescription = fatherdescription;
    }
}
