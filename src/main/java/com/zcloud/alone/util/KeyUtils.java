package com.zcloud.alone.util;

import com.google.common.base.Joiner;

import java.util.Random;

/**
 * 生成key值(根据定义的规则)
 * @author dzm
 */
public class KeyUtils {

	/** Redis 分组时连接器 固定符号：，*为通配符 */
	private static final Joiner joiner = Joiner.on(":").useForNull("*");

	/**
	 * 获取8个字符的随机值（终端验证时使用随机生成的key值做验证）
	 * @return
	 */
	public static String getEightLengthKey() {
		return getKey(8);
	}

	/**
	 * 获取指定长度字符的随机值
	 * @param length
	 * @return
	 */
	public static String getKey(int length) {
		String base = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int num = random.nextInt(base.length() - 1);
			sb.append(base.charAt(num));
		}
		return sb.toString();
	}

	/**
	 * 获取redis分组连接的key值
	 * @param paramKey
	 * @return
	 */
	public static String getRedisGroupKey(String... paramKey) {
		return joiner.join(paramKey);
	}
}