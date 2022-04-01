package com.zcloud.alone.network.manage;

import com.zcloud.ginkgo.core.device.DeviceUniqueId;
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
	private static final Map<DeviceUniqueId, Channel> CHANNEL_MAP = new ConcurrentHashMap<DeviceUniqueId, Channel>();

	public static Map<DeviceUniqueId, Channel> getChannelMap() {
		return CHANNEL_MAP;
	}

	public static Channel getChannel(DeviceUniqueId deviceUniqueId) {
		return CHANNEL_MAP.get(deviceUniqueId);
	}
	
	public static void putChannel(DeviceUniqueId deviceUniqueId, Channel channel) {
		CHANNEL_MAP.put(deviceUniqueId, channel);
    }

	public static void removeChannel(DeviceUniqueId deviceUniqueId) {
		CHANNEL_MAP.remove(deviceUniqueId);
    }

	public static void removeChannel(Channel channel) {
		CHANNEL_MAP.entrySet().stream().filter(entry -> entry.getValue() == channel).forEach(entry -> CHANNEL_MAP.remove(entry.getKey()));
    }
	
	public static boolean isChannelAvailable(DeviceUniqueId deviceUniqueId) {
		Channel channel = getChannel(deviceUniqueId);
		return (null != channel && channel.isActive()) ? true : false;
	}

	public static boolean isChannelNotAvailable(DeviceUniqueId deviceUniqueId) {
		return !isChannelAvailable(deviceUniqueId);
	}
}
