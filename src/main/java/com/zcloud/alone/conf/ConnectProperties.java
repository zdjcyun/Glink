package com.zcloud.alone.conf;

public class ConnectProperties {

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 断开重连时间周期(秒)
     * 用于客户端
     */
    private long reconnect = 30;

    /**
     * 没读和没写多长时间判断失活
     */
    private long allIdleTime;

    /**
     * 多久没读判断失活的时间
     */
    private long readerIdleTime;

    /**
     * 多久没写判断失活的时间
     */
    private long writerIdleTime;

    /**
     * 产品ID
     */
    private String productId;

    /***
     * netty服务铭
     */
    private String nettyServer;

    public ConnectProperties() {
        this(null, 0);
    }

    public ConnectProperties(Integer port) {
        this(null, port);
    }

    public ConnectProperties(String host, Integer port) {
        this(host, port, 60, 0, 0);
    }

    public ConnectProperties(Integer port, long allIdleTime, long readerIdleTime, long writerIdleTime) {
        this(null, port, allIdleTime, readerIdleTime, writerIdleTime);
    }

    public ConnectProperties(String host, Integer port, long allIdleTime, long readerIdleTime, long writerIdleTime) {
        this.host = host;
        this.port = port;
        this.allIdleTime = allIdleTime;
        this.readerIdleTime = readerIdleTime;
        this.writerIdleTime = writerIdleTime;
    }

    public ConnectProperties(String host, Integer port, long allIdleTime, long readerIdleTime, long writerIdleTime, String productId) {
        this.host = host;
        this.port = port;
        this.allIdleTime = allIdleTime;
        this.readerIdleTime = readerIdleTime;
        this.writerIdleTime = writerIdleTime;
        this.productId = productId;
    }

    public ConnectProperties(String host, Integer port, long allIdleTime, long readerIdleTime, long writerIdleTime, String productId, String nettyServer) {
        this.host = host;
        this.port = port;
        this.allIdleTime = allIdleTime;
        this.readerIdleTime = readerIdleTime;
        this.writerIdleTime = writerIdleTime;
        this.productId = productId;
        this.nettyServer = nettyServer;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public long getReconnect() {
        return reconnect;
    }

    public void setReconnect(long reconnect) {
        this.reconnect = reconnect;
    }

    public long getAllIdleTime() {
        return allIdleTime;
    }

    public void setAllIdleTime(long allIdleTime) {
        this.allIdleTime = allIdleTime;
    }

    public long getReaderIdleTime() {
        return readerIdleTime;
    }

    public void setReaderIdleTime(long readerIdleTime) {
        this.readerIdleTime = readerIdleTime;
    }

    public long getWriterIdleTime() {
        return writerIdleTime;
    }

    public void setWriterIdleTime(long writerIdleTime) {
        this.writerIdleTime = writerIdleTime;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getNettyServer() {
        return nettyServer;
    }

    public void setNettyServer(String nettyServer) {
        this.nettyServer = nettyServer;
    }

    @Override
    public String toString() {
        return this.host + ":" + this.port;
    }
}
