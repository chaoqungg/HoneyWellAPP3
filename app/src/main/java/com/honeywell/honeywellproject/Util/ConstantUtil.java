package com.honeywell.honeywellproject.Util;

public class ConstantUtil {


	public static String APPLICATION_NAME="Honeywell";
	/**
	 * 测试芯片名字
	 * */
//	  public static final String BLE_NAME="Skylab_BLE";
	/**
	 * 生产芯片名字
	 * */
	  	public static final String BLE_NAME="HoneywellToolkit";
		public static final String EL_NAME="HoneywellIR";
//		public static final String EL_NAME="Skylab_BLE";
	/**
	 * NOTIFY是否成功
	 */
	public static boolean NOTIFYSUCCESS = true;
	/**
	 * 回路是否断开电源
	 */
	@Deprecated
	public static boolean LOOPPOWER = false;
	/**
	 * 单独编址属于临时编址、新建任务编址、历史任务编址(分别对应1、2、3)
	 */
	public static String MEMORYTASK= "";
	/**
	 * 红外USB是否外接
	 */
	@Deprecated
	public static boolean USBInsert=false;
	/**
	 * 红外驱动的选项，若改为shareprefrence 则相当于保存，写成static只生效当前
	 */
	public static int selectDriveItem=-1;

	/**
	 *蓝牙UUID  Nordic
	 */
//	public static final String  SERVER_UUID   = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
//	public static final String  Write_UUID    = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
//	public static final String  NOTIFIy_UUID  = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
	/**
	 *蓝牙UUID  TI
	 * Write_UUID  Write_No_Response
	 */
	public static final String  SERVER_UUID   = "F000C0E0-0451-4000-B000-000000000000";
	public static final String  Write_UUID    = "F000C0E1-0451-4000-B000-000000000000";
	public static final String  NOTIFIy_UUID  = "F000C0E1-0451-4000-B000-000000000000";
	/**
	 * 红外UUID  TI
	 * Write_UUID  Write_No_Response
	 */
	public static final String  SERVER_UUID_EL   = "F0001130-0451-4000-B000-000000000000";
	public static final String  Write_UUID_EL    = "F0001131-0451-4000-B000-000000000000";
//	public static final String  SERVER_UUID_EL   = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
//	public static final String  Write_UUID_EL    = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
	/**
	 * 百度语音appId
	 * */
	public static final String  baiduappId = "10731535";
	public static final String  baiduappKey = "zmhHW4Grp8BFV0K53BhyI5vu";
	public static final String  baidusecretKey = "5b3102ee6671ee50cce5fc43c84cd199";
}
