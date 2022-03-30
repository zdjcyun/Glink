package com.zcloud.alone.network.zd;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.constant.CoreConst;
import com.zcloud.alone.network.annotation.NettyHandler;
import com.zcloud.alone.network.component.DeviceServerComponent;
import com.zcloud.alone.network.handler.common.IotIdleStateHandler;
import com.zcloud.alone.network.server.IotSocketServer;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zcloud.alone.constant.CoreConst.EVENT_MANAGER_HANDLER;
import static com.zcloud.alone.constant.CoreConst.IDLE_STATE_EVENT_HANDLER;


/**
 * @author xiadaru
 */
@Slf4j
public abstract class GinkgoServerComponent extends DeviceServerComponent implements IotSocketServer {

    private ConnectProperties connectProperties;


    private ChannelInitializer channelInitializer;

    private LinkedHashMap<String, ChannelHandlerAdapter> channelHandlerAdapterLinkedHashMap = new LinkedHashMap<>();


    public GinkgoServerComponent(ConnectProperties connectProperties) {
        this.connectProperties = connectProperties;
    }

    @Override
    public abstract String getName();

    @Override
    public int port() {
        return this.connectProperties.getPort();
    }

    @Override
    public ConnectProperties config() {
        return this.connectProperties;
    }

    @Override
    protected IotSocketServer createDeviceServer() {
        return this;
    }

    @Override
    public void init(Object... args) {
        this.doInitChannel((ChannelPipeline) args[0]);
    }


    public void initChannelInitializer(ChannelPipeline channelPipeline) {
        // 新增事件管理处理
        /**/
        channelHandlerAdapterLinkedHashMap.keySet().forEach(handlerName -> {
            ChannelHandlerAdapter handler = channelHandlerAdapterLinkedHashMap.get(handlerName);
            if (handler instanceof IotIdleStateHandler) {
                if (this.config().getReaderIdleTime() > 0 || this.config().getAllIdleTime() > 0
                        || this.config().getWriterIdleTime() > 0) {
                    channelPipeline.addLast(IDLE_STATE_EVENT_HANDLER, new IotIdleStateHandler(this.config().getReaderIdleTime()
                            , this.config().getWriterIdleTime(), this.config().getAllIdleTime(), TimeUnit.SECONDS));
                }
            } else {
                try {
                    Constructor<? extends ChannelHandlerAdapter> constructor = ReflectUtil.getConstructor(handler.getClass(), ConnectProperties.class);
                    if (constructor == null) {
                        channelPipeline.addLast(handlerName, handler.getClass().newInstance());
                    } else {
                        channelPipeline.addLast(handlerName, constructor.newInstance(this.config()));
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    public void parseChannelHandler(List<ChannelHandlerAdapter> nettyHandlers) {
        List<ChannelHandlerAdapter> channelHandlerAdapterList =
                nettyHandlers.stream()
                        .filter(handlerBean -> {
                            NettyHandler nettyHandler = AnnotationUtils.findAnnotation(handlerBean.getClass(), NettyHandler.class);
                            List<String> nettyServices = Arrays.asList(nettyHandler.nettyServer());
                            return nettyHandler != null && CollectionUtil.isNotEmpty(nettyServices) && (handlerBean instanceof ChannelHandlerAdapter)
                                    && (nettyServices.contains(this.connectProperties.getNettyServer()) || nettyServices.contains("all"));
                        })
                        .sorted(Comparator.comparingInt(o -> AnnotationUtils.findAnnotation(o.getClass(), NettyHandler.class).order()))
                        .collect(Collectors.toList());
        for (ChannelHandlerAdapter handlerAdapter : channelHandlerAdapterList) {
            String handlerName = handlerAdapter.getClass().getSimpleName();
            if (handlerName.contains("$$")) {
                handlerName = handlerName.substring(0, handlerName.indexOf("$$"));
            }
            channelHandlerAdapterLinkedHashMap.put(handlerName, handlerAdapter);
        }
    }

}
