package com.honeywell.honeywellproject.LoginModule.data;

import org.litepal.crud.DataSupport;

/**
 * Created by QHT on 2017-10-17.
 */
public class LoginInfoBeanEL extends DataSupport{

    private  int id;
    private int usertype;       //用户类型
    private  String username;   //用户名
    private String password;    //密码
    private String phone;    //手机号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
