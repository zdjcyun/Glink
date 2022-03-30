package com.zcloud.alone.network.handler.common;


import com.zcloud.alone.network.annotation.NettyHandler;
import com.zcloud.alone.network.zd.GinkgoServerComponent;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Idle处理
 * @author xiadaru
 */
@Component
@NettyHandler(nettyServer = "all",order = 0)
public class IotIdleStateHandler extends IdleStateHandler{

    /**
     * 默认构造方法
     */
    public IotIdleStateHandler(){
        super(0, 0, 0);
    }

    /**
     * 根据实际配置的构造方法,{@link GinkgoServerComponent}
     * @param readerIdleTimeSeconds
     * @param writerIdleTimeSeconds
     * @param allIdleTimeSeconds
     * @param unit
     */
    public IotIdleStateHandler(long readerIdleTimeSeconds, long writerIdleTimeSeconds, long allIdleTimeSeconds, TimeUnit unit) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds,unit);
    }
}
