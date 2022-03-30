package com.zcloud.alone.network.manage;


import com.zcloud.alone.network.helper.SyncHelper;

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
    private static final Map<String, SyncHelper> SYNC_MAP = new ConcurrentHashMap<String, SyncHelper>();

    public static Map<String, SyncHelper> getSyncMap() {
        return SYNC_MAP;
    }

    public static SyncHelper getSyncHelper(String deviceId) {
        return SYNC_MAP.get(deviceId);
    }

    public static void putSyncHelper(String deviceId, SyncHelper syncHelper) {
        SYNC_MAP.put(deviceId, syncHelper);
    }

    public static void removeSyncHelper(String deviceId) {
        SYNC_MAP.remove(deviceId);
    }

    public static void removeSyncHelper(SyncHelper syncHelper) {
        SYNC_MAP.entrySet().stream().filter(entry -> entry.getValue() == syncHelper).forEach(entry -> SYNC_MAP.remove(entry.getKey()));
    }

    /**
     * 判断指定终端是否正在进行绑定或者手动发送指令操作
     * @param deviceId 终端id
     * @return
     */
    public static boolean isManualNow(String deviceId) {
        return SYNC_MAP.keySet().stream().anyMatch(key -> key.equals(deviceId));
    }

    /**
     * 判断指定终端是否没有进行绑定或者手动发送指令操作
     * @param deviceId 终端id
     * @return
     */
    public static boolean isNotManualNow(String deviceId) {
        return !isManualNow(deviceId);
    }
}
