package com.zcloud.alone.network.component.youren;

import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.network.annotation.NettyHandler;
import com.zcloud.alone.network.handler.LoginHeartHandler;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dzm
 */
@Slf4j
@NettyHandler(nettyServer = {"youren"},order = 1)
public class YouRenLoginHeartHandler extends LoginHeartHandler {

    public YouRenLoginHeartHandler(ConnectProperties connectProperties) {
        this.connectProperties = connectProperties;
    }

    /**
     * 有人DTU的注册包以ZDJC开头，冒号分割，例：ZDJC:ZY2020050501
     */
    @Override
    protected String getLoginData(String msg) {
        char separators = ':';
        if (msg.length() > 4 && separators == msg.charAt(4)) {
            String[] splitArr = msg.split(":");
            String deviceId = splitArr[1];
            return deviceId;
        }
        return null;
    }

    /**
     * 有人dtu心跳包("zdjc")
     */
    @Override
    protected boolean isHeart(ByteBuf in) {
        if (in.capacity() != 0) {
            return in.getByte(0) == 122 && in.getByte(1) == 100 && in.getByte(2) == 106 && in.getByte(3) == 99;
        } else {
            return false;
        }
    }
}
