package com.honeywell.honeywellproject.Util;

import android.os.Build;

import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * 蓝牙空中包最大为20字节。模块允许你发255是会分成若干个20字节往上传的。所以手机端会接收到若干20字节的包
 * 操他妈真恶心，死协议真麻烦，垃圾公司，垃圾协议！
 */
public class DataHandler {

    /**
     *发送数据的序列号占用4个字节，总共32位
     */
    private static final int WRITESERIESLENGTH=32;
    /**
     * DLIP的协议中的ADDR_NEW，因为DLIP每次写完都要和读到的地址进行比较，一样证明写入成功，所以需要暂时保存一下Write的地址。
     */
    public static  String DLIP_ADDR_NEW="";
    /**
     * 单独读址标志，区分单独编址的时候的CLIP读，读址成功或者结束记得置为false
     */
    public    static    boolean      singleReading            = false;

    /**@param  sonBean
     * @return String
     * 分割要写入蓝牙的数据
     * DLCP序列号命令
     * */
    public static String DLIPSeries(SonTaskBean sonBean) {
        String S3s = null,S2s= null,S1s= null,S0s= null;
        try{
            if( Long.parseLong(sonBean.getTaskserialnumber())<4294967295L &&
                    Long.parseLong(sonBean.getTaskserialnumber())>2147483647L) {
            long seriesNumL = Long.parseLong(sonBean.getTaskserialnumber());
                long s3=(seriesNumL>>24)  & 0XFF;
                long s2=(seriesNumL>>16)  & 0XFF;
                long s1=(seriesNumL>>8)   & 0XFF;
                long s0=(seriesNumL)      & 0XFF;
                S3s=Alone2Hex(Long.toHexString(s3));
                S2s=Alone2Hex(Long.toHexString(s2));
                S1s=Alone2Hex(Long.toHexString(s1));
                S0s=Alone2Hex(Long.toHexString(s0));
            }else{
                int seriesNum =Integer.parseInt(sonBean.getTaskserialnumber());
                int s3=(seriesNum>>24)  & 0XFF;
                int s2=(seriesNum>>16)  & 0XFF;
                int s1=(seriesNum>>8)   & 0XFF;
                int s0=(seriesNum)      & 0XFF;
                S3s=Alone2Hex(Integer.toHexString(s3));
                S2s=Alone2Hex(Integer.toHexString(s2));
                S1s=Alone2Hex(Integer.toHexString(s1));
                S0s=Alone2Hex(Integer.toHexString(s0));
            }

        }catch (NumberFormatException e){
            LogUtil.e(e.getMessage());
            return null;
        }

        String[] bytes1=new String[]{"55","AA","01","85","F9","F4",
                //S3最高字节,S0最低字节
                S3s,
                S2s,
                S1s,
                S0s,
                //单元地址UNIT_ADDRESS
                Alone2Hex(Integer.toHexString(sonBean.getTaskdigitaladdress())),
                //VB 校验位，异或
                "00",
                "55","5A"};

        bytes1[11]=CheckVB(2,10,bytes1);

        StringBuffer sb = new StringBuffer();
        for(int k = 0; k < bytes1.length; k++){
            sb. append(bytes1[k]);
            //再判断里面是否含有0x55，若有需要后面补00
            if(k>=2 && k<=11){
                if("55".equals(bytes1[k])){
                    sb. append("00");
                }
            }
        }
        return sb.toString();
    }

    /**
     * CLIP 写地址
     * CLIP地址范围：1-199
     * 1.连发3条命令,CLIP编址范围1～199;
     * 2.地址为（0～99）时，Y=0,ADDR=实际地址；
     * 3.地址为（100～199，地址范围不超过199）时，Y=1,ADDR=实际地址-100 or 实际地址%100；
     *
     * 12-07 新改动
     * CLIP写地址（三合一）:
     * 55 AA 01 80 FF 63 05 18 55 5A  //63表示99
     * CLIP轮询读地址 需要超时10S
     * 55 AA 01 80 FE 00 05 7A 55 5A
     *
     * @param index 代表第几条命令，前两条为死数据，后两条带地址
     * @return 一条完整命令，String拼接16进制
     * */
    public static String CLIP_WRITE(String addr,int index,boolean isSingleReading){
        String Y = null;
        String addr2 = null;
        String[] strs;
        if(index==1){
            //编址命令
            addr2=Alone2Hex(Integer.toHexString(parseInt(addr)));
            strs=new String[]{"55","AA","01","80","FF",addr2,"05","00","55","5A"};
        }else{
            //2 和 3相等的发送命令
            if(isSingleReading){
                strs=new String[]{"55","AA","01","80","FE","00","05","7A","55","5A"};
            }else{
                // 如果不是单独读，而是对写的验证，则使用老协议，不用轮询，更快点
                // 55 AA 01 80 00 02 05 86 55 5A
                if(parseInt(addr)>=1 && parseInt(addr)<=99){
                addr2=Alone2Hex(Integer.toHexString(parseInt(addr)));
                Y="00";
                    }else if(parseInt(addr)>=100 && parseInt(addr)<=199){
                addr2=Alone2Hex(Integer.toHexString(parseInt(addr)%100));
                Y="01";
            }else{
                    return null;
                }
            strs=new String[]{"55","AA","01","80",Y,addr2,"05","00","55","5A"};
            }

        }
        //检验位
        strs[7]=CheckVB(2,6,strs);
        StringBuffer sb = new StringBuffer();
        for(int k = 0; k < strs.length; k++){
            sb. append(strs[k]);
            //再判断里面是否含有0x55，若有需要后面补00
            if(k>=2 && k<=7){
                if("55".equals(strs[k])){
                    sb. append("00");
                }
            }
        }
        return sb.toString();
    }

    /**
     * CLIP 连续编址的读
     * */
    public static boolean CLIP_READLOOP(String values,int index){


        String[] datas=new String[values.length()/2];
        //数据长度不固定，注意处理0x55的情况
        for(int i=0,j=0;i<values.length();i+=2,j++){
            datas[j]=values.substring(i,i+2);
            if(j!=0 && "00".equals(datas[j]) && "55".equals(datas[j-1])){
                //出现0x55 0x00的情况则干掉00保留55,这样能保证数据长度不变，便于取值
                j--;
            }
        }
        //故障位
        String malfunction=datas[4];
        //ADDR
        if(index==1){
            String ADDR=datas[5];
            if("00".equals(malfunction)){
                DataHandler.DLIP_ADDR_NEW=ADDR;
                return true;
            }
        }else if(index==2){
            String pW1 = datas[5];
            String pW2 = datas[6];
            //暂时修改一下，5、6位有一个不为00就行
            int H = Integer.parseInt(pW1, 16);
            int L = Integer.parseInt(pW2, 16);
            if ((H * 256 + L) > 100 && "00".equals(malfunction)) {
                return true;
            }
            return false;
        }else {
            //index==3
            if ("00".equals(malfunction)) {
                //失败，设备已经移除，提示地址累加
                String pW1 = datas[5];
                String pW2 = datas[6];
                //暂时修改一下，5、6位有一个不为00就行
                int H = Integer.parseInt(pW1, 16);
                int L = Integer.parseInt(pW2, 16);
                if ((H * 256 + L) > 100) {
                    //设备还未更换
                    return false;
                }else{
                    //无此地址，设备已经更换,因为CLIP不能直接读到地址，所以要用PW1判断
                    return true;
                }
            }else {
                //设备已经移除
                return true;
            }
        }
        return false;
    }

    /**
     * CLIP 读地址
     *
     * 55 AA 01 80 00 63 E2 55 5A     //表示写99成功 63代表99
     *
     * @param index 代表第几条命令，前两条为死数据，后两条带地址,主要是解码规则稍有不同
     * */
    public static boolean CLIP_READ(String values,int index){


        String[] datas=new String[values.length()/2];
        //数据长度不固定，注意处理0x55的情况
        for(int i=0,j=0;i<values.length();i+=2,j++){
            datas[j]=values.substring(i,i+2);
            if(j!=0 && "00".equals(datas[j]) && "55".equals(datas[j-1])){
                //出现0x55 0x00的情况则干掉00保留55,这样能保证数据长度不变，便于取值
                    j--;
            }
        }
        //故障位
        String malfunction=datas[4];
        if(!"00".equals(malfunction)){
            return false;
        }
        //ADDR
        String ADDR=datas[5];
        if(index==1){
            if("00".equals(malfunction)){
                DataHandler.DLIP_ADDR_NEW=ADDR;
                return true;
            }
        }else if(index==2){
            //回复:
            //55 AA 01 80 00 10 91 55 5A  //10表示地址16
            //55 AA 01 80 00 63 E2 55 5A  //63表示地址99
            if(singleReading){
                //单独读址的话如果读到了地址直接显示即可
                if("00".equals(malfunction)){
                    DataHandler.DLIP_ADDR_NEW=ADDR;
                    return true;
                }
            }else{
                String pW1=datas[5];
                String pW2=datas[6];
                //暂时修改一下，5、6位有一个不为00就行
                int H=Integer.parseInt(pW1,16);
                int L=Integer.parseInt(pW2,16);
                if((H *256 + L)>100  && "00".equals(malfunction)){
                    //从机在发完3条命令之后只回复一条指令,一个PW为2个字节，如xxxx表示两个字节；如果故障标志为0，表示写入成功，其余为写入错误；
                    return true;
                }
//                //不是单独读址的话需要验证写入的地址和读到的地址是否一致
//                if("00".equals(malfunction) && DataHandler.DLIP_ADDR_NEW.equals(ADDR)){
//                    DataHandler.DLIP_ADDR_NEW=ADDR;
//                    return true;
//                }
            }
        }
        return false;
    }

    /**
     * DLIPEEPROM 写地址
     * 'ADDR_OLD表示老地址;地址范围为1～239，如果不知道地址，可以用地址0，表示广播地址；
     * 'ADDR_NEW表示新地址； 十进制
     * 'DLIP EEPROM写地址02（广播）
     * 55 AA 01 85 00 09 30 02 BF 55 5A
     * DLIP EEPROM写地址02（非广播）
     * 55 AA 01 85 02 09 30 02 BD 55 5A
     * ADDR_OLD写死，0x00，但是为了以后的兼容性，留出接口
     * */
    public static String DLIP_WRITE(String addrOld,String addrNew,int index){
        String addr2 ,addr3;
        String[] strs;
        if(!Integer.toHexString(parseInt(addrNew)).equals("ff") &&
           !Integer.toHexString(parseInt(addrNew)).equals("fe") &&
           Integer.parseInt(addrNew)>239){
            return null;
        }
        if(index==1){
            //第一条命令
            addr2=Alone2Hex(Integer.toHexString(parseInt(addrNew)));
            addr3=Alone2Hex(Integer.toHexString(parseInt(addrOld)));
            strs=new String[]{"55","AA","01","85",addr3,"09","30",addr2,"00","55","5A"};
        }else if(index==2){
            //第二条命令
            addr3=Alone2Hex(Integer.toHexString(parseInt(addrOld)));
            strs=new String[]{"55","AA","01","85",addr3,"01","30","FF","00","55","5A"};
        }else if(index==3){
            //第三条命令
            addr3=Alone2Hex(Integer.toHexString(parseInt(addrOld)));
            strs=new String[]{"55","AA","01","85",addr3,"01","30","FF","00","55","5A"};
        }else{
            //读取设备类型
            if(DLIP_ADDR_NEW.equals("")){
                strs=new String[]{"55","AA","01","85","00","01","00","FF","00","55","5A"};
            }else{
                strs=new String[]{"55","AA","01","85",DLIP_ADDR_NEW,"01","00","FF","00","55","5A"};
            }
        }
        //检验位
        strs[8]=CheckVB(2,7,strs);
        StringBuffer sb = new StringBuffer();
        for(int k = 0; k < strs.length; k++){
            sb. append(strs[k]);
            //再判断里面是否含有0x55，若有需要后面补00
            if(k>=2 && k<=8){
                if("55".equals(strs[k])){
                    sb. append("00");
                }
            }
        }
        return sb.toString();
    }
    /**
     *分组事件搜索
     * 第一次命令固定  groupNum==“0F”
     * 第二次填入第一次返回的DATAH和DATAL计算的组号
     * 第一次查是否有多个组，如果有多个组则肯定有多个探头，第二次查询单个组是否有多个探头
     * */
    public static String groupQuery(String groupNum){
        String[] strs;
        strs=new String[]{"55","AA","01","85","FB",Alone2Hex(groupNum),"F5","F5","00","55","5A"};
        //检验位
        strs[8]=CheckVB(2,7,strs);
        StringBuffer sb = new StringBuffer();
        for(int k = 0; k < strs.length; k++){
            sb. append(strs[k]);
            //再判断里面是否含有0x55，若有需要后面补00
            if(k>=2 && k<=8){
                if("55".equals(strs[k])){
                    sb. append("00");
                }
            }
        }
        return sb.toString();
    }
    public static int DLIP_READGroupQuery(String values){
        String[] datas=new String[values.length()/2];
        //数据长度不固定，注意处理0x55的情况
        for(int i=0,j=0;i<values.length();i+=2,j++){
            datas[j]=values.substring(i,i+2);
            if(j!=0 && "00".equals(datas[j]) && "55".equals(datas[j-1])){
                //出现0x55 0x00的情况则干掉00保留55,这样能保证数据长度不变，便于取值
                j--;
            }
        }
        //故障位
        String malfunction=datas[4];
        if(!"00".equals(malfunction)){
            return -2;
        }
        String DATAH=datas[7];
        String DATAL=datas[8];
        String BinaryH=ELUtil.hex2binary(DATAH);
        String BinaryL=ELUtil.hex2binary(DATAL);
        String BinaryALL=BinaryH+BinaryL;
        int sum=0;
        int result=0;
        for (int i = BinaryALL.length()-1; i >=0 ; i--) {
            if(BinaryALL.charAt(i)=='1'){
                sum++;
                result=15-i;
            }
        }
        if(sum>1){
            return -1;
        }else if(sum<1){
            return -3;
        }else{
            return result;
        }
    }
    /**
     * DLIPEEPROM 读地址
     * */
    public static boolean DLIP_READ(String values,int index){


        String[] datas=new String[values.length()/2];
        //数据长度不固定，注意处理0x55的情况
        for(int i=0,j=0;i<values.length();i+=2,j++){
            datas[j]=values.substring(i,i+2);
            if(j!=0 && "00".equals(datas[j]) && "55".equals(datas[j-1])){
                //出现0x55 0x00的情况则干掉00保留55,这样能保证数据长度不变，便于取值
                j--;
            }
        }
        //故障位
        String malfunction=datas[4];
        if(!"00".equals(malfunction)){
            return false;
        }
        //Addr_old
        String Addr_old=datas[5];
        //Addr_new
        String Addr_new=datas[8];
        //校验规则明天再看
        if(index==1){
            if("00".equals(malfunction) && Addr_new.equals(DLIP_ADDR_NEW)){
                return true;
            }
        }else{
            if(singleReading){
                if("00".equals(malfunction)){
                    DLIP_ADDR_NEW=Addr_new;
                    return true;
                }
            }else if("00".equals(malfunction) && Addr_new.equals(DLIP_ADDR_NEW)){
                DLIP_ADDR_NEW=Addr_new;
                return true;
            }
        }
        return false;
    }
    /**
     * DLIPEEPROM 循环读 读地址
     * */
    public static boolean DLIP_READLOOP(String values,int index){

        String[] datas=new String[values.length()/2];
        //数据长度不固定，注意处理0x55的情况
        for(int i=0,j=0;i<values.length();i+=2,j++){
            datas[j]=values.substring(i,i+2);
            if(j!=0 && "00".equals(datas[j]) && "55".equals(datas[j-1])){
                //出现0x55 0x00的情况则干掉00保留55,这样能保证数据长度不变，便于取值
                j--;
            }
        }
        //故障位
        String malfunction=datas[4];
        //Addr_old
        String Addr_old=datas[5];

        if(index==1){
            if("00".equals(malfunction) ){
                //Addr_new
                String Addr_new=datas[8];
                if(Addr_new.equals(DLIP_ADDR_NEW)){
                    //写地址成功
                    return true;
                }
            }
        }else if(index==2){
            //第二条，对写的验证
            if("00".equals(malfunction) ){
                //Addr_new
                String Addr_new=datas[8];
                if(Addr_new.equals(DLIP_ADDR_NEW)){
                    //写地址成功
                    return true;
                }
            }
        }else {
            //index==3
            if ("00".equals(malfunction)) {
                //Addr_new
                String Addr_new=datas[8];
                if (Addr_new.equals(DLIP_ADDR_NEW)) {
                    //一读，地址还一样，设备还未更换
                    return false;
                } else  {
                    //设备已经更换
                    return true;
                }
            }else {
                //设备已经移除 malfunction 为02 或者 01
                return true;
            }
        }
        return false;
    }

    /**
     * DLIP 读设备类型
     * */
    public static String DLIP_READDeviceType(String values){
        LogUtil.e(values);

        String[] datas=new String[values.length()/2];
        //数据长度不固定，注意处理0x55的情况
        for(int i=0,j=0;i<values.length();i+=2,j++){
            datas[j]=values.substring(i,i+2);
            if(j!=0 && "00".equals(datas[j]) && "55".equals(datas[j-1])){
                //出现0x55 0x00的情况则干掉00保留55,这样能保证数据长度不变，便于取值
                j--;
            }
        }
        //故障位
        String malfunction=datas[4];
        String Addr=datas[5];
        String type=datas[8];
        if(!"00".equals(malfunction)){
            return null;
        }
            if(Addr.equals(DLIP_ADDR_NEW)){
                if(1<=Integer.parseInt(type,16) && Integer.parseInt(type,16)<=4){
                    //探头
                    return "T";
                }else  if(5<=Integer.parseInt(type,16) && Integer.parseInt(type,16)<=16){
                    //模块
                    return "M";
                    //未定义
                }else{
                    return "N";
                }
            }
        return null;
    }

    /**
     * FLASHSCAN 写
     * */
    public static void FLASHSCAN_WRITE(String addrOld,String addrNew,int index){

    }
    /**
     * FLASHSCAN 读
     * */
    public static void FLASHSCAN_READ(String values,int index){

    }
    /**
     * DLIP 更新设备程序
     * */
    public static void DLIPUpdata(){
    }

    /**
     * 获取回路卡电池电压
     * */
    public static String LOOPCARDBattery_WRITE(){
        String[] strs;
            strs=new String[]{"55","AA","01","81","33","01","B2","55","5A"};
        //检验位
        strs[6]=CheckVB(2,5,strs);
        StringBuffer sb = new StringBuffer();
        for(int k = 0; k < strs.length; k++){
            sb. append(strs[k]);
        }
        return sb.toString();
    }
    /**
     * READ回路卡电池电压
     * */
    public static double LOOPCARDBattery_READ(String values) {
        String[] datas = new String[values.length() / 2];
        //数据长度不固定，注意处理0x55的情况
        for (int i = 0, j = 0; i < values.length(); i += 2, j++) {
            datas[j] = values.substring(i, i + 2);
            if (j != 0 && "00".equals(datas[j]) && "55".equals(datas[j - 1])) {
                //出现0x55 0x00的情况则干掉00保留55,这样能保证数据长度不变，便于取值
                j--;
            }
        }
        //故障位
        String malfunction = datas[4];
        if (!"00".equals(malfunction)) {
            return 0;
        }
        //高
        String BatteryHigh = datas[6];
        //低
        String BatteryLow = datas[7];
        double result = 0;
        try {
            result = Integer.parseInt(BatteryHigh, 16) + Integer.parseInt(BatteryLow, 16) * 0.01;
        } catch (NumberFormatException e) {
            result = 0.0;
            ToastUtil.showToastShort("下位机电量格式错误：" + BatteryHigh + BatteryLow);
        }
        return result;
    }

    /**
     * 校验位判断,异或
     * */
    public static String CheckVB(int start,int end, String[] values){

        String code = values[start];
        for (int i = start+1; i <=end; i++) {
                code = xor(code, values[i]);
        }
        return Alone2Hex(code);
    }

    /**
     * 16进制的字符串异或
     * */
    private static String xor(String strHex_X,String strHex_Y){
        //将x、y转成二进制形式
        String anotherBinary=Integer.toBinaryString(Integer.valueOf(strHex_X,16));
        String thisBinary=Integer.toBinaryString(Integer.valueOf(strHex_Y,16));
        String result = "";
        //判断是否为8位二进制，否则左补零
        if(anotherBinary.length() != 8){
            for (int i = anotherBinary.length(); i <8; i++) {
                anotherBinary = "0"+anotherBinary;
            }
        }
        if(thisBinary.length() != 8){
            for (int i = thisBinary.length(); i <8; i++) {
                thisBinary = "0"+thisBinary;
            }
        }
        //异或运算
        for(int i=0;i<anotherBinary.length();i++){
            //如果相同位置数相同，则补0，否则补1
            if(thisBinary.charAt(i)==anotherBinary.charAt(i)) {
                result += "0";
            } else{
                result+="1";
            }
        }
        return Integer.toHexString(parseInt(result, 2));
    }

    /**
     * 将单位数转化为两位数
     * */
    public static String  Alone2Hex(String s){
        switch (s)
        {
            case "0":
                return "00";
            case "1":
                return "01";
            case "2":
                return "02";
            case "3":
                return "03";
            case "4":
                return "04";
            case "5":
                return "05";
            case "6":
                return "06";
            case "7":
                return "07";
            case "8":
                return "08";
            case "9":
                return "09";
            case "a":
                return "0a";
            case "b":
                return "0b";
            case "c":
                return "0c";
            case "d":
                return "0d";
            case "e":
                return "0e";
            case "f":
                return "0f";
            default:
        }
        return s;
    }

    /**
     * 解码序列号写入操作
     * 55	AA	01	85	0/1/2	F4	01020304	'ADDR	CC	55	5A

     * */
    public static boolean Series_READ(String values){
        LogUtil.e("接收：----->"+values);
        String[] datas=new String[values.length()/2];
        //数据长度不固定，注意处理0x55的情况
        for(int i=0,j=0;i<values.length();i+=2,j++){
            datas[j]=values.substring(i,i+2);
            if(j!=0 && "00".equals(datas[j]) && "55".equals(datas[j-1])){
                //出现0x55 0x00的情况则干掉00保留55,这样能保证数据长度不变，便于取值
                j--;
            }
        }
        //故障位
        String malfunction=datas[4];
        if("00".equals(malfunction)){
            return true;
        }
        return false;
    }

    /**
     * 三星手机命令转码，实际为十六进制转10进制
     * */
    public static String hex2dec(String irData) {
        List<String> list = new ArrayList<String>(Arrays.asList(irData
                .split(" ")));
        list.remove(0); // dummy
        int frequency = parseInt(list.remove(0), 16); // frequency
        list.remove(0); // seq1
        list.remove(0); // seq2
        for (int i = 0; i < list.size(); i++) {
            list.set(i, Integer.toString(parseInt(list.get(i), 16)));
        }
        frequency = (int) (1000000 / (frequency * 0.241246));
        list.add(0, Integer.toString(frequency));

        irData = "";
        for (String s : list) {
            irData += s + ",";
        }
        return irData;
    }
    /**
    * preforms some calculations on the codesets we have in order to make them work with certain models of phone.
    *
    * HTC devices need formula 1
    * Samsungs want formula 2
    *
    * Samsung Pre-4.4.3 want nothing, so just return the input data
    *
    */
    private static int[] string2dec(int[] irData, int frequency) {
        int formula = shouldEquationRun();

        //Should we run any computations on the irData?
        if (formula != 0) {
            for (int i = 0; i < irData.length; i++) {
                if (formula == 1) {
                    irData[i] = irData[i] * (1000000/frequency);//the brackets should avoid an arithmetic overflow
                } else if (formula == 2) {
                    irData[i] = (int) Math.ceil(irData[i] * 26.27272727272727); //this is the samsung formula as per http://developer.samsung.com/android/technical-docs/Workaround-to-solve-issues-with-the-ConsumerIrManager-in-Android-version-lower-than-4-4-3-KitKat
                }
            }
        }
        return irData;
    }

    /**
     * This method figures out if we should be running the equation in string2dec,
     * which is based on the type of device. Some need it to run in order to function, some need it NOT to run
     *
     * HTC needs it on (HTC One M8)
     * Samsung needs occasionally a special formula, depending on the version
     * Android 5.0+ need it on.
     * Other devices DO NOT need anything special.
     */
    private static int shouldEquationRun() {
        //Some notes on what Build.X will return
        //System.out.println(Build.MODEL); //One M8
        //System.out.println(Build.MANUFACTURER); //htc
        //System.out.println(Build.VERSION.SDK_INT); //19

        //Samsung's way of finding out if their OS is too new to work without a formula:
        //int lastIdx = Build.VERSION.RELEASE.lastIndexOf(".");
        //System.out.println(Build.VERSION.RELEASE.substring(lastIdx+1)); //4

        //handle HTC
        if (Build.MANUFACTURER.equalsIgnoreCase("HTC")) {
            return 1;
        }
        //handle Lollipop (Android 5.0.1 == SDK 21) / beyond
        if (Build.VERSION.SDK_INT >= 21) {
            return 1;
        }
        //handle Samsung PRE-Android 5
        if (Build.MANUFACTURER.equalsIgnoreCase("SAMSUNG")) {
            if (Build.VERSION.SDK_INT >= 19) {
                int lastIdx = Build.VERSION.RELEASE.lastIndexOf(".");
                int VERSION_MR = Integer.valueOf(Build.VERSION.RELEASE.substring(lastIdx + 1));
                if (VERSION_MR < 3) {
                    // Before version of Android 4.4.2
                    //Note: NO formula here, not even the other one
                    return 0;
                } else {
                    // Later version of Android 4.4.3
                    //run the special samsung formula here
                    return 2;
                }
            }
        }
        //if something else...
        return 0;
    }
}
