package com.zcloud.alone.network.entity;

import lombok.Data;

import java.util.Date;

/**
 * 
 * @Created: with IDEA
 * @Description:
 * @Author:BradyXu<313582767@qq.com>
 * @Date:Create in 2020-03-23 16:40
 * 
 */
@Data
public class VibratingWire {

	/**
	 * 传感器编号(传感器唯一标识)
	 */
	private String sensorNumber;

	/**
	 * 频率值
	 */
	private Double frequency;

	/**
	 * 温度值
	 */
	private Double temperature;

	/**
	 * 高度值
	 */
	private Double height;

	/**
	 * 长度值
	 */
	private Double length;

	/**
	 * 宽度值
	 */
	private Double width;

	/**
	 * 角度值
	 */
	private Double angle;

	private Date createTime;

}