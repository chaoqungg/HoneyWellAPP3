package com.honeywell.honeywellproject.Util;

import android.support.annotation.NonNull;

import com.honeywell.honeywellproject.ELModule.data.equipmentBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QHT on 2018-03-08.
 */
public class BuildModuleUtil {
    public static List<List<equipmentBean>> buildModuleBean() {
        List<List<equipmentBean>> listroot=new ArrayList<List<equipmentBean>>();
        List<equipmentBean> list1=new ArrayList<equipmentBean>();
        equipmentBean equipment11=new equipmentBean(30,1,"单面双向指示灯");
        equipmentBean equipment12=new equipmentBean(30,7,"单面双向指示灯（IP65）");
        equipmentBean equipment13=new equipmentBean(31,1,"双面双向指示灯");
        list1.add(equipment11);
        list1.add(equipment12);
        list1.add(equipment13);
        listroot.add(list1);

        List<equipmentBean> list2=new ArrayList<equipmentBean>();
        equipmentBean equipment21=new equipmentBean(30,2,"单面左向指示灯");
        equipmentBean equipment22=new equipmentBean(31,2,"双面单向指示灯");
        list2.add(equipment21);
        list2.add(equipment22);
        listroot.add(list2);

        List<equipmentBean> list3=new ArrayList<equipmentBean>();
        equipmentBean equipment31=new equipmentBean(30,3,"单面右向指示灯");
        list3.add(equipment31);
        listroot.add(list3);

        List<equipmentBean> list4=new ArrayList<equipmentBean>();
        equipmentBean equipment41=new equipmentBean(30,4,"单面楼层指示灯");
        list4.add(equipment41);
        listroot.add(list4);

        List<equipmentBean> list5=new ArrayList<equipmentBean>();
        equipmentBean equipment51=new equipmentBean(30,5,"单面出口指示灯");
        equipmentBean equipment52=new equipmentBean(30,6,"单面出口指示灯语音型");
        equipmentBean equipment53=new equipmentBean(30,8,"单面出口指示灯（IP65）");
        equipmentBean equipment54=new equipmentBean(31,3,"双面出口指示灯");
        list5.add(equipment51);
        list5.add(equipment52);
        list5.add(equipment53);
        list5.add(equipment54);
        listroot.add(list5);

        List<equipmentBean> list6=new ArrayList<equipmentBean>();
        equipmentBean equipment61=new equipmentBean(33,1,"吊装式3W照明灯");
        equipmentBean equipment62=new equipmentBean(33,2,"吊装式5W照明灯");
        list6.add(equipment61);
        list6.add(equipment62);
        listroot.add(list6);

        List<equipmentBean> list7=new ArrayList<equipmentBean>();
        equipmentBean equipment71=new equipmentBean(33,3,"壁挂式3W照明灯");
        equipmentBean equipment72=new equipmentBean(33,4,"壁挂式5W照明灯");
        list7.add(equipment71);
        list7.add(equipment72);
        listroot.add(list7);


        List<equipmentBean> list8=new ArrayList<equipmentBean>();
        equipmentBean equipment81=new equipmentBean(32,1,"地埋式单向照明灯");
        list8.add(equipment81);
        listroot.add(list8);

        List<equipmentBean> list9=new ArrayList<equipmentBean>();
        equipmentBean equipment91=new equipmentBean(32,2,"地埋式双向指示灯");
        list9.add(equipment91);
        listroot.add(list9);

        return listroot;
    }

    public static String qr2DeviceType(@NonNull String qrtype){
        int type=Integer.parseInt(qrtype);
        String DeviceType = null;
        if(type<=15 && type>=1){

        switch (type){
            case 1:
                DeviceType="感烟探测器";
            break;
            case 2:
                DeviceType="定温探测器";
            break;
            case 3:
                DeviceType="差定温探测器（FT with ROR,A2R）";
            break;
            case 4:
                DeviceType="烟温复合探测器";
            break;
            case 5:
                DeviceType="输入模块";
            break;
            case 6:
                DeviceType="手动报警按钮";
            break;
            case 7:
                DeviceType="消火栓按钮";
            break;
            case 8:
                DeviceType="接口模块（中继模块）";
            break;
            case 9:
                DeviceType="输出模块";
            break;
            case 10:
                DeviceType="一入一出模块";
            break;
            case 11:
                DeviceType="声警报器";
            break;
            case 12:
                DeviceType="光警报器";
            break;
            case 13:
                DeviceType="声光警报器";
            break;
            case 14:
                DeviceType="模块箱状态指示器";
            break;
            case 15:
                DeviceType="继电器模块（干接点输出）";
            break;
            default:
        }
        }else{
            DeviceType="RESERVED";
        }
        return DeviceType;
    }
}
