package com.honeywell.honeywellproject.BleTaskModule.ProjectTask.data;

import com.honeywell.honeywellproject.BleTaskModule.ControllerTask.data.ControllerBean;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;





public class ProjectBean extends DataSupport {

    private int     id;
    private boolean isselect=false;        //是否选择
    public boolean isexpand=false;         //是否展开,默认第一条展开  public 表示不映射此字段
    private int    tasknumber;             //任务编号
    private String logininfo_id;
    private String projectname;
    private String projectdescription;
    private String projectdata;
    private int controlleramount;
    private List<ControllerBean> controllerlist =new ArrayList<ControllerBean>();


    public List<ControllerBean> getControllerlist() {
        return DataSupport.where("project_id = ? and logininfo_id= ?", String.valueOf(getId()),getLogininfo_id()).find(ControllerBean.class);
    }

    public void setControllerlist(List<ControllerBean> controllerlist) {
        this.controllerlist = controllerlist;
    }

    public boolean isexpand() {
        return isexpand;
    }

    public void setIsexpand(boolean isexpand) {
        this.isexpand = isexpand;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getTasknumber() {
        return tasknumber;
    }

    public void setTasknumber(int tasknumber) {
        this.tasknumber = tasknumber;
    }


    public String getLogininfo_id() {
        return logininfo_id;
    }

    public void setLogininfo_id(String logininfo_id) {
        this.logininfo_id = logininfo_id;
    }
    public boolean isselect() {
        return isselect;
    }

    public void setIsselect(boolean isselect) {
        this.isselect = isselect;
    }
    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getProjectdescription() {
        return projectdescription;
    }

    public void setProjectdescription(String projectdescription) {
        this.projectdescription = projectdescription;
    }

    public String getProjectdata() {
        return projectdata;
    }

    public void setProjectdata(String projectdata) {
        this.projectdata = projectdata;
    }

    public int getControlleramount() {
        return controlleramount;
    }

    public void setControlleramount(int controlleramount) {
        this.controlleramount = controlleramount;
    }
}
