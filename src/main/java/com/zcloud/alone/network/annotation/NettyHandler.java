package com.zcloud.alone.network.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * netty服务的处理逻辑注解
 * @author xiadaru
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NettyHandler {
    /**
     * 对应的是netty服务名,all代表使用所有的服务
     * @return
     */
    String[] nettyServer();

    /**
     * 加入到channelPipeline中的顺序,越小越靠前
     * @return
     */
    int order();
}