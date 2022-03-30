package com.zcloud.alone.network.component;

/**
 * 用来声明和设备对接时用来管理设备的设备组件
 */
public interface FrameworkComponent {
    /**
     * 组件功能名称
     * @return
     */
    String getName();

    /**
     * 设备服务说明
     * @return
     */
    String getDesc();

    /**
     * 初始化组件
     * @param args
     */
    void init(Object ...args);

}