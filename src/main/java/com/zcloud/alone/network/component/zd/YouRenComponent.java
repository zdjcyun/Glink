package com.zcloud.alone.network.component.zd;

import com.zcloud.alone.network.zd.GinkgoServerComponent;
import com.zcloud.alone.conf.ConnectProperties;


/**
 * 有人终端初始化组件
 * @author xiadaru
 */
public class YouRenComponent extends GinkgoServerComponent {

    public YouRenComponent(ConnectProperties connectProperties) {
        super(connectProperties);
    }

    @Override
    public String getName() {
        return "有人终端";
    }

    @Override
    public String getDesc() {
        return "有人终端";
    }

}
