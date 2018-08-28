package com.honeywell.honeywellproject.BleTaskModule.AddressDownLoad.RightTop;

import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by QHT on 2018-03-02.
 */
public class AddressDownLoadComparator {

    // 按编号（默认）排序
    public static void SortAsNumber(List<SonTaskBean> list) {
        Collections.sort(list, new Comparator<SonTaskBean>() {
            @Override
            public int compare(SonTaskBean o1, SonTaskBean o2) {
                try {
                    if (o1.getTasknumber()>o2.getTasknumber()) {
                        return 1;
                    } else if (o1.getTasknumber()<o2.getTasknumber()) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
    // 按地址排序
    public static void SortAsAddres(List<SonTaskBean> list) {
        Collections.sort(list, new Comparator<SonTaskBean>() {
            @Override
            public int compare(SonTaskBean o1, SonTaskBean o2) {
                try {
                    if (o1.getTaskdigitaladdress()>o2.getTaskdigitaladdress()) {
                        return 1;
                    } else if (o1.getTaskdigitaladdress()<o2.getTaskdigitaladdress()) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
    // 按状态排序
    public static void SortAsCompleState(List<SonTaskBean> list) {
        Collections.sort(list, new Comparator<SonTaskBean>() {
            @Override
            public int compare(SonTaskBean o1, SonTaskBean o2) {
                try {
                    int i1=0,i2=0;
                    if (o1.isProcess()) {
                        i1=1;
                    }
                    if (o2.isProcess()) {
                        i2=1;
                    }
                    if (i1>i2) {
                        return 1;
                    } else if (i1<i2) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}
