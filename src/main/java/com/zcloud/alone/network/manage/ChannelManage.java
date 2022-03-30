package com.zcloud.alone.network.manage;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title : ChannelManage管理
 * Description : 处理通道和终端id的映射关系工具类
 * @author dzm
 */
public class ChannelManage {

	/**
	 * 存放终端编号和通道的映射关系
	 */
	private static final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<String, Channel>();

	public static Map<String, Channel> getChannelMap() {
		return CHANNEL_MAP;
	}

	public static Channel getChannel(String deviceId) {
		return CHANNEL_MAP.get(deviceId);
	}
	
	public static void putChannel(String deviceId, Channel channel) {
		CHANNEL_MAP.put(deviceId, channel);
    }

	public static void removeChannel(String deviceId) {
		CHANNEL_MAP.remove(deviceId);
    }

	public static void removeChannel(Channel channel) {
		CHANNEL_MAP.entrySet().stream().filter(entry -> entry.getValue() == channel).forEach(entry -> CHANNEL_MAP.remove(entry.getKey()));
    }
	
	public static boolean isChannelAvailable(String deviceId) {
		Channel channel = getChannel(deviceId);
		return (null != channel && channel.isActive()) ? true : false;
	}

	public static boolean isChannelNotAvailable(String deviceId) {
		return !isChannelAvailable(deviceId);
	}
}
