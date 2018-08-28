package com.honeywell.honeywellproject.Util;

import static com.honeywell.honeywellproject.Util.DataHandler.Alone2Hex;

/**
 * Created by QHT on 2017-11-14.
 */
public class ELUtil {

    /**
     * 16进制转二进制函数
     * @param Hex  16进制的命令
     * @return binery_command
     * */
    public static String hex2binary(String Hex){

        if(TextUtil.isEmpty(Hex) || Hex.length()>2){
            return null;
        }
        String binary=Integer.toBinaryString(Integer.valueOf(Hex,16));
        if (binary.length()<8) {
            switch (binary.length()){
                case 0:
                    binary="00000000";
                    break;
                case 1:
                    binary="0000000"+binary;
                    break;
                case 2:
                    binary="000000"+binary;
                    break;
                case 3:
                    binary="00000"+binary;
                    break;
                case 4:
                    binary="0000"+binary;
                    break;
                case 5:
                    binary="000"+binary;
                    break;
                case 6:
                    binary="00"+binary;
                    break;
                case 7:
                    binary="0"+binary;
                    break;
                default:
            }
        }
        return binary;
    }

    /**
     * 求反码函数
     * @param command  二进制的命令
     * @return binery_convertcommand
     * */
    public static String convert(String command){

            if(TextUtil.isEmpty(command) || command.length()>8){
                return null;
            }
        StringBuilder builder=new StringBuilder();
        for (char c:command.toCharArray()) {
            if(c=='0'){
                builder.append('1');
            }else if(c=='1'){
                builder.append('0');
            }
        }
        return builder.toString();
    }

    /**
     * 二进制字符串反转函数
     * @param binary  二进制字符串
     * @return binery_convert
     * */
    public static String binaryconvert(String binary){

        if(TextUtil.isEmpty(binary) || binary.length()>8){
            return null;
        }
        StringBuilder builder=new StringBuilder();
        char[] ch=binary.toCharArray();
        for (int i=ch.length-1;i>=0;i--) {
            builder.append(ch[i]);
        }
        return builder.toString();
    }

    /**
     * 二进制字符串转电平函数
     * @param power  二进制字符
     * @return power
     * */
    public static int[] binary2power(String power){

        if(TextUtil.isEmpty(power)){
            return null;
        }
        char[] c=power.toCharArray();
        int[] powerarr=new int[power.length()*2+2+2];
        //帧头
        powerarr[0]=9000;
        powerarr[1]=4500;
        int idx=2;
        for(int i=0;i<c.length;i++){
            if(c[i]=='0'){
                powerarr[idx]=560;
                powerarr[idx+1]=560;
            }else if(c[i]=='1'){
                powerarr[idx]=560;
                powerarr[idx+1]=1690;
            }
            idx+=2;
        }
        //帧尾
        powerarr[powerarr.length-2]=560;
        powerarr[powerarr.length-1]=560;
        return powerarr;
    }
    /**
     * 红外编址的红外命令
     * */
    public static int[] getParamIR(int typeID,int moduleID,String addr) {
        String typeIDs= Alone2Hex(Integer.toHexString(typeID));
        String moduleIDs= Alone2Hex(Integer.toHexString(moduleID));
        String addrs= Alone2Hex(Integer.toHexString(Integer.parseInt(addr)));
        //ACC检验 所有字节相加取高8位字节
        int checkNum=Integer.parseInt(typeIDs,16)+Integer.parseInt(moduleIDs,16)+Integer.parseInt(addrs,16);
        //用256求余最大是255，即16进制的FF，超过256则保证取低8位
        int iCheckNum = checkNum % 256;

        String typeIDsConvert=binaryconvert(hex2binary(typeIDs)) ;
        String moduleIDsConvert=binaryconvert( hex2binary(moduleIDs));
        String addrsConvert= binaryconvert(hex2binary(addrs));
        String  accConvert=binaryconvert(hex2binary(Alone2Hex(Integer.toHexString(iCheckNum))));
        StringBuffer sb = new StringBuffer();
        sb. append(typeIDsConvert).append(moduleIDsConvert).
                append(addrsConvert).
                append(accConvert);
        return binary2power(sb.toString());
    }
    /**
     * 蓝牙发送的红外命令
     * */
    public static String  EL_WRITE(int typeID,int moduleID,String addr){
        String typeIDs= Alone2Hex(Integer.toHexString(typeID));
        String moduleIDs= Alone2Hex(Integer.toHexString(moduleID));
        String addrs= Alone2Hex(Integer.toHexString(Integer.parseInt(addr)));
        //ACC检验 所有字节相加取高8位字节
        int checkNum=Integer.parseInt(typeIDs,16)+Integer.parseInt(moduleIDs,16)+Integer.parseInt(addrs,16);
        //用256求余最大是255，即16进制的FF，超过256则保证取低8位
        int iCheckNum = checkNum % 256;
        StringBuffer sb = new StringBuffer();
        sb. append(typeIDs).append(moduleIDs).
                append(addrs).
                append(Alone2Hex(Integer.toHexString(iCheckNum)));
        return sb.toString();
    }
    /**
     * fragment1-5 灯板的红外命令
     * @param address
     * @param command
     * 处理地址和命令参数为最终输入
     * @return power[] 电平数组
     * */
    public static int[] getParam(String address,String command) {
        String addressC=hex2binary(address);
        String addressConvert=convert(addressC);
        String binary1 = binaryconvert(addressC)+ binaryconvert(addressConvert);

        String commandC=hex2binary(command);
        String commandConvert=convert(commandC);
        String binary2 = binaryconvert(commandC)+ binaryconvert(commandConvert);
        //binary1和binary2加上前面的引导码加上结束码组成一组命令
        String finalBinary=binary1+binary2;
        return binary2power(finalBinary);
    }
}
