package com.zcloud.alone.network.manage;


import com.zcloud.alone.network.helper.SyncHelper;
import com.zcloud.ginkgo.core.device.DeviceUniqueId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  @Description:  存放同步工具的全局类
 *  @author: dzm
 */
public class MsgManage {

    /**
     * 存放终端编号和发送消息实体的映射关系
     */
    private static final Map<DeviceUniqueId, SyncHelper> SYNC_MAP = new ConcurrentHashMap<DeviceUniqueId, SyncHelper>();

    public static Map<DeviceUniqueId, SyncHelper> getSyncMap() {
        return SYNC_MAP;
    }

    public static SyncHelper getSyncHelper(DeviceUniqueId deviceUniqueId) {
        return SYNC_MAP.get(deviceUniqueId);
    }

    public static void putSyncHelper(DeviceUniqueId deviceUniqueId, SyncHelper syncHelper) {
        SYNC_MAP.put(deviceUniqueId, syncHelper);
    }

    public static void removeSyncHelper(DeviceUniqueId deviceUniqueId) {
        SYNC_MAP.remove(deviceUniqueId);
    }

    public static void removeSyncHelper(SyncHelper syncHelper) {
        SYNC_MAP.entrySet().stream().filter(entry -> entry.getValue() == syncHelper).forEach(entry -> SYNC_MAP.remove(entry.getKey()));
    }

    /**
     * 判断指定终端是否正在进行绑定或者手动发送指令操作
     * @param deviceUniqueId 设备
     * @return
     */
    public static boolean isManualNow(DeviceUniqueId deviceUniqueId) {
        return SYNC_MAP.keySet().stream().anyMatch(key -> key.equals(deviceUniqueId));
    }

    /**
     * 判断指定终端是否没有进行绑定或者手动发送指令操作
     * @param deviceUniqueId 设备
     * @return
     */
    public static boolean isNotManualNow(DeviceUniqueId deviceUniqueId) {
        return !isManualNow(deviceUniqueId);
    }
}
