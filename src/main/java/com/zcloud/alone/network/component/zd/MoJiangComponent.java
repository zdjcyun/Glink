package com.zcloud.alone.network.component.zd;

import com.zcloud.alone.network.zd.GinkgoServerComponent;
import com.zcloud.alone.conf.ConnectProperties;


/**
 * 初始化组件
 * @author xiadaru
 */
public class MoJiangComponent extends GinkgoServerComponent{

    public MoJiangComponent(ConnectProperties connectProperties) {
        super(connectProperties);
    }

    @Override
    public String getName() {
        return "墨匠终端";
    }

    @Override
    public String getDesc() {
        return "墨匠终端";
    }

}
