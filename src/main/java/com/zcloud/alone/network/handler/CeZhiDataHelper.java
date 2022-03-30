package com.zcloud.alone.network.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.zcloud.alone.network.device.FeignAdapterImpl;
import com.zcloud.alone.network.entity.CeZhiModel;
import com.zcloud.ginkgo.core.device.DeviceInfo;
import com.zcloud.ginkgo.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Monster
 * @since 2020-09-30 16:37
 */
@Slf4j
public class CeZhiDataHelper {

    /**
     * 获取终端，传感器的绑定关系，同时CeZhiModel设置传感器编号
     * @param productId  产品ID
     * @param ceZhiModel  对象
     * @param isVibratingWire 是否为弦式传感器
     * @return
     */
    public static DeviceInfo getDeviceLink(String productId,CeZhiModel ceZhiModel, boolean isVibratingWire) {
        String terminalNumber = ceZhiModel.getDeviceId();
        String terminalChannel = ceZhiModel.getTerminalChannel();
        String sensorAddress = ceZhiModel.getSensorAddress();
        if(isVibratingWire){
            // 弦式传感器不能通过地址来区分设备，将传感器编号当做地址使用
            ceZhiModel.setSensorAddress(ceZhiModel.getSensorNumber());
            sensorAddress = null;
        }
        FeignAdapterImpl feignAdapterImpl = SpringUtil.getBean(FeignAdapterImpl.class);
        List<DeviceInfo> deviceInfos = feignAdapterImpl.getSubDeviceInfo(productId,ceZhiModel.getDeviceId());
        if(CollectionUtil.isEmpty(deviceInfos)){
            log.error("get subdeviceinfo is null,productId:{},deviceId:{}",productId,ceZhiModel.getDeviceId());
            return null;
        }
        for (DeviceInfo deviceInfo : deviceInfos) {
            String sensorAddr = feignAdapterImpl.getSensorAddr(deviceInfo.getProductId(), deviceInfo.getDeviceId());
            Integer terminalchan = feignAdapterImpl.getTerminalChannel(deviceInfo.getProductId(), deviceInfo.getDeviceId());
            if (StringUtils.isNotBlank(sensorAddr) && terminalchan != null &&
                    sensorAddr.equals(sensorAddress) && terminalchan == NumberUtil.parseInt(terminalChannel)) {
                return deviceInfo;
            }
        }
        if(sensorAddress == null){
            log.error("=== ERROR === 终端编号{}、终端通道号{} 数据库没有录入终端和弦式传感器相应的绑定信息 === ERROR ===", terminalNumber, terminalChannel);
        }else{
            log.error("=== ERROR === 终端编号{}、终端通道号{}、地址为{} 数据库没有录入终端和传感器相应的绑定信息=== ERROR ===", terminalNumber, terminalChannel, sensorAddress);
        }
        return null;
    }

}
