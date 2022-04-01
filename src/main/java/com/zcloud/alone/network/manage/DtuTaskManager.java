package com.zcloud.alone.network.manage;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zcloud.alone.network.entity.SocketTaskIdentity;
import com.zcloud.ginkgo.core.device.DeviceUniqueId;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * dtu任务的启停
 */
public class DtuTaskManager {

    /**
     * 主动停起任务的产品
     */
    private static Set<String> dtuProductIds = Sets.newConcurrentHashSet();

    private static Object lock = new Object();

    /***
     *  任务状态
     */
    private static final Map<DeviceUniqueId, SocketTaskIdentity> taskStatus = Maps.newConcurrentMap();

    public static void addDtuProduct(String productId){
        dtuProductIds.add(productId);
    }

    /**
     * 启动定时任务
     * @param deviceUniqueId
     * @param socketId
     */
    public static void startTask(DeviceUniqueId deviceUniqueId,String socketId) {
        if(!dtuProductIds.contains(deviceUniqueId.getProductId())){
            return;
        }
        synchronized (lock) {
            taskStatus.put(deviceUniqueId, SocketTaskIdentity.of(socketId, true));
        }
    }

    /**
     * 停止定时任务
     * @param deviceUniqueId
     * @param socketId
     */
    public static void stopTask(DeviceUniqueId deviceUniqueId,String socketId) {
        if(!dtuProductIds.contains(deviceUniqueId.getProductId())){
            return;
        }
        synchronized (lock) {
            if (!taskStatus.containsKey(deviceUniqueId)) {
                return;
            }
            SocketTaskIdentity socketTask = taskStatus.get(deviceUniqueId);
            if (Objects.equals(socketId, socketTask.getSocketId())) {
                taskStatus.remove(deviceUniqueId);
            }
        }
    }

    /**
     * 任务是否可以使用
     * @param deviceUniqueId
     * @return
     */
    public static boolean taskIsActive(DeviceUniqueId deviceUniqueId){
        if(!dtuProductIds.contains(deviceUniqueId.getProductId())){
            return true;
        }
        SocketTaskIdentity taskIdentity = MapUtil.get(taskStatus,deviceUniqueId,SocketTaskIdentity.class);
        return taskIdentity!=null && taskIdentity.isTaskStatus();
    }
}
