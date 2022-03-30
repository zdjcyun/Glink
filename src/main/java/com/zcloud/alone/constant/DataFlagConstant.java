package com.zcloud.alone.constant;

/**
 * @author brady_cc
 */
public class DataFlagConstant {
	
	/** 对应终端传感器的第一条数据标识 */
	public static final int FIRST_DATA = 1;
	
	/** 对应终端传感器的最后一条数据标识 */
	public static final int LAST_DATA = 2;
	
	/**  数据采集方式  自动 */
	public static final int DATA_AUTO = 1;
	
	/**  数据采集方式  手动 */
	public static final int DATA_MINAL = 2;
	
	/**  数据存储方式  普通一组数据 */
	public static final int STORE_COMMON = 1;
	
	/**  数据存储方式  X,Y两组数据 */
	public static final int STORE_SPECIAL_TWO = 2;
	
	/**  数据存储方式  X,Y,Z三组数组 */
	public static final int STORE_SPECIAL_THREE = 3;
}
