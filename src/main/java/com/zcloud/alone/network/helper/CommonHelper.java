package com.zcloud.alone.network.helper;

import cn.hutool.extra.spring.SpringUtil;
import com.zcloud.alone.network.link.GinkgoLinkClint;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 设备上下线
 */
@Slf4j
public class CommonHelper {

    /**
     * 设备上线
     * @param productId
     * @param deviceId
     */
    public static void deviceOnLine(String productId,String deviceId) {
        GinkgoLinkClint ginkgoLinkClint = SpringUtil.getBean(GinkgoLinkClint.class);
        ginkgoLinkClint.deviceConnected(productId,deviceId);
    }

    /**
     * 设备下线
     * @param productId
     * @param deviceId
     */
    public static void deviceOffLine(String productId,String deviceId) {
        GinkgoLinkClint ginkgoLinkClint = SpringUtil.getBean(GinkgoLinkClint.class);
        ginkgoLinkClint.deviceDisconnected(productId,deviceId);
    }

}
