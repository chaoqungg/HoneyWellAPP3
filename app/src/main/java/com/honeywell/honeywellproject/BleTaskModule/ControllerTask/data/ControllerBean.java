package com.honeywell.honeywellproject.BleTaskModule.ControllerTask.data;

import com.honeywell.honeywellproject.BleTaskModule.FatherTask.data.FatherTaskBean;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QHT on 2017-12-26.
 */
public class ControllerBean extends DataSupport {

    private int     id;
    private boolean isselect=false;
    public boolean isexpand=false;         //是否展开,默认展开  public 表示不映射此字段
    private int    tasknumber;             //任务编号
    private String logininfo_id;
    private String project_id;
    private String controllername;
    private String controllerdescription;
    private String controllerdata;
    private int fathertaskamount;
    private List<FatherTaskBean> fathertasklist =new ArrayList<FatherTaskBean>();

    public void setFathertasklist(List<FatherTaskBean> fathertasklist) {
        this.fathertasklist = fathertasklist;
    }

    public List<FatherTaskBean> getFathertasklist(){
        return DataSupport.where("controller_id = ? and logininfo_id= ?", String.valueOf(getId()),getLogininfo_id()).find(FatherTaskBean.class);
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

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public int getFathertaskamount() {
        return fathertaskamount;
    }

    public void setFathertaskamount(int fathertaskamount) {
        this.fathertaskamount = fathertaskamount;
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

    public String getLogininfo_id() {
        return logininfo_id;
    }

    public void setLogininfo_id(String logininfo_id) {
        this.logininfo_id = logininfo_id;
    }

    public String getControllername() {
        return controllername;
    }

    public void setControllername(String controllername) {
        this.controllername = controllername;
    }

    public String getControllerdescription() {
        return controllerdescription;
    }

    public void setControllerdescription(String controllerdescription) {
        this.controllerdescription = controllerdescription;
    }

    public String getControllerdata() {
        return controllerdata;
    }

    public void setControllerdata(String controllerdata) {
        this.controllerdata = controllerdata;
    }
}
