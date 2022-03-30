package com.zcloud.alone.network.server;

//@EnableConfigurationProperties(NettyServerProperties.class)
public class IotServeBootstrap {/* implements InitializingBean, DisposableBean, ApplicationListener<ContextRefreshedEvent>, BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(IotServeBootstrap.class);
    public static ApplicationContext applicationContext;
    public static BeanFactory BEAN_FACTORY; //spring bean factory
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap tcpBootstrap;
    @Autowired
    private NettyServerProperties properties;

    private static List<ChannelHandlerAdapter> nettyHandlers = new ArrayList<>();

    private static ServerComponentFactory COMPONENT_FACTORY;

    @Override
    public void afterPropertiesSet() throws Exception {
        IotServeBootstrap.COMPONENT_FACTORY = BEAN_FACTORY.getBean(ServerComponentFactory.class);
        this.initTcpHandler();
        //初始化Netty服务
        initNettyServer();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("Spring容器启动完成, 正在开启Iot Netty服务...");
        IotServeBootstrap.applicationContext = event.getApplicationContext();

        try {
            doBind(applicationContext);
        } catch (Exception exception) {

            // 异常关闭应用上下文
            if (applicationContext instanceof ConfigurableApplicationContext) {
                ((ConfigurableApplicationContext) applicationContext).close();
            }
        }
    }

    *//**
     * 开启netty服务器
     *//*
    protected IotServeBootstrap initNettyServer() {
        try {
            // Netty框架配置
            // ISS Iot Server Selector
            bossGroup = new NioEventLoopGroup(properties.getBossThreadNum(), new DefaultThreadFactory("ISS"));
            // ISW Iot Server Worker
            workerGroup = new NioEventLoopGroup(properties.getWorkerThreadNum(), new DefaultThreadFactory("ISW"));

            // 初始化tcp服务
            final List<DeviceServerComponent> serverComponents = COMPONENT_FACTORY.getServerComponents();
            if (!CollectionUtils.isEmpty(serverComponents)) {
                initTcpServe();
            }

        } catch (Exception e) {
            logger.error("Nio服务端启动类未知异常：", e);
        }

        return this;
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
                        IotServeBootstrap.this.doSocketChannelInitializer(ch, p, port);
                    }
                });
    }


    protected void doSocketChannelInitializer(SocketChannel ch, ChannelPipeline p, int port) {
        ServerComponent serverComponent = COMPONENT_FACTORY.getByPort(port);
        if (serverComponent instanceof DeviceServerComponent) {
            ((GinkgoServerComponent) serverComponent).parseChannelHandler(nettyHandlers);
            IotSocketServer iotSocketServer = serverComponent.deviceServer();
            if (iotSocketServer instanceof BeanFactoryAware) {
                ((BeanFactoryAware) iotSocketServer).setBeanFactory(BEAN_FACTORY);
            }
            serverComponent.init(p, ch);
            //设置事件处理器

            //设置处理handler
            ((GinkgoServerComponent) serverComponent).initChannelInitializer(p);
        } else {
            logger.error("查无与端口: {}匹配的服务组件: {}, 所有与此端口连接的设备都无法处理", port, DeviceServerComponent.class.getSimpleName());
        }
    }


    protected void doBind(final ApplicationContext context) {
        // 监听TCP端口
        COMPONENT_FACTORY.getServerComponents().forEach(item -> item.deviceServer().doBind(tcpBootstrap, context));
    }

    public static void publishApplicationEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }

    public static ServerComponent getServerComponent(int port) {
        return COMPONENT_FACTORY.getByPort(port);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        IotServeBootstrap.BEAN_FACTORY = beanFactory;
    }

    *//**
     * 服务组件工厂
     *
     * @return ServerComponentFactory
     *//*
    @Bean
    public ServerComponentFactory serverComponentFactory() {
        return new ServerComponentFactory();
    }



    @Override
    public void destroy() throws Exception {
        try {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }

        } catch (Exception e) {
            logger.error("关闭应用时错误", e);
        }
    }*/

}
