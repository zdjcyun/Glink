package com.zcloud.alone.network.component;


import com.zcloud.alone.network.server.IotSocketServer;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;

public abstract class ServerComponent implements FrameworkComponent, InitializingBean {


    private IotSocketServer iotSocketServer;
    /**
     * 创建设备服务端
     * @return
     */
    public final IotSocketServer deviceServer() {
        if(this.iotSocketServer != null) {
            return this.iotSocketServer;
        }

        this.iotSocketServer = createDeviceServer();
        if(this.iotSocketServer == null) {
            throw new IllegalStateException("未指定设备服务端对象: " + IotSocketServer.class.getName() + "在设备组件对象中: " + getClass().getSimpleName());
        }

        return iotSocketServer;
    }

    protected abstract IotSocketServer createDeviceServer();


    @Override
    public void afterPropertiesSet() throws Exception {
        deviceServer();
    }
}
