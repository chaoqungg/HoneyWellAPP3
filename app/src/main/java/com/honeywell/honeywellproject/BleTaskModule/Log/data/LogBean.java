package com.honeywell.honeywellproject.BleTaskModule.Log.data;

import org.litepal.crud.DataSupport;

/**
 * Created by QHT on 2018-01-10.
 */
public class LogBean extends DataSupport {

    private int     id;
    private String user;
    private String data;
    private String protocoltype;
    private String operatetype;
    private String address;
    private String result;
    private String detail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getProtocoltype() {
        return protocoltype;
    }

    public void setProtocoltype(String protocoltype) {
        this.protocoltype = protocoltype;
    }

    public String getOperatetype() {
        return operatetype;
    }

    public void setOperatetype(String operatetype) {
        this.operatetype = operatetype;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
