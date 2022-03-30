package com.zcloud.alone.network.server;


import cn.hutool.extra.spring.SpringUtil;
import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.network.component.ServerComponent;
import com.zcloud.alone.network.component.ServerComponentFactory;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * 服务端套接字
 */
public interface IotSocketServer {

    Logger LOGGER = LoggerFactory.getLogger(IotSocketServer.class);

    /**
     * 监听的端口
     *
     * @return
     * @see #config()
     */
    @Deprecated
    int port();

    /**
     * 服务端配置
     *
     * @return
     * @see ConnectProperties#getPort()
     * @see ConnectProperties#getHost()
     */
    ConnectProperties config();


    /**
     * 开启监听端口并且绑定
     *
     * @param sb
     */
    default void doBind(AbstractBootstrap sb, ApplicationContext context) {
        ChannelFuture bind;
        if (StringUtils.hasText(config().getHost())) {
            bind = sb.bind(config().getHost(), config().getPort());
        } else {
            bind = sb.bind(config().getPort());
        }

        // 绑定此设备要开启的端口
        bind.addListener(future -> {
            ServerComponent serverComponent = SpringUtil.getBean(ServerComponentFactory.class).getByPort(config().getPort());

            if (future.isSuccess()) {
                LOGGER.info("监听端口成功({}) 端口：{} - 简介：{}", serverComponent.getName(), this.port(), serverComponent.getDesc());
            } else {
                LOGGER.error("监听端口失败({}) 端口: {} - 简介：{} - 异常信息: {}", serverComponent.getName(), this.port()
                        , serverComponent.getDesc(), future.cause().getMessage(), future.cause());

                Throwable cause = future.cause();
                if (context instanceof ConfigurableApplicationContext) {
                    LOGGER.warn("开启端口失败: {}, 将关闭Spring Application", this.port(), cause);
                    if (((ConfigurableApplicationContext) context).isActive()) {
                        ((ConfigurableApplicationContext) context).close();
                        LOGGER.warn("关闭Spring Application: {} - 状态: 关闭完成", context.getApplicationName());
                    }
                }
            }
        });
    }

    /**
     * 自定义Handler
     *
     * @param p
     */
    default void doInitChannel(ChannelPipeline p) {
    }
}
