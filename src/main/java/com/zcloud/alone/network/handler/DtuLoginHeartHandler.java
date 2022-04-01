package com.zcloud.alone.network.handler;

import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.network.manage.DtuTaskManager;
import com.zcloud.ginkgo.core.device.DeviceUniqueId;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dzm
 */
@Slf4j
public class DtuLoginHeartHandler extends LoginHeartHandler {

    public DtuLoginHeartHandler(ConnectProperties connectProperties){
        DtuTaskManager.addDtuProduct(connectProperties.getProductId());
        super.connectProperties = connectProperties;
    }

    @Override
    protected void callBackActive(String terminalId, String socketId) {
        DtuTaskManager.startTask(DeviceUniqueId.of(this.connectProperties.getProductId(),terminalId),socketId);
    }

    @Override
    protected void callBackInactive(String terminalId, String socketId) {
        DtuTaskManager.stopTask(DeviceUniqueId.of(this.connectProperties.getProductId(),terminalId),socketId);
    }
}
