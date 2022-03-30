package com.zcloud.alone.network.component;


import cn.hutool.extra.spring.SpringUtil;
import com.zcloud.alone.network.server.IotSocketServer;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务组件工厂
 */
@Service
public class ServerComponentFactory {

    private List<DeviceServerComponent> serverComponents = new ArrayList<>();
    private Map<Integer, ServerComponent> componentMap = new HashMap<>(8);
    public List<DeviceServerComponent> getServerComponents() {
        return this.serverComponents;
    }

    public ServerComponent getByPort(Integer port) {
        return componentMap.get(port);
    }


    public void initComponentFactory() throws Exception {
        Map<String,DeviceServerComponent> map = SpringUtil.getBeansOfType(DeviceServerComponent.class);
        map.forEach((k,component) ->{
            IotSocketServer deviceServer = component.deviceServer();
            if (null == deviceServer) {
                throw new IllegalArgumentException("未指定设备服务对象: DeviceServerComponent.deviceServer()");
            }

            if (component instanceof DeviceServerComponent) {
                serverComponents.add(component);
            }

            int port = deviceServer.port();
            if (port <= 0 || port > 65535)
                throw new BeanInitializationException("服务端组件: " + component.getName() + "使用错误的端口: " + port);

            // 已经有组件使用此端口, 抛出异常
            final ServerComponent serverComponent = componentMap.get(port);
            if (serverComponent != null) {
                throw new BeanInitializationException(serverComponent.getName()
                        + "和" + component.getName() + "使用同一个端口: " + deviceServer.port());
            }

            componentMap.put(port, component);
        });
    }
}
