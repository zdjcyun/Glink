package com.zcloud.alone.network.helper;

import com.zcloud.alone.network.manage.ChannelManage;
import com.zcloud.alone.network.manage.MsgManage;
import com.zcloud.alone.network.manage.ThreadManage;
import com.zcloud.ginkgo.core.device.DeviceUniqueId;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description:辅助处理DTU和终端返回的应答数据(交互辅助)
 * @author: dzm
 */
@Slf4j
public class InteractiveHelper {

    /**
     * 发送指令并获取返回的结果
     * @param productId 产品ID
     * @param deviceId 设备ID
     * @param subDeviceId 子设备ID
     * @param sensorAddr  子设备地址
     * @param queryInstruct  查询指令
     * @param isDelay  是否延时
     * @return
     */
    public static String sendAndGetRes(String productId,String deviceId, String subDeviceId, String sensorAddr, Object queryInstruct, boolean isDelay) {
        Channel channel = ChannelManage.getChannel(DeviceUniqueId.of(productId,deviceId));
        // 设置线程状态变量
        SyncHelper syncHelper = new SyncHelper();
        syncHelper.setSubDeviceId(subDeviceId);
        syncHelper.setSensorAddr(sensorAddr);
        syncHelper.setSendFlag((String) queryInstruct);
        syncHelper.setDelay(isDelay);
        log.debug("添加发送消息的唯一标识 产品:{},设备:{}", productId,deviceId);
        // 保存发送消息的唯一标识
        MsgManage.putSyncHelper(DeviceUniqueId.of(productId,deviceId), syncHelper);
        if(channel==null){
            log.error("产品:{},设备:{},subDeviceId:{},sensorAddr:{}发送指令{}失败：channel is null", productId,deviceId,subDeviceId,sensorAddr,queryInstruct);
            return null;
        }
        // 发送查询指令
        channel.writeAndFlush(queryInstruct);
        try {
            // 获取返回结果，如果结果没有返回，则阻塞该线程(超时时间10秒)
            return syncHelper.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("产品:{},设备:{} 发送指令{}失败：{}",productId,deviceId,queryInstruct, e.getMessage());
        } finally {
            log.debug("移除发送消息的唯一标识 产品:{},设备:{}", productId,deviceId);
            // 移除发送消息的唯一标识
            MsgManage.removeSyncHelper(DeviceUniqueId.of(productId,deviceId));
        }
        return null;
    }

    public static boolean getAndParserRes(SyncHelper syncHelper, String msg) {
        //如果结果不需要延迟处理
        if(!syncHelper.isDelay()){
            //将结果直接返回返回
            syncHelper.setResponse(msg);
            return true;
        }
        //延时获取数据
        syncHelper.setResponseAndWait(msg);
        //如果延迟任务已存在，不创建新的延迟任务,直接返回
        if(syncHelper.isExit()){
            return false;
        }
        //如果延迟任务不存在，则设置任务已存在，创建新的延迟任务
        syncHelper.setExit(true);
        //延时1秒后响应请求
        ThreadManage.getInstance().addScheduleTask(new Runnable() {
            @Override
            public void run() {
                //1秒后恢复初始化状态
                syncHelper.setDelay(false);
                syncHelper.setExit(false);
                syncHelper.setCountDown();
            }
        }, 1, TimeUnit.SECONDS);
        return false;
    }
}
