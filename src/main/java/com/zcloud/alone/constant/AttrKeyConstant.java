package com.zcloud.alone.constant;

import io.netty.util.AttributeKey;

/**
 * Title : AttrKeyConstant管理
 * Description :通道属性Key值常量类
 * @author dzm
 */
public class AttrKeyConstant {
	
	/**
	 * 设备ID（网关设备ID）
	 */
	public static final AttributeKey<String> TERMINAL_ID = AttributeKey.valueOf("terminalId");
}
