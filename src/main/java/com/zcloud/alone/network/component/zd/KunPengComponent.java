package com.zcloud.alone.network.component.zd;

import com.zcloud.alone.network.zd.GinkgoServerComponent;
import com.zcloud.alone.conf.ConnectProperties;


/**
 * 北斗终端初始化组件
 * @author xiadaru
 */
public class KunPengComponent extends GinkgoServerComponent {

    public KunPengComponent(ConnectProperties connectProperties) {
        super(connectProperties);
    }

    @Override
    public String getName() {
        return "鲲鹏终端";
    }

    @Override
    public String getDesc() {
        return "鲲鹏终端";
    }

}
