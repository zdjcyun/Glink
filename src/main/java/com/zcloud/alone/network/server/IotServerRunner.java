package com.zcloud.alone.network.server;

import cn.hutool.extra.spring.SpringUtil;
import com.zcloud.alone.conf.NettyServerProperties;
import com.zcloud.alone.network.annotation.NettyHandler;
import com.zcloud.alone.network.component.DeviceServerComponent;
import com.zcloud.alone.network.component.ServerComponent;
import com.zcloud.alone.network.component.ServerComponentFactory;
import com.zcloud.alone.network.zd.GinkgoServerComponent;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(1)
public class IotServerRunner implements CommandLineRunner{

    @Autowired
    private NettyServerProperties properties;

    @Autowired
    private ServerComponentFactory serverComponentFactory;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap tcpBootstrap;

    private static List<ChannelHandlerAdapter> nettyHandlers = new ArrayList<>();

    @Override
    public void run(String... args) throws Exception {
        //初始化服务组件工厂
        serverComponentFactory.initComponentFactory();
        //初始化handler
        this.initTcpHandler();
        //初始化Netty服务
        initNettyServer();
        //监听端口
        doBind();
    }

    /**
     * 开启netty服务器
     */
    protected void initNettyServer() {
        try {
            // Netty框架配置
            // ISS Iot Server Selector
            bossGroup = new NioEventLoopGroup(properties.getBossThreadNum(), new DefaultThreadFactory("ISS"));
            // ISW Iot Server Worker
            workerGroup = new NioEventLoopGroup(properties.getWorkerThreadNum(), new DefaultThreadFactory("ISW"));

            // 初始化tcp服务
            final List<DeviceServerComponent> serverComponents = SpringUtil.getBean(ServerComponentFactory.class).getServerComponents();
            if (!CollectionUtils.isEmpty(serverComponents)) {
                initTcpServe();
            }

        } catch (Exception e) {
            log.error("Nio服务端启动类未知异常：", e);
        }
    }

    private void initTcpHandler(){
        Map<String, Object> beans = SpringUtil.getApplicationContext().getBeansWithAnnotation(NettyHandler.class);
        beans.forEach((k,v) -> nettyHandlers.add((ChannelHandlerAdapter) v));
    }

    protected void initTcpServe() throws InterruptedException {
        tcpBootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(properties.getLevel()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();

                        int port = ch.localAddress().getPort();
                        doSocketChannelInitializer(ch, p, port);
                    }
                });
    }


    protected void doSocketChannelInitializer(SocketChannel ch, ChannelPipeline p, int port) {
        ServerComponent serverComponent = SpringUtil.getBean(ServerComponentFactory.class).getByPort(port);
        if (serverComponent instanceof DeviceServerComponent) {
            ((GinkgoServerComponent) serverComponent).parseChannelHandler(nettyHandlers);
            serverComponent.init(p, ch);
            //设置事件处理器

            //设置处理handler
            ((GinkgoServerComponent) serverComponent).initChannelInitializer(p);
        } else {
            log.error("查无与端口: {}匹配的服务组件: {}, 所有与此端口连接的设备都无法处理", port, DeviceServerComponent.class.getSimpleName());
        }
    }


    protected void doBind() {
        // 监听TCP端口
        SpringUtil.getBean(ServerComponentFactory.class).getServerComponents().forEach(item -> item.deviceServer().doBind(tcpBootstrap, SpringUtil.getApplicationContext()));
    }
}
