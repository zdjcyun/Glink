package com.zcloud.alone.network.component.zd;

import com.zcloud.alone.network.zd.GinkgoServerComponent;
import com.zcloud.alone.conf.ConnectProperties;


/**
 * 测智终端初始化组件
 * @author xiadaru
 */
public class CeZhiComponent extends GinkgoServerComponent{

    public CeZhiComponent(ConnectProperties connectProperties) {
        super(connectProperties);
    }

    @Override
    public String getName() {
        return "测智终端";
    }

    @Override
    public String getDesc() {
        return "测智终端";
    }

}
